package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.TermoUsuario;
import com.webnowbr.siscoat.infra.db.model.User;

/**
 * DAO access layer for User entity.
 */
public class TermoUsuarioDao extends HibernateDao<TermoUsuario, Long> {

	private static final String QUERY_TERMOS_USUARIO = " select tu.id from infra.termoUsuario tu   "
			+ " where tu.idtermo = ? and tu.idusuario = ? ";

	@SuppressWarnings("unchecked")
	public TermoUsuario termosUsuario(Termo termo, User usuario) {
		return (TermoUsuario) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				TermoUsuario retorno = null;
				if (CommonsUtil.semValor(usuario))
					return new ArrayList<>();

				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_TERMOS_USUARIO);

					ps.setLong(1, termo.getId());
					ps.setLong(2, usuario.getId());

					rs = ps.executeQuery();

					while (rs.next()) {
						retorno = findById(rs.getLong(1));
						break;
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return retorno;
			}
		});
	}
}
