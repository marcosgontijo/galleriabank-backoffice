package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class IPCADao extends HibernateDao <IPCA,Long> {
	private static final String QUERY_GET_TAXA_MES = "select taxa from cobranca.ipca " + 
			"where date_trunc('month', data) = date_trunc('month', ? ::timestamp) ";
	
	@SuppressWarnings("unchecked")
	public BigDecimal getTaxaIPCAMes(final Date dataReferencia) {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				BigDecimal taxaRetorno = BigDecimal.ZERO;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_TAXA_MES);		
					
					java.sql.Date data = new java.sql.Date(dataReferencia.getTime());
	
					ps.setDate(1, data);
	
					rs = ps.executeQuery();
										
					while (rs.next()) {
						taxaRetorno = rs.getBigDecimal(1);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return taxaRetorno;
			}
		});	
	}
	
	private static final String QUERY_PARCELAS_POR_MES = "select cd.id, c.id from cobranca.contratocobranca c "
														+ "inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = c.id  "
														+ "inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id  "
														+ "where c.corrigidoipca = true  "
														+ "and cd.datavencimentoatual >= ? ::timestamp "
														+ "and cd.datavencimentoatual <= ? ::timestamp ";	 
	
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaDetalhes> getContratosPorInvestidorInformeRendimentos(final Date dataInicioReferenciaIPCA, final Date dataFimReferenciaIPCA) {
		return (List<ContratoCobrancaDetalhes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaDetalhes> contratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_PARCELAS_POR_MES);		
					
					java.sql.Date dataInicio = new java.sql.Date(dataInicioReferenciaIPCA.getTime());
					java.sql.Date dataFim = new java.sql.Date(dataFimReferenciaIPCA.getTime());
	
					ps.setDate(1, dataInicio);
					ps.setDate(2, dataFim);
	
					rs = ps.executeQuery();
					
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					ContratoCobrancaDetalhes cDetalhes = new ContratoCobrancaDetalhes();
					while (rs.next()) {
						cDetalhes = contratoCobrancaDetalhesDao.findById(rs.getLong(1));
						cDetalhes.setIdContrato(rs.getLong(2));
						
						contratoCobrancaDetalhes.add(cDetalhes);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return contratoCobrancaDetalhes;
			}
		});	
	}
}
