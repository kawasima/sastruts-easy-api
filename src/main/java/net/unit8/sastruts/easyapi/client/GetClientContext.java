package net.unit8.sastruts.easyapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.dto.ErrorDto;
import net.unit8.sastruts.easyapi.dto.FailureDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.ClassUtil;

public class GetClientContext<T> extends ClientContext<T> {
	private EasyApiSettingProvider provider;

	private final static Pattern DYNAMIC_SEGMENT_PTN = Pattern.compile("(\\{\\w+\\})");
	private HttpClient client;
	private String name;
	private Class<T> dtoClass;
	private HttpParams params;

	public GetClientContext(HttpClient client, Class<T> dtoClass) {
		this.dtoClass = dtoClass;
		this.client = client;
		provider = SingletonS2Container.getComponent(EasyApiSettingProvider.class);
		params = new SyncBasicHttpParams();
	}

	public GetClientContext<T> from(String name) throws EasyApiException {
		this.name = name;
		return this;
	}

	public void setQuery(Object query) {
		if (query == null) return;
		if (query instanceof Map) {
			for (Map.Entry<?,?> e : ((Map<?,?>)query).entrySet()) {
				params.setParameter(e.getKey().toString(), e.getValue());
			}
		} else {
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(query.getClass());
			int size = beanDesc.getPropertyDescSize();
			for (int i=0; i < size; i++) {
				PropertyDesc propDesc = beanDesc.getPropertyDesc(i);
				Object value = propDesc.getValue(query);
				params.setParameter(propDesc.getPropertyName(), value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> getResultList() throws EasyApiException {
		T obj = executeQuery();
		if (Collection.class.isAssignableFrom(obj.getClass())) {
			return new ArrayList<T>((Collection<T>) obj);
		} else {
			List<T> results = new ArrayList<T>();
			results.add(obj);
			return results;
		}
	}

	public T getSingleResult() throws EasyApiException {
		return executeQuery();
	}

	protected String processDynamicPath(String path) {
		StringBuffer sb = new StringBuffer();
		Matcher m = DYNAMIC_SEGMENT_PTN.matcher(path);
		while(m.find()) {
			String paramName = m.group(1);
			Object val = params.getParameter(paramName);
			String paramValue = (val == null) ? "" : val.toString();
			m.appendReplacement(sb, "");
			sb.append(paramValue);
			params.removeParameter(paramName);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	protected T executeQuery() throws EasyApiException {
		T dto = (T) ClassUtil.newInstance(dtoClass);
		EasyApiSetting setting = provider.get(name);
		HttpGet method = new HttpGet(setting.getHost() + processDynamicPath(setting.getPath()));
		method.setParams(params);
		try {
			HttpResponse response = client.execute(method);
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance().fromXML(in);
			if (responseDto.header != null) {
				if (responseDto.header.errors != null) {
					for (ErrorDto error : responseDto.header.errors) {
						throw new EasyApiSystemException(error.getMessage());
					}
				}
				EasyApiException ex = null;
				if (responseDto.header.failures != null) {
					for (FailureDto failure : responseDto.header.failures) {
						if (ex == null) {
							ex = new EasyApiException(failure.getCode());
						} else {
							ex.append(new EasyApiException(failure.getCode()));
						}
					}
				}
				if (ex != null) throw ex;
			}

			if (dtoClass.isInstance(responseDto.body)) {
				return (T)responseDto.body;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		return dto;
	}
}
