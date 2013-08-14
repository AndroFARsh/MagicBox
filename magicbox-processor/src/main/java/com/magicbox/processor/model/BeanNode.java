package com.magicbox.processor.model;

import javax.lang.model.element.TypeElement;

public class BeanNode extends BaseNode {
	@Override
	public String name() {
		if (getNodeClass().isExist(P.element)){
			return getNodeClass().<TypeElement>get(P.element).getQualifiedName().toString();
		}
		return super.name();
	}

	@Override
	public NodeType type() {
		return NodeType.Bean;
	}
}
