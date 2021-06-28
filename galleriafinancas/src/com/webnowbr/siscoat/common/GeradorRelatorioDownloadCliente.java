package com.webnowbr.siscoat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

@SuppressWarnings("deprecation")
public class GeradorRelatorioDownloadCliente {
	private final FacesContext facesContext;
	private String outputFileName = null;
	private String mimeType = null;

	public GeradorRelatorioDownloadCliente(final FacesContext facesContext) {
		super();
		this.facesContext = facesContext;
	}

	
	public void open(final String outputFileName) {
		
		this.outputFileName = outputFileName;
		this.mimeType = ReportUtil.getMimeTypeFromName(outputFileName);
	}

	
	public void feed(InputStream reportContents){
		streamReportToClient(facesContext, reportContents, mimeType,
				outputFileName, true);
	}

	
	public void feed(JasperPrint reportContents)  {
		streamReportToClient(facesContext, reportContents, mimeType,
				outputFileName, true);
	}

	
	public String feedHtml(JasperPrint reportContents) {
		
		//verificar se tem a possibilidade de fazer download de HTML
		streamReportToClient (facesContext, reportContents, mimeType,
				outputFileName, true);
		return null;
	}
	
	
	public void feed(byte[] reportContents)  {
		streamReportToClient(facesContext, reportContents, mimeType,
				outputFileName, true);
	}

	
	public void feed2Faces(InputStream reportContents) {
		 feed(reportContents);
	}
	
	
	public void close() {
	}

	
	public void feedPdf(JasperPrint reportContents)  {
		feed(reportContents);
	}
	
	@SuppressWarnings("deprecation")
	private static void streamReportToClient(FacesContext facesContext,
			JasperPrint reportContents, String contentType,
			String outputFileName, Boolean download) {
		ServletOutputStream out = null;
		try {
			HttpServletResponse response = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
			response.reset();
			response.setContentType(contentType);
			if (download){
			response.setHeader("Content-Disposition", "attachment;filename="
					+ outputFileName);
			}else{
				response.setHeader("Content-Disposition", "filename="
						+ outputFileName);
			}
			out = response.getOutputStream();

			if (outputFileName.endsWith("xls")) {
				JRXlsExporter exporter = new JRXlsExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT,
						reportContents);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				exporter.setParameter(
						JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
						Boolean.TRUE);
				exporter.setParameter(
						JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
						Boolean.FALSE);
				exporter.setParameter(
						JRXlsExporterParameter.IS_IGNORE_GRAPHICS,
						Boolean.FALSE);
				exporter.exportReport();
			} else {
				JasperExportManager
						.exportReportToPdfStream(reportContents, out);
			}

			out.flush();
			out.close();
			out = null;
		
			facesContext.responseComplete();

		} catch (JRException e) {
			// throw new BusinessException("GeradorRelatorioSalvaArquivo: "
			// + "erro ao gravar arquivo de saida do relatório!", e);			
		} catch (IOException e) {
			//LOGGER.error("streamReportToClient: IOException: ", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private static void streamReportToClient(FacesContext facesContext,
			InputStream reportContents, String contentType,
			String outputFileName, Boolean download) {
		ServletOutputStream out = null;
		try {

			HttpServletResponse response = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
			response.reset();
			response.setContentType(contentType);
			if (download){
			response.setHeader("Content-Disposition", "attachment;filename="
					+ outputFileName);
			}else{
				response.setHeader("Content-Disposition", "filename="
						+ outputFileName);
			}
			
			out = response.getOutputStream();
			copyToOutputStream(reportContents, out);
			out.flush();
			out.close();
			out = null;

			facesContext.responseComplete();

		} catch (IOException e) {
			//LOGGER.error("streamReportToClient: IOException ignorado ");
		} finally {
			if (out != null) {			
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
	
	private static void streamReportToClient(FacesContext facesContext,
			byte[] reportContents, String contentType, String outputFileName, Boolean download) {
		ServletOutputStream out = null;
		try {
			HttpServletResponse response = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
			response.reset();
			response.setContentType(contentType);

			if (download){
			response.setHeader("Content-Disposition", "attachment;filename="
					+ outputFileName);
			}else{
				response.setHeader("Content-Disposition", "filename="
						+ outputFileName);
			}
			
			
			out = response.getOutputStream();
			out.write(reportContents);
			out.flush();
			out.close();
			out = null;

			facesContext.responseComplete();

		} catch (IOException e) {
			//LOGGER.error("streamReportToClient: IOException: ", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
	
	private static final void copyToOutputStream(InputStream input,
			OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = input.read(buffer, 0, buffer.length)) > 0) {
			output.write(buffer, 0, len);
		}
	}
}
