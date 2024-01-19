
package com.webnowbr.siscoat.infra.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.MenuModel;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.MenuFavoritoDao;
import com.webnowbr.siscoat.infra.db.dao.MenuItemDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.IDmenus;
import com.webnowbr.siscoat.infra.db.model.MenuFavorito;
import com.webnowbr.siscoat.infra.db.model.MenuItem;
import com.webnowbr.siscoat.infra.service.MenuService;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "menuItemMB")
@SessionScoped
public class MenuItemMB {

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	private boolean clickFavorito;
	private String menuAtual = null;
	private boolean temFavorito = false;
	private MenuItemDao dao = new MenuItemDao();
	private MenuItem objetoMenuItem;
	private MenuItem objetoItemFavorito;
	private MenuFavorito objetoMenuFavorito = new MenuFavorito();
	private List<MenuItem> listaMenuItem = new ArrayList<>();
	private List<MenuItem> menuItemModulo;// = dao.ExibeModulo();
	private List<MenuItem> menuItemSubmodulo = dao.exibeSubmodulo();
	private List<MenuItem> menuItemItem = dao.exibeItem();
	private MenuItem menu = new MenuItem();
	private MenuService menuService = new MenuService();
	private MenuModel menuCarregado = null;
	IDmenus idMenus;
	private MenuModel modelMenuFavorito;
	private String parametroMenuConsultar;
	private String parametroConsultaTabela;
	private long parametroConsultaLong;


	public MenuItemMB() {
		super();

		if (loginBean != null) {
			menuCarregado = menuService.constroiMenu(IDmenus.Atedimento.getIndice(), loginBean.getUsuarioLogado().getId(), false);
	
			carregaMenu();
		}

	}
	public String clearFieldsMenu() {
		this.parametroConsultaTabela = null;
		this.listaParametros = new ArrayList<>();
		this.moduloParametro = null;
		this.parametroMenuConsultar = null;
		this.menuConsultado = new ArrayList<>();
		
		return "/Menus/MenuConsultar.xhtml";
		
	}
	public void apagaDados() {
		this.parametroConsultaTabela = null;
		this.parametroConsultaLong = 0;
		this.listaParametros = null;
		this.moduloParametro = null;
	}
	private List<MenuItem> menuConsultado = new ArrayList<>();
	public void carregaListaMenuString() {
		menuConsultado = new ArrayList<>();
		MenuItemDao dao = new MenuItemDao();
		 menuConsultado = dao.ConsultaitemConsultadoString(parametroMenuConsultar, parametroConsultaTabela);
	}
	public void carregaListaMenuNumero() {
		menuConsultado = new ArrayList<>();
		MenuItemDao dao = new MenuItemDao();
		menuConsultado = dao.ConsultaitemConsultadoLong(parametroMenuConsultar, parametroConsultaLong);
	}

	public String carregafavoritar() {
		if (CommonsUtil.mesmoValor(menuAtual, "Atendimento")) {
			modelMenuFavorito = menuService.constroiMenu(IDmenus.Atedimento.getIndice(), loginBean.getUsuarioLogado().getId(), true);
		} else if (CommonsUtil.mesmoValor(menuAtual, "Cadastros")) {
			modelMenuFavorito = menuService.constroiMenu(IDmenus.cadastros.getIndice(), loginBean.getUsuarioLogado().getId(), true);
		} else if (CommonsUtil.mesmoValor(menuAtual, "Relatorios")) {
			modelMenuFavorito = menuService.constroiMenu(IDmenus.Relatorio.getIndice(), loginBean.getUsuarioLogado().getId(), true);
		} else if (CommonsUtil.mesmoValor(menuAtual, "Manutencao")) {
			modelMenuFavorito = menuService.constroiMenu(IDmenus.manutencao.getIndice(), loginBean.getUsuarioLogado().getId(), true);
		} else {
			modelMenuFavorito = menuService.constroiMenu(IDmenus.Atedimento.getIndice(), loginBean.getUsuarioLogado().getId(), true);
		}
		menuAtual = null;
		return "/Favoritos/FavoritosMenu.xhtml";

	}

	public String carregaMenu() {
		if (CommonsUtil.mesmoValor(menuAtual, "Atendimento")) {
			menuCarregado = menuService.constroiMenu(IDmenus.Atedimento.getIndice(), loginBean.getUsuarioLogado().getId(), false);
		} else if (CommonsUtil.mesmoValor(menuAtual, "Cadastros")) {
			menuCarregado = menuService.constroiMenu(IDmenus.cadastros.getIndice(), loginBean.getUsuarioLogado().getId(), false);

		} else if (CommonsUtil.mesmoValor(menuAtual, "Relatorios")) {
			menuCarregado = menuService.constroiMenu(IDmenus.Relatorio.getIndice(), loginBean.getUsuarioLogado().getId(), false);
		} else if (CommonsUtil.mesmoValor(menuAtual, "Manutencao")) {
			menuCarregado = menuService.constroiMenu(IDmenus.manutencao.getIndice(), loginBean.getUsuarioLogado().getId(), false);
		} else if (CommonsUtil.mesmoValor(menuAtual, "Favoritos")) {
			menuCarregado = menuService.carregaFavoritos(loginBean.getUsuarioLogado());
		} else {
			menuCarregado = menuService.constroiMenu(IDmenus.Atedimento.getIndice(), loginBean.getUsuarioLogado().getId(), false);
		}
		return "/Menus/Menu.xhtml";

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
		
		
		return "/Cadastros/Cobranca/MenuCadastro.xhtml";
	}

	public String clearFieldsExcluir() {
		return "/Cadastros/Cobranca/MenuItemDetalhes.xhtml";
	}

	public String clearFieldsUpdate() {
		if (objetoMenuItem.getItemPai() == null) {
			objetoMenuItem.setItemPai(new MenuItem());
		}

		return "/Cadastros/Cobranca/MenuCadastro.xhtml";
	}

	public String salvarMenu() {
		List<MenuItem> listaOrdemMaior;
		MenuItemDao menuDao = new MenuItemDao();
		MenuItem menuatual = new MenuItem();
		if (objetoMenuItem.getItemPai().getId() != null) {
			objetoMenuItem.setItemPai(menuDao.findById(objetoMenuItem.getItemPai().getId()));
		} else {
			objetoMenuItem.setItemPai(null);
		}
		if (objetoMenuItem.getId() == null || objetoMenuItem.getId() <= 0) {
			listaOrdemMaior = menuDao.ConsultaItemMaiorOuIgual(objetoMenuItem.getItemPai(), objetoMenuItem.getOrdem());
			for(MenuItem menuOrdem : listaOrdemMaior) {
				menuOrdem.setOrdem(menuOrdem.getOrdem() + 1);
				menuDao.merge(menuOrdem);
			}
			menuDao.create(objetoMenuItem);
		} else {
			listaOrdemMaior = menuDao.ConsultaItemMaiorOuIgual(objetoMenuItem.getItemPai(), objetoMenuItem.getOrdem());
			menuatual = menuDao.findById(objetoMenuItem.getId());
			
			for(MenuItem menuOrdem : listaOrdemMaior) {
				if(objetoMenuItem.getOrdem() != menuatual.getOrdem()) {
				menuOrdem.setOrdem(menuOrdem.getOrdem() + 1);
				menuDao.merge(menuOrdem);
				}
			}
			menuDao.merge(objetoMenuItem);
		}
		objetoMenuItem = new MenuItem();
		menuConsultado = new ArrayList<>();
		return "/Menus/MenuConsultar.xhtml";
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
		List<MenuItem> menuMaior = new ArrayList<>();
		try {
			menuMaior = menuItemDao.ConsultaItemMaior(objetoMenuItem.getItemPai(), objetoMenuItem.getOrdem());
			
			for(MenuItem item : menuMaior) {
				item.setOrdem(item.getOrdem() - 1);
				menuItemDao.merge(item);
			}

			menuItemDao.delete(objetoMenuItem);
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ContaContabil: " + e, ""));

			return "";
		}
		menuConsultado = new ArrayList<>();
		return "/Menus/MenuConsultar.xhtml";
	}

	public void favoritar(Long id) {
		MenuFavorito menuFavorito = new MenuFavorito();
		MenuItemDao menuItemDao = new MenuItemDao();
		MenuFavoritoDao favoritoDao = new MenuFavoritoDao();
		UserDao userDao = new UserDao();
		MenuItem menu = menuItemDao.findById(id);
		menuFavorito.setFavoritado(true);
		menuFavorito.setMenuItemFavorito(menu);
		menuFavorito.setUser(loginBean.getUsuarioLogado());
		favoritoDao.create(menuFavorito);
		clearFieldsFavorito(menu.getId());

	}

	public String clearFieldsFavorito(Long id) {
		MenuFavorito favorito = new MenuFavorito();
		MenuItemDao dao = new MenuItemDao();
		objetoItemFavorito = new MenuItem();

		objetoItemFavorito = dao.findById(id);
		favorito = dao.consultaFavorito(objetoItemFavorito, loginBean.getUsuarioLogado());
		if (favorito != null && favorito.getId() != null ) {
			objetoItemFavorito.setFavorito(true);

		} else {
			objetoItemFavorito.setFavorito(false);

		}
		return "/Favoritos/ItemFavorito.xhtml";
	}

	public void desfavoritar(Long id) {
		MenuFavorito menuFavorito = new MenuFavorito();
		MenuItemDao menuItemDao = new MenuItemDao();
		MenuFavoritoDao favoritoDao = new MenuFavoritoDao();
		MenuItem menu = menuItemDao.findById(id);
		favoritoDao.apagaMenu(loginBean.getUsuarioLogado(), menu);
		clearFieldsFavorito(menu.getId());
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
		if ( loginBean.getUsuarioLogado() == null )
			return null;

		if (menuItemModulo == null) {
			MenuItemDao dao = new MenuItemDao();
			menuItemModulo = dao.consultaMenuItem("Módulo", 0l, loginBean.getUsuarioLogado().getId());
		}
		temFavorito = menuService.possuiFavorito(loginBean.getUsuarioLogado());
		return menuItemModulo;
	}
	private List<MenuItem> listaParametros = new ArrayList<>();
	private Long moduloParametro;
	public void consultaSubmodulo() {
		listaParametros = new ArrayList<>();
		MenuItemDao dao =  new MenuItemDao();
		listaParametros = dao.Consultaitemsubmodulo(moduloParametro);
		
	}

	public List<MenuItem> getListaParametros() {
		return listaParametros;
	}
	public void setListaParametros(List<MenuItem> listaParametros) {
		this.listaParametros = listaParametros;
	}
	public Long getModuloParametro() {
		return moduloParametro;
	}
	public void setModuloParametro(Long moduloParametro) {
		this.moduloParametro = moduloParametro;
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

	public MenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
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



	public boolean isClickFavorito() {
		return clickFavorito;
	}

	public void setClickFavorito(boolean clickFavorito) {
		this.clickFavorito = clickFavorito;
	}

	public String getMenuAtual() {
		if (menuCarregado == null) {
			 temFavorito = menuService.possuiFavorito(loginBean.getUsuarioLogado());
			if(!temFavorito) {
				menuAtual = "Atendimento";

			} else {
				menuAtual = "Favoritos";

			}
		}
		return menuAtual;
	}

	public void setMenuAtual(String menuAtual) {
		this.menuAtual = menuAtual;
	}

	public MenuModel getModelMenuFavorito() {
		return modelMenuFavorito;
	}

	public void setModelMenuFavorito(MenuModel modelMenuFavorito) {
		this.modelMenuFavorito = modelMenuFavorito;
	}

	public boolean isTemFavorito() {
		return temFavorito;
	}

	public void setTemFavorito(boolean temFavorito) {
		this.temFavorito = temFavorito;
	}

	public MenuModel getMenuCarregado() {
		if (menuCarregado == null) {
			 temFavorito = menuService.possuiFavorito(loginBean.getUsuarioLogado());
			if(!temFavorito) {
				menuCarregado = menuService.constroiMenu(IDmenus.Atedimento.getIndice(), loginBean.getUsuarioLogado().getId(),false);

			} else {
				menuCarregado = menuService.carregaFavoritos(loginBean.getUsuarioLogado());
				menuAtual = "Favoritos";

			}
		}
		return menuCarregado;
	}

	public void setMenuCarregado(MenuModel menuCarregado) {
		this.menuCarregado = menuCarregado;
	}
	public String getParametroMenuConsultar() {
		return parametroMenuConsultar;
	}
	public void setParametroMenuConsultar(String parametroMenuConsultar) {
		this.parametroMenuConsultar = parametroMenuConsultar;
	}
	public String getParametroConsultaTabela() {
		return parametroConsultaTabela;
	}
	public void setParametroConsultaTabela(String parametroConsultaTabela) {
		this.parametroConsultaTabela = parametroConsultaTabela;
	}
	public List<MenuItem> getMenuConsultado() {
		return menuConsultado;
	}
	public void setMenuConsultado(List<MenuItem> menuConsultado) {
		this.menuConsultado = menuConsultado;
	}
	public long getParametroConsultaLong() {
		return parametroConsultaLong;
	}
	public void setParametroConsultaLong(long parametroConsultaLong) {
		this.parametroConsultaLong = parametroConsultaLong;
	}

}
