package com.webnowbr.siscoat.cobranca.mb;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;

import org.primefaces.model.SortOrder;

import java.util.Map;

/** ManagedBean. */
@ManagedBean(name = "responsavelMB")
@SessionScoped
public class ResponsavelMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<Responsavel> lazyModel;
	/** Variavel. */
	private Responsavel objetoResponsavel;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	private boolean tipoPessoaIsFisica = false;
	/**
	 * Construtor.
	 */
	public ResponsavelMB() {

		objetoResponsavel = new Responsavel();

		lazyModel = new LazyDataModel<Responsavel>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Responsavel> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				ResponsavelDao responsavelDao = new ResponsavelDao();

				setRowCount(responsavelDao.count(filters));
				return responsavelDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
	}

	public String clearFields() {
		objetoResponsavel = new Responsavel();
		this.tituloPainel = "Adicionar";
		
		this.tipoPessoaIsFisica = true;

		return "ResponsavelInserir.xhtml";
	}
	
	public String clearFieldsView() {
		if (this.objetoResponsavel.getCnpj() != null) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}		
	
		return "ResponsavelDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		if (this.objetoResponsavel.getCnpj() != null) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}	
	
		return "ResponsavelInserir.xhtml";
	}		

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResponsavelDao responsavelDao = new ResponsavelDao();
		String msgRetorno = null;
		try {
			if (objetoResponsavel.getId() <= 0) {
				responsavelDao.create(objetoResponsavel);
				msgRetorno = "inserido";
			} else {
				responsavelDao.merge(objetoResponsavel);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Responsavel: Registro "
							+ msgRetorno + " com sucesso! (Responsavel: "
							+ objetoResponsavel.getNome() + ")", ""));
			
			objetoResponsavel = new Responsavel();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Responsavel: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Responsavel: " + e, ""));

			return "";
		}

		return "ResponsavelConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResponsavelDao responsavelDao = new ResponsavelDao();

		try {
			responsavelDao.delete(objetoResponsavel);

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Responsavel: Registro excluído com sucesso! (Responsavel: "
							+ objetoResponsavel.getNome() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Responsavel: Exclusão não permitida!! Este registro está relacionado com algum atendimento.",
							""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Responsavel: " + e, ""));

			return "";
		}

		return "ResponsavelConsultar.xhtml";
	}
		
	public void selectedTipoPessoa() {
		if (this.tipoPessoaIsFisica) {
			this.objetoResponsavel.setCnpj(null);
			this.objetoResponsavel.setNome(null);
		} else {
			this.objetoResponsavel.setCpf(null);
			this.objetoResponsavel.setRg(null);
			this.objetoResponsavel.setNome(null);
		}
	}
		
	public boolean validaCNPJ(FacesContext facesContext, 
            UIComponent uiComponent, Object object){
		return ValidaCNPJ.isCNPJ(object.toString());
	}	

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<Responsavel> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel
	 *            the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<Responsavel> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoResponsavel
	 */
	public Responsavel getObjetoResponsavel() {
		return objetoResponsavel;
	}

	/**
	 * @param objetoResponsavel
	 *            the objetoResponsavel to set
	 */
	public void setObjetoResponsavel(Responsavel objetoResponsavel) {
		this.objetoResponsavel = objetoResponsavel;
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

	/**
	 * @return the tipoPessoaIsFisica
	 */
	public boolean isTipoPessoaIsFisica() {
		return tipoPessoaIsFisica;
	}

	/**
	 * @param tipoPessoaIsFisica the tipoPessoaIsFisica to set
	 */
	public void setTipoPessoaIsFisica(boolean tipoPessoaIsFisica) {
		this.tipoPessoaIsFisica = tipoPessoaIsFisica;
	}	
}
