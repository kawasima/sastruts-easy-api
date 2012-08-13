package net.unit8.sastruts.easyapi.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
		try {
			muchMoneyDto.amount = 10000;
			client.post(muchMoneyDto).to("citiBank").execute();
			assertThat(muchMoneyDto.tokenGift, is("towel"));
			assertThat(muchMoneyDto.amount, is(10000));
		} catch (EasyApiException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGet() throws EasyApiException {
		EasyApiClient client = ctx.getComponent(EasyApiClient.class);
		BeanMap query = new BeanMap();
		query.put("id", "3");
		query.put("name", "hogehoge");
		UserDto user = client
				.get(UserDto.class, query)
				.from("familyRegister")
				.getSingleResult();
		assertThat(user.name, is("Yoshitaka Kawashima"));
	}

}
