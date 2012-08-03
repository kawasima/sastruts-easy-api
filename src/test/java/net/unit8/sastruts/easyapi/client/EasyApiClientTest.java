package net.unit8.sastruts.easyapi.client;

import java.util.List;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.testapp.dto.MuchMoneyDto;
import net.unit8.sastruts.easyapi.testapp.dto.UserDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.TestContext;

@RunWith(Seasar2.class)
public class EasyApiClientTest {
	private TestContext ctx;
	@Test
	public void testPost() {
		EasyApiClient client = ctx.getComponent(EasyApiClient.class);
		MuchMoneyDto muchMoneyDto = new MuchMoneyDto();
		client.post(muchMoneyDto).to("cityBank");
	}

	@Test
	public void testGet() throws EasyApiException {
		EasyApiClient client = ctx.getComponent(EasyApiClient.class);
		MuchMoneyDto muchMoneyDto = new MuchMoneyDto();
		BeanMap query = new BeanMap();
		query.put("id", "3");
		query.put("name", "hogehoge");
		List<UserDto> userList = client
				.get(UserDto.class, query)
				.from("cityBank")
				.getResultList();
	}
}
