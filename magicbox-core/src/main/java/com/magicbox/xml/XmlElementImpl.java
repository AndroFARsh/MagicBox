package com.magicbox.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.magicbox.xml.dtd.DtdMappings;

class XmlElementImpl implements XmlElement {
	private String elementKey;
	private final int elementId;
	private final DtdMappings dtdMap;
	public String[] attrValues;
	public String[] freeAttrNames;
	public String[] freeAttrValues;
	public String content;
	private List<XmlElement> childNodes;

	static public class ParserInterrupted extends Exception {
		private static final long serialVersionUID = -8875313102374376998L;
	}

	public XmlElementImpl(DtdMappings dtd) {
		elementId = 0;
		dtdMap = dtd;
	}

	private XmlElementImpl(DtdMappings dtd, int eId, Attributes attributes) {
		dtdMap = dtd;
		elementId = eId;
		final int count = attributes.getLength();
		// 1. check how many attributes we know
		int knownAttrsCount = 0;
		final int[] attrIndexes = dtdMap.AttributeIdToIndex[eId];
		final String[] attrNames = new String[count];
		final int[] attrIds = new int[count];
		for (int i = 0; i < count; ++i) {
			final String attrName = attributes.getLocalName(i);
			final Integer id = (Integer) dtdMap.AttributeNameToId.get(attrName);
			if (id != null) {
				final int idValue = id.intValue();
				if (attrIndexes[idValue] != -1) {
					++knownAttrsCount;
					attrIds[i] = idValue;
				} else {
					attrIds[i] = -1;
				}
			} else {
				attrIds[i] = -1;
			}
			attrNames[i] = attrName;
		}
		attrValues = new String[dtdMap.TagAttributesCount[eId]];
		final boolean hasArbitraryAttribute = dtdMap.hasArbitraryAttribute[elementId];
		if ((count > knownAttrsCount) && hasArbitraryAttribute) {
			final int freeCount = count - knownAttrsCount;
			freeAttrNames = new String[freeCount];
			freeAttrValues = new String[freeCount];
		}
		int unknownElId = 0;
		for (int i = 0; i < count; ++i) {
			final int id = attrIds[i];
			if (id >= 0) {
				final int attrIndex = attrIndexes[id];
				final String value = attributes.getValue(i);// .intern();
				attrValues[attrIndex] = value;
				if (id == dtdMap.TagKeys[eId]) {
					elementKey = value;
				}
			} else if (hasArbitraryAttribute) {
				freeAttrNames[unknownElId] = attrNames[i];// .intern();
				freeAttrValues[unknownElId] = attributes.getValue(i);// .intern();
				++unknownElId;
			}
		}
	}

	public XmlElement startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String name;
		if ((localName != null) && (localName.length() > 0)) {
			name = localName;
		} else {
			name = qName;
		}

		final Integer i = (Integer) dtdMap.TagNameToId.get(name);
		if (i == null) {
			return null;
		}
		final XmlElement el = new XmlElementImpl(dtdMap, i.intValue(),
				attributes);
		children().add(el);
		return el;
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (!dtdMap.hasPCDATA[elementId]) {
			return;
		}

		if (content == null) {
			content = new String(ch, start, length);
		} else {
			content = content + (new String(ch, start, length));
		}
	}

	public String getContent() {
		return content;
	}

	@Override
	public String getKey() {
		return elementKey;
	}

	@Override
	public int getId() {
		return elementId;
	}

	@Override
	public List<XmlElement> children() {
		if (childNodes == null) {
			childNodes = new ArrayList<XmlElement>();
		}
		return childNodes;
	}

	@Override
	public int childrenCount() {
		return childNodes != null ? childNodes.size() : 0;
	}

	@Override
	public String getAttrByIndex(int index) {
		return attrValues[index];
	}

	@Override
	public boolean hasAttrById(int id) {
		return getAttrById(id) != null;
	}

	@Override
	public boolean hasAttrByIndex(int index) {
		return getAttrByIndex(index) != null;
	}

	@Override
	public String getAttrById(int id) {
		try {
			return attrValues[dtdMap.AttributeIdToIndex[elementId][id]];
		} catch (final IndexOutOfBoundsException exception) {
			throw new RuntimeException("Couldn't resolve index of attribute ["
					+ dtdMap.AttributeIdToName[id] + "] of tag <"
					+ dtdMap.TagIdToName[elementId] + ">");
		}
	}

	@Override
	public String[] getFreeAttrNames() {
		return freeAttrNames;
	}

	@Override
	public String getFreeAttrByIndex(int i) {
		return freeAttrValues[i];
	}

	@Override
	public String getFreeAttrByName(String name) {
		for (int i = 0; i < freeAttrNames.length; ++i) {
			if (freeAttrNames[i].equals(name)) {
				return freeAttrValues[i];
			}
		}

		return null;
	}

	@Override
	public int[] getAttrIds() {
		return dtdMap.AttributeIndexToId[elementId];
	}

	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append("[").append(dtdMap.TagIdToName[elementId]).append(":");

		for (int index = 0; index < attrValues.length; ++index) {
			if (attrValues[index] != null) {
				final int id = dtdMap.AttributeIndexToId[elementId][index];
				b.append(" ").append(dtdMap.AttributeIdToName[id]).append("=")
						.append(attrValues[index]);
			}
		}

		if (freeAttrNames != null) {
			for (int i = 0; i < freeAttrNames.length; ++i) {
				b.append(" (").append(freeAttrNames[i]).append(")=")
						.append(freeAttrValues[i]);
			}
		}

		return b.append("]").toString();
	}
}
