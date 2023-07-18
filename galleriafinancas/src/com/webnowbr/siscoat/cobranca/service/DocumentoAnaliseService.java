package com.webnowbr.siscoat.cobranca.service;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoRequestEnterprisePartnership;
import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;
import br.com.galleriabank.serasarelato.cliente.model.Administrador;
import br.com.galleriabank.serasarelato.cliente.model.Participada;
import br.com.galleriabank.serasarelato.cliente.model.Participante;
import br.com.galleriabank.serasarelato.cliente.model.Socio;
import br.com.galleriabank.serasarelato.cliente.model.embedded.RelatoDadosCadastrais;

public class DocumentoAnaliseService {

	public PagadorRecebedor cadastrarPessoRetornoCredNet(PessoaParticipacao pessoaParticipacao, User user,
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
		return pagador;
	}
	
	public PagadorRecebedor cadastrarPagadorRetornoRelato(RelatoDadosCadastrais dados, 
			PagadorRecebedorService pagadorRecebedorService) {
		
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		String cnpj = dados.getCnpjEditado().trim();
		//cnpj = CommonsUtil.formataCnpjCpf(cnpj, false);
		pagador.setCnpj(cnpj);
		pagador.setNome(dados.getRazaoSocial());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
	}
	
	public PagadorRecebedor cadastrarParticipadaRetornoRelato(Participada participada, 
			PagadorRecebedorService pagadorRecebedorService) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		String digito = participada.getDigitoCnpj().toString();
		if(digito.length() == 1) {
			digito = "0" + digito;
		}
		String cnpj = participada.getCnpj() + "0001" + digito;
		cnpj = CommonsUtil.formataCnpjCpf(cnpj, false);
		pagador.setCnpj(cnpj);
		pagador.setNome(participada.getNome());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
	}
	
	public PagadorRecebedor cadastrarParticipanteRetornoRelato(Participante participante, 
			PagadorRecebedorService pagadorRecebedorService) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		String cnpjCpf;
		if(CommonsUtil.mesmoValor(participante.getParticipacaoTipo(), "J")) {
			String digito = participante.getDigitoCnpjCpf().toString();
			if(digito.length() == 1) {
				digito = "0" + digito;
			}
			cnpjCpf = participante.getCnpjCpf() + "0001" + digito;
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCnpj(cnpjCpf);
		} else {
			String digito = participante.getDigitoCnpjCpf().toString();
			if(digito.length() == 1) {
				digito = "0" + digito;
			}
			cnpjCpf = participante.getCnpjCpf() + digito;
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCpf(cnpjCpf);
		}
		pagador.setNome(participante.getNome());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
	}
	
	public PagadorRecebedor cadastrarAdministradorRetornoRelato(Administrador administrador, 
			PagadorRecebedorService pagadorRecebedorService) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		String cnpjCpf;
		if(CommonsUtil.mesmoValor(administrador.getTipo(), "J")) {
			String digito = administrador.getDigitoCpf().toString();
			if(digito.length() == 1) {
				digito = "0" + digito;
			}
			cnpjCpf = administrador.getCnpjCpf() + "0001" + digito;
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCnpj(cnpjCpf);
		} else {
			String digito = administrador.getDigitoCpf().toString();
			if(digito.length() == 1) {
				digito = "0" + digito;
			}
			cnpjCpf = administrador.getCnpjCpf() + digito;
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCpf(cnpjCpf);
		}
		pagador.setNome(administrador.getNome());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
	}
	
	public PagadorRecebedor cadastrarSocioRetornoRelato(Socio socio, 
			PagadorRecebedorService pagadorRecebedorService) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		String cnpjCpf;
		if(CommonsUtil.mesmoValor(socio.getTipo(), "J")) {
			String digito = socio.getDigitoCpf().toString();
			if(digito.length() == 1) {
				digito = "0" + digito;
			}
			cnpjCpf = socio.getCnpj() + "0001" + digito;
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCnpj(cnpjCpf);
		} else {
			String digito = socio.getDigitoCpf().toString();
			if(digito.length() == 1) {
				digito = "0" + digito;
			}
			cnpjCpf = socio.getCnpj() + digito;
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCpf(cnpjCpf);
		}
		pagador.setNome(socio.getNome());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
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

}
