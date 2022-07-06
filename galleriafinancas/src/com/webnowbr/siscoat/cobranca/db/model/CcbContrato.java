package com.webnowbr.siscoat.cobranca.db.model;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.primefaces.model.UploadedFile;

public class CcbContrato implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	
	private String numeroCcb;
	private String numeroOperacao;
	private String nomeEmitente;
	private String cpfEmitente;
	private String logradouroEmitente;
	private String numeroEmitente;
	private String complementoEmitente;
	private String cidadeEmitente;
	private String ufEmitente;
	private String cepEmitente;
	
	private List<CcbParticipantes> listaParticipantes;

	private BigDecimal valorLiquidoCredito;
	private BigDecimal valorCredito;
	private BigDecimal custoEmissao;
	private BigDecimal valorIOF;
	private BigDecimal valorDespesas;
	
	private BigDecimal taxaDeJurosMes;
	private BigDecimal taxaDeJurosAno;
	private BigDecimal cetMes;
	private BigDecimal cetAno;
	
	private String contaCorrente;
	private String agencia;
	private String numeroBanco;
	private String nomeBanco;
	private String titularConta;
	
	private String sistemaAmortizacao;
	
	private String prazo;
	private String numeroParcelasPagamento;
	private Date vencimentoPrimeiraParcelaPagamento;
	private Date vencimentoUltimaParcelaPagamento;
	private BigDecimal montantePagamento;
	private BigDecimal valorParcela;

	private String numeroParcelasDFI;
	private Date vencimentoPrimeiraParcelaDFI;
	private Date vencimentoUltimaParcelaDFI;
	private BigDecimal montanteDFI;
	private BigDecimal valorDfiParcela;

	private String numeroParcelasMIP;
	private Date vencimentoPrimeiraParcelaMIP;
	private Date vencimentoUltimaParcelaMIP;
	private BigDecimal montanteMIP;
	private BigDecimal valorMipParcela;

	private BigDecimal tarifaAntecipada;
	private Date dataDeEmissao;

	private String numeroImovel;
	private String cartorioImovel;
	private String cidadeImovel;
	private String ufImovel;
	private String inscricaoMunicipal;
	
	private BigDecimal vendaLeilao;
	private String elaboradorNome;
	private String elaboradorCrea;
	private String responsavelNome;
	private String responsavelCrea;
	private BigDecimal porcentagemImovel;
	
	private String nomeTestemunha1;
	private String cpfTestemunha1;
	private String rgTestemunha1;
	
	private String nomeTestemunha2;
	private String cpfTestemunha2;
	private String rgTestemunha2;
	
	public UploadedFile uploadedFile;
    public String fileName;
    public String fileType;
    public int fileTypeInt;
    ByteArrayInputStream bis = null;
    
    private boolean terceiroGarantidor = false;
    
    private BigDecimal custasCartorariasValor = BigDecimal.ZERO;
    private BigDecimal certidaoDeCasamentoValor = BigDecimal.ZERO;
    private BigDecimal laudoDeAvaliacaoValor = BigDecimal.ZERO;
    
    private BigDecimal intermediacaoValor = BigDecimal.ZERO; // - (Banco, Agência, C/C, CNPJ, nome completo e PIX)
    private String intermediacaoBanco; 
    private String intermediacaoAgencia; 
    private String intermediacaoCC; 
    private String intermediacaoCNPJ; 
    private String intermediacaoNome; 
    private String intermediacaoPix; 
    
    private List<CcbProcessosJudiciais> processosJucidiais; // (++)
    private BigDecimal iptuEmAtrasoValor = BigDecimal.ZERO;
    private BigDecimal condominioEmAtrasoValor = BigDecimal.ZERO;
    private BigDecimal iqValor = BigDecimal.ZERO;
    
    private List<Segurado> listSegurados;
    
    private ArrayList<UploadedFile> filesList = new ArrayList<UploadedFile>();
    
    private ContratoCobranca objetoContratoCobranca;
    
    public CcbContrato() {
    	this.listaParticipantes = new ArrayList<CcbParticipantes>(); 
    	this.processosJucidiais = new ArrayList<CcbProcessosJudiciais>(); 
    }

	public String getNomeEmitente() {
		return nomeEmitente;
	}

	public void setNomeEmitente(String nomeEmitente) {
		this.nomeEmitente = nomeEmitente;
	}

	public String getCpfEmitente() {
		return cpfEmitente;
	}

	public void setCpfEmitente(String cpfEmitente) {
		this.cpfEmitente = cpfEmitente;
	}

	public String getLogradouroEmitente() {
		return logradouroEmitente;
	}

	public void setLogradouroEmitente(String logradouroEmitente) {
		this.logradouroEmitente = logradouroEmitente;
	}

	public String getNumeroEmitente() {
		return numeroEmitente;
	}

	public void setNumeroEmitente(String numeroEmitente) {
		this.numeroEmitente = numeroEmitente;
	}

	public String getComplementoEmitente() {
		return complementoEmitente;
	}

	public void setComplementoEmitente(String complementoEmitente) {
		this.complementoEmitente = complementoEmitente;
	}

	public String getCidadeEmitente() {
		return cidadeEmitente;
	}

	public void setCidadeEmitente(String cidadeEmitente) {
		this.cidadeEmitente = cidadeEmitente;
	}

	public String getUfEmitente() {
		return ufEmitente;
	}

	public void setUfEmitente(String ufEmitente) {
		this.ufEmitente = ufEmitente;
	}

	public String getCepEmitente() {
		return cepEmitente;
	}

	public void setCepEmitente(String cepEmitente) {
		this.cepEmitente = cepEmitente;
	}

	public List<CcbParticipantes> getListaParticipantes() {
		return listaParticipantes;
	}

	public void setListaParticipantes(List<CcbParticipantes> listaParticipantes) {
		this.listaParticipantes = listaParticipantes;
	}

	public BigDecimal getValorLiquidoCredito() {
		return valorLiquidoCredito;
	}

	public void setValorLiquidoCredito(BigDecimal valorLiquidoCredito) {
		this.valorLiquidoCredito = valorLiquidoCredito;
	}

	public BigDecimal getValorCredito() {
		return valorCredito;
	}

	public void setValorCredito(BigDecimal valorCredito) {
		this.valorCredito = valorCredito;
	}

	public BigDecimal getCustoEmissao() {
		return custoEmissao;
	}

	public void setCustoEmissao(BigDecimal custoEmissao) {
		this.custoEmissao = custoEmissao;
	}

	public BigDecimal getValorIOF() {
		return valorIOF;
	}

	public void setValorIOF(BigDecimal valorIOF) {
		this.valorIOF = valorIOF;
	}

	public BigDecimal getValorDespesas() {
		return valorDespesas;
	}

	public void setValorDespesas(BigDecimal valorDespesas) {
		this.valorDespesas = valorDespesas;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getTaxaDeJurosMes() {
		return taxaDeJurosMes;
	}

	public void setTaxaDeJurosMes(BigDecimal taxaDeJurosMes) {
		this.taxaDeJurosMes = taxaDeJurosMes;
	}

	public BigDecimal getTaxaDeJurosAno() {
		return taxaDeJurosAno;
	}

	public void setTaxaDeJurosAno(BigDecimal taxaDeJurosAno) {
		this.taxaDeJurosAno = taxaDeJurosAno;
	}

	public BigDecimal getCetMes() {
		return cetMes;
	}

	public void setCetMes(BigDecimal cetMes) {
		this.cetMes = cetMes;
	}

	public BigDecimal getCetAno() {
		return cetAno;
	}

	public void setCetAno(BigDecimal cetAno) {
		this.cetAno = cetAno;
	}

	public String getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getNumeroBanco() {
		return numeroBanco;
	}

	public void setNumeroBanco(String numeroBanco) {
		this.numeroBanco = numeroBanco;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	public String getNumeroParcelasPagamento() {
		return numeroParcelasPagamento;
	}

	public void setNumeroParcelasPagamento(String numeroParcelasPagamento) {
		this.numeroParcelasPagamento = numeroParcelasPagamento;
	}

	public Date getVencimentoPrimeiraParcelaPagamento() {
		return vencimentoPrimeiraParcelaPagamento;
	}

	public void setVencimentoPrimeiraParcelaPagamento(Date vencimentoPrimeiraParcelaPagamento) {
		this.vencimentoPrimeiraParcelaPagamento = vencimentoPrimeiraParcelaPagamento;
	}

	public Date getVencimentoUltimaParcelaPagamento() {
		return vencimentoUltimaParcelaPagamento;
	}

	public void setVencimentoUltimaParcelaPagamento(Date vencimentoUltimaParcelaPagamento) {
		this.vencimentoUltimaParcelaPagamento = vencimentoUltimaParcelaPagamento;
	}

	public BigDecimal getMontantePagamento() {
		return montantePagamento;
	}

	public void setMontantePagamento(BigDecimal montantePagamento) {
		this.montantePagamento = montantePagamento;
	}

	public String getNumeroParcelasDFI() {
		return numeroParcelasDFI;
	}

	public void setNumeroParcelasDFI(String numeroParcelasDFI) {
		this.numeroParcelasDFI = numeroParcelasDFI;
	}

	public Date getVencimentoPrimeiraParcelaDFI() {
		return vencimentoPrimeiraParcelaDFI;
	}

	public void setVencimentoPrimeiraParcelaDFI(Date vencimentoPrimeiraParcelaDFI) {
		this.vencimentoPrimeiraParcelaDFI = vencimentoPrimeiraParcelaDFI;
	}

	public Date getVencimentoUltimaParcelaDFI() {
		return vencimentoUltimaParcelaDFI;
	}

	public void setVencimentoUltimaParcelaDFI(Date vencimentoUltimaParcelaDFI) {
		this.vencimentoUltimaParcelaDFI = vencimentoUltimaParcelaDFI;
	}

	public BigDecimal getMontanteDFI() {
		return montanteDFI;
	}

	public void setMontanteDFI(BigDecimal montanteDFI) {
		this.montanteDFI = montanteDFI;
	}

	public String getNumeroParcelasMIP() {
		return numeroParcelasMIP;
	}

	public void setNumeroParcelasMIP(String numeroParcelasMIP) {
		this.numeroParcelasMIP = numeroParcelasMIP;
	}

	public Date getVencimentoPrimeiraParcelaMIP() {
		return vencimentoPrimeiraParcelaMIP;
	}

	public void setVencimentoPrimeiraParcelaMIP(Date vencimentoPrimeiraParcelaMIP) {
		this.vencimentoPrimeiraParcelaMIP = vencimentoPrimeiraParcelaMIP;
	}

	public Date getVencimentoUltimaParcelaMIP() {
		return vencimentoUltimaParcelaMIP;
	}

	public void setVencimentoUltimaParcelaMIP(Date vencimentoUltimaParcelaMIP) {
		this.vencimentoUltimaParcelaMIP = vencimentoUltimaParcelaMIP;
	}

	public BigDecimal getMontanteMIP() {
		return montanteMIP;
	}

	public void setMontanteMIP(BigDecimal montanteMIP) {
		this.montanteMIP = montanteMIP;
	}

	public BigDecimal getTarifaAntecipada() {
		return tarifaAntecipada;
	}

	public void setTarifaAntecipada(BigDecimal tarifaAntecipada) {
		this.tarifaAntecipada = tarifaAntecipada;
	}

	public Date getDataDeEmissao() {
		return dataDeEmissao;
	}

	public void setDataDeEmissao(Date dataDeEmissao) {
		this.dataDeEmissao = dataDeEmissao;
	}

	public String getNumeroImovel() {
		return numeroImovel;
	}

	public void setNumeroImovel(String numeroImovel) {
		this.numeroImovel = numeroImovel;
	}

	public String getCartorioImovel() {
		return cartorioImovel;
	}

	public void setCartorioImovel(String cartorioImovel) {
		this.cartorioImovel = cartorioImovel;
	}

	public String getCidadeImovel() {
		return cidadeImovel;
	}

	public void setCidadeImovel(String cidadeImovel) {
		this.cidadeImovel = cidadeImovel;
	}

	public String getUfImovel() {
		return ufImovel;
	}

	public void setUfImovel(String ufImovel) {
		this.ufImovel = ufImovel;
	}

	public BigDecimal getVendaLeilao() {
		return vendaLeilao;
	}

	public void setVendaLeilao(BigDecimal vendaLeilao) {
		this.vendaLeilao = vendaLeilao;
	}

	public String getElaboradorNome() {
		return elaboradorNome;
	}

	public void setElaboradorNome(String elaboradorNome) {
		this.elaboradorNome = elaboradorNome;
	}

	public String getElaboradorCrea() {
		return elaboradorCrea;
	}

	public void setElaboradorCrea(String elaboradorCrea) {
		this.elaboradorCrea = elaboradorCrea;
	}

	public String getResponsavelNome() {
		return responsavelNome;
	}

	public void setResponsavelNome(String responsavelNome) {
		this.responsavelNome = responsavelNome;
	}

	public String getResponsavelCrea() {
		return responsavelCrea;
	}

	public void setResponsavelCrea(String responsavelCrea) {
		this.responsavelCrea = responsavelCrea;
	}

	public BigDecimal getPorcentagemImovel() {
		return porcentagemImovel;
	}

	public void setPorcentagemImovel(BigDecimal porcentagemImovel) {
		this.porcentagemImovel = porcentagemImovel;
	}

	public String getNomeTestemunha1() {
		return nomeTestemunha1;
	}

	public void setNomeTestemunha1(String nomeTestemunha1) {
		this.nomeTestemunha1 = nomeTestemunha1;
	}

	public String getCpfTestemunha1() {
		return cpfTestemunha1;
	}

	public void setCpfTestemunha1(String cpfTestemunha1) {
		this.cpfTestemunha1 = cpfTestemunha1;
	}

	public String getRgTestemunha1() {
		return rgTestemunha1;
	}

	public void setRgTestemunha1(String rgTestemunha1) {
		this.rgTestemunha1 = rgTestemunha1;
	}

	public String getNomeTestemunha2() {
		return nomeTestemunha2;
	}

	public void setNomeTestemunha2(String nomeTestemunha2) {
		this.nomeTestemunha2 = nomeTestemunha2;
	}

	public String getCpfTestemunha2() {
		return cpfTestemunha2;
	}

	public void setCpfTestemunha2(String cpfTestemunha2) {
		this.cpfTestemunha2 = cpfTestemunha2;
	}

	public String getRgTestemunha2() {
		return rgTestemunha2;
	}

	public void setRgTestemunha2(String rgTestemunha2) {
		this.rgTestemunha2 = rgTestemunha2;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getFileTypeInt() {
		return fileTypeInt;
	}

	public void setFileTypeInt(int fileTypeInt) {
		this.fileTypeInt = fileTypeInt;
	}

	public ByteArrayInputStream getBis() {
		return bis;
	}

	public void setBis(ByteArrayInputStream bis) {
		this.bis = bis;
	}

	public boolean isTerceiroGarantidor() {
		return terceiroGarantidor;
	}

	public void setTerceiroGarantidor(boolean terceiroGarantidor) {
		this.terceiroGarantidor = terceiroGarantidor;
	}

	public ArrayList<UploadedFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<UploadedFile> filesList) {
		this.filesList = filesList;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroCcb() {
		return numeroCcb;
	}

	public void setNumeroCcb(String numeroCcb) {
		this.numeroCcb = numeroCcb;
	}

	public String getNumeroOperacao() {
		return numeroOperacao;
	}

	public void setNumeroOperacao(String numeroOperacao) {
		this.numeroOperacao = numeroOperacao;
	}


	public BigDecimal getCustasCartorariasValor() {
		return custasCartorariasValor;
	}


	public void setCustasCartorariasValor(BigDecimal custasCartorariasValor) {
		this.custasCartorariasValor = custasCartorariasValor;
	}


	public BigDecimal getCertidaoDeCasamentoValor() {
		return certidaoDeCasamentoValor;
	}


	public void setCertidaoDeCasamentoValor(BigDecimal certidaoDeCasamentoValor) {
		this.certidaoDeCasamentoValor = certidaoDeCasamentoValor;
	}


	public BigDecimal getLaudoDeAvaliacaoValor() {
		return laudoDeAvaliacaoValor;
	}


	public void setLaudoDeAvaliacaoValor(BigDecimal laudoDeAvaliacaoValor) {
		this.laudoDeAvaliacaoValor = laudoDeAvaliacaoValor;
	}


	public BigDecimal getIntermediacaoValor() {
		return intermediacaoValor;
	}


	public void setIntermediacaoValor(BigDecimal intermediacaoValor) {
		this.intermediacaoValor = intermediacaoValor;
	}


	public String getIntermediacaoBanco() {
		return intermediacaoBanco;
	}


	public void setIntermediacaoBanco(String intermediacaoBanco) {
		this.intermediacaoBanco = intermediacaoBanco;
	}


	public String getIntermediacaoAgencia() {
		return intermediacaoAgencia;
	}


	public void setIntermediacaoAgencia(String intermediacaoAgencia) {
		this.intermediacaoAgencia = intermediacaoAgencia;
	}


	public String getIntermediacaoCC() {
		return intermediacaoCC;
	}


	public void setIntermediacaoCC(String intermediacaoCC) {
		this.intermediacaoCC = intermediacaoCC;
	}


	public String getIntermediacaoCNPJ() {
		return intermediacaoCNPJ;
	}


	public void setIntermediacaoCNPJ(String intermediacaoCNPJ) {
		this.intermediacaoCNPJ = intermediacaoCNPJ;
	}


	public String getIntermediacaoNome() {
		return intermediacaoNome;
	}


	public void setIntermediacaoNome(String intermediacaoNome) {
		this.intermediacaoNome = intermediacaoNome;
	}

	public String getIntermediacaoPix() {
		return intermediacaoPix;
	}

	public void setIntermediacaoPix(String intermediacaoPix) {
		this.intermediacaoPix = intermediacaoPix;
	}

	public List<CcbProcessosJudiciais> getProcessosJucidiais() {
		return processosJucidiais;
	}

	public void setProcessosJucidiais(List<CcbProcessosJudiciais> processosJucidiais) {
		this.processosJucidiais = processosJucidiais;
	}

	public BigDecimal getIptuEmAtrasoValor() {
		return iptuEmAtrasoValor;
	}


	public void setIptuEmAtrasoValor(BigDecimal iptuEmAtrasoValor) {
		this.iptuEmAtrasoValor = iptuEmAtrasoValor;
	}


	public BigDecimal getCondominioEmAtrasoValor() {
		return condominioEmAtrasoValor;
	}


	public void setCondominioEmAtrasoValor(BigDecimal condominioEmAtrasoValor) {
		this.condominioEmAtrasoValor = condominioEmAtrasoValor;
	}

	public BigDecimal getIqValor() {
		return iqValor;
	}

	public void setIqValor(BigDecimal iqValor) {
		this.iqValor = iqValor;
	}

	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}

	public String getTitularConta() {
		return titularConta;
	}

	public void setTitularConta(String titularConta) {
		this.titularConta = titularConta;
	}

	public String getSistemaAmortizacao() {
		return sistemaAmortizacao;
	}

	public void setSistemaAmortizacao(String sistemaAmortizacao) {
		this.sistemaAmortizacao = sistemaAmortizacao;
	}

	public BigDecimal getValorDfiParcela() {
		return valorDfiParcela;
	}

	public void setValorDfiParcela(BigDecimal valorDfiParcela) {
		this.valorDfiParcela = valorDfiParcela;
	}

	public BigDecimal getValorMipParcela() {
		return valorMipParcela;
	}

	public void setValorMipParcela(BigDecimal valorMipParcela) {
		this.valorMipParcela = valorMipParcela;
	}

	public String getPrazo() {
		return prazo;
	}

	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}

	public List<Segurado> getListSegurados() {
		return listSegurados;
	}

	public void setListSegurados(List<Segurado> listSegurados) {
		this.listSegurados = listSegurados;
	}
	
}