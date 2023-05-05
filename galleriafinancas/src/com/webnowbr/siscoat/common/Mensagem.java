package com.webnowbr.siscoat.common;

public class Mensagem {

	public static String MENSAGEM_FALE_CONOSCO = "Fale Conosco";
	public static String MENSAGEM_DETALHES_IMOVEL = "E-mail com os detalhes do im�vel";
	public static String MENSAGEM_FALE_CONOSCO_I2_BRASIL = "Fale Conosco I2 Brasil";
	public static String MENSAGEM_CADASTRE_SEU_IMOVEL = "Cadastre seu Imovel";
	public static String MENSAGEM_REENVIO_SENHA = "Senha Site";

	private String destino = "";
	private String nome = "";
	private String telefone = "";
	private String email = "";
	private String assunto = "";
	private String conteudo = "";
	private String tipoEmail = ""; // Indica se é  "Fale Conosco" ou "Cadastre seu imovel" ou "Fale Conosco I2 Brasil"
	
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getDestino() {
		return destino;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getTipoEmail() {
		return tipoEmail;
	}

	public void setTipoEmail(String tipoEmail) {
		this.tipoEmail = tipoEmail;
	}

	public void limparCampos() {
		nome = ""; 
		email = "";
		assunto = "";
		conteudo = "";
		telefone = "";
		destino = "";
		tipoEmail = "";
	}
}
