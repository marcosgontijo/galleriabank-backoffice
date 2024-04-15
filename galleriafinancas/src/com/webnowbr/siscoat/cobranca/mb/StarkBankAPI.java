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
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.starkbank.Balance;
import com.starkbank.BoletoPayment;
import com.starkbank.BrcodePayment;
import com.starkbank.DictKey;
import com.starkbank.PaymentPreview;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.starkbank.TaxPayment;
import com.starkbank.Transfer;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.Signature;
import com.starkbank.utils.Generator;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankBaixa;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankBoleto;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankPix;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankTax;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankBaixaDAO;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankBoletoDAO;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankPixDAO;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankTaxDAO;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@ManagedBean(name = "starkBankAPI")
@SessionScoped
public class StarkBankAPI{
	
	private Date dataInicio;
	private Date dataFim;
	private List<StarkBankBoleto> listBoletos;
	
	private StarkBankBaixa contaIndividual = new StarkBankBaixa();
	private StarkBankBaixa selectedContaIndividual = new StarkBankBaixa();
	private List<StarkBankBaixa> contasIndividual= new ArrayList<StarkBankBaixa>();
	
	private String nomeComprovanteStarkBank = "";
	private String pathComprovanteStarkBank = ""; 
	
	StreamedContent downloadComprovanteStarkBank;
	public boolean comprovanteStarkBankGerado;
    
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
    
    public String clearFieldsContaIndividual() {
    	this.contaIndividual = new StarkBankBaixa();
    	contasIndividual= new ArrayList<StarkBankBaixa>();
    
    	getContasIndividualBD();
    	
    	return "/Atendimento/Cobranca/CobrancaContaIndividualStarkBank.xhtml";
    }
    
    public void getContasIndividualBD() {
    	StarkBankBaixaDAO sBBDao = new StarkBankBaixaDAO();
    	
    	this.contasIndividual = sBBDao.findByFilter("contaIndividual", true);    	
    }
    
    public void cadastrarContaIndividual() {

		FacesContext context = FacesContext.getCurrentInstance();

		StarkBankBaixaDAO sBBDao = new StarkBankBaixaDAO();
		
		this.contaIndividual.setContaIndividual(true);
		this.contaIndividual.setStatusPagamento("Aguardando Pagamento");
		
		if (this.contaIndividual.getDescricaoStarkBank().length() < 10) {
			this.contaIndividual.setDescricaoStarkBank("Galleria Bank - Conta Individual - " + this.contaIndividual.getDescricaoStarkBank());
		}
		
		sBBDao.create(this.contaIndividual);
		
		getContasIndividualBD();
		
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"[StarkBank - Pagamento Conta Individual] Cadastro de Conta efetuado com Sucesso!", ""));
    }
    
	public void processarPagamentoContaIndividual() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		boolean finalizaOperacao = false;
		
		StarkBankAPI starkBankAPI = new StarkBankAPI();

		if (this.selectedContaIndividual.getFormaPagamento().equals("Boleto")) {
			
			System.out.println("processaPagamentoStarkBank - Boleto");

			StarkBankBoleto starkBankBoleto = starkBankAPI.paymentBoleto(
					this.selectedContaIndividual.getLinhaBoleto(), null,
					null, this.selectedContaIndividual.getDescricaoStarkBank(),
					this.selectedContaIndividual.getDocumento(),
					null);

			if (starkBankBoleto != null) {
				// this.contasPagarSelecionada.setComprovantePagamentoStarkBank(starkBankBoleto);
				StarkBankBaixa baixa = updateBaixaStarkBank(this.selectedContaIndividual,							
						String.valueOf(starkBankBoleto.getId()),
						starkBankBoleto.getCreated(),
						this.selectedContaIndividual.getValor(),
						"Pago",
						starkBankBoleto.getLine());

				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Pagamento StarkBank: Boleto pago sucesso!", ""));

				finalizaOperacao = true;
			}
		}
		
		if (this.selectedContaIndividual.getFormaPagamento().equals("Imposto")) {
			
			System.out.println("processaPagamentoStarkBank - Imposto");

			StarkBankTax starkBankTax = starkBankAPI.paymentTax(
					this.selectedContaIndividual.getLinhaBoleto(), this.selectedContaIndividual.getDescricaoStarkBank());

			if (starkBankTax != null) {
				// this.contasPagarSelecionada.setComprovantePagamentoStarkBank(starkBankBoleto);
				StarkBankBaixa baixa = updateBaixaStarkBank(this.selectedContaIndividual,							
						String.valueOf(starkBankTax.getId()),
						starkBankTax.getCreated(),
						this.selectedContaIndividual.getValor(),
						"Pago",
						starkBankTax.getLine());

				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Pagamento StarkBank: Imposto pago sucesso!", ""));

				finalizaOperacao = true;
			}
		}

		if (this.selectedContaIndividual.getFormaPagamento().equals("Pix") || 
				this.selectedContaIndividual.getFormaPagamento().equals("PIX")) {
			
			System.out.println("processaPagamentoStarkBank - Pix");
			
			StarkBankPix starkBankPix = null;
			
			System.out.println("processaPagamentoStarkBank - Pix");
			
			if (this.selectedContaIndividual.getMetodoPix().equals("Chave Pix")) {
				starkBankPix = starkBankAPI.paymentPixCodigo(
						this.selectedContaIndividual.getPix(),
						this.selectedContaIndividual.getAgencia(),
						this.selectedContaIndividual.getConta(),
						this.selectedContaIndividual.getDocumento(),
						this.selectedContaIndividual.getNomeRecebedor(),
						this.selectedContaIndividual.getValor(),
						this.selectedContaIndividual.getFormaPagamento(),
						null,
						this.selectedContaIndividual.getTipoContaBancaria());
			}
			
			if (this.selectedContaIndividual.getMetodoPix().equals("Dados Bancários")) {
				starkBankPix = starkBankAPI.paymentPixDadosBancarios(
						this.selectedContaIndividual.getIspb(),
						this.selectedContaIndividual.getAgencia(),
						this.selectedContaIndividual.getConta(),
						this.selectedContaIndividual.getDocumento(),
						this.selectedContaIndividual.getNomeRecebedor(),
						this.selectedContaIndividual.getValor(),
						this.selectedContaIndividual.getFormaPagamento(),
						null,
						this.selectedContaIndividual.getTipoContaBancaria());
			}
			
			if (this.selectedContaIndividual.getMetodoPix().equals("QR Code Copia e Cola")) {
				starkBankPix = starkBankAPI.paymentPixQRCode(
						this.selectedContaIndividual.getPix(),
						this.selectedContaIndividual.getDocumento(),
						this.selectedContaIndividual.getValor(),
						null);
			}
			
			if (starkBankPix != null) {	
				System.out.println("processaPagamentoStarkBank - Update Baixa");
				StarkBankBaixa baixa = updateBaixaStarkBank(this.selectedContaIndividual,							
						String.valueOf(starkBankPix.getId()),
						starkBankPix.getCreated(),
						starkBankPix.getAmount(),
						"Pago",
						null);
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Pagamento StarkBank: PIX efetuado com sucesso!", ""));

				finalizaOperacao = true;
			}
		}

		if (this.selectedContaIndividual.getFormaPagamento().equals("TED")) {
			System.out.println("processaPagamentoStarkBank - TED");
			
			StarkBankPix starkBankPix;
			
			starkBankPix = starkBankAPI.paymentTED(
					this.selectedContaIndividual.getBanco(),
					this.selectedContaIndividual.getAgencia(),
					this.selectedContaIndividual.getConta(),
					this.selectedContaIndividual.getDocumento(),
					this.selectedContaIndividual.getNomeRecebedor(),
					this.selectedContaIndividual.getValor(),
					this.selectedContaIndividual.getFormaPagamento(),
					null,
					this.selectedContaIndividual.getTipoContaBancaria());
		
			if (starkBankPix != null) {
				StarkBankBaixa baixa = updateBaixaStarkBank(this.selectedContaIndividual,							
						String.valueOf(starkBankPix.getId()),
						starkBankPix.getCreated(),
						starkBankPix.getAmount(),
						"Pago",
						null);
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Pagamento StarkBank: TED efetuado com sucesso!", ""));

				finalizaOperacao = true;
			}
		}
	
		/*
		if (finalizaOperacao) {

		}
		*/
	}
	
	public StreamedContent getDownloadComprovanteStarkBank(StarkBankBaixa baixaStarkBank) {
		FacesContext context = FacesContext.getCurrentInstance();

		geraReciboPagamentoStarkBank(baixaStarkBank);

		String caminho = this.pathComprovanteStarkBank + this.nomeComprovanteStarkBank;
		String arquivo = this.nomeComprovanteStarkBank;
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(caminho);
			downloadComprovanteStarkBank = new DefaultStreamedContent(stream, this.pathComprovanteStarkBank,
					this.nomeComprovanteStarkBank);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("StarkBank - Comprovante não encontrado!");
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"[Stark Bank - Recibo de Pagamento] Recibo de pagamento gerado com sucesso!", ""));

		return this.downloadComprovanteStarkBank;
	}
	
	public void geraReciboPagamentoStarkBank(StarkBankBaixa baixaStarkBank) {
		/*
		 * this.transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
		 * this.transferenciasObservacoesIUGU.setId(1);
		 * this.transferenciasObservacoesIUGU.setIdTransferencia(
		 * "jdsfhdsfhjskfhjhslafdshf"); this.transferenciasObservacoesIUGU.
		 * setObservacao("asdklfhjksdhfjd dsjfhjhdsfjashgdfj ");
		 * 
		 * this.valorItem = new BigDecimal("30000.00");
		 */

		this.comprovanteStarkBankGerado = false;

		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloGray = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloGray.setColor(BaseColor.LIGHT_GRAY);
			
			Font tituloGreen = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloGreen.setColor(158, 195, 32);
			
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

			doc = new Document(PageSize.A4);
			this.nomeComprovanteStarkBank = "Recibo Pagamento -  " + baixaStarkBank.getNomePagador() + ".pdf";
			this.pathComprovanteStarkBank = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathComprovanteStarkBank + this.nomeComprovanteStarkBank);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(2);
			//table.setWidthPercentage(100.0f);

			BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/pgto430.png"));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(buff, "png", bos);
			Image img = Image.getInstance(bos.toByteArray());

			img.setAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(img);
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setPaddingTop(20f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Pagador", tituloGreen));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(20f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			if (baixaStarkBank.getContasPagar().getDescricao().contains("Pagamento Carta Split")) {
				cell1 = new PdfPCell(new Phrase("Galleria SCD", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(2f);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CNPJ: 51.604.356/0001-75", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(30f);
				table.addCell(cell1);
			} else {
				cell1 = new PdfPCell(new Phrase("Galleria Correspondente Bancário Eireli", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(2f);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CNPJ: 34.787.885/0001-32", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(2f);
				table.addCell(cell1);
			}
			
			cell1 = new PdfPCell(new Phrase("Recebedor", tituloGreen));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(20f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			if (baixaStarkBank.getContasPagar().getDescricao().contains("Pagamento Carta Split")) {
				if (baixaStarkBank.getContasPagar().getFormaTransferencia().equals("Pix") || baixaStarkBank.getContasPagar().getFormaTransferencia().equals("PIX")
						|| baixaStarkBank.getContasPagar().getFormaTransferencia().equals("TED")) {	
					
					if (baixaStarkBank.getContasPagar() != null) {
						cell1 = new PdfPCell(new Phrase(baixaStarkBank.getContasPagar().getNomeTed(), titulo));
						cell1.setBorder(0);
						cell1.setPaddingLeft(8f);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(10f);
						cell1.setPaddingBottom(2f);
						table.addCell(cell1);
						
						cell1 = new PdfPCell(new Phrase("CPF/CNPJ: " + baixaStarkBank.getContasPagar().getCpfTed(), titulo));
						cell1.setBorder(0);
						cell1.setPaddingLeft(8f);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(10f);
						cell1.setPaddingBottom(2f);
						table.addCell(cell1);
					}
				} else {
					cell1 = new PdfPCell(new Phrase(baixaStarkBank.getNomePagador(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(2f);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("CPF/CNPJ: " + baixaStarkBank.getDocumento(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(2f);
					table.addCell(cell1);
				}
			} else {
				cell1 = new PdfPCell(new Phrase(baixaStarkBank.getNomePagador(), titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(2f);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CPF/CNPJ: " + baixaStarkBank.getDocumento(), titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(2f);
				table.addCell(cell1);
			}
			
			if (baixaStarkBank.getContasPagar().getBancoTed() != null && baixaStarkBank.getContasPagar().getAgenciaTed() != null && 
					baixaStarkBank.getContasPagar().getContaTed() != null) {
				
				if (baixaStarkBank.getContasPagar().getFormaTransferencia().equals("Pix") || baixaStarkBank.getContasPagar().getFormaTransferencia().equals("PIX")) {										
					cell1 = new PdfPCell(new Phrase("Banco: " + baixaStarkBank.getContasPagar().getBancoTed(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(2f);
					cell1.setPaddingBottom(2f);	
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Ag.: " + baixaStarkBank.getContasPagar().getAgenciaTed() + " | C/C: " + baixaStarkBank.getContasPagar().getContaTed(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(2f);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("PIX: " + baixaStarkBank.getContasPagar().getPix(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setColspan(2);
					cell1.setPaddingBottom(20f);	
					table.addCell(cell1);
				} else {					
					cell1 = new PdfPCell(new Phrase("Banco: " + baixaStarkBank.getContasPagar().getBancoTed(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(20f);	
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Ag.: " + baixaStarkBank.getContasPagar().getAgenciaTed() + " | C/C: " + baixaStarkBank.getContasPagar().getContaTed(), titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(20f);
					table.addCell(cell1);
				}
			}
			
			cell1 = new PdfPCell(new Phrase("Transferência - " + baixaStarkBank.getFormaPagamento(), tituloGreen));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(20f);
			cell1.setColspan(2);
			cell1.setPaddingBottom(10f);			
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Valor: R$ " + df.format(baixaStarkBank.getValor()), titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Data do Pagamento: " + sdfDataRelComHoras.format(baixaStarkBank.getDataPagamento()), titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			if (baixaStarkBank.getContasPagar().getFormaTransferencia().equals("Boleto")) {	
				cell1 = new PdfPCell(new Phrase("Linha Digitável: " + baixaStarkBank.getLinhaBoleto(), titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(2f);
				cell1.setColspan(2);
				table.addCell(cell1);
			}
			
			cell1 = new PdfPCell(new Phrase("Autenticação: " + baixaStarkBank.getIdTransacao(), titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[Stark Bank - Recibo de Pagamento] Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[Stark Bank - Recibo de Pagamento] Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.comprovanteStarkBankGerado = true;

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
		}
	}
	
	public StarkBankBaixa updateBaixaStarkBank(StarkBankBaixa starkBankBaixa, String idTransacao, Date dataPagamento,
			BigDecimal valorPago, String statusPagamento, String linhaBoleto) {
		StarkBankBaixaDAO sbDAO = new StarkBankBaixaDAO();

		starkBankBaixa.setDataPagamento(dataPagamento);
		starkBankBaixa.setStatusPagamento(statusPagamento);
		starkBankBaixa.setIdTransacao(idTransacao);
		starkBankBaixa.setLinhaBoleto(linhaBoleto);
		starkBankBaixa.setValor(valorPago);

		sbDAO.merge(starkBankBaixa);

		return starkBankBaixa;
	}
    
    public void deletarPagamentoContaIndividual() {

		FacesContext context = FacesContext.getCurrentInstance();

		StarkBankBaixaDAO sBBDao = new StarkBankBaixaDAO();
		sBBDao.delete(this.selectedContaIndividual);
		
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"[StarkBank - Pagamento Conta Individual] Conta excluída com sucesso!", ""));
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
	
	public void consultarBoletos() {
		loginStarkBank();
		this.listBoletos = new ArrayList<StarkBankBoleto>();
		StarkBankBoleto boleto = new StarkBankBoleto();
		
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("after", DateUtil.getDataAmericano(this.dataInicio));
			params.put("before", DateUtil.getDataAmericano(this.dataFim));
			Generator<BoletoPayment> payments;
			payments = BoletoPayment.query(params);

			for (BoletoPayment payment : payments){
				boleto = new StarkBankBoleto();
			    
	    		String tagsStr = "";
	    		if (payment.tags.length > 0) {
	    			for (String tag : payment.tags) {
	    				if (tag.equals("")) {
	    					tagsStr = tag;
	    				} else {
	    					tagsStr = tagsStr + " | " + tag;
	    				}
	    			}
	    		}
	    		
	    		String retornoLog = paymentBoletoLog(payment.id);
	    		
	    		String valorTratadoStr = payment.amount.toString();
	    		
	    		if (valorTratadoStr.length() >= 3) {
	    			valorTratadoStr = valorTratadoStr.substring(0, valorTratadoStr.length() - 2) + "." + valorTratadoStr.substring(valorTratadoStr.length() - 2, valorTratadoStr.length());
	    		}
	    		
	    		double valorTratadoLong = Double.valueOf(valorTratadoStr);
			    
			    boleto = new StarkBankBoleto(Long.valueOf(payment.id), BigDecimal.valueOf(valorTratadoLong).setScale(2), payment.taxId, tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.fee, payment.status, DateUtil.convertDateTimeToDate(payment.created), retornoLog);
	    		
			    
			    this.listBoletos.add(boleto);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void listPaymentBoleto() {
		loginStarkBank();
		
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("after", "2023-05-01");
			params.put("before", "2023-12-31");
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
	

	public static PaymentPreview paymentPreview(String idPagamento) {
		List<PaymentPreview> previews = new ArrayList<>();
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", idPagamento);
		//params.put("scheduled", DateUtil.getDataHojeAmericano());
		loginStarkBank();
		
		try {
			previews.add(new PaymentPreview(params));
			
			PaymentPreview teste;
			
			previews = (List<PaymentPreview>) PaymentPreview.create(previews);
			
			if(previews.size() > 0) {
				//System.out.println(previews);
				return previews.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		//retorno boleto
		/* 
			PaymentPreview({
			    "scheduled": "2023-01-29",
			    "type": "brcode-payment",
			    "payment": {
			        "accountType": "savings",
			        "allowChange": false,
			        "amount": 1000.0,
			        "bankCode": "01705236",
			        "cashAmount": 0.0,
			        "cashierBankCode": "",
			        "cashierType": "",
			        "discountAmount": 0.0,
			        "fineAmount": 0.0,
			        "interestAmount": 0.0,
			        "name": "Humberto EI",
			        "nominalAmount": 1000.0,
			        "reconciliationId": "12345",
			        "reductionAmount": 0.0,
			        "status": "created",
			        "taxId": "27.564.801/0001-36"
			    },
			    "id": "00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A"
			})
		 */
		// retorno pix qr code
		/*
		 * {
		    "scheduled": "2024-03-28",
		    "type": "brcode-payment",
		    "payment": {
		      "accountType": "payment",
		      "allowChange": false,
		      "amount": 800.0,
		      "bankCode": "10573521",
		      "cashAmount": 0.0,
		      "cashierBankCode": "",
		      "cashierType": "",
		      "description": "teste",
		      "discountAmount": 0.0,
		      "fineAmount": 0.0,
		      "interestAmount": 0.0,
		      "keyId": "10743112644",
		      "name": "Lucas Leal Araujo Murta Rezende",
		      "nominalAmount": 800.0,
		      "reconciliationId": "p8b96",
		      "reductionAmount": 0.0,
		      "status": "created",
		      "taxId": "***.431.126-**"
		    },
		    "id": "00020126500014br.gov.bcb.pix0111107431126440213presente Jana52040000530398654048.005802BR5924Lucas Leal Araujo Murta 6008Brasilia62090505p8b966304386D"
		  }
		 */
	}
	
	public static DictKey dictKey(String idPagamento) {
		DictKey dictKey = null;
		loginStarkBank();
		try {
			dictKey = DictKey.get(idPagamento);
			if(!CommonsUtil.semValor(dictKey)) {
				return dictKey;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		/*
			
		 */
	}
	
	public String paymentBoletoLog(String idBoleto) {
		
		loginStarkBank();
		
		String retorno = "";
	
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("paymentIds", idBoleto);
			Generator<BoletoPayment.Log> logs = BoletoPayment.Log.query(params);
			
			for (BoletoPayment.Log log : logs){
			    if (log.type.equals("failed")) {
			    	for (String erro : log.errors) {
			    		retorno = retorno + erro;
			    	}
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		return retorno;
	}
	
	public void paymentBoletoLog() {
	
		loginStarkBank();
	
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("paymentIds", "4991624148942848");
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
    	
    	
    	paymentBoleto(linhaDigitavel, null, pagador, descricao, null, null);
    	
    	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Boleto Stark Bank - Pago", ""));
    	
    }
    
	public StarkBankTax paymentTax(String barCode, String descricao) {

		FacesContext context = FacesContext.getCurrentInstance();
		StarkBankTax taxTransacao = null;
    	String[] tags = {""};
    	
    	loginStarkBank();

    	Settings.language = "pt-BR";

    	try {
    		
    		List<TaxPayment> payments = new ArrayList<>();

    		HashMap<String, Object> data = new HashMap<>();
    		data.put("line", barCode.replace(" ", "").replace("-", ""));
    		data.put("description", descricao);
    		//data.put("tags", new String[]{"take", "my", "money"});
    		data.put("scheduled", DateUtil.getDataHojeAmericano());

    		payments.add(new TaxPayment(data));

    		payments = TaxPayment.create(payments);

	    	for (TaxPayment payment : payments){
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
	    
	    		taxTransacao = new StarkBankTax(Long.valueOf(payment.id), BigDecimal.valueOf(Long.parseLong(payment.amount)), tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.status, DateUtil.convertDateTimeToDate(payment.created), null, null);
	    		
	    		StarkBankTaxDAO starkBankTaxDAO = new StarkBankTaxDAO();
	    		starkBankTaxDAO.create(taxTransacao);
	    		
	    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[StarkBank - Pagamento Imposto] Imposto Pago com Sucesso! Transação: " + payment.id, ""));
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
						"[StarkBank - Pagamento Imposto] Falha no pagamento: " + errosStrStarkBank, ""));
			}
		}
    	
    	 return taxTransacao;
    }
	
	public void getDadosDePagamento() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		loginStarkBank();
		
		List<PaymentPreview> previews = new ArrayList<>();
		
		HashMap<String, Object> data = new HashMap<>();
		data.put("id", "34191.09008 71884.170946 05303.220007 3 96520000208889");
		data.put("scheduled", DateUtil.getDataHojeAmericano());

		try {
			
			previews.add(new PaymentPreview(data));	
			previews = (List<PaymentPreview>) PaymentPreview.create(previews);
			
			for (PaymentPreview preview : previews) {
			    System.out.println(preview);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public StarkBankBoleto paymentBoleto(String boleto, ContratoCobranca contrato, PagadorRecebedor pessoa, String descricao, String documentoPagadorCustom, String descricaoConta) {

		FacesContext context = FacesContext.getCurrentInstance();
    	StarkBankBoleto boletoTransacao = null;
    	String[] tags = {""};
    	
    	if (descricaoConta == null || !descricaoConta.equals("Pagamento Carta Split")) {
    		loginStarkBank();
    	} else {
    		loginStarkBankSCD();
    	}
    	
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
	    		
	    		boletoTransacao = new StarkBankBoleto(Long.valueOf(payment.id), BigDecimal.valueOf(payment.amount), payment.taxId, tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.fee, payment.status, DateUtil.convertDateTimeToDate(payment.created), null, null);
	    		
	    		StarkBankBoletoDAO starkBankBoletoDAO = new StarkBankBoletoDAO();
	    		starkBankBoletoDAO.create(boletoTransacao);
	    		
	    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[StarkBank - Pagamento Boleto] Boleto Pago com Sucesso! Transação: " + payment.id, ""));
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
	
	public static void loginStarkBank() {
		System.out.println("processaPagamentoStarkBank - Login Correspondente");
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
	
	public static void loginStarkBankSCD() {
		System.out.println("processaPagamentoStarkBank - Login SCD");
		
    	Settings.language = "pt-BR";
    	
    	String privateKeyContent = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIE56ofDNqzdDCrYEAnwpq2CiEfknPhBV+NTWBtpYEy90oAcGBSuBBAAK\noUQDQgAEsXCtulqc9kC5Tkmy/wuJ6JIq8R+GWJzlqmp/pO4r4i76BFivs4hVBZrS\nD5Sil3MxCjUjKbr95ZxDjuq4dYCBOA==\n-----END EC PRIVATE KEY-----";
    	
    	Project project;
		try {
			project = new Project(
				    "production",
				    //"5465772415516672", // financas
				    //"5092647223951360", // coban
				    "6562009682280448",
				    privateKeyContent
				);
			
	    	Settings.user = project;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
    
    public static void transfer() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
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
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX/TED! Erro: " + e, ""));
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }   
    
    public StarkBankPix paymentPixQRCode(String qrCode, String documento, BigDecimal valor, String descricaoConta) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<BrcodePayment> payments = new ArrayList<>();
    
    	if (descricaoConta != null && descricaoConta.contains("Pagamento Carta Split")) {
    		loginStarkBankSCD();
    	} else {
    		loginStarkBank();    		
    	}
    	
    	Date dataHoje = DateUtil.gerarDataHoje();
    	
    	try {
    		
    		List<BrcodePayment.Rule> rules = new ArrayList<>();
    		rules.add(new BrcodePayment.Rule("resendingLimit", 5));
	    	
	    	HashMap<String, Object> data = new HashMap<>();
	    	String valorStr = valor.toString();
	    	data.put("amount", Long.valueOf(valorStr.replace(".", "").replace(",", "")));	
	    	data.put("brcode", qrCode);
	    	data.put("taxId", documento);
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    	data.put("scheduled", sdf.format(DateUtil.gerarDataHoje()));
	    	data.put("description", descricaoConta);
	    	data.put("tags", new String[]{"pix", "qrcode"});
	    	data.put("rules", rules);
	    	payments.add(new BrcodePayment(data));
	    	
	    	payments = BrcodePayment.create(payments);

			StarkBankPix pixTransacao = new StarkBankPix();
	    	for (BrcodePayment transfer : payments){
				 pixTransacao.setId(Long.valueOf(transfer.id));
				 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
				 pixTransacao.setScheduled(transfer.scheduled);
				 pixTransacao.setNomeComprovante(transfer.name);
				 pixTransacao.setAmount(valor);
				 pixTransacao.setTaxId(documento);
				 pixTransacao.setPathComprovante(null);
				 pixTransacao.setNomeComprovante(null);
	    	}
	    	
	    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(pixTransacao);
	    	
	    	context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "StarkBank PIX: Pagamento efetuado com sucesso!", ""));
	    	
	    	return pixTransacao;
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX! Erro: " + e.getMessage(), "")); 
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public StarkBankPix paymentPixCodigo(String codigoPixBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor, String tipoOperacao, String descricaoConta, String tipoContaBancaria) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	if (descricaoConta != null && descricaoConta.contains("Pagamento Carta Split")) {
    		loginStarkBankSCD();
    	} else {
    		loginStarkBank();    		
    	}
    	
    	Date dataHoje = DateUtil.gerarDataHoje();
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	    	
	    	// get dados chave PIX
			DictKey dictKey = DictKey.get(codigoPixBanco);
			
			if (dictKey != null && !dictKey.ispb.equals("")) {
		    	HashMap<String, Object> data = new HashMap<>();
		    	String valorStr = valor.toString();
		    	data.put("amount", Long.valueOf(valorStr.replace(".", "").replace(",", "")));		    	 
		    	data.put("bankCode", dictKey.ispb);
		    	data.put("branchCode", dictKey.branchCode);
		    	data.put("accountNumber", dictKey.accountNumber);
		    	data.put("taxId", documento);
		    	data.put("name", dictKey.name);
		    	data.put("externalId", "PagamentoPix-" + dictKey.name.replace(" ", "-") + "-" + DateUtil.todayInMilli());
		    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    	data.put("scheduled", sdf.format(DateUtil.gerarDataHoje()));
		    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
		    	//data.put("rules", rules);
		    	
		    	if (tipoContaBancaria != null) {
		    		if (!tipoContaBancaria.equals("") && tipoContaBancaria.equals("Conta Poupança")) {
		    			data.put("accountType", "savings");
		    		}
		    	}
		    	
				transfers.add(new Transfer(data));
				
				transfers = Transfer.create(transfers);
	
				StarkBankPix pixTransacao = new StarkBankPix();
		    	for (Transfer transfer : transfers){
					 pixTransacao.setId(Long.valueOf(transfer.id));
					 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
					 pixTransacao.setScheduled(transfer.scheduled);
					 pixTransacao.setNomeComprovante(nomeBeneficiario);
					 pixTransacao.setAmount(valor);
					 pixTransacao.setTaxId(documento);
					 pixTransacao.setPathComprovante(null);
					 pixTransacao.setNomeComprovante(null);
		    	}
		    	
		    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
		    	starkBankPixDAO.create(pixTransacao);
		    	
		    	context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "StarkBank PIX: Pagamento efetuado com sucesso!", ""));
		    	
		    	return pixTransacao;
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX com a chave: " + codigoPixBanco + "!", ""));
				
				return null;
			}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX! Erro: " + e.getMessage(), "")); 
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public StarkBankPix paymentPixDadosBancarios(String ispb, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor, String tipoOperacao, String descricaoConta, String tipoContaBancaria) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	if (descricaoConta != null && descricaoConta.contains("Pagamento Carta Split")) {
    		loginStarkBankSCD();
    	} else {
    		loginStarkBank();    		
    	}
    	
    	Date dataHoje = DateUtil.gerarDataHoje();
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	
	    	HashMap<String, Object> data = new HashMap<>();
	    	String valorStr = valor.toString();
	    	data.put("amount", Long.valueOf(valorStr.replace(".", "").replace(",", "")));	
	    	data.put("bankCode", ispb);
	    	data.put("branchCode", agencia);
	    	data.put("accountNumber", numeroConta);
	    	data.put("taxId", documento);
	    	data.put("name", nomeBeneficiario);
	    	data.put("externalId", "PagamentoPIXDadosBancarios-" + DateUtil.todayInMilli());
	    	//data.put("scheduled", "2020-08-14");
	    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
	    	//data.put("rules", rules);
	    	
	    	if (tipoContaBancaria != null) {
	    		if (!tipoContaBancaria.equals("") && tipoContaBancaria.equals("Conta Poupança")) {
	    			data.put("accountType", "savings");
	    		}
	    	}
	    	
	    	System.out.println("Payment | " + "PagamentoPIXDadosBancarios-" + DateUtil.todayInMilli());
	    	System.out.println("Payment | " + data);
	    	
			transfers.add(new Transfer(data));
			
			transfers = Transfer.create(transfers);

			StarkBankPix pixTransacao = new StarkBankPix();
	    	for (Transfer transfer : transfers){
				 pixTransacao.setId(Long.valueOf(transfer.id));
				 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
				 pixTransacao.setScheduled(transfer.scheduled);
				 pixTransacao.setNomeComprovante(nomeBeneficiario);
				 pixTransacao.setAmount(valor);
				 pixTransacao.setTaxId(documento);
				 pixTransacao.setPathComprovante(null);
				 pixTransacao.setNomeComprovante(null);
	    	}
	    	
	    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(pixTransacao);
	    	
	    	context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "StarkBank PIX (Dados Bancários): Pagamento efetuado com sucesso!", ""));
	    	
	    	return pixTransacao;
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank PIX (Dados Bancários): Ocorreu um problema ao fazer TED! Erro: " + e, ""));
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    	
    public StarkBankPix paymentTED(String codigoBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor, String tipoOperacao, String descricaoConta, String tipoContaBancaria) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	if (descricaoConta != null && descricaoConta.contains("Pagamento Carta Split")) {
    		loginStarkBankSCD();
    	} else {
    		loginStarkBank();    		
    	}
    	
    	Date dataHoje = DateUtil.gerarDataHoje();
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	
	    	HashMap<String, Object> data = new HashMap<>();
	    	String valorStr = valor.toString();
	    	data.put("amount", Long.valueOf(valorStr.replace(".", "").replace(",", "")));	
	    	data.put("bankCode", codigoBanco);
	    	data.put("branchCode", agencia);
	    	data.put("accountNumber", numeroConta);
	    	data.put("taxId", documento);
	    	data.put("name", nomeBeneficiario);
	    	data.put("externalId", "PagamentoTED-" + nomeBeneficiario.replace(" ", "") + DateUtil.todayInMilli());
	    	//data.put("scheduled", "2020-08-14");
	    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
	    	//data.put("rules", rules);
	    	
	    	System.out.println("Payment TED Payload - " + data);
	    	
			transfers.add(new Transfer(data));
			
			transfers = Transfer.create(transfers);

			StarkBankPix pixTransacao = new StarkBankPix();
	    	for (Transfer transfer : transfers){
				 pixTransacao.setId(Long.valueOf(transfer.id));
				 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
				 pixTransacao.setScheduled(transfer.scheduled);
				 pixTransacao.setNomeComprovante(nomeBeneficiario);
				 pixTransacao.setAmount(valor);
				 pixTransacao.setTaxId(documento);
				 pixTransacao.setPathComprovante(null);
				 pixTransacao.setNomeComprovante(null);
	    	}
	    	
	    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(pixTransacao);
	    	
	    	context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "StarkBank TED: Pagamento efetuado com sucesso!", ""));
	    	
	    	return pixTransacao;
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank TED: Ocorreu um problema ao fazer TED! Erro: " + e, ""));
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public StarkBankPix paymentPixTest(String codigoPixBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor) {
    	StarkBankPix starkBankPix = new StarkBankPix();
    	
    	starkBankPix.setId(212);
    	starkBankPix.setCreated(DateUtil.gerarDataHoje());
    	starkBankPix.setScheduled("sadsad");
    	starkBankPix.setNomeComprovante("nome");
    	starkBankPix.setAmount(BigDecimal.TEN);
    	starkBankPix.setTaxId("docu");
    	starkBankPix.setPathComprovante(null);
    	 starkBankPix.setNomeComprovante(null);
    	 
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

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}
	
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public List<StarkBankBoleto> getListBoletos() {
		return listBoletos;
	}

	public void setListBoletos(List<StarkBankBoleto> listBoletos) {
		this.listBoletos = listBoletos;
	}

	public StarkBankBaixa getContaIndividual() {
		return contaIndividual;
	}

	public void setContaIndividual(StarkBankBaixa contaIndividual) {
		this.contaIndividual = contaIndividual;
	}

	public StarkBankBaixa getSelectedContaIndividual() {
		return selectedContaIndividual;
	}

	public void setSelectedContaIndividual(StarkBankBaixa selectedContaIndividual) {
		this.selectedContaIndividual = selectedContaIndividual;
	}

	public List<StarkBankBaixa> getContasIndividual() {
		return contasIndividual;
	}

	public void setContasIndividual(List<StarkBankBaixa> contasIndividual) {
		this.contasIndividual = contasIndividual;
	}

	public String getNomeComprovanteStarkBank() {
		return nomeComprovanteStarkBank;
	}

	public void setNomeComprovanteStarkBank(String nomeComprovanteStarkBank) {
		this.nomeComprovanteStarkBank = nomeComprovanteStarkBank;
	}

	public String getPathComprovanteStarkBank() {
		return pathComprovanteStarkBank;
	}

	public void setPathComprovanteStarkBank(String pathComprovanteStarkBank) {
		this.pathComprovanteStarkBank = pathComprovanteStarkBank;
	}

	public StreamedContent getDownloadComprovanteStarkBank() {
		return downloadComprovanteStarkBank;
	}

	public void setDownloadComprovanteStarkBank(StreamedContent downloadComprovanteStarkBank) {
		this.downloadComprovanteStarkBank = downloadComprovanteStarkBank;
	}

	public boolean isComprovanteStarkBankGerado() {
		return comprovanteStarkBankGerado;
	}

	public void setComprovanteStarkBankGerado(boolean comprovanteStarkBankGerado) {
		this.comprovanteStarkBankGerado = comprovanteStarkBankGerado;
	}
}