package com.magicbox.processor.model;

public class ContextNode extends BaseNode {
	@Override
	public String name() {
		if (getNodeClass().isExist(P.fullQulifiedName)) {
			return getNodeClass().getString(P.fullQulifiedName);
		}
		return super.name();
	}

	@Override
	public NodeType type() {
		return NodeType.Context;
	}
}
