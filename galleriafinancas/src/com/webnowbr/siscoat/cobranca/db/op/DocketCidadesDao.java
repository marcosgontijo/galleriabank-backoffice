package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocketCidades;
import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DocketCidadesDao extends HibernateDao <DocketCidades,Long> {
	
	private static final String QUERY_GET_CIDADES = "select id from cobranca.docketcidades where url like ? ";
	
	@SuppressWarnings("unchecked")
	public List<DocketCidades> getListaCidades(final String ufEstado) {
		return (List<DocketCidades>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				List<DocketCidades> listaCidades = new ArrayList<DocketCidades>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_CIDADES);	
					
					ps.setString(1, ufEstado.toLowerCase() + "-%");
					
					rs = ps.executeQuery();
										
					while (rs.next()) {
						listaCidades.add(findById(rs.getLong("id")));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listaCidades;
			}
		});	
	}
	
	private static final String QUERY_GET_CIDADES_DOCKET_ID = "select iddocket from cobranca.docketcidades where nome = ? ";
	
	@SuppressWarnings("unchecked")
	public String getCidadeId(final String nomeCidade) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				String cidadeId = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_CIDADES_DOCKET_ID);	
					
					ps.setString(1, nomeCidade);
					
					rs = ps.executeQuery();
										
					while (rs.next()) {
						cidadeId = (rs.getString("iddocket"));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return cidadeId;
			}
		});	
	}
	
}
