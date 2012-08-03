package net.unit8.sastruts.easyapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.SyncBasicHttpParams;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.exception.IORuntimeException;

public class PostClientContext<T> extends ClientContext<T> {
	@Resource
	private EasyApiSettingProvider provider;

	private HttpClient client;
	private String name;
	private Object data;

	public PostClientContext(HttpClient client, Object data) {
		this.client = client;
		this.data = data;
		provider = SingletonS2Container.getComponent(EasyApiSettingProvider.class);
		params = new SyncBasicHttpParams();
	}

	public PostClientContext<T> to(String name) {
		this.name = name;
		return this;
	}

	public int execute() throws EasyApiException {
		if (provider.useMock) return processMock();
		EasyApiSetting setting = provider.get(name);
		HttpPost method = new HttpPost(setting.getHost() + processDynamicPath(setting.getPath()));
		if (data != null) {
			String xml = XStreamFactory.getInstance().toXML(data);
			try {
				HttpEntity entity = new StringEntity(xml, setting.getEncoding());
				method.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				throw new IORuntimeException(e);
			}
		}

		try {
			HttpResponse response = client.execute(method);
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance().fromXML(in);
			processHeader(responseDto);

		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		return 1;
	}

	private int processMock() {
		return 1;
	}
}
