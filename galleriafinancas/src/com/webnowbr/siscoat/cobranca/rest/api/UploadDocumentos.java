package com.webnowbr.siscoat.cobranca.rest.api;

import java.io.Serializable;
import java.util.Collection;

import com.google.gson.Gson;

public class UploadDocumentos implements Serializable {
	
	private static final long serialVersionUID = 598953331243019772L;
	private String name;
	private String path;

	public UploadDocumentos() {
		super();
	}

	public UploadDocumentos(String name, String path) {
		super();
		this.name = name;
		this.path = path;
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
		return "UploadArquivos [name=" + name + ", path=" + path + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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

	public static String converterFromListJson(Collection<UploadDocumentos> uploadArquivos) {
		Gson gson = new Gson();
		return gson.toJson(uploadArquivos);
	}
}
