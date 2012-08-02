package net.unit8.sastruts.easyapi;

import net.unit8.sastruts.easyapi.dto.RequestDto;

import com.thoughtworks.xstream.XStream;

public class XStreamFactory {
	private static XStream xstream; 
	
	protected XStreamFactory() {
		
	}
	
	public static synchronized XStream getInstance() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.autodetectAnnotations(true);
			xstream.alias("request", RequestDto.class);
			xstream.alias("response", RequestDto.class);
		}
		return xstream;
	}
}
