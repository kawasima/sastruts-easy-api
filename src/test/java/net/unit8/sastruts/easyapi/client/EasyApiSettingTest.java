package net.unit8.sastruts.easyapi.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.container.impl.ComponentDefImpl;
import org.seasar.framework.container.impl.InitMethodDefImpl;
import org.seasar.framework.container.impl.PropertyDefImpl;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.TestContext;

@RunWith(Seasar2.class)
public class EasyApiSettingTest {
	private TestContext ctx;
	@Test
	public void testWarning() {
		ComponentDef componentDef = new ComponentDefImpl(EasyApiSetting.class, "setting1");
		componentDef.addPropertyDef(new PropertyDefImpl("responseFormat", "CSV"));
		componentDef.addInitMethodDef(new InitMethodDefImpl("validate"));
		componentDef.setInstanceDef(InstanceDefFactory.SINGLETON);
		ctx.register(componentDef);
		ctx.getComponent("setting1");
	}
}
