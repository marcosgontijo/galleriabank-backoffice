package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.webnowbr.siscoat.cobranca.db.model.CcbContrato;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class CcbDao extends HibernateDao <CcbContrato,Long> {
	
	private static final String QUERY_CONSULTA_CCBS =  " select "
			+ "	ccb.id, ccb.numeroCcb,  ccb.numeroOperacao, ccb.nomeEmitente "
			+ " FROM "
			+ "	cobranca.ccbcontrato ccb ";
	
	@SuppressWarnings("unchecked")
	public List<CcbContrato> ConsultaCCBs() {
		return (List<CcbContrato>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<CcbContrato> objects = new ArrayList<CcbContrato>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONSULTA_CCBS);

					rs = ps.executeQuery();
					while (rs.next()) {						CcbContrato ccbContrato = new CcbContrato();
						ccbContrato.setId(rs.getLong("id"));
						ccbContrato.setNumeroCcb(rs.getString("numeroccb"));
						ccbContrato.setNumeroOperacao(rs.getString("numeroOperacao"));
						ccbContrato.setNomeEmitente(rs.getString("nomeEmitente"));
						objects.add(ccbContrato);	
															
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
}
