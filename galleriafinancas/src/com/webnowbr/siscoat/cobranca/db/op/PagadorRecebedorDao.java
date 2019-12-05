package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.db.dao.*;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class PagadorRecebedorDao extends HibernateDao <PagadorRecebedor,Long> {
	
	private static final String QUERY_RECEBEDOR_IUGU =  "select id from cobranca.pagadorrecebedor " + 
			"where iuguaccountid = upper(?) ";
	
	@SuppressWarnings("unchecked")
	public PagadorRecebedor getRecebedorByAccountIdIugu(final String idAccountIugu) {
		return (PagadorRecebedor) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedor pagadorRecebedor = new PagadorRecebedor();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_RECEBEDOR_IUGU);
					
					ps.setString(1, idAccountIugu);

					rs = ps.executeQuery();
					
					if (rs.next()) {
						pagadorRecebedor = findById(rs.getLong(1));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return pagadorRecebedor;
			}
		});	
	}
	
	private static final String QUERY_BY_USUARIO_INVESTIDOR =  "select id from cobranca.pagadorrecebedor " + 
			"where usuario = ? ";
	
	@SuppressWarnings("unchecked")
	public PagadorRecebedor getRecebedorByUsuarioInvestidor(final long idUsuarioInvestidor) {
		return (PagadorRecebedor) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedor pagadorRecebedor = new PagadorRecebedor();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_BY_USUARIO_INVESTIDOR);
					
					ps.setLong(1, idUsuarioInvestidor);

					rs = ps.executeQuery();
					
					if (rs.next()) {
						pagadorRecebedor = findById(rs.getLong(1));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return pagadorRecebedor;
			}
		});	
	}
	
	private static final String QUERY_SUBCONTAS_IUGU =  "select id from cobranca.pagadorrecebedor " + 
			"where iuguaccountid != '' ";
	
	@SuppressWarnings("unchecked")
	public List<PagadorRecebedor> getSubContasIugu() {
		return (List<PagadorRecebedor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PagadorRecebedor> pagadorRecebedor = new ArrayList<PagadorRecebedor>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_SUBCONTAS_IUGU);					

					rs = ps.executeQuery();
					
					while (rs.next()) {
						pagadorRecebedor.add(findById(rs.getLong(1)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return pagadorRecebedor;
			}
		});	
	}	
}
