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

import javax.annotation.Resource;

import net.unit8.sastruts.easyapi.EasyApiException;
import net.unit8.sastruts.easyapi.EasyApiSystemException;
import net.unit8.sastruts.easyapi.MessageFormat;
import net.unit8.sastruts.easyapi.XStreamFactory;
import net.unit8.sastruts.easyapi.dto.ResponseDto;
import net.unit8.sastruts.easyapi.xstream.io.CsvMappedXmlDriver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.seasar.extension.jdbc.IterationCallback;
import org.seasar.extension.jdbc.IterationContext;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ResourceUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CachingMapper;

public class GetClientContext<T> extends ClientContext<T> {
	@Resource(name="easyApiSettingProvider")
	private EasyApiSettingProvider provider;

	private Class<T> dtoClass;

	public GetClientContext() {
		params = new ArrayList<NameValuePair>();
	}

	public GetClientContext<T> from(String name) throws EasyApiException {
		this.name = name;
		return this;
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
		EasyApiSetting setting = provider.get(name);
		if (provider.useMock) {
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
				in = new FileInputStream(dataFile);
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
						CsvMappedXmlDriver.setRoot(setting.getRootElement());
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

	@SuppressWarnings("unchecked")
	protected T executeQuery() throws EasyApiException {
		EasyApiSetting setting = provider.get(name);
		if (provider.useMock) return processMock();
		HttpEntity entity = null;
		try {
			entity = processHttpRequest();
			InputStream in = entity.getContent();
			if (StringUtils.equals(setting.getResponseType(), "plain")) {
				XStream xstream = XStreamFactory.getInstance(setting.getResponseFormat());
				T dto = (T)ClassUtil.newInstance(dtoClass);
				((CachingMapper)xstream.getMapper()).flushCache();
				xstream.alias(setting.getRootElement(), dtoClass);
				if (setting.getResponseFormat() == MessageFormat.CSV)
					CsvMappedXmlDriver.setRoot(setting.getRootElement());
				return (T)xstream.fromXML(in, dto);
			} else {
				XStreamFactory.setBodyDto(dtoClass);
				ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance(setting.getResponseFormat()).fromXML(in);
				processHeader(responseDto);
				if (dtoClass.isInstance(responseDto.body)) {
					return (T)responseDto.body;
				} else {
					throw new EasyApiSystemException("mismatch DTO type.");
				}
			}
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
		EasyApiSetting setting = provider.get(name);
		HttpGet method = new HttpGet(buildUri(setting));
		HttpParams httpParams = new BasicHttpParams();
		if (proxy != null)
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		HttpConnectionParams.setConnectionTimeout(httpParams, setting.getConnectionTimeout());
		HttpConnectionParams.setConnectionTimeout(httpParams, setting.getSocketTimeout());
		method.setParams(httpParams);
		HttpResponse response = client.execute(method);
		return response.getEntity();
	}

	@SuppressWarnings("unchecked")
	private <RESULT> RESULT processIterate(InputStream in, IterationCallback<T, RESULT> callback)
		throws EasyApiException {
		RESULT res = null;
		XStreamFactory.setBodyDto(dtoClass);
		EasyApiSetting setting = provider.get(name);
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

	@SuppressWarnings("unchecked")
	private T processMock() throws EasyApiException {
		EasyApiSetting setting = provider.get(name);
		File dir = ResourceUtil.getResourceAsFile("mock/" + name);
		if (!dir.exists()) {
			return null;
		}
		Collection<File> dataFiles = FileUtils.listFiles(dir, new String[]{"xml", "json", "csv"}, false);
		if (dataFiles.isEmpty())
			return null;

		File dataFile = dataFiles.toArray(new File[0])[RandomUtils.nextInt(dataFiles.size())];
		if (StringUtils.equals(setting.getResponseType(), "plain")) {
			XStream xstream = XStreamFactory.getInstance(setting.getResponseFormat());
			T dto = (T)ClassUtil.newInstance(dtoClass);
			((CachingMapper)xstream.getMapper()).flushCache();
			xstream.alias(setting.getRootElement(), dtoClass);
			if (setting.getResponseFormat() == MessageFormat.CSV)
				CsvMappedXmlDriver.setRoot(setting.getRootElement());
			return (T)xstream.fromXML(dataFile, dto);
		} else {
			XStreamFactory.setBodyDto(dtoClass);
			ResponseDto responseDto = (ResponseDto)XStreamFactory.getInstance(setting.getResponseFormat()).fromXML(dataFile);
			processHeader(responseDto);
			return (T)responseDto.body;
		}
	}

}
