package com.magicbox.core;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.magicbox.demo.model.ResourceManager;

public class CompositeResourceManager implements ResourceManager {
	Set<ResourceManager> resManagers = new HashSet<ResourceManager>();
	
	@Override
	public String getName() {
		return getClass().getSimpleName() + resManagers.toString();
	}
	
	@Override
	public InputStream getResource() {
		return null;
	}
	
	public void addResourceManager(ResourceManager managers){
		resManagers.add(managers);
	}
}
