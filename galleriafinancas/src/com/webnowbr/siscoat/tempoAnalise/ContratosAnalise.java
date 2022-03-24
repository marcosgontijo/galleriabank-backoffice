package com.webnowbr.siscoat.tempoAnalise;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class ContratosAnalise {
	
	private ContratoCobranca contrato;
	
	private String tempoDeAnalise;
	
	public ContratosAnalise() {
		super();
		this.contrato = new ContratoCobranca();
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public String getTempoDeAnalise() {
		return tempoDeAnalise;
	}

	public void setTempoDeAnalise(long tempoDeAnalise) {
		
		long difference_In_Hours = (tempoDeAnalise / (1000 * 60 * 60)) % 24;
		long difference_In_Minutes = (tempoDeAnalise / (1000 * 60)) % 60;
		long difference_In_Seconds = (tempoDeAnalise / 1000) % 60;
		
		String tempoStr = difference_In_Hours + ":" + difference_In_Minutes + ":" + difference_In_Seconds;
		
		this.tempoDeAnalise = tempoStr;
	}


}
