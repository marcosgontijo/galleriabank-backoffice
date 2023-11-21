package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;
import com.webnowbr.siscoat.infra.db.model.MenuFavorito;
import com.webnowbr.siscoat.infra.db.model.MenuItem;
import com.webnowbr.siscoat.infra.db.model.User;

public class MenuFavoritoDao extends HibernateDao<MenuFavorito, Long> {
	@SuppressWarnings("unchecked")
	public boolean consultaMenu(User user, MenuItem menu) {

			return (Boolean)	executeDBOperation(new DBRunnable() {

			public Object run() throws Exception {

				Connection connection = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("delete from infra.menufavorito ");					
					query.append("where idmenuitemfavorito = " + menu.getId());
					query.append(" and iduser = " + user.getId());
							

					ps = connection.prepareStatement(query.toString());

					ps.executeUpdate();
					return true;
					
				}finally {
					closeResources(connection, ps);
				}
				
			}
		});
	}

}




