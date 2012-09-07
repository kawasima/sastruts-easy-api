package net.unit8.sastruts.easyapi;

import org.seasar.framework.util.StringUtil;

public enum RequestType {
	URL_ENCODE, XML;

	public static RequestType camelValueOf(String value) {
		return RequestType.valueOf(StringUtil.decamelize(value));
	}
}
