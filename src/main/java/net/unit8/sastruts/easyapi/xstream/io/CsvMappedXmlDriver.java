package net.unit8.sastruts.easyapi.xstream.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;
import com.thoughtworks.xstream.io.xml.StaxWriter;

public class CsvMappedXmlDriver extends AbstractDriver {
	private static ThreadLocal<String> root = new ThreadLocal<String>();
	public CsvMappedXmlDriver() {
	}
	public static void setRoot(String name) {
		root.set(name);
	}
	public HierarchicalStreamReader createReader(final Reader reader) {
		return new StaxReader(new QNameMap(),  new CsvXmlStreamReader(reader, root.get()), getNameCoder());
	}


	public HierarchicalStreamReader createReader(final InputStream in) {
		Reader reader;
		try {
			reader = new InputStreamReader(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new StreamException(e);
		}
		return new StaxReader(new QNameMap(),  new CsvXmlStreamReader(reader, root.get()), getNameCoder());
	}

	public HierarchicalStreamWriter createWriter(final Writer writer) {
		try {
			return new StaxWriter(new QNameMap(),
					new CsvXmlStreamWriter(writer), getNameCoder());
		} catch (XMLStreamException e) {
			throw new StreamException(e);
		}
	}

	public HierarchicalStreamWriter createWriter(OutputStream out) {
		return null;
	}

}
