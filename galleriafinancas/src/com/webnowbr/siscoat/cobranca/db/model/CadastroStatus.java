package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;

public class CadastroStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String status;
	private Date data;
	private String usuarioLogin;
	private User usuario;
	private String detalhes;
	private ContratoCobranca contratoCobranca;
	

	public CadastroStatus(String status, Date data, User usuario,
			ContratoCobranca contratoCobranca) {
		super();
		this.status = status;
		this.data = data;
		if(!CommonsUtil.semValor(usuario)) {
			this.usuarioLogin = usuario.getLogin();
		}	
		this.usuario = usuario;
		this.contratoCobranca = contratoCobranca;
	}

	public CadastroStatus() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(String usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}	
}
