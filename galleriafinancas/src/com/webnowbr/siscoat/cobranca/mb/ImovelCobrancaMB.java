package com.webnowbr.siscoat.cobranca.mb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.db.model.Cidade;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobrancaRestricao;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.db.op.CidadeDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaRestricaoDao;
import com.webnowbr.siscoat.cobranca.service.ImovelCobrancaRestricaoService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "imovelCobrancaMB")
@SessionScoped
public class ImovelCobrancaMB {

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	/** Controle dos dados da Paginação. */
	private LazyDataModel<ImovelCobranca> lazyModel;
	/** Variavel. */
	private ImovelCobranca objetoImovelCobranca;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;

	public UploadedFile uploadedFile;

	private ImovelEstoque objetoImovelEstoque;
	private ContratoCobranca objetoContratoCobranca;
	private boolean editarEstoque;
	private List<ContratoCobranca> listaConsultaEstoque = new ArrayList<ContratoCobranca>();
	private List<ImovelCobrancaRestricao> imovelRestricoes = new ArrayList<ImovelCobrancaRestricao>();

	/**
	 * Construtor.
	 */
	public ImovelCobrancaMB() {

		objetoImovelCobranca = new ImovelCobranca();
		objetoImovelEstoque = new ImovelEstoque();

		lazyModel = new LazyDataModel<ImovelCobranca>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<ImovelCobranca> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();

				setRowCount(imovelCobrancaDao.count(filters));
				return imovelCobrancaDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};
	}

	public String clearFields() {
		objetoImovelCobranca = new ImovelCobranca();
		this.tituloPainel = "Adicionar";

		return "/Atendimento/Cobranca/ImovelCobrancaInserir.xhtml";
	}

	public String clearFieldsEstoqueImoveis() {
		objetoContratoCobranca = new ContratoCobranca();
		objetoImovelCobranca = new ImovelCobranca();
		this.consultaEstoque();

		return "/Atendimento/Cobranca/ImovelEstoqueConsulta.xhtml";
	}

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
		String msgRetorno = null;
		try {
			if (objetoImovelCobranca.getId() <= 0) {
				imovelCobrancaDao.create(objetoImovelCobranca);
				msgRetorno = "inserido";
			} else {
				imovelCobrancaDao.merge(objetoImovelCobranca);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ImovelCobranca: Registro "
					+ msgRetorno + " com sucesso! (Imóvel: " + objetoImovelCobranca.getNome() + ")", ""));

			objetoImovelCobranca = new ImovelCobranca();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ImovelCobranca: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ImovelCobranca: " + e, ""));

			return "";
		}

		return "ImovelCobrancaConsultar.xhtml";
	}

	public void inserir(ImovelCobranca ic) {
		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();

		if (ic.getId() <= 0) {
			imovelCobrancaDao.create(ic);
		} else {
			imovelCobrancaDao.merge(ic);
		}
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();

		try {
			imovelCobrancaDao.delete(objetoImovelCobranca);

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"ImovelCobranca: Registro excluído com sucesso! (Registro: " + objetoImovelCobranca.getNome() + ")",
					""));

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"ImovelCobranca: Exclusão não permitida!! Este registro está relacionado com algum atendimento.",
					""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ImovelCobranca: " + e, ""));

			return "";
		}

		return "ImovelCobrancaConsultar.xhtml";
	}

	public void removerRestricao(ImovelCobrancaRestricao imovelCobrancaRestricao) {
		FacesContext context = FacesContext.getCurrentInstance();
		ImovelCobrancaRestricaoDao imovelCobrancaRestricaoDao = new ImovelCobrancaRestricaoDao();

		try {
			imovelCobrancaRestricao.setAtiva(false);
			imovelCobrancaRestricao.setDataInativa(new Date());
			imovelCobrancaRestricao.setUsuarioInativa(loginBean.getUsuarioLogado().getLogin());
			imovelCobrancaRestricaoDao.merge(imovelCobrancaRestricao);

			this.imovelRestricoes.remove(imovelCobrancaRestricao);
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"ImovelCobrancaRestricao: Registro excluído com sucesso!", ""));

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"ImovelCobrancaRestricao: Exclusão não permitida!!", ""));
		} catch (DBConnectionException e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ImovelCobrancaRestricao: " + e, ""));
		}

	}

	public void consultaEstoque() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		listaConsultaEstoque = contratoCobrancaDao.consultaImovelEstoque();
	}

	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	}

	public void popularCidades() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook((uploadedFile.getInputstream()));
		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);

		iLinha++;
		while (!CommonsUtil.semValor(linha)) {

			linha = sheet.getRow(iLinha);
			if (CommonsUtil.semValor(linha) || CommonsUtil.semValor(linha.getCell(0))
					|| CommonsUtil.semValor(linha.getCell(0).getNumericCellValue())
					|| CommonsUtil.semValor(linha.getCell(1))
					|| CommonsUtil.semValor(linha.getCell(1).getStringCellValue())
					|| CommonsUtil.semValor(linha.getCell(2))
					|| CommonsUtil.semValor(linha.getCell(2).getStringCellValue())
					|| CommonsUtil.semValor(linha.getCell(3)) || CommonsUtil.semValor(linha.getCell(4))
					|| CommonsUtil.semValor(linha.getCell(4).getStringCellValue())
					|| CommonsUtil.semValor(linha.getCell(5))
					|| CommonsUtil.semValor(linha.getCell(5).getNumericCellValue())
					|| CommonsUtil.semValor(linha.getCell(6))
					|| CommonsUtil.semValor(linha.getCell(6).getStringCellValue())) {
				break;
			}

			int rankingNacional = CommonsUtil.intValue(linha.getCell(0).getNumericCellValue());
			String nome = (linha.getCell(1).getStringCellValue());
			String estado = (linha.getCell(2).getStringCellValue());
			estado = estado.trim();
			int populacao = 0;
			try {
				if (!CommonsUtil.semValor(linha.getCell(3).getNumericCellValue())) {
					populacao = CommonsUtil.intValue(linha.getCell(3).getNumericCellValue());
				} else if (!CommonsUtil.semValor(linha.getCell(3).getStringCellValue())) {
					populacao = CommonsUtil.intValue(CommonsUtil.removeEspacos(linha.getCell(3).getStringCellValue()));
				}
			} catch (Exception e) {
				if (!CommonsUtil.semValor(linha.getCell(3).getStringCellValue())) {
					populacao = CommonsUtil.intValue(CommonsUtil.removeEspacos(linha.getCell(3).getStringCellValue()));
				}
			}

			String praiaStr = (linha.getCell(4).getStringCellValue());
			int rankingEstadual = CommonsUtil.intValue(linha.getCell(5).getNumericCellValue());
			String pintarStr = (linha.getCell(6).getStringCellValue());

			while (estado.startsWith(" ") || estado.startsWith(Character.toString((char) 160))) {
				estado = estado.substring(1);
			}

			while (estado.endsWith(" ") || estado.endsWith(Character.toString((char) 160))) {
				estado = estado.trim();
			}

			CidadeDao cDao = new CidadeDao();
			Cidade cidade = cDao.buscaCidade(nome, estado);
			if (CommonsUtil.semValor(cidade)) {
				cidade = new Cidade();
			}

			cidade.setNome(nome);
			cidade.setEstado(estado);
			cidade.setRankingNacional(rankingNacional);
			cidade.setPopulacao(populacao);
			if (CommonsUtil.mesmoValor(praiaStr.trim(), "não")) {
				cidade.setPraia(false);
			} else if (CommonsUtil.mesmoValor(praiaStr.trim(), "sim")) {
				cidade.setPraia(true);
			}
			cidade.setRankingEstadual(rankingEstadual);
			if (CommonsUtil.mesmoValor(pintarStr.trim(), "não")) {
				cidade.setPintarLinha(false);
			} else if (CommonsUtil.mesmoValor(pintarStr.trim(), "sim")) {
				cidade.setPintarLinha(true);
			}
			if (cidade.getId() > 0) {
				cDao.merge(cidade);
			} else {
				System.out.println("nova Cidade" + cidade.cidadeString());
				cDao.create(cidade);
			}

			iLinha++;
		}
	}

	public void clearDialog() {
		this.uploadedFile = null;
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<ImovelCobranca> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
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
	 * @param objetoImovelCobranca the objetoImovelCobranca to set
	 */
	public void setObjetoImovelCobranca(ImovelCobranca objetoImovelCobranca) {
		this.objetoImovelCobranca = objetoImovelCobranca;
		this.imovelRestricoes = new ArrayList<ImovelCobrancaRestricao>(0);
		if (!CommonsUtil.semValor(this.objetoImovelCobranca)) {
			ImovelCobrancaRestricaoService imovelCobrancaRestricaoService = new ImovelCobrancaRestricaoService();
			this.imovelRestricoes.addAll(imovelCobrancaRestricaoService.getImovelRestricoes(objetoImovelCobranca));
		}
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

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
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

	public List<ImovelCobrancaRestricao> getImovelRestricoes() {
		return imovelRestricoes;
	}

	public void setImovelRestricoes(List<ImovelCobrancaRestricao> imovelRestricoes) {
		this.imovelRestricoes = imovelRestricoes;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

}
