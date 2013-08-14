package com.magicbox.processor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class NodeClassImpl implements NodeClass {
	private Map<Integer, Object> bundle = new HashMap<Integer, Object>();

	NodeClassImpl() {
	}

	@Override
	public boolean isExist(int pId) {
		return bundle.containsKey(pId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(int pId) throws NodeException {
		if (!isExist(pId)) {
			throw new NodeException(String.format(
					"Object with id %d not found", pId));
		}
		return (T) bundle.get(pId);
	}

	@Override
	public int getInteger(int pId) throws NodeException {
		return (Integer) get(pId);
	}

	@Override
	public long getLong(int pId) throws NodeException {
		return (Long) get(pId);
	}

	@Override
	public boolean getBoolean(int pId) throws NodeException {
		return (Boolean) get(pId);
	}

	@Override
	public String getString(int pId) throws NodeException {
		return (String) get(pId);
	}

	@Override
	public float getFloat(int pId) throws NodeException {
		return (Float) get(pId);
	}

	@Override
	public ClassComposer classComposer() {
		return new ClassComposerImpl().setBundle(bundle);
	}

	@Override
	public String toString() {
		return String.format("[Bundle: {%s}]", bundle.toString());
	}

	@Override
	public int[] ids() {
		int index = 0;
		final int[] ids = new int[bundle.size()];
		for (Iterator<Integer> it = bundle.keySet().iterator(); it.hasNext(); ++index) {
			ids[index] = it.next();
		}
		return ids;
	}

	final class ClassComposerImpl implements ClassComposer {
		private Map<Integer, Object> bundle;

		private ClassComposer setBundle(Map<Integer, Object> bundle) {
			this.bundle = new HashMap<Integer, Object>();
			for (final Entry<Integer, Object> entry : bundle.entrySet()) {
				this.bundle.put(entry.getKey(), entry.getValue());
			}
			return this;
		};

		@Override
		public <T> ClassComposer set(int pId, T pValue) throws NodeException {
			if (pValue == null) {
				throw new NodeException("Property shoudn't be null");
			}
			this.bundle.put(pId, pValue);
			return this;
		}

		@Override
		public ClassComposer setInteger(int pId, int pValue)
				throws NodeException {
			return set(pId, pValue);
		}

		@Override
		public ClassComposer setLong(int pId, int pValue) throws NodeException {
			return set(pId, pValue);
		}

		@Override
		public ClassComposer setBoolean(int pId, int pValue)
				throws NodeException {
			return set(pId, pValue);
		}

		@Override
		public ClassComposer setFloat(int pId, float pValue)
				throws NodeException {
			return set(pId, pValue);
		}

		@Override
		public ClassComposer setString(int pId, String pValue)
				throws NodeException {
			return set(pId, pValue);
		}

		@Override
		public ClassComposer remove(int pId) {
			if (bundle.containsKey(pId)) {
				bundle.remove(pId);
			}
			return this;
		}

		@Override
		public NodeClass compose() {
			NodeClassImpl.this.setBundle(bundle);
			return NodeClassImpl.this;
		}
	}

	private NodeClass setBundle(Map<Integer, Object> bundle) {
		this.bundle = bundle;
		return this;
	}
}
