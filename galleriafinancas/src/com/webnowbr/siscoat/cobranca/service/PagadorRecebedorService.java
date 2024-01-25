package com.webnowbr.siscoat.cobranca.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.RelacionamentoPagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.bigdata.cliente.model.cadastro.DadosBasicosResultPj;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPF;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPJ;

public class PagadorRecebedorService {

	public PagadorRecebedor buscaOuInsere(PagadorRecebedor pagadorAdicionar) {
		if (CommonsUtil.semValor(pagadorAdicionar.getId())) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			if (!CommonsUtil.semValor(pagadorAdicionar.getCpf())) {
				List<PagadorRecebedor> cadastrados = pDao.findByFilter("cpf", pagadorAdicionar.getCpf());
				cadastrados.addAll(pDao.findByFilter("cpf", 
						CommonsUtil.somenteNumeros(pagadorAdicionar.getCpf())));
				PagadorRecebedor pagadorCadastrado = new PagadorRecebedor();

				if (cadastrados.size() > 0)
					pagadorCadastrado = cadastrados.get(0);
				else
					pagadorCadastrado = pagadorAdicionar;

				if (CommonsUtil.semValor(pagadorCadastrado.getNomeMae())
						&& !CommonsUtil.semValor(pagadorAdicionar.getNomeMae())) {
					pagadorCadastrado.setNomeMae(pagadorAdicionar.getNomeMae());
				}
				if (CommonsUtil.semValor(pagadorCadastrado.getRg())
						&& !CommonsUtil.semValor(pagadorAdicionar.getRg())) {
					pagadorCadastrado.setRg(pagadorAdicionar.getRg());
				}

				if (CommonsUtil.semValor(pagadorCadastrado.getDtNascimento())
						&& !CommonsUtil.semValor(pagadorAdicionar.getDtNascimento())) {
					pagadorCadastrado.setDtNascimento(pagadorAdicionar.getDtNascimento());
				}
				if (pagadorCadastrado.getId() > 0)
					pDao.merge(pagadorCadastrado);
				else
					pDao.create(pagadorAdicionar);
				pagadorAdicionar = pagadorCadastrado;

			} else if (!CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {
				List<PagadorRecebedor> cadastrados = pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj());
				cadastrados.addAll(pDao.findByFilter("cnpj", 
						CommonsUtil.somenteNumeros(pagadorAdicionar.getCnpj())));
				PagadorRecebedor pagadorCadastrado = new PagadorRecebedor();

				if (cadastrados.size() > 0)
					pagadorCadastrado = cadastrados.get(0);
				else
					pagadorCadastrado = pagadorAdicionar;

				if (CommonsUtil.semValor(pagadorCadastrado.getCnpj())
						&& !CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {
					pagadorCadastrado.setCnpj(pagadorAdicionar.getCnpj());
				}

				if (pagadorCadastrado.getId() > 0)
					pDao.merge(pagadorCadastrado);
				else
					pDao.create(pagadorAdicionar);
				pagadorAdicionar = pagadorCadastrado;

			} else {
				long idIncluido = pDao.create(pagadorAdicionar);
				pagadorAdicionar = pDao.findById(idIncluido);
				pDao.merge(pagadorAdicionar);
			}
		}
		return pagadorAdicionar;

	}

	public PagadorRecebedor buscaOuInsere(String cnpjCpf) {

		cnpjCpf = CommonsUtil.somenteNumeros(cnpjCpf);

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		PagadorRecebedor pagadorRecebedor = new PagadorRecebedor();

		String tipoPessoa = CommonsUtil.pessoaFisicaJuridicaCnpjCpf(cnpjCpf);
		if (CommonsUtil.mesmoValor("PF", tipoPessoa))
			pagadorRecebedor.setCpf(CommonsUtil.formataCpf(cnpjCpf));
		else
			pagadorRecebedor.setCnpj(CommonsUtil.formataCnpjCpf(cnpjCpf, false));

		pagadorRecebedor = buscaOuInsere(pagadorRecebedor);

		preecheDadosReceita(pagadorRecebedor);

		pagadorRecebedorDao.merge(pagadorRecebedor);

		return pagadorRecebedor;

	}

	public PagadorRecebedor preecheDadosReceita(PagadorRecebedor pagadorAdicionar) {
		String stringResponse = null;
		NetrinService netrinService = new NetrinService();
		BigDataService bigDataService = new BigDataService();

//		if (!CommonsUtil.semValor(pagadorAdicionar.getCpf()) || !CommonsUtil.semValor(pagadorAdicionar.getCnpj()))
//			pagadorAdicionar = buscaOuInsere(pagadorAdicionar);

		DocumentosAnaliseEnum tipoConsulta = null;
		if (!CommonsUtil.semValor(pagadorAdicionar.getCpf())) {

			ReceitaFederalPF receitaFederalPF = netrinService.requestCadastroPF(pagadorAdicionar);

			stringResponse = GsonUtil.toJson(receitaFederalPF);
			tipoConsulta = DocumentosAnaliseEnum.RECEITA_FEDERAL;

		} else if (!CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {
			DadosBasicosResultPj dadosBasicosResultPj = bigDataService.requestCadastroPJ(pagadorAdicionar);

			stringResponse = GsonUtil.toJson(dadosBasicosResultPj);
			tipoConsulta = DocumentosAnaliseEnum.CADASTROBB;

		}
		if (!CommonsUtil.semValor(pagadorAdicionar.getCpf()) || !CommonsUtil.semValor(pagadorAdicionar.getCnpj()))
			pagadorAdicionar = buscaOuInsere(pagadorAdicionar);

		if (!CommonsUtil.semValor(stringResponse) && !CommonsUtil.semValor(pagadorAdicionar.getId())) {
			adicionarConsultaNoPagadorRecebedor(pagadorAdicionar, tipoConsulta, stringResponse);
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

		PagadorRecebedorConsultaDao pagadorRecebedorConsultaDao = new PagadorRecebedorConsultaDao();

		// buscando ultima consutla do pagador
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta);
		if (pagadorRecebedorConsulta == null) {
			pagadorRecebedorConsulta = new PagadorRecebedorConsulta();
		}
		pagadorRecebedorConsulta.setDataConsulta(new Date());
		pagadorRecebedorConsulta.setRetornoConsulta(consulta);
		pagadorRecebedorConsulta.setPessoa(pagador);
		pagadorRecebedorConsulta.setTipoEnum(tipoConsulta);

//		if (CommonsUtil.semValor(pagadorRecebedorConsulta.getId()))
//			PagadorRecebedorConsultaDao.create(pagadorRecebedorConsulta);
//		else
		pagadorRecebedorConsultaDao.merge(pagadorRecebedorConsulta);

	}

	public void adicionarConsultaNoPagadorRecebedor(PagadorRecebedor pagador, DocumentosAnaliseEnum tipoConsulta,
			String consulta, String uf) {

		PagadorRecebedorConsultaDao pagadorRecebedorConsultaDao = new PagadorRecebedorConsultaDao();

		// buscando ultima consutla do pagador
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta, uf);
		if (pagadorRecebedorConsulta == null) {
			pagadorRecebedorConsulta = new PagadorRecebedorConsulta();
		}
		pagadorRecebedorConsulta.setDataConsulta(new Date());
		pagadorRecebedorConsulta.setRetornoConsulta(consulta);
		pagadorRecebedorConsulta.setPessoa(pagador);
		pagadorRecebedorConsulta.setTipoEnum(tipoConsulta, uf);

//		if (CommonsUtil.semValor(pagadorRecebedorConsulta.getId()))
//			PagadorRecebedorConsultaDao.create(pagadorRecebedorConsulta);
//		else
		pagadorRecebedorConsultaDao.merge(pagadorRecebedorConsulta);

	}

	public PagadorRecebedorConsulta buscaConsultaNoPagadorRecebedor(PagadorRecebedor pagador,
			DocumentosAnaliseEnum tipoConsulta) {

		PagadorRecebedorConsultaDao PagadorRecebedorConsultaDao = new PagadorRecebedorConsultaDao();

		// buscando ultima consutla do pagador
		PagadorRecebedorConsulta pagadorRecebedorConsulta = PagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta);
		return pagadorRecebedorConsulta;

	}

	public PagadorRecebedorConsulta buscaConsultaNoPagadorRecebedor(PagadorRecebedor pagador,
			DocumentosAnaliseEnum tipoConsulta, String uf) {

		PagadorRecebedorConsultaDao PagadorRecebedorConsultaDao = new PagadorRecebedorConsultaDao();

		// buscando ultima consutla do pagador
		PagadorRecebedorConsulta pagadorRecebedorConsulta = PagadorRecebedorConsultaDao
				.getConsultaByPagadorAndTipo(pagador, tipoConsulta, uf);
		return pagadorRecebedorConsulta;

	}

	public void geraRelacionamento(PagadorRecebedor pessoaRoot, String relacao, PagadorRecebedor pessoaChild,
			BigDecimal porcentagem) {
		RelacionamentoPagadorRecebedor relacioanameto = new RelacionamentoPagadorRecebedor(pessoaRoot, relacao,
				pessoaChild, porcentagem);

		RelacionamentoPagadorRecebedorDao rDao = new RelacionamentoPagadorRecebedorDao();
		List<RelacionamentoPagadorRecebedor> relacaoExistente = rDao.verificaRelacaoExistente(pessoaRoot, pessoaChild);
		if (relacaoExistente.size() <= 0) {
			rDao.create(relacioanameto);
		}
	}

	public List<String> verificaRestricao(PagadorRecebedor pessoa) {
		List<RelacionamentoPagadorRecebedor> listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		listRelacoes = rprDao.getRelacionamentos(pessoa, listRelacoes);

		Set<PagadorRecebedor> unique = new HashSet<PagadorRecebedor>();
		unique.add(pessoa);
		if (!CommonsUtil.semValor(listRelacoes)) {
			unique.addAll(listRelacoes.stream().map(m -> m.getPessoaRoot()).collect(Collectors.toSet()));
			unique.addAll(listRelacoes.stream().map(m -> m.getPessoaChild()).collect(Collectors.toSet()));
		}

		if (!CommonsUtil.semValor(unique))
			return unique.stream().filter(u -> u.isRestricao()).map( m -> m.getNome() + " (" + (!CommonsUtil.semValor(m.getCpf())?m.getCpf():m.getCnpj()) + ")").collect(Collectors.toList());
		else
			return null;
	}
}