package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Cartorio;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.infra.db.model.User;

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
				String QUERY_VERIFICA_CARTORIO = "select id from cobranca.cartorio where idcontrato = " + Contrato.getId() ;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder(QUERY_VERIFICA_CARTORIO);

					ps = connection.prepareStatement(query.toString());
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
	
	@SuppressWarnings("unchecked")
	public Cartorio consultaUltimoCartorio(ContratoCobranca Contrato) {
		return consultaUltimoCartorio(Contrato.getId());
	}
	
	@SuppressWarnings("unchecked")
	public Cartorio consultaUltimoCartorio(long id) {
		return (Cartorio) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Cartorio itemCartorio = new Cartorio();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String QUERY_VERIFICA_CARTORIO = "select id from cobranca.cartorio where idcontrato = " + id + " and status != '' order by id desc " ;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder(QUERY_VERIFICA_CARTORIO);

					ps = connection.prepareStatement(query.toString());
					rs = ps.executeQuery();

					CartorioDao dao = new CartorioDao();

					if (rs.next()) {
						itemCartorio = dao.findById(rs.getLong(1));
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

