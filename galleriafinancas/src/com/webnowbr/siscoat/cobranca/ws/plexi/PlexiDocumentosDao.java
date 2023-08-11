package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.db.dao.HibernateDao;

public class PlexiDocumentosDao extends HibernateDao<PlexiDocumentos, Long> {

	private static final String QUERY_DOC_PF = "select p.id from cobranca.plexiDocumentos p "
			+ "where p.pf = true";

	@SuppressWarnings("unchecked")
	public List<PlexiDocumentos> getDocumentosPF() {
		return (List<PlexiDocumentos>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PlexiDocumentos> documentosPf = new ArrayList<PlexiDocumentos>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_DOC_PF);
					rs = ps.executeQuery();

					PlexiDocumentosDao plexiDocumentosDao = new PlexiDocumentosDao();
					
					while (rs.next()) {
						PlexiDocumentos doc = plexiDocumentosDao.findById(rs.getLong(1));
						documentosPf.add(doc);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return documentosPf;
			}
		});
	}
	
	private static final String QUERY_DOC_PJ = "select p.id from cobranca.plexiDocumentos p "
			+ "where p.pj = true";

	@SuppressWarnings("unchecked")
	public List<PlexiDocumentos> getDocumentosPJ() {
		return (List<PlexiDocumentos>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PlexiDocumentos> documentosPf = new ArrayList<PlexiDocumentos>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_DOC_PJ);
					rs = ps.executeQuery();

					PlexiDocumentosDao plexiDocumentosDao = new PlexiDocumentosDao();
					
					while (rs.next()) {
						PlexiDocumentos doc = plexiDocumentosDao.findById(rs.getLong(1));
						documentosPf.add(doc);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return documentosPf;
			}
		});
	}

}
