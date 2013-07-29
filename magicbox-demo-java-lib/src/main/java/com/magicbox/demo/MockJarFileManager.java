package com.magicbox.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.magicbox.demo.model.FileManager;

public class MockJarFileManager implements FileManager {
	
	public String getName(){
		return MockJarFileManager.class.getSimpleName();
	}
	
	public String toString(){
		return getName();
	}

	@Override
	public File getFile(final String path) {
		return new File() {
			java.io.File file = new java.io.File(path);
			OutputStream outputStream;
			InputStream inputStream;
			
			@Override
			public void renameTo(String newPath) throws IOException {
				file.renameTo(new java.io.File(newPath));
			}
			
			@Override
			public OutputStream getOutputStream() throws IOException {
				if (outputStream == null){
					outputStream = new FileOutputStream(file);
				}
				return outputStream;
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				if (inputStream == null){
					inputStream = new FileInputStream(file);
				}
				return inputStream;
			}
			
			@Override
			public String getAbsolutePath() throws IOException {
				return file.getAbsolutePath();
			}
			
			@Override
			public long fileSize() throws IOException {
				return file.length();
			}
			
			@Override
			public boolean exists() throws IOException {
				return file.exists();
			}
			
			@Override
			public boolean delete() {
				return file.delete();
			}
			
			@Override
			public boolean create() throws IOException {
				return file.createNewFile();
			}
			
			@Override
			public void close() throws IOException {
				closeInputStream();
				closeOutputStream();
			}

			public void closeInputStream() {
				if (inputStream != null){
					try{
						inputStream.close();
					}catch (IOException e){
					}finally {
						inputStream = null;
					}
				}
			}
			
			public void closeOutputStream() {
				if (outputStream != null){
					try{
						outputStream.close();
					}catch (IOException e){
					}finally {
						outputStream = null;
					}
				}
			}
			
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				
				close();
			}
		};
	}
}
