package com.webnowbr.siscoat.credlocaliza.response;

public class CredlocalizaCabecalho {

	private int status;
	private String mensagem_status;
	private String id_produto;
	private String nome_produto;
	private String data_consulta;
	private String versao;
	private String versao_leiaute;
	private String ip;
	private int id_pesquisa;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMensagem_status() {
		return mensagem_status;
	}
	public void setMensagem_status(String mensagem_status) {
		this.mensagem_status = mensagem_status;
	}
	public String getId_produto() {
		return id_produto;
	}
	public void setId_produto(String id_produto) {
		this.id_produto = id_produto;
	}
	public String getNome_produto() {
		return nome_produto;
	}
	public void setNome_produto(String nome_produto) {
		this.nome_produto = nome_produto;
	}
	public String getData_consulta() {
		return data_consulta;
	}
	public void setData_consulta(String data_consulta) {
		this.data_consulta = data_consulta;
	}
	public String getVersao() {
		return versao;
	}
	public void setVersao(String versao) {
		this.versao = versao;
	}
	public String getVersao_leiaute() {
		return versao_leiaute;
	}
	public void setVersao_leiaute(String versao_leiaute) {
		this.versao_leiaute = versao_leiaute;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getId_pesquisa() {
		return id_pesquisa;
	}
	public void setId_pesquisa(int id_pesquisa) {
		this.id_pesquisa = id_pesquisa;
	}
}
