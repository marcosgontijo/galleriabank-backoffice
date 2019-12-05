package com.webnowbr.siscoat.common.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BoletosRemessa implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroContrato;
	private String parcela;
	private Date dtVencimento;
	private Date dtEmissao;
	private Boolean geradoRemessa;
	private String sistema;
	private BigDecimal valor;
	private String documento;
	private String nomeSacado;
	private String endereco;
	private String bairro;
	private String cep;
	private String cidade;
	private String uf;
	private String nomeArquivoRemessa;
	private Date dtRemessa;
	
	public BoletosRemessa(){
	}
	
	public BoletosRemessa(String sistema, String numeroContrato, String parcela, Date dtVencimento, Date dtEmissao,
			BigDecimal valor, String documento, String nomeSacado, String endereco, String bairro, String cep, String cidade,
			String uf, boolean geradoRemessa){
		this.sistema = sistema;
		this.numeroContrato = numeroContrato;
		this.parcela = parcela;
		this.dtVencimento = dtVencimento;
		this.dtEmissao = dtEmissao;
		this.documento = documento;
		this.nomeSacado = nomeSacado;
		this.endereco = endereco;
		this.bairro = bairro;
		this.cep = cep;
		this.cidade = cidade;
		this.uf = uf;
		this.valor = valor;
		this.geradoRemessa = geradoRemessa;
	}

	public BoletosRemessa(String sistema, String numeroContrato, String parcela, Date dtVencimento, Date dtEmissao,
			BigDecimal valor, String documento, String nomeSacado, String endereco, String bairro, String cep, String cidade,
			String uf, boolean geradoRemessa, long id){
		this.sistema = sistema;
		this.numeroContrato = numeroContrato;
		this.parcela = parcela;
		this.dtVencimento = dtVencimento;
		this.dtEmissao = dtEmissao;
		this.documento = documento;
		this.nomeSacado = nomeSacado;
		this.endereco = endereco;
		this.bairro = bairro;
		this.cep = cep;
		this.cidade = cidade;
		this.uf = uf;
		this.valor = valor;
		this.geradoRemessa = geradoRemessa;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getParcela() {
		return parcela;
	}

	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

	public Date getDtVencimento() {
		return dtVencimento;
	}

	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}

	public Date getDtEmissao() {
		return dtEmissao;
	}

	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	public Boolean getGeradoRemessa() {
		return geradoRemessa;
	}

	public void setGeradoRemessa(Boolean geradoRemessa) {
		this.geradoRemessa = geradoRemessa;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getNomeSacado() {
		return nomeSacado;
	}

	public void setNomeSacado(String nomeSacado) {
		this.nomeSacado = nomeSacado;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	/**
	 * @return the nomeArquivoRemessa
	 */
	public String getNomeArquivoRemessa() {
		return nomeArquivoRemessa;
	}

	/**
	 * @param nomeArquivoRemessa the nomeArquivoRemessa to set
	 */
	public void setNomeArquivoRemessa(String nomeArquivoRemessa) {
		this.nomeArquivoRemessa = nomeArquivoRemessa;
	}

	/**
	 * @return the dtRemessa
	 */
	public Date getDtRemessa() {
		return dtRemessa;
	}

	/**
	 * @param dtRemessa the dtRemessa to set
	 */
	public void setDtRemessa(Date dtRemessa) {
		this.dtRemessa = dtRemessa;
	}
}