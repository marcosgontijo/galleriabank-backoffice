package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.service.BigDataService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name = "bigDataMB")
@SessionScoped
public class BigDataMB {

	private BigDataService bigDataService;

	public BigDataMB() {
		bigDataService = new BigDataService();
	}


	public void baixarDocumentoProcesso(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = bigDataService.baixarDocumentoProcesso(documentoAnalise);
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
		String nomeArquivoDownload = String.format("Galleria Bank - " + Relatorio + " %s.pdf", CommonsUtil.somenteNumeros(cnpjcpf));
		gerador.open(nomeArquivoDownload);
		gerador.feed(in);
		gerador.close();
		return null;

	}


}