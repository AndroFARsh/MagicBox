package com.magicbox;

public interface Initializable {
	void init(ProgressCallback callback) throws InterruptedException;
}