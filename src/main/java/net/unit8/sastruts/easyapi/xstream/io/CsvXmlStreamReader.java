package net.unit8.sastruts.easyapi.xstream.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.io.StreamException;

public class CsvXmlStreamReader implements XMLStreamReader {
	private BufferedReader reader;
	private int event;
	private String currentValue;
	private Map<String, String> currentRow = new HashMap<String, String>();
	private List<String> titleColumns;
	private int rowIndex = 0;
	private int colIndex = 0;
	private NodeType nodeType = NodeType.ROOT;
	private String rootName;
	private QName name;

	private static enum NodeType {
		ROOT, ROW, COLUMN
	}
	public CsvXmlStreamReader(Reader reader, String rootName) {
		this.rootName = rootName;
		this.reader = IOUtils.toBufferedReader(reader);
		try {
			titleColumns = new ArrayList<String>(readLine());
		} catch (IOException e) {
			throw new StreamException(e);
		}
		event = START_DOCUMENT;
	}

	private LinkedList<String> readLine() throws IOException {
		LinkedList<String> columns = new LinkedList<String>();
		StringBuilder line = new StringBuilder(1024);
		while (true) {
			String l = reader.readLine();
			if (l != null)
				line.append(l);
			if (line.length() > 0 && StringUtils.countMatches(line.toString(), "\"") % 2 == 0) {
				splitRecord(line.toString(), columns);
				return columns;
			} else if (l != null) {
				line.append("\n");
				continue;
			}
			if (l == null)
				break;
		}
		return null;
	}

	private void splitRecord(String src, LinkedList<String> dest) {
		String[] columns = src.split(",");
		int maxlen = columns.length;
		int startPos, endPos, columnlen;
		StringBuffer buff = new StringBuffer(1024);
		String column;
		boolean isInString, isEscaped;
		for (int index = 0; index < maxlen; index++) {
			column = columns[index];
			if ((endPos = column.indexOf("\"")) < 0) {
				dest.addLast(column);
			} else {
				isInString = (endPos == 0);
				isEscaped = false;
				columnlen = column.length();
				buff.setLength(0);
				startPos = (isInString) ? 1 : 0;
				while (startPos < columnlen) {
					if (0 <= (endPos = column.indexOf("\"", startPos))) {
						buff.append((startPos < endPos) ? column.substring(
								startPos, endPos) : isEscaped ? "\"" : "");
						isEscaped = !isEscaped;
						isInString = !isInString;
						startPos = ++endPos;
					} else {
						buff.append(column.substring(startPos));
						if (isInString && index < maxlen - 1) {
							column = columns[++index];
							columnlen = column.length();
							buff.append(",");
							startPos = 0;
						} else {
							break;
						}
					}
				}
				dest.addLast(buff.toString());
			}
		}
	}

	public void close() throws XMLStreamException {
		try {
			reader.close();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	public int getAttributeCount() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getAttributeLocalName(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public QName getAttributeName(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getAttributeNamespace(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getAttributePrefix(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getAttributeType(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getAttributeValue(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getAttributeValue(String arg0, String arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getCharacterEncodingScheme() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getEncoding() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public int getEventType() {
		return event;
	}

	public String getLocalName() {
		return getName().getLocalPart();
	}

	public Location getLocation() {
	       return new Location() {
	            public int getCharacterOffset() {
	                return 0;
	            }

	            public int getColumnNumber() {
	                return colIndex;
	            }

	            public int getLineNumber() {
	                return rowIndex;
	            }

	            public String getPublicId() {
	                return null;
	            }

	            public String getSystemId() {
	                return null;
	            }
	       };
	}

	public QName getName() {
		return name;
	}

	public NamespaceContext getNamespaceContext() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public int getNamespaceCount() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getNamespacePrefix(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getNamespaceURI() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getNamespaceURI(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getNamespaceURI(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getPIData() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getPITarget() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getPrefix() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Object getProperty(String arg0) throws IllegalArgumentException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getText() {
		return currentValue;
	}

	public char[] getTextCharacters() {
		return currentValue == null ? null : currentValue.toCharArray();
	}

	public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
			throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public int getTextLength() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public int getTextStart() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	public String getVersion() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public boolean hasName() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean hasNext() throws XMLStreamException {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean hasText() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean isAttributeSpecified(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean isCharacters() {
		return event == CHARACTERS;
	}

	public boolean isEndElement() {
		return event == END_ELEMENT;
	}

	public boolean isStandalone() {
		return false;
	}

	public boolean isStartElement() {
		return event == START_ELEMENT;
	}

	public boolean isWhiteSpace() {
		return false;
	}

	public int next() throws XMLStreamException {
		try {
		switch (event) {
		case START_DOCUMENT:
			name = new QName("linked-list");
			nodeType = NodeType.ROOT;
			event = START_ELEMENT;
			break;
		case START_ELEMENT:
			switch (nodeType) {
			case ROOT:
				LinkedList<String> columns = readLine();
				if (columns != null) {
					nodeType = NodeType.ROW;
					name =  new QName(rootName);
					event = START_ELEMENT;
					currentRow.clear();
					for (int i=0; i < titleColumns.size(); i++) {
						currentRow.put(titleColumns.get(i), columns.get(i));
					}
				} else {
					nodeType = NodeType.ROOT;
					event = END_ELEMENT;
				}
				break;
			case ROW:
				nodeType = NodeType.COLUMN;
				name =  new QName(titleColumns.get(colIndex));
				event = START_ELEMENT;
				break;
			case COLUMN:
				name =  new QName(titleColumns.get(colIndex));
				currentValue = currentRow.get(titleColumns.get(colIndex));
				event = CHARACTERS;
				break;
			}
			break;
		case END_ELEMENT:
			switch (nodeType) {
			case ROOT:
				event = END_DOCUMENT;
			case ROW:
				colIndex = 0;
				rowIndex++;
				LinkedList<String> columns = readLine();
				if (columns != null) {
					currentRow.clear();
					for (int i=0; i < titleColumns.size(); i++) {
						currentRow.put(titleColumns.get(i), columns.get(i));
					}
					name =  new QName(rootName);
					event = START_ELEMENT;
				} else {
					nodeType = NodeType.ROOT;
					event = END_ELEMENT;
				}
				break;
			case COLUMN:
				colIndex++;
				if (colIndex < titleColumns.size()) {
					name =  new QName(titleColumns.get(colIndex));
					event = START_ELEMENT;
				} else {
					nodeType = NodeType.ROW;
					name =  new QName(rootName);
					event = END_ELEMENT;
				}
				break;
			}
			break;
		case CHARACTERS:
			event = END_ELEMENT;
			currentValue = null;
			break;
		}
		return event;
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	public int nextTag() throws XMLStreamException {
		int event = next();
		while (event != START_ELEMENT && event != END_ELEMENT) {
			event = next();
		}
		return event;
	}

	public void require(int arg0, String arg1, String arg2)
			throws XMLStreamException {
	}

	public boolean standaloneSet() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public String getElementText() throws XMLStreamException {
		event = CHARACTERS;
		return currentValue;
	}

}
