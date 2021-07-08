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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
import com.webnowbr.siscoat.cobranca.db.model.directd.AntCriminais;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


@ManagedBean(name = "consultaAntCriminais")
@SessionScoped
public class ConsultaAntCriminais {

	/****
	 * 
	 * LIVE TOKEN
	 * 0d54a6b5fa28f6dc42ce76d0011952e3
	 */

	private String accessToken = "0d54a6b5fa28f6dc42ce76d0011952e3";

	private String nome;
	private String cpf;
	private AntCriminais retornoConsultaAntCriminais;
	
	private boolean consultaGerada;

	public ConsultaAntCriminais() {

	}

	public String clearConsultaAntCriminais() {
		this.nome = "";
		this.cpf = "";
		
		this.consultaGerada = false; 
		
		clearPDF();

		return "/Atendimento/ConsultasDirectd/ConsultaAntCriminais.xhtml";
	}

	public void consultaAntCriminais() {
		/*
		 *  https://api.directd.com.br/consultas/PoliciaFederal/v1/consulta-pf-ant-criminais?token={tokenAcesso}&nome={nome}&cpf={cpf}
		 */
		clearPDF();
		
		FacesContext context = FacesContext.getCurrentInstance();

		this.retornoConsultaAntCriminais = new AntCriminais();

		int HTTP_COD_SUCESSO = 200;

		boolean valid = true;

		//String cnpjNumeros = this.cnpj.replace(".", "").replace("/", "").replace("-", "");

		URL myURL;

		String encodedQueryNome = encodeValue(this.nome);
		
		try {
			myURL = new URL("https://apiv2.directd.com.br/consultas/PoliciaFederal/v1/consulta-pf-ant-criminais?token=" + this.accessToken + 
					"&nome=" + encodedQueryNome + "&cpf=" + this.cpf);

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setConnectTimeout(100000);
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");			
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;
 
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					InputStream is = myURLConnection.getErrorStream();
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Antecedentes Criminais: Erro ao realizar a consulta! (Código: " + myURLConnection.getResponseCode() + ")!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());				

					if (myResponse.getString("Tipo").equals("Sucesso")) {
						this.retornoConsultaAntCriminais.getInfoServico().setConsultaUid(myResponse.getString("ConsultaUid"));
						this.retornoConsultaAntCriminais.getInfoServico().setIdTipo(myResponse.getLong("IdTipo"));
						this.retornoConsultaAntCriminais.getInfoServico().setTipo(myResponse.getString("Tipo"));
						this.retornoConsultaAntCriminais.getInfoServico().setMensagem(myResponse.getString("Mensagem"));
						this.retornoConsultaAntCriminais.getInfoServico().setTempoExecucaoMs(myResponse.getLong("TempoExecucaoMs"));
						this.retornoConsultaAntCriminais.getInfoServico().setCustoTotalEmCreditos(myResponse.getLong("CustoTotalEmCreditos"));
						this.retornoConsultaAntCriminais.getInfoServico().setSaldoEmCreditos(myResponse.getLong("SaldoEmCreditos"));
						this.retornoConsultaAntCriminais.getInfoServico().setApiVersion(myResponse.getString("ApiVersion"));

						//JSONArray retorno = myResponse.getJSONObject("Retorno").getJSONArray("Retorno");

						JSONObject retorno = myResponse.getJSONObject("Retorno");

						this.retornoConsultaAntCriminais.setNome(this.nome);
						this.retornoConsultaAntCriminais.setCPF(this.cpf);

						if (!retorno.isNull("PossuiAntecedCriminais")) {
							this.retornoConsultaAntCriminais.setPossuiAntecedCriminais(retorno.getBoolean("PossuiAntecedCriminais"));
						}

						if (!retorno.isNull("NumeroCertidao")) {
							this.retornoConsultaAntCriminais.setNumeroCertidao(String.valueOf(retorno.getLong("NumeroCertidao")));
						}

						if (!retorno.isNull("DataHoraEmissao")) {
							this.retornoConsultaAntCriminais.setDataHoraEmissao(converteDate(retorno.getString("DataHoraEmissao")));
						}

						if (!retorno.isNull("DataConsulta")) {
							this.retornoConsultaAntCriminais.setDataConsulta(converteDate(retorno.getString("DataConsulta")));
						}

						if (!retorno.isNull("Observacoes")) {
							this.retornoConsultaAntCriminais.setObservacoes(retorno.getString("Observacoes"));
						}

						if (!retorno.isNull("CertidaoAntecedentesPDF")) {
							this.retornoConsultaAntCriminais.setCertidaoAntecedentesPDF(retorno.getString("CertidaoAntecedentesPDF"));
						}

						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Consulta Antecedentes Criminais: Consulta efetuada com sucesso!", ""));
						
						this.consultaGerada = true; 
					} else {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Consulta Antecedentes Criminais: Erro ao realizar a consulta! (Erro: " + myResponse.getString("Tipo") + " | Mensagem: " + myResponse.getString("Mensagem") + ").", ""));
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
	
	// Method to encode a string value using `UTF-8` encoding scheme
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
	
	private byte[] unGZIP(byte[] compressedData) {
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        GZIPInputStream gzipIS = null;

        try {
            bis = new ByteArrayInputStream(compressedData);
            bos = new ByteArrayOutputStream();
            gzipIS = new GZIPInputStream(bis);

            byte[] buffer = new byte[1024];
            int len;
            while((len = gzipIS.read(buffer)) != -1){
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert gzipIS != null;
                gzipIS.close();
                bos.close();
                bis.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new byte[]{};
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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public AntCriminais getRetornoConsultaAntCriminais() {
		return retornoConsultaAntCriminais;
	}

	public void setRetornoConsultaAntCriminais(AntCriminais retornoConsultaAntCriminais) {
		this.retornoConsultaAntCriminais = retornoConsultaAntCriminais;
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
		OutputStream os = null;

		try {
			/*
			 *  Fonts Utilizadas no PDF
			 */
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
			this.nomePDF = "Consulta - Antecedentes Criminais - " + this.retornoConsultaAntCriminais.getNome() + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "CONSULTAS_DIRECTD").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);  	

			// Associa a stream de saída ao 
			PdfWriter.getInstance(doc, os);

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

			PdfPCell cell1 = new PdfPCell(new Phrase("Consulta Antecedentes Criminais", header));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaAntCriminais.getInfoServico().getConsultaUid(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Emissão: ", titulo));
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
			
			if (this.retornoConsultaAntCriminais.getDataHoraEmissao() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaAntCriminais.getDataHoraEmissao()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Consultado em: ", titulo));
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
			
			if (this.retornoConsultaAntCriminais.getDataConsulta() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaAntCriminais.getDataConsulta()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nome: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaAntCriminais.getNome(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CPF: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaAntCriminais.getCPF(), normal));
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

			cell1 = new PdfPCell(new Phrase("Número Certidão: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaAntCriminais.getNumeroCertidao(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Possui Antecedentes Criminais: ", titulo));
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
			
			if (this.retornoConsultaAntCriminais.isPossuiAntecedCriminais()) {
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
			
			cell1 = new PdfPCell(new Phrase("Observações: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaAntCriminais.getObservacoes(), normal));
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
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF PJ: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF PJ: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.pdfGerado = true;

			if (doc != null) {
				//fechamento do documento
				doc.close();
			}
			if (os != null) {
				//fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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