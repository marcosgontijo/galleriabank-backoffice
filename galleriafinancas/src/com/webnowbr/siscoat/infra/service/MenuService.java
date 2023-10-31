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
	private DataTable data = new DataTable();
	
	
	
	
	
	private MenuModel modelCobranca;
	private MenuModel modelAtendimento;
	private MenuModel modelRelatórios;
	private MenuModel modelManutencao;
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
	public MenuModel constroiMenuCobranca() {
		modelCobranca = new DefaultMenuModel();
		DefaultSubMenu Submenu = new DefaultSubMenu();
		MenuItemDao dao = new MenuItemDao();
		DefaultMenuItem menuPrime = new DefaultMenuItem(); 
		List<MenuItem> menusSubmoduloCadastro = dao.ConsultaSubmodulo( (long) 17);
		for(MenuItem sub : menusSubmoduloCadastro) {
			Submenu = new DefaultSubMenu();
			Submenu.setLabel(sub.getNome());
			Submenu.setStyleClass("titulosMenu");
			List<MenuItem> menusItem = dao.ConsultaItem(sub.getId());
			for(MenuItem item : menusItem) {
				
				menuPrime = new DefaultMenuItem();
				menuPrime.setValue(item.getNome());
				menuPrime.setCommand(item.getAcao());
				menuPrime.setIcon(item.getIcone());
				menuPrime.setStyleClass("itemMenu");
				Submenu.addElement(menuPrime);
			}
			modelCobranca.addElement(Submenu);
		}
		return modelCobranca;
	}
	public MenuModel constroiMenuAtendimento() {
		modelAtendimento = new DefaultMenuModel();
		DefaultSubMenu primeiroSubmenu = new DefaultSubMenu();
		MenuItemDao dao = new MenuItemDao();
		DefaultMenuItem menuPrime = new DefaultMenuItem(); 
		List<MenuItem> menusSubmoduloCadastro = dao.ConsultaSubmodulo( (long) 18);
		for(MenuItem sub : menusSubmoduloCadastro) {
			primeiroSubmenu = new DefaultSubMenu();
			primeiroSubmenu.setLabel(sub.getNome());
			primeiroSubmenu.setStyleClass("titulosMenu");
			List<MenuItem> menusItem = dao.ConsultaItem(sub.getId());
			for(MenuItem item : menusItem) {
				
				menuPrime = new DefaultMenuItem();
				menuPrime.setValue(item.getNome());
				menuPrime.setCommand(item.getAcao());
				menuPrime.setIcon(item.getIcone());
				menuPrime.setStyleClass("itemMenu");
				primeiroSubmenu.addElement(menuPrime);
			}
			modelAtendimento.addElement(primeiroSubmenu);
		}
		return modelAtendimento;
	}

	public MenuModel constroiMenuRelatório() {
		modelRelatórios = new DefaultMenuModel();
		DefaultSubMenu primeiroSubmenu = new DefaultSubMenu();
		MenuItemDao dao = new MenuItemDao();
		DefaultMenuItem menuPrime = new DefaultMenuItem(); 
		List<MenuItem> menusSubmoduloCadastro = dao.ConsultaSubmodulo( (long) 22);
		for(MenuItem sub : menusSubmoduloCadastro) {
			primeiroSubmenu = new DefaultSubMenu();
			primeiroSubmenu.setLabel(sub.getNome());
			primeiroSubmenu.setStyleClass("titulosMenu");
			List<MenuItem> menusItem = dao.ConsultaItem(sub.getId());
			for(MenuItem item : menusItem) {
				
				menuPrime = new DefaultMenuItem();
				menuPrime.setValue(item.getNome());
				menuPrime.setCommand(item.getAcao());
				menuPrime.setIcon(item.getIcone());
				menuPrime.setStyleClass("itemMenu");
				primeiroSubmenu.addElement(menuPrime);
			}
			modelRelatórios.addElement(primeiroSubmenu);
		}
		return modelRelatórios;
	}
	
	public MenuModel constroiMenuManutencao() {
		modelManutencao = new DefaultMenuModel();
		DefaultSubMenu primeiroSubmenu = new DefaultSubMenu();
		MenuItemDao dao = new MenuItemDao();
		DefaultMenuItem menuPrime = new DefaultMenuItem(); 
		List<MenuItem> menusSubmoduloCadastro = dao.ConsultaSubmodulo( (long) 23);
		for(MenuItem sub : menusSubmoduloCadastro) {
			primeiroSubmenu = new DefaultSubMenu();
			primeiroSubmenu.setLabel(sub.getNome());
			primeiroSubmenu.setStyleClass("titulosMenu");
			List<MenuItem> menusItem = dao.ConsultaItem(sub.getId());
			for(MenuItem item : menusItem) {
				
				menuPrime = new DefaultMenuItem();
				menuPrime.setValue(item.getNome());
				menuPrime.setCommand(item.getAcao());
				menuPrime.setIcon(item.getIcone());
				menuPrime.setStyleClass("itemMenu");
				primeiroSubmenu.addElement(menuPrime);
			}
			modelManutencao.addElement(primeiroSubmenu);
		}
		return modelManutencao;
	}
public MenuModel constroiFavoritarCadastro() {
	modelFavoritos = new DefaultMenuModel();
	DefaultSubMenu primeiroSubmenu = new DefaultSubMenu();
	MenuItemDao dao = new MenuItemDao();
	DefaultMenuItem menuPrime = new DefaultMenuItem(); 
	List<MenuItem> menusSubmoduloCadastro = dao.exibeSubmodulo();
	for(MenuItem sub : menusSubmoduloCadastro) {
		primeiroSubmenu = new DefaultSubMenu();
		primeiroSubmenu.setLabel(sub.getNome());
		primeiroSubmenu.setStyleClass("titulosMenu");
		List<MenuItem> menusItem = dao.ConsultaItem(sub.getId());
		for(MenuItem item : menusItem) {
			
			menuPrime = new DefaultMenuItem();
			menuPrime.setValue(item.getNome());
			menuPrime.setCommand("#{menuItemMB.clearFieldsFavorito(" + item.getId() + ")}");
			menuPrime.setIcon("ui-icon-star");
			menuPrime.setStyleClass("itemMenu");
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
			DefaultSubMenu	submenu = new DefaultSubMenu();
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
			submenu.addElement(menuPrime);


		}

	}
	return modelFavoritoUsuario;
}
	


	
	public void constroiMenuIndex() {
		modelAtendimento = new DefaultMenuModel();
	}

	public MenuModel getModel() {
		return modelCobranca;
	}

	public void setModel(DefaultMenuModel model) {
		this.modelCobranca = model;
	}




	public MenuModel getModelAtendimento() {
		return modelAtendimento;
	}




	public void setModelindex(MenuModel modelindex) {
		this.modelAtendimento = modelindex;
	}
	public MenuModel getModelRelatórios() {
		return modelRelatórios;
	}
	public void setModelRelatórios(MenuModel modelRelatórios) {
		this.modelRelatórios = modelRelatórios;
	}
	public MenuModel getModelManutencao() {
		return modelManutencao;
	}
	public void setModelManutencao(MenuModel modelManutencao) {
		this.modelManutencao = modelManutencao;
	}
	public DataTable getData() {
		return data;
	}
	public void setData(DataTable data) {
		this.data = data;
	}
	public MenuModel getModelFavoritos() {
		return modelFavoritos;
	}
	public void setModelFavoritos(MenuModel modelFavoritos) {
		this.modelFavoritos = modelFavoritos;
	}
	public MenuModel getModelFavoritoUsuario() {
		return modelFavoritoUsuario;
	}
	public void setModelFavoritoUsuario(MenuModel modelFavoritoUsuario) {
		this.modelFavoritoUsuario = modelFavoritoUsuario;
	}
	
	

}
