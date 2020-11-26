package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PJ implements Serializable {

	private static final long serialVersionUID = 1L;
	/*
	 * 
		{
		    "ConsultaUid": "27ek04l2s50zzu47l3fzvu2f7",
		    "IdTipo": 1,
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 710,
		    "CustoTotalEmCreditos": 0,
		    "SaldoEmCreditos": 2031,
		    "ApiVersion": "v1",
		    "Retorno": {
		        "CNPJ": 91981505000115,
		        "RazaoSocial": "ENTREGAS EXPRESSAS ME",
		        "NomeFantasia": "ENTREGAS EXPRESSAS",
		        "DataFundacao": "1985-11-21T00:00:00.0000000",
		        "CNAECodigo": 4930202,
		        "CNAEDescricao": "Transporte rodoviário de carga, exceto produtos     perigosos e mudanças, intermunicipal, interestadual e internacional",
		        "CNAECodigoSecundario": null,
		        "CNAEDescricaoSecundario": null,
		        "QtdFuncionarios": 424,
		        "SituacaoCadastral": "ATIVA",
		        "NatJurCodigo": null,
		        "NatJurDescricao": "SOCIEDADE EMPRESÁRIA LIMITADA",
		        "NatJurTipo": null,
		        "Porte": "GRANDE EMPRESA",
		        "FaixaFuncionarios": "de 100 a 499 Funcionários",
		        "FaixaFaturamento": null,
		        "Matriz": "Sim",
		        "OrgaoPublico": null,
		        "Ramo": null,
		        "TipoEmpresa": "MATRIZ",
		        "Telefones": [
		            {
		                "TelefoneComDDD": "6712341234",
		                "TelemarketingBloqueado": null,
		                "TelemarketingUltBloqDesb": null,
		                "Operadora": "OI - Fixo",
		                "UltimaAtualizacao": null
		            },
		            {
		                "TelefoneComDDD": "6743214321",
		                "TelemarketingBloqueado": null,
		                "TelemarketingUltBloqDesb": null,
		                "Operadora": "OI - Fixo",
		                "UltimaAtualizacao": null
		            },
		            {"etc": ... }
		        ],
		        "Enderecos": [
		            {
		                "Logradouro": "R ÓBIDOS",
		                "Numero": "674",
		                "Complemento": null,
		                "Bairro": "PARQUE INDUSTRIAL",
		                "Cidade": "SÃO JOSÉ DOS CAMPOS",
		                "UF": "SP",
		                "CEP": "79005350",
		                "UltimaAtualizacao": null
		            },
		            {
		                "Logradouro": "R ÓBIDOS",
		                "Numero": "674",
		                "Complemento": null,
		                "Bairro": "PARQUE INDUSTRIAL",
		                "Cidade": "SÃO JOSÉ DOS CAMPOS",
		                "UF": "SP",
		                "CEP": "79005350",
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
		        "Socios": [
		            {
		                "Documento": 64237377595,
		                "Nome": "FRANCISCO LEANDRO ROCHA",
		                "PercentualParticipacao": "60.00%",
		                "DataEntrada": "1985-11-21T00:00:00.0000000"
		            },
		            {
		                "Documento": 12345678912,
		                "Nome": "JOSÉ DA SILVA",
		                "PercentualParticipacao": "40.00%",
		                "DataEntrada": "1985-11-21T00:00:00.0000000"
		            },
		            {"etc": ... }
		        ],
		        "ParticipacoesEmpresas": [
		            {
		                "DataEntrada": "22/07/2010",
		                "DataSaida": null,
		                "Documento": "91981505000115",
		                "Nome": "ENTREGAS EXPRESSAS ME",
		                "Participacao": null,
		                "Posicao": "1",
		                "QualificacaoSocio": "SÓCIO",
		                "Relacao": null
		            }
		        ],
		        "UltimaAtualizacao": null,
		        "DataConsulta": "2018-10-03T14:35:33.7515238-03:00"
		    }
		}
	 */

	private String cnpj;
	private String razaoSocial;
	private String nomeFantasia;
	private Date dataFundacao;
	private String cnaeCodigo;
	private String cnaeDescricao;
	private String cnaeCodigoSecundario;
	private String cnaeDescricaoSecundario;
	private String qtdFuncionarios;
	private String situacaoCadastral;
	private String natJurCodigo;
	private String natJurDescricao;
	private String natJurTipo;
	private String porte;
	private String faixaFuncionarios;
	private String faixaFaturamento;
	private String matriz;
	private String orgaoPublico;
	private String ramo;
	private String tipoEmpresa;
	
	private Date ultimaAtualizacao;
	private Date dataConsulta;

	private InfoServico infoServico;
	private List<Endereco> enderecos;
	private List<Telefone> telefones;
	private List<Email> emails;
	private List<Sociedade> socios;
	private List<ParticipacoesEmpresa> participacoesEmpresas;

	public PJ() {
		this.infoServico = new InfoServico();
		this.enderecos = new ArrayList<Endereco>();
		this.emails = new ArrayList<Email>();
		this.socios = new ArrayList<Sociedade>();
		this.participacoesEmpresas = new ArrayList<ParticipacoesEmpresa>();
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
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

	public List<Sociedade> getSocios() {
		return socios;
	}

	public void setSocios(List<Sociedade> socios) {
		this.socios = socios;
	}

	public List<ParticipacoesEmpresa> getParticipacoesEmpresas() {
		return participacoesEmpresas;
	}

	public void setParticipacoesEmpresas(List<ParticipacoesEmpresa> participacoesEmpresas) {
		this.participacoesEmpresas = participacoesEmpresas;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public Date getDataFundacao() {
		return dataFundacao;
	}

	public void setDataFundacao(Date dataFundacao) {
		this.dataFundacao = dataFundacao;
	}

	public String getCnaeCodigo() {
		return cnaeCodigo;
	}

	public void setCnaeCodigo(String cnaeCodigo) {
		this.cnaeCodigo = cnaeCodigo;
	}

	public String getCnaeDescricao() {
		return cnaeDescricao;
	}

	public void setCnaeDescricao(String cnaeDescricao) {
		this.cnaeDescricao = cnaeDescricao;
	}

	public String getCnaeCodigoSecundario() {
		return cnaeCodigoSecundario;
	}

	public void setCnaeCodigoSecundario(String cnaeCodigoSecundario) {
		this.cnaeCodigoSecundario = cnaeCodigoSecundario;
	}

	public String getCnaeDescricaoSecundario() {
		return cnaeDescricaoSecundario;
	}

	public void setCnaeDescricaoSecundario(String cnaeDescricaoSecundario) {
		this.cnaeDescricaoSecundario = cnaeDescricaoSecundario;
	}

	public String getQtdFuncionarios() {
		return qtdFuncionarios;
	}

	public void setQtdFuncionarios(String qtdFuncionarios) {
		this.qtdFuncionarios = qtdFuncionarios;
	}

	public String getSituacaoCadastral() {
		return situacaoCadastral;
	}

	public void setSituacaoCadastral(String situacaoCadastral) {
		this.situacaoCadastral = situacaoCadastral;
	}

	public String getNatJurCodigo() {
		return natJurCodigo;
	}

	public void setNatJurCodigo(String natJurCodigo) {
		this.natJurCodigo = natJurCodigo;
	}

	public String getNatJurDescricao() {
		return natJurDescricao;
	}

	public void setNatJurDescricao(String natJurDescricao) {
		this.natJurDescricao = natJurDescricao;
	}

	public String getNatJurTipo() {
		return natJurTipo;
	}

	public void setNatJurTipo(String natJurTipo) {
		this.natJurTipo = natJurTipo;
	}

	public String getPorte() {
		return porte;
	}

	public void setPorte(String porte) {
		this.porte = porte;
	}

	public String getFaixaFuncionarios() {
		return faixaFuncionarios;
	}

	public void setFaixaFuncionarios(String faixaFuncionarios) {
		this.faixaFuncionarios = faixaFuncionarios;
	}

	public String getFaixaFaturamento() {
		return faixaFaturamento;
	}

	public void setFaixaFaturamento(String faixaFaturamento) {
		this.faixaFaturamento = faixaFaturamento;
	}

	public String getMatriz() {
		return matriz;
	}

	public void setMatriz(String matriz) {
		this.matriz = matriz;
	}

	public String getOrgaoPublico() {
		return orgaoPublico;
	}

	public void setOrgaoPublico(String orgaoPublico) {
		this.orgaoPublico = orgaoPublico;
	}

	public String getRamo() {
		return ramo;
	}

	public void setRamo(String ramo) {
		this.ramo = ramo;
	}

	public String getTipoEmpresa() {
		return tipoEmpresa;
	}

	public void setTipoEmpresa(String tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
	}
}