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

	private static final String QUERY_TERMOS_NAO_ASSINADOS_USUARIO = " select t.id from infra.termo t "
			+ " left join infra.termoUsuario tu on t.id = tu.idtermo and tu.idusuario = ? "
			+ " where t.iduserPerfil <= ? "  
			+ " and ( tu.idtermo is null "
			+ " or tu.dataAceite is null )"
			+ " and( tu.dataAdiado is null or "
			+ " cast(tu.dataAdiado as date) < cast( ?::timestamp as date)) ";

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
					else
						ps.setLong(2, 1000);
					java.sql.Date dtRelInicioSQL = new java.sql.Date(DateUtil.getDataHoje().getTime());
					ps.setDate(3, dtRelInicioSQL);

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
}
