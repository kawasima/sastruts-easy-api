package net.unit8.sastruts.easyapi.client.handler;

import net.unit8.sastruts.easyapi.MessageFormat;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.xstream.io.CsvStreamXmlDriver;
import net.unit8.sastruts.easyapi.xstream.io.JettisonMappedXmlWrapperDriver;

import org.seasar.framework.util.ClassUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CachingMapper;

public class PlainHandler<T> implements MessageHandler<T> {

	public T handle() {
		XStream xstream = XStreamFactory.getInstance(setting.getResponseFormat());
		T dto = (T)ClassUtil.newInstance(dtoClass);
		((CachingMapper)xstream.getMapper()).flushCache();
		xstream.alias(setting.getRootElement(), dtoClass);
		if (setting.getResponseFormat() == MessageFormat.CSV)
			CsvStreamXmlDriver.setRoot(setting.getRootElement());
		else if (setting.getResponseFormat() == MessageFormat.JSON) {
			JettisonMappedXmlWrapperDriver.setRoot(setting.getRootElement());
		}
		return (T)xstream.fromXML(in, dto);
	}
}
