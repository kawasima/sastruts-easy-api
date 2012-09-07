package net.unit8.sastruts.easyapi.client;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.dto.ErrorDto;
import net.unit8.sastruts.easyapi.dto.FailureDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.FileInputStreamUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringConversionUtil;
import org.seasar.framework.util.StringUtil;

public abstract class ClientContext<T> {
	private static final Pattern DYNAMIC_SEGMENT_PTN = Pattern.compile("(\\{\\w+\\})");
	protected List<NameValuePair> params = new ArrayList<NameValuePair>();
	protected String name;

	@Binding(bindingType=BindingType.NONE)
	protected HttpClient client;

	@Binding(bindingType=BindingType.MAY)
	public String transactionIdName;


	public void setQuery(Object query) {
		if (query == null) return;
		if (query instanceof Map) {
			for (Map.Entry<?,?> e : ((Map<?,?>)query).entrySet()) {
				params.add(new BasicNameValuePair(e.getKey().toString(), StringConversionUtil.toString(e.getValue())));
			}
		} else {
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(query.getClass());
			int size = beanDesc.getPropertyDescSize();
			for (int i=0; i < size; i++) {
				PropertyDesc propDesc = beanDesc.getPropertyDesc(i);
				Object value = propDesc.getValue(query);
				params.add(new BasicNameValuePair(propDesc.getPropertyName(), StringConversionUtil.toString(value)));
			}
		}
	}

	protected String processDynamicPath(String path) {
		StringBuffer sb = new StringBuffer();
		Matcher m = DYNAMIC_SEGMENT_PTN.matcher(path);
		while(m.find()) {
			final String paramName = m.group(1);
			NameValuePair pair = (NameValuePair)CollectionUtils.find(params, new Predicate() {
				public boolean evaluate(Object obj) {
					return StringUtil.equals(((NameValuePair)obj).getName(), paramName);
				}
			});
			String paramValue = (pair == null) ? "" : pair.getValue();
			m.appendReplacement(sb, "");
			sb.append(paramValue);
			params.remove(pair);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	protected void processHeader(ResponseDto responseDto) throws EasyApiException {
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
	}
	protected URI buildUri(EasyApiSetting setting) {
		String query = URLEncodedUtils.format(params, setting.getEncoding());
		try {
			return new URIBuilder()
				.setScheme(setting.getScheme())
				.setHost(setting.getHost())
				.setPath(processDynamicPath(setting.getPath()))
				.setQuery(query)
				.build();
		} catch (URISyntaxException e) {
			throw new EasyApiSystemException("Invalid URI", e);
		}

	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

	protected InputStream getMockResponseStream() {
		File dir = ResourceUtil.getResourceAsFileNoException("mock/" + name);
		if (dir == null || !dir.exists()) {
			return null;
		}
		Collection<File> dataFiles = FileUtils.listFiles(dir, new String[]{"xml"}, false);
		if (dataFiles.isEmpty())
			return null;

		File dataFile = dataFiles.toArray(new File[0])[RandomUtils.nextInt(dataFiles.size())];
		return FileInputStreamUtil.create(dataFile);
	}

}
