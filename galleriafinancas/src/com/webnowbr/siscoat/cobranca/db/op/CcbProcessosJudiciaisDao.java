package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class CcbProcessosJudiciaisDao extends HibernateDao <CcbProcessosJudiciais, Long> {
	
	private static final String QUERY_PROCESSOS_EXISTENTES = "select c.id from cobranca.ccbprocessosjudiciais c "
			+ " where numero = ? and contrato = ? ";

	@SuppressWarnings("unchecked")
	public CcbProcessosJudiciais getProcessosExistentes(String numeroProcesso, ContratoCobranca contrato) {
		return (CcbProcessosJudiciais) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				CcbProcessosJudiciais processo = new CcbProcessosJudiciais();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_PROCESSOS_EXISTENTES);
					ps.setString(1, numeroProcesso);
					ps.setLong(2, contrato.getId());
					rs = ps.executeQuery();
					CcbProcessosJudiciaisDao processosJudiciaisDao = new CcbProcessosJudiciaisDao();
					if (rs.next()) {
						processo = processosJudiciaisDao.findById(rs.getLong(1));
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return processo;
			}
		});
	}
}
