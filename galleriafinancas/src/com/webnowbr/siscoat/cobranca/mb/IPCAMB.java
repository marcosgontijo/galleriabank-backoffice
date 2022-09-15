package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
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
import com.webnowbr.siscoat.job.IpcaJobCalcular;
import com.webnowbr.siscoat.job.IpcaJobContrato;


@ManagedBean(name = "ipcaMB")
@SessionScoped

public class IPCAMB {
	private List<IPCA> listIPCA;
	private Date data;
	private BigDecimal taxa;	
	private IPCA selectedIPCA;
	
	private final IpcaJobCalcular ipcaJobCalcular; 
	
	private static final Log LOGGER = LogFactory.getLog(IpcaJobContrato.class);
	
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
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();
			
			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findByFilter("numeroContrato", numeroContrato);

			LOGGER.info("incio atualizaIPCAPorContrato");
			
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
			}
			//contratoCobranca = contratoCobrancaDao.findById(contratoCobranca.getId());
				
			LOGGER.info("Fim atualizaIPCAPorContrato");

		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
		}
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
}
