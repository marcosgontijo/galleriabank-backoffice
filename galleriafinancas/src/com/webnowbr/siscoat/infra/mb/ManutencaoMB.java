package com.webnowbr.siscoat.infra.mb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.op.FilaInvestidoresDao;
import com.webnowbr.siscoat.cobranca.mb.InvestidorMB.FileUploaded;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

/** ManagedBean. */
@ManagedBean(name = "manutencaoMB")
@SessionScoped
public class ManutencaoMB {

	/**
	 * Construtor.
	 */
	public ManutencaoMB() {


	}
	
	/**** 
	 * LIMPA DIRETÓRIOS DE ARQUIVOS DO SERVIDOR
	 */
	public void limpaDiretorios() {		
		/***
		 * GALLERIA
		 */
		deleteFile("LOCACAO_PATH_CONTRATO");
		deleteFile("LOCACAO_PATH_COBRANCA");
		deleteFile("LOCACAO_PATH_BOLETO");
		deleteFile("COBRANCA_PATH_BOLETO");
		deleteFile("BOLETO_REMESSAS");
		deleteFile("RECIBOS_IUGU");
		deleteFile("ARQUIVOS_PDF");
		deleteFile("ARQUIVOS_PDF_DOWNLOAD");
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_ERROR, "Manutenção - Limpeza dos diretórios efetuada com Sucesso", ""));
	}
	
	/**
	 * deleta o arquivo selecionado na tela
	 */
	public void deleteFile(String diretorioLimpeza) {
		List<FileUploaded> arquivosDownload = (List<FileUploaded>) this.listaArquivos(diretorioLimpeza);
		
		for (FileUploaded f : arquivosDownload) {
			f.getFile().delete();
		}    	
	}
	
	/***
	 * Lista os arquivos contidos no diretório
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos(String diretorioLimpeza) {
		//DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao(); 
		String pathContrato = pDao.findByFilter("nome", diretorioLimpeza).get(0).getValorString() + "/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				//String nome = arquivo.getName();
				// String dt_ateracao = formatData.format(new Date(arquivo.lastModified()));
				lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
			}
		}
		return lista;
	}
	
	public class FileUploaded {
		private File file;
		private String name;
		private String path;

		public FileUploaded() {
		}

		public FileUploaded(String name, File file, String path) {
			this.name = name;
			this.file = file;
			this.path = path;
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

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}
	}
}
