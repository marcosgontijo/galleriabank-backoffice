package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.User;

/**
 * DAO access layer for User entity.
 */
public class TermoDao extends HibernateDao<Termo, Long> {

	private static final String QUERY_TERMOS_NAO_ASSINADOS_USUARIO = " select t.id from infra.termo t "//
			+ " left join infra.termoUsuario tu on t.id = tu.idtermo and tu.idusuario = ? "//
			+ " where (t.iduserPerfil <= ? "//
			+ " and ( tu.idtermo is null "//
			+ " or tu.dataAceite is null )"//
			+ " and( tu.dataAdiado is null or "//
			+ " cast(tu.dataAdiado as date) < cast( ?::timestamp as date)) "//
			+ "and cast(t.inicioValidade as date) <= cast(?::timestamp as date) " //
			+ "and (CAST(t.fimValidade AS DATE) >= CAST(?::TIMESTAMP AS DATE) OR t.fimValidade IS NULL))  "//
			+ "or (t.iduserPerfil = 5000 and tu.idTermo is not null and tu.dataAceite is null "//
			+ "and( tu.dataAdiado is null or "//
			+ " cast(tu.dataAdiado as date) < cast( ?::timestamp as date)) "//
			+ "and cast(t.inicioValidade as date) <= cast(?::timestamp as date) " //
			+ "and cast(t.fimValidade as date) >= cast(?::timestamp as date))";//

	@SuppressWarnings("unchecked")
	public List<Termo> termosNaoAssinadosUsuario(User usuario) {
		return (List<Termo>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				List<Termo> retorno = new ArrayList<>();
				if (CommonsUtil.semValor(usuario))
					return new ArrayList<>();

				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_TERMOS_NAO_ASSINADOS_USUARIO);

					ps.setLong(1, usuario.getId());
					// se nao tiver perfil no usuario usa o publico
					if (!CommonsUtil.semValor(usuario.getUserPerfil()))
						ps.setLong(2, usuario.getUserPerfil().getId());
//					else if(usuario.getUserPerfil().getId() ==  5000){
//						ps.setLong(2, usuario.getId());
//					}
					else
						ps.setLong(2, 1000);
					java.sql.Date dtRelInicioSQL = new java.sql.Date(DateUtil.getDataHoje().getTime());
					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelInicioSQL);
					ps.setDate(5, dtRelInicioSQL);
					ps.setDate(6, dtRelInicioSQL);
					ps.setDate(7, dtRelInicioSQL);
					ps.setDate(8, dtRelInicioSQL);

					rs = ps.executeQuery();

					while (rs.next()) {

						retorno.add(findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return retorno;
			}
		});
	}
	@SuppressWarnings("unchecked")
	public List<Termo> termosAssinados (User usuario) {
		return (List<Termo>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				List<Termo> retorno = new ArrayList<>();
				if (CommonsUtil.semValor(usuario))
					return new ArrayList<>();

			
					connection = getConnection();
					StringBuilder query = new StringBuilder();
					query.append("select t.id" +
							" from infra.termo t" + 
							" inner join infra.termousuario t2 on t2.idtermo = t.id" +
							" where t2.idusuario  = ?" );

					ps = connection.prepareStatement(query.toString());

				
					ps.setLong(1, usuario.getId());

					rs = ps.executeQuery();

					while (rs.next()) {
						TermoDao termoDao = new TermoDao();
					retorno.add(termoDao.findById(rs.getLong(1)));
					}
				
				return retorno;
			}
		});
	}
}
