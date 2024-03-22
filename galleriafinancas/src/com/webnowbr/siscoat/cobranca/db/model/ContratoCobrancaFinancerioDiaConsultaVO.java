package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;

public class ContratoCobrancaFinancerioDiaConsultaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8484136604618335620L;

	private long id;
	
	private String numeroContrato;
	private Date dataContrato; // data do contrato mas muda na aprovacao	
	private Date dataInicio; // data de inicio das parcelas	
	private Date dataAssinatura; // data de inicio das parcelas	
	
	private String nomePagador;	
	
	private String pagador_nome;
	private String pagador_cpf;
	private String pagador_cnpj;
	private Date pagador_dtNascimento;
	private String pagador_endereco;
	private String pagador_numero;
	private String pagador_cidade;	
	private String pagador_estado;
	private String pagador_cep;
	private String pagador_nomeConjuge;	
	private String pagador_cpfConjuge; 
	
	private String imovel_cidade;	
	private String imovel_estado;
	
	private BigDecimal valorCCB;	
	private BigDecimal txJurosParcelas;
	private String tipoCalculo;

	private boolean temSeguroDFI;
	private boolean temSeguroMIP;
	private long mesesCarencia;
	private boolean temTxAdm;
	
	private BigDecimal cetMes;
	private String empresa;
	private String nomeCidadeImovel;
	private boolean contratoQuitado;
	private BigDecimal valorImovel;
	private BigDecimal valorLeilaoImovel;
	private String tipoImovel;

	private boolean corrigidoIPCA;
	private boolean corrigidoNovoIPCA;

	private String documentoPagador;
	private String emailPagador;
	private String celularPagador;
	private long qtdeParcelas;
	private String numeroContratoSeguro;
	private String serieCci;
	private Cartorio ultimoCartorio;
	
	private String tipoOperacao;
	
	private List<ContratoCobrancaFinanceiroDiaConsultaDetalhesVO> listContratoCobrancaDetalhes;
	
	
	
	public ContratoCobrancaFinancerioDiaConsultaVO() {
		super();
	}

	public ContratoCobrancaFinancerioDiaConsultaVO(long id, String numeroContrato, Date dataContrato, Date dataInicio,
			String pagador_nome, String pagador_cpf, String pagador_cnpj, Date pagador_dtNascimento,
			String pagador_endereco, String pagador_numero, String pagador_cidade, String pagador_estado,
			String pagador_cep, String pagador_nomeConjuge, String pagador_cpfConjuge, String imovel_cidade,
			String imovel_estado, BigDecimal valorCCB, BigDecimal txJurosParcelas, String tipoCalculo,
			boolean temSeguroDFI, boolean temSeguroMIP, long mesesCarencia, boolean temTxAdm,
			String empresa, BigDecimal valorImovel,
			String tipoImovel, boolean corrigidoIPCA, boolean corrigidoNovoIPCA, 
			String emailPagador, String celularPagador, long qtdeParcelas, String numeroContratoSeguro,
			BigDecimal valorLeilaoImovel, Date dataContratoAssinado, Date agassinaturadata, String tipoOperacao) {
		super();
		this.id = id;
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.dataInicio = dataInicio;
		this.nomePagador = pagador_nome;
		this.pagador_nome = pagador_nome;
		this.pagador_cpf = pagador_cpf;
		this.pagador_cnpj = pagador_cnpj;
		this.pagador_dtNascimento = pagador_dtNascimento;
		this.pagador_endereco = pagador_endereco;
		this.pagador_numero = pagador_numero;
		this.pagador_cidade = pagador_cidade;
		this.pagador_estado = pagador_estado;
		this.pagador_cep = pagador_cep;
		this.pagador_nomeConjuge = CommonsUtil.stringValueVazio( pagador_nomeConjuge );
		this.pagador_cpfConjuge = CommonsUtil.stringValueVazio( pagador_cpfConjuge );
		this.imovel_cidade = imovel_cidade;
		this.imovel_estado = imovel_estado;
		this.valorCCB = valorCCB;
		this.txJurosParcelas = txJurosParcelas;
		this.tipoCalculo = tipoCalculo;
		this.temSeguroDFI = temSeguroDFI;
		this.temSeguroMIP = temSeguroMIP;
		this.mesesCarencia = mesesCarencia;
		this.temTxAdm = temTxAdm;
		this.empresa = empresa;
//		this.nomeCidadeImovel = nomeCidadeImovel;		
//		this.contratoQuitado = contratoQuitado;		
		this.valorImovel = valorImovel;	
		this.tipoImovel = tipoImovel;
		this.corrigidoIPCA = corrigidoIPCA;
		this.corrigidoNovoIPCA = corrigidoNovoIPCA;
		if ( !CommonsUtil.semValor(pagador_cpf) )
			this.documentoPagador = pagador_cpf;
		else
			this.documentoPagador = pagador_cnpj;
		this.emailPagador = emailPagador;
		this.celularPagador = celularPagador;
		this.qtdeParcelas = qtdeParcelas;
		this.numeroContratoSeguro = numeroContratoSeguro;
		this.valorLeilaoImovel = valorLeilaoImovel;
		if ( !CommonsUtil.semValor(dataContratoAssinado) )
			this.dataAssinatura = dataContratoAssinado;
		else
			this.dataAssinatura = agassinaturadata;
		
		if (CommonsUtil.mesmoValor(tipoOperacao, "Home Equity") )
			this.tipoOperacao = "Home Equity";
		else if (CommonsUtil.mesmoValor(tipoOperacao, "Emprestimo"))
			this.tipoOperacao = "Financiamento";
		else
			this.tipoOperacao = tipoOperacao;
		
		this.listContratoCobrancaDetalhes = new ArrayList<>();
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
	public Date getDataContrato() {
		return dataContrato;
	}
	public void setDataContrato(Date dataContrato) {
		this.dataContrato = dataContrato;
	}
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getNomePagador() {
		return nomePagador;
	}
	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public String getPagador_nome() {
		return pagador_nome;
	}
	public void setPagador_nome(String pagador_nome) {
		this.pagador_nome = pagador_nome;
	}
	public String getPagador_cpf() {
		return pagador_cpf;
	}
	public void setPagador_cpf(String pagador_cpf) {
		this.pagador_cpf = pagador_cpf;
	}
	public String getPagador_cnpj() {
		return pagador_cnpj;
	}
	public void setPagador_cnpj(String pagador_cnpj) {
		this.pagador_cnpj = pagador_cnpj;
	}
	public Date getPagador_dtNascimento() {
		return pagador_dtNascimento;
	}
	public void setPagador_dtNascimento(Date pagador_dtNascimento) {
		this.pagador_dtNascimento = pagador_dtNascimento;
	}
	public String getPagador_endereco() {
		return pagador_endereco;
	}
	public void setPagador_endereco(String pagador_endereco) {
		this.pagador_endereco = pagador_endereco;
	}
	public String getPagador_numero() {
		return pagador_numero;
	}
	public void setPagador_numero(String pagador_numero) {
		this.pagador_numero = pagador_numero;
	}
	public String getPagador_cidade() {
		return pagador_cidade;
	}
	public void setPagador_cidade(String pagador_cidade) {
		this.pagador_cidade = pagador_cidade;
	}
	public String getPagador_estado() {
		return pagador_estado;
	}
	public void setPagador_estado(String pagador_estado) {
		this.pagador_estado = pagador_estado;
	}
	public String getPagador_cep() {
		return pagador_cep;
	}
	public void setPagador_cep(String pagador_cep) {
		this.pagador_cep = pagador_cep;
	}
	public String getPagador_nomeConjuge() {
		return pagador_nomeConjuge;
	}
	public void setPagador_nomeConjuge(String pagador_nomeConjuge) {
		this.pagador_nomeConjuge = pagador_nomeConjuge;
	}
	public String getPagador_cpfConjuge() {
		return pagador_cpfConjuge;
	}
	public void setPagador_cpfConjuge(String pagador_cpfConjuge) {
		this.pagador_cpfConjuge = pagador_cpfConjuge;
	}
	public String getImovel_cidade() {
		return imovel_cidade;
	}
	public void setImovel_cidade(String imovel_cidade) {
		this.imovel_cidade = imovel_cidade;
	}
	public String getImovel_estado() {
		return imovel_estado;
	}
	public void setImovel_estado(String imovel_estado) {
		this.imovel_estado = imovel_estado;
	}
	public BigDecimal getValorCCB() {
		return valorCCB;
	}
	public void setValorCCB(BigDecimal valorCCB) {
		this.valorCCB = valorCCB;
	}
	public BigDecimal getTxJurosParcelas() {
		return txJurosParcelas;
	}
	public void setTxJurosParcelas(BigDecimal txJurosParcelas) {
		this.txJurosParcelas = txJurosParcelas;
	}
	public String getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(String tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	public boolean isTemSeguroDFI() {
		return temSeguroDFI;
	}

	public void setTemSeguroDFI(boolean temSeguroDFI) {
		this.temSeguroDFI = temSeguroDFI;
	}

	public boolean isTemSeguroMIP() {
		return temSeguroMIP;
	}

	public void setTemSeguroMIP(boolean temSeguroMIP) {
		this.temSeguroMIP = temSeguroMIP;
	}

	public long getMesesCarencia() {
		return mesesCarencia;
	}

	public void setMesesCarencia(long mesesCarencia) {
		this.mesesCarencia = mesesCarencia;
	}

	public boolean isTemTxAdm() {
		return temTxAdm;
	}

	public void setTemTxAdm(boolean temTxAdm) {
		this.temTxAdm = temTxAdm;
	}

	public BigDecimal getCetMes() {
		return cetMes;
	}

	public void setCetMes(BigDecimal cetMes) {
		this.cetMes = cetMes;
	}

	public List<ContratoCobrancaFinanceiroDiaConsultaDetalhesVO> getListContratoCobrancaDetalhes() {
		return listContratoCobrancaDetalhes;
	}
	public void setListContratoCobrancaDetalhes(List<ContratoCobrancaFinanceiroDiaConsultaDetalhesVO> listContratoCobrancaDetalhes) {
		this.listContratoCobrancaDetalhes = listContratoCobrancaDetalhes;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getNomeCidadeImovel() {
		return nomeCidadeImovel;
	}

	public void setNomeCidadeImovel(String nomeCidadeImovel) {
		this.nomeCidadeImovel = nomeCidadeImovel;
	}

	public boolean isContratoQuitado() {
		return contratoQuitado;
	}

	public void setContratoQuitado(boolean contratoQuitado) {
		this.contratoQuitado = contratoQuitado;
	}

	public BigDecimal getValorImovel() {
		return valorImovel;
	}

	public void setValorImovel(BigDecimal valorImovel) {
		this.valorImovel = valorImovel;
	}

	public String getTipoImovel() {
		return tipoImovel;
	}

	public void setTipoImovel(String tipoImovel) {
		this.tipoImovel = tipoImovel;
	}

	public boolean isCorrigidoIPCA() {
		return corrigidoIPCA;
	}

	public void setCorrigidoIPCA(boolean corrigidoIPCA) {
		this.corrigidoIPCA = corrigidoIPCA;
	}

	public boolean isCorrigidoNovoIPCA() {
		return corrigidoNovoIPCA;
	}

	public void setCorrigidoNovoIPCA(boolean corrigidoNovoIPCA) {
		this.corrigidoNovoIPCA = corrigidoNovoIPCA;
	}

	public String getDocumentoPagador() {
		return documentoPagador;
	}

	public void setDocumentoPagador(String documentoPagador) {
		this.documentoPagador = documentoPagador;
	}

	public String getEmailPagador() {
		return emailPagador;
	}

	public void setEmailPagador(String emailPagador) {
		this.emailPagador = emailPagador;
	}

	public String getCelularPagador() {
		return celularPagador;
	}

	public void setCelularPagador(String celularPagador) {
		this.celularPagador = celularPagador;
	}

	public long getQtdeParcelas() {
		return qtdeParcelas;
	}

	public void setQtdeParcelas(long qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}

	public String getNumeroContratoSeguro() {
		return numeroContratoSeguro;
	}

	public void setNumeroContratoSeguro(String numeroContratoSeguro) {
		this.numeroContratoSeguro = numeroContratoSeguro;
	}

	public BigDecimal getValorLeilaoImovel() {
		return valorLeilaoImovel;
	}

	public void setValorLeilaoImovel(BigDecimal valorLeilaoImovel) {
		this.valorLeilaoImovel = valorLeilaoImovel;
	}

	public String getSerieCci() {
		return serieCci;
	}

	public void setSerieCci(String serieCci) {
		this.serieCci = serieCci;
	}

	public Date getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(Date dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public Cartorio getUltimoCartorio() {
		return ultimoCartorio;
	}

	public void setUltimoCartorio(Cartorio ultimoCartorio) {
		this.ultimoCartorio = ultimoCartorio;
	}

	public String getTipoOperacao() {
		return tipoOperacao;
	}

	public void setTipoOperacao(String tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}
}