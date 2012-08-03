package net.unit8.sastruts.easyapi.client;

import net.unit8.sastruts.easyapi.EasyApiException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

public class EasyApiClient {
	private Object data;
	private static PoolingClientConnectionManager manager;
	HttpClient client;
		
	public void post(Object data) {
		this.data = data; 
	}
	
	public void to() {
		
	}
}
