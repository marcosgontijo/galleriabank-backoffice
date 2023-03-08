package com.webnowbr.siscoat.contab.mb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.contab.db.dao.BalancoPatrimonialDao;
import com.webnowbr.siscoat.contab.db.model.BalancoPatrimonial;

/** ManagedBean. */
@ManagedBean(name = "balancoPatrimonialMB")
@SessionScoped
public class BalancoPatrimonialMB {
	
	private BalancoPatrimonial objetoBalanco;
	private Date relDataContratoInicio;
	private Date relDataContratoFim;
	
	private String tituloPagina = "Todos";
	private List<BalancoPatrimonial> todosBalancos;
	

public String clearFieldsBalancoPatrimonial() {
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		BalancoPatrimonialDao balancopatrimonialDao = new BalancoPatrimonialDao();
		this.todosBalancos = balancopatrimonialDao.consultaBalancoPatrimonial();		
		return "/Atendimento/Cobranca/Contabilidade/BalancoPatrimonialConsulta.xhtml";
	}
	
	public String clearBalancoPatrimonial() {
		objetoBalanco = new BalancoPatrimonial();
		return "/Atendimento/Cobranca/ContabilidadeEdicao.xhtml";
	}

	public BalancoPatrimonialMB() {
		
		objetoBalanco = new BalancoPatrimonial();
	}

	public BalancoPatrimonial getObjetoBalanco() {
		return objetoBalanco;
	}

	public void setObjetoBalanco(BalancoPatrimonial objetoBalanco) {
		this.objetoBalanco = objetoBalanco;
	}
	
	
	public Date getRelDataContratoInicio() {
		return relDataContratoInicio;
	}

	public void setRelDataContratoInicio(Date relDataContratoInicio) {
		this.relDataContratoInicio = relDataContratoInicio;
	}

	public Date getRelDataContratoFim() {
		return relDataContratoFim;
	}

	public void setRelDataContratoFim(Date relDataContratoFim) {
		this.relDataContratoFim = relDataContratoFim;
	}

	public String getTituloPagina() {
		return tituloPagina;
	}

	public void setTituloPagina(String tituloPagina) {
		this.tituloPagina = tituloPagina;
	}

	public List<BalancoPatrimonial> getTodosBalancos() {
		return todosBalancos;
	}

	public void setTodosBalancos(List<BalancoPatrimonial> todosBalancos) {
		this.todosBalancos = todosBalancos;
	}

	
}