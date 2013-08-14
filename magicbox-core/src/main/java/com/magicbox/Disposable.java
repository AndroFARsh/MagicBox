package com.magicbox;

public interface Disposable {
	void dispose(ProgressCallback callback) throws InterruptedException;
}