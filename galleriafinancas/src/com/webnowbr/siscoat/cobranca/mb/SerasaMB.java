package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

import br.com.galleriabank.serasacrednet.cliente.model.CredNet;
import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;
import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

@ManagedBean(name = "serasaMB")
@SessionScoped
public class SerasaMB {

	private SerasaService serasaService;

	public SerasaMB() {
		serasaService = new SerasaService();
	}

	public void requestSerasa(DocumentoAnalise documentoAnalise) {
		serasaService.serasaCriarConsulta(documentoAnalise);
		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		documentoAnalise = documentoAnaliseDao.findById(documentoAnalise.getId());

		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {
			CredNet credNet = GsonUtil.fromJson(documentoAnalise.getRetornoSerasa(), CredNet.class);

			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.semValor(documentoAnalise.getPagador().getDtNascimento()))
					documentoAnalise.getPagador().setDtNascimento(credNet.getPessoa().getDataNascimentoFundacao());

				if (CommonsUtil.semValor(documentoAnalise.getPagador().getNomeMae()))
					documentoAnalise.getPagador().setNomeMae(credNet.getPessoa().getNomeMae());
			}

			if (!CommonsUtil.semValor(credNet.getParticipacoes())) {

				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

				for (PessoaParticipacao pessoaParticipacao : credNet.getParticipacoes()) {

					cadastrarPessoRetornoCredNet(pessoaParticipacao, documentoAnaliseDao, pagadorRecebedorService,
							documentoAnalise.getContratoCobranca(),
							"Empresa Vinculada ao " + documentoAnalise.getMotivoAnalise());
				}
			}

		}

	}

	public void baixarDocumentoSerasa(DocumentoAnalise documentoAnalise) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Consulta sem retorno", ""));
			return;
		}

		baixarDocumento(documentoAnalise);
	}

	public void baixarDocumento(DocumentoAnalise documentoAnalise) {

		String documentoBase64 = serasaService.baixarDocumento(documentoAnalise);
		decodarBaixarArquivo(documentoAnalise, documentoBase64);
	}

	public StreamedContent decodarBaixarArquivo(DocumentoAnalise documentoAnalise, String base64) {
		byte[] decoded = Base64.getDecoder().decode(base64);

		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		String cnpjcpf = documentoAnalise.getCnpjcpf();
		if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
			if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
				cnpjcpf = documentoAnalise.getPagador().getCpf();
			else
				cnpjcpf = documentoAnalise.getPagador().getCnpj();
		}
		
		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
			gerador.open(String.format("Galleria Bank - CredNet %s.pdf",
					CommonsUtil.somenteNumeros(cnpjcpf)));
		else
			gerador.open(String.format("Galleria Bank - Relato %s.pdf",
					CommonsUtil.somenteNumeros(cnpjcpf)));

		gerador.feed(in);
		gerador.close();
		return null;
	}

	private void cadastrarPessoRetornoCredNet(PessoaParticipacao pessoaParticipacao, DocumentoAnaliseDao documentoAnaliseDao,
			PagadorRecebedorService pagadorRecebedorService, ContratoCobranca contratoCobranca, String motivo) {

	

			DocumentoAnalise documentoAnalise = new DocumentoAnalise();
			documentoAnalise.setContratoCobranca(contratoCobranca);
			documentoAnalise.setIdentificacao(pessoaParticipacao.getNomeRazaoSocial());

			documentoAnalise.setTipoPessoa("PJ");
			documentoAnalise.setMotivoAnalise(motivo);
			
			if (documentoAnalise.getTipoPessoa() == "PJ") {
				documentoAnalise.setCnpjcpf(pessoaParticipacao.getCnpjcpf());
				documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
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


}