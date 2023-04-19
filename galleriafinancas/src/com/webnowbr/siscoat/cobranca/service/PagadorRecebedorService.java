package com.webnowbr.siscoat.cobranca.service;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;

public class PagadorRecebedorService {
	
	public PagadorRecebedor buscaOuInsere(PagadorRecebedor pagadorAdicionar) {
		if(pagadorAdicionar.getId() <= 0) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			if(!CommonsUtil.semValor(pagadorAdicionar.getCpf()) && pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).size() > 0) {
				pagadorAdicionar = pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).get(0);
			} else if(!CommonsUtil.semValor(pagadorAdicionar.getCnpj()) && pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).size() > 0) {
				pagadorAdicionar = pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).get(0);
			} else {
				long idIncluido = pDao.create(pagadorAdicionar);
				pagadorAdicionar = pDao.findById(idIncluido);				
			}		
		}
		return pagadorAdicionar;			
		
	}
	

}
