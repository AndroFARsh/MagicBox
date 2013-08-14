package com.magicbox;

public interface Initializable {
	void initialize(ProgressCallback callback) throws InterruptedException;
}