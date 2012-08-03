package net.unit8.sastruts.easyapi.client;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.dto.ErrorDto;
import net.unit8.sastruts.easyapi.dto.FailureDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.apache.http.params.HttpParams;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

public abstract class ClientContext<T> {
	private static final Pattern DYNAMIC_SEGMENT_PTN = Pattern.compile("(\\{\\w+\\})");
	private Object sendData;
	protected HttpParams params;

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

}
