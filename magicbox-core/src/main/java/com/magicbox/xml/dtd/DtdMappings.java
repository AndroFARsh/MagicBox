package com.magicbox.xml.dtd;

import java.util.Hashtable;

@SuppressWarnings({ "rawtypes" })
public class DtdMappings {
	public Hashtable AttributeNameToId;

	public int[][] AttributeIdToIndex;

	public String[] AttributeIdToName;

	public int[][] AttributeIndexToId;

	public int[] TagAttributesCount;

	public int[] TagKeys;

	public Hashtable TagNameToId;

	public String[] TagIdToName;

	public boolean[] hasPCDATA;

	public boolean[] hasArbitraryAttribute;
}
