package net.unit8.sastruts.easyapi.converter;

import net.unit8.sastruts.easyapi.dto.HeaderDto;
import net.unit8.sastruts.easyapi.dto.RequestDto;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class RequestConverter implements Converter {
	public ThreadLocal<Class<?>> bodyDtoClass = new ThreadLocal<Class<?>>();

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return RequestDto.class == clazz;
	}

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		RequestDto requestDto = (RequestDto) source;
		writer.startNode("request");
		context.convertAnother(requestDto.header);
		writer.startNode("body");
		context.convertAnother(requestDto.body);
		writer.endNode();
		writer.endNode();
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		RequestDto requestDto = new RequestDto();
		reader.moveDown();
		requestDto.header = (HeaderDto)context.convertAnother(requestDto, HeaderDto.class);
		reader.moveUp();
		reader.moveDown();
		requestDto.body = context.convertAnother(requestDto, bodyDtoClass.get());
		reader.moveUp();
		return requestDto;
	}
}
