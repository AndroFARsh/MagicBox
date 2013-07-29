package com.magicbox.demo.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileManager extends Named {

	File getFile(String path);

	public interface File {
		OutputStream getOutputStream() throws IOException;

		InputStream getInputStream() throws IOException;

		boolean exists() throws IOException;

		boolean create() throws IOException;

		long fileSize() throws IOException;

		void close() throws IOException;

		boolean delete();

		String getAbsolutePath() throws IOException;

		void renameTo(String newPath) throws IOException;
	}
}
