package net.unit8.sastruts.easyapi.dto;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("request")
public class RequestDto extends EasyApiMessageDto implements Serializable {
	public RequestDto() {
		header = new HeaderDto();
	}
}
