package com.magicbox.demo.model;

import java.io.InputStream;

public interface Parser<T>  extends Named {
	T parse(InputStream stream);
}
