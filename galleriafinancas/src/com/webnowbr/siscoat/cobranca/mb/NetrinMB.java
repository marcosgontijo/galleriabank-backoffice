package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

import br.com.galleriabank.serasacrednet.cliente.model.CredNet;
import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;
import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

@ManagedBean(name = "netrinMB")
@SessionScoped
public class NetrinMB {

	private NetrinService netrinService;

	public NetrinMB() {
		netrinService = new NetrinService();
	}

	public void requestCenprot(DocumentoAnalise documentoAnalise) {

		netrinService.requestCenprot(documentoAnalise);

	}

	public void baixarDocumentoCenprot(DocumentoAnalise documentoAnalise) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Consulta sem retorno", ""));
			return;
		}

		baixarDocumento(documentoAnalise);
	}

	public void baixarDocumento(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = netrinService.baixarDocumento(documentoAnalise);
		decodarBaixarArquivo(documentoAnalise, documentoBase64, "Cenprot");
	}
	
	public void baixarDocumentoPpe(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = netrinService.baixarDocumentoPpe(documentoAnalise);
		decodarBaixarArquivo(documentoAnalise, documentoBase64, "PPE");
	}
	
	public void baixarDocumentoDossie(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = netrinService.baixarDocumentoDossie(documentoAnalise);
		decodarBaixarArquivo(documentoAnalise, documentoBase64, "Dossie");
	}
	
	public void baixarDocumentoProcesso(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = netrinService.baixarDocumentoProcesso(documentoAnalise);
		decodarBaixarArquivo(documentoAnalise, documentoBase64, "Processo");
	}

	public StreamedContent decodarBaixarArquivo(DocumentoAnalise documentoAnalise, String base64, String Relatorio) {
		if (CommonsUtil.semValor(base64)) {
			// System.out.println("Arquivo Base64 não existe");
			return null;
		}
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

		gerador.open(String.format("Galleria Bank - " + Relatorio + " %s.pdf", CommonsUtil.somenteNumeros(cnpjcpf)));

		gerador.feed(in);
		gerador.close();
		return null;
	}


}