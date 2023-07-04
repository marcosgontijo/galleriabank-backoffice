package com.webnowbr.siscoat.cobranca.ws.caf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name = "combateAFraudeMB")
@SessionScoped
public class CombateAFraudeMB {

	String fileType;
	List<CombateAFraudeFiles> files = new ArrayList<CombateAFraudeFiles>();
	List<CombateAFraudeFiles> deleteFiles = new ArrayList<CombateAFraudeFiles>();
	String templateIdSelfie = "6304e61854dcba000929036b";
	String templateIdSemSelfie = "6359735b29768d000849aa5d";
	String cpf;

	public String clearFields() {
		return "/Atendimento/CombateAFraude/CombateAFraude.xhtml";
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		CombateAFraudeFiles cafFile = new CombateAFraudeFiles();
		//File file = uploadedFileToFileConverter(event.getFile());
		try {
			cafFile.data = encode64(event.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cafFile.type = fileType;
		fileType = "";
		files.add(cafFile);
    }
		
	public String encode64(UploadedFile file) throws IOException {
		String encoded = Base64.getMimeEncoder().encodeToString(file.getContents());
		encoded = "data:" + file.getContentType() + ";base64," + encoded;
		return encoded;
	}
	
	public StreamedContent downloadDoc(CombateAFraudeFiles file) {
		///https://stackoverflow.com/questions/25763533/how-to-identify-file-type-by-base64-encoded-string-of-a-image	
		InputStream in = file.getInputStream();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		gerador.open(String.format("Galleria Bank - " + file.type + " %s."+ file.getFileExtension(), ""));
		gerador.feed(in);
		gerador.close();
		return null;
	}
		
	public void combateAFraudeTransaction() {
		CombateAFraudeWebhook combateAFraudeWebhook = new CombateAFraudeWebhook();
		CombateAFraudeTransaction transaction = comporObjeto();
		if(!CommonsUtil.semValor(transaction)) {
			combateAFraudeWebhook.ChamarCombateAFraude(transaction, null);
		}
	}
	
	public CombateAFraudeTransaction comporObjeto() {
		FacesContext context = FacesContext.getCurrentInstance();
		String templateId;
		boolean encontrouSelfie = false;
		for(CombateAFraudeFiles file : files) {
			if(CommonsUtil.mesmoValor(file.type, "SELFIE")) {
				encontrouSelfie = true;
				break;
			}
		}
			
		if(encontrouSelfie) {
			templateId = templateIdSelfie;
		} else {
			templateId = templateIdSemSelfie;
		}
		
		CombateAFraudeAttributes attributes = new CombateAFraudeAttributes();
		attributes.cpf = CommonsUtil.somenteNumeros(cpf);
		CombateAFraudeTransaction transaction = new CombateAFraudeTransaction();
		transaction.files = files;
		transaction.templateId = templateId;
		transaction.attributes = attributes;
		return transaction;
	}
	
	public void viewFileCaF(CombateAFraudeFiles file) {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			
			InputStream in = file.getInputStream();
			input = new BufferedInputStream(in, 10240);

			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", file.getMimeType());

			//response.setContentLength((int) in.read());

			response.setHeader("Content-disposition", "inline; filename=" + file.getType());
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			// Write file contents to response.
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			// Finalize task.
			output.flush();
			output.close();
			facesContext.responseComplete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteFile(CombateAFraudeFiles file) {
		files.remove(file);
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	/*public static File uploadedFileToFileConverter(UploadedFile uf) {
	    InputStream inputStream = null;
	    OutputStream outputStream = null;
	    //Add you expected file encoding here:
	    System.setProperty("file.encoding", "UTF-8");
	    File newFile = new File(uf.getFileName());
	    try {
	        inputStream = uf.getInputstream();
	        outputStream = new FileOutputStream(newFile);
	        int read = 0;
	        byte[] bytes = new byte[1024];
	        while ((read = inputStream.read(bytes)) != -1) {
	            outputStream.write(bytes, 0, read);
	        }
	    } catch (IOException e) {
	       //Do something with the Exception (logging, etc.)
	    }
	    return newFile;
	}*/
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public List<CombateAFraudeFiles> getFiles() {
		return files;
	}
	public void setFiles(List<CombateAFraudeFiles> files) {
		this.files = files;
	}

	public List<CombateAFraudeFiles> getDeleteFiles() {
		return deleteFiles;
	}

	public void setDeleteFiles(List<CombateAFraudeFiles> deleteFiles) {
		this.deleteFiles = deleteFiles;
	}

	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}	
}
