package net.unit8.sastruts.easyapi;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

import net.unit8.sastruts.easyapi.converter.RequestConverter;
import net.unit8.sastruts.easyapi.dto.RequestDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

import org.seasar.extension.jdbc.annotation.InOut;
import org.seasar.extension.jdbc.annotation.Out;
import org.seasar.extension.jdbc.annotation.ResultSet;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.ModifierUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XStreamFactory {
	private static XStream xstream;
	private static RequestConverter requestConverter = new RequestConverter();
	protected static final ConcurrentMap<Class<?>, DtoConfig> dtoConfigs = CollectionsUtil
			.newConcurrentHashMap();

	protected XStreamFactory() {
	}

	public static synchronized XStream getInstance() {
		if (xstream == null) {
			xstream = new XStream(new Xpp3DomDriver(new XmlFriendlyNameCoder("_-", "_"))) {
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new MapperWrapper(next) {
						@SuppressWarnings("rawtypes")
						public boolean shouldSerializeMember(Class definedIn,
								String fieldName) {
							try {
								return definedIn != Object.class
										|| realClass(fieldName) != null;
							} catch (CannotResolveClassException cnrce) {
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

	public static DtoConfig configureDto(Object dto) {
		final Field[] fields = ClassUtil.getDeclaredFields(dto.getClass());
		DtoConfig config = new DtoConfig();
		for (int i = 0; i < fields.length; ++i) {
			final Field field = fields[i];
			if (!ModifierUtil.isInstanceField(field)) {
				continue;
			}
			if (field.getAnnotation(ResultSet.class) != null) {
				getInstance().omitField(dto.getClass(), field.getName());
				config.resultsetField = field;
			} else if (field.getAnnotation(Out.class) != null) {
				getInstance().omitField(dto.getClass(), field.getName());
				config.outFields.add(field);
			} else if (field.getAnnotation(InOut.class) != null) {
				config.outFields.add(field);
			}
		}
		dtoConfigs.put(dto.getClass(), config);
		return config;
	}

	public static void setOmitFields(Object dto) {
		DtoConfig dtoConfig = dtoConfigs.get(dto.getClass());
		if (dtoConfig == null) {
			dtoConfig = configureDto(dto);
		}
	}

	public static void setResponse(Object dto, Object response) {
		DtoConfig dtoConfig = dtoConfigs.get(dto.getClass());
		if (dtoConfig == null) {
			dtoConfig = configureDto(dto);
		}
		if (dtoConfig.resultsetField != null
				&& dtoConfig.resultsetField.getDeclaringClass().equals(
						response.getClass())) {
			FieldUtil.set(dtoConfig.resultsetField, dto, response);
		} else if (!dtoConfig.outFields.isEmpty()
				&& dto.getClass().equals(response.getClass())) {
			for (Field outField : dtoConfig.outFields) {
				Object value = FieldUtil.get(outField, response);
				FieldUtil.set(outField, dto, value);
			}
		}
	}

	public static Class<?> getResutSetClass(Object dto) {
		DtoConfig dtoConfig = dtoConfigs.get(dto.getClass());
		if (dtoConfig == null) {
			dtoConfig = configureDto(dto);
		}
		if (dtoConfig.resultsetField != null) {
			return dtoConfig.resultsetField.getDeclaringClass();
		} else {
			return null;
		}
	}

}
