package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.db.dao.*;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;
import com.webnowbr.siscoat.infra.db.model.User;

/**
 * DAO access layer for User entity.
 */
public class UserDao extends HibernateDao<User, Long> {
	
	private final String QUERY_LISTA_RESPONSAVEL = "select u.id idusuario, u.\"name\",  r.id idresponsavel, r.nome, r.donoresponsavel, r2.nome  from cobranca.responsavel r "
			+ "inner join cobranca.responsavel r2 on r2.id = r.donoresponsavel "
			+ "inner join infra.users u on u.codigoresponsavel = r2.codigo "
			+ "order by u.id "; 
	
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
					rs = ps.executeQuery();
					User user = new User();
					UserDao uDao = new UserDao();
					ResponsavelDao rDao = new ResponsavelDao();
					while (rs.next()) {
						user = uDao.findById(rs.getLong(1));
						if(!user.getListResponsavel().contains(rDao.findById(rs.getLong(3)))) {
							user.getListResponsavel().add(rDao.findById(rs.getLong(3)));
							uDao.merge(user);
							System.out.println("Responsacvel Adicionado");
						}
					}
					
				} finally {
					closeResources(connection, ps, rs);					
				}
				return 0;
			}
		});	
	}	
}
