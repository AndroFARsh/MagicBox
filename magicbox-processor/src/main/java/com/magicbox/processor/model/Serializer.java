package com.magicbox.processor.model;

import javax.tools.FileObject;

public interface Serializer {
	void serialize(Node node, FileObject output);
}
