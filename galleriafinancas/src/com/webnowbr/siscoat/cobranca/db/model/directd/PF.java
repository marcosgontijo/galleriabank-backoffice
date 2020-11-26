package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PF implements Serializable {

	private static final long serialVersionUID = 1L;
	/*
	 * 
		{
		    "ConsultaUid": "33codse4xs73o5kndshb4fnn",
		    "IdTipo": 1,
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 346,
		    "CustoTotalEmCreditos": 0,
		    "SaldoEmCreditos": 10,
		    "ApiVersion": "v1",
		    "Retorno": {
		        "CPF": 64237377595,
		        "Nome": "FRANCISCO LEANDRO ROCHA",
		        "Sexo": "M",
		        "DataNascimento": "1986-03-23T00:00:00.0000000",
		        "NomeMae": "ELISABETE ROCHA",
		        "Idade": 31,
		        "Signo": "Áries",
		        "Telefones": [
		            {
		                "TelefoneComDDD": "1612345678",
		                "TelemarketingBloqueado": null,
		                "TelemarketingUltBloqDesb": null,
		                "Operadora": "Vivo - Fixo",
		                "UltimaAtualizacao": null
		            },
		            {
		                "TelefoneComDDD": "1123456789",
		                "TelemarketingBloqueado": null,
		                "TelemarketingUltBloqDesb": null,
		                "Operadora": "Embratel  - Fixo",
		                "UltimaAtualizacao": null
		            },
		            {"etc": ... }
		        ],
		        "Enderecos": [
		            {
		                "Logradouro": "R ÓBIDOS",
		                "Numero": "674",
		                "Complemento": "",
		                "Bairro": "PARQUE INDUSTRIAL",
		                "Cidade": "SÃO JOSÉ DOS CAMPOS",
		                "UF": "SP",
		                "CEP": "14091283",
		                "UltimaAtualizacao": null
		            },
		            {
		                "Logradouro": "R ÓBIDOS",
		                "Numero": "674",
		                "Complemento": "",
		                "Bairro": "PARQUE INDUSTRIAL",
		                "Cidade": "SÃO JOSÉ DOS CAMPOS",
		                "UF": "SP",
		                "CEP": "14091283",
		                "UltimaAtualizacao": null
		            },
		            {"etc": ... }
		        ],
		        "Emails": [
		            {
		                "EnderecoEmail": "francisco.rocha@gmail.com",
		                "UltimaAtualizacao": null
		            },
		            {
		                "EnderecoEmail": "francisco.rocha@gmail.com",
		                "UltimaAtualizacao": null
		            },
		            {"etc": ... }
		        ],
		        "UltimaAtualizacaoPF": "2018-02-12T00:00:00.0000000",
		        "SituacaoReceitaBancoDados": "REGULAR",
		        "RendaEstimada": null,
		        "Obito": "NÃO",
		        "Sociedades": [
		            {
		                "Documento": 91981505000115,
		                "Nome": "ENTREGAS EXPRESSAS ME",
		                "PercentualParticipacao": null,
		                "DataEntrada": "2005-11-21T00:00:00.0000000"
		            }
		        ],
		        "Relacionados": [
		            {
		             "CPF": 12345678912,
		            "Nome": "MARIA ROCHA DA SILVA",
		            "Relacionamento": "Mãe"
		            }
		        ],
		        "Interpol": null,
		        "FAC": null,
		        "PEP": null,
		        "PEPParente": null
		    }
		}
	 */

	private String cpf;
	private String nome;
	private String sexo;
	private Date dataNascimento;
	private String nomeMae;
	private String idade;
	private String signo;
	private Date ultimaAtualizacaoPF;
	private String situacaoReceitaBancoDados;
	private String rendaEstimada;
	private String obito;
	private String interpol;
	private String fac;
	private String pep;
	private String pepParente;
	
	private Date dataConsulta;

	private InfoServico infoServico;
	private List<Endereco> enderecos;
	private List<Telefone> telefones;
	private List<Email> emails;
	private List<Sociedade> sociedades;
	private List<Relacionado> relacionados;

	public PF() {
		this.infoServico = new InfoServico();
		this.enderecos = new ArrayList<Endereco>();
		this.emails = new ArrayList<Email>();
		this.sociedades = new ArrayList<Sociedade>();
		this.relacionados = new ArrayList<Relacionado>();
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getSigno() {
		return signo;
	}

	public void setSigno(String signo) {
		this.signo = signo;
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}

	public List<Endereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<Endereco> enderecos) {
		this.enderecos = enderecos;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	public List<Sociedade> getSociedades() {
		return sociedades;
	}

	public void setSociedades(List<Sociedade> sociedades) {
		this.sociedades = sociedades;
	}

	public List<Relacionado> getRelacionados() {
		return relacionados;
	}

	public void setRelacionados(List<Relacionado> relacionados) {
		this.relacionados = relacionados;
	}

	public Date getUltimaAtualizacaoPF() {
		return ultimaAtualizacaoPF;
	}

	public void setUltimaAtualizacaoPF(Date ultimaAtualizacaoPF) {
		this.ultimaAtualizacaoPF = ultimaAtualizacaoPF;
	}

	public String getSituacaoReceitaBancoDados() {
		return situacaoReceitaBancoDados;
	}

	public void setSituacaoReceitaBancoDados(String situacaoReceitaBancoDados) {
		this.situacaoReceitaBancoDados = situacaoReceitaBancoDados;
	}

	public String getRendaEstimada() {
		return rendaEstimada;
	}

	public void setRendaEstimada(String rendaEstimada) {
		this.rendaEstimada = rendaEstimada;
	}

	public String getObito() {
		return obito;
	}

	public void setObito(String obito) {
		this.obito = obito;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getInterpol() {
		return interpol;
	}

	public void setInterpol(String interpol) {
		this.interpol = interpol;
	}

	public String getFac() {
		return fac;
	}

	public void setFac(String fac) {
		this.fac = fac;
	}

	public String getPep() {
		return pep;
	}

	public void setPep(String pep) {
		this.pep = pep;
	}

	public String getPepParente() {
		return pepParente;
	}

	public void setPepParente(String pepParente) {
		this.pepParente = pepParente;
	}
}