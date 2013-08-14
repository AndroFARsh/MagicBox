package com.magicbox.processor.model;

public class RootNode extends BaseNode {
	@Override
	public NodeType type() {
		return NodeType.Root;
	}
	
	void merge(RootNode node){
		if (node != null){
			for (BaseNode n : node.children()){
				attachChild(n);
			}
		}
	}
}
