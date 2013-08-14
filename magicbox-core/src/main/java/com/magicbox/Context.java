package com.magicbox;

import java.util.List;

public interface Context {
	<T> T get(String beanId);

	<T> T get(String beanId, String tag);
	
	/**
	 * Returns all beans that are registered for given class under some tag.
	 * <b>IMPORTANT:</b> Does not return bean that is registered without tag!
	 */
	<T> List<? extends T> getTagged(String beanId, String... tagPattern);
	
	boolean hasBean(String beanId);
	
	boolean hasBean(String beanId, String tag);
}
