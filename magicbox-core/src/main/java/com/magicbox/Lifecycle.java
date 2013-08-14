package com.magicbox;

public interface Lifecycle extends Initializable, Disposable {
	boolean isInitialized();

	boolean isDisposed();
}
