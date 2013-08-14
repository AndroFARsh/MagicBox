package com.magicbox.processor.model;

import java.util.Set;

public interface Node {
	enum NodeType {
		Root, Context, Bean, Alias, Property
	}

	String name();

	NodeType type();

	Node parent();

	Set<? extends Node> children();

	Node child(String name);

	NodeClass getNodeClass();
}
