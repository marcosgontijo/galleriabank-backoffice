package com.webnowbr.siscoat.credlocaliza.response;

import java.util.ArrayList;
import java.util.List;

public class CredlocalizaFrota {

	private String documento;
	private String mensagem;
	private String mensagem_fonte;
	private int quantidade_veiculos;
	
	private List<CredlocalizaVeiculos> veiculos = new ArrayList<CredlocalizaVeiculos>();

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getMensagem_fonte() {
		return mensagem_fonte;
	}

	public void setMensagem_fonte(String mensagem_fonte) {
		this.mensagem_fonte = mensagem_fonte;
	}

	public int getQuantidade_veiculos() {
		return quantidade_veiculos;
	}

	public void setQuantidade_veiculos(int quantidade_veiculos) {
		this.quantidade_veiculos = quantidade_veiculos;
	}

	public List<CredlocalizaVeiculos> getVeiculos() {
		return veiculos;
	}

	public void setVeiculos(List<CredlocalizaVeiculos> veiculos) {
		this.veiculos = veiculos;
	}
	
}
