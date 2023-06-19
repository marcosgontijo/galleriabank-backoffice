package com.webnowbr.siscoat.cobranca.ws.caf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name = "combateAFraudeMB")
@SessionScoped
public class CombateAFraudeMB {

	String fileType;
	boolean temSelfie;
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
		String base64 = file.data;
		String delims="[,]";
	    String[] parts = base64.split(delims);
	    String imageString = parts[1];
	    byte[] imageByteArray = Base64.getMimeDecoder().decode(imageString);
	    
	    InputStream is = new ByteArrayInputStream(imageByteArray);

	    //Find out image type
	    String mimeType = null;
	    String fileExtension = null;
	    try {
	        mimeType = URLConnection.guessContentTypeFromStream(is); //mimeType is something like "image/jpeg"
	        String delimiter="[/]";
	        String[] tokens = mimeType.split(delimiter);
	        fileExtension = tokens[1];
	    } catch (IOException ioException){

	    }
	    
		/*byte[] decoded = Base64.getDecoder().decode(file.data);*/
		InputStream in = new ByteArrayInputStream(imageByteArray);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		gerador.open(String.format("Galleria Bank - " + file.type + " %s."+ fileExtension, ""));
		gerador.feed(in);
		gerador.close();
		return null;
	}
	
	public static File uploadedFileToFileConverter(UploadedFile uf) {
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
	}
	
	public void combateAFraudeTransaction() {
		CombateAFraudeWebhook combateAFraudeWebhook = new CombateAFraudeWebhook();
		CombateAFraudeTransaction transaction = comporObjeto();
		if(CommonsUtil.semValor(transaction)) {
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
	
	
	/*public void handleFilePagarUpload(FileUploadEvent event) throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		// recupera local onde será gravado o arquivo
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
			//	String pathContrato = "C:/Users/Usuario/Desktop/"	
				+ this.selectedContratoLov.getNumeroContrato() + "//pagar/";

		// cria o diretório, caso não exista
		File diretorio = new File(pathContrato);
		if (!diretorio.isDirectory()) {
			diretorio.mkdir();
		}
		if(!SiscoatConstants.DEV && !CommonsUtil.sistemaWindows()) {
			if(event.getFile().getFileName().contains("Pag ")
					|| event.getFile().getFileName().contains("PAG ")) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				ResponsavelDao rDao = new ResponsavelDao();
				Responsavel rGerente = new Responsavel();
				rGerente = rDao.findById((long) 1175); //camilo
				takeBlipMB.sendWhatsAppMessageComprovante(rGerente,
						"comprovante_anexado", 
						getNomeUsuarioLogado(),
						this.selectedContratoLov.getNumeroContrato(),
						event.getFile().getFileName());
			}
		}

		if(event.getFile().getFileName().endsWith(".zip")) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: não é possível anexar .zip", " não é possível anexar .zip"));
		} else {
			// cria o arquivo
			byte[] conteudo = event.getFile().getContents();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(pathContrato + event.getFile().getFileName());
				fos.write(conteudo);
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}

			// atualiza lista de arquivos contidos no diretório
			filesPagar = listaArquivosPagar();
		}
	}
	
	public String generateFileID() {
		return CommonsUtil.stringValue(System.currentTimeMillis());
	}
	
	public Collection<FileUploaded> listaArquivosPagar() {
		if(CommonsUtil.semValor(this.selectedContratoLov)) {
			return null;
		}
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
	//			String pathContrato = "C:/Users/Usuario/Desktop/"
		+ this.selectedContratoLov.getNumeroContrato() + "//pagar/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				if(arquivo.isFile()) {
					lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
				}
				
			}
		}
		return lista;
	}*/
	
	/*public StreamedContent getDownloadFile() {
		if (this.selectedFile != null) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(this.selectedFile.getFile().getAbsolutePath());
				downloadFile = new DefaultStreamedContent(stream, this.selectedFile.getPath(),
						this.selectedFile.getFile().getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Cobrança - Download de Arquivos - Arquivo Não Encontrado");
			}
		}
		return this.downloadFile;
	}*/
	
	/*public void viewFilePagar(String fileName) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
			//String pathContrato = "C:/Users/Usuario/Desktop/"	
			+ this.selectedContratoLov.getNumeroContrato() + "//pagar/" + fileName;

			/*
			 * 'docx' =>
			 * 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
			 * 'xlsx' =>
			 * 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'word'
			 * => 'application/msword', 'xls' => 'application/excel', 'pdf' =>
			 * 'application/pdf' 'psd' => 'application/x-photoshop'
			 *
			String mineFile = "";

			if (fileName.contains(".jpg") || fileName.contains(".JPG")) {
				mineFile = "image-jpg";
			}

			if (fileName.contains(".jpeg") || fileName.contains(".jpeg")) {
				mineFile = "image-jpeg";
			}

			if (fileName.contains(".png") || fileName.contains(".PNG")) {
				mineFile = "image-png";
			}

			if (fileName.contains(".pdf") || fileName.contains(".PDF")) {
				mineFile = "application/pdf";
			}

			File arquivo = new File(pathContrato);

			input = new BufferedInputStream(new FileInputStream(arquivo), 10240);

			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);

			response.setContentLength((int) arquivo.length());

			response.setHeader("Content-disposition", "inline; filename=" + arquivo.getName());
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
	}*/
	
	public void deleteFile(List<FileUploaded> deleteFiles) {
		/*for (FileUploaded f : deleteFiles) {
			f.getFile().delete();
		}
		File here = new File(".");
		System.out.println(here.getAbsolutePath());
		deleteFiles = new ArrayList<FileUploaded>();
		filesPagar = listaArquivosPagar();
		listaArquivosContasPagar(contasPagarArquivos);*/
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
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
	public boolean isTemSelfie() {
		return temSelfie;
	}
	public void setTemSelfie(boolean temSelfie) {
		this.temSelfie = temSelfie;
	}
	
	
}
