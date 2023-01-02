package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesParcialDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.job.IpcaJobCalcular;
import com.webnowbr.siscoat.job.IpcaJobContrato;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;


@ManagedBean(name = "ipcaMB")
@SessionScoped

public class IPCAMB {
	private List<IPCA> listIPCA;
	private Date data;
	private BigDecimal taxa;	
	private IPCA selectedIPCA;
	
	private final IpcaJobCalcular ipcaJobCalcular; 
	
	private static final Log LOGGER = LogFactory.getLog(IpcaJobContrato.class);
	
	@ManagedProperty(value = "#{contratoCobrancaMB}")
	protected ContratoCobrancaMB contratoCobrancaMB;
	
	public IPCAMB() {
		this.ipcaJobCalcular = new IpcaJobCalcular();		
	}
	
	public String clearFieldsIPCA() {
		this.data = gerarDataHoje();
		this.taxa = BigDecimal.ZERO;				
		
		IPCADao ipcaDao = new IPCADao();
		this.listIPCA = ipcaDao.findAll();
		
		return "/Cadastros/Cobranca/IPCA.xhtml";
	}
	
	public void inserirIPCA() {
		FacesContext context = FacesContext.getCurrentInstance();
		IPCADao ipcaDao = new IPCADao();
		
		if (ipcaDao.findByFilter("data", this.data).size() == 0) { 		
			IPCA ipca = new IPCA();
			ipca.setData(this.data);
			ipca.setTaxa(this.taxa);
					
			ipcaDao.create(ipca);
			
			//atualizaValoresContratos();
			
			clearFieldsIPCA();
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "[IPCA] Taxa inserida com sucesso!", ""));
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "[IPCA] A Data informada já possui Taxa!", ""));
		}
	}
	
	public void atualizaValoresContratos() {
		// busca contratos com checkcorrigido
		//faz calculos
		// atualiza parcela		
		
		// busca contratos com check corrigidoIPCA
		IPCADao IPCADao = new IPCADao();
		List<ContratoCobrancaDetalhes> listaParcelas = new ArrayList<ContratoCobrancaDetalhes>();
		
		// Prepara as datas para consulta das parcelas
		// a primeira data deve ser o dia 14 do mês seguinte ao inserido na taxa
		Date dataInicioConsulta = getDataComAcrescimoDeMes(this.data);
		// a segunda data deve ser o dia 14 do mês seguinte a data inicio
		//Date dataFimConsulta = getDataComAcrescimoDeMes(dataInicioConsulta);
		
		//ATUALIZA PARCELAS DO MES VIGENTE
		listaParcelas = IPCADao.getContratosPorInvestidorInformeRendimentos(dataInicioConsulta);
		
		if (listaParcelas.size() > 0) {
			if (this.taxa.compareTo(BigDecimal.ZERO) == 1) {
				BigDecimal taxaCalculo = this.taxa.divide(BigDecimal.valueOf(100));
				
				BigDecimal saldoParcelaAnterior = BigDecimal.ZERO;
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
				for (ContratoCobrancaDetalhes parcela : listaParcelas) {
					
					ContratoCobranca contrato = cDao.findById(parcela.getIdContrato());
					
					// se primeira parcela calcula com o valor da CCB
					if (parcela.getNumeroParcela().equals("1")) {																					
						parcela.setIpca(contrato.getValorCCB().multiply(taxaCalculo));		
						
						saldoParcelaAnterior = contrato.getValorCCB();
					} else {
						int parcelaAtual = Integer.valueOf(parcela.getNumeroParcela());
						
						// Pega Saldo da parcela anterior
						for (ContratoCobrancaDetalhes parcelaContrato : contrato.getListContratoCobrancaDetalhes()) {
							
							if ((Integer.valueOf(parcelaContrato.getNumeroParcela()) == (parcelaAtual - 1))) {
								// se não calcula com o saldo devedor
								parcela.setIpca(parcelaContrato.getVlrSaldoParcela().multiply(taxaCalculo));
							}
						}
						
					}
					
					parcela.setVlrParcela(parcela.getVlrJurosParcela().add(parcela.getVlrAmortizacaoParcela()).add(parcela.getIpca()));
					
					// persistir parcela
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					contratoCobrancaDetalhesDao.merge(parcela);				
				}
			}
		}
		
		// Prepara as datas para consulta das parcelas
		// a primeira data deve ser o dia 14 do mês seguinte ao inserido na taxa
		dataInicioConsulta = getDataComAcrescimoDeMes(dataInicioConsulta);
		// a segunda data deve ser o dia 14 do mês seguinte a data inicio
		//dataFimConsulta = getDataComAcrescimoDeMes(dataInicioConsulta);
		
		//ATUALIZA PARCELAS DO MES SEGUINTE (PROJECAO)
		listaParcelas = IPCADao.getContratosPorInvestidorInformeRendimentos(dataInicioConsulta);
		
		if (listaParcelas.size() > 0) {
			if (this.taxa.compareTo(BigDecimal.ZERO) == 1) {
				BigDecimal taxaCalculo = this.taxa.divide(BigDecimal.valueOf(100));
				
				BigDecimal saldoParcelaAnterior = BigDecimal.ZERO;
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
				for (ContratoCobrancaDetalhes parcela : listaParcelas) {
					
					ContratoCobranca contrato = cDao.findById(parcela.getIdContrato());
					
					// se primeira parcela calcula com o valor da CCB
					if (parcela.getNumeroParcela().equals("1")) {																					
						parcela.setIpca(contrato.getValorCCB().multiply(taxaCalculo));		
						
						saldoParcelaAnterior = contrato.getValorCCB();
					} else {
						int parcelaAtual = Integer.valueOf(parcela.getNumeroParcela());
						
						// Pega Saldo da parcela anterior
						for (ContratoCobrancaDetalhes parcelaContrato : contrato.getListContratoCobrancaDetalhes()) {
							
							if ((Integer.valueOf(parcelaContrato.getNumeroParcela()) == (parcelaAtual - 1))) {
								// se não calcula com o saldo devedor
								parcela.setIpca(parcelaContrato.getVlrSaldoParcela().multiply(taxaCalculo));
							}
						}
						
					}
					
					parcela.setVlrParcela(parcela.getVlrJurosParcela().add(parcela.getVlrAmortizacaoParcela()).add(parcela.getIpca()));
					
					// persistir parcela
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					contratoCobrancaDetalhesDao.merge(parcela);				
				}
			}
		}
	}
	
	public void reProcessaIPCAContrato(ContratoCobranca contrato) {
		FacesContext context = FacesContext.getCurrentInstance();
		IPCADao IPCADao = new IPCADao();
		
		if (contrato.isCorrigidoIPCA()) {
			BigDecimal saldoParcelaAnterior = BigDecimal.ZERO;

			for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
				if (!parcela.isParcelaPaga()) {
					// get a taxa do mês
					Date dataConsulta = getDataComMesAnterior(parcela.getDataVencimento());
					BigDecimal taxaMesReferencia = IPCADao.getTaxaIPCAMes(dataConsulta);
					if (taxaMesReferencia.compareTo(BigDecimal.ZERO) == 1) {									
						taxaMesReferencia = taxaMesReferencia.divide(BigDecimal.valueOf(100));
						
						// se primeira parcela calcula com o valor da CCB
						if (parcela.getNumeroParcela().equals("1")) {							
							parcela.setIpca(contrato.getValorCCB().multiply(taxaMesReferencia));
							
							saldoParcelaAnterior = contrato.getValorCCB();
						} else {
							// se não calcula com o saldo devedor
							parcela.setIpca(saldoParcelaAnterior.multiply(taxaMesReferencia));
							
							saldoParcelaAnterior = parcela.getVlrSaldoParcela();
						}
						
						parcela.setVlrParcela(parcela.getVlrJurosParcela().add(parcela.getVlrAmortizacaoParcela()).add(parcela.getIpca()));
						
						// persistir parcela
						ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
						contratoCobrancaDetalhesDao.merge(parcela);	
					}
				}
			}
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "[IPCA] Contrato corrigido pelo IPCA com sucesso!", ""));
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "[IPCA] Este contrato não está configurado para ser corrigido pelo IPCA!", ""));
		}		
	}
	
	public void atualizaIPCAPorContrato(String numeroContrato) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();
			
			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findByFilter("numeroContrato", numeroContrato);

			LOGGER.info("incio atualizaIPCAPorContrato");
			
			if (contratosCobranca.size() > 0) {		
				if (!contratosCobranca.get(0).isCorrigidoNovoIPCA()) {		
					for (ContratoCobranca contratoCobranca : contratosCobranca) {
						
						contratoCobranca.setRecalculaIPCA(true);
						
						for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {
							if (CommonsUtil.mesmoValor(contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe).getNumeroParcela() , "0") )
								continue;
							
							try {
								if (!ipcaJobCalcular.calcularIPCACustom(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe), contratoCobranca))
									break;
							} catch (Exception e) {
								LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
								continue;
							}
						}
						
						contratoCobranca.setRecalculaIPCA(false);
						contratoCobrancaDao.merge(contratoCobranca);
						
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "[Reprocessamento IPCA] Contrato " + contratoCobranca.getNumeroContrato() + " reprocessado com sucesso!", ""));
					}
				} else {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "[Reprocessamento IPCA] Este reprocessamento não é permitido para contratos corrigidos pelo NOVO IPCA!", ""));
				}
			}
			//contratoCobranca = contratoCobrancaDao.findById(contratoCobranca.getId());
				
			LOGGER.info("Fim atualizaIPCAPorContrato");

		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
		}
	}
	
	public void atualizaIPCAPorContratoMaluco(String numeroContrato) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();
			
			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findByFilter("numeroContrato", numeroContrato);

			LOGGER.info("incio atualizaIPCAPorContrato");
			
			if (contratosCobranca.size() > 0) {			
					for (ContratoCobranca contratoCobranca : contratosCobranca) {
						
						contratoCobranca.setRecalculaIPCA(true);
						
						contratoCobranca.setCorrigidoIPCA(true);
						contratoCobranca.setCorrigidoNovoIPCA(false);
						
						for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {
							if (CommonsUtil.mesmoValor(contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe).getNumeroParcela() , "0") )
								continue;
							
							try {
								if (!ipcaJobCalcular.calcularIPCACustomMaluco(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe), contratoCobranca))
									break;
							} catch (Exception e) {
								LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
								continue;
							}
						}
						
						contratoCobranca.setRecalculaIPCA(false);
						contratoCobrancaDao.merge(contratoCobranca);
						
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "[Reprocessamento IPCA] Contrato " + contratoCobranca.getNumeroContrato() + " reprocessado com sucesso!", ""));
					}
			}
			//contratoCobranca = contratoCobrancaDao.findById(contratoCobranca.getId());
				
			LOGGER.info("Fim atualizaIPCAPorContrato");

		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
		}
	}
	
	public void atualizaNovoIPCAPorContrato(String numeroContrato) {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		IPCADao ipcaDao = new IPCADao();
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		
		BigInteger ultimaParcela = BigInteger.ZERO;
		
		List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findByFilter("numeroContrato", numeroContrato);
		
		if (contratosCobranca.size() > 0) {		
			if (contratosCobranca.get(0).isCorrigidoNovoIPCA()) {		
				for (ContratoCobranca contratoCobranca : contratosCobranca) {
					contratoCobrancaMB.setObjetoContratoCobranca(contratoCobranca);
					contratoCobrancaMB.setQtdeParcelas(String.valueOf(contratoCobrancaMB.getObjetoContratoCobranca().getQtdeParcelas()));
				}
			}
		}		
		
		try {			
			if (contratoCobrancaMB.getObjetoContratoCobranca().getTipoCalculo().equals("Price [IPCA Novo]")) {
				contratoCobrancaMB.setSimuladorParcelas(contratoCobrancaMB.calcularParcelasPriceIPCANovo());
			} else {
				if (contratoCobrancaMB.getObjetoContratoCobranca().isCorrigidoNovoIPCA()) {
					if (contratoCobrancaMB.getObjetoContratoCobranca().getTipoCalculo().equals("Price")) {
						contratoCobrancaMB.setSimuladorParcelas(contratoCobrancaMB.calcularParcelasPriceIPCANovo());
					}
					if (contratoCobrancaMB.getObjetoContratoCobranca().getTipoCalculo().equals("SAC")) {
						contratoCobrancaMB.setSimuladorParcelas(contratoCobrancaMB.calcularParcelasSACIPCANovo());
					}
					if (contratoCobrancaMB.getObjetoContratoCobranca().getTipoCalculo().equals("Americano")) {
						//this.simuladorParcelas = calcularParcelasPriceIPCANovo();
					}
				}
			}
		} catch (Exception e) {
		}
		
		contratoCobrancaDao.merge(contratoCobrancaMB.getObjetoContratoCobranca());

		int numeroParcelaReparcelamento = 0;		
		
		contratoCobrancaMB.setSaldoDevedorReparcelamento(contratoCobrancaMB.getObjetoContratoCobranca().getValorCCB());
			
		Date dataVencimentoNova = contratoCobrancaMB.getObjetoContratoCobranca().getDataInicio();
		
		for (SimulacaoDetalheVO parcela : contratoCobrancaMB.getSimuladorParcelas().getParcelas()) {
			boolean encontrouParcela = false;
			BigDecimal saldoAnterior = BigDecimal.ZERO;
			
			for (ContratoCobrancaDetalhes detalhe : contratoCobrancaMB.getObjetoContratoCobranca().getListContratoCobrancaDetalhes()) {
				
				
				if (CommonsUtil.mesmoValor(parcela.getNumeroParcela().toString(), detalhe.getNumeroParcela())) {
					
					Date dataParcela =null;
					
					if (!detalhe.isAmortizacao())
						dataParcela = contratoCobrancaDao
								.geraDataParcela((CommonsUtil.intValue(parcela.getNumeroParcela())
										- numeroParcelaReparcelamento), dataVencimentoNova);
					
					if ( detalhe.isParcelaPaga()) {
						
						if ( CommonsUtil.mesmoValor(BigInteger.ZERO, numeroParcelaReparcelamento)) {
							detalhe.setDataVencimento(dataParcela);
							detalhe.setVlrSaldoInicial(saldoAnterior);
							//detalhe.setVlrSaldoParcela(parcela.getSaldoDevedorInicial());
							
							if (contratoCobrancaMB.getObjetoContratoCobranca().isCorrigidoNovoIPCA()) {
								detalhe.setVlrSaldoParcela(parcela.getSaldoDevedorFinal().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							} else {
								detalhe.setVlrSaldoParcela(parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							}
						}
						
						encontrouParcela = true;
						if ( CommonsUtil.mesmoValor(BigDecimal.ZERO, parcela.getValorParcela()))
							break;		
						
						if ( CommonsUtil.mesmoValor(BigDecimal.ZERO, detalhe.getVlrParcela()))
							detalhe.setParcelaPaga(false);
					}
					
					
					if (detalhe.getDataVencimentoAtual().compareTo(detalhe.getDataVencimento()) < 1) {
						detalhe.setDataVencimentoAtual(dataParcela);
					}
					
					detalhe.setDataVencimento(dataParcela);
					
					detalhe.setVlrSaldoInicial(saldoAnterior);
					//detalhe.setVlrSaldoParcela(
					//parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					
					if (contratoCobrancaMB.getObjetoContratoCobranca().isCorrigidoNovoIPCA()) {
						detalhe.setVlrSaldoParcela(parcela.getSaldoDevedorFinal().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					} else {
						detalhe.setVlrSaldoParcela(parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					}
					
					detalhe.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setVlrAmortizacaoParcela(parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setSeguroDFI(parcela.getSeguroDFI());
					detalhe.setSeguroMIP(parcela.getSeguroMIP());
					detalhe.setTaxaAdm(parcela.getTxAdm());
					if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
						detalhe.setParcelaPaga(true);
						detalhe.setDataPagamento(detalhe.getDataVencimento());
						detalhe.setVlrParcela(BigDecimal.ZERO);
					}
					
					if (DateUtil.isAfterDate(detalhe.getDataVencimento(), DateUtil.getDataHoje()) && !detalhe.isParcelaPaga()) {
						detalhe.setParcelaVencida(true);
					}else 
						detalhe.setParcelaVencida(false);

					if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalhe.isParcelaPaga()) {
						detalhe.setParcelaVencendo(true);
					}else 
						detalhe.setParcelaVencendo(false);
					
					if (!CommonsUtil.semValor(detalhe.getIpca()) ) {
						detalhe.setIpca(null);
						contratoCobrancaMB.calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao,detalhe);						
					}
					
					encontrouParcela = true;
					break;
				}
				saldoAnterior = detalhe.getVlrSaldoParcela();
			}
			if (!encontrouParcela) {
				contratoCobrancaMB.getObjetoContratoCobranca().getListContratoCobrancaDetalhes()
						.add(contratoCobrancaMB.criaContratoCobrancaDetalhe(contratoCobrancaDao, parcela, dataVencimentoNova, saldoAnterior, "legado"));
			}

			ultimaParcela = parcela.getNumeroParcela();
		}
		
		// valida se tem parcela para se retirada, tem que ser ao contrario o for
		for (Integer iDetalhe = contratoCobrancaMB.getObjetoContratoCobranca().getListContratoCobrancaDetalhes().size()
				- 1; iDetalhe >= 0; iDetalhe--) {
			ContratoCobrancaDetalhes detalhe = contratoCobrancaMB.getObjetoContratoCobranca().getListContratoCobrancaDetalhes()
					.get(iDetalhe);
			if (!CommonsUtil.mesmoValor(detalhe.getNumeroParcela(), "Armotização") && !detalhe.isParcelaPaga()) {
				if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > ultimaParcela.intValue()) {
					contratoCobrancaMB.getObjetoContratoCobranca().getListContratoCobrancaDetalhes().remove(detalhe);
				} 
			}
		}	
		
		contratoCobrancaDao.merge(contratoCobrancaMB.getObjetoContratoCobranca());
	}
	
	public Date getDataComMesAnterior(Date dataOriginal) {
		Date dataRetorno = new Date();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		
		Calendar calendar = Calendar.getInstance(zone, locale);	
		
		calendar.setTime(dataOriginal);
		calendar.add(Calendar.MONTH, -1);		
		calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	public Date getDataComAcrescimoDeMes(Date dataOriginal) {
		Date dataRetorno = new Date();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		
		Calendar calendar = Calendar.getInstance(zone, locale);	
		
		calendar.setTime(dataOriginal);
		calendar.add(Calendar.MONTH, 1);		
		//calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	public void excluirIPCA() {
		FacesContext context = FacesContext.getCurrentInstance();
		IPCADao ipcaDao = new IPCADao();		
		
		removerTaxaIPCA();

		ipcaDao.delete(this.selectedIPCA);
			
		clearFieldsIPCA();
			
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "[IPCA] Taxa excluída com sucesso!", ""));
	}
	
	
	public void removerTaxaIPCA() {		
		// busca contratos com check corrigidoIPCA
		IPCADao IPCADao = new IPCADao();
		List<ContratoCobrancaDetalhes> listaParcelas = new ArrayList<ContratoCobrancaDetalhes>();
		
		// Prepara as datas para consulta das parcelas
		// a primeira data deve ser o dia 14 do mês seguinte ao inserido na taxa
		Date dataInicioConsulta = getDataComAcrescimoDeMes(this.selectedIPCA.getData());
		// a segunda data deve ser o dia 14 do mês seguinte a data inicio
		//Date dataFimConsulta = getDataComAcrescimoDeMes(dataInicioConsulta);
		
		//ATUALIZA PARCELAS DO MES VIGENTE
		listaParcelas = IPCADao.getContratosPorInvestidorInformeRendimentos(dataInicioConsulta);
		
		if (listaParcelas.size() > 0) {
			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			
			for (ContratoCobrancaDetalhes parcela : listaParcelas) {
				if (parcela.getIpca() != null) {
					parcela.setVlrParcela(parcela.getVlrParcela().subtract(parcela.getIpca()));
					parcela.setIpca(null);
					
					// persistir parcela
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					contratoCobrancaDetalhesDao.merge(parcela);				
				}
			}
		}
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public List<IPCA> getListIPCA() {
		return listIPCA;
	}

	public void setListIPCA(List<IPCA> listIPCA) {
		this.listIPCA = listIPCA;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getTaxa() {
		return taxa;
	}

	public void setTaxa(BigDecimal taxa) {
		this.taxa = taxa;
	}

	public IPCA getSelectedIPCA() {
		return selectedIPCA;
	}

	public void setSelectedIPCA(IPCA selectedIPCA) {
		this.selectedIPCA = selectedIPCA;
	}

	public ContratoCobrancaMB getContratoCobrancaMB() {
		return contratoCobrancaMB;
	}

	public void setContratoCobrancaMB(ContratoCobrancaMB contratoCobrancaMB) {
		this.contratoCobrancaMB = contratoCobrancaMB;
	}
}
