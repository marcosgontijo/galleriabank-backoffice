package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;



public class PowerBiDao extends HibernateDao <PowerBiVO,Long> {

	private static final String POWER_BI= " select datacontrato, status,  inicioanalisedata, cadastroAprovadoData, cadastroAprovadoValor, agassinaturadata, aprovadodata, quantoprecisa, valorccb "
			+ " from cobranca.contratocobranca coco  ";
	
	@SuppressWarnings("unchecked")
	public PowerBiVO powerBiConsulta(Date data) {
		return (PowerBiVO) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PowerBiVO object = new PowerBiVO();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				int mes = data.getMonth(); 
				int ano = data.getYear(); 
				int dia = data.getDate(); 
				
				try {
					connection = getConnection();
					
					ps = connection.prepareStatement(POWER_BI);

					rs = ps.executeQuery();
					
					PowerBiVO powerBi = new PowerBiVO();
					
					int qtdCadastradas = 0;
					int qtdAnalisadas = 0;
					int qtdAssinadas = 0;
					int qtdRegistradas = 0;
					int qtdInicioAnalise = 0;
					
					BigDecimal vlrCadastradas = BigDecimal.ZERO;
					BigDecimal vlrInicioAnalise = BigDecimal.ZERO;
					BigDecimal vlrAnalisadas = BigDecimal.ZERO;
					BigDecimal vlrAssinadas = BigDecimal.ZERO;
					BigDecimal vlrRegistradas = BigDecimal.ZERO;
					
					while (rs.next()) {
						if (!CommonsUtil.semValor((rs.getDate("datacontrato")))) {
							if (!CommonsUtil.mesmoValor(rs.getString("status"), "Aprovado")) {
								if (CommonsUtil.mesmoValor(rs.getDate("datacontrato").getMonth(), mes)
										&& CommonsUtil.mesmoValor(rs.getDate("datacontrato").getYear(), ano)
										&& CommonsUtil.mesmoValor(rs.getDate("datacontrato").getDate(), dia)) {
									qtdCadastradas++;
									if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
										vlrCadastradas = vlrCadastradas.add(rs.getBigDecimal("quantoprecisa"));
									}
								}
							}
						}
						
						if (!CommonsUtil.semValor((rs.getDate("inicioanalisedata")))) {
							if (CommonsUtil.mesmoValor(rs.getDate("inicioanalisedata").getMonth(), mes)
									&& CommonsUtil.mesmoValor(rs.getDate("inicioanalisedata").getYear(), ano)
									&& CommonsUtil.mesmoValor(rs.getDate("inicioanalisedata").getDate(), dia)) {
								qtdInicioAnalise++;
								if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
									vlrInicioAnalise = vlrInicioAnalise.add(rs.getBigDecimal("quantoprecisa"));
								}
							}
						}
						
						if (!CommonsUtil.semValor((rs.getDate("cadastroAprovadoData")))) {
							if (CommonsUtil.mesmoValor(rs.getString("cadastroAprovadoValor"), "Aprovado")) {
								if (CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getMonth(), mes)
										&& CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getYear(), ano)
										&& CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getDate(), dia)) {
									qtdAnalisadas++;
									if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
										vlrAnalisadas = vlrAnalisadas.add(rs.getBigDecimal("quantoprecisa"));
									}
								}
							}
						}

						if (!CommonsUtil.semValor((rs.getDate("agassinaturadata")))) {
							if (CommonsUtil.mesmoValor(rs.getDate("agassinaturadata").getMonth(), mes)
									&& CommonsUtil.mesmoValor(rs.getDate("agassinaturadata").getYear(), ano)
									&& CommonsUtil.mesmoValor(rs.getDate("agassinaturadata").getDate(), dia)) {
								qtdAssinadas++;
								if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
									vlrAssinadas = vlrAssinadas.add(rs.getBigDecimal("quantoprecisa"));
								}
							}
						}

						if (!CommonsUtil.semValor((rs.getDate("aprovadodata")))) {
							if (CommonsUtil.mesmoValor(rs.getDate("aprovadodata").getMonth(), mes)
									&& CommonsUtil.mesmoValor(rs.getDate("aprovadodata").getYear(), ano)
									&& CommonsUtil.mesmoValor(rs.getDate("aprovadodata").getDate(), dia)) {
								qtdRegistradas++;
								if (!CommonsUtil.semValor(rs.getBigDecimal("valorccb"))) {
									vlrRegistradas = vlrRegistradas.add(rs.getBigDecimal("valorccb"));
								}
							}
						}		
					}
					
					powerBi.setNumeroOperacoesAssinadas(BigInteger.valueOf(qtdAssinadas));
					powerBi.setNumeroOperacoesInicioAnalise(BigInteger.valueOf(qtdInicioAnalise));
					powerBi.setNumeroOperacoesAnalisadas(BigInteger.valueOf(qtdAnalisadas));
					powerBi.setNumeroOperacoesCadastradas(BigInteger.valueOf(qtdCadastradas));
					powerBi.setNumeroOperacoesRegistradas(BigInteger.valueOf(qtdRegistradas));
					
					powerBi.setValorOperacoesAssinadas(vlrAssinadas);
					powerBi.setValorOperacoesInicioAnalise(vlrInicioAnalise);
					powerBi.setValorOperacoesCadastradas(vlrCadastradas);
					powerBi.setValorOperacoesRegistradas(vlrRegistradas);
					powerBi.setValorOperacoesAnalisadas(vlrAnalisadas);
					
					powerBi.setDataConsulta(data);
					
					object = powerBi;
						
				} finally {
					closeResources(connection, ps);
				}
				return object;
			}
		});
	}  
	
	private static final String PARCELAS_FIDC = " select cc.id from cobranca.contratocobranca cc "
			+ "	where empresa = 'FIDC GALLERIA' and status = 'Aprovado' ";
	
	private static final String PARCELAS_SECURITIZADORA = " select cc.id from cobranca.contratocobranca cc "
			+ "	where empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.'and status = 'Aprovado' ";
	
	@SuppressWarnings("unchecked")
	public DadosContratosVO dadosContratosConsulta(final Date data, final String empresa) {
		return (DadosContratosVO) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				DadosContratosVO object = new DadosContratosVO();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				TimeZone zone = TimeZone.getDefault();
				Locale locale = new Locale("pt", "BR");
				Calendar dataHoje = Calendar.getInstance(zone, locale);
				Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
				dataHoje.set(Calendar.HOUR_OF_DAY, 0);
				dataHoje.set(Calendar.MINUTE, 0);
				dataHoje.set(Calendar.SECOND, 0);
				dataHoje.set(Calendar.MILLISECOND, 0);
				Date dataAtual = dataHoje.getTime();
				
				int mes = data.getMonth(); 
				int ano = data.getYear(); 
				int dia = data.getDate();
				
				try {
					connection = getConnection();
					
					if(CommonsUtil.mesmoValor(empresa, "Securitizadora")) {
						ps = connection.prepareStatement(PARCELAS_SECURITIZADORA);
					} else if (CommonsUtil.mesmoValor(empresa, "Fidc")) {
						ps = connection.prepareStatement(PARCELAS_FIDC);
					}
				
					rs = ps.executeQuery();
					
					DadosContratosVO dadosSecuritizadora = new DadosContratosVO();
					
					int numeroContratosAtraso = 0;
					int numeroContratosPagas = 0;
					int numeroContratosQuitados = 0;
					
					BigDecimal vlrParcelasAtraso = BigDecimal.ZERO;
					BigDecimal vlrParcelasPagas = BigDecimal.ZERO;
					BigDecimal vlrParcelasQuitadas = BigDecimal.ZERO;
					
					BigDecimal vlrContratosQuitados = BigDecimal.ZERO;	
					BigDecimal vlrContratosPagos = BigDecimal.ZERO;
					BigDecimal vlrContratosAtraso = BigDecimal.ZERO;
					
					int prazoContrato = 1;
					BigDecimal valorPareclaPaga = BigDecimal.ZERO;
					
					String numeroContratoAtrasoAntigo = "asdasdasd";
					String numeroContratoPagoAntigo = "asdasdasd";
					String numeroContratoQuitadoAntigo = "asdasdasd";
					
					Collection<ContratoCobranca> contratosAtraso = new ArrayList<ContratoCobranca>();
					Collection<ContratoCobranca> contratosPagos = new ArrayList<ContratoCobranca>();
					Collection<ContratoCobranca> contratosQuitados = new ArrayList<ContratoCobranca>();
					
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					while (rs.next()) {
						ContratoCobranca contrato = contratoCobrancaDao.findById(rs.getLong("id"));
						valorPareclaPaga = BigDecimal.ZERO;
						prazoContrato = 1;
						if(CommonsUtil.mesmoValor(contrato.getNumeroContrato(), "08546")) {
							String aaa = "oi";
						}
						
						if (!CommonsUtil.semValor(contrato.getListContratoCobrancaDetalhes())) {

							for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
								BigDecimal valorParcela = BigDecimal.ZERO;
								if (!CommonsUtil.semValor(ccd.getVlrParcela())) {
									valorParcela = ccd.getVlrParcela();
								} else {
									if (!CommonsUtil.semValor(ccd.getSeguroDFI())) {
										valorParcela = valorParcela.add(ccd.getSeguroDFI());
									}
									if (!CommonsUtil.semValor(ccd.getSeguroMIP())) {
										valorParcela = valorParcela.add(ccd.getSeguroMIP());
									}
									if (!CommonsUtil.semValor(ccd.getVlrAmortizacaoParcela())) {
										valorParcela = valorParcela.add(ccd.getVlrAmortizacaoParcela());
									}
									if (!CommonsUtil.semValor(ccd.getVlrJurosParcela())) {
										valorParcela = valorParcela.add(ccd.getVlrJurosParcela());
									}
								}

								BigDecimal somaBaixas = BigDecimal.ZERO;
								if (ccd.isAmortizacao()) {
									somaBaixas = ccd.getVlrParcela();
								} else {
									for (ContratoCobrancaDetalhesParcial cBaixas : ccd
											.getListContratoCobrancaDetalhesParcial()) {
										ccd.setDataUltimoPagamento(cBaixas.getDataPagamento());
										somaBaixas = somaBaixas.add(cBaixas.getVlrRecebido());
									}
								}

								ccd.setValorTotalPagamento(somaBaixas);

								dataVencimentoParcela.setTime(ccd.getDataVencimento());
								dataVencimentoParcela.set(Calendar.HOUR_OF_DAY, 0);
								dataVencimentoParcela.set(Calendar.MINUTE, 0);
								dataVencimentoParcela.set(Calendar.SECOND, 0);
								dataVencimentoParcela.set(Calendar.MILLISECOND, 0);

								if (dataVencimentoParcela.getTime().before(data) && !ccd.isParcelaPaga()) {
									ccd.setParcelaVencida(true);
									vlrParcelasAtraso = vlrParcelasAtraso.add(valorParcela);
									if (!CommonsUtil.semValor(contrato.getValorCCB())) {
										vlrContratosAtraso = vlrContratosAtraso.add(contrato.getValorCCB());
									}

									if (!CommonsUtil.mesmoValor(numeroContratoAtrasoAntigo,
											contrato.getNumeroContrato())) {
										numeroContratoAtrasoAntigo = contrato.getNumeroContrato();
										numeroContratosAtraso++;
										contratosAtraso.add(contrato);
									}
								} else if (ccd.isParcelaPaga()) {
									if (!CommonsUtil.semValor(ccd.getDataUltimoPagamento())) {
										if (ccd.getDataUltimoPagamento().after(data)) {
											vlrParcelasAtraso = vlrParcelasAtraso.add(valorParcela);

											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosAtraso = vlrContratosAtraso.add(contrato.getValorCCB());
											}

											if (!CommonsUtil.mesmoValor(numeroContratoAtrasoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoAtrasoAntigo = contrato.getNumeroContrato();
												numeroContratosAtraso++;
												contratosAtraso.add(contrato);
											}
										} else if (CommonsUtil.mesmoValor(ccd.getDataUltimoPagamento().getMonth(), mes)
												&& CommonsUtil.mesmoValor(ccd.getDataUltimoPagamento().getYear(), ano)
												&& CommonsUtil.mesmoValor(ccd.getDataUltimoPagamento().getDate(),
														dia)) {
											valorPareclaPaga = valorParcela;
											if (!CommonsUtil.mesmoValor(numeroContratoPagoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoPagoAntigo = contrato.getNumeroContrato();
												numeroContratosPagas++;
												contratosPagos.add(contrato);
											}
											vlrParcelasPagas = vlrParcelasPagas.add(valorParcela);
											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosPagos = vlrContratosPagos.add(contrato.getValorCCB());
											}
											if (!CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "Amortização")) {
												if(CommonsUtil.mesmoValor(contrato.getListContratoCobrancaDetalhes().get(0).getNumeroParcela(), "0")){
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size() - 1
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												} else {
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size()
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												}
											}
										}
									} else if (!CommonsUtil.semValor(ccd.getDataPagamento())) {
										if (ccd.getDataPagamento().after(data)) {
											vlrParcelasAtraso = vlrParcelasAtraso.add(valorParcela);

											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosAtraso = vlrContratosAtraso.add(contrato.getValorCCB());
											}

											if (!CommonsUtil.mesmoValor(numeroContratoAtrasoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoAtrasoAntigo = contrato.getNumeroContrato();
												numeroContratosAtraso++;
												contratosAtraso.add(contrato);
											}
										} else if (CommonsUtil.mesmoValor(ccd.getDataPagamento().getMonth(), mes)
												&& CommonsUtil.mesmoValor(ccd.getDataPagamento().getYear(), ano)
												&& CommonsUtil.mesmoValor(ccd.getDataPagamento().getDate(), dia)) {
											valorPareclaPaga = valorParcela;
											if (!CommonsUtil.mesmoValor(numeroContratoPagoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoPagoAntigo = contrato.getNumeroContrato();
												numeroContratosPagas++;
												contratosPagos.add(contrato);
											}
											vlrParcelasPagas = vlrParcelasPagas.add(valorParcela);
											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosPagos = vlrContratosPagos.add(contrato.getValorCCB());
											}
											if (!CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "Amortização")) {
												if(CommonsUtil.mesmoValor(contrato.getListContratoCobrancaDetalhes().get(0).getNumeroParcela(), "0")){
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size() - 1
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												} else {
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size()
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												}
											}
										}
									}
								}
							}

							if (CommonsUtil.mesmoValor(prazoContrato, 0) && !CommonsUtil
									.mesmoValor(numeroContratoQuitadoAntigo, contrato.getNumeroContrato())) {
								numeroContratoQuitadoAntigo = contrato.getNumeroContrato();
								numeroContratosQuitados++;
								contratosQuitados.add(contrato);
								if (!CommonsUtil.semValor(contrato.getValorCCB())) {
									vlrContratosQuitados = vlrContratosQuitados.add(contrato.getValorCCB());
								}
								vlrParcelasQuitadas = vlrParcelasQuitadas.add(valorPareclaPaga);
							}
						}
					}
					
					dadosSecuritizadora.setNumeroContratosAtraso(numeroContratosAtraso);
					dadosSecuritizadora.setNumeroContratosPagas(numeroContratosPagas);
					dadosSecuritizadora.setNumeroContratosQuitados(numeroContratosQuitados);
					
					dadosSecuritizadora.setVlrContratosAtraso(vlrContratosAtraso);
					dadosSecuritizadora.setVlrContratosPagos(vlrContratosPagos);
					dadosSecuritizadora.setVlrContratosQuitados(vlrContratosQuitados);
					
					dadosSecuritizadora.setVlrParcelasAtraso(vlrParcelasAtraso);
					dadosSecuritizadora.setVlrParcelasPagas(vlrParcelasPagas);
					dadosSecuritizadora.setVlrParcelasQuitadas(vlrParcelasQuitadas);
					
					dadosSecuritizadora.setContratosAtraso(contratosAtraso);
					dadosSecuritizadora.setContratosPagos(contratosPagos);
					dadosSecuritizadora.setContratosQuitados(contratosQuitados);
					
					object = dadosSecuritizadora;
						
				} finally {
					closeResources(connection, ps);
				}
				return object;
			}
		});
	}  
	
	
}
