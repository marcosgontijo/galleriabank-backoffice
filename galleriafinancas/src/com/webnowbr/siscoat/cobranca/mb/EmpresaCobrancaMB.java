package com.webnowbr.siscoat.cobranca.mb;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.cobranca.db.model.EmpresaCobranca;
import com.webnowbr.siscoat.cobranca.db.op.EmpresaCobrancaDao;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;

import org.primefaces.model.SortOrder;

import java.util.Map;

/** ManagedBean. */
@ManagedBean(name = "empresaCobrancaMB")
@SessionScoped
public class EmpresaCobrancaMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<EmpresaCobranca> lazyModel;
	/** Variavel. */
	private EmpresaCobranca objetoEmpresaCobranca;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	/**
	 * Construtor.
	 */
	public EmpresaCobrancaMB() {

		objetoEmpresaCobranca = new EmpresaCobranca();

		lazyModel = new LazyDataModel<EmpresaCobranca>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<EmpresaCobranca> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();

				setRowCount(empresaCobrancaDao.count(filters));
				return empresaCobrancaDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
	}

	public String clearFields() {
		objetoEmpresaCobranca = new EmpresaCobranca();
		this.tituloPainel = "Adicionar";

		return "EmpresaCobrancaInserir.xhtml";
	}
	
	public String clearFieldsView() {
		return "EmpresaCobrancaDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		return "EmpresaCobrancaInserir.xhtml";
	}	

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();
		String msgRetorno = null;
		try {			
			if (objetoEmpresaCobranca.getId() <= 0) {
				empresaCobrancaDao.create(objetoEmpresaCobranca);
				msgRetorno = "inserido";
			} else {
				empresaCobrancaDao.merge(objetoEmpresaCobranca);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "EmpresaCobranca: Registro "
							+ msgRetorno + " com sucesso! (EmpresaCobranca: "
							+ objetoEmpresaCobranca.getNome() + ")", ""));
			
			objetoEmpresaCobranca = new EmpresaCobranca();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "EmpresaCobranca: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "EmpresaCobranca: " + e, ""));

			return "";
		}

		return "EmpresaCobrancaConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();

		try {
			empresaCobrancaDao.delete(objetoEmpresaCobranca);

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"EmpresaCobranca: Registro excluído com sucesso! (EmpresaCobranca: "
							+ objetoEmpresaCobranca.getNome() + ")", ""));

		} catch (DAOException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "EmpresaCobranca: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "EmpresaCobranca: " + e, ""));

			return "";
		}

		return "EmpresaCobrancaConsultar.xhtml";
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<EmpresaCobranca> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel
	 *            the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<EmpresaCobranca> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoEmpresaCobranca
	 */
	public EmpresaCobranca getObjetoEmpresaCobranca() {
		return objetoEmpresaCobranca;
	}

	/**
	 * @param objetoEmpresaCobranca
	 *            the objetoEmpresaCobranca to set
	 */
	public void setObjetoEmpresaCobranca(EmpresaCobranca objetoEmpresaCobranca) {
		this.objetoEmpresaCobranca = objetoEmpresaCobranca;
	}

	/**
	 * @return the updateMode
	 */
	public boolean isUpdateMode() {
		return updateMode;
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
	
	public boolean validaCPF(FacesContext facesContext, 
            UIComponent uiComponent, Object object){
		return ValidaCPF.isCPF(object.toString());
	}
	
	public boolean validaCNPJ(FacesContext facesContext, 
            UIComponent uiComponent, Object object){
		return ValidaCNPJ.isCNPJ(object.toString());
	}
}
