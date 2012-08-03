package net.unit8.sastruts.easyapi.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class EasyApiClient {
	HttpClient client;
	public EasyApiClient() {
		client = new DefaultHttpClient();
	}

	@SuppressWarnings("rawtypes")
	public GetClientContext get(Class<?> dtoClass) {
		return get(dtoClass, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GetClientContext get(Class<?> dtoClass, Object query) {
		GetClientContext ctx = new GetClientContext(client, dtoClass);
		if (query == null) {
			ctx.setQuery(query);
		}
		return ctx;
	}

	public PostClientContext post(Object data) {
		return new PostClientContext(client, data);
	}
}
