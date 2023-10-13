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
import com.webnowbr.siscoat.cobranca.db.model.directd.Email;
import com.webnowbr.siscoat.cobranca.db.model.directd.Endereco;
import com.webnowbr.siscoat.cobranca.db.model.directd.PJ;
import com.webnowbr.siscoat.cobranca.db.model.directd.ParticipacoesEmpresa;
import com.webnowbr.siscoat.cobranca.db.model.directd.Sociedade;
import com.webnowbr.siscoat.cobranca.db.model.directd.Telefone;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


@ManagedBean(name = "consultaPJ")
@SessionScoped
public class ConsultaPJ {

	/****
	 * 
	 * LIVE TOKEN
	 * 0d54a6b5fa28f6dc42ce76d0011952e3
	 */
	
	private String accessToken = "0d54a6b5fa28f6dc42ce76d0011952e3";

	private String cnpj;
	private PJ retornoConsultaPJ;
	
	private boolean consultaGerada;

	public ConsultaPJ() {

	}

	public String clearConsultaPJ() {
		this.cnpj = "";
		this.consultaGerada = false;

		clearPDF();
		return "/Atendimento/ConsultasDirectd/ConsultaPJ.xhtml";
	}

	public void consultaPJ() {
		clearPDF();
		/*
		 *  https://apiv2.directd.com.br/consultas/cadastro/v1/consulta-pj-por-cnpj?token={token}&cnpj={CNPJ}
		 */

		FacesContext context = FacesContext.getCurrentInstance();

		this.retornoConsultaPJ = new PJ();

		int HTTP_COD_SUCESSO = 200;

		boolean valid = true;
		
		//String cnpjNumeros = this.cnpj.replace(".", "").replace("/", "").replace("-", "");

		URL myURL;
		try {
			myURL = new URL("https://apiv2.directd.com.br/consultas/cadastro/v1/consulta-pj-por-cnpj?token=" + this.accessToken + "&cnpj=" + this.cnpj);

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
							FacesMessage.SEVERITY_ERROR, "Consulta CNPJ: Erro ao realizar a consulta CNPJ! (Código: " + myURLConnection.getResponseCode() + ")!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());				

					if (myResponse.getString("Tipo").equals("Sucesso")) {
						this.retornoConsultaPJ.getInfoServico().setConsultaUid(myResponse.getString("ConsultaUid"));
						this.retornoConsultaPJ.getInfoServico().setIdTipo(myResponse.getLong("IdTipo"));
						this.retornoConsultaPJ.getInfoServico().setTipo(myResponse.getString("Tipo"));
						this.retornoConsultaPJ.getInfoServico().setMensagem(myResponse.getString("Mensagem"));
						this.retornoConsultaPJ.getInfoServico().setTempoExecucaoMs(myResponse.getLong("TempoExecucaoMs"));
						this.retornoConsultaPJ.getInfoServico().setCustoTotalEmCreditos(myResponse.getLong("CustoTotalEmCreditos"));
						this.retornoConsultaPJ.getInfoServico().setSaldoEmCreditos(myResponse.getLong("SaldoEmCreditos"));
						this.retornoConsultaPJ.getInfoServico().setApiVersion(myResponse.getString("ApiVersion"));
	
						//JSONArray retorno = myResponse.getJSONObject("Retorno").getJSONArray("Retorno");
						
						JSONObject retorno = myResponse.getJSONObject("Retorno");
	
						// TODO CONVERTER PARA DATA EMITIDA E VALIDA
						this.retornoConsultaPJ.setCnpj(String.valueOf(retorno.getLong("CNPJ")));
						this.retornoConsultaPJ.setRazaoSocial(retorno.getString("RazaoSocial"));
						this.retornoConsultaPJ.setDataFundacao(converteDate(retorno.getString("DataFundacao")));
						this.retornoConsultaPJ.setCnaeCodigo(String.valueOf(retorno.getLong("CNAECodigo")));
						this.retornoConsultaPJ.setCnaeDescricao(retorno.getString("CNAEDescricao"));
						this.retornoConsultaPJ.setNatJurCodigo(String.valueOf(retorno.getLong("NatJurCodigo")));
						this.retornoConsultaPJ.setNatJurDescricao(retorno.getString("NatJurDescricao"));
						this.retornoConsultaPJ.setPorte(retorno.getString("Porte"));
						this.retornoConsultaPJ.setMatriz(retorno.getString("Matriz"));
						this.retornoConsultaPJ.setDataConsulta(DateUtil.gerarDataHoje());
						
						/**
						 * POSSIVEIS VALORES NULOS
						 */				
						if (!retorno.isNull("NomeFantasia")) {
							this.retornoConsultaPJ.setNomeFantasia(retorno.getString("NomeFantasia"));
						}
						if (!retorno.isNull("CNAECodigoSecundario")) {
							this.retornoConsultaPJ.setCnaeCodigoSecundario(String.valueOf(retorno.getLong("CNAECodigoSecundario")));
						}
						if (!retorno.isNull("CNAEDescricaoSecundario")) {
							this.retornoConsultaPJ.setCnaeDescricaoSecundario(retorno.getString("CNAEDescricaoSecundario"));
						}
						if (!retorno.isNull("QtdFuncionarios")) {
							this.retornoConsultaPJ.setQtdFuncionarios(String.valueOf(retorno.getInt("QtdFuncionarios")));
						}
						if (!retorno.isNull("SituacaoCadastral")) {
							this.retornoConsultaPJ.setSituacaoCadastral(retorno.getString("SituacaoCadastral"));
						}
						if (!retorno.isNull("NatJurTipo")) {
							this.retornoConsultaPJ.setNatJurTipo(retorno.getString("NatJurTipo"));
						}
						if (!retorno.isNull("FaixaFuncionarios")) {
							this.retornoConsultaPJ.setFaixaFuncionarios(retorno.getString("FaixaFuncionarios"));
						}
						if (!retorno.isNull("FaixaFaturamento")) {
							this.retornoConsultaPJ.setFaixaFaturamento(retorno.getString("FaixaFaturamento"));
						}
						if (!retorno.isNull("OrgaoPublico")) {
							this.retornoConsultaPJ.setOrgaoPublico(retorno.getString("OrgaoPublico"));
						}
						if (!retorno.isNull("Ramo")) {
							this.retornoConsultaPJ.setRamo(retorno.getString("Ramo"));
						}
						if (!retorno.isNull("TipoEmpresa")) {
							this.retornoConsultaPJ.setTipoEmpresa(retorno.getString("TipoEmpresa"));
						}
						if (!retorno.isNull("UltimaAtualizacao")) {
							this.retornoConsultaPJ.setUltimaAtualizacao(converteDate(retorno.getString("UltimaAtualizacao")));
						}

						/***
						 * TELEFONES
						 */
						JSONArray retornoTelefones = retorno.getJSONArray("Telefones");
						List<Telefone> listaTelefones = new ArrayList<Telefone>();

						for (int i = 0; i < retornoTelefones.length(); i++) {
							JSONObject obj = retornoTelefones.getJSONObject(i);
							
							Telefone telefone = new Telefone();
							telefone.setTelefoneComDDD(obj.getString("TelefoneComDDD"));
							
							if (!obj.isNull("TelemarketingBloqueado")) {
								telefone.setTelemarketingBloqueado(obj.getBoolean("TelemarketingBloqueado"));
							}
							if (!obj.isNull("TelemarketingUltBloqDesb")) {
								telefone.setTelemarketingUltBloqDesb(obj.getBoolean("TelemarketingUltBloqDesb"));
							}
							if (!obj.isNull("Operadora")) {
								telefone.setOperadora(obj.getString("Operadora"));
							}
							if (!obj.isNull("UltimaAtualizacao")) {
								telefone.setUltimaAtualizacao(converteDate(obj.getString("UltimaAtualizacao")));
							}
						
							listaTelefones.add(telefone);
						}		
						
						this.retornoConsultaPJ.setTelefones(listaTelefones);
						
						/***
						 * Enderecos
						 */
						JSONArray retornoEnderecos = retorno.getJSONArray("Enderecos");
						List<Endereco> listaEnderecos = new ArrayList<Endereco>();

						for (int i = 0; i < retornoEnderecos.length(); i++) {
							JSONObject obj = retornoEnderecos.getJSONObject(i);
							
							Endereco endereco = new Endereco();
							endereco.setLogradouro(obj.getString("Logradouro"));
							endereco.setNumero(obj.getString("Numero"));
							endereco.setComplemento(obj.getString("Complemento"));
							endereco.setBairro(obj.getString("Bairro"));
							endereco.setCidade(obj.getString("Cidade"));
							endereco.setUf(obj.getString("UF"));
							endereco.setCep(obj.getString("CEP"));
							
							if (!obj.isNull("UltimaAtualizacao")) {
								endereco.setUltimaAtualizacao(converteDate(obj.getString("UltimaAtualizacao")));
							}
							
							listaEnderecos.add(endereco);
						}		
						
						this.retornoConsultaPJ.setEnderecos(listaEnderecos);
						
						/***
						 * Emails
						 */
						JSONArray retornoEmails = retorno.getJSONArray("Emails");
						List<Email> listaEmails = new ArrayList<Email>();

						for (int i = 0; i < retornoEmails.length(); i++) {
							JSONObject obj = retornoEmails.getJSONObject(i);
							
							Email email = new Email();
							email.setEnderecoEmail(obj.getString("EnderecoEmail"));
							
							if (!obj.isNull("UltimaAtualizacao")) {
								email.setUltimaAtualizacao(converteDate(obj.getString("UltimaAtualizacao")));
							}
							
							listaEmails.add(email);
						}		
						
						this.retornoConsultaPJ.setEmails(listaEmails);
						
						/***
						 * Socios
						 */
						JSONArray retornoSociedades = retorno.getJSONArray("Socios");
						List<Sociedade> listaSociedades = new ArrayList<Sociedade>();
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

						for (int i = 0; i < retornoSociedades.length(); i++) {
							JSONObject obj = retornoSociedades.getJSONObject(i);
							
							Sociedade sociedade = new Sociedade();
							sociedade.setDocumento(String.valueOf(obj.getLong("Documento")));
							sociedade.setNome(obj.getString("Nome"));
							sociedade.setDataEntrada(converteDateOriginal(obj.getString("DataEntrada")));
							sociedade.setCargo(obj.getString("Cargo"));
							
							if (!obj.isNull("PercentualParticipacao")) {
								sociedade.setPercentualParticipacao(obj.getString("PercentualParticipacao"));
							}
							
							listaSociedades.add(sociedade);
						}		
						
						this.retornoConsultaPJ.setSocios(listaSociedades);
	
						/***
						 * ParticipacoesEmpresa
						 */
						JSONArray retornoParticipacoesEmpresa = retorno.getJSONArray("ParticipacoesEmpresas");
						List<ParticipacoesEmpresa> listaParticipacoesEmpresa = new ArrayList<ParticipacoesEmpresa>();

						for (int i = 0; i < retornoParticipacoesEmpresa.length(); i++) {
							JSONObject obj = retornoParticipacoesEmpresa.getJSONObject(i);
							
							ParticipacoesEmpresa participacoesEmpresa = new ParticipacoesEmpresa();
							participacoesEmpresa.setDataEntrada(converteDate(obj.getString("DataEntrada")));
							participacoesEmpresa.setDocumento(obj.getString("Documento"));
							participacoesEmpresa.setNome(obj.getString("Nome"));
							participacoesEmpresa.setPosicao(obj.getString("Posicao"));
							participacoesEmpresa.setQualificacaoSocio(obj.getString("QualificacaoSocio"));

							if (!obj.isNull("DataSaida")) {
								participacoesEmpresa.setDataSaida(converteDate(obj.getString("DataSaida")));
							}
							if (!obj.isNull("Participacao")) {
								participacoesEmpresa.setParticipacao(obj.getString("Participacao"));
							}
							if (!obj.isNull("Relacao")) {
								participacoesEmpresa.setRelacao(obj.getString("Relacao"));
							}
							
							listaParticipacoesEmpresa.add(participacoesEmpresa);
						}		
						
						this.retornoConsultaPJ.setParticipacoesEmpresas(listaParticipacoesEmpresa);
						
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Consulta CNPJ: Consulta do CCNPJ " + this.cnpj + " efetuada com sucesso!", ""));
						
						this.consultaGerada = true;
					} else {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Consulta CNPJ: Erro ao realizar a consulta! (Erro: " + myResponse.getString("Tipo") + " | Mensagem: " + myResponse.getString("Mensagem") + ").", ""));
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
				retorno = DateUtil.gerarDataHoje();
				
				return retorno;
			}
			return retorno;
		}
	}
	
	public Date converteDateOriginal(String dateStr) {
		Date retorno = new Date();
		
		//2019-08-27T13:38:10.1420086-03:00
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateFormat formatterOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			try {
				retorno = ((java.util.Date)formatter.parse(dateStr));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return retorno;
		} catch (StringIndexOutOfBoundsException e) {
			try {
				retorno = ((java.util.Date) formatterOnlyDate.parse(dateStr));
			} catch (ParseException e1) {
				retorno = DateUtil.gerarDataHoje();
				
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

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public PJ getRetornoConsultaPJ() {
		return retornoConsultaPJ;
	}

	public void setRetornoConsultaPJ(PJ retornoConsultaPJ) {
		this.retornoConsultaPJ = retornoConsultaPJ;
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
			this.nomePDF = "Consulta - PJ - " + this.retornoConsultaPJ.getRazaoSocial() + ".pdf";
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

			PdfPCell cell1 = new PdfPCell(new Phrase("Cadastro PJ", header));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getInfoServico().getConsultaUid(), normal));
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
			
			if (this.retornoConsultaPJ.getDataConsulta() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaPJ.getDataConsulta()), normal));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getCnpj(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Razão Social: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getRazaoSocial(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nome Fantasia: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getNomeFantasia(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Data Fundação: ", titulo));
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
			
			if (this.retornoConsultaPJ.getDataFundacao() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaPJ.getDataFundacao()), normal));
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

			cell1 = new PdfPCell(new Phrase("CNAE Código: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getCnaeCodigo(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CNAE Descrição: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getCnaeDescricao(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CNAE Código Secundário: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getCnaeCodigoSecundario(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CNAE Descrição Secundário: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getCnaeDescricaoSecundario(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Qtde. Funcionários: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getQtdFuncionarios(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Situação Cadastral: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getSituacaoCadastral(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nat. Jur. Código: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getNatJurCodigo(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nat. Jur. Descrição: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getNatJurDescricao(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nat. Jur. Tipo: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getNatJurTipo(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Porte: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getPorte(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Faixa Funcionários: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getFaixaFuncionarios(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Faixa Faturamento: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getFaixaFaturamento(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Matriz: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getMatriz(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Órgão Público: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getOrgaoPublico(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Ramo: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getRamo(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Tipo Empresa: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPJ.getTipoEmpresa(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Última Atualização: ", titulo));
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
			
			if (this.retornoConsultaPJ.getUltimaAtualizacao() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaPJ.getUltimaAtualizacao()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Endereços", header));
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

			for (Endereco enderecos : this.retornoConsultaPJ.getEnderecos()) {
				cell1 = new PdfPCell(new Phrase("Logradouro: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(enderecos.getLogradouro(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Número: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(enderecos.getNumero(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Complemento: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(enderecos.getComplemento(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Bairro: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(enderecos.getBairro(), normal));
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
				
				cell1 = new PdfPCell(new Phrase(enderecos.getCidade() + "/" + enderecos.getUf(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("CEP: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(enderecos.getCep(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Última Atualização: ", titulo));
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
				
				if (enderecos.getUltimaAtualizacao() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(enderecos.getUltimaAtualizacao()), normal));
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
			}
			
			cell1 = new PdfPCell(new Phrase("Telefones", header));
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

			for (Telefone telefone : this.retornoConsultaPJ.getTelefones()) {
				cell1 = new PdfPCell(new Phrase("Telefone Com DDD: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(telefone.getTelefoneComDDD(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Telemarketing Bloqueado: ", titulo));
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
				
				if (telefone.isTelemarketingBloqueado()) {
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
				
				cell1 = new PdfPCell(new Phrase("Telemarketing Ult. Bloq. Desb.: ", titulo));
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
				
				if (telefone.isTelemarketingUltBloqDesb()) {
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
				
				cell1 = new PdfPCell(new Phrase("Operadora: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(telefone.getOperadora(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Última Atualização: ", titulo));
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
				
				if (telefone.getUltimaAtualizacao() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(telefone.getUltimaAtualizacao()), normal));
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
			}
			
			cell1 = new PdfPCell(new Phrase("E-mails", header));
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

			for (Email email : this.retornoConsultaPJ.getEmails()) {
				cell1 = new PdfPCell(new Phrase("Endereço de Email: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(email.getEnderecoEmail(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Última Atualização: ", titulo));
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
				
				if (email.getUltimaAtualizacao() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(email.getUltimaAtualizacao()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Sociedades", header));
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

			for (Sociedade sociedade : this.retornoConsultaPJ.getSocios()) {
				cell1 = new PdfPCell(new Phrase("Documento: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(sociedade.getDocumento(), normal));
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
				
				cell1 = new PdfPCell(new Phrase(sociedade.getNome(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Percentual Participação: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(sociedade.getPercentualParticipacao(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Data Entrada: ", titulo));
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
				
				if (sociedade.getDataEntrada() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(sociedade.getDataEntrada()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Relacionados", header));
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

			for (ParticipacoesEmpresa participacoesEmpresa : this.retornoConsultaPJ.getParticipacoesEmpresas()) {
				cell1 = new PdfPCell(new Phrase("Data Entrada: ", titulo));
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
				
				if (participacoesEmpresa.getDataEntrada() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(participacoesEmpresa.getDataEntrada()), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Data Saída: ", titulo));
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
				
				if (participacoesEmpresa.getDataSaida() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(participacoesEmpresa.getDataSaida()), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Documento: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(participacoesEmpresa.getDocumento(), normal));
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
				
				cell1 = new PdfPCell(new Phrase(participacoesEmpresa.getNome(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Participação: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(participacoesEmpresa.getParticipacao(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Posição: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(participacoesEmpresa.getPosicao(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Qualificação Sócio: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(participacoesEmpresa.getQualificacaoSocio(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Relação: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(participacoesEmpresa.getRelacao(), normal));
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