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
import javax.faces.view.facelets.FaceletContext;

import org.apache.commons.logging.Log;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
@ManagedBean(name = "consultasMB")
@SessionScoped

public class ConsultasMB {
		private String cpfCnpj;
//		private String retornoSerasa;
//		private String retornoCenprot;
//		private String retornoProtestos;
//		private String retornoPEP;
		
		
		public String clear() {
			cpfCnpj = "";
			return "/Atendimento/ConsultasDirectd/Consultas.xhtml";
			
		}

		public void consultaSerasa()
				throws MalformedURLException, ProtocolException, UnsupportedEncodingException, IOException {
			FacesContext context = FacesContext.getCurrentInstance();
			try {
				SerasaService serasa = new SerasaService();
				String retornoSerasa = serasa.serasaCriarConsulta(cpfCnpj);
				if (retornoSerasa != null) {
					String Base64 = serasa.baixarDocumentoConsulta(retornoSerasa, "PF");
					decodarBaixarArquivo(cpfCnpj, Base64);
				} else {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta", ""));
				}
			} catch (Exception e) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta", ""));

			}

		}

		public void consultaCenprot() throws Exception {
			FacesContext context = FacesContext.getCurrentInstance();
			try {
				NetrinService cenprot = new NetrinService();
				String retornoCenprot = cenprot.netrinCriarConsultaCenprot(cpfCnpj);
				if (retornoCenprot != null) {
					String base64 = cenprot.baixarDocumentoCenprot(retornoCenprot);
					decodarBaixarArquivo(cpfCnpj, base64);
				} else {

					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta", ""));
				}
			} catch (Exception e) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta", ""));
			}

		}

		public void consultarProcessos() {
			FacesContext context = FacesContext.getCurrentInstance();
			try {
			NetrinService processos = new NetrinService();
			String retornoProcessos = processos.netrinCriarConsultaProcesso(cpfCnpj);
			if(retornoProcessos != null) {
			String base64 = processos.baixarDocumentoProcesso(retornoProcessos);
			decodarBaixarArquivo(cpfCnpj, base64);
			} else {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta",""));
			}
			} catch(Exception e) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta",""));
				
			}
		}
		public void consultaPEP() {
			FacesContext context = FacesContext.getCurrentInstance();
			try {
			NetrinService pep = new NetrinService();
			String retornoPEP = pep.netrinCriarConsultaCadastroPpePF(cpfCnpj);
			if(retornoPEP != null) {
			String base64 = pep.baixarDocumentoPpe(retornoPEP);
			decodarBaixarArquivo(cpfCnpj, base64);
			} else {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta",""));
			}
			} catch(Exception e) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao baixar Consulta",""));
				
			}
			
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
	
	



