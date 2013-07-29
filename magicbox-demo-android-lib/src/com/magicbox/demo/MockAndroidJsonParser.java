package com.magicbox.demo;


import java.io.InputStream;

import com.magicbox.demo.model.Parser;

public class MockAndroidJsonParser implements Parser<Object>{
	@Override
	public Object parse(InputStream stream) {
		return null;
	}

	@Override
	public String getName() {
		return MockAndroidJsonParser.class.getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
