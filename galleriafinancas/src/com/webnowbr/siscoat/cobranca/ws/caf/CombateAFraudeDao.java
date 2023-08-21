package com.webnowbr.siscoat.cobranca.ws.caf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.db.dao.HibernateDao;

public class CombateAFraudeDao extends HibernateDao<CombateAFraude, Long> {
	
	private static final String CONSULTAR_COMBATE_A_FRAUDE = "select c.id, c.cpf, c.status, c.date, c.obs "
			+ "from cobranca.combateafraude c ";
		

	@SuppressWarnings("unchecked")
	public List<CombateAFraude> getCombateAFraudeListDB() {
		return (List<CombateAFraude>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<CombateAFraude> cafList = new ArrayList<CombateAFraude>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(CONSULTAR_COMBATE_A_FRAUDE);
					rs = ps.executeQuery();
					
					while (rs.next()) {
						CombateAFraude caf = new CombateAFraude();
						caf.setId(rs.getLong("id"));
						caf.setCpf(rs.getString("cpf"));
						caf.setStatus(rs.getString("status"));
						caf.setDate(rs.getDate("date"));
						caf.setObs(rs.getString("obs"));
						cafList.add(caf);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return cafList;
			}
		});
	}
}
