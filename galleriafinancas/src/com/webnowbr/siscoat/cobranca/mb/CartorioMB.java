package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.model.Cartorio;

@ManagedBean(name = "cartorioMB")
@SessionScoped
public class CartorioMB {
private Cartorio objetoCartorio;




public Cartorio getObjetoCartorio() {
	return objetoCartorio;
}

public void setObjetoCartorio(Cartorio objetoCartorio) {
	this.objetoCartorio = objetoCartorio;
}
	
	
	
	
}
