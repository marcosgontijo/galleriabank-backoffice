package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

public class DocumentoAnaliseDao extends HibernateDao<DocumentoAnalise, Long> {

	
	
	private static final String QUERY_VERIFICA_PESSOA_ANALISE = " select id "
			+ "from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and cnpjcpf = ? ";
	
	private static final String Verifica_Excluido = "select id"
			+ " from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and excluido = false "; 
	
	public boolean cadastradoAnalise(ContratoCobranca contratoCobranca, String cnpjCpf) {
		return (Boolean) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_VERIFICA_PESSOA_ANALISE);

					ps.setLong(1, contratoCobranca.getId());
					ps.setString(2, cnpjCpf);	
					
					ResultSet rs = ps.executeQuery();
					
					return rs.next();
						
				} finally {
					closeResources(connection, ps);
				}
			}
		});	
	
			
		
			
			
			
			
		}
		@SuppressWarnings("unchecked")
		public List<DocumentoAnalise> listagemDocumentoAnalise(ContratoCobranca contrato) {
			return (List<DocumentoAnalise>) executeDBOperation(new DBRunnable() {

				@Override
				public Object run() throws Exception {
					List<DocumentoAnalise> listaAnalise = new ArrayList<DocumentoAnalise>();
					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						connection = getConnection();
						ps = connection.prepareStatement(Verifica_Excluido);
						ps.setLong(1, contrato.getId());
						rs = ps.executeQuery();
						while (rs.next()) {
							listaAnalise.add(findById(rs.getLong("id")));

						}
					} finally {
						closeResources(connection, ps, rs);

					}
					return listaAnalise;
				}
			});
		}
		
	}
