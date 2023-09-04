package com.webnowbr.siscoat.cobranca.mb.directd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.db.model.directd.Cadin;
import com.webnowbr.siscoat.cobranca.db.model.directd.Pendencias;
import com.webnowbr.siscoat.cobranca.db.model.directd.Protestos;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


@ManagedBean(name = "consultaCadin")
@SessionScoped
public class ConsultaCadin {

	/****
	 * 
	 * LIVE TOKEN
	 * 0d54a6b5fa28f6dc42ce76d0011952e3
	 */
	
	private String accessToken = "0d54a6b5fa28f6dc42ce76d0011952e3";

	private String cnpj;
	private String cpf;
	private Cadin retornoConsultaCadin;
	
	private boolean consultaGerada;

	public ConsultaCadin() {

	}

	public String clearConsultaCadin() {
		this.cnpj = "";
		this.cpf = "";
		
		this.consultaGerada = false;
		
		clearPDF();

		return "/Atendimento/ConsultasDirectd/ConsultaCadin.xhtml";
	}

	public void consultaCadin() {
		/*
		 *  https://api.directd.com.br/consultas/trf/v1/consulta-certidao-trf-3?token={token}&cnpj={cnpj}
		 */

		clearPDF();
		FacesContext context = FacesContext.getCurrentInstance();

		this.retornoConsultaCadin = new Cadin();

		int HTTP_COD_SUCESSO = 200;

		boolean valid = true;
		
		//String cnpjNumeros = this.cnpj.replace(".", "").replace("/", "").replace("-", "");

		URL myURL;
		try {
			
			myURL = new URL("https://api.directd.com.br/consultas/fazenda/v1/consulta-cadin-inscrito-sp?token=" + this.accessToken + "&cpf=" + this.cpf + "&cnpj=" + this.cnpj);	

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Cadin: Erro ao realizar a consulta Cadin! (Código: " + myURLConnection.getResponseCode() + ")!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());				

					if (myResponse.getString("Tipo").equals("Sucesso")) {
						if (myResponse.getString("ConsultaUid") != null) {
							this.retornoConsultaCadin.getInfoServico().setConsultaUid(myResponse.getString("ConsultaUid"));
							this.retornoConsultaCadin.getInfoServico().setIdTipo(myResponse.getLong("IdTipo"));
							this.retornoConsultaCadin.getInfoServico().setTipo(myResponse.getString("Tipo"));
							this.retornoConsultaCadin.getInfoServico().setMensagem(myResponse.getString("Mensagem"));
							this.retornoConsultaCadin.getInfoServico().setTempoExecucaoMs(myResponse.getLong("TempoExecucaoMs"));
							this.retornoConsultaCadin.getInfoServico().setCustoTotalEmCreditos(myResponse.getLong("CustoTotalEmCreditos"));
							this.retornoConsultaCadin.getInfoServico().setSaldoEmCreditos(myResponse.getLong("SaldoEmCreditos"));
							this.retornoConsultaCadin.getInfoServico().setApiVersion(myResponse.getString("ApiVersion"));
		
							JSONObject retorno = myResponse.getJSONObject("Retorno");
		
							this.retornoConsultaCadin.setCodigoDeclaracao(retorno.getString("CodigoDeclaracao"));
							this.retornoConsultaCadin.setTemPendencia(retorno.getBoolean("TemPendencia"));
							this.retornoConsultaCadin.setData(converteDate(retorno.getString("Data")));
	
							JSONArray listaPendencias = retorno.getJSONArray("Pendencias");
							List<Pendencias> listListaPendencias= new ArrayList<Pendencias>();
	
							for (int j = 0; j < listaPendencias.length(); j++) {
								JSONObject obj = listaPendencias.getJSONObject(j);
								Pendencias pendenciaObj = new Pendencias();
								
								if (!obj.isNull("Entidade")) {
									pendenciaObj.setEntidade(obj.getString("Entidade"));
								}
								
								if (!obj.isNull("LocalRegularizacao")) {
									pendenciaObj.setLocalRegularizacao(obj.getString("LocalRegularizacao"));
								}
								
								if (!obj.isNull("QuantidadePendencias")) {
									pendenciaObj.setQuantidadePendencias(obj.getInt("QuantidadePendencias"));
								}
								
								if (!obj.isNull("DataInclusaoCadin")) {
									pendenciaObj.setDataInclusaoCadin(converteDate(obj.getString("DataInclusaoCadin")));
								}	
								
								listListaPendencias.add(pendenciaObj);
							}	
							
							this.retornoConsultaCadin.setPendencias(listListaPendencias);
		
							context.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_INFO, "Consulta Cadin: Consulta " + this.cnpj + " efetuada com sucesso!", ""));
							
							this.consultaGerada = true;
						} else {
							context.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "Consulta Cadin: Erro ao realizar a consulta!", ""));
						}
					} else {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Consulta Cadin: Erro ao realizar a consulta! (Erro: " + myResponse.getString("Tipo") + " | Mensagem: " + myResponse.getString("Mensagem") + ").", ""));
					}
				}

				myURLConnection.disconnect();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Date converteDate(String dateStr) {
		Date retorno = new Date();
		
		//2019-08-27T13:38:10.1420086-03:00
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateFormat formatterOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			String dateParsedStr = dateStr.substring(8, 10) + "/" + dateStr.substring(5, 7) + "/" + dateStr.substring(0, 4) + 
					" " + dateStr.substring(11, 19) ;
			
			try {
				retorno = ((java.util.Date)formatter.parse(dateParsedStr));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return retorno;
		} catch (StringIndexOutOfBoundsException e) {
			try {
				retorno = ((java.util.Date) formatterOnlyDate.parse(dateStr));
			} catch (ParseException e1) {
				retorno = gerarDataHoje();
				
				return retorno;
			}
			return retorno;
		}
	}
	
	/**
	 * GERA A DATA DE HOJE
	 * @return
	 */
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	/***
	 * 
	 * PARSE DO RETORNO SUCESSO
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Cadin getRetornoConsultaCadin() {
		return retornoConsultaCadin;
	}

	public void setRetornoConsultaCadin(Cadin retornoConsultaCadin) {
		this.retornoConsultaCadin = retornoConsultaCadin;
	}
	
	/****
	 * PDF
	 */
	private boolean pdfGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent file;
	
	public void clearPDF() {
		this.pdfGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
	}

	public void gerarPDF() {
		/*
		this.transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
		this.transferenciasObservacoesIUGU.setId(1);
		this.transferenciasObservacoesIUGU.setIdTransferencia("jdsfhdsfhjskfhjhslafdshf");
		this.transferenciasObservacoesIUGU.setObservacao("asdklfhjksdhfjd dsjfhjhdsfjashgdfj ");

		this.valorItem = new BigDecimal("30000.00");
		 */
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;

		try {
			/*
			 *  Fonts Utilizadas no PDF
			 */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao(); 

			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
			this.nomePDF = "Consulta - Cadin - " + this.retornoConsultaCadin.getCodigoDeclaracao() + ".pdf";
			

			// Associa a stream de saída ao 
			PdfWriter.getInstance(doc, baos);

			// Abre o documento
			doc.open();     			
			/*
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.2f, 0.8f, 0.2f, 0.8f});
			table.setWidthPercentage(100.0f); 

			PdfPCell cell1 = new PdfPCell(new Phrase("Consulta Cadin", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(4);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("ID Consulta: ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaCadin.getInfoServico().getConsultaUid(), normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Data Emissão: ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (this.retornoConsultaCadin.getData() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaCadin.getData()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("", normal));
			}
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Código da Declaração: ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaCadin.getCodigoDeclaracao(), normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Tem pendências? ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (this.retornoConsultaCadin.isTemPendencia()) {
				cell1 = new PdfPCell(new Phrase("Sim", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("Não", normal));
			}
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Lista de Pendências", header));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(4);
			table.addCell(cell1);
			
			for (Pendencias pendencias : this.retornoConsultaCadin.getPendencias()) {
				cell1 = new PdfPCell(new Phrase("Entidade: ", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(pendencias.getEntidade(), normal));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("Data Inclusão: ", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				if (pendencias.getDataInclusaoCadin() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(pendencias.getDataInclusaoCadin()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("", normal));
				}
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("Qtde. Pendências: ", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(String.valueOf(pendencias.getQuantidadePendencias()), normal));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("Local Regularização: ", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(pendencias.getLocalRegularizacao(), normal));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			doc.add(table);
			doc.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format(nomePDF, "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(baos.toByteArray()));
			gerador.close();

		}  catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Cadin: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.pdfGerado = true;

			
			
			}
		}
	

	public boolean isPdfGerado() {
		return pdfGerado;
	}

	public void setPdfGerado(boolean pdfGerado) {
		this.pdfGerado = pdfGerado;
	}

	public String getPathPDF() {
		return pathPDF;
	}

	public void setPathPDF(String pathPDF) {
		this.pathPDF = pathPDF;
	}

	public String getNomePDF() {
		return nomePDF;
	}

	public void setNomePDF(String nomePDF) {
		this.nomePDF = nomePDF;
	}

	public StreamedContent getFile() {
		String caminho =  this.pathPDF + this.nomePDF;        
		String arquivo = this.nomePDF;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		file = new DefaultStreamedContent(stream, caminho, arquivo); 

		return file;  
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public boolean isConsultaGerada() {
		return consultaGerada;
	}

	public void setConsultaGerada(boolean consultaGerada) {
		this.consultaGerada = consultaGerada;
	}
}