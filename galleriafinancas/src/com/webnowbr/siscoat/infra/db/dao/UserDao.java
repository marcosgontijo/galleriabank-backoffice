package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.*;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.User;

/**
 * DAO access layer for User entity.
 */
public class UserDao extends HibernateDao<User, Long> {


	private final String QUERY_LISTA_RESPONSAVEL = "select u.id, u.login , r.id  from infra.users u \r\n"
			+ "inner join cobranca.responsavel r on r.codigo = u.codigoresponsavel \r\n" + "where u.id not in (\r\n"
			+ "inner join cobranca.responsavel r on r.codigo = u.codigoresponsavel \r\n" + "where u.id not in (\r\n"
			+ "select u2.id from infra.users u2 \r\n"
			+ "	inner join infra.usuario_responsavel_join urj on urj.idusuario = u2.id\r\n" + ")\r\n";

	@SuppressWarnings("unchecked")
	public List<User> carregarUsuariosLista() {
		
		return (List<User>)executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				List<User> usuarios = new ArrayList<>();
				try {
					connection = getConnection();

					ps = connection.prepareStatement("SELECT id, name FROM infra.users WHERE iduserperfil > 0 AND iduserperfil IS NOT NULL");
					rs = ps.executeQuery();

					while (rs.next()) {
						User user = new User();
						user.setId(rs.getLong("id"));
						user.setName(rs.getString("name"));
						usuarios.add(user);
						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return usuarios;
			}
		});

	}

			+ "	inner join infra.usuario_responsavel_join urj on urj.idusuario = u2.id\r\n" + ")\r\n";

	@SuppressWarnings("unchecked")
	public List<User> carregarUsuariosLista() {
		
		return (List<User>)executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				List<User> usuarios = new ArrayList<>();
				try {
					connection = getConnection();

					ps = connection.prepareStatement("SELECT id, name FROM infra.users WHERE iduserperfil > 0 AND iduserperfil IS NOT NULL");
					rs = ps.executeQuery();

					while (rs.next()) {
						User user = new User();
						user.setId(rs.getLong("id"));
						user.setName(rs.getString("name"));
						usuarios.add(user);
						
					}
				} finally {
					closeResources(connection, ps, rs);
				}
				return usuarios;
			}
		});

	}

	public void popularListaResponsavel() {
		executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_LISTA_RESPONSAVEL);

					ps = connection.prepareStatement(QUERY_LISTA_RESPONSAVEL);
					rs = ps.executeQuery();
					User user = new User();
					UserDao uDao = new UserDao();
					ResponsavelDao rDao = new ResponsavelDao();
					while (rs.next()) {
						user = uDao.findById(rs.getLong(1));
						if (CommonsUtil.semValor(user.getListResponsavel())) {
						if (CommonsUtil.semValor(user.getListResponsavel())) {
							user.setListResponsavel(new ArrayList<>());
						}
						if (!user.getListResponsavel().contains(rDao.findById(rs.getLong(3)))) {
						if (!user.getListResponsavel().contains(rDao.findById(rs.getLong(3)))) {
							user.getListResponsavel().add(rDao.findById(rs.getLong(3)));
							uDao.merge(user);
							System.out.print("User" + rs.getLong(1) + "att / ");
						}
					}


				} finally {
					closeResources(connection, ps, rs);
					closeResources(connection, ps, rs);
				}
				return 0;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<User> PesquisaUserPorPerfil (long idUserPerfil) {
		return (List<User>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				List<User> retorno = new ArrayList<>();
			

			try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();
					if(idUserPerfil == 0) {
						query.append("select u.id from infra.users u");
					}
					else {
					query.append("select u.id from infra.users u  where u.iduserperfil = " + idUserPerfil );
					}
					ps = connection.prepareStatement(query.toString());

					rs = ps.executeQuery();

					while (rs.next()) {
						UserDao userDao = new UserDao();
					retorno.add(userDao.findById(rs.getLong(1)));
					}
			}catch (Exception e) {
				e.printStackTrace();
			}
				
				return retorno;
			
		}
	});
	
}
	



	private final String QUERY_LISTA_RESP = "select	r.id, r.nome  from 	cobranca.responsavel r"
			+ " inner join cobranca.responsavel r2 on r2.id = r.donoresponsavel  where 	r2.codigo  = ?";

	@SuppressWarnings("unchecked")
	public void carregarListaResponsavel(User u) {
		executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();


					String query_QUERY_GET_GUARDA_CHUVA = QUERY_LISTA_RESP;

					ps = connection.prepareStatement(query_QUERY_GET_GUARDA_CHUVA);

					ps = connection.prepareStatement(query_QUERY_GET_GUARDA_CHUVA);
					ps.setString(1, u.getCodigoResponsavel());
					rs = ps.executeQuery();


					ResponsavelDao rDao = new ResponsavelDao();
					while (rs.next()) {
						// if(!u.getListResponsavel().contains(rs.getLong("id"))) {
						// if(!u.getListResponsavel().contains(rs.getLong("id"))) {
						Responsavel r = rDao.findById(rs.getLong("id"));
						// }

						if (!u.getListResponsavel().contains(r)) {
						// }

						if (!u.getListResponsavel().contains(r)) {
							u.getListResponsavel().add(r);
						}
						}
					}


					UserDao uDao = new UserDao();
					uDao.merge(u);
				} finally {
					closeResources(connection, ps, rs);
					closeResources(connection, ps, rs);
				}
				return null;
			}
		});
		});
	}
}
		
