package com.webnowbr.siscoat.contab.mb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.contab.db.model.BalancoPatrimonial;

/** ManagedBean. */
@ManagedBean(name = "balancopatrimonialMB")
@SessionScoped
public class BalancoPatrimonialMB {
	
	private BalancoPatrimonial objetoBalanco;

	public BalancoPatrimonialMB() {
		
		objetoBalanco = new BalancoPatrimonial();
	}

	public BalancoPatrimonial getObjetoBalanco() {
		return objetoBalanco;
	}

	public void setObjetoBalanco(BalancoPatrimonial objetoBalanco) {
		this.objetoBalanco = objetoBalanco;
	}
	
	
	public String clearFieldsBalancoPatrimonial() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();

		this.contratoGerado = false;
		
		
		return "/Atendimento/Cobranca/BalancoPatrimonial.xhtml";
	}
}