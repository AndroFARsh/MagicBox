package com.magicbox.demo;

import com.magicbox.annotation.Alias;
import com.magicbox.annotation.Bean;
import com.magicbox.annotation.Property;
import com.magicbox.demo.model.Parser;

@Bean(alias={@Alias(id="resourceManager")})
public class MockJarResourceManager extends MockBaseJarResourceManager {
	private Parser<Object> jsonParser;
	
	@Property(id = "parser", tag ="jar_xml")
	public Parser<Object> xmlParser;
	
	@Property(id = "parser", tag ="jar_json")
	public void setParses(Parser<Object> jsonParser) {
		this.jsonParser = jsonParser;
	}
	
	@Override
	public String getName() {
		return super.getName()+
				"[jsonParser="+jsonParser!=null ? jsonParser.getName() : "NULL]"+
				"[xmlParser="+jsonParser!=null ? xmlParser.getName() : "NULL]";
	}
}
