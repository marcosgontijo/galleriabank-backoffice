package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class EmpresaCobranca implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nome;
	private String cnpj;
	private String endereco;
	private String bairro;
	private String cep;
	private String cidade;
	private String estado;
	private String agencia;
	private String digitoAgencia;
	private String codigoBeneficiario;
	private String digitoBeneficiario;
	private String numeroConvenio;
	private String carteira;
	private String localPagamento;
	private String instrucao1;
	private String instrucao2;
	private String instrucao3;
	private String instrucao4;
	private String instrucao5;
	private String nossoNumero;
	private String prefixoNumeroDoc;
	
	private String sistema;
		
	public EmpresaCobranca(){
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
	 * @return the cnpj
	 */
	public String getCnpj() {
		return cnpj;
	}



	/**
	 * @param cnpj the cnpj to set
	 */
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
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
	 * @return the agencia
	 */
	public String getAgencia() {
		return agencia;
	}



	/**
	 * @param agencia the agencia to set
	 */
	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}



	/**
	 * @return the digitoAgencia
	 */
	public String getDigitoAgencia() {
		return digitoAgencia;
	}



	/**
	 * @param digitoAgencia the digitoAgencia to set
	 */
	public void setDigitoAgencia(String digitoAgencia) {
		this.digitoAgencia = digitoAgencia;
	}



	/**
	 * @return the codigoBeneficiario
	 */
	public String getCodigoBeneficiario() {
		return codigoBeneficiario;
	}



	/**
	 * @param codigoBeneficiario the codigoBeneficiario to set
	 */
	public void setCodigoBeneficiario(String codigoBeneficiario) {
		this.codigoBeneficiario = codigoBeneficiario;
	}



	/**
	 * @return the digitoBeneficiario
	 */
	public String getDigitoBeneficiario() {
		return digitoBeneficiario;
	}



	/**
	 * @param digitoBeneficiario the digitoBeneficiario to set
	 */
	public void setDigitoBeneficiario(String digitoBeneficiario) {
		this.digitoBeneficiario = digitoBeneficiario;
	}



	/**
	 * @return the numeroConvenio
	 */
	public String getNumeroConvenio() {
		return numeroConvenio;
	}



	/**
	 * @param numeroConvenio the numeroConvenio to set
	 */
	public void setNumeroConvenio(String numeroConvenio) {
		this.numeroConvenio = numeroConvenio;
	}



	/**
	 * @return the carteira
	 */
	public String getCarteira() {
		return carteira;
	}



	/**
	 * @param carteira the carteira to set
	 */
	public void setCarteira(String carteira) {
		this.carteira = carteira;
	}


	/**
	 * @return the localPagamento
	 */
	public String getLocalPagamento() {
		return localPagamento;
	}


	/**
	 * @param localPagamento the localPagamento to set
	 */
	public void setLocalPagamento(String localPagamento) {
		this.localPagamento = localPagamento;
	}


	/**
	 * @return the instrucao1
	 */
	public String getInstrucao1() {
		return instrucao1;
	}


	/**
	 * @param instrucao1 the instrucao1 to set
	 */
	public void setInstrucao1(String instrucao1) {
		this.instrucao1 = instrucao1;
	}


	/**
	 * @return the instrucao2
	 */
	public String getInstrucao2() {
		return instrucao2;
	}


	/**
	 * @param instrucao2 the instrucao2 to set
	 */
	public void setInstrucao2(String instrucao2) {
		this.instrucao2 = instrucao2;
	}


	/**
	 * @return the instrucao3
	 */
	public String getInstrucao3() {
		return instrucao3;
	}


	/**
	 * @param instrucao3 the instrucao3 to set
	 */
	public void setInstrucao3(String instrucao3) {
		this.instrucao3 = instrucao3;
	}


	/**
	 * @return the instrucao4
	 */
	public String getInstrucao4() {
		return instrucao4;
	}


	/**
	 * @param instrucao4 the instrucao4 to set
	 */
	public void setInstrucao4(String instrucao4) {
		this.instrucao4 = instrucao4;
	}


	/**
	 * @return the instrucao5
	 */
	public String getInstrucao5() {
		return instrucao5;
	}


	/**
	 * @param instrucao5 the instrucao5 to set
	 */
	public void setInstrucao5(String instrucao5) {
		this.instrucao5 = instrucao5;
	}
	
	/**
	 * @return the sistema
	 */
	public String getSistema() {
		return sistema;
	}


	/**
	 * @param sistema the sistema to set
	 */
	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	/**
	 * @return the nossoNumero
	 */
	public String getNossoNumero() {
		return nossoNumero;
	}


	/**
	 * @param nossoNumero the nossoNumero to set
	 */
	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}


	/**
	 * @return the prefixoNumeroDoc
	 */
	public String getPrefixoNumeroDoc() {
		return prefixoNumeroDoc;
	}

	/**
	 * @param prefixoNumeroDoc the prefixoNumeroDoc to set
	 */
	public void setPrefixoNumeroDoc(String prefixoNumeroDoc) {
		this.prefixoNumeroDoc = prefixoNumeroDoc;
	}


	@Override  
	    public boolean equals(Object obj){  
	        if (this == obj)  
	            return true;  
	        if (obj == null)  
	            return false;  
	        if (!(obj instanceof EmpresaCobranca))  
	            return false;  
	        EmpresaCobranca other = (EmpresaCobranca) obj;  
	        if (nome == null){  
	            if (other.nome != null)  
	                return false;  
	        } else if (!nome.equals(other.nome))  
	            return false;  
	        return true;  
	    }
}