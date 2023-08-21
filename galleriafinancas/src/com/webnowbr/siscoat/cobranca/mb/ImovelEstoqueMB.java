package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelEstoqueDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

/** ManagedBean. */
@ManagedBean(name = "imovelEstoqueMB")
@SessionScoped
public class ImovelEstoqueMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<ImovelCobranca> lazyModel;
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
	private List<RelatorioEstoque> listRelatorioEstoque;
	
	
	
	/**
	 * Construtor.
	 */
	public ImovelEstoqueMB() {

		objetoImovelCobranca = new ImovelCobranca();
		objetoImovelEstoque = new ImovelEstoque();

		lazyModel = new LazyDataModel<ImovelCobranca>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<ImovelCobranca> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();

				setRowCount(imovelCobrancaDao.count(filters));
				return imovelCobrancaDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
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
		
		
		try {
			if(CommonsUtil.semValor(this.objetoImovelEstoque.getId())) {
				imovelEstoqueDao.create(objetoImovelEstoque);
				
			}
			else imovelEstoqueDao.merge(this.objetoImovelEstoque);
			
			
			if(CommonsUtil.semValor(this.objetoImovelCobranca.getImovelEstoque())) {
				this.objetoImovelCobranca.setImovelEstoque(this.objetoImovelEstoque);
			
			ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();				
			imovelCobrancaDao.merge(this.objetoImovelCobranca);
			}
			

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Estoque Inserido com sucesso!!", ""));
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

		return "/Atendimento/Cobranca/ImovelEstoqueEditar.xhtml";
	}
	
	
	public void consultaEstoque() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		listaConsultaEstoque = contratoCobrancaDao.consultaImovelEstoque();
	}
	
	public StreamedContent readXLSXFileRelatorioEstoque() throws IOException {

		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));

		XSSFSheet sheet = wb.getSheetAt(0);

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
		gravaCelula(13, "STATUS LEILÃO", linha);
		gravaCelula(14, "STATUS ATUAL", linha);
		gravaCelula(15, "VALOR 2º LEILÃO", linha);
		gravaCelula(16, "VALOR VENDA", linha);
		gravaCelula(17, "DATA VENDA", linha);
		gravaCelula(18, "TIPO VENDA", linha);


		iLinha++;

		for (int iRelatorio = 0; iRelatorio < this.listRelatorioEstoque.size(); iRelatorio++) {
			RelatorioEstoque relatorio = this.listRelatorioEstoque.get(iRelatorio);

			linha = sheet.getRow(iLinha);
			if (linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			gravaCelula(0, relatorio.getNumeroContratoRelatorio(), linha);
			gravaCelula(2, relatorio.getVariacaoCustoRelatorio(), linha);
			gravaCelula(3, relatorio.getLtvLeilaoRelatorio(), linha);
			gravaCelula(4, relatorio.getValorEmprestimoRelatorio(), linha);
			gravaCelula(5, relatorio.getVendaForcadaRelatorio(), linha);
			gravaCelula(6, relatorio.getValorMercadoRelatorio(), linha);
			gravaCelula(7, relatorio.getNomePagadorRelatorio(), linha);
			gravaCelula(8, relatorio.getNumeroMatriculaRelatorio(), linha);
			gravaCelula(9, relatorio.getEnderecoCompletoRelatorio(), linha);
			gravaCelula(10, relatorio.getDataConsolidadoRelatorio(), linha);
			gravaCelula(11, relatorio.getDataLeilao1Relatorio(), linha);
			gravaCelula(12, relatorio.getDataLeilao2Relatorio(), linha);
			gravaCelula(13, relatorio.getDataLeilao3Relatorio(), linha);
			gravaCelula(14, relatorio.getStatusLeilaoRelatorio(), linha);
			gravaCelula(15, relatorio.getStatusAtualRelatorio(), linha);
			gravaCelula(16, relatorio.getValorLeilao2Relatorio(), linha);
			gravaCelula(17, relatorio.getValorVendaRelatorio(), linha);
			gravaCelula(18, relatorio.getDataVendaRelatorio(), linha);
			gravaCelula(19, relatorio.getTipoVendaRelatorio(), linha);


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

		return null;

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
		if(!CommonsUtil.semValor(value)) {
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
	public LazyDataModel<ImovelCobranca> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel
	 *            the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<ImovelCobranca> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoImovelCobranca
	 */
	public ImovelCobranca getObjetoImovelCobranca() {
		return objetoImovelCobranca;
	}

	/**
	 * @param objetoImovelCobranca
	 *            the objetoImovelCobranca to set
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
	 * @param updateMode
	 *            the updateMode to set
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
	 * @param deleteMode
	 *            the deleteMode to set
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
	 * @param tituloPainel
	 *            the tituloPainel to set
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

	public List<RelatorioEstoque> getListRelatorioEstoque() {
		return listRelatorioEstoque;
	}

	public void setListRelatorioEstoque(List<RelatorioEstoque> listRelatorioEstoque) {
		this.listRelatorioEstoque = listRelatorioEstoque;
	}
	
}
