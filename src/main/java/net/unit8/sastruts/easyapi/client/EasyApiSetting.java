package net.unit8.sastruts.easyapi.client;

import net.unit8.sastruts.easyapi.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.container.annotation.tiger.InstanceType;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;

@Component(instance=InstanceType.SINGLETON)
public class EasyApiSetting {
	private static final Logger logger = Logger.getLogger(EasyApiSetting.class);
	/** Setting name */
	private String scheme = "http";
	private int port = 80;
	private String name;
	private String host;
	private String path;
	private int retryCount = 0;
	private int retryInterval = 0;
	private int connectionTimeout = 2 * 1000;
	private int socketTimeout = 10 * 1000;
	private String encoding = "UTF-8";
	private String responseType = "message";
	private String requestType = "message";
	private MessageFormat requestFormat = MessageFormat.XML;
	private MessageFormat responseFormat = MessageFormat.XML;

	private String rootElement;

	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
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

	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
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
	public MessageFormat getRequestFormat() {
		return requestFormat;
	}
	public void setRequestFormat(MessageFormat requestFormat) {
		this.requestFormat = requestFormat;
	}
	public MessageFormat getResponseFormat() {
		return responseFormat;
	}
	public void setResponseFormat(MessageFormat responseFormat) {
		this.responseFormat = responseFormat;
	}

	@InitMethod
	public void validate() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		ComponentDef[] defs = container.findComponentDefs(EasyApiSetting.class);
		String path = null;
		for (ComponentDef def : defs) {
			EasyApiSetting setting = (EasyApiSetting)def.getComponent();
			if (this == setting) {
				setName(def.getComponentName());
				path = def.getContainer().getPath();
				break;
			}
		}
		if (responseFormat == MessageFormat.CSV && !StringUtils.equals(responseType, "plain"))
			logger.log("WSEA0003", new Object[]{path, name});
		if (StringUtils.equals(responseType, "plain") && StringUtils.isEmpty(rootElement))
			logger.log("WSEA0004", new Object[]{path, name});
	}
}
