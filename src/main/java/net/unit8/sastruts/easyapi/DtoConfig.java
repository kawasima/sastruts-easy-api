package net.unit8.sastruts.easyapi;

import java.lang.reflect.Field;
import java.util.List;

import org.seasar.framework.util.tiger.CollectionsUtil;

public class DtoConfig {
	public DtoConfig() {
		outFields = CollectionsUtil.newArrayList();
	}
	public List<Field> outFields;
	public Field resultsetField;
}
