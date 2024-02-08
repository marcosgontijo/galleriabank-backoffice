package com.webnowbr.siscoat.cobranca.service;

import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

public class ResponsavelService {
	
	private final ResponsavelDao responsavelDao = new ResponsavelDao();
	
	public Responsavel getObjetoResponsavelGeral() {
//		if ( CommonsUtil.semValor(objetoResponsavelGeral)) {
//			ResponsavelDao rDao = new ResponsavelDao();
		Responsavel objetoResponsavelGeral = responsavelDao.findById(SiscoatConstants.RESPONSAVEL_GERAL_ID);
		if (CommonsUtil.semValor(objetoResponsavelGeral)) {
			objetoResponsavelGeral = new Responsavel();
			objetoResponsavelGeral.setNome("RESPONSAVEL GERAL");
			objetoResponsavelGeral.setId(SiscoatConstants.RESPONSAVEL_GERAL_ID);
			objetoResponsavelGeral.setDataCadastro(new Date());
			objetoResponsavelGeral.setDesativado(true);
			responsavelDao.create(objetoResponsavelGeral);
		}

		return objetoResponsavelGeral;
	}

}
