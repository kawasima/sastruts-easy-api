package net.unit8.sastruts.easyapi.xstream.io;

import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CsvXmlStreamWriter implements XMLStreamWriter {
	private Writer writer;
	public CsvXmlStreamWriter(Writer writer) {
		this.writer = writer;
	}
	public void close() throws XMLStreamException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}
	public void flush() throws XMLStreamException {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}
	public NamespaceContext getNamespaceContext() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	public String getPrefix(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	public Object getProperty(String arg0) throws IllegalArgumentException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	public void setDefaultNamespace(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void setNamespaceContext(NamespaceContext arg0)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void setPrefix(String arg0, String arg1) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeAttribute(String arg0, String arg1)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeAttribute(String arg0, String arg1, String arg2)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeAttribute(String arg0, String arg1, String arg2,
			String arg3) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeCData(String arg0) throws XMLStreamException {
		System.out.println(arg0);
	}
	public void writeCharacters(String arg0) throws XMLStreamException {
		System.out.println(arg0);
	}
	public void writeCharacters(char[] arg0, int arg1, int arg2)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeComment(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeDTD(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeDefaultNamespace(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeEmptyElement(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeEmptyElement(String arg0, String arg1)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeEmptyElement(String arg0, String arg1, String arg2)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeEndDocument() throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeEndElement() throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeEntityRef(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeNamespace(String arg0, String arg1)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeProcessingInstruction(String arg0)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeProcessingInstruction(String arg0, String arg1)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeStartDocument() throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeStartDocument(String arg0) throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeStartDocument(String arg0, String arg1)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ

	}
	public void writeStartElement(String arg0) throws XMLStreamException {
		System.out.println(arg0);

	}
	public void writeStartElement(String arg0, String arg1)
			throws XMLStreamException {
		System.out.println(arg1);

	}
	public void writeStartElement(String arg0, String arg1, String arg2)
			throws XMLStreamException {
		System.out.println(arg1);
	}

}
