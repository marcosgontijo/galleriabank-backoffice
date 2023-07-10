package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedProperty;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

public class CadastroStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	private long id;
	private String status;
	private String statusAnteriror;
	private Date data;
	private String usuarioLogin;
	private User usuario;
	private ContratoCobranca contratoCobranca;
	

	public CadastroStatus(String status, String statusAnterior, ContratoCobranca contratoCobranca) {
		super();
		this.status = status;
		this.statusAnteriror = statusAnterior;
		this.data = gerarDataHoje();
		
		usuario = loginBean.getUsuarioLogado();
		if(!CommonsUtil.semValor(loginBean.getUsuarioLogado())) {
			this.usuarioLogin = usuario.getLogin();
		}	
		
		if(CommonsUtil.mesmoValor(status, "Baixado")) {
			if(CommonsUtil.mesmoValor(contratoCobranca.getBaixadoUsuario(), "Sistema")) {
				usuario = null;
				this.usuarioLogin = "Sistema";
			}
		}
		this.contratoCobranca = contratoCobranca;
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
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

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getStatusAnteriror() {
		return statusAnteriror;
	}

	public void setStatusAnteriror(String statusAnteriror) {
		this.statusAnteriror = statusAnteriror;
	}	
	
	
}
