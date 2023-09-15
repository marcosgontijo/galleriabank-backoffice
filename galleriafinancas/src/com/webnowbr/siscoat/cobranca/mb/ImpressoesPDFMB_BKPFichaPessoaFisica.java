package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

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
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


/** ManagedBean. */
public class ImpressoesPDFMB_BKPFichaPessoaFisica {

	private boolean pdfCadastroPessoaFisicaGerado;
	private String pathPDF;
	
	private String nomePDF;
	private StreamedContent file;
	
	private String nomePDFConjuge;
	private StreamedContent fileConjuge;
	private boolean pdfCadastroPessoaFisicaConjugeGerado;
	
	private ContratoCobranca objetoContratoCobranca;
	
	/**
	 * Construtor.
	 */
	public ImpressoesPDFMB_BKPFichaPessoaFisica() {

	}
	
	public void clearPdfCadastroPessoaFisica() {
		this.pdfCadastroPessoaFisicaGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		this.fileConjuge = null;
		this.nomePDFConjuge = "";
		this.pdfCadastroPessoaFisicaConjugeGerado = false;
	}
	
	/**
	 * GERA CONTRATO DE PESSOA FISICA
	 */
	
	public void geraPdfCadastroPessoaFisica() {
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
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 7);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao(); 
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Ficha Cadastral Pessoa Física - " + this.objetoContratoCobranca.getPagador().getNome() + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();

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
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 

			BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/logocadastrosbanksmall.png"));
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(buff, "png", bos);
	        Image img = Image.getInstance(bos.toByteArray());
	        
			img.setAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(img);
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Ficha Cadastral Pessoa Física", headerFull));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("IDENTIFICAÇÃO DO CLIENTE", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			/*
			 * LINHA 1
			 */
			cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(4);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getNome(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(4);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmail(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			/*
			 * LINHA 2
			 */
			cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("RG / CNH", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DATA EMISSÃO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("ESTADO CIVIL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("SEXO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCpf(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getRg(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (this.objetoContratoCobranca.getPagador().getDataEmissaoRG() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDataEmissaoRG()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstadocivil(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getSexo(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);	
			
			/*
			 * LINHA 5
			 */
			cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			String endereco = ""; 
			if (this.objetoContratoCobranca.getPagador().getNumero() != null) {
				if (!this.objetoContratoCobranca.getPagador().getNumero().equals("")) {
					endereco = this.objetoContratoCobranca.getPagador().getEndereco() + ", " + this.objetoContratoCobranca.getPagador().getNumero();
				}
			} else {
				endereco = this.objetoContratoCobranca.getPagador().getEndereco();
			}
			
			cell1 = new PdfPCell(new Phrase(endereco + " - " + this.objetoContratoCobranca.getPagador().getComplemento(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			/*
			 * LINHA 6
			 */
			cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBairro(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCidade(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstado(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCep(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);	
			
			/*
			 * LINHA 7
			 */
			cell1 = new PdfPCell(new Phrase("TELEFONE", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CELULAR", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DATA NASCIMENTO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelResidencial(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelCelular(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			if (this.objetoContratoCobranca.getPagador().getDtNascimento() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDtNascimento()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);	
			
			cell1 = new PdfPCell(new Phrase("CARGO / OCUPAÇÃO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getAtividade(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			if (this.objetoContratoCobranca.getPagador().getEstadocivil() != null && this.objetoContratoCobranca.getPagador().getEstadocivil().equals("CASADO")) {
				
				geraPdfCadastroPessoaFisicaConjugePrincipal();
				
				cell1 = new PdfPCell(new Phrase("DADOS DO CÔNJUGE", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);		
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);				
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getNomeConjuge(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmailConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("RG / CNH", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("DATA EMISSÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("SEXO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCpfConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getRgConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				if (this.objetoContratoCobranca.getPagador().getDataEmissaoRGConjuge() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDataEmissaoRGConjuge()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("--", normal));
				}
								
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getSexoConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEnderecoConjuge() + " - " + this.objetoContratoCobranca.getPagador().getComplementoConjuge(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBairroConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCidadeConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstadoConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCepConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);		
				
				cell1 = new PdfPCell(new Phrase("TELEFONE", tituloSmall));
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
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CELULAR", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("DATA NASCIMENTO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelResidencialConjuge(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelCelularConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				if (this.objetoContratoCobranca.getPagador().getDtNascimentoConjuge() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDtNascimentoConjuge()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("", normal));
				}
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);					
				
				cell1 = new PdfPCell(new Phrase("CARGO / OCUPAÇÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCargoConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(6);
				table.addCell(cell1);
			}
			
			if (this.objetoContratoCobranca.getPagador().isCoobrigado()) {
				cell1 = new PdfPCell(new Phrase("DADOS DO COOBRIGADO", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);		
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getNomeCoobrigado(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmailCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
	
				cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("RG / CNH", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("DATA EMISSÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
	
				cell1 = new PdfPCell(new Phrase("CARGO / OCUPAÇÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCpfCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getRgCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				if (this.objetoContratoCobranca.getPagador().getDataEmissaoRGCoobrigado() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDataEmissaoRGCoobrigado()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("--", normal));
				}
								
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCargoCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEnderecoCoobrigado() + " - " + this.objetoContratoCobranca.getPagador().getComplementoCoobrigado(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBairroCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCidadeCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstadoCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCepConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);					
			}
			
			cell1 = new PdfPCell(new Phrase("BANCOS", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			/*
			 * LINHA 1
			 */
			cell1 = new PdfPCell(new Phrase("INSTITUIÇÃO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("AGÊNCIA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CONTA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);		
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBanco(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getAgencia(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getConta(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);		
			
			cell1 = new PdfPCell(new Phrase("DADOS DA OPERAÇÃO", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("BEM DADO EM GARANTIA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("NÚMERO MATRÍCULA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CARTÓRIO DO IMÓVEL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getTipo(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getNumeroMatricula(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getCartorio(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			/*
			 * LINHA 5
			 */
			cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("POSSUI DÍVIDA?", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getEndereco() + " - " + this.objetoContratoCobranca.getImovel().getComplemento(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getPossuiDivida(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			/*
			 * LINHA 6
			 */
			cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getBairro(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getCidade(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getEstado(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getCep(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);	

			//PULA LINHA
			cell1 = new PdfPCell(new Phrase("", titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DECLARAÇÃO", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			
			cell1 = new PdfPCell(new Phrase("Declaramos, sob as penas da lei, que as informações prestadas são verdadeiras, e comprometo-me a informar, no prazo de 10 (dez) dias, quaisquer alterações que vierem a ocorrer nos meus dados cadastrais, bem como autorizamos a GALLERIA SOCIEDADE DE CRÉDITO DIRETO S/A. a consultar as fontes de referência indicadas (clientes, fornecedores e bancos) e inserir e solicitar informações relacionadas com nossa empresa e coligadas, junto ao mercado financeiro e entidades cadastrais em geral, inclusive junto ao SCR-Sistema de Informações de Crédito do Banco Central do Brasil (Res. 3.658 do Conselho Monetário Nacional), SERASA ou qualquer outro órgão ou entidade assemelhada.", normal));
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
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("_____________________________________________________________________", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(30f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("ASSINATURA", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Ocorreu um problema ao gerar o PDF!" + e, ""));
		}
		
		this.pdfCadastroPessoaFisicaGerado = true;

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
	
	/**
	 * GERA CONTRATO DE PESSOA FISICA - CONJUGE COMO PRINCIPAL
	 */
	
	public void geraPdfCadastroPessoaFisicaConjugePrincipal() {
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
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 7);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao(); 
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDFConjuge = "Ficha Cadastral Pessoa Física - " + this.objetoContratoCobranca.getPagador().getNomeConjuge() + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDFConjuge);  	

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
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 

			BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/logocadastrosbanksmall.png"));
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(buff, "png", bos);
	        Image img = Image.getInstance(bos.toByteArray());
	        
			img.setAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(img);
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Ficha Cadastral Pessoa Física", headerFull));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("IDENTIFICAÇÃO DO CLIENTE", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			/*
			 * LINHA 1
			 */
			cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(4);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getNomeConjuge(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(4);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmailConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			/*
			 * LINHA 2
			 */
			cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("RG / CNH", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DATA EMISSÃO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("ESTADO CIVIL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("SEXO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCpfConjuge(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getRgConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (this.objetoContratoCobranca.getPagador().getDataEmissaoRGConjuge() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDataEmissaoRGConjuge()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CASADO", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getSexoConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);	
			
			/*
			 * LINHA 5
			 */
			cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEnderecoConjuge() + " - " + this.objetoContratoCobranca.getPagador().getComplementoConjuge(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			/*
			 * LINHA 6
			 */
			cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBairroConjuge(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCidadeConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstadoConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCepConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);	
			
			/*
			 * LINHA 7
			 */
			cell1 = new PdfPCell(new Phrase("TELEFONE", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CELULAR", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DATA NASCIMENTO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelResidencialConjuge(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelCelularConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			if (this.objetoContratoCobranca.getPagador().getDtNascimentoConjuge() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDtNascimentoConjuge()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);	
			
			cell1 = new PdfPCell(new Phrase("CARGO / OCUPAÇÃO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCargoConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			if (this.objetoContratoCobranca.getPagador().getEstadocivil() != null && this.objetoContratoCobranca.getPagador().getEstadocivil().equals("CASADO")) {
				cell1 = new PdfPCell(new Phrase("DADOS DO CÔNJUGE", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);		
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);				
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getNome(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmail(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("RG / CNH", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("DATA EMISSÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("SEXO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCpf(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getRg(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				if (this.objetoContratoCobranca.getPagador().getDataEmissaoRG() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDataEmissaoRG()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("--", normal));
				}
								
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getSexo(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				String endereco = ""; 
				if (this.objetoContratoCobranca.getPagador().getNumero() != null) {
					if (!this.objetoContratoCobranca.getPagador().getNumero().equals("")) {
						endereco = this.objetoContratoCobranca.getPagador().getEndereco() + ", " + this.objetoContratoCobranca.getPagador().getNumero();
					}
				} else {
					endereco = this.objetoContratoCobranca.getPagador().getEndereco();
				}

				cell1 = new PdfPCell(new Phrase(endereco + " - " + this.objetoContratoCobranca.getPagador().getComplemento(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBairro(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCidade(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCep(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);		
				
				cell1 = new PdfPCell(new Phrase("TELEFONE", tituloSmall));
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
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CELULAR", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("DATA NASCIMENTO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelResidencial(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelCelular(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				if (this.objetoContratoCobranca.getPagador().getDtNascimento() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDtNascimento()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("", normal));
				}
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);					
				
				cell1 = new PdfPCell(new Phrase("CARGO / OCUPAÇÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getAtividade(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(6);
				table.addCell(cell1);
			}
			
			if (this.objetoContratoCobranca.getPagador().isCoobrigado()) {
				cell1 = new PdfPCell(new Phrase("DADOS DO COOBRIGADO", titulo));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);		
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(6);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getNomeCoobrigado(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmailCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
	
				cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("RG / CNH", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("DATA EMISSÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
	
				cell1 = new PdfPCell(new Phrase("CARGO / OCUPAÇÃO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCpfCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getRgCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				if (this.objetoContratoCobranca.getPagador().getDataEmissaoRGCoobrigado() != null) {
					cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoContratoCobranca.getPagador().getDataEmissaoRGCoobrigado()), normal));
				} else {
					cell1 = new PdfPCell(new Phrase("--", normal));
				}
								
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCargoCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEnderecoCoobrigado() + " - " + this.objetoContratoCobranca.getPagador().getComplementoCoobrigado(), normal));
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
				cell1.setPaddingBottom(5f);
				cell1.setColspan(6);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBairroCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCidadeCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEstadoCoobrigado(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getCepConjuge(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);					
			}
			
			cell1 = new PdfPCell(new Phrase("BANCOS", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			/*
			 * LINHA 1
			 */
			cell1 = new PdfPCell(new Phrase("INSTITUIÇÃO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("AGÊNCIA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CONTA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);		
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getBanco(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getAgencia(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getConta(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);		
			
			cell1 = new PdfPCell(new Phrase("DADOS DA OPERAÇÃO", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("BEM DADO EM GARANTIA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("NÚMERO MATRÍCULA", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CARTÓRIO DO IMÓVEL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getTipo(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getNumeroMatricula(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getCartorio(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			/*
			 * LINHA 5
			 */
			cell1 = new PdfPCell(new Phrase("ENDEREÇO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("POSSUI DÍVIDA?", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getEndereco() + " - " + this.objetoContratoCobranca.getImovel().getComplemento(), normal));
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
			cell1.setPaddingBottom(5f);
			cell1.setColspan(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getPossuiDivida(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			/*
			 * LINHA 6
			 */
			cell1 = new PdfPCell(new Phrase("BAIRRO", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CIDADE", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("UF", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CEP", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getBairro(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getCidade(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getEstado(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getImovel().getCep(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);	

			//PULA LINHA
			cell1 = new PdfPCell(new Phrase("", titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DECLARAÇÃO", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			
			cell1 = new PdfPCell(new Phrase("Declaramos, sob as penas da lei, que as informações prestadas são verdadeiras, e comprometo-me a informar, no prazo de 10 (dez) dias, quaisquer alterações que vierem a ocorrer nos meus dados cadastrais, bem como autorizamos a GALLERIA SOCIEDADE DE CRÉDITO DIRETO S/A. a consultar as fontes de referência indicadas (clientes, fornecedores e bancos) e inserir e solicitar informações relacionadas com nossa empresa e coligadas, junto ao mercado financeiro e entidades cadastrais em geral, inclusive junto ao SCR-Sistema de Informações de Crédito do Banco Central do Brasil (Res. 3.658 do Conselho Monetário Nacional), SERASA ou qualquer outro órgão ou entidade assemelhada.", normal));
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
			cell1.setPaddingTop(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("_____________________________________________________________________", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(30f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("_____________________________________________________________________", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(30f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("LOCAL / DATA", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("ASSINATURA", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(3);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Ocorreu um problema ao gerar o PDF!" + e, ""));
		}
		
		this.pdfCadastroPessoaFisicaConjugeGerado = true;

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
	

	public boolean isPdfCadastroPessoaFisicaGerado() {
		return pdfCadastroPessoaFisicaGerado;
	}

	public void setPdfCadastroPessoaFisicaGerado(boolean pdfCadastroPessoaFisicaGerado) {
		this.pdfCadastroPessoaFisicaGerado = pdfCadastroPessoaFisicaGerado;
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
	
	public StreamedContent getFileConjuge() {
		String caminho =  this.pathPDF + this.nomePDFConjuge;        
		String arquivo = this.nomePDFConjuge;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		fileConjuge = new DefaultStreamedContent(stream, caminho, arquivo); 

		return fileConjuge;  
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public String getNomePDFConjuge() {
		return nomePDFConjuge;
	}

	public void setNomePDFConjuge(String nomePDFConjuge) {
		this.nomePDFConjuge = nomePDFConjuge;
	}

	public void setFileConjuge(StreamedContent fileConjuge) {
		this.fileConjuge = fileConjuge;
	}

	public boolean isPdfCadastroPessoaFisicaConjugeGerado() {
		return pdfCadastroPessoaFisicaConjugeGerado;
	}

	public void setPdfCadastroPessoaFisicaConjugeGerado(boolean pdfCadastroPessoaFisicaConjugeGerado) {
		this.pdfCadastroPessoaFisicaConjugeGerado = pdfCadastroPessoaFisicaConjugeGerado;
	}
}
