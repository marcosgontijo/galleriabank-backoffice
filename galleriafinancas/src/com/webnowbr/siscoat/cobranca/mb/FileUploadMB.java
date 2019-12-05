package com.webnowbr.siscoat.cobranca.mb;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
 
@ManagedBean(name = "fileUploadView")
@SessionScoped
public class FileUploadMB {
	
	// Lista os arquivos contidos no diretório
	Collection<FileUploaded> files = new ArrayList<FileUploaded>();
	// armazena arquivo selecionado a ser excluido
	FileUploaded selectedFile = new FileUploaded();
 
	// handler de upload do arquivo
    public void handleFileUpload(FileUploadEvent event) throws IOException {
    	UploadedFile file;    	
    	file = event.getFile();
    	
    	// recupera local onde será gravado o arquivo
    	ParametrosDao pDao = new ParametrosDao(); 
    	String pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString() + "contrato/";
    	
    	File diretorio = new File(pathContrato);
    	if (!diretorio.isDirectory()) {
    		diretorio.mkdir();
    	}
    	
		byte[] conteudo = event.getFile().getContents();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathContrato + event.getFile().getFileName());
			
			fos.write(conteudo);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		files = listaArquivos2();	
    		
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void deleteFile() {
    	selectedFile.getFile().delete();
    	
    	files = listaArquivos2();
    	
    	 FacesMessage message = new FacesMessage("Succesful", selectedFile.getFile().getName() + " is deleted.");
         FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public Collection<FileUploaded> listaArquivos2() {
        DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
        ParametrosDao pDao = new ParametrosDao(); 
    	String pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
        File diretorio = new File(pathContrato);
        File arqs[] = diretorio.listFiles();
        Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
        for (int i = 0; i < arqs.length; i++) {
            File arquivo = arqs[i];
            
            String nome = arquivo.getName();
            String dt_ateracao = formatData.format(new Date(arquivo.lastModified()));
            lista.add(new FileUploaded(arquivo.getName(), arquivo));
        }
        return lista;
    }   
    
	/**
	 * @return the files
	 */
	public Collection<FileUploaded> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(Collection<FileUploaded> files) {
		this.files = files;
	}

	/**
	 * @return the selectedFile
	 */
	public FileUploaded getSelectedFile() {
		return selectedFile;
	}

	/**
	 * @param selectedFile the selectedFile to set
	 */
	public void setSelectedFile(FileUploaded selectedFile) {
		this.selectedFile = selectedFile;
	}



	public class FileUploaded {
		private File file;
		private String name;
		
		public FileUploaded() {
		}
		
		public FileUploaded(String name, File file) {
			this.name = name;
			this.file = file;
		}
		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}
		/**
		 * @param file the file to set
		 */
		public void setFile(File file) {
			this.file = file;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		
	}
}