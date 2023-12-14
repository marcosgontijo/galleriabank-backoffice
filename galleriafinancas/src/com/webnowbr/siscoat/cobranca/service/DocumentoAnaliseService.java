package com.webnowbr.siscoat.cobranca.service;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.job.DocumentoAnaliseJob;

import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoRequestEnterprisePartnership;
import br.com.galleriabank.jwt.common.JwtUtil;
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
		
		DocumentoAnalise documentoAnalisePesquisa = documentoAnaliseDao.cadastradoAnalise(contratoCobranca,
				documentoAnalise.getCnpjcpf());

		if (!CommonsUtil.semValor(documentoAnalisePesquisa))
			documentoAnalise = documentoAnalisePesquisa;
		else {
			PagadorRecebedor pagador = new PagadorRecebedor();
			pagador.setId(0);

			pagador.setCnpj(pessoaParticipacao.getCnpjcpf());
			pagador.setNome(pessoaParticipacao.getNomeRazaoSocial());

			pagador = pagadorRecebedorService.buscaOuInsere(pagador);
			documentoAnalise.setPagador(pagador);
		}	

		
		documentoAnalise.adiconarEstadosPeloCadastro();
		documentoAnaliseDao.create(documentoAnalise);
		return documentoAnalise.getPagador();
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
		String sCPFCNPJ ="";
		
		if (CommonsUtil.mesmoValor(partnership.getEntityType(), "J")) {
			sCPFCNPJ = CommonsUtil.formataCnpjCpf(CommonsUtil.strZero(partnership.getCNPJ(), 14), false);
//		else
//			sCPFCNPJ =  CommonsUtil.formataCnpjCpf(CommonsUtil.strZero(partnership.getCNPJ(),9), false);

			documentoAnalise.setTipoPessoa("PJ");
			documentoAnalise.setMotivoAnalise(motivo);

			// if (documentoAnalise.getTipoPessoa() == "PJ") {
			documentoAnalise.setCnpjcpf(sCPFCNPJ);

			DocumentoAnalise documentoAnalisePesquisa = documentoAnaliseDao.cadastradoAnalise(contratoCobranca,
					sCPFCNPJ);
			documentoAnalisePesquisa.setLiberadoAnalise(true);
			if (!CommonsUtil.semValor(documentoAnalisePesquisa))
				documentoAnalise = documentoAnalisePesquisa;

			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			documentoAnalise.setLiberadoAnalise(true);
			
		
			
			
		} else {
			return;
		}

		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);

		pagador.setCnpj(sCPFCNPJ);
		pagador.setNome(partnership.getCompanyName());

		pagador = pagadorRecebedorService.preecheDadosReceita(pagador);
		
		documentoAnalise.setPagador(pagador);
		documentoAnalise.adiconarEstadosPeloCadastro();
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

		boolean cnpjCpfValido = false;
		if( CommonsUtil.mesmoValor( pJPartnership.getRelatedEntityTaxIdType(), "CPF" )) {
			documentoAnalise.setTipoPessoa("PF");
			documentoAnalise.setCnpjcpf( CommonsUtil.formataCnpjCpf(CommonsUtil.strZero(pJPartnership.getRelatedEntityTaxIdNumber(),11),false));
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.CREDNET);
			cnpjCpfValido = ValidaCPF.isCPF(CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf()));
			
			pagador.setCpf(documentoAnalise.getCnpjcpf());
		} else {
			documentoAnalise.setTipoPessoa("PJ");
			documentoAnalise.setCnpjcpf( CommonsUtil.formataCnpjCpf(CommonsUtil.strZero(pJPartnership.getRelatedEntityTaxIdNumber(), 14),false));
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			cnpjCpfValido = ValidaCNPJ.isCNPJ(CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf()));
			
			pagador.setCnpj(documentoAnalise.getCnpjcpf());
		}
		
		if (!cnpjCpfValido) {
			System.out.println("******** cnpjCpfValido Invalido /");
			System.out.println(GsonUtil.toJson(pJPartnership));
			return;
		}
		
		if ( !CommonsUtil.semValor( documentoAnaliseDao.cadastradoAnalise(contratoCobranca, documentoAnalise.getCnpjcpf())))
			return;
		
		documentoAnalise.setMotivoAnalise(motivo);
		documentoAnalise.setLiberadoAnalise(true);

		pagador.setNome(documentoAnalise.getIdentificacao());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		documentoAnalise.setPagador(pagador);
		
		documentoAnalise.adiconarEstadosPeloCadastro();
		documentoAnaliseDao.create(documentoAnalise);
		
		
		try {
			List<DocumentoAnalise> listaDocumentoAnaliseAnalise = new ArrayList<DocumentoAnalise>(
					Arrays.asList(documentoAnalise));
			criandoJobExecutarConsultas(listaDocumentoAnaliseAnalise, user, documentoAnalise.getContratoCobranca());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PagadorRecebedor cadastrarPartnershipRetornoEngine(EngineRetornoRequestEnterprisePartnership partnership, 
			PagadorRecebedorService pagadorRecebedorService) {
		if (!CommonsUtil.mesmoValor(partnership.getEntityType(), "J")) {
			return null;
		} 
		
		String sCNPJ = CommonsUtil.formataCnpjCpf(CommonsUtil.strZero(partnership.getCNPJ(), 14), false);
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		pagador.setCnpj(sCNPJ);
		pagador.setNome(partnership.getCompanyName());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
	}
	
	public PagadorRecebedor cadastrarPartnershipRetornoEnginePJ(EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership partnership, 
			PagadorRecebedorService pagadorRecebedorService) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		pagador.setId(0);
		String cnpjCpf = partnership.getRelatedEntityTaxIdNumber();

		cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
		if (CommonsUtil.mesmoValor(partnership.getRelatedEntityTaxIdType(), "CPF")) {
			cnpjCpf = CommonsUtil.strZero(cnpjCpf, 11);
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCpf(cnpjCpf);
		} else {
			cnpjCpf = CommonsUtil.strZero(cnpjCpf, 14);
			cnpjCpf = CommonsUtil.formataCnpjCpf(cnpjCpf, false);
			pagador.setCnpj(cnpjCpf);
		}
		pagador.setNome(partnership.getRelatedEntityName());
		pagador = pagadorRecebedorService.buscaOuInsere(pagador);
		return pagador;
	}

	
	public void criandoJobExecutarConsultas(List<DocumentoAnalise> listaDocumentoAnaliseAnalise, User user, ContratoCobranca objetoContratoCobranca) throws SchedulerException {
		SchedulerFactory shedFact = new StdSchedulerFactory();
		Scheduler scheduler = shedFact.getScheduler();
		scheduler.start();
		JobDetail jobDetail = JobBuilder.newJob(DocumentoAnaliseJob.class)
				.withIdentity("documentoAnaliseJOB", objetoContratoCobranca.getNumeroContrato()).build();
	
		FacesContext fContext = FacesContext.getCurrentInstance();
		ExternalContext extContext = fContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) fContext.getExternalContext().getRequest();
		
		String webHookJWT = JwtUtil.generateJWTWebhook(true);
		String urlWenhook = SiscoatConstants.URL_SISCOAT_ENGINE_WEBHOOK + webHookJWT;
		jobDetail.getJobDataMap().put("listaDocumentoAnalise", listaDocumentoAnaliseAnalise);
		jobDetail.getJobDataMap().put("user", user);
		jobDetail.getJobDataMap().put("urlWenhook", urlWenhook);
		jobDetail.getJobDataMap().put("objetoContratoCobranca", objetoContratoCobranca);
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("documentoAnaliseJOB", objetoContratoCobranca.getNumeroContrato()).startNow().build();
		scheduler.scheduleJob(jobDetail, trigger);
	}
}
