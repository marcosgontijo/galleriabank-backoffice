package com.webnowbr.siscoat.cobranca.mb;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.db.model.RegistroImovelTabela;
import com.webnowbr.siscoat.cobranca.db.op.RegistroImovelTabelaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

@ManagedBean(name = "registroImovelTabelaMB")
@SessionScoped

public class RegistroImovelTabelaMB {
	private List<RegistroImovelTabela> listRegistro;
	// private RegistroImovelTabela registroSelecionado;
	public UploadedFile uploadedFile;

	public RegistroImovelTabelaMB() {

	}

	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	}

	public String clearFieldsRegistros() {
		RegistroImovelTabelaDao registroImovelTabelaDao = new RegistroImovelTabelaDao();
		this.listRegistro = registroImovelTabelaDao.listarRegistros(gerarDataHoje());

		return "/Cadastros/Cobranca/RegistroImovelTabela.xhtml";
	}
	
	public void processarRegistros() throws IOException {
		readXLSXInserirRegistros();
		RegistroImovelTabelaDao registroImovelTabelaDao = new RegistroImovelTabelaDao();
		this.listRegistro = registroImovelTabelaDao.findAll();
	}

	public void readXLSXInserirRegistros() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook((uploadedFile.getInputstream()));
		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);
		Date data = CommonsUtil.dateValue(linha.getCell(0).getStringCellValue(), "dd/MM/yyyy");
		
		RegistroImovelTabelaDao registroImovelTabelaDao = new RegistroImovelTabelaDao();
		RegistroImovelTabela registro = new RegistroImovelTabela(data, BigDecimal.ZERO,  BigDecimal.ZERO,  BigDecimal.ZERO);
		registroImovelTabelaDao.create(registro);
		
		iLinha++;
		while (!CommonsUtil.semValor(linha)) {
			linha = sheet.getRow(iLinha);
			if(CommonsUtil.semValor(linha) 
				||  CommonsUtil.semValor(linha.getCell(1)) 
				||  CommonsUtil.semValor(linha.getCell(1).getNumericCellValue())) {
				break;
			}
			BigDecimal valorMin = CommonsUtil.bigDecimalValue(linha.getCell(1).getNumericCellValue());
			BigDecimal valorMax = CommonsUtil.bigDecimalValue(linha.getCell(2).getNumericCellValue());
			BigDecimal total = CommonsUtil.bigDecimalValue(linha.getCell(3).getNumericCellValue());

			registro = new RegistroImovelTabela(data, valorMin, valorMax, total);
			registroImovelTabelaDao.create(registro);

			iLinha++;
		}
	}

	public void excluirRegistro(RegistroImovelTabela registro) {
		RegistroImovelTabelaDao registroImovelTabelaDao = new RegistroImovelTabelaDao();
		registroImovelTabelaDao.delete(registro);
	}
	
	public void clearDialog() {
		this.uploadedFile = null;
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public List<RegistroImovelTabela> getListRegistro() {
		return listRegistro;
	}

	public void setListRegistro(List<RegistroImovelTabela> listRegistro) {
		this.listRegistro = listRegistro;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
}
