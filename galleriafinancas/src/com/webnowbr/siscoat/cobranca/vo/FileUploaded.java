package com.webnowbr.siscoat.cobranca.vo;

import java.io.File;

public class FileUploaded {
	private File file;
	private String name;
	private String path;

	public FileUploaded() {
	}

	public FileUploaded(String name, File file, String path) {
		this.name = name;
		this.file = file;
		this.path = path;
	
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
