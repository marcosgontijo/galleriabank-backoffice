package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

public class DocumentoAnaliseDao extends HibernateDao<DocumentoAnalise, Long> {

	
	
	private static final String QUERY_VERIFICA_PESSOA_ANALISE = " select id "
			+ "from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and cnpjcpf = ? ";
	
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
}