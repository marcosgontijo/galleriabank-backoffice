package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PJQSA implements Serializable {
	
	/*
	 * {
		  "ConsultaUid": "dfknx40lt1rgh0no45iiyoyt6",
		  "IdTipo": 1,
		  "Tipo": "Sucesso",
		  "Mensagem": "Sucesso. ",
		  "TempoExecucaoMs": 92887,
		  "CustoTotalEmCreditos": 0,
		  "SaldoEmCreditos": 17,
		  "ApiVersion": "v1",
		  "Retorno": {
		    "NumeroInscricao": "34.425.347/0001-06",
		    "Matriz": true,
		    "DataAbertura": "2019-08-05T00:00:00",
		    "NomeEmpresarial": "GALLERIA FINANCAS SECURITIZADORA S.A.",
		    "NomeFantasia": "********",
		    "Porte": "DEMAIS",
		    "AtividadeEconomicaPrincipal": "64.92-1-00 - Securitização de créditos",
		    "AtividadesEconomicasSecundarias": [
		      "Não informada"
		    ],
		    "NaturezaJuridica": "205-4 - Sociedade Anônima Fechada",
		    "Logradouro": "AV DOUTOR JOSE BONIFACIO COUTINHO NOGUEIRA",
		    "Numero": "150",
		    "Complemento": "TERREO",
		    "CEP": "13.091-611",
		    "BairroDistrito": "JARDIM MADALENA",
		    "Municipio": "CAMPINAS",
		    "UF": "SP",
		    "SituacaoCadastral": "ATIVA",
		    "DataSituacaoCadastral": "2019-08-05T00:00:00",
		    "MotivoSituacaoCadastral": "",
		    "SituacaoEspecial": "********",
		    "DataSituacaoEspecial": null,
		    "DataEmissao": "2020-08-24T16:47:37",
		    "DataConsulta": "2020-08-24T19:47:40.9282471+00:00",
		    "EnderecoEletronico": "",
		    "Telefone": "(11) 3353-5636",
		    "EFR": "*****",
		    "CapitalSocialQSA": "R$50.000,00 (Cinquenta mil reais)",
		    "EmRecuperJudicial": false,
		    "Socios": [
		      {
		        "NomeNomeEmpresarial": "FABRICIO FIGUEIREDO ",
		        "Qualificacao": "16-Presidente",
		        "QualifRepLegal": null,
		        "NomeRepresLegal": null,
		        "IsRepresentanteLegal": null,
		        "Doc_Socio": null
		      },
		      {
		        "NomeNomeEmpresarial": "JOAO AUGUSTO MAGATTI ALVES ",
		        "Qualificacao": "10-Diretor",
		        "QualifRepLegal": null,
		        "NomeRepresLegal": null,
		        "IsRepresentanteLegal": null,
		        "Doc_Socio": null
		      }
		    ],
		    "IsContingencia": false
		  }
		}
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private InfoServico infoServico;
	private String numeroInscricao;
	private boolean matriz;
	private Date dataAbertura;
	private String nomeEmpresarial;
	private String nomeFantasia;
	private String porte;
	private String atividadeEconomicaPrincipal;
	private List<String> atividadesEconomicasSecundarias;
	private String naturezaJuridica;
	private String logradouro;
	private String numero;
	private String complemento;
	private String cEP;
	private String bairroDistrito;
	private String municipio;
	private String situacaoCadastral;
	private Date dataSituacaoCadastral;
	private String motivoSituacaoCadastral;
	private String situacaoEspecial;
	private Date dataSituacaoEspecial;
	private Date dataEmissao;
	private Date dataConsulta;
	private String enderecoEletronico;
	private String telefone;
	private String eFR;
	private String capitalSocialQSA;
	private boolean emRecuperJudicial;
	private boolean contingencia;
	
	private List<Socio> socios;

	public PJQSA() {
		this.infoServico = new InfoServico();
		this.atividadesEconomicasSecundarias = new ArrayList<String>();
		this.socios = new ArrayList<Socio>();
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}

	public String getNumeroInscricao() {
		return numeroInscricao;
	}

	public void setNumeroInscricao(String numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	public boolean isMatriz() {
		return matriz;
	}

	public void setMatriz(boolean matriz) {
		this.matriz = matriz;
	}

	public Date getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Date dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public String getNomeEmpresarial() {
		return nomeEmpresarial;
	}

	public void setNomeEmpresarial(String nomeEmpresarial) {
		this.nomeEmpresarial = nomeEmpresarial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getPorte() {
		return porte;
	}

	public void setPorte(String porte) {
		this.porte = porte;
	}

	public String getAtividadeEconomicaPrincipal() {
		return atividadeEconomicaPrincipal;
	}

	public void setAtividadeEconomicaPrincipal(String atividadeEconomicaPrincipal) {
		this.atividadeEconomicaPrincipal = atividadeEconomicaPrincipal;
	}

	public List<String> getAtividadesEconomicasSecundarias() {
		return atividadesEconomicasSecundarias;
	}

	public void setAtividadesEconomicasSecundarias(List<String> atividadesEconomicasSecundarias) {
		this.atividadesEconomicasSecundarias = atividadesEconomicasSecundarias;
	}

	public String getNaturezaJuridica() {
		return naturezaJuridica;
	}

	public void setNaturezaJuridica(String naturezaJuridica) {
		this.naturezaJuridica = naturezaJuridica;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getcEP() {
		return cEP;
	}

	public void setcEP(String cEP) {
		this.cEP = cEP;
	}

	public String getBairroDistrito() {
		return bairroDistrito;
	}

	public void setBairroDistrito(String bairroDistrito) {
		this.bairroDistrito = bairroDistrito;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getSituacaoCadastral() {
		return situacaoCadastral;
	}

	public void setSituacaoCadastral(String situacaoCadastral) {
		this.situacaoCadastral = situacaoCadastral;
	}

	public Date getDataSituacaoCadastral() {
		return dataSituacaoCadastral;
	}

	public void setDataSituacaoCadastral(Date dataSituacaoCadastral) {
		this.dataSituacaoCadastral = dataSituacaoCadastral;
	}

	public String getMotivoSituacaoCadastral() {
		return motivoSituacaoCadastral;
	}

	public void setMotivoSituacaoCadastral(String motivoSituacaoCadastral) {
		this.motivoSituacaoCadastral = motivoSituacaoCadastral;
	}

	public String getSituacaoEspecial() {
		return situacaoEspecial;
	}

	public void setSituacaoEspecial(String situacaoEspecial) {
		this.situacaoEspecial = situacaoEspecial;
	}

	public Date getDataSituacaoEspecial() {
		return dataSituacaoEspecial;
	}

	public void setDataSituacaoEspecial(Date dataSituacaoEspecial) {
		this.dataSituacaoEspecial = dataSituacaoEspecial;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getEnderecoEletronico() {
		return enderecoEletronico;
	}

	public void setEnderecoEletronico(String enderecoEletronico) {
		this.enderecoEletronico = enderecoEletronico;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String geteFR() {
		return eFR;
	}

	public void seteFR(String eFR) {
		this.eFR = eFR;
	}

	public String getCapitalSocialQSA() {
		return capitalSocialQSA;
	}

	public void setCapitalSocialQSA(String capitalSocialQSA) {
		this.capitalSocialQSA = capitalSocialQSA;
	}

	public boolean isEmRecuperJudicial() {
		return emRecuperJudicial;
	}

	public void setEmRecuperJudicial(boolean emRecuperJudicial) {
		this.emRecuperJudicial = emRecuperJudicial;
	}

	public List<Socio> getSocios() {
		return socios;
	}

	public void setSocios(List<Socio> socios) {
		this.socios = socios;
	}

	public boolean isContingencia() {
		return contingencia;
	}

	public void setContingencia(boolean contingencia) {
		this.contingencia = contingencia;
	}
}