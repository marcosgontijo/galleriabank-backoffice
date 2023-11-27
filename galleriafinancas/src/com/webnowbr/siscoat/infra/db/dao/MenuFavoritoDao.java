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
	public boolean apagaMenu(User user, MenuItem menu) {

		return (Boolean) executeDBOperation(new DBRunnable() {

			public Object run() throws Exception {

				Connection connection = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("delete from infra.menufavorito ");
					query.append("where idmenuitemfavorito = " + menu.getId());
					query.append(" and iduser = " + user.getId());

					ps = connection.prepareStatement(query.toString());

					ps.executeUpdate();
					return true;

				} finally {
					closeResources(connection, ps);
				}

			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<MenuFavorito> ConsultaFavoritoUsuario(User User) {

		return (List<MenuFavorito>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuFavorito> menuItemItem = new ArrayList<MenuFavorito>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("select m.id from infra.menufavorito m ");
					query.append("inner join infra.menuitem i on m.idmenuitemfavorito = i.id ");
					query.append("inner join infra.menuitem p on i.itemPai = p.id ");
					query.append("where iduser = ? ");
					query.append("order by p.ordem,i.ordem ");

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, User.getId());
					rs = ps.executeQuery();

					MenuFavoritoDao menuDao = new MenuFavoritoDao();

					while (rs.next()) {
						menuItemItem.add(menuDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return menuItemItem;
			}
		});
	}
}
