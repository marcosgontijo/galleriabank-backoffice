package com.webnowbr.siscoat.infra.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.Parametros;
import com.webnowbr.siscoat.infra.db.model.User;

import org.primefaces.model.SortOrder;

import java.util.Map;

/** ManagedBean. */
@ManagedBean(name = "parametroMB")
@SessionScoped
public class ParametroMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<Parametros> lazyModel;
	/** Variavel. */
	private Parametros objetoParametro;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	
	/**
	 * Construtor.
	 */
	public ParametroMB() {

		objetoParametro = new Parametros();

		lazyModel = new LazyDataModel<Parametros>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Parametros> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				ParametrosDao parametrosDao = new ParametrosDao();

				setRowCount(parametrosDao.count(filters));
				return parametrosDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
	}


	public String clearFields() {
		objetoParametro = new Parametros();
		this.tituloPainel = "Adicionar";	

		return "ParametroInserir.xhtml";
	}
	
	public String clearFieldsUpdate() {
		this.tituloPainel = "Editar";

		return "ParametroInserir.xhtml";
	}

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ParametrosDao parametrosDao = new ParametrosDao();
		String msgRetorno = null;
		try {
			
			if (objetoParametro.getId() <= 0) {
				parametrosDao.create(objetoParametro);
				msgRetorno = "inserido";
			} else {
				parametrosDao.merge(objetoParametro);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Parâmetro: Registro "
							+ msgRetorno + " com sucesso! (Parâmetro: "
							+ objetoParametro.getNome() + ")", ""));
			
			objetoParametro = new Parametros();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Parâmetro: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Parâmetro: " + e, ""));

			return "";
		}

		return "ParametroConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ParametrosDao parametrosDao = new ParametrosDao();

		try {
			parametrosDao.delete(objetoParametro);

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Parâmetro: Registro excluído com sucesso! (Parâmetro: "
							+ objetoParametro.getNome() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Parâmetro: Exclusão não permitida!! Este registro está relacionado com algum outro registro.",
							""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Parâmetro: " + e, ""));

			return "";
		}

		return "ParametroConsultar.xhtml";
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<Parametros> getLazyModel() {
		return lazyModel;
	}


	/**
	 * @param lazyModel the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<Parametros> lazyModel) {
		this.lazyModel = lazyModel;
	}


	/**
	 * @return the objetoParametro
	 */
	public Parametros getObjetoParametro() {
		return objetoParametro;
	}


	/**
	 * @param objetoParametro the objetoParametro to set
	 */
	public void setObjetoParametro(Parametros objetoParametro) {
		this.objetoParametro = objetoParametro;
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
}
