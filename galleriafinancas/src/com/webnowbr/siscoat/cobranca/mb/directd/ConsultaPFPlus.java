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
import com.webnowbr.siscoat.cobranca.db.model.directd.Email;
import com.webnowbr.siscoat.cobranca.db.model.directd.Endereco;


@ManagedBean(name = "consultaPFPlus")
@SessionScoped
public class ConsultaPFPlus {

	/****
	 * 
	 * LIVE TOKEN
	 * 0d54a6b5fa28f6dc42ce76d0011952e3
	 */
	
	private String accessToken = "0d54a6b5fa28f6dc42ce76d0011952e3";

	private String cpf;
	private PFPlus retornoConsultaPFPlus;
	
	private boolean consultaGerada;

	public ConsultaPFPlus() {

	}

	public String clearConsultaPFPlus() {
		this.cpf = "";
		this.consultaGerada = false;
		
		clearPDF();

		return "/Atendimento/ConsultasDirectd/ConsultaPFPlus.xhtml";
	}

	public void consultaPFPlus() {
		clearPDF();
		/*
		 *  https://apiv2.directd.com.br/consultas/cadastro/v1/consulta-pf-plus-por-cpf?token={token}&cpf={cpf}
		 */

		FacesContext context = FacesContext.getCurrentInstance();

		this.retornoConsultaPFPlus = new PFPlus();

		int HTTP_COD_SUCESSO = 200;

		boolean valid = true;
		
		//String cnpjNumeros = this.cnpj.replace(".", "").replace("/", "").replace("-", "");

		URL myURL;
		try {
			myURL = new URL("https://apiv2.directd.com.br/consultas/cadastro/v1/consulta-pf-plus-por-cpf?token=" + this.accessToken + "&cpf=" + this.cpf);


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
							FacesMessage.SEVERITY_ERROR, "Consulta PF Plus: Erro ao realizar a consulta PF Plus! (Código: " + myURLConnection.getResponseCode() + ")!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());				

					if (myResponse.getString("Tipo").equals("Sucesso")) {
						this.retornoConsultaPFPlus.getInfoServico().setConsultaUid(myResponse.getString("ConsultaUid"));
						this.retornoConsultaPFPlus.getInfoServico().setIdTipo(myResponse.getLong("IdTipo"));
						this.retornoConsultaPFPlus.getInfoServico().setTipo(myResponse.getString("Tipo"));
						this.retornoConsultaPFPlus.getInfoServico().setMensagem(myResponse.getString("Mensagem"));
						this.retornoConsultaPFPlus.getInfoServico().setTempoExecucaoMs(myResponse.getLong("TempoExecucaoMs"));
						this.retornoConsultaPFPlus.getInfoServico().setCustoTotalEmCreditos(myResponse.getLong("CustoTotalEmCreditos"));
						this.retornoConsultaPFPlus.getInfoServico().setSaldoEmCreditos(myResponse.getLong("SaldoEmCreditos"));
						this.retornoConsultaPFPlus.getInfoServico().setApiVersion(myResponse.getString("ApiVersion"));
	
						//JSONArray retorno = myResponse.getJSONObject("Retorno").getJSONArray("Retorno");
						
						JSONObject retorno = myResponse.getJSONObject("Retorno");
	
						// TODO CONVERTER PARA DATA EMITIDA E VALIDA
						this.retornoConsultaPFPlus.setCpf(String.valueOf(retorno.getLong("CPF")));
						this.retornoConsultaPFPlus.setNome(retorno.getString("Nome"));
						this.retornoConsultaPFPlus.setSexo(retorno.getString("Sexo"));
						this.retornoConsultaPFPlus.setDataNascimento(converteDate(retorno.getString("DataNascimento")));
						this.retornoConsultaPFPlus.setNomeMae(retorno.getString("NomeMae"));
						this.retornoConsultaPFPlus.setNomePai(retorno.getString("NomePai"));
						this.retornoConsultaPFPlus.setRg(retorno.getString("RG"));
						this.retornoConsultaPFPlus.setOrgaoEmissor(retorno.getString("OrgaoEmissor"));
						this.retornoConsultaPFPlus.setIdade(String.valueOf(retorno.getInt("Idade")));						
						this.retornoConsultaPFPlus.setNaturalidadeCidade(retorno.getString("NaturalidadeCidade"));
						this.retornoConsultaPFPlus.setNaturalidadeUF(retorno.getString("NaturalidadeUF"));
						this.retornoConsultaPFPlus.setPossivelNaturalidade(retorno.getString("PossivelNaturalidade"));
						this.retornoConsultaPFPlus.setNacionalidade(retorno.getString("Nacionalidade"));					
						this.retornoConsultaPFPlus.setCpfConjuge(retorno.getString("CPFConjuge"));
						this.retornoConsultaPFPlus.setClasseSocial(retorno.getString("ClasseSocial"));
						this.retornoConsultaPFPlus.setEscolaridade(retorno.getString("Escolaridade"));
						this.retornoConsultaPFPlus.setCboCodigo(retorno.getString("CBOCodigo"));
						this.retornoConsultaPFPlus.setCboDescricao(retorno.getString("CBODescricao"));
						this.retornoConsultaPFPlus.setServidorPublico(retorno.getString("ServidorPublico"));
						this.retornoConsultaPFPlus.setTipoIdentidade(retorno.getString("TipoIdentidade"));
						this.retornoConsultaPFPlus.setEspecialidade(retorno.getString("Especialidade"));
						this.retornoConsultaPFPlus.setDataConsulta(gerarDataHoje());
						
						if (retorno.getString("EstadoCivil").equals("C")) {
							this.retornoConsultaPFPlus.setEstadoCivil("Casado");
						}
						if (retorno.getString("EstadoCivil").equals("S")) {
							this.retornoConsultaPFPlus.setEstadoCivil("Solteiro");
						}
						if (retorno.getString("EstadoCivil").equals("D")) {
							this.retornoConsultaPFPlus.setEstadoCivil("Divorciado");
						}
						if (retorno.getString("EstadoCivil").equals("V")) {
							this.retornoConsultaPFPlus.setEstadoCivil("Viúvo");
						}
						if (retorno.getString("EstadoCivil").equals("O")) {
							this.retornoConsultaPFPlus.setEstadoCivil("Outros");
						}

						/**
						 * POSSIVEIS VALORES NULOS
						 */
						if (!retorno.isNull("Signo")) {
							this.retornoConsultaPFPlus.setSigno(retorno.getString("Signo"));
						}
						if (!retorno.isNull("UltimaAtualizacaoPF")) {
							this.retornoConsultaPFPlus.setUltimaAtualizacaoPF(converteDate(retorno.getString("UltimaAtualizacaoPF")));
						}
						if (!retorno.isNull("SituacaoReceitaBancoDados")) {
							this.retornoConsultaPFPlus.setSituacaoReceitaBancoDados(retorno.getString("SituacaoReceitaBancoDados"));
						}
						if (!retorno.isNull("RendaEstimada")) {
							this.retornoConsultaPFPlus.setRendaEstimada(retorno.getString("RendaEstimada"));
						}
						if (!retorno.isNull("Obito")) {
							this.retornoConsultaPFPlus.setObito(retorno.getString("Obito"));
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
						
						this.retornoConsultaPFPlus.setTelefones(listaTelefones);
						
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
						
						this.retornoConsultaPFPlus.setEnderecos(listaEnderecos);
						
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
						
						this.retornoConsultaPFPlus.setEmails(listaEmails);
						
						/***
						 * Sociedades
						 */
						JSONArray retornoSociedades = retorno.getJSONArray("Sociedades");
						List<Sociedade> listaSociedades = new ArrayList<Sociedade>();

						for (int i = 0; i < retornoSociedades.length(); i++) {
							JSONObject obj = retornoSociedades.getJSONObject(i);
							
							Sociedade sociedade = new Sociedade();
							sociedade.setDocumento(String.valueOf(obj.getLong("Documento")));
							sociedade.setNome(obj.getString("Nome"));
							sociedade.setDataEntrada(converteDate(obj.getString("UltimaAtualizacao")));
							
							if (!obj.isNull("PercentualParticipacao")) {
								sociedade.setPercentualParticipacao(obj.getString("PercentualParticipacao"));
							}
							
							listaSociedades.add(sociedade);
						}		
						
						this.retornoConsultaPFPlus.setSociedades(listaSociedades);
	
						/***
						 * Relacionados
						 */
						JSONArray retornoRelacionados = retorno.getJSONArray("Relacionados");
						List<Relacionado> listaRelacionados = new ArrayList<Relacionado>();

						for (int i = 0; i < retornoRelacionados.length(); i++) {
							JSONObject obj = retornoRelacionados.getJSONObject(i);
							
							Relacionado relacionado = new Relacionado();
							relacionado.setCpf(String.valueOf(obj.getLong("CPF")));
							relacionado.setNome(obj.getString("Nome"));
							relacionado.setRelacionamento(obj.getString("Relacionamento"));
							
							listaRelacionados.add(relacionado);
						}		
						
						this.retornoConsultaPFPlus.setRelacionados(listaRelacionados);
						
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Consulta PF Plus: Consulta do CPF " + this.cpf + " efetuada com sucesso!", ""));
						
						this.consultaGerada = true;
					} else {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Consulta PF Plus: Erro ao realizar a consulta! (Erro: " + myResponse.getString("Tipo") + " | Mensagem: " + myResponse.getString("Mensagem") + ").", ""));
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

	public PFPlus getRetornoConsultaPFPlus() {
		return retornoConsultaPFPlus;
	}

	public void setRetornoConsultaPFPlus(PFPlus retornoConsultaPFPlus) {
		this.retornoConsultaPFPlus = retornoConsultaPFPlus;
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
			this.nomePDF = "Consulta - PF PLUS - " + this.retornoConsultaPFPlus.getNome() + ".pdf";
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

			PdfPCell cell1 = new PdfPCell(new Phrase("Cadastro PF Plus", header));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getInfoServico().getConsultaUid(), normal));
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
			
			if (this.retornoConsultaPFPlus.getDataConsulta() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaPFPlus.getDataConsulta()), normal));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getCpf(), normal));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getNome(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Sexo: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getSexo(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Data Nascimento: ", titulo));
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
			
			if (this.retornoConsultaPFPlus.getDataNascimento() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaPFPlus.getDataNascimento()), normal));
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

			cell1 = new PdfPCell(new Phrase("Nome da Mãe: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getNomeMae(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nome do Pai: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getNomePai(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("RG: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getRg(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Órgão Emissor: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getOrgaoEmissor(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Idade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getIdade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Signo: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getSigno(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Naturalidade Cidade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getNaturalidadeCidade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Naturalidade UF: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getNaturalidadeUF(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Possível Naturalidade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getPossivelNaturalidade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Nacionalidade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getNacionalidade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Estado Civil: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getEstadoCivil(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CPF Cônjuge: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getCpfConjuge(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Classe Social: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getClasseSocial(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Escolaridade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getEscolaridade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CBO Código: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getCboCodigo(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("CBO Descrição: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getCboDescricao(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Servidor Público: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getServidorPublico(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Tipo Identidade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getTipoIdentidade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Especialidade: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getEspecialidade(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Última Atualização PF: ", titulo));
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
			
			if (this.retornoConsultaPFPlus.getUltimaAtualizacaoPF() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaPFPlus.getUltimaAtualizacaoPF()), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Situação Receita: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getSituacaoReceitaBancoDados(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Renda Estimada: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getRendaEstimada(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Óbito: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaPFPlus.getObito(), normal));
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

			for (Endereco enderecos : this.retornoConsultaPFPlus.getEnderecos()) {
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

			for (Telefone telefone : this.retornoConsultaPFPlus.getTelefones()) {
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

			for (Email email : this.retornoConsultaPFPlus.getEmails()) {
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

			for (Sociedade sociedade : this.retornoConsultaPFPlus.getSociedades()) {
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

			for (Relacionado relacionado : this.retornoConsultaPFPlus.getRelacionados()) {
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
				
				cell1 = new PdfPCell(new Phrase(relacionado.getCpf(), normal));
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
				
				cell1 = new PdfPCell(new Phrase(relacionado.getNome(), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Relacionamento: ", titulo));
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
				
				cell1 = new PdfPCell(new Phrase(relacionado.getRelacionamento(), normal));
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

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF PF Plus: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF PF Plus: Ocorreu um problema ao gerar o PDF!" + e, ""));
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