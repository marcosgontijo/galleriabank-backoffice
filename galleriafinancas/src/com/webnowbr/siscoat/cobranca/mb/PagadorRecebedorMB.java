package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.json.JSONObject;
import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.GroupDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.GroupAdm;
import com.webnowbr.siscoat.infra.db.model.User;

import org.primefaces.model.SortOrder;

import java.util.Map;

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
		this.objetoPagadorRecebedor.setCpfCC(this.objetoPagadorRecebedor.getCpf());		
	}
	
	public void selectedCNPJ() {
		this.objetoPagadorRecebedor.setCnpjCC(this.objetoPagadorRecebedor.getCnpj());		
	}

	public String inserir() {
		FacesContext context = FacesContext.getCurrentInstance();
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		String msgRetorno = null;
		try {			
			if (!this.objetoPagadorRecebedor.getSite().contains("http") && !this.objetoPagadorRecebedor.getSite().contains("HTTP")) {
				this.objetoPagadorRecebedor.setSite("http://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
			}
			
			if (this.objetoPagadorRecebedor.getWhatsAppNumero() == null || this.objetoPagadorRecebedor.getWhatsAppNumero().equals("")) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
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
			
			objetoPagadorRecebedor = new PagadorRecebedor();

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
	
	public String atualizarIUGU() {
	
		// atualiza IUGU
		IuguMB iuguMB = new IuguMB();
		iuguMB.alteraDomicilioBancarioSubConta(this.objetoPagadorRecebedor);
		
		// atualiza cadastro
		inserir();
		
		return "PagadorRecebedorConsultar.xhtml";
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
			String inputCep = this.objetoPagadorRecebedor.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();
			
			int HTTP_COD_SUCESSO = 200;
			
			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			
			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				this.objetoPagadorRecebedor.setEndereco("");
				this.objetoPagadorRecebedor.setBairro("");
				this.objetoPagadorRecebedor.setCidade("");
				this.objetoPagadorRecebedor.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				if(myResponse.has("logradouro")) {
					this.objetoPagadorRecebedor.setEndereco(myResponse.get("logradouro").toString());
				}
				if(myResponse.has("bairro")) {
					this.objetoPagadorRecebedor.setBairro(myResponse.get("bairro").toString());
				}		
				if(myResponse.has("localidade")) {
					this.objetoPagadorRecebedor.setCidade(myResponse.get("localidade").toString());
				}
				if(myResponse.has("uf")) {
					this.objetoPagadorRecebedor.setEstado(myResponse.get("uf").toString());
				}
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getEnderecoByViaNetConjuge() {
		try {			
			String inputCep = this.objetoPagadorRecebedor.getCepConjuge().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();
			
			int HTTP_COD_SUCESSO = 200;
			
			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			
			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				this.objetoPagadorRecebedor.setEnderecoConjuge("");
				this.objetoPagadorRecebedor.setBairroConjuge("");
				this.objetoPagadorRecebedor.setCidadeConjuge("");
				this.objetoPagadorRecebedor.setEstadoConjuge("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				if(myResponse.has("logradouro")) {
					this.objetoPagadorRecebedor.setEnderecoConjuge(myResponse.get("logradouro").toString());
				}
				if(myResponse.has("bairro")) {
					this.objetoPagadorRecebedor.setBairroConjuge(myResponse.get("bairro").toString());
				}			
				if(myResponse.has("localidade")) {
					this.objetoPagadorRecebedor.setCidadeConjuge(myResponse.get("localidade").toString());
				}
				if(myResponse.has("uf")) {
					this.objetoPagadorRecebedor.setEstadoConjuge(myResponse.get("uf").toString());
				}	
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
