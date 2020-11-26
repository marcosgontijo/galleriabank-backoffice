package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Protestos implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String estado;
	private Date pesquisaEfetuadaEm;
	private String pesquisaRetroativaAte;
	private int totalNumProtestosUf;
	
	private List<CartoriosProtesto> cartoriosProtesto;

	public Protestos() {
		
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getPesquisaEfetuadaEm() {
		return pesquisaEfetuadaEm;
	}

	public void setPesquisaEfetuadaEm(Date pesquisaEfetuadaEm) {
		this.pesquisaEfetuadaEm = pesquisaEfetuadaEm;
	}

	public String getPesquisaRetroativaAte() {
		return pesquisaRetroativaAte;
	}

	public void setPesquisaRetroativaAte(String pesquisaRetroativaAte) {
		this.pesquisaRetroativaAte = pesquisaRetroativaAte;
	}

	public int getTotalNumProtestosUf() {
		return totalNumProtestosUf;
	}

	public void setTotalNumProtestosUf(int totalNumProtestosUf) {
		this.totalNumProtestosUf = totalNumProtestosUf;
	}

	public List<CartoriosProtesto> getCartoriosProtesto() {
		return cartoriosProtesto;
	}

	public void setCartoriosProtesto(List<CartoriosProtesto> cartoriosProtesto) {
		this.cartoriosProtesto = cartoriosProtesto;
	}	
}