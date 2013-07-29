package com.magicbox.demo;

import java.io.InputStream;

import com.magicbox.demo.model.Parser;

public class MockJarJsonParser implements Parser<Object> {
	@Override
	public Object parse(InputStream stream) {
		return null;
	}

	@Override
	public String getName() {
		return MockJarJsonParser.class.getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
