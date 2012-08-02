package net.unit8.sastruts.easyapi.dto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HeaderDto implements Serializable {
	public List<FailureDto> failures;
	public List<ErrorDto>   errors;
}
