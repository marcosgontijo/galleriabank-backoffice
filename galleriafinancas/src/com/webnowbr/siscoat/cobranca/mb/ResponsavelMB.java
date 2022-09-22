package com.webnowbr.siscoat.cobranca.mb;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.mb.UsuarioMB;

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
	
	private long idResponsavel;
	private String nomeResponsavel = null;
	private Responsavel selectedResponsavel;
	private List<Responsavel> listResponsaveis;
	
	private long idResponsavelOriginal;
	private String nomeResponsavelOriginal = null;
	
	private boolean addUsuario = false;
	private boolean showUsuario = false;
	private String tipoPesquisa;
	
	private String login = "";
	private String senha = "";
	private Responsavel selectedResponsaveis[];
	
	
	/**
	 * Construtor
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
		objetoResponsavel.setDataCadastro(new Date());
		
		this.tituloPainel = "Adicionar";
		clearResponsavel();
		loadLovResponsavel();
		
		int maiorCodigo = 0;
		for(Responsavel responsavel : listResponsaveis ) {
			if(!CommonsUtil.semValor(responsavel.getCodigo()) && CommonsUtil.eSomenteNumero(responsavel.getCodigo()) ) {
				try {
					if(maiorCodigo < CommonsUtil.integerValue(responsavel.getCodigo())){
						maiorCodigo = CommonsUtil.integerValue(responsavel.getCodigo());
					}
				} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
		}
		maiorCodigo += 10;
		String codigoAutomatico = "";
		codigoAutomatico = CommonsUtil.stringValue(maiorCodigo);
		objetoResponsavel.setCodigo(codigoAutomatico);
		showUsuario = false;
		addUsuario = false;		
		login = "";
		senha = "";
		this.selectedResponsaveis = new Responsavel[0];
		
		ResponsavelDao rDao = new ResponsavelDao();
		this.listResponsaveis = rDao.findAll();
		
		this.tipoPessoaIsFisica = true;

		return "ResponsavelInserir.xhtml";
	}
		
	public String clearFieldsView() {
		if (this.objetoResponsavel.getCnpj() != null) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}	
		
		UserDao uDao = new UserDao();
		List<User> listaUser = uDao.findByFilter("codigoResponsavel", objetoResponsavel.getCodigo());
		if(!CommonsUtil.semValor(listaUser)) {
			User user = listaUser.get(0);
			addUsuario = false;
			if(!CommonsUtil.semValor(user)) {
				showUsuario = true;
				login = user.getLogin();
				senha = user.getPassword();
			} else {
				showUsuario = false;
				login = "";
				senha = "";
			}
		}
		
		this.selectedResponsaveis = new Responsavel[0];
		
		if (this.objetoResponsavel.getDonoResponsavel() != null) {
			this.selectedResponsavel = this.objetoResponsavel.getDonoResponsavel();
			populateSelectedResponsavel();
		}
	
		return "ResponsavelDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		if (this.objetoResponsavel.getCnpj() != null) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}	
		
		UserDao uDao = new UserDao();
		List<User> listaUser = uDao.findByFilter("codigoResponsavel", objetoResponsavel.getCodigo());
		if(!CommonsUtil.semValor(listaUser)) {
			User user = listaUser.get(0);
			addUsuario = false;
			if(!CommonsUtil.semValor(user)) {
				showUsuario = true;
				login = user.getLogin();
				senha = user.getPassword();
			} else {
				showUsuario = false;
				login = "";
				senha = "";
			}
		}
		
		this.selectedResponsaveis = new Responsavel[0];
		
		clearResponsavel();
		loadLovResponsavel();
		
		if (this.objetoResponsavel.getDonoResponsavel() != null) {
			this.selectedResponsavel = this.objetoResponsavel.getDonoResponsavel();
			populateSelectedResponsavel();
		}
		
		return "ResponsavelInserir.xhtml";
	}		

	public String inserir() {
		UserDao userDao = new UserDao();
		User user = new User();
		FacesContext context = FacesContext.getCurrentInstance();
		ResponsavelDao responsavelDao = new ResponsavelDao();
		String msgRetorno = null;
		showUsuario = false;
		try {

			if (objetoResponsavel.getWhatsAppNumero() == null || objetoResponsavel.getWhatsAppNumero().equals("")) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				objetoResponsavel.setWhatsAppNumero(takeBlipMB.getWhatsAppURLNovoResponsavel(objetoResponsavel));
			}
			
			if (this.selectedResponsavel != null) {
				if (this.selectedResponsavel.getId() > 0) {
					objetoResponsavel.setDonoResponsavel(this.selectedResponsavel);
				}
			}

			if (objetoResponsavel.getId() <= 0) {
				if (responsavelDao.findByFilter("codigo", this.objetoResponsavel.getCodigo()).size() <= 0) {
					responsavelDao.create(objetoResponsavel);
					if(this.addUsuario) {
						UsuarioMB userMb = new UsuarioMB();
						userMb.clearFields();
						userMb.getObjetoUsuario().setPassword(this.getSenha());
						userMb.getObjetoUsuario().setLogin(this.getLogin());
						userMb.getObjetoUsuario().setCodigoResponsavel(this.objetoResponsavel.getCodigo());
						userMb.getObjetoUsuario().setName(this.objetoResponsavel.getNome());
						userMb.getObjetoUsuario().setUserPreContrato(true);
						userMb.inserir();
						for (Responsavel responsavel : this.selectedResponsaveis) {
							user = userDao.findByFilter("codigoResponsavel", responsavel.getCodigo()).get(0);
							user.getListResponsavel().add(this.objetoResponsavel);
							userDao.merge(user);
						}	
					}
					msgRetorno = "inserido";
				} else {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Codigo já Resgistrado", ""));
					return "";
				}
			} else {
				responsavelDao.merge(objetoResponsavel);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Responsavel: Registro " + msgRetorno
					+ " com sucesso! (Responsavel: " + objetoResponsavel.getNome() + ")", ""));

			objetoResponsavel = new Responsavel();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Responsavel: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Responsavel: " + e, ""));

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
	
	public void pesquisaResponsavel() {
		//ResponsavelDao rDao = new ResponsavelDao();
		//this.listResponsaveis = rDao.findAll();		
		this.tipoPesquisa = "Responsavel";
	
		//this.nomeResponsavel = "";
		//this.idResponsavel = 0;
		//selectedResponsavel = new Responsavel();
	}
	
	public void pesquisaResponsavelOriginal() {
		//ResponsavelDao rDao = new ResponsavelDao();
		//this.listResponsaveis = rDao.findAll();		
		this.tipoPesquisa = "Original";
	
		//this.nomeResponsavelOriginal = "";
		//this.idResponsavelOriginal = 0;
		//selectedResponsavel = new Responsavel();
	}
	
	
	public final void populateSelectedResponsavel2() {
		this.idResponsavel = this.selectedResponsavel.getId();
		this.nomeResponsavel = this.selectedResponsavel.getNome();
	}
	
	public final void populateSelectedResponsavel() {
		if(CommonsUtil.mesmoValor(tipoPesquisa, "Responsavel")) {
			this.idResponsavel = this.selectedResponsavel.getId();
			this.nomeResponsavel = this.selectedResponsavel.getNome();
		} else if(CommonsUtil.mesmoValor(tipoPesquisa, "Original")) {
			this.idResponsavelOriginal = this.selectedResponsavel.getId();
			this.nomeResponsavelOriginal = this.selectedResponsavel.getNome();
		}
	}
	
	public void clearResponsavel() {
		this.idResponsavel = 0;
		this.nomeResponsavel = null;
		this.nomeResponsavelOriginal = "";
		this.idResponsavelOriginal = 0;
		this.selectedResponsavel = new Responsavel();
	}
	
	public void loadLovResponsavel() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		this.listResponsaveis = responsavelDao.findAll();
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

	public long getIdResponsavel() {
		return idResponsavel;
	}

	public void setIdResponsavel(long idResponsavel) {
		this.idResponsavel = idResponsavel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public Responsavel getSelectedResponsavel() {
		return selectedResponsavel;
	}

	public void setSelectedResponsavel(Responsavel selectedResponsavel) {
		this.selectedResponsavel = selectedResponsavel;
	}

	public List<Responsavel> getListResponsaveis() {
		return listResponsaveis;
	}

	public void setListResponsaveis(List<Responsavel> listResponsaveis) {
		this.listResponsaveis = listResponsaveis;
	}

	public boolean isAddUsuario() {
		return addUsuario;
	}

	public void setAddUsuario(boolean addUsuario) {
		this.addUsuario = addUsuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Responsavel[] getSelectedResponsaveis() {
		return selectedResponsaveis;
	}

	public void setSelectedResponsaveis(Responsavel[] selectedResponsaveis) {
		this.selectedResponsaveis = selectedResponsaveis;
	}

	public boolean isShowUsuario() {
		return showUsuario;
	}

	public void setShowUsuario(boolean showUsuario) {
		this.showUsuario = showUsuario;
	}

	public String getNomeResponsavelOriginal() {
		return nomeResponsavelOriginal;
	}

	public void setNomeResponsavelOriginal(String nomeResponsavelOriginal) {
		this.nomeResponsavelOriginal = nomeResponsavelOriginal;
	}

	public long getIdResponsavelOriginal() {
		return idResponsavelOriginal;
	}

	public void setIdResponsavelOriginal(long idResponsavelOriginal) {
		this.idResponsavelOriginal = idResponsavelOriginal;
	}	
	
	
	
}
