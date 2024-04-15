package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.cobranca.db.model.ComissaoResponsavel;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ComissaoResponsavelDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.cobranca.service.ResponsavelService;
import com.webnowbr.siscoat.common.ComissaoOrigemEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.mb.UsuarioMB;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "responsavelMB")
@SessionScoped
public class ResponsavelMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<Responsavel> lazyModel;
	/** Variavel. */
	private Responsavel objetoResponsavel;
	private Responsavel objetoResponsavelGeral;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	private boolean tipoPessoaIsFisica = false;
	
	private long idResponsavel;
	private String nomeResponsavel = null;
	private Responsavel selectedResponsavel;
	private List<Responsavel> listResponsaveis;
	private List<Responsavel> listResponsaveisGeral;
	
	
	private long idResponsavelCaptador;
	private String nomeResponsavelCaptador = null;
	
	private long idResponsavelAssistenteComercial;
	private String nomeResponsavelAssistenteComercial = null;
	
	private boolean addUsuario = false;
	private boolean showUsuario = false;
	private String tipoPesquisa;
	
	private String login = "";
	private String senha = "";
	private Responsavel selectedResponsaveis[];
	
	private boolean addComissao = false;
	private ComissaoResponsavel selectedComissao = new ComissaoResponsavel();
	List<ComissaoResponsavel> taxasComissao;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
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
		objetoResponsavel.salvarDadosBancarios();
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
		showUsuario = false;
		addUsuario = false;
		login = "";
		senha = "";
		List<User> listaUser = uDao.findByFilter("codigoResponsavel", objetoResponsavel.getCodigo());
		if(!CommonsUtil.semValor(listaUser)) {
			User user = listaUser.get(0);
			addUsuario = false;
			if(!CommonsUtil.semValor(user)) {
				showUsuario = true;
				login = user.getLogin();
				senha = user.getPassword();
			}
		}
		
		this.selectedResponsaveis = new Responsavel[0];
		
		loadResponsavel();
	
		return "ResponsavelDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		if (this.objetoResponsavel.getCnpj() != null) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}	
		
		UserDao uDao = new UserDao();
		showUsuario = false;
		addUsuario = false;
		login = "";
		senha = "";
		List<User> listaUser = new ArrayList<User>();
		listaUser = uDao.findByFilter("codigoResponsavel", objetoResponsavel.getCodigo());
		if(!CommonsUtil.semValor(listaUser)) {
			User user = listaUser.get(0);
			addUsuario = false;
			if(!CommonsUtil.semValor(user)) {
				showUsuario = true;
				login = user.getLogin();
				senha = user.getPassword();
			}
		}
		
		this.selectedResponsaveis = new Responsavel[0];
		
		loadResponsavel();
		loadLovResponsavel();
		
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
			
			if (!CommonsUtil.semValor(this.idResponsavel)) {
				objetoResponsavel.setDonoResponsavel(responsavelDao.findById(this.idResponsavel));
			} else {
				objetoResponsavel.setDonoResponsavel(null);
			}
			
			if (!CommonsUtil.semValor(this.idResponsavelCaptador)) {
				objetoResponsavel.setResponsavelCaptador(responsavelDao.findById(this.idResponsavelCaptador));
			} else {
				objetoResponsavel.setResponsavelCaptador(null);
			}
			
			if (!CommonsUtil.semValor(this.idResponsavelAssistenteComercial)) {
				objetoResponsavel.setResponsavelAssistenteComercial(responsavelDao.findById(this.idResponsavelAssistenteComercial));
			} else {
				objetoResponsavel.setResponsavelAssistenteComercial(null);
			}

			if (objetoResponsavel.getId() <= 0) {
				responsavelDao.create(objetoResponsavel);
				msgRetorno = "inserido";
			} else {
				responsavelDao.merge(objetoResponsavel);
				msgRetorno = "atualizado";
			}
			
			if(this.addUsuario) {
				if (userDao.findByFilter("codigoResponsavel", this.objetoResponsavel.getCodigo()).size() <= 0) {	
					UsuarioMB userMb = new UsuarioMB();
					userMb.clearFields();
					userMb.getObjetoUsuario().setPassword(this.getSenha());
					userMb.getObjetoUsuario().setLogin(this.getLogin());
					userMb.getObjetoUsuario().setCodigoResponsavel(this.objetoResponsavel.getCodigo());
					userMb.getObjetoUsuario().setName(this.objetoResponsavel.getNome());
					userMb.getObjetoUsuario().setUserPreContrato(true);
					if(CommonsUtil.semValor(userMb.getObjetoUsuario().getListResponsavel())) {
						userMb.getObjetoUsuario().setListResponsavel(new ArrayList<>());
					}
					userMb.getObjetoUsuario().getListResponsavel().add(this.objetoResponsavel);
					userMb.inserir();
					
					for (Responsavel responsavel : this.selectedResponsaveis) {
						user = userDao.findByFilter("codigoResponsavel", responsavel.getCodigo()).get(0);
						user.getListResponsavel().add(this.objetoResponsavel);
						userDao.merge(user);
					}
				} else {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Codigo já Resgistrado", ""));
					return "";
				}
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Responsavel: Registro " + msgRetorno
					+ " com sucesso! (Responsavel: " + objetoResponsavel.getNome() + ")", ""));
			
			removerUsuariosDoGuardaChuva(userDao, responsavelDao);
			adicionarGuardaChuvaComercial(userDao, responsavelDao);
			adicionarGuardaChuvaAssitente(userDao, responsavelDao);
			
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
	
	public String desativar() {
		UserDao uDao = new UserDao();
		ResponsavelDao responsavelDao = new ResponsavelDao();
		User user;
		if(!CommonsUtil.semValor(uDao.findByFilter("login", this.login))) {
			user = uDao.findByFilter("login", this.login).get(0);
			UsuarioMB userMb = new UsuarioMB();
			userMb.setObjetoUsuario(user);		
			userMb.clearFieldsUpdate();
			userMb.getObjetoUsuario().setComiteConsultar(false);
			userMb.getObjetoUsuario().setPassword(gerarUUID());
			//userMb.setSelectedResponsaveis(new Responsavel[0]);
			userMb.getObjetoUsuario().setName("DESATIVADO - " + user.getName());
			userMb.inserir();
		}
		
		objetoResponsavel.setDesativado(true);
		objetoResponsavel.setDataDesativado(DateUtil.gerarDataHoje());
		objetoResponsavel.setNome("DESATIVADO - " + objetoResponsavel.getNome());
		responsavelDao.merge(objetoResponsavel);
		
		return "ResponsavelConsultar.xhtml";
	}
	
	public String gerarUUID() {
		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString();
		return uuidAsString;
	}
	
	public void pesquisaResponsavel() {	
		this.setListResponsaveis(listResponsaveisGeral);
		this.tipoPesquisa = "Responsavel";
	}
	
	public void pesquisaResponsavelCaptador() {
		this.setListResponsaveis(listResponsaveisGeral);
		this.tipoPesquisa = "Captador";
	}
	
	public void pesquisaAssistenteComercial() {
		this.setListResponsaveis(this.listResponsaveisGeral.stream().filter(Responsavel::isAssistenteComercial).collect(Collectors.toList()));
		this.tipoPesquisa = "Assistente";
	}
	
	public final void populateSelectedResponsavel2() {
		this.idResponsavel = this.selectedResponsavel.getId();
		this.nomeResponsavel = this.selectedResponsavel.getNome();
	}
	
	public final void populateSelectedResponsavel() {
		if(CommonsUtil.mesmoValor(tipoPesquisa, "Responsavel")) {
			this.idResponsavel = this.selectedResponsavel.getId();
			this.nomeResponsavel = this.selectedResponsavel.getNome();
		} else if(CommonsUtil.mesmoValor(tipoPesquisa, "Captador")) {
			this.idResponsavelCaptador = this.selectedResponsavel.getId();
			this.nomeResponsavelCaptador = this.selectedResponsavel.getNome();
		} else if(CommonsUtil.mesmoValor(tipoPesquisa, "Assistente")) {
			this.idResponsavelAssistenteComercial = this.selectedResponsavel.getId();
			this.nomeResponsavelAssistenteComercial = this.selectedResponsavel.getNome();
		}
		this.tipoPesquisa = "";
	}
	
	public void clearResponsavel() {
		this.idResponsavel = 0;
		this.nomeResponsavel = null;
		this.idResponsavelCaptador = 0;
		this.nomeResponsavelCaptador = "";
		this.idResponsavelAssistenteComercial = 0;
		this.nomeResponsavelAssistenteComercial = "";
		this.selectedResponsavel = new Responsavel();
		this.tipoPesquisa = "";
	}
	
	public void clearResponsavelDialog() {
		if(CommonsUtil.mesmoValor(tipoPesquisa, "Responsavel")) {
			this.idResponsavel = 0;
			this.nomeResponsavel = "";
		} else if(CommonsUtil.mesmoValor(tipoPesquisa, "Captador")) {
			this.idResponsavelCaptador = 0;
			this.nomeResponsavelCaptador = "";
		}
		this.tipoPesquisa = "";
	}
	
	public void loadLovResponsavel() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		//this.listResponsaveis = responsavelDao.findAll();
		this.listResponsaveisGeral = responsavelDao.findAll();
		this.setListResponsaveis(listResponsaveisGeral);
	}
	
	public void loadResponsavel() {
		if(!CommonsUtil.semValor(this.objetoResponsavel.getDonoResponsavel())) {
			this.idResponsavel = this.objetoResponsavel.getDonoResponsavel().getId();
			this.nomeResponsavel = this.objetoResponsavel.getDonoResponsavel().getNome();
		} else {
			this.idResponsavel = 0;
			this.nomeResponsavel = null;	
		}
		
		if(!CommonsUtil.semValor(this.objetoResponsavel.getResponsavelCaptador())) {
			this.idResponsavelCaptador = this.objetoResponsavel.getResponsavelCaptador().getId();
			this.nomeResponsavelCaptador = this.objetoResponsavel.getResponsavelCaptador().getNome();
		} else {
			this.idResponsavelCaptador = 0;
			this.nomeResponsavelCaptador = "";
		}
		if(!CommonsUtil.semValor(this.objetoResponsavel.getResponsavelAssistenteComercial())) {
			this.idResponsavelAssistenteComercial = this.objetoResponsavel.getResponsavelAssistenteComercial().getId();
			this.nomeResponsavelAssistenteComercial = this.objetoResponsavel.getResponsavelAssistenteComercial().getNome();
		} else {
			this.idResponsavelAssistenteComercial = 0;
			this.nomeResponsavelAssistenteComercial = "";
		}
		
		objetoResponsavel.salvarDadosBancarios();
		
		this.tipoPesquisa = "";
	}
	
	private void adicionarGuardaChuvaComercial(UserDao userDao, ResponsavelDao responsavelDao) {
		User userComercial = null;
		userComercial = responsavelDao.getUsersComercialDono(objetoResponsavel);
		if(!CommonsUtil.semValor(userComercial)) {
			if(!userComercial.getListResponsavel().contains(objetoResponsavel)) {
				userComercial.getListResponsavel().add(objetoResponsavel);
				userDao.merge(userComercial);
			}
		}
	}
	
	private void adicionarGuardaChuvaAssitente(UserDao userDao, ResponsavelDao responsavelDao) {
		User userComercial = null;
		userComercial = responsavelDao.getUsersAssistente(objetoResponsavel);
		if(!CommonsUtil.semValor(userComercial)) {
			if(!userComercial.getListResponsavel().contains(objetoResponsavel)) {
				userComercial.getListResponsavel().add(objetoResponsavel);
				userDao.merge(userComercial);
			}
		}
	}
	
	private void removerUsuariosDoGuardaChuva(UserDao userDao, ResponsavelDao responsavelDao) {
		List<User> listaUsuarios = new ArrayList<User>();
		listaUsuarios = responsavelDao.getUsersComPermissao(objetoResponsavel);
		for(User userDono : listaUsuarios) {
			userDono.getListResponsavel().remove(objetoResponsavel);
			userDao.merge(userDono);
		}
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

	public void selectedNome() {
		this.objetoResponsavel.setNomeCC(this.objetoResponsavel.getNome());		
	}
	
	public void selectedCPF() {
		this.objetoResponsavel.setCpfCnpjCC(this.objetoResponsavel.getCpf());		
	}
	
	public void selectedCNPJ() {
		this.objetoResponsavel.setCpfCnpjCC(this.objetoResponsavel.getCnpj());		
	}
	
	public void clearComissaoTodos() {
		this.selectedComissao = new ComissaoResponsavel();
		this.taxasComissao = new ArrayList<ComissaoResponsavel>();
		this.objetoResponsavel = null;

		ResponsavelService responsavelService = new ResponsavelService();
		objetoResponsavelGeral = responsavelService.getObjetoResponsavelGeral();
		objetoResponsavel = objetoResponsavelGeral ;
		this.addComissao = false;
		loadLovResponsavel();
	}
	public void adicionarComissao(List<ComissaoResponsavel> lista) {
		this.selectedComissao = new ComissaoResponsavel();
	
		
		Optional<BigDecimal> valorMinimo = Optional.of(BigDecimal.ZERO);
		if (CommonsUtil.semValor(this.objetoResponsavel.getTaxasComissao())) {
			 this.objetoResponsavel.setTaxasComissao( new HashSet<ComissaoResponsavel>());
		} else {
			valorMinimo = this.objetoResponsavel.getTaxasComissao().stream().map(m -> m.getValorMaximo()).max(Comparator.naturalOrder());
			if (valorMinimo.isPresent())
				valorMinimo=  Optional.of(valorMinimo.get().add(BigDecimal.valueOf(0.01)));
		}
		if (valorMinimo.isPresent())
			this.selectedComissao.setValorMinimo(valorMinimo.get());
	}
	
	public void adicionarComissao(List<ComissaoResponsavel> lista, 	ComissaoResponsavel selectedComissao) {
		this.selectedComissao = new ComissaoResponsavel();
	
		Optional<BigDecimal> valorMinimo = Optional.of(BigDecimal.ZERO);
		if (CommonsUtil.semValor(this.objetoResponsavel.getTaxasComissao())) {
			 this.objetoResponsavel.setTaxasComissao( new HashSet<ComissaoResponsavel>());
		} else {
			valorMinimo = this.objetoResponsavel.getTaxasComissao().stream().map(m -> m.getValorMaximo()).max(Comparator.naturalOrder());
			if (valorMinimo.isPresent())
				valorMinimo=  Optional.of(valorMinimo.get().add(BigDecimal.valueOf(0.01)));
		}
		if (valorMinimo.isPresent())
			this.selectedComissao.setValorMinimo(valorMinimo.get());
	}
	
	
	public void concluirComissao(List<ComissaoResponsavel> lista) {
		FacesContext context = FacesContext.getCurrentInstance();
		String erro = "";
		if(this.selectedComissao.getValorMinimo().compareTo(this.selectedComissao.getValorMaximo()) >= 1) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "valor minimo maior que maximo" , ""));
			return;
		}
		if(CommonsUtil.semValor(this.objetoResponsavel.getTaxasComissao())) {
			 this.objetoResponsavel.setTaxasComissao( new HashSet<ComissaoResponsavel>());
		} else {
			
			if ( this.objetoResponsavel.getTaxasComissao().stream().filter( comissao -> this.selectedComissao.getValorMinimo().compareTo(comissao.getValorMinimo()) >= 1 
					&& this.selectedComissao.getValorMinimo().compareTo(comissao.getValorMaximo()) <= 0  ).findAny().isPresent())
				erro = erro + "Valor minimo Indisponivel";	
			
			if ( this.objetoResponsavel.getTaxasComissao().stream().filter( comissao -> this.selectedComissao.getValorMaximo().compareTo(comissao.getValorMinimo()) >= 1 
					&& this.selectedComissao.getValorMaximo().compareTo(comissao.getValorMaximo()) <= 0  ).findAny().isPresent())
				erro = erro + "Valor maximo Indisponivel";	
			
//			for(ComissaoResponsavel comissao : lista) {
//				if(this.selectedComissao.getValorMinimo().compareTo(comissao.getValorMinimo()) >= 1 
//						&& this.selectedComissao.getValorMinimo().compareTo(comissao.getValorMaximo()) <= 0 ) {
//					erro = erro + "Valor minimo Indisponivel";	
//				}
//				if(this.selectedComissao.getValorMaximo().compareTo(comissao.getValorMinimo()) >= 1 
//						&& this.selectedComissao.getValorMaximo().compareTo(comissao.getValorMaximo()) <= 0 ) {
//					if(erro.length() > 1) {
//						erro = erro + " / ";	
//					}		
//					erro = erro + "Valor maximo Indisponivel";	
//				}
//			}
			if(erro.length() > 1) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, erro , ""));
				return;
			} 		
		}
		if(!CommonsUtil.semValor(this.objetoResponsavel)) {
			this.selectedComissao.setResponsavel(this.objetoResponsavel);
		}
		this.selectedComissao.setUserCriacao(getUsuarioLogado());
		this.selectedComissao.setLoginCriacao(getNomeUsuarioLogado());
		this.selectedComissao.setDataCriacao(DateUtil.gerarDataHoje());
		this.selectedComissao.setAtiva(true);
		
		ComissaoResponsavelDao comDao = new ComissaoResponsavelDao();
		comDao.create(this.selectedComissao);
		Set<ComissaoResponsavel> taxasAtualizada = this.objetoResponsavel.getTaxasComissao();
		taxasAtualizada.add(this.selectedComissao);
		this.objetoResponsavel.setTaxasComissao(taxasAtualizada);
		
		ResponsavelDao respDao = new ResponsavelDao();
		respDao.update(this.objetoResponsavel);
		this.setAddComissao(false);
	}
	
	public void editarComissao(ComissaoResponsavel comissao) {
		this.setAddComissao(true);
		selectedComissao = new ComissaoResponsavel(comissao);
		removerComissao(comissao);
	}
	
	public void removerComissao(ComissaoResponsavel comissao) {
		comissao.setUserRemocao(getUsuarioLogado());
		comissao.setLoginRemocao(getNomeUsuarioLogado());
		comissao.setDataRemocao(DateUtil.gerarDataHoje());
		comissao.setAtiva(false);
		ComissaoResponsavelDao comDao = new ComissaoResponsavelDao();
		comDao.merge(comissao);
		this.objetoResponsavel.getTaxasComissao().remove(comissao);
	}
	
	public void atualizarComissaoTodosResponsaveis() {
		ResponsavelDao rDao = new ResponsavelDao();
		for(Responsavel r : this.listResponsaveis) {
			this.objetoResponsavel = r;
			if(!CommonsUtil.semValor(this.objetoResponsavel.getTaxasComissao())) {
				while(this.objetoResponsavel.getTaxasComissao().size() > 0) {
					removerComissao(this.objetoResponsavel.getTaxasComissao().stream().collect(Collectors.toList()).get(0));
				}
				/*for(ComissaoResponsavel c : this.objetoResponsavel.getTaxasComissao()) {
					removerComissao(c);
				}*/
			}
			for(ComissaoResponsavel c : this.taxasComissao) {
				this.selectedComissao = new ComissaoResponsavel(c);
				concluirComissao(this.objetoResponsavel.getTaxasComissao().stream().collect(Collectors.toList()));
			}
			rDao.merge(r);
		}
		this.objetoResponsavel = new Responsavel();
		PrimeFaces current = PrimeFaces.current();
		current.executeScript("PF('ComissaoResponsaveis').hide();");
	}
	

	public List<ComissaoResponsavel> getTaxasComissao() {
		
		if( CommonsUtil.semValor(this.objetoResponsavel ) )
			return null;
		if ( CommonsUtil.mesmoValor( this.objetoResponsavel.getId() , SiscoatConstants.RESPONSAVEL_GERAL_ID  ))
			return this.objetoResponsavel.getTaxasComissao(ComissaoOrigemEnum.GERAL);
		else
			return this.objetoResponsavel.getTaxasComissao(ComissaoOrigemEnum.INDIVIDUAL);
	}

	public void setTaxasComissao(List<ComissaoResponsavel> taxasComissao) {
		this.taxasComissao = taxasComissao;
	}
	
	public List<ComissaoResponsavel> getTaxasComissaoIndividual() {
		return this.objetoResponsavel.getTaxasComissao(ComissaoOrigemEnum.INDIVIDUAL);
	}
	
	public List<ComissaoResponsavel> getTaxasComissaoGlobal() {
		ResponsavelService responsavelService = new ResponsavelService();
		objetoResponsavelGeral = responsavelService.getObjetoResponsavelGeral();		
		return this.objetoResponsavelGeral.getTaxasComissao(ComissaoOrigemEnum.GERAL);
	}

	
	
	public User getUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);
			}
		}

		return usuario;
	}
	
	public String getNomeUsuarioLogado() {
		User usuario = getUsuarioLogado();

		if (usuario.getLogin() != null) {
			if (!usuario.getLogin().equals("")) {
				return usuario.getLogin();
			} else {
				return "";
			}
		} else {
			return "";
		}
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
		addComissao = false;
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

	public String getNomeResponsavelCaptador() {
		return nomeResponsavelCaptador;
	}

	public void setNomeResponsavelCaptador(String nomeResponsavelCaptador) {
		this.nomeResponsavelCaptador = nomeResponsavelCaptador;
	}

	public long getIdResponsavelCaptador() {
		return idResponsavelCaptador;
	}

	public void setIdResponsavelCaptador(long idResponsavelCaptador) {
		this.idResponsavelCaptador = idResponsavelCaptador;
	}

	public String getNomeAssistenteComercial() {
		return nomeResponsavelAssistenteComercial;
	}

	public void setNomeAssistenteComercial(String nomeAssistenteComercial) {
		this.nomeResponsavelAssistenteComercial = nomeAssistenteComercial;
	}

	public long getIdResponsavelAssistenteComercial() {
		return idResponsavelAssistenteComercial;
	}

	public void setIdResponsavelAssistenteComercial(long idResponsavelAssistenteComercial) {
		this.idResponsavelAssistenteComercial = idResponsavelAssistenteComercial;
	}

	
	public boolean isAddComissao() {
		return addComissao;
	}

	public void setAddComissao(boolean addComissao) {
		this.addComissao = addComissao;
	}

	public ComissaoResponsavel getSelectedComissao() {
		return selectedComissao;
	}

	public void setSelectedComissao(ComissaoResponsavel selectedComissao) {
		this.selectedComissao = selectedComissao;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}	
}
