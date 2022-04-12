package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.model.UniProof;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class UniProofDao extends HibernateDao <UniProof,Long> {
	
	private static final String QUERY_GET_PROCESSO = "select id from cobranca.uniproof " + 
			"where lotItemId = ? ";
	
	@SuppressWarnings("unchecked")
	public UniProof getProcessoByLotItemId(final String lotItemId) {
		return (UniProof) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				UniProof processo = null;
				UniProofDao uniProofDao = new UniProofDao();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_PROCESSO);		
	
					ps.setString(1, lotItemId);
	
					rs = ps.executeQuery();
										
					while (rs.next()) {
						processo = uniProofDao.findById(rs.getLong(1));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return processo;
			}
		});	
	}
}
