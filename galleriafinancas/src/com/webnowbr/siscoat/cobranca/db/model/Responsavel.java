package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import com.webnowbr.siscoat.common.BancosEnum;

public class Responsavel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nome;
	private String endereco;
	private String bairro;
	private String complemento;
	private String cidade;
	private String estado;
	private String telResidencial;
	private String telCelular;
	private String email;
	private Date dtNascimento;
	private String observacao;
	private String codigo;
	private String contato;
	
	private Date dataCadastro;
	private Responsavel responsavelCaptador;
	private Responsavel responsavelAssistenteComercial;
	
	private String rg;
	private String cpf;
	private String cnpj;
	
	private String cpfCC;//
	private String cnpjCC;//
	private String nomeCC;//
	private String banco;//
	private List<SelectItem> listaBancos;
	private String agencia;//
	private String conta;//
	private String pix;//

	
	private String cep;
	private Responsavel donoResponsavel;
	
	private BigDecimal taxaRemuneracao;
	
	private String whatsAppNumero;
	
	
	public Responsavel(){
	}
	
	public Responsavel(long id, String nome, String endereco, String bairro, String complemento,
						 String cidade, String estado, String telResidencial, String telCelular,
						 String email, Date dtNascimento, String observacao, String rg, String cpf, String cep){
		this.id = id;
		this.nome = nome;
		this.endereco = endereco;
		this.bairro = bairro;
		this.complemento = complemento;
		this.cidade = cidade;
		this.estado = estado;
		this.telResidencial = telResidencial;
		this.telCelular = telCelular;
		this.email = email;
		this.dtNascimento = dtNascimento;
		this.observacao = observacao;
		this.rg = rg;
		this.cpf = cpf;
		this.cep = cep;
	}
	
	public List<String> completeBancos(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> bancos = new ArrayList<>();
		for(BancosEnum banco : BancosEnum.values()) {
			String bancoStr = banco.getNomeCompleto().toString();
			bancos.add(bancoStr);
		}
		return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
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
	 * @return the telCelular
	 */
	public String getTelCelular() {
		return telCelular;
	}

	/**
	 * @param telCelular the telCelular to set
	 */
	public void setTelCelular(String telCelular) {
		this.telCelular = telCelular;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the dtNascimento
	 */
	public Date getDtNascimento() {
		return dtNascimento;
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
	 * @param dtNascimento the dtNascimento to set
	 */
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	 @Override  
	    public boolean equals(Object obj){  
	        if (this == obj)  
	            return true;  
	        if (obj == null)  
	            return false;  
	        if (!(obj instanceof Responsavel))  
	            return false;  
	        Responsavel other = (Responsavel) obj;  
	        if (nome == null){  
	            if (other.nome != null)  
	                return false;  
	        } else if (!nome.equals(other.nome))  
	            return false;  
	        return true;  
	    }

	/**
	 * @return the rg
	 */
	public String getRg() {
		return rg;
	}

	/**
	 * @param rg the rg to set
	 */
	public void setRg(String rg) {
		this.rg = rg;
	}

	/**
	 * @return the cpf
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * @param cpf the cpf to set
	 */
	public void setCpf(String cpf) {
		this.cpf = cpf;
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
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public Responsavel getDonoResponsavel() {
		return donoResponsavel;
	}

	public void setDonoResponsavel(Responsavel donoResponsavel) {
		this.donoResponsavel = donoResponsavel;
	}

	public BigDecimal getTaxaRemuneracao() {
		return taxaRemuneracao;
	}

	public void setTaxaRemuneracao(BigDecimal taxaRemuneracao) {
		this.taxaRemuneracao = taxaRemuneracao;
	}

	public String getWhatsAppNumero() {
		return whatsAppNumero;
	}

	public void setWhatsAppNumero(String whatsAppNumero) {
		this.whatsAppNumero = whatsAppNumero;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Responsavel getResponsavelCaptador() {
		return responsavelCaptador;
	}

	public void setResponsavelCaptador(Responsavel responsavelCaptador) {
		this.responsavelCaptador = responsavelCaptador;
	}

	public Responsavel getResponsavelAssistenteComercial() {
		return responsavelAssistenteComercial;
	}

	public void setResponsavelAssistenteComercial(Responsavel responsavelAssistenteComercial) {
		this.responsavelAssistenteComercial = responsavelAssistenteComercial;
	}

	public String getCpfCC() {
		return cpfCC;
	}

	public void setCpfCC(String cpfCC) {
		this.cpfCC = cpfCC;
	}

	public String getCnpjCC() {
		return cnpjCC;
	}

	public void setCnpjCC(String cnpjCC) {
		this.cnpjCC = cnpjCC;
	}

	public String getNomeCC() {
		return nomeCC;
	}

	public void setNomeCC(String nomeCC) {
		this.nomeCC = nomeCC;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public List<SelectItem> getListaBancos() {
		return listaBancos;
	}

	public void setListaBancos(List<SelectItem> listaBancos) {
		this.listaBancos = listaBancos;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	
}