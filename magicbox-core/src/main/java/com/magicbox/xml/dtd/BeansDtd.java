package com.magicbox.xml.dtd;



public interface BeansDtd {
	public static final DtdMappings INSTANCE = new DtdMappings() {
		{
			AttributeNameToId = A.NAME_TO_ID;
			AttributeIdToIndex = A.ID_TO_INDEX;
			AttributeIdToName = A.ID_TO_NAME;
			AttributeIndexToId = A.INDEX_TO_ID;
			TagAttributesCount = T._attCount;
			TagKeys = T._keys;
			TagNameToId = T.NAME_TO_ID;
			TagIdToName = T.ID_TO_NAME;
			hasPCDATA = T._hasPCDATA;
			hasArbitraryAttribute = T._hasArbitraryAttributes;
		}
	};
}
