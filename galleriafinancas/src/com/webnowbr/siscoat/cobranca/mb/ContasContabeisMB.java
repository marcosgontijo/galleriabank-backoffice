package com.webnowbr.siscoat.cobranca.mb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.cobranca.db.op.ContaContabilDao;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;

/** ManagedBean. */
@ManagedBean(name = "contasContabeisMB")
@SessionScoped
public class ContasContabeisMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<ContaContabil> lazyModel;
	private boolean updateMode = false;
	private boolean deleteMode = false;

	/** Variavel. */
	private ContaContabil contaContabil;
	private String tituloPainel = null;

	private List<ContaContabil> lstContaContabilPai;

	/**
	 * Construtor.
	 */
	public ContasContabeisMB() {

		contaContabil = new ContaContabil();
		contaContabil.setContaContabilPai(new ContaContabil());
		final ContaContabilDao contaContabilDao = new ContaContabilDao();

		lazyModel = new LazyDataModel<ContaContabil>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<ContaContabil> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				setRowCount(contaContabilDao.count(filters));
				return contaContabilDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};

		lstContaContabilPai = contaContabilDao.ContasContabilRaiz();
	}

	public String clearFields() {
		contaContabil = new ContaContabil();
		contaContabil.setContaContabilPai(new ContaContabil());
		this.tituloPainel = "Adicionar";

		return "ContasContabeisInserir.xhtml";
	}

	public String clearFieldsView() {
		return "ContasContabeisDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		this.tituloPainel = "Alterar";
		if (contaContabil.getContaContabilPai() == null) {
			contaContabil.setContaContabilPai(new ContaContabil());
		}
		return "ContasContabeisInserir.xhtml";
	}

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContaContabilDao contaContabilDao = new ContaContabilDao();
		String msgRetorno = null;
		try {

			if (contaContabil.getContaContabilPai().getId() > 0) {
				contaContabil
						.setContaContabilPai(contaContabilDao.findById(contaContabil.getContaContabilPai().getId()));
			} else {
				contaContabil.setContaContabilPai(null);
			}

			if (contaContabil.getId() <= 0) {
				contaContabilDao.create(contaContabil);
				msgRetorno = "inserido";
			} else {
				contaContabilDao.merge(contaContabil);
				msgRetorno = "atualizado";
			}
			if (contaContabil.getContaContabilPai() == null)
				lstContaContabilPai = contaContabilDao.ContasContabilRaiz();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ContaContabil: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ContaContabil: " + e, ""));

			return "";
		}

		return "ContasContabeisConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContaContabilDao contaContabilDao = new ContaContabilDao();

		try {

			contaContabilDao.delete(contaContabil);

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"ContaContabil: Registro excluído com sucesso! (ContaContabil: " + contaContabil.getNome() + ")",
					""));
		} catch (DAOException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"ContaContabil: Exclusão não permitida!! Este registro está relacionado com algum atendimento.",
					""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ContaContabil: " + e, ""));

			return "";
		}

		return "ContaContabilConsultar.xhtml";
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<ContaContabil> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<ContaContabil> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isDeleteMode() {
		return deleteMode;
	}

	public void setDeleteMode(boolean deleteMode) {
		this.deleteMode = deleteMode;
	}

	public ContaContabil getContaContabil() {
		return contaContabil;
	}

	public void setContaContabil(ContaContabil contaContabil) {
		this.contaContabil = contaContabil;
	}

	public String getTituloPainel() {
		return tituloPainel;
	}

	public void setTituloPainel(String tituloPainel) {
		this.tituloPainel = tituloPainel;
	}

	public List<ContaContabil> getLstContaContabilPai() {

		if (contaContabil.getId() > 0) {
			List<ContaContabil> result = new ArrayList<ContaContabil>(0);
			for (ContaContabil contaContabil : lstContaContabilPai) {
				if (contaContabil.getId() != contaContabil.getId()) {
					result.add(contaContabil);
				}
			}
			return result;
		} else {
			return lstContaContabilPai;
		}
	}

	public void setLstContaContabilPai(List<ContaContabil> lstContaContabilPai) {
		this.lstContaContabilPai = lstContaContabilPai;
	}

}
