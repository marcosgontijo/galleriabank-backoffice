package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ImovelCobranca implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroMatricula;
	private String nome;
	private String endereco;
	private String bairro;
	private String complemento;
	private String cidade;
	private String estado;
	private String telResidencial;
	private String observacao;
	private String cep;
	private String cartorio;
	private String tipo;
	private String areaConstruida;
	private String areaTotal;
	private String linkGMaps;
	private String possuiDivida;
	private String ocupacao;
	private BigDecimal valoEstimado;
	private Date dataCompra;
	private String idadeCompra;
	private String nomeProprietario;
	private boolean comprovanteMatriculaCheckList;
	private boolean comprovanteFotosImovelCheckList;
	private boolean comprovanteIptuImovelCheckList;
	
	public ImovelCobranca(){
		resetarBololean();
	}
	
	public ImovelCobranca(long id, String numeroMatricula, String nome, String endereco, String bairro, String complemento,
						 String cidade, String estado, String telResidencial, String observacao, String cep){
		resetarBololean();
		this.id = id;
		this.numeroMatricula = numeroMatricula;
		this.nome = nome;
		this.endereco = endereco;
		this.bairro = bairro;
		this.complemento = complemento;
		this.cidade = cidade;
		this.estado = estado;
		this.telResidencial = telResidencial;
		this.observacao = observacao;
		this.cep = cep;
	}
	
	public void calcularDataDeCompra() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date dateHoje = dataHoje.getTime();
		
		long idadeLong = dateHoje.getTime() - this.getDataCompra().getTime();
		idadeLong = TimeUnit.DAYS.convert(idadeLong, TimeUnit.MILLISECONDS);
		idadeLong = idadeLong / 30;
		idadeLong = idadeLong / 12;
	    this.setIdadeCompra(CommonsUtil.stringValue(idadeLong));
	}
	
	public void resetarBololean() {
		this.comprovanteMatriculaCheckList = false;
		this.comprovanteFotosImovelCheckList = false;
		this.comprovanteIptuImovelCheckList = false;
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
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the endereco
	 */
	public String getEndereco() {
		return endereco;
	}

	/**
	 * @param endereco the endereco to set
	 */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	/**
	 * @return the bairro
	 */
	public String getBairro() {
		return bairro;
	}

	/**
	 * @param bairro the bairro to set
	 */
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	/**
	 * @return the complemento
	 */
	public String getComplemento() {
		return complemento;
	}

	/**
	 * @param complemento the complemento to set
	 */
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	/**
	 * @return the cidade
	 */
	public String getCidade() {
		return cidade;
	}

	/**
	 * @param cidade the cidade to set
	 */
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	/**
	 * @return the estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * @return the telResidencial
	 */
	public String getTelResidencial() {
		return telResidencial;
	}

	/**
	 * @param telResidencial the telResidencial to set
	 */
	public void setTelResidencial(String telResidencial) {
		this.telResidencial = telResidencial;
	}
	
	 /**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	 /**
	 * @return the cep
	 */
	public String getCep() {
		return cep;
	}

	/**
	 * @param cep the cep to set
	 */
	public void setCep(String cep) {
		this.cep = cep;
	}	

	/**
	 * @return the numeroMatricula
	 */
	public String getNumeroMatricula() {
		return numeroMatricula;
	}

	/**
	 * @param numeroMatricula the numeroMatricula to set
	 */
	public void setNumeroMatricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}

	/**
	 * @return the cartorio
	 */
	public String getCartorio() {
		return cartorio;
	}

	/**
	 * @param cartorio the cartorio to set
	 */
	public void setCartorio(String cartorio) {
		this.cartorio = cartorio;
	}
	
	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the areaConstruida
	 */
	public String getAreaConstruida() {
		return areaConstruida;
	}

	/**
	 * @param areaConstruida the areaConstruida to set
	 */
	public void setAreaConstruida(String areaConstruida) {
		this.areaConstruida = areaConstruida;
	}

	/**
	 * @return the areaTotal
	 */
	public String getAreaTotal() {
		return areaTotal;
	}

	/**
	 * @param areaTotal the areaTotal to set
	 */
	public void setAreaTotal(String areaTotal) {
		this.areaTotal = areaTotal;
	}

	/**
	 * @return the linkGMaps
	 */
	public String getLinkGMaps() {
		return linkGMaps;
	}

	public String getPossuiDivida() {
		return possuiDivida;
	}

	public void setPossuiDivida(String possuiDivida) {
		this.possuiDivida = possuiDivida;
	}

	/**
	 * @param linkGMaps the linkGMaps to set
	 */
	public void setLinkGMaps(String linkGMaps) {
		this.linkGMaps = linkGMaps;
	}

	@Override  
	    public boolean equals(Object obj){  
	        if (this == obj)  
	            return true;  
	        if (obj == null)  
	            return false;  
	        if (!(obj instanceof ImovelCobranca))  
	            return false;  
	        ImovelCobranca other = (ImovelCobranca) obj;  
	        if (nome == null){  
	            if (other.nome != null)  
	                return false;  
	        } else if (!nome.equals(other.nome))  
	            return false;  
	        return true;  
	    }

	public String getOcupacao() {
		return ocupacao;
	}

	public void setOcupacao(String ocupacao) {
		this.ocupacao = ocupacao;
	}

	public BigDecimal getValoEstimado() {
		return valoEstimado;
	}

	public void setValoEstimado(BigDecimal valoEstimado) {
		this.valoEstimado = valoEstimado;
	}

	public Date getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	public String getIdadeCompra() {
		return idadeCompra;
	}

	public void setIdadeCompra(String idadeCompra) {
		this.idadeCompra = idadeCompra;
	}

	public String getNomeProprietario() {
		return nomeProprietario;
	}

	public void setNomeProprietario(String nomeProprietario) {
		this.nomeProprietario = nomeProprietario;
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
}