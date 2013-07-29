package com.magicbox;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.magicbox.xml.DefaultXmlParser;
import com.magicbox.xml.XmlElement;
import com.magicbox.xml.XmlParser;
import com.magicbox.xml.XmlParser.XmlParserException;
import com.magicbox.xml.dtd.A;
import com.magicbox.xml.dtd.T;

public class XmlBeanFactory extends AbstractBeanFactory {
	private Resource[] resources;
	private XmlParser xmlParser = new DefaultXmlParser();
	
	public XmlBeanFactory(Resource... resources){
		this.resources = resources;
	}
	
	@Override
	protected final void registerBeanDefinitions() {
		super.registerBeanDefinitions();
		
		List<XmlElement> beans = new ArrayList<XmlElement>();
		for (final Resource res : resources){
			final InputStream is = res.open();
			if (is == null){
				continue;
			}
			
			try {
				XmlElement element = xmlParser.load(is);
				beans.addAll(element.children());
			} catch (XmlParserException e) {
			} finally {
				if (is != null){
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		
		for (XmlElement bean : beans){
			try {
				final String id = bean.getAttrById(A.Id);
				final String tag = bean.getAttrById(A.Tag);
				final Class<?> clazz = Class.forName(bean.getAttrById(A.Class));
				
				if (tag != null){
					register(id, tag, new BeanDefImpl(this, clazz, resolveProperty(bean)));
				} else {
					register(id, new BeanDefImpl(this, clazz, resolveProperty(bean)));
				}
				
				registerAliases(bean);
			} catch (ClassNotFoundException e) {
				throw new BeanInitializeException(e);
			}
		}
	}
	
	
	private void registerAliases(XmlElement bean) {
		if (bean.childrenCount() == 0){
			return;
		}
		
		final String id = bean.getAttrById(A.Id);
		final String tag = bean.getAttrById(A.Tag);
		
		for (XmlElement el : bean.children()){
			if (el.getId() == T.Alias){
				final String aliasId = el.getAttrById(A.Id);
				final String aliasTag = el.getAttrById(A.Tag);
				
				if (aliasTag != null){
					register(aliasId, aliasTag, new AliasBeanDef(id, tag));
				} else {
					register(aliasId, new AliasBeanDef(id, tag));
				}
			}
		}
	}

	private Set<Property> resolveProperty(XmlElement bean){
		if (bean.childrenCount() == 0){
			return Collections.emptySet();
		}
		final Set<Property> properties = new HashSet<XmlBeanFactory.Property>();
		for (XmlElement el : bean.children()){
			if (el.getId() == T.Property){
				final String name = el.getAttrById(A.Name);
				final String ref = el.getAttrById(A.Ref);
				final String refTag = el.getAttrById(A.RefTag);
				final String taggedBy = el.getAttrById(A.TaggedBy);
				if (taggedBy != null){
					properties.add(new TeggedByReferenceProperty(name, ref, taggedBy));
				} else{
					properties.add(new ReferenceProperty(name, ref, refTag));
				}
			}
		}
		return properties;
	}

	private interface Property {
		String name();
		
		boolean setProperty(BeanModule beans, Class<?> clazz, Object bean);
	}
	
	private abstract static class BaseProperty implements Property {
		final String name;
		
		protected BaseProperty(String name) {
			this.name = name.intern();
		}
		
		@Override
		public final  String name() {
			return name;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@SuppressWarnings("unused")
		Method findMethod(String name, Class<?> clazz){
			return findMethod("",name, clazz);
		}
		
		Method findMethod(String prefix, String name, Class<?> clazz, Class<?>... args){
			final String methodName = new StringBuilder(prefix)
			.append(Character.toUpperCase(name().charAt(0)))
			.append(name().substring(1)).toString();
			
			try {
				for (Method method : clazz.getDeclaredMethods()){
					if (!methodName.equals(method.getName()) ||
						args.length != method.getParameterTypes().length){
						continue;
					}
					
					boolean rightMethod = true;
					final Class<?>[] argsTypes = method.getParameterTypes();
					for (int i=0; i < args.length; ++i){
						if (argsTypes[i] == args[i]){
							continue;
						} 
								
						if (!argsTypes[i].isAssignableFrom(args[i])){
							rightMethod = false;
							break;
						}
					}
					
					if (rightMethod){
						return method;
					}
				}
			} catch (SecurityException e) {
			}
			return null;
		}
		
		boolean invoke(Method method, Object obj, Object... args){
			try {
				if (method != null){
					method.invoke(obj, args);
					return true;
				}
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
				throw new BeanInitializeException(e);
			} catch (IllegalAccessException e) {
				throw new BeanInitializeException(e);
			} catch (InvocationTargetException e) {
				throw new BeanInitializeException(e);
			}
			return false;
		}
		
		boolean assignField(Class<?> clazz, Object obj, Object value) {
			try {
				Field field = clazz.getField(name());
				if (field.getType().isAssignableFrom(value.getClass())){
					field.set(obj, value);
					return true;
				}
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
				throw new BeanInitializeException(e);
			} catch (IllegalAccessException e) {
				throw new BeanInitializeException(e);
			}
			return false;
		}
		
		boolean assignSetter(Class<?> clazz, Object obj, Object value) {
			Method method = findMethod("set", name(), clazz, value.getClass());
			return invoke(method, obj, value);
		}
	}
	
	private static class TeggedByReferenceProperty extends BaseProperty {
		private static final String TAGGED_ALL = "*";
		
		private final String id;
		private final String pattern;

		TeggedByReferenceProperty(String name, String id, String pattern){
			super(name);
			this.id = id;
			this.pattern =  !TAGGED_ALL.equals(pattern) ? pattern : null;
		}

		
		public boolean setProperty(BeanModule beans, Class<?> clazz, Object bean) {
			final List<?> tagged = (pattern != null) ? beans.getTagged(id, pattern) : beans.getTagged(id);
			if (tagged.isEmpty()){
				return true;
			}
				
			if (invoke(findMethod("add", name(), clazz, Collection.class), bean, tagged)){
				return true;
			}
			
			boolean result = false;
			for (Object value : tagged){
				if (invoke(findMethod("add", name(), clazz, value.getClass()), bean, value)){ 
					result = true;
				}
			}
			
			return result;
		}
	}
		
	
	private static class ReferenceProperty extends BaseProperty {

		private final String id;
		private final String tag;

		@SuppressWarnings("unused")
		ReferenceProperty(String name, String id){
			this(name, id, null);
		}
		
		ReferenceProperty(String name, String id, String tag){
			super(name);
			this.id = id;
			this.tag = tag;
		}

		@Override
		public boolean setProperty(BeanModule beans, Class<?> clazz, Object bean) {
			final Object value = getValue(beans);
			
			if (assignField(clazz, bean, value))
				return true;
			
			if (assignSetter(clazz, bean, value))
				return true;
			
			return false;
		}

		Object getValue(BeanModule beans) {
			if (tag != null){
				return beans.get(id, tag);
			}
				
			return beans.get(id);
		}
	}
	
	private static class BeanDefImpl extends AbstractBeanDef {
		BeanModule beans;
		Class<?> clazz;
		Set<Property> properties;
		
		BeanDefImpl(BeanModule beans, Class<?> clazz, Set<Property> properties){
			this.beans = beans;
			this.clazz = clazz;
			this.properties = properties;
		}
		
		@Override
		public Object create() {
			Constructor<?> constructor;
			try {
				constructor = clazz.getConstructor();
				return constructor.newInstance();
			} catch (SecurityException e) {
				throw new BeanInitializeException(e);
			} catch (NoSuchMethodException e) {
				throw new BeanInitializeException(e);
			} catch (IllegalArgumentException e) {
				throw new BeanInitializeException(e);
			} catch (InstantiationException e) {
				throw new BeanInitializeException(e);
			} catch (IllegalAccessException e) {
				throw new BeanInitializeException(e);
			} catch (InvocationTargetException e) {
				throw new BeanInitializeException(e);
			}
		}

		@Override
		public void assemble(Object bean) {
			for (Property p : properties){
				if (!p.setProperty(beans, clazz, bean)){
					throw new BeanInitializeException("Property \""+p.name()+"\" not found ");
				}
			}
		}
	} 
}
