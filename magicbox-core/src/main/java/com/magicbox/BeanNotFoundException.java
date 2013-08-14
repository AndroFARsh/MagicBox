package com.magicbox;

public class BeanNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -3649459237024977162L;

	public BeanNotFoundException(String tag, String id) {
		super("Cannot find bean with tag [" + tag + "] and class " + id);
	}

	public BeanNotFoundException(String id) {
		super("Cannot find bean with class " + id);
	}
}
