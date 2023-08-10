package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.io.IOException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.common.CommonsUtil;

/** ManagedBean. */
@ManagedBean(name = "plexiMB")
@SessionScoped
public class PlexiMB {

	
	public UploadedFile uploadedFile;
	PlexiConsulta plexiConsulta = new PlexiConsulta();
	String cpfCnpj;
	
	public void pedirCertidoes() {
		PlexiDocumentosDao docsDao = new PlexiDocumentosDao();
		List<PlexiDocumentos> documentos = docsDao.findAll();
		PlexiService service = new PlexiService();
		for( PlexiDocumentos doc : documentos) {
			plexiConsulta = new PlexiConsulta();
			plexiConsulta.setCpfCnpj(cpfCnpj);
			plexiConsulta.setPlexiDocumentos(doc);
			service.PedirConsulta(plexiConsulta, null);
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	}
	
	public void popularDocumentos() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook((uploadedFile.getInputstream()));
		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);
		
		PlexiDocumentosDao plexiDocsDao = new PlexiDocumentosDao();
		while (!CommonsUtil.semValor(linha)) {
			
			linha = sheet.getRow(iLinha);
			if(CommonsUtil.semValor(linha) 
				||  CommonsUtil.semValor(linha.getCell(0)) 
				||  CommonsUtil.semValor(linha.getCell(0).getStringCellValue())
				||  CommonsUtil.semValor(linha.getCell(1)) 
				||  CommonsUtil.semValor(linha.getCell(1).getStringCellValue())
				||  CommonsUtil.semValor(linha.getCell(2)) 
				||  CommonsUtil.semValor(linha.getCell(2).getStringCellValue())
				||  CommonsUtil.semValor(linha.getCell(3)) 
				||  CommonsUtil.semValor(linha.getCell(3).getStringCellValue())
				) {
				break;
			}
			
			String url = (linha.getCell(0).getStringCellValue());
			String nome = (linha.getCell(1).getStringCellValue());
			String pfStr = (linha.getCell(2).getStringCellValue());
			String pjStr = (linha.getCell(3).getStringCellValue());
			String obs = "";
			if(!CommonsUtil.semValor(linha.getCell(4)) && !CommonsUtil.semValor(linha.getCell(4).getStringCellValue()) ) {
				obs = (linha.getCell(4).getStringCellValue());
			}
			
			
			PlexiDocumentos doc = new PlexiDocumentos();
			if(plexiDocsDao.findByFilter("url", url).size() > 0) {
				doc = plexiDocsDao.findByFilter("url", url).get(0);
			}
			doc.setUrl(url);
			doc.setNome(nome);
			doc.setObs(obs);
			
			if(CommonsUtil.mesmoValor(pfStr, "false")) {
				doc.setPf(false);
			} else if(CommonsUtil.mesmoValor(pfStr, "true")) {
				doc.setPf(true);
			}
			
			if(CommonsUtil.mesmoValor(pjStr, "false")) {
				doc.setPj(false);
			} else if(CommonsUtil.mesmoValor(pjStr, "true")) {
				doc.setPj(true);
			}
			
			if(doc.getId() > 0) {
				plexiDocsDao.merge(doc);
			} else {
				plexiDocsDao.create(doc);
			}
			
			iLinha++;
		}
	}
	
	public void clearDialog() {
		this.uploadedFile = null;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	
}
