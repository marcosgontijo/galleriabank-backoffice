package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;


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
	
	@SuppressWarnings("unchecked")
	public List<User> findUsersByTermoId(Long idtermo) {
	    return (List<User>) executeDBOperation(new DBRunnable() {
	        @Override
	        public Object run() throws Exception {
	            Connection connection = null;
	            PreparedStatement ps = null;
	            ResultSet rs = null;
	            UserDao userDao = new UserDao();

	            List<User> users = new ArrayList<>();

	            try {
	                connection = getConnection();
	                ps = connection.prepareStatement("SELECT idusuario FROM infra.termoUsuario WHERE idtermo = ?");
	                ps.setLong(1, idtermo);
	                rs = ps.executeQuery();

	                while (rs.next()) {
	                    long userId = rs.getLong("idusuario");
	                    User user = userDao.findById(userId); 
	                    if (user != null) {
	                        users.add(user);
	                    }
	                }
	            } finally {
	                closeResources(connection, ps, rs);
	            }

	            return users;
	        }
	    });
	}

	public TermoUsuario findTermoUsuario(Long idTermo, Long idUsuario) {
        return (TermoUsuario) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() throws Exception {
                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                TermoUsuario termoUsuario = null;

                try {
                    connection = getConnection();

                    String query = "SELECT * FROM infra.termoUsuario WHERE idtermo = ? AND idusuario = ?";
                    ps = connection.prepareStatement(query);
                    ps.setLong(1, idTermo);
                    ps.setLong(2, idUsuario);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        termoUsuario = new TermoUsuario();
                        termoUsuario.setId(rs.getLong("id"));
                        termoUsuario.setIdTermo(rs.getLong("idtermo"));
                        termoUsuario.setIdUsuario(rs.getLong("idusuario"));
       
                    }
                } finally {
                    closeResources(connection, ps, rs);
                }

                return termoUsuario;
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
