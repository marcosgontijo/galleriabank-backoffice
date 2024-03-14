package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Br;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.json.JSONObject;
import org.primefaces.PrimeFaces;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplate;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplateBloco;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplateCampo;
import com.webnowbr.siscoat.cobranca.db.model.DocketConsulta;
import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.CcbProcessosJudiciaisDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketEstadosDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.template.ContratoTipoTemplateDao;
import com.webnowbr.siscoat.cobranca.model.docket.DocketDocumento;
import com.webnowbr.siscoat.cobranca.model.docket.DocketRetornoConsulta;
import com.webnowbr.siscoat.cobranca.vo.CertidoesPaju;
import com.webnowbr.siscoat.cobranca.ws.netrin.NetrinConsulta;
import com.webnowbr.siscoat.cobranca.ws.plexi.PlexiConsulta;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.WordUtil;
import com.webnowbr.siscoat.exception.SiscoatException;

import br.com.galleriabank.bigdata.cliente.model.processos.AcaoJudicial;
import br.com.galleriabank.bigdata.cliente.model.processos.ProcessoParte;
import br.com.galleriabank.bigdata.cliente.model.processos.ProcessoResult;
import br.com.galleriabank.drcalc.cliente.model.DebitosJudiciais;
import br.com.galleriabank.drcalc.cliente.model.DebitosJudiciaisRequest;
import br.com.galleriabank.drcalc.cliente.model.DebitosJudiciaisRequestValor;
import br.com.galleriabank.drcalc.cliente.model.DebitosJudiciaisValores;

//import net.sf.jasperreports.engine.util.ExpressionParser;

public class PajuService {

	private static final String BLOCO_DOCUMENTO = "D";
	private static final String BLOCO_CABECALHO = "C";
	private static final String BLOCO_PESSOA_FISICA_CONSULTA = "PFC";
	private static final String BLOCO_PESSOA_FISICA_CERTIDOES = "PFCERT";
	private static final String BLOCO_PESSOA_FISICA_DOCUMENTOS = "PFDOCS";
	private static final String BLOCO_PESSOA_FISICA_DOCUMENTOS_PLEXI = "PFDOCS_PLEXI";
	private static final String BLOCO_PESSOA_FISICA_DOCUMENTOS_NETRIN = "PFDOCS_NETRIN";
	private static final String BLOCO_PESSOA_FISICA_CERTIDOES_PLEXI = "PFCERTIDAO_PLEXI";

	private static final String BLOCO_PESSOA_JURIDICA_CONSULTA = "PJC";
	private static final String BLOCO_PESSOA_JURIDICA_CERTIDOES = "PJCERT";
	private static final String BLOCO_PESSOA_JURIDICA_DOCUMENTOS = "PJDOCS";
	private static final String BLOCO_PESSOA_JURIDICA_DOCUMENTOS_PLEXI = "PJDOCS_PLEXI";
	private static final String BLOCO_PESSOA_JURIDICA_DOCUMENTOS_NETRIN = "PJDOCS_NETRIN";
	private static final String BLOCO_PESSOA_JURIDICA_CERTIDOES_PLEXI = "PJCERTIDAO_PLEXI";

	private ExpressionParser parser;

	private StandardEvaluationContext context;

	private String arquivoWord;

	private ContratoCobranca contrato;

	private WordUtil wordUtil;
	private List<DocumentoAnalise> listaDocumentoAnalise;
	private List<CcbProcessosJudiciais> listProcessos = new ArrayList<CcbProcessosJudiciais>();

//	retorna  o doc em base64
	public byte[] generateModeloPaju(ContratoCobranca contrato, String arquivoWord) throws SiscoatException {

		setContrato(contrato);
//		this.contrato = contrato;
		this.arquivoWord = arquivoWord;
		WordprocessingMLPackage docTemplate;

		InputStream resourceAsStream = getClass().getResourceAsStream("/resource/" + arquivoWord);

		parser = new SpelExpressionParser();
		context = new StandardEvaluationContext();
		wordUtil = new WordUtil();

		try {
			docTemplate = WordprocessingMLPackage.load(resourceAsStream);

			context.registerFunction("getEnderecoImovel",
					PajuService.class.getMethod("getEnderecoImovel", new Class[] { ImovelCobranca.class }));
			context.registerFunction("getCidadeImovel",
					PajuService.class.getMethod("getCidadeImovel", new Class[] { ImovelCobranca.class }));
			context.registerFunction("getUfImovel",
					PajuService.class.getMethod("getUfImovel", new Class[] { ImovelCobranca.class }));

		} catch (SecurityException e) {
			throw new SiscoatException("Erro gerar documento ao instanciar classe", e);
		} catch (NoSuchMethodException e) {
			throw new SiscoatException("Erro gerar documento ao instanciar classe", e);
		} catch (Docx4JException e) {
			throw new SiscoatException("Erro gerar documento ao ler docx " + arquivoWord, e);
		}

		ContratoTipoTemplateDao contratoTipoTemplateDao = new ContratoTipoTemplateDao();
		ContratoTipoTemplate ContratoTipoTemplate = contratoTipoTemplateDao.getTemplate("PJ");
		List<ContratoTipoTemplateBloco> lstBlocos = ContratoTipoTemplate.getBlocos();

		List<DocumentoAnalise> listaDocumentoAnaliseAnalisados = this.listaDocumentoAnalise.stream()
				.filter(p -> p.isLiberadoAnalise()).collect(Collectors.toList());

		Set<DocumentoAnalise> pessoasPF = listaDocumentoAnaliseAnalisados.stream()
				.filter(p -> p.isLiberadoAnalise() && CommonsUtil.mesmoValor("PF", p.getTipoPessoa()))
				.collect(Collectors.toSet());

		Set<DocumentoAnalise> pessoasPJ = listaDocumentoAnaliseAnalisados.stream()
				.filter(p -> p.isLiberadoAnalise() && CommonsUtil.mesmoValor("PJ", p.getTipoPessoa()))
				.collect(Collectors.toSet());

		if (CommonsUtil.semValor(pessoasPF) && CommonsUtil.semValor(pessoasPJ)) {
			DocumentoAnalise documentoAnalise = new DocumentoAnalise();
			documentoAnalise.setPagador(contrato.getPagador());

			if (CommonsUtil.semValor(contrato.getPagador().getCnpj())) {
				documentoAnalise.setCnpjcpf(contrato.getPagador().getCpf());
				documentoAnalise.setTipoPessoa("PF");
				pessoasPF.add(documentoAnalise);
			} else {
				documentoAnalise.setCnpjcpf(contrato.getPagador().getCnpj());
				documentoAnalise.setTipoPessoa("PJ");
				pessoasPJ.add(documentoAnalise);
			}
		}

		// certidoes Docket
		DocketRetornoConsulta docketRetornoConsulta = processaCertidoesDocket(contrato, pessoasPF, pessoasPJ);

		Map<String, List<String>> mapProcesos = new HashMap<String, List<String>>();

		// certidoes engine
		for (ContratoTipoTemplateBloco bloco : lstBlocos) {

			if (bloco.getFlagInativo()) {
				continue;
			}

			String tipo = bloco.getCodigoTipoTemplateBloco();

			switch (tipo) {

			case BLOCO_DOCUMENTO:

				replacePlaceholder(docTemplate.getMainDocumentPart(), "dataGeracao",
						(String) CommonsUtil.formataData(DateUtil.getDataHoje(), "dd 'de' MMMM 'de' yyyy"));

				for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {

					Object valor = getValor(campo.getExpressao());

					replacePlaceholder(docTemplate.getMainDocumentPart(), campo.getTag(), (String) valor);
				}
				break;

			case BLOCO_CABECALHO:

				for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {

					Object valor = getValor(campo.getExpressao());

					replacePlaceholderAtHeader(docTemplate, campo.getTag(), (String) valor);
				}
				break;

			case BLOCO_PESSOA_FISICA_CONSULTA:
				populaParagrafoParticipantes(docTemplate, bloco, pessoasPF);
				break;

			case BLOCO_PESSOA_JURIDICA_CONSULTA:
				populaParagrafoParticipantes(docTemplate, bloco, pessoasPJ);
				break;

			case BLOCO_PESSOA_FISICA_CERTIDOES:
				populaParagrafoCertidoesPF(docTemplate, bloco, pessoasPF, docketRetornoConsulta, mapProcesos);
				break;

			case BLOCO_PESSOA_JURIDICA_CERTIDOES:
				populaParagrafoCertidoesPJ(docTemplate, bloco, pessoasPJ, docketRetornoConsulta, mapProcesos);
				break;

			/*
			 * case BLOCO_DEBITOS_IPTU:
			 * 
			 * participantes = buscaParticipantesPoder(BanisysConstants.
			 * PESSOA_PARTICIPACAO_PODER_ASSINAR_EMPRESA,
			 * BanisysConstants.TIPO_PESSOA_FISICA);
			 * populaParagrafoParticipantes(docTemplate, bloco, participantes); break;
			 * 
			 * case BLOCO_DEBITOS_CONDOMNIO:
			 * 
			 * participantes = buscaParticipantesTipo(BanisysConstants.
			 * PESSOA_PARTICIPACAO_TIPO_DEVEDOR_SOLIDARIO,
			 * BanisysConstants.TIPO_PESSOA_FISICA);
			 * 
			 * populaParagrafoParticipantes(docTemplate, bloco, participantes); break;
			 */
			}

		}
		

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			docTemplate.save(baos);
			criarProcessoBancoDados();
		} catch (Exception e) {
			throw new SiscoatException("Erro ao gerar modelo Paju: ", e);
		}
		return baos.toByteArray();

	}

	private DocketRetornoConsulta processaCertidoesDocket(ContratoCobranca contrato, Set<DocumentoAnalise> pessoasPF,
			Set<DocumentoAnalise> pessoasPJ) {

		DocketEstadosDao docketEstadosDao = new DocketEstadosDao();
		List<DocketEstados> docketEstados = docketEstadosDao.findAll();

		DocketDao docketDao = new DocketDao();
		String idCallManager = docketDao.consultaContratosPendentesResponsaveis(contrato);

		DocketService docketService = new DocketService();
		DocketRetornoConsulta docketRetornoConsulta = docketService.verificarCertidoesContrato(contrato, idCallManager);
		if (!CommonsUtil.semValor(docketRetornoConsulta))
			for (DocketDocumento docketDocumento : docketRetornoConsulta.getPedido().getDocumentos()) {
				Optional<String> estadoNome = docketEstados.stream()
						.filter(e -> CommonsUtil.mesmoValor(e.getIdDocket(), docketDocumento.getCampos().getEstado()))
						.map(e -> e.getNome()).findFirst();
				docketDocumento.getCampos().setEstadoNome(estadoNome.get());
				if (CommonsUtil.semValor(docketDocumento.getCampos().getCnpj())) {
					if (!pessoasPF.stream()
							.filter(p -> CommonsUtil.mesmoValor(
									CommonsUtil.formataCnpjCpf(docketDocumento.getCampos().getCpf(), false),
									CommonsUtil.formataCnpjCpf(p.getCnpjcpf(), false)))
							.findAny().isPresent()) {
						PagadorRecebedorService pagagadorRecebedorService = new PagadorRecebedorService();

						PagadorRecebedor pessoaConsultaDocket = new PagadorRecebedor("processaCertidoesDocket");
						pessoaConsultaDocket
								.setCpf(CommonsUtil.formataCnpjCpf(docketDocumento.getCampos().getCpf(), false));
						pessoaConsultaDocket.setNome(docketDocumento.getCampos().getNomeCompleto());
						pessoaConsultaDocket.setNomeMae(docketDocumento.getCampos().getNomeMae());
						pessoaConsultaDocket.setRg(docketDocumento.getCampos().getRg());
						pessoaConsultaDocket.setDtNascimento(docketDocumento.getCampos().getDataNascimento());

						pessoaConsultaDocket = pagagadorRecebedorService.buscaOuInsere(pessoaConsultaDocket);
						DocumentoAnalise documentoAnalise = new DocumentoAnalise();
						documentoAnalise.setPagador(pessoaConsultaDocket);
						documentoAnalise.setCnpjcpf(pessoaConsultaDocket.getCpf());

						documentoAnalise.setTipoPessoa("PF");
						pessoasPF.add(documentoAnalise);
					}
				} else {
					if (!pessoasPJ.stream()
							.filter(p -> CommonsUtil.mesmoValor(
									CommonsUtil.formataCnpjCpf(docketDocumento.getCampos().getCnpj(), false),
									CommonsUtil.formataCnpjCpf(p.getCnpjcpf(), false)))
							.findAny().isPresent()) {
						PagadorRecebedorService pagagadorRecebedorService = new PagadorRecebedorService();

						PagadorRecebedor pessoaConsultaDocket = new PagadorRecebedor();
						pessoaConsultaDocket
								.setCnpj(CommonsUtil.formataCnpjCpf(docketDocumento.getCampos().getCnpj(), false));
						pessoaConsultaDocket.setNome(docketDocumento.getCampos().getRazaoSocial());

						pessoaConsultaDocket = pagagadorRecebedorService.buscaOuInsere(pessoaConsultaDocket);
						DocumentoAnalise documentoAnalise = new DocumentoAnalise();
						documentoAnalise.setPagador(pessoaConsultaDocket);
						documentoAnalise.setCnpjcpf(pessoaConsultaDocket.getCnpj());
						documentoAnalise.setTipoPessoa("PJ");
						pessoasPJ.add(documentoAnalise);
					}
				}

			}
		return docketRetornoConsulta;
	}

	private void populaParagrafoParticipantes(WordprocessingMLPackage docTemplate, ContratoTipoTemplateBloco bloco,
			Set<DocumentoAnalise> participantes) throws SiscoatException {

		List<P> paragrafoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getTagIdentificacao());

		if (paragrafoTemplate == null) {
			return;
		}

		for (DocumentoAnalise participante : participantes) {
			adicionaParagrafo(docTemplate, paragrafoTemplate, bloco, participante);
		}

		if (paragrafoTemplate != null) {
			removeParagrafo(docTemplate, paragrafoTemplate);
		}

	}

	private void populaParagrafoCertidoesPF(WordprocessingMLPackage docTemplate, ContratoTipoTemplateBloco bloco,
			Set<DocumentoAnalise> participantes, DocketRetornoConsulta docketRetornoConsulta,
			Map<String, List<String>> mapProcesos) throws SiscoatException {

		List<P> paragrafoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getTagIdentificacao());
		List<P> paragrafoDocumentoTemplate = getParagrafoDocumentoTemplate(bloco, paragrafoTemplate);
		ajustaParagrafoTemplate(docTemplate, paragrafoTemplate, paragrafoDocumentoTemplate);

//		List<P> paragrafoDocumentoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getBlocosFilho().get(0).getTagIdentificacao());

		if (paragrafoTemplate == null) {
			return;
		}

		final List<String> nomesParticipantes = participantes.stream().map(p -> p.getPagador().getNome()).distinct()
				.collect(Collectors.toList());
		for (DocumentoAnalise participante : participantes) {
			ProcessoResult bigData = null;
			if (CommonsUtil.mesmoValor(participante.getTipoProcesso(), "B")) {
				bigData = GsonUtil.fromJson(participante.getRetornoProcesso(), ProcessoResult.class);
			}

			adicionaParagrafo(docTemplate, paragrafoTemplate, bloco, participante);

			if (!CommonsUtil.semValor(docketRetornoConsulta)) {
				List<DocketDocumento> documentosParticipante = docketRetornoConsulta.getPedido().getDocumentos();
				List<DocketDocumento> documentosParticipanteFiltro = documentosParticipante.stream()
						.filter(d -> CommonsUtil.mesmoValor(d.getCampos().getCpf(),
								CommonsUtil.somenteNumeros(participante.getCnpjcpf())))
						.collect(Collectors.toList());
				for (DocketConsulta docketDocumento : participante.getDocketConsultas()) {
					ContratoTipoTemplateBloco blocoFilho = bloco.getBlocosFilho().stream().filter(
							b -> CommonsUtil.mesmoValor(b.getCodigoTipoTemplateBloco(), BLOCO_PESSOA_FISICA_DOCUMENTOS))
							.findFirst().orElse(null);
					if (!CommonsUtil.semValor(blocoFilho)) {

						CertidoesPaju certidoesPaju = new CertidoesPaju();
						//if (CommonsUtil.mesmoValor(SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS,
						//		docketDocumento.getSituacaoPaju())) {
							DocketService docketService = new DocketService();
							try {
								//String idCallManager = docketDocumento.getArquivos().get(0).getLinks().get(0).getHref()
								//		.substring(docketDocumento.getArquivos().get(0).getLinks().get(0).getHref()
								//				.lastIndexOf("/") + 1);
								List<String> pdfLines = lerCND(docketDocumento.getPdf());

								// split by whitespace
								StringBuilder sb = new StringBuilder();
								
								if(docketDocumento.getDocketDocumentos().getId() == 7L &&
										CommonsUtil.mesmoValor(docketDocumento.getUf(), "MT")) {
									processaDocketMT(docTemplate, mapProcesos, paragrafoDocumentoTemplate,
											nomesParticipantes, participante, bigData, docketDocumento, blocoFilho,
											certidoesPaju, pdfLines);
								} else {
									processaDocketSP(docTemplate, mapProcesos, paragrafoDocumentoTemplate,
											nomesParticipantes, participante, bigData, docketDocumento, blocoFilho,
											certidoesPaju, pdfLines);
								}
								

							} catch (Exception e) {
								System.out.println(docketDocumento);
								System.out.println(docketDocumento.getRetorno());
								e.printStackTrace();

							}
						//}
					}
				}
			}

			if (!CommonsUtil.semValor(participante.getPlexiConsultas())) {

				for (PlexiConsulta plexiConsulta : participante.getPlexiConsultas()) {

					ContratoTipoTemplateBloco blocoFilho = bloco.getBlocosFilho().stream().filter(b -> CommonsUtil
							.mesmoValor(b.getCodigoTipoTemplateBloco(), BLOCO_PESSOA_FISICA_DOCUMENTOS_PLEXI))
							.findFirst().orElse(null);
					if (!CommonsUtil.semValor(blocoFilho)) {

						if (!CommonsUtil.semValor(plexiConsulta.getPlexiWebhookRetorno())
								&& !CommonsUtil.booleanValue(plexiConsulta.getPlexiWebhookRetorno().getError())
								&& CommonsUtil.booleanValue(plexiConsulta.getPlexiDocumentos().isMostrarPaju())) {

							CertidoesPaju certidoesPaju = new CertidoesPaju();

							if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 31L)) {
								processaCertidaoTJSP_1Grau(plexiConsulta, certidoesPaju);
							} else if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 42L)) {
								processaCertidaoTRT2_PJe(plexiConsulta, bigData, certidoesPaju);
							} else if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 35L)) {
								processaCertidaoTRF3_PJe(plexiConsulta, bigData, certidoesPaju);
							} else if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 39L)) {
								processaCertidaoTRF15(plexiConsulta, bigData, certidoesPaju);
							}
							
							adicionaParagrafoPlexi(docTemplate, paragrafoDocumentoTemplate, blocoFilho,
									plexiConsulta, certidoesPaju, bigData, mapProcesos, nomesParticipantes,
									participante);
						}
					}
				}
			}

			if (!CommonsUtil.semValor(participante.getNetrinConsultas())) {
				for (NetrinConsulta netrinConsulta : participante.getNetrinConsultas()) {

					ContratoTipoTemplateBloco blocoFilho = bloco.getBlocosFilho().stream().filter(b -> CommonsUtil
							.mesmoValor(b.getCodigoTipoTemplateBloco(), BLOCO_PESSOA_FISICA_DOCUMENTOS_NETRIN))
							.findFirst().orElse(null);
					if (!CommonsUtil.semValor(blocoFilho)) {
						if (!CommonsUtil.semValor(netrinConsulta.getRetorno())
								&& CommonsUtil.booleanValue(netrinConsulta.getNetrinDocumentos().isMostrarPaju()))
							adicionaParagrafoNetrin(docTemplate, paragrafoDocumentoTemplate, blocoFilho,
									netrinConsulta);
					}
				}
			}

		}

		if (paragrafoTemplate != null) {

			removeParagrafo(docTemplate, paragrafoDocumentoTemplate);
			removeParagrafo(docTemplate, paragrafoTemplate);
		}

	}

	private void processaDocketSP(WordprocessingMLPackage docTemplate, Map<String, List<String>> mapProcesos,
			List<P> paragrafoDocumentoTemplate, final List<String> nomesParticipantes, DocumentoAnalise participante,
			ProcessoResult bigData, DocketConsulta docketDocumento, ContratoTipoTemplateBloco blocoFilho,
			CertidoesPaju certidoesPaju, List<String> pdfLines) throws SiscoatException {
		boolean processos = false;
		DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
		debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));
		DebitosJudiciais debitosJudiciais = null;
		for (String line : pdfLines) {
			if (processos && line.contains("Total de A"))
				break;
			if (processos && !CommonsUtil.semValor(line)) {
				final String numeroProcesso = CommonsUtil.somenteNumeros(line);
				AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);

				if (acao != null) {
					DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
					debitosJudiciaisRequestValor.setDescricao(numeroProcesso);
					debitosJudiciaisRequestValor.setVencimento("0101" + DateUtil.getAnoProcesso(numeroProcesso));
					debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

					debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);
				}
			}
			if (!CommonsUtil.semValor(debitosJudiciaisRequest.getValores())) {
				DrCalcService drCalcService = new DrCalcService();
				debitosJudiciais = drCalcService.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);
			}
		}
		for (String line : pdfLines) {

			if (processos && line.contains("Total de A"))
				break;
			if (processos && !CommonsUtil.semValor(line)) {
				final String numeroProcesso = CommonsUtil.somenteNumeros(line);
				AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
				String sLinha = "Processo: " + line.trim();
				sLinha = getDadosAcaoBigData(mapProcesos, nomesParticipantes, participante, numeroProcesso, acao,
						debitosJudiciais, sLinha, "Docket - TJSP");
				certidoesPaju.getDebitosDocumento().add(sLinha);
			}
			if (line.contains("conforme listagem abaixo:"))
				processos = true;

		}
		adicionaParagrafoDocket(docTemplate, paragrafoDocumentoTemplate, blocoFilho, docketDocumento, certidoesPaju,
				bigData);
	}

	private void processaDocketMT(WordprocessingMLPackage docTemplate, Map<String, List<String>> mapProcesos,
			List<P> paragrafoDocumentoTemplate, final List<String> nomesParticipantes, DocumentoAnalise participante,
			ProcessoResult bigData, DocketConsulta docketDocumento, ContratoTipoTemplateBloco blocoFilho,
			CertidoesPaju certidoesPaju, List<String> pdfLines) throws SiscoatException {
		DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
		debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));
		DebitosJudiciais debitosJudiciais = null;
		try {
			
		for (String line : pdfLines) {
			if (CommonsUtil.semValor(line))
				continue;
			if (!line.contains("Processo: "))
				continue;
			if (line.contains("Situação do Processo:"))
				continue;
			if (line.split("\\s+").length < 2) 
				continue;
			
			final String numeroProcesso = line.split("\\s+")[1];
			AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
			if (acao != null) {
				DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
				debitosJudiciaisRequestValor.setDescricao(numeroProcesso);
				debitosJudiciaisRequestValor
						.setVencimento("0101" + DateUtil.getAnoProcesso(numeroProcesso));
				debitosJudiciaisRequestValor
						.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));
				debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);
			}
		}
		if (!CommonsUtil.semValor(debitosJudiciaisRequest.getValores())) {
			DrCalcService drCalcService = new DrCalcService();
			debitosJudiciais = drCalcService
					.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);
		}
		for (String line : pdfLines) {
			if (!CommonsUtil.semValor(line)) {
				if (CommonsUtil.semValor(line))
					continue;
				if (!line.contains("Processo: "))
					continue;
				if (line.split("\\s+").length < 2) 
					continue;
				if (!CommonsUtil.mesmoValor(line.split("\\s+")[0], "Processo:"))
					continue;
				
				
				final String numeroProcesso = line.split("\\s+")[1];
				AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
				String sLinha = "Processo: " + CommonsUtil.formataNumeroProcesso(numeroProcesso);
				sLinha = getDadosAcaoBigData(mapProcesos, nomesParticipantes, participante,
						numeroProcesso, acao, debitosJudiciais, sLinha, "Docket - TJMT");
				certidoesPaju.getDebitosDocumento().add(sLinha);		
			}
		}
		adicionaParagrafoDocket(docTemplate, paragrafoDocumentoTemplate, blocoFilho,
				docketDocumento, certidoesPaju, bigData);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getDadosAcaoBigData(Map<String, List<String>> mapProcesos, final List<String> nomesParticipantes,
			DocumentoAnalise participante, final String numeroProcesso, AcaoJudicial acao,
			DebitosJudiciais debitosJudiciais, String sLinha, String origem) {
		DebitosJudiciaisValores debitosJudiciaisValores = null;
		sLinha = "Processo: " + CommonsUtil.formataNumeroProcesso(numeroProcesso) + " - ";
		if (!mapProcesos.keySet().contains(numeroProcesso)) {
			String svalor = "";
			if (CommonsUtil.semValor(acao)) {
				svalor = svalor + "Não localizado na consulta de processos";
				sLinha = sLinha + svalor;
				criarProcessoContrato(participante, acao, debitosJudiciaisValores, sLinha, numeroProcesso, origem);
				return sLinha;
			} else if (CommonsUtil.semValor(acao.getValue())) {
				svalor = svalor + " Valor: sem valor localizado";
			} else {
				svalor = svalor + " Valor: "
						+ CommonsUtil.formataValorMonetario(CommonsUtil.bigDecimalValue(acao.getValue()), "");

				debitosJudiciaisValores = debitosJudiciais.getValores().stream().filter(d -> CommonsUtil
						.mesmoValor(d.getDescricao(), CommonsUtil.formataNumeroProcesso(acao.getNumber()))).findFirst()
						.orElse(null);

				if (debitosJudiciaisValores != null) {
					svalor = svalor + " - valor em  " + debitosJudiciais.getMes() + " de " + debitosJudiciais.getAno()
							+ ": " + CommonsUtil.formataValorMonetario(debitosJudiciaisValores.getTotal(), "");
				}
			}
			ProcessoParte processoParte = acao.getParties().stream()
					.filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMANT")).findFirst().orElse(null);
			if (processoParte != null)
				sLinha = sLinha + processoParte.getName() + " - ";

			sLinha = sLinha + acao.getMainSubject() + " - ";
			sLinha = sLinha + svalor;

			List<String> processoPartes = getPartesProcesso(mapProcesos, nomesParticipantes, numeroProcesso, acao,
					participante);
			if (!CommonsUtil.semValor(processoPartes)) {
				if (processoPartes.size() > 1) {
					sLinha = sLinha + ". Outros Partcipantes: ";
					String prefixo = "";
					for (String nomeParticipante : nomesParticipantes) {
						if (!CommonsUtil.mesmoValor(nomeParticipante, participante.getPagador().getNome())) {
							sLinha = sLinha + prefixo + nomeParticipante;
							prefixo = ", ";
						}
					}
				}
			}
		} else {
			sLinha = sLinha + "Já mencionado em : " + mapProcesos.get(numeroProcesso).get(0);
		}
		criarProcessoContrato(participante, acao, debitosJudiciaisValores, sLinha, numeroProcesso, origem);
		return sLinha;
	}

	private List<String> getPartesProcesso(Map<String, List<String>> mapProcesos, final List<String> nomesParticipantes,
			final String numeroProcesso, AcaoJudicial acao, DocumentoAnalise participante) {
		List<String> processoPartes = null;
		nomesParticipantes.remove(participante.getPagador().getNome());
		if (!mapProcesos.keySet().contains(numeroProcesso)) {
			processoPartes = acao.getParties().stream().filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMED"))
					.filter(a -> nomesParticipantes.contains(a.getName())).map(p -> p.getName())
					.collect(Collectors.toList());
			processoPartes.add(0, participante.getPagador().getNome());
			mapProcesos.put(numeroProcesso, processoPartes);
		} else
			processoPartes = mapProcesos.get(numeroProcesso);

		return processoPartes;
	}

	private List<String> lerCND(String base64) {
		// Query Url

		List<String> result = new ArrayList<String>();
		URL myURL;
		try {

			myURL = new URL("https://servicos.galleriabank.com.br/pdf/read");
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("documento", base64);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			// try (OutputStream os = myURLConnection.getOutputStream()) {
			// os.write(postDataBytes, 0, postDataBytes.length);
			// }

			String retornoConsulta = null;
			if (myURLConnection.getResponseCode() == SiscoatConstants.HTTP_COD_SUCESSO) {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				retornoConsulta = response.toString();
			}

			if (!CommonsUtil.semValor(retornoConsulta)) {
				result = GsonUtil.fromJson(retornoConsulta, GsonUtil.getColletionType(result));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private void ajustaParagrafoTemplate(WordprocessingMLPackage docTemplate, List<P> paragrafoTemplate,
			List<P> paragrafoDocumentoTemplate) {
		if (!CommonsUtil.semValor(paragrafoDocumentoTemplate)) {

			paragrafoTemplate.removeAll(paragrafoDocumentoTemplate);

			removeParagrafo(docTemplate, paragrafoDocumentoTemplate.get(0));
			paragrafoDocumentoTemplate.remove(0);
			removeParagrafo(docTemplate, paragrafoDocumentoTemplate.get(paragrafoDocumentoTemplate.size() - 1));
			paragrafoDocumentoTemplate.remove(paragrafoDocumentoTemplate.size() - 1);

			removeParagrafo(docTemplate, paragrafoTemplate.get(0));
			paragrafoTemplate.remove(0);
			removeParagrafo(docTemplate, paragrafoTemplate.get(paragrafoTemplate.size() - 1));
			paragrafoTemplate.remove(paragrafoTemplate.size() - 1);
		}
	}

	private List<P> getParagrafoDocumentoTemplate(ContratoTipoTemplateBloco bloco, List<P> paragrafoTemplate) {
		List<P> paragrafoDocumentoTemplate = new ArrayList<>();
		String startPlaceholder = "#{" + bloco.getBlocosFilho().get(0).getTagIdentificacao() + "}";
		String endPlaceholder = "#{/" + bloco.getBlocosFilho().get(0).getTagIdentificacao() + "}";

		boolean bfilho = false;
		for (P p : paragrafoTemplate) {
			if (CommonsUtil.mesmoValor(startPlaceholder, p.toString()))
				bfilho = true;
			else if (CommonsUtil.mesmoValor(endPlaceholder, p.toString())) {
				bfilho = false;
				paragrafoDocumentoTemplate.add(p);
			}

			if (bfilho) {
				paragrafoDocumentoTemplate.add(p);
			}
		}
		return paragrafoDocumentoTemplate;
	}

	private void processaCertidaoTJSP_1Grau(PlexiConsulta plexiConsulta, CertidoesPaju certidoesPaju) {

		// faz leitura do PDF
		if (CommonsUtil.mesmoValor(SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS,
				plexiConsulta.getPlexiWebhookRetorno().getSituacao())) {
			String texto = null;
			try {
				PDDocument document = PDDocument.load(java.util.Base64.getDecoder().decode(plexiConsulta.getPdf()));

				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				String pdfFileInText = tStripper.getText(document);

				// split by whitespace
				String lines[] = pdfFileInText.split("\\r?\\n");
				List<String> pdfLines = new ArrayList<>();

				List<String> pdfLinesSimilariedade = new ArrayList<>();
				StringBuilder sb = new StringBuilder();
				for (String line : lines) {
					pdfLines.add(line);
					sb.append(line + "\n");
				}

				List<String> pdfLinesClean = pdfLines;
				List<String> paginas = new ArrayList<String>();

				String textoInicio = pdfLines.get(0);
				int iFinal = 0;
				int iInicial = getPosicaoLinha(pdfLinesClean, textoInicio);

				while (iInicial > -1) {
					iFinal = getPosicaoLinha(pdfLinesClean, "PEDIDO N°:");
					if (iFinal == -1)
						iFinal = pdfLinesClean.size();
//									else
//										iFinal = iFinal-2;
//																		
					paginas.addAll(
							pdfLinesClean.stream().limit(iFinal - 1).skip(iInicial + 6).collect(Collectors.toList()));

					pdfLinesClean = pdfLinesClean.stream().skip(iFinal + 1).collect(Collectors.toList());

					iInicial = getPosicaoLinha(pdfLinesClean, textoInicio);
				}

				pdfLines = new ArrayList<String>(paginas);

				iInicial = getPosicaoLinha(pdfLines, "As seguintes distribuições:") + 1;

				iFinal = getPosicaoLinha(pdfLines, "CERTIFICA  ainda  que");
				if (iFinal == -1)
					iFinal = getPosicaoLinha(pdfLines, "CERTIFICA ainda  que");
				if (iFinal == -1)
					iFinal = getPosicaoLinha(pdfLines, "Esta   certidão   não   aponta   ordinariamente");
				else
					pdfLinesSimilariedade = pdfLines.stream().skip(iFinal).collect(Collectors.toList());

				iFinal = iFinal - 1;

				if (iInicial > -1 && iFinal > -1) {
					certidoesPaju.setDebitosDocumento(
							pdfLines.stream().limit(iFinal).skip(iInicial).collect(Collectors.toList()));
				}

				if (!CommonsUtil.semValor(pdfLinesSimilariedade)) {
					iInicial = getPosicaoLinha(pdfLinesSimilariedade, "que pode referir-se a homônimo:") + 1;
					iFinal = getPosicaoLinha(pdfLinesSimilariedade, "Esta certidão não aponta");

					if (iInicial > -1 && iFinal > -1) {
						certidoesPaju.setDebitosSimilariedade(pdfLinesSimilariedade.stream().limit(iFinal)
								.skip(iInicial).collect(Collectors.toList()));
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processaCertidaoTRT2_PJe(PlexiConsulta plexiConsulta, ProcessoResult bigData,
			CertidoesPaju certidoesPaju) {

		// faz leitura do PDF
		if (CommonsUtil.mesmoValor(SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS,
				plexiConsulta.getPlexiWebhookRetorno().getSituacao())) {
			String texto = null;
			try {
				List<String> pdfLines = new ArrayList<>();
				List<AcaoJudicial> acoesBigData = bigData.getProcessoResumo().getTrabalhistaProtesto();
				DebitosJudiciais debitosJudiciais = null;
				for (Object objProcesso : plexiConsulta.getPlexiWebhookRetorno().getProcessos()) {

					DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
					debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));

					// adiciona todas do bigdata
					if(CommonsUtil.semValor(acoesBigData)) {
						acoesBigData = new ArrayList<AcaoJudicial>();
					}
					for (AcaoJudicial acao : acoesBigData) {
						DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
						debitosJudiciaisRequestValor.setDescricao(CommonsUtil.formataNumeroProcesso(acao.getNumber()));
						debitosJudiciaisRequestValor.setVencimento("0101" + DateUtil.getAnoProcesso(acao.getNumber()));
						debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

						debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);
					}

					for (Entry<String, List<String>> valores : ((LinkedTreeMap<String, List<String>>) objProcesso)
							.entrySet()) {
						if (valores.getKey().equals("numeroProcessos")) {
							final String numeroProcesso = CommonsUtil
									.somenteNumeros(valores.getValue().get(0).toString());
							AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);

							if (acao != null) {
								if (!debitosJudiciaisRequest.getValores().stream()
										.filter(d -> CommonsUtil.mesmoValor(d.getDescricao(),
												CommonsUtil.formataNumeroProcesso(acao.getNumber())))
										.findFirst().isPresent()) {
									DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
									debitosJudiciaisRequestValor.setDescricao(numeroProcesso);
									debitosJudiciaisRequestValor
											.setVencimento("0101" + DateUtil.getAnoProcesso(numeroProcesso));
									debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

									debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);
								}
							}
						}
						if (!CommonsUtil.semValor(debitosJudiciaisRequest.getValores())) {
							DrCalcService drCalcService = new DrCalcService();
							debitosJudiciais = drCalcService.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);
						}
					}

					for (Entry<String, List<String>> valores : ((LinkedTreeMap<String, List<String>>) objProcesso)
							.entrySet()) {
						if (valores.getKey().equals("numeroProcessos")) {
							final String numeroProcesso = CommonsUtil
									.somenteNumeros(valores.getValue().get(0).toString());
							AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
							DebitosJudiciaisValores debitosJudiciaisValores = null;
							String sLinha = "";
							if (acao != null) {
								acoesBigData.remove(acao);
								sLinha = "Processo: " + valores.getValue().get(0) + " - ";
								sLinha = sLinha + " Ação: " + acao.getMainSubject() + " - ";
								ProcessoParte processoParte = acao.getParties().stream()
										.filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMANT")).findFirst()
										.orElse(null);
								if (processoParte != null)
									sLinha = sLinha + processoParte.getName();
								sLinha = sLinha + " - Valor - " + CommonsUtil
										.formataValorMonetario(CommonsUtil.bigDecimalValue(acao.getValue()), "");

								debitosJudiciaisValores = debitosJudiciais.getValores().stream()
										.filter(d -> CommonsUtil.mesmoValor(d.getDescricao(),
												CommonsUtil.formataNumeroProcesso(acao.getNumber())))
										.findFirst().orElse(null);

								if (debitosJudiciaisValores != null) {
									sLinha = sLinha + " - valor em  " + debitosJudiciais.getMes() + " de "
											+ debitosJudiciais.getAno() + ": "
											+ CommonsUtil.formataValorMonetario(debitosJudiciaisValores.getTotal(), "");
								}

								pdfLines.add(sLinha);
							} else {
								sLinha = "Processo: " + valores.getValue().get(0)
										+ " - não listado na Consulta Processos";
								pdfLines.add(sLinha);
							}
							criarProcessoContrato(plexiConsulta.getDocumentoAnalise(), acao, debitosJudiciaisValores,
									sLinha, numeroProcesso, "Plexi - TRT2 PJe");
						}
					}
				}
				if (!CommonsUtil.semValor(acoesBigData))
					for (AcaoJudicial acao : acoesBigData) {
						if (!CommonsUtil.mesmoValor(acao.getNumber().substring(13, 14), "5"))
							continue;
						DebitosJudiciaisValores debitosJudiciaisValores = null;
						String sLinha = "Processo: " + CommonsUtil.formataNumeroProcesso(acao.getNumber()) + " - ";
						sLinha = sLinha + " Ação: " + acao.getMainSubject() + " - ";
						ProcessoParte processoParte = acao.getParties().stream()
								.filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMANT")).findFirst().orElse(null);
						if (processoParte != null)
							sLinha = sLinha + processoParte.getName();
						sLinha = sLinha + " - Valor - "
								+ CommonsUtil.formataValorMonetario(CommonsUtil.bigDecimalValue(acao.getValue()), "");
//
//						DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
//						debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));
//						DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
//						debitosJudiciaisRequestValor.setDescricao(CommonsUtil.formataNumeroProcesso(acao.getNumber()));
//						debitosJudiciaisRequestValor.setVencimento(
//								"0101" + CommonsUtil.formataNumeroProcesso(acao.getNumber()).substring(11, 15));
//						debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));
//
//						debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);

						if (debitosJudiciais != null) {
							debitosJudiciaisValores = debitosJudiciais.getValores().stream()
									.filter(d -> CommonsUtil.mesmoValor(d.getDescricao(),
											CommonsUtil.formataNumeroProcesso(acao.getNumber())))
									.findFirst().orElse(null);
							if (debitosJudiciaisValores != null) {
								sLinha = sLinha + " - valor em  " + debitosJudiciais.getMes() + " de "
										+ debitosJudiciais.getAno() + ": "
										+ CommonsUtil.formataValorMonetario(debitosJudiciaisValores.getTotal(), "");
							}
						}

						sLinha = sLinha + " - não listado na Consulta Plexi";
						pdfLines.add(sLinha);

						criarProcessoContrato(plexiConsulta.getDocumentoAnalise(), acao, debitosJudiciaisValores,
								sLinha, acao.getNumber(), "BigData - Processos");
					}
				certidoesPaju.setDebitosDocumento(pdfLines);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processaCertidaoTRF3_PJe(PlexiConsulta plexiConsulta, ProcessoResult bigData,
			CertidoesPaju certidoesPaju) {

		// faz leitura do PDF
		if (CommonsUtil.mesmoValor(SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS,
				plexiConsulta.getPlexiWebhookRetorno().getSituacao())) {
			String texto = null;
			try {
				List<String> pdfLines = new ArrayList<>();

				DebitosJudiciais debitosJudiciais = null;

				DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
				debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));

				for (Object objProcesso : plexiConsulta.getPlexiWebhookRetorno().getProcessos()) {

					for (Entry<String, String> valores : ((LinkedTreeMap<String, String>) objProcesso).entrySet()) {
						if (valores.getKey().equals("numero")) {
							final String numeroProcesso = CommonsUtil.somenteNumeros(valores.getValue().toString());

							AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);

							if (acao != null) {
								DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
								debitosJudiciaisRequestValor.setDescricao(numeroProcesso);
								debitosJudiciaisRequestValor
										.setVencimento("0101" + DateUtil.getAnoProcesso(numeroProcesso));
								debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

								debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);

							}
						}
					}
					if (!CommonsUtil.semValor(debitosJudiciaisRequest.getValores())) {
						DrCalcService drCalcService = new DrCalcService();
						debitosJudiciais = drCalcService.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);
					}
				}

				for (Object objProcesso : plexiConsulta.getPlexiWebhookRetorno().getProcessos()) {

					for (Entry<String, String> valores : ((LinkedTreeMap<String, String>) objProcesso).entrySet()) {
						if (valores.getKey().equals("numero")) {
							final String numeroProcesso = CommonsUtil.somenteNumeros(valores.getValue().toString());
							AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
							DebitosJudiciaisValores debitosJudiciaisValores = null;
							String sLinha = "";
							if (acao != null) {
								sLinha = "Processo: " + valores.getValue() + " - ";

								ProcessoParte processoParte = acao.getParties().stream()
										.filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMANT")).findFirst()
										.orElse(null);
								if (processoParte != null)
									sLinha = sLinha + processoParte.getName();
								sLinha = sLinha + " - Valor - " + CommonsUtil
										.formataValorMonetario(CommonsUtil.bigDecimalValue(acao.getValue()), "");


								debitosJudiciaisValores = debitosJudiciais.getValores().stream()
										.filter(d -> CommonsUtil.mesmoValor(d.getDescricao(),
												CommonsUtil.formataNumeroProcesso(acao.getNumber())))
										.findFirst().orElse(null);
								if (debitosJudiciaisValores != null) {
									sLinha = sLinha + " - valor em  " + debitosJudiciais.getMes() + " de "
											+ debitosJudiciais.getAno() + ": "
											+ CommonsUtil.formataValorMonetario(debitosJudiciaisValores.getTotal(), "");
								}

								pdfLines.add(sLinha);
							} else {
								sLinha = "Processo: " + valores.getValue() + " - não listado na Consulta Processos";
								pdfLines.add(sLinha);
							}

							criarProcessoContrato(plexiConsulta.getDocumentoAnalise(), acao, debitosJudiciaisValores,
									sLinha, numeroProcesso, "Plexi - TRF3 Pje");
						}
					}
				}
				
				certidoesPaju.setDebitosDocumento(pdfLines);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processaCertidaoTRF15(PlexiConsulta plexiConsulta, ProcessoResult bigData,
			CertidoesPaju certidoesPaju) {

		// faz leitura do PDF
		if (CommonsUtil.mesmoValor(SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS,
				plexiConsulta.getPlexiWebhookRetorno().getSituacao())) {
			String texto = null;
			try {
				List<String> pdfLines = new ArrayList<>();

				DebitosJudiciais debitosJudiciais = null;

				DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
				debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));

				for (Object objProcesso : plexiConsulta.getPlexiWebhookRetorno().getProcessos()) {
					String numeroProcesso = CommonsUtil.somenteNumeros(objProcesso.toString());
					if (objProcesso.toString().indexOf(" ") > -1)
						numeroProcesso = CommonsUtil.somenteNumeros(
								objProcesso.toString().substring(01, objProcesso.toString().indexOf(" ")));

							AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);

							if (acao != null) {
								DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
								debitosJudiciaisRequestValor.setDescricao(numeroProcesso);
								debitosJudiciaisRequestValor
										.setVencimento("0101" + DateUtil.getAnoProcesso(numeroProcesso));
								debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

								debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);

							}
						}
					
					if (!CommonsUtil.semValor(debitosJudiciaisRequest.getValores())) {
						DrCalcService drCalcService = new DrCalcService();
						debitosJudiciais = drCalcService.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);
					}
				

				for (Object objProcesso : plexiConsulta.getPlexiWebhookRetorno().getProcessos()) {

							String numeroProcesso = CommonsUtil.somenteNumeros(objProcesso.toString());
							if (objProcesso.toString().indexOf(" ") > -1)
								numeroProcesso = CommonsUtil.somenteNumeros(
										objProcesso.toString().substring(01, objProcesso.toString().indexOf(" ")));
							AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
							DebitosJudiciaisValores debitosJudiciaisValores = null;
							String sLinha = "";
							if (acao != null) {
//								acoesBigData.remove(acao);
								sLinha = "Processo: " + CommonsUtil.formataNumeroProcesso(numeroProcesso) + " - ";

								ProcessoParte processoParte = acao.getParties().stream()
										.filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMANT")).findFirst()
										.orElse(null);
								if (processoParte != null)
									sLinha = sLinha + processoParte.getName();
								sLinha = sLinha + " - Valor - " + CommonsUtil
										.formataValorMonetario(CommonsUtil.bigDecimalValue(acao.getValue()), "");


								debitosJudiciaisValores = debitosJudiciais.getValores().stream()
										.filter(d -> CommonsUtil.mesmoValor(d.getDescricao(),
												CommonsUtil.formataNumeroProcesso(acao.getNumber())))
										.findFirst().orElse(null);
								if (debitosJudiciaisValores != null) {
									sLinha = sLinha + " - valor em  " + debitosJudiciais.getMes() + " de "
											+ debitosJudiciais.getAno() + ": "
											+ CommonsUtil.formataValorMonetario(debitosJudiciaisValores.getTotal(), "");
								}

								pdfLines.add(sLinha);
							} else {
								sLinha = "Processo: " +  CommonsUtil.formataNumeroProcesso(numeroProcesso) + " - não listado na Consulta Processos";
								pdfLines.add(sLinha);
							}

							criarProcessoContrato(plexiConsulta.getDocumentoAnalise(), acao, debitosJudiciaisValores,
									sLinha, (!CommonsUtil.semValor(acao))?acao.getNumber():CommonsUtil.formataNumeroProcesso(numeroProcesso), "Plexi - TRT15");
				}
				certidoesPaju.setDebitosDocumento(pdfLines);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int getPosicaoLinha(List<String> pdfLines, String pequisa) {
		int iPosicao = -1;
		Optional<String> linhaLocalizada = pdfLines.stream()
				.filter(x -> x.trim().replaceAll("  +", " ").contains(pequisa.trim().replaceAll("  +", " ")))
				.findFirst();
		if (linhaLocalizada.isPresent())
			iPosicao = pdfLines.indexOf(linhaLocalizada.get());

		return iPosicao;
	}

	private void populaParagrafoCertidoesPJ(WordprocessingMLPackage docTemplate, ContratoTipoTemplateBloco bloco,
			Set<DocumentoAnalise> participantes, DocketRetornoConsulta docketRetornoConsulta,
			Map<String, List<String>> mapProcesos) throws SiscoatException {

		List<P> paragrafoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getTagIdentificacao());
		List<P> paragrafoDocumentoTemplate = getParagrafoDocumentoTemplate(bloco, paragrafoTemplate);
		ajustaParagrafoTemplate(docTemplate, paragrafoTemplate, paragrafoDocumentoTemplate);

//		List<P> paragrafoDocumentoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getBlocosFilho().get(0).getTagIdentificacao());

		if (paragrafoTemplate == null) {
			return;
		}

		final List<String> nomesParticipantes = participantes.stream().map(p -> p.getPagador().getNome()).distinct()
				.collect(Collectors.toList());

		for (DocumentoAnalise participante : participantes) {
			ProcessoResult bigData = null;
			if (CommonsUtil.mesmoValor(participante.getTipoProcesso(), "B")) {
				bigData = GsonUtil.fromJson(participante.getRetornoProcesso(), ProcessoResult.class);
			}

			adicionaParagrafo(docTemplate, paragrafoTemplate, bloco, participante);
			if (!CommonsUtil.semValor(docketRetornoConsulta)) {

				List<DocketDocumento> documentosParticipante = docketRetornoConsulta.getPedido().getDocumentos();
				List<DocketDocumento> documentosParticipanteFiltro = documentosParticipante.stream()
						.filter(d -> CommonsUtil.mesmoValor(d.getCampos().getCpf(),
								CommonsUtil.somenteNumeros(participante.getCnpjcpf())))
						.collect(Collectors.toList());
				for (DocketDocumento docketDocumento : documentosParticipanteFiltro) {

					ContratoTipoTemplateBloco blocoFilho = bloco
							.getBlocosFilho().stream().filter(b -> CommonsUtil
									.mesmoValor(b.getCodigoTipoTemplateBloco(), BLOCO_PESSOA_JURIDICA_DOCUMENTOS))
							.findFirst().orElse(null);
					if (!CommonsUtil.semValor(blocoFilho)) {
						DocketService docketService = new DocketService();

						String idCallManager = docketDocumento.getArquivos().get(0).getLinks().get(0).getHref()
								.substring(docketDocumento.getArquivos().get(0).getLinks().get(0).getHref()
										.lastIndexOf("/") + 1);
						List<String> pdfLines = lerCND(docketService.getPdfBase64Web(idCallManager));
						CertidoesPaju certidoesPaju = new CertidoesPaju();

						// split by whitespace
						StringBuilder sb = new StringBuilder();
						boolean processos = false;
						for (String line : pdfLines) {

							if (processos && line.contains("Total de A"))
								break;
							if (processos && !CommonsUtil.semValor(line)) {
								final String numeroProcsso = CommonsUtil.somenteNumeros(line);
								AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcsso);

								if (acao != null) {
									String sLinha = "Processo: " + line.trim() + " - ";

									ProcessoParte processoParte = acao.getParties().stream()
											.filter(p -> CommonsUtil.mesmoValor(p.getType(), "CLAIMANT")).findFirst()
											.orElse(null);
									if (processoParte != null)
										sLinha = sLinha + processoParte.getName();
									sLinha = sLinha + " - Valor - " + CommonsUtil
											.formataValorMonetario(CommonsUtil.bigDecimalValue(acao.getValue()), "");

									DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
									debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));
									DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
									debitosJudiciaisRequestValor
											.setDescricao(CommonsUtil.formataNumeroProcesso(acao.getNumber()));
									debitosJudiciaisRequestValor
											.setVencimento("0101" + DateUtil.getAnoProcesso(line.trim()));
									debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

									debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);

									DrCalcService drCalcService = new DrCalcService();
									DebitosJudiciais debitosJudiciais = drCalcService
											.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);

									if (debitosJudiciais != null) {

										sLinha = sLinha + " - valor em  " + debitosJudiciais.getMes() + " de "
												+ debitosJudiciais.getAno() + ": " + CommonsUtil.formataValorMonetario(
														debitosJudiciais.getValores().get(0).getTotal(), "");

									}

									certidoesPaju.getDebitosDocumento().add(sLinha);
								} else {
									String sLinha = "Processo: " + line.trim() + " - não listado na Consulta Processos";
									certidoesPaju.getDebitosDocumento().add(sLinha);
								}

//								certidoesPaju.getDebitosDocumento().add(line);
							}
							if (line.contains("conforme listagem abaixo:"))
								processos = true;

						}

						adicionaParagrafoDocket(docTemplate, paragrafoDocumentoTemplate, blocoFilho, docketDocumento,
								certidoesPaju, bigData);
					}
				}
			}

			if (!CommonsUtil.semValor(participante.getPlexiConsultas())) {

				for (PlexiConsulta plexiConsulta : participante.getPlexiConsultas()) {

					ContratoTipoTemplateBloco blocoFilho = bloco
							.getBlocosFilho().stream().filter(b -> CommonsUtil
									.mesmoValor(b.getCodigoTipoTemplateBloco(), BLOCO_PESSOA_JURIDICA_DOCUMENTOS_PLEXI))
							.findFirst().orElse(null);
					if (!CommonsUtil.semValor(blocoFilho)) {

						if (!CommonsUtil.semValor(plexiConsulta.getPlexiWebhookRetorno())
								&& !CommonsUtil.booleanValue(plexiConsulta.getPlexiWebhookRetorno().getError())
								&& CommonsUtil.booleanValue(plexiConsulta.getPlexiDocumentos().isMostrarPaju())) {

							CertidoesPaju certidoesPaju = new CertidoesPaju();

							if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 31L)) {
								processaCertidaoTJSP_1Grau(plexiConsulta, certidoesPaju);
							} else if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 42L)) {
								processaCertidaoTRT2_PJe(plexiConsulta, bigData, certidoesPaju);
							} else if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 35L)) {
								processaCertidaoTRF3_PJe(plexiConsulta, bigData, certidoesPaju);
							}
							adicionaParagrafoPlexi(docTemplate, paragrafoDocumentoTemplate, blocoFilho, plexiConsulta,
									certidoesPaju, bigData, mapProcesos, nomesParticipantes, participante);
						}
					}
				}
			}

			if (!CommonsUtil.semValor(participante.getNetrinConsultas())) {
				for (NetrinConsulta netrinConsulta : participante.getNetrinConsultas()) {

					ContratoTipoTemplateBloco blocoFilho = bloco.getBlocosFilho().stream().filter(b -> CommonsUtil
							.mesmoValor(b.getCodigoTipoTemplateBloco(), BLOCO_PESSOA_JURIDICA_DOCUMENTOS_NETRIN))
							.findFirst().orElse(null);
					if (!CommonsUtil.semValor(blocoFilho)) {
						if (!CommonsUtil.semValor(netrinConsulta.getRetorno())
								&& CommonsUtil.booleanValue(netrinConsulta.getNetrinDocumentos().isMostrarPaju()))
							adicionaParagrafoNetrin(docTemplate, paragrafoDocumentoTemplate, blocoFilho,
									netrinConsulta);
					}
				}
			}

//			adicionaParagrafo(docTemplate, paragrafoDocumentoTemplate, bloco, documentosParticipanteFiltro);

		}

		if (paragrafoTemplate != null) {
			removeParagrafo(docTemplate, paragrafoDocumentoTemplate);
			removeParagrafo(docTemplate, paragrafoTemplate);
		}

	}

	private void removeParagrafo(WordprocessingMLPackage template, List<P> paragrafos) {
		for (P paragrafo : paragrafos) {
			((ContentAccessor) paragrafo.getParent()).getContent().removeAll(paragrafos);
		}
	}

	private void removeParagrafo(WordprocessingMLPackage template, P paragrafo) {
		((ContentAccessor) paragrafo.getParent()).getContent().remove(paragrafo);
	}

	private void populaParagrafoPessoaConsulta(WordprocessingMLPackage docTemplate, ContratoTipoTemplateBloco bloco)
			throws SiscoatException {

		P paragrafoTemplate = getParagrafoTemplate(docTemplate, bloco.getTagIdentificacao());

		if (paragrafoTemplate == null) {
			return;
		}

//		for (PessoaParticipacao participante : participantes) {
//			PessoaFisica pf = participante.getParticipante().getPessoaFisica().get(0);
//			adicionaParagrafo(docTemplate, paragrafoTemplate, bloco, pf);
//
//		}

		if (paragrafoTemplate != null) {
			wordUtil.removeParagrafo(docTemplate, paragrafoTemplate);
		}

	}

	private P getParagrafoTemplate(WordprocessingMLPackage template, String tagBusca) {

		List<Object> tables = wordUtil.getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

		for (Object tbl : tables) {

			// 1. get the paragraph
			List<Object> paragraphs = wordUtil.getAllElementFromObject(tbl, P.class);

			for (Object p : paragraphs) {
				List<Object> texts = wordUtil.getAllElementFromObject(p, Text.class);
				for (Object t : texts) {
					Text content = (Text) t;
					if (content.getValue() != null && ((String) content.getValue()).contains(tagBusca)) {
						return (P) p;
					}
				}
			}
		}

		return null;
	}

	private List<P> getParagrafoBlocoTemplate(WordprocessingMLPackage template, String tagBusca) {

		List<Object> tables = wordUtil.getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
		List<P> retorno = new ArrayList<>();
		for (Object tbl : tables) {

			// 1. get the paragraph
			List<Object> paragraphs = wordUtil.getAllElementFromObject(tbl, P.class);
			boolean achou = false;
			for (Object p : paragraphs) {
				List<Object> texts = wordUtil.getAllElementFromObject(p, Text.class);
				for (Object t : texts) {
					Text content = (Text) t;
					if (content.getValue() != null && ((String) content.getValue()).contains(tagBusca)) {
						achou = true; // return (P) p;
					}
					// fim do bloco
					if (content.getValue() != null && ((String) content.getValue()).contains("/" + tagBusca)) {
						achou = false;
						retorno.add((P) p);
						return retorno;
					}
				}
				if (achou) {
					retorno.add((P) p);
				}
			}
		}

		return null;
	}

	private void adicionaParagrafo(WordprocessingMLPackage template, List<P> paragrafoTemplate,
			ContratoTipoTemplateBloco bloco, Object dataSource) throws SiscoatException {

		List<P> copy = new ArrayList<>();
		// 3. copy the found paragraph to keep styling correct
		for (P p : paragrafoTemplate) {
			if (!p.toString().contains(bloco.getTagIdentificacao()))
				copy.add((P) XmlUtils.deepCopy(p));
		}
//		List<P> copy =  XmlUtils.deepCopy(paragrafoTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {
			if (CommonsUtil.semValor(campo))
				continue;

			Object valor;
			if (!campo.getExpressao().startsWith("\""))
				valor = getValor(campo.getExpressao(), dataSource);
			else
				valor = campo.getExpressao().replace("\"", "");

			if (CommonsUtil.semValor((String) valor)
					&& CommonsUtil.mesmoValorIgnoreCase("situacao", campo.getExpressao())) {
				valor = "VERIFICAR CERTIDÃO";
			}

			// if (valor != null) {
			for (P p : copy) {
				if (!CommonsUtil.semValor((String) valor))
					replacePlaceholder(p, campo.getTag(), (String) valor);
				else
					replacePlaceholder(p, campo.getTag(), "");
			}
			// }
		}

		// add the paragraph to the document
		((ContentAccessor) paragrafoTemplate.get(0).getParent()).getContent().addAll(copy);

		addParagrafoVazio((ContentAccessor) paragrafoTemplate.get(0).getParent());
	}

	private void adicionaParagrafoDocket(WordprocessingMLPackage template, List<P> paragrafoTemplate,
			ContratoTipoTemplateBloco bloco, Object dataSource, CertidoesPaju certidoesPaju, ProcessoResult bigData)
			throws SiscoatException {

		List<P> copy = new ArrayList<>();
		for (P p : paragrafoTemplate) {
			if (!p.toString().contains(bloco.getTagIdentificacao())) {
				boolean addParagrafo = true;
				if ((p.toString().contains("processosIdentificacaoCertidoesDocumento")
						|| p.toString().contains("pfCertidoesDocumento")
						|| p.toString().contains("pjCertidoesDocumento"))
						&& (CommonsUtil.semValor(certidoesPaju)
								|| CommonsUtil.semValor(certidoesPaju.getDebitosDocumento())))
					addParagrafo = false;
				else if ((p.toString().contains("processosIdentificacaoCertidoesSimilariedade")
						|| p.toString().contains("pfCertidoesSimilariedade")
						|| p.toString().contains("pjCertidoesSimilariedade"))
						&& (CommonsUtil.semValor(certidoesPaju)
								|| CommonsUtil.semValor(certidoesPaju.getDebitosSimilariedade())))
					addParagrafo = false;
				else if (p.toString().contains("situacao") && !CommonsUtil.semValor(certidoesPaju)
						&& (!CommonsUtil.semValor(certidoesPaju.getDebitosDocumento())
								|| !CommonsUtil.semValor(certidoesPaju.getDebitosSimilariedade()))) {
					addParagrafo = false;
				}

				if (addParagrafo) {
					copy.add((P) XmlUtils.deepCopy(p));
				}
			}
		}

//		for (P p : paragrafoTemplate) {
//			if (!p.toString().contains(bloco.getTagIdentificacao())) {
//				boolean addParagrafo = true;
//				if ((p.toString().contains("processosIdentificacaoCertidoesDocumento")
//						|| p.toString().contains("pfCertidoesDocumento")
//						|| p.toString().contains("pjCertidoesDocumento"))
////						&& CommonsUtil.semValor(debitosDocumento)
//				)
//					addParagrafo = false;
//				else if ((p.toString().contains("processosIdentificacaoCertidoesSimilariedade")
//						|| p.toString().contains("pfCertidoesSimilariedade")
//						|| p.toString().contains("pjCertidoesSimilariedade"))
////						&& CommonsUtil.semValor(debitosSimilariedade)
//				)
//					addParagrafo = false;
//
//				if (addParagrafo) {
//					copy.add((P) XmlUtils.deepCopy(p));
//				}
//			}
//		}

//		List<P> copy =  XmlUtils.deepCopy(paragrafoTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {
			if (CommonsUtil.semValor(campo))
				continue;

			Object valor;

			if (!campo.getExpressao().startsWith("\"")) {
				if (CommonsUtil.mesmoValor(campo.getTag(), "pfCertidoesDocumento")) {
					if (!CommonsUtil.semValor(certidoesPaju)
							&& !CommonsUtil.semValor(certidoesPaju.getDebitosDocumento())) {

						P paragraph = new P();
						for (String certidao : certidoesPaju.getDebitosDocumento()) {
							R run = new R();
							run.getContent().add(createText((String) certidao));
							// Adicionar quebra de linha
							Br lineBreak = new Br();
							lineBreak.setType(STBrType.TEXT_WRAPPING);
							run.getContent().add(lineBreak);
							run.getContent().add(lineBreak);
							paragraph.getContent().add(run);
						}
						valor = paragraph;

					} else
						valor = null;
				} else if (CommonsUtil.mesmoValor(campo.getTag(), "pfCertidoesSimilariedade")) {
					if (!CommonsUtil.semValor(certidoesPaju)
							&& !CommonsUtil.semValor(certidoesPaju.getDebitosSimilariedade())) {
						P paragraph = new P();
						for (String certidao : certidoesPaju.getDebitosDocumento()) {
							R run = new R();
							run.getContent().add(createText(certidao));
							// Adicionar quebra de linha
							Br lineBreak = new Br();
							lineBreak.setType(STBrType.TEXT_WRAPPING);
							run.getContent().add(lineBreak);
							run.getContent().add(lineBreak);
							paragraph.getContent().add(run);

						}
						valor = paragraph;
					} else
						valor = null;
				} else
					valor = getValor(campo.getExpressao(), dataSource);
			} else
				valor = campo.getExpressao().replace("\"", "");

//			if (CommonsUtil.semValor((String) valor)
//					&& CommonsUtil.mesmoValorIgnoreCase("situacao", campo.getExpressao())) {
//				valor = "VERIFICAR CERTIDÃO";
//			}
			if (CommonsUtil.semValor(valor)
					|| (valor.getClass().getName().equals("java.lang.String") && CommonsUtil.semValor((String) valor))
							&& CommonsUtil.mesmoValorIgnoreCase("situacao", campo.getExpressao())) {
				valor = "VERIFICAR CERTIDÃO";
			}

			for (P p : copy) {

				List<Object> texts = wordUtil.getAllElementFromObject(p, Text.class);

				boolean substitui = true;
				boolean achou = false;
				for (Object o2 : texts) {
					if (((Text) o2).getValue().contains(campo.getTag()))
						achou = true;
					break;
				}
				if (achou) {
//				if (campo.getTag().contains("pfCertidoesDocumento") || campo.getTag().contains("pjCertidoesDocumento")
//						|| campo.getTag().contains("pfCertidoesSimilariedade")
//						|| campo.getTag().contains("pjCertidoesSimilariedade")) {
					if (!CommonsUtil.semValor(valor)) {
						if (!valor.getClass().getName().equals("java.lang.String")) {
							P pNovo = (P) valor;
							p.getContent().clear();
							p.getContent().addAll(pNovo.getContent());
							substitui = false;
						}
					} else
						valor = "";
				}

				if (substitui && valor.getClass().getName().equals("java.lang.String")) {
					if (!CommonsUtil.semValor((String) valor))
						replacePlaceholder(p, campo.getTag(), (String) valor);
					else
						replacePlaceholder(p, campo.getTag(), "");
				}
			}

//			for (P p : copy) {
//				if (!CommonsUtil.semValor((String) valor))
//					replacePlaceholder(p, campo.getTag(), (String) valor);
//				else
//					replacePlaceholder(p, campo.getTag(), "");
//			}
			// }
		}

		// add the paragraph to the document
		((ContentAccessor) paragrafoTemplate.get(0).getParent()).getContent().addAll(copy);

		addParagrafoVazio((ContentAccessor) paragrafoTemplate.get(0).getParent());
	}

	private void adicionaParagrafoPlexi(WordprocessingMLPackage template, List<P> paragrafoTemplate,
			ContratoTipoTemplateBloco bloco, PlexiConsulta plexiConsulta, CertidoesPaju certidoesPaju,
			ProcessoResult bigData, Map<String, List<String>> mapProcesos, final List<String> nomesParticipantes,
			DocumentoAnalise participante) throws SiscoatException {

		List<P> copy = new ArrayList<>();
		// 3. copy the found paragraph to keep styling correct
		for (P p : paragrafoTemplate) {
			if (!p.toString().contains(bloco.getTagIdentificacao())) {
				boolean addParagrafo = true;
				if ((p.toString().contains("processosIdentificacaoCertidoesDocumento")
						|| p.toString().contains("pfCertidoesDocumento")
						|| p.toString().contains("pjCertidoesDocumento"))
						&& (CommonsUtil.semValor(certidoesPaju)
								|| CommonsUtil.semValor(certidoesPaju.getDebitosDocumento())))
					addParagrafo = false;
				else if ((p.toString().contains("processosIdentificacaoCertidoesSimilariedade")
						|| p.toString().contains("pfCertidoesSimilariedade")
						|| p.toString().contains("pjCertidoesSimilariedade"))
						&& (CommonsUtil.semValor(certidoesPaju)
								|| CommonsUtil.semValor(certidoesPaju.getDebitosSimilariedade())))
					addParagrafo = false;
				else if (p.toString().contains("situacao") && !CommonsUtil.semValor(certidoesPaju)
						&& (!CommonsUtil.semValor(certidoesPaju.getDebitosDocumento())
								|| !CommonsUtil.semValor(certidoesPaju.getDebitosSimilariedade()))) {
					addParagrafo = false;
				}

				if (addParagrafo) {
					copy.add((P) XmlUtils.deepCopy(p));
				}
			}
		}

//		List<P> copy =  XmlUtils.deepCopy(paragrafoTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {
			if (CommonsUtil.semValor(campo))
				continue;
			Object valor = null;
			if (!campo.getExpressao().startsWith("\"")) {
				if (CommonsUtil.mesmoValor(campo.getTag(), "pfCertidoesDocumento")
						|| CommonsUtil.mesmoValor(campo.getTag(), "pjCertidoesDocumento")) {
					if (!CommonsUtil.semValor(certidoesPaju)
							&& !CommonsUtil.semValor(certidoesPaju.getDebitosDocumento())) {
						if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 31L))
							valor = getPlexiProcessosCertidaoSP(certidoesPaju.getDebitosDocumento(), bigData,
									mapProcesos, nomesParticipantes, participante);
						else {
							P paragraph = null;
							for (String certidao : certidoesPaju.getDebitosDocumento()) {
								if (paragraph == null) {
									paragraph = createParagraph((String) certidao);
								} else {
									R run = new R();
									run.getContent().add(createText((String) certidao));
									// Adicionar quebra de linha
									Br lineBreak = new Br();
									lineBreak.setType(STBrType.TEXT_WRAPPING);
									run.getContent().add(lineBreak);
									run.getContent().add(lineBreak);
									paragraph.getContent().add(run);
								}

							}
							valor = paragraph;
						}

					} else
						valor = null;
				} else if (CommonsUtil.mesmoValor(campo.getTag(), "pfCertidoesSimilariedade")
						|| CommonsUtil.mesmoValor(campo.getTag(), "pjCertidoesSimilariedade")) {
					if (!CommonsUtil.semValor(certidoesPaju)
							&& !CommonsUtil.semValor(certidoesPaju.getDebitosSimilariedade())) {
						if (CommonsUtil.mesmoValor(plexiConsulta.getPlexiDocumentos().getId(), 31L))
							valor = getPlexiProcessosCertidaoSP(certidoesPaju.getDebitosSimilariedade(), bigData,
									mapProcesos, nomesParticipantes, participante);
						else {
							P paragraph = new P();
							for (String certidao : certidoesPaju.getDebitosDocumento()) {
								R run = new R();
								run.getContent().add(createText(certidao));
								// Adicionar quebra de linha
								Br lineBreak = new Br();
								lineBreak.setType(STBrType.TEXT_WRAPPING);
								run.getContent().add(lineBreak);
								run.getContent().add(lineBreak);
								paragraph.getContent().add(run);

							}
							valor = paragraph;
						}
					} else
						valor = null;
				} else
					valor = getValor(campo.getExpressao(), plexiConsulta);
			} else
				valor = campo.getExpressao().replace("\"", "");

			if (CommonsUtil.semValor(valor)
					|| (valor.getClass().getName().equals("java.lang.String") && CommonsUtil.semValor((String) valor))
							&& CommonsUtil.mesmoValorIgnoreCase("situacao", campo.getExpressao())) {
				valor = "";
			}

			// if (valor != null) {
			for (P p : copy) {

				List<Object> texts = wordUtil.getAllElementFromObject(p, Text.class);

				boolean substitui = true;
				boolean achou = false;
				for (Object o2 : texts) {
					if (((Text) o2).getValue().contains(campo.getTag()))
						achou = true;
					break;
				}
				if (achou) {
//				if (campo.getTag().contains("pfCertidoesDocumento") || campo.getTag().contains("pjCertidoesDocumento")
//						|| campo.getTag().contains("pfCertidoesSimilariedade")
//						|| campo.getTag().contains("pjCertidoesSimilariedade")) {
					if (!CommonsUtil.semValor(valor)) {
						if (!valor.getClass().getName().equals("java.lang.String")) {
							P pNovo = (P) valor;
							p.getContent().clear();
							p.getContent().addAll(pNovo.getContent());
							substitui = false;
						}
					} else
						valor = "";
				}

				if (substitui && valor.getClass().getName().equals("java.lang.String")) {
					if (!CommonsUtil.semValor((String) valor))
						replacePlaceholder(p, campo.getTag(), (String) valor);
					else
						replacePlaceholder(p, campo.getTag(), "");
				}
			}
			// }
		}

		// add the paragraph to the document
		((ContentAccessor) paragrafoTemplate.get(0).getParent()).getContent().addAll(copy);

		addParagrafoVazio((ContentAccessor) paragrafoTemplate.get(0).getParent());
	}

	private Object getPlexiProcessosCertidaoSP(List<String> certidoes, ProcessoResult bigData,
			Map<String, List<String>> mapProcesos, final List<String> nomesParticipantes,
			DocumentoAnalise participante) {
		Object valor;

		P paragraph = new P();
		;

		valor = "";
		for (String debito : certidoes) {
			valor = valor + debito;
		}
		valor = ((String) valor);
		String[] splitProcessos = ((String) valor).split("»");
		valor = "";

		DebitosJudiciaisRequest debitosJudiciaisRequest = new DebitosJudiciaisRequest();
		debitosJudiciaisRequest.setHonorario(CommonsUtil.bigDecimalValue(10));

		for (String debito : splitProcessos) {
			if (debito.indexOf("Processo:") > -1) {
				String sLinha = debito.substring(debito.indexOf("Processo:")).replace("*", "");
				while (sLinha.indexOf("  ") > -1) {
					sLinha = sLinha.replace("  ", " ");
				}
				sLinha = sLinha.substring(0, sLinha.lastIndexOf(".") + 1);
				String numeroProcesso = sLinha.substring(10, 35);
				AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);
				if (acao != null) {
					DebitosJudiciaisRequestValor debitosJudiciaisRequestValor = new DebitosJudiciaisRequestValor();
					debitosJudiciaisRequestValor.setDescricao(CommonsUtil.formataNumeroProcesso(acao.getNumber()));
					debitosJudiciaisRequestValor.setVencimento("0101" + DateUtil.getAnoProcesso(acao.getNumber()));
					debitosJudiciaisRequestValor.setValor(CommonsUtil.bigDecimalValue(acao.getValue()));

					debitosJudiciaisRequest.getValores().add(debitosJudiciaisRequestValor);
				}
			}
		}
		DebitosJudiciais debitosJudiciais = null;
		if (!CommonsUtil.semValor(debitosJudiciaisRequest.getValores())) {
			DrCalcService drCalcService = new DrCalcService();
			debitosJudiciais = drCalcService.criarConsultaAtualizacaoMonetaria(debitosJudiciaisRequest);
		}

		for (String debito : splitProcessos) {
			String sLinha = "";
			if (debito.indexOf("Processo:") > -1) {
				sLinha = debito.substring(debito.indexOf("Processo:")).replace("*", "");
				while (sLinha.indexOf("  ") > -1) {
					sLinha = sLinha.replace("  ", " ");
				}
				sLinha = sLinha.substring(0, sLinha.lastIndexOf(".") + 1);
				String numeroProcesso = sLinha.substring(10, 35);
				AcaoJudicial acao = bigData.getAcaoJudicial(numeroProcesso);

				sLinha = getDadosAcaoBigData(mapProcesos, nomesParticipantes, participante, numeroProcesso, acao,
						debitosJudiciais, sLinha, "Plexi - TJSP 1Grau");

				R run = new R();
				run.getContent().add(createText(sLinha));
				// Adicionar quebra de linha
				Br lineBreak = new Br();
				lineBreak.setType(STBrType.TEXT_WRAPPING);
				run.getContent().add(lineBreak);
				run.getContent().add(lineBreak);
				paragraph.getContent().add(run);
//				}
//				paragraph.getContent().add(createText((String) valor));
				DebitosJudiciaisValores debitosJudiciaisValores = null;
				if (!CommonsUtil.semValor(debitosJudiciais)) {
					debitosJudiciaisValores = debitosJudiciais.getValores().stream().filter(d -> CommonsUtil
							.mesmoValor(d.getDescricao(), CommonsUtil.formataNumeroProcesso(numeroProcesso)))
							.findFirst().orElse(null);
				}
				criarProcessoContrato(participante, acao, debitosJudiciaisValores, sLinha, numeroProcesso,
						"Plexi - TJSP 1Grau");
			}
		}
		return paragraph;
	}

	private static P createParagraph(String text) {
		P paragraph = new P();
		R run = new R();
		run.getContent().add(createText(text));

		paragraph.getContent().add(run);
		return paragraph;
	}

	private static Text createText(String text) {
		Text t = new Text();
		t.setValue(text);
		return t;
	}

	private void adicionaParagrafoNetrin(WordprocessingMLPackage template, List<P> paragrafoTemplate,
			ContratoTipoTemplateBloco bloco, NetrinConsulta netrinConsulta) throws SiscoatException {

		List<P> copy = new ArrayList<>();

		for (P p : paragrafoTemplate) {
			if (!p.toString().contains(bloco.getTagIdentificacao())) {
				boolean addParagrafo = true;
				if ((p.toString().contains("processosIdentificacaoCertidoesDocumento")
						|| p.toString().contains("pfCertidoesDocumento")
						|| p.toString().contains("pjCertidoesDocumento"))
//						&& CommonsUtil.semValor(debitosDocumento)
				)
					addParagrafo = false;
				else if ((p.toString().contains("processosIdentificacaoCertidoesSimilariedade")
						|| p.toString().contains("pfCertidoesSimilariedade")
						|| p.toString().contains("pjCertidoesSimilariedade"))
//						&& CommonsUtil.semValor(debitosSimilariedade)
				)
					addParagrafo = false;

				if (addParagrafo) {
					copy.add((P) XmlUtils.deepCopy(p));
				}
			}
		}

//		List<P> copy =  XmlUtils.deepCopy(paragrafoTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {
			if (CommonsUtil.semValor(campo))
				continue;

			Object valor = null;
			if (!campo.getExpressao().startsWith("\""))
				valor = getValor(campo.getExpressao(), netrinConsulta);
			else
				valor = campo.getExpressao().replace("\"", "");

			if (CommonsUtil.semValor((String) valor)
					&& CommonsUtil.mesmoValorIgnoreCase("situacao", campo.getExpressao())) {
				valor = "VERIFICAR CERTIDÃO";
			}

			// if (valor != null) {
			for (P p : copy) {
				if (!CommonsUtil.semValor((String) valor))
					replacePlaceholder(p, campo.getTag(), (String) valor);
				else
					replacePlaceholder(p, campo.getTag(), "");
			}
			// }
		}

		// add the paragraph to the document
		((ContentAccessor) paragrafoTemplate.get(0).getParent()).getContent().addAll(copy);

		addParagrafoVazio((ContentAccessor) paragrafoTemplate.get(0).getParent());
	}

	private void addParagrafoVazio(ContentAccessor place2Add) {
		ObjectFactory factory = Context.getWmlObjectFactory();

		P spc = factory.createP();

		place2Add.getContent().add(spc);
	}

	// ************************************************************************
	// *** Rotinas para calculo de expressão

	public static String getEnderecoImovel(ImovelCobranca imovel) throws SiscoatException {

		if (imovel != null) {
			String endereco = imovel.getEndereco().trim()
					+ (CommonsUtil.semValor(imovel.getComplemento()) ? "" : " " + imovel.getComplemento().trim())
					+ (CommonsUtil.semValor(imovel.getBairro()) ? "" : " - " + imovel.getBairro().trim() + " -") + " "
					+ imovel.getCidade().trim() + " - " + imovel.getEstado().trim() + " - Cep: "
					+ CommonsUtil.formataCEP(imovel.getCep());

			return endereco;
		}

		return "";

	}

	public static String getCidadeImovel(ImovelCobranca imovel) throws SiscoatException {
//		PessoaEnderecoVO ender = getPessoaEndereco(pessoa);

		if (imovel != null) {
			return imovel.getCidade();
		}

		return "";
	}

	public static String getUfImovel(ImovelCobranca imovel) throws SiscoatException {
		if (imovel != null) {
			return imovel.getEstado();
		}

		return "";
	}

	private WordprocessingMLPackage carregaTemplate(File arq) throws Docx4JException, FileNotFoundException {
		return WordprocessingMLPackage.load(new FileInputStream(arq));
	}

	private Object getValor(String expressao) throws SiscoatException {

		return getValor(expressao, this);

	}

	private Object getValor(String expressao, Object o) throws SiscoatException {

		try {
			if (o == null) {
				o = this;
			}

			Expression exp = parser.parseExpression(expressao);

			return exp.getValue(context, o);

		} catch (ParseException e) {
			throw new SiscoatException("Erro ao calcular valor de expressão: " + expressao, e);
		} catch (EvaluationException e) {
			return null;
		}
	}

	private void replacePlaceholder(ContentAccessor place2Replace, String placeholder, String valor) {
		List<Object> texts = wordUtil.getAllElementFromObject(place2Replace, Text.class);

		for (Object text : texts) {
			Text textElement = (Text) text;
			if (textElement.getValue() != null && textElement.getValue().contains("#{" + placeholder + "}")) {
				String novoPlaceholder = "#\\{" + placeholder + "\\}";
				String novoTexto = textElement.getValue().replaceAll(novoPlaceholder,
						valor == null ? "" : valor.trim());
				if (!textElement.getValue().equals(novoTexto)) {
					textElement.setValue(novoTexto);
				}
			}
		}

	}

	private void replacePlaceholderAtHeader(WordprocessingMLPackage template, String placeholder, String valor) {

		RelationshipsPart rp = template.getMainDocumentPart().getRelationshipsPart();

		HeaderPart rpHeaderPart = null;

		for (Relationship r : rp.getRelationships().getRelationship()) {

			if (r.getType().equals(Namespaces.HEADER)) {
				rpHeaderPart = (HeaderPart) rp.getPart(r);
			}
		}

		if (rpHeaderPart != null) {

			List<Object> texts = wordUtil.getAllElementFromObject(rpHeaderPart.getJaxbElement(), Text.class);

			for (Object text : texts) {
				Text textElement = (Text) text;
				String novoTexto = textElement.getValue().replaceAll("#\\{" + placeholder + "\\}", valor);

				if (!textElement.getValue().equals(novoTexto)) {
					textElement.setValue(novoTexto);
				}
			}
		}

	}

	public void criarProcessoContrato(DocumentoAnalise participante, AcaoJudicial acao,
			DebitosJudiciaisValores debitosJudiciaisValores, String sLinha, String numeroProcesso, String origem) {
		CcbProcessosJudiciaisDao processosJudiciaisDao = new CcbProcessosJudiciaisDao();
		CcbProcessosJudiciais processo = listProcessos.stream().filter(p -> CommonsUtil
				.mesmoValor(CommonsUtil.somenteNumeros(p.getNumero()), CommonsUtil.somenteNumeros(numeroProcesso)))
				.findFirst().orElse(null);
		BigDecimal valorAtualizado = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(debitosJudiciaisValores) && !CommonsUtil.semValor(debitosJudiciaisValores.getTotal()))
			valorAtualizado = debitosJudiciaisValores.getTotal();
		if (CommonsUtil.semValor(processo)) {
			if (!participante.getMotivoAnalise().contains("Proprietario Atual")) {
				return;
			}
			processo = processosJudiciaisDao.getProcessosExistentes(CommonsUtil.formataNumeroProcesso(numeroProcesso),
					participante.getContratoCobranca());
			if (processo.getId() <= 0) {
				String obs = "";
				BigDecimal valor = BigDecimal.ZERO;
				String natureza = "";
				if (CommonsUtil.semValor(acao)) {
					obs = obs + "Processo não localizado na consulta de processos.";
				} else {
					if (CommonsUtil.semValor(acao.getValue())) {
						if (!CommonsUtil.semValor(obs))
							obs = obs + " \n";
						obs = obs + "Valor: sem valor localizado.";
					} else {
						valor = CommonsUtil.bigDecimalValue(acao.getValue());
					}

					if (CommonsUtil.semValor(acao.getMainSubject())) {
						if (!CommonsUtil.semValor(obs))
							obs = obs + " \n";
						obs = obs + "Natureza: sem natureza localizada.";
						natureza = "Natureza não localizada";
					} else {
						natureza = acao.getMainSubject();
					}
				}
				// obs = obs + "\n" + sLinha;
				processo.setPagador(participante.getPagador());
				processo.setNumero(CommonsUtil.formataNumeroProcesso(numeroProcesso));
				processo.setValor(valor);
				processo.setNatureza(natureza);
				processo.setValorAtualizado(valorAtualizado);
				processo.setObservacao(obs);
				processo.setOrigem(origem);
				processo.setContrato(participante.getContratoCobranca());
				processo.setContaPagar(new ContasPagar());
				processo.getContaPagar().setValor(processo.getValorAtualizado());
				processo.getContaPagar().setDescricao("Processo N°: " + processo.getNumero());
				processo.getContaPagar().setFormaTransferencia("Boleto");
				processo.getContaPagar().setNumeroDocumento(participante.getContratoCobranca().getNumeroContrato());
				processo.getContaPagar().setPagadorRecebedor(participante.getContratoCobranca().getPagador());
				processo.getContaPagar().setResponsavel(participante.getContratoCobranca().getResponsavel());
			} else if (valorAtualizado.compareTo(processo.getValorAtualizado()) > 0) {
				processo.setValorAtualizado(valorAtualizado);
				processo.getContaPagar().setValor(valorAtualizado);
			}
			listProcessos.add(processo);
		} else {
			String participantes = processo.getOutrosParticipantes();
			if (CommonsUtil.semValor(participantes)) {
				participantes = "Outros Participantes: ";
				participantes = participantes + participante.getPagador().getNome();
			} else {
				if (!participantes.contains(participante.getPagador().getNome())) {
					participantes = participantes + ", ";
					participantes = participantes + participante.getPagador().getNome();
				}
			}
			processo.setOutrosParticipantes(participantes);
		}
	}

	public void criarProcessoBancoDados() {
		CcbProcessosJudiciaisDao processosJudiciaisDao = new CcbProcessosJudiciaisDao();
		ContasPagarDao cpDao = new ContasPagarDao();
		for (CcbProcessosJudiciais processo : listProcessos) {
			if(CommonsUtil.semValor(processo.getValor()))
				continue;
			if (processo.getContaPagar().getId() <= 0) {
				cpDao.create(processo.getContaPagar());
			} else {
				cpDao.merge(processo.getContaPagar());
			}

			if (processo.getId() <= 0) {
				String obs = processo.getObservacao();
				if (CommonsUtil.semValor(obs)) {
					obs = processo.getOutrosParticipantes();
				} else {
					if (!CommonsUtil.semValor(processo.getOutrosParticipantes())) {
						obs = obs + "\n" + processo.getOutrosParticipantes();
					}
				}
				processo.setObservacao(obs);
				processosJudiciaisDao.create(processo);
			} else {
				processosJudiciaisDao.merge(processo);
			}
		}
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		this.listaDocumentoAnalise = documentoAnaliseDao.listagemDocumentoAnalise(this.contrato);
	}

}
