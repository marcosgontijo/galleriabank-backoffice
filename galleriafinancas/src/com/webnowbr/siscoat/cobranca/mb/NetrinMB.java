package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

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

	
	public void baixarDocumentoPpe(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = netrinService.baixarDocumentoPpe(documentoAnalise);
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
	
	public void baixarDocumentoDossie(DocumentoAnalise documentoAnalise) {
		String documentoBase64 = netrinService.baixarDocumentoDossie(documentoAnalise);
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
	
	public void baixarDocumentoProcesso(DocumentoAnalise documentoAnalise) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {

			String documentoBase64 = netrinService.baixarDocumentoProcesso(documentoAnalise);
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Processos: Ocorreu um problema ao gerar o PDF!", ""));
				return;
			}

			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

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
			output.flush();
			output.close();
			
		} catch (NullPointerException e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Processos: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Processos: Ocorreu um problema ao gerar o PDF!", ""));
		}
	}
	
	public void baixarDocumentoCNDEstadual(DocumentoAnalise documentoAnalise) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {

			String documentoBase64 = netrinService.baixarDocumentoCNDEstadual(documentoAnalise);
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"CND Estadual: Ocorreu um problema ao gerar o PDF!", ""));
				return;
			}

			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

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
			output.flush();
			output.close();
			
		} catch (NullPointerException e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"CND Estadual: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"CND Estadual: Ocorreu um problema ao gerar o PDF!", ""));

		}
	}
	
	public void baixarDocumentoCNDFederal(DocumentoAnalise documentoAnalise) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {

			String documentoBase64 = netrinService.baixarDocumentoCNDFederal(documentoAnalise);
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"CND Federal: Ocorreu um problema ao gerar o PDF!", ""));
				return;
			}

			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

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
			output.flush();
			output.close();
			
		} catch (NullPointerException e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"CND Federal: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"CND Federal: Ocorreu um problema ao gerar o PDF!", ""));

		}
	}
	
	public void baixarDocumentoCNDTrabalhistaTST(DocumentoAnalise documentoAnalise) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {

			String documentoBase64 = netrinService.baixarDocumentoCNDTrabalhistaTST(documentoAnalise);
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"CNDT TST: Ocorreu um problema ao gerar o PDF!", ""));
				return;
			}

			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

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
			output.flush();
			output.close();
			
		} catch (NullPointerException e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"CNDT TST: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"CNDT TST: Ocorreu um problema ao gerar o PDF!", ""));

		}
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