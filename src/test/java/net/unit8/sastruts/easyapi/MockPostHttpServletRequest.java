package net.unit8.sastruts.easyapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;

import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;

public class MockPostHttpServletRequest extends MockHttpServletRequestImpl {
	private String body;
	public MockPostHttpServletRequest(ServletContext servletContext,
			String servletPath) {
		super(servletContext, servletPath);
	}
	
	public void setRequestBody(String body) {
		this.body = body;
	}
	
	@Override
	public ServletInputStream getInputStream() {
		String encoding = getCharacterEncoding();
		if (encoding == null)
			encoding = "UTF-8";
		byte[] b = null;
		try {
			b = body.getBytes(encoding);
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		final ByteArrayInputStream baos = new ByteArrayInputStream(b);
		return new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return baos.read();
			}
		};
	}
}
