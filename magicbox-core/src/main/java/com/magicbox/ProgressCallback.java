package com.magicbox;

public interface ProgressCallback {
	// Used to change the status text (in case of complex operations)
	void onProgressText(float current, float max, String text);
}
