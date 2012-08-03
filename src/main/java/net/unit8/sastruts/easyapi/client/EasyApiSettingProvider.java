package net.unit8.sastruts.easyapi.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.framework.container.SingletonS2Container;

public class EasyApiSettingProvider {
	public boolean useMock = false;
	private Map<String, EasyApiSetting> settings;

	public EasyApiSettingProvider() {
		this.settings = new HashMap<String, EasyApiSetting>();
	}

	public void register(String name) {
		EasyApiSetting setting = (EasyApiSetting)SingletonS2Container.getComponent(name);
		setting.setName(name);
		settings.put(name, setting);
	}

	public void register(List<String> nameList) {
		for (String name : nameList) {
			register(name);
		}
	}

	public EasyApiSetting get(String name) {
		return settings.get(name);
	}
}
