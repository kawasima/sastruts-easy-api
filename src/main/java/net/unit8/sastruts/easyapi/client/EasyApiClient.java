package net.unit8.sastruts.easyapi.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.seasar.framework.container.SingletonS2Container;

public class EasyApiClient {
	HttpClient client;
	public EasyApiClient() {
		client = new DefaultHttpClient();
	}

	public GetClientContext<?> get(Class<?> dtoClass) {
		return get(dtoClass, null);
	}

	public <T>GetClientContext<T> get(Class<T> dtoClass, Object query) {
		GetClientContext<T> ctx = SingletonS2Container.getComponent("getClientContext");

		ctx.setClient(client);
		ctx.setDtoClass(dtoClass);
		if (query == null) {
			ctx.setQuery(query);
		}
		return ctx;
	}

	public <T>PostClientContext<T> post(Object data) {
		return post(data, null);
	}

	public <T>PostClientContext<T> post(Object data, Object query) {
		PostClientContext<T> ctx = SingletonS2Container.getComponent("postClientContext");
		ctx.setClient(client);
		ctx.setDto(data);
		if (query == null) {
			ctx.setQuery(query);
		}
		return ctx;
	}
}
