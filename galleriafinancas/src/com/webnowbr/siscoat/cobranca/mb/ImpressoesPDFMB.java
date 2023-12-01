package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.model.request.FichaIndividualRequest;
import com.webnowbr.siscoat.cobranca.service.RelatoriosService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


/** ManagedBean. */
@ManagedBean(name = "impressoesPDFMB")
@SessionScoped
public class ImpressoesPDFMB {

	private boolean pdfCadastroPessoaFisicaGerado;
	private String pathPDF;
	
	private String nomePDF;
	private StreamedContent file;
	
	private String nomePDFConjuge;
	private StreamedContent fileConjuge;
	private boolean pdfCadastroPessoaFisicaConjugeGerado;
	
	private ContratoCobranca objetoContratoCobranca;
	
	/**+
	 * PDF Ficha Cadastral
	 */
	private boolean tipoPessoaIsFisica;
	private String nome;
	private String documento;
	private String email;
	private String telefone;
	private String origemChamada;
	
	/**
	 * Construtor.
	 */
	public ImpressoesPDFMB() {

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
	
	public void selectedTipoPessoaPublico() {
		this.documento = "";
	}
	
	
	public void clearFieldsFichaIndividual() {
		this.tipoPessoaIsFisica = true;
		this.nome = "";
		this.documento = "";
		this.email = "";
		this.telefone = "";
		this.origemChamada = "FichaIndividual";
		this.pdfCadastroPessoaFisicaConjugeGerado = false;
		this.pdfCadastroPessoaFisicaGerado = false;		
	}
	
	public ContratoCobranca getContratoById(long idContrato) {
		ContratoCobranca contrato = new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
		contrato = cDao.findById(idContrato);
		
		return contrato;
	}
	
	/**
	 * GERA CONTRATO DE PESSOA FISICA
	 */
	
	public void geraPdfCadastroPessoaFisica() {
		if (this.objetoContratoCobranca != null) {
			if (this.objetoContratoCobranca.getId() > 0) {
				this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());		
			}
		}		
		
		if (this.origemChamada.equals("FichaContrato")) {
			this.nome = this.objetoContratoCobranca.getPagador().getNome();
			if(!CommonsUtil.semValor(this.objetoContratoCobranca.getPagador().getCpf())) {
				this.documento = this.objetoContratoCobranca.getPagador().getCpf();
				this.tipoPessoaIsFisica = true;
			} else {
				this.documento = this.objetoContratoCobranca.getPagador().getCnpj();
				this.tipoPessoaIsFisica = false;
			}
			this.email = this.objetoContratoCobranca.getPagador().getEmail();
			this.telefone = this.objetoContratoCobranca.getPagador().getTelCelular();
		}
		
		
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		OutputStream os = null;
		byte[] relatorioByte = null;

		try {

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			if (this.nome.contains("/")) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: Favor REMOVER '/' do campo NOME", ""));
				return;
			}

			this.nomePDF = "Ficha Cadastral Pessoa Física - " + this.nome + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();

			RelatoriosService relatorioService = new RelatoriosService();

//			doc = new Document(PageSize.A4, 10, 10, 10, 10);

//			os = new FileOutputStream(this.pathPDF + this.nomePDF);
			relatorioByte = relatorioService.geraPdfFichaIndividual(
					new FichaIndividualRequest(this.origemChamada, this.tipoPessoaIsFisica, this.nome, this.documento));
//			os.write(relatorioByte);
//			try {
//				os.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());

			gerador.open(this.nomePDF);
			gerador.feed(new ByteArrayInputStream(relatorioByte));
			gerador.close();

			if (this.objetoContratoCobranca != null) {
				if (this.objetoContratoCobranca.getPagador().getEstadocivil() != null
						&& this.objetoContratoCobranca.getPagador().getEstadocivil().equals("CASADO")
						&& this.origemChamada.equals("FichaContrato")) {
					if (this.origemChamada.equals("FichaContrato")) {

						this.nome = this.objetoContratoCobranca.getPagador().getNomeConjuge();
						this.documento = this.objetoContratoCobranca.getPagador().getCpfConjuge();
						this.email = this.objetoContratoCobranca.getPagador().getEmailConjuge();
						this.telefone = this.objetoContratoCobranca.getPagador().getTelCelularConjuge();

						this.nomePDFConjuge = "Ficha Cadastral Pessoa Física - " + this.nome + ".pdf";
						this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();

//						os = new FileOutputStream(this.pathPDF + this.nome);

						relatorioByte = relatorioService.geraPdfFichaIndividual(new FichaIndividualRequest(
								this.origemChamada, this.tipoPessoaIsFisica, this.nome, this.documento));

						final GeradorRelatorioDownloadCliente geradorConjugue = new GeradorRelatorioDownloadCliente(
								FacesContext.getCurrentInstance());

						geradorConjugue.open(this.nomePDFConjuge);
						geradorConjugue.feed(new ByteArrayInputStream(relatorioByte));
						geradorConjugue.close();

					}
				}
			}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Ocorreu um problema ao gerar o PDF!" + e, ""));
		}
		
		this.pdfCadastroPessoaFisicaGerado = true;

	}
	
	
	public byte[] geraPdfCadastroPagadorRecebedor(PagadorRecebedor pagador) throws IOException {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");
		FacesContext context = FacesContext.getCurrentInstance();		
		Document doc = null;
		
		String nome = pagador.getNome();
		String documento;
		boolean tipoPessoaIsFisica = true;
		String email = pagador.getEmail();
		String telefone = pagador.getTelCelular();
		
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			documento = pagador.getCpf();
			tipoPessoaIsFisica = true;
		} else {
			documento = pagador.getCnpj();
			tipoPessoaIsFisica = false;
		}
		
		if(nome.contains("/")) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro: Favor REMOVER '/' do campo NOME", ""));
			return null;
		}
		
		try {
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
				
			String nomePDF = "Ficha Cadastral - " + nome + ".pdf";

			ByteArrayOutputStream out  = new ByteArrayOutputStream();  	
			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			PdfWriter.getInstance(doc, out);
			
			doc.open();     			
	
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
			if (tipoPessoaIsFisica) {
				cell1 = new PdfPCell(new Phrase("Ficha Cadastral Pessoa Física", headerFull));	
			} else {
				cell1 = new PdfPCell(new Phrase("Ficha Cadastral Pessoa Jurídica", headerFull));
			}		
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

			if (tipoPessoaIsFisica) {
				cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
			} else {
				cell1 = new PdfPCell(new Phrase("RAZÃO SOCIAL", tituloSmall));
			}
			
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
			
			cell1 = new PdfPCell(new Phrase(nome, normal));
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
			
			cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
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
			
			cell1 = new PdfPCell(new Phrase(email, normal));
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
			cell1.setColspan(6);
			table.addCell(cell1);
			if (tipoPessoaIsFisica) {
				cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
			} else {
				cell1 = new PdfPCell(new Phrase("CNPJ", tituloSmall));
			}
			
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
			
			cell1 = new PdfPCell(new Phrase(documento, normal));
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
			
			cell1 = new PdfPCell(new Phrase("CELULAR", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
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
			
			cell1 = new PdfPCell(new Phrase(telefone, normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
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
			
			doc.close();
			
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format(nomePDF, "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			
			return out.toByteArray();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF Contrato de Pessoa Fisica: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} catch (Throwable e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public byte[] geraPdfCadastroPagadorRecebedorNovo(PagadorRecebedor pagador) throws IOException {
		RelatoriosService relatorioService = new RelatoriosService();
		byte[] relatorioByte = null;
		if(!CommonsUtil.semValor(pagador.getCpf())) 
			this.tipoPessoaIsFisica = true;
		else
			this.tipoPessoaIsFisica = false;
		relatorioByte = relatorioService.geraPdfFichaIndividual(
			new FichaIndividualRequest("FichaIndividual",
					this.tipoPessoaIsFisica, pagador.getNome(), pagador.getCpfCnpj()));
		return relatorioByte;
	}
	/**
	 * GERA CONTRATO DE PESSOA FISICA - CONJUGE COMO PRINCIPAL
	 */
	
	public void geraPdfCadastroPessoaFisicaConjugePrincipal() {
		
		if (this.origemChamada.equals("FichaContrato")) {
			this.nome = this.objetoContratoCobranca.getPagador().getNomeConjuge();
			this.documento = this.objetoContratoCobranca.getPagador().getCpfConjuge();
			this.email = this.objetoContratoCobranca.getPagador().getEmailConjuge();
			this.telefone = this.objetoContratoCobranca.getPagador().getTelCelularConjuge();
		}
		
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
			this.nomePDFConjuge = "Ficha Cadastral Pessoa Física - " + this.nome + ".pdf";
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
			cell1.setColspan(6);
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
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("EMAIL", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
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
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getEmailConjuge(), normal));
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
			cell1.setColspan(6);
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
			cell1.setColspan(6);
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
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CELULAR", tituloSmall));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
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
			
			cell1 = new PdfPCell(new Phrase(this.objetoContratoCobranca.getPagador().getTelCelularConjuge(), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
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

	public boolean isTipoPessoaIsFisica() {
		return tipoPessoaIsFisica;
	}

	public void setTipoPessoaIsFisica(boolean tipoPessoaIsFisica) {
		this.tipoPessoaIsFisica = tipoPessoaIsFisica;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getOrigemChamada() {
		return origemChamada;
	}

	public void setOrigemChamada(String origemChamada) {
		this.origemChamada = origemChamada;
	}
}
