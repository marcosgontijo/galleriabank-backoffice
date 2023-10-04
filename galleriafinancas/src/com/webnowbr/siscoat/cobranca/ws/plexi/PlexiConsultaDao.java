package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class PlexiConsultaDao extends HibernateDao<PlexiConsulta, Long> {
	
	private static final String QUERY_CONSULTAS_EXISTENTES = "select p.id from cobranca.plexiConsulta p "
			+ " where p.expirado = false "
			+ " and cpfCnpj = ? "
			+ " and plexiDocumentos = ? "
			+ " and id != ? ";


	@SuppressWarnings("unchecked")
	public List<PlexiConsulta> getConsultasExistentes(PlexiConsulta plexiConsulta) {
		return (List<PlexiConsulta>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PlexiConsulta> consultas = new ArrayList<PlexiConsulta>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_CONSULTAS_EXISTENTES);
					ps.setString(1, plexiConsulta.getCpfCnpj());
					ps.setLong(2, plexiConsulta.getPlexiDocumentos().getId());
					ps.setLong(3, plexiConsulta.getId());
					rs = ps.executeQuery();
					
					Date dataHj = DateUtil.gerarDataHoje();
					PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();				
					
					while (rs.next()) {
						PlexiConsulta consulta = plexiConsultaDao.findById(rs.getLong(1));
						Date dataConsulta = consulta.getDataConsulta();
						if(!CommonsUtil.semValor(dataConsulta)) {
							if(DateUtil.getDaysBetweenDates(dataConsulta, dataHj) > 30) {
								consulta.setExpirado(true);
								plexiConsultaDao.merge(consulta);
							} else {
								if(compararDocumentos(plexiConsulta, consulta)) {
									consultas.add(consulta);
								}
							}
						} else {
							if(compararDocumentos(plexiConsulta, consulta)) {
								consultas.add(consulta);
							}
						}
						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return consultas;
			}
		});
	}
	
	public boolean compararDocumentos(PlexiConsulta plexiConsulta, PlexiConsulta consultaDB) {
		// retorna true se os documentos de nova consulta e ja existente no banco são iguais
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjdft/certidao-distribuicao")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipoCertidao(), consultaDB.getTipoCertidao())); 
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrj/consulta-processual")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getOrigem(), consultaDB.getOrigem())
				&& CommonsUtil.mesmoValor(plexiConsulta.getComarca(), consultaDB.getComarca())
				&& CommonsUtil.mesmoValor(plexiConsulta.getCompetencia(), consultaDB.getCompetencia()));
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipo(), consultaDB.getTipo()));
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getModelo(), consultaDB.getModelo()));			
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf1/certidao-distribuicao")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipo(), consultaDB.getTipo())
					&& CommonsUtil.mesmoValor(plexiConsulta.getOrgaos(), consultaDB.getOrgaos()));
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf2/certidao-distribuicao")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipo(), consultaDB.getTipo()));
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf3/certidao-distribuicao")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipo(), consultaDB.getTipo())
					&& CommonsUtil.mesmoValor(plexiConsulta.getAbrangencia(), consultaDB.getAbrangencia()));
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf4/certidao-regional")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipo(), consultaDB.getTipo()));
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf6/certidao-distribuicao")) {
			return (CommonsUtil.mesmoValor(plexiConsulta.getTipo(), consultaDB.getTipo())
					&& CommonsUtil.mesmoValor(plexiConsulta.getOrgaos(), consultaDB.getOrgaos()));
		}
		return true;
	}

	private static final String QUERY_GET_NUMCONTRATO_ANALISE= "select c.numerocontrato, d.identificacao  from cobranca.documentoanalise_plexiconsultas_join dpj \r\n"
			+ "inner join cobranca.documentosanalise d on d.id = dpj.iddocumentoanalise \r\n"
			+ "inner join cobranca.contratocobranca c on c.id = d.contratocobranca \r\n"
			+ "where dpj.idplexiconsulta = ?";


	@SuppressWarnings("unchecked")
	public String getNumeroContratoAnalise(PlexiConsulta plexiConsulta) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String retorno = "";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_GET_NUMCONTRATO_ANALISE);
					ps.setLong(1, plexiConsulta.getId());
					rs = ps.executeQuery();	
					
					while (rs.next()) {
						retorno = rs.getString("numerocontrato");						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return retorno;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public String getNomeAnalise(PlexiConsulta plexiConsulta) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String retorno = "";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_GET_NUMCONTRATO_ANALISE);
					ps.setLong(1, plexiConsulta.getId());
					rs = ps.executeQuery();	
					
					while (rs.next()) {
						retorno = rs.getString("identificacao");						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return retorno;
			}
		});
	}
	
	private static final String QUERY_GET_ADD_DOCANALISE= "select * from cobranca.documentoanalise_plexiconsultas_join dpj";			;

	@SuppressWarnings("unchecked")
	public void addDocumentoAnalise() {
		 executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_GET_ADD_DOCANALISE);
					rs = ps.executeQuery();	
					PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
					DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
					PlexiConsulta plexiConsulta;
					DocumentoAnalise documentoAnalise;
					while (rs.next()) {
						plexiConsulta = plexiConsultaDao.findById(rs.getLong("idplexiconsulta"));
						documentoAnalise = documentoAnaliseDao.findById(rs.getLong("iddocumentoanalise"));
						plexiConsulta.setDocumentoAnalise(documentoAnalise);
						plexiConsultaDao.merge(plexiConsulta);
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return null;
			}
		});
	}
}
