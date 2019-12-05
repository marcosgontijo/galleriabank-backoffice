package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class GruposFavorecidos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nomeGrupo;
	
	private PagadorRecebedor recebedor1;
	private PagadorRecebedor recebedor2;
	private PagadorRecebedor recebedor3;
	private PagadorRecebedor recebedor4;
	private PagadorRecebedor recebedor5;
	private PagadorRecebedor recebedor6;
	private PagadorRecebedor recebedor7;
	private PagadorRecebedor recebedor8;
	private PagadorRecebedor recebedor9;
	private PagadorRecebedor recebedor10;
	
	public GruposFavorecidos(){
	}
	
	public GruposFavorecidos(long id, String nomeGrupo, PagadorRecebedor recebedor1, PagadorRecebedor recebedor2, PagadorRecebedor recebedor3,
			PagadorRecebedor recebedor4, PagadorRecebedor recebedor5, PagadorRecebedor recebedor6, PagadorRecebedor recebedor7, PagadorRecebedor recebedor8, PagadorRecebedor recebedor9, PagadorRecebedor recebedor10){
		this.id = id;
		this.nomeGrupo = nomeGrupo;
		this.recebedor1 = recebedor1;
		this.recebedor2 = recebedor2;
		this.recebedor3 = recebedor3;
		this.recebedor4 = recebedor4;
		this.recebedor5 = recebedor5;
		this.recebedor6 = recebedor6;
		this.recebedor7 = recebedor7;
		this.recebedor8 = recebedor8;
		this.recebedor9 = recebedor9;
		this.recebedor10 = recebedor10;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the nomeGrupo
	 */
	public String getNomeGrupo() {
		return nomeGrupo;
	}

	/**
	 * @param nomeGrupo the nomeGrupo to set
	 */
	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	/**
	 * @return the recebedor1
	 */
	public PagadorRecebedor getRecebedor1() {
		return recebedor1;
	}

	/**
	 * @param recebedor1 the recebedor1 to set
	 */
	public void setRecebedor1(PagadorRecebedor recebedor1) {
		this.recebedor1 = recebedor1;
	}

	/**
	 * @return the recebedor2
	 */
	public PagadorRecebedor getRecebedor2() {
		return recebedor2;
	}

	/**
	 * @param recebedor2 the recebedor2 to set
	 */
	public void setRecebedor2(PagadorRecebedor recebedor2) {
		this.recebedor2 = recebedor2;
	}

	/**
	 * @return the recebedor3
	 */
	public PagadorRecebedor getRecebedor3() {
		return recebedor3;
	}

	/**
	 * @param recebedor3 the recebedor3 to set
	 */
	public void setRecebedor3(PagadorRecebedor recebedor3) {
		this.recebedor3 = recebedor3;
	}

	/**
	 * @return the recebedor4
	 */
	public PagadorRecebedor getRecebedor4() {
		return recebedor4;
	}

	/**
	 * @param recebedor4 the recebedor4 to set
	 */
	public void setRecebedor4(PagadorRecebedor recebedor4) {
		this.recebedor4 = recebedor4;
	}

	/**
	 * @return the recebedor5
	 */
	public PagadorRecebedor getRecebedor5() {
		return recebedor5;
	}

	/**
	 * @param recebedor5 the recebedor5 to set
	 */
	public void setRecebedor5(PagadorRecebedor recebedor5) {
		this.recebedor5 = recebedor5;
	}

	/**
	 * @return the recebedor6
	 */
	public PagadorRecebedor getRecebedor6() {
		return recebedor6;
	}

	/**
	 * @param recebedor6 the recebedor6 to set
	 */
	public void setRecebedor6(PagadorRecebedor recebedor6) {
		this.recebedor6 = recebedor6;
	}

	/**
	 * @return the recebedor7
	 */
	public PagadorRecebedor getRecebedor7() {
		return recebedor7;
	}

	/**
	 * @param recebedor7 the recebedor7 to set
	 */
	public void setRecebedor7(PagadorRecebedor recebedor7) {
		this.recebedor7 = recebedor7;
	}

	/**
	 * @return the recebedor8
	 */
	public PagadorRecebedor getRecebedor8() {
		return recebedor8;
	}

	/**
	 * @param recebedor8 the recebedor8 to set
	 */
	public void setRecebedor8(PagadorRecebedor recebedor8) {
		this.recebedor8 = recebedor8;
	}

	/**
	 * @return the recebedor9
	 */
	public PagadorRecebedor getRecebedor9() {
		return recebedor9;
	}

	/**
	 * @param recebedor9 the recebedor9 to set
	 */
	public void setRecebedor9(PagadorRecebedor recebedor9) {
		this.recebedor9 = recebedor9;
	}

	/**
	 * @return the recebedor10
	 */
	public PagadorRecebedor getRecebedor10() {
		return recebedor10;
	}

	/**
	 * @param recebedor10 the recebedor10 to set
	 */
	public void setRecebedor10(PagadorRecebedor recebedor10) {
		this.recebedor10 = recebedor10;
	}
}