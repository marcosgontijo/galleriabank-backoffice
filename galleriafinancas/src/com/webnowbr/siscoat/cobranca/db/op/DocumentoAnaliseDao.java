package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class DocumentoAnaliseDao extends HibernateDao<DocumentoAnalise, Long> {

	private static final String QUERY_VERIFICA_PESSOA_ANALISE = " select id " + "from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and cnpjcpf = ? and reanalise = ? ";

	private static final String QUERY_VERIFICA_EXCLUIDO = "select id" + " from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and excluido = false and liberadoAnalise = true and reanalise = false";
	
	private static final String QUERY_REANALISE = "select id" + " from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and excluido = false and liberadoAnalise = true and reanalise = true";
	
	private static final String QUERY_NAO_ANALISADOS = "select id" + " from cobranca.documentosanalise "
			+ "where contratocobranca  = ? and excluido = false and liberadoAnalise = false";


	public DocumentoAnalise cadastradoAnalise(ContratoCobranca contratoCobranca, String cnpjCpf, boolean reanalise) {
		return (DocumentoAnalise) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				DocumentoAnalise documentoAnalise = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_VERIFICA_PESSOA_ANALISE);

					ps.setLong(1, contratoCobranca.getId());
					ps.setString(2, cnpjCpf);
					ps.setBoolean(3, reanalise);

					ResultSet rs = ps.executeQuery();
					if (rs.next())
						documentoAnalise = findById(rs.getLong("id"));

				} finally {
					closeResources(connection, ps);
				}
				
				return documentoAnalise;
			}
		});

	}

	@SuppressWarnings("unchecked")
	public List<DocumentoAnalise> listagemDocumentoAnalise(ContratoCobranca contrato) {
		return (List<DocumentoAnalise>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocumentoAnalise> listaAnalise = new ArrayList<DocumentoAnalise>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_VERIFICA_EXCLUIDO);
					ps.setLong(1, contrato.getId());
					rs = ps.executeQuery();
					while (rs.next()) {
						listaAnalise.add(findById(rs.getLong("id")));
					}
				} finally {
					closeResources(connection, ps, rs);

				}
				return listaAnalise;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoAnalise> listagemDocumentoAnaliseNaoAnalisados(ContratoCobranca contrato) {
		return (List<DocumentoAnalise>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocumentoAnalise> listaAnalise = new ArrayList<DocumentoAnalise>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_NAO_ANALISADOS);
					ps.setLong(1, contrato.getId());
					rs = ps.executeQuery();
					while (rs.next()) {
						listaAnalise.add(findById(rs.getLong("id")));
					}
				} finally {
					closeResources(connection, ps, rs);

				}
				return listaAnalise;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoAnalise> listagemDocumentoAnaliseReanalise(ContratoCobranca contrato) {
		return (List<DocumentoAnalise>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocumentoAnalise> listaAnalise = new ArrayList<DocumentoAnalise>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_REANALISE);
					ps.setLong(1, contrato.getId());
					rs = ps.executeQuery();
					while (rs.next()) {
						listaAnalise.add(findById(rs.getLong("id")));
					}
				} finally {
					closeResources(connection, ps, rs);

				}
				return listaAnalise;
			}
		});
	}
	
	private static final String QUERY_CERTIDOES_INCOMPLETAS = "select distinct id from (select d.id from cobranca.documentosanalise d \r\n"
			+ " inner join cobranca.contratocobranca c on c.id = d.contratocobranca \r\n"
			+ " inner join cobranca.plexiconsulta p on p.documentoanalise = d.id \r\n"
			+ " where p.requestid is not null and p.pdf is null"
			+ " and p.status not ilike '%expirada%' \r\n"
			+ " union \r\n"
			+ " select d.id from cobranca.documentosanalise d \r\n"
			+ " inner join cobranca.contratocobranca c on c.id = d.contratocobranca \r\n"
			+ " inner join cobranca.netrinconsulta n  on n.documentoanalise = d.id \r\n"
			+ " where n.retorno is not null and n.pdf is null\r\n"
			+ " union \r\n"
			+ " select d.id from cobranca.documentosanalise d \r\n"
			+ " inner join cobranca.contratocobranca c on c.id = d.contratocobranca \r\n"
			+ " inner join cobranca.docketconsulta d2 on d2.documentoanalise = d.id \r\n"
			+ " where d2.iddocket is not null and d2.pdf is null ) total";
	
	@SuppressWarnings("unchecked")
	public List<DocumentoAnalise> listagemCertidoesIncompletas() {
		return (List<DocumentoAnalise>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<DocumentoAnalise> listaAnalise = new ArrayList<DocumentoAnalise>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CERTIDOES_INCOMPLETAS);
					rs = ps.executeQuery();
					while (rs.next()) {
						listaAnalise.add(findById(rs.getLong("id")));
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return listaAnalise;
			}
		});
	}
	
}
