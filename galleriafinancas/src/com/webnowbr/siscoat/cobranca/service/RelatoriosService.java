package com.webnowbr.siscoat.cobranca.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

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
import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PreAprovadoPDF;
import com.webnowbr.siscoat.cobranca.db.model.PreAprovadoPDFDetalheDespesas;
import com.webnowbr.siscoat.cobranca.db.model.RegistroImovelTabela;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.RegistroImovelTabelaDao;
import com.webnowbr.siscoat.cobranca.model.request.FichaIndividualRequest;
import com.webnowbr.siscoat.cobranca.model.request.TermoCienciaRequest;
import com.webnowbr.siscoat.cobranca.vo.PlanilhaRestituicaoVO;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.ReportUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.simulador.SimulacaoVO;
import com.webnowbr.siscoat.simulador.SimuladorMB;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;

public class RelatoriosService {

	public JasperPrint geraPDFPAprovadoComite(long idContrato) throws JRException, IOException {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		final ReportUtil ReportUtil = new ReportUtil();
		JasperReport rptSimulacao = ReportUtil.getRelatorio("AprovadoComitePDFN");
		InputStream logoStream = getClass().getResourceAsStream("/resource/novoCreditoAprovado2.png");
		InputStream rodapeStream = getClass().getResourceAsStream("/resource/novoCreditoAprovadoRodape.png");
		InputStream barraStream = getClass().getResourceAsStream("/resource/novoCreditoAprovadoBarra.png");

		JasperReport rptDetalhe = ReportUtil.getRelatorio("AprovadoComitePDFNDetalhe");
		JasperReport rptDetalheParcelas = ReportUtil.getRelatorio("AprovadoComitePDFNParcelas");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

		parameters.put("SUBREPORT_DETALHE_DESPESA", rptDetalhe);
		parameters.put("SUBREPORT_DETALHE_PARCELA", rptDetalheParcelas);
		parameters.put("IMAGEMFUNDO", IOUtils.toByteArray(logoStream));
		parameters.put("IMAGEMRODAPE", IOUtils.toByteArray(rodapeStream));
		parameters.put("IMAGEMBARRA", IOUtils.toByteArray(barraStream));
		parameters.put("MOSTRARIPCA", true);

		List<PreAprovadoPDF> list = new ArrayList<PreAprovadoPDF>();
		ContratoCobranca con = cDao.findById(idContrato);
		
		SimuladorMB simuladorMB = new SimuladorMB();
		simuladorMB.clearFields();
		
		String cpf = "";
		if (!CommonsUtil.semValor(con.getPagador().getCpf())) {
			cpf = con.getPagador().getCpf();
			simuladorMB.setTipoPessoa("PF");
		} else {
			cpf = con.getPagador().getCnpj();
			simuladorMB.setTipoPessoa("PJ");
		}
		simuladorMB.setTipoCalculo("Price");
		simuladorMB.setValorImovel(con.getValorMercadoImovel());
		simuladorMB.setValorCredito(con.getValorAprovadoComite());
		simuladorMB.setTaxaJuros(con.getTaxaAprovada());
		simuladorMB.setParcelas(con.getPrazoAprovadoCCB());
		simuladorMB.setCarencia(BigInteger.ONE);
		simuladorMB.setNaoCalcularMIP(false);
		simuladorMB.setNaoCalcularDFI(false);
		simuladorMB.setNaoCalcularTxAdm(false);
		simuladorMB.setMostrarIPCA(true);
		simuladorMB.setTipoCalculoFinal(con.getTipoValorComite().toUpperCase().charAt(0));
		simuladorMB.setValidar(false);
		simuladorMB.setSimularComIPCA(false);
		simuladorMB.setIpcaSimulado(BigDecimal.ZERO);
		simuladorMB.simular();
		SimulacaoVO simulador = simuladorMB.getSimulacao();

		BigDecimal parcelaPGTO = simulador.getParcelas().get(2).getValorParcela();
		BigDecimal rendaMinima = parcelaPGTO.divide(BigDecimal.valueOf(0.3), MathContext.DECIMAL128);

		String cep = con.getImovel().getCep();
		String observacao = con.getProcessosQuitarComite();

		BigInteger carencia = BigInteger.ONE.add(CommonsUtil.bigIntegerValue(con.getCarenciaComite()))
				.multiply(CommonsUtil.bigIntegerValue(30));
		BigDecimal despesa = BigDecimal.ZERO;

		BigDecimal valorIOF = simulador.getValorTotalIOF().add(simulador.getValorTotalIOFAdicional());

		BigDecimal valorLiquido = BigDecimal.ZERO;
		List<PreAprovadoPDFDetalheDespesas> detalhesDespesas = new ArrayList<>();
		
		detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Dívidas Fiscais", "Se houver"));
		detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Débitos de IPTU", "Se houver"));
		List<String> ImoveisComCondominio = Arrays.asList("Casa de Condomínio", "Apartamento", "Terreno de Condomínio",
				"Terreno", "Sala Comercial");

		if (ImoveisComCondominio.contains(con.getImovel().getTipo()))
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Débitos de Condomínio", "Se houver"));

		int qtdMatriculas = 1;
		String matriculas = con.getImovel().getNumeroMatricula().trim();
		if (matriculas.endsWith(",")) {
			matriculas = matriculas.substring(0, matriculas.lastIndexOf(",")).trim();
		}

		qtdMatriculas = matriculas.split(",").length;
		// se for apartamento é no minimo 3
		if (CommonsUtil.mesmoValorIgnoreCase("Apartamento", con.getImovel().getTipo()) && qtdMatriculas == 1) {
			qtdMatriculas = 3;
		}

		RegistroImovelTabelaDao rDao = new RegistroImovelTabelaDao();
		final BigDecimal valorRegistro = rDao
				.getValorRegistro(simulador.getValorCredito().multiply(CommonsUtil.bigDecimalValue(qtdMatriculas)));

		List<RegistroImovelTabela> registroImovelTabela = rDao.findAll();
		Optional<RegistroImovelTabela> registroImovel = registroImovelTabela.stream()
				.sorted((o1, o2) -> o1.getTotal().compareTo(o2.getTotal()))
				.filter(a -> a.getTotal().compareTo(valorRegistro) == 1).findFirst();

		if (registroImovel.isPresent()) {

			BigDecimal valorRegistroDespesa = registroImovel.get().getTotal();

			// Emprestimo = financiamento
			if (CommonsUtil.mesmoValorIgnoreCase("Emprestimo", con.getTipoOperacao()))
				valorRegistroDespesa = valorRegistroDespesa.multiply(CommonsUtil.bigDecimalValue(2));

			despesa = despesa.add(valorRegistroDespesa);
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Custas Estimadas Para Registro",
					CommonsUtil.formataValorMonetario(valorRegistroDespesa, "R$ ")));
		}

		BigDecimal valorCustoEmissao = simulador.getCustoEmissaoValor();
//		despesa = despesa.add(valorCustoEmissao);
//		detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Custo de Emissão", 
//				CommonsUtil.formataValorMonetario(valorCustoEmissao, "R$ ")));

		if (con.getValorLaudoPajuFaltante().compareTo(BigDecimal.ZERO) > 0) {
			despesa = despesa.add(con.getValorLaudoPajuFaltante());
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Laudo + Parecer Jurídico",
					CommonsUtil.formataValorMonetario(con.getValorLaudoPajuFaltante(), "R$ ")));
		}

		for (CcbProcessosJudiciais processo : con.getListProcessos().stream().filter(p -> p.isSelecionadoComite())// &&
																													// p.getQuitar().contains("Quitar"))
				.collect(Collectors.toList())) {

			String retiraObservaco = processo.getNumero() + " - "
					+ CommonsUtil.formataValorMonetario(processo.getValorAtualizado(), "R$ ") + "\n";
			observacao = observacao.replace(retiraObservaco, "");
			retiraObservaco = processo.getNumero() + " - "
					+ CommonsUtil.formataValorMonetario(processo.getValorAtualizado(), "R$ ") + "\r\n";
			observacao = observacao.replace(retiraObservaco, "");
			despesa = despesa.add(processo.getValorAtualizado());

			
			
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Processo Nº " + processo.getNumero(),
					CommonsUtil.formataValorMonetario(processo.getValorAtualizado(), "R$ ")));
		}

		valorLiquido = simulador.getValorCredito().subtract(valorIOF).subtract(valorCustoEmissao).subtract(despesa);

		// adicionar cep e carencia ( 1 + carencia * 30 )
		PreAprovadoPDF documento = new PreAprovadoPDF(con.getPagador().getNome(),
				(!CommonsUtil.semValor(con.getAprovadoComiteData()) ? con.getAprovadoComiteData()
						: con.getDataContrato()),
				con.getNumeroContrato(), cpf, con.getTaxaAprovada(), observacao, con.getImovel().getCidade(),
				con.getImovel().getNumeroMatricula(), con.getImovel().getEstado(), con.getPrazoAprovadoCCB().toString(),
				simulador.getValorCredito(), con.getValorMercadoImovel(), parcelaPGTO, con.getTipoValorComite(), cep,
				carencia, despesa, valorCustoEmissao, valorIOF, valorLiquido, detalhesDespesas,
				simulador.getParcelas());
		list.add(documento);

		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);
	}

	public byte[] geraPDFPAprovadoComiteByteArray(long idContrato) throws JRException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JasperPrint jp = geraPDFPAprovadoComite(idContrato);

		JasperExportManager.exportReportToPdfStream(jp, bos);

		return bos.toByteArray();
	}

	public byte[] geraPdfFichaIndividual(FichaIndividualRequest fichaIndividualRequest) {

		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
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
//			if (fichaIndividualRequest.getNome().contains("/")) {
//				context.addMessage(null,
//						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: Favor REMOVER '/' do campo NOME", ""));
//				return null;
//			}

			doc = new Document(PageSize.A4, 10, 10, 10, 10);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, baos);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
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

			if (CommonsUtil.booleanValue(fichaIndividualRequest.isTipoPessoaIsFisica())) {
				cell1 = new PdfPCell(new Phrase("Autorização Consulta SCR", headerFull));
			} else {
				cell1 = new PdfPCell(new Phrase("Autorização Consulta SCR", headerFull));
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

			/*
			 * LINHA 1
			 */
			if (fichaIndividualRequest.getOrigemChamada().equals("FichaIndividual")) {
				if (CommonsUtil.booleanValue(fichaIndividualRequest.isTipoPessoaIsFisica())) {
					cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("RAZÃO SOCIAL", tituloSmall));
				}
			} else {
				cell1 = new PdfPCell(new Phrase("NOME", tituloSmall));
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

			cell1 = new PdfPCell(new Phrase(fichaIndividualRequest.getNome(), normal));
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
			 * LINHA 2
			 */
			if (fichaIndividualRequest.getOrigemChamada().equals("FichaIndividual")) {
				if (CommonsUtil.booleanValue(fichaIndividualRequest.isTipoPessoaIsFisica())) {
					cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("CNPJ", tituloSmall));
				}
			} else {
				cell1 = new PdfPCell(new Phrase("CPF", tituloSmall));
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

			cell1 = new PdfPCell(new Phrase(fichaIndividualRequest.getDocumento(), normal));
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
			cell1.setColspan(6);
			table.addCell(cell1);

			// PULA LINHA
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

			cell1 = new PdfPCell(new Phrase(
					"Autorizo (amos) a Galleria Sociedade de Crédito Direto S/A, bem como o conglomerado composto pelas empresas do Grupo Galleria, a consultar, detalhadamente, todos os dados e informações, judiciais ou administrativas, existentes em meu (nosso) nome e de todos os demais envolvidos na proposta, decorrentes de quaisquer operações com características de crédito que constem ou venham a constar no Sistema de Informações de Crédito (SCR), gerido pelo Banco Central do Brasil – BACEN, ou no sistema que venha a complementá-lo e/ou a substituí-lo, a qualquer tempo.",
					normal));
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

			cell1 = new PdfPCell(
					new Phrase("_____________________________________________________________________", normal));
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
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"PDF Contrato de Pessoa Fisica: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"PDF Contrato de Pessoa Fisica: Ocorreu um problema ao gerar o PDF!" + e, ""));
		}

		if (doc != null) {
			// fechamento do documento
			doc.close();
		}

		return baos.toByteArray();

	}

	public byte[] geraPDFTermoCienciaByteArray(TermoCienciaRequest termoCienciaRequest) throws JRException, IOException {
		JasperPrint jp = geraPDFTermoCiencia(termoCienciaRequest);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		JasperExportManager.exportReportToPdfStream(jp, bos);

		return bos.toByteArray();
	}

	public JasperPrint geraPDFTermoCiencia(TermoCienciaRequest termoCienciaRequest) throws JRException, IOException {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		final ReportUtil ReportUtil = new ReportUtil();
		JasperReport rptTermoCiencia = ReportUtil.getRelatorio("TermoCiencia");
		InputStream logoStream = getClass().getResourceAsStream("/resource/GalleriaBank.png");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

		parameters.put("IMAGEMLOGO", IOUtils.toByteArray(logoStream));

//		
//		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        Document document = docBuilder.parse(new InputSource(new StringReader(GsonUtil.toJson(contratoCobranca))));
//
//        JRDataSource xmlDataSource = new JRXmlDataSource(document, impressao.getPathExpression());
//		  JRDataSource jsonDataSource = new JsonDataSource(GsonUtil.toJson(termoCienciaRequest));
//		parameters.put(JsonQueryExecuterFactory.JSON_INPUT_STREAM, GsonUtil.toJson(termoCienciaRequest));
		JsonDataSource dataSource = new JsonDataSource(
				new ByteArrayInputStream(GsonUtil.toJson(termoCienciaRequest).getBytes()));

		return JasperFillManager.fillReport(rptTermoCiencia, parameters, dataSource);

	}

	

	public byte[] geraPDFPPlanilhaRestituicaoByteArray(PlanilhaRestituicaoVO planilhaRestituicaoVO) throws JRException, IOException {
		JasperPrint jp = geraPDFPPlanilhaRestituicao(planilhaRestituicaoVO);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		JasperExportManager.exportReportToPdfStream(jp, bos);

		return bos.toByteArray();
	}
	
	public JasperPrint geraPDFPPlanilhaRestituicao(PlanilhaRestituicaoVO planilhaRestituicaoVO) throws JRException, IOException {
		final ReportUtil ReportUtil = new ReportUtil();
		JasperReport rptSimulacao = ReportUtil.getRelatorio("PlanilhaRestituicao");
		InputStream logoStream = getClass().getResourceAsStream("/resource/GalleriaBankTransp.png");
			
		JasperReport rptDetalhe = ReportUtil.getRelatorio("PlanilhaRestituicao_Detalhe");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

		parameters.put("SUBREPORT_DETALHE_DESPESA", rptDetalhe);
		parameters.put("IMAGEMLOGO", IOUtils.toByteArray(logoStream));

		
		 String json =   GsonUtil.toJson(planilhaRestituicaoVO);
		 
		 InputStream jsonInput = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

         // Create a JsonDataSource with your JSON data
         JsonDataSource dataSource = new JsonDataSource(jsonInput);
         
//		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);
	}

}
