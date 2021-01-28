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

import com.webnowbr.siscoat.cobranca.db.model.GruposFavorecidos;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.GruposFavorecidosDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;

import org.primefaces.model.SortOrder;

import java.util.Map;

/** ManagedBean. */
@ManagedBean(name = "gruposFavorecidosMB")
@SessionScoped
public class GruposFavorecidosMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<GruposFavorecidos> lazyModel;
	/** Variavel. */
	private GruposFavorecidos objetoGruposFavorecidos;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	
	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor2;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor3;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor4;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor5;
	
	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor6;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor7;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor8;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor9;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor10;
	
	/** Lista dos Recebedores utilizada pela LOV. */
	private List<PagadorRecebedor> listRecebedores;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor2;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor2;		

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor3;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor3;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor4;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor4;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor5;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor5;	
	
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor6;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor6;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor7;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor7;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor8;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor8;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor9;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor9;	

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor10;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor10;	
	
	/**
	 * Construtor.
	 */
	public GruposFavorecidosMB() {

		objetoGruposFavorecidos = new GruposFavorecidos();

		lazyModel = new LazyDataModel<GruposFavorecidos>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<GruposFavorecidos> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				GruposFavorecidosDao GruposFavorecidosDao = new GruposFavorecidosDao();

				setRowCount(GruposFavorecidosDao.count(filters));
				return GruposFavorecidosDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
	}
	
	public final void populateSelectedRecebedor() {
		this.idRecebedor = this.selectedRecebedor.getId();
		this.nomeRecebedor = this.selectedRecebedor.getNome();
	}

	public void clearRecebedor() {
		this.idRecebedor = 0;
		this.nomeRecebedor = null;
		this.selectedRecebedor = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor2() {
		this.idRecebedor2 = this.selectedRecebedor2.getId();
		this.nomeRecebedor2 = this.selectedRecebedor2.getNome();
	}

	public void clearRecebedor2() {
		this.idRecebedor2 = 0;
		this.nomeRecebedor2 = null;
		this.selectedRecebedor2 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor3() {
		this.idRecebedor3 = this.selectedRecebedor3.getId();
		this.nomeRecebedor3 = this.selectedRecebedor3.getNome();
	}

	public void clearRecebedor3() {
		this.idRecebedor3 = 0;
		this.nomeRecebedor3 = null;
		this.selectedRecebedor3 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor4() {
		this.idRecebedor4 = this.selectedRecebedor4.getId();
		this.nomeRecebedor4 = this.selectedRecebedor4.getNome();
	}

	public void clearRecebedor4() {
		this.idRecebedor4 = 0;
		this.nomeRecebedor4 = null;
		this.selectedRecebedor4 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor5() {
		this.idRecebedor5 = this.selectedRecebedor5.getId();
		this.nomeRecebedor5 = this.selectedRecebedor5.getNome();
	}

	public void clearRecebedor5() {
		this.idRecebedor5 = 0;
		this.nomeRecebedor5 = null;
		this.selectedRecebedor5 = new PagadorRecebedor();
	}
	
	public final void populateSelectedRecebedor6() {
		this.idRecebedor6 = this.selectedRecebedor6.getId();
		this.nomeRecebedor6 = this.selectedRecebedor6.getNome();
	}

	public void clearRecebedor6() {
		this.idRecebedor6 = 0;
		this.nomeRecebedor6 = null;
		this.selectedRecebedor6 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor7() {
		this.idRecebedor7 = this.selectedRecebedor7.getId();
		this.nomeRecebedor7 = this.selectedRecebedor7.getNome();
	}

	public void clearRecebedor7() {
		this.idRecebedor7 = 0;
		this.nomeRecebedor7 = null;
		this.selectedRecebedor7 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor8() {
		this.idRecebedor8 = this.selectedRecebedor8.getId();
		this.nomeRecebedor8 = this.selectedRecebedor8.getNome();
	}

	public void clearRecebedor8() {
		this.idRecebedor8 = 0;
		this.nomeRecebedor8 = null;
		this.selectedRecebedor8 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor9() {
		this.idRecebedor9 = this.selectedRecebedor9.getId();
		this.nomeRecebedor9 = this.selectedRecebedor9.getNome();
	}

	public void clearRecebedor9() {
		this.idRecebedor9 = 0;
		this.nomeRecebedor9 = null;
		this.selectedRecebedor9 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor10() {
		this.idRecebedor10 = this.selectedRecebedor10.getId();
		this.nomeRecebedor10 = this.selectedRecebedor10.getNome();
	}

	public void clearRecebedor10() {
		this.idRecebedor10 = 0;
		this.nomeRecebedor10 = null;
		this.selectedRecebedor10 = new PagadorRecebedor();
	}
	
	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();
	}
	
	public void clearSelectedLovs() {
		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor6();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();
	}

	public String clearFields() {
		objetoGruposFavorecidos = new GruposFavorecidos();
		this.tituloPainel = "Adicionar";
		loadLovs();
		clearSelectedLovs();

		return "GruposFavorecidosInserir.xhtml";
	}
	
	public String clearFieldsView() {
		return "GruposFavorecidosDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		clearSelectedLovs();
		loadLovs();
		
		if (this.objetoGruposFavorecidos.getRecebedor1() != null) {
			this.nomeRecebedor = this.objetoGruposFavorecidos.getRecebedor1().getNome();
			this.idRecebedor = this.objetoGruposFavorecidos.getRecebedor1().getId();
			this.selectedRecebedor = this.objetoGruposFavorecidos.getRecebedor1();
		}
		if (this.objetoGruposFavorecidos.getRecebedor2() != null) {
			this.nomeRecebedor2 = this.objetoGruposFavorecidos.getRecebedor2().getNome();
			this.idRecebedor2 = this.objetoGruposFavorecidos.getRecebedor2().getId();
			this.selectedRecebedor2 = this.objetoGruposFavorecidos.getRecebedor2();
		}
		if (this.objetoGruposFavorecidos.getRecebedor3() != null) {
			this.nomeRecebedor3 = this.objetoGruposFavorecidos.getRecebedor3().getNome();
			this.idRecebedor3 = this.objetoGruposFavorecidos.getRecebedor3().getId();
			this.selectedRecebedor3 = this.objetoGruposFavorecidos.getRecebedor3();
		}
		if (this.objetoGruposFavorecidos.getRecebedor4() != null) {
			this.nomeRecebedor4 = this.objetoGruposFavorecidos.getRecebedor4().getNome();
			this.idRecebedor4 = this.objetoGruposFavorecidos.getRecebedor4().getId();
			this.selectedRecebedor4 = this.objetoGruposFavorecidos.getRecebedor4();
		}
		if (this.objetoGruposFavorecidos.getRecebedor5() != null) {
			this.nomeRecebedor5 = this.objetoGruposFavorecidos.getRecebedor5().getNome();
			this.idRecebedor5 = this.objetoGruposFavorecidos.getRecebedor5().getId();
			this.selectedRecebedor5 = this.objetoGruposFavorecidos.getRecebedor5();
		}
		if (this.objetoGruposFavorecidos.getRecebedor6() != null) {
			this.nomeRecebedor6 = this.objetoGruposFavorecidos.getRecebedor6().getNome();
			this.idRecebedor6 = this.objetoGruposFavorecidos.getRecebedor6().getId();
			this.selectedRecebedor6 = this.objetoGruposFavorecidos.getRecebedor6();
		}
		
		if (this.objetoGruposFavorecidos.getRecebedor6() != null) {
			this.nomeRecebedor6 = this.objetoGruposFavorecidos.getRecebedor6().getNome();
			this.idRecebedor6 = this.objetoGruposFavorecidos.getRecebedor6().getId();
			this.selectedRecebedor6 = this.objetoGruposFavorecidos.getRecebedor6();
		}
		
		if (this.objetoGruposFavorecidos.getRecebedor8() != null) {
			this.nomeRecebedor8 = this.objetoGruposFavorecidos.getRecebedor8().getNome();
			this.idRecebedor8 = this.objetoGruposFavorecidos.getRecebedor8().getId();
			this.selectedRecebedor8 = this.objetoGruposFavorecidos.getRecebedor8();
		}

		if (this.objetoGruposFavorecidos.getRecebedor9() != null) {
			this.nomeRecebedor9 = this.objetoGruposFavorecidos.getRecebedor9().getNome();
			this.idRecebedor9 = this.objetoGruposFavorecidos.getRecebedor9().getId();
			this.selectedRecebedor9 = this.objetoGruposFavorecidos.getRecebedor9();
		}

		if (this.objetoGruposFavorecidos.getRecebedor10() != null) {
			this.nomeRecebedor10 = this.objetoGruposFavorecidos.getRecebedor10().getNome();
			this.idRecebedor10 = this.objetoGruposFavorecidos.getRecebedor10().getId();
			this.selectedRecebedor10 = this.objetoGruposFavorecidos.getRecebedor10();
		}		
		
		return "GruposFavorecidosInserir.xhtml";
	}	

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		GruposFavorecidosDao GruposFavorecidosDao = new GruposFavorecidosDao();
		String msgRetorno = null;
		try {		
			PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
			if (this.selectedRecebedor != null) {
				this.objetoGruposFavorecidos.setRecebedor1(pagadorRecebedorDao.findById(this.selectedRecebedor.getId()));
			}
			if (this.selectedRecebedor2 != null) {
				this.objetoGruposFavorecidos.setRecebedor2(pagadorRecebedorDao.findById(this.selectedRecebedor2.getId()));
			}
			if (this.selectedRecebedor3 != null) {
				this.objetoGruposFavorecidos.setRecebedor3(pagadorRecebedorDao.findById(this.selectedRecebedor3.getId()));
			}
			if (this.selectedRecebedor4 != null) {
				this.objetoGruposFavorecidos.setRecebedor4(pagadorRecebedorDao.findById(this.selectedRecebedor4.getId()));
			}
			if (this.selectedRecebedor5 != null) {
				this.objetoGruposFavorecidos.setRecebedor5(pagadorRecebedorDao.findById(this.selectedRecebedor5.getId()));
			}
			
			if (this.selectedRecebedor6 != null) {
				this.objetoGruposFavorecidos.setRecebedor6(pagadorRecebedorDao.findById(this.selectedRecebedor6.getId()));
			}
			
			if (this.selectedRecebedor7 != null) {
				this.objetoGruposFavorecidos.setRecebedor7(pagadorRecebedorDao.findById(this.selectedRecebedor7.getId()));
			}

			if (this.selectedRecebedor8 != null) {
				this.objetoGruposFavorecidos.setRecebedor8(pagadorRecebedorDao.findById(this.selectedRecebedor8.getId()));
			}

			if (this.selectedRecebedor9 != null) {
				this.objetoGruposFavorecidos.setRecebedor9(pagadorRecebedorDao.findById(this.selectedRecebedor9.getId()));
			}

			if (this.selectedRecebedor10 != null) {
				this.objetoGruposFavorecidos.setRecebedor10(pagadorRecebedorDao.findById(this.selectedRecebedor10.getId()));
			}			

			if (objetoGruposFavorecidos.getId() <= 0) {
				GruposFavorecidosDao.create(objetoGruposFavorecidos);
				msgRetorno = "inserido";
			} else {
				GruposFavorecidosDao.merge(objetoGruposFavorecidos);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "GruposFavorecidos: Registro "
							+ msgRetorno + " com sucesso! (GruposFavorecidos: "
							+ objetoGruposFavorecidos.getNomeGrupo() + ")", ""));
			
			objetoGruposFavorecidos = new GruposFavorecidos();
		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GruposFavorecidos: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GruposFavorecidos: " + e, ""));

			return "";
		}

		return "GruposFavorecidosConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		GruposFavorecidosDao GruposFavorecidosDao = new GruposFavorecidosDao();

		try {
			GruposFavorecidosDao.delete(objetoGruposFavorecidos);

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"GruposFavorecidos: Registro excluído com sucesso! (GruposFavorecidos: "
							+ objetoGruposFavorecidos.getNomeGrupo() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"GruposFavorecidos: Exclusão não permitida!!" + e,
							""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GruposFavorecidos: " + e, ""));

			return "";
		}

		return "GruposFavorecidosConsultar.xhtml";
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<GruposFavorecidos> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<GruposFavorecidos> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoGruposFavorecidos
	 */
	public GruposFavorecidos getObjetoGruposFavorecidos() {
		return objetoGruposFavorecidos;
	}

	/**
	 * @param objetoGruposFavorecidos the objetoGruposFavorecidos to set
	 */
	public void setObjetoGruposFavorecidos(GruposFavorecidos objetoGruposFavorecidos) {
		this.objetoGruposFavorecidos = objetoGruposFavorecidos;
	}

	/**
	 * @return the updateMode
	 */
	public boolean isUpdateMode() {
		return updateMode;
	}

	/**
	 * @param updateMode the updateMode to set
	 */
	public void setUpdateMode(boolean updateMode) {
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

	/**
	 * @return the selectedRecebedor
	 */
	public PagadorRecebedor getSelectedRecebedor() {
		return selectedRecebedor;
	}

	/**
	 * @param selectedRecebedor the selectedRecebedor to set
	 */
	public void setSelectedRecebedor(PagadorRecebedor selectedRecebedor) {
		this.selectedRecebedor = selectedRecebedor;
	}

	/**
	 * @return the selectedRecebedor2
	 */
	public PagadorRecebedor getSelectedRecebedor2() {
		return selectedRecebedor2;
	}

	/**
	 * @param selectedRecebedor2 the selectedRecebedor2 to set
	 */
	public void setSelectedRecebedor2(PagadorRecebedor selectedRecebedor2) {
		this.selectedRecebedor2 = selectedRecebedor2;
	}

	/**
	 * @return the selectedRecebedor3
	 */
	public PagadorRecebedor getSelectedRecebedor3() {
		return selectedRecebedor3;
	}

	/**
	 * @param selectedRecebedor3 the selectedRecebedor3 to set
	 */
	public void setSelectedRecebedor3(PagadorRecebedor selectedRecebedor3) {
		this.selectedRecebedor3 = selectedRecebedor3;
	}

	/**
	 * @return the selectedRecebedor4
	 */
	public PagadorRecebedor getSelectedRecebedor4() {
		return selectedRecebedor4;
	}

	/**
	 * @param selectedRecebedor4 the selectedRecebedor4 to set
	 */
	public void setSelectedRecebedor4(PagadorRecebedor selectedRecebedor4) {
		this.selectedRecebedor4 = selectedRecebedor4;
	}

	/**
	 * @return the selectedRecebedor5
	 */
	public PagadorRecebedor getSelectedRecebedor5() {
		return selectedRecebedor5;
	}

	/**
	 * @param selectedRecebedor5 the selectedRecebedor5 to set
	 */
	public void setSelectedRecebedor5(PagadorRecebedor selectedRecebedor5) {
		this.selectedRecebedor5 = selectedRecebedor5;
	}

	/**
	 * @return the listRecebedores
	 */
	public List<PagadorRecebedor> getListRecebedores() {
		return listRecebedores;
	}

	/**
	 * @param listRecebedores the listRecebedores to set
	 */
	public void setListRecebedores(List<PagadorRecebedor> listRecebedores) {
		this.listRecebedores = listRecebedores;
	}

	/**
	 * @return the nomeRecebedor
	 */
	public String getNomeRecebedor() {
		return nomeRecebedor;
	}

	/**
	 * @param nomeRecebedor the nomeRecebedor to set
	 */
	public void setNomeRecebedor(String nomeRecebedor) {
		this.nomeRecebedor = nomeRecebedor;
	}

	/**
	 * @return the idRecebedor
	 */
	public long getIdRecebedor() {
		return idRecebedor;
	}

	/**
	 * @param idRecebedor the idRecebedor to set
	 */
	public void setIdRecebedor(long idRecebedor) {
		this.idRecebedor = idRecebedor;
	}

	/**
	 * @return the nomeRecebedor2
	 */
	public String getNomeRecebedor2() {
		return nomeRecebedor2;
	}

	/**
	 * @param nomeRecebedor2 the nomeRecebedor2 to set
	 */
	public void setNomeRecebedor2(String nomeRecebedor2) {
		this.nomeRecebedor2 = nomeRecebedor2;
	}

	/**
	 * @return the idRecebedor2
	 */
	public long getIdRecebedor2() {
		return idRecebedor2;
	}

	/**
	 * @param idRecebedor2 the idRecebedor2 to set
	 */
	public void setIdRecebedor2(long idRecebedor2) {
		this.idRecebedor2 = idRecebedor2;
	}

	/**
	 * @return the nomeRecebedor3
	 */
	public String getNomeRecebedor3() {
		return nomeRecebedor3;
	}

	/**
	 * @param nomeRecebedor3 the nomeRecebedor3 to set
	 */
	public void setNomeRecebedor3(String nomeRecebedor3) {
		this.nomeRecebedor3 = nomeRecebedor3;
	}

	/**
	 * @return the idRecebedor3
	 */
	public long getIdRecebedor3() {
		return idRecebedor3;
	}

	/**
	 * @param idRecebedor3 the idRecebedor3 to set
	 */
	public void setIdRecebedor3(long idRecebedor3) {
		this.idRecebedor3 = idRecebedor3;
	}

	/**
	 * @return the nomeRecebedor4
	 */
	public String getNomeRecebedor4() {
		return nomeRecebedor4;
	}

	/**
	 * @param nomeRecebedor4 the nomeRecebedor4 to set
	 */
	public void setNomeRecebedor4(String nomeRecebedor4) {
		this.nomeRecebedor4 = nomeRecebedor4;
	}

	/**
	 * @return the idRecebedor4
	 */
	public long getIdRecebedor4() {
		return idRecebedor4;
	}

	/**
	 * @param idRecebedor4 the idRecebedor4 to set
	 */
	public void setIdRecebedor4(long idRecebedor4) {
		this.idRecebedor4 = idRecebedor4;
	}

	/**
	 * @return the nomeRecebedor5
	 */
	public String getNomeRecebedor5() {
		return nomeRecebedor5;
	}

	/**
	 * @param nomeRecebedor5 the nomeRecebedor5 to set
	 */
	public void setNomeRecebedor5(String nomeRecebedor5) {
		this.nomeRecebedor5 = nomeRecebedor5;
	}

	/**
	 * @return the idRecebedor5
	 */
	public long getIdRecebedor5() {
		return idRecebedor5;
	}

	/**
	 * @param idRecebedor5 the idRecebedor5 to set
	 */
	public void setIdRecebedor5(long idRecebedor5) {
		this.idRecebedor5 = idRecebedor5;
	}

	/**
	 * @return the selectedRecebedor6
	 */
	public PagadorRecebedor getSelectedRecebedor6() {
		return selectedRecebedor6;
	}

	/**
	 * @param selectedRecebedor6 the selectedRecebedor6 to set
	 */
	public void setSelectedRecebedor6(PagadorRecebedor selectedRecebedor6) {
		this.selectedRecebedor6 = selectedRecebedor6;
	}

	/**
	 * @return the selectedRecebedor7
	 */
	public PagadorRecebedor getSelectedRecebedor7() {
		return selectedRecebedor7;
	}

	/**
	 * @param selectedRecebedor7 the selectedRecebedor7 to set
	 */
	public void setSelectedRecebedor7(PagadorRecebedor selectedRecebedor7) {
		this.selectedRecebedor7 = selectedRecebedor7;
	}

	/**
	 * @return the selectedRecebedor8
	 */
	public PagadorRecebedor getSelectedRecebedor8() {
		return selectedRecebedor8;
	}

	/**
	 * @param selectedRecebedor8 the selectedRecebedor8 to set
	 */
	public void setSelectedRecebedor8(PagadorRecebedor selectedRecebedor8) {
		this.selectedRecebedor8 = selectedRecebedor8;
	}

	/**
	 * @return the selectedRecebedor9
	 */
	public PagadorRecebedor getSelectedRecebedor9() {
		return selectedRecebedor9;
	}

	/**
	 * @param selectedRecebedor9 the selectedRecebedor9 to set
	 */
	public void setSelectedRecebedor9(PagadorRecebedor selectedRecebedor9) {
		this.selectedRecebedor9 = selectedRecebedor9;
	}

	/**
	 * @return the selectedRecebedor10
	 */
	public PagadorRecebedor getSelectedRecebedor10() {
		return selectedRecebedor10;
	}

	/**
	 * @param selectedRecebedor10 the selectedRecebedor10 to set
	 */
	public void setSelectedRecebedor10(PagadorRecebedor selectedRecebedor10) {
		this.selectedRecebedor10 = selectedRecebedor10;
	}

	/**
	 * @return the nomeRecebedor6
	 */
	public String getNomeRecebedor6() {
		return nomeRecebedor6;
	}

	/**
	 * @param nomeRecebedor6 the nomeRecebedor6 to set
	 */
	public void setNomeRecebedor6(String nomeRecebedor6) {
		this.nomeRecebedor6 = nomeRecebedor6;
	}

	/**
	 * @return the idRecebedor6
	 */
	public long getIdRecebedor6() {
		return idRecebedor6;
	}

	/**
	 * @param idRecebedor6 the idRecebedor6 to set
	 */
	public void setIdRecebedor6(long idRecebedor6) {
		this.idRecebedor6 = idRecebedor6;
	}

	/**
	 * @return the nomeRecebedor7
	 */
	public String getNomeRecebedor7() {
		return nomeRecebedor7;
	}

	/**
	 * @param nomeRecebedor7 the nomeRecebedor7 to set
	 */
	public void setNomeRecebedor7(String nomeRecebedor7) {
		this.nomeRecebedor7 = nomeRecebedor7;
	}

	/**
	 * @return the idRecebedor7
	 */
	public long getIdRecebedor7() {
		return idRecebedor7;
	}

	/**
	 * @param idRecebedor7 the idRecebedor7 to set
	 */
	public void setIdRecebedor7(long idRecebedor7) {
		this.idRecebedor7 = idRecebedor7;
	}

	/**
	 * @return the nomeRecebedor8
	 */
	public String getNomeRecebedor8() {
		return nomeRecebedor8;
	}

	/**
	 * @param nomeRecebedor8 the nomeRecebedor8 to set
	 */
	public void setNomeRecebedor8(String nomeRecebedor8) {
		this.nomeRecebedor8 = nomeRecebedor8;
	}

	/**
	 * @return the idRecebedor8
	 */
	public long getIdRecebedor8() {
		return idRecebedor8;
	}

	/**
	 * @param idRecebedor8 the idRecebedor8 to set
	 */
	public void setIdRecebedor8(long idRecebedor8) {
		this.idRecebedor8 = idRecebedor8;
	}

	/**
	 * @return the nomeRecebedor9
	 */
	public String getNomeRecebedor9() {
		return nomeRecebedor9;
	}

	/**
	 * @param nomeRecebedor9 the nomeRecebedor9 to set
	 */
	public void setNomeRecebedor9(String nomeRecebedor9) {
		this.nomeRecebedor9 = nomeRecebedor9;
	}

	/**
	 * @return the idRecebedor9
	 */
	public long getIdRecebedor9() {
		return idRecebedor9;
	}

	/**
	 * @param idRecebedor9 the idRecebedor9 to set
	 */
	public void setIdRecebedor9(long idRecebedor9) {
		this.idRecebedor9 = idRecebedor9;
	}

	/**
	 * @return the nomeRecebedor10
	 */
	public String getNomeRecebedor10() {
		return nomeRecebedor10;
	}

	/**
	 * @param nomeRecebedor10 the nomeRecebedor10 to set
	 */
	public void setNomeRecebedor10(String nomeRecebedor10) {
		this.nomeRecebedor10 = nomeRecebedor10;
	}

	/**
	 * @return the idRecebedor10
	 */
	public long getIdRecebedor10() {
		return idRecebedor10;
	}

	/**
	 * @param idRecebedor10 the idRecebedor10 to set
	 */
	public void setIdRecebedor10(long idRecebedor10) {
		this.idRecebedor10 = idRecebedor10;
	}
}
