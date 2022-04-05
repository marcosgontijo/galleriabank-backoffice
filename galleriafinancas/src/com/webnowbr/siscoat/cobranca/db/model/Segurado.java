package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Segurado implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4327959574378948956L;
	private long id;
	private BigDecimal porcentagemSegurador;
	private PagadorRecebedor pessoa;
	private ContratoCobranca contratoCobranca;
	private int posicao;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getPorcentagemSegurador() {
		return porcentagemSegurador;
	}

	public void setPorcentagemSegurador(BigDecimal porcentagemSegurador) {
		this.porcentagemSegurador = porcentagemSegurador;
	}

	public PagadorRecebedor getPessoa() {
		return pessoa;
	}

	public void setPessoa(PagadorRecebedor pessoa) {
		this.pessoa = pessoa;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

}
