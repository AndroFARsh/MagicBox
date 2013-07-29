package com.magicbox.demo;

import com.magicbox.demo.model.FileManager;

public class MockAndroidFileManager implements FileManager {
	
	public String getName(){
		return MockAndroidFileManager.class.getSimpleName();
	}
	
	public String toString(){
		return getName();
	}

	@Override
	public File getFile(String path) {
		return null;
	}
}
