package com.magicbox;

public class BeanInitializeException extends RuntimeException {
	private static final long serialVersionUID = 2851176235752742567L;

	public BeanInitializeException(String message) {
		super(message);
	}

	public BeanInitializeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanInitializeException(Throwable cause) {
		super(cause);
	}
}
