package com.magicbox.processor.model;

public class AliasNode extends BaseNode {

	@Override
	public String name() {
		StringBuilder name = new StringBuilder();
		if (getNodeClass().isExist(P.id)){
			name.append(getNodeClass().getString(P.id));
		}
		if (getNodeClass().isExist(P.tag)){
			if (name.length() > 0){
				name.append("|");
			}
			name.append(getNodeClass().getString(P.tag));
		}
		
		if (name.length() > 0){
			name.append(getClass().getSimpleName());
		}
		return name.toString();
	}

	@Override
	public NodeType type() {
		return NodeType.Root;
	}

}
