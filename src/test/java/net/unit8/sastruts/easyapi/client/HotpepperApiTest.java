package net.unit8.sastruts.easyapi.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.testapp.dto.GourmetDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.TestContext;

@RunWith(Seasar2.class)
public class HotpepperApiTest {
	private TestContext ctx;

	@Test
	public void testGet() throws EasyApiException {
		EasyApiSettingProvider provider = ctx.getComponent(EasyApiSettingProvider.class);
		provider.setUseMock(false);
		EasyApiClient client = ctx.getComponent(EasyApiClient.class);
		BeanMap query = new BeanMap();
		query.put("key", "fd2c0d29b6a76bb1");
		query.put("large_area", "Z011");
		GourmetDto gourmet = client
				.get(GourmetDto.class, query)
				.from("hotpepper")
				.getSingleResult();
		assertThat(gourmet.apiVersion, is(1.26));
	}

}
