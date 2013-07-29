package com.magicbox;

public interface BeanFactory {
	void initialize(ProgressCallback callback) throws InterruptedException;

	void dispose(ProgressCallback callback) throws InterruptedException;

	boolean isInitialized();

	boolean isDisposed();
}
