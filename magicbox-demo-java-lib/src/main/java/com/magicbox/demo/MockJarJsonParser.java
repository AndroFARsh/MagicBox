package com.magicbox.demo;

import java.io.InputStream;

import com.magicbox.annotation.Bean;
import com.magicbox.demo.model.Parser;

@Bean(id ="parser", tag = "jar_json")
public class MockJarJsonParser implements Parser<Object> {
	@Override
	public Object parse(InputStream stream) {
		return null;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
