package com.webnowbr.siscoat.cobranca.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplate;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplateBloco;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplateCampo;
import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocketDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketEstadosDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.template.ContratoTipoTemplateDao;
import com.webnowbr.siscoat.cobranca.model.docket.DocketDocumento;
import com.webnowbr.siscoat.cobranca.model.docket.DocketRetornoConsulta;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.WordUtil;
import com.webnowbr.siscoat.exception.SiscoatException;

public class PajuService {

	private static final String BLOCO_DOCUMENTO = "D";
	private static final String BLOCO_CABECALHO = "C";
	private static final String BLOCO_PESSOA_FISICA_CONSULTA = "PFC";	
	private static final String BLOCO_PESSOA_FISICA_CERTIDOES = "PFCERT";
	private static final String BLOCO_PESSOA_FISICA_DOCUMENTOS = "PFDOCS";	
	
	private static final String BLOCO_PESSOA_JURIDICA_CONSULTA = "PJC";	
	private static final String BLOCO_PESSOA_JURIDICA_CERTIDOES = "PJCERT";
	private static final String BLOCO_PESSOA_JURIDICA_DOCUMENTOS = "PJDOCS";
	
	private ExpressionParser parser;

	private StandardEvaluationContext context;

	private String arquivoWord;

	private ContratoCobranca contrato;
	
	private WordUtil wordUtil;
	private List<DocumentoAnalise> listaDocumentoAnalise;

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

		
		List<DocumentoAnalise> listaDocumentoAnaliseAnalisados =  this.listaDocumentoAnalise.stream().filter(p -> p.isLiberadoAnalise()).collect(Collectors.toList());
		
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
				documentoAnalise.setCnpjcpf(contrato.getPagador().getCpf());;
				documentoAnalise.setTipoPessoa("PF");
				pessoasPF.add(documentoAnalise);
			} else {
				documentoAnalise.setCnpjcpf(contrato.getPagador().getCnpj());
				documentoAnalise.setTipoPessoa("PJ");	
				pessoasPJ.add(documentoAnalise);
			}
		}
		
		
		DocketDao docketDao = new DocketDao();
		String idCallManager = docketDao.consultaContratosPendentesResponsaveis(contrato);
		DocketService docketService = new DocketService();
		DocketRetornoConsulta docketRetornoConsulta = docketService.verificarCertidoesContrato(contrato, idCallManager);
		
		DocketEstadosDao docketEstadosDao = new DocketEstadosDao();
		List<DocketEstados> docketEstados = docketEstadosDao.findAll();
		
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
						
						PagadorRecebedor pessoaConsultaDocket = new PagadorRecebedor();
						pessoaConsultaDocket.setCpf(CommonsUtil.formataCnpjCpf(docketDocumento.getCampos().getCpf(), false));
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
						pessoaConsultaDocket.setCnpj(CommonsUtil.formataCnpjCpf(docketDocumento.getCampos().getCnpj(),false));
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

		for (ContratoTipoTemplateBloco bloco : lstBlocos) {

			if (bloco.getFlagInativo()) {
				continue;
			}

			String tipo = bloco.getCodigoTipoTemplateBloco();

			switch (tipo) {

			case BLOCO_DOCUMENTO:
				
				replacePlaceholder(docTemplate.getMainDocumentPart(), "dataGeracao", (String) CommonsUtil.formataData( DateUtil.getDataHoje() , "dd 'de' MMMM 'de' yyyy"));
				
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
				populaParagrafoCertidoesPF(docTemplate, bloco, pessoasPF, docketRetornoConsulta);
				break;	
				
			case BLOCO_PESSOA_JURIDICA_CERTIDOES:
				populaParagrafoCertidoesPJ(docTemplate, bloco, pessoasPJ, docketRetornoConsulta);
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
		} catch (Exception e) {
			throw new SiscoatException("Erro ao gerar modelo Paju: " , e);
		}
		return baos.toByteArray();

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
			Set<DocumentoAnalise> participantes, DocketRetornoConsulta docketRetornoConsulta) throws SiscoatException {

		List<P> paragrafoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getTagIdentificacao());
		List<P> paragrafoDocumentoTemplate=  new ArrayList<>();
		String startPlaceholder =  "#{" + bloco.getBlocosFilho().get(0).getTagIdentificacao() + "}";
		String endPlaceholder =  "#{/" + bloco.getBlocosFilho().get(0).getTagIdentificacao() + "}";
		
		boolean  bfilho = false;
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
		if ( !CommonsUtil.semValor(paragrafoDocumentoTemplate)) {


			paragrafoTemplate.removeAll(paragrafoDocumentoTemplate);
			
			removeParagrafo(docTemplate, paragrafoDocumentoTemplate.get(0));
			paragrafoDocumentoTemplate.remove(0);
			removeParagrafo(docTemplate, paragrafoDocumentoTemplate.get(paragrafoDocumentoTemplate.size()-1));
			paragrafoDocumentoTemplate.remove(paragrafoDocumentoTemplate.size()-1);
	
			removeParagrafo(docTemplate, paragrafoTemplate.get(0));
			paragrafoTemplate.remove(0);
			removeParagrafo(docTemplate, paragrafoTemplate.get(paragrafoTemplate.size()-1));
			paragrafoTemplate.remove(paragrafoTemplate.size()-1);
		}

//		List<P> paragrafoDocumentoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getBlocosFilho().get(0).getTagIdentificacao());
		
		if (paragrafoTemplate == null) {
			return;
		}

		for (DocumentoAnalise participante : participantes) {
			
			adicionaParagrafo(docTemplate, paragrafoTemplate, bloco, participante);
			if (!CommonsUtil.semValor(docketRetornoConsulta)) {
				List<DocketDocumento> documentosParticipante = docketRetornoConsulta.getPedido().getDocumentos();
				List<DocketDocumento> documentosParticipanteFiltro = documentosParticipante.stream()
						.filter(d -> CommonsUtil.mesmoValor(d.getCampos().getCpf(),
								CommonsUtil.somenteNumeros(participante.getCnpjcpf())))
						.collect(Collectors.toList());
				for (DocketDocumento docketDocumento : documentosParticipanteFiltro) {
					adicionaParagrafo(docTemplate, paragrafoDocumentoTemplate, bloco.getBlocosFilho().get(0),
							docketDocumento);
				}
			}

//			adicionaParagrafo(docTemplate, paragrafoDocumentoTemplate, bloco, documentosParticipanteFiltro);

		}

		if (paragrafoTemplate != null) {

			removeParagrafo(docTemplate, paragrafoDocumentoTemplate);
			removeParagrafo(docTemplate, paragrafoTemplate);
		}

	}

	
	private void populaParagrafoCertidoesPJ(WordprocessingMLPackage docTemplate, ContratoTipoTemplateBloco bloco,
			Set<DocumentoAnalise> participantes, DocketRetornoConsulta docketRetornoConsulta) throws SiscoatException {
		
		List<P> paragrafoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getTagIdentificacao());
		List<P> paragrafoDocumentoTemplate=  new ArrayList<>();
		String startPlaceholder =  "#{" + bloco.getBlocosFilho().get(0).getTagIdentificacao() + "}";
		String endPlaceholder =  "#{/" + bloco.getBlocosFilho().get(0).getTagIdentificacao() + "}";
		
		boolean  bfilho = false;
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
		if ( !CommonsUtil.semValor(paragrafoDocumentoTemplate)) {


			paragrafoTemplate.removeAll(paragrafoDocumentoTemplate);
			
			removeParagrafo(docTemplate, paragrafoDocumentoTemplate.get(0));
			paragrafoDocumentoTemplate.remove(0);
			removeParagrafo(docTemplate, paragrafoDocumentoTemplate.get(paragrafoDocumentoTemplate.size()-1));
			paragrafoDocumentoTemplate.remove(paragrafoDocumentoTemplate.size()-1);
	
			removeParagrafo(docTemplate, paragrafoTemplate.get(0));
			paragrafoTemplate.remove(0);
			removeParagrafo(docTemplate, paragrafoTemplate.get(paragrafoTemplate.size()-1));
			paragrafoTemplate.remove(paragrafoTemplate.size()-1);
		}

//		List<P> paragrafoDocumentoTemplate = getParagrafoBlocoTemplate(docTemplate, bloco.getBlocosFilho().get(0).getTagIdentificacao());
		
		if (paragrafoTemplate == null) {
			return;
		}

		for (DocumentoAnalise participante : participantes) {
			
			adicionaParagrafo(docTemplate, paragrafoTemplate, bloco, participante);
			if(!CommonsUtil.semValor(docketRetornoConsulta)
				&& !CommonsUtil.semValor(docketRetornoConsulta.getPedido())
				&& !CommonsUtil.semValor(docketRetornoConsulta.getPedido().getDocumentos())) {
				
				List<DocketDocumento> documentosParticipante = docketRetornoConsulta.getPedido().getDocumentos();
				List<DocketDocumento> documentosParticipanteFiltro = documentosParticipante.stream().filter(d -> CommonsUtil
						.mesmoValor(d.getCampos().getCnpj(), CommonsUtil.somenteNumeros(participante.getCnpjcpf())))
						.collect(Collectors.toList());
				for (DocketDocumento docketDocumento : documentosParticipanteFiltro) {
					adicionaParagrafo(docTemplate, paragrafoDocumentoTemplate, bloco.getBlocosFilho().get(0), docketDocumento);
				}
			}
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
	
	private void populaParagrafoPessoaConsulta(WordprocessingMLPackage docTemplate, ContratoTipoTemplateBloco bloco) throws SiscoatException {

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
						achou  =true ; //return (P) p;
					}
					//fim do bloco
					if (content.getValue() != null && ((String) content.getValue()).contains("/"+tagBusca)) {
						achou  =false ; 
						retorno.add((P) p);
						return retorno;
					}
				}
				if ( achou ) {
					retorno.add((P) p);
				}
			}
		}

		return null;
	}

	private void adicionaParagrafo(WordprocessingMLPackage template, List<P> paragrafoTemplate,
			ContratoTipoTemplateBloco bloco, Object dataSource) throws SiscoatException {

		List<P> copy =  new ArrayList<>();
		// 3. copy the found paragraph to keep styling correct
		for (P p : paragrafoTemplate) {			
			if (!p.toString().contains(bloco.getTagIdentificacao()))
				copy.add((P) XmlUtils.deepCopy(p));
		}
//		List<P> copy =  XmlUtils.deepCopy(paragrafoTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {
			if ( CommonsUtil.semValor(campo))
				continue;

			Object valor = getValor(campo.getExpressao(), dataSource);
			
			if ( CommonsUtil.semValor((String)valor) && CommonsUtil.mesmoValorIgnoreCase("situacao", campo.getExpressao())  ) {
				valor = "VERIFICAR CERTIDÃO";
			}

			//if (valor != null) {
				for (P p : copy) {
					if (!CommonsUtil.semValor((String) valor))
						replacePlaceholder(p, campo.getTag(), (String) valor);
					else
						replacePlaceholder(p, campo.getTag(), "");
				}
			//}
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
					+ (CommonsUtil.semValor(imovel.getComplemento()) ? ""
							: " " + imovel.getComplemento().trim())
					+ (CommonsUtil.semValor(imovel.getBairro()) ? ""
							: " - " + imovel.getBairro().trim() + " -")
					+ " " + imovel.getCidade().trim() + " - " + imovel.getEstado().trim() + " - Cep: "
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
			if (textElement.getValue() != null && textElement.getValue().contains("#{"+placeholder+"}")) {
				String novoPlaceholder =  "#\\{" + placeholder + "\\}";
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

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;

		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		this.listaDocumentoAnalise = documentoAnaliseDao.listagemDocumentoAnalise(this.contrato);

	}

}
