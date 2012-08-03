package net.unit8.sastruts.easyapi.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.TestContext;

@RunWith(Seasar2.class)
public class EasyApiClientTest {
	private TestContext ctx;
	@Test
	public void testPost() {
		EasyApiClient client = ctx.getComponent(EasyApiClient.class);
		muchMoneyDto = new MuchMoneyDto();
		client.post(muchMoneyDto).to("CityBank");
	}
}
