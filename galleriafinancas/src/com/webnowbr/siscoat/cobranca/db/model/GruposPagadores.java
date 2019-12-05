package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class GruposPagadores implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nomeGrupo;
	
	private PagadorRecebedor pagador1;
	private PagadorRecebedor pagador2;
	private PagadorRecebedor pagador3;
	private PagadorRecebedor pagador4;
	private PagadorRecebedor pagador5;
	private PagadorRecebedor pagador6;
	private PagadorRecebedor pagador7;
	private PagadorRecebedor pagador8;
	private PagadorRecebedor pagador9;
	private PagadorRecebedor pagador10;
	
	public GruposPagadores(){
	}
	
	public GruposPagadores(long id, String nomeGrupo, PagadorRecebedor pagador1, PagadorRecebedor pagador2, PagadorRecebedor pagador3,
			PagadorRecebedor pagador4, PagadorRecebedor pagador5, PagadorRecebedor pagador6, PagadorRecebedor pagador7, PagadorRecebedor pagador8, PagadorRecebedor pagador9, PagadorRecebedor pagador10){
		this.id = id;
		this.nomeGrupo = nomeGrupo;
		this.pagador1 = pagador1;
		this.pagador2 = pagador2;
		this.pagador3 = pagador3;
		this.pagador4 = pagador4;
		this.pagador5 = pagador5;
		this.pagador6 = pagador6;
		this.pagador7 = pagador7;
		this.pagador8 = pagador8;
		this.pagador9 = pagador9;
		this.pagador10 = pagador10;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNomeGrupo() {
		return nomeGrupo;
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	public PagadorRecebedor getPagador1() {
		return pagador1;
	}

	public void setPagador1(PagadorRecebedor pagador1) {
		this.pagador1 = pagador1;
	}

	public PagadorRecebedor getPagador2() {
		return pagador2;
	}

	public void setPagador2(PagadorRecebedor pagador2) {
		this.pagador2 = pagador2;
	}

	public PagadorRecebedor getPagador3() {
		return pagador3;
	}

	public void setPagador3(PagadorRecebedor pagador3) {
		this.pagador3 = pagador3;
	}

	public PagadorRecebedor getPagador4() {
		return pagador4;
	}

	public void setPagador4(PagadorRecebedor pagador4) {
		this.pagador4 = pagador4;
	}

	public PagadorRecebedor getPagador5() {
		return pagador5;
	}

	public void setPagador5(PagadorRecebedor pagador5) {
		this.pagador5 = pagador5;
	}

	public PagadorRecebedor getPagador6() {
		return pagador6;
	}

	public void setPagador6(PagadorRecebedor pagador6) {
		this.pagador6 = pagador6;
	}

	public PagadorRecebedor getPagador7() {
		return pagador7;
	}

	public void setPagador7(PagadorRecebedor pagador7) {
		this.pagador7 = pagador7;
	}

	public PagadorRecebedor getPagador8() {
		return pagador8;
	}

	public void setPagador8(PagadorRecebedor pagador8) {
		this.pagador8 = pagador8;
	}

	public PagadorRecebedor getPagador9() {
		return pagador9;
	}

	public void setPagador9(PagadorRecebedor pagador9) {
		this.pagador9 = pagador9;
	}

	public PagadorRecebedor getPagador10() {
		return pagador10;
	}

	public void setPagador10(PagadorRecebedor pagador10) {
		this.pagador10 = pagador10;
	}
}