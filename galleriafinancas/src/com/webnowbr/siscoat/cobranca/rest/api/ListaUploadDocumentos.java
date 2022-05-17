package com.webnowbr.siscoat.cobranca.rest.api;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

public class ListaUploadDocumentos {
	
	Collection<UploadDocumentos> listaUploadDocumentos = new ArrayList<UploadDocumentos>();

	/**
	 * @return the listaUploadDocumentos
	 */
	public Collection<UploadDocumentos> getListaUploadDocumentos() {
		return listaUploadDocumentos;
	}

	/**
	 * @param listaUploadDocumentos the listaUploadDocumentos to set
	 */
	public void setListaUploadDocumentos(Collection<UploadDocumentos> listaUploadDocumentos) {
		this.listaUploadDocumentos = listaUploadDocumentos;
	}
	
	public static String converterFromListJson(ListaUploadDocumentos listaUploadDocumentos) {
		Gson gson = new Gson();
		return gson.toJson(listaUploadDocumentos);
	}	
}
