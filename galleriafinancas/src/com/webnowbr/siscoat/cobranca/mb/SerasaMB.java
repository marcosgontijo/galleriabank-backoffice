package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name = "serasaMB")
@SessionScoped
public class SerasaMB {

	private SerasaService serasaService;

	public SerasaMB() {
		serasaService = new SerasaService();
	}

	public void requestSerasa(DocumentoAnalise documentoAnalise) {
		serasaService.serasaCriarConsulta(documentoAnalise);

	}

	
	public void baixarDocumentoSerasa(DocumentoAnalise documentoAnalise) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Consulta sem retorno", ""));	
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
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		
		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
			gerador.open(String.format("Galleria Bank - CredNet %s.pdf", CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf())));
		else
			gerador.open(String.format("Galleria Bank - Relato %s.pdf", CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf())));
			
		
		gerador.feed(in);
		gerador.close();
		return null;
	}

}