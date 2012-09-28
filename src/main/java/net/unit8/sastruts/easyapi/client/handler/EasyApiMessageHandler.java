package net.unit8.sastruts.easyapi.client.handler;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.MessageFormat;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.dto.ErrorDto;
import net.unit8.sastruts.easyapi.dto.FailureDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;
import net.unit8.sastruts.easyapi.xstream.io.CsvStreamXmlDriver;
import net.unit8.sastruts.easyapi.xstream.io.JettisonMappedXmlWrapperDriver;

import org.seasar.framework.util.ClassUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CachingMapper;

public class EasyApiMessageHandler<T> implements MessageHandler<T> {
	public Object handle() {
		XStreamFactory.setBodyDto(dtoClass);
		ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance(setting.getResponseFormat()).fromXML(in);
		processHeader(responseDto);
		if (dtoClass.isInstance(responseDto.body)) {
			return (T)responseDto.body;
		} else {
			throw new EasyApiSystemException("mismatch DTO type.");
		}
	}

	private void processHeader(ResponseDto responseDto) throws EasyApiException {
		if (responseDto.header != null) {
			if (responseDto.header.errors != null) {
				for (ErrorDto error : responseDto.header.errors) {
					throw new EasyApiSystemException(error.getMessage());
				}
			}
			EasyApiException ex = null;
			if (responseDto.header.failures != null) {
				for (FailureDto failure : responseDto.header.failures) {
					if (ex == null) {
						ex = new EasyApiException(failure.getCode(), failure.getMessage());
					} else {
						ex.append(new EasyApiException(failure.getCode(), failure.getMessage()));
					}
				}
			}
			if (ex != null) throw ex;
		}
	}

}
