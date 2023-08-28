package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
								consultas.add(consulta);
							}
						} else {
							consultas.add(consulta);
						}
						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return consultas;
			}
		});
	}
}
