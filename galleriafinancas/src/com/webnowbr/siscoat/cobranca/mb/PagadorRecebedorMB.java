package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.json.JSONObject;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorReceborDadosBancarios;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.model.cep.CepResult;
import com.webnowbr.siscoat.cobranca.service.CepService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.GroupDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.GroupAdm;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaContaBancariaRequest;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaContaBancariaResponse;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaPixRequest;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaPixResponse;

/** ManagedBean. */
@ManagedBean(name = "pagadorRecebedorMB")
@SessionScoped
public class PagadorRecebedorMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<PagadorRecebedor> lazyModel;
	/** Variavel. */
	private PagadorRecebedor objetoPagadorRecebedor;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	private boolean tipoPessoaIsFisica = false;
	private boolean tipoPessoaIsFisicaCC = false;
	private List<PagadorRecebedor> listaPagadorRecebedor;
	private List<PagadorRecebedor> filteredPagadorRecebedor;
	
	private String telefoneAnterior;
	
	private PagadorReceborDadosBancarios pagadorReceborDadosBancariosOriginal;
	/**
	 * Construtor.
	 */
	public PagadorRecebedorMB() {

		objetoPagadorRecebedor = new PagadorRecebedor();

		lazyModel = new LazyDataModel<PagadorRecebedor>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<PagadorRecebedor> load(final int first, final int pageSize,
					final String sortField, final SortOrder sortOrder,
					final Map<String, Object> filters) {

				PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

				setRowCount(pagadorRecebedorDao.count(filters));
				return pagadorRecebedorDao.findByFilter(first, pageSize, sortField,
						sortOrder.toString(), filters);
			}
		};
	}

//	@ManagedProperty("#{paramValues.foo}")
//	private String[] foos;
//	
	
	
	public void specialMethod()
	{
	    Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	    String param1 = params.get("param1");
	}
	
	public String clearFields() {
		objetoPagadorRecebedor = new PagadorRecebedor();
		this.tituloPainel = "Adicionar";
		
		this.tipoPessoaIsFisica = true;
		
		this.tipoPessoaIsFisicaCC = true;
		
		this.objetoPagadorRecebedor.setEstado("SP");

		return "PagadorRecebedorInserir.xhtml";
	}
	
	public String clearFieldsView() {
		if (this.objetoPagadorRecebedor.getCnpj() != null && !this.objetoPagadorRecebedor.getCnpj().equals("")) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}	
		
		if (this.objetoPagadorRecebedor.getCnpjCC() != null && !this.objetoPagadorRecebedor.getCnpjCC().equals("")) {
			this.tipoPessoaIsFisicaCC = false;
		}
		
		if (this.objetoPagadorRecebedor.getCpfCC() != null && !this.objetoPagadorRecebedor.getCpfCC().equals("")) {
			this.tipoPessoaIsFisicaCC = true;
		}

		return "PagadorRecebedorDetalhes.xhtml";
	}
	
	public String clearFieldsUpdate() {
		if (this.objetoPagadorRecebedor.getCnpj() != null && !this.objetoPagadorRecebedor.getCnpj().equals("")) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}	
		
		if (this.objetoPagadorRecebedor.getCnpjCC() != null && !this.objetoPagadorRecebedor.getCnpjCC().equals("")) {
			this.tipoPessoaIsFisicaCC = false;
		}
		
		if (this.objetoPagadorRecebedor.getCpfCC() != null && !this.objetoPagadorRecebedor.getCpfCC().equals("")) {
			this.tipoPessoaIsFisicaCC = true;
		}
		
		this.telefoneAnterior = this.objetoPagadorRecebedor.getTelCelular();
		
		pagadorReceborDadosBancariosOriginal = new PagadorReceborDadosBancarios(objetoPagadorRecebedor);
		

		return "PagadorRecebedorInserir.xhtml";
	}	
	
	public void selectedTipoPessoa() {
		if (this.tipoPessoaIsFisica) {
			this.objetoPagadorRecebedor.setCnpj(null);
			this.objetoPagadorRecebedor.setNome(null);
			
			this.objetoPagadorRecebedor.setCnpjCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
			
			this.tipoPessoaIsFisicaCC = true;
		} else {
			this.objetoPagadorRecebedor.setCpf(null);
			this.objetoPagadorRecebedor.setRg(null);
			this.objetoPagadorRecebedor.setNome(null);
			
			this.tipoPessoaIsFisicaCC = false;
			
			this.objetoPagadorRecebedor.setCpfCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		}
	}
	
	public void selectedTipoPessoaCC() {
		if (this.tipoPessoaIsFisica) {
			this.objetoPagadorRecebedor.setCnpjCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		} else {			
			this.objetoPagadorRecebedor.setCpfCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		}
	}
	
	public void selectedNome() {
		this.objetoPagadorRecebedor.setNomeCC(this.objetoPagadorRecebedor.getNome());		
	}
	
	public void selectedCPF() {
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		pagadorRecebedorService.preecheDadosReceita(objetoPagadorRecebedor);
		this.objetoPagadorRecebedor.setCpfCC(this.objetoPagadorRecebedor.getCpf());
		this.objetoPagadorRecebedor.setNomeCC(objetoPagadorRecebedor.getNome());
	}
	
	public void selectedCNPJ() {		
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		pagadorRecebedorService.preecheDadosReceita(objetoPagadorRecebedor);
		this.objetoPagadorRecebedor.setCnpjCC(this.objetoPagadorRecebedor.getCnpj());
		this.objetoPagadorRecebedor.setNomeCC(objetoPagadorRecebedor.getNome());				
	}

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		String msgRetorno = null;
		try {			
			if (!this.objetoPagadorRecebedor.getSite().contains("http") && !this.objetoPagadorRecebedor.getSite().contains("HTTP")) {
				this.objetoPagadorRecebedor.setSite("http://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
			}
			
			TakeBlipMB takeBlipMB = new TakeBlipMB();
			
			if (!CommonsUtil.mesmoValor(this.telefoneAnterior, this.objetoPagadorRecebedor.getTelCelular())) {
				this.objetoPagadorRecebedor.setWhatsAppNumero(takeBlipMB.getWhatsAppURLNovoPagadorRecebedor(this.objetoPagadorRecebedor));
			}
			
			if (this.objetoPagadorRecebedor.getWhatsAppNumero() == null || this.objetoPagadorRecebedor.getWhatsAppNumero().equals("")) {				
				this.objetoPagadorRecebedor.setWhatsAppNumero(takeBlipMB.getWhatsAppURLNovoPagadorRecebedor(this.objetoPagadorRecebedor));
			}
			/*			
			if (this.objetoPagadorRecebedor.getCpf() != null) {
				if (pagadorRecebedorDao.findByFilter("cpf", this.objetoPagadorRecebedor.getCpf()).size() > 0) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "PagadorRecebedor: Já existe um Pagador/Recebedor com o CPF informado, por favor, pesquise-o e altere o cadastro ao invés de inserir um novo!", ""));
					return "";
				}
			}
			
			if (this.objetoPagadorRecebedor.getCnpj() != null) {
				if (pagadorRecebedorDao.findByFilter("cnpj", this.objetoPagadorRecebedor.getCnpj()).size() > 0) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "PagadorRecebedor: Já existe um Pagador/Recebedor com o CNPJ informado, por favor, pesquise-o e altere o cadastro ao invés de inserir um novo!", ""));
					return "";
				}
			}
	*/
			
			if (objetoPagadorRecebedor.getId() <= 0) {
				pagadorRecebedorDao.create(objetoPagadorRecebedor);
				msgRetorno = "inserido";
			} else {
				pagadorRecebedorDao.merge(objetoPagadorRecebedor);
				msgRetorno = "atualizado";
			}
			
			// se a pessoa for investidor e terá acesso ao portal
			UserDao userDao = new UserDao();	
			
			if (this.objetoPagadorRecebedor.isUserInvestidor()) {
				//verifica se o usuário já existe 				
				List<User> users = new ArrayList<User>();
				users = userDao.findByFilter("login", this.objetoPagadorRecebedor.getLoginInvestidor());
				
				// se usuário ja existe atualiza a senha
				if (users.size() > 0) {
					users.get(0).setPassword(this.objetoPagadorRecebedor.getSenhaInvestidor());
					userDao.merge(users.get(0));
					
					this.objetoPagadorRecebedor.setUsuario(users.get(0));
				} else {
					// senao cria usuário
					User investidor = new User();
					investidor.setName(this.objetoPagadorRecebedor.getNome());
					investidor.setLogin(this.objetoPagadorRecebedor.getLoginInvestidor());
					investidor.setPassword(this.objetoPagadorRecebedor.getSenhaInvestidor());
					investidor.setUserInvestidor(true);
					investidor.setLevel(0);

					GroupDao gDao = new GroupDao();
					List<GroupAdm> gAdm = new ArrayList<GroupAdm>();
					List<GroupAdm> gAdmAux = new ArrayList<GroupAdm>();
					gAdm = gDao.findByFilter("acronym", "INVESTIDOR");
					if (investidor.isUserInvestidor()) {				
						gAdmAux.add(gAdm.get(0));
					} else {
						if (investidor.getGroupList() != null) {
							investidor.getGroupList().remove(gAdm);
						}
						investidor.setCodigoResponsavel(null);
					}
					
					investidor.setGroupList(gAdmAux);
					
					userDao.create(investidor);
					
					this.objetoPagadorRecebedor.setUsuario(investidor);
				}				
			} else {
				this.objetoPagadorRecebedor.setLoginInvestidor(null);
				this.objetoPagadorRecebedor.setSenhaInvestidor(null);
				
				long idUserDelete = 0;
				if (this.objetoPagadorRecebedor.getUsuario() != null) {
					idUserDelete = this.objetoPagadorRecebedor.getUsuario().getId();
				}
				this.objetoPagadorRecebedor.setUsuario(null);				
				pagadorRecebedorDao.merge(objetoPagadorRecebedor);
				
				// se deixar de ter acesso, apaga o usuário
				if (idUserDelete > 0) {
					userDao.delete(userDao.findById(idUserDelete));
				}
			}
			
			pagadorRecebedorDao.merge(objetoPagadorRecebedor);
			
			if(!CommonsUtil.semValor(objetoPagadorRecebedor.getCpfConjuge())) {
				objetoPagadorRecebedor.criarConjugeNoSistema();
			}
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "PagadorRecebedor: Registro "
							+ msgRetorno + " com sucesso! (PagadorRecebedor: "
							+ objetoPagadorRecebedor.getNome() + ")", ""));
			
			objetoPagadorRecebedor = new PagadorRecebedor("PagadorRecebedorMB");

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PagadorRecebedor: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PagadorRecebedor: " + e, ""));

			return "";
		}

		return "PagadorRecebedorConsultar.xhtml";
	}
	
	public String verificaAlteracaoContaCobranca() {

		if (!CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getBanco(),
				pagadorReceborDadosBancariosOriginal.getBanco())
				|| !CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getAgencia(),
						pagadorReceborDadosBancariosOriginal.getAgencia())
				|| !CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getConta(),
						pagadorReceborDadosBancariosOriginal.getConta())
				|| !CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getContaDigito(),
						pagadorReceborDadosBancariosOriginal.getContaDigito())
				|| !CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getTipoConta(),
						pagadorReceborDadosBancariosOriginal.getTipoConta())) {
			this.objetoPagadorRecebedor.setContaBancariaValidada(false);
			PrimeFaces.current().ajax().update("form:PanelDadosBancarios");
		}		
		return null;
		
	}
	
	public String verificaAlteracaoPix() {

		if (!CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getPix(), pagadorReceborDadosBancariosOriginal.getPix())
				|| !CommonsUtil.mesmoValor(this.objetoPagadorRecebedor.getTipoPix(),
						pagadorReceborDadosBancariosOriginal.getTipoPix())) {
			this.objetoPagadorRecebedor.setPixValidado(false);
			this.objetoPagadorRecebedor.setBancoPix(null);
			this.objetoPagadorRecebedor.setAgenciaPix(null);
			this.objetoPagadorRecebedor.setContaPix(null);
			this.objetoPagadorRecebedor.setContaDigitoPix(null);
			PrimeFaces.current().ajax().update("form:PanelDadosBancarios");
		}
		return null;

	}
	
	public String atualizarIUGU() {
	
		// atualiza IUGU
		IuguMB iuguMB = new IuguMB();
		iuguMB.alteraDomicilioBancarioSubConta(this.objetoPagadorRecebedor);
		
		// atualiza cadastro
		inserir();
		
		return "PagadorRecebedorConsultar.xhtml";
	}
	
	public String validaPix() {

		FacesContext context = FacesContext.getCurrentInstance();
		NetrinService netrinService = new NetrinService();
		
		
		String documento = CommonsUtil.somenteNumeros( this.objetoPagadorRecebedor.getCpf());
		if (!CommonsUtil.semValor( this.objetoPagadorRecebedor.getCnpj())) {
			documento= CommonsUtil.somenteNumeros( this.objetoPagadorRecebedor.getCnpj());
		}
		
		ValidaPixRequest validaPixRequest = new ValidaPixRequest(this.objetoPagadorRecebedor.getPix(), this.objetoPagadorRecebedor.getTipoPix(), documento);
		ValidaPixResponse result = netrinService.requestValidaPix(validaPixRequest, context);
		
		if (!CommonsUtil.semValor(result) && !CommonsUtil.semValor(result.getValidaPix())
				&& CommonsUtil.mesmoValorIgnoreCase("Sim", result.getValidaPix().getValidacaoConta())) {
			this.objetoPagadorRecebedor.setPixValidado(true);
			this.objetoPagadorRecebedor.setBancoPix(result.getValidaPix().getConta().getCodigoBanco());
			this.objetoPagadorRecebedor.setAgenciaPix(result.getValidaPix().getConta().getAgencia());
			this.objetoPagadorRecebedor.setContaPix(result.getValidaPix().getConta().getConta());
			this.objetoPagadorRecebedor.setContaDigitoPix(result.getValidaPix().getConta().getContaDigito());
			PrimeFaces.current().ajax().update("form:PanelDadosBancarios");	
		}
		
		return null;
	}
	
	public String validaContaBancaria() {

		FacesContext context = FacesContext.getCurrentInstance();
		NetrinService netrinService = new NetrinService();
		
		
		String documento = CommonsUtil.somenteNumeros( this.objetoPagadorRecebedor.getCpf());
		if (!CommonsUtil.semValor( this.objetoPagadorRecebedor.getCnpj())) {
			documento= CommonsUtil.somenteNumeros( this.objetoPagadorRecebedor.getCnpj());
		}
		
		ValidaContaBancariaRequest validaContaBancariaRequest = new ValidaContaBancariaRequest(documento, this.objetoPagadorRecebedor.getCodigoBanco(), 
				this.objetoPagadorRecebedor.getAgencia(), this.objetoPagadorRecebedor.getConta(), this.objetoPagadorRecebedor.getContaDigito(), 
				this.objetoPagadorRecebedor.getTipoConta());
		ValidaContaBancariaResponse result = netrinService.requestValidaContaBancaria(validaContaBancariaRequest, context);

		if (!CommonsUtil.semValor(result)
				&& CommonsUtil.mesmoValorIgnoreCase("Sim", result.getValidaContaBancaria().getValidacaoConta())) {
			this.objetoPagadorRecebedor.setContaBancariaValidada(true);
			PrimeFaces.current().ajax().update("form:PanelDadosBancarios");
		}
		
		return null;
	}

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		
		UserDao userDao = new UserDao();

		try {
			if (this.objetoPagadorRecebedor.getUsuario() != null) {
				userDao.delete(this.objetoPagadorRecebedor.getUsuario());
			}
			
			pagadorRecebedorDao.delete(objetoPagadorRecebedor);

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"PagadorRecebedor: Registro excluído com sucesso! (PagadorRecebedor: "
							+ objetoPagadorRecebedor.getNome() + ")", ""));

		} catch (DAOException e) {

			context.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"PagadorRecebedor: Exclusão não permitida!! Este registro está relacionado com algum atendimento.",
							""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PagadorRecebedor: " + e, ""));

			return "";
		}

		return "PagadorRecebedorConsultar.xhtml";
	}
	
	public void geraSenhaInvestidor() {
		
		if (this.objetoPagadorRecebedor.isUserInvestidor()) {				
			this.objetoPagadorRecebedor.setLoginInvestidor(this.objetoPagadorRecebedor.getEmail());
			
			// gera senha
			String[] carctSenha ={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		    String senha="";
	
		    for (int x=0; x<10; x++){
		        int j = (int) (Math.random()*carctSenha.length);
		        senha += carctSenha[j];
		    }
			
		    this.objetoPagadorRecebedor.setSenhaInvestidor(senha);
		}
	}
	
	public boolean filterByCPF(Object value, Object filter, Locale locale) {
		//System.out.println("entrou em filter");
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
        String filterText = (filter == null) ? null : filter.toString().trim();
        String valueStr = "";
        if (CommonsUtil.semValor(value)) {
            return false;
        } else {
        	 valueStr = value.toString();
        }
       
        if(valueStr.contains(".")) {
        	valueStr = valueStr.replace(".", "");
	    }
        if(valueStr.contains("-")) {
        	valueStr = valueStr.replace("-", "");
	    }
        
        if (filterText == null || filterText.equals("")) {
        	//filteredPagadorRecebedor = pagadorRecebedorDao.findAll();
            return true;
        }
 
        if (valueStr == null) {
            return false;
        }
        
        System.out.println("value = " + valueStr);
        System.out.println(valueStr.contains(filterText));
        //System.out.println("filterText = " + filterText);
        
        //System.out.println("saiu em filter");
        return valueStr.contains(filterText);
    }
	
	public String clearConsultar() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		listaPagadorRecebedor = pagadorRecebedorDao.findAll();
		filteredPagadorRecebedor = listaPagadorRecebedor;
		return "/Cadastros/Cobranca/PagadorRecebedorConsultar.xhtml";
	}
	
	/**
	 * SERVICO PARA PEGAR O ENDEREÇO AUTOMATICAMENTE
	 * 
	 * vianet.com.br
	 */
	public void getEnderecoByViaNet() {
		
		try {
			CepService cepService = new CepService();
			CepResult consultaCep = cepService.consultaCep(this.objetoPagadorRecebedor.getCep());

			if (CommonsUtil.semValor(consultaCep) || !CommonsUtil.semValor(consultaCep.getErro())) {	
				this.objetoPagadorRecebedor.setEndereco("");
				this.objetoPagadorRecebedor.setBairro("");
				this.objetoPagadorRecebedor.setCidade("");
				this.objetoPagadorRecebedor.setEstado("");
			} else {

				if (!CommonsUtil.semValor(consultaCep.getEndereco())) {
					this.objetoPagadorRecebedor.setEndereco(consultaCep.getEndereco());
				}
				if (!CommonsUtil.semValor(consultaCep.getBairro())) {
					this.objetoPagadorRecebedor.setBairro(consultaCep.getBairro());
				}
				if (!CommonsUtil.semValor(consultaCep.getCidade())) {
					this.objetoPagadorRecebedor.setCidade(consultaCep.getCidade());
				}
				if (!CommonsUtil.semValor(consultaCep.getEstado())) {
					this.objetoPagadorRecebedor.setEstado(consultaCep.getEstado());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getEnderecoByViaNetConjuge() {

		try {
			CepService cepService = new CepService();
			CepResult consultaCep = cepService.consultaCep(this.objetoPagadorRecebedor.getCepConjuge());

			if (CommonsUtil.semValor(consultaCep) || !CommonsUtil.semValor(consultaCep.getErro())) {
				this.objetoPagadorRecebedor.setEnderecoConjuge("");
				this.objetoPagadorRecebedor.setBairroConjuge("");
				this.objetoPagadorRecebedor.setCidadeConjuge("");
				this.objetoPagadorRecebedor.setEstadoConjuge("");
			} else {

				if (!CommonsUtil.semValor(consultaCep.getEndereco())) {
					this.objetoPagadorRecebedor.setEnderecoConjuge(consultaCep.getEndereco());
				}
				if (!CommonsUtil.semValor(consultaCep.getBairro())) {
					this.objetoPagadorRecebedor.setBairroConjuge(consultaCep.getBairro());
				}
				if (!CommonsUtil.semValor(consultaCep.getCidade())) {
					this.objetoPagadorRecebedor.setCidadeConjuge(consultaCep.getCidade());
				}
				if (!CommonsUtil.semValor(consultaCep.getEstado())) {
					this.objetoPagadorRecebedor.setEstadoConjuge(consultaCep.getEstado());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}		

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<PagadorRecebedor> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel
	 *            the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<PagadorRecebedor> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoPagadorRecebedor
	 */
	public PagadorRecebedor getObjetoPagadorRecebedor() {
		return objetoPagadorRecebedor;
	}

	/**
	 * @param objetoPagadorRecebedor
	 *            the objetoPagadorRecebedor to set
	 */
	public void setObjetoPagadorRecebedor(PagadorRecebedor objetoPagadorRecebedor) {
		this.objetoPagadorRecebedor = objetoPagadorRecebedor;
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

	/**
	 * @return the tipoPessoaIsFisicaCC
	 */
	public boolean isTipoPessoaIsFisicaCC() {
		return tipoPessoaIsFisicaCC;
	}

	/**
	 * @param tipoPessoaIsFisicaCC the tipoPessoaIsFisicaCC to set
	 */
	public void setTipoPessoaIsFisicaCC(boolean tipoPessoaIsFisicaCC) {
		this.tipoPessoaIsFisicaCC = tipoPessoaIsFisicaCC;
	}

	public List<PagadorRecebedor> getListaPagadorRecebedor() {
		return listaPagadorRecebedor;
	}

	public void setListaPagadorRecebedor(List<PagadorRecebedor> listaPagadorRecebedor) {
		this.listaPagadorRecebedor = listaPagadorRecebedor;
	}

	public List<PagadorRecebedor> getFilteredPagadorRecebedor() {
		return filteredPagadorRecebedor;
	}

	public void setFilteredPagadorRecebedor(List<PagadorRecebedor> filteredPagadorRecebedor) {
		this.filteredPagadorRecebedor = filteredPagadorRecebedor;
	}
	
	

}
