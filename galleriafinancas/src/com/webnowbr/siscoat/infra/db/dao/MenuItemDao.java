package com.webnowbr.siscoat.infra.db.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.infra.db.model.MenuFavorito;
import com.webnowbr.siscoat.infra.db.model.MenuItem;
import com.webnowbr.siscoat.infra.db.model.User;

public class MenuItemDao extends HibernateDao<MenuItem, Long> {
	@SuppressWarnings("unchecked")
	public List<MenuItem> ExibeModulo() {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemModulo = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from infra.menuitem ");					
					query.append("where tipo = 'Módulo' ");
					query.append("order by ordem ");
							

					ps = connection.prepareStatement(query.toString());

					rs = ps.executeQuery();

					MenuItemDao menuDao = new MenuItemDao();

					while (rs.next()) {
						menuItemModulo.add(menuDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return menuItemModulo;
			}
		});
	}
	@SuppressWarnings("unchecked")
	public List<MenuItem> ConsultaSubmodulo(Long idModulo) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemSubmodulo = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from infra.menuitem ");					
					query.append("where tipo = 'Submódulo' and itempai = ? ");
					query.append("order by ordem ");
							

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, idModulo);

					rs = ps.executeQuery();

					MenuItemDao menuDao = new MenuItemDao();

					while (rs.next()) {
						menuItemSubmodulo.add(menuDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return menuItemSubmodulo;
			}
		});
	}
	@SuppressWarnings("unchecked")
	public List<MenuItem> ConsultaItem(Long idSubmodulo) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from infra.menuitem ");					
					query.append("where tipo = 'Item' and itempai = ? ");
					query.append("order by ordem ");
					

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, idSubmodulo);
					rs = ps.executeQuery();
		

					MenuItemDao menuDao = new MenuItemDao();

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
	@SuppressWarnings("unchecked")
	public List<MenuItem> exibeSubmodulo() {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemSubmodulo = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from infra.menuitem ");					
					query.append("where tipo = 'Submódulo' ");
					query.append("order by ordem ");
							

					ps = connection.prepareStatement(query.toString());

					rs = ps.executeQuery();

					MenuItemDao menuDao = new MenuItemDao();

					while (rs.next()) {
						menuItemSubmodulo.add(menuDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return menuItemSubmodulo;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem> exibeItem() {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder();
					
					query.append("select id from infra.menuitem ");					
					query.append("where tipo = 'Item' ");
					query.append("order by ordem ");
					

					ps = connection.prepareStatement(query.toString());
					rs = ps.executeQuery();
		

					MenuItemDao menuDao = new MenuItemDao();

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
	@SuppressWarnings("unchecked")
	public MenuItem consultaFavorito(MenuItem menu, User user) {

		return (MenuItem) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				MenuItem menuItemItem = new MenuItem();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String QUERY_VERIFICA_FAVORITO = "select f.id, m.id  from infra.menuitem m  left join infra.menufavorito f  on (m.id = f.idmenuitemfavorito) "
									+ "where (iduser = " + user.getId() + " and m.id = " + menu.getId() + ")";
				try {
					connection = getConnection();
					StringBuilder query  = new StringBuilder(QUERY_VERIFICA_FAVORITO);
					
					
					

					ps = connection.prepareStatement(query.toString());
					rs = ps.executeQuery();
		

					MenuItemDao menuDao = new MenuItemDao();

					while(rs.next()) {
						menuItemItem = menuDao.findById(rs.getLong(1));
					}

				
					closeResources(connection, ps, rs);
				}catch (Exception e) {
					return null;
				}
				return menuItemItem;
			}
			
		});
	}
}
