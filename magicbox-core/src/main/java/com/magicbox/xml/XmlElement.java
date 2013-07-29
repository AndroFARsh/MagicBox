package com.magicbox.xml;

import java.util.List;

public interface XmlElement {
	int getId();

	String getKey();

	List<XmlElement> children();

	int childrenCount();

	int[] getAttrIds();

	String getAttrByIndex(int index);

	String getAttrById(int id);

	String[] getFreeAttrNames();

	String getFreeAttrByIndex(int index);

	String getFreeAttrByName(String name);
}
