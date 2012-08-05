package net.unit8.sastruts.easyapi.testapp.dto;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class GourmetDto {
	@XStreamAlias("api_version")
	public double apiVersion;

	@XStreamAlias("results_start")
	public int resultsStart;

	@XStreamImplicit(itemFieldName="shop")
	public List<ShopDto> shopList;
}
