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
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetornoBloco;
import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetornoProprietario;
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

		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {
			CredNet credNet = GsonUtil.fromJson(documentoAnalise.getRetornoSerasa(), CredNet.class);

			if (!CommonsUtil.semValor(credNet.getParticipacoes())) {

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();

				for (PessoaParticipacao pessoaParticipacao : credNet.getParticipacoes()) {

					cadastrarPessoRetornoCredNet(pessoaParticipacao, documentoAnaliseDao,
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

		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
			gerador.open(String.format("Galleria Bank - CredNet %s.pdf",
					CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf())));
		else
			gerador.open(String.format("Galleria Bank - Relato %s.pdf",
					CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf())));

		gerador.feed(in);
		gerador.close();
		return null;
	}

	private void cadastrarPessoRetornoCredNet(PessoaParticipacao pessoaParticipacao, DocumentoAnaliseDao documentoAnaliseDao,
			ContratoCobranca contratoCobranca, String motivo) {

	

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
			
			documentoAnaliseDao.create(documentoAnalise);
			
		}


}