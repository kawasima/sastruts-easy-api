package net.unit8.sastruts.easyapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.dto.RequestDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.StringConversionUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class PostClientContext<T> extends ClientContext<T> {
	private static final Logger logger = Logger.getLogger(ClientContext.class);

	@Resource(name="easyApiSettingProvider")
	private EasyApiSettingProvider provider;

	private Object data;

	public PostClientContext() {
		params = new ArrayList<NameValuePair>();
	}

	public PostClientContext<T> to(String name) {
		this.name = name;
		return this;
	}

	public int execute() throws EasyApiException {
		EasyApiSetting setting = provider.get(name);
		HttpPost method = new HttpPost(buildUri(setting));
		if (data != null) {
			XStreamFactory.setOmitFields(data);
			HttpEntity entity = null;
			switch (setting.getRequestType()) {
			case URL_ENCODE:
				List<NameValuePair> parameters = CollectionsUtil.newArrayList();
				BeanDesc beanDesc = BeanDescFactory.getBeanDesc(data.getClass());
				for (int i=0; i < beanDesc.getPropertyDescSize(); i++) {
					PropertyDesc propertyDesc = beanDesc.getPropertyDesc(i);
					if (propertyDesc.getField().getAnnotation(XStreamOmitField.class) == null) {
						Object value = propertyDesc.getValue(data);
						if (value != null) {
							parameters.add(new BasicNameValuePair(
									propertyDesc.getPropertyName(), StringConversionUtil.toString(value)));
						}
					}
				}
				try {
					entity = new UrlEncodedFormEntity(parameters, setting.getEncoding());
				} catch (UnsupportedEncodingException e) {
					throw new IORuntimeException(e);
				}
				break;
			default:
				RequestDto requestDto = new RequestDto();
				requestDto.body = data;
				String xml = XStreamFactory.getInstance().toXML(requestDto);
				try {
					entity = new StringEntity(xml, setting.getEncoding());
				} catch (UnsupportedEncodingException e) {
					throw new IORuntimeException(e);
				}
				break;
			}
			method.setEntity(entity);
		}

		String transactionId = UUID.randomUUID().toString();
		if (transactionIdName != null)
			method.addHeader(transactionIdName, transactionId);
		InputStream in = null;
		HttpEntity entity = null;
		try {
			logger.log("ISEA0001", new Object[]{transactionId, name});
			if (provider.useMock) {
				in = getMockResponseStream();
			} else {
				HttpResponse response = client.execute(method);
				entity = response.getEntity();
				in = entity.getContent();
			}
			Class<?> resultSetDtoClass = XStreamFactory.getResutSetClass(data);
			if (resultSetDtoClass != null) {
				XStreamFactory.setBodyDto(resultSetDtoClass);
			} else {
				XStreamFactory.setBodyDto(data.getClass());
			}
			ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance(setting.getResponseFormat()).fromXML(in);
			processHeader(responseDto);
			if (data != null && responseDto.body != null) {
				XStreamFactory.setResponse(data, responseDto.body);
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			logger.log("ISEA0002", new Object[]{transactionId, name});
			IOUtils.closeQuietly(in);
			EntityUtils.consumeQuietly(entity);
		}
		return 1;
	}

	public Object getDto() {
		return this.data;
	}

	public void setDto(Object data) {
		this.data = data;
	}

}
