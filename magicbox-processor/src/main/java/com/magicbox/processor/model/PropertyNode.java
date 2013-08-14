package com.magicbox.processor.model;

import javax.lang.model.element.Element;

public class PropertyNode extends BaseNode {

	@Override
	public String name() {
		if (getNodeClass().isExist(P.element)){
			return getNodeClass().<Element>get(P.element).toString();
		}
		
		return super.name();
	}

	@Override
	public NodeType type() {
		return NodeType.Property;
	}

}
