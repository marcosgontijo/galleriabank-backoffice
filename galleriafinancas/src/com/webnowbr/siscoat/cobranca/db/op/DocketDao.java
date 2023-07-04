package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Docket;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DocketDao extends HibernateDao <Docket,Long> {
	
	private static final String QUERY_CONSULTA_IDDOCKET = " select idcallmanager from cobranca.docket "
			+ " where objetocontratocobranca = ? ";
	
	
	@SuppressWarnings("unchecked")
	public String consultaContratosPendentesResponsaveis(final ContratoCobranca contrato) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public String run() throws Exception {
				String idCallManager = "";
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONSULTA_IDDOCKET;
					
					ps = connection.prepareStatement(query);
					ps.setLong(1, contrato.getId());
					
					rs = ps.executeQuery();
					
					while (rs.next()) {
						idCallManager = rs.getString("idcallmanager");					
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return idCallManager;
			}
		});	
	}
	
}
