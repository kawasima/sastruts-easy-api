package net.unit8.sastruts.easyapi.testapp.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("person")
public class PersonDto {
	public String name;
	public String email;
	public String city;
	@XStreamAlias("updated_at")
	public String updatedAt;
}
