package com.magicbox.xml;

import java.util.Collections;
import java.util.List;

public interface XmlElement {
	int getId();

	String getKey();

	List<XmlElement> children();

	boolean hasAttrById(int id);

	boolean hasAttrByIndex(int index);

	int childrenCount();

	int[] getAttrIds();

	String getAttrByIndex(int index);

	String getAttrById(int id);

	String[] getFreeAttrNames();

	String getFreeAttrByIndex(int index);

	String getFreeAttrByName(String name);

	XmlElement EMPTY = new XmlElement() {

		@Override
		public boolean hasAttrByIndex(int index) {
			return false;
		}

		@Override
		public boolean hasAttrById(int id) {
			return false;
		}

		@Override
		public String getKey() {
			return null;
		}

		@Override
		public int getId() {
			return -1;
		}

		@Override
		public String[] getFreeAttrNames() {
			return new String[0];
		}

		@Override
		public String getFreeAttrByName(String name) {
			return null;
		}

		@Override
		public String getFreeAttrByIndex(int index) {
			return null;
		}

		@Override
		public int[] getAttrIds() {
			return new int[0];
		}

		@Override
		public String getAttrByIndex(int index) {
			return null;
		}

		@Override
		public String getAttrById(int id) {
			return null;
		}

		@Override
		public int childrenCount() {
			return 0;
		}

		@Override
		public List<XmlElement> children() {
			return Collections.emptyList();
		}
	};

}
