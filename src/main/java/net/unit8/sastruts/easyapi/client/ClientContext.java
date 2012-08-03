package net.unit8.sastruts.easyapi.client;

public class ClientContext {
	private Object sendData;

	public ClientContext(Object sendData) {
		
	}
	
	public ClientContext to(String name) {
		return this;
	}
	
	public ClientContext from(String name) {
		return this;
	}
}
