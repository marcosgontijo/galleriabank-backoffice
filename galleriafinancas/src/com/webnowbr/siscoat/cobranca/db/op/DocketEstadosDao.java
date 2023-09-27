package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DocketEstadosDao extends HibernateDao <DocketEstados,Long> {
	
	private static final String QUERY_GET_ESTADO = "select id from cobranca.docketestados where idDocket = ? ";
	
	@SuppressWarnings("unchecked")
	public DocketEstados getEstado(final String idEstadoDocket) {
		return (DocketEstados) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				DocketEstados estado = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_ESTADO);	
					
					ps.setString(1, idEstadoDocket);
					
					rs = ps.executeQuery();
										
					while (rs.next()) {
						estado = findById(rs.getLong("id"));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return estado;
			}
		});	
	}
	
private static final String QUERY_GET_ESTADO_UF = "select id from cobranca.docketestados where url = ? ";
	
	@SuppressWarnings("unchecked")
	public DocketEstados getEstadoByUf(final String uf) {
		return (DocketEstados) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				DocketEstados estado = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_ESTADO_UF);	
					
					ps.setString(1, uf);
					
					rs = ps.executeQuery();
										
					while (rs.next()) {
						estado = findById(rs.getLong("id"));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return estado;
			}
		});	
	}
	
}
