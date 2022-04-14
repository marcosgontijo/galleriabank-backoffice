package com.webnowbr.siscoat.relatorioLaudoPaju;

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
@ManagedBean(name = "laudoPajuMB")
@SessionScoped
public class LaudoPajuMB {

	private List<LaudoPajuVO> listLaudoPaju;

	public String clearFields() {
		listLaudoPaju = new ArrayList<LaudoPajuVO>(0);
		return "/Relatorios/Pagamentos/LaudoPaju.xhtml";
	}

	public String carregaListagem() {
		this.listLaudoPaju = new ArrayList<LaudoPajuVO>(0);
		try {
			LaudoPajuDao laudoPajuDao = new LaudoPajuDao();
			this.listLaudoPaju = laudoPajuDao.listaLaudoPaju();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public StreamedContent readXLSXFile() throws IOException {

		// String sheetName
		// =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));

		XSSFSheet sheet = wb.getSheetAt(0);
		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);
		if (linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
		}

		gravaCelula(0, "Numero Contrato", linha);
		gravaCelula(1, "Cliente", linha);
		gravaCelula(2, "Valor Total", linha);
		gravaCelula(3, "Valor Pago", linha);
		gravaCelula(4, "Valor Restante", linha);

		iLinha = 1;
		for (int iLaPa = 0; iLaPa < this.listLaudoPaju.size(); iLaPa++) {
			LaudoPajuVO laudoPajuVO = this.listLaudoPaju.get(iLaPa);

			linha = sheet.getRow(iLinha);
			if (linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			gravaCelula(0, laudoPajuVO.getNumeroContrato(), linha);
			gravaCelula(1, laudoPajuVO.getNomePagador(), linha);
			gravaCelula(2, laudoPajuVO.getValorTotal().doubleValue(), linha);
			gravaCelula(3, laudoPajuVO.getValorPago().doubleValue(), linha);
			gravaCelula(4, laudoPajuVO.getValorRestante().doubleValue(), linha);

			iLinha++;
		}

		// FileOutputStream fileOut = new FileOutputStream("c:\\TabelaSeguroDFI.xlsx");

		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
		// escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		// fecha a escrita de dados nessa planilha
		wb.close();

		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		gerador.open(String.format("Galleria Bank - Laudo Paju %s.xlsx", ""));
		gerador.feed(new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;

	}

	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	private void gravaCelula(Integer celula, Double value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	public List<LaudoPajuVO> getListLaudoPaju() {
		return listLaudoPaju;
	}

	public void setListLaudoPaju(List<LaudoPajuVO> listLaudoPaju) {
		this.listLaudoPaju = listLaudoPaju;
	}
}
