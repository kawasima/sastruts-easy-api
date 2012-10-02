package net.unit8.sastruts.easyapi.client;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.MessageFormat;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.client.handler.MessageHandler;
import net.unit8.sastruts.easyapi.xstream.io.CsvStreamXmlDriver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.seasar.extension.jdbc.IterationCallback;
import org.seasar.extension.jdbc.IterationContext;
import org.seasar.framework.util.ResourceUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CachingMapper;

public class GetClientContext<T> extends ClientContext<T> {
	private Class<T> dtoClass;

	public GetClientContext() {
		params = new ArrayList<NameValuePair>();
	}

	public GetClientContext<T> from(String name) throws EasyApiException {
		this.name = name;
		return this;
	}

	public GetClientContext<T> addHeader(String name, String value) {
		return (GetClientContext<T>)super.addHeader(name, value);
	}

	@SuppressWarnings("unchecked")
	public List<T> getResultList() throws EasyApiException {
		T obj = executeQuery();
		if (Collection.class.isAssignableFrom(obj.getClass())) {
			return new ArrayList<T>((Collection<T>) obj);
		} else {
			List<T> results = new ArrayList<T>();
			results.add(obj);
			return results;
		}
	}

	public T getSingleResult() throws EasyApiException {
		return executeQuery();
	}

	public <RESULT> RESULT iterate(IterationCallback<T, RESULT> callback) throws EasyApiException {
		EasyApiSetting setting = settingProvider.get(name);
		if (settingProvider.useMock) {
			File dir = ResourceUtil.getResourceAsFile("mock/" + name);
			if (!dir.exists()) {
				return null;
			}
			Collection<File> dataFiles = FileUtils.listFiles(dir, new String[]{"xml", "json", "csv"}, false);
			if (dataFiles.isEmpty())
				return null;

			File dataFile = dataFiles.toArray(new File[0])[RandomUtils.nextInt(dataFiles.size())];
			InputStream in = null;
			try {
				XStream xstream = XStreamFactory.getInstance(setting.getResponseFormat());
				((CachingMapper)xstream.getMapper()).flushCache();
				xstream.alias(setting.getRootElement(), dtoClass);
				in = new FileInputStream(dataFile);
				if (setting.getResponseFormat() == MessageFormat.CSV)
					CsvStreamXmlDriver.setRoot(setting.getRootElement());
				return processIterate(in, callback);
			} catch(FileNotFoundException e) {
				throw new EasyApiSystemException(e);
			} finally {
				IOUtils.closeQuietly(in);
			}
		} else {
			HttpEntity entity = null;
			try {
				entity = processHttpRequest();
				InputStream in = entity.getContent();
				if (StringUtils.equals(setting.getResponseType(), "plain")) {
					XStream xstream = XStreamFactory.getInstance(setting.getResponseFormat());
					((CachingMapper)xstream.getMapper()).flushCache();
					xstream.alias(setting.getRootElement(), dtoClass);
					if (setting.getResponseFormat() == MessageFormat.CSV)
						CsvStreamXmlDriver.setRoot(setting.getRootElement());
					return processIterate(in, callback);
				} else {
					XStreamFactory.setBodyDto(dtoClass);
					return processIterate(in, callback);
				}
			} catch (IOException e) {
				throw new EasyApiSystemException(e);
			} finally {
				EntityUtils.consumeQuietly(entity);
			}
		}
	}

	protected T executeQuery() throws EasyApiException {
		EasyApiSetting setting = settingProvider.get(name);
		if (settingProvider.useMock) return processMock();
		HttpEntity entity = null;
		try {
			entity = processHttpRequest();
			InputStream in = entity.getContent();
			MessageHandler<T> handler = handlerProvider.get(setting.getResponseType());
			return handler.handle(in, dtoClass, setting);
		} catch (IOException e) {
			throw new EasyApiSystemException(e);
		} finally {
			EntityUtils.consumeQuietly(entity);
		}
	}

	public Class<T> getDtoClass() {
		return dtoClass;
	}

	public void setDtoClass(Class<T> dtoClass) {
		this.dtoClass = dtoClass;
	}

	private HttpEntity processHttpRequest() throws ClientProtocolException, IOException {
		EasyApiSetting setting = settingProvider.get(name);
		HttpGet method = new HttpGet(buildUri(setting));
		processRequestHeaders(method);
		HttpResponse response = client.execute(method);
		return response.getEntity();
	}

	@SuppressWarnings("unchecked")
	private <RESULT> RESULT processIterate(InputStream in, IterationCallback<T, RESULT> callback)
		throws EasyApiException {
		RESULT res = null;
		XStreamFactory.setBodyDto(dtoClass);
		EasyApiSetting setting = settingProvider.get(name);
		try {
			ObjectInputStream ois = XStreamFactory.getInstance(setting.getResponseFormat())
				.createObjectInputStream(new InputStreamReader(in, setting.getEncoding()));
			IterationContext context = new IterationContext();
			while(true) {
				try {
					T dto = (T)ois.readObject();
					if (dto == null) break;
					res = callback.iterate(dto, context);
					if (context.isExit())
						break;
				} catch (EOFException e) {
					break;
				}
			}
		} catch (Exception e) {
			throw new EasyApiSystemException(e);
		}
		return res;
	}

	private T processMock() throws EasyApiException {
		EasyApiSetting setting = settingProvider.get(name);
		File dir = ResourceUtil.getResourceAsFile("mock/" + name);
		if (!dir.exists()) {
			return null;
		}
		Collection<File> dataFiles = FileUtils.listFiles(dir, new String[]{"xml", "json", "csv"}, false);
		if (dataFiles.isEmpty())
			return null;

		File dataFile = dataFiles.toArray(new File[0])[RandomUtils.nextInt(dataFiles.size())];
		MessageHandler<T> handler = handlerProvider.get(setting.getResponseType());
		try {
			InputStream in = new FileInputStream(dataFile);
			return handler.handle(in, dtoClass, setting);
		} catch(FileNotFoundException e) {
			throw new EasyApiSystemException(e);
		}
	}
}
