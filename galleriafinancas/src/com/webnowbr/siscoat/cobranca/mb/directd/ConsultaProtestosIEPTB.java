package com.webnowbr.siscoat.cobranca.mb.directd;

import java.io.BufferedReader;
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

import com.webnowbr.siscoat.cobranca.db.model.directd.Telefone;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.cobranca.db.model.directd.PFPlus;
import com.webnowbr.siscoat.cobranca.db.model.directd.ParticipacoesEmpresa;
import com.webnowbr.siscoat.cobranca.db.model.directd.Protesto;
import com.webnowbr.siscoat.cobranca.db.model.directd.Protestos;
import com.webnowbr.siscoat.cobranca.db.model.directd.ProtestosIEPTB;
import com.webnowbr.siscoat.cobranca.db.model.directd.Relacionado;
import com.webnowbr.siscoat.cobranca.db.model.directd.Sociedade;
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
import com.webnowbr.siscoat.cobranca.db.model.directd.CartoriosProtesto;
import com.webnowbr.siscoat.cobranca.db.model.directd.Email;
import com.webnowbr.siscoat.cobranca.db.model.directd.Endereco;
import com.webnowbr.siscoat.cobranca.db.model.directd.PF;


@ManagedBean(name = "consultaProtestosIEPTB")
@SessionScoped
public class ConsultaProtestosIEPTB {

	/****
	 * 
	 * LIVE TOKEN
	 * 0d54a6b5fa28f6dc42ce76d0011952e3
	 */
	
	private String accessToken = "0d54a6b5fa28f6dc42ce76d0011952e3";

	private String cpf;
	private String cnpj;
	private String uf;
	
	private boolean consultaGerada;
	
	private ProtestosIEPTB retornoProtestosIEPTB;

	public ConsultaProtestosIEPTB() {

	}

	public String clearProtestosIEPTB() {
		this.cpf = "";
		this.cnpj = "";
		this.uf = "";
		
		this.consultaGerada = false;
		
		clearPDF();

		return "/Atendimento/ConsultasDirectd/ConsultaProtestosIEPTB.xhtml";
	}

	public void consultaProtestosIEPTB() {
		clearPDF();
		/*
		 *  https://apiv2.directd.com.br/consultas/cadastro/v1/consulta-pf-por-cpf?token={token}&cpf={CPF}
		 */

		FacesContext context = FacesContext.getCurrentInstance();

		this.retornoProtestosIEPTB = new ProtestosIEPTB();

		int HTTP_COD_SUCESSO = 200;

		boolean valid = true;
		
		//String cnpjNumeros = this.cnpj.replace(".", "").replace("/", "").replace("-", "");

		URL myURL;
		
		String URL = "https://apiv2.directd.com.br/consultas/protesto/v1/consulta-protesto-ieptb?token=" + this.accessToken ;
		
		if (!this.uf.equals("")) {
			URL = URL + "&estado=" + this.cpf;
		}
		
		if (!this.cpf.equals("")) {
			URL = URL + "&cpf=" + this.cpf;
		}
		
		if (!this.cnpj.equals("")) {
			URL = URL + "&cnpj=" + this.cnpj;
		} 
		
		try {
			myURL = new URL(URL);

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
							FacesMessage.SEVERITY_ERROR, "Consulta Protestos IEPTB: Erro ao realizar a consulta! (Código: " + myURLConnection.getResponseCode() + ")!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());				

					if (myResponse.getString("Tipo").equals("Sucesso")) {
						this.retornoProtestosIEPTB.getInfoServico().setConsultaUid(myResponse.getString("ConsultaUid"));
						this.retornoProtestosIEPTB.getInfoServico().setIdTipo(myResponse.getLong("IdTipo"));
						this.retornoProtestosIEPTB.getInfoServico().setTipo(myResponse.getString("Tipo"));
						this.retornoProtestosIEPTB.getInfoServico().setMensagem(myResponse.getString("Mensagem"));
						this.retornoProtestosIEPTB.getInfoServico().setTempoExecucaoMs(myResponse.getLong("TempoExecucaoMs"));
						this.retornoProtestosIEPTB.getInfoServico().setCustoTotalEmCreditos(myResponse.getLong("CustoTotalEmCreditos"));
						this.retornoProtestosIEPTB.getInfoServico().setSaldoEmCreditos(myResponse.getLong("SaldoEmCreditos"));
						this.retornoProtestosIEPTB.getInfoServico().setApiVersion(myResponse.getString("ApiVersion"));
	
						//JSONArray retorno = myResponse.getJSONObject("Retorno").getJSONArray("Retorno");
						
						if (!myResponse.isNull("Retorno")) {
							JSONObject retorno = myResponse.getJSONObject("Retorno");
		
							if (!retorno.isNull("ConstamProtestos")) {
								this.retornoProtestosIEPTB.setConstamProtestos(retorno.getBoolean("ConstamProtestos"));
							}
							
							if (!retorno.isNull("DocumentoConsultado")) {
								this.retornoProtestosIEPTB.setDocumentoConsultado(retorno.getString("DocumentoConsultado"));
							}
							
							if (!retorno.isNull("DataConsulta")) {
								this.retornoProtestosIEPTB.setDataConsulta(converteDate(retorno.getString("DataConsulta")));
							}
							
							if (!retorno.isNull("TotalNumProtestos")) {
								this.retornoProtestosIEPTB.setTotalNumProtestos(retorno.getInt("TotalNumProtestos"));
							}
							
							/***
							 * PROTESTOS
							 */
							JSONArray retornoProtestos = retorno.getJSONArray("Protestos");
							List<Protestos> protestos = new ArrayList<Protestos>();
							for (int i = 0; i < retornoProtestos.length(); i++) {
								JSONObject obj = retornoProtestos.getJSONObject(i);
								Protestos protestosObj = new Protestos();
								
								if (!obj.isNull("Estado")) {
									protestosObj.setEstado(obj.getString("Estado"));
								}	
								
								if (!obj.isNull("PesquisaEfetuadaEm")) {
									protestosObj.setPesquisaEfetuadaEm(converteDate(obj.getString("PesquisaEfetuadaEm")));
								}
								
								if (!obj.isNull("PesquisaRetroativaAte")) {
									protestosObj.setPesquisaRetroativaAte(obj.getString("PesquisaRetroativaAte"));
								}
								
								if (!obj.isNull("TotalNumProtestosUf")) {
									protestosObj.setTotalNumProtestosUf(obj.getInt("TotalNumProtestosUf"));
								}
								
								/***
								 * CARTORIOS PROTESTOS
								 */
								JSONArray retornoCartorioProtesto = obj.getJSONArray("CartoriosProtesto");
								List<CartoriosProtesto> cartoriosProtesto = new ArrayList<CartoriosProtesto>();
								
								for (int j = 0; j < retornoCartorioProtesto.length(); j++) {
									JSONObject objCP = retornoCartorioProtesto.getJSONObject(j);
									CartoriosProtesto cartoriosProtestoObj = new CartoriosProtesto();
									
									if (!objCP.isNull("Cidade")) {
										cartoriosProtestoObj.setCidade(objCP.getString("Cidade"));
									}
									
									if (!objCP.isNull("Nome")) {
										cartoriosProtestoObj.setNome(objCP.getString("Nome"));
									}
									
									if (!objCP.isNull("Endereco")) {
										cartoriosProtestoObj.setEndereco(objCP.getString("Endereco"));
									}
									
									if (!objCP.isNull("Observacao")) {
										cartoriosProtestoObj.setObservacao(objCP.getString("Observacao"));
									}
									
									if (!objCP.isNull("Telefone")) {
										cartoriosProtestoObj.setTelefone(objCP.getString("Telefone"));
									}
									
									if (!objCP.isNull("CodigoCartorio")) {
										cartoriosProtestoObj.setCodigoCartorio(objCP.getString("CodigoCartorio"));
									}
									
									if (!objCP.isNull("CodigoCidade")) {
										cartoriosProtestoObj.setCodigoCidade(objCP.getString("CodigoCidade"));
									}
									
									if (!objCP.isNull("DataAtualizacao")) {
										cartoriosProtestoObj.setDataAtualizacao(converteDate(objCP.getString("DataAtualizacao")));
									}
									
									if (!objCP.isNull("NumProtestos")) {
										cartoriosProtestoObj.setNumProtestos(objCP.getInt("NumProtestos"));
									}
									
									/***
									 * PROTESTO
									 */
									JSONArray retornoProtesto = objCP.getJSONArray("Protesto");
									List<Protesto> protesto = new ArrayList<Protesto>();
									
									for (int k = 0; k < retornoProtesto.length(); k++) {
										JSONObject objProtesto = retornoProtesto.getJSONObject(k);
										Protesto protestoObj = new Protesto();
										
										if (!objProtesto.isNull("DataProtesto")) {
											protestoObj.setDataProtesto(converteDate(objProtesto.getString("DataProtesto")));
										}
										
										if (!objProtesto.isNull("Valorprotestado")) {
											protestoObj.setValorprotestado(Float.valueOf(objProtesto.getString("Valorprotestado")));
										}
										
										protesto.add(protestoObj);
									}			
									
									cartoriosProtestoObj.setProtesto(protesto);
	
									cartoriosProtesto.add(cartoriosProtestoObj);
								}
								
								protestosObj.setCartoriosProtesto(cartoriosProtesto);
								
								protestos.add(protestosObj);
							}
							
							this.retornoProtestosIEPTB.setProtestos(protestos);						
						}
						
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Consulta Protestos IEPTB: Consulta efetuada com sucesso!", ""));
						
						this.consultaGerada = true;
						
					} else {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Consulta Protestos IEPTB: Erro ao realizar a consulta! (Erro: " + myResponse.getString("Tipo") + " | Mensagem: " + myResponse.getString("Mensagem") + ").", ""));
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

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public ProtestosIEPTB getRetornoProtestosIEPTB() {
		return retornoProtestosIEPTB;
	}

	public void setRetornoProtestosIEPTB(ProtestosIEPTB retornoProtestosIEPTB) {
		this.retornoProtestosIEPTB = retornoProtestosIEPTB;
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
			this.nomePDF = "Consulta - Protestos - " + this.retornoProtestosIEPTB.getDocumentoConsultado() + ".pdf";
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

			PdfPCell cell1 = new PdfPCell(new Phrase("Protestos", header));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoProtestosIEPTB.getInfoServico().getConsultaUid(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Data Consulta: ", titulo));
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
			
			if (this.retornoProtestosIEPTB.getDataConsulta() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoProtestosIEPTB.getDataConsulta()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Documento Consultado: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoProtestosIEPTB.getDocumentoConsultado(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Possui Protesto(s): ", titulo));
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
			
			if (this.retornoProtestosIEPTB.isConstamProtestos()) {
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
			
			cell1 = new PdfPCell(new Phrase("Total Protestos: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(this.retornoProtestosIEPTB.getTotalNumProtestos()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Protestos", header));
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

			if (this.retornoProtestosIEPTB.getProtestos().size() > 0) {
				for (Protestos protestos : this.retornoProtestosIEPTB.getProtestos()) {
					cell1 = new PdfPCell(new Phrase("Estado: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(protestos.getEstado(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("Pesquisa Efetuada em: ", titulo));
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
					
					if (protestos.getPesquisaEfetuadaEm() != null) {
						cell1 = new PdfPCell(new Phrase(sdfDataRel.format(protestos.getPesquisaEfetuadaEm()), normal));
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
					
					cell1 = new PdfPCell(new Phrase("Total Protestos no Estado: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(String.valueOf(protestos.getTotalNumProtestosUf()), normal));
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
					
					cell1 = new PdfPCell(new Phrase("", titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(15f);
					cell1.setColspan(2);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Cartórios Protesto", header));
					cell1.setBorder(0);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorLeft(BaseColor.DARK_GRAY);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorRight(BaseColor.DARK_GRAY);	
					cell1.setPaddingLeft(8f);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(15f);
					cell1.setColspan(4);
					table.addCell(cell1);

					if (protestos.getCartoriosProtesto().size() > 0) {
						for (CartoriosProtesto cartorio : protestos.getCartoriosProtesto()) {
							cell1 = new PdfPCell(new Phrase("Cidade: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getCidade(), normal));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getNome(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Endereço: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getEndereco(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Observação: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getObservacao(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Telefone: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getTelefone(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Código Cartório: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getCodigoCartorio(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Código Cidade: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(cartorio.getCodigoCidade(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Data Atualização: ", titulo));
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
							
							if (cartorio.getDataAtualizacao() != null) {
								cell1 = new PdfPCell(new Phrase(sdfDataRel.format(cartorio.getDataAtualizacao()), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Qtde. Protestos: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(String.valueOf(cartorio.getNumProtestos()), normal));
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
							
							cell1 = new PdfPCell(new Phrase("", titulo));
							cell1.setBorder(0);
							cell1.setPaddingLeft(8f);
							cell1.setBackgroundColor(BaseColor.WHITE);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell1.setUseBorderPadding(true);
							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(15f);
							cell1.setColspan(2);
							table.addCell(cell1);
							
							cell1 = new PdfPCell(new Phrase("Informações Protesto", header));
							cell1.setBorder(0);
							cell1.setBorderWidthBottom(1);
							cell1.setBorderColorLeft(BaseColor.DARK_GRAY);
							cell1.setBorderWidthTop(1);
							cell1.setBorderColorRight(BaseColor.DARK_GRAY);	
							cell1.setPaddingLeft(8f);
							cell1.setBackgroundColor(BaseColor.WHITE);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell1.setUseBorderPadding(true);
							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(15f);
							cell1.setColspan(4);
							table.addCell(cell1);
							
							if (protestos.getCartoriosProtesto().size() > 0) {
								for (Protesto protestoDetalhe : cartorio.getProtesto()) {
									cell1 = new PdfPCell(new Phrase("Data Atualização: ", titulo));
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
									
									if (protestoDetalhe.getDataProtesto() != null) {
										cell1 = new PdfPCell(new Phrase(sdfDataRel.format(protestoDetalhe.getDataProtesto()), normal));
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
									
									cell1 = new PdfPCell(new Phrase("Valor Protestado: ", titulo));
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
									
									if (protestoDetalhe.getValorprotestado() != null) {
										cell1 = new PdfPCell(new Phrase(df.format(protestoDetalhe.getValorprotestado()), normal));	
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
								}
							} else {
								cell1 = new PdfPCell(new Phrase("Nenhum detalhe encontrado!", titulo));
								cell1.setBorder(0);
								cell1.setPaddingLeft(8f);
								cell1.setBackgroundColor(BaseColor.WHITE);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
								cell1.setUseBorderPadding(true);
								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(15f);
								cell1.setColspan(4);
								table.addCell(cell1);
							}
						}						
					} else {
						cell1 = new PdfPCell(new Phrase("Nenhum cartório encontrado!", titulo));
						cell1.setBorder(0);
						cell1.setPaddingLeft(8f);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(5f);
						cell1.setPaddingBottom(15f);
						cell1.setColspan(4);
						table.addCell(cell1);
					}	
				}
			} else {
				cell1 = new PdfPCell(new Phrase("Nenhum protesto encontrado!", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(4);
				table.addCell(cell1);
			}

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Protestos: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Protestos: Ocorreu um problema ao gerar o PDF!" + e, ""));
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