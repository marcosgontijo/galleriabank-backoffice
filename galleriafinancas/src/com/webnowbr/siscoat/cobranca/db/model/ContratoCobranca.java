package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ContratoCobranca implements Serializable {

	/**
	 * 
	 */
	
	private String tipoCalculoInvestidor1;
	private BigDecimal vlrInvestidor1;
	private Integer qtdeParcelasInvestidor1;
	private Integer carenciaInvestidor1;
	private Date dataInicioInvestidor1;
	
	private String tipoCalculoInvestidor2;
	private BigDecimal vlrInvestidor2;
	private Integer qtdeParcelasInvestidor2;
	private Integer carenciaInvestidor2;
	private Date dataInicioInvestidor2;

	
	private String tipoCalculoInvestidor3;
	private BigDecimal vlrInvestidor3;
	private Integer qtdeParcelasInvestidor3;
	private Integer carenciaInvestidor3;
	private Date dataInicioInvestidor3;

	
	private String tipoCalculoInvestidor4;
	private BigDecimal vlrInvestidor4;
	private Integer qtdeParcelasInvestidor4;
	private Integer carenciaInvestidor4;
	private Date dataInicioInvestidor4;

	
	private String tipoCalculoInvestidor5;
	private BigDecimal vlrInvestidor5;
	private Integer qtdeParcelasInvestidor5;
	private Integer carenciaInvestidor5;
	private Date dataInicioInvestidor5;

	
	private String tipoCalculoInvestidor6;
	private BigDecimal vlrInvestidor6;
	private Integer qtdeParcelasInvestidor6;
	private Integer carenciaInvestidor6;
	private Date dataInicioInvestidor6;

	
	private String tipoCalculoInvestidor7;
	private BigDecimal vlrInvestidor7;
	private Integer qtdeParcelasInvestidor7;
	private Integer carenciaInvestidor7;
	private Date dataInicioInvestidor7;

	
	private String tipoCalculoInvestidor8;
	private BigDecimal vlrInvestidor8;
	private Integer qtdeParcelasInvestidor8;
	private Integer carenciaInvestidor8;
	private Date dataInicioInvestidor8;


	private String tipoCalculoInvestidor9;
	private BigDecimal vlrInvestidor9;
	private Integer qtdeParcelasInvestidor9;
	private Integer carenciaInvestidor9;
	private Date dataInicioInvestidor9;


	private String tipoCalculoInvestidor10;
	private BigDecimal vlrInvestidor10;
	private Integer qtdeParcelasInvestidor10;
	private Integer carenciaInvestidor10;
	private Date dataInicioInvestidor10;

	
	private static final long serialVersionUID = 1L;
	private long id;
	private Date dataInicio;
	private Date dataContrato;
	private int diaMes;
	private int qtdeParcelas;
	
	private int mesesCarencia;
		
	private BigDecimal txAdministracao;
	private BigDecimal txJuros;
	private BigDecimal vlrInvestimento;
	private BigDecimal txHonorario;
	private BigDecimal vlrRepasse;
	private BigDecimal vlrLucro;
	private BigDecimal vlrComissao;
	private String tipoCalculo;
	private String acao;
	private String observacao;
	private String observacao2;
	private String urlLead;
	private String statusLead;
	
	private String nomePagador;
	private String nomeCidadeImovel;
	private String nomeResponsavel;

	private BigDecimal txJurosParcelas;
	
	private BigDecimal txMulta;

	private PagadorRecebedor pagador;
	private String empresa;
	private PagadorRecebedor recebedor;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor1;
	private PagadorRecebedor recebedor2;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor2;
	private PagadorRecebedor recebedor3;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor3;
	private PagadorRecebedor recebedor4;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor4;
	private PagadorRecebedor recebedor5;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor5;
	private PagadorRecebedor recebedor6;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor6;
	private PagadorRecebedor recebedor7;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor7;
	private PagadorRecebedor recebedor8;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor8;
	private PagadorRecebedor recebedor9;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor9;
	private PagadorRecebedor recebedor10;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor10;
	
	private Set<Segurado> listSegurados;

	// n�o persistida a lista abaixo
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidorSelecionado;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidorSelecionadoEnvelope;

	private BigDecimal taxaRemuneracaoInvestidor1;
	private BigDecimal taxaRemuneracaoInvestidor2;
	private BigDecimal taxaRemuneracaoInvestidor3;
	private BigDecimal taxaRemuneracaoInvestidor4;
	private BigDecimal taxaRemuneracaoInvestidor5;
	private BigDecimal taxaRemuneracaoInvestidor6;
	private BigDecimal taxaRemuneracaoInvestidor7;
	private BigDecimal taxaRemuneracaoInvestidor8;
	private BigDecimal taxaRemuneracaoInvestidor9;
	private BigDecimal taxaRemuneracaoInvestidor10;

	private BigDecimal valorCCB;

	private BigDecimal vlrRecebedor;
	private BigDecimal vlrRecebedor2;
	private BigDecimal vlrRecebedor3;
	private BigDecimal vlrRecebedor4;
	private BigDecimal vlrRecebedor5;
	private BigDecimal vlrRecebedor6;
	private BigDecimal vlrRecebedor7;
	private BigDecimal vlrRecebedor8;
	private BigDecimal vlrRecebedor9;
	private BigDecimal vlrRecebedor10;

	private BigDecimal vlrFinalRecebedor1;
	private PagadorRecebedor recebedorParcelaFinal1;

	private BigDecimal vlrFinalRecebedor2;
	private PagadorRecebedor recebedorParcelaFinal2;

	private BigDecimal vlrFinalRecebedor3;
	private PagadorRecebedor recebedorParcelaFinal3;

	private BigDecimal vlrFinalRecebedor4;
	private PagadorRecebedor recebedorParcelaFinal4;

	private BigDecimal vlrFinalRecebedor5;
	private PagadorRecebedor recebedorParcelaFinal5;

	private BigDecimal vlrFinalRecebedor6;
	private PagadorRecebedor recebedorParcelaFinal6;

	private BigDecimal vlrFinalRecebedor7;
	private PagadorRecebedor recebedorParcelaFinal7;

	private BigDecimal vlrFinalRecebedor8;
	private PagadorRecebedor recebedorParcelaFinal8;

	private BigDecimal vlrFinalRecebedor9;
	private PagadorRecebedor recebedorParcelaFinal9;

	private BigDecimal vlrFinalRecebedor10;
	private PagadorRecebedor recebedorParcelaFinal10;

	private Date dataInclusaoRecebedor1;
	private Date dataInclusaoRecebedor2;
	private Date dataInclusaoRecebedor3;
	private Date dataInclusaoRecebedor4;
	private Date dataInclusaoRecebedor5;
	private Date dataInclusaoRecebedor6;
	private Date dataInclusaoRecebedor7;
	private Date dataInclusaoRecebedor8;
	private Date dataInclusaoRecebedor9;
	private Date dataInclusaoRecebedor10;
	
	private boolean corrigidoIPCA;
	
	private boolean exibeRecebedor1;
	private boolean exibeRecebedor2;
	private boolean exibeRecebedor3;
	private boolean exibeRecebedor4;
	private boolean exibeRecebedor5;
	private boolean exibeRecebedor6;
	private boolean exibeRecebedor7;
	private boolean exibeRecebedor8;
	private boolean exibeRecebedor9;
	private boolean exibeRecebedor10;

	private boolean ocultaRecebedor;
	private boolean ocultaRecebedor2;
	private boolean ocultaRecebedor3;
	private boolean ocultaRecebedor4;
	private boolean ocultaRecebedor5;
	private boolean ocultaRecebedor6;
	private boolean ocultaRecebedor7;
	private boolean ocultaRecebedor8;
	private boolean ocultaRecebedor9;
	private boolean ocultaRecebedor10;

	private boolean recebedorEnvelope;
	private boolean recebedorEnvelope2;
	private boolean recebedorEnvelope3;
	private boolean recebedorEnvelope4;
	private boolean recebedorEnvelope5;
	private boolean recebedorEnvelope6;
	private boolean recebedorEnvelope7;
	private boolean recebedorEnvelope8;
	private boolean recebedorEnvelope9;
	private boolean recebedorEnvelope10;

	private boolean recebedorGarantido1;
	private boolean recebedorGarantido2;
	private boolean recebedorGarantido3;
	private boolean recebedorGarantido4;
	private boolean recebedorGarantido5;
	private boolean recebedorGarantido6;
	private boolean recebedorGarantido7;
	private boolean recebedorGarantido8;
	private boolean recebedorGarantido9;
	private boolean recebedorGarantido10;

	private boolean parcelasAlteradas1;
	private boolean parcelasAlteradas2;
	private boolean parcelasAlteradas3;
	private boolean parcelasAlteradas4;
	private boolean parcelasAlteradas5;
	private boolean parcelasAlteradas6;
	private boolean parcelasAlteradas7;
	private boolean parcelasAlteradas8;
	private boolean parcelasAlteradas9;
	private boolean parcelasAlteradas10;

	private Responsavel responsavel;

	private ImovelCobranca imovel;

	private List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhes;
	private List<ContratoCobrancaObservacoes> listContratoCobrancaObservacoes;

	private Date dataPagamentoIni;
	private Date dataPagamentoFim;

	private BigDecimal vlrParcela;

	private BigDecimal vlrParcelaAtualizada;

	private boolean geraParcelaFinal;

	private String numeroContrato;

	private boolean contratoRestritoAdm;

	private String status;

	private String vlrParcelaStr;

	private BigDecimal vlrParcelaFinal;
	private BigDecimal quantoPrecisa;
	private String estadoCivil;
	private String temMaisImoveis;
	private String finalidade;
	private String iprf;
	private String profissao;

	/*** usados no sistema ***/
	
	private boolean leadCompleto;
	private Date leadCompletoData;
	private String leadCompletoUsuario;
	
	private Date inicioAnaliseData;
	private boolean inicioAnalise;
	private String inicioAnaliseUsuario;
	
	private Date analiseReprovadaData;
	private boolean analiseReprovada;
	private String analiseReprovadaUsuario;

	private Date cadastroAprovadoData;
	private boolean cadastroAprovado;
	private String cadastroAprovadoValor;
	private String cadastroAprovadoUsuario;

	private boolean matriculaAprovada;
	private Date matriculaAprovadaData;
	private String matriculaAprovadaValor;
	private String matriculaAprovadaUsuario;

	private Date laudoRecebidoData;
	private boolean laudoRecebido;
	private String laudoRecebidoUsuario;

	private Date pajurFavoravelData;
	private boolean pajurFavoravel;
	private String pajurFavoravelUsuario;

	private Date pagtoLaudoConfirmadaData;
	private boolean pagtoLaudoConfirmada;
	private String pagtoLaudoConfirmadaUsuario;

	private Date documentosCompletosData;
	private boolean documentosCompletos;
	private String documentosCompletosUsuario;

	private Date ccbProntaData;
	private boolean ccbPronta;
	private String ccbProntaUsuario;

	private Date agAssinaturaData;
	private boolean agAssinatura;
	private String agAssinaturaUsuario;
	
	private Date agRegistroData;
	private boolean agRegistro;
	private String agRegistroUsuario;

	private Date statusContratoData;
	private String statusContrato;
	private String statusContratoUsuario;

	/*** fim usados no sistema ***/

	private boolean aguardandoDocumento;
	private Date aguardandoDocumentoData;
	private String aguardandoDocumentoUsuario;

	private boolean matriculaReprovada;
	private Date matriculaReprovadaData;
	private String matriculaReprovadaUsuario;

	private boolean fotoImovelAprovada;
	private Date fotoImovelAprovadaData;
	private String fotoImovelAprovadaUsuario;

	private boolean fotoImovelReprovada;
	private Date fotoImovelReprovadaData;
	private String fotoImovelReprovadaUsuario;

	private Date aprovadoData;
	private boolean aprovado;
	private String aprovadoUsuario;

	private Date reprovadoData;
	private boolean reprovado;
	private String reprovadoUsuario;

	private Date semFotoImovelData;
	private boolean semFotoImovel;
	private String semFotoImovelUsuario;

	private Date documentosIncompletosData;
	private boolean documentosIncompletos;
	private String documentosIncompletosUsuario;

	private Date cadastroReprovadoData;
	private boolean cadastroReprovado;
	private String cadastroReprovadoUsuario;

	private Date aguardandoCertidoesData;
	private boolean aguardandoCertidoes;
	private String aguardandoCertidoesUsuario;

	private Date aguardandoCNDData;
	private boolean aguardandoCND;
	private String aguardandoCNDUsuario;

	private Date agendarVisitaEmpresaData;
	private boolean agendarVisitaEmpresa;
	private String agendarVisitaEmpresaUsuario;

	private Date visitaEmpresaAprovadaData;
	private boolean visitaEmpresaAprovada;
	private String visitaEmpresaAprovadaUsuario;

	private Date visitaEmpresaReprovadaData;
	private boolean visitaEmpresaReprovada;
	private String visitaEmpresaReprovadaUsuario;

	private Date agendarVisitaImovelData;
	private boolean agendarVisitaImovel;
	private String agendarVisitaImovelUsuario;

	private Date visitaImovelAprovadaData;
	private boolean visitaImovelAprovada;
	private String visitaImovelAprovadaUsuario;

	private Date visitaImovelReprovadaData;
	private boolean visitaImovelReprovada;
	private String visitaImovelReprovadaUsuario;

	private Date enviadoCobrancaLaudoData;
	private boolean enviadoCobrancaLaudo;
	private String enviadoCobrancaLaudoUsuario;

	private Date laudoSolicitadoData;
	private boolean laudoSolicitado;
	private String laudoSolicitadoUsuario;

	private Date pajurSolicitadoData;
	private boolean pajurSolicitado;
	private String pajurSolicitadoUsuario;

	private Date pajurDesfavoravelData;
	private boolean pajurDesfavoravel;
	private String pajurDesfavoravelUsuario;

	private Date reanalisarPajurData;
	private boolean reanalisarPajur;
	private String reanalisarPajurUsuario;

	private Date aguardandoInvestidorData;
	private boolean aguardandoInvestidor;
	private String aguardandoInvestidorUsuario;

	private Date agendadoCartorioData;
	private boolean agendadoCartorio;
	private String agendadoCartorioUsuario;

	private Date contratoAssinadoData;
	private boolean contratoAssinado;
	private String contratoAssinadoUsuario;
	
	private Date dataPrevistaVistoria;
	private String motivoReprovacaoAnalise;
	
	private String observacaolead;
	private String motivoReprovaLead;
	
	private boolean temSeguro;
	private boolean temSeguroDFI;
	private boolean temSeguroMIP;
	private BigDecimal valorImovel;
	private String numeroContratoSeguro;
	
	private Date vencimentoBoleto;
	private BigDecimal valorBoletoPreContrato;
	private BigDecimal taxaPreAprovada;
	private BigInteger prazoMaxPreAprovado;
	private BigDecimal valorMercadoImovel;
	private BigDecimal valorVendaForcadaImovel;
	private String comentarioJuridico;

	public ContratoCobranca() {
		super();
		this.pagador = new PagadorRecebedor();
		this.recebedor = new PagadorRecebedor();
		this.recebedor2 = null;
		this.recebedor3 = null;
		this.recebedor4 = null;
		this.recebedor5 = null;
		this.recebedor6 = null;
		this.recebedor7 = null;
		this.recebedor8 = null;
		this.recebedor9 = null;
		this.recebedor10 = null;
		this.responsavel = new Responsavel();
		this.imovel = new ImovelCobranca();
		this.listContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();
		this.listSegurados = new HashSet<>();
		
		this.exibeRecebedor1 = true;
		this.exibeRecebedor2 = true;
		this.exibeRecebedor3 = true;
		this.exibeRecebedor4 = true;
		this.exibeRecebedor5 = true;
		this.exibeRecebedor6 = true;
		this.exibeRecebedor7 = true;
		this.exibeRecebedor8 = true;
		this.exibeRecebedor9 = true;
		this.exibeRecebedor10 = true;
	}
	
	
	
	private void reordenaListagemDetalhes() {
		if (CommonsUtil.semValor(listContratoCobrancaDetalhes))
			return;
		
		Collections.sort(this.listContratoCobrancaDetalhes, new Comparator<ContratoCobrancaDetalhes>() {
			@Override
			public int compare(ContratoCobrancaDetalhes one, ContratoCobrancaDetalhes other) {
				int result = one.getDataVencimento().compareTo(other.getDataVencimento());
				if (result == 0) {
					try {
						Integer oneParcela = Integer.parseInt(one.getNumeroParcela());
						Integer otherParcela = Integer.parseInt(other.getNumeroParcela());
						result = oneParcela.compareTo(otherParcela);
					} catch (Exception e) {
						result = 0;
					}
				}
				return result;
			}
		});

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
	 * @return the dataInicio
	 */
	public Date getDataInicio() {
		return dataInicio;
	}

	/**
	 * @param dataInicio the dataInicio to set
	 */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * @return the dataContrato
	 */
	public Date getDataContrato() {
		return dataContrato;
	}

	/**
	 * @param dataContrato the dataContrato to set
	 */
	public void setDataContrato(Date dataContrato) {
		this.dataContrato = dataContrato;
	}

	/**
	 * @return the diaMes
	 */
	public int getDiaMes() {
		return diaMes;
	}

	/**
	 * @param diaMes the diaMes to set
	 */
	public void setDiaMes(int diaMes) {
		this.diaMes = diaMes;
	}

	/**
	 * @return the qtdeParcelas
	 */
	public int getQtdeParcelas() {
		return qtdeParcelas;
	}

	/**
	 * @param qtdeParcelas the qtdeParcelas to set
	 */
	public void setQtdeParcelas(int qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}

	/**
	 * @return the txAdministracao
	 */
	public BigDecimal getTxAdministracao() {
		return txAdministracao;
	}

	/**
	 * @param txAdministracao the txAdministracao to set
	 */
	public void setTxAdministracao(BigDecimal txAdministracao) {
		this.txAdministracao = txAdministracao;
	}

	/**
	 * @return the txJuros
	 */
	public BigDecimal getTxJuros() {
		return txJuros;
	}

	/**
	 * @param txJuros the txJuros to set
	 */
	public void setTxJuros(BigDecimal txJuros) {
		this.txJuros = txJuros;
	}

	/**
	 * @return the vlrInvestimento
	 */
	public BigDecimal getVlrInvestimento() {
		return vlrInvestimento;
	}

	/**
	 * @param vlrInvestimento the vlrInvestimento to set
	 */
	public void setVlrInvestimento(BigDecimal vlrInvestimento) {
		this.vlrInvestimento = vlrInvestimento;
	}

	/**
	 * @return the vlrRepasse
	 */
	public BigDecimal getVlrRepasse() {
		return vlrRepasse;
	}

	/**
	 * @param vlrRepasse the vlrRepasse to set
	 */
	public void setVlrRepasse(BigDecimal vlrRepasse) {
		this.vlrRepasse = vlrRepasse;
	}

	/**
	 * @return the vlrLucro
	 */
	public BigDecimal getVlrLucro() {
		return vlrLucro;
	}

	/**
	 * @param vlrLucro the vlrLucro to set
	 */
	public void setVlrLucro(BigDecimal vlrLucro) {
		this.vlrLucro = vlrLucro;
	}

	/**
	 * @return the vlrComissao
	 */
	public BigDecimal getVlrComissao() {
		return vlrComissao;
	}

	/**
	 * @param vlrComissao the vlrComissao to set
	 */
	public void setVlrComissao(BigDecimal vlrComissao) {
		this.vlrComissao = vlrComissao;
	}

	/**
	 * @return the acao
	 */
	public String getAcao() {
		return acao;
	}

	/**
	 * @param acao the acao to set
	 */
	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(String tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
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
	 * @return the pagador
	 */
	public PagadorRecebedor getPagador() {
		return pagador;
	}

	/**
	 * @param pagador the pagador to set
	 */
	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	/**
	 * @return the recebedor
	 */
	public PagadorRecebedor getRecebedor() {
		return recebedor;
	}

	/**
	 * @param recebedor the recebedor to set
	 */
	public void setRecebedor(PagadorRecebedor recebedor) {
		this.recebedor = recebedor;
	}

	/**
	 * @return the recebedor2
	 */
	public PagadorRecebedor getRecebedor2() {
		return recebedor2;
	}

	/**
	 * @param recebedor2 the recebedor2 to set
	 */
	public void setRecebedor2(PagadorRecebedor recebedor2) {
		this.recebedor2 = recebedor2;
	}

	/**
	 * @return the recebedor3
	 */
	public PagadorRecebedor getRecebedor3() {
		return recebedor3;
	}

	/**
	 * @param recebedor3 the recebedor3 to set
	 */
	public void setRecebedor3(PagadorRecebedor recebedor3) {
		this.recebedor3 = recebedor3;
	}

	/**
	 * @return the recebedor4
	 */
	public PagadorRecebedor getRecebedor4() {
		return recebedor4;
	}

	/**
	 * @param recebedor4 the recebedor4 to set
	 */
	public void setRecebedor4(PagadorRecebedor recebedor4) {
		this.recebedor4 = recebedor4;
	}

	/**
	 * @return the recebedor5
	 */
	public PagadorRecebedor getRecebedor5() {
		return recebedor5;
	}

	/**
	 * @param recebedor5 the recebedor5 to set
	 */
	public void setRecebedor5(PagadorRecebedor recebedor5) {
		this.recebedor5 = recebedor5;
	}

	/**
	 * @return the recebedor6
	 */
	public PagadorRecebedor getRecebedor6() {
		return recebedor6;
	}

	/**
	 * @param recebedor6 the recebedor6 to set
	 */
	public void setRecebedor6(PagadorRecebedor recebedor6) {
		this.recebedor6 = recebedor6;
	}

	/**
	 * @return the recebedor7
	 */
	public PagadorRecebedor getRecebedor7() {
		return recebedor7;
	}

	/**
	 * @param recebedor7 the recebedor7 to set
	 */
	public void setRecebedor7(PagadorRecebedor recebedor7) {
		this.recebedor7 = recebedor7;
	}

	/**
	 * @return the recebedor8
	 */
	public PagadorRecebedor getRecebedor8() {
		return recebedor8;
	}

	/**
	 * @param recebedor8 the recebedor8 to set
	 */
	public void setRecebedor8(PagadorRecebedor recebedor8) {
		this.recebedor8 = recebedor8;
	}

	/**
	 * @return the recebedor9
	 */
	public PagadorRecebedor getRecebedor9() {
		return recebedor9;
	}

	/**
	 * @param recebedor9 the recebedor9 to set
	 */
	public void setRecebedor9(PagadorRecebedor recebedor9) {
		this.recebedor9 = recebedor9;
	}

	/**
	 * @return the recebedor10
	 */
	public PagadorRecebedor getRecebedor10() {
		return recebedor10;
	}

	/**
	 * @param recebedor10 the recebedor10 to set
	 */
	public void setRecebedor10(PagadorRecebedor recebedor10) {
		this.recebedor10 = recebedor10;
	}

	/**
	 * @return the vlrRecebedor
	 */
	public BigDecimal getVlrRecebedor() {
		return vlrRecebedor;
	}

	/**
	 * @param vlrRecebedor the vlrRecebedor to set
	 */
	public void setVlrRecebedor(BigDecimal vlrRecebedor) {
		this.vlrRecebedor = vlrRecebedor;
	}

	/**
	 * @return the vlrRecebedor2
	 */
	public BigDecimal getVlrRecebedor2() {
		return vlrRecebedor2;
	}

	/**
	 * @param vlrRecebedor2 the vlrRecebedor2 to set
	 */
	public void setVlrRecebedor2(BigDecimal vlrRecebedor2) {
		this.vlrRecebedor2 = vlrRecebedor2;
	}

	/**
	 * @return the vlrRecebedor3
	 */
	public BigDecimal getVlrRecebedor3() {
		return vlrRecebedor3;
	}

	/**
	 * @param vlrRecebedor3 the vlrRecebedor3 to set
	 */
	public void setVlrRecebedor3(BigDecimal vlrRecebedor3) {
		this.vlrRecebedor3 = vlrRecebedor3;
	}

	/**
	 * @return the vlrRecebedor4
	 */
	public BigDecimal getVlrRecebedor4() {
		return vlrRecebedor4;
	}

	/**
	 * @param vlrRecebedor4 the vlrRecebedor4 to set
	 */
	public void setVlrRecebedor4(BigDecimal vlrRecebedor4) {
		this.vlrRecebedor4 = vlrRecebedor4;
	}

	/**
	 * @return the vlrRecebedor5
	 */
	public BigDecimal getVlrRecebedor5() {
		return vlrRecebedor5;
	}

	/**
	 * @param vlrRecebedor5 the vlrRecebedor5 to set
	 */
	public void setVlrRecebedor5(BigDecimal vlrRecebedor5) {
		this.vlrRecebedor5 = vlrRecebedor5;
	}

	/**
	 * @return the vlrRecebedor6
	 */
	public BigDecimal getVlrRecebedor6() {
		return vlrRecebedor6;
	}

	/**
	 * @param vlrRecebedor6 the vlrRecebedor6 to set
	 */
	public void setVlrRecebedor6(BigDecimal vlrRecebedor6) {
		this.vlrRecebedor6 = vlrRecebedor6;
	}

	/**
	 * @return the vlrRecebedor7
	 */
	public BigDecimal getVlrRecebedor7() {
		return vlrRecebedor7;
	}

	/**
	 * @param vlrRecebedor7 the vlrRecebedor7 to set
	 */
	public void setVlrRecebedor7(BigDecimal vlrRecebedor7) {
		this.vlrRecebedor7 = vlrRecebedor7;
	}

	/**
	 * @return the vlrRecebedor8
	 */
	public BigDecimal getVlrRecebedor8() {
		return vlrRecebedor8;
	}

	/**
	 * @param vlrRecebedor8 the vlrRecebedor8 to set
	 */
	public void setVlrRecebedor8(BigDecimal vlrRecebedor8) {
		this.vlrRecebedor8 = vlrRecebedor8;
	}

	/**
	 * @return the vlrRecebedor9
	 */
	public BigDecimal getVlrRecebedor9() {
		return vlrRecebedor9;
	}

	/**
	 * @param vlrRecebedor9 the vlrRecebedor9 to set
	 */
	public void setVlrRecebedor9(BigDecimal vlrRecebedor9) {
		this.vlrRecebedor9 = vlrRecebedor9;
	}

	/**
	 * @return the vlrRecebedor10
	 */
	public BigDecimal getVlrRecebedor10() {
		return vlrRecebedor10;
	}

	/**
	 * @param vlrRecebedor10 the vlrRecebedor10 to set
	 */
	public void setVlrRecebedor10(BigDecimal vlrRecebedor10) {
		this.vlrRecebedor10 = vlrRecebedor10;
	}

	/**
	 * @return the vlrFinalRecebedor1
	 */
	public BigDecimal getVlrFinalRecebedor1() {
		return vlrFinalRecebedor1;
	}

	/**
	 * @param vlrFinalRecebedor1 the vlrFinalRecebedor1 to set
	 */
	public void setVlrFinalRecebedor1(BigDecimal vlrFinalRecebedor1) {
		this.vlrFinalRecebedor1 = vlrFinalRecebedor1;
	}

	/**
	 * @return the recebedorParcelaFinal1
	 */
	public PagadorRecebedor getRecebedorParcelaFinal1() {
		return recebedorParcelaFinal1;
	}

	/**
	 * @param recebedorParcelaFinal1 the recebedorParcelaFinal1 to set
	 */
	public void setRecebedorParcelaFinal1(PagadorRecebedor recebedorParcelaFinal1) {
		this.recebedorParcelaFinal1 = recebedorParcelaFinal1;
	}

	/**
	 * @return the vlrFinalRecebedor2
	 */
	public BigDecimal getVlrFinalRecebedor2() {
		return vlrFinalRecebedor2;
	}

	/**
	 * @param vlrFinalRecebedor2 the vlrFinalRecebedor2 to set
	 */
	public void setVlrFinalRecebedor2(BigDecimal vlrFinalRecebedor2) {
		this.vlrFinalRecebedor2 = vlrFinalRecebedor2;
	}

	/**
	 * @return the recebedorParcelaFinal2
	 */
	public PagadorRecebedor getRecebedorParcelaFinal2() {
		return recebedorParcelaFinal2;
	}

	/**
	 * @param recebedorParcelaFinal2 the recebedorParcelaFinal2 to set
	 */
	public void setRecebedorParcelaFinal2(PagadorRecebedor recebedorParcelaFinal2) {
		this.recebedorParcelaFinal2 = recebedorParcelaFinal2;
	}

	/**
	 * @return the vlrFinalRecebedor3
	 */
	public BigDecimal getVlrFinalRecebedor3() {
		return vlrFinalRecebedor3;
	}

	/**
	 * @param vlrFinalRecebedor3 the vlrFinalRecebedor3 to set
	 */
	public void setVlrFinalRecebedor3(BigDecimal vlrFinalRecebedor3) {
		this.vlrFinalRecebedor3 = vlrFinalRecebedor3;
	}

	/**
	 * @return the recebedorParcelaFinal3
	 */
	public PagadorRecebedor getRecebedorParcelaFinal3() {
		return recebedorParcelaFinal3;
	}

	/**
	 * @param recebedorParcelaFinal3 the recebedorParcelaFinal3 to set
	 */
	public void setRecebedorParcelaFinal3(PagadorRecebedor recebedorParcelaFinal3) {
		this.recebedorParcelaFinal3 = recebedorParcelaFinal3;
	}

	/**
	 * @return the vlrFinalRecebedor4
	 */
	public BigDecimal getVlrFinalRecebedor4() {
		return vlrFinalRecebedor4;
	}

	/**
	 * @param vlrFinalRecebedor4 the vlrFinalRecebedor4 to set
	 */
	public void setVlrFinalRecebedor4(BigDecimal vlrFinalRecebedor4) {
		this.vlrFinalRecebedor4 = vlrFinalRecebedor4;
	}

	/**
	 * @return the recebedorParcelaFinal4
	 */
	public PagadorRecebedor getRecebedorParcelaFinal4() {
		return recebedorParcelaFinal4;
	}

	/**
	 * @param recebedorParcelaFinal4 the recebedorParcelaFinal4 to set
	 */
	public void setRecebedorParcelaFinal4(PagadorRecebedor recebedorParcelaFinal4) {
		this.recebedorParcelaFinal4 = recebedorParcelaFinal4;
	}

	/**
	 * @return the vlrFinalRecebedor5
	 */
	public BigDecimal getVlrFinalRecebedor5() {
		return vlrFinalRecebedor5;
	}

	/**
	 * @param vlrFinalRecebedor5 the vlrFinalRecebedor5 to set
	 */
	public void setVlrFinalRecebedor5(BigDecimal vlrFinalRecebedor5) {
		this.vlrFinalRecebedor5 = vlrFinalRecebedor5;
	}

	/**
	 * @return the recebedorParcelaFinal5
	 */
	public PagadorRecebedor getRecebedorParcelaFinal5() {
		return recebedorParcelaFinal5;
	}

	/**
	 * @param recebedorParcelaFinal5 the recebedorParcelaFinal5 to set
	 */
	public void setRecebedorParcelaFinal5(PagadorRecebedor recebedorParcelaFinal5) {
		this.recebedorParcelaFinal5 = recebedorParcelaFinal5;
	}

	/**
	 * @return the exibeRecebedor1
	 */
	public boolean isExibeRecebedor1() {
		return exibeRecebedor1;
	}

	/**
	 * @param exibeRecebedor1 the exibeRecebedor1 to set
	 */
	public void setExibeRecebedor1(boolean exibeRecebedor1) {
		this.exibeRecebedor1 = exibeRecebedor1;
	}

	/**
	 * @return the exibeRecebedor2
	 */
	public boolean isExibeRecebedor2() {
		return exibeRecebedor2;
	}

	/**
	 * @param exibeRecebedor2 the exibeRecebedor2 to set
	 */
	public void setExibeRecebedor2(boolean exibeRecebedor2) {
		this.exibeRecebedor2 = exibeRecebedor2;
	}

	/**
	 * @return the exibeRecebedor3
	 */
	public boolean isExibeRecebedor3() {
		return exibeRecebedor3;
	}

	/**
	 * @param exibeRecebedor3 the exibeRecebedor3 to set
	 */
	public void setExibeRecebedor3(boolean exibeRecebedor3) {
		this.exibeRecebedor3 = exibeRecebedor3;
	}

	/**
	 * @return the exibeRecebedor4
	 */
	public boolean isExibeRecebedor4() {
		return exibeRecebedor4;
	}

	/**
	 * @param exibeRecebedor4 the exibeRecebedor4 to set
	 */
	public void setExibeRecebedor4(boolean exibeRecebedor4) {
		this.exibeRecebedor4 = exibeRecebedor4;
	}

	/**
	 * @return the exibeRecebedor5
	 */
	public boolean isExibeRecebedor5() {
		return exibeRecebedor5;
	}

	/**
	 * @param exibeRecebedor5 the exibeRecebedor5 to set
	 */
	public void setExibeRecebedor5(boolean exibeRecebedor5) {
		this.exibeRecebedor5 = exibeRecebedor5;
	}

	/**
	 * @return the exibeRecebedor6
	 */
	public boolean isExibeRecebedor6() {
		return exibeRecebedor6;
	}

	/**
	 * @param exibeRecebedor6 the exibeRecebedor6 to set
	 */
	public void setExibeRecebedor6(boolean exibeRecebedor6) {
		this.exibeRecebedor6 = exibeRecebedor6;
	}

	/**
	 * @return the exibeRecebedor7
	 */
	public boolean isExibeRecebedor7() {
		return exibeRecebedor7;
	}

	/**
	 * @param exibeRecebedor7 the exibeRecebedor7 to set
	 */
	public void setExibeRecebedor7(boolean exibeRecebedor7) {
		this.exibeRecebedor7 = exibeRecebedor7;
	}

	/**
	 * @return the exibeRecebedor8
	 */
	public boolean isExibeRecebedor8() {
		return exibeRecebedor8;
	}

	/**
	 * @param exibeRecebedor8 the exibeRecebedor8 to set
	 */
	public void setExibeRecebedor8(boolean exibeRecebedor8) {
		this.exibeRecebedor8 = exibeRecebedor8;
	}

	/**
	 * @return the exibeRecebedor9
	 */
	public boolean isExibeRecebedor9() {
		return exibeRecebedor9;
	}

	/**
	 * @param exibeRecebedor9 the exibeRecebedor9 to set
	 */
	public void setExibeRecebedor9(boolean exibeRecebedor9) {
		this.exibeRecebedor9 = exibeRecebedor9;
	}

	/**
	 * @return the exibeRecebedor10
	 */
	public boolean isExibeRecebedor10() {
		return exibeRecebedor10;
	}

	/**
	 * @param exibeRecebedor10 the exibeRecebedor10 to set
	 */
	public void setExibeRecebedor10(boolean exibeRecebedor10) {
		this.exibeRecebedor10 = exibeRecebedor10;
	}

	/**
	 * @return the responsavel
	 */
	public Responsavel getResponsavel() {
		return responsavel;
	}

	/**
	 * @param responsavel the responsavel to set
	 */
	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	/**
	 * @return the imovel
	 */
	public ImovelCobranca getImovel() {
		return imovel;
	}

	/**
	 * @param imovel the imovel to set
	 */
	public void setImovel(ImovelCobranca imovel) {
		this.imovel = imovel;
	}

	/**
	 * @return the listContratoCobrancaDetalhes
	 */
	public List<ContratoCobrancaDetalhes> getListContratoCobrancaDetalhes() {
		reordenaListagemDetalhes();
		return listContratoCobrancaDetalhes;
	}

	/**
	 * @param listContratoCobrancaDetalhes the listContratoCobrancaDetalhes to set
	 */
	public void setListContratoCobrancaDetalhes(List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhes) {
		this.listContratoCobrancaDetalhes = listContratoCobrancaDetalhes;
	}

	/**
	 * @return the listContratoCobrancaObservacoes
	 */
	public List<ContratoCobrancaObservacoes> getListContratoCobrancaObservacoes() {
		return listContratoCobrancaObservacoes;
	}

	/**
	 * @param listContratoCobrancaObservacoes the listContratoCobrancaObservacoes to
	 *                                        set
	 */
	public void setListContratoCobrancaObservacoes(List<ContratoCobrancaObservacoes> listContratoCobrancaObservacoes) {
		this.listContratoCobrancaObservacoes = listContratoCobrancaObservacoes;
	}

	/**
	 * @return the dataPagamentoIni
	 */
	public Date getDataPagamentoIni() {
		return dataPagamentoIni;
	}

	/**
	 * @param dataPagamentoIni the dataPagamentoIni to set
	 */
	public void setDataPagamentoIni(Date dataPagamentoIni) {
		this.dataPagamentoIni = dataPagamentoIni;
	}

	/**
	 * @return the dataPagamentoFim
	 */
	public Date getDataPagamentoFim() {
		return dataPagamentoFim;
	}

	/**
	 * @param dataPagamentoFim the dataPagamentoFim to set
	 */
	public void setDataPagamentoFim(Date dataPagamentoFim) {
		this.dataPagamentoFim = dataPagamentoFim;
	}

	/**
	 * @return the vlrParcela
	 */
	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}

	/**
	 * @param vlrParcela the vlrParcela to set
	 */
	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}

	/**
	 * @return the vlrParcelaAtualizada
	 */
	public BigDecimal getVlrParcelaAtualizada() {
		return vlrParcelaAtualizada;
	}

	/**
	 * @param vlrParcelaAtualizada the vlrParcelaAtualizada to set
	 */
	public void setVlrParcelaAtualizada(BigDecimal vlrParcelaAtualizada) {
		this.vlrParcelaAtualizada = vlrParcelaAtualizada;
	}

	/**
	 * @return the geraParcelaFinal
	 */
	public boolean isGeraParcelaFinal() {
		return geraParcelaFinal;
	}

	/**
	 * @param geraParcelaFinal the geraParcelaFinal to set
	 */
	public void setGeraParcelaFinal(boolean geraParcelaFinal) {
		this.geraParcelaFinal = geraParcelaFinal;
	}

	/**
	 * @return the numeroContrato
	 */
	public String getNumeroContrato() {
		return numeroContrato;
	}

	/**
	 * @param numeroContrato the numeroContrato to set
	 */
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	/**
	 * @return the contratoRestritoAdm
	 */
	public boolean isContratoRestritoAdm() {
		return contratoRestritoAdm;
	}

	/**
	 * @param contratoRestritoAdm the contratoRestritoAdm to set
	 */
	public void setContratoRestritoAdm(boolean contratoRestritoAdm) {
		this.contratoRestritoAdm = contratoRestritoAdm;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the vlrParcelaStr
	 */
	public String getVlrParcelaStr() {
		return vlrParcelaStr;
	}

	/**
	 * @param vlrParcelaStr the vlrParcelaStr to set
	 */
	public void setVlrParcelaStr(String vlrParcelaStr) {
		this.vlrParcelaStr = vlrParcelaStr;
	}

	/**
	 * @return the vlrParcelaFinal
	 */
	public BigDecimal getVlrParcelaFinal() {
		return vlrParcelaFinal;
	}

	/**
	 * @param vlrParcelaFinal the vlrParcelaFinal to set
	 */
	public void setVlrParcelaFinal(BigDecimal vlrParcelaFinal) {
		this.vlrParcelaFinal = vlrParcelaFinal;
	}

	/**
	 * @return the quantoPrecisa
	 */
	public BigDecimal getQuantoPrecisa() {
		return quantoPrecisa;
	}

	/**
	 * @param quantoPrecisa the quantoPrecisa to set
	 */
	public void setQuantoPrecisa(BigDecimal quantoPrecisa) {
		this.quantoPrecisa = quantoPrecisa;
	}

	/**
	 * @return the estadoCivil
	 */
	public String getEstadoCivil() {
		return estadoCivil;
	}

	/**
	 * @param estadoCivil the estadoCivil to set
	 */
	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	/**
	 * @return the temMaisImoveis
	 */
	public String getTemMaisImoveis() {
		return temMaisImoveis;
	}

	/**
	 * @param temMaisImoveis the temMaisImoveis to set
	 */
	public void setTemMaisImoveis(String temMaisImoveis) {
		this.temMaisImoveis = temMaisImoveis;
	}

	/**
	 * @return the finalidade
	 */
	public String getFinalidade() {
		return finalidade;
	}

	/**
	 * @param finalidade the finalidade to set
	 */
	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	/**
	 * @return the iprf
	 */
	public String getIprf() {
		return iprf;
	}

	/**
	 * @param iprf the iprf to set
	 */
	public void setIprf(String iprf) {
		this.iprf = iprf;
	}

	/**
	 * @return the profissao
	 */
	public String getProfissao() {
		return profissao;
	}

	/**
	 * @param profissao the profissao to set
	 */
	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	public Date getAprovadoData() {
		return aprovadoData;
	}

	public void setAprovadoData(Date aprovadoData) {
		this.aprovadoData = aprovadoData;
	}

	public boolean isAprovado() {
		return aprovado;
	}

	public void setAprovado(boolean aprovado) {
		this.aprovado = aprovado;
	}

	public String getAprovadoUsuario() {
		return aprovadoUsuario;
	}

	public void setAprovadoUsuario(String aprovadoUsuario) {
		this.aprovadoUsuario = aprovadoUsuario;
	}

	public Date getReprovadoData() {
		return reprovadoData;
	}

	public void setReprovadoData(Date reprovadoData) {
		this.reprovadoData = reprovadoData;
	}

	public boolean isReprovado() {
		return reprovado;
	}

	public void setReprovado(boolean reprovado) {
		this.reprovado = reprovado;
	}

	public String getReprovadoUsuario() {
		return reprovadoUsuario;
	}

	public void setReprovadoUsuario(String reprovadoUsuario) {
		this.reprovadoUsuario = reprovadoUsuario;
	}

	public Date getInicioAnaliseData() {
		return inicioAnaliseData;
	}

	public void setInicioAnaliseData(Date inicioAnaliseData) {
		this.inicioAnaliseData = inicioAnaliseData;
	}

	public boolean isInicioAnalise() {
		return inicioAnalise;
	}

	public void setInicioAnalise(boolean inicioAnalise) {
		this.inicioAnalise = inicioAnalise;
	}

	public String getInicioAnaliseUsuario() {
		return inicioAnaliseUsuario;
	}

	public void setInicioAnaliseUsuario(String inicioAnaliseUsuario) {
		this.inicioAnaliseUsuario = inicioAnaliseUsuario;
	}

	public boolean isAguardandoDocumento() {
		return aguardandoDocumento;
	}

	public void setAguardandoDocumento(boolean aguardandoDocumento) {
		this.aguardandoDocumento = aguardandoDocumento;
	}

	public Date getAguardandoDocumentoData() {
		return aguardandoDocumentoData;
	}

	public void setAguardandoDocumentoData(Date aguardandoDocumentoData) {
		this.aguardandoDocumentoData = aguardandoDocumentoData;
	}

	public String getAguardandoDocumentoUsuario() {
		return aguardandoDocumentoUsuario;
	}

	public void setAguardandoDocumentoUsuario(String aguardandoDocumentoUsuario) {
		this.aguardandoDocumentoUsuario = aguardandoDocumentoUsuario;
	}

	public boolean isMatriculaAprovada() {
		return matriculaAprovada;
	}

	public void setMatriculaAprovada(boolean matriculaAprovada) {
		this.matriculaAprovada = matriculaAprovada;
	}

	public Date getMatriculaAprovadaData() {
		return matriculaAprovadaData;
	}

	public void setMatriculaAprovadaData(Date matriculaAprovadaData) {
		this.matriculaAprovadaData = matriculaAprovadaData;
	}

	public String getMatriculaAprovadaUsuario() {
		return matriculaAprovadaUsuario;
	}

	public void setMatriculaAprovadaUsuario(String matriculaAprovadaUsuario) {
		this.matriculaAprovadaUsuario = matriculaAprovadaUsuario;
	}

	public boolean isMatriculaReprovada() {
		return matriculaReprovada;
	}

	public void setMatriculaReprovada(boolean matriculaReprovada) {
		this.matriculaReprovada = matriculaReprovada;
	}

	public Date getMatriculaReprovadaData() {
		return matriculaReprovadaData;
	}

	public void setMatriculaReprovadaData(Date matriculaReprovadaData) {
		this.matriculaReprovadaData = matriculaReprovadaData;
	}

	public String getMatriculaReprovadaUsuario() {
		return matriculaReprovadaUsuario;
	}

	public void setMatriculaReprovadaUsuario(String matriculaReprovadaUsuario) {
		this.matriculaReprovadaUsuario = matriculaReprovadaUsuario;
	}

	public boolean isFotoImovelAprovada() {
		return fotoImovelAprovada;
	}

	public void setFotoImovelAprovada(boolean fotoImovelAprovada) {
		this.fotoImovelAprovada = fotoImovelAprovada;
	}

	public Date getFotoImovelAprovadaData() {
		return fotoImovelAprovadaData;
	}

	public void setFotoImovelAprovadaData(Date fotoImovelAprovadaData) {
		this.fotoImovelAprovadaData = fotoImovelAprovadaData;
	}

	public String getFotoImovelAprovadaUsuario() {
		return fotoImovelAprovadaUsuario;
	}

	public void setFotoImovelAprovadaUsuario(String fotoImovelAprovadaUsuario) {
		this.fotoImovelAprovadaUsuario = fotoImovelAprovadaUsuario;
	}

	public boolean isFotoImovelReprovada() {
		return fotoImovelReprovada;
	}

	public void setFotoImovelReprovada(boolean fotoImovelReprovada) {
		this.fotoImovelReprovada = fotoImovelReprovada;
	}

	public Date getFotoImovelReprovadaData() {
		return fotoImovelReprovadaData;
	}

	public void setFotoImovelReprovadaData(Date fotoImovelReprovadaData) {
		this.fotoImovelReprovadaData = fotoImovelReprovadaData;
	}

	public String getFotoImovelReprovadaUsuario() {
		return fotoImovelReprovadaUsuario;
	}

	public void setFotoImovelReprovadaUsuario(String fotoImovelReprovadaUsuario) {
		this.fotoImovelReprovadaUsuario = fotoImovelReprovadaUsuario;
	}

	public Date getSemFotoImovelData() {
		return semFotoImovelData;
	}

	public void setSemFotoImovelData(Date semFotoImovelData) {
		this.semFotoImovelData = semFotoImovelData;
	}

	public boolean isSemFotoImovel() {
		return semFotoImovel;
	}

	public void setSemFotoImovel(boolean semFotoImovel) {
		this.semFotoImovel = semFotoImovel;
	}

	public String getSemFotoImovelUsuario() {
		return semFotoImovelUsuario;
	}

	public void setSemFotoImovelUsuario(String semFotoImovelUsuario) {
		this.semFotoImovelUsuario = semFotoImovelUsuario;
	}

	public Date getDocumentosCompletosData() {
		return documentosCompletosData;
	}

	public void setDocumentosCompletosData(Date documentosCompletosData) {
		this.documentosCompletosData = documentosCompletosData;
	}

	public boolean isDocumentosCompletos() {
		return documentosCompletos;
	}

	public void setDocumentosCompletos(boolean documentosCompletos) {
		this.documentosCompletos = documentosCompletos;
	}

	public String getDocumentosCompletosUsuario() {
		return documentosCompletosUsuario;
	}

	public void setDocumentosCompletosUsuario(String documentosCompletosUsuario) {
		this.documentosCompletosUsuario = documentosCompletosUsuario;
	}

	public Date getDocumentosIncompletosData() {
		return documentosIncompletosData;
	}

	public void setDocumentosIncompletosData(Date documentosIncompletosData) {
		this.documentosIncompletosData = documentosIncompletosData;
	}

	public boolean isDocumentosIncompletos() {
		return documentosIncompletos;
	}

	public void setDocumentosIncompletos(boolean documentosIncompletos) {
		this.documentosIncompletos = documentosIncompletos;
	}

	public String getDocumentosIncompletosUsuario() {
		return documentosIncompletosUsuario;
	}

	public void setDocumentosIncompletosUsuario(String documentosIncompletosUsuario) {
		this.documentosIncompletosUsuario = documentosIncompletosUsuario;
	}

	public Date getCadastroAprovadoData() {
		return cadastroAprovadoData;
	}

	public void setCadastroAprovadoData(Date cadastroAprovadoData) {
		this.cadastroAprovadoData = cadastroAprovadoData;
	}

	public boolean isCadastroAprovado() {
		return cadastroAprovado;
	}

	public void setCadastroAprovado(boolean cadastroAprovado) {
		this.cadastroAprovado = cadastroAprovado;
	}

	public String getCadastroAprovadoUsuario() {
		return cadastroAprovadoUsuario;
	}

	public void setCadastroAprovadoUsuario(String cadastroAprovadoUsuario) {
		this.cadastroAprovadoUsuario = cadastroAprovadoUsuario;
	}

	public Date getCadastroReprovadoData() {
		return cadastroReprovadoData;
	}

	public void setCadastroReprovadoData(Date cadastroReprovadoData) {
		this.cadastroReprovadoData = cadastroReprovadoData;
	}

	public boolean isCadastroReprovado() {
		return cadastroReprovado;
	}

	public void setCadastroReprovado(boolean cadastroReprovado) {
		this.cadastroReprovado = cadastroReprovado;
	}

	public String getCadastroReprovadoUsuario() {
		return cadastroReprovadoUsuario;
	}

	public void setCadastroReprovadoUsuario(String cadastroReprovadoUsuario) {
		this.cadastroReprovadoUsuario = cadastroReprovadoUsuario;
	}

	public Date getAguardandoCertidoesData() {
		return aguardandoCertidoesData;
	}

	public void setAguardandoCertidoesData(Date aguardandoCertidoesData) {
		this.aguardandoCertidoesData = aguardandoCertidoesData;
	}

	public boolean isAguardandoCertidoes() {
		return aguardandoCertidoes;
	}

	public void setAguardandoCertidoes(boolean aguardandoCertidoes) {
		this.aguardandoCertidoes = aguardandoCertidoes;
	}

	public String getAguardandoCertidoesUsuario() {
		return aguardandoCertidoesUsuario;
	}

	public void setAguardandoCertidoesUsuario(String aguardandoCertidoesUsuario) {
		this.aguardandoCertidoesUsuario = aguardandoCertidoesUsuario;
	}

	public Date getAguardandoCNDData() {
		return aguardandoCNDData;
	}

	public void setAguardandoCNDData(Date aguardandoCNDData) {
		this.aguardandoCNDData = aguardandoCNDData;
	}

	public boolean isAguardandoCND() {
		return aguardandoCND;
	}

	public void setAguardandoCND(boolean aguardandoCND) {
		this.aguardandoCND = aguardandoCND;
	}

	public String getAguardandoCNDUsuario() {
		return aguardandoCNDUsuario;
	}

	public void setAguardandoCNDUsuario(String aguardandoCNDUsuario) {
		this.aguardandoCNDUsuario = aguardandoCNDUsuario;
	}

	public Date getAgendarVisitaEmpresaData() {
		return agendarVisitaEmpresaData;
	}

	public void setAgendarVisitaEmpresaData(Date agendarVisitaEmpresaData) {
		this.agendarVisitaEmpresaData = agendarVisitaEmpresaData;
	}

	public boolean isAgendarVisitaEmpresa() {
		return agendarVisitaEmpresa;
	}

	public void setAgendarVisitaEmpresa(boolean agendarVisitaEmpresa) {
		this.agendarVisitaEmpresa = agendarVisitaEmpresa;
	}

	public String getAgendarVisitaEmpresaUsuario() {
		return agendarVisitaEmpresaUsuario;
	}

	public void setAgendarVisitaEmpresaUsuario(String agendarVisitaEmpresaUsuario) {
		this.agendarVisitaEmpresaUsuario = agendarVisitaEmpresaUsuario;
	}

	public Date getVisitaEmpresaAprovadaData() {
		return visitaEmpresaAprovadaData;
	}

	public void setVisitaEmpresaAprovadaData(Date visitaEmpresaAprovadaData) {
		this.visitaEmpresaAprovadaData = visitaEmpresaAprovadaData;
	}

	public boolean isVisitaEmpresaAprovada() {
		return visitaEmpresaAprovada;
	}

	public void setVisitaEmpresaAprovada(boolean visitaEmpresaAprovada) {
		this.visitaEmpresaAprovada = visitaEmpresaAprovada;
	}

	public String getVisitaEmpresaAprovadaUsuario() {
		return visitaEmpresaAprovadaUsuario;
	}

	public void setVisitaEmpresaAprovadaUsuario(String visitaEmpresaAprovadaUsuario) {
		this.visitaEmpresaAprovadaUsuario = visitaEmpresaAprovadaUsuario;
	}

	public Date getVisitaEmpresaReprovadaData() {
		return visitaEmpresaReprovadaData;
	}

	public void setVisitaEmpresaReprovadaData(Date visitaEmpresaReprovadaData) {
		this.visitaEmpresaReprovadaData = visitaEmpresaReprovadaData;
	}

	public boolean isVisitaEmpresaReprovada() {
		return visitaEmpresaReprovada;
	}

	public void setVisitaEmpresaReprovada(boolean visitaEmpresaReprovada) {
		this.visitaEmpresaReprovada = visitaEmpresaReprovada;
	}

	public String getVisitaEmpresaReprovadaUsuario() {
		return visitaEmpresaReprovadaUsuario;
	}

	public void setVisitaEmpresaReprovadaUsuario(String visitaEmpresaReprovadaUsuario) {
		this.visitaEmpresaReprovadaUsuario = visitaEmpresaReprovadaUsuario;
	}

	public Date getAgendarVisitaImovelData() {
		return agendarVisitaImovelData;
	}

	public void setAgendarVisitaImovelData(Date agendarVisitaImovelData) {
		this.agendarVisitaImovelData = agendarVisitaImovelData;
	}

	public boolean isAgendarVisitaImovel() {
		return agendarVisitaImovel;
	}

	public void setAgendarVisitaImovel(boolean agendarVisitaImovel) {
		this.agendarVisitaImovel = agendarVisitaImovel;
	}

	public String getAgendarVisitaImovelUsuario() {
		return agendarVisitaImovelUsuario;
	}

	public void setAgendarVisitaImovelUsuario(String agendarVisitaImovelUsuario) {
		this.agendarVisitaImovelUsuario = agendarVisitaImovelUsuario;
	}

	public Date getVisitaImovelAprovadaData() {
		return visitaImovelAprovadaData;
	}

	public void setVisitaImovelAprovadaData(Date visitaImovelAprovadaData) {
		this.visitaImovelAprovadaData = visitaImovelAprovadaData;
	}

	public boolean isVisitaImovelAprovada() {
		return visitaImovelAprovada;
	}

	public void setVisitaImovelAprovada(boolean visitaImovelAprovada) {
		this.visitaImovelAprovada = visitaImovelAprovada;
	}

	public String getVisitaImovelAprovadaUsuario() {
		return visitaImovelAprovadaUsuario;
	}

	public void setVisitaImovelAprovadaUsuario(String visitaImovelAprovadaUsuario) {
		this.visitaImovelAprovadaUsuario = visitaImovelAprovadaUsuario;
	}

	public Date getVisitaImovelReprovadaData() {
		return visitaImovelReprovadaData;
	}

	public void setVisitaImovelReprovadaData(Date visitaImovelReprovadaData) {
		this.visitaImovelReprovadaData = visitaImovelReprovadaData;
	}

	public boolean isVisitaImovelReprovada() {
		return visitaImovelReprovada;
	}

	public void setVisitaImovelReprovada(boolean visitaImovelReprovada) {
		this.visitaImovelReprovada = visitaImovelReprovada;
	}

	public String getVisitaImovelReprovadaUsuario() {
		return visitaImovelReprovadaUsuario;
	}

	public void setVisitaImovelReprovadaUsuario(String visitaImovelReprovadaUsuario) {
		this.visitaImovelReprovadaUsuario = visitaImovelReprovadaUsuario;
	}

	public Date getEnviadoCobrancaLaudoData() {
		return enviadoCobrancaLaudoData;
	}

	public void setEnviadoCobrancaLaudoData(Date enviadoCobrancaLaudoData) {
		this.enviadoCobrancaLaudoData = enviadoCobrancaLaudoData;
	}

	public boolean isEnviadoCobrancaLaudo() {
		return enviadoCobrancaLaudo;
	}

	public void setEnviadoCobrancaLaudo(boolean enviadoCobrancaLaudo) {
		this.enviadoCobrancaLaudo = enviadoCobrancaLaudo;
	}

	public String getEnviadoCobrancaLaudoUsuario() {
		return enviadoCobrancaLaudoUsuario;
	}

	public void setEnviadoCobrancaLaudoUsuario(String enviadoCobrancaLaudoUsuario) {
		this.enviadoCobrancaLaudoUsuario = enviadoCobrancaLaudoUsuario;
	}

	public Date getPagtoLaudoConfirmadaData() {
		return pagtoLaudoConfirmadaData;
	}

	public void setPagtoLaudoConfirmadaData(Date pagtoLaudoConfirmadaData) {
		this.pagtoLaudoConfirmadaData = pagtoLaudoConfirmadaData;
	}

	public boolean isPagtoLaudoConfirmada() {
		return pagtoLaudoConfirmada;
	}

	public void setPagtoLaudoConfirmada(boolean pagtoLaudoConfirmada) {
		this.pagtoLaudoConfirmada = pagtoLaudoConfirmada;
	}

	public String getPagtoLaudoConfirmadaUsuario() {
		return pagtoLaudoConfirmadaUsuario;
	}

	public void setPagtoLaudoConfirmadaUsuario(String pagtoLaudoConfirmadaUsuario) {
		this.pagtoLaudoConfirmadaUsuario = pagtoLaudoConfirmadaUsuario;
	}

	public Date getLaudoSolicitadoData() {
		return laudoSolicitadoData;
	}

	public void setLaudoSolicitadoData(Date laudoSolicitadoData) {
		this.laudoSolicitadoData = laudoSolicitadoData;
	}

	public boolean isLaudoSolicitado() {
		return laudoSolicitado;
	}

	public void setLaudoSolicitado(boolean laudoSolicitado) {
		this.laudoSolicitado = laudoSolicitado;
	}

	public String getLaudoSolicitadoUsuario() {
		return laudoSolicitadoUsuario;
	}

	public void setLaudoSolicitadoUsuario(String laudoSolicitadoUsuario) {
		this.laudoSolicitadoUsuario = laudoSolicitadoUsuario;
	}

	public Date getLaudoRecebidoData() {
		return laudoRecebidoData;
	}

	public void setLaudoRecebidoData(Date laudoRecebidoData) {
		this.laudoRecebidoData = laudoRecebidoData;
	}

	public boolean isLaudoRecebido() {
		return laudoRecebido;
	}

	public void setLaudoRecebido(boolean laudoRecebido) {
		this.laudoRecebido = laudoRecebido;
	}

	public String getLaudoRecebidoUsuario() {
		return laudoRecebidoUsuario;
	}

	public void setLaudoRecebidoUsuario(String laudoRecebidoUsuario) {
		this.laudoRecebidoUsuario = laudoRecebidoUsuario;
	}

	public Date getPajurSolicitadoData() {
		return pajurSolicitadoData;
	}

	public void setPajurSolicitadoData(Date pajurSolicitadoData) {
		this.pajurSolicitadoData = pajurSolicitadoData;
	}

	public boolean isPajurSolicitado() {
		return pajurSolicitado;
	}

	public void setPajurSolicitado(boolean pajurSolicitado) {
		this.pajurSolicitado = pajurSolicitado;
	}

	public String getPajurSolicitadoUsuario() {
		return pajurSolicitadoUsuario;
	}

	public void setPajurSolicitadoUsuario(String pajurSolicitadoUsuario) {
		this.pajurSolicitadoUsuario = pajurSolicitadoUsuario;
	}

	public Date getPajurFavoravelData() {
		return pajurFavoravelData;
	}

	public void setPajurFavoravelData(Date pajurFavoravelData) {
		this.pajurFavoravelData = pajurFavoravelData;
	}

	public boolean isPajurFavoravel() {
		return pajurFavoravel;
	}

	public void setPajurFavoravel(boolean pajurFavoravel) {
		this.pajurFavoravel = pajurFavoravel;
	}

	public String getPajurFavoravelUsuario() {
		return pajurFavoravelUsuario;
	}

	public void setPajurFavoravelUsuario(String pajurFavoravelUsuario) {
		this.pajurFavoravelUsuario = pajurFavoravelUsuario;
	}

	public Date getPajurDesfavoravelData() {
		return pajurDesfavoravelData;
	}

	public void setPajurDesfavoravelData(Date pajurDesfavoravelData) {
		this.pajurDesfavoravelData = pajurDesfavoravelData;
	}

	public boolean isPajurDesfavoravel() {
		return pajurDesfavoravel;
	}

	public void setPajurDesfavoravel(boolean pajurDesfavoravel) {
		this.pajurDesfavoravel = pajurDesfavoravel;
	}

	public String getPajurDesfavoravelUsuario() {
		return pajurDesfavoravelUsuario;
	}

	public void setPajurDesfavoravelUsuario(String pajurDesfavoravelUsuario) {
		this.pajurDesfavoravelUsuario = pajurDesfavoravelUsuario;
	}

	public Date getReanalisarPajurData() {
		return reanalisarPajurData;
	}

	public void setReanalisarPajurData(Date reanalisarPajurData) {
		this.reanalisarPajurData = reanalisarPajurData;
	}

	public boolean isReanalisarPajur() {
		return reanalisarPajur;
	}

	public void setReanalisarPajur(boolean reanalisarPajur) {
		this.reanalisarPajur = reanalisarPajur;
	}

	public String getReanalisarPajurUsuario() {
		return reanalisarPajurUsuario;
	}

	public void setReanalisarPajurUsuario(String reanalisarPajurUsuario) {
		this.reanalisarPajurUsuario = reanalisarPajurUsuario;
	}

	public Date getAguardandoInvestidorData() {
		return aguardandoInvestidorData;
	}

	public void setAguardandoInvestidorData(Date aguardandoInvestidorData) {
		this.aguardandoInvestidorData = aguardandoInvestidorData;
	}

	public boolean isAguardandoInvestidor() {
		return aguardandoInvestidor;
	}

	public void setAguardandoInvestidor(boolean aguardandoInvestidor) {
		this.aguardandoInvestidor = aguardandoInvestidor;
	}

	public String getAguardandoInvestidorUsuario() {
		return aguardandoInvestidorUsuario;
	}

	public void setAguardandoInvestidorUsuario(String aguardandoInvestidorUsuario) {
		this.aguardandoInvestidorUsuario = aguardandoInvestidorUsuario;
	}

	public Date getAgendadoCartorioData() {
		return agendadoCartorioData;
	}

	public void setAgendadoCartorioData(Date agendadoCartorioData) {
		this.agendadoCartorioData = agendadoCartorioData;
	}

	public boolean isAgendadoCartorio() {
		return agendadoCartorio;
	}

	public void setAgendadoCartorio(boolean agendadoCartorio) {
		this.agendadoCartorio = agendadoCartorio;
	}

	public String getAgendadoCartorioUsuario() {
		return agendadoCartorioUsuario;
	}

	public void setAgendadoCartorioUsuario(String agendadoCartorioUsuario) {
		this.agendadoCartorioUsuario = agendadoCartorioUsuario;
	}

	public Date getContratoAssinadoData() {
		return contratoAssinadoData;
	}

	public void setContratoAssinadoData(Date contratoAssinadoData) {
		this.contratoAssinadoData = contratoAssinadoData;
	}

	public boolean isContratoAssinado() {
		return contratoAssinado;
	}

	public void setContratoAssinado(boolean contratoAssinado) {
		this.contratoAssinado = contratoAssinado;
	}

	public String getContratoAssinadoUsuario() {
		return contratoAssinadoUsuario;
	}

	public void setContratoAssinadoUsuario(String contratoAssinadoUsuario) {
		this.contratoAssinadoUsuario = contratoAssinadoUsuario;
	}

	public boolean isOcultaRecebedor() {
		return ocultaRecebedor;
	}

	public void setOcultaRecebedor(boolean ocultaRecebedor) {
		this.ocultaRecebedor = ocultaRecebedor;
	}

	public boolean isOcultaRecebedor2() {
		return ocultaRecebedor2;
	}

	public void setOcultaRecebedor2(boolean ocultaRecebedor2) {
		this.ocultaRecebedor2 = ocultaRecebedor2;
	}

	public boolean isOcultaRecebedor3() {
		return ocultaRecebedor3;
	}

	public void setOcultaRecebedor3(boolean ocultaRecebedor3) {
		this.ocultaRecebedor3 = ocultaRecebedor3;
	}

	public boolean isOcultaRecebedor4() {
		return ocultaRecebedor4;
	}

	public void setOcultaRecebedor4(boolean ocultaRecebedor4) {
		this.ocultaRecebedor4 = ocultaRecebedor4;
	}

	public boolean isOcultaRecebedor5() {
		return ocultaRecebedor5;
	}

	public void setOcultaRecebedor5(boolean ocultaRecebedor5) {
		this.ocultaRecebedor5 = ocultaRecebedor5;
	}

	public boolean isOcultaRecebedor6() {
		return ocultaRecebedor6;
	}

	public void setOcultaRecebedor6(boolean ocultaRecebedor6) {
		this.ocultaRecebedor6 = ocultaRecebedor6;
	}

	public boolean isOcultaRecebedor7() {
		return ocultaRecebedor7;
	}

	public void setOcultaRecebedor7(boolean ocultaRecebedor7) {
		this.ocultaRecebedor7 = ocultaRecebedor7;
	}

	public boolean isOcultaRecebedor8() {
		return ocultaRecebedor8;
	}

	public void setOcultaRecebedor8(boolean ocultaRecebedor8) {
		this.ocultaRecebedor8 = ocultaRecebedor8;
	}

	public boolean isOcultaRecebedor9() {
		return ocultaRecebedor9;
	}

	public void setOcultaRecebedor9(boolean ocultaRecebedor9) {
		this.ocultaRecebedor9 = ocultaRecebedor9;
	}

	public boolean isOcultaRecebedor10() {
		return ocultaRecebedor10;
	}

	public void setOcultaRecebedor10(boolean ocultaRecebedor10) {
		this.ocultaRecebedor10 = ocultaRecebedor10;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor1() {
		return listContratoCobrancaParcelasInvestidor1;
	}

	public void setListContratoCobrancaParcelasInvestidor1(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor1) {
		this.listContratoCobrancaParcelasInvestidor1 = listContratoCobrancaParcelasInvestidor1;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor2() {
		return listContratoCobrancaParcelasInvestidor2;
	}

	public void setListContratoCobrancaParcelasInvestidor2(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor2) {
		this.listContratoCobrancaParcelasInvestidor2 = listContratoCobrancaParcelasInvestidor2;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor3() {
		return listContratoCobrancaParcelasInvestidor3;
	}

	public void setListContratoCobrancaParcelasInvestidor3(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor3) {
		this.listContratoCobrancaParcelasInvestidor3 = listContratoCobrancaParcelasInvestidor3;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor4() {
		return listContratoCobrancaParcelasInvestidor4;
	}

	public void setListContratoCobrancaParcelasInvestidor4(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor4) {
		this.listContratoCobrancaParcelasInvestidor4 = listContratoCobrancaParcelasInvestidor4;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor5() {
		return listContratoCobrancaParcelasInvestidor5;
	}

	public void setListContratoCobrancaParcelasInvestidor5(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor5) {
		this.listContratoCobrancaParcelasInvestidor5 = listContratoCobrancaParcelasInvestidor5;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor6() {
		return listContratoCobrancaParcelasInvestidor6;
	}

	public void setListContratoCobrancaParcelasInvestidor6(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor6) {
		this.listContratoCobrancaParcelasInvestidor6 = listContratoCobrancaParcelasInvestidor6;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor7() {
		return listContratoCobrancaParcelasInvestidor7;
	}

	public void setListContratoCobrancaParcelasInvestidor7(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor7) {
		this.listContratoCobrancaParcelasInvestidor7 = listContratoCobrancaParcelasInvestidor7;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor8() {
		return listContratoCobrancaParcelasInvestidor8;
	}

	public void setListContratoCobrancaParcelasInvestidor8(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor8) {
		this.listContratoCobrancaParcelasInvestidor8 = listContratoCobrancaParcelasInvestidor8;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor9() {
		return listContratoCobrancaParcelasInvestidor9;
	}

	public void setListContratoCobrancaParcelasInvestidor9(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor9) {
		this.listContratoCobrancaParcelasInvestidor9 = listContratoCobrancaParcelasInvestidor9;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor10() {
		return listContratoCobrancaParcelasInvestidor10;
	}

	public void setListContratoCobrancaParcelasInvestidor10(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor10) {
		this.listContratoCobrancaParcelasInvestidor10 = listContratoCobrancaParcelasInvestidor10;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor1() {
		return taxaRemuneracaoInvestidor1;
	}

	public void setTaxaRemuneracaoInvestidor1(BigDecimal taxaRemuneracaoInvestidor1) {
		this.taxaRemuneracaoInvestidor1 = taxaRemuneracaoInvestidor1;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor2() {
		return taxaRemuneracaoInvestidor2;
	}

	public void setTaxaRemuneracaoInvestidor2(BigDecimal taxaRemuneracaoInvestidor2) {
		this.taxaRemuneracaoInvestidor2 = taxaRemuneracaoInvestidor2;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor3() {
		return taxaRemuneracaoInvestidor3;
	}

	public void setTaxaRemuneracaoInvestidor3(BigDecimal taxaRemuneracaoInvestidor3) {
		this.taxaRemuneracaoInvestidor3 = taxaRemuneracaoInvestidor3;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor4() {
		return taxaRemuneracaoInvestidor4;
	}

	public void setTaxaRemuneracaoInvestidor4(BigDecimal taxaRemuneracaoInvestidor4) {
		this.taxaRemuneracaoInvestidor4 = taxaRemuneracaoInvestidor4;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor5() {
		return taxaRemuneracaoInvestidor5;
	}

	public void setTaxaRemuneracaoInvestidor5(BigDecimal taxaRemuneracaoInvestidor5) {
		this.taxaRemuneracaoInvestidor5 = taxaRemuneracaoInvestidor5;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor6() {
		return taxaRemuneracaoInvestidor6;
	}

	public void setTaxaRemuneracaoInvestidor6(BigDecimal taxaRemuneracaoInvestidor6) {
		this.taxaRemuneracaoInvestidor6 = taxaRemuneracaoInvestidor6;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor7() {
		return taxaRemuneracaoInvestidor7;
	}

	public void setTaxaRemuneracaoInvestidor7(BigDecimal taxaRemuneracaoInvestidor7) {
		this.taxaRemuneracaoInvestidor7 = taxaRemuneracaoInvestidor7;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor8() {
		return taxaRemuneracaoInvestidor8;
	}

	public void setTaxaRemuneracaoInvestidor8(BigDecimal taxaRemuneracaoInvestidor8) {
		this.taxaRemuneracaoInvestidor8 = taxaRemuneracaoInvestidor8;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor9() {
		return taxaRemuneracaoInvestidor9;
	}

	public void setTaxaRemuneracaoInvestidor9(BigDecimal taxaRemuneracaoInvestidor9) {
		this.taxaRemuneracaoInvestidor9 = taxaRemuneracaoInvestidor9;
	}

	public BigDecimal getTaxaRemuneracaoInvestidor10() {
		return taxaRemuneracaoInvestidor10;
	}

	public void setTaxaRemuneracaoInvestidor10(BigDecimal taxaRemuneracaoInvestidor10) {
		this.taxaRemuneracaoInvestidor10 = taxaRemuneracaoInvestidor10;
	}

	public BigDecimal getVlrFinalRecebedor6() {
		return vlrFinalRecebedor6;
	}

	public void setVlrFinalRecebedor6(BigDecimal vlrFinalRecebedor6) {
		this.vlrFinalRecebedor6 = vlrFinalRecebedor6;
	}

	public PagadorRecebedor getRecebedorParcelaFinal6() {
		return recebedorParcelaFinal6;
	}

	public void setRecebedorParcelaFinal6(PagadorRecebedor recebedorParcelaFinal6) {
		this.recebedorParcelaFinal6 = recebedorParcelaFinal6;
	}

	public BigDecimal getVlrFinalRecebedor7() {
		return vlrFinalRecebedor7;
	}

	public void setVlrFinalRecebedor7(BigDecimal vlrFinalRecebedor7) {
		this.vlrFinalRecebedor7 = vlrFinalRecebedor7;
	}

	public PagadorRecebedor getRecebedorParcelaFinal7() {
		return recebedorParcelaFinal7;
	}

	public void setRecebedorParcelaFinal7(PagadorRecebedor recebedorParcelaFinal7) {
		this.recebedorParcelaFinal7 = recebedorParcelaFinal7;
	}

	public BigDecimal getVlrFinalRecebedor8() {
		return vlrFinalRecebedor8;
	}

	public void setVlrFinalRecebedor8(BigDecimal vlrFinalRecebedor8) {
		this.vlrFinalRecebedor8 = vlrFinalRecebedor8;
	}

	public PagadorRecebedor getRecebedorParcelaFinal8() {
		return recebedorParcelaFinal8;
	}

	public void setRecebedorParcelaFinal8(PagadorRecebedor recebedorParcelaFinal8) {
		this.recebedorParcelaFinal8 = recebedorParcelaFinal8;
	}

	public BigDecimal getVlrFinalRecebedor9() {
		return vlrFinalRecebedor9;
	}

	public void setVlrFinalRecebedor9(BigDecimal vlrFinalRecebedor9) {
		this.vlrFinalRecebedor9 = vlrFinalRecebedor9;
	}

	public PagadorRecebedor getRecebedorParcelaFinal9() {
		return recebedorParcelaFinal9;
	}

	public void setRecebedorParcelaFinal9(PagadorRecebedor recebedorParcelaFinal9) {
		this.recebedorParcelaFinal9 = recebedorParcelaFinal9;
	}

	public BigDecimal getVlrFinalRecebedor10() {
		return vlrFinalRecebedor10;
	}

	public void setVlrFinalRecebedor10(BigDecimal vlrFinalRecebedor10) {
		this.vlrFinalRecebedor10 = vlrFinalRecebedor10;
	}

	public Date getDataInclusaoRecebedor1() {
		return dataInclusaoRecebedor1;
	}

	public void setDataInclusaoRecebedor1(Date dataInclusaoRecebedor1) {
		this.dataInclusaoRecebedor1 = dataInclusaoRecebedor1;
	}

	public Date getDataInclusaoRecebedor2() {
		return dataInclusaoRecebedor2;
	}

	public void setDataInclusaoRecebedor2(Date dataInclusaoRecebedor2) {
		this.dataInclusaoRecebedor2 = dataInclusaoRecebedor2;
	}

	public Date getDataInclusaoRecebedor3() {
		return dataInclusaoRecebedor3;
	}

	public void setDataInclusaoRecebedor3(Date dataInclusaoRecebedor3) {
		this.dataInclusaoRecebedor3 = dataInclusaoRecebedor3;
	}

	public Date getDataInclusaoRecebedor4() {
		return dataInclusaoRecebedor4;
	}

	public void setDataInclusaoRecebedor4(Date dataInclusaoRecebedor4) {
		this.dataInclusaoRecebedor4 = dataInclusaoRecebedor4;
	}

	public Date getDataInclusaoRecebedor5() {
		return dataInclusaoRecebedor5;
	}

	public void setDataInclusaoRecebedor5(Date dataInclusaoRecebedor5) {
		this.dataInclusaoRecebedor5 = dataInclusaoRecebedor5;
	}

	public Date getDataInclusaoRecebedor6() {
		return dataInclusaoRecebedor6;
	}

	public void setDataInclusaoRecebedor6(Date dataInclusaoRecebedor6) {
		this.dataInclusaoRecebedor6 = dataInclusaoRecebedor6;
	}

	public Date getDataInclusaoRecebedor7() {
		return dataInclusaoRecebedor7;
	}

	public void setDataInclusaoRecebedor7(Date dataInclusaoRecebedor7) {
		this.dataInclusaoRecebedor7 = dataInclusaoRecebedor7;
	}

	public Date getDataInclusaoRecebedor8() {
		return dataInclusaoRecebedor8;
	}

	public void setDataInclusaoRecebedor8(Date dataInclusaoRecebedor8) {
		this.dataInclusaoRecebedor8 = dataInclusaoRecebedor8;
	}

	public Date getDataInclusaoRecebedor9() {
		return dataInclusaoRecebedor9;
	}

	public void setDataInclusaoRecebedor9(Date dataInclusaoRecebedor9) {
		this.dataInclusaoRecebedor9 = dataInclusaoRecebedor9;
	}

	public Date getDataInclusaoRecebedor10() {
		return dataInclusaoRecebedor10;
	}

	public void setDataInclusaoRecebedor10(Date dataInclusaoRecebedor10) {
		this.dataInclusaoRecebedor10 = dataInclusaoRecebedor10;
	}

	public PagadorRecebedor getRecebedorParcelaFinal10() {
		return recebedorParcelaFinal10;
	}

	public void setRecebedorParcelaFinal10(PagadorRecebedor recebedorParcelaFinal10) {
		this.recebedorParcelaFinal10 = recebedorParcelaFinal10;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidorSelecionado() {
		return listContratoCobrancaParcelasInvestidorSelecionado;
	}

	public void setListContratoCobrancaParcelasInvestidorSelecionado(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidorSelecionado) {
		this.listContratoCobrancaParcelasInvestidorSelecionado = listContratoCobrancaParcelasInvestidorSelecionado;
	}

	public BigDecimal getTxMulta() {
		return txMulta;
	}

	public void setTxMulta(BigDecimal txMulta) {
		this.txMulta = txMulta;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public boolean isRecebedorEnvelope() {
		return recebedorEnvelope;
	}

	public void setRecebedorEnvelope(boolean recebedorEnvelope) {
		this.recebedorEnvelope = recebedorEnvelope;
	}

	public boolean isRecebedorEnvelope2() {
		return recebedorEnvelope2;
	}

	public void setRecebedorEnvelope2(boolean recebedorEnvelope2) {
		this.recebedorEnvelope2 = recebedorEnvelope2;
	}

	public boolean isRecebedorEnvelope3() {
		return recebedorEnvelope3;
	}

	public void setRecebedorEnvelope3(boolean recebedorEnvelope3) {
		this.recebedorEnvelope3 = recebedorEnvelope3;
	}

	public boolean isRecebedorEnvelope4() {
		return recebedorEnvelope4;
	}

	public void setRecebedorEnvelope4(boolean recebedorEnvelope4) {
		this.recebedorEnvelope4 = recebedorEnvelope4;
	}

	public boolean isRecebedorEnvelope5() {
		return recebedorEnvelope5;
	}

	public void setRecebedorEnvelope5(boolean recebedorEnvelope5) {
		this.recebedorEnvelope5 = recebedorEnvelope5;
	}

	public boolean isRecebedorEnvelope6() {
		return recebedorEnvelope6;
	}

	public void setRecebedorEnvelope6(boolean recebedorEnvelope6) {
		this.recebedorEnvelope6 = recebedorEnvelope6;
	}

	public boolean isRecebedorEnvelope7() {
		return recebedorEnvelope7;
	}

	public void setRecebedorEnvelope7(boolean recebedorEnvelope7) {
		this.recebedorEnvelope7 = recebedorEnvelope7;
	}

	public boolean isRecebedorEnvelope8() {
		return recebedorEnvelope8;
	}

	public void setRecebedorEnvelope8(boolean recebedorEnvelope8) {
		this.recebedorEnvelope8 = recebedorEnvelope8;
	}

	public boolean isRecebedorEnvelope9() {
		return recebedorEnvelope9;
	}

	public void setRecebedorEnvelope9(boolean recebedorEnvelope9) {
		this.recebedorEnvelope9 = recebedorEnvelope9;
	}

	public boolean isRecebedorEnvelope10() {
		return recebedorEnvelope10;
	}

	public void setRecebedorEnvelope10(boolean recebedorEnvelope10) {
		this.recebedorEnvelope10 = recebedorEnvelope10;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidorSelecionadoEnvelope() {
		return listContratoCobrancaParcelasInvestidorSelecionadoEnvelope;
	}

	public void setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidorSelecionadoEnvelope) {
		this.listContratoCobrancaParcelasInvestidorSelecionadoEnvelope = listContratoCobrancaParcelasInvestidorSelecionadoEnvelope;
	}

	public boolean isRecebedorGarantido1() {
		return recebedorGarantido1;
	}

	public void setRecebedorGarantido1(boolean recebedorGarantido1) {
		this.recebedorGarantido1 = recebedorGarantido1;
	}

	public boolean isRecebedorGarantido2() {
		return recebedorGarantido2;
	}

	public void setRecebedorGarantido2(boolean recebedorGarantido2) {
		this.recebedorGarantido2 = recebedorGarantido2;
	}

	public boolean isRecebedorGarantido3() {
		return recebedorGarantido3;
	}

	public void setRecebedorGarantido3(boolean recebedorGarantido3) {
		this.recebedorGarantido3 = recebedorGarantido3;
	}

	public boolean isRecebedorGarantido4() {
		return recebedorGarantido4;
	}

	public void setRecebedorGarantido4(boolean recebedorGarantido4) {
		this.recebedorGarantido4 = recebedorGarantido4;
	}

	public boolean isRecebedorGarantido5() {
		return recebedorGarantido5;
	}

	public void setRecebedorGarantido5(boolean recebedorGarantido5) {
		this.recebedorGarantido5 = recebedorGarantido5;
	}

	public boolean isRecebedorGarantido6() {
		return recebedorGarantido6;
	}

	public void setRecebedorGarantido6(boolean recebedorGarantido6) {
		this.recebedorGarantido6 = recebedorGarantido6;
	}

	public boolean isRecebedorGarantido7() {
		return recebedorGarantido7;
	}

	public void setRecebedorGarantido7(boolean recebedorGarantido7) {
		this.recebedorGarantido7 = recebedorGarantido7;
	}

	public boolean isRecebedorGarantido8() {
		return recebedorGarantido8;
	}

	public void setRecebedorGarantido8(boolean recebedorGarantido8) {
		this.recebedorGarantido8 = recebedorGarantido8;
	}

	public boolean isRecebedorGarantido9() {
		return recebedorGarantido9;
	}

	public void setRecebedorGarantido9(boolean recebedorGarantido9) {
		this.recebedorGarantido9 = recebedorGarantido9;
	}

	public boolean isRecebedorGarantido10() {
		return recebedorGarantido10;
	}

	public void setRecebedorGarantido10(boolean recebedorGarantido10) {
		this.recebedorGarantido10 = recebedorGarantido10;
	}

	public BigDecimal getValorCCB() {
		return valorCCB;
	}

	public void setValorCCB(BigDecimal valorCCB) {
		this.valorCCB = valorCCB;
	}

	public String getCadastroAprovadoValor() {
		return cadastroAprovadoValor;
	}

	public void setCadastroAprovadoValor(String cadastroAprovadoValor) {
		this.cadastroAprovadoValor = cadastroAprovadoValor;
	}

	public String getMatriculaAprovadaValor() {
		return matriculaAprovadaValor;
	}

	public void setMatriculaAprovadaValor(String matriculaAprovadaValor) {
		this.matriculaAprovadaValor = matriculaAprovadaValor;
	}

	public Date getCcbProntaData() {
		return ccbProntaData;
	}

	public void setCcbProntaData(Date ccbProntaData) {
		this.ccbProntaData = ccbProntaData;
	}

	public boolean isCcbPronta() {
		return ccbPronta;
	}

	public void setCcbPronta(boolean ccbPronta) {
		this.ccbPronta = ccbPronta;
	}

	public String getCcbProntaUsuario() {
		return ccbProntaUsuario;
	}

	public void setCcbProntaUsuario(String ccbProntaUsuario) {
		this.ccbProntaUsuario = ccbProntaUsuario;
	}

	public Date getStatusContratoData() {
		return statusContratoData;
	}

	public void setStatusContratoData(Date statusContratoData) {
		this.statusContratoData = statusContratoData;
	}

	public String getStatusContrato() {
		return statusContrato;
	}

	public void setStatusContrato(String statusContrato) {
		this.statusContrato = statusContrato;
	}

	public String getStatusContratoUsuario() {
		return statusContratoUsuario;
	}

	public void setStatusContratoUsuario(String statusContratoUsuario) {
		this.statusContratoUsuario = statusContratoUsuario;
	}

	public Date getAgAssinaturaData() {
		return agAssinaturaData;
	}

	public void setAgAssinaturaData(Date agAssinaturaData) {
		this.agAssinaturaData = agAssinaturaData;
	}

	public boolean isAgAssinatura() {
		return agAssinatura;
	}

	public void setAgAssinatura(boolean agAssinatura) {
		this.agAssinatura = agAssinatura;
	}

	public String getAgAssinaturaUsuario() {
		return agAssinaturaUsuario;
	}

	public void setAgAssinaturaUsuario(String agAssinaturaUsuario) {
		this.agAssinaturaUsuario = agAssinaturaUsuario;
	}

	public boolean isParcelasAlteradas1() {
		return parcelasAlteradas1;
	}

	public void setParcelasAlteradas1(boolean parcelasAlteradas1) {
		this.parcelasAlteradas1 = parcelasAlteradas1;
	}

	public boolean isParcelasAlteradas2() {
		return parcelasAlteradas2;
	}

	public void setParcelasAlteradas2(boolean parcelasAlteradas2) {
		this.parcelasAlteradas2 = parcelasAlteradas2;
	}

	public boolean isParcelasAlteradas3() {
		return parcelasAlteradas3;
	}

	public void setParcelasAlteradas3(boolean parcelasAlteradas3) {
		this.parcelasAlteradas3 = parcelasAlteradas3;
	}

	public boolean isParcelasAlteradas4() {
		return parcelasAlteradas4;
	}

	public void setParcelasAlteradas4(boolean parcelasAlteradas4) {
		this.parcelasAlteradas4 = parcelasAlteradas4;
	}

	public boolean isParcelasAlteradas5() {
		return parcelasAlteradas5;
	}

	public void setParcelasAlteradas5(boolean parcelasAlteradas5) {
		this.parcelasAlteradas5 = parcelasAlteradas5;
	}

	public boolean isParcelasAlteradas6() {
		return parcelasAlteradas6;
	}

	public void setParcelasAlteradas6(boolean parcelasAlteradas6) {
		this.parcelasAlteradas6 = parcelasAlteradas6;
	}

	public boolean isParcelasAlteradas7() {
		return parcelasAlteradas7;
	}

	public void setParcelasAlteradas7(boolean parcelasAlteradas7) {
		this.parcelasAlteradas7 = parcelasAlteradas7;
	}

	public boolean isParcelasAlteradas8() {
		return parcelasAlteradas8;
	}

	public void setParcelasAlteradas8(boolean parcelasAlteradas8) {
		this.parcelasAlteradas8 = parcelasAlteradas8;
	}

	public boolean isParcelasAlteradas9() {
		return parcelasAlteradas9;
	}

	public void setParcelasAlteradas9(boolean parcelasAlteradas9) {
		this.parcelasAlteradas9 = parcelasAlteradas9;
	}

	public boolean isParcelasAlteradas10() {
		return parcelasAlteradas10;
	}

	public void setParcelasAlteradas10(boolean parcelasAlteradas10) {
		this.parcelasAlteradas10 = parcelasAlteradas10;
	}

	public BigDecimal getTxHonorario() {
		return txHonorario;
	}

	public void setTxHonorario(BigDecimal txHonorario) {
		this.txHonorario = txHonorario;
	}

	public BigDecimal getTxJurosParcelas() {
		return txJurosParcelas;
	}

	public void setTxJurosParcelas(BigDecimal txJurosParcelas) {
		this.txJurosParcelas = txJurosParcelas;
	}

	public boolean isLeadCompleto() {
		return leadCompleto;
	}

	public void setLeadCompleto(boolean leadCompleto) {
		this.leadCompleto = leadCompleto;
	}

	public Date getLeadCompletoData() {
		return leadCompletoData;
	}

	public void setLeadCompletoData(Date leadCompletoData) {
		this.leadCompletoData = leadCompletoData;
	}

	public String getLeadCompletoUsuario() {
		return leadCompletoUsuario;
	}

	public void setLeadCompletoUsuario(String leadCompletoUsuario) {
		this.leadCompletoUsuario = leadCompletoUsuario;
	}

	public int getMesesCarencia() {
		return mesesCarencia;
	}

	public void setMesesCarencia(int mesesCarencia) {
		this.mesesCarencia = mesesCarencia;
	}

	public Date getAnaliseReprovadaData() {
		return analiseReprovadaData;
	}

	public void setAnaliseReprovadaData(Date analiseReprovadaData) {
		this.analiseReprovadaData = analiseReprovadaData;
	}

	public boolean isAnaliseReprovada() {
		return analiseReprovada;
	}

	public void setAnaliseReprovada(boolean analiseReprovada) {
		this.analiseReprovada = analiseReprovada;
	}

	public String getAnaliseReprovadaUsuario() {
		return analiseReprovadaUsuario;
	}

	public void setAnaliseReprovadaUsuario(String analiseReprovadaUsuario) {
		this.analiseReprovadaUsuario = analiseReprovadaUsuario;
	}

	public Date getDataPrevistaVistoria() {
		return dataPrevistaVistoria;
	}

	public void setDataPrevistaVistoria(Date dataPrevistaVistoria) {
		this.dataPrevistaVistoria = dataPrevistaVistoria;
	}

	public String getMotivoReprovacaoAnalise() {
		return motivoReprovacaoAnalise;
	}

	public void setMotivoReprovacaoAnalise(String motivoReprovacaoAnalise) {
		this.motivoReprovacaoAnalise = motivoReprovacaoAnalise;
	}

	public String getUrlLead() {
		return urlLead;
	}

	public void setUrlLead(String urlLead) {
		this.urlLead = urlLead;
	}

	public String getStatusLead() {
		return statusLead;
	}

	public void setStatusLead(String statusLead) {
		this.statusLead = statusLead;
	}

	public String getObservacaolead() {
		return observacaolead;
	}

	public void setObservacaolead(String observacaolead) {
		this.observacaolead = observacaolead;
	}

	public String getMotivoReprovaLead() {
		return motivoReprovaLead;
	}

	public void setMotivoReprovaLead(String motivoReprovaLead) {
		this.motivoReprovaLead = motivoReprovaLead;
	}

	public boolean isCorrigidoIPCA() {
		return corrigidoIPCA;
	}

	public void setCorrigidoIPCA(boolean corrigidoIPCA) {
		this.corrigidoIPCA = corrigidoIPCA;
	}

	public boolean isTemSeguro() {
		return temSeguro;
	}

	public void setTemSeguro(boolean temSeguro) {
		this.temSeguro = temSeguro;
	}

	public boolean isTemSeguroDFI() {
		return temSeguroDFI;
	}

	public void setTemSeguroDFI(boolean temSeguroDFI) {
		this.temSeguroDFI = temSeguroDFI;
	}

	public boolean isTemSeguroMIP() {
		return temSeguroMIP;
	}

	public void setTemSeguroMIP(boolean temSeguroMIP) {
		this.temSeguroMIP = temSeguroMIP;
	}

	public BigDecimal getValorImovel() {
		return valorImovel;
	}

	public void setValorImovel(BigDecimal valorImovel) {
		this.valorImovel = valorImovel;
	}

	public Date getAgRegistroData() {
		return agRegistroData;
	}

	public void setAgRegistroData(Date agRegistroData) {
		this.agRegistroData = agRegistroData;
	}

	public boolean isAgRegistro() {
		return agRegistro;
	}

	public void setAgRegistro(boolean agRegistro) {
		this.agRegistro = agRegistro;
	}

	public String getAgRegistroUsuario() {
		return agRegistroUsuario;
	}

	public void setAgRegistroUsuario(String agRegistroUsuario) {
		this.agRegistroUsuario = agRegistroUsuario;
	}

	public Set<Segurado> getListSegurados() {
		return listSegurados;
	}

	public void setListSegurados(Set<Segurado> listSegurados) {
		this.listSegurados = listSegurados;
	}

	public String getNumeroContratoSeguro() {
		return numeroContratoSeguro;
	}

	public void setNumeroContratoSeguro(String numeroContratoSeguro) {
		this.numeroContratoSeguro = numeroContratoSeguro;
	}

	public String getTipoCalculoInvestidor1() {
		return tipoCalculoInvestidor1;
	}

	public void setTipoCalculoInvestidor1(String tipoCalculoInvestidor1) {
		this.tipoCalculoInvestidor1 = tipoCalculoInvestidor1;
	}

	public BigDecimal getVlrInvestidor1() {
		return vlrInvestidor1;
	}

	public void setVlrInvestidor1(BigDecimal vlrInvestidor1) {
		this.vlrInvestidor1 = vlrInvestidor1;
	}

	public Integer getQtdeParcelasInvestidor1() {
		return qtdeParcelasInvestidor1;
	}

	public void setQtdeParcelasInvestidor1(Integer qtdeParcelasInvestidor1) {
		this.qtdeParcelasInvestidor1 = qtdeParcelasInvestidor1;
	}

	public String getTipoCalculoInvestidor2() {
		return tipoCalculoInvestidor2;
	}

	public void setTipoCalculoInvestidor2(String tipoCalculoInvestidor2) {
		this.tipoCalculoInvestidor2 = tipoCalculoInvestidor2;
	}

	public BigDecimal getVlrInvestidor2() {
		return vlrInvestidor2;
	}

	public void setVlrInvestidor2(BigDecimal vlrInvestidor2) {
		this.vlrInvestidor2 = vlrInvestidor2;
	}

	public Integer getQtdeParcelasInvestidor2() {
		return qtdeParcelasInvestidor2;
	}

	public void setQtdeParcelasInvestidor2(Integer qtdeParcelasInvestidor2) {
		this.qtdeParcelasInvestidor2 = qtdeParcelasInvestidor2;
	}

	public String getTipoCalculoInvestidor3() {
		return tipoCalculoInvestidor3;
	}

	public void setTipoCalculoInvestidor3(String tipoCalculoInvestidor3) {
		this.tipoCalculoInvestidor3 = tipoCalculoInvestidor3;
	}

	public BigDecimal getVlrInvestidor3() {
		return vlrInvestidor3;
	}

	public void setVlrInvestidor3(BigDecimal vlrInvestidor3) {
		this.vlrInvestidor3 = vlrInvestidor3;
	}

	public Integer getQtdeParcelasInvestidor3() {
		return qtdeParcelasInvestidor3;
	}

	public void setQtdeParcelasInvestidor3(Integer qtdeParcelasInvestidor3) {
		this.qtdeParcelasInvestidor3 = qtdeParcelasInvestidor3;
	}

	public String getTipoCalculoInvestidor4() {
		return tipoCalculoInvestidor4;
	}

	public void setTipoCalculoInvestidor4(String tipoCalculoInvestidor4) {
		this.tipoCalculoInvestidor4 = tipoCalculoInvestidor4;
	}

	public BigDecimal getVlrInvestidor4() {
		return vlrInvestidor4;
	}

	public void setVlrInvestidor4(BigDecimal vlrInvestidor4) {
		this.vlrInvestidor4 = vlrInvestidor4;
	}

	public Integer getQtdeParcelasInvestidor4() {
		return qtdeParcelasInvestidor4;
	}

	public void setQtdeParcelasInvestidor4(Integer qtdeParcelasInvestidor4) {
		this.qtdeParcelasInvestidor4 = qtdeParcelasInvestidor4;
	}

	public String getTipoCalculoInvestidor5() {
		return tipoCalculoInvestidor5;
	}

	public void setTipoCalculoInvestidor5(String tipoCalculoInvestidor5) {
		this.tipoCalculoInvestidor5 = tipoCalculoInvestidor5;
	}

	public BigDecimal getVlrInvestidor5() {
		return vlrInvestidor5;
	}

	public void setVlrInvestidor5(BigDecimal vlrInvestidor5) {
		this.vlrInvestidor5 = vlrInvestidor5;
	}

	public Integer getQtdeParcelasInvestidor5() {
		return qtdeParcelasInvestidor5;
	}

	public void setQtdeParcelasInvestidor5(Integer qtdeParcelasInvestidor5) {
		this.qtdeParcelasInvestidor5 = qtdeParcelasInvestidor5;
	}

	public String getTipoCalculoInvestidor6() {
		return tipoCalculoInvestidor6;
	}

	public void setTipoCalculoInvestidor6(String tipoCalculoInvestidor6) {
		this.tipoCalculoInvestidor6 = tipoCalculoInvestidor6;
	}

	public BigDecimal getVlrInvestidor6() {
		return vlrInvestidor6;
	}

	public void setVlrInvestidor6(BigDecimal vlrInvestidor6) {
		this.vlrInvestidor6 = vlrInvestidor6;
	}

	public Integer getQtdeParcelasInvestidor6() {
		return qtdeParcelasInvestidor6;
	}

	public void setQtdeParcelasInvestidor6(Integer qtdeParcelasInvestidor6) {
		this.qtdeParcelasInvestidor6 = qtdeParcelasInvestidor6;
	}

	public String getTipoCalculoInvestidor7() {
		return tipoCalculoInvestidor7;
	}

	public void setTipoCalculoInvestidor7(String tipoCalculoInvestidor7) {
		this.tipoCalculoInvestidor7 = tipoCalculoInvestidor7;
	}

	public BigDecimal getVlrInvestidor7() {
		return vlrInvestidor7;
	}

	public void setVlrInvestidor7(BigDecimal vlrInvestidor7) {
		this.vlrInvestidor7 = vlrInvestidor7;
	}

	public Integer getQtdeParcelasInvestidor7() {
		return qtdeParcelasInvestidor7;
	}

	public void setQtdeParcelasInvestidor7(Integer qtdeParcelasInvestidor7) {
		this.qtdeParcelasInvestidor7 = qtdeParcelasInvestidor7;
	}

	public String getTipoCalculoInvestidor8() {
		return tipoCalculoInvestidor8;
	}

	public void setTipoCalculoInvestidor8(String tipoCalculoInvestidor8) {
		this.tipoCalculoInvestidor8 = tipoCalculoInvestidor8;
	}

	public BigDecimal getVlrInvestidor8() {
		return vlrInvestidor8;
	}

	public void setVlrInvestidor8(BigDecimal vlrInvestidor8) {
		this.vlrInvestidor8 = vlrInvestidor8;
	}

	public Integer getQtdeParcelasInvestidor8() {
		return qtdeParcelasInvestidor8;
	}

	public void setQtdeParcelasInvestidor8(Integer qtdeParcelasInvestidor8) {
		this.qtdeParcelasInvestidor8 = qtdeParcelasInvestidor8;
	}

	public String getTipoCalculoInvestidor9() {
		return tipoCalculoInvestidor9;
	}

	public void setTipoCalculoInvestidor9(String tipoCalculoInvestidor9) {
		this.tipoCalculoInvestidor9 = tipoCalculoInvestidor9;
	}

	public BigDecimal getVlrInvestidor9() {
		return vlrInvestidor9;
	}

	public void setVlrInvestidor9(BigDecimal vlrInvestidor9) {
		this.vlrInvestidor9 = vlrInvestidor9;
	}

	public Integer getQtdeParcelasInvestidor9() {
		return qtdeParcelasInvestidor9;
	}

	public void setQtdeParcelasInvestidor9(Integer qtdeParcelasInvestidor9) {
		this.qtdeParcelasInvestidor9 = qtdeParcelasInvestidor9;
	}

	public String getTipoCalculoInvestidor10() {
		return tipoCalculoInvestidor10;
	}

	public void setTipoCalculoInvestidor10(String tipoCalculoInvestidor10) {
		this.tipoCalculoInvestidor10 = tipoCalculoInvestidor10;
	}

	public BigDecimal getVlrInvestidor10() {
		return vlrInvestidor10;
	}

	public void setVlrInvestidor10(BigDecimal vlrInvestidor10) {
		this.vlrInvestidor10 = vlrInvestidor10;
	}

	public Integer getQtdeParcelasInvestidor10() {
		return qtdeParcelasInvestidor10;
	}

	public void setQtdeParcelasInvestidor10(Integer qtdeParcelasInvestidor10) {
		this.qtdeParcelasInvestidor10 = qtdeParcelasInvestidor10;
	}

	public Integer getCarenciaInvestidor1() {
		return carenciaInvestidor1;
	}

	public void setCarenciaInvestidor1(Integer carenciaInvestidor1) {
		this.carenciaInvestidor1 = carenciaInvestidor1;
	}

	public Integer getCarenciaInvestidor2() {
		return carenciaInvestidor2;
	}

	public void setCarenciaInvestidor2(Integer carenciaInvestidor2) {
		this.carenciaInvestidor2 = carenciaInvestidor2;
	}

	public Integer getCarenciaInvestidor3() {
		return carenciaInvestidor3;
	}

	public void setCarenciaInvestidor3(Integer carenciaInvestidor3) {
		this.carenciaInvestidor3 = carenciaInvestidor3;
	}

	public Integer getCarenciaInvestidor4() {
		return carenciaInvestidor4;
	}

	public void setCarenciaInvestidor4(Integer carenciaInvestidor4) {
		this.carenciaInvestidor4 = carenciaInvestidor4;
	}

	public Integer getCarenciaInvestidor5() {
		return carenciaInvestidor5;
	}

	public void setCarenciaInvestidor5(Integer carenciaInvestidor5) {
		this.carenciaInvestidor5 = carenciaInvestidor5;
	}

	public Integer getCarenciaInvestidor6() {
		return carenciaInvestidor6;
	}

	public void setCarenciaInvestidor6(Integer carenciaInvestidor6) {
		this.carenciaInvestidor6 = carenciaInvestidor6;
	}

	public Integer getCarenciaInvestidor7() {
		return carenciaInvestidor7;
	}

	public void setCarenciaInvestidor7(Integer carenciaInvestidor7) {
		this.carenciaInvestidor7 = carenciaInvestidor7;
	}

	public Integer getCarenciaInvestidor8() {
		return carenciaInvestidor8;
	}

	public void setCarenciaInvestidor8(Integer carenciaInvestidor8) {
		this.carenciaInvestidor8 = carenciaInvestidor8;
	}

	public Integer getCarenciaInvestidor9() {
		return carenciaInvestidor9;
	}

	public void setCarenciaInvestidor9(Integer carenciaInvestidor9) {
		this.carenciaInvestidor9 = carenciaInvestidor9;
	}

	public Integer getCarenciaInvestidor10() {
		return carenciaInvestidor10;
	}

	public void setCarenciaInvestidor10(Integer carenciaInvestidor10) {
		this.carenciaInvestidor10 = carenciaInvestidor10;
	}

	public Date getDataInicioInvestidor1() {
		return dataInicioInvestidor1;
	}

	public void setDataInicioInvestidor1(Date dataInicioInvestidor1) {
		this.dataInicioInvestidor1 = dataInicioInvestidor1;
	}

	public Date getDataInicioInvestidor2() {
		return dataInicioInvestidor2;
	}

	public void setDataInicioInvestidor2(Date dataInicioInvestidor2) {
		this.dataInicioInvestidor2 = dataInicioInvestidor2;
	}

	public Date getDataInicioInvestidor3() {
		return dataInicioInvestidor3;
	}

	public void setDataInicioInvestidor3(Date dataInicioInvestidor3) {
		this.dataInicioInvestidor3 = dataInicioInvestidor3;
	}

	public Date getDataInicioInvestidor4() {
		return dataInicioInvestidor4;
	}

	public void setDataInicioInvestidor4(Date dataInicioInvestidor4) {
		this.dataInicioInvestidor4 = dataInicioInvestidor4;
	}

	public Date getDataInicioInvestidor5() {
		return dataInicioInvestidor5;
	}

	public void setDataInicioInvestidor5(Date dataInicioInvestidor5) {
		this.dataInicioInvestidor5 = dataInicioInvestidor5;
	}

	public Date getDataInicioInvestidor6() {
		return dataInicioInvestidor6;
	}

	public void setDataInicioInvestidor6(Date dataInicioInvestidor6) {
		this.dataInicioInvestidor6 = dataInicioInvestidor6;
	}

	public Date getDataInicioInvestidor7() {
		return dataInicioInvestidor7;
	}

	public void setDataInicioInvestidor7(Date dataInicioInvestidor7) {
		this.dataInicioInvestidor7 = dataInicioInvestidor7;
	}

	public Date getDataInicioInvestidor8() {
		return dataInicioInvestidor8;
	}

	public void setDataInicioInvestidor8(Date dataInicioInvestidor8) {
		this.dataInicioInvestidor8 = dataInicioInvestidor8;
	}

	public Date getDataInicioInvestidor9() {
		return dataInicioInvestidor9;
	}

	public void setDataInicioInvestidor9(Date dataInicioInvestidor9) {
		this.dataInicioInvestidor9 = dataInicioInvestidor9;
	}

	public Date getDataInicioInvestidor10() {
		return dataInicioInvestidor10;
	}

	public void setDataInicioInvestidor10(Date dataInicioInvestidor10) {
		this.dataInicioInvestidor10 = dataInicioInvestidor10;
	}
	
	public BigDecimal getValorBoletoPreContrato() {
		return valorBoletoPreContrato;
	}

	public void setValorBoletoPreContrato(BigDecimal valorBoletoPreContrato) {
		this.valorBoletoPreContrato = valorBoletoPreContrato;
	}

	public BigDecimal getTaxaPreAprovada() {
		return taxaPreAprovada;
	}

	public void setTaxaPreAprovada(BigDecimal taxaPreAprovada) {
		this.taxaPreAprovada = taxaPreAprovada;
	}

	public BigDecimal getValorMercadoImovel() {
		return valorMercadoImovel;
	}

	public void setValorMercadoImovel(BigDecimal valorMercadoImovel) {
		this.valorMercadoImovel = valorMercadoImovel;
	}

	public BigDecimal getValorVendaForcadaImovel() {
		return valorVendaForcadaImovel;
	}

	public void setValorVendaForcadaImovel(BigDecimal valorVendaForcadaImovel) {
		this.valorVendaForcadaImovel = valorVendaForcadaImovel;
	}

	public String getComentarioJuridico() {
		return comentarioJuridico;
	}
 	
	public void setComentarioJuridico(String comentarioJuridico) {
		this.comentarioJuridico = comentarioJuridico;
	}

	public Date getVencimentoBoleto() {
		return vencimentoBoleto;
	}

	public void setVencimentoBoleto(Date vencimentoBoleto) {
		this.vencimentoBoleto = vencimentoBoleto;
	}

	public BigInteger getPrazoMaxPreAprovado() {
		return prazoMaxPreAprovado;
	}
	
	public void setPrazoMaxPreAprovado(BigInteger prazoMaxPreAprovado) {
		this.prazoMaxPreAprovado = prazoMaxPreAprovado;
	}


	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public String getNomeCidadeImovel() {
		return nomeCidadeImovel;
	}

	public void setNomeCidadeImovel(String nomeCidadeImovel) {
		this.nomeCidadeImovel = nomeCidadeImovel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
}