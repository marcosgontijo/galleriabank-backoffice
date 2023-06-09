package com.webnowbr.siscoat.cobranca.service;

import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

public class PagadorRecebedorService {
	
	public PagadorRecebedor buscaOuInsere(PagadorRecebedor pagadorAdicionar) {
		if (CommonsUtil.semValor(pagadorAdicionar.getId())) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			if (!CommonsUtil.semValor(pagadorAdicionar.getCpf())
					&& pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).size() > 0) {
				pagadorAdicionar = pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).get(0);

				if (CommonsUtil.semValor(pagadorAdicionar.getNomeMae())
						&& !CommonsUtil.semValor(pagadorAdicionar.getNomeMae())) {
					pagadorAdicionar.setNomeMae(pagadorAdicionar.getNomeMae());
				}
				if (CommonsUtil.semValor(pagadorAdicionar.getRg()) && !CommonsUtil.semValor(pagadorAdicionar.getRg())) {
					pagadorAdicionar.setRg(pagadorAdicionar.getRg());
				}

				if (CommonsUtil.semValor(pagadorAdicionar.getDtNascimento())
						&& !CommonsUtil.semValor(pagadorAdicionar.getDtNascimento())) {
					pagadorAdicionar.setDtNascimento(pagadorAdicionar.getDtNascimento());
				}
				pDao.merge(pagadorAdicionar);

			} else if (!CommonsUtil.semValor(pagadorAdicionar.getCnpj())
					&& pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).size() > 0) {
				pagadorAdicionar = pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).get(0);
			} else {
				long idIncluido = pDao.create(pagadorAdicionar);
				pagadorAdicionar = pDao.findById(idIncluido);
			}
		}
		return pagadorAdicionar;			
		
	}
	
	public PagadorRecebedor findById(Long id) {
		PagadorRecebedor pagadorAdicionar = null;

		if (!CommonsUtil.semValor(id)) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			pagadorAdicionar = pDao.findById(id);

		}
		return pagadorAdicionar;

	}
	
	public PagadorRecebedor findByCpfCnpj(String cpfCnpj) {
		PagadorRecebedor pagadorAdicionar = null;

		if (!CommonsUtil.semValor(cpfCnpj)) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			pagadorAdicionar = pDao.getConsultaByCpfCnpj(cpfCnpj);

		}
		return pagadorAdicionar;

	}
	
	public void adicionarConsultaNoPagadorRecebedor(PagadorRecebedor pagador, DocumentosAnaliseEnum tipoConsulta,
			String consulta) {

		PagadorRecebedorConsultaDao PagadorRecebedorConsultaDao = new PagadorRecebedorConsultaDao();

		// buscando ultima consutla do pagador
		PagadorRecebedorConsulta PagadorRecebedorConsulta = PagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta);
		if (PagadorRecebedorConsulta == null) {
			PagadorRecebedorConsulta = new PagadorRecebedorConsulta();
		}
		PagadorRecebedorConsulta.setDataConsulta(new Date());
		PagadorRecebedorConsulta.setRetornConsulta(consulta);
		PagadorRecebedorConsulta.setPessoa(pagador);
		PagadorRecebedorConsulta.setTipoEnum(tipoConsulta);

		if (CommonsUtil.semValor(PagadorRecebedorConsulta.getId()))
			PagadorRecebedorConsultaDao.create(PagadorRecebedorConsulta);
		else
			PagadorRecebedorConsultaDao.merge(PagadorRecebedorConsulta);

	}
	
}
