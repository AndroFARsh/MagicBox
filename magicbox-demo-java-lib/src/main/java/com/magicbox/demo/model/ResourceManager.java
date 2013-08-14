package com.magicbox.demo.model;

import java.io.InputStream;

public interface ResourceManager extends Named {
	InputStream getResource();
}
