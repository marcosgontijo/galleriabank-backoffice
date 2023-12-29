package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Cartorio;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class CartorioDao extends HibernateDao<Cartorio, Long> {
	@SuppressWarnings("unchecked")
	public List<Cartorio> consultaCartorio(ContratoCobranca Contrato) {

		return (List<Cartorio>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<Cartorio> itemCartorio = new ArrayList<>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String QUERY_VERIFICA_FAVORITO = "select id from cobranca.cartorio where idcontrato = ? ";
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder(QUERY_VERIFICA_FAVORITO);

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, Contrato.getId());
					rs = ps.executeQuery();

					CartorioDao dao = new CartorioDao();

					while (rs.next()) {
						itemCartorio.add(dao.findById(rs.getLong(1)));
					}

					closeResources(connection, ps, rs);
				} catch (Exception e) {
					return null;
				}
				return itemCartorio;
			}

		});
	}
}
