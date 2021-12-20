package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * 
 * @author hv.junior
 *
 */
public class ContaContabilDao extends HibernateDao<ContaContabil, Long> {

	@SuppressWarnings("unchecked")
	public List<ContaContabil> ContasContabilRaiz() {

		return (List<ContaContabil>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<ContaContabil> ContasContabilRaiz = new ArrayList<ContaContabil>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from cobranca.ContaContabil ");					
					query.append("where contaContabilPai is null ");
					query.append("order by nome ");
							

					ps = connection.prepareStatement(query.toString());

					rs = ps.executeQuery();

					ContaContabilDao contaContabilDao = new ContaContabilDao();

					while (rs.next()) {
						ContasContabilRaiz.add(contaContabilDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return ContasContabilRaiz;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ContaContabil> ContasContabilOrdenadaRaiz() {

		return (List<ContaContabil>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<ContaContabil> ContasContabilRaiz = new ArrayList<ContaContabil>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from cobranca.ContaContabil ");
					query.append("order by case when contacontabilpai is null then id else contacontabilpai end, ");
					query.append("contacontabilpai desc, codigocontacontabil");		

					ps = connection.prepareStatement(query.toString());

					rs = ps.executeQuery();

					ContaContabilDao contaContabilDao = new ContaContabilDao();

					while (rs.next()) {
						ContasContabilRaiz.add(contaContabilDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return ContasContabilRaiz;
			}
		});
	}
}
