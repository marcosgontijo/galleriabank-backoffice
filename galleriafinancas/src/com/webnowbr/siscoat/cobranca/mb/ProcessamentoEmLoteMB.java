package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ErrosProcessamentoEmLote;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ErrosProcessamentoEmLoteDao;

@ManagedBean(name = "processamentoEmLoteMB")
@SessionScoped
public class ProcessamentoEmLoteMB {
	List<ContratoCobranca> listContratos;
	BigDecimal txJurosCustom;
	String numeroContratoCustom;
	
	public String clearFieldsvaloresParcelasContratos() {
		this.txJurosCustom = BigDecimal.ZERO;
		this.numeroContratoCustom = null;
		
		return "/Manutencao/ProcessamentoEmLote.xhtml";
	}
	
	public void gravaContratoComErro(String descricao, String contrato) {
		ErrosProcessamentoEmLoteDao errosDao = new ErrosProcessamentoEmLoteDao();
		ErrosProcessamentoEmLote erro = new ErrosProcessamentoEmLote();
	
		erro = new ErrosProcessamentoEmLote();
		erro.setDataProcessamento(gerarDataHoje());
		erro.setDescricaoProcessamento(descricao);
		erro.setNumeroContrato(contrato);
		 
		errosDao.create(erro);
	}

	/***
	 * Efetua o cálculo de Juros, multa e amortização das parcelas de todos os contratos
	 * 
	 */		
	public void atualizaSaldoJurosAmortizacaoParcelasContratos() {
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = cDao.consultaContratosAprovados();
		
		// percorre todos os contratos 
		for (ContratoCobranca contrato : this.listContratos) {
			// Pega o valor da CCB
			BigDecimal saldoAtualizado = BigDecimal.ZERO;
			saldoAtualizado = contrato.getValorCCB();
			
			// verifica se o valor da CCB está preenchido
			// Só processa se valor maior que ZERO
			if (saldoAtualizado == null || saldoAtualizado.compareTo(BigDecimal.ZERO) == 0) {
				// popula a tabela de erro do sistema, contratos que não puderam ser processados
				gravaContratoComErro("[Valor CCB] Cálculo Juros, Multa e Amortização", contrato.getNumeroContrato());
			} else {
				BigDecimal txJurosParcela = BigDecimal.ZERO;
				if (contrato.getTxJurosParcelas() == null || contrato.getTxJurosParcelas().compareTo(BigDecimal.ZERO) == 0) {
					// popula a tabela de erro do sistema, contratos que não puderam ser processados
					gravaContratoComErro("[Taxa de Juros] Cálculo Juros, Multa e Amortização", contrato.getNumeroContrato());
				} else {
					/**
					 * calcula juros, amortização e saldo 
					 */							
					txJurosParcela = contrato.getTxJurosParcelas().divide(BigDecimal.valueOf(100));
					
					// se tem meses de carência calcula a carência				
					if (contrato.getMesesCarencia() > 0) {
						for (int i = 0; i < contrato.getMesesCarencia(); i++) {
							saldoAtualizado = saldoAtualizado.add(saldoAtualizado.multiply(txJurosParcela).setScale(2, RoundingMode.HALF_EVEN));
						}						
					} 
					
					for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
						parcela.setVlrJurosParcela(saldoAtualizado.multiply(txJurosParcela).setScale(2, RoundingMode.HALF_EVEN));
						parcela.setVlrAmortizacaoParcela(parcela.getVlrParcela().subtract(parcela.getVlrJurosParcela()));
						parcela.setVlrSaldoParcela(saldoAtualizado.subtract(parcela.getVlrAmortizacaoParcela()));
						
						saldoAtualizado = parcela.getVlrSaldoParcela();		
					}
					
					// se Saldo da última parcela for menor que R$ 1 zeramos o saldo
					BigDecimal saldoFinal = BigDecimal.ZERO;
					if (contrato.getListContratoCobrancaDetalhes().size() > 0) {
						saldoFinal = contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).getVlrSaldoParcela();
						
						if (saldoFinal.compareTo(BigDecimal.ONE) == -1) {
							contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).setVlrSaldoParcela(BigDecimal.ZERO);
						}						
					}

					// atualiza contrato
					cDao.merge(contrato);
				}
			}
		}		
	}
	
	/***
	 * Efetua o cálculo de Juros, multa e amortização das parcelas de todos os contratos
	 * 
	 */		
	public void atualizaSaldoJurosAmortizacaoParcelasContratosCustomizado() {
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		
		if (this.numeroContratoCustom.length() == 4) {
			this.listContratos = cDao.findByFilter("numeroContrato", "0" + this.numeroContratoCustom);
		} else {
			this.listContratos = cDao.findByFilter("numeroContrato", this.numeroContratoCustom);
		}		
		
		// percorre todos os contratos 
		for (ContratoCobranca contrato : this.listContratos) {
			// Pega o valor da CCB
			BigDecimal saldoAtualizado = BigDecimal.ZERO;
			saldoAtualizado = contrato.getValorCCB();
			
			// verifica se o valor da CCB está preenchido
			// Só processa se valor maior que ZERO
			if (saldoAtualizado == null || saldoAtualizado.compareTo(BigDecimal.ZERO) == 0) {
				// popula a tabela de erro do sistema, contratos que não puderam ser processados
				gravaContratoComErro("[Valor CCB] Cálculo Juros, Multa e Amortização", contrato.getNumeroContrato());
			} else {
				BigDecimal txJurosParcela = BigDecimal.ZERO;
				if (this.txJurosCustom == null || this.txJurosCustom.compareTo(BigDecimal.ZERO) == 0) {
					txJurosCustom = contrato.getTxJurosParcelas();
				}
				
				if (this.txJurosCustom == null || this.txJurosCustom.compareTo(BigDecimal.ZERO) == 0) {
					// popula a tabela de erro do sistema, contratos que não puderam ser processados
					gravaContratoComErro("[Taxa de Juros] Cálculo Juros, Multa e Amortização", contrato.getNumeroContrato());
				} else {
					/**
					 * calcula juros, amortização e saldo 
					 */							
					txJurosParcela = this.txJurosCustom.divide(BigDecimal.valueOf(100));
					
					// se tem meses de carência calcula a carência				
					if (contrato.getMesesCarencia() > 0) {
						for (int i = 0; i < contrato.getMesesCarencia(); i++) {
							saldoAtualizado = saldoAtualizado.add(saldoAtualizado.multiply(txJurosParcela));
						}						
					} 
					
					saldoAtualizado = saldoAtualizado.setScale(2, RoundingMode.HALF_EVEN);
					
					// Verifica se calcula PGTO ou segue com amortização zero
					BigDecimal parcelaCalculadaCCB = saldoAtualizado.multiply(txJurosParcela);
					
					//BigDecimal valorPArcela = new BigDecimal("2200.193838");

					// verifica se usa PGTO ou faz sem amortização
					if (contrato.getVlrParcela().compareTo(parcelaCalculadaCCB.subtract(BigDecimal.ONE)) >= 0  && 
							contrato.getVlrParcela().compareTo(parcelaCalculadaCCB.add(BigDecimal.ONE)) <= 0) {
						for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
							parcela.setVlrJurosParcela(saldoAtualizado.multiply(txJurosParcela));
							parcela.setVlrAmortizacaoParcela(BigDecimal.ZERO);
							parcela.setVlrSaldoParcela(saldoAtualizado);
							
							parcela.setVlrJurosParcela(parcela.getVlrJurosParcela().setScale(2, RoundingMode.HALF_EVEN));
							parcela.setVlrAmortizacaoParcela(parcela.getVlrAmortizacaoParcela().setScale(2, RoundingMode.HALF_EVEN));
							parcela.setVlrSaldoParcela(parcela.getVlrSaldoParcela().setScale(2, RoundingMode.HALF_EVEN));	
						}	
						
						// seta amortizacao e zera saldo
						BigDecimal saldoFinal = BigDecimal.ZERO;
						if (contrato.getListContratoCobrancaDetalhes().size() > 0) {
							contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).setVlrAmortizacaoParcela(contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).getVlrSaldoParcela());
							contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).setVlrSaldoParcela(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));										
						}
					} else {
						//PGTO
						Double parcelaPGTO = FinanceLib.pmt(txJurosParcela.doubleValue(), Double.valueOf(contrato.getListContratoCobrancaDetalhes().size()), saldoAtualizado.doubleValue(), Double.valueOf("0"), false);
						// Deixa positivo
						parcelaPGTO = parcelaPGTO - (parcelaPGTO * 2);	
						BigDecimal parcelaPGTOCalculada = new BigDecimal(parcelaPGTO);
						
						for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
							parcela.setVlrJurosParcela(saldoAtualizado.multiply(txJurosParcela));
							parcela.setVlrAmortizacaoParcela(parcelaPGTOCalculada.subtract(parcela.getVlrJurosParcela()));
							parcela.setVlrSaldoParcela(saldoAtualizado.subtract(parcela.getVlrAmortizacaoParcela()));
							
							saldoAtualizado = parcela.getVlrSaldoParcela();		
							
							parcela.setVlrJurosParcela(parcela.getVlrJurosParcela().setScale(2, RoundingMode.HALF_EVEN));
							parcela.setVlrAmortizacaoParcela(parcela.getVlrAmortizacaoParcela().setScale(2, RoundingMode.HALF_EVEN));
							parcela.setVlrSaldoParcela(parcela.getVlrSaldoParcela().setScale(2, RoundingMode.HALF_EVEN));							
						}
						
						// se Saldo da última parcela for menor que R$ 1 zeramos o saldo
						BigDecimal saldoFinal = BigDecimal.ZERO;
						if (contrato.getListContratoCobrancaDetalhes().size() > 0) {
							saldoFinal = contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).getVlrSaldoParcela();
							
							if (saldoFinal.compareTo(BigDecimal.ONE) == -1) {
								contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).setVlrSaldoParcela(BigDecimal.ZERO);
							}						
						}
					}
					
					// atualiza contrato
					cDao.merge(contrato);
				}
			}

		}		
	}	
	
	public double PV(float rate, float nper, float pmt)
	{
	    return pmt / rate * (1 - Math.pow(1 + rate, -nper));
	}

	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public BigDecimal getTxJurosCustom() {
		return txJurosCustom;
	}

	public void setTxJurosCustom(BigDecimal txJurosCustom) {
		this.txJurosCustom = txJurosCustom;
	}

	public String getNumeroContratoCustom() {
		return numeroContratoCustom;
	}

	public void setNumeroContratoCustom(String numeroContratoCustom) {
		this.numeroContratoCustom = numeroContratoCustom;
	}
}