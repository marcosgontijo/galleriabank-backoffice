package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.model.Cidade;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

public class ContratoCobrancaLogsAlteracaoDao extends HibernateDao<ContratoCobrancaLogsAlteracao, Long> {

	private static final String QUERY_GET_LOGSALTERACAO = "select id" 
			+ " from cobranca.contratocobrancalogsalteracao "
			+ " where usuario = ? "
			+ " and contratocobranca = ? "
			+ " and logJustificado = false";
	
	@SuppressWarnings("unchecked")
	public ContratoCobrancaLogsAlteracao buscaLogAlteracao(final String usuarioLogin, final Long numeroContratoCobranca) {
		return (ContratoCobrancaLogsAlteracao) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_LOGSALTERACAO);	
					
					ps.setString(1, usuarioLogin);
					ps.setLong(2, numeroContratoCobranca);
					
					rs = ps.executeQuery();
										
					if (rs.next()) {
						ContratoCobrancaLogsAlteracaoDao dcDao = new ContratoCobrancaLogsAlteracaoDao();
						return dcDao.findById(rs.getLong(1));						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return null;
			}
		});	
	}
	
	private static final String QUERY_RETORNO_LOG_JUSTIFICADO = "select id "
			+ " from cobranca.contratocobrancalogsalteracao c " 
			+ " where logjustificado = false and usuario = ?";
   
 	public ContratoCobrancaLogsAlteracao consultaLogsNaoJustificados(String login) {
 		return (ContratoCobrancaLogsAlteracao) executeDBOperation(new DBRunnable() {
 			@Override
			public Object run() throws Exception {

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				ContratoCobrancaLogsAlteracao result = null;

				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_RETORNO_LOG_JUSTIFICADO);

					ps.setString(1, login);
					rs = ps.executeQuery();
					if( rs.next())					
						result = findById(rs.getLong("id"));

				} finally {
					closeResources(connection, ps, rs);
				}
				return result;
			}
 		});
 	}
	
}
