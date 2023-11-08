package com.webnowbr.siscoat.infra.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedProperty;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.MenuFavoritoDao;
import com.webnowbr.siscoat.infra.db.dao.MenuItemDao;
import com.webnowbr.siscoat.infra.db.model.MenuFavorito;
import com.webnowbr.siscoat.infra.db.model.MenuItem;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

public class MenuService {
	private MenuModel modelMenu;
	private MenuModel modelFavoritos;
	private MenuModel modelFavoritoUsuario;

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public MenuModel constroiMenu(Long idModulo, Long idUsuario, boolean montaFavorito) {
		modelMenu = new DefaultMenuModel();
		DefaultSubMenu primeiroSubmenu = new DefaultSubMenu();
		MenuItemDao dao = new MenuItemDao();
		DefaultMenuItem menuPrime = new DefaultMenuItem();
		List<MenuItem> menusSubmoduloCadastro = dao.consultaMenuItem("Submódulo", idModulo, idUsuario);
		for (MenuItem sub : menusSubmoduloCadastro) {
			primeiroSubmenu = new DefaultSubMenu();
			primeiroSubmenu.setLabel(sub.getNome());
			primeiroSubmenu.setStyleClass("titulosMenu");
			List<MenuItem> menusItem = dao.consultaMenuItem("Item", sub.getId(), idUsuario);
			for (MenuItem item : menusItem) {

				menuPrime = new DefaultMenuItem();
				menuPrime.setValue(item.getNome());
				menuPrime.setCommand(item.getAcao());
				if( item.isFavorito())
					menuPrime.setIcon("ui-icon-star");
				else
					menuPrime.setIcon(item.getIcone());
				menuPrime.setStyleClass("itemMenu");
				if(montaFavorito) {
					menuPrime.setCommand("#{menuItemMB.clearFieldsFavorito(" + item.getId() + ")}");
				}else {
					menuPrime.setCommand(item.getAcao());
				}
				menuPrime.setAjax(montaFavorito);
				
				menuPrime.setUpdate("@all");
				primeiroSubmenu.addElement(menuPrime);
			}
			modelMenu.addElement(primeiroSubmenu);
		}
		return modelMenu;
	}

	public MenuModel constroiFavoritar(Long idModulo, Long idUsuario) {
		modelFavoritos = new DefaultMenuModel();
		DefaultSubMenu primeiroSubmenu = new DefaultSubMenu();
		MenuItemDao dao = new MenuItemDao();
		DefaultMenuItem menuPrime = new DefaultMenuItem();
		List<MenuItem> menusSubmoduloCadastro = dao.consultaMenuItem("Submódulo", idModulo, idUsuario);
		for (MenuItem sub : menusSubmoduloCadastro) {
			primeiroSubmenu = new DefaultSubMenu();
			primeiroSubmenu.setLabel(sub.getNome());
			
			primeiroSubmenu.setStyleClass("titulosMenu");
			List<MenuItem> menusItem = dao.ConsultaItem(sub.getId());
			for (MenuItem item : menusItem) {
				
				menuPrime = new DefaultMenuItem();
				menuPrime.setValue(item.getNome());
				menuPrime.setCommand("#{menuItemMB.clearFieldsFavorito(" + item.getId() + ")}");
				menuPrime.setIcon("ui-icon-star");
				menuPrime.setStyleClass("itemMenu");
				menuPrime.setAjax(false);
				menuPrime.setUpdate("@all");
				primeiroSubmenu.addElement(menuPrime);
			}
			modelFavoritos.addElement(primeiroSubmenu);
		}
		return modelFavoritos;

	}

	public MenuModel carregaFavoritos(User user) {
		List<MenuItem> listasubmenu = new ArrayList<>();
		List<MenuItem> menuitem = new ArrayList<>();
		modelFavoritoUsuario = new DefaultMenuModel();
		DefaultMenuItem menuPrime = new DefaultMenuItem();
		MenuFavoritoDao favoritoDao = new MenuFavoritoDao();
		MenuItemDao itemDao = new MenuItemDao();
		List<MenuFavorito> favorito = favoritoDao.findByFilter("user", user);
		for (MenuFavorito menu : favorito) {
			menuitem = itemDao.findByFilter("id", menu.getMenuItemFavorito().getId());
			for (MenuItem item : menuitem) {
				DefaultSubMenu submenu = new DefaultSubMenu();
				Optional<MenuElement> menuElement = modelFavoritoUsuario.getElements().stream()
						.filter(e -> ((DefaultSubMenu) e).getLabel().equals(item.getItemPai().getNome())).findFirst();
				if (!menuElement.isPresent()) {
					submenu.setLabel(item.getItemPai().getNome());
					submenu.setStyleClass("titulosMenu");
					modelFavoritoUsuario.addElement(submenu);
				} else {
					submenu = (DefaultSubMenu) menuElement.get();
				}

				menuPrime = new DefaultMenuItem();

				menuPrime.setValue(item.getNome());
				menuPrime.setCommand(item.getAcao());
				menuPrime.setIcon(item.getIcone());
				menuPrime.setStyleClass("itemMenu");
				menuPrime.setAjax(false);
				submenu.addElement(menuPrime);

			}

		}
		return modelFavoritoUsuario;
	}

}
