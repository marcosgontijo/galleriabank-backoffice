package com.webnowbr.siscoat.cobranca.ws.netrin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class NetrinConsultaDao extends HibernateDao<NetrinConsulta, Long> {
	
	private static final String QUERY_CONSULTAS_EXISTENTES = "select n.id from cobranca.netrinConsulta n "
			+ " where n.expirado = false "
			+ " and cpfCnpj = ? "
			+ " and netrinDocumentos = ? "
			+ " and id != ? ";


	@SuppressWarnings("unchecked")
	public List<NetrinConsulta> getConsultasExistentes(NetrinConsulta netrinConsulta) {
		return (List<NetrinConsulta>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<NetrinConsulta> consultas = new ArrayList<NetrinConsulta>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_CONSULTAS_EXISTENTES);
					ps.setString(1, netrinConsulta.getCpfCnpj());
					ps.setLong(2, netrinConsulta.getNetrinDocumentos().getId());
					ps.setLong(3, netrinConsulta.getId());
					rs = ps.executeQuery();
					
					Date dataHj = DateUtil.gerarDataHoje();
					NetrinConsultaDao netrinConsultaDao = new NetrinConsultaDao();				
					
					while (rs.next()) {
						NetrinConsulta consulta = netrinConsultaDao.findById(rs.getLong(1));
						Date dataConsulta = consulta.getDataConsulta();
						if(!CommonsUtil.semValor(dataConsulta)) {
							if(DateUtil.getDaysBetweenDates(dataConsulta, dataHj) > 30) {
								consulta.setExpirado(true);
								netrinConsultaDao.merge(consulta);
							} else {
								if(compararDocumentos(netrinConsulta, consulta)) {
									consultas.add(consulta);
								}
							}
						} else {
							if(compararDocumentos(netrinConsulta, consulta)) {
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
	
	public boolean compararDocumentos(NetrinConsulta netrinConsulta, NetrinConsulta consultaDB) {
		// retorna true se os documentos de nova consulta e ja existente no banco são iguais
		NetrinDocumentos doc = netrinConsulta.getNetrinDocumentos();
		if(CommonsUtil.mesmoValor(doc.getUrlService(), 
				"/api/v1/CNDEstadual")) {
			return (CommonsUtil.mesmoValor(netrinConsulta.getUf(), consultaDB.getUf())); 
		}
		return true;
	}

	private static final String QUERY_GET_NUMCONTRATO_ANALISE= "select c.numerocontrato, d.identificacao  from cobranca.documentoanalise_netrinConsultas_join dpj \r\n"
			+ "inner join cobranca.documentosanalise d on d.id = dpj.iddocumentoanalise \r\n"
			+ "inner join cobranca.contratocobranca c on c.id = d.contratocobranca \r\n"
			+ "where dpj.idnetrinConsulta = ?";

	@SuppressWarnings("unchecked")
	public String getNumeroContratoAnalise(NetrinConsulta netrinConsulta) {
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
					ps.setLong(1, netrinConsulta.getId());
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
	public String getNomeAnalise(NetrinConsulta netrinConsulta) {
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
					ps.setLong(1, netrinConsulta.getId());
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
}
