package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class PlexiDocumentosDao extends HibernateDao<PlexiDocumentos, Long> {

	private static final String QUERY_DOC_PF = "select p.id from cobranca.plexiDocumentos p "
			+ "where p.pf = true ";

	@SuppressWarnings("unchecked")
	public List<PlexiDocumentos> getDocumentosPF(List<String> estados, String etapa) {
		return (List<PlexiDocumentos>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PlexiDocumentos> documentosPf = new ArrayList<PlexiDocumentos>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					String query = QUERY_DOC_PF;
					
					connection = getConnection();
					if(!CommonsUtil.semValor(estados)){
						query = query + " and(";
						boolean primeiro = true;
						for (String uf : estados) {
							if(primeiro) {
								query = query + " estados like '%" + uf.toUpperCase() + "%' ";
								primeiro = false;
							} else {
								query = query + " or estados like '%" + uf.toUpperCase() + "%' ";
							}
							query = query + ")";
						}
					}
					
					if(!CommonsUtil.semValor(etapa)) {
						query = query + " and etapa like '%" + etapa + "%'";
					}

					ps = connection.prepareStatement(query);
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
	public List<PlexiDocumentos> getDocumentosPJ(List<String> estados, String etapa, DocumentoAnalise docAnalise) {
		return (List<PlexiDocumentos>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PlexiDocumentos> documentosPf = new ArrayList<PlexiDocumentos>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					String query = QUERY_DOC_PJ;
					
					connection = getConnection();
					if(!CommonsUtil.semValor(estados)){
						query = query + " and(";
						boolean primeiro = true;
						for (String uf : estados) {
							if(primeiro) {
								query = query + " estados like '%" + uf.toUpperCase() + "%' ";
								primeiro = false;
							} else {
								query = query + " or estados like '%" + uf.toUpperCase() + "%' ";
							}
							query = query + ")";
						}
					}
					
					if(!CommonsUtil.semValor(etapa)) {
						query = query + " and etapa like '%" + etapa + "%'";
					}

					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();

					PlexiDocumentosDao plexiDocumentosDao = new PlexiDocumentosDao();
					
					while (rs.next()) {
						PlexiDocumentos doc = plexiDocumentosDao.findById(rs.getLong(1));
						if(CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/receita/qsa")
								&& !CommonsUtil.mesmoValor(docAnalise.getMotivoAnalise(), "Proprietario Atual")) {
							continue;
						}
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
