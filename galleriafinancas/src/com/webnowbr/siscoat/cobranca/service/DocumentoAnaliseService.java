package com.webnowbr.siscoat.cobranca.service;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;

public class DocumentoAnaliseService {

	public void cadastrarPessoRetornoCredNet(PessoaParticipacao pessoaParticipacao, User user,
			DocumentoAnaliseDao documentoAnaliseDao, PagadorRecebedorService pagadorRecebedorService,
			ContratoCobranca contratoCobranca, String motivo) {

		DocketService docketService = new DocketService();

		DocumentoAnalise documentoAnalise = new DocumentoAnalise();
		documentoAnalise.setContratoCobranca(contratoCobranca);
		documentoAnalise.setIdentificacao(pessoaParticipacao.getNomeRazaoSocial());

		documentoAnalise.setTipoPessoa("PJ");
		documentoAnalise.setMotivoAnalise(motivo);

		if (documentoAnalise.getTipoPessoa() == "PJ") {
			documentoAnalise.setCnpjcpf(pessoaParticipacao.getCnpjcpf());
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			documentoAnalise.setLiberadoAnalise(true);
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

		if (documentoAnalise.getTipoPessoa() == "PJ") {
			DataEngine engine = docketService.engineInserirPessoa(documentoAnalise.getPagador(), contratoCobranca);
			docketService.engineCriarConsulta(documentoAnalise, engine, user);
		}

	}
}
