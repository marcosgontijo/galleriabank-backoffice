package com.webnowbr.siscoat.cobranca.vo;

import java.io.File;

public class FileGenerator {
	private File file;
	private String name;
	private String path;
	private String documento;
	private boolean pdfGerado;

	public FileGenerator() {
	}

	public FileGenerator(String name, File file, String path, String documento) {
		this.file = file;
		this.name = name;
		this.path = path;
		this.documento  = documento;
		this.pdfGerado =false;

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

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public boolean isPdfGerado() {
		return pdfGerado;
	}

	public void setPdfGerado(boolean pdfGerado) {
		this.pdfGerado = pdfGerado;
	}

}
