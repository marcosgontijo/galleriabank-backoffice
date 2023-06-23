package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name = "consultaSerasaMB")
@SessionScoped

public class ConsultaSerasaMB {
	private String cpfCnpj;
	private String retornoSerasa;
	
	
	public String clear() {
		cpfCnpj = "";
		return "/Atendimento/ConsultasDirectd/consultaSerasa.xhtml";
		
	}
	public void consultaSerasa() throws MalformedURLException, ProtocolException, UnsupportedEncodingException, IOException  {
		SerasaService serasa = new SerasaService();
		retornoSerasa =	serasa.serasaCriarConsulta(cpfCnpj);
		String docBase64 = serasa.baixarDocumentoConsulta(retornoSerasa, "PF");
		decodarBaixarArquivo(cpfCnpj, docBase64);
		
		
	}
	public StreamedContent decodarBaixarArquivo(String cpfCnpj, String base64 ) {

		byte[] decoded = Base64.getDecoder().decode(base64);

		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		String cnpjcpf = this.cpfCnpj;
		gerador.open(String.format("Galleria Bank - CredNet %s.pdf",
		CommonsUtil.somenteNumeros(cnpjcpf)));

		gerador.feed(in);
		gerador.close();
		return null;
	}


	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	
	

}
