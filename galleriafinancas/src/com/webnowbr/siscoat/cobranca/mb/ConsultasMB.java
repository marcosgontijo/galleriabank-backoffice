package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Base64;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
@ManagedBean(name = "consultasMB")
@SessionScoped

public class ConsultasMB {
		private String cpfCnpj;
		private String retornoSerasa;
		private String retornoCenprot;
		private String retornoProtestos;
		private String retornoPEP;
		
		
		public String clear() {
			cpfCnpj = "";
			return "/Atendimento/ConsultasDirectd/Consultas.xhtml";
			
		}
		public void consultaSerasa() throws MalformedURLException, ProtocolException, UnsupportedEncodingException, IOException  {
			SerasaService serasa = new SerasaService();
			retornoSerasa =	serasa.serasaCriarConsulta(cpfCnpj);
			String docBase64 = serasa.baixarDocumentoConsulta(retornoSerasa, "PF");
			decodarBaixarArquivo(cpfCnpj, docBase64);
			
			
		}
		public void consultaCenprot() throws Exception{
			NetrinService cenprot = new NetrinService();
			retornoCenprot = cenprot.netrinCriarConsultaCenprot(cpfCnpj);
			String doc = cenprot.baixarDocumentoCenprot(retornoCenprot);
			decodarBaixarArquivo(cpfCnpj, doc);
		
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
		public String getRetornoProtestos() {
			return retornoProtestos;
		}
		public void setRetornoProtestos(String retornoProtestos) {
			this.retornoProtestos = retornoProtestos;
		}
		public String getRetornoCenprot() {
			return retornoCenprot;
		}
		public void setRetornoCenprot(String retornoCenprot) {
			this.retornoCenprot = retornoCenprot;
		}
		public String getRetornoPEP() {
			return retornoPEP;
		}
		public void setRetornoPEP(String retornoPEP) {
			this.retornoPEP = retornoPEP;
		}
		
		

	}
	
	



