package com.webnowbr.siscoat.infra.db.model;

public class MenuItem {
	private Long id;
	private String nome;
	
	private String acao;
	
	private String icone;
	
	private String permissao;
	
	private int ordem;
	
	private String tipo = "";
	
	private MenuItem itemPai;
	
	private boolean favorito =false;
	private boolean clickFavorito = false;
	
	public String getPermissao() {
		return permissao;
	}

	public void setPermissao(String permissao) {
		this.permissao = permissao;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public MenuItem getItemPai() {
		return itemPai;
	}

	public void setItemPai(MenuItem itemPai) {
		this.itemPai = itemPai;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isFavorito() {
		return favorito;
	}

	public void setFavorito(boolean favorito) {
		this.favorito = favorito;
	}

	public boolean isClickFavorito() {
		return clickFavorito;
	}

	public void setClickFavorito(boolean clickFavorito) {
		this.clickFavorito = clickFavorito;
	}
	

}
