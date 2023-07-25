package com.webnowbr.siscoat.cobranca.service;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoRequestEnterprisePartnership;
import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;

public class DocumentoAnaliseService {

	public void cadastrarPessoRetornoCredNet(PessoaParticipacao pessoaParticipacao, User user,
			DocumentoAnaliseDao documentoAnaliseDao, PagadorRecebedorService pagadorRecebedorService,
			ContratoCobranca contratoCobranca, String motivo) {

		DocumentoAnalise documentoAnalise = new DocumentoAnalise();
		documentoAnalise.setContratoCobranca(contratoCobranca);
		documentoAnalise.setIdentificacao(pessoaParticipacao.getNomeRazaoSocial());

		documentoAnalise.setTipoPessoa("PJ");
		documentoAnalise.setMotivoAnalise(motivo);

		if (documentoAnalise.getTipoPessoa() == "PJ") {
			documentoAnalise.setCnpjcpf(pessoaParticipacao.getCnpjcpf());
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			documentoAnalise.setLiberadoAnalise(false);
		} else {
			documentoAnalise.setCnpjcpf(pessoaParticipacao.getCnpjcpf());
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.CREDNET);
		}

		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);

		pagador.setCnpj(pessoaParticipacao.getCnpjcpf());
		pagador.setNome(pessoaParticipacao.getNomeRazaoSocial());

		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		documentoAnalise.setPagador(pagador);

		documentoAnaliseDao.create(documentoAnalise);

	}
	
	public void cadastrarPessoRetornoEngine(EngineRetornoRequestEnterprisePartnership partnership, User user,
			DocumentoAnaliseDao documentoAnaliseDao, PagadorRecebedorService pagadorRecebedorService,
			ContratoCobranca contratoCobranca, String motivo) {

		DocumentoAnalise documentoAnalise = new DocumentoAnalise();
		documentoAnalise.setContratoCobranca(contratoCobranca);
		documentoAnalise.setIdentificacao(partnership.getCompanyName());

		documentoAnalise.setTipoPessoa("PJ");
		documentoAnalise.setMotivoAnalise(motivo);

		if (documentoAnalise.getTipoPessoa() == "PJ") {
			documentoAnalise.setCnpjcpf(partnership.getCNPJ());
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			documentoAnalise.setLiberadoAnalise(false);
		} else {
			return;
		}

		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);

		pagador.setCnpj(partnership.getCNPJ());
		pagador.setNome(partnership.getCompanyName());

		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		documentoAnalise.setPagador(pagador);

		documentoAnaliseDao.create(documentoAnalise);

	}
	public void cadastrarPessoRetornoEngine(EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership pJPartnership, User user,
			DocumentoAnaliseDao documentoAnaliseDao, PagadorRecebedorService pagadorRecebedorService,
			ContratoCobranca contratoCobranca, String motivo) {
		
		

		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);

		
		DocumentoAnalise documentoAnalise = new DocumentoAnalise();
		documentoAnalise.setContratoCobranca(contratoCobranca);
		documentoAnalise.setIdentificacao(pJPartnership.getRelatedEntityName());

		if( CommonsUtil.mesmoValor( pJPartnership.getRelatedEntityTaxIdType(), "CPF" )) {
			documentoAnalise.setTipoPessoa("PF");
			documentoAnalise.setCnpjcpf( CommonsUtil.formataCpf(pJPartnership.getRelatedEntityTaxIdNumber()));
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.CREDNET);
			pagador.setCpf(documentoAnalise.getCnpjcpf());
		} else {
			documentoAnalise.setTipoPessoa("PJ");
			documentoAnalise.setCnpjcpf( CommonsUtil.formataCnpj(pJPartnership.getRelatedEntityTaxIdNumber()));
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			pagador.setCnpj(documentoAnalise.getCnpjcpf());
		}
		
		if ( documentoAnaliseDao.cadastradoAnalise(contratoCobranca, documentoAnalise.getCnpjcpf()))
			return;
		
		documentoAnalise.setMotivoAnalise(motivo);
		documentoAnalise.setLiberadoAnalise(false);

		pagador.setNome(documentoAnalise.getIdentificacao());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		documentoAnalise.setPagador(pagador);
		

		documentoAnaliseDao.create(documentoAnalise);
		

	}

}
