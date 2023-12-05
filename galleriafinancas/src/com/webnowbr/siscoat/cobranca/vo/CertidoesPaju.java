package com.webnowbr.siscoat.cobranca.vo;

import java.util.ArrayList;
import java.util.List;

public class CertidoesPaju {
	List<String> debitosDocumento = new ArrayList<String>();
	List<String> debitosSimilariedade = new ArrayList<String>();
	
	public List<String> getDebitosDocumento() {
		return debitosDocumento;
	}
	public void setDebitosDocumento(List<String> debitosDocumento) {
		this.debitosDocumento = debitosDocumento;
	}
	public List<String> getDebitosSimilariedade() {
		return debitosSimilariedade;
	}
	public void setDebitosSimilariedade(List<String> debitosSimilariedade) {
		this.debitosSimilariedade = debitosSimilariedade;
	}
	
	
}
