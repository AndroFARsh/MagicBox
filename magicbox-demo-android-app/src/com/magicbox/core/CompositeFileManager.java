package com.magicbox.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.magicbox.demo.model.FileManager;

public class CompositeFileManager implements FileManager {
	Set<FileManager> fileManagers = new HashSet<FileManager>();
	@Override
	public String getName() {
		return getClass().getSimpleName() + fileManagers.toString();
	}

	@Override
	public File getFile(String path) {
		return null;
	}

	public void addFileManagers(Collection<FileManager> managers){
		fileManagers.addAll(managers);
	}
	
	public void addFileManager(FileManager managers){
		fileManagers.add(managers);
	}
}
