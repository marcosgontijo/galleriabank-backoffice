package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelEstoqueDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

/** ManagedBean. */
@ManagedBean(name = "imovelEstoqueMB")
@SessionScoped
public class ImovelEstoqueMB {

	/** Controle dos dados da Paginação. */
	private List<ContratoCobranca> listaImovelEstoque;
	/** Variavel. */
	private ImovelCobranca objetoImovelCobranca;
	private ImovelEstoque objetoImovelEstoque;
	private ContratoCobranca objetoContratoCobranca;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	private boolean editarEstoque;
	private List<ContratoCobranca> listaConsultaEstoque = new ArrayList<ContratoCobranca>();
	private List<ImovelEstoque> listImovelEstoque;
	private boolean relatorioGerado = false;

	/**
	 * Construtor.
	 */
	public ImovelEstoqueMB() {

		objetoImovelCobranca = new ImovelCobranca();
		objetoImovelEstoque = new ImovelEstoque();

//		consultaEstoque();

	}

	public String clearFieldsEstoqueImoveis() {
		objetoContratoCobranca = new ContratoCobranca();
		objetoImovelCobranca = new ImovelCobranca();
		this.consultaEstoque();

		return "/Atendimento/Cobranca/ImovelEstoqueConsulta.xhtml";
	}

	public String salvarEstoque() {
		FacesContext context = FacesContext.getCurrentInstance();
		ImovelEstoqueDao imovelEstoqueDao = new ImovelEstoqueDao();
		if(objetoImovelEstoque == null) {
			objetoImovelEstoque = new ImovelEstoque();
			objetoImovelEstoque.setObjetoContratoCobranca(objetoContratoCobranca);
			objetoImovelEstoque.setObjetoImovelCobranca(objetoImovelCobranca);
		}
		
		
		try {
			if(CommonsUtil.semValor(objetoImovelCobranca.getImovelEstoque())){
				objetoImovelCobranca.setImovelEstoque(objetoImovelEstoque);	

			}
	
			
			// Chama os métodos de cálculo e define os valores diretamente nos campos do objeto ImovelEstoque
			this.objetoImovelEstoque.setVariacaoCusto(
		            calcularVariacaoCustos(objetoImovelEstoque.getValorLeilao2(), objetoImovelEstoque.getValorEmprestimo())
		        );
		    this.objetoImovelEstoque.setLtvLeilao(
		            calcularLtvLeilao(objetoImovelEstoque.getValorLeilao2(), objetoImovelEstoque.getValorMercado())
		        );
		     if(objetoContratoCobranca != null) {
		    	 preencherCamposComDadosContrato();
		     }
		     ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();	
		     if(objetoImovelEstoque.getId() <= 0) {
		    	 objetoImovelEstoque.setQuitado(false);
		    	 objetoImovelEstoque.setStatusAtual("Estoque");
					imovelEstoqueDao.create(objetoImovelEstoque);

				}
				else 
				imovelEstoqueDao.merge(objetoImovelEstoque);
				imovelCobrancaDao.merge(objetoImovelCobranca); 


			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Estoque Inserido com sucesso!!", ""));
			clearFieldsEstoqueImoveis();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: " + e, ""));
		}
		return clearFieldsEstoqueImoveis();
	}

	public String editarEstoque() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();

		if (CommonsUtil.semValor(this.objetoImovelEstoque)) {
			objetoImovelEstoque = new ImovelEstoque();
		}

		if (objetoContratoCobranca != null) {
			preencherCamposComDadosContrato(); // Chama o método para preencher os campos com os dados do contrato
		}

		return "/Atendimento/Cobranca/ImovelEstoqueEditar.xhtml";
	}

	public void consultaEstoque() {
//		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		ImovelEstoqueDao dao = new ImovelEstoqueDao();
		listaConsultaEstoque = dao.consultaImovelEstoque();
//		listaConsultaEstoque = contratoCobrancaDao.consultaImovelEstoque();
	}

	public void preencherCamposComDadosContrato() {
		if (objetoContratoCobranca != null) {
			// Verifique se os campos do ContratoCobranca que deseja copiar não são nulos
			if (objetoContratoCobranca.getValorCCB() != null) {
				objetoImovelEstoque.setValorEmprestimo(objetoContratoCobranca.getValorCCB());
			}
			if (objetoContratoCobranca.getValorVendaForcadaImovel() != null) {
				objetoImovelEstoque.setVendaForcada(objetoContratoCobranca.getValorVendaForcadaImovel());
			}
			if (objetoContratoCobranca.getValorImovel() != null) {
				objetoImovelEstoque.setValorMercado(objetoContratoCobranca.getValorImovel());
			}
			// Continue preenchendo outros campos conforme necessário
		}
	}

	public BigDecimal calcularVariacaoCustos(BigDecimal valorLeilao2, BigDecimal valorEmprestimo) {
		if (valorLeilao2 != null && valorEmprestimo != null && BigDecimal.ZERO.compareTo(valorEmprestimo) != 0) {
			return (valorLeilao2.divide(valorEmprestimo, RoundingMode.HALF_UP).subtract(BigDecimal.ONE));
		} else {
			// Trata o caso em que um dos valores é nulo ou zero
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal calcularLtvLeilao(BigDecimal valorLeilao2, BigDecimal valorMercado) {
		if (valorLeilao2 != null && valorMercado != null && BigDecimal.ZERO.compareTo(valorMercado) != 0) {
			return (valorLeilao2.divide(valorMercado, RoundingMode.HALF_UP));
		} else {
			// Trata o caso em que um dos valores é nulo ou zero
			return BigDecimal.ZERO; // Ou outro valor padrão, dependendo do seu caso
		}
	}

	public void readXLSXFileRelatorioEstoque() throws IOException {

		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
		XSSFSheet sheet = wb.getSheetAt(0);

		ImovelEstoqueDao imovelEstoqueDao = new ImovelEstoqueDao();
		List<RelatorioEstoque> listRelatorioEstoque = imovelEstoqueDao.listRelatorioEstoque();

		// Cria uma instância de XSSFCellStyle para aplicar formatação ao cabeçalho
		XSSFCellStyle headerCellStyle = wb.createCellStyle();

		// Define a cor de fundo para o cabeçalho
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Cria uma fonte para o texto do cabeçalho
		XSSFFont font = wb.createFont();
		font.setBold(true); // Defina o texto em negrito
		headerCellStyle.setFont(font);

		// Alinha o texto ao centro horizontalmente e verticalmente
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// Crie o cabeçalho e aplique o estilo a cada célula do cabeçalho
		XSSFRow headerRow = sheet.createRow(0);
		String[] headerTexts = { "N° CONTRATO", "VARIAÇÃO CUSTOS ATÉ LEILÃO", "LTV DO LEILÃO", "VALOR DO EMPRÉSTIMO",
				"VENDA FORÇADA", "VALOR MERCADO", "CLIENTE", "MATRÍCULA", "IMÓVEL", "CONSOLIDADO EM", "1º LEILÃO",
				"2º LEILÃO", "LEILÃO ESTOQUE", "LEILOEIRO", "STATUS LEILÃO", "STATUS ATUAL", "VALOR 2º LEILÃO",
				"VALOR VENDA", "DATA VENDA", "TIPO VENDA" };

		for (int i = 0; i < headerTexts.length; i++) {
			XSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerTexts[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Ajusta automaticamente o tamanho das colunas com base no conteúdo
		for (int i = 0; i < headerTexts.length; i++) {
			sheet.setColumnWidth(i, 30*256);
		}

		// Cria uma instância de XSSFCellStyle para a formatação das linhas pares
		// XSSFCellStyle evenRowStyle = wb.createCellStyle();

		// Define a cor de fundo para as linhas pares
		// evenRowStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		// evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Cria uma instância de XSSFCellStyle para a formatação das linhas ímpares
		// XSSFCellStyle oddRowStyle = wb.createCellStyle();

		// Define a cor de fundo para as linhas ímpares
		// oddRowStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
		// oddRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Cria uma fonte para os estilos das linhas
		XSSFFont rowFont = wb.createFont();
		rowFont.setBold(false); // Desligue o negrito
		// evenRowStyle.setFont(rowFont);
		// oddRowStyle.setFont(rowFont);

		// formatação dados geral
		XSSFCellStyle normal_style = wb.createCellStyle();
		CellStyle normalStyle = wb.createCellStyle();
		normalStyle.setAlignment(HorizontalAlignment.LEFT);
		normalStyle.setVerticalAlignment(VerticalAlignment.TOP);

		// Cria um estilo personalizado para formato de moeda
		XSSFCellStyle currencyCellStyle = wb.createCellStyle();
		CreationHelper ch = wb.getCreationHelper();
		currencyCellStyle.setDataFormat(
				ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)")); // Define o
																												// formato
																												// da
																												// moeda

		// Cria um estilo personalizado para formato percentual
		XSSFCellStyle percentCellStyle = wb.createCellStyle();
		percentCellStyle.setDataFormat(ch.createDataFormat().getFormat("0.00%"));

		// Inicia um contador para rastrear linhas ímpares/pares
		int rowNum = 0;

		for (RelatorioEstoque relatorio : listRelatorioEstoque) {
			XSSFRow linha = sheet.createRow(rowNum + 1);// Comece a partir da segunda linha (0 é o cabeçalho)
			

			// Aplicar o estilo alternado com base no número da linha
			// XSSFCellStyle rowStyle = (rowNum % 2 == 0) ? evenRowStyle : oddRowStyle;
			for (int i = 0; i < headerTexts.length; i++) {
				XSSFCell cell = linha.createCell(i);
				// Defina o valor da célula com base nos dados do relatório
				// ...
				cell.setCellStyle(normalStyle);
			}

			rowNum++;
		}

		int iLinha = 0;

		XSSFRow linha = sheet.getRow(iLinha);
		if (linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
			
		}

		gravaCelula(0, "N° CONTRATO", linha);
		gravaCelula(1, "VARIAÇÃO CUSTOS ATÉ LEILÃO", linha);
		gravaCelula(2, "LTV DO LEILÃO", linha);
		gravaCelula(3, "VALOR DO EMPRESTIMO", linha);
		gravaCelula(4, "VENDA FORÇADA", linha);
		gravaCelula(5, "VALOR MERCADO", linha);
		gravaCelula(6, "CLIENTE", linha);
		gravaCelula(7, "MATRÍCULA", linha);
		gravaCelula(8, "IMÓVEL", linha);
		gravaCelula(9, "CONSOLIDADO EM", linha);
		gravaCelula(10, "1º LEILÃO", linha);
		gravaCelula(11, "2º LEILÃO", linha);
		gravaCelula(12, "LEILÃO ESTOQUE", linha);
		gravaCelula(13, "LEILOEIRO", linha);
		gravaCelula(14, "STATUS LEILÃO", linha);
		gravaCelula(15, "STATUS ATUAL", linha);
		gravaCelula(16, "VALOR 2º LEILÃO", linha);
		gravaCelula(17, "VALOR VENDA", linha);
		gravaCelula(18, "DATA VENDA", linha);
		gravaCelula(19, "TIPO VENDA", linha);

		iLinha++;

		for (int iRelatorio = 0; iRelatorio < listRelatorioEstoque.size(); iRelatorio++) {
			RelatorioEstoque relatorio = listRelatorioEstoque.get(iRelatorio);

			linha = sheet.getRow(iLinha);
			if (linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			gravaCelula(0, relatorio.getNumeroContratoRelatorio(), linha);
			gravaCelula(1, relatorio.getVariacaoCustoRelatorio(), linha);
			gravaCelula(2, relatorio.getLtvLeilaoRelatorio(), linha);
			gravaCelulaComEstiloMoeda(3, relatorio.getValorEmprestimoRelatorio(), linha, currencyCellStyle);
			gravaCelulaComEstiloMoeda(4, relatorio.getVendaForcadaRelatorio(), linha, currencyCellStyle);
			gravaCelulaComEstiloMoeda(5, relatorio.getValorMercadoRelatorio(), linha, currencyCellStyle);
			gravaCelula(6, relatorio.getNomePagadorRelatorio(), linha);
			gravaCelula(7, relatorio.getNumeroMatriculaRelatorio(), linha);
			gravaCelula(8, relatorio.getEnderecoCompletoRelatorio(), linha);
			gravaCelula(9, relatorio.getDataConsolidadoRelatorio(), linha);
			gravaCelula(10, relatorio.getDataLeilao1Relatorio(), linha);
			gravaCelula(11, relatorio.getDataLeilao2Relatorio(), linha);
			gravaCelula(12, relatorio.getDataLeilao3Relatorio(), linha);
			gravaCelula(13, relatorio.getLeiloeiroRelatorio(), linha);
			gravaCelula(14, relatorio.getStatusLeilaoRelatorio(), linha);
			gravaCelula(15, relatorio.getStatusAtualRelatorio(), linha);
			gravaCelulaComEstiloMoeda(16, relatorio.getValorLeilao2Relatorio(), linha, currencyCellStyle);
			gravaCelulaComEstiloMoeda(17, relatorio.getValorVendaRelatorio(), linha, currencyCellStyle);
			gravaCelula(18, relatorio.getDataVendaRelatorio(), linha);
			gravaCelula(19, relatorio.getTipoVendaRelatorio(), linha);

			// Aplica o estilo de formatação da linha
			// XSSFCellStyle rowStyle = (iLinha % 2 == 0) ? evenRowStyle : oddRowStyle;

			for (int i = 0; i < headerTexts.length; i++) {
				XSSFCell cell = linha.getCell(i);

				if (i == 1 || i == 2) { // Colunas 1 e 2 são formatadas como percentual
					cell.setCellStyle(percentCellStyle);
				}

				if (i != 1 && i != 2) { // Colunas 1 e 2 não são formatadas como moeda
					cell.setCellStyle(currencyCellStyle);
				}
			}

			rowNum++;

			iLinha++;
		}

		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
		// escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		// fecha a escrita de dados nessa planilha
		wb.close();

		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		gerador.open(String.format("Galleria Bank - Estoque %s.xlsx", ""));
		gerador.feed(new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();
	}

	private void gravaCelulaComEstiloMoeda(int columnIndex, BigDecimal valor, XSSFRow linha, XSSFCellStyle style) {
		XSSFCell cell = linha.createCell(columnIndex);

		if (valor != null) {
			cell.setCellValue(valor.doubleValue()); // Converte o BigDecimal para double antes de definir o valor
		} else {
			cell.setCellValue(0.0);
		}

		cell.setCellStyle(style);
	}

	// Função para obter o nome da coluna do Excel com base no índice (ex: 0 -> A, 1
	// -> B, ...)
	private String getColunaExcel(int coluna) {
		int div = coluna;
		String colunaExcel = "";
		int modulo;

		while (div > 0) {
			modulo = (div - 1) % 26;
			colunaExcel = (char) (65 + modulo) + colunaExcel;
			div = (int) ((div - modulo) / 26);
		}
		return colunaExcel;
	}

	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	private void gravaCelula(Integer celula, BigDecimal value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		if (value != null) {
			linha.getCell(celula).setCellValue(value.doubleValue());
		}
	}

	private void gravaCelula(Integer celula, Date value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		Locale locale = new Locale("pt", "BR");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
		String valueStr = "";
		if (!CommonsUtil.semValor(value)) {
			valueStr = sdf.format(value.getTime());
		}
		linha.getCell(celula).setCellValue(valueStr);
	}

	private void gravaCelula(Integer celula, int value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	/**
	 * @return the lazyModel
	 */


	/**
	 * @param lazyModel the lazyModel to set
	 */

	/**
	 * @return the objetoImovelCobranca
	 */
	public ImovelCobranca getObjetoImovelCobranca() {
		return objetoImovelCobranca;
	}

	/**
	 * @param objetoImovelCobranca the objetoImovelCobranca to set
	 */
	public void setObjetoImovelCobranca(ImovelCobranca objetoImovelCobranca) {
		this.objetoImovelCobranca = objetoImovelCobranca;
	}

	/**
	 * @return the updateMode
	 */

	public boolean isUpdateMode() {
		return updateMode;
	}

	public ImovelEstoque getObjetoImovelEstoque() {
		return objetoImovelEstoque;
	}

	public void setObjetoImovelEstoque(ImovelEstoque objetoImovelEstoque) {
		this.objetoImovelEstoque = objetoImovelEstoque;
	}

	/**
	 * @param updateMode the updateMode to set
	 */
	public void setUpdateMode(boolean updateMode) {
		if (updateMode) {
			this.tituloPainel = "Editar";
		} else {
			this.tituloPainel = "Visualizar";
		}
		this.updateMode = updateMode;
	}

	/**
	 * @return the deleteMode
	 */
	public boolean isDeleteMode() {
		return deleteMode;
	}

	/**
	 * @param deleteMode the deleteMode to set
	 */
	public void setDeleteMode(boolean deleteMode) {
		if (deleteMode) {
			this.tituloPainel = "Excluir";
		} else {
			if (this.updateMode) {
				this.tituloPainel = "Editar";
			} else {
				this.tituloPainel = "Visualizar";
			}
		}
		this.deleteMode = deleteMode;
	}

	/**
	 * @return the tituloPainel
	 */
	public String getTituloPainel() {
		return tituloPainel;
	}

	/**
	 * @param tituloPainel the tituloPainel to set
	 */
	public void setTituloPainel(String tituloPainel) {
		this.tituloPainel = tituloPainel;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public boolean isEditarEstoque() {
		return editarEstoque;
	}

	public void setEditarEstoque(boolean editarEstoque) {
		this.editarEstoque = editarEstoque;
	}

	public List<ContratoCobranca> getListaConsultaEstoque() {
		return listaConsultaEstoque;
	}

	public void setListaConsultaEstoque(List<ContratoCobranca> listaConsultaEstoque) {
		this.listaConsultaEstoque = listaConsultaEstoque;
	}

	public List<ImovelEstoque> getListImovelEstoque() {
		return listImovelEstoque;
	}

	public void setListImovelEstoque(List<ImovelEstoque> listImovelEstoque) {
		this.listImovelEstoque = listImovelEstoque;
	}

	public List<ContratoCobranca> getListaImovelEstoque() {
		return listaImovelEstoque;
	}

	public void setListaImovelEstoque(List<ContratoCobranca> listaImovelEstoque) {
		this.listaImovelEstoque = listaImovelEstoque;
	}

}
