package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.cobranca.service.ScrService;
import com.webnowbr.siscoat.cobranca.vo.FileGenerator;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.GsonUtil;

@ManagedBean(name = "bmpDigitalMB")
@SessionScoped
public class BmpDigitalMB {

	/****
	 * 
		
	{
	  "auth": {
	    "Usuario": "joao@galleriafinancas.com.br",
	    "Senha": "Scr!2021",
	    "CodigoParametro": "GALLERIA_SCR",
	    "Chave": "eb11110f-9f0e-4a16-83d7-6229c949da4a"
	  },
	
	
	•	URL de Produção
	o	Integração
	https://bmpdigital.moneyp.com.br/api/BMPDigital/ <- concatenando o serviço
	o	Swagger
			https://bmpteste.moneyp.com.br/swagger/ui/index
	o	Dashboard
		https://bmpdigital.moneyp.com.br/
		Para acessarem podem utilizar o mesmo login e senha da integração
		Caso necessitem de outros usuários podem solicitar que liberamos

	 */

	
	/***
	 * INICIO ATRIBUTOS RECIBO
	 */
	
	private String documento;
	
	private TransferenciasObservacoesIUGU transferenciasObservacoesIUGU;
	private boolean pdfGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent file;
	/***
	 * FIM ATRIBUTOS RECIBO
	 */



	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA TRANSFERENCIA DE VALORES SUBCONTAS IUGU
	 * 
	 * @return
	 */
	
	public String clearFields() {
		this.documento = "";
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		this.pdfGerado = false;
		
		return "/Atendimento/Cobranca/ConsultaSCR.xhtml";
	}

	// retorna a string do objeto JSON, mesmo em caso de nulos
	public boolean getObjectJSON(JSONObject objetoJSON, String chave) {
		if (objetoJSON.has(chave)) {
			if (!objetoJSON.isNull(chave)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * EFETUA TRANSFERENCIA ENTRE CONTAS
	 */
	public void consultaSCR() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();

			ScrService scrService = new ScrService();
			ScrResult scrResult = scrService.consultaSCR(documento, context);
						
			FileGenerator fileGenerator = new FileGenerator();
			fileGenerator.setDocumento(documento);

			if (!CommonsUtil.semValor(scrResult)) {
				System.out.println("SUCESSO NA GERAÇÃO DO SCR");
				// gera pdf
				scrService.imprimeContrato(scrResult.getResumoDoCliente(), scrResult.getResumoDoClienteTraduzido(),
						fileGenerator, context);
				
				this.pdfGerado = fileGenerator.isPdfGerado();
				this.pathPDF = fileGenerator.getPath();
				this.nomePDF= fileGenerator.getName();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void baixarDocumento ( DocumentoAnalise documentoAnalise) {

		FileGenerator fileGenerator = new FileGenerator();
		fileGenerator.setDocumento(documentoAnalise.getCnpjcpf());
		
		ScrResult scrResult = GsonUtil.fromJson(documentoAnalise.getRetornoScr(), ScrResult.class);
		
		ScrService scrService = new ScrService();
		byte[] contrato = scrService.geraContrato(scrResult, fileGenerator);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {

		
			String mineFile = "application/pdf";
			input = new BufferedInputStream(new ByteArrayInputStream(contrato));
			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);

			response.setContentLength(contrato.length);

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
			facesContext.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent decodarBaixarArquivo(String base64) {
		if(CommonsUtil.semValor(base64)) {
			//System.out.println("Arquivo Base64 não existe");
			return null;
		}
		
		byte[] decoded = Base64.getDecoder().decode(base64);
		
		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		String nomeArquivoDownload = String.format("Galleria Bank - Data Engine %s.pdf", "");
		gerador.open(nomeArquivoDownload);
		gerador.feed(in);
		gerador.close();
		return null;
	}
	
	
	
	
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO 
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	public TransferenciasObservacoesIUGU getTransferenciasObservacoesIUGU() {
		return transferenciasObservacoesIUGU;
	}

	public void setTransferenciasObservacoesIUGU(TransferenciasObservacoesIUGU transferenciasObservacoesIUGU) {
		this.transferenciasObservacoesIUGU = transferenciasObservacoesIUGU;
	}

	public boolean isPdfGerado() {
		return pdfGerado;
	}

	public void setPdfGerado(boolean pdfGerado) {
		this.pdfGerado = pdfGerado;
	}

	public String getPathPDF() {
		return pathPDF;
	}
	public void setPathPDF(String pathPDF) {
		this.pathPDF = pathPDF;
	}
	public String getNomePDF() {
		return nomePDF;
	}
	public void setNomePDF(String nomePDF) {
		this.nomePDF = nomePDF;
	}
	public StreamedContent getFile() {
		String caminho =  this.pathPDF + this.nomePDF;        
		String arquivo = this.nomePDF;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		file = new DefaultStreamedContent(stream, caminho, arquivo); 

		return file;  
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
	
	
}