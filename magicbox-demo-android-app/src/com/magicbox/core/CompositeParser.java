package com.magicbox.core;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.magicbox.demo.model.Parser;

public class CompositeParser implements Parser<Object> {
	Set<Parser<?>> parsers = new HashSet<Parser<?>>();
	@Override
	public String getName() {
		return getClass().getSimpleName() + parsers.toString();
	}

	@Override
	public Object parse(InputStream stream) {
		return null;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void addParser(Collection<Parser<?>> parsers){
		this.parsers.addAll(parsers);
	}	
}
