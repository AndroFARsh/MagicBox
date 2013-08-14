package com.magicbox.demo;

import java.io.InputStream;

import com.magicbox.annotation.Property;
import com.magicbox.demo.model.FileManager;
import com.magicbox.demo.model.ResourceManager;

public class MockBaseJarResourceManager implements ResourceManager {
	private FileManager fileManager;
	
	@Property(id = "FileManager")
	public void setFileManager(FileManager manager) {
		fileManager = manager;
	}	
	
	
	public String getName(){
		return getClass().getSimpleName() + "[" + (fileManager != null ? fileManager.getName() : "NULL") + "]";
	}
	
	public String toString(){
		return getName();
	}

	@Override
	public InputStream getResource() {
		return null;
	}
}
