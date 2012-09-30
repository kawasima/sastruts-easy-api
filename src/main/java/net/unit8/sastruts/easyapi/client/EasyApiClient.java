package net.unit8.sastruts.easyapi.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class EasyApiClient {
	private HttpClient client;

	@Binding(bindingType=BindingType.MAY)
	public long timeToLive = 3L;

	@Binding(bindingType=BindingType.MAY)
	public String httpProxy;

	private HttpHost proxyHost;

	public EasyApiClient() {
	}

	@InitMethod
	public void init() {
		SchemeRegistry schemeRegistry = SchemeRegistryFactory.createSystemDefault();
		ClientConnectionManager connectionManager = new PoolingClientConnectionManager(
				schemeRegistry, timeToLive, TimeUnit.SECONDS);
		client = new DefaultHttpClient(connectionManager);
		if (httpProxy != null) {
			try {
				URI proxyUri = new URI(httpProxy);
				proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort(), proxyUri.getScheme());
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public <T>GetClientContext<T> get(Class<T> dtoClass) {
		return get(dtoClass, null);
	}

	public <T>GetClientContext<T> get(Class<T> dtoClass, Object query) {
		GetClientContext<T> ctx = SingletonS2Container.getComponent("getClientContext");

		ctx.setClient(client);
		ctx.setDtoClass(dtoClass);
		if (query != null) {
			ctx.setQuery(query);
		}
		if (proxyHost != null) {
			ctx.setProxy(proxyHost);
		}
		return ctx;
	}

	public <T>PostClientContext<T> post(T data) {
		return post(data, null);
	}

	public <T>PostClientContext<T> post(T data, Object query) {
		PostClientContext<T> ctx = SingletonS2Container.getComponent("postClientContext");
		ctx.setClient(client);
		ctx.setDto(data);
		if (query != null) {
			ctx.setQuery(query);
		}
		return ctx;
	}
}
