package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;


public class ImovelCobrancaAdicionais implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private ImovelCobranca imovel;
	private ContratoCobranca contratoCobranca;
	
	private BigDecimal porcentagem;
	private BigDecimal porcentagemLeilao;
	private BigDecimal valorRegistro;
	private BigDecimal valorCredito;
	private String relacaoComGarantia;
	private boolean comprovanteMatriculaCheckList;
	private boolean comprovanteFotosImovelCheckList;
	private boolean comprovanteIptuImovelCheckList;
	private boolean cndIptuExtratoDebitoCheckList;//
	private boolean cndCondominioExtratoDebitoCheckList;//
	private boolean matriculaGaragemCheckList;//
	private boolean simuladorCheckList;

	public ImovelCobrancaAdicionais() {
		super();
		this.imovel = new ImovelCobranca();
		this.contratoCobranca = new ContratoCobranca();
	}

	public ImovelCobrancaAdicionais(ImovelCobranca imovel, ContratoCobranca contratoCobranca) {
		super();
		this.imovel = imovel;
		this.contratoCobranca = contratoCobranca;
	}
	
	public ImovelCobrancaAdicionais(ImovelCobranca imovel) {
		super();
		this.imovel = imovel;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public ImovelCobranca getImovel() {
		return imovel;
	}
	public void setImovel(ImovelCobranca imovel) {
		this.imovel = imovel;
	}
	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}
	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}
	public String getRelacaoComGarantia() {
		return relacaoComGarantia;
	}
	public void setRelacaoComGarantia(String relacaoComGarantia) {
		this.relacaoComGarantia = relacaoComGarantia;
	}
	public boolean isComprovanteMatriculaCheckList() {
		return comprovanteMatriculaCheckList;
	}
	public void setComprovanteMatriculaCheckList(boolean comprovanteMatriculaCheckList) {
		this.comprovanteMatriculaCheckList = comprovanteMatriculaCheckList;
	}
	public boolean isComprovanteFotosImovelCheckList() {
		return comprovanteFotosImovelCheckList;
	}
	public void setComprovanteFotosImovelCheckList(boolean comprovanteFotosImovelCheckList) {
		this.comprovanteFotosImovelCheckList = comprovanteFotosImovelCheckList;
	}
	public boolean isComprovanteIptuImovelCheckList() {
		return comprovanteIptuImovelCheckList;
	}
	public void setComprovanteIptuImovelCheckList(boolean comprovanteIptuImovelCheckList) {
		this.comprovanteIptuImovelCheckList = comprovanteIptuImovelCheckList;
	}
	public boolean isCndIptuExtratoDebitoCheckList() {
		return cndIptuExtratoDebitoCheckList;
	}
	public void setCndIptuExtratoDebitoCheckList(boolean cndIptuExtratoDebitoCheckList) {
		this.cndIptuExtratoDebitoCheckList = cndIptuExtratoDebitoCheckList;
	}
	public boolean isCndCondominioExtratoDebitoCheckList() {
		return cndCondominioExtratoDebitoCheckList;
	}
	public void setCndCondominioExtratoDebitoCheckList(boolean cndCondominioExtratoDebitoCheckList) {
		this.cndCondominioExtratoDebitoCheckList = cndCondominioExtratoDebitoCheckList;
	}
	public boolean isMatriculaGaragemCheckList() {
		return matriculaGaragemCheckList;
	}
	public void setMatriculaGaragemCheckList(boolean matriculaGaragemCheckList) {
		this.matriculaGaragemCheckList = matriculaGaragemCheckList;
	}
	public boolean isSimuladorCheckList() {
		return simuladorCheckList;
	}
	public void setSimuladorCheckList(boolean simuladorCheckList) {
		this.simuladorCheckList = simuladorCheckList;
	}
	public BigDecimal getPorcentagem() {
		return porcentagem;
	}
	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}
	public BigDecimal getValorRegistro() {
		return valorRegistro;
	}
	public void setValorRegistro(BigDecimal valorRegistro) {
		this.valorRegistro = valorRegistro;
	}
	public BigDecimal getPorcentagemLeilao() {
		return porcentagemLeilao;
	}
	public void setPorcentagemLeilao(BigDecimal porcentagemLeilao) {
		this.porcentagemLeilao = porcentagemLeilao;
	}
	public BigDecimal getValorCredito() {
		return valorCredito;
	}
	public void setValorCredito(BigDecimal valorCredito) {
		this.valorCredito = valorCredito;
	}
}


