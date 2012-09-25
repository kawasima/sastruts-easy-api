package net.unit8.sastruts.easyapi.client;

import net.unit8.sastruts.easyapi.MessageFormat;

public class EasyApiSetting {
	/** Setting name */
	private String scheme = "http";
	private String name;
	private String host;
	private String path;
	private int retryCount = 0;
	private int retryInterval = 0;
	private int connectionTimeout = 2 * 1000;
	private int socketTimeout = 10 * 1000;
	private String encoding = "UTF-8";
	private String responseType = "message";
	private MessageFormat requestType = MessageFormat.XML;
	private MessageFormat responseFormat = MessageFormat.XML;

	private String rootElement;

	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public MessageFormat getRequestType() {
		return requestType;
	}
	public void setRequestType(MessageFormat requestType) {
		this.requestType = requestType;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getRootElement() {
		return rootElement;
	}
	public void setRootElement(String rootElement) {
		this.rootElement = rootElement;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getSocketTimeout() {
		return socketTimeout;
	}
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	public MessageFormat getResponseFormat() {
		return responseFormat;
	}
	public void setResponseFormat(MessageFormat responseFormat) {
		this.responseFormat = responseFormat;
	}
}
