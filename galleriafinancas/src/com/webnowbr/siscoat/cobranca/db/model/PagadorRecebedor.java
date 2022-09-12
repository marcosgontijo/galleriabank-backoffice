package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
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
	private String idade;
	private String nomePai;
	private String nomeMae;
	private String observacao1;
	private String observacao2;
	private String atividade;
	private String contato;
	private String numero;
	private String sexo;
	
	private String codigoMoneyPlus;
	
	private String estadocivil;
	
	private String regimeCasamento; 
	private String cargoConjuge;
	private String nomeConjuge;	
	private String cpfConjuge; 
	private String rgConjuge; 
	private String sexoConjuge; 
	private String telResidencialConjuge; 
	private String telCelularConjuge; 
	private Date dtNascimentoConjuge; 
	private String idadeConjuge;
	private String nomePaiConjuge;
	private String nomeMaeConjuge;
	private String enderecoConjuge;
	private String bairroConjuge;
	private String complementoConjuge;
	private String cidadeConjuge;
	private String estadoConjuge;
	private String cepConjuge;
	private String emailConjuge;
	
	
	private String bancoConjuge;
	private String agenciaConjuge;
	private String contaConjuge;
	private String nomeCCConjuge;
	private String cpfCCConjuge;
	private String cnpjCCConjuge;

	
	private Date dataEmissaoRGConjuge;
	
	private String rg;
	private String cpf;
	private String cnpj;
	private Date dataEmissaoRG;
	private String cep;
	
	private String bancoCompleto;
	private List<SelectItem> listaBancos;
	private String banco;
	private String agencia;
	private String conta;
	private String nomeCC;
	private String cpfCC;
	private String cnpjCC;	
	
	private String idIugu;
	
	private boolean userInvestidor;
	private String loginInvestidor;
	private String senhaInvestidor;
	
	private boolean casado;
	private boolean coobrigado;
	
	private String reputacao;
	
	private String iuguAccountId;
	private String iuguNameAccount;
	private String iuguLiveApiToken;
	private String iuguTestApiToken;
	private String iuguUserToken;
	private String site;
	
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
	private Date dataEmissaoRGCoobrigado;
	
	private String bancoCoobrigado;
	private String agenciaCoobrigado;
	private String contaCoobrigado;
	private String nomeCCCoobrigado;
	private String cpfCCCoobrigado;
	private String cnpjCCCoobrigado;	

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
	
	private String bancoCoobrigadoCasado;
	private String agenciaCoobrigadoCasado;
	private String contaCoobrigadoCasado;
	private String nomeCCCoobrigadoCasado;
	private String cpfCCCoobrigadoCasado;
	private String cnpjCCCoobrigadoCasado;	

	private String nomeSecundario;
	private String cpfSecundario;
	private String cnpjSecundario;
	private String telCelularSecundario;
	private String emailSecundario;
	private String relacaoComTomador;
	private Boolean tipoPessoaSecundarioIsFisica;
	
	private String nomeParticipanteCheckList;
	private boolean rgDocumentosCheckList;
	private boolean comprovanteEnderecoDocumentosCheckList;
	private boolean certidaoCasamentoNascimentoDocumentosCheckList;
	private boolean fichaCadastralDocumentosCheckList;
	private boolean bancoDocumentosCheckList;
	private boolean telefoneEmailDocumentosCheckList;
	private boolean comprovanteRendaCheckList;
	private boolean combateFraudeCheckList;
	private boolean cargoOcupacaoCheckList;
	private boolean taxaCheckList;
	
	private String nomeParticipanteCheckListConjuge;
	private boolean rgDocumentosCheckListConjuge;
	private boolean comprovanteEnderecoDocumentosCheckListConjuge;
	private boolean certidaoCasamentoNascimentoDocumentosCheckListConjuge;
	private boolean fichaCadastralDocumentosCheckListConjuge;
	private boolean bancoDocumentosCheckListConjuge;
	private boolean telefoneEmailDocumentosCheckListConjuge;
	private boolean comprovanteRendaCheckListConjuge;
	private boolean combateFraudeCheckListConjuge;
	private boolean cargoOcupacaoCheckListConjuge;
	private boolean taxaCheckListConjuge;
	
	private String nomeParticipanteCheckListCoobrigado;
	private boolean rgDocumentosCheckListCoobrigado;
	private boolean comprovanteEnderecoDocumentosCheckListCoobrigado;
	private boolean certidaoCasamentoNascimentoDocumentosCheckListCoobrigado;
	private boolean fichaCadastralDocumentosCheckListCoobrigado;
	private boolean bancoDocumentosCheckListCoobrigado;
	private boolean telefoneEmailDocumentosCheckListCoobrigado;
	private boolean comprovanteRendaCheckListCoobrigado;
	private boolean combateFraudeCheckListCoobrigado;
	private boolean cargoOcupacaoCheckListCoobrigado;
	private boolean taxaCheckListCoobrigado;
	
	private String nomeParticipanteCheckListCoobrigadoCasado;
	private boolean rgDocumentosCheckListCoobrigadoCasado;
	private boolean comprovanteEnderecoDocumentosCheckListCoobrigadoCasado;
	private boolean certidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado;
	private boolean fichaCadastralDocumentosCheckListCoobrigadoCasado;
	private boolean bancoDocumentosCheckListCoobrigadoCasado;
	private boolean telefoneEmailDocumentosCheckListCoobrigadoCasado;
	private boolean comprovanteRendaCheckListCoobrigadoCasado;
	private boolean combateFraudeCheckListCoobrigadoCasado;
	private boolean cargoOcupacaoCheckListCoobrigadoCasado;
	private boolean taxaCheckListCoobrigadoCasado;
	/////////
	
	private User usuario;
	
	private BigDecimal saldoInvestidor;
	
	private String whatsAppNumero;

	public PagadorRecebedor(){
		resetarBololean();
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
		
		resetarBololean();
		
	}
	
	public void resetarBololean() {
		this.rgDocumentosCheckList = false;
		this.comprovanteEnderecoDocumentosCheckList = false;
		this.certidaoCasamentoNascimentoDocumentosCheckList = false;
		this.fichaCadastralDocumentosCheckList = false;
		this.bancoDocumentosCheckList = false;
		this.telefoneEmailDocumentosCheckList = false;
		this.comprovanteRendaCheckList = false;
		this.combateFraudeCheckList = false;
		this.cargoOcupacaoCheckList = false;
		this.taxaCheckList = false;
		
		this.rgDocumentosCheckListConjuge = false;
		this.comprovanteEnderecoDocumentosCheckListConjuge = false;
		this.certidaoCasamentoNascimentoDocumentosCheckListConjuge = false;
		this.fichaCadastralDocumentosCheckListConjuge = false;
		this.bancoDocumentosCheckListConjuge = false;
		this.telefoneEmailDocumentosCheckListConjuge = false;
		this.comprovanteRendaCheckListConjuge = false;
		this.combateFraudeCheckListConjuge = false;
		this.cargoOcupacaoCheckListConjuge = false;
		this.taxaCheckListConjuge = false;
		
		this.rgDocumentosCheckListCoobrigado = false;
		this.comprovanteEnderecoDocumentosCheckListCoobrigado = false;
		this.certidaoCasamentoNascimentoDocumentosCheckListCoobrigado = false;
		this.fichaCadastralDocumentosCheckListCoobrigado = false;
		this.bancoDocumentosCheckListCoobrigado = false;
		this.telefoneEmailDocumentosCheckListCoobrigado = false;
		this.comprovanteRendaCheckListCoobrigado = false;
		this.combateFraudeCheckListCoobrigado = false;
		this.cargoOcupacaoCheckListCoobrigado = false;
		this.taxaCheckListCoobrigado = false;
	
		this.rgDocumentosCheckListCoobrigadoCasado = false;
		this.comprovanteEnderecoDocumentosCheckListCoobrigadoCasado = false;
		this.certidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado = false;
		this.fichaCadastralDocumentosCheckListCoobrigadoCasado = false;
		this.bancoDocumentosCheckListCoobrigadoCasado = false;
		this.telefoneEmailDocumentosCheckListCoobrigadoCasado = false;
		this.comprovanteRendaCheckListCoobrigadoCasado = false;
		this.combateFraudeCheckListCoobrigadoCasado = false;
		this.cargoOcupacaoCheckListCoobrigadoCasado = false;
		this.taxaCheckListCoobrigadoCasado = false;
		
		this.casado = false;
		this.coobrigado = false;
		this.userInvestidor = false;
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
	
	public void pesquisaBancosListaNome() {
		System.out.println("PagadorRecebedor metodo - pesquisaBancosListaNome");
		this.listaBancos = new ArrayList<>();
		for(BancosEnum banco : BancosEnum.values()) {
			SelectItem item = new SelectItem(banco.getNomeCompleto());
			this.listaBancos.add(item);
		}
	}
	
	public void calcularIdade() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date dateHoje = dataHoje.getTime();
		
		long idadeLong = dateHoje.getTime() - this.getDtNascimento().getTime();
		idadeLong = TimeUnit.DAYS.convert(idadeLong, TimeUnit.MILLISECONDS);
		idadeLong = idadeLong / 30;
		idadeLong = idadeLong / 12;
	    this.setIdade(CommonsUtil.stringValue(idadeLong));
	}
	
	public void calcularIdadeConjuge() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date dateHoje = dataHoje.getTime();
		
		long idadeLong = dateHoje.getTime() - this.getDtNascimentoConjuge().getTime();
		idadeLong = TimeUnit.DAYS.convert(idadeLong, TimeUnit.MILLISECONDS);
		idadeLong = idadeLong / 30;
		idadeLong = idadeLong / 12;
	    this.setIdadeConjuge(CommonsUtil.stringValue(idadeLong));
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

	public String getRelacaoComTomador() {
		return relacaoComTomador;
	}

	public void setRelacaoComTomador(String relacaoComTomador) {
		this.relacaoComTomador = relacaoComTomador;
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

	public String getBancoCompleto() {
		return bancoCompleto;
	}

	public void setBancoCompleto(String bancoCompleto) {
		this.bancoCompleto = bancoCompleto;
	}

	public List<SelectItem> getListaBancos() {
		return listaBancos;
	}

	public void setListaBancos(List<SelectItem> listaBancos) {
		this.listaBancos = listaBancos;
	}

	public String getNomeParticipanteCheckList() {
		return nomeParticipanteCheckList;
	}

	public void setNomeParticipanteCheckList(String nomeParticipanteCheckList) {
		this.nomeParticipanteCheckList = nomeParticipanteCheckList;
	}

	public Boolean getRgDocumentosCheckList() {
		return rgDocumentosCheckList;
	}

	public void setRgDocumentosCheckList(Boolean rgDocumentosCheckList) {
		this.rgDocumentosCheckList = rgDocumentosCheckList;
	}

	public Boolean getComprovanteEnderecoDocumentosCheckList() {
		return comprovanteEnderecoDocumentosCheckList;
	}

	public void setComprovanteEnderecoDocumentosCheckList(Boolean comprovanteEnderecoDocumentosCheckList) {
		this.comprovanteEnderecoDocumentosCheckList = comprovanteEnderecoDocumentosCheckList;
	}

	public Boolean getCertidaoCasamentoNascimentoDocumentosCheckList() {
		return certidaoCasamentoNascimentoDocumentosCheckList;
	}

	public void setCertidaoCasamentoNascimentoDocumentosCheckList(Boolean certidaoCasamentoNascimentoDocumentosCheckList) {
		this.certidaoCasamentoNascimentoDocumentosCheckList = certidaoCasamentoNascimentoDocumentosCheckList;
	}

	public Boolean getFichaCadastralDocumentosCheckList() {
		return fichaCadastralDocumentosCheckList;
	}

	public void setFichaCadastralDocumentosCheckList(Boolean fichaCadastralDocumentosCheckList) {
		this.fichaCadastralDocumentosCheckList = fichaCadastralDocumentosCheckList;
	}

	public Boolean getBancoDocumentosCheckList() {
		return bancoDocumentosCheckList;
	}

	public void setBancoDocumentosCheckList(Boolean bancoDocumentosCheckList) {
		this.bancoDocumentosCheckList = bancoDocumentosCheckList;
	}

	public Boolean getTelefoneEmailDocumentosCheckList() {
		return telefoneEmailDocumentosCheckList;
	}

	public void setTelefoneEmailDocumentosCheckList(Boolean telefoneEmailDocumentosCheckList) {
		this.telefoneEmailDocumentosCheckList = telefoneEmailDocumentosCheckList;
	}

	public Boolean getComprovanteRendaCheckList() {
		return comprovanteRendaCheckList;
	}

	public void setComprovanteRendaCheckList(Boolean comprovanteRendaCheckList) {
		this.comprovanteRendaCheckList = comprovanteRendaCheckList;
	}

	public void setRgDocumentosCheckList(boolean rgDocumentosCheckList) {
		this.rgDocumentosCheckList = rgDocumentosCheckList;
	}

	public void setComprovanteEnderecoDocumentosCheckList(boolean comprovanteEnderecoDocumentosCheckList) {
		this.comprovanteEnderecoDocumentosCheckList = comprovanteEnderecoDocumentosCheckList;
	}

	public void setCertidaoCasamentoNascimentoDocumentosCheckList(boolean certidaoCasamentoNascimentoDocumentosCheckList) {
		this.certidaoCasamentoNascimentoDocumentosCheckList = certidaoCasamentoNascimentoDocumentosCheckList;
	}

	public void setFichaCadastralDocumentosCheckList(boolean fichaCadastralDocumentosCheckList) {
		this.fichaCadastralDocumentosCheckList = fichaCadastralDocumentosCheckList;
	}

	public void setBancoDocumentosCheckList(boolean bancoDocumentosCheckList) {
		this.bancoDocumentosCheckList = bancoDocumentosCheckList;
	}

	public void setTelefoneEmailDocumentosCheckList(boolean telefoneEmailDocumentosCheckList) {
		this.telefoneEmailDocumentosCheckList = telefoneEmailDocumentosCheckList;
	}

	public void setComprovanteRendaCheckList(boolean comprovanteRendaCheckList) {
		this.comprovanteRendaCheckList = comprovanteRendaCheckList;
	}

	public boolean isCombateFraudeCheckList() {
		return combateFraudeCheckList;
	}

	public void setCombateFraudeCheckList(boolean combateFraudeCheckList) {
		this.combateFraudeCheckList = combateFraudeCheckList;
	}

	public boolean isCargoOcupacaoCheckList() {
		return cargoOcupacaoCheckList;
	}

	public void setCargoOcupacaoCheckList(boolean cargoOcupacaoCheckList) {
		this.cargoOcupacaoCheckList = cargoOcupacaoCheckList;
	}

	public boolean isTaxaCheckList() {
		return taxaCheckList;
	}

	public void setTaxaCheckList(boolean taxaCheckList) {
		this.taxaCheckList = taxaCheckList;
	}

	public String getNomeParticipanteCheckListConjuge() {
		return nomeParticipanteCheckListConjuge;
	}

	public void setNomeParticipanteCheckListConjuge(String nomeParticipanteCheckListConjuge) {
		this.nomeParticipanteCheckListConjuge = nomeParticipanteCheckListConjuge;
	}

	public boolean isRgDocumentosCheckListConjuge() {
		return rgDocumentosCheckListConjuge;
	}

	public void setRgDocumentosCheckListConjuge(boolean rgDocumentosCheckListConjuge) {
		this.rgDocumentosCheckListConjuge = rgDocumentosCheckListConjuge;
	}

	public boolean isComprovanteEnderecoDocumentosCheckListConjuge() {
		return comprovanteEnderecoDocumentosCheckListConjuge;
	}

	public void setComprovanteEnderecoDocumentosCheckListConjuge(boolean comprovanteEnderecoDocumentosCheckListConjuge) {
		this.comprovanteEnderecoDocumentosCheckListConjuge = comprovanteEnderecoDocumentosCheckListConjuge;
	}

	public boolean isCertidaoCasamentoNascimentoDocumentosCheckListConjuge() {
		return certidaoCasamentoNascimentoDocumentosCheckListConjuge;
	}

	public void setCertidaoCasamentoNascimentoDocumentosCheckListConjuge(
			boolean certidaoCasamentoNascimentoDocumentosCheckListConjuge) {
		this.certidaoCasamentoNascimentoDocumentosCheckListConjuge = certidaoCasamentoNascimentoDocumentosCheckListConjuge;
	}

	public boolean isFichaCadastralDocumentosCheckListConjuge() {
		return fichaCadastralDocumentosCheckListConjuge;
	}

	public void setFichaCadastralDocumentosCheckListConjuge(boolean fichaCadastralDocumentosCheckListConjuge) {
		this.fichaCadastralDocumentosCheckListConjuge = fichaCadastralDocumentosCheckListConjuge;
	}

	public boolean isBancoDocumentosCheckListConjuge() {
		return bancoDocumentosCheckListConjuge;
	}

	public void setBancoDocumentosCheckListConjuge(boolean bancoDocumentosCheckListConjuge) {
		this.bancoDocumentosCheckListConjuge = bancoDocumentosCheckListConjuge;
	}

	public boolean isTelefoneEmailDocumentosCheckListConjuge() {
		return telefoneEmailDocumentosCheckListConjuge;
	}

	public void setTelefoneEmailDocumentosCheckListConjuge(boolean telefoneEmailDocumentosCheckListConjuge) {
		this.telefoneEmailDocumentosCheckListConjuge = telefoneEmailDocumentosCheckListConjuge;
	}

	public boolean isComprovanteRendaCheckListConjuge() {
		return comprovanteRendaCheckListConjuge;
	}

	public void setComprovanteRendaCheckListConjuge(boolean comprovanteRendaCheckListConjuge) {
		this.comprovanteRendaCheckListConjuge = comprovanteRendaCheckListConjuge;
	}

	public boolean isCombateFraudeCheckListConjuge() {
		return combateFraudeCheckListConjuge;
	}

	public void setCombateFraudeCheckListConjuge(boolean combateFraudeCheckListConjuge) {
		this.combateFraudeCheckListConjuge = combateFraudeCheckListConjuge;
	}

	public boolean isCargoOcupacaoCheckListConjuge() {
		return cargoOcupacaoCheckListConjuge;
	}

	public void setCargoOcupacaoCheckListConjuge(boolean cargoOcupacaoCheckListConjuge) {
		this.cargoOcupacaoCheckListConjuge = cargoOcupacaoCheckListConjuge;
	}

	public boolean isTaxaCheckListConjuge() {
		return taxaCheckListConjuge;
	}

	public void setTaxaCheckListConjuge(boolean taxaCheckListConjuge) {
		this.taxaCheckListConjuge = taxaCheckListConjuge;
	}

	public String getNomeParticipanteCheckListCoobrigado() {
		return nomeParticipanteCheckListCoobrigado;
	}

	public void setNomeParticipanteCheckListCoobrigado(String nomeParticipanteCheckListCoobrigado) {
		this.nomeParticipanteCheckListCoobrigado = nomeParticipanteCheckListCoobrigado;
	}

	public boolean isRgDocumentosCheckListCoobrigado() {
		return rgDocumentosCheckListCoobrigado;
	}

	public void setRgDocumentosCheckListCoobrigado(boolean rgDocumentosCheckListCoobrigado) {
		this.rgDocumentosCheckListCoobrigado = rgDocumentosCheckListCoobrigado;
	}

	public boolean isComprovanteEnderecoDocumentosCheckListCoobrigado() {
		return comprovanteEnderecoDocumentosCheckListCoobrigado;
	}

	public void setComprovanteEnderecoDocumentosCheckListCoobrigado(
			boolean comprovanteEnderecoDocumentosCheckListCoobrigado) {
		this.comprovanteEnderecoDocumentosCheckListCoobrigado = comprovanteEnderecoDocumentosCheckListCoobrigado;
	}

	public boolean isCertidaoCasamentoNascimentoDocumentosCheckListCoobrigado() {
		return certidaoCasamentoNascimentoDocumentosCheckListCoobrigado;
	}

	public void setCertidaoCasamentoNascimentoDocumentosCheckListCoobrigado(
			boolean certidaoCasamentoNascimentoDocumentosCheckListCoobrigado) {
		this.certidaoCasamentoNascimentoDocumentosCheckListCoobrigado = certidaoCasamentoNascimentoDocumentosCheckListCoobrigado;
	}

	public boolean isFichaCadastralDocumentosCheckListCoobrigado() {
		return fichaCadastralDocumentosCheckListCoobrigado;
	}

	public void setFichaCadastralDocumentosCheckListCoobrigado(boolean fichaCadastralDocumentosCheckListCoobrigado) {
		this.fichaCadastralDocumentosCheckListCoobrigado = fichaCadastralDocumentosCheckListCoobrigado;
	}

	public boolean isBancoDocumentosCheckListCoobrigado() {
		return bancoDocumentosCheckListCoobrigado;
	}

	public void setBancoDocumentosCheckListCoobrigado(boolean bancoDocumentosCheckListCoobrigado) {
		this.bancoDocumentosCheckListCoobrigado = bancoDocumentosCheckListCoobrigado;
	}

	public boolean isTelefoneEmailDocumentosCheckListCoobrigado() {
		return telefoneEmailDocumentosCheckListCoobrigado;
	}

	public void setTelefoneEmailDocumentosCheckListCoobrigado(boolean telefoneEmailDocumentosCheckListCoobrigado) {
		this.telefoneEmailDocumentosCheckListCoobrigado = telefoneEmailDocumentosCheckListCoobrigado;
	}

	public boolean isComprovanteRendaCheckListCoobrigado() {
		return comprovanteRendaCheckListCoobrigado;
	}

	public void setComprovanteRendaCheckListCoobrigado(boolean comprovanteRendaCheckListCoobrigado) {
		this.comprovanteRendaCheckListCoobrigado = comprovanteRendaCheckListCoobrigado;
	}

	public boolean isCombateFraudeCheckListCoobrigado() {
		return combateFraudeCheckListCoobrigado;
	}

	public void setCombateFraudeCheckListCoobrigado(boolean combateFraudeCheckListCoobrigado) {
		this.combateFraudeCheckListCoobrigado = combateFraudeCheckListCoobrigado;
	}

	public boolean isCargoOcupacaoCheckListCoobrigado() {
		return cargoOcupacaoCheckListCoobrigado;
	}

	public void setCargoOcupacaoCheckListCoobrigado(boolean cargoOcupacaoCheckListCoobrigado) {
		this.cargoOcupacaoCheckListCoobrigado = cargoOcupacaoCheckListCoobrigado;
	}

	public boolean isTaxaCheckListCoobrigado() {
		return taxaCheckListCoobrigado;
	}

	public void setTaxaCheckListCoobrigado(boolean taxaCheckListCoobrigado) {
		this.taxaCheckListCoobrigado = taxaCheckListCoobrigado;
	}

	public String getNomeParticipanteCheckListCoobrigadoCasado() {
		return nomeParticipanteCheckListCoobrigadoCasado;
	}

	public void setNomeParticipanteCheckListCoobrigadoCasado(String nomeParticipanteCheckListCoobrigadoCasado) {
		this.nomeParticipanteCheckListCoobrigadoCasado = nomeParticipanteCheckListCoobrigadoCasado;
	}

	public boolean isRgDocumentosCheckListCoobrigadoCasado() {
		return rgDocumentosCheckListCoobrigadoCasado;
	}

	public void setRgDocumentosCheckListCoobrigadoCasado(boolean rgDocumentosCheckListCoobrigadoCasado) {
		this.rgDocumentosCheckListCoobrigadoCasado = rgDocumentosCheckListCoobrigadoCasado;
	}

	public boolean isComprovanteEnderecoDocumentosCheckListCoobrigadoCasado() {
		return comprovanteEnderecoDocumentosCheckListCoobrigadoCasado;
	}

	public void setComprovanteEnderecoDocumentosCheckListCoobrigadoCasado(
			boolean comprovanteEnderecoDocumentosCheckListCoobrigadoCasado) {
		this.comprovanteEnderecoDocumentosCheckListCoobrigadoCasado = comprovanteEnderecoDocumentosCheckListCoobrigadoCasado;
	}

	public boolean isCertidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado() {
		return certidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado;
	}

	public void setCertidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado(
			boolean certidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado) {
		this.certidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado = certidaoCasamentoNascimentoDocumentosCheckListCoobrigadoCasado;
	}

	public boolean isFichaCadastralDocumentosCheckListCoobrigadoCasado() {
		return fichaCadastralDocumentosCheckListCoobrigadoCasado;
	}

	public void setFichaCadastralDocumentosCheckListCoobrigadoCasado(
			boolean fichaCadastralDocumentosCheckListCoobrigadoCasado) {
		this.fichaCadastralDocumentosCheckListCoobrigadoCasado = fichaCadastralDocumentosCheckListCoobrigadoCasado;
	}

	public boolean isBancoDocumentosCheckListCoobrigadoCasado() {
		return bancoDocumentosCheckListCoobrigadoCasado;
	}

	public void setBancoDocumentosCheckListCoobrigadoCasado(boolean bancoDocumentosCheckListCoobrigadoCasado) {
		this.bancoDocumentosCheckListCoobrigadoCasado = bancoDocumentosCheckListCoobrigadoCasado;
	}

	public boolean isTelefoneEmailDocumentosCheckListCoobrigadoCasado() {
		return telefoneEmailDocumentosCheckListCoobrigadoCasado;
	}

	public void setTelefoneEmailDocumentosCheckListCoobrigadoCasado(
			boolean telefoneEmailDocumentosCheckListCoobrigadoCasado) {
		this.telefoneEmailDocumentosCheckListCoobrigadoCasado = telefoneEmailDocumentosCheckListCoobrigadoCasado;
	}

	public boolean isComprovanteRendaCheckListCoobrigadoCasado() {
		return comprovanteRendaCheckListCoobrigadoCasado;
	}

	public void setComprovanteRendaCheckListCoobrigadoCasado(boolean comprovanteRendaCheckListCoobrigadoCasado) {
		this.comprovanteRendaCheckListCoobrigadoCasado = comprovanteRendaCheckListCoobrigadoCasado;
	}

	public boolean isCombateFraudeCheckListCoobrigadoCasado() {
		return combateFraudeCheckListCoobrigadoCasado;
	}

	public void setCombateFraudeCheckListCoobrigadoCasado(boolean combateFraudeCheckListCoobrigadoCasado) {
		this.combateFraudeCheckListCoobrigadoCasado = combateFraudeCheckListCoobrigadoCasado;
	}

	public boolean isCargoOcupacaoCheckListCoobrigadoCasado() {
		return cargoOcupacaoCheckListCoobrigadoCasado;
	}

	public void setCargoOcupacaoCheckListCoobrigadoCasado(boolean cargoOcupacaoCheckListCoobrigadoCasado) {
		this.cargoOcupacaoCheckListCoobrigadoCasado = cargoOcupacaoCheckListCoobrigadoCasado;
	}

	public boolean isTaxaCheckListCoobrigadoCasado() {
		return taxaCheckListCoobrigadoCasado;
	}

	public void setTaxaCheckListCoobrigadoCasado(boolean taxaCheckListCoobrigadoCasado) {
		this.taxaCheckListCoobrigadoCasado = taxaCheckListCoobrigadoCasado;
	}

	public String getBancoConjuge() {
		return bancoConjuge;
	}

	public void setBancoConjuge(String bancoConjuge) {
		this.bancoConjuge = bancoConjuge;
	}

	public String getAgenciaConjuge() {
		return agenciaConjuge;
	}

	public void setAgenciaConjuge(String agenciaConjuge) {
		this.agenciaConjuge = agenciaConjuge;
	}

	public String getContaConjuge() {
		return contaConjuge;
	}

	public void setContaConjuge(String contaConjuge) {
		this.contaConjuge = contaConjuge;
	}

	public String getNomeCCConjuge() {
		return nomeCCConjuge;
	}

	public void setNomeCCConjuge(String nomeCCConjuge) {
		this.nomeCCConjuge = nomeCCConjuge;
	}

	public String getCpfCCConjuge() {
		return cpfCCConjuge;
	}

	public void setCpfCCConjuge(String cpfCCConjuge) {
		this.cpfCCConjuge = cpfCCConjuge;
	}

	public String getCnpjCCConjuge() {
		return cnpjCCConjuge;
	}

	public void setCnpjCCConjuge(String cnpjCCConjuge) {
		this.cnpjCCConjuge = cnpjCCConjuge;
	}

	public String getBancoCoobrigado() {
		return bancoCoobrigado;
	}

	public void setBancoCoobrigado(String bancoCoobrigado) {
		this.bancoCoobrigado = bancoCoobrigado;
	}

	public String getAgenciaCoobrigado() {
		return agenciaCoobrigado;
	}

	public void setAgenciaCoobrigado(String agenciaCoobrigado) {
		this.agenciaCoobrigado = agenciaCoobrigado;
	}

	public String getContaCoobrigado() {
		return contaCoobrigado;
	}

	public void setContaCoobrigado(String contaCoobrigado) {
		this.contaCoobrigado = contaCoobrigado;
	}

	public String getNomeCCCoobrigado() {
		return nomeCCCoobrigado;
	}

	public void setNomeCCCoobrigado(String nomeCCCoobrigado) {
		this.nomeCCCoobrigado = nomeCCCoobrigado;
	}

	public String getCpfCCCoobrigado() {
		return cpfCCCoobrigado;
	}

	public void setCpfCCCoobrigado(String cpfCCCoobrigado) {
		this.cpfCCCoobrigado = cpfCCCoobrigado;
	}

	public String getCnpjCCCoobrigado() {
		return cnpjCCCoobrigado;
	}

	public void setCnpjCCCoobrigado(String cnpjCCCoobrigado) {
		this.cnpjCCCoobrigado = cnpjCCCoobrigado;
	}

	public String getBancoCoobrigadoCasado() {
		return bancoCoobrigadoCasado;
	}

	public void setBancoCoobrigadoCasado(String bancoCoobrigadoCasado) {
		this.bancoCoobrigadoCasado = bancoCoobrigadoCasado;
	}

	public String getAgenciaCoobrigadoCasado() {
		return agenciaCoobrigadoCasado;
	}

	public void setAgenciaCoobrigadoCasado(String agenciaCoobrigadoCasado) {
		this.agenciaCoobrigadoCasado = agenciaCoobrigadoCasado;
	}

	public String getContaCoobrigadoCasado() {
		return contaCoobrigadoCasado;
	}

	public void setContaCoobrigadoCasado(String contaCoobrigadoCasado) {
		this.contaCoobrigadoCasado = contaCoobrigadoCasado;
	}

	public String getNomeCCCoobrigadoCasado() {
		return nomeCCCoobrigadoCasado;
	}

	public void setNomeCCCoobrigadoCasado(String nomeCCCoobrigadoCasado) {
		this.nomeCCCoobrigadoCasado = nomeCCCoobrigadoCasado;
	}

	public String getCpfCCCoobrigadoCasado() {
		return cpfCCCoobrigadoCasado;
	}

	public void setCpfCCCoobrigadoCasado(String cpfCCCoobrigadoCasado) {
		this.cpfCCCoobrigadoCasado = cpfCCCoobrigadoCasado;
	}

	public String getCnpjCCCoobrigadoCasado() {
		return cnpjCCCoobrigadoCasado;
	}

	public void setCnpjCCCoobrigadoCasado(String cnpjCCCoobrigadoCasado) {
		this.cnpjCCCoobrigadoCasado = cnpjCCCoobrigadoCasado;
	}

	public String getNomePaiConjuge() {
		return nomePaiConjuge;
	}

	public void setNomePaiConjuge(String nomePaiConjuge) {
		this.nomePaiConjuge = nomePaiConjuge;
	}

	public String getNomeMaeConjuge() {
		return nomeMaeConjuge;
	}

	public void setNomeMaeConjuge(String nomeMaeConjuge) {
		this.nomeMaeConjuge = nomeMaeConjuge;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getIdadeConjuge() {
		return idadeConjuge;
	}

	public void setIdadeConjuge(String idadeConjuge) {
		this.idadeConjuge = idadeConjuge;
	}

	public String getCodigoMoneyPlus() {
		return codigoMoneyPlus;
	}

	public void setCodigoMoneyPlus(String codigoMoneyPlus) {
		this.codigoMoneyPlus = codigoMoneyPlus;
	}

	public String getWhatsAppNumero() {
		return whatsAppNumero;
	}

	public void setWhatsAppNumero(String whatsAppNumero) {
		this.whatsAppNumero = whatsAppNumero;
	}

	public String getReputacao() {
		return reputacao;
	}

	public void setReputacao(String reputacao) {
		this.reputacao = reputacao;
	}
	
}