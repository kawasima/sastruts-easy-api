package net.unit8.sastruts.easyapi.xstream.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.json.JettisonStaxWriter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;
import com.thoughtworks.xstream.io.xml.StaxWriter;

public class JettisonMappedXmlWrapperDriver extends AbstractDriver {
	private static ThreadLocal<String> root = new ThreadLocal<String>();
	private final MappedXMLOutputFactory mof;
	private final MappedXMLInputFactory mif;
	private final MappedNamespaceConvention convention;
	private boolean useSerializeAsArray = true;

	public JettisonMappedXmlWrapperDriver() {
		this(new Configuration());
	}

	public JettisonMappedXmlWrapperDriver(final Configuration config) {
		this(config, true);
	}

	public JettisonMappedXmlWrapperDriver(final Configuration config,
			final boolean useSerializeAsArray) {
		mof = new MappedXMLOutputFactory(config);
		mif = new MappedXMLInputFactory(config);
		convention = new MappedNamespaceConvention(config);
		this.useSerializeAsArray = useSerializeAsArray;
	}

	public HierarchicalStreamReader createReader(final Reader reader) {
		try {
			String json = IOUtils.toString(reader);
			String rootName = root.get();
			if (rootName != null) {
				json = "{\"" + rootName + "\":" + json + "}";
			}
			return new StaxReader(new QNameMap(),
					mif.createXMLStreamReader(new StringReader(json)), getNameCoder());
		} catch (Exception e) {
			throw new StreamException(e);
		}
	}

	public HierarchicalStreamReader createReader(final InputStream input) {
		try {
			String json = IOUtils.toString(input);
			String rootName = root.get();
			if (rootName != null) {
				json = "{\"" + rootName + "\":" + json + "}";
			}
			return new StaxReader(new QNameMap(),
					mif.createXMLStreamReader(new StringReader(json)), getNameCoder());
		} catch (Exception e) {
			throw new StreamException(e);
		}
	}

	public HierarchicalStreamWriter createWriter(final Writer writer) {
		try {
			if (useSerializeAsArray) {
				return new JettisonStaxWriter(new QNameMap(),
						mof.createXMLStreamWriter(writer), getNameCoder(),
						convention);
			} else {
				return new StaxWriter(new QNameMap(),
						mof.createXMLStreamWriter(writer), getNameCoder());
			}
		} catch (final XMLStreamException e) {
			throw new StreamException(e);
		}
	}

	public HierarchicalStreamWriter createWriter(final OutputStream output) {
		try {
			if (useSerializeAsArray) {
				return new JettisonStaxWriter(new QNameMap(),
						mof.createXMLStreamWriter(output), getNameCoder(),
						convention);
			} else {
				return new StaxWriter(new QNameMap(),
						mof.createXMLStreamWriter(output), getNameCoder());
			}
		} catch (final XMLStreamException e) {
			throw new StreamException(e);
		}
	}

	public static void setRoot(String name) {
		root.set(name);
	}
}
