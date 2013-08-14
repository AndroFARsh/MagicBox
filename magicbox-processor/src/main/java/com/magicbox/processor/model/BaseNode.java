package com.magicbox.processor.model;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseNode implements Node {
	private Node parent;
	private final Set<BaseNode> children = new HashSet<BaseNode>();
	private NodeClass nodeClass = new NodeClassImpl();

	@Override
	public Node parent() {
		return parent;
	}

	public Set<? extends BaseNode> children() {
		return children;
	}

	public Node child(String name) {
		for (Node child : children) {
			if (child.name().equals(name)) {
				return child;
			}
		}
		return null;
	}

	@Override
	public NodeClass getNodeClass() {
		return nodeClass;
	}

	@Override
	public int hashCode() {
		return name().hashCode();
	}

	@Override
	public String toString() {
		return name();
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass()
				&& obj.hashCode() == hashCode();
	}

	BaseNode attachChild(BaseNode node) {
		if (node != null) {
			Node child = child(node.name());
			if (child != null){
				for (BaseNode n : node.children){
					((BaseNode)child).attachChild(n);
				}
				node = (BaseNode) child;
			}
			
			node.parent = this;
			children.add(node);
		}
		return node;
	}
}
