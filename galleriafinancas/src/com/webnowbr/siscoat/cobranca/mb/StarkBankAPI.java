package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.starkbank.Balance;
import com.starkbank.BoletoPayment;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.starkbank.Transfer;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.Signature;
import com.starkbank.utils.Generator;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankBoleto;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankPix;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankBoletoDAO;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankPixDAO;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@ManagedBean(name = "starkBankAPI")
@SessionScoped
public class StarkBankAPI{
    
	/*
	 * VALIDAÇÃO DAS CHAVES DE SEGURANÇA*/
    public static void main(String[] args){
    	//transfer();
    	getBalanceSDK();
    
    /*
        String publicKeyPem = File.read("src/resource/publicKey.pem");
        byte[] signatureBin = File.readBytes("src/resource/signatureBinary.txt");
        String message = File.read("src/resource/message.txt");

        ByteString byteString = new ByteString(signatureBin);

        PublicKey publicKey = PublicKey.fromPem(publicKeyPem);
        Signature signature = Signature.fromDer(byteString);

        // Get verification status:
        boolean verified = Ecdsa.verify(message, signature, publicKey);
        System.out.println("Verification status: " + verified);
        */
    }

    
    public void testeAutenticacaoManual() {
    	// Set your user Id
    	String accessId = "project/5465772415516672";
    	
    	// Get current Unix Time
    	Date date = new Date();
    	long time = date.getTime() / 1000;
    	String accessTime = String.valueOf(time);
    	
    	
    	// Get you json content in string format for POST/PUT/PATCH requests
    	// or empty string for GET/DELETE requests.
    	String bodyString = "";
    	
    	// Build the message in this format:
    	String message = accessId + ":" + accessTime + ":" + bodyString;
    	
    	// Load your private key from its pem string
    	PrivateKey privateKey = PrivateKey.fromPem("src/resource/privateKey.pem");
    	
    	// Use ECDSA to sign the message
    	Signature signature = Ecdsa.sign(message, privateKey);
    	
    	// Convert the signature to base 64
    	String accessSignature = signature.toBase64();
    	
    	
    }
    
	public void getBalance() {
    	// Set your user Id
    	String accessId = "project/5465772415516672";
    	
    	// Get current Unix Time
    	Date date = new Date();
    	long time = date.getTime() / 1000;
    	String accessTime = String.valueOf(time);
    	
    	
    	// Get you json content in string format for POST/PUT/PATCH requests
    	// or empty string for GET/DELETE requests.
    	String bodyString = "";
    	
    	// Build the message in this format:
    	String message = accessId + ":" + accessTime + ":" + bodyString;
    	
    	// Load your private key from its pem string
    	PrivateKey privateKey = PrivateKey.fromPem("src/resource/privateKey.pem");
    	
    	// Use ECDSA to sign the message
    	Signature signature = Ecdsa.sign(message, privateKey);
    	
    	// Convert the signature to base 64
    	String accessSignature = signature.toBase64();
    	
		try {						
			FacesContext context = FacesContext.getCurrentInstance();
			
			/*

			curl --location --request GET '{{baseUrl}}/v2/balance' \
			--header 'Access-Id: {{accessId}}' \
			--header 'Access-Time: {{accessTime}}' \
			--header 'Access-Signature: {{accessSignature}}'

			 */

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.starkbank.com/v2/balance");

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				
				myURLConnection.setRequestProperty("Access-Id", accessId);
				myURLConnection.setRequestProperty("Access-Time", accessTime);
				myURLConnection.setRequestProperty("Access-Signature", accessSignature);
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "EROOOOOO!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consultar Transferências Bancárias: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO IUGU
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
    
    public static void getBalanceSDK() {
    	String privateKeyContent = "-----BEGIN EC PARAMETERS-----\nBgUrgQQACg==\n-----END EC PARAMETERS-----\n-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIAKGZfO+lee7tdtcLGbCT++oGyUcmm/2Jdozg8D8mF4ioAcGBSuBBAAKoUQDQgAEUvi66NomZ1HeFqEwrXvnM/IjDQEJjVp6nYYojlOsTP1tYO34tW+bO1ypWln5lfkDNCcARQ710SmPPrLRHRbMAA==\n-----END EC PRIVATE KEY-----";
    	
    	Settings.language = "pt-BR";
    	
    	Balance balance;
		try {
			Project project = new Project(
				    "production",
				    //"5465772415516672", // financas
				    "5092647223951360", // coban
				    privateKeyContent
				);
			
			balance = Balance.get(project);
			System.out.println(balance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
 
    public String getLinhaDigitavel() {
		return linhaDigitavel;
	}


	public void setLinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void listPaymentBoleto() {
		loginStarkBank();
		
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("after", "2023-05-01");
			params.put("before", "2023-06-10");
			Generator<BoletoPayment> payments;
			payments = BoletoPayment.query(params);

			for (BoletoPayment payment : payments){
			    System.out.println(payment);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paymentBoletoLog() {
	
		loginStarkBank();
	
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("paymentIds", "4666592117915648");
			Generator<BoletoPayment.Log> logs = BoletoPayment.Log.query(params);
			
			for (BoletoPayment.Log log : logs){
			    System.out.println(log);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public String linhaDigitavel = "";
    public String descricao = "";
    
    public void chamaBoletoStarkTela() {
		FacesContext context = FacesContext.getCurrentInstance();
		
    	PagadorRecebedor pagador = new PagadorRecebedor();
    	PagadorRecebedorDao pDao = new PagadorRecebedorDao();
    	pagador = pDao.findById((long) 7);
    	
    	
    	paymentBoleto(linhaDigitavel, null, pagador, descricao, null);
    	
    	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Boleto Stark Bank - Pago", ""));
    	
    }

	public StarkBankBoleto paymentBoleto(String boleto, ContratoCobranca contrato, PagadorRecebedor pessoa, String descricao, String documentoPagadorCustom) {

		FacesContext context = FacesContext.getCurrentInstance();
    	StarkBankBoleto boletoTransacao = null;
    	String[] tags = {""};
    	
    	loginStarkBank();
    	
    	Settings.language = "pt-BR";

    	try {
	    	List<BoletoPayment> payments = new ArrayList<>();
	    	HashMap<String, Object> data = new HashMap<>();
	    	data.put("line", boleto);
	    	
	    	if (documentoPagadorCustom == null || documentoPagadorCustom.equals("")) {
		    	if (pessoa.getCpf() != null && !pessoa.getCpf().equals("")) {
		    		data.put("taxId", pessoa.getCpf());	
		    	} else {
		    		data.put("taxId", pessoa.getCnpj());	
		    	}
	    	} else {
	    		data.put("taxId", documentoPagadorCustom);	
	    	}	    	

	    	data.put("scheduled", DateUtil.getDataHojeAmericano());
	    	data.put("description", descricao);
	    	
	    	String numeroContrato = "";
	    	
	    	// atribui tags
	    	if (contrato != null) {
	    	  	tags[0] = contrato.getNumeroContrato();
	    	  	data.put("tags", tags);
	    	}
	
			payments.add(new BoletoPayment(data));
	
	    	payments = BoletoPayment.create(payments);

	    	for (BoletoPayment payment : payments){
	    		String tagsStr = "";
	    		if (payment.tags.length > 0) {
	    			for (String tag : tags) {
	    				if (tag.equals("")) {
	    					tagsStr = tag;
	    				} else {
	    					tagsStr = tagsStr + " | " + tag;
	    				}
	    			}
	    		}
	    		
	    		/*
	    		 * TODO REVER PARSE DA DATA 
	    		 */
	    		
	    		geraReciboPagamentoBoleto(payment.id, BigDecimal.valueOf(payment.amount), payment.line, DateUtil.convertDateTimeToDate(payment.created), payment.taxId, pessoa.getNome());
	    		
	    		boletoTransacao = new StarkBankBoleto(Long.valueOf(payment.id), BigDecimal.valueOf(payment.amount), payment.taxId, tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.fee, payment.status, DateUtil.convertDateTimeToDate(payment.created), this.pathPDF, this.nomePDF);
	    		
	    		StarkBankBoletoDAO starkBankBoletoDAO = new StarkBankBoletoDAO();
	    		starkBankBoletoDAO.create(boletoTransacao);
	    		
	    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[StarkBank - Pagamento Boleto] Boleto Pago com Sucesso! Transação: " + payment.id, ""));
	    		// GERAR Recibo
	    	}
    	
		} catch (Exception e) {
			JSONObject erroStarkBank = new JSONObject(e.getMessage());
			JSONArray erros = new JSONArray(erroStarkBank.getJSONArray("errors"));
			
			String errosStrStarkBank = "";
			for (int i = 0; i < erros.length(); i++) {
				JSONObject dados = erros.getJSONObject(i);
				
				if (errosStrStarkBank.equals("")) {
					errosStrStarkBank = i + "-" + dados.getString("message").replace("Element 0: ", "");
				} else {
					errosStrStarkBank = errosStrStarkBank + " | " + i + "-" + dados.getString("message").replace("Element 0: ", "");
				}
			}	
			
			if (!errosStrStarkBank.equals("")) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[StarkBank - Pagamento Boleto] Falha no pagamento: " + errosStrStarkBank, ""));
			}
		}
    	
    	 return boletoTransacao;
    }
	
	private String nomePDF;
	private String pathPDF;
	private boolean comprovanteGerado;
	
	public void geraReciboPagamentoBoleto(String idTransacao, BigDecimal valor, String linhaBoleto, Date dataPagamento, String taxID, String nomePagador) {
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
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */


			doc = new Document(PageSize.A4.rotate(), 10, 80, 10, 80);
			this.nomePDF = "Recibo - Pagamento Boleto - " + nomePagador + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

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
			PdfPTable table = new PdfPTable(new float[] { 0.8f, 0.8f});
			table.setWidthPercentage(50.0f); 
			
			BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/logoStarkBank.jpg"));
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(buff, "jpg", bos);
	        Image img = Image.getInstance(bos.toByteArray());
	        
			img.setAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(img);
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);			
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("StarkBank - Sistema de Pagamento online", header));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Comprovante de Pagamento - Boleto", tituloBranco));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(new BaseColor(92, 156, 204));
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("ID da Transação: " + idTransacao, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Data: " + sdfDataRelComHoras.format(dataPagamento), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(2f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor R$ " + df.format(valor), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Boleto: " + linhaBoleto, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CPF/CNPJ: " + taxID, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Nome: " + nomePagador, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Obs.: Compensação no próximo dia útil.", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(20f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Recibo de Pagamento: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Recibo de Pagamento: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.comprovanteGerado = true;

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
	
	public void geraReciboPagamentoPix(String idTransacao, BigDecimal valor, Date dataPagamento, String taxID, String nomePagador) {
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
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */


			doc = new Document(PageSize.A4.rotate(), 10, 80, 10, 80);
			this.nomePDF = "Recibo - Pagamento Pix/TED - " + nomePagador + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

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
			PdfPTable table = new PdfPTable(new float[] { 0.8f, 0.8f});
			table.setWidthPercentage(50.0f); 
			
			BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/logoStarkBank.jpg"));
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(buff, "jpg", bos);
	        Image img = Image.getInstance(bos.toByteArray());
	        
			img.setAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(img);
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);			
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("StarkBank - Sistema de Pagamento online", header));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Comprovante de Pagamento - Pix/TED", tituloBranco));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(new BaseColor(92, 156, 204));
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("ID da Transação: " + idTransacao, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Data: " + sdfDataRelComHoras.format(dataPagamento), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(2f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor R$ " + df.format(valor), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CPF/CNPJ: " + taxID, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Nome: " + nomePagador, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(20f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Recibo de Pagamento: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Recibo de Pagamento: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.comprovanteGerado = true;

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
	
	StreamedContent downloadFile;
	
	public StreamedContent getDownloadFile() {
		String caminho =  this.pathPDF + this.nomePDF;        
		String arquivo = this.nomePDF;
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(caminho);
			downloadFile = new DefaultStreamedContent(stream, this.pathPDF,
					this.nomePDF);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("StarkBank - Comprovante não encontrado!");
		}
		
		return this.downloadFile;
	}
	
	private StreamedContent file;
	
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
	
	public static void loginStarkBank() {
    	Settings.language = "pt-BR";
    	
    	String privateKeyContent = "-----BEGIN EC PARAMETERS-----\nBgUrgQQACg==\n-----END EC PARAMETERS-----\n-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIAKGZfO+lee7tdtcLGbCT++oGyUcmm/2Jdozg8D8mF4ioAcGBSuBBAAKoUQDQgAEUvi66NomZ1HeFqEwrXvnM/IjDQEJjVp6nYYojlOsTP1tYO34tW+bO1ypWln5lfkDNCcARQ710SmPPrLRHRbMAA==\n-----END EC PRIVATE KEY-----";
    	
    	Project project;
		try {
			project = new Project(
				    "production",
				    //"5465772415516672", // financas
				    "5092647223951360", // coban
				    privateKeyContent
				);
			
	    	Settings.user = project;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
    
    public static void transfer() {
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	loginStarkBank(); 
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	
	    	HashMap<String, Object> data = new HashMap<>();
	    	data.put("amount", 100);
	    	data.put("bankCode", "00000000");
	    	data.put("branchCode", "1515-6");
	    	data.put("accountNumber", "131094-1");
	    	data.put("taxId", "34.787.885/0001-32");
	    	data.put("name", "GALLERIA BANK");
	    	data.put("externalId", "transactionTest002");
	    	//data.put("scheduled", "2020-08-14");
	    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
	    	//data.put("rules", rules);
	    	
			transfers.add(new Transfer(data));
			
			transfers = Transfer.create(transfers);

	    	for (Transfer transfer : transfers){
	    	    System.out.println(transfer);
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public StarkBankPix paymentPix(String codigoPixBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor) {
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	loginStarkBank(); 
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	
	    	HashMap<String, Object> data = new HashMap<>();
	    	data.put("amount", valor);
	    	data.put("bankCode", codigoPixBanco);
	    	data.put("branchCode", agencia);
	    	data.put("accountNumber", numeroConta);
	    	data.put("taxId", documento);
	    	data.put("name", nomeBeneficiario);
	    	data.put("externalId", "PagamentoPix/" + nomeBeneficiario);
	    	//data.put("scheduled", "2020-08-14");
	    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
	    	//data.put("rules", rules);
	    	
			transfers.add(new Transfer(data));
			
			transfers = Transfer.create(transfers);
			
			if (transfers.size() > 0) {
	    		geraReciboPagamentoPix(String.valueOf(transfers.get(0).id), valor, DateUtil.convertDateTimeToDate(transfers.get(0).created), documento, nomeBeneficiario);
	    	}

			StarkBankPix pixTransacao = new StarkBankPix();
	    	for (Transfer transfer : transfers){
				 pixTransacao.setId(Long.valueOf(transfer.id));
				 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
				 pixTransacao.setScheduled(transfer.scheduled);
				 pixTransacao.setNomeComprovante(nomeBeneficiario);
				 pixTransacao.setAmount(valor);
				 pixTransacao.setTaxId(documento);
				 pixTransacao.setPathComprovante(this.pathPDF);
				 pixTransacao.setNomeComprovante(this.nomePDF);
	    	}
	    	
	    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(pixTransacao);
	    	
	    	return pixTransacao;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public StarkBankPix paymentPixTest(String codigoPixBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor) {
    	
    	geraReciboPagamentoPix("999", valor, gerarDataHoje(), documento, nomeBeneficiario);
    	
    	StarkBankPix starkBankPix = new StarkBankPix();
    	
    	starkBankPix.setId(212);
    	starkBankPix.setCreated(gerarDataHoje());
    	starkBankPix.setScheduled("sadsad");
    	starkBankPix.setNomeComprovante("nome");
    	starkBankPix.setAmount(BigDecimal.TEN);
    	starkBankPix.setTaxId("docu");
    	starkBankPix.setPathComprovante(this.pathPDF);
    	 starkBankPix.setNomeComprovante(this.nomePDF);
    	 
    	 StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(starkBankPix);
	    	
    	 return starkBankPix;
    }
    
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}


	public String getNomePDF() {
		return nomePDF;
	}


	public void setNomePDF(String nomePDF) {
		this.nomePDF = nomePDF;
	}


	public String getPathPDF() {
		return pathPDF;
	}


	public void setPathPDF(String pathPDF) {
		this.pathPDF = pathPDF;
	}


	public boolean isComprovanteGerado() {
		return comprovanteGerado;
	}


	public void setComprovanteGerado(boolean comprovanteGerado) {
		this.comprovanteGerado = comprovanteGerado;
	}


	public void setFile(StreamedContent file) {
		this.file = file;
	}
}