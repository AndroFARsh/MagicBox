package com.magicbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseContext implements Context, Lifecycle {
	private final Context[] parents;

	private Map<String, Object> beans = new HashMap<String, Object>();
	private Map<String, Map<String, Object>> tags = new HashMap<String, Map<String, Object>>();

	private Map<String, BeanDef> beanDefs = new HashMap<String, BeanDef>();
	private List<BeanDef> beanDefsVector = new ArrayList<BeanDef>();
	private List<String> beanIdsVector = new ArrayList<String>();
	private List<String> beanTagsVector = new ArrayList<String>();

	private Map<String, Map<String, BeanDef>> tagDefs = new HashMap<String, Map<String, BeanDef>>();

	private int nBeans;

	private List beansVector = new ArrayList();
	private Map beanIndices = new HashMap();
	private boolean[][] dependsOn;
	private int[] initSequence;
	private int countDepsFor = -1;

	private boolean flag = false;
	private boolean initialized = false;
	private boolean disposed = false;

	public BaseContext(Context... parents) {
		this.parents = parents != null ? parents : new Context[0];
	}

	protected void registerBeanDefinitions() {
		flag = true;
	};

	@Override
	public final boolean hasBean(String id) {
		Object bean = beans.get(id);

		if (parents != null) {
			for (int i = 0; (i < parents.length) && (bean == null); ++i) {
				try {
					bean = parents[i].get(id);
				} catch (final BeanNotFoundException exception) {
					// keep calm and carry on
				}
			}
		}

		return bean != null;
	}

	public final <T> T get(Class<T> id) {
		return (T) get(id.getCanonicalName());
	}

	@Override
	public final <T> T get(String id) {
		Object bean = beans.get(id);

		if (parents != null) {
			for (int i = 0; (i < parents.length) && (bean == null); ++i) {
				try {
					bean = parents[i].get(id);
				} catch (final BeanNotFoundException exception) {
					// keep calm and carry on
				}
			}
		}

		if (bean == null) {
			throw new BeanNotFoundException(id);
		}

		if (countDepsFor >= 0) {
			final Integer depIndex = (Integer) beanIndices.get(bean);
			if (depIndex != null) {
				dependsOn[countDepsFor][depIndex.intValue()] = true;
			}
		}

		if (bean instanceof AliasBeanDef) {
			final AliasBeanDef alias = (AliasBeanDef) bean;
			bean = (alias.tag == null ? get(alias.id)
					: get(alias.id, alias.tag));
		}

		return (T) bean;
	}

	@Override
	public final boolean hasBean(String id, String tag) {
		Object bean = null;

		if (tags.containsKey(tag)) {
			bean = tags.get(tag).get(id);
		}

		for (int i = 0; (i < parents.length) && (bean == null); ++i) {
			try {
				bean = parents[i].get(id, tag);
			} catch (final BeanNotFoundException exception) {
				// keep calm and carry on
			}
		}

		return bean != null;
	}

	public final <T> T get(Class<T> id, String tag) {
		return (T) get(id.getCanonicalName(), tag);
	}

	@Override
	public final <T> T get(String id, String tag) {
		Object bean = null;

		if (tags.containsKey(tag)) {
			bean = tags.get(tag).get(id);
		}

		for (int i = 0; (i < parents.length) && (bean == null); ++i) {
			try {
				bean = parents[i].get(id, tag);
			} catch (final BeanNotFoundException exception) {
				// keep calm and carry on
			}
		}

		if (bean == null) {
			throw new BeanNotFoundException(tag, id);
		}

		if (countDepsFor >= 0) {
			final Integer beanIndex = (Integer) beanIndices.get(bean);
			if (beanIndex != null) {
				dependsOn[countDepsFor][beanIndex.intValue()] = true;
			}
		}

		if (bean instanceof AliasBeanDef) {
			final AliasBeanDef alias = (AliasBeanDef) bean;
			bean = (alias.tag == null ? get(alias.id)
					: get(alias.id, alias.tag));
		}

		return (T) bean;
	}

	public Set<BeanInfo> beanInfos() {
		if (!isInitialized() || isDisposed()) {
			return Collections.emptySet();
		}

		final Set<BeanInfo> beanInfos = new HashSet<BeanInfo>();
		for (Entry<String, Object> entry : beans.entrySet()) {
			boolean alias = false;
			Object bean = entry.getValue();
			if (bean instanceof AliasBeanDef) {
				final AliasBeanDef aliasDef = (AliasBeanDef) bean;
				bean = (aliasDef.tag == null ? get(aliasDef.id) : get(
						aliasDef.id, aliasDef.tag));
			}
			beanInfos.add(new BeanInfo(entry.getKey(), alias, bean));
		}

		// tagged
		for (Entry<String, Map<String, Object>> tagEntry : tags.entrySet()) {
			for (Entry<String, Object> beanEntry : beans.entrySet()) {
				boolean alias = false;
				Object bean = beanEntry.getValue();
				if (bean instanceof AliasBeanDef) {
					final AliasBeanDef aliasDef = (AliasBeanDef) bean;
					bean = (aliasDef.tag == null ? get(aliasDef.id) : get(
							aliasDef.id, aliasDef.tag));
				}
				beanInfos.add(new BeanInfo(beanEntry.getKey(), tagEntry
						.getKey(), alias, bean));
			}
		}

		return beanInfos;
	}

	private boolean matches(String tag, String... patterns) {
		if (patterns == null || patterns.length == 0)
			return true;

		for (String pattern : patterns) {
			if (tag.matches(pattern)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final <T> List<? extends T> getTagged(String id, String... patterns) {
		final List<T> beans = new ArrayList<T>();

		for (final String tag : tags.keySet()) {
			if (!matches(tag, patterns)) {
				continue;
			}

			final Map<String, Object> tagBeans = tags.get(tag);
			for (final String eId : tagBeans.keySet()) {
				if (eId.equals(id)) {
					try {
						beans.add(this.<T> get(id, tag));
					} catch (final BeanNotFoundException exception) {
						// keep calm and carry on
					}
				}
			}
		}

		for (int i = 0; i < parents.length; ++i) {
			beans.addAll(parents[i].<T> getTagged(id, patterns));
		}

		return beans;
	}

	protected final BeanDef getDef(String id) {
		return (BeanDef) beanDefs.get(id);
	}

	protected final BeanDef getDef(String id, String tag) {
		return (BeanDef) ((Hashtable) tagDefs.get(tag)).get(id);
	}

	protected final void register(Class<?> id, BeanDef beanDef) {
		register(id.getCanonicalName(), beanDef);
	}

	protected final void register(String id, BeanDef beanDef) {
		if (beanDefs.containsKey(id)) {
			final BeanDef oldDef = (BeanDef) beanDefs.get(id);
			final int index = beanDefsVector.indexOf(oldDef);
			beanDefsVector.set(index, beanDef);
		} else {
			beanDefsVector.add(beanDef);
			beanIdsVector.add(id);
			beanTagsVector.add(null);
		}

		beanDefs.put(id, beanDef);
	}

	protected final void register(Class<?> id, String tag, BeanDef beanDef) {
		register(id.getCanonicalName(), tag, beanDef);
	}

	protected final void register(String id, String tag, BeanDef beanDef) {
		Map<String, BeanDef> defs = tagDefs.get(tag);
		if (defs == null) {
			defs = new HashMap<String, BeanDef>();
			tagDefs.put(tag, defs);
		}

		if (defs.containsKey(id)) {
			final BeanDef def = (BeanDef) defs.get(id);
			final int index = beanDefsVector.indexOf(def);
			beanDefsVector.set(index, beanDef);
		} else {
			beanDefsVector.add(beanDef);
			beanIdsVector.add(id);
			beanTagsVector.add(tag);
		}

		defs.put(id, beanDef);
	}

	@Override
	public synchronized final void initialize(ProgressCallback callback)
			throws InterruptedException {
		if (initialized) {
			return;
		}

		flag = false;
		registerBeanDefinitions();
		if (!flag) {
			throw new RuntimeException(
					"afterBeansAssembled isn't call super.registerBeanDefinitions");
		}

		nBeans = beanDefsVector.size();
		dependsOn = new boolean[nBeans][nBeans];

		createBeans();
		assembleBeans();

		flag = false;
		afterBeansAssembled();
		if (!flag) {
			throw new RuntimeException(
					"afterBeansAssembled isn't call super.afterBeansAssembled");
		}

		initializeBeans(callback);
		flag = false;
		afterBeansInitialized();
		if (!flag) {
			throw new RuntimeException(
					"afterBeansInitialized isn't call super.afterBeansAssembled");
		}

		initialized = true;

		// Clean up some memory

		beanDefs = null;
		tagDefs = null;
		beanIndices = null;
		dependsOn = null;
		beanDefsVector = null;
		beanIdsVector = null;
		beanTagsVector = null;
		dependsOn = null;
	}

	private void createBeans() {
		for (int iBean = 0; iBean < beanDefsVector.size(); ++iBean) {
			final BeanDef def = beanDefsVector.get(iBean);
			final String beanId = beanIdsVector.get(iBean);
			final String beanTag = (String) beanTagsVector.get(iBean);

			final Object bean = def.create();

			if (beanTag == null) {
				beans.put(beanId, bean);
			} else {
				if (tags.get(beanTag) == null) {
					tags.put(beanTag, new HashMap<String, Object>());
				}

				tags.get(beanTag).put(beanId, bean);
			}

			beansVector.add(bean);
			beanIndices.put(bean, (beansVector.size() - 1));
		}
	}

	private void assembleBeans() {
		for (int iBean = 0; iBean < beanDefsVector.size(); ++iBean) {
			final BeanDef def = beanDefsVector.get(iBean);
			final Object bean = beansVector.get(iBean);
			countDepsFor = iBean;
			def.assemble(bean);
		}

		countDepsFor = -1;
	}

	private void initializeBeans(ProgressCallback callback)
			throws InterruptedException {
		final boolean[] initialized = new boolean[nBeans];
		initSequence = new int[nBeans];
		int initCount = 0;

		// Initialize beans in dependency order

		NEXTCYCLE: while (initCount < nBeans) {
			NEXTBEAN: for (int iBean = 0; iBean < nBeans; ++iBean) {
				if (initialized[iBean]) {
					continue NEXTBEAN;
				}

				// Bean not initialized, check dependencies

				for (int iDep = 0; iDep < nBeans; ++iDep) {
					if (dependsOn[iBean][iDep] && !initialized[iDep]) {
						continue NEXTBEAN; // Dependency not initialized yet
					}
				}

				// All dependencies initialized, can initialize this bean

				final Object bean = beansVector.get(iBean);
				final BeanDef beanDef = (BeanDef) beanDefsVector.get(iBean);
				beanDef.init(bean, callback);
				initialized[iBean] = true;
				initSequence[initCount] = iBean;
				++initCount;

				continue NEXTCYCLE;
			}

			// Dependency cycle detected, breaking it with first bean in cycle

			NEXTBEAN: for (int iBean = 0; iBean < nBeans; ++iBean) {
				if (initialized[iBean]) {
					continue NEXTBEAN;
				}

				final Object bean = beansVector.get(iBean);
				final BeanDef beanDef = (BeanDef) beanDefsVector.get(iBean);
				beanDef.init(bean, callback);
				initialized[iBean] = true;
				initSequence[initCount] = iBean;
				++initCount;

				continue NEXTCYCLE;
			}

			throw new RuntimeException("This should not happen");
		}
	}

	protected void afterBeansAssembled() {
		flag = true;
	}

	protected void afterBeansInitialized() {
		flag = true;
	}

	@Override
	public final synchronized void dispose(ProgressCallback callback)
			throws InterruptedException {
		if (disposed) {
			throw new RuntimeException("Already closed!");
		}

		disposeBeans(callback);
		beans = null;
		beansVector = null;
		initialized = false;

		disposed = true;
	}

	protected void disposeBeans(ProgressCallback callback)
			throws InterruptedException {
		for (int iSequence = nBeans - 1; iSequence >= 0; --iSequence) {
			final Object bean = beansVector.get(initSequence[iSequence]);
			if (bean instanceof Disposable) {
				((Disposable) bean).dispose(callback);
			}
		}
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	public interface BeanDef {
		Object create();

		void assemble(Object bean);

		void init(Object bean, ProgressCallback callback)
				throws InterruptedException;
	}

	public static abstract class AbstractBeanDef implements BeanDef {
		@Override
		public void assemble(Object bean) {
		}

		@Override
		public void init(Object bean, ProgressCallback callback)
				throws InterruptedException {
			if (bean instanceof Initializable) {
				((Initializable) bean).initialize(callback);
			}
		}
	}

	public static final class AliasBeanDef implements BeanDef {
		private String id;
		private String tag;

		public AliasBeanDef(String id, String tag) {
			this.id = id;
			this.tag = tag;
		}

		public AliasBeanDef(String id) {
			this.id = id;
		}

		@Override
		public AliasBeanDef create() {
			return this;
		}

		@Override
		public void assemble(Object bean) {
		}

		@Override
		public void init(Object bean, ProgressCallback callback)
				throws InterruptedException {
		}
	}

	public final static class BeanInfo {
		public final String id;
		public final String tag;
		public final boolean alias;
		public final Object bean;

		private BeanInfo(String id, boolean alias, Object bean) {
			this(id, null, alias, bean);
		}

		private BeanInfo(String id, String tag, boolean alias, Object bean) {
			this.id = id;
			this.tag = tag;
			this.alias = alias;
			this.bean = bean;
		}

		@Override
		public int hashCode() {
			if (tag != null) {
				return id.hashCode() | tag.hashCode();
			}
			return id.hashCode();
		}
	}
}
