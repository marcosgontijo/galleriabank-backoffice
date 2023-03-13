package com.webnowbr.siscoat.contab.mb;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

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
	private boolean editar;
	private boolean excluir;
	

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
		return "/Atendimento/Cobranca/Contabilidade/BalancoPatrimonialInserir.xhtml";
	}
	
	
	public void salvarBalanco() {
		FacesContext context = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao balancoPatrimonialDao = new BalancoPatrimonialDao();
		try {				
			balancoPatrimonialDao.merge(this.objetoBalanco);
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Balanço Inserido com sucesso!!",
							""));
			clearBalancoPatrimonial();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: " + e, ""));
		}
	}

	public String editarBalanco() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao cDao = new BalancoPatrimonialDao();
		
		if (this.objetoBalanco.getId() >0) { 
			cDao.merge(this.objetoBalanco);
		}

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Balanço Patrimonial: Balanço alterado com sucesso!", ""));

		return clearFieldsBalancoPatrimonial();
	}

	public String excluirBalanco() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao cDao = new BalancoPatrimonialDao();

		cDao.delete(this.objetoBalanco);

		this.todosBalancos.remove(this.objetoBalanco);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Balanço Patrimonial: Balanço excluído com sucesso!", ""));

		return clearFieldsBalancoPatrimonial();
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

	public boolean isEditar() {
		return editar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}

	public boolean isExcluir() {
		return excluir;
	}

	public void setExcluir(boolean excluir) {
		this.excluir = excluir;
	}

	
}