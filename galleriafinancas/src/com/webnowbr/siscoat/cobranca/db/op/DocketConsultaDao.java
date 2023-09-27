package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocketConsulta;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class DocketConsultaDao extends HibernateDao<DocketConsulta, Long> {
	
	private static final String QUERY_CONSULTAS_EXISTENTES = "select d.id from cobranca.DocketConsulta d "
			+ " where d.expirado = false "
			+ " and cpfCnpj = ? "
			+ " and docketdocumentos = ? "
			+ " and id != ? ";


	@SuppressWarnings("unchecked")
	public List<DocketConsulta> getConsultasExistentes(DocketConsulta DocketConsulta) {
		return (List<DocketConsulta>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DocketConsulta> consultas = new ArrayList<DocketConsulta>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_CONSULTAS_EXISTENTES);
					ps.setString(1, DocketConsulta.getCpfCnpj());
					ps.setLong(2, DocketConsulta.getDocketDocumentos().getId());
					ps.setLong(3, DocketConsulta.getId());
					rs = ps.executeQuery();
					
					Date dataHj = DateUtil.gerarDataHoje();
					DocketConsultaDao DocketConsultaDao = new DocketConsultaDao();				
					
					while (rs.next()) {
						DocketConsulta consulta = DocketConsultaDao.findById(rs.getLong(1));
						Date dataConsulta = consulta.getDataConsulta();
						if(!CommonsUtil.semValor(dataConsulta)) {
							if(DateUtil.getDaysBetweenDates(dataConsulta, dataHj) > 30) {
								consulta.setExpirado(true);
								DocketConsultaDao.merge(consulta);
							} else {
								if(compararDocumentos(DocketConsulta, consulta)) {
									consultas.add(consulta);
								}
							}
						} else {
							if(compararDocumentos(DocketConsulta, consulta)) {
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
	
	private static final String QUERY_CONSULTAS_EXISTENTES_WEBHOOK = "select d.id from cobranca.DocketConsulta d "
			+ " where d.idDocket = ? ";
	
	@SuppressWarnings("unchecked")
	public DocketConsulta getConsultasExistentesWebhook(String idDocket) {
		return (DocketConsulta) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				DocketConsulta consulta = null;
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONSULTAS_EXISTENTES_WEBHOOK);
					ps.setString(1, idDocket);
					rs = ps.executeQuery();
					DocketConsultaDao DocketConsultaDao = new DocketConsultaDao();						
					while (rs.next()) {
						consulta = DocketConsultaDao.findById(rs.getLong(1));
						return consulta;						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return consulta;
			}
		});
	}
	
	public boolean compararDocumentos(DocketConsulta DocketConsulta, DocketConsulta consultaDB) {
		// retorna true se os documentos de nova consulta e ja existente no banco são iguais
		DocumentosDocket doc = DocketConsulta.getDocketDocumentos();
		if(!doc.getDocumentoNome().contains("Federal")) {
			return (CommonsUtil.mesmoValor(DocketConsulta.getUf(), consultaDB.getUf())); 
		}
		return true;
	}

}
