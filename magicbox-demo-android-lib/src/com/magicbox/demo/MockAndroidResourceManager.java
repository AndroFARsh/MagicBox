package com.magicbox.demo;

import java.io.InputStream;

import com.magicbox.demo.model.FileManager;
import com.magicbox.demo.model.ResourceManager;


public class MockAndroidResourceManager implements ResourceManager {
	private FileManager fileManager;
	
	public void setFileManager(FileManager manager) {
		fileManager = manager;
	}
	
	public String getName(){
		return MockAndroidResourceManager.class.getSimpleName() + "[" + (fileManager != null ? fileManager.getName() : "NULL") + "]";
	}
	
	public String toString(){
		return getName();
	}

	@Override
	public InputStream getResource() {
		// TODO Auto-generated method stub
		return null;
	}
}
