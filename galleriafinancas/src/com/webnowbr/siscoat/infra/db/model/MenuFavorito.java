package com.webnowbr.siscoat.infra.db.model;

public class MenuFavorito {
	
	private Long id;
	
	private MenuItem menuItemFavorito;
	
	private User user;
	private boolean favoritado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MenuItem getMenuItemFavorito() {
		return menuItemFavorito;
	}

	public void setMenuItemFavorito(MenuItem menuItemFavorito) {
		this.menuItemFavorito = menuItemFavorito;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isFavoritado() {
		return favoritado;
	}

	public void setFavoritado(boolean favoritado) {
		this.favoritado = favoritado;
	}

}
