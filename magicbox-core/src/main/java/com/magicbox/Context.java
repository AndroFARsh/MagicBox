package com.magicbox;

import java.util.List;

public interface Context {
	<T> T get(Class<T> beanId);
	
	<T> T get(String beanId);

	<T> T get(Class<T> beanId, String tag);

	<T> T get(String beanId, String tag);
	
	/**
	 * Returns all beans that are registered for given class under some tag.
	 * <b>IMPORTANT:</b> Does not return bean that is registered without tag!
	 */
	<T> List<? extends T> getTagged(Class<T> beanId, String... tagPattern);
	
	<T> List<? extends T> getTagged(String beanId, String... tagPattern);
	
	boolean hasBean(Class<?> beanId);
	
	boolean hasBean(String beanId);
	
	boolean hasBean(Class<?> beanId, String tag);
	
	boolean hasBean(String beanId, String tag);
}
