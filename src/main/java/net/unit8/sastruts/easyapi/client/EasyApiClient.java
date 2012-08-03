package net.unit8.sastruts.easyapi.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class EasyApiClient {
	HttpClient client;
	public EasyApiClient() {
		client = new DefaultHttpClient();
	}

	public GetClientContext<?> get(Class<?> dtoClass) {
		return get(dtoClass, null);
	}

	public <T>GetClientContext<T> get(Class<T> dtoClass, Object query) {
		GetClientContext<T> ctx = new GetClientContext<T>(client, dtoClass);
		if (query == null) {
			ctx.setQuery(query);
		}
		return ctx;
	}

	public <T>PostClientContext<T> post(Object data) {
		return post(data, null);
	}

	public <T>PostClientContext<T> post(Object data, Object query) {
		PostClientContext<T> ctx = new PostClientContext<T>(client, data);
		if (query == null) {
			ctx.setQuery(query);
		}
		return ctx;
	}
}
