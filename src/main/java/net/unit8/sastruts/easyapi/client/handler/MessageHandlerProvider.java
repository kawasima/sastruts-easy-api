package net.unit8.sastruts.easyapi.client.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;

import net.unit8.sastruts.easyapi.EasyApiSystemException;

@Component(instance=InstanceType.SINGLETON)
public class MessageHandlerProvider<T> {
	private Map<String, MessageHandler<T>> handlerCache = new ConcurrentHashMap<String, MessageHandler<T>>();

	public MessageHandler<T> get(String responseType) {
		MessageHandler<T> handler = handlerCache.get(responseType);
		if (handler == null)
			handler = create(responseType);
		return handler;
	}

	protected MessageHandler<T> create(String responseType) {
		if (StringUtils.equals(responseType, "plain")) {
			return new PlainHandler<T>();
		} else if (StringUtils.equals(responseType, "message")) {
			return new EasyApiMessageHandler<T>();
		}
		throw new EasyApiSystemException("No such handler: " + responseType);
	}
}
