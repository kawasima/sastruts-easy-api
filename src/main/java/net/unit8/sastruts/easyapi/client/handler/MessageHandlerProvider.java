package net.unit8.sastruts.easyapi.client.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandlerProvider<T> {
	private static Map<String, MessageHandler> handlerCache = new ConcurrentHashMap<String, MessageHandler>();

	public MessageHandler<T> get(String responseType) {
		MessageHandler handler = handlerCache.get(responseType);
		if (handler == null)
			handler = create(responseType);
		return handler;
	}

	protected MessageHandler<T> create(String responseType) {
		if (responseType == "plain") {
			return new PlainHandler<T>();
		} else if (responseType == "message") {
			return new EasyApiMessageHandler<T>();
		}
	}
}
