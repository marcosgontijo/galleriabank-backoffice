package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ParticipacoesEmpresa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date dataEntrada;	
	private Date dataSaida;	
	private String documento;
	private String nome;
	private String participacao;
	private String posicao;
	private String qualificacaoSocio;
	private String relacao;
	
	public ParticipacoesEmpresa() {
		
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getParticipacao() {
		return participacao;
	}

	public void setParticipacao(String participacao) {
		this.participacao = participacao;
	}

	public String getPosicao() {
		return posicao;
	}

	public void setPosicao(String posicao) {
		this.posicao = posicao;
	}

	public String getQualificacaoSocio() {
		return qualificacaoSocio;
	}

	public void setQualificacaoSocio(String qualificacaoSocio) {
		this.qualificacaoSocio = qualificacaoSocio;
	}

	public String getRelacao() {
		return relacao;
	}

	public void setRelacao(String relacao) {
		this.relacao = relacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}