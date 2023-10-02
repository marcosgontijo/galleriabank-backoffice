package com.webnowbr.siscoat.cobranca.ws.netrin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class NetrinDocumentosDao extends HibernateDao<NetrinDocumentos, Long> {

	private static final String QUERY_DOC_PF = "select n.id from cobranca.netrinDocumentos n "
			+ "where n.pf = true ";

	@SuppressWarnings("unchecked")
	public List<NetrinDocumentos> getDocumentosPF(List<String> estados, String etapa) {
		return (List<NetrinDocumentos>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<NetrinDocumentos> documentosPf = new ArrayList<NetrinDocumentos>();

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

					NetrinDocumentosDao netrinDocumentosDao = new NetrinDocumentosDao();
					
					while (rs.next()) {
						NetrinDocumentos doc = netrinDocumentosDao.findById(rs.getLong(1));
						documentosPf.add(doc);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return documentosPf;
			}
		});
	}
	
	private static final String QUERY_DOC_PJ = "select n.id from cobranca.netrinDocumentos n "
			+ "where n.pj = true";

	@SuppressWarnings("unchecked")
	public List<NetrinDocumentos> getDocumentosPJ(List<String> estados, String etapa) {
		return (List<NetrinDocumentos>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<NetrinDocumentos> documentosPf = new ArrayList<NetrinDocumentos>();

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

					NetrinDocumentosDao netrinDocumentosDao = new NetrinDocumentosDao();
					
					while (rs.next()) {
						NetrinDocumentos doc = netrinDocumentosDao.findById(rs.getLong(1));
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
