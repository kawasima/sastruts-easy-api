package net.unit8.sastruts.easyapi;

import net.unit8.sastruts.easyapi.converter.RequestConverter;
import net.unit8.sastruts.easyapi.dto.RequestDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XStreamFactory {
	private static XStream xstream;
	private static RequestConverter requestConverter = new RequestConverter();

	protected XStreamFactory() {

	}

	public static synchronized XStream getInstance() {
		if (xstream == null) {
			xstream = new XStream() {
	            protected MapperWrapper wrapMapper(MapperWrapper next) {
	                return new MapperWrapper(next) {
	                    @SuppressWarnings("rawtypes")
						public boolean shouldSerializeMember(Class definedIn, String fieldName) {
	                        try {
	                            return definedIn != Object.class || realClass(fieldName) != null;
	                        } catch(CannotResolveClassException cnrce) {
	                            return false;
	                        }
	                    }
	                };
	            }
			};
			xstream.autodetectAnnotations(true);
			xstream.alias("request", RequestDto.class);
			xstream.alias("response", ResponseDto.class);
			xstream.registerConverter(requestConverter);
		}
		return xstream;
	}

	public static void setBodyDto(Class<?> bodyDto) {
		requestConverter.bodyDtoClass.set(bodyDto);
	}
}
