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
import com.webnowbr.siscoat.common.CommonsUtil;
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
					if (listInvestidores != null) { 
						for (PagadorRecebedor investidor : listInvestidores) {						
							if (query_IDs_INVESTIDORES == null) {
								query_IDs_INVESTIDORES = String.valueOf(investidor.getId()); 
							} else {
								query_IDs_INVESTIDORES = query_IDs_INVESTIDORES + ", " + investidor.getId();
							}
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
							//debenturesCompleta.setValorDebenture(BigDecimal.valueOf(db.getQtdeDebentures() * 1000).setScale(2, BigDecimal.ROUND_UP));
							//debenturesCompleta.setPrazo(c.getQtdeParcelas());
							
							debenturesCompleta.setContrato(c);
							
							if (c.getRecebedor() != null) {
								if (c.getRecebedor().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor1());
									//debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());	
									
									if(!CommonsUtil.semValor(c.getVlrInvestidor1())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor1());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor1())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor1().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor1());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor1().get(
													c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor1().get(
														c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor1()) {
										
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor2() != null) {
								if (c.getRecebedor2().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor2());
									debenturesCompleta.setValorDebenture(c.getVlrInvestidor2());
									
									if(!CommonsUtil.semValor(c.getVlrInvestidor2())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor2());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor2());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor2())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor2().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor2());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor2().get(
													c.getListContratoCobrancaParcelasInvestidor2().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor2().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor2().get(
														c.getListContratoCobrancaParcelasInvestidor2().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor2()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor3() != null) {
								if (c.getRecebedor3().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor3());

									if(!CommonsUtil.semValor(c.getVlrInvestidor3())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor3());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor3());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor3())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor3().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor3());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor3().get(
													c.getListContratoCobrancaParcelasInvestidor3().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor3().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor3().get(
														c.getListContratoCobrancaParcelasInvestidor3().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor3()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor4() != null) {
								if (c.getRecebedor4().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor4());

									if(!CommonsUtil.semValor(c.getVlrInvestidor4())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor4());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor4());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor4())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor4().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor4());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor4().get(
													c.getListContratoCobrancaParcelasInvestidor4().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor4().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor4().get(
														c.getListContratoCobrancaParcelasInvestidor4().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor4()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor5() != null) {
								if (c.getRecebedor5().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor5());

									if(!CommonsUtil.semValor(c.getVlrInvestidor5())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor5());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor5());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor5())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor5().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor5());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor5().get(
													c.getListContratoCobrancaParcelasInvestidor5().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor5().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor5().get(
														c.getListContratoCobrancaParcelasInvestidor5().size() - 1).getDataVencimento());
									}
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor5()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor6() != null) {
								if (c.getRecebedor6().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor6());

									if(!CommonsUtil.semValor(c.getVlrInvestidor6())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor6());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor6());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor6())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor6().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor6());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor6().get(
													c.getListContratoCobrancaParcelasInvestidor6().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor6().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor6().get(
														c.getListContratoCobrancaParcelasInvestidor6().size() - 1).getDataVencimento());
									}
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor6()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor7() != null) {
								if (c.getRecebedor7().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor7());

									if(!CommonsUtil.semValor(c.getVlrInvestidor7())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor7());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor7());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor7())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor7().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor7());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor7().get(
													c.getListContratoCobrancaParcelasInvestidor7().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor7().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor7().get(
														c.getListContratoCobrancaParcelasInvestidor7().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor7()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor8() != null) {
								if (c.getRecebedor8().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor8());

									if(!CommonsUtil.semValor(c.getVlrInvestidor8())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor8());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor8());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor8())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor8().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor8());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor8().get(
													c.getListContratoCobrancaParcelasInvestidor8().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor8().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor8().get(
														c.getListContratoCobrancaParcelasInvestidor8().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor8()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor9() != null) {
								if (c.getRecebedor9().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor9());

									if(!CommonsUtil.semValor(c.getVlrInvestidor9())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor9());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor9());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor9())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor9().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor9());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor9().get(
													c.getListContratoCobrancaParcelasInvestidor9().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor9().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor9().get(
														c.getListContratoCobrancaParcelasInvestidor9().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor9()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor10() != null) {
								if (c.getRecebedor10().getId() == debenturesCompleta.getRecebedor().getId()) {
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor10());

									if(!CommonsUtil.semValor(c.getVlrInvestidor10())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor10());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor10());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor10())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor10().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor10());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor10().get(
													c.getListContratoCobrancaParcelasInvestidor10().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor10().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor10().get(
														c.getListContratoCobrancaParcelasInvestidor10().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor10()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
										} else {
											quitado = false;
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
						} else {	
							// se pagador for galleria
							debenturesCompleta.setDataDebentures(c.getDataInicio());
							
							if (c.getRecebedor() != null) {
								debenturesCompleta.setContrato(c);
								debenturesCompleta.setRecebedor(c.getRecebedor());
								debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor1());
								//debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());	
								
								if(!CommonsUtil.semValor(c.getVlrInvestidor1())) {
									debenturesCompleta.setValorDebenture(c.getVlrInvestidor1());
								} else if(!CommonsUtil.semValor(c.getVlrRecebedor())) {
									debenturesCompleta.setValorDebenture(c.getVlrRecebedor());
								} else if(!CommonsUtil.semValor(c.getValorCCB())) {
									debenturesCompleta.setValorDebenture(c.getValorCCB());
								}
								
								// ADICIONAR ISSO
								if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor1())) {
									debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor1().size());
								} else {
									debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor1());
								}
								
								debenturesCompleta.setParcelaFinal(
										c.getListContratoCobrancaParcelasInvestidor1().get(
												c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getParcelaMensalBaixa());
								
								if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
									debenturesCompleta.setDataUltimaParcela(
											c.getListContratoCobrancaParcelasInvestidor1().get(
													c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getDataVencimento());
								}	
								
								// Verifica se estão quitadas todas as parcelas
								boolean quitado = true;								
								for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor1()) {
									
									if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
										debenturesCompleta.setParcelaMensal(
												parcelas.getParcelaMensalBaixa());
									}
									
									if(parcelas.isBaixado()) {
										debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
										debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
									} else {
										quitado = false;
										break;
									}
								}
								if (quitado) {
									debenturesCompleta.setQuitado("Sim");
								} else {
									debenturesCompleta.setQuitado("Não");
								}					
								
								if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
									debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
								}
								
								if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
									debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
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
	
	private static final String QUERY_DEBENTURES_REL_EMITIDAS_PART_1 = "select iddebenture, idcontrato from ("
			+ "select d.id iddebenture, c.id idcontrato from cobranca.DebenturesInvestidor d "
			+ "inner join cobranca.contratocobranca c on d.contrato = c.id ";
			
		private static final String QUERY_DEBENTURES_REL_EMITIDAS_PART_2 = "select 0 iddebenture, ct.id idcontrato from cobranca.contratocobranca ct  "
			+ "inner join cobranca.responsavel res on ct.responsavel = res.id  "
			+ "where ct.status = 'Aprovado' "
			+ "and ct.pagador in (15, 34,14, 182, 417, 803) ";			
		
	@SuppressWarnings("unchecked")
	public List<DebenturesInvestidor> getRelatorioDebenturesEmitidas(final Date dtRelInicio, final Date dtRelFim, final String tipoDocumento, final String documento, final String status,
			final String filtraValorFace, final BigDecimal valorFaceInicial, final BigDecimal valorFaceFinal, final String filtroNumeroContrato, final String filtroDebenturesTipoFiltro) {
		return (List<DebenturesInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DebenturesInvestidor> objects = new ArrayList<DebenturesInvestidor>();
				
				String query_QUERY_DEBENTURES_EMITIDAS = "";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {				
					
					String query_QUERY_DEBENTURES_EMITIDAS_1 = QUERY_DEBENTURES_REL_EMITIDAS_PART_1;
					String query_QUERY_DEBENTURES_EMITIDAS_2 = QUERY_DEBENTURES_REL_EMITIDAS_PART_2;
					
					if (filtroDebenturesTipoFiltro.equals("Periodo")) {
						query_QUERY_DEBENTURES_EMITIDAS_1 = query_QUERY_DEBENTURES_EMITIDAS_1 + " where d.dataDebentures >= ? ::timestamp and d.dataDebentures <= ? ::timestamp ";
						query_QUERY_DEBENTURES_EMITIDAS_2 = query_QUERY_DEBENTURES_EMITIDAS_2 + " and ct.datainicio >= ? ::timestamp and ct.datainicio <= ? ::timestamp ";
					}

					query_QUERY_DEBENTURES_EMITIDAS =  query_QUERY_DEBENTURES_EMITIDAS_1 + " union all " + query_QUERY_DEBENTURES_EMITIDAS_2;
										
					query_QUERY_DEBENTURES_EMITIDAS = query_QUERY_DEBENTURES_EMITIDAS + " ) debentures ";	
					
					query_QUERY_DEBENTURES_EMITIDAS = query_QUERY_DEBENTURES_EMITIDAS + "order by idcontrato desc";	
					
					connection = getConnection();

					ps = connection.prepareStatement(query_QUERY_DEBENTURES_EMITIDAS);
					
					if (filtroDebenturesTipoFiltro.equals("Periodo")) {					
						java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
						java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);
						ps.setDate(3, dtRelInicioSQL);
						ps.setDate(4, dtRelFimSQL);
					}
					
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
							//debenturesCompleta.setValorDebenture(BigDecimal.valueOf(db.getQtdeDebentures() * 1000).setScale(2, BigDecimal.ROUND_UP));
							//debenturesCompleta.setPrazo(c.getQtdeParcelas());
							
							debenturesCompleta.setContrato(c);
							
							if (c.getRecebedor() != null) {
								if (c.getRecebedor().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido1()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor1());
									//debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());	
									
									if(!CommonsUtil.semValor(c.getVlrInvestidor1())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor1());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor1())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor1().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor1());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor1().get(
													c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor1().get(
														c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor1()) {
										
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											
											debenturesCompleta.setValorLiquido(parcelas.getParcelaMensal());
											
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
										
										if (c.getListContratoCobrancaParcelasInvestidor1().size() > 1) { 
											debenturesCompleta.setValorLiquido(c.getListContratoCobrancaParcelasInvestidor1().get(1).getParcelaMensal());
										}
									} else {
										debenturesCompleta.setQuitado("Não");
									}									
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
									
									if (c.getCarenciaInvestidor1() != null) {
										debenturesCompleta.setMesesCarencia(c.getCarenciaInvestidor1());
									}
									
									debenturesCompleta.setValorFace(c.getVlrInvestidor1());
									
									
									if (debenturesCompleta.getMesesCarencia() > 0) {
										debenturesCompleta.setPagamentoMensal("Não");
									} else {
										debenturesCompleta.setPagamentoMensal("Sim");
									}
									
									debenturesCompleta.setTipoCalculo(c.getTipoCalculoInvestidor1());
								}
							}
							/*
							if (c.getRecebedor2() != null) {
								if (c.getRecebedor2().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido2()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor2());
									debenturesCompleta.setValorDebenture(c.getVlrInvestidor2());
									
									if(!CommonsUtil.semValor(c.getVlrInvestidor2())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor2());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor2());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor2())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor2().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor2());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor2().get(
													c.getListContratoCobrancaParcelasInvestidor2().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor2().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor2().get(
														c.getListContratoCobrancaParcelasInvestidor2().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor2()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor3() != null) {
								if (c.getRecebedor3().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido3()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor3());

									if(!CommonsUtil.semValor(c.getVlrInvestidor3())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor3());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor3());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor3())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor3().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor3());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor3().get(
													c.getListContratoCobrancaParcelasInvestidor3().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor3().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor3().get(
														c.getListContratoCobrancaParcelasInvestidor3().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor3()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor4() != null) {
								if (c.getRecebedor4().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido4()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor4());

									if(!CommonsUtil.semValor(c.getVlrInvestidor4())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor4());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor4());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor4())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor4().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor4());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor4().get(
													c.getListContratoCobrancaParcelasInvestidor4().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor4().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor4().get(
														c.getListContratoCobrancaParcelasInvestidor4().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor4()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor5() != null) {
								if (c.getRecebedor5().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido5()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor5());

									if(!CommonsUtil.semValor(c.getVlrInvestidor5())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor5());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor5());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor5())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor5().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor5());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor5().get(
													c.getListContratoCobrancaParcelasInvestidor5().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor5().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor5().get(
														c.getListContratoCobrancaParcelasInvestidor5().size() - 1).getDataVencimento());
									}
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor5()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor6() != null) {
								if (c.getRecebedor6().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido6()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor6());

									if(!CommonsUtil.semValor(c.getVlrInvestidor6())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor6());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor6());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor6())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor6().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor6());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor6().get(
													c.getListContratoCobrancaParcelasInvestidor6().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor6().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor6().get(
														c.getListContratoCobrancaParcelasInvestidor6().size() - 1).getDataVencimento());
									}
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor6()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor7() != null) {
								if (c.getRecebedor7().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido7()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor7());

									if(!CommonsUtil.semValor(c.getVlrInvestidor7())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor7());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor7());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor7())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor7().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor7());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor7().get(
													c.getListContratoCobrancaParcelasInvestidor7().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor7().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor7().get(
														c.getListContratoCobrancaParcelasInvestidor7().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor7()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor8() != null) {
								if (c.getRecebedor8().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido8()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor8());

									if(!CommonsUtil.semValor(c.getVlrInvestidor8())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor8());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor8());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor8())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor8().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor8());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor8().get(
													c.getListContratoCobrancaParcelasInvestidor8().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor8().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor8().get(
														c.getListContratoCobrancaParcelasInvestidor8().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor8()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor9() != null) {
								if (c.getRecebedor9().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido9()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor9());

									if(!CommonsUtil.semValor(c.getVlrInvestidor9())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor9());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor9());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor9())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor9().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor9());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor9().get(
													c.getListContratoCobrancaParcelasInvestidor9().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor9().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor9().get(
														c.getListContratoCobrancaParcelasInvestidor9().size() - 1).getDataVencimento());
									}	
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor9()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							
							if (c.getRecebedor10() != null) {
								if (c.getRecebedor10().getId() == debenturesCompleta.getRecebedor().getId()) {
									if (c.isRecebedorGarantido10()) {
										debenturesCompleta.setGarantido("Sim");
									} else {
										debenturesCompleta.setGarantido("Não");
									}
									
									debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor10());

									if(!CommonsUtil.semValor(c.getVlrInvestidor10())) {
										debenturesCompleta.setValorDebenture(c.getVlrInvestidor10());
									} else {
										debenturesCompleta.setValorDebenture(c.getVlrRecebedor10());
									}
									
									if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor10())) {
										debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor10().size());
									} else {
										debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor10());
									}
									
									debenturesCompleta.setParcelaFinal(
											c.getListContratoCobrancaParcelasInvestidor10().get(
													c.getListContratoCobrancaParcelasInvestidor10().size() - 1).getParcelaMensalBaixa());
									
									if (c.getListContratoCobrancaParcelasInvestidor10().size() > 0) {
										debenturesCompleta.setDataUltimaParcela(
												c.getListContratoCobrancaParcelasInvestidor10().get(
														c.getListContratoCobrancaParcelasInvestidor10().size() - 1).getDataVencimento());
									}		
									
									// Verifica se estão quitadas todas as parcelas
									boolean quitado = true;								
									for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor10()) {
										if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
											debenturesCompleta.setParcelaMensal(
													parcelas.getParcelaMensalBaixa());
										}
										
										if(parcelas.isBaixado()) {
											debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
											debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
											
											debenturesCompleta.setDataQuitacao(parcelas.getDataBaixa());
										} else {
											quitado = false;
											
											debenturesCompleta.setDataQuitacao(null);
											
											break;
										}
									}
									if (quitado) {
										debenturesCompleta.setQuitado("Sim");
									} else {
										debenturesCompleta.setQuitado("Não");
									}	
									
									if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
										debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
									}
									
									if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
										debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
									}
								}
							}
							*/
						} else {	
							// se pagador for galleria
							debenturesCompleta.setDataDebentures(c.getDataInicio());
							
							if (c.getRecebedor() != null) {
								debenturesCompleta.setContrato(c);
								debenturesCompleta.setRecebedor(c.getRecebedor());
								debenturesCompleta.setTaxa(c.getTaxaRemuneracaoInvestidor1());
								
								if (c.isRecebedorGarantido1()) {
									debenturesCompleta.setGarantido("Sim");
								} else {
									debenturesCompleta.setGarantido("Não");
								}
								//debenturesCompleta.setParcelaMensal(c.getVlrRecebedor());	
								
								if(!CommonsUtil.semValor(c.getVlrInvestidor1())) {
									debenturesCompleta.setValorDebenture(c.getVlrInvestidor1());
								} else if(!CommonsUtil.semValor(c.getVlrRecebedor())) {
									debenturesCompleta.setValorDebenture(c.getVlrRecebedor());
								} else if(!CommonsUtil.semValor(c.getValorCCB())) {
									debenturesCompleta.setValorDebenture(c.getValorCCB());
								}
								
								// ADICIONAR ISSO
								if(CommonsUtil.semValor(c.getQtdeParcelasInvestidor1())) {
									debenturesCompleta.setPrazo(c.getListContratoCobrancaParcelasInvestidor1().size());
								} else {
									debenturesCompleta.setPrazo(c.getQtdeParcelasInvestidor1());
								}
								
								debenturesCompleta.setParcelaFinal(
										c.getListContratoCobrancaParcelasInvestidor1().get(
												c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getParcelaMensalBaixa());
								
								if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
									debenturesCompleta.setDataUltimaParcela(
											c.getListContratoCobrancaParcelasInvestidor1().get(
													c.getListContratoCobrancaParcelasInvestidor1().size() - 1).getDataVencimento());
								}	
								
								// Verifica se estão quitadas todas as parcelas
								boolean quitado = true;								
								for (ContratoCobrancaParcelasInvestidor parcelas : c.getListContratoCobrancaParcelasInvestidor1()) {
									
									if(CommonsUtil.semValor(debenturesCompleta.getParcelaMensal())){
										debenturesCompleta.setParcelaMensal(
												parcelas.getParcelaMensalBaixa());
									}
									
									if(parcelas.isBaixado()) {
										debenturesCompleta.setDataUltimaParcelaPaga(parcelas.getDataVencimento());//
										debenturesCompleta.setValorUltimaParcelaPaga(parcelas.getSaldoCredorAtualizado());//
									} else {
										quitado = false;
										
										debenturesCompleta.setValorLiquido(parcelas.getParcelaMensal());
										
										break;
									}
								}
								if (quitado) {
									debenturesCompleta.setQuitado("Sim");
									
									if (c.getListContratoCobrancaParcelasInvestidor1().size() > 1) { 
										debenturesCompleta.setValorLiquido(c.getListContratoCobrancaParcelasInvestidor1().get(1).getParcelaMensal());
									}
								} else {
									debenturesCompleta.setQuitado("Não");
								}					
								
								if(CommonsUtil.semValor(debenturesCompleta.getDataUltimaParcelaPaga())) {
									debenturesCompleta.setDataUltimaParcelaPaga(debenturesCompleta.getDataDebentures());//
								}
								
								if(CommonsUtil.semValor(debenturesCompleta.getValorUltimaParcelaPaga())) {
									debenturesCompleta.setValorUltimaParcelaPaga(debenturesCompleta.getValorDebenture());//
								}
																
								debenturesCompleta.setValorFace(debenturesCompleta.getValorDebenture());
								
								if (c.getCarenciaInvestidor1() != null) {
									debenturesCompleta.setMesesCarencia(c.getCarenciaInvestidor1());
								}
								
								if (debenturesCompleta.getMesesCarencia() > 0) {
									debenturesCompleta.setPagamentoMensal("Não");
								} else {
									debenturesCompleta.setPagamentoMensal("Sim");
								}
								
								debenturesCompleta.setTipoCalculo(c.getTipoCalculoInvestidor1());
								
								
							}							
						}
						
						/***
						 * Aplica filtros de Documento e Status
						 */
						boolean addDebenture = true;
						
						// filtro documento
						if (!tipoDocumento.equals("Todos")) {
							if (tipoDocumento.equals("CPF")) {
								if (debenturesCompleta.getRecebedor().getCpf() != null) { 
									if (!debenturesCompleta.getRecebedor().getCpf().equals(documento)) {
										addDebenture = false;
									}
								} else {
									addDebenture = false;
								}
							}
							if (tipoDocumento.equals("CNPJ")) {
								if (debenturesCompleta.getRecebedor().getCnpj() != null) {
									if (!debenturesCompleta.getRecebedor().getCnpj().equals(documento)) {
										addDebenture = false;
									}
								} else {
									addDebenture = false;
								}
							}
						}	
						
						// filtro status	
						if (!status.equals("Todos")) {
							if (status.equals("Quitadas")) {
								if (debenturesCompleta.getQuitado().equals("Não")) {
									addDebenture = false;
								}
							}
							if (status.equals("Ativas")) {
								if (debenturesCompleta.getQuitado().equals("Sim")) {
									addDebenture = false;
								}
							}
						}			
						
						// filtro valor face
						if (filtraValorFace.equals("Filtrar")) {
							if (debenturesCompleta.getValorFace().compareTo(valorFaceInicial) < 0 || debenturesCompleta.getValorFace().compareTo(valorFaceFinal) > 0) {
								addDebenture = false;
							}
						}
						
						// filtro número do contrato
						if (filtroDebenturesTipoFiltro.equals("Contrato")) {		
							if (!filtroNumeroContrato.equals("")) {
								addDebenture = false;
								
								if (debenturesCompleta.getContrato().getNumeroContrato().equals(filtroNumeroContrato)) {
									addDebenture = true;
								}
							}
						}
						
						if (addDebenture) {
							objects.add(debenturesCompleta);
						}						
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
