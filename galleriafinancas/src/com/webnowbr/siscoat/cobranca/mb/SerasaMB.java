package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.DocketService;
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

@ManagedBean(name = "serasaMB")
@SessionScoped
public class SerasaMB {
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	

	private SerasaService serasaService;

	public SerasaMB() {
		serasaService = new SerasaService();
	}

	public void requestSerasa(DocumentoAnalise documentoAnalise) {

		serasaService.requestSerasa(documentoAnalise, loginBean.getUsuarioLogado());
		
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
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {

			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			String mineFile = "application/pdf";
			input = new BufferedInputStream(new ByteArrayInputStream(pdfBytes));
			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);

			response.setContentLength(pdfBytes.length);

			response.setHeader("Content-disposition", "inline; FileName=" + "Engine.pdf");
			output = new BufferedOutputStream(response.getOutputStream(), 10240);
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			// Finalize task.
			output.flush();
			output.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent decodarBaixarArquivo(DocumentoAnalise documentoAnalise, String base64) {
		if(CommonsUtil.semValor(base64)) {
			//System.out.println("Arquivo Base64 não existe");
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
		String nomeArquivoDownload ="";
		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
			nomeArquivoDownload =String.format("Galleria Bank - CredNet %s.pdf",
					CommonsUtil.somenteNumeros(cnpjcpf));
		else
			nomeArquivoDownload = String.format("Galleria Bank - Relato %s.pdf",
					CommonsUtil.somenteNumeros(cnpjcpf));
		gerador.open(nomeArquivoDownload);
		
		gerador.feed(in);
		gerador.close();
		return null;
	}

	


	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}


}