package com.webnowbr.siscoat.cobranca.service;

import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.RelacionamentoPagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPF;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPJ;

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

	public void preecheDadosReceita(PagadorRecebedor pagadorAdicionar) {
		String stringResponse = null;
		NetrinService netrinService = new NetrinService();
		
		if (!CommonsUtil.semValor(pagadorAdicionar.getCpf())) {

			ReceitaFederalPF receitaFederalPF = netrinService.requestCadastroPF(pagadorAdicionar);

			stringResponse = GsonUtil.toJson(receitaFederalPF);

		} else if (!CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {

			ReceitaFederalPJ receitaFederalPJ = netrinService.requestCadastroPJ(pagadorAdicionar);

			stringResponse = GsonUtil.toJson(receitaFederalPJ);

		}
		if (!CommonsUtil.semValor(pagadorAdicionar.getCpf()) || !CommonsUtil.semValor(pagadorAdicionar.getCnpj()))
			pagadorAdicionar = buscaOuInsere(pagadorAdicionar);

		if (!CommonsUtil.semValor(stringResponse) && !CommonsUtil.semValor(pagadorAdicionar.getId())) {
			adicionarConsultaNoPagadorRecebedor(pagadorAdicionar, DocumentosAnaliseEnum.RECEITA_FEDERAL,
					stringResponse);
		}
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
		PagadorRecebedorConsulta pagadorRecebedorConsulta = PagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta);
		if (pagadorRecebedorConsulta == null) {
			pagadorRecebedorConsulta = new PagadorRecebedorConsulta();
		}
		pagadorRecebedorConsulta.setDataConsulta(new Date());
		pagadorRecebedorConsulta.setRetornoConsulta(consulta);
		pagadorRecebedorConsulta.setPessoa(pagador);
		pagadorRecebedorConsulta.setTipoEnum(tipoConsulta);

		if (CommonsUtil.semValor(pagadorRecebedorConsulta.getId()))
			PagadorRecebedorConsultaDao.create(pagadorRecebedorConsulta);
		else
			PagadorRecebedorConsultaDao.merge(pagadorRecebedorConsulta);

	}

	public PagadorRecebedorConsulta buscaConsultaNoPagadorRecebedor(PagadorRecebedor pagador,
			DocumentosAnaliseEnum tipoConsulta) {

		PagadorRecebedorConsultaDao PagadorRecebedorConsultaDao = new PagadorRecebedorConsultaDao();

		// buscando ultima consutla do pagador
		PagadorRecebedorConsulta pagadorRecebedorConsulta = PagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta);
		return pagadorRecebedorConsulta;

	}

	public void geraRelacionamento(PagadorRecebedor pessoaRoot, String relacao, PagadorRecebedor pessoaChild,
			BigDecimal porcentagem) {
		RelacionamentoPagadorRecebedor relacioanameto = 
				new RelacionamentoPagadorRecebedor(pessoaRoot, relacao, pessoaChild, porcentagem);
		
		RelacionamentoPagadorRecebedorDao rDao = new RelacionamentoPagadorRecebedorDao();
		rDao.create(relacioanameto);
	}
}