package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class Averbacao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3364649308267521304L;

	private long id;
	
	private BigDecimal valor;
	private String descricao;
	private ContratoCobranca contratoCobranca;
	private PagadorRecebedor pagador;
	private String tipoAverbacao;
	private String informacao;
	private String documento;
	private String texto1;
	private String texto2;
	
	public Averbacao() {
		
	}

	public Averbacao(BigDecimal valor) {
		super();
		this.valor = valor;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}
	
	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public String getTipoAverbacao() {
		return tipoAverbacao;
	}

	public void setTipoAverbacao(String tipoAverbacao) {
		this.tipoAverbacao = tipoAverbacao;
	}

	public String getInformacao() {
		return informacao;
	}

	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getTexto1() {
		return texto1;
	}

	public void setTexto1(String texto1) {
		this.texto1 = texto1;
	}

	public String getTexto2() {
		return texto2;
	}

	public void setTexto2(String texto2) {
		this.texto2 = texto2;
	}
}
