package net.unit8.sastruts.easyapi.client.handler;

import java.io.InputStream;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.client.EasyApiSetting;
import net.unit8.sastruts.easyapi.dto.ErrorDto;
import net.unit8.sastruts.easyapi.dto.FailureDto;
import net.unit8.sastruts.easyapi.dto.ResponseDto;

public class EasyApiMessageHandler<T> implements MessageHandler<T> {
	@SuppressWarnings("unchecked")
	public T handle(InputStream in, Class<T> dtoClass, EasyApiSetting setting) throws EasyApiException{
		XStreamFactory.setBodyDto(dtoClass);
		ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance(setting.getResponseFormat()).fromXML(in);
		processHeader(responseDto);
		if (dtoClass.isInstance(responseDto.body)) {
			return (T)responseDto.body;
		} else {
			throw new EasyApiSystemException("mismatch DTO type.");
		}
	}

	public T handle(InputStream in, T dto, EasyApiSetting setting) throws EasyApiException {
		ResponseDto responseDto = (ResponseDto) XStreamFactory.getInstance(
				setting.getResponseFormat()).fromXML(in);
		processHeader(responseDto);
		if (dto != null && responseDto.body != null) {
			XStreamFactory.setResponse(dto, responseDto.body);
		}
		return dto;
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
