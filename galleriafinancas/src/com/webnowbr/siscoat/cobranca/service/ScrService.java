package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONObject;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoCliente;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoClienteTraduzido;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.cobranca.vo.FileGenerator;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

public class ScrService {

	SimpleDateFormat sdfDataArquivo = null;
	
	TimeZone zone;
	Locale locale;
	
	public ScrService() {
		super();
		zone = TimeZone.getDefault();
		locale = new Locale("pt", "BR");
		this.sdfDataArquivo = new SimpleDateFormat("dd-MMM-yyyy", locale);

	}

	public void requestScr(DocumentoAnalise documentoAnalise) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoScr())) {
			ScrResult scrResult = consultaSCR(documentoAnalise.getCnpjcpf(), null);			
			if (!CommonsUtil.semValor(scrResult)) {
			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			documentoAnalise.setRetornoScr(GsonUtil.toJson(scrResult));
			documentoAnaliseDao.merge(documentoAnalise);
			
			DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
			documentoAnaliseService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
					DocumentosAnaliseEnum.SCR, documentoAnalise.getRetornoScr());
			}
			
		}
	}
	
	/**
	 * EFETUA TRANSFERENCIA ENTRE CONTAS
	 */
	public ScrResult consultaSCR(String documento, FacesContext context) {
		try {		
			
			System.out.println(" INICIO DO PROCESSO ");
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://bmpdigital.moneyp.com.br/api/BMPDigital/ConsultaSCR");

			String dados = composeJSONPayload(documento);			

			JSONObject jsonObj = new JSONObject(dados);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				System.out.println("Erro ao Consultar o SCR BMP Digital - " + myURLConnection.getResponseCode());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Erro ao Consultar o SCR BMP Digital - " + myURLConnection.getResponseCode(),
						""));
			} else {				
				
				String stringResponse = getJsonString(myURLConnection.getInputStream());
				
				ScrResult scResult = GsonUtil.fromJson(stringResponse, ScrResult.class);
				
				
				if (!scResult.isErro()) {
					System.out.println("SUCESSO NA GERAÇÃO DO SCR" );
					
					DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
					
					PagadorRecebedorService  pagadorRecebedorService = new PagadorRecebedorService();
					PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.findByCpfCnpj(documento);
							
					documentoAnaliseService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
							DocumentosAnaliseEnum.SCR, stringResponse);
					
					
					return scResult;
								
				} else {
					// TODO Auto-generated catch block
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SCR: Ocorreu um problema ao consultar o SCR! (Mensagem: " + scResult.getMensagemOperador()+ ")",
							""));
				}				
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	@SuppressWarnings("deprecation")
	public String composeJSONPayload(String documento) {
		String json = "";
		
		Date dataHoje = DateUtil.getDataHoje();
		//Date dataReferencia = DateUtil.adicionarMes(dataHoje, -2);
		Date dataReferencia = DateUtil.adicionarDias(dataHoje, -60);
		
		int mesReferenciaInt = dataReferencia.getMonth();
		mesReferenciaInt ++;
		String mesReferencia = CommonsUtil.stringValue(mesReferenciaInt);
		
		int anoReferenciaInt = dataReferencia.getYear();
		anoReferenciaInt += 1900;
		String anoReferencia = CommonsUtil.stringValue(anoReferenciaInt);
		
		//calendar.get(Calendar.MONTH) retorna 1 mês antes porque começa em 0
		// Subtraimos 1 porque a consulta é de 45 dias pra tras apenas

		json = "{\"auth\":{\"Usuario\":\"JOAO@GALLERIAFINANCAS.COM.BR\",\"Senha\":\"Scr!2021\",\"CodigoParametro\":\"GALLERIA_SCR\",\"Chave\":\"eb11110f-9f0e-4a16-83d7-6229c949da4a\"}, " +
			    "\"consulta\":{\"Documento\":\"" + documento + "\",\"DataBaseMes\":\"" + mesReferencia + "\",\"DataBaseAno\":\"" + anoReferencia + "\"}}";
		
		return json;
	}
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO 
	 * 
	 * @param inputStream
	 * @return
	 */
	public String getJsonString(InputStream inputStream) {
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

			return response.toString();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public  void imprimeContrato(ResumoDoCliente resumoDoCliente, ResumoDoClienteTraduzido resumoDoClienteTraduzido, FileGenerator fileGenerator, FacesContext context) {

		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */
		
		ByteArrayOutputStream baos = null;
		
		try {

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);

			/*
			 * DAOs
			 */
			ParametrosDao pDao = new ParametrosDao();

			String documentoSemCaracters = fileGenerator.getDocumento().replace(".", "").replace("-", "").replace("/",
					"");

			fileGenerator.setPath(pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString());
			fileGenerator.setName(sdfDataArquivo.format(date.getTime()) + " SCR " + documentoSemCaracters + ".pdf");

			// os = new FileOutputStream(fileGenerator.getPath() + fileGenerator.getName());

			baos = geraContrato(resumoDoCliente, resumoDoClienteTraduzido, fileGenerator,
					context);
			// Associa a stream de saída ao
			FileOutputStream fos;

			fos = new FileOutputStream(fileGenerator.getPath() + fileGenerator.getName());
			fos.write(baos.toByteArray());
			fos.close();
			
			fileGenerator.setPdfGerado(true);

		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SCR: Ocorreu um problema ao gerar o PDF! " + e,
							""));
		} 
		
		finally {

			if (baos != null) {
				// fechamento da stream de saída
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"SCR: PDF gerado com sucesso!",
							""));
		}
	}
	

	public byte[] geraContrato(ScrResult scrResult, FileGenerator fileGenerator) {
		ByteArrayOutputStream baus = geraContrato(scrResult.getResumoDoCliente(),
				scrResult.getResumoDoClienteTraduzido(), fileGenerator, null);
		if (!CommonsUtil.semValor(baus))
			return baus.toByteArray();
		else
			return null;
	}
	
	@SuppressWarnings("finally")
	public ByteArrayOutputStream geraContrato(ResumoDoCliente resumoDoCliente,
			ResumoDoClienteTraduzido resumoDoClienteTraduzido, FileGenerator fileGenerator, FacesContext context) {

		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */
		
		Document doc = null;
		OutputStream os = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			
			

			/*
			 * Formatadores de Data/Hora
			 */
//			SimpleDateFormat sdfDataContrato = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);
			SimpleDateFormat sdfDataFormatada = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataFormatadaMesAno = new SimpleDateFormat("MMM/yyyy", locale);
			
			
			DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

			
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			PdfWriter.getInstance(doc, baos); 
			doc.open();
		    
	    
			PdfPTable table = new PdfPTable(new float[] {0.16f, 0.16f, 0.16f});
			table.setWidthPercentage(100.0f); 
			
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd"); 
			SimpleDateFormat formatoMesAno = new SimpleDateFormat("yyyy-MM"); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Consulta SCR - Data Base Consultada (" + sdfDataFormatadaMesAno.format(formatoMesAno.parse(resumoDoCliente.getDataBaseConsultada())) + ")" , header));
			
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Documento Síntese", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Documento", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);			
			
			cell1 = new PdfPCell(new Phrase(fileGenerator.getDocumento(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Dt. Inicio Relacionamento", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);			
			
			if (resumoDoClienteTraduzido.getDtInicioRelacionamento() != null && !resumoDoClienteTraduzido.getDtInicioRelacionamento().equals("")) { 
				cell1 = new PdfPCell(new Phrase(sdfDataFormatada.format(formato.parse(resumoDoClienteTraduzido.getDtInicioRelacionamento())), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Instituições", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeInstituicoes()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Operações", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeOperacoes()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Operações Discordância", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeOperacoesDiscordancia()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Vlr. Operações Discordância", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			if (resumoDoClienteTraduzido.getVlrOperacoesDiscordancia() != null && (resumoDoClienteTraduzido.getVlrOperacoesDiscordancia().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getVlrOperacoesDiscordancia()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Operações Sob Judice", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeOperacoesSobJudice()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Vlr. Operações Sob Judice", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
						
			if (resumoDoClienteTraduzido.getVlrOperacoesSobJudice() != null && (resumoDoClienteTraduzido.getVlrOperacoesSobJudice().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getVlrOperacoesSobJudice()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Fluxo", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(15f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Ativa (A)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("A Vencer", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setPaddingLeft(25f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer() != null && (resumoDoClienteTraduzido.getCarteiraVencer().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer() != null && (resumoDoClienteTraduzido.getCarteiraVencer().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer Até 30 dias e Vencidos Até 14 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias().compareTo(BigDecimal.ZERO) > 0)) { 				
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 31 a 60 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer31a60dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer31a60dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 61 a 90 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer61a90dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer61a90dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 91 a 180 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer91a180dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer91a180dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 181 a 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer181a360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer181a360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}			
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Carteira a Vencer Acima 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencerAcima360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencerAcima360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer Prazo Indeterminado", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado() != null && (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado() != null && (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Vencido", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setPaddingLeft(25f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido() != null && (resumoDoClienteTraduzido.getCarteiraVencido().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido() != null && (resumoDoClienteTraduzido.getCarteiraVencido().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 15 a 30 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido15a30dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido15a30dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido15a30dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido15a30dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido15a30dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido15a30dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 31 a 60 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido31a60dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido31a60dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 61 a 90 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido61a90dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido61a90dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Carteira Vencido 91 a 180 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido91a180dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido91a180dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 181 a 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido181a360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido181a360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido Acima 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Prejuízo (B)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prejuízo", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizo() != null && (resumoDoClienteTraduzido.getPrejuizo().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getPrejuizo()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizo() != null && (resumoDoClienteTraduzido.getPrejuizo().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getPrejuizo())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prejuizo Até 12 meses", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAte12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAte12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getPrejuizoAte12meses()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAte12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAte12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getPrejuizoAte12meses())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}			
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prejuízo Acima 12 meses", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAcima12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAcima12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getPrejuizoAcima12meses()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAcima12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAcima12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getPrejuizoAcima12meses())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Carteira de Crédito Tomado (A+B=C)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira de Crédito Tomado", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiradeCredito() != null && (resumoDoClienteTraduzido.getCarteiradeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiradeCredito()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiradeCredito() != null && (resumoDoClienteTraduzido.getCarteiradeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiradeCredito())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}			
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Repasses interfinanceiros (D)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Repasses", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRepasses() != null && (resumoDoClienteTraduzido.getRepasses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getRepasses()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRepasses() != null && (resumoDoClienteTraduzido.getRepasses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getRepasses())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Coobrigações (E)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Coobrigações", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCoobrigacoes() != null && (resumoDoClienteTraduzido.getCoobrigacoes().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCoobrigacoes()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCoobrigacoes() != null && (resumoDoClienteTraduzido.getCoobrigacoes().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCoobrigacoes())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Responsabilidade Total (C+D+E=F)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Responsabilidade Total", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getResponsabilidadeTotal() != null && (resumoDoClienteTraduzido.getResponsabilidadeTotal().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getResponsabilidadeTotal()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getResponsabilidadeTotal() != null && (resumoDoClienteTraduzido.getResponsabilidadeTotal().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getResponsabilidadeTotal())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Créditos a liberar (G)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Creditos a Liberar", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCreditosaLiberar() != null && (resumoDoClienteTraduzido.getCreditosaLiberar().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCreditosaLiberar()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCreditosaLiberar() != null && (resumoDoClienteTraduzido.getCreditosaLiberar().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCreditosaLiberar())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Disponível (H)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Disponível", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCredito() != null && (resumoDoClienteTraduzido.getLimitesdeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getLimitesdeCredito()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCredito() != null && (resumoDoClienteTraduzido.getLimitesdeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getLimitesdeCredito())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Até 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Acima 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Indireto (I)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Indireto Vendor", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRiscoIndiretoVendor() != null && (resumoDoClienteTraduzido.getRiscoIndiretoVendor().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getRiscoIndiretoVendor()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRiscoIndiretoVendor() != null && (resumoDoClienteTraduzido.getRiscoIndiretoVendor().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(CommonsUtil.calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getRiscoIndiretoVendor())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Total (F+G+H+I=J)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Total", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRiscoTotal() != null && (resumoDoClienteTraduzido.getRiscoTotal().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getRiscoTotal()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("100 %", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			doc.add(table);
			
			fileGenerator.setPdfGerado(true);
			
		} catch (Exception e) {
			if (context != null)
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SCR: Ocorreu um problema ao gerar o PDF! " + e,
							""));
		} finally {
			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

//			ByteArrayOutputStream baos = new ByteArrayOutputStream();

//			PdfReader writer;
//			try {
//				writer = PdfReader.getInstance(doc, os);
//
//				PdfContentByte contentByte = writer.getDirectContent();
//
////			try {
//				baos.writeTo(os);
//			} catch (DocumentException e) {
//				throw new RuntimeException("An error occurred while creating a PdfWriter object.", e);
//			} catch (IOException e) {
//				if (context != null)
//					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//							"SCR: Ocorreu um problema ao gerar o PDF! " + e, ""));
//				return null;
//			}
			
			return baos;
		}
	
	}
	
}

