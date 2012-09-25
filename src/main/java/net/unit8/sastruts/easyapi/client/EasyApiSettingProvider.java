package net.unit8.sastruts.easyapi.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class EasyApiSettingProvider {
	public boolean useMock = false;
	private Set<String> settingNames;

	public EasyApiSettingProvider() {
		settingNames = new HashSet<String>();
	}

	@InitMethod
	public void autoRegister() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		ComponentDef[] defs = container.findAllComponentDefs(EasyApiSetting.class);
		for (ComponentDef def : defs) {
			register(def.getComponentName());
		}
	}
	public void register(String name) {
		settingNames.add(name);
	}

	public void register(List<String> nameList) {
		for (String name : nameList) {
			register(name);
		}
	}

	public EasyApiSetting get(String name) {
		return SingletonS2Container.getComponent(name);
	}

	public void setUseMock(boolean useMock) {
		this.useMock = useMock;
	}
}
