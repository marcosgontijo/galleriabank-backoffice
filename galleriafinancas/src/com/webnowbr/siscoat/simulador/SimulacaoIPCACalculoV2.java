package com.webnowbr.siscoat.simulador;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.SiscoatConstants;

@ManagedBean(name = "simulacaoIPCACalculoV2")
public class SimulacaoIPCACalculoV2 {
	
	private Date dataInicio;
	private BigInteger carencia;
	private BigInteger prazo;
	private BigDecimal taxaJuros;
	private BigDecimal valorCredito;
	private BigDecimal valorImovel;
	private BigDecimal seguroMIP = SiscoatConstants.SEGURO_MIP;
	private BigDecimal seguroDFI = SiscoatConstants.SEGURO_DFI;
	private BigDecimal txAdm;

	private boolean calculaTxAdm;	
	private boolean calculaSeguroDFI;
	private boolean calculaSeguroMIP;
	
	List<SimulacaoIPCADadosV2> listSimulacaoIPCADadosV2;
	
	public SimulacaoIPCACalculoV2() {
	
	}
	
	public void loadDados() {		
		this.listSimulacaoIPCADadosV2 = new ArrayList<SimulacaoIPCADadosV2>();
		
		SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			this.dataInicio = formataData.parse("05/08/2021");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.taxaJuros = BigDecimal.valueOf(1.19);
		this.carencia = new BigInteger("2");
		this.prazo = new BigInteger("120");
		this.valorCredito = new BigDecimal("2000000.00");
		this.valorImovel = new BigDecimal("8172000.00");
		this.seguroMIP = SiscoatConstants.SEGURO_MIP;
		this.seguroDFI = SiscoatConstants.SEGURO_DFI;
	}
	
	public void calcularPriceIPCANovo() {
		//loadDados();
		this.listSimulacaoIPCADadosV2 = new ArrayList<SimulacaoIPCADadosV2>();
		
		SimulacaoIPCADadosV2 simulacaoIPCADadosV2 = new SimulacaoIPCADadosV2();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataParcelas = Calendar.getInstance(zone, locale);		
		dataParcelas.setTime(this.dataInicio);
		
		// percorre o prazo
		int numeroParcela = 0;
		BigDecimal saldoDevedorAtualizado = this.valorCredito;
		boolean primeiraParcelaPosCarencia = false;
		
		BigDecimal taxaAdmMensal = new BigDecimal(25.00);
		
		for (int i = 0; i <= this.prazo.intValue(); i++) {
			simulacaoIPCADadosV2 = new SimulacaoIPCADadosV2();
			BigDecimal juros = BigDecimal.ZERO;
			BigDecimal ipcaMesReferencia = BigDecimal.ZERO;
			
			simulacaoIPCADadosV2.setNumeroParcela(BigInteger.valueOf(numeroParcela));
			
			simulacaoIPCADadosV2.setTaxaADM(BigDecimal.ZERO);
						
			// se primeiro registro apenas registra a linha 0
			if (i == 0) {
				simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());
				simulacaoIPCADadosV2.setSaldoDevedorInicial(this.valorCredito);
				simulacaoIPCADadosV2.setSaldoDevedorFinal(this.valorCredito);
				
				simulacaoIPCADadosV2.setJuros(BigDecimal.ZERO);
				simulacaoIPCADadosV2.setAmortizacao(BigDecimal.ZERO);
				simulacaoIPCADadosV2.setValorParcela(BigDecimal.ZERO);
				
				simulacaoIPCADadosV2.setIpca(BigDecimal.ZERO);
				
				simulacaoIPCADadosV2.setSeguroDFI(BigDecimal.ZERO);
				simulacaoIPCADadosV2.setSeguroMIP(BigDecimal.ZERO);
				
			} else {
				ipcaMesReferencia = getIPCAMes(dataParcelas.getTime());
				simulacaoIPCADadosV2.setTaxaIPCA(ipcaMesReferencia);
				
				simulacaoIPCADadosV2.setTaxaADM(BigDecimal.ZERO);
				
				// tratamos os meses de carência
				if (numeroParcela <= this.carencia.intValue()) {			
					
					simulacaoIPCADadosV2.setValorParcela(BigDecimal.ZERO);
					simulacaoIPCADadosV2.setSeguroDFI(BigDecimal.ZERO);
					simulacaoIPCADadosV2.setSeguroMIP(BigDecimal.ZERO);
					
					simulacaoIPCADadosV2.setSaldoDevedorInicial(saldoDevedorAtualizado);
					
					juros = saldoDevedorAtualizado.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));
					
					BigDecimal valorJurosIPCA = saldoDevedorAtualizado.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
										
					// atualiza saldo incial com o IPCA do mês anterior
					saldoDevedorAtualizado = saldoDevedorAtualizado.add(valorJurosIPCA).add(juros);

					simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());					

					simulacaoIPCADadosV2.setIpca(valorJurosIPCA);
					simulacaoIPCADadosV2.setJuros(juros);
					simulacaoIPCADadosV2.setAmortizacao(juros.negate());
					
					//saldoDevedorAtualizado = saldoDevedorAtualizado.subtract(simulacaoIPCADadosV2.getAmortizacao());
					
					simulacaoIPCADadosV2.setSaldoDevedorFinal(saldoDevedorAtualizado);
				} else {
					// tratamos as parcelas normais pós-carência					
					simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());
					
					// Saldo inicial é o Final da última parcela da carência
					simulacaoIPCADadosV2.setSaldoDevedorInicial(saldoDevedorAtualizado);
					
					// Valor parcela
					
					BigDecimal valorJurosIPCA = saldoDevedorAtualizado.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
					
					BigDecimal parcelaPGTOJurosIPCA = BigDecimal
							.valueOf(FinanceLib.pmt(this.taxaJuros.divide(BigDecimal.valueOf(100)).doubleValue(), // taxa
									this.prazo.subtract(BigInteger.valueOf(numeroParcela - 1)).intValue(), // (Prazo - Número da Parcela Anterior)
									saldoDevedorAtualizado.negate().doubleValue(), // valor credito - VP
									Double.valueOf("0"), // VF
									false // pagamento no inico
					));
					
					parcelaPGTOJurosIPCA = parcelaPGTOJurosIPCA.add(valorJurosIPCA);
					
					// Calcular DFI
					BigDecimal seguroDFI = BigDecimal.ZERO;
					
					// Calcular MIP
					BigDecimal seguroMPI = BigDecimal.ZERO;
					
					// Taxa Adm
					BigDecimal taxaAdm = BigDecimal.ZERO;
					
					// se primeira parcela pós carencia, calcula com ipca acumulado
					if (!primeiraParcelaPosCarencia) {
						primeiraParcelaPosCarencia = true;
						
						BigDecimal seguroDFIAcumulado = BigDecimal.ZERO;
						BigDecimal seguroMPIAcumulado = BigDecimal.ZERO;
						BigDecimal taxaAdmAcumulada = BigDecimal.ZERO;
						
						// percorre todas as parcelas da carência
						for (SimulacaoIPCADadosV2 parcela : this.listSimulacaoIPCADadosV2) {
							// se número parcela maior que 0 soma ao seguro
							if (parcela.getNumeroParcela().compareTo(BigInteger.ZERO) == 1) {
								if (this.calculaSeguroDFI) {
									seguroDFIAcumulado = seguroDFIAcumulado.add((this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(parcela.getTaxaIPCA())).divide(BigDecimal.valueOf(100)));
								}
								
								if (this.calculaSeguroMIP) {
									seguroMPIAcumulado = seguroMPIAcumulado.add((parcela.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(parcela.getTaxaIPCA())).divide(BigDecimal.valueOf(100)));
								}
								
								if (this.calculaTxAdm) {
									taxaAdmAcumulada = taxaAdmAcumulada.add(taxaAdmMensal);	
								}								
							}							
						}	
						
						// calcula com o ipca do mês referência (parcela atual)
						if (ipcaMesReferencia.compareTo(BigDecimal.ZERO) > 0) {
							if (this.calculaSeguroDFI) {
								seguroDFI = seguroDFIAcumulado.add((this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100)));
								seguroDFI = seguroDFI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
							}
							
							if (this.calculaSeguroMIP) {
								seguroMPI = seguroMPIAcumulado.add((simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100)));
								seguroMPI = seguroMPI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
							}
						} else {
							if (this.calculaSeguroDFI) {
								seguroDFI = seguroDFIAcumulado.add(this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS));
							}
							
							if (this.calculaSeguroMIP) {
								seguroMPI = seguroMPIAcumulado.add(simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS));
							}
						}
						
						// set parcela
						if (this.calculaTxAdm) {
							simulacaoIPCADadosV2.setValorParcela(parcelaPGTOJurosIPCA.add(seguroDFI).add(seguroMPI).add(taxaAdmAcumulada).add(taxaAdmMensal));
							
							simulacaoIPCADadosV2.setTaxaADM(taxaAdmAcumulada.add(taxaAdmMensal));
						} else {
							simulacaoIPCADadosV2.setValorParcela(parcelaPGTOJurosIPCA.add(seguroDFI).add(seguroMPI));
						}
					} else {
						// demais parcelas calcula com o IPCA do Mês
						if (ipcaMesReferencia.compareTo(BigDecimal.ZERO) > 0) {
							if (this.calculaSeguroDFI) {
								seguroDFI = (this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100));
								seguroDFI = seguroDFI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
							}
							
							if (this.calculaSeguroMIP) {
								seguroMPI = (simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100));
								seguroMPI = seguroMPI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
							}
						} else {
							if (this.calculaSeguroDFI) {
								seguroDFI = this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS);
							}
							
							if (this.calculaSeguroMIP) {
								seguroMPI = simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS);
							}
						}
						
						// set parcela
						if (this.calculaTxAdm) {
							simulacaoIPCADadosV2.setValorParcela(parcelaPGTOJurosIPCA.add(seguroDFI).add(seguroMPI).add(taxaAdmMensal));
							
							simulacaoIPCADadosV2.setTaxaADM(taxaAdmMensal);
						} else {
							simulacaoIPCADadosV2.setValorParcela(parcelaPGTOJurosIPCA.add(seguroDFI).add(seguroMPI));
						}
					}
					
					simulacaoIPCADadosV2.setSeguroDFI(seguroDFI);
					simulacaoIPCADadosV2.setSeguroMIP(seguroMPI);

					// ipca
					BigDecimal parcelaPGTOJuros = BigDecimal
							.valueOf(FinanceLib.pmt(this.taxaJuros.divide(BigDecimal.valueOf(100)).doubleValue(), // taxa
									this.prazo.subtract(BigInteger.valueOf(numeroParcela - 1)).intValue(), // (Prazo - Número da Parcela Anterior)
									saldoDevedorAtualizado.negate().doubleValue(), // valor credito - VP
									Double.valueOf("0"), // VF
									false // pagamento no inico
					));
					/*
					BigDecimal ipca = simulacaoIPCADadosV2.getValorParcela().subtract(parcelaPGTOJuros);
					
					if (this.calculaSeguroDFI) {
						ipca = ipca.subtract(seguroDFI);						
					}
					
					if (this.calculaSeguroMIP) {
						ipca = ipca.subtract(seguroMIP);
					}

					simulacaoIPCADadosV2.setIpca(ipca);
					*/
					simulacaoIPCADadosV2.setIpca(valorJurosIPCA);
										
					// juros
					juros = saldoDevedorAtualizado.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));
					simulacaoIPCADadosV2.setJuros(juros);
					
					// amortizacao
					BigDecimal amortizacao = parcelaPGTOJuros.subtract(juros);
					simulacaoIPCADadosV2.setAmortizacao(amortizacao);
					
					// saldo devedor atualizado
					saldoDevedorAtualizado = saldoDevedorAtualizado.subtract(amortizacao);
					simulacaoIPCADadosV2.setSaldoDevedorFinal(saldoDevedorAtualizado);
				}
			}
			
			dataParcelas.add(Calendar.MONTH, 1);
			numeroParcela = numeroParcela + 1;
			this.listSimulacaoIPCADadosV2.add(simulacaoIPCADadosV2);			
		}
		
		// zera saldo devedor final
		if (this.listSimulacaoIPCADadosV2.size() > 0) {
			this.listSimulacaoIPCADadosV2.get(this.listSimulacaoIPCADadosV2.size() -1).setSaldoDevedorFinal(BigDecimal.ZERO);
		}
	}
	
	public void calcularSACIPCANovo() {
		//loadDados();
		this.listSimulacaoIPCADadosV2 = new ArrayList<SimulacaoIPCADadosV2>();
		
		SimulacaoIPCADadosV2 simulacaoIPCADadosV2 = new SimulacaoIPCADadosV2();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataParcelas = Calendar.getInstance(zone, locale);		
		dataParcelas.setTime(this.dataInicio);
		
		// percorre o prazo
		int numeroParcela = 0;
		BigDecimal saldoDevedorAtualizado = this.valorCredito;
		boolean primeiraParcelaPosCarencia = false;
		
		BigDecimal taxaAdmMensal = new BigDecimal(25.00);
		
		BigInteger prazoAmortizacao = this.prazo.subtract(this.carencia);
		
		BigDecimal valorAmortizacao = BigDecimal.ZERO;
		
		for (int i = 0; i <= this.prazo.intValue(); i++) {
			simulacaoIPCADadosV2 = new SimulacaoIPCADadosV2();
			BigDecimal juros = BigDecimal.ZERO;
			BigDecimal ipca = BigDecimal.ZERO;
			BigDecimal ipcaMesReferencia = BigDecimal.ZERO;
			
			simulacaoIPCADadosV2.setNumeroParcela(BigInteger.valueOf(numeroParcela));
			
			simulacaoIPCADadosV2.setTaxaADM(BigDecimal.ZERO);
						
			// se primeiro registro apenas registra a linha 0
			if (i == 0) {
				simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());
				simulacaoIPCADadosV2.setSaldoDevedorInicial(this.valorCredito);
				simulacaoIPCADadosV2.setSaldoDevedorFinal(this.valorCredito);
				
				simulacaoIPCADadosV2.setJuros(BigDecimal.ZERO);
				simulacaoIPCADadosV2.setAmortizacao(BigDecimal.ZERO);
				simulacaoIPCADadosV2.setValorParcela(BigDecimal.ZERO);
				
				
				simulacaoIPCADadosV2.setSeguroDFI(BigDecimal.ZERO);
				simulacaoIPCADadosV2.setSeguroMIP(BigDecimal.ZERO);
			} else {
				ipcaMesReferencia = getIPCAMes(dataParcelas.getTime());
				simulacaoIPCADadosV2.setTaxaIPCA(ipcaMesReferencia);
				ipca = BigDecimal.ZERO;
				
				// tratamos os meses de carência
				if (numeroParcela <= this.carencia.intValue()) {			
					
					simulacaoIPCADadosV2.setValorParcela(BigDecimal.ZERO);
					simulacaoIPCADadosV2.setSeguroDFI(BigDecimal.ZERO);
					simulacaoIPCADadosV2.setSeguroMIP(BigDecimal.ZERO);
					
					simulacaoIPCADadosV2.setSaldoDevedorInicial(saldoDevedorAtualizado);
										
					juros = saldoDevedorAtualizado.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));
					
					ipca = saldoDevedorAtualizado.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));					

					simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());

					simulacaoIPCADadosV2.setJuros(juros);
					
					simulacaoIPCADadosV2.setIpca(ipca);
					
					simulacaoIPCADadosV2.setAmortizacao(juros.add(ipca));
					
					// atualiza saldo incial com o IPCA do mês anterior
					saldoDevedorAtualizado = saldoDevedorAtualizado.add(simulacaoIPCADadosV2.getAmortizacao());
					
					simulacaoIPCADadosV2.setAmortizacao(simulacaoIPCADadosV2.getAmortizacao().negate());
					
					simulacaoIPCADadosV2.setSaldoDevedorFinal(saldoDevedorAtualizado);
				} else {
					// tratamos as parcelas normais pós-carência					

					simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());
					
					// Saldo inicial é o Final da última parcela da carência
					simulacaoIPCADadosV2.setSaldoDevedorInicial(saldoDevedorAtualizado);
					
					juros = saldoDevedorAtualizado.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));
					
					simulacaoIPCADadosV2.setJuros(juros);
					
					ipca = saldoDevedorAtualizado.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));					

					simulacaoIPCADadosV2.setIpca(ipca);
					
					// Calcular DFI
					BigDecimal seguroDFI = BigDecimal.ZERO;
					
					// Calcular MIP
					BigDecimal seguroMPI = BigDecimal.ZERO;
					
					// Taxa Adm
					BigDecimal taxaAdm = BigDecimal.ZERO;
					
					// se primeira parcela pós carencia, calcula com ipca acumulado
					if (!primeiraParcelaPosCarencia) {
						primeiraParcelaPosCarencia = true;
						
						valorAmortizacao = saldoDevedorAtualizado.divide(new BigDecimal(prazoAmortizacao),MathContext.DECIMAL128);
						
						BigDecimal seguroDFIAcumulado = BigDecimal.ZERO;
						BigDecimal seguroMPIAcumulado = BigDecimal.ZERO;
						BigDecimal taxaAdmAcumulada = BigDecimal.ZERO;
						
						// percorre todas as parcelas da carência
						for (SimulacaoIPCADadosV2 parcela : this.listSimulacaoIPCADadosV2) {
							// se número parcela maior que 0 soma ao seguro
							if (parcela.getNumeroParcela().compareTo(BigInteger.ZERO) == 1) {
								if (this.calculaSeguroDFI) {
									seguroDFIAcumulado = seguroDFIAcumulado.add((this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(parcela.getTaxaIPCA())).divide(BigDecimal.valueOf(100)));
								}
								
								if (this.calculaSeguroMIP) {
									seguroMPIAcumulado = seguroMPIAcumulado.add((parcela.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(parcela.getTaxaIPCA())).divide(BigDecimal.valueOf(100)));
								}
								
								if (this.calculaTxAdm) {
									taxaAdmAcumulada = taxaAdmAcumulada.add(taxaAdmMensal);	
								}								
							}							
						}	
						
						// calcula com o ipca do mês referência (parcela atual)
						if (this.calculaSeguroDFI) {
							seguroDFI = seguroDFIAcumulado.add((this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100)));
							seguroDFI = seguroDFI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
						}
						
						if (this.calculaSeguroMIP) {
							seguroMPI = seguroMPIAcumulado.add((simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100)));
							seguroMPI = seguroMPI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
						}
						
						// set parcela
						simulacaoIPCADadosV2.setAmortizacao(valorAmortizacao);
						
						simulacaoIPCADadosV2.setValorParcela(juros.add(ipca).add(valorAmortizacao));						
						
						if (this.calculaTxAdm) {
							simulacaoIPCADadosV2.setValorParcela(simulacaoIPCADadosV2.getValorParcela().add(seguroDFI).add(seguroMPI).add(taxaAdmAcumulada).add(taxaAdmMensal));
							
							simulacaoIPCADadosV2.setTaxaADM(taxaAdmAcumulada.add(taxaAdmMensal));
						} else {
							simulacaoIPCADadosV2.setValorParcela(simulacaoIPCADadosV2.getValorParcela().add(seguroDFI).add(seguroMPI));
						}
					} else {
						// demais parcelas calcula com o IPCA do Mês
						if (this.calculaSeguroDFI) {
							seguroDFI = (this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100));
							seguroDFI = seguroDFI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
						}
						
						if (this.calculaSeguroMIP) {
							seguroMPI = (simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100));
							seguroMPI = seguroMPI.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100)));
						}
						
						// set parcela
						simulacaoIPCADadosV2.setAmortizacao(valorAmortizacao);
						
						simulacaoIPCADadosV2.setValorParcela(juros.add(ipca).add(valorAmortizacao));
						
						if (this.calculaTxAdm) {						
							simulacaoIPCADadosV2.setValorParcela(simulacaoIPCADadosV2.getValorParcela().add(seguroDFI).add(seguroMPI).add(taxaAdmMensal));
							
							simulacaoIPCADadosV2.setTaxaADM(taxaAdmMensal);
						} else {
							simulacaoIPCADadosV2.setValorParcela(simulacaoIPCADadosV2.getValorParcela().add(seguroDFI).add(seguroMPI));
						}
					}
					

					saldoDevedorAtualizado = saldoDevedorAtualizado.subtract(valorAmortizacao);
										
					simulacaoIPCADadosV2.setSaldoDevedorFinal(saldoDevedorAtualizado);
					
					simulacaoIPCADadosV2.setSeguroDFI(seguroDFI);
					simulacaoIPCADadosV2.setSeguroMIP(seguroMPI);
				}
			}
			
			dataParcelas.add(Calendar.MONTH, 1);
			numeroParcela = numeroParcela + 1;
			this.listSimulacaoIPCADadosV2.add(simulacaoIPCADadosV2);			
		}
		
		// zera saldo devedor final
		if (this.listSimulacaoIPCADadosV2.size() > 0) {
			this.listSimulacaoIPCADadosV2.get(this.listSimulacaoIPCADadosV2.size() -1).setSaldoDevedorFinal(BigDecimal.ZERO);
		}
	}
	
	private BigDecimal getIPCAMes(Date dataReferencia) {
		// Transforma data para 2 meses antes
		// 0-Janeiro, 1-fevereiro, 2-Março ....
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar calendar = Calendar.getInstance(zone, locale);
		
		calendar.setTime(dataReferencia);
		String mesReferencia = "";
		
		// se subtrair 1 mês
		if (calendar.get(Calendar.MONTH) == 0) {
			mesReferencia = "11";
		} else {
			mesReferencia = String.valueOf(calendar.get(Calendar.MONTH) - 1);
		}
		
		String dataStr = calendar.get(Calendar.DAY_OF_MONTH) + "/" + mesReferencia + "/" + calendar.get(Calendar.YEAR);
		SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
		
		IPCADao ipcaDao = new IPCADao();
		BigDecimal taxaMes = BigDecimal.ZERO;
		
		try {
			taxaMes = ipcaDao.getTaxaIPCAMes(formataData.parse(dataStr));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (taxaMes == null) {
			taxaMes = BigDecimal.ZERO;
		}
		
		return taxaMes;
	}
	
	
	private Date getDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		
		return dataHoje.getTime();
	}
	
	private BigDecimal getSaldoDevedorCarencia() {
		BigDecimal saldoDevedorCarencia = BigDecimal
				.valueOf(FinanceLib.fv(this.taxaJuros.divide(BigDecimal.valueOf(100)).doubleValue(),
						this.carencia.intValue(), 0, this.valorCredito.negate().doubleValue(), false));
		return saldoDevedorCarencia;
	}

	public BigInteger getCarencia() {
		return carencia;
	}

	public void setCarencia(BigInteger carencia) {
		this.carencia = carencia;
	}

	public BigInteger getPrazo() {
		return prazo;
	}

	public void setPrazo(BigInteger prazo) {
		this.prazo = prazo;
	}

	public BigDecimal getTaxaJuros() {
		return taxaJuros;
	}

	public void setTaxaJuros(BigDecimal taxaJuros) {
		this.taxaJuros = taxaJuros;
	}

	public BigDecimal getValorCredito() {
		return valorCredito;
	}

	public void setValorCredito(BigDecimal valorCredito) {
		this.valorCredito = valorCredito;
	}

	public BigDecimal getValorImovel() {
		return valorImovel;
	}

	public void setValorImovel(BigDecimal valorImovel) {
		this.valorImovel = valorImovel;
	}

	public BigDecimal getSeguroMIP() {
		return seguroMIP;
	}

	public void setSeguroMIP(BigDecimal seguroMIP) {
		this.seguroMIP = seguroMIP;
	}

	public BigDecimal getSeguroDFI() {
		return seguroDFI;
	}

	public void setSeguroDFI(BigDecimal seguroDFI) {
		this.seguroDFI = seguroDFI;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public List<SimulacaoIPCADadosV2> getListSimulacaoIPCADadosV2() {
		return listSimulacaoIPCADadosV2;
	}

	public void setListSimulacaoIPCADadosV2(List<SimulacaoIPCADadosV2> listSimulacaoIPCADadosV2) {
		this.listSimulacaoIPCADadosV2 = listSimulacaoIPCADadosV2;
	}

	public boolean isCalculaSeguroDFI() {
		return calculaSeguroDFI;
	}

	public void setCalculaSeguroDFI(boolean calculaSeguroDFI) {
		this.calculaSeguroDFI = calculaSeguroDFI;
	}

	public boolean isCalculaSeguroMIP() {
		return calculaSeguroMIP;
	}

	public void setCalculaSeguroMIP(boolean calculaSeguroMIP) {
		this.calculaSeguroMIP = calculaSeguroMIP;
	}

	public BigDecimal getTxAdm() {
		return txAdm;
	}

	public void setTxAdm(BigDecimal txAdm) {
		this.txAdm = txAdm;
	}

	public boolean isCalculaTxAdm() {
		return calculaTxAdm;
	}

	public void setCalculaTxAdm(boolean calculaTxAdm) {
		this.calculaTxAdm = calculaTxAdm;
	}
}
