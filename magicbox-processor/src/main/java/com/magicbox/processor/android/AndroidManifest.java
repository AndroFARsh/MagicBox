package com.magicbox.processor.android;

import java.util.Collections;
import java.util.List;

public class AndroidManifest {

	private final String applicationPackage;
	private final List<String> componentQualifiedNames;
	private final List<String> permissionQualifiedNames;
	private final String applicationClassName;
	private final boolean libraryProject;
	private final boolean debugabble;

	public static AndroidManifest createManifest(String applicationPackage, String applicationClassName, List<String> componentQualifiedNames, List<String> permissionQualifiedNames, boolean debugabble) {
		return new AndroidManifest(false, applicationPackage, applicationClassName, componentQualifiedNames, permissionQualifiedNames, debugabble);
	}

	public static AndroidManifest createLibraryManifest(String applicationPackage) {
		return new AndroidManifest(true, applicationPackage, "", Collections.<String> emptyList(), Collections.<String> emptyList(), false);
	}

	private AndroidManifest(boolean libraryProject, String applicationPackage, String applicationClassName, List<String> componentQualifiedNames, List<String> permissionQualifiedNames, boolean debuggable) {
		this.libraryProject = libraryProject;
		this.applicationPackage = applicationPackage;
		this.applicationClassName = applicationClassName;
		this.componentQualifiedNames = componentQualifiedNames;
		this.permissionQualifiedNames = permissionQualifiedNames;
		this.debugabble = debuggable;
	}

	public String getApplicationPackage() {
		return applicationPackage;
	}

	public List<String> getComponentQualifiedNames() {
		return Collections.unmodifiableList(componentQualifiedNames);
	}

	public List<String> getPermissionQualifiedNames() {
		return Collections.unmodifiableList(permissionQualifiedNames);
	}

	public String getApplicationClassName() {
		return applicationClassName;
	}

	public boolean isLibraryProject() {
		return libraryProject;
	}

	public boolean isDebuggable() {
		return debugabble;
	}

}
