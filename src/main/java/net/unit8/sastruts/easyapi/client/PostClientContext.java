package net.unit8.sastruts.easyapi.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import net.unit8.sastruts.easyapi.XStreamFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
	}

	public PostClientContext<T> to(String name) {
		this.name = name;
		return this;
	}

	public int execute() {
		EasyApiSetting setting = provider.get(name);
		HttpPost method = new HttpPost(setting.getHost() + setting.getPath());
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

		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		return 1;
	}
}
