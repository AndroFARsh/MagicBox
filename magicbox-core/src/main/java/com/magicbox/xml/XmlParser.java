package com.magicbox.xml;

import java.io.InputStream;

import com.magicbox.xml.dtd.DtdMappings;

public interface XmlParser {
	XmlElement load(InputStream stream, DtdMappings dtdMappings)
			throws XmlParserException;

	public static class XmlParserException extends Exception {
		private static final long serialVersionUID = -202587385501957214L;

		public XmlParserException() {
		}

		public XmlParserException(Throwable couse) {
			super(couse);
		}

		public XmlParserException(String message) {
			super(message);
		}

	}
}
