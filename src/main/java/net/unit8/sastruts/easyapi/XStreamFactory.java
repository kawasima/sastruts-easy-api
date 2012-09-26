package net.unit8.sastruts.easyapi;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import net.unit8.sastruts.easyapi.converter.RequestConverter;
import net.unit8.sastruts.easyapi.dto.RequestDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;
import net.unit8.sastruts.easyapi.xstream.io.CsvStreamXmlDriver;

import org.seasar.extension.jdbc.annotation.InOut;
import org.seasar.extension.jdbc.annotation.Out;
import org.seasar.extension.jdbc.annotation.ResultSet;
import org.seasar.framework.container.hotdeploy.HotdeployUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.ModifierUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XStreamFactory {
	private static Map<MessageFormat, XStream> xstreamCache = new HashMap<MessageFormat, XStream>();
	private static RequestConverter requestConverter = new RequestConverter();
	protected static final ConcurrentMap<Class<?>, DtoConfig> dtoConfigs = CollectionsUtil
			.newConcurrentHashMap();

	protected XStreamFactory() {
	}

	public static XStream getInstance(MessageFormat format) {
		XStream xstream = xstreamCache.get(format);
		if (xstream == null || HotdeployUtil.isHotdeploy()) {
			synchronized (XStreamFactory.class) {
				switch (format) {
				case XML:
					xstream = createDefaultXStream();
					break;
				case JSON:
					xstream = createJsonXStream();
					break;
				case CSV:
					xstream = createCsvXStream();
					break;
				}
				xstream.autodetectAnnotations(true);
				xstream.alias("request", RequestDto.class);
				xstream.alias("response", ResponseDto.class);
				xstream.registerConverter(requestConverter);
				xstreamCache.put(format, xstream);
			}
		}
		return xstream;
	}

	public static XStream getInstance() {
		return getInstance(MessageFormat.XML);
	}

	private static XStream createDefaultXStream() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		return new XStream(new Sun14ReflectionProvider(), new Xpp3DomDriver(new XmlFriendlyNameCoder("_-", "_")), loader) {
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
	}
	private static XStream createJsonXStream() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return new XStream(new Sun14ReflectionProvider(), new JettisonMappedXmlDriver(), loader) {
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
	}

	private static XStream createCsvXStream() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return new XStream(new Sun14ReflectionProvider(), new CsvStreamXmlDriver(), loader);
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
