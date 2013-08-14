package com.magicbox.processor.model;

import java.util.Set;
import javax.lang.model.element.TypeElement;

import com.magicbox.xml.XmlElement;

public interface Builder {
	Node build(XmlElement element);

	Node build(Set<? extends TypeElement> elements);
}
