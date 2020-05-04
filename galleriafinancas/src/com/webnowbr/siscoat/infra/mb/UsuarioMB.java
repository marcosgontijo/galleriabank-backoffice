package com.webnowbr.siscoat.infra.mb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;


import com.webnowbr.siscoat.infra.db.dao.GroupDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.GroupAdm;
import com.webnowbr.siscoat.infra.db.model.User;

import org.primefaces.model.SortOrder;

import java.util.Map;

/** ManagedBean. */
@ManagedBean(name = "usuarioMB")
@SessionScoped
public class UsuarioMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<User> lazyModel;
	/** Variavel. */
	private User objetoUsuario;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	
	private List<String> listPostos;
	
	private List<String> diasSemana;
	private String[] selectedDiasSemana;
	
	/**
	 * Construtor.
	 */
	public UsuarioMB() {

		objetoUsuario = new User();

		lazyModel = new LazyDataModel<User>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<User> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				UserDao postoDao = new UserDao();
				
				filters.put("userInvestidor", "false");

				setRowCount(postoDao.count(filters));
				return postoDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
	}

	public String clearFields() {
		objetoUsuario = new User();
		this.tituloPainel = "Adicionar";	
		
		this.diasSemana = new ArrayList<String>();
		this.diasSemana.add("Segunda-Feira");
		this.diasSemana.add("Terça-Feira");
		this.diasSemana.add("Quarta-Feira");
		this.diasSemana.add("Quinta-Feira");
		this.diasSemana.add("Sexta-Feira");
		this.diasSemana.add("Sábado");
		this.diasSemana.add("Domingo"); 
		
		this.selectedDiasSemana = new String[0];

		return "UsuarioInserir.xhtml";
	}
	
	public String clearFieldsUpdate() {
		this.tituloPainel = "Editar";
		
		this.diasSemana = new ArrayList<String>();
		this.diasSemana.add("Segunda-Feira");
		this.diasSemana.add("Terça-Feira");
		this.diasSemana.add("Quarta-Feira");
		this.diasSemana.add("Quinta-Feira");
		this.diasSemana.add("Sexta-Feira");
		this.diasSemana.add("Sábado");
		this.diasSemana.add("Domingo");
		
		this.selectedDiasSemana = new String[this.objetoUsuario.getDiasSemana().size()];
		
		if (this.objetoUsuario.getDiasSemana().size() > 0) {
			for (int i = 0; i < this.objetoUsuario.getDiasSemana().size(); i++) {		
				this.selectedDiasSemana[i] = this.objetoUsuario.getDiasSemana().get(i);
	        }
		}

		return "UsuarioInserir.xhtml";
	}
	
	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserDao postoDao = new UserDao();
		String msgRetorno = null;
		try {
			GroupDao gDao = new GroupDao();
			List<GroupAdm> gAdm = new ArrayList<GroupAdm>();
			List<GroupAdm> gAdmAux = new ArrayList<GroupAdm>();
			gAdm = gDao.findByFilter("acronym", "ROOT");
			if (objetoUsuario.isAdministrador()) {												
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "POSTO");
			if (objetoUsuario.isUserPosto()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "LOCACAO");
			if (objetoUsuario.isUserLocacao()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "COBRANCA");
			if (objetoUsuario.isUserCobranca()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "COBRANCA_EDITA");
			if (objetoUsuario.isUserCobrancaEdita()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "COBRANCA_BAIXA");
			if (objetoUsuario.isUserCobrancaBaixa()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "COBRANCA_IUGU");
			if (objetoUsuario.isUserCobrancaIugu()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "IUGU_POSTO");
			if (objetoUsuario.isUserIuguPosto()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PRECOBRANCA");
			if (objetoUsuario.isUserPreContrato()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PRECOBRANCAIUGU");
			if (objetoUsuario.isUserPreContratoIUGU()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "INVESTIDOR");
			if (objetoUsuario.isUserInvestidor()) {				
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}				
			}
			
			if (!objetoUsuario.isUserInvestidor() && !objetoUsuario.isUserPreContrato()) {
				objetoUsuario.setCodigoResponsavel(null);
			}
			
			objetoUsuario.setGroupList(gAdmAux);
			
			if (this.objetoUsuario.getHoraInicioPermissaoAcesso() == null || this.objetoUsuario.getHoraFimPermissaoAcesso() == null) {
				this.objetoUsuario.setHoraInicioPermissaoAcesso(null);
				this.objetoUsuario.setHoraFimPermissaoAcesso(null);
			}
			
			if (this.selectedDiasSemana.length > 0) {
				objetoUsuario.setDiasSemana(Arrays.asList(this.selectedDiasSemana));
			}
			
			if (objetoUsuario.getId() <= 0) {
				postoDao.create(objetoUsuario);
				msgRetorno = "inserido";
			} else {
				postoDao.merge(objetoUsuario);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Usuário: Registro "
							+ msgRetorno + " com sucesso! (Usuário: "
							+ objetoUsuario.getLogin() + ")", ""));
			
			objetoUsuario = new User();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Usuário: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Usuário: " + e, ""));

			return "";
		}

		return "UsuarioConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserDao postoDao = new UserDao();

		try {
			objetoUsuario.getGroupList().clear();
			postoDao.delete(objetoUsuario);
			

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Usuário: Registro excluído com sucesso! (Usuário: "
							+ objetoUsuario.getLogin() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Usuário: Exclusão não permitida!! Este registro está relacionado com algum outro registro.",
							""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Usuário: " + e, ""));

			return "";
		}

		return "UsuarioConsultar.xhtml";
	}
	
	public void geraLoginSenha() {
		
		if (!this.updateMode && !this.deleteMode) {
			String[] carctLogin ={"0","1","2","3","4","5","6","7","8","9"};
	
			String login = "";
			
			if (this.objetoUsuario.getName().length() >= 6) {
				login = this.objetoUsuario.getName().substring(0, 6);			
			} else {
				login = this.objetoUsuario.getName().substring(0, this.objetoUsuario.getName().length());	
				
				int count = (6 - this.objetoUsuario.getName().length()) + 1;
				
			    for (int x=0; x < count; x++){
			        int j = (int) (Math.random()*carctLogin.length);
			        login += carctLogin[j];
			    }
			}
			
			this.objetoUsuario.setLogin(login);		
			
			// gera senha
			String[] carctSenha ={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		    String senha="";
	
		    for (int x=0; x<10; x++){
		        int j = (int) (Math.random()*carctSenha.length);
		        senha += carctSenha[j];
		    }
			
		    this.objetoUsuario.setPassword(senha);
		}
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<User> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel
	 *            the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<User> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoUsuario
	 */
	public User getObjetoUsuario() {
		return objetoUsuario;
	}

	/**
	 * @param objetoUsuario
	 *            the objetoUsuario to set
	 */
	public void setObjetoUsuario(User objetoUsuario) {
		this.objetoUsuario = objetoUsuario;
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

	/**
	 * @return the listPostos
	 */
	public List<String> getListPostos() {
		return listPostos;
	}

	/**
	 * @param listPostos the listPostos to set
	 */
	public void setListPostos(List<String> listPostos) {
		this.listPostos = listPostos;
	}

	public List<String> getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(List<String> diasSemana) {
		this.diasSemana = diasSemana;
	}

	public String[] getSelectedDiasSemana() {
		return selectedDiasSemana;
	}

	public void setSelectedDiasSemana(String[] selectedDiasSemana) {
		this.selectedDiasSemana = selectedDiasSemana;
	}	
}
