package net.unit8.sastruts.easyapi.testapp.dto;

import org.seasar.extension.jdbc.annotation.Out;

public class MuchMoneyDto {
	public int amount;
	@Out
	public String tokenGift;
}
