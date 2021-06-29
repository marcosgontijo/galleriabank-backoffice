package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;


@ManagedBean(name = "ipcaMB")
@SessionScoped

public class IPCAMB {
	private List<IPCA> listIPCA;
	private Date data;
	private BigDecimal taxa;	
	private IPCA selectedIPCA;
	
	public IPCAMB() {
		
	}
	
	public String clearFieldsIPCA() {
		this.data = gerarDataHoje();
		this.taxa = BigDecimal.ZERO;				
		
		IPCADao ipcaDao = new IPCADao();
		this.listIPCA = ipcaDao.findAll();
		
		return "/Cadastros/Cobranca/IPCA.xhtml";
	}
	
	public void inserirIPCA() {
		FacesContext context = FacesContext.getCurrentInstance();
		IPCADao ipcaDao = new IPCADao();
		
		if (ipcaDao.findByFilter("data", this.data).size() == 0) { 		
			IPCA ipca = new IPCA();
			ipca.setData(this.data);
			ipca.setTaxa(this.taxa);
					
			ipcaDao.create(ipca);
			
			clearFieldsIPCA();
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "[IPCA] Taxa inserida com sucesso!", ""));
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "[IPCA] A Data informada já possui Taxa!", ""));
		}
	}
	
	public void excluirIPCA() {
		FacesContext context = FacesContext.getCurrentInstance();
		IPCADao ipcaDao = new IPCADao();

		ipcaDao.delete(this.selectedIPCA);
			
		clearFieldsIPCA();
			
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "[IPCA] Taxa excluída com sucesso!", ""));
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public List<IPCA> getListIPCA() {
		return listIPCA;
	}

	public void setListIPCA(List<IPCA> listIPCA) {
		this.listIPCA = listIPCA;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getTaxa() {
		return taxa;
	}

	public void setTaxa(BigDecimal taxa) {
		this.taxa = taxa;
	}

	public IPCA getSelectedIPCA() {
		return selectedIPCA;
	}

	public void setSelectedIPCA(IPCA selectedIPCA) {
		this.selectedIPCA = selectedIPCA;
	}
}
