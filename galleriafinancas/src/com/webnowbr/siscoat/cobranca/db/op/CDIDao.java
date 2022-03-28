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
import com.webnowbr.siscoat.cobranca.db.model.CDI;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class CDIDao extends HibernateDao <CDI,Long> {
	private static final String QUERY_GET_TAXA_MES = "select taxa from cobranca.cdi " + 
			"where date_trunc('month', data) = date_trunc('month', ? ::timestamp) ";
	
	@SuppressWarnings("unchecked")
	public BigDecimal getTaxaCDIMes(final Date dataReferencia) {
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
	
	private static final String QUERY_ULTIMO_CDI = "select * from cobranca.cdi where date_trunc('day', data) < date_trunc('day', ? ::timestamp) order by data desc limit 1";
	
	@SuppressWarnings("unchecked")
	public CDI getUltimoCDI(final Date dataReferencia) {
		return (CDI) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				final CDI cdi = new CDI();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_ULTIMO_CDI);	
					
					java.sql.Date data = new java.sql.Date(dataReferencia.getTime());
					
					ps.setDate(1, data);
					
					rs = ps.executeQuery();
										
					while (rs.next()) {
						
						cdi.setId(rs.getLong("id"));
						cdi.setData(rs.getDate("data"));				
						cdi.setTaxa(rs.getBigDecimal("taxa"));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return cdi;
			}
		});	
	}
	
}
