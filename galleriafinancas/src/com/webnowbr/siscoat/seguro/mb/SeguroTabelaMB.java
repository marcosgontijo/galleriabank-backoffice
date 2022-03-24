package com.webnowbr.siscoat.seguro.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.SeguradoDAO;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;




/** ManagedBean. */
@ManagedBean(name = "seguroTabelaMB")
@SessionScoped
public class SeguroTabelaMB {
	
	boolean seguroDFI;
	boolean seguroMIP;
	public String empresa;
	private Date dataDesagio = new Date();
	
	
	private List<SeguroTabelaVO> contratosSeguroDFI;
	private List<SeguroTabelaVO> contratosSeguroMIP;
	
	public String clearFields() {
		this.empresa = "Todas";
		this.seguroDFI = false;
		this.seguroMIP = false;
		this.dataDesagio = new Date();
		return "/Relatorios/Seguro/SeguradoTabela.xhtml";
	}
	
	public String carregaListagem() {
		this.contratosSeguroDFI = new ArrayList<SeguroTabelaVO>(0);
		this.contratosSeguroMIP = new ArrayList<SeguroTabelaVO>(0); 
			try {
				SeguradoDAO seguroDAO = new SeguradoDAO();
				this.contratosSeguroDFI = seguroDAO.listaSeguradosDFI(0, this.dataDesagio, this.empresa);				
				this.contratosSeguroMIP = seguroDAO.listaSeguradosMIP(0, this.dataDesagio, this.empresa);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
	}
	
	public StreamedContent readXLSXFileDFI() throws IOException {
		
		//String sheetName =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaDFI.xlsx"));
		
		XSSFSheet sheet = wb.getSheetAt(0);
		
		int iLinha = 1;
		for (int iSegurado = 0 ; iSegurado < this.contratosSeguroDFI.size();iSegurado++) {
			SeguroTabelaVO seguroTabelaVO = this.contratosSeguroDFI.get(iSegurado);
			
			XSSFRow linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			gravaCelula(0, seguroTabelaVO.getCodigoSegurado(), linha);
			gravaCelula(1, seguroTabelaVO.getNumeroContratoSeguro(), linha);
			gravaCelula(2, seguroTabelaVO.getParcelasOriginais(), linha);
			gravaCelula(3, seguroTabelaVO.getParcelasFaltantes(), linha);
			gravaCelula(4, seguroTabelaVO.getAvaliacao().doubleValue(), linha);
			gravaCelula(5, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpfPrincipal())), linha);
			gravaCelula(6, seguroTabelaVO.getNomePrincipal(), linha);
			gravaCelula(7, seguroTabelaVO.getPorcentagemPrincipal().doubleValue(), linha);
			
			if(!CommonsUtil.semValor(seguroTabelaVO.getCpf2())) {
				gravaCelula(8, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpf2())), linha);
				gravaCelula(9, seguroTabelaVO.getNome2(), linha);
				gravaCelula(10, seguroTabelaVO.getPorcentagem2().doubleValue(), linha);
			}
			
			if(!CommonsUtil.semValor(seguroTabelaVO.getCpf3())) {
				gravaCelula(11, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpf3())), linha);
				gravaCelula(12, seguroTabelaVO.getNome3(), linha);
				gravaCelula(13, seguroTabelaVO.getPorcentagem3().doubleValue(), linha);
			}
			
			if(!CommonsUtil.semValor(seguroTabelaVO.getCpf4())) {
				gravaCelula(14, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpf4())), linha);
				gravaCelula(15, seguroTabelaVO.getNome4(), linha);
				gravaCelula(16, seguroTabelaVO.getPorcentagem4().doubleValue(), linha);		
			}
			
			gravaCelula(17, seguroTabelaVO.getLogradouro(), linha);
			gravaCelula(18, seguroTabelaVO.getNumeroResidencia(), linha);
			gravaCelula(19, seguroTabelaVO.getComplemento(), linha);
			gravaCelula(20, seguroTabelaVO.getBairro(), linha);
			gravaCelula(21, seguroTabelaVO.getCidade(), linha);
			gravaCelula(22, seguroTabelaVO.getUf(), linha);
			gravaCelula(23, seguroTabelaVO.getCep(), linha);
			
			iLinha++;
		}
		
		//FileOutputStream fileOut = new FileOutputStream("c:\\TabelaSeguroDFI.xlsx");
		
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		
		gerador.open(String.format("Galleria Bank - SeguradoTabelaDFI %s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;
		
	}
	
	public String getYearRange() {		
		Integer anoAtual = DateUtil.getDataHoje().getYear() + 1900;
		Integer anoAnterior = anoAtual - 2;
		return  anoAnterior + ":" + anoAtual;
	}
	

	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}
	
	private void gravaCelula(Integer celula, Date value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	private void gravaCelula(Integer celula, Double value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}
	
	public StreamedContent readXLSXFileMIP() throws IOException {
		
		//String sheetName =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaMIP.xlsx"));
		
		XSSFSheet sheet = wb.getSheetAt(0);
		
		int iLinha = 1;
		for (int iSegurado = 0 ; iSegurado < this.contratosSeguroMIP.size();iSegurado++) {
			SeguroTabelaVO seguroTabelaVO = this.contratosSeguroMIP.get(iSegurado);
			
			XSSFRow linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			gravaCelula(0, seguroTabelaVO.getCodigoSegurado(), linha);
			gravaCelula(1, seguroTabelaVO.getNumeroContratoSeguro(), linha);
			gravaCelula(2, seguroTabelaVO.getParcelasOriginais(), linha);
			gravaCelula(3, seguroTabelaVO.getParcelasFaltantes(), linha);
			if(!CommonsUtil.semValor(seguroTabelaVO.getSaldoDevedor()))
				gravaCelula(4, seguroTabelaVO.getSaldoDevedor().doubleValue(), linha);
			gravaCelula(5, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpfPrincipal())), linha);
			gravaCelula(6, seguroTabelaVO.getNomePrincipal(), linha);
			gravaCelula(7, CommonsUtil.dateValue(seguroTabelaVO.getDataNascimento(), "yyyy-MM-dd"), linha);
			gravaCelula(8, (CommonsUtil.semValor(seguroTabelaVO.getSexo()))?"":seguroTabelaVO.getSexo().substring(0, 1), linha);
			gravaCelula(9, seguroTabelaVO.getPorcentagemPrincipal().doubleValue(), linha);
			
			if(!CommonsUtil.semValor(seguroTabelaVO.getCpf2())) {
				gravaCelula(10, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpf2())), linha);
				gravaCelula(11, seguroTabelaVO.getNome2(), linha);
				gravaCelula(12, CommonsUtil.dateValue(seguroTabelaVO.getDataNascimento2(), "yyyy-MM-dd"), linha);
				gravaCelula(13, (CommonsUtil.semValor(seguroTabelaVO.getSexo2()))?"":seguroTabelaVO.getSexo2().substring(0, 1), linha);
				gravaCelula(14, seguroTabelaVO.getPorcentagem2().doubleValue(), linha);
			}
			
			if(!CommonsUtil.semValor(seguroTabelaVO.getCpf3())) {
				gravaCelula(15, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpf3())), linha);
				gravaCelula(16, seguroTabelaVO.getNome3(), linha);
				gravaCelula(17, CommonsUtil.dateValue(seguroTabelaVO.getDataNascimento3(), "yyyy-MM-dd"), linha);
				gravaCelula(18, (CommonsUtil.semValor(seguroTabelaVO.getSexo3()))?"":seguroTabelaVO.getSexo3().substring(0, 1), linha);
				gravaCelula(19, seguroTabelaVO.getPorcentagem3().doubleValue(), linha);
			}
			
			if(!CommonsUtil.semValor(seguroTabelaVO.getCpf4())) {
				gravaCelula(20, CommonsUtil.doubleValue(CommonsUtil.somenteNumeros(seguroTabelaVO.getCpf4())), linha);
				gravaCelula(21, seguroTabelaVO.getNome4(), linha);
				gravaCelula(22, CommonsUtil.dateValue(seguroTabelaVO.getDataNascimento4(), "yyyy-MM-dd"), linha);
				gravaCelula(23, (CommonsUtil.semValor(seguroTabelaVO.getSexo4()))?"":seguroTabelaVO.getSexo4().substring(0, 1), linha);
				gravaCelula(24, seguroTabelaVO.getPorcentagem4().doubleValue(), linha);
			}
						
			gravaCelula(25, seguroTabelaVO.getLogradouro(), linha);
			gravaCelula(26, seguroTabelaVO.getNumeroResidencia(), linha);
			gravaCelula(27, seguroTabelaVO.getComplemento(), linha);
			gravaCelula(28, seguroTabelaVO.getBairro(), linha);
			gravaCelula(29, seguroTabelaVO.getCidade(), linha);
			gravaCelula(30, seguroTabelaVO.getUf(), linha);
			gravaCelula(31, seguroTabelaVO.getCep(), linha);
			
			iLinha++;
		}
		
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		
		gerador.open(String.format("Galleria Bank - SeguradoTabelaMIP %s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;
	}
	
	
	

	public List<SeguroTabelaVO> getContratosSeguroDFI() {
		return contratosSeguroDFI;
	}

	public void setContratosSeguroDFI(List<SeguroTabelaVO> contratosSeguroDFI) {
		this.contratosSeguroDFI = contratosSeguroDFI;
	}

	public List<SeguroTabelaVO> getContratosSeguroMIP() {
		return contratosSeguroMIP;
	}

	public void setContratosSeguroMIP(List<SeguroTabelaVO> contratosSeguroMIP) {
		this.contratosSeguroMIP = contratosSeguroMIP;
	}

	public boolean isSeguroDFI() {
		return seguroDFI;
	}

	public void setSeguroDFI(boolean seguroDFI) {
		this.seguroDFI = seguroDFI;
	}

	public boolean isSeguroMIP() {
		return seguroMIP;
	}

	public void setSeguroMIP(boolean seguroMIP) {
		this.seguroMIP = seguroMIP;
	}

	public Date getDataDesagio() {
		return dataDesagio;
	}

	public void setDataDesagio(Date dataDesagio) {
		this.dataDesagio = dataDesagio;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
	
	
	
	
}
