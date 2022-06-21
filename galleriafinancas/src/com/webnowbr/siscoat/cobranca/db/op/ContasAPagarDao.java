package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContasAPagar;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class ContasAPagarDao extends HibernateDao<ContasAPagar, Long> {
	
	private static final String QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = "select id from cobranca.contaspagar " + 
			"where 1=1 " + 
			"and tipodespesa = ? " +
			"and contapaga = ? ";

	public List<ContasAPagar> atualizaListagemContasPagar(final String tipoDespesa, final Boolean contaPaga, final Date dataInicio, final Date dataFim) throws Exception {

		List<ContasAPagar> listContasPagar = new ArrayList<ContasAPagar>();

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			
			String query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR;
			
			if (dataInicio != null) {
				query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR + " and datavencimento >= ? ::timestamp ";
			}
			
			if (dataFim != null) {
				query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR + " and datavencimento <= ? ::timestamp ";
			}
			
			ps = connection.prepareStatement(query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR);

			ps.setString(1, tipoDespesa);
			ps.setBoolean(2, contaPaga);
			
			int countParam = 2;
			
			if (dataInicio != null) {
				java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
				countParam = countParam + 1;
				ps.setDate(countParam, dtRelInicioSQL);				
			}
			
			if (dataFim != null) {
				java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
				countParam = countParam + 1;
				ps.setDate(countParam, dtRelFimSQL);
			}
			
			rs = ps.executeQuery();
			ContasAPagarDao cDao = new ContasAPagarDao();
			ContasAPagar contasPagar = new ContasAPagar();
			
			while (rs.next()) {
				contasPagar = cDao.findById(rs.getLong("id"));
				listContasPagar.add(contasPagar);
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return listContasPagar;
	}
}