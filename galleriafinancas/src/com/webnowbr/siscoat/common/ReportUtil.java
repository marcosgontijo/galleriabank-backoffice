package com.webnowbr.siscoat.common;

import java.io.InputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class ReportUtil {

	// For PDF files:
	public static final String PDF_MIMETYPE = "application/pdf";

	// For BIFF .xls files:
	public static final String XLS_MIMETYPE = "application/vnd.ms-excel";

	// For Excel2007 and above .xlsx files:
	public static final String XLSX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	// For Word2007 and above .docx files:
	public static final String DOCX_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	/**
	 * Recebe nome do relatório e verificar se já está compilado (se contem arquivo
	 * .jasper), caso não esteja, compila. Ao final retorna objeto JasperReport
	 * 
	 * @param nomeRelatorio
	 * @return
	 * @throws BusinessException
	 */
	public JasperReport getRelatorio(String nomeRelatorio) throws JRException {
		JasperReport jasperReport = null;

		InputStream resourceAsStream = getClass().getResourceAsStream("/resource/" + nomeRelatorio + ".jasper");

		if (resourceAsStream == null) {
			resourceAsStream = getClass().getResourceAsStream("/resource/" + nomeRelatorio + ".jrxml");
			JasperDesign jasperDesign = JRXmlLoader.load(resourceAsStream);

			jasperReport = JasperCompileManager.compileReport(jasperDesign);
		} else {
			jasperReport = (JasperReport) JRLoader.loadObject(resourceAsStream);
		}

		return jasperReport;

	}

	public static final String getMimeTypeFromName(final String outputFileName) {
		if (outputFileName != null) {
			if (outputFileName.endsWith(".pdf")) {
				return PDF_MIMETYPE;
			} else if (outputFileName.endsWith(".txt")) {
				return "text/plain";
			} else if (outputFileName.endsWith(".REM")) {
				return "text/plain";
			} else if (outputFileName.endsWith(".RET")) {
				return "text/plain";
			} else if (outputFileName.endsWith(".xls")) {
				return XLS_MIMETYPE;
			} else if (outputFileName.endsWith(".xlsx")) {
				return XLSX_MIMETYPE;
			} else if (outputFileName.endsWith(".docx")) {
				return DOCX_MIMETYPE;
			}
		}
		return "application/pdf";
	}

}
