package com.webnowbr.siscoat.job;

import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.cobranca.mb.TakeBlipMB;

public class PajuLuvisonJobConsultar {

	@SuppressWarnings("deprecation")
	public int buscarContratosPajuLuvison(ContratoCobrancaDao contratoCobrancaDao) {
		List<String> contratosPajuLuvison = contratoCobrancaDao.consultaPajusLuvisonJob();
		return contratosPajuLuvison.size();
	}
	
	public void enviarMensagem(int size) {
		TakeBlipMB tkblpMb = new TakeBlipMB();
		ResponsavelDao rDao = new ResponsavelDao();
		Responsavel responsavel = new Responsavel();
		Responsavel responsavel2 = new Responsavel();
		
		// Lucas
		responsavel = rDao.findById((long) 1137);
		tkblpMb.sendWhatsAppMessageLuvisonJob(responsavel, size);
		
		//Fernanda
		//responsavel2 = rDao.findById((long) 1699);
		//tkblpMb.sendWhatsAppMessageLuvisonJob(responsavel2, size);
	}
}