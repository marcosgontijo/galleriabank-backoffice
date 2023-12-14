package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.db.dao.*;

/**
 * DAO access layer for the Tecnico entity
 * 
 * @author hv.junior
 *
 */
public class PagadorRecebedorConsultaDao extends HibernateDao<PagadorRecebedorConsulta, Long> {

	private static final String QUERY_PESQUISA_CONSULTA = "select id from cobranca.pagadorrecebedorconsulta "
			+ "where pessoa = ? and tipo = ? ";

	@SuppressWarnings("unchecked")
	public PagadorRecebedorConsulta getConsultaByPagadorAndTipo(final PagadorRecebedor pagador,
			final DocumentosAnaliseEnum tipoConsulta) {
		
		if ( CommonsUtil.semValor(pagador) )
			return null;
		
		return (PagadorRecebedorConsulta) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedorConsulta pagadorRecebedor = null;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_PESQUISA_CONSULTA);

					ps.setLong(1, pagador.getId());
					ps.setString(2, tipoConsulta.getNome());

					rs = ps.executeQuery();

					if (rs.next()) {
						pagadorRecebedor = findById(rs.getLong(1));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return pagadorRecebedor;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public PagadorRecebedorConsulta getConsultaByPagadorAndTipo(final PagadorRecebedor pagador,
			final DocumentosAnaliseEnum tipoConsulta,final String uf) {
		
		if ( CommonsUtil.semValor(pagador) )
			return null;
		
		return (PagadorRecebedorConsulta) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedorConsulta pagadorRecebedor = null;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_PESQUISA_CONSULTA);

					ps.setLong(1, pagador.getId());
					ps.setString(2, tipoConsulta.getNome() + " " + uf);

					rs = ps.executeQuery();

					if (rs.next()) {
						pagadorRecebedor = findById(rs.getLong(1));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return pagadorRecebedor;
			}
		});
	}
	
	
	private static final String QUERY_PESQUISA_CONSULTA_RETORNO = "select id, retornoConsulta from cobranca.pagadorrecebedorconsulta "
			+ " where pessoa = ?  "
			+ " and tipo = ?"
			+ " and (DATE_PART('day', ? ::timestamp - dataconsulta ) >= 30 or dataconsulta is null)";

	@SuppressWarnings("unchecked")
	public PagadorRecebedorConsulta getConsultaVencidaByPagadorAndRetorno(final PagadorRecebedor pagador,
			final DocumentosAnaliseEnum tipo,
			final String retorno) {
		
		if (CommonsUtil.semValor(pagador))
			return null;
		
		return (PagadorRecebedorConsulta) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedorConsulta pagadorRecebedor = null;
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				java.sql.Date dtHojeSQL = new java.sql.Date(DateUtil.gerarDataHoje().getTime());
				try {
					connection = getConnection();
					String query = QUERY_PESQUISA_CONSULTA_RETORNO;
					String nome = tipo.getNome();
					ps = connection.prepareStatement(query);
					ps.setLong(1, pagador.getId());
					ps.setString(2, nome);
					ps.setDate(3, dtHojeSQL);
					
					rs = ps.executeQuery();

					while (rs.next()) {
						if(CommonsUtil.semValor(rs.getString(2))) {
							pagadorRecebedor = findById(rs.getLong(1));
							closeResources(connection, ps, rs);
							return pagadorRecebedor;
						}
						try {
							/*int levDistance = CommonsUtil.levenshteinDistance(retorno, rs.getString(2));
							int porcent = (((retorno.length() - levDistance) *100)/retorno.length());
							//System.out.println("Levenshtein Distance between is: " + (100 - porcent));
							if(porcent > 90) {*/
							pagadorRecebedor = findById(rs.getLong(1));
							closeResources(connection, ps, rs);
							return pagadorRecebedor;
							//}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return pagadorRecebedor;
			}
		});
	}
	
}
