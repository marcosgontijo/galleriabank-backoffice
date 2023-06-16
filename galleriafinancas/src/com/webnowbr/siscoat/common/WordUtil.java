package com.webnowbr.siscoat.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.TraversalUtil;
import org.docx4j.TraversalUtil.CallbackImpl;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Body;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplateBloco;
import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplateCampo;
import com.webnowbr.siscoat.exception.SiscoatException;

public class WordUtil {
	
	/** Logger instance. */
	private  final Log LOGGER = LogFactory.getLog(WordUtil.class);


	private ExpressionParser parser;

	private StandardEvaluationContext context;
	
	private WordprocessingMLPackage carregaTemplate(File arq) throws Docx4JException, FileNotFoundException {
		return WordprocessingMLPackage.load(new FileInputStream(arq));
	}

	public List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement)
			obj = ((JAXBElement<?>) obj).getValue();

		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}

		}
		return result;
	}

	private void replacePlaceholder(ContentAccessor place2Replace, String placeholder, String valor) {
		List<Object> texts = getAllElementFromObject(place2Replace, Text.class);

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

			List<Object> texts = getAllElementFromObject(rpHeaderPart.getJaxbElement(), Text.class);

			for (Object text : texts) {
				Text textElement = (Text) text;
				String novoTexto = textElement.getValue().replaceAll("#\\{" + placeholder + "\\}", valor);

				if (!textElement.getValue().equals(novoTexto)) {
					textElement.setValue(novoTexto);
				}
			}
		}

	}

	private P getParagrafoTemplate(WordprocessingMLPackage template, String tagBusca) {

		List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

		for (Object tbl : tables) {

			// 1. get the paragraph
			List<Object> paragraphs = getAllElementFromObject(tbl, P.class);

			for (Object p : paragraphs) {
				List<Object> texts = getAllElementFromObject(p, Text.class);
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

	// private void addQuebraPagina(ContentAccessor place2PageBreak) {
	// ObjectFactory factory = Context.getWmlObjectFactory();
	//
	// Br objBr = new Br();
	// objBr.setType(STBrType.PAGE);
	// R run = factory.createR();
	// run.getContent().add(objBr);
	// P para = factory.createP();
	// para.getContent().add(run);
	//
	// place2PageBreak.getContent().add(para);
	// }

	private void adicionaTabelaDocumento(WordprocessingMLPackage template, Tbl tabelaTemplate,
			ContratoTipoTemplateBloco bloco, Object dataSource) throws SiscoatException {

		// 3. copy the found paragraph to keep styling correct
		Tbl copy = (Tbl) XmlUtils.deepCopy(tabelaTemplate);

		for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {

			Object valor = getValor(campo.getExpressao(), dataSource);

			if (valor != null) {
				replacePlaceholder(copy, campo.getTag(), (String) valor);
			}
		}

		// add table to the document
		template.getMainDocumentPart().addObject(copy);

	}

	private void populaTabelas(WordprocessingMLPackage template, ContratoTipoTemplateBloco bloco, int maxColumns,
			List<Object> dataSource, boolean completaColunas) throws SiscoatException {

		ObjectFactory factory = Context.getWmlObjectFactory();

		// Busca a tabela baseada em uma tag dos dados a serem populados
		Tbl table = findTableByTag(template, bloco.getTagIdentificacao());

		if (table == null) {
			LOGGER.error("##ERRO## : tag '" + bloco.getTagIdentificacao() + "' não encontrato.");
			return;
		}

		int numColumns = dataSource.size() > maxColumns ? maxColumns : dataSource.size();
		
		
		// Configura as colunas novas
		TblGrid tblGrid = table.getTblGrid();
		if (tblGrid.getGridCol().size() < numColumns) {

			TblGridCol col = tblGrid.getGridCol().get(0);
			col.setW(col.getW().divide(BigInteger.valueOf(numColumns)));

			for (int i = 0; i < (numColumns - tblGrid.getGridCol().size()); i++) {
				TblGridCol copyCol = XmlUtils.deepCopy(col);
				tblGrid.getGridCol().add(copyCol);
			}

			table.setTblGrid(tblGrid);
		}

		if (table == null || CommonsUtil.semValor(table.getContent())) {
			return;
		}

		Tr row = (Tr) table.getContent().get(0);

		Tc cellaux = (Tc) ((JAXBElement<?>) row.getContent().get(0)).getValue();

		Tc templateCell = XmlUtils.deepCopy(cellaux);

		int iCol = 0;

		Iterator<Object> iterDataSource = dataSource.iterator();
		while (iterDataSource.hasNext()) {
			Object o = iterDataSource.next();

			for (ContratoTipoTemplateCampo campo : bloco.getCampos()) {

				String valor = (String) getValor(campo.getExpressao(), o);

				if (!CommonsUtil.semValor(valor)) {
					replacePlaceholder(table, campo.getTag(), valor);
				} else {
					// Se não houver valor para tag, remove parágrafo contendo a
					// tag
					// (necessário para quando não é PJ)
					List<Object> paragrafos = getAllElementFromObject(table, P.class);

					for (Object p : paragrafos) {
						List<Object> texts = getAllElementFromObject(p, Text.class);
						for (Object t : texts) {
							Text content = (Text) t;
							if (((String) content.getValue()).contains(campo.getTag())) {
								removeParagrafo(template, (P) p);
							}
						}
					}

				}
			}

			if (iterDataSource.hasNext()) {

				if (++iCol >= maxColumns) {
					iCol = 0;
					row = factory.createTr();
					table.getContent().add(row);
				}

				Tc copy = XmlUtils.deepCopy(templateCell);

				row.getContent().add(copy);

			}

		}

		// Completa as colunas com celulas vazias
		if (completaColunas && (++iCol < maxColumns)) {
			for (int i = iCol; i < maxColumns; i++) {
				P columnPara = factory.createP();
				Text tx = factory.createText();
				R run = factory.createR();
				run.getContent().add(tx);
				columnPara.getContent().add(run);
				Tc column = factory.createTc();
				column.getContent().add(columnPara);				
				row.getContent().add(column);
			}
		}
	}

	private Tbl findTableByTag(WordprocessingMLPackage template, String tagBusca) {
		List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

		Tbl tblResult = null;

		loop_externo: for (Object tbl : tables) {

			// 1. get the paragraph
			List<Object> paragraphs = getAllElementFromObject(tbl, P.class);

			for (Object p : paragraphs) {
				List<Object> texts = getAllElementFromObject(p, Text.class);
				for (Object t : texts) {
					Text content = (Text) t;
					if (content.getValue() != null && ((String) content.getValue()).contains(tagBusca)) {
						tblResult = (Tbl) tbl;
						break loop_externo;
					}
				}
			}
		}

		if (tblResult != null) {
			// Procura a tabela mais interna a tabela encontrada
			for (Object o : tblResult.getContent()) {
				List<Object> subTables = getAllElementFromObject(o, Tbl.class);
				// Retorna primeira sub-tabela retornada
				if (!CommonsUtil.semValor(subTables)) {
					tblResult = (Tbl) subTables.get(0);
					break;
				}
			}
		}

		return tblResult;
	}

	private void removeParagrafo(WordprocessingMLPackage template, P paragrafo) {
		((ContentAccessor) paragrafo.getParent()).getContent().remove(paragrafo);
	}

	// Remove tabela que contém parágrafos que não devem ser apresentados
	private void removeTodoContainer(WordprocessingMLPackage template, Tbl tabela) {

		org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) template.getMainDocumentPart()
				.getJaxbElement();
		Body body = wmlDocumentEl.getBody();

		TableFinder tf = new TableFinder();
		new TraversalUtil(body, tf);

		for (Child tableElement : tf.getTableElements()) {
			if (tableElement.equals(tabela)) {
				Object parent = tableElement.getParent();
				List<Object> theList = ((ContentAccessor) parent).getContent();
				remove(theList, tableElement);
				break;
			}
		}
	}

	private boolean remove(List<Object> theList, Object bm) {
		// Can't just remove the object from the parent,
		// since in the parent, it may be wrapped in a JAXBElement
		for (Object ox : theList) {
			if (XmlUtils.unwrap(ox).equals(bm)) {
				return theList.remove(ox);
			}
		}
		return false;
	}

	private void writeDocxToStream(WordprocessingMLPackage template, String diretorioDocumento,
			String nomeArquivoDestino) throws IOException, Docx4JException {

		// Cria diretório se não existe
		new File(diretorioDocumento).mkdirs();

		File f = new File(diretorioDocumento + "/" + nomeArquivoDestino);
		template.save(f);
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


	/**
	 * Sub-classe utilizada para encontrar os nós onde estão as tabelas do
	 * documento, permitindo assim que uma tabela seja apagada do documento
	 */
	static class TableFinder extends CallbackImpl {

		List<Child> tableElements = new ArrayList<Child>();

		@Override
		public List<Object> apply(Object o) {

			if (o instanceof javax.xml.bind.JAXBElement
					&& (((JAXBElement<?>) o).getName().getLocalPart().equals("tbl"))) {
				tableElements.add((Child) XmlUtils.unwrap(o));
			} else if (o instanceof Tbl) {
				tableElements.add((Child) o);
			}
			return null;
		}

		@Override
		// to setParent
		public void walkJAXBElements(Object parent) {

			List<?> children = getChildren(parent);
			if (children != null) {

				for (Object o : children) {

					if (o instanceof javax.xml.bind.JAXBElement
							&& (((JAXBElement<?>) o).getName().getLocalPart().equals("tbl"))) {

						((Child) ((JAXBElement<?>) o).getValue()).setParent(XmlUtils.unwrap(parent));
					} else {
						o = XmlUtils.unwrap(o);
						if (o instanceof Child) {
							((Child) o).setParent(XmlUtils.unwrap(parent));
						}
					}

					this.apply(o);

					if (this.shouldTraverse(o)) {
						walkJAXBElements(o);
					}

				}
			}
		}

		/** @return the tableElements */
		public List<Child> getTableElements() {
			return tableElements;
		}

	}
}
