package net.unit8.sastruts.easyapi;

import net.unit8.sastruts.easyapi.converter.RequestConverter;
import net.unit8.sastruts.easyapi.dto.RequestDto;

import com.thoughtworks.xstream.XStream;

public class XStreamFactory {
	private static XStream xstream;
	private static RequestConverter requestConverter = new RequestConverter();

	protected XStreamFactory() {

	}

	public static synchronized XStream getInstance() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.autodetectAnnotations(true);
			xstream.alias("request", RequestDto.class);
			xstream.alias("response", RequestDto.class);
			xstream.registerConverter(requestConverter);
		}
		return xstream;
	}

	public static void setBodyDto(Class<?> bodyDto) {
		requestConverter.bodyDtoClass.set(bodyDto);
	}
}
