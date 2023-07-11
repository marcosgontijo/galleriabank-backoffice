package com.webnowbr.siscoat.cobranca.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

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
import com.webnowbr.siscoat.cobranca.db.template.ContratoTipoTemplateDao;
import com.webnowbr.siscoat.common.WordUtil;
import com.webnowbr.siscoat.exception.SiscoatException;

import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;

public class PajuService {

	private static final String BLOCO_DOCUMENTO = "D";
	private static final String BLOCO_CABECALHO = "C";
	private static final String BLOCO_PESSOA_CONSULTA = "PC";
	private static final String BLOCO_PESSOA_FISICA_CONSULTA = "PFC";	
	private static final String BLOCO_DEBITOS_IPTU = "DB_IPTU";
	private static final String BLOCO_DEBITOS_CONDOMNIO = "DB_COND";

	private ExpressionParser parser;

	private StandardEvaluationContext context;

	private String arquivoWord;

	private ContratoCobranca contrato;
	
	private WordUtil wordUtil;

//	retorna  o doc em base64
	public byte[] generateModeloPaju(ContratoCobranca contrato, String arquivoWord) throws SiscoatException {

		this.contrato = contrato;
		this.arquivoWord = arquivoWord;
		WordprocessingMLPackage docTemplate;

		File arquivoTemplate = new File(arquivoWord);
		try {
			docTemplate = carregaTemplate(arquivoTemplate);
		} catch (Exception e) {
			throw new SiscoatException("Erro ao gerar documento", e);
		}

		parser = new SpelExpressionParser();
		context = new StandardEvaluationContext();
		wordUtil = new WordUtil();

		ContratoTipoTemplateDao contratoTipoTemplateDao = new ContratoTipoTemplateDao();
		ContratoTipoTemplate ContratoTipoTemplate = contratoTipoTemplateDao.getTemplate("PJ");
		List<ContratoTipoTemplateBloco> lstBlocos = ContratoTipoTemplate.getBlocos();

		for (ContratoTipoTemplateBloco bloco : lstBlocos) {

			if (bloco.getFlagInativo()) {
				continue;
			}

			String tipo = bloco.getCodigoTipoTemplateBloco();

			switch (tipo) {

			case BLOCO_DOCUMENTO:

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
				
			case BLOCO_PESSOA_CONSULTA:

				populaParagrafoPessoaConsulta(docTemplate, bloco);
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

	private void adicionaParagrafo(WordprocessingMLPackage template, P paragrafoTemplate,
			ContratoTipoTemplateBloco bloco, Object dataSource) throws SiscoatException {

		// 3. copy the found paragraph to keep styling correct
		P copy = (P) XmlUtils.deepCopy(paragrafoTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {

			Object valor = getValor(campo.getExpressao(), dataSource);

			if (valor != null) {
				replacePlaceholder(copy, campo.getTag(), (String) valor);
			}
		}

		// add the paragraph to the document
		((ContentAccessor) paragrafoTemplate.getParent()).getContent().add(copy);

		addParagrafoVazio((ContentAccessor) paragrafoTemplate.getParent());
	}

	private void addParagrafoVazio(ContentAccessor place2Add) {
		ObjectFactory factory = Context.getWmlObjectFactory();

		P spc = factory.createP();

		place2Add.getContent().add(spc);
	}
	
	// ************************************************************************
	// *** Rotinas para calculo de expressão

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
			if (textElement.getValue() != null) {
				String novoTexto = textElement.getValue().replaceAll("#\\{" + placeholder + "\\}",
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
	}

}
