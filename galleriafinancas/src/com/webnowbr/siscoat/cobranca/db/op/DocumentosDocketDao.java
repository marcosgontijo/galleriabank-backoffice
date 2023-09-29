package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.common.CommonsUtil;
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
	
	private static final String QUERY_DOC_PF = "select d.id from cobranca.documentosdocket d "
			+ "where d.pf = true ";

	@SuppressWarnings("unchecked")
	public List<DocumentosDocket> getDocumentosPF(List<String> estados, String etapa) {
		return (List<DocumentosDocket>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocumentosDocket> documentosPf = new ArrayList<DocumentosDocket>();

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
						}
						query = query + ")";
					}
					
					if(!CommonsUtil.semValor(etapa)) {
						query = query + " and etapa like '%" + etapa + "%'";
					}

					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();

					DocumentosDocketDao documentosDocketDao = new DocumentosDocketDao();
					
					while (rs.next()) {
						DocumentosDocket doc = documentosDocketDao.findById(rs.getLong(1));
						documentosPf.add(doc);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return documentosPf;
			}
		});
	}
	
	private static final String QUERY_DOC_PJ = "select d.id from cobranca.documentosdocket d "
			+ "where d.pj = true";

	@SuppressWarnings("unchecked")
	public List<DocumentosDocket> getDocumentosPJ(List<String> estados, String etapa) {
		return (List<DocumentosDocket>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocumentosDocket> documentosPf = new ArrayList<DocumentosDocket>();

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
						}
						query = query + ")";
					}
					
					if(!CommonsUtil.semValor(etapa)) {
						query = query + " and etapa like '%" + etapa + "%'";
					}

					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();

					DocumentosDocketDao documentosDocketDao = new DocumentosDocketDao();
					
					while (rs.next()) {
						DocumentosDocket doc = documentosDocketDao.findById(rs.getLong(1));
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
