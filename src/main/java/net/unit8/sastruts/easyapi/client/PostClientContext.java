package net.unit8.sastruts.easyapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Resource;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.log.Logger;

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
			String xml = XStreamFactory.getInstance().toXML(data);
			try {
				HttpEntity entity = new StringEntity(xml, setting.getEncoding());
				method.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				throw new IORuntimeException(e);
			}
		}

		String transactionId = UUID.randomUUID().toString();
		if (transactionIdName != null)
			method.addHeader(transactionIdName, transactionId);
		InputStream in = null;
		try {
			logger.log("ISEA0001", new Object[]{transactionId, name});
			if (provider.useMock) {
				in = getMockResponseStream();
			} else {
				HttpResponse response = client.execute(method);
				HttpEntity entity = response.getEntity();
				in = entity.getContent();
			}
			Class<?> resultSetDtoClass = XStreamFactory.getResutSetClass(data);
			if (resultSetDtoClass != null) {
				XStreamFactory.setBodyDto(resultSetDtoClass);
			} else {
				XStreamFactory.setBodyDto(data.getClass());
			}
			ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance().fromXML(in);
			processHeader(responseDto);
			if (data != null && responseDto.body != null) {
				XStreamFactory.setResponse(data, responseDto.body);
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			logger.log("ISEA0002", new Object[]{transactionId, name});
			IOUtils.closeQuietly(in);
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
