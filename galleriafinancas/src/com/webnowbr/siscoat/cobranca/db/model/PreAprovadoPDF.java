package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;

public class PreAprovadoPDF implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nome;
	private Date data;
	private String numeroOp;
	private String cpf;
	private BigDecimal taxa;
	private String observacao;
	private String cidade;
	private String matricula;
	private String uf;
	private String prazo;
	private String tipoValor;
	
	private BigDecimal valorSolicitado;
	private BigDecimal valorGarantia;
	private BigDecimal valorParcela;
	private BigDecimal rendaMinima;
	private String cep ;
	private BigInteger carencia;
	private BigDecimal despesa;
	private BigDecimal valorCustoEmissao;
	private BigDecimal valorIOF;
	private BigDecimal valorLiquido;
	
	List<PreAprovadoPDFDetalheDespesas> detalhesDespesas;
	
	
	public PreAprovadoPDF(String nome, Date data, String numeroOp, String cpf, BigDecimal taxa, String observacao,
			String cidade, String matricula, String uf, String prazo, BigDecimal valorSolicitado,
			BigDecimal valorGarantia, BigDecimal valorParcela, BigDecimal rendaMinima) {
		super();
		this.nome = nome;
		this.data = data;
		this.numeroOp = numeroOp;
		this.cpf = cpf;
		this.taxa = taxa;
		this.observacao = observacao;
		this.cidade = cidade;
		this.matricula = matricula;
		this.uf = uf;
		this.prazo = prazo;
		this.valorSolicitado = valorSolicitado;
		this.valorGarantia = valorGarantia;
		this.valorParcela = valorParcela;
		this.rendaMinima = rendaMinima;
	}
	
	public PreAprovadoPDF(String nome, Date data, String numeroOp, String cpf, BigDecimal taxa, String observacao,
			String cidade, String matricula, String uf, String prazo, BigDecimal valorSolicitado,
			BigDecimal valorGarantia, BigDecimal valorParcela, String tipoValor) {
		super();
		this.nome = nome;
		this.data = data;
		this.numeroOp = numeroOp;
		this.cpf = cpf;
		this.taxa = taxa;
		this.observacao = observacao;
		this.cidade = cidade;
		this.matricula = matricula;
		this.uf = uf;
		this.prazo = prazo;
		this.valorSolicitado = valorSolicitado;
		this.valorGarantia = valorGarantia;
		this.valorParcela = valorParcela;
		String s1 = tipoValor.substring(0, 1).toUpperCase();
		this.tipoValor = s1 + tipoValor.substring(1);
		if(CommonsUtil.semValor(this.observacao)) {
			this.observacao = "";
		}
	}

	public PreAprovadoPDF(String nome, Date data, String numeroOp, String cpf, BigDecimal taxa, String observacao,
			String cidade, String matricula, String uf, String prazo, BigDecimal valorSolicitado,
			BigDecimal valorGarantia, BigDecimal valorParcela, String tipoValor, String cep, BigInteger carencia,
			BigDecimal despesa, BigDecimal valorCustoEmissao, BigDecimal valorIOF, BigDecimal valorLiquido,
			List<PreAprovadoPDFDetalheDespesas> detalhesDespesas) {
		super();
		this.nome = nome;
		this.data = data;
		this.numeroOp = numeroOp;
		this.cpf = cpf;
		this.taxa = taxa;
		this.observacao = observacao;
		this.cidade = cidade;
		this.matricula = matricula;
		this.uf = uf;
		this.prazo = prazo;
		this.valorSolicitado = valorSolicitado;
		this.valorGarantia = valorGarantia;
		this.valorParcela = valorParcela;
		String s1 = tipoValor.substring(0, 1).toUpperCase();
		this.tipoValor = s1 + tipoValor.substring(1);
		if(CommonsUtil.semValor(this.observacao)) {
			this.observacao = "";
		}
		this.cep = cep;
		this.carencia = carencia;
		this.despesa = despesa;
		this.valorCustoEmissao = valorCustoEmissao;
		this.valorIOF = valorIOF;
		
		this.valorLiquido = valorLiquido;
		this.detalhesDespesas = detalhesDespesas;
	}
	
	

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getNumeroOp() {
		return numeroOp;
	}
	public void setNumeroOp(String numeroOp) {
		this.numeroOp = numeroOp;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public BigDecimal getTaxa() {
		return taxa;
	}
	public void setTaxa(BigDecimal taxa) {
		this.taxa = taxa;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getPrazo() {
		return prazo;
	}
	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}
	public BigDecimal getValorSolicitado() {
		return valorSolicitado;
	}
	public void setValorSolicitado(BigDecimal valorSolicitado) {
		this.valorSolicitado = valorSolicitado;
	}
	public BigDecimal getValorGarantia() {
		return valorGarantia;
	}
	public void setValorGarantia(BigDecimal valorGarantia) {
		this.valorGarantia = valorGarantia;
	}
	public BigDecimal getValorParcela() {
		return valorParcela;
	}
	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}
	public BigDecimal getRendaMinima() {
		return rendaMinima;
	}
	public void setRendaMinima(BigDecimal rendaMinima) {
		this.rendaMinima = rendaMinima;
	}
	public String getTipoValor() {
		return tipoValor;
	}
	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public BigInteger getCarencia() {
		return carencia;
	}

	public void setCarencia(BigInteger carencia) {
		this.carencia = carencia;
	}

	public BigDecimal getDespesa() {
		return despesa;
	}

	public void setDespesa(BigDecimal despesa) {
		this.despesa = despesa;
	}

	public BigDecimal getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(BigDecimal valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public List<PreAprovadoPDFDetalheDespesas> getDetalhesDespesas() {
		return detalhesDespesas;
	}

	public void setDetalhesDespesas(List<PreAprovadoPDFDetalheDespesas> detalhesDespesas) {
		this.detalhesDespesas = detalhesDespesas;
	}

	public BigDecimal getValorCustoEmissao() {
		return valorCustoEmissao;
	}

	public void setValorCustoEmissao(BigDecimal valorCustoEmissao) {
		this.valorCustoEmissao = valorCustoEmissao;
	}

	public BigDecimal getValorIOF() {
		return valorIOF;
	}

	public void setValorIOF(BigDecimal valorIOF) {
		this.valorIOF = valorIOF;
	}
	
}
