package com.webnowbr.siscoat.cobranca.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobrancaRestricao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaRestricaoDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;

public class ImovelCobrancaRestricaoService {

//	public PagadorRecebedor buscaOuInsere(PagadorRecebedor pagadorAdicionar) {
//		if (CommonsUtil.semValor(pagadorAdicionar.getId())) {
//			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
//			if (!CommonsUtil.semValor(pagadorAdicionar.getCpf())) {
//				List<PagadorRecebedor> cadastrados = pDao.findByFilter("cpf", pagadorAdicionar.getCpf());
//				PagadorRecebedor pagadorCadastrado = new PagadorRecebedor();
//
//				if (cadastrados.size() > 0)
//					pagadorCadastrado = cadastrados.get(0);
//				else
//					pagadorCadastrado = pagadorAdicionar;
//
//				if (CommonsUtil.semValor(pagadorCadastrado.getNomeMae())
//						&& !CommonsUtil.semValor(pagadorAdicionar.getNomeMae())) {
//					pagadorCadastrado.setNomeMae(pagadorAdicionar.getNomeMae());
//				}
//				if (CommonsUtil.semValor(pagadorCadastrado.getRg())
//						&& !CommonsUtil.semValor(pagadorAdicionar.getRg())) {
//					pagadorCadastrado.setRg(pagadorAdicionar.getRg());
//				}
//
//				if (CommonsUtil.semValor(pagadorCadastrado.getDtNascimento())
//						&& !CommonsUtil.semValor(pagadorAdicionar.getDtNascimento())) {
//					pagadorCadastrado.setDtNascimento(pagadorAdicionar.getDtNascimento());
//				}
//				if (pagadorCadastrado.getId() > 0)
//					pDao.merge(pagadorCadastrado);
//				else
//					pDao.create(pagadorAdicionar);
//				pagadorAdicionar = pagadorCadastrado;
//
//			} else if (!CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {
//				List<PagadorRecebedor> cadastrados = pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj());
//				PagadorRecebedor pagadorCadastrado = new PagadorRecebedor();
//
//				if (cadastrados.size() > 0)
//					pagadorCadastrado = cadastrados.get(0);
//				else
//					pagadorCadastrado = pagadorAdicionar;
//
//				if (CommonsUtil.semValor(pagadorCadastrado.getCnpj())
//						&& !CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {
//					pagadorCadastrado.setCnpj(pagadorAdicionar.getCnpj());
//				}
//
//				if (pagadorCadastrado.getId() > 0)
//					pDao.merge(pagadorCadastrado);
//				else
//					pDao.create(pagadorAdicionar);
//				pagadorAdicionar = pagadorCadastrado;
//
//			} else {
//				long idIncluido = pDao.create(pagadorAdicionar);
//				pagadorAdicionar = pDao.findById(idIncluido);
//				pDao.merge(pagadorAdicionar);
//			}
//		}
//		return pagadorAdicionar;
//
//	}

	public ImovelCobrancaRestricao findById(Long id) {
		ImovelCobrancaRestricao imovelCobrancaRestricao = null;

		if (!CommonsUtil.semValor(id)) {
			ImovelCobrancaRestricaoDao icrDao = new ImovelCobrancaRestricaoDao();
			imovelCobrancaRestricao = icrDao.findById(id);

		}
		return imovelCobrancaRestricao;

	}

	public List<String> verificaRestricao(ContratoCobranca contratoCobranca) {
		

		Set<ImovelCobranca> lstImovel = new HashSet<ImovelCobranca>(Arrays.asList(contratoCobranca.getImovel()));

		lstImovel.addAll(
				contratoCobranca.getListaImoveis().stream().map(l -> l.getImovel()).collect(Collectors.toSet()));

		Set<ImovelCobrancaRestricao> restricoes = new HashSet<ImovelCobrancaRestricao>(0);

		for (ImovelCobranca imovelCobranca : lstImovel) {
			restricoes.addAll( getImovelRestricoes( imovelCobranca));
		}

		List<String> result = new ArrayList<String>(0);
		if (!CommonsUtil.semValor(restricoes))
			result = restricoes.stream()
					.map(r -> String.format("Matricula %s com restrição desde %s%s", r.getNumeroMatricula(),
							CommonsUtil.formataData(r.getDataCadastro(), "dd/MM/yyyy"),
							(!CommonsUtil.semValor(r.getContratoCobranca()))
									? " pelo contrato Nº " + r.getContratoCobranca().getNumeroContrato()
									: ""))
					.collect(Collectors.toList());
		return result;
	}

	public  Set<ImovelCobrancaRestricao>  getImovelRestricoes(ImovelCobranca imovelCobranca) {
		ImovelCobrancaRestricaoDao icrDao = new ImovelCobrancaRestricaoDao();
		
		String[] sMatriculas = imovelCobranca.getNumeroMatricula().split(",");
		Set<ImovelCobrancaRestricao> restricoes = new HashSet<ImovelCobrancaRestricao>();
		for (String matricula : sMatriculas) {
			restricoes.addAll(icrDao.pesquisaImovelRestricao(matricula, imovelCobranca.getNumeroCartorio(),
					imovelCobranca.getCartorio(), imovelCobranca.getCartorioEstado(), imovelCobranca.getCartorioMunicipio()));
		}
		
		return restricoes;
		
	}
	
	public boolean adicionarBlackFlagImovel(ImovelCobranca imovelCobranca, ContratoCobranca contratoCobranca,
			User usuario) {
		try {
			ImovelCobrancaRestricaoDao icrDao = new ImovelCobrancaRestricaoDao();			
			ImovelCobrancaRestricao imovelCobrancaRestricao = new ImovelCobrancaRestricao();
			imovelCobrancaRestricao.setNumeroMatricula(imovelCobranca.getNumeroMatricula());
			imovelCobrancaRestricao.setCartorio(imovelCobranca.getCartorio());
			imovelCobrancaRestricao.setNumeroCartorio(imovelCobranca.getNumeroCartorio());
			imovelCobrancaRestricao.setCartorioEstado(imovelCobranca.getCartorioEstado());
			imovelCobrancaRestricao.setCartorioMunicipio(imovelCobranca.getCartorioMunicipio());
			imovelCobrancaRestricao.setAtiva(true);
			imovelCobrancaRestricao.setUsuarioCadastro(usuario.getLogin());
			imovelCobrancaRestricao.setDataCadastro(new Date());
			imovelCobrancaRestricao.setContratoCobranca(contratoCobranca);
			icrDao.create(imovelCobrancaRestricao);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean removerBlackFlagImovel(ImovelCobranca imovelCobranca, ContratoCobranca contratoCobranca,
			User usuario) {
		ImovelCobrancaRestricaoDao icrDao = new ImovelCobrancaRestricaoDao();

		try {
			List<ImovelCobrancaRestricao> imovelCobrancaRestricoes = icrDao.findByFilter("numeroMatricula",
					imovelCobranca.getNumeroMatricula());
			for (ImovelCobrancaRestricao imovelCobrancaRestricao : imovelCobrancaRestricoes) {
				imovelCobrancaRestricao.setAtiva(false);
				imovelCobrancaRestricao.setUsuarioInativa(usuario.getLogin());
				imovelCobrancaRestricao.setDataInativa(new Date());
				icrDao.update(imovelCobrancaRestricao);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}