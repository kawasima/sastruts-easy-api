package net.unit8.sastruts.easyapi.client.handler;

import java.io.InputStream;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.client.EasyApiSetting;

public interface MessageHandler<T> {

	public abstract T handle(InputStream in, T dto, EasyApiSetting setting) throws EasyApiException;

	public abstract T handle(InputStream in, Class<T> dtoClass, EasyApiSetting setting) throws EasyApiException;

}
