package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.CDI;
import com.webnowbr.siscoat.cobranca.db.op.CDIDao;
import com.webnowbr.siscoat.common.DateUtil;


@ManagedBean(name = "cdiMB")
@SessionScoped

public class CDIMB {
	private List<CDI> listCDI;
	private Date data;
	private BigDecimal taxa;	
	private CDI selectedCDI;
	
	public CDIMB() {
		
	}
	
	public String clearFieldsCDI() {
		this.data = DateUtil.gerarDataHoje();
		this.taxa = BigDecimal.ZERO;				
		
		CDIDao cidDao = new CDIDao();
		this.listCDI = cidDao.findAll();
		
		return "/Cadastros/Cobranca/CDI.xhtml";
	}
	
	public void inserirCDI() {
		FacesContext context = FacesContext.getCurrentInstance();
		CDIDao cidDao = new CDIDao();
		
		if (cidDao.findByFilter("data", this.data).size() == 0) { 		
			CDI cdi = new CDI();
			cdi.setData(this.data);
			cdi.setTaxa(this.taxa);
					
			cidDao.create(cdi);
			
			//atualizaValoresContratos();
			
			clearFieldsCDI();
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "[CDI] Taxa inserida com sucesso!", ""));
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "[CDI] A Data informada já possui Taxa!", ""));
		}
	}
	
	public Date getDataComMesAnterior(Date dataOriginal) {
		Date dataRetorno = new Date();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		
		Calendar calendar = Calendar.getInstance(zone, locale);	
		
		calendar.setTime(dataOriginal);
		calendar.add(Calendar.MONTH, -1);		
		calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	public Date getDataCom2MesesAnterior(Date dataOriginal) {
		Date dataRetorno = new Date();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		
		Calendar calendar = Calendar.getInstance(zone, locale);	
		
		calendar.setTime(dataOriginal);
		calendar.add(Calendar.MONTH, -2);		
		calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	public Date getDataComAcrescimoDeMes(Date dataOriginal) {
		Date dataRetorno = new Date();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		
		Calendar calendar = Calendar.getInstance(zone, locale);	
		
		calendar.setTime(dataOriginal);
		calendar.add(Calendar.MONTH, 1);		
		//calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	public void excluirCDI() {
		FacesContext context = FacesContext.getCurrentInstance();
		CDIDao cidDao = new CDIDao();		

		cidDao.delete(this.selectedCDI);
			
		clearFieldsCDI();
			
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "[CDI] Taxa excluída com sucesso!", ""));
	}

	public List<CDI> getListCDI() {
		return listCDI;
	}

	public void setListCDI(List<CDI> listCDI) {
		this.listCDI = listCDI;
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

	public CDI getSelectedCDI() {
		return selectedCDI;
	}

	public void setSelectedCDI(CDI selectedCDI) {
		this.selectedCDI = selectedCDI;
	}
}
