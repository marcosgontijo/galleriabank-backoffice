package com.webnowbr.siscoat.infra.mb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.cobranca.db.op.ContaContabilDao;
import com.webnowbr.siscoat.infra.db.dao.MenuFavoritoDao;
import com.webnowbr.siscoat.infra.db.dao.MenuItemDao;
import com.webnowbr.siscoat.infra.db.model.MenuFavorito;
import com.webnowbr.siscoat.infra.db.model.MenuItem;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.service.MenuService;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "menuItemMB")
@SessionScoped
public class MenuItemMB {
	private MenuItemDao dao = new MenuItemDao();
	private MenuItem objetoMenuItem = new MenuItem();
	private MenuItem objetoItemFavorito;
	private MenuFavorito objetoMenuFavorito = new MenuFavorito();
	private List<MenuItem> listaMenuItem = new ArrayList<>();
	private List<MenuItem> menuItemModulo = dao.ExibeModulo();
	private List<MenuItem> menuItemSubmodulo = dao.exibeSubmodulo();
	private List<MenuItem> menuItemItem = dao.exibeItem();
	private MenuItem menu = new MenuItem();
	private MenuService menuService = new MenuService();
	private MenuModel modelCadastro = menuService.constroiMenuCobranca();
	private MenuModel modelAtendimento = menuService.constroiMenuAtendimento();
	private MenuModel modelRelatorio = menuService.constroiMenuRelatório();
	private MenuModel modelManutencao = menuService.constroiMenuManutencao();
	private MenuModel modelFavoritarCadastro = menuService.constroiFavoritarCadastro();
	private MenuModel modelFavoritarAtendimento = menuService.constroiFavoritarAtendimento();
	private MenuModel modelFavoritarRelatorio = menuService.constroiFavoritarRelatorio();
	private MenuModel modelFavoritarManutencao = menuService.constroiFavoritarManutencao();
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	private MenuModel modelFavoritoUsuario;
	public String carregarFavoritos() {
		 modelFavoritoUsuario = menuService.carregaFavoritos(loginBean.getUsuarioLogado());
		
		 return "/Favoritos/MenuFavoritos.xhtml";
	}
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public MenuItem getMenu() {
		return menu;
	}

	public void setMenu(MenuItem menu) {
		this.menu = menu;
	}

	public String menuCadastros() {
		listaMenuRegistrados();
		return "/Cadastros/MenuCadastros.xhtml";

	}

	public String clearFieldsMenuItem() {
		objetoMenuItem = new MenuItem();
		objetoMenuItem.setItemPai(new MenuItem());

		return "MenuCadastro.xhtml";
	}

	public String clearFieldsExcluir() {
		return "MenuItemDetalhes.xhtml";
	}

	public String clearFieldsUpdate() {
		if (objetoMenuItem.getItemPai() == null) {
			objetoMenuItem.setItemPai(new MenuItem());
		}

		return "MenuCadastro.xhtml";
	}

	public String salvarMenu() {
		MenuItemDao menuDao = new MenuItemDao();
		if (objetoMenuItem.getItemPai().getId() != null) {
			objetoMenuItem.setItemPai(menuDao.findById(objetoMenuItem.getItemPai().getId()));
		} else {
			objetoMenuItem.setItemPai(null);
		}
		if (objetoMenuItem.getId() == null || objetoMenuItem.getId() <= 0) {
			menuDao.create(objetoMenuItem);
		} else {
			menuDao.merge(objetoMenuItem);
		}
		objetoMenuItem = new MenuItem();
		listaMenuRegistrados();
		return "/Cadastros/Cobranca/MenuListagem.xhtml";
	}

	public void listaMenuRegistrados() {
		MenuItemDao menuDao = new MenuItemDao();
		setListaMenuItem(menuDao.findAll());

	}

	public String clearFieldsListagemMenu() {
		listaMenuRegistrados();
		return "/Cadastros/Cobranca/MenuListagem.xhtml";
	}

	public String excluirMenuItem() {
		FacesContext context = FacesContext.getCurrentInstance();
		MenuItemDao menuItemDao = new MenuItemDao();

		try {

			menuItemDao.delete(objetoMenuItem);
			listaMenuRegistrados();
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ContaContabil: " + e, ""));

			return "";
		}
		return "MenuListagem.xhtml";
	}

	public String exibeMenuAtendimento() {
		MenuService menu = new MenuService();
		modelAtendimento = menu.constroiMenuAtendimento();
		return "/Atendimento/atendimentoTeste.xhtml";
	}

	public void favoritar(Long id) {
		MenuFavorito menuFavorito = new MenuFavorito();
		MenuItemDao menuItemDao = new MenuItemDao();
		MenuFavoritoDao favoritoDao = new MenuFavoritoDao();
		MenuItem menu = menuItemDao.findById(id);
		menuFavorito.setFavoritado(true);
		menuFavorito.setMenuItemFavorito(menu);
		menuFavorito.setUser(loginBean.getUsuarioLogado());
	
		favoritoDao.create(menuFavorito);
		clearFieldsFavorito(id);
				
}
	public String clearFieldsFavorito(Long id) {
		MenuItem favorito = new MenuItem();
		MenuItemDao dao = new MenuItemDao();
		objetoItemFavorito = new MenuItem();
		this.objetoItemFavorito = dao.findById(id);
		favorito = dao.consultaFavorito(objetoItemFavorito, loginBean.getUsuarioLogado());
		if(favorito.getId() != null) {
			objetoItemFavorito.setFavorito(true);
			
		}
		else {
			objetoItemFavorito.setFavorito(false);
		}
		
		
		return "/Favoritos/ItemFavorito.xhtml";
	}
		
	public void desfavoritar(Long id) {
		MenuFavorito menuFavorito = new MenuFavorito();
		MenuItemDao menuItemDao = new MenuItemDao();
		MenuFavoritoDao favoritoDao = new MenuFavoritoDao();
		MenuItem menu = menuItemDao.findById(id);
		favoritoDao.consultaMenu(loginBean.getUsuarioLogado(), menu);
		favoritoDao.delete(menuFavorito);
		clearFieldsFavorito(id);
	}
	
	

	public MenuItem getObjetoMenuItem() {
		return objetoMenuItem;
	}

	public void setObjetoMenuItem(MenuItem objetoMenuItem) {
		this.objetoMenuItem = objetoMenuItem;
	}

	public List<MenuItem> getListaMenuItem() {
		return listaMenuItem;
	}

	public void setListaMenuItem(List<MenuItem> listaMenuItem) {
		this.listaMenuItem = listaMenuItem;
	}

	public List<MenuItem> getMenuItemModulo() {
		return menuItemModulo;
	}

	public void setMenuItemModulo(List<MenuItem> menuItemModulo) {
		this.menuItemModulo = menuItemModulo;
	}

	public List<MenuItem> getMenuItemSubmodulo() {
		return menuItemSubmodulo;
	}

	public void setMenuItemSubmodulo(List<MenuItem> menuItemSubmodulo) {
		this.menuItemSubmodulo = menuItemSubmodulo;
	}

	public List<MenuItem> getMenuItemItem() {
		return menuItemItem;
	}

	public void setMenuItemItem(List<MenuItem> menuItemItem) {
		this.menuItemItem = menuItemItem;
	}

	public MenuModel getModelCadastro() {
		return modelCadastro;
	}

	public void setModelCadastro(MenuModel modelCadastro) {
		this.modelCadastro = modelCadastro;
	}

	public MenuModel getModelAtendimento() {
		return modelAtendimento;
	}

	public void setModelAtendimento(MenuModel modelAtendimento) {
		this.modelAtendimento = modelAtendimento;
	}

	public MenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}

	public MenuModel getModelRelatorio() {
		return modelRelatorio;
	}

	public void setModelRelatorio(MenuModel modelRelatorio) {
		this.modelRelatorio = modelRelatorio;
	}

	public MenuModel getModelManutencao() {
		return modelManutencao;
	}

	public void setModelManutencao(MenuModel modelManutencao) {
		this.modelManutencao = modelManutencao;
	}

	public MenuModel getModelFavoritoUsuario() {
		return modelFavoritoUsuario;
	}

	public void setModelFavoritoUsuario(MenuModel modelFavoritoUsuario) {
		this.modelFavoritoUsuario = modelFavoritoUsuario;
	}
	public MenuFavorito getObjetoMenuFavorito() {
		return objetoMenuFavorito;
	}
	public void setObjetoMenuFavorito(MenuFavorito objetoMenuFavorito) {
		this.objetoMenuFavorito = objetoMenuFavorito;
	}
	public MenuItem getObjetoItemFavorito() {
		return objetoItemFavorito;
	}
	public void setObjetoItemFavorito(MenuItem objetoItemFavorito) {
		this.objetoItemFavorito = objetoItemFavorito;
	}
	public MenuModel getModelFavoritarAtendimento() {
		return modelFavoritarAtendimento;
	}
	public void setModelFavoritarAtendimento(MenuModel modelFavoritarAtendimento) {
		this.modelFavoritarAtendimento = modelFavoritarAtendimento;
	}
	public MenuModel getModelfavoritarCadastro() {
		return modelFavoritarCadastro;
	}
	public void setModelfavoritarCadastro(MenuModel modelfavoritarCadastro) {
		this.modelFavoritarCadastro = modelfavoritarCadastro;
	}
	public MenuModel getModelFavoritarRelatorio() {
		return modelFavoritarRelatorio;
	}
	public void setModelFavoritarRelatorio(MenuModel modelFavoritarRelatorio) {
		this.modelFavoritarRelatorio = modelFavoritarRelatorio;
	}
	public MenuModel getModelFavoritarManutencao() {
		return modelFavoritarManutencao;
	}
	public void setModelFavoritarManutencao(MenuModel modelFavoritarManutencao) {
		this.modelFavoritarManutencao = modelFavoritarManutencao;
	}
	public MenuModel getModelFavoritarCadastro() {
		return modelFavoritarCadastro;
	}
	public void setModelFavoritarCadastro(MenuModel modelFavoritarCadastro) {
		this.modelFavoritarCadastro = modelFavoritarCadastro;
	}

}
