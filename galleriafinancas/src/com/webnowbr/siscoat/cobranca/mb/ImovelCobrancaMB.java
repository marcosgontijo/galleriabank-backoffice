package com.webnowbr.siscoat.cobranca.mb;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;

/** ManagedBean. */
@ManagedBean(name = "imovelCobrancaMB")
@SessionScoped
public class ImovelCobrancaMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<ImovelCobranca> lazyModel;
	/** Variavel. */
	private ImovelCobranca objetoImovelCobranca;
	private ImovelEstoque objetoImovelEstoque;
	private ContratoCobranca objetoContratoCobranca;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
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

	public String clearFields() {
		objetoImovelCobranca = new ImovelCobranca();
		this.tituloPainel = "Adicionar";

		return "/Atendimento/Cobranca/ImovelCobrancaInserir.xhtml";
	}
	
	public String clearFieldsEstoqueImoveis() {
		objetoContratoCobranca = new ContratoCobranca();
		objetoImovelCobranca = new ImovelCobranca();

		
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

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "ImovelCobranca: Registro "
							+ msgRetorno + " com sucesso! (Imóvel: "
							+ objetoImovelCobranca.getNome() + ")", ""));
			
			objetoImovelCobranca = new ImovelCobranca();
			

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "ImovelCobranca: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "ImovelCobranca: " + e, ""));

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

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"ImovelCobranca: Registro excluído com sucesso! (Registro: "
							+ objetoImovelCobranca.getNome() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"ImovelCobranca: Exclusão não permitida!! Este registro está relacionado com algum atendimento.",
							""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "ImovelCobranca: " + e, ""));

			return "";
		}

		return "ImovelCobrancaConsultar.xhtml";
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
}
