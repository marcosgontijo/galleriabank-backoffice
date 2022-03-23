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
	
	private Time tempoDeAnalise;
	
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

	public Time getTempoDeAnalise() {
		return tempoDeAnalise;
	}

	public void setTempoDeAnalise(Time tempoDeAnalise) {
		this.tempoDeAnalise = tempoDeAnalise;
	}


}
