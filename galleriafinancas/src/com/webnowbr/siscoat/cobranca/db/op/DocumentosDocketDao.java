package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DocumentosDocketDao extends HibernateDao <DocumentosDocket,Long> {
	
	private static final String QUERY_DOC_DOCKET = "select * from cobranca.documentosdocket d "
			+ "where d.id not in (1,3,4)";

	@SuppressWarnings("unchecked")
	public List<DocumentosDocket> getAllDocumentosDocket() {
		return (List<DocumentosDocket>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocumentosDocket> documentos = new ArrayList<DocumentosDocket>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {					
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_DOC_DOCKET);
					rs = ps.executeQuery();

					DocumentosDocketDao docDocketDao = new DocumentosDocketDao();
					
					while (rs.next()) {
						DocumentosDocket doc = docDocketDao.findById(rs.getLong(1));
						documentos.add(doc);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return documentos;
			}
		});
	}
}
