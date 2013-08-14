package com.magicbox.processor.model;

public interface NodeClass {
	boolean isExist(int id);

	/**
	 * Return property by id
	 * 
	 * @param id
	 * @return
	 * @throws NodeException
	 *             - throw exception if property not exist
	 */
	<T> T get(int id) throws NodeException;

	int getInteger(int id) throws NodeException;

	float getFloat(int id) throws NodeException;

	long getLong(int id) throws NodeException;

	boolean getBoolean(int id) throws NodeException;

	String getString(int id) throws NodeException;

	ClassComposer classComposer();

	int[] ids();

	public interface ClassComposer {
		<T> ClassComposer set(int id, T value) throws NodeException;

		ClassComposer setInteger(int id, int value) throws NodeException;

		ClassComposer setLong(int id, int value) throws NodeException;

		ClassComposer setBoolean(int id, int value) throws NodeException;

		ClassComposer setString(int id, String value) throws NodeException;

		ClassComposer setFloat(int id, float value) throws NodeException;

		ClassComposer remove(int id);

		// return new instancy of LevelComponentClass
		NodeClass compose();
	}

	class NodeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NodeException() {
		};

		public NodeException(String message) {
			super(message);
		}

		public NodeException(Throwable couse) {
			super(couse);
		}

		public NodeException(Throwable couse, String message) {
			super(message, couse);
		}
	}
}
