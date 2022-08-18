package com.webnowbr.siscoat.simulador;

import java.math.BigDecimal;
import java.math.BigInteger;
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
	
	public void calcularIPVAv2() {
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
		
		for (int i = 0; i <= this.prazo.intValue(); i++) {
			simulacaoIPCADadosV2 = new SimulacaoIPCADadosV2();
			BigDecimal juros = BigDecimal.ZERO;
			BigDecimal ipcaMesReferencia = BigDecimal.ZERO;
			
			simulacaoIPCADadosV2.setNumeroParcela(BigInteger.valueOf(numeroParcela));
						
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
				
				// tratamos os meses de carência
				if (numeroParcela <= this.carencia.intValue()) {			
					
					simulacaoIPCADadosV2.setValorParcela(BigDecimal.ZERO);
					simulacaoIPCADadosV2.setSeguroDFI(BigDecimal.ZERO);
					simulacaoIPCADadosV2.setSeguroMIP(BigDecimal.ZERO);
										
					// atualiza saldo incial com o IPCA do mês anterior
					saldoDevedorAtualizado = saldoDevedorAtualizado.add(saldoDevedorAtualizado.multiply(ipcaMesReferencia.divide(BigDecimal.valueOf(100))));

					simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());
					simulacaoIPCADadosV2.setSaldoDevedorInicial(saldoDevedorAtualizado);
					
					juros = saldoDevedorAtualizado.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));
					
					simulacaoIPCADadosV2.setJuros(juros);
					simulacaoIPCADadosV2.setAmortizacao(juros.negate());
					
					saldoDevedorAtualizado = saldoDevedorAtualizado.subtract(simulacaoIPCADadosV2.getAmortizacao());
					
					simulacaoIPCADadosV2.setSaldoDevedorFinal(saldoDevedorAtualizado);
				} else {
					// tratamos as parcelas normais pós-carência					

					simulacaoIPCADadosV2.setDataReferencia(dataParcelas.getTime());
					
					// Saldo inicial é o Final da última parcela da carência
					simulacaoIPCADadosV2.setSaldoDevedorInicial(saldoDevedorAtualizado);
					
					// Valor parcela
					BigDecimal taxaJurosIPCA = this.taxaJuros.add(ipcaMesReferencia);
					BigDecimal parcelaPGTOJurosIPCA = BigDecimal
							.valueOf(FinanceLib.pmt(taxaJurosIPCA.divide(BigDecimal.valueOf(100)).doubleValue(), // taxa
									this.prazo.subtract(BigInteger.valueOf(numeroParcela - 1)).intValue(), // (Prazo - Número da Parcela Anterior)
									saldoDevedorAtualizado.negate().doubleValue(), // valor credito - VP
									Double.valueOf("0"), // VF
									false // pagamento no inico
					));
					
					// Calcular DFI
					BigDecimal seguroDFI = BigDecimal.ZERO;
					
					// Calcular MIP
					BigDecimal seguroMPI = BigDecimal.ZERO;
					
					// se primeira parcela pós carencia, calcula com ipca acumulado
					if (!primeiraParcelaPosCarencia) {
						primeiraParcelaPosCarencia = true;
						
						BigDecimal seguroDFIAcumulado = BigDecimal.ZERO;
						BigDecimal seguroMPIAcumulado = BigDecimal.ZERO;
						
						// percorre todas as parcelas da carência
						for (SimulacaoIPCADadosV2 parcela : this.listSimulacaoIPCADadosV2) {
							// se número parcela maior que 0 soma ao seguro
							if (parcela.getNumeroParcela().compareTo(BigInteger.ZERO) == 1) {
								seguroDFIAcumulado = seguroDFIAcumulado.add((this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(parcela.getTaxaIPCA())).divide(BigDecimal.valueOf(100)));
								
								seguroMPIAcumulado = seguroMPIAcumulado.add((parcela.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(parcela.getTaxaIPCA())).divide(BigDecimal.valueOf(100)));								
							}							
						}	
						
						// calcula com o ipca do mês referência (parcela atual)
						seguroDFI = seguroDFIAcumulado.add((this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100)));
						
						seguroMPI = seguroMPIAcumulado.add((simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100)));
						
						// set parcela
						simulacaoIPCADadosV2.setValorParcela(parcelaPGTOJurosIPCA.add(seguroDFI).add(seguroMPI));
					} else {
						// demais parcelas calcula com o IPCA do Mês
						seguroDFI = (this.valorImovel.multiply(SiscoatConstants.SEGURO_DFI_6_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100));
						seguroMPI = (simulacaoIPCADadosV2.getSaldoDevedorInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS)).multiply(BigDecimal.valueOf(100).add(ipcaMesReferencia)).divide(BigDecimal.valueOf(100));
						
						// set parcela
						simulacaoIPCADadosV2.setValorParcela(parcelaPGTOJurosIPCA);
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
					BigDecimal ipca = simulacaoIPCADadosV2.getValorParcela().subtract(parcelaPGTOJuros);
					ipca = ipca.subtract(seguroDFI).subtract(seguroMPI);
					simulacaoIPCADadosV2.setIpca(ipca);
					
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
	
	private BigDecimal getIPCAMes(Date dataReferencia) {
		// Transforma data para 2 meses antes
		// 0-Janeiro, 1-fevereiro, 2-Março ....
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar calendar = Calendar.getInstance(zone, locale);
		
		calendar.setTime(dataReferencia);
		String dataStr = calendar.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(calendar.get(Calendar.MONTH) - 1) + "/" + calendar.get(Calendar.YEAR);
		SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
		
		IPCADao ipcaDao = new IPCADao();
		BigDecimal taxaMes = new BigDecimal("0.5");
		
		try {
			taxaMes = ipcaDao.getTaxaIPCAMes(formataData.parse(dataStr));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (taxaMes == null || taxaMes.compareTo(BigDecimal.ZERO) == 0) {
			taxaMes = new BigDecimal("0.5");
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
}
