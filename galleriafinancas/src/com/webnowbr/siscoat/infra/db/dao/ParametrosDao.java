package com.webnowbr.siscoat.infra.db.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.infra.db.model.Parametros;

/**
 * DAO access layer for User entity.
 */
public class ParametrosDao extends HibernateDao<Parametros, Long> {
	
	
	
	private static final String QUERY_PARAMS_BIGDECIMAL = " select valorbigdecimal from infra.parametros p "
			+ "where p.nome = ? ";

	@SuppressWarnings("unchecked")
	public BigDecimal getParamBigDecimal(String parametro) {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				BigDecimal retorno = new BigDecimal(0);
								
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_PARAMS_BIGDECIMAL);
					
					ps.setString(1, parametro);	
					
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					
					if (rs.next()) {

						retorno	= rs.getBigDecimal(1);
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return retorno; 
			}
		});	
	}	
}
