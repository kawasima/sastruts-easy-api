package net.unit8.sastruts.easyapi.dto;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("response")
public class ResponseDto extends EasyApiMessageDto implements Serializable {
	public ResponseDto() {
		header = new HeaderDto();
	}
}
