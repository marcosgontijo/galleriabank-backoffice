package com.webnowbr.siscoat.cobranca.rest.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import com.google.gson.Gson;

public class UploadDocumentos implements Serializable {
	
	private static final long serialVersionUID = 598953331243019772L;
	private byte[] file;
	private String name;
	private String path;

	public UploadDocumentos() {
		super();
	}

	public UploadDocumentos(byte[] file, String name, String path) {
		super();
		this.file = file;
		this.name = name;
		this.path = path;
	}
	
	/**
	 * @return the file
	 */
	public byte[] getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(byte[] file) {
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
	
	public String toString() {
		return "uploadDocumentos [name=" + name + ", path=" + path + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(file);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadDocumentos other = (UploadDocumentos) obj;
		if (!Arrays.equals(file, other.file))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public static String converterFromListJson(Collection<UploadDocumentos> uploadDocumentos) {
		Gson gson = new Gson();
		return gson.toJson(uploadDocumentos);
	}
}
