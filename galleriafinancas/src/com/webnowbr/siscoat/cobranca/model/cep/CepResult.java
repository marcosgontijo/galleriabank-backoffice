package com.webnowbr.siscoat.cobranca.model.cep;

import com.google.gson.annotations.SerializedName;

public class CepResult {

	@SerializedName(value = "erro")
	private String erro;

	@SerializedName(value = "endereco", alternate={"logradouro, address"})
	private String endereco;

	@SerializedName(value = "bairro, district")
	private String bairro;

	@SerializedName(value = "cidade", alternate={"localidade, city"})
	private String cidade;

	@SerializedName(value = "estado", alternate={"uf, state"})
	private String estado;

	

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
