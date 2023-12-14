package com.webnowbr.siscoat.infra.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.infra.db.model.MenuFavorito;
import com.webnowbr.siscoat.infra.db.model.MenuItem;
import com.webnowbr.siscoat.infra.db.model.User;

public class MenuItemDao extends HibernateDao<MenuItem, Long> {
	@SuppressWarnings("unchecked")
	public List<MenuItem> exibeModulo() {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemModulo = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

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
	public List<MenuItem> consultaMenuItem(String tipo, Long idModulo, Long idUsuario) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> listMenuItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append(" select distinct m.id, m.ordem, f.id favorito ");
					query.append("from infra.users u ");
					query.append("inner join infra.user_group ug on u.id  = ug.user_id ");
					query.append("inner join infra.groupadm g on ug.group_id = g.id ");
					query.append(
							"inner join infra.menuitem m on m.permissao = '' or g.acronym = ANY( string_to_array(  replace(m.permissao,' ','') , ',' ) )");
					query.append(
							" left join infra.menufavorito f  on (m.id = f.idmenuitemfavorito) and f.iduser =  u.id ");
					query.append(" where tipo = ? and ( itempai = ? or itempai is null ) and");
					query.append(" u.id = ? ");
					query.append(" order by  m.ordem ");

					ps = connection.prepareStatement(query.toString());
					ps.setString(1, tipo);
					ps.setLong(2, idModulo);
					ps.setLong(3, idUsuario);

					rs = ps.executeQuery();

					MenuItemDao menuDao = new MenuItemDao();

					while (rs.next()) {
						MenuItem menu = menuDao.findById(rs.getLong("id"));
						if (!CommonsUtil.semValor(rs.getLong("favorito")))
							menu.setFavorito(true);
						listMenuItem.add(menu);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return listMenuItem;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem> ConsultaModulo(String tipo, Long idUsuario) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemSubmodulo = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append(" select distinct m.id, m.ordem, f.id favorito ");
					query.append("from infra.users u ");
					query.append("inner join infra.user_group ug on u.id  = ug.user_id ");
					query.append("inner join infra.groupadm g on ug.group_id = g.id ");
					query.append(
							"inner join infra.menuitem m on m.permissao = '' or g.acronym = ANY( string_to_array(  replace(m.permissao,' ','') , ',' ) )");
					query.append(
							" left join infra.menufavorito f  on (m.id = f.idmenuitemfavorito) and f.iduser =  u.id ");
					query.append(" where tipo = ? and");
					query.append(" u.id = ? ");
					query.append(" order by  m.ordem ");

					ps = connection.prepareStatement(query.toString());
					ps.setString(1, tipo);
					ps.setLong(2, idUsuario);

					rs = ps.executeQuery();

					MenuItemDao menuDao = new MenuItemDao();

					while (rs.next()) {
						MenuItem menu = menuDao.findById(rs.getLong("id"));
						if (!CommonsUtil.semValor(rs.getLong("favorito")))
							menu.setFavorito(true);
						menuItemSubmodulo.add(menu);
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
					StringBuilder query = new StringBuilder();

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
					StringBuilder query = new StringBuilder();

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
					StringBuilder query = new StringBuilder();

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
	public MenuFavorito consultaFavorito(MenuItem menu, User user) {

		return (MenuFavorito) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				MenuFavorito menuItemItem = new MenuFavorito();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String QUERY_VERIFICA_FAVORITO = "select  F.id  from infra.menuitem m  left join infra.menufavorito f  on (m.id = f.idmenuitemfavorito) "
						+ "where (iduser = " + user.getId() + " and m.id = " + menu.getId() + ")";
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder(QUERY_VERIFICA_FAVORITO);

					ps = connection.prepareStatement(query.toString());
					rs = ps.executeQuery();

					MenuFavoritoDao menuFavoDao = new MenuFavoritoDao();

					while (rs.next()) {
						menuItemItem = menuFavoDao.findById(rs.getLong(1));
					}

					closeResources(connection, ps, rs);
				} catch (Exception e) {
					return null;
				}
				return menuItemItem;
			}

		});
	}
	@SuppressWarnings("unchecked")
	public List<MenuItem> Consultaitemfavorito(Long idfavorito) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("select id from infra.menuitem ");
					query.append("where id = ? ");
					query.append("order by ordem ");

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, idfavorito);
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
	public List<MenuItem> ConsultaitemConsultadoString(String tipoParametro, String parametro ) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("select id from infra.menuitem ");
					query.append("where " + tipoParametro + " = ? " );

					ps = connection.prepareStatement(query.toString());
					ps.setString(1, parametro);
					
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
	public List<MenuItem> ConsultaitemConsultadoLong(String tipoParametro, Long parametro ) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("select id from infra.menuitem ");
					query.append("where " + tipoParametro + " = ? " );

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, parametro);
					
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
	public List<MenuItem> Consultaitemsubmodulo(Long parametro ) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("select id from infra.menuitem ");
					query.append("where itempai  = ? " );

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, parametro);
					
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
	public List<MenuItem> ConsultaItemMaior(MenuItem itempai, int ordem ) {

		return (List<MenuItem>) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				List<MenuItem> menuItemItem = new ArrayList<MenuItem>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder();

					query.append("select id from infra.menuitem ");
					query.append("where itempai  = ? and ordem >= ? " );

					ps = connection.prepareStatement(query.toString());
					ps.setLong(1, itempai.getId());
					ps.setInt(2, ordem);
					
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
}
