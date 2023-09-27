package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Cidade;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class CidadeDao extends HibernateDao <Cidade,Long> {
		
    
    private static final String QUERY_GET_CIDADES_ESTADO = "select nome, id from cobranca.cidade "
			+ " where nome = ? "
			+ " and estado = ? ";
	
	@SuppressWarnings("unchecked")
	public Cidade buscaCidade(final String nomeCidade, final String estado) {
		return (Cidade) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				Cidade cidade = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_CIDADES_ESTADO);	
					
					ps.setString(1, nomeCidade);
					ps.setString(2, estado);
					
					rs = ps.executeQuery();
										
					while (rs.next()) {
						CidadeDao dcDao = new CidadeDao();
						cidade = dcDao.findById(rs.getLong("id"));						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return cidade;
			}
		});	
	}
	
	 private static final String QUERY_GET_CIDADES_ESTADO_CONSULTA = "select nome, id, pintarLinha from cobranca.cidade "
				+ " where nome = ? "
				+ " and estado = ? ";
		
		@SuppressWarnings("unchecked")
		public Cidade busccaCidadeConculta(final String nomeCidade, final String estado, final boolean close) {
			return (Cidade) executeDBOperation(new DBRunnable() {
				@Override
				public Object run() throws Exception {
					
					Cidade cidade = new Cidade();
		
					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;
					
					
					try {
						connection = getConnection();

						ps = connection
								.prepareStatement(QUERY_GET_CIDADES_ESTADO_CONSULTA);	
						
						ps.setString(1, nomeCidade);
						ps.setString(2, estado);
						
						rs = ps.executeQuery();
											
						while (rs.next()) {
							
							cidade.setNome(rs.getString("nome"));	
							cidade.setId(rs.getLong("id"));	
							cidade.setPintarLinha(rs.getBoolean("pintarLinha"));
						}
					} finally {
						if(close) {
							closeResources(connection, ps, rs);		
						}
					}
					return cidade;
				}
			});	
		}
	
	 private static final String QUERY_GET_CIDADES = "select nome, id from cobranca.cidade "
				+ " where estado = ? ";
	 
	 @SuppressWarnings("unchecked")
		public List<String> pegarCidadesPeloEstado(final String estado) {
			return (List<String>) executeDBOperation(new DBRunnable() {
				@Override
				public Object run() throws Exception {
					
					List<String> lista = new ArrayList<String>();
		
					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;
					
					
					try {
						connection = getConnection();

						ps = connection.prepareStatement(QUERY_GET_CIDADES);	
						
						ps.setString(1, estado);
						
						rs = ps.executeQuery();
											
						while (rs.next()) {
							lista.add(rs.getString("nome"));
						}
					} finally {
						closeResources(connection, ps, rs);					
					}
					return lista;
				}
			});	
		}
}
