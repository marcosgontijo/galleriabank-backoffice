package com.webnowbr.siscoat.cobranca.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesParcialDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.job.IpcaJobCalcular;
import com.webnowbr.siscoat.job.IpcaJobContrato;

import br.com.galleriabank.debit.cliente.model.indice.Indice;
import br.com.galleriabank.debit.cliente.model.indice.TabelaIndices;

public class IpcaService {

	private static final Log LOGGER = LogFactory.getLog(IpcaService.class);

	public void verificaNovoIPCA() {

		IPCADao ipcaDao = new IPCADao();

		DebitService debitService = new DebitService();
		TabelaIndices tabelaIndices = debitService.criarConsultaTabela("ipca");

		// este método pegara o último IPCA na base, com data anterior a data base,
		// mesmo que não do mesmo mês.
		
		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(DateUtil.getDataHoje());

		if (!CommonsUtil.semValor(tabelaIndices.getIndices())) {

			List<Indice> listNovosIpca = tabelaIndices.getIndices().stream()
					.filter(i -> i.getDataAsDate() != null && i.getDataAsDate().compareTo(ultimoIpca.getData()) > 0)
					.collect(Collectors.toList());

			for (Indice ultimoIndiceHistorico : listNovosIpca) {
				IPCA ipca = new IPCA();
				ipca.setData(ultimoIndiceHistorico.getDataAsDate());
				if (ultimoIndiceHistorico.getValor().compareTo(BigDecimal.ZERO) > 0)
					ipca.setTaxa(ultimoIndiceHistorico.getValor());
				else
					ipca.setTaxa(BigDecimal.ZERO);
				ipcaDao.create(ipca);
			}

		}

	}

	public String atualizaIPCAPorContrato(ContratoCobranca contratoCobranca, Date dataCorteBaixa ,IpcaJobCalcular ipcaJobCalcular) {
		try {
			
			IPCADao ipcaDao = new IPCADao();

			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();
			
//			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findByFilter("numeroContrato", numeroContrato);

			verificaNovoIPCA();
			LOGGER.info("incio atualizaIPCAPorContrato");
			
//			if (contratosCobranca.size() > 0) {		
				if (!contratoCobranca.isCorrigidoNovoIPCA()) {		
//					for (ContratoCobranca contratoCobranca : contratosCobranca) {
						
						contratoCobranca.setRecalculaIPCA(true);
						
						for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {
							if (CommonsUtil.mesmoValor(contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe).getNumeroParcela() , "0") )
								continue;
							
							try {
								if (!ipcaJobCalcular.calcularIPCACustom(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, 
										contratoCobrancaDetalhesParcialDao, contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe), contratoCobranca))
									break;
							} catch (Exception e) {
								LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
								return e.getMessage();
							}
						}
						
						contratoCobranca.setRecalculaIPCA(false);
						contratoCobrancaDao.merge(contratoCobranca);
						LOGGER.info("Fim atualizaIPCAPorContrato");
						return "[Reprocessamento IPCA] Contrato " + contratoCobranca.getNumeroContrato() + " reprocessado com sucesso!";						
				} else {
					LOGGER.info("Fim atualizaIPCAPorContrato");
					return "[Reprocessamento IPCA] Este reprocessamento não é permitido para contratos corrigidos pelo NOVO IPCA!";					
				}
//			}
		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
		}
		return "[Reprocessamento IPCA] Este reprocessamento não é permitido para contratos corrigidos pelo NOVO IPCA!";	
	}

	public String atualizaIPCAPorContratoMaluco(ContratoCobranca contratoCobranca, Date dataCorteBaixa,
			IpcaJobCalcular ipcaJobCalcular) {
		try {
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();

//			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findByFilter("numeroContrato", this.numeroContrato);

			verificaNovoIPCA();
			
			LOGGER.info("incio atualizaIPCAPorContrato");

//			if (contratosCobranca.size() > 0) {			
//					for (ContratoCobranca contratoCobranca : contratosCobranca) {

			contratoCobranca.setRecalculaIPCA(true);

			contratoCobranca.setCorrigidoIPCA(true);
			contratoCobranca.setCorrigidoNovoIPCA(false);
			contratoCobranca.setCorrigidoIPCAHibrido(true);

			for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {
				if (CommonsUtil.mesmoValor(
						contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe).getNumeroParcela(), "0"))
					continue;

				try {
					if (!ipcaJobCalcular.calcularIPCACustomMaluco(ipcaDao, contratoCobrancaDetalhesDao,
							contratoCobrancaDao, contratoCobrancaDetalhesParcialDao,
							contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe), contratoCobranca,
							dataCorteBaixa))
						break;
				} catch (Exception e) {
					LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
					continue;
				}
			}

			contratoCobranca.setRecalculaIPCA(false);
			contratoCobrancaDao.merge(contratoCobranca);

			// }
			// }
			// contratoCobranca = contratoCobrancaDao.findById(contratoCobranca.getId());

			LOGGER.info("Fim atualizaIPCAPorContrato");
			

		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
			return "[Reprocessamento IPCA] Contrato " + contratoCobranca.getNumeroContrato()
			+ " " +  e.getMessage();
		}
		return "[Reprocessamento IPCA] Contrato " + contratoCobranca.getNumeroContrato()
		+ " reprocessado com sucesso!";
	}
}
