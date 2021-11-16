package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.infra.db.model.User;

public class PagadorRecebedor implements Serializable {

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
	private String nomePai;
	private String nomeMae;
	private String observacao1;
	private String observacao2;
	private String atividade;
	private String contato;
	private String numero;
	
	private String estadocivil;
	
	private String rg;
	private String cpf;
	private String cnpj;
	
	private String cep;
	
	private String banco;
	private String agencia;
	private String conta;
	private String nomeCC;
	private String cpfCC;
	private String cnpjCC;
	
	private String regimeCasamento;
	private String nomeConjuge;	
	private String cpfConjuge;	
	private String rgConjuge;
	private String sexoConjuge;
	private String telResidencialConjuge;
	private String telCelularConjuge;
	private Date dtNascimentoConjuge;	
	
	private String idIugu;
	
	private boolean userInvestidor;
	private String loginInvestidor;
	private String senhaInvestidor;
	
	private boolean casado;
	private boolean coobrigado;
	
	private String iuguAccountId;
	private String iuguNameAccount;
	private String iuguLiveApiToken;
	private String iuguTestApiToken;
	private String iuguUserToken;
	
	private String site;

	private String sexo;
	private String cargoConjuge;
	
	private String enderecoConjuge;
	private String bairroConjuge;
	private String complementoConjuge;
	private String cidadeConjuge;
	private String estadoConjuge;
	private String cepConjuge;
	
	private String nomeCoobrigado;	
	private String cpfCoobrigado;	
	private String rgCoobrigado;	
	private String cargoCoobrigado;
	private String enderecoCoobrigado;
	private String bairroCoobrigado;
	private String complementoCoobrigado;
	private String cidadeCoobrigado;
	private String estadoCoobrigado;
	private String cepCoobrigado;
	
	private String emailCoobrigado;
	private String emailConjuge;
	
	private Date dataEmissaoRGCoobrigado;
	private Date dataEmissaoRG;
	private Date dataEmissaoRGConjuge;
	
	private String estadocivilcoobrigado;
	private String cargoCoobrigadoCasado;
	private String nomeCoobrigadoCasado;
	private String cpfCoobrigadoCasado;
	private String rgCoobrigadoCasado;
	private Date dataEmissaoRGCoobrigadoCasado;
	private String telResidencialCoobrigadoCasado;
	private String telCelularCoobrigadoCasado;
	private Date dtNascimentoCoobrigadoCasado;
	private String sexoCoobrigadoCasado;
	private String enderecoCoobrigadoCasado;
	private String bairroCoobrigadoCasado;
	private String complementoCoobrigadoCasado;
	private String cidadeCoobrigadoCasado;
	private String estadoCoobrigadoCasado;
	private String cepCoobrigadoCasado;
	private String emailCoobrigadoCasado;
	private Date dtNascimentoCoobrigado;
	private String telResidencialCoobrigado;
	private String telCelularCoobrigado;
	private String sexoCoobrigado;
	
	
	private String nomeSecundario;
	private String cpfSecundario;
	private String cnpjSecundario;
	private String telCelularSecundario;
	private String emailSecundario;
	private String relaçãoComTomador;
	private Boolean tipoPessoaSecundarioIsFisica;
	
	private User usuario;
	
	private BigDecimal saldoInvestidor;

	public PagadorRecebedor(){
	}
	
	public PagadorRecebedor(long id, String nome, String endereco, String bairro, String complemento,
						 String cidade, String estado, String telResidencial, String telCelular,
						 String email, Date dtNascimento, String observacao1, String rg, String cpf, String cep){
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
		this.observacao1 = observacao1;
		this.rg = rg;
		this.cpf = cpf;
		this.cep = cep;
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
	 * @return the observacao1
	 */
	public String getObservacao1() {
		return observacao1;
	}

	/**
	 * @param observacao1 the observacao1 to set
	 */
	public void setObservacao1(String observacao1) {
		this.observacao1 = observacao1;
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
	        if (!(obj instanceof PagadorRecebedor))  
	            return false;  
	        PagadorRecebedor other = (PagadorRecebedor) obj;  
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
	 * @return the banco
	 */
	public String getBanco() {
		return banco;
	}

	/**
	 * @param banco the banco to set
	 */
	public void setBanco(String banco) {
		this.banco = banco;
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
	 * @return the conta
	 */
	public String getConta() {
		return conta;
	}

	/**
	 * @param conta the conta to set
	 */
	public void setConta(String conta) {
		this.conta = conta;
	}

	/**
	 * @return the nomeCC
	 */
	public String getNomeCC() {
		return nomeCC;
	}

	/**
	 * @param nomeCC the nomeCC to set
	 */
	public void setNomeCC(String nomeCC) {
		this.nomeCC = nomeCC;
	}

	/**
	 * @return the cpfCC
	 */
	public String getCpfCC() {
		return cpfCC;
	}

	/**
	 * @param cpfCC the cpfCC to set
	 */
	public void setCpfCC(String cpfCC) {
		this.cpfCC = cpfCC;
	}

	/**
	 * @return the cnpjCC
	 */
	public String getCnpjCC() {
		return cnpjCC;
	}

	/**
	 * @param cnpjCC the cnpjCC to set
	 */
	public void setCnpjCC(String cnpjCC) {
		this.cnpjCC = cnpjCC;
	}

	/**
	 * @return the observacao2
	 */
	public String getObservacao2() {
		return observacao2;
	}

	/**
	 * @param observacao2 the observacao2 to set
	 */
	public void setObservacao2(String observacao2) {
		this.observacao2 = observacao2;
	}

	/**
	 * @return the atividade
	 */
	public String getAtividade() {
		return atividade;
	}

	/**
	 * @param atividade the atividade to set
	 */
	public void setAtividade(String atividade) {
		this.atividade = atividade;
	}

	/**
	 * @return the nomeConjuge
	 */
	public String getNomeConjuge() {
		return nomeConjuge;
	}

	/**
	 * @param nomeConjuge the nomeConjuge to set
	 */
	public void setNomeConjuge(String nomeConjuge) {
		this.nomeConjuge = nomeConjuge;
	}

	/**
	 * @return the cpfConjuge
	 */
	public String getCpfConjuge() {
		return cpfConjuge;
	}

	/**
	 * @param cpfConjuge the cpfConjuge to set
	 */
	public void setCpfConjuge(String cpfConjuge) {
		this.cpfConjuge = cpfConjuge;
	}

	/**
	 * @return the rgConjuge
	 */
	public String getRgConjuge() {
		return rgConjuge;
	}

	/**
	 * @param rgConjuge the rgConjuge to set
	 */
	public void setRgConjuge(String rgConjuge) {
		this.rgConjuge = rgConjuge;
	}

	/**
	 * @return the casado
	 */
	public boolean isCasado() {
		return casado;
	}

	/**
	 * @param casado the casado to set
	 */
	public void setCasado(boolean casado) {
		this.casado = casado;
	}

	public String getIdIugu() {
		return idIugu;
	}

	public void setIdIugu(String idIugu) {
		this.idIugu = idIugu;
	}

	/**
	 * @return the iuguAccountId
	 */
	public String getIuguAccountId() {
		return iuguAccountId;
	}

	/**
	 * @param iuguAccountId the iuguAccountId to set
	 */
	public void setIuguAccountId(String iuguAccountId) {
		this.iuguAccountId = iuguAccountId;
	}

	/**
	 * @return the iuguNameAccount
	 */
	public String getIuguNameAccount() {
		return iuguNameAccount;
	}

	/**
	 * @param iuguNameAccount the iuguNameAccount to set
	 */
	public void setIuguNameAccount(String iuguNameAccount) {
		this.iuguNameAccount = iuguNameAccount;
	}

	/**
	 * @return the iuguLiveApiToken
	 */
	public String getIuguLiveApiToken() {
		return iuguLiveApiToken;
	}

	/**
	 * @param iuguLiveApiToken the iuguLiveApiToken to set
	 */
	public void setIuguLiveApiToken(String iuguLiveApiToken) {
		this.iuguLiveApiToken = iuguLiveApiToken;
	}

	/**
	 * @return the iuguTestApiToken
	 */
	public String getIuguTestApiToken() {
		return iuguTestApiToken;
	}

	/**
	 * @param iuguTestApiToken the iuguTestApiToken to set
	 */
	public void setIuguTestApiToken(String iuguTestApiToken) {
		this.iuguTestApiToken = iuguTestApiToken;
	}

	/**
	 * @return the iuguUserToken
	 */
	public String getIuguUserToken() {
		return iuguUserToken;
	}

	/**
	 * @param iuguUserToken the iuguUserToken to set
	 */
	public void setIuguUserToken(String iuguUserToken) {
		this.iuguUserToken = iuguUserToken;
	}

	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getCargoConjuge() {
		return cargoConjuge;
	}

	public void setCargoConjuge(String cargoConjuge) {
		this.cargoConjuge = cargoConjuge;
	}

	public String getEnderecoConjuge() {
		return enderecoConjuge;
	}

	public void setEnderecoConjuge(String enderecoConjuge) {
		this.enderecoConjuge = enderecoConjuge;
	}

	public String getBairroConjuge() {
		return bairroConjuge;
	}

	public void setBairroConjuge(String bairroConjuge) {
		this.bairroConjuge = bairroConjuge;
	}

	public String getComplementoConjuge() {
		return complementoConjuge;
	}

	public void setComplementoConjuge(String complementoConjuge) {
		this.complementoConjuge = complementoConjuge;
	}

	public String getCidadeConjuge() {
		return cidadeConjuge;
	}

	public void setCidadeConjuge(String cidadeConjuge) {
		this.cidadeConjuge = cidadeConjuge;
	}

	public String getEstadoConjuge() {
		return estadoConjuge;
	}

	public void setEstadoConjuge(String estadoConjuge) {
		this.estadoConjuge = estadoConjuge;
	}

	public String getCepConjuge() {
		return cepConjuge;
	}

	public void setCepConjuge(String cepConjuge) {
		this.cepConjuge = cepConjuge;
	}

	public String getNomeCoobrigado() {
		return nomeCoobrigado;
	}

	public void setNomeCoobrigado(String nomeCoobrigado) {
		this.nomeCoobrigado = nomeCoobrigado;
	}

	public String getCpfCoobrigado() {
		return cpfCoobrigado;
	}

	public void setCpfCoobrigado(String cpfCoobrigado) {
		this.cpfCoobrigado = cpfCoobrigado;
	}

	public String getRgCoobrigado() {
		return rgCoobrigado;
	}

	public void setRgCoobrigado(String rgCoobrigado) {
		this.rgCoobrigado = rgCoobrigado;
	}

	public String getCargoCoobrigado() {
		return cargoCoobrigado;
	}

	public void setCargoCoobrigado(String cargoCoobrigado) {
		this.cargoCoobrigado = cargoCoobrigado;
	}

	public String getEnderecoCoobrigado() {
		return enderecoCoobrigado;
	}

	public void setEnderecoCoobrigado(String enderecoCoobrigado) {
		this.enderecoCoobrigado = enderecoCoobrigado;
	}

	public String getBairroCoobrigado() {
		return bairroCoobrigado;
	}

	public void setBairroCoobrigado(String bairroCoobrigado) {
		this.bairroCoobrigado = bairroCoobrigado;
	}

	public String getComplementoCoobrigado() {
		return complementoCoobrigado;
	}

	public void setComplementoCoobrigado(String complementoCoobrigado) {
		this.complementoCoobrigado = complementoCoobrigado;
	}

	public String getCidadeCoobrigado() {
		return cidadeCoobrigado;
	}

	public void setCidadeCoobrigado(String cidadeCoobrigado) {
		this.cidadeCoobrigado = cidadeCoobrigado;
	}

	public String getEstadoCoobrigado() {
		return estadoCoobrigado;
	}

	public void setEstadoCoobrigado(String estadoCoobrigado) {
		this.estadoCoobrigado = estadoCoobrigado;
	}

	public String getCepCoobrigado() {
		return cepCoobrigado;
	}

	public void setCepCoobrigado(String cepCoobrigado) {
		this.cepCoobrigado = cepCoobrigado;
	}

	public boolean isCoobrigado() {
		return coobrigado;
	}

	public void setCoobrigado(boolean coobrigado) {
		this.coobrigado = coobrigado;
	}

	public String getEmailCoobrigado() {
		return emailCoobrigado;
	}

	public void setEmailCoobrigado(String emailCoobrigado) {
		this.emailCoobrigado = emailCoobrigado;
	}

	public String getEmailConjuge() {
		return emailConjuge;
	}

	public void setEmailConjuge(String emailConjuge) {
		this.emailConjuge = emailConjuge;
	}

	public Date getDataEmissaoRGCoobrigado() {
		return dataEmissaoRGCoobrigado;
	}

	public void setDataEmissaoRGCoobrigado(Date dataEmissaoRGCoobrigado) {
		this.dataEmissaoRGCoobrigado = dataEmissaoRGCoobrigado;
	}

	public Date getDataEmissaoRG() {
		return dataEmissaoRG;
	}

	public void setDataEmissaoRG(Date dataEmissaoRG) {
		this.dataEmissaoRG = dataEmissaoRG;
	}

	public Date getDataEmissaoRGConjuge() {
		return dataEmissaoRGConjuge;
	}

	public void setDataEmissaoRGConjuge(Date dataEmissaoRGConjuge) {
		this.dataEmissaoRGConjuge = dataEmissaoRGConjuge;
	}

	public String getEstadocivil() {
		return estadocivil;
	}

	public void setEstadocivil(String estadocivil) {
		this.estadocivil = estadocivil;
	}

	public String getSexoConjuge() {
		return sexoConjuge;
	}

	public void setSexoConjuge(String sexoConjuge) {
		this.sexoConjuge = sexoConjuge;
	}

	public String getTelResidencialConjuge() {
		return telResidencialConjuge;
	}

	public void setTelResidencialConjuge(String telResidencialConjuge) {
		this.telResidencialConjuge = telResidencialConjuge;
	}

	public String getTelCelularConjuge() {
		return telCelularConjuge;
	}

	public void setTelCelularConjuge(String telCelularConjuge) {
		this.telCelularConjuge = telCelularConjuge;
	}

	public Date getDtNascimentoConjuge() {
		return dtNascimentoConjuge;
	}

	public void setDtNascimentoConjuge(Date dtNascimentoConjuge) {
		this.dtNascimentoConjuge = dtNascimentoConjuge;
	}

	public String getEstadocivilcoobrigado() {
		return estadocivilcoobrigado;
	}

	public void setEstadocivilcoobrigado(String estadocivilcoobrigado) {
		this.estadocivilcoobrigado = estadocivilcoobrigado;
	}

	public String getNomeCoobrigadoCasado() {
		return nomeCoobrigadoCasado;
	}

	public void setNomeCoobrigadoCasado(String nomeCoobrigadoCasado) {
		this.nomeCoobrigadoCasado = nomeCoobrigadoCasado;
	}

	public String getCpfCoobrigadoCasado() {
		return cpfCoobrigadoCasado;
	}

	public void setCpfCoobrigadoCasado(String cpfCoobrigadoCasado) {
		this.cpfCoobrigadoCasado = cpfCoobrigadoCasado;
	}

	public String getRgCoobrigadoCasado() {
		return rgCoobrigadoCasado;
	}

	public void setRgCoobrigadoCasado(String rgCoobrigadoCasado) {
		this.rgCoobrigadoCasado = rgCoobrigadoCasado;
	}

	public Date getDataEmissaoRGCoobrigadoCasado() {
		return dataEmissaoRGCoobrigadoCasado;
	}

	public void setDataEmissaoRGCoobrigadoCasado(Date dataEmissaoRGCoobrigadoCasado) {
		this.dataEmissaoRGCoobrigadoCasado = dataEmissaoRGCoobrigadoCasado;
	}

	public String getTelResidencialCoobrigadoCasado() {
		return telResidencialCoobrigadoCasado;
	}

	public void setTelResidencialCoobrigadoCasado(String telResidencialCoobrigadoCasado) {
		this.telResidencialCoobrigadoCasado = telResidencialCoobrigadoCasado;
	}

	public String getTelCelularCoobrigadoCasado() {
		return telCelularCoobrigadoCasado;
	}

	public void setTelCelularCoobrigadoCasado(String telCelularCoobrigadoCasado) {
		this.telCelularCoobrigadoCasado = telCelularCoobrigadoCasado;
	}

	public Date getDtNascimentoCoobrigadoCasado() {
		return dtNascimentoCoobrigadoCasado;
	}

	public void setDtNascimentoCoobrigadoCasado(Date dtNascimentoCoobrigadoCasado) {
		this.dtNascimentoCoobrigadoCasado = dtNascimentoCoobrigadoCasado;
	}

	public String getSexoCoobrigadoCasado() {
		return sexoCoobrigadoCasado;
	}

	public void setSexoCoobrigadoCasado(String sexoCoobrigadoCasado) {
		this.sexoCoobrigadoCasado = sexoCoobrigadoCasado;
	}

	public String getEnderecoCoobrigadoCasado() {
		return enderecoCoobrigadoCasado;
	}

	public void setEnderecoCoobrigadoCasado(String enderecoCoobrigadoCasado) {
		this.enderecoCoobrigadoCasado = enderecoCoobrigadoCasado;
	}

	public String getBairroCoobrigadoCasado() {
		return bairroCoobrigadoCasado;
	}

	public void setBairroCoobrigadoCasado(String bairroCoobrigadoCasado) {
		this.bairroCoobrigadoCasado = bairroCoobrigadoCasado;
	}

	public String getComplementoCoobrigadoCasado() {
		return complementoCoobrigadoCasado;
	}

	public void setComplementoCoobrigadoCasado(String complementoCoobrigadoCasado) {
		this.complementoCoobrigadoCasado = complementoCoobrigadoCasado;
	}

	public String getCidadeCoobrigadoCasado() {
		return cidadeCoobrigadoCasado;
	}

	public void setCidadeCoobrigadoCasado(String cidadeCoobrigadoCasado) {
		this.cidadeCoobrigadoCasado = cidadeCoobrigadoCasado;
	}

	public String getEstadoCoobrigadoCasado() {
		return estadoCoobrigadoCasado;
	}

	public void setEstadoCoobrigadoCasado(String estadoCoobrigadoCasado) {
		this.estadoCoobrigadoCasado = estadoCoobrigadoCasado;
	}

	public String getCepCoobrigadoCasado() {
		return cepCoobrigadoCasado;
	}

	public void setCepCoobrigadoCasado(String cepCoobrigadoCasado) {
		this.cepCoobrigadoCasado = cepCoobrigadoCasado;
	}

	public String getEmailCoobrigadoCasado() {
		return emailCoobrigadoCasado;
	}

	public void setEmailCoobrigadoCasado(String emailCoobrigadoCasado) {
		this.emailCoobrigadoCasado = emailCoobrigadoCasado;
	}

	public Date getDtNascimentoCoobrigado() {
		return dtNascimentoCoobrigado;
	}

	public void setDtNascimentoCoobrigado(Date dtNascimentoCoobrigado) {
		this.dtNascimentoCoobrigado = dtNascimentoCoobrigado;
	}

	public String getTelResidencialCoobrigado() {
		return telResidencialCoobrigado;
	}

	public void setTelResidencialCoobrigado(String telResidencialCoobrigado) {
		this.telResidencialCoobrigado = telResidencialCoobrigado;
	}

	public String getTelCelularCoobrigado() {
		return telCelularCoobrigado;
	}

	public void setTelCelularCoobrigado(String telCelularCoobrigado) {
		this.telCelularCoobrigado = telCelularCoobrigado;
	}

	public String getSexoCoobrigado() {
		return sexoCoobrigado;
	}

	public void setSexoCoobrigado(String sexoCoobrigado) {
		this.sexoCoobrigado = sexoCoobrigado;
	}

	public String getCargoCoobrigadoCasado() {
		return cargoCoobrigadoCasado;
	}

	public void setCargoCoobrigadoCasado(String cargoCoobrigadoCasado) {
		this.cargoCoobrigadoCasado = cargoCoobrigadoCasado;
	}

	public boolean isUserInvestidor() {
		return userInvestidor;
	}

	public void setUserInvestidor(boolean userInvestidor) {
		this.userInvestidor = userInvestidor;
	}

	public String getSenhaInvestidor() {
		return senhaInvestidor;
	}

	public void setSenhaInvestidor(String senhaInvestidor) {
		this.senhaInvestidor = senhaInvestidor;
	}

	public String getLoginInvestidor() {
		return loginInvestidor;
	}

	public void setLoginInvestidor(String loginInvestidor) {
		this.loginInvestidor = loginInvestidor;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public BigDecimal getSaldoInvestidor() {
		return saldoInvestidor;
	}

	public void setSaldoInvestidor(BigDecimal saldoInvestidor) {
		this.saldoInvestidor = saldoInvestidor;
	}

	public String getNomeSecundario() {
		return nomeSecundario;
	}

	public void setNomeSecundario(String nomeSecundario) {
		this.nomeSecundario = nomeSecundario;
	}

	public String getCpfSecundario() {
		return cpfSecundario;
	}

	public void setCpfSecundario(String cpfSecundario) {
		this.cpfSecundario = cpfSecundario;
	}

	public String getCnpjSecundario() {
		return cnpjSecundario;
	}

	public void setCnpjSecundario(String cnpjSecundario) {
		this.cnpjSecundario = cnpjSecundario;
	}

	public String getTelCelularSecundario() {
		return telCelularSecundario;
	}

	public void setTelCelularSecundario(String telCelularSecundario) {
		this.telCelularSecundario = telCelularSecundario;
	}

	public String getEmailSecundario() {
		return emailSecundario;
	}

	public void setEmailSecundario(String emailSecundario) {
		this.emailSecundario = emailSecundario;
	}

	public String getRelaçãoComTomador() {
		return relaçãoComTomador;
	}

	public void setRelaçãoComTomador(String relaçãoComTomador) {
		this.relaçãoComTomador = relaçãoComTomador;
	}

	public Boolean getTipoPessoaSecundarioIsFisica() {
		return tipoPessoaSecundarioIsFisica;
	}

	public void setTipoPessoaSecundarioIsFisica(Boolean tipoPessoaSecundarioIsFisica) {
		this.tipoPessoaSecundarioIsFisica = tipoPessoaSecundarioIsFisica;
	}

	public String getRegimeCasamento() {
		return regimeCasamento;
	}

	public void setRegimeCasamento(String regimeCasamento) {
		this.regimeCasamento = regimeCasamento;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
}

