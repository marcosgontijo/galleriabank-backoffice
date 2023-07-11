package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedProperty;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

public class CadastroStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String status;
	private String statusAnteriror;
	private Date data;
	private String usuarioLogin;
	private User usuario;
	private ContratoCobranca contratoCobranca;
	private String numeroContrato;
	private BigInteger ordemStatus;
	

	public CadastroStatus(String status, String statusAnterior, ContratoCobranca contratoCobranca, User user) {
		super();
		this.status = status;
		this.statusAnteriror = statusAnterior;
		this.data = gerarDataHoje();
		
		if(CommonsUtil.mesmoValor(status, "Baixado")) {
			if(CommonsUtil.mesmoValor(contratoCobranca.getBaixadoUsuario(), "Sistema")) {
				UserDao userDao = new UserDao();
				user = userDao.findById((long) -1);
			}
		}
		
		if(!CommonsUtil.semValor(user)
				|| user.getId() > 0) {
			usuario = user;
			if(!CommonsUtil.semValor(user.getLogin())) {
				this.usuarioLogin = usuario.getLogin();
			}	
		} else {
			usuario = null;
			usuarioLogin = null;
		}
		
		this.contratoCobranca = contratoCobranca;
		numeroContrato = this.contratoCobranca.getNumeroContrato();
		ordemStatus = CommonsUtil.bigIntegerValue(this.contratoCobranca.getListCadastroStatus().size());
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

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public BigInteger getOrdemStatus() {
		return ordemStatus;
	}

	public void setOrdemStatus(BigInteger ordemStatus) {
		this.ordemStatus = ordemStatus;
	}	
}
