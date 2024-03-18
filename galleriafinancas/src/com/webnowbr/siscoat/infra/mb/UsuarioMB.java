package com.webnowbr.siscoat.infra.mb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.GroupDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.dao.UserPerfilDao;
import com.webnowbr.siscoat.infra.db.model.GroupAdm;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.db.model.UserPerfil;
import com.webnowbr.siscoat.security.TwoFactorAuth;

import org.primefaces.model.SortOrder;

import java.util.Map;
import java.util.Optional;

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

	private Responsavel selectedResponsaveis[];
	private List<Responsavel> responsaveis;
	private List<UserPerfil> perfil;
	Optional<UserPerfil> userSemPerfil;

	/**
	 * Construtor.
	 */
	public UsuarioMB() {

		objetoUsuario = new User();
		
		
		lazyModel = new LazyDataModel<User>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<User> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				UserDao postoDao = new UserDao();

				filters.put("userInvestidor", "false");

				setRowCount(postoDao.count(filters));
				return postoDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};
	}

	public void loadResponsavel() {
		this.responsaveis = new ArrayList<Responsavel>();
		ResponsavelDao rDao = new ResponsavelDao();
		this.responsaveis = rDao.findAll();
	}
	
	private void carregaListaPerfil() {
		if (perfil == null) {
			UserPerfilDao userPerfilDao = new UserPerfilDao();
			perfil = userPerfilDao.findAll().stream().sorted(Comparator.comparing(UserPerfil::getId))
					.collect(Collectors.toList());
			userSemPerfil = perfil.stream().filter(p -> p.getId() == -1000).findFirst();
		}
	}

	public String clearFields() {
		objetoUsuario = new User();
		
		if (userSemPerfil == null)
			carregaListaPerfil();
		objetoUsuario.setUserPerfil(userSemPerfil.get());
		
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
		this.selectedResponsaveis = new Responsavel[0];
		carregaListaPerfil();
		loadResponsavel();

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

		if (this.objetoUsuario.getDiasSemana() != null) {
			this.selectedDiasSemana = new String[this.objetoUsuario.getDiasSemana().size()];

			if (this.objetoUsuario.getDiasSemana().size() > 0) {
				for (int i = 0; i < this.objetoUsuario.getDiasSemana().size(); i++) {
					this.selectedDiasSemana[i] = this.objetoUsuario.getDiasSemana().get(i);
				}
			}
		}
		carregaListaPerfil();
		loadResponsavel();
		this.selectedResponsaveis = new Responsavel[this.objetoUsuario.getListResponsavel().size()];

		if (this.objetoUsuario.getListResponsavel().size() > 0) {
			for (int i = 0; i < this.objetoUsuario.getListResponsavel().size(); i++) {
				this.selectedResponsaveis[i] = this.objetoUsuario.getListResponsavel().get(i);
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

			gAdm = gDao.findByFilter("acronym", "COBRANCA_LEAD");
			if (objetoUsuario.isUserCobrancaLead()) {
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

			gAdm = gDao.findByFilter("acronym", "COBRANCA_FINANCEIRO");
			if (objetoUsuario.isUserCobrancaFinanceiro()) {
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

			gAdm = gDao.findByFilter("acronym", "AVALIADORIMOVEL");
			if (objetoUsuario.isUserAvaliadorImovel()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "LAUDO");
			if (objetoUsuario.isUserLaudo()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "USER_GALACHE");
			if (objetoUsuario.isUserGalache()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "AGENTE_ESPELHAMENTO");
			if (objetoUsuario.isUserAgenteEspelhamento()) {
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

			gAdm = gDao.findByFilter("acronym", "BLOCK_BACKOFFICE");
			if (objetoUsuario.isBlockBackoffice()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PRECOBRANCAANALISTA");
			if (objetoUsuario.isUserPreContratoAnalista()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "ASSISTENTEFINANCEIRO");
			if (objetoUsuario.isAssistFinanceiro()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "POSOPERACAO");
			if (objetoUsuario.isUserPosOperacao()) {
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

			gAdm = gDao.findByFilter("acronym", "COMITEEDITAR");
			if (objetoUsuario.isComiteEditar()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "COMITECONSULTAR");
			if (objetoUsuario.isComiteConsultar()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_ANALISTA_CREDITO");
			if (objetoUsuario.isProfileAnalistaCredito()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_ANALISTA_COMITE");
			if (objetoUsuario.isProfileAnalistaComite()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_ANALISTA_POS_COMITE");
			if (objetoUsuario.isProfileAnalistaPosComite()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_GERENTE_ANALISE");
			if (objetoUsuario.isProfileGerenteAnalise()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_CONTRATO");
			if (objetoUsuario.isProfileContrato()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_COBRANCA");
			if (objetoUsuario.isProfileCobranca()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_COMENTARIO_JURIDICO");
			if (objetoUsuario.isProfileComentarioJuridico()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_AVALIADOR_IMOVEL");
			if (objetoUsuario.isProfileAvaliadorImovel()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_AVALIADOR_IMOVEL_COMPASS");
			if (objetoUsuario.isProfileAvaliadorImovelCompass()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_AVALIADOR_IMOVEL_GALACHE");
			if (objetoUsuario.isProfileAvaliadorImovelGalache()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_LAUDO");
			if (objetoUsuario.isProfileLaudo()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_PAJU_NEVES");
			if (objetoUsuario.isProfilePajuNeves()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_PAJU_LUVISON");
			if (objetoUsuario.isProfilePajuLuvison()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}

			gAdm = gDao.findByFilter("acronym", "PROFILE_MARKETING");
			if (objetoUsuario.isProfileMarketing()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "CADASTRA_RESPONSAVEL");
			if (objetoUsuario.isCadastraResponsavel()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_COMPLIANCE");
			if (objetoUsuario.isProfileCompliance()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_CONTROLLER");
			if (objetoUsuario.isProfileController()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_CONSULTA_KOBANA");
			if (objetoUsuario.isProfileConsultaKobana()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "CONSULTA_INDIVIDUAL");
			if (objetoUsuario.isConsultaIndividual()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PLANEJAMENTO");
			if (objetoUsuario.isUserPlanejamento()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_CARTORIO");
			if (objetoUsuario.isProfileCartorio()) {
				gAdmAux.add(gAdm.get(0));
			} else {
				if (objetoUsuario.getGroupList() != null) {
					objetoUsuario.getGroupList().remove(gAdm);
				}
			}
			
			gAdm = gDao.findByFilter("acronym", "PROFILE_JURIDICO_COBRANCA");
			if (objetoUsuario.isProfileJuridicoCobranca()) {
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

			if (this.objetoUsuario.getHoraInicioPermissaoAcesso() == null
					|| this.objetoUsuario.getHoraFimPermissaoAcesso() == null) {
				this.objetoUsuario.setHoraInicioPermissaoAcesso(null);
				this.objetoUsuario.setHoraFimPermissaoAcesso(null);
			}

			if (this.selectedDiasSemana.length > 0) {
				objetoUsuario.setDiasSemana(Arrays.asList(this.selectedDiasSemana));
			}

			// Google Authenticator
			if (objetoUsuario.isTwoFactorAuth()) {
				if (objetoUsuario.getKey() == null || objetoUsuario.getKey().equals("")) {
					TwoFactorAuth TwoFactorAuth = new TwoFactorAuth();
					String base32Secret = TwoFactorAuth.generateBase32Secret();

					String qrCode = TwoFactorAuth.getQrCodeGoogle(this.objetoUsuario.getLogin() + "@siscoat.com.br",
							base32Secret);

					objetoUsuario.setKey(base32Secret);
					objetoUsuario.setUrlQRCode(qrCode);
				}
			}

			this.objetoUsuario.setListResponsavel(Arrays.asList(this.selectedResponsaveis));

			if (CommonsUtil.semValor(objetoUsuario.getId())) {
				postoDao.create(objetoUsuario);
				msgRetorno = "inserido";
			} else {
				postoDao.merge(objetoUsuario);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Usuário: Registro " + msgRetorno + " com sucesso! (Usuário: " + objetoUsuario.getLogin() + ")",
					""));

			objetoUsuario = new User();

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário: " + e, ""));

			return "";
		}
		
		popularListaResponsavel();

		return "UsuarioConsultar.xhtml";
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserDao postoDao = new UserDao();

		try {
			objetoUsuario.getGroupList().clear();
			postoDao.delete(objetoUsuario);

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Usuário: Registro excluído com sucesso! (Usuário: " + objetoUsuario.getLogin() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Usuário: Exclusão não permitida!! Este registro está relacionado com algum outro registro.", ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário: " + e, ""));

			return "";
		}

		return "UsuarioConsultar.xhtml";
	}

	public void geraLoginSenha() {

		if (!this.updateMode && !this.deleteMode) {
			String[] carctLogin = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

			String login = "";

			if (this.objetoUsuario.getName().length() >= 6) {
				login = this.objetoUsuario.getName().substring(0, 6);
			} else {
				login = this.objetoUsuario.getName().substring(0, this.objetoUsuario.getName().length());

				int count = (6 - this.objetoUsuario.getName().length()) + 1;

				for (int x = 0; x < count; x++) {
					int j = (int) (Math.random() * carctLogin.length);
					login += carctLogin[j];
				}
			}

			this.objetoUsuario.setLogin(login);

			// gera senha
			String[] carctSenha = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g",
					"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A",
					"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
					"V", "W", "X", "Y", "Z" };
			String senha = "";

			for (int x = 0; x < 10; x++) {
				int j = (int) (Math.random() * carctSenha.length);
				senha += carctSenha[j];
			}

			this.objetoUsuario.setPassword(senha);
		}
	}

	public void popularListaResponsavel() {
		UserDao uDao = new UserDao();
		uDao.popularListaResponsavel();
	}

	public void atualizaListagem() {
		UserDao userDao = new UserDao();
		userDao.carregarListaResponsavel(objetoUsuario);
		//selectedResponsaveis = (Responsavel[]) objetoUsuario.getListResponsavel().toArray();
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<User> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
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
	 * @param objetoUsuario the objetoUsuario to set
	 */
	public void setObjetoUsuario(User objetoUsuario) {
		if (CommonsUtil.semValor(objetoUsuario.getUserPerfil())) {
			if (userSemPerfil == null)
				carregaListaPerfil();
			objetoUsuario.setUserPerfil(userSemPerfil.get());
		}
		this.objetoUsuario = objetoUsuario;
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

	public Responsavel[] getSelectedResponsaveis() {
		return selectedResponsaveis;
	}

	public void setSelectedResponsaveis(Responsavel[] selectedResponsaveis) {
		this.selectedResponsaveis = selectedResponsaveis;
	}

	public List<Responsavel> getResponsaveis() {
		return responsaveis;
	}

	public void setResponsaveis(List<Responsavel> responsaveis) {
		this.responsaveis = responsaveis;
	}

	public List<UserPerfil> getPerfil() {
		return perfil;
	}

	public void setPerfil(List<UserPerfil> perfil) {
		this.perfil = perfil;
	}
}
