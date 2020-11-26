package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.db.dao.*;

public class DebenturesInvestidorDao extends HibernateDao<DebenturesInvestidor, Long> {

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
}
