package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.db.dao.*;

public class DebenturesInvestidorDao extends HibernateDao<DebenturesInvestidor, Long> {
	
	private static final String QUERY_DEBENTURES_EMITIDAS_PART_1 = "select iddebenture, idcontrato from ("
			+ "select d.id iddebenture, c.id idcontrato from cobranca.DebenturesInvestidor d "
			+ "inner join cobranca.contratocobranca c on d.contrato = c.id "
			+ "where d.dataDebentures >= ? ::timestamp and d.dataDebentures <= ? ::timestamp ";
			
		private static final String QUERY_DEBENTURES_EMITIDAS_PART_2 = "select 0 iddebenture, ct.id idcontrato from cobranca.contratocobranca ct  "
			+ "inner join cobranca.responsavel res on ct.responsavel = res.id  "
			+ "where ct.status = 'Aprovado' "
			+ "and ct.pagador in (15, 34,14, 182, 417, 803) "
			+ "and ct.datainicio >= ? ::timestamp and ct.datainicio <= ? ::timestamp ";
			
	@SuppressWarnings("unchecked")
	public List<DebenturesInvestidor> getDebenturesEmitidasPorPeriodo(final Date dtRelInicio, final Date dtRelFim, final List<PagadorRecebedor> listInvestidores) {
		return (List<DebenturesInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DebenturesInvestidor> objects = new ArrayList<DebenturesInvestidor>();
				
				String query_QUERY_DEBENTURES_EMITIDAS = "";
				String query_INVESTIDORES_DEBENTURES = null;
				String query_INVESTIDORES_CONTRATO = "";
				String query_IDs_INVESTIDORES = null;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {				
					// prepara filtros de investidores
					for (PagadorRecebedor investidor : listInvestidores) {						
						if (query_IDs_INVESTIDORES == null) {
							query_IDs_INVESTIDORES = String.valueOf(investidor.getId()); 
						} else {
							query_IDs_INVESTIDORES = query_IDs_INVESTIDORES + ", " + investidor.getId();
						}
					}
					
					if (query_IDs_INVESTIDORES != null) {
						query_INVESTIDORES_DEBENTURES = " and d.recebedor in ( " + query_IDs_INVESTIDORES + ")";
						
						query_INVESTIDORES_CONTRATO = " and ( recebedor in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor2 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor3 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor4 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor5 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor6 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor7 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor8 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor9 in ( " + query_IDs_INVESTIDORES + ") or " +
						" recebedor10 in ( " + query_IDs_INVESTIDORES + ")) ";			
						
						query_QUERY_DEBENTURES_EMITIDAS = QUERY_DEBENTURES_EMITIDAS_PART_1 + query_INVESTIDORES_DEBENTURES + " union all " + QUERY_DEBENTURES_EMITIDAS_PART_2 + query_INVESTIDORES_CONTRATO;
					} else {
						query_QUERY_DEBENTURES_EMITIDAS =  QUERY_DEBENTURES_EMITIDAS_PART_1 + " union all " + QUERY_DEBENTURES_EMITIDAS_PART_2;
					}
										
					query_QUERY_DEBENTURES_EMITIDAS = query_QUERY_DEBENTURES_EMITIDAS + " ) debentures ";	
					
					query_QUERY_DEBENTURES_EMITIDAS = query_QUERY_DEBENTURES_EMITIDAS + "order by idcontrato desc";	
					
					connection = getConnection();

					ps = connection.prepareStatement(query_QUERY_DEBENTURES_EMITIDAS);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelFimSQL);
					
					rs = ps.executeQuery();
					
					ContratoCobrancaDao cDao = new ContratoCobrancaDao();
					ContratoCobranca c = new ContratoCobranca();
					DebenturesInvestidorDao dbDao = new DebenturesInvestidorDao();
					DebenturesInvestidor db = new DebenturesInvestidor();
					DebenturesInvestidor debenturesCompleta = new DebenturesInvestidor();

					while (rs.next()) {
						db = new DebenturesInvestidor();
						debenturesCompleta = new DebenturesInvestidor();
						c = new ContratoCobranca();

						c = cDao.findById(rs.getLong(2));
						
						// se tem cadastro de debentures (pagador diferente de Galleria)
						if (rs.getLong(1) > 0) {
							db = dbDao.findById(rs.getLong(1));
							
							debenturesCompleta.setDataDebentures(db.getDataDebentures());
							debenturesCompleta.setRecebedor(db.getRecebedor());
							debenturesCompleta.setValorDebenture(BigDecimal.valueOf(db.getQtdeDebentures() * 1000).setScale(2, BigDecimal.ROUND_UP));
							debenturesCompleta.setPrazo(c.getQtdeParcelas());
							
							debenturesCompleta.setContrato(c);
							
							if (c.getRecebedor() != null) {
								if (c.getRecebedor().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor1());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor1() != null) {
										if (c.getVlrFinalRecebedor1().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor1());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor1().get(
														c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor1()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}								
								}
							}
							
							if (c.getRecebedor2() != null) {
								if (c.getRecebedor2().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor2());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor2());
									
									if (c.getVlrFinalRecebedor2() != null) {
										if (c.getVlrFinalRecebedor2().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor2());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor2().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor2().get(
														c.getListContratoCobrancaParcelasInvestidor2().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor2()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor3() != null) {
								if (c.getRecebedor3().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor3());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor3());
									
									if (c.getVlrFinalRecebedor3() != null) {
										if (c.getVlrFinalRecebedor3().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor3());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor3().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor3().get(
														c.getListContratoCobrancaParcelasInvestidor3().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor3()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor4() != null) {
								if (c.getRecebedor4().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor4());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor4() != null) {
										if (c.getVlrFinalRecebedor4().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor4());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor4().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor4().get(
														c.getListContratoCobrancaParcelasInvestidor4().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor4()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor5() != null) {
								if (c.getRecebedor5().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor5());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor5() != null) {
										if (c.getVlrFinalRecebedor5().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor5());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor5().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor5().get(
														c.getListContratoCobrancaParcelasInvestidor5().size() - 1).getDataVencimento());
									}
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor5()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor6() != null) {
								if (c.getRecebedor6().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor6());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor6() != null) {
										if (c.getVlrFinalRecebedor6().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor6());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor6().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor6().get(
														c.getListContratoCobrancaParcelasInvestidor6().size() - 1).getDataVencimento());
									}
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor6()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor7() != null) {
								if (c.getRecebedor7().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor7());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor7() != null) {
										if (c.getVlrFinalRecebedor7().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor7());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor7().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor7().get(
														c.getListContratoCobrancaParcelasInvestidor7().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor7()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor8() != null) {
								if (c.getRecebedor8().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor8());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor8() != null) {
										if (c.getVlrFinalRecebedor8().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor8());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor8().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor8().get(
														c.getListContratoCobrancaParcelasInvestidor8().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor8()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor9() != null) {
								if (c.getRecebedor9().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor9());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor9() != null) {
										if (c.getVlrFinalRecebedor9().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor9());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor9().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor9().get(
														c.getListContratoCobrancaParcelasInvestidor9().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor9()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
							
							if (c.getRecebedor10() != null) {
								if (c.getRecebedor10().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor10());
									debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
									
									if (c.getVlrFinalRecebedor10() != null) {
										if (c.getVlrFinalRecebedor10().compareTo(BigDecimal.ZERO) == 1) {
											debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor10());
										}
									}
									
									if (c.getListContratoCobrancaParcelasInvestidor10().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor10().get(
														c.getListContratoCobrancaParcelasInvestidor10().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor10()) {
										if (!parcelas.isBaixado()) {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
								}
							}
						} else {		
							// se pagador for galleria
							debenturesCompleta.setDataDebentures(c.getDataInicio());
							debenturesCompleta.setPrazo(c.getQtdeParcelas());
							
							if (c.getRecebedor() != null) {
								debenturesCompleta.setContrato(c);
								
								debenturesCompleta.setRecebedor(c.getRecebedor());
								debenturesCompleta.setValorDebenture(c.getValorCCB());
								debenturesCompleta.setParcelaFinal(c.getVlrFinalRecebedor1());
								
								debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor1());
								debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());
								
								if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
									debenturesCompleta.setDataUltimaParcela(
											c.getListContratoCobrancaParcelasInvestidor1().get(
													c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getDataVencimento());
								}	
								
								// Verifica se estão quitadas todas as parcelas
								boolean quitado = true;								
								for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor1()) {
									if (!parcelas.isBaixado()) {
										quitado = false;
										break;
									}
								}
								if (quitado) {
									debenturesCompleta.setQuitado("Sim");
								} else {
									debenturesCompleta.setQuitado("Não");
								}								
							}
						}

						objects.add(debenturesCompleta);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}

	private static final String QUERY_TITULOS_QUITADOS = "select d.id from cobranca.DebenturesInvestidor d "
			+ "where d.dataDebentures >= ? ::timestamp and d.dataDebentures <= ? ::timestamp ";

	@SuppressWarnings("unchecked")
	public List<DebenturesInvestidor> getDebenturesPorPeriodo(final Date dtRelInicio, final Date dtRelFim) {
		return (List<DebenturesInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DebenturesInvestidor> objects = new ArrayList<DebenturesInvestidor>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_TITULOS_QUITADOS);

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					rs = ps.executeQuery();

					DebenturesInvestidorDao dbDao = new DebenturesInvestidorDao();
					DebenturesInvestidor db = new DebenturesInvestidor();

					while (rs.next()) {
						db = new DebenturesInvestidor();

						db = dbDao.findById(rs.getLong(1));

						objects.add(db);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	
	private static final String QUERY_GET_DRE_DEBENTURES = "select " 
			+ "       dataDebentures,"
			+ "       contrato, "
			+ "       numeroCautela, "
			+ "       pare.nome,"
			+ "       qtdedebentures "
			+ " from cobranca.DebenturesInvestidor dein "
			+ " inner join cobranca.pagadorrecebedor pare on dein.recebedor = pare.id"
	 + " where dataDebentures between ? ::timestamp and  ? ::timestamp"
	 + " order by dataDebentures;";	

	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreDebentures(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
		demonstrativosResultadosGrupoDetalhe
				.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
		demonstrativosResultadosGrupoDetalhe.setTipo("Debêntures emitidas");
		demonstrativosResultadosGrupoDetalhe.setCodigo(4);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query_QUERY_GET_DRE_SAIDAS = QUERY_GET_DRE_DEBENTURES;

			ps = connection.prepareStatement(query_QUERY_GET_DRE_SAIDAS);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();

				demonstrativoResultadosGrupoDetalhe.setIdContratoCobranca(rs.getLong("contrato"));
				demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroCautela"));
				demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
				Date dataContrato = rs.getDate("dataDebentures");
				demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataContrato);
				demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("qtdedebentures").multiply(BigDecimal.valueOf(1000)));

					demonstrativosResultadosGrupoDetalhe.getDetalhe()
							.add(demonstrativoResultadosGrupoDetalhe);
					demonstrativosResultadosGrupoDetalhe
							.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return demonstrativosResultadosGrupoDetalhe;
	}
	
	
}
