package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.GeracaoBoletoMB;

/** ManagedBean. */
@ManagedBean(name = "boletosBancoDoBrasilMB")
@SessionScoped

public class BoletosBancoDoBrasilMB {
	
	GeracaoBoletoMB geracaoBoletoMB = new GeracaoBoletoMB();
	
	public void geraBoletosBancoDoBrasil() {
		
		ContratoCobranca objetoContratoCobranca = new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		objetoContratoCobranca = cDao.findById((long) 5);
		
		geracaoBoletoMB.geraBoletosBancoDoBrasil("sistema", objetoContratoCobranca.getNumeroContrato(), 
			objetoContratoCobranca.getPagador().getNome(), objetoContratoCobranca.getPagador().getCpf(), 
			objetoContratoCobranca.getPagador().getCnpj(), 
	        objetoContratoCobranca.getPagador().getEndereco(), objetoContratoCobranca.getPagador().getBairro(),
	        objetoContratoCobranca.getPagador().getCep(), objetoContratoCobranca.getPagador().getCidade(), 
	        objetoContratoCobranca.getPagador().getEstado(), 
	        objetoContratoCobranca.getDataPagamentoFim(), objetoContratoCobranca.getVlrParcela(),"10");
		
	}
}
