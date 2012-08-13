package net.unit8.sastruts.easyapi.testapp.dto;

import org.seasar.extension.jdbc.annotation.Out;

public class BlogDto {
	@Out
	public String id;
	public String title;
	public String description;
}
