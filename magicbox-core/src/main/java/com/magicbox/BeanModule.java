package com.magicbox;

import java.util.List;

public interface BeanModule {
	<T> T get(String beanId);

	<T> T get(String beanId, String tag);
	
	/**
	 * Returns all beans that are registered for given class under some tag.
	 * <b>IMPORTANT:</b> Does not return bean that is registered without tag!
	 */
	<T> List<? extends T> getTagged(String beanId, String... tagPattern);
}
