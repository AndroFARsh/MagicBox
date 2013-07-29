package com.magicbox.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.magicbox.xml.dtd.BeansDtd;

public class DefaultXmlParser implements XmlParser {
	private static final SAXParser PARSER;
	static {
		try {
			PARSER = SAXParserFactory.newInstance().newSAXParser();
		} catch (final java.lang.Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public synchronized XmlElement load(final InputStream is) throws XmlParserException {
		try {
			final XmlParser parse = new XmlParser();
			PARSER.parse(is, parse);
			return parse.document;
		} catch (final SAXException e) {
			throw new XmlParserException(e);
		} catch (final IOException e) {
			throw new XmlParserException(e);
		}
	}

	static class XmlParser extends DefaultHandler {
		Stack<XmlElement> tagStack = new Stack<XmlElement>();
		XmlElement root = new XmlElementImpl(BeansDtd.INSTANCE);
		XmlElement document;

		@Override
		public void startDocument() throws SAXException {
			// Starts the document, reset the stack
			tagStack.removeAllElements();
			tagStack.push(root);
		}

		@Override
		public void endDocument() throws SAXException {
			if (tagStack.size() > 1) {
				throw new SAXException("XML: Tag Stack has " + tagStack.size() + " open elements");
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// put to the stack any element (event if it null)
			if (tagStack.empty()) {
				throw new SAXException("XML: Stack is empty");
			}

			final XmlElementImpl r = (XmlElementImpl) tagStack.peek();
			final XmlElement element = r.startElement(uri, localName, qName, attributes);
			tagStack.push(element);
			if (r.equals(root)) {
				document = tagStack.peek();
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (tagStack.empty()) {
				throw new SAXException("XML: Stack is empty");
			}

			final XmlElement r = tagStack.peek();
			if (r != null) {
				((XmlElementImpl) r).characters(ch, start, length);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (tagStack.empty()) {
				throw new SAXException("XML: Stack is empty");
			}
			tagStack.pop();
		}

		// Just throw exceptions
		@Override
		public void warning(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}
	}
}