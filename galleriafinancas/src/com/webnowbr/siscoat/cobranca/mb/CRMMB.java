package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "crmMB")
@SessionScoped

public class CRMMB {
	private List<ContratoCobranca> novoLead;
	private int qtdeLeads;
	private BigDecimal valorTotalLeads;
	
	private List<ContratoCobranca> agAnalise;
	private int qtdeAgAnalise;
	private BigDecimal valorTotalAgAnalise;
	
	private List<ContratoCobranca> emAnalise;
	private int qtdeEmAnalise;
	private BigDecimal valorTotalEmAnalise;
	
	private List<ContratoCobranca> analisePendente;
	private int qtdeAnalisePendente;
	private BigDecimal valorAnalisePendente;
	
	private List<ContratoCobranca> analiseReprovada;
	private int qtdeAnaliseReprovada;
	private BigDecimal valorAnaliseReprovada;
	
	private List<ContratoCobranca> agPagtoBoleto;
	private int qtdeAgPagtoBoleto;
	private BigDecimal valorTotalAgPagtoBoleto;
	
	private List<ContratoCobranca> agPAJUeLaudo;
	private int qtdeAgPAJUeLaudo;
	private BigDecimal valorTotalAgPAJUeLaudo;
	
	private List<ContratoCobranca> analiseComercial;
	private int qtdeAnaliseComercial;
	private BigDecimal valorTotalAnaliseComercial;
	
	private List<ContratoCobranca> comentarioJuridicoEsteira;
	private int qtdeComentarioJuridicoEsteira;
	private BigDecimal valorTotalComentarioJuridicoEsteira;
	
	private List<ContratoCobranca> preComite;
	private int qtdePreComite;
	private BigDecimal valorTotalPreComite;
	
	private List<ContratoCobranca> agComite;
	private int qtdeAgComite;
	private BigDecimal valorTotalAgComite;
	
	private List<ContratoCobranca> agDOC;
	private int qtdeAgDOC;
	private BigDecimal valorTotalAgDOC;
	
	private List<ContratoCobranca> agCCB;
	private int qtdeAgCCB;
	private BigDecimal valorTotalAgCCB;
	
	private List<ContratoCobranca> agAssinatura;
	private int qtdeAgAssinatura;
	private BigDecimal valorTotalAgAssinatura;
	
	private List<ContratoCobranca> agRegistro;
	private int qtdeAgRegistro;
	private BigDecimal valorTotalAgRegistro;
	
	private List<ContratoCobranca> baixado;
	private int qtdeBaixado;
	private BigDecimal valorTotalBaixado;
	
	private List<ContratoCobranca> reprovado;
	private int qtdeReprovado;
	private BigDecimal valorTotalReprovado;
	
	private List<ContratoCobranca> todosContratos;
	private int qtdeTodosContratos;
	private BigDecimal valorTodosContratos;
	
	private String tituloPagina = "Todos";
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	public CRMMB() {
		
	}
	
	public String clearFields() {
		geraConsultaContratosNovoLead();
		geraConsultaContratosAgAnalise();
		geraConsultaContratosEmAnalise();
		geraConsultaContratosAgPagtoBoleto();
		geraConsultaContratosAgPAJUeLaudo();
		geraConsultaContratosPreComite();
		geraConsultaContratosAgComite();
		geraConsultaContratosAgDOC();
		geraConsultaContratosAgCCB();
		geraConsultaContratosAgAssinatura();
		geraConsultaContratosAgRegistro();

		
		return "/Atendimento/Cobranca/CRM.xhtml";
	}
	
	public String clearFieldsDetalhado() {
		geraConsultaContratosTodos();
		
		// popula status
		this.todosContratos = populaStatus(this.todosContratos);
		
		return "/Atendimento/Cobranca/ContratoCobrancaCRMConsultar.xhtml";
	}
	
	public void consultaContratosCRM(String filtro) {
		if (filtro.equals("Todos")) {
			geraConsultaContratosTodos();
		}
		if (filtro.equals("NovoLead")) {
			geraConsultaContratosNovoLead();
			this.todosContratos = this.novoLead;
			this.qtdeTodosContratos = this.qtdeLeads;
			this.valorTodosContratos = this.valorTotalLeads;
			
			this.tituloPagina = "Novo Lead";
		}
		if (filtro.equals("AgAnalise")) {
			geraConsultaContratosAgAnalise();
			this.todosContratos = this.agAnalise;
			this.qtdeTodosContratos = this.qtdeAgAnalise;
			this.valorTodosContratos = this.valorTotalAgAnalise;
			
			this.tituloPagina = "Ag Análise";
		}
		if (filtro.equals("EmAnalise")) {
			geraConsultaContratosEmAnalise();
			this.todosContratos = this.emAnalise;
			this.qtdeTodosContratos = this.qtdeEmAnalise;
			this.valorTodosContratos = this.valorTotalEmAnalise;
			
			this.tituloPagina = "Em Análise";
		}
		
		if (filtro.equals("AnalisePendente")) {
			geraConsultaContratosAnalisePendente();
			this.todosContratos = this.analisePendente;
			this.qtdeTodosContratos = this.qtdeAnalisePendente;
			this.valorTodosContratos = this.valorAnalisePendente;
			
			this.tituloPagina = "Análise Pendente";
		}
		
		if (filtro.equals("AnaliseReprovada")) {
			geraConsultaContratosAnaliseReprovada();
			this.todosContratos = this.analiseReprovada;
			this.qtdeTodosContratos = this.qtdeAnaliseReprovada;
			this.valorTodosContratos = this.valorAnaliseReprovada;
			
			this.tituloPagina = "Análise Reprovada";
		}
		
		if (filtro.equals("AgPagtoBoleto")) {
			geraConsultaContratosAgPagtoBoleto();
			this.todosContratos = this.agPagtoBoleto;
			this.qtdeTodosContratos = this.qtdeAgPagtoBoleto;
			this.valorTodosContratos = this.valorTotalAgPagtoBoleto;
			
			this.tituloPagina = "Análise Aprovada";
		}
		if (filtro.equals("AgPAJULaudo")) {
			geraConsultaContratosAgPAJUeLaudo();
			this.todosContratos = this.agPAJUeLaudo;
			this.qtdeTodosContratos = this.qtdeAgPAJUeLaudo;
			this.valorTodosContratos = this.valorTotalAgPAJUeLaudo;
			
			this.tituloPagina = "Ag. PAJU e Laudo";
		}
		
		if (filtro.equals("analiseComercial")) {
			geraConsultaContratosAnaliseComercial();
			this.todosContratos = this.analiseComercial;
			this.qtdeTodosContratos = this.qtdeAnaliseComercial;
			this.valorTodosContratos = this.valorTotalAnaliseComercial;
			
			this.tituloPagina = "Análise Comercial";
		}
		
		if (filtro.equals("comentarioJuridicoEsteira")) {
			geraConsultaContratosComentarioJuridicoEsteira();
			this.todosContratos = this.comentarioJuridicoEsteira;
			this.qtdeTodosContratos = this.qtdeComentarioJuridicoEsteira;
			this.valorTodosContratos = this.valorTotalComentarioJuridicoEsteira;
			
			this.tituloPagina = "Comentário Jurídico";
		}
		
		if (filtro.equals("PreComite")) {
			geraConsultaContratosPreComite();
			this.todosContratos = this.preComite;
			this.qtdeTodosContratos = this.qtdePreComite;
			this.valorTodosContratos = this.valorTotalPreComite;
			
			this.tituloPagina = "Pré-Comite";
		}
		
		if (filtro.equals("AgComite")) {
			geraConsultaContratosAgComite();
			this.todosContratos = this.agComite;
			this.qtdeTodosContratos = this.qtdeAgComite;
			this.valorTodosContratos = this.valorTotalAgComite;
			
			this.tituloPagina = "Ag. Comite";
		}
		
		if (filtro.equals("AgDOC")) {
			geraConsultaContratosAgDOC();
			this.todosContratos = this.agDOC;
			this.qtdeTodosContratos = this.qtdeAgDOC;
			this.valorTodosContratos = this.valorTotalAgDOC;
			
			this.tituloPagina = "Ag. DOC";
		}
		
		
		if (filtro.equals("AgCCB")) {
			geraConsultaContratosAgCCB();
			this.todosContratos = this.agCCB;
			this.qtdeTodosContratos = this.qtdeAgCCB;
			this.valorTodosContratos = this.valorTotalAgCCB;
			
			this.tituloPagina = "Ag. CCB";
		}
		if (filtro.equals("AgAssinatura")) {
			geraConsultaContratosAgAssinatura();
			this.todosContratos = this.agAssinatura;
			this.qtdeTodosContratos = this.qtdeAgAssinatura;
			this.valorTodosContratos = this.valorTotalAgAssinatura;
			
			this.tituloPagina = "Ag. Assinatura";
		}
		
		if (filtro.equals("AgRegistro")) {
			geraConsultaContratosAgRegistro();
			this.todosContratos = this.agRegistro;
			this.qtdeTodosContratos = this.qtdeAgRegistro;
			this.valorTodosContratos = this.valorTotalAgRegistro;
			
			this.tituloPagina = "Ag. Registro";
		}
		
		else if(filtro.equals("Baixado")){
			geraConsultaContratosBaixado();
			this.todosContratos = this.baixado;
			this.qtdeTodosContratos = this.qtdeBaixado;
			this.valorTodosContratos = this.valorTotalBaixado;
			
			this.tituloPagina = "Baixado";
		}
		
		else if(filtro.equals("Reprovado")){
			geraConsultaContratosReprovado();
			this.todosContratos = this.reprovado;
			this.qtdeTodosContratos = this.qtdeReprovado;
			this.valorTodosContratos = this.valorTotalReprovado;
			
			this.tituloPagina = "Reprovado";
		}
		
		// popula status
		this.todosContratos = populaStatus(this.todosContratos);
	}
	
	public void geraConsultaContratosTodos() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.todosContratos = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.todosContratos = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Todos");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.todosContratos = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Todos"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeTodosContratos = 0;
		this.valorTodosContratos = BigDecimal.ZERO;
		
		if (this.todosContratos.size() > 0) {
			this.qtdeTodosContratos = this.todosContratos.size();
			
			for (ContratoCobranca c : this.todosContratos) {
				this.valorTodosContratos = valorTodosContratos.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosNovoLead() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.novoLead = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.novoLead = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Lead");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.novoLead = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Lead"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeLeads = 0;
		this.valorTotalLeads = BigDecimal.ZERO;
		
		if (this.novoLead.size() > 0) {
			this.qtdeLeads = this.novoLead.size();
			
			for (ContratoCobranca c : this.novoLead) {
				this.valorTotalLeads = valorTotalLeads.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgAnalise() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agAnalise = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agAnalise = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Aguardando Análise");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agAnalise = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Aguardando Análise"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgAnalise = 0;
		this.valorTotalAgAnalise = BigDecimal.ZERO;
		
		if (this.agAnalise.size() > 0) {
			this.qtdeAgAnalise = this.agAnalise.size();
			
			for (ContratoCobranca c : this.agAnalise) {
				this.valorTotalAgAnalise = valorTotalAgAnalise.add(c.getQuantoPrecisa());
			}
		}
	}

	
	public void geraConsultaContratosAnaliseReprovada() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.analiseReprovada = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.analiseReprovada = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Análise Reprovada");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.analiseReprovada = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Análise Reprovada"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAnaliseReprovada = 0;
		this.valorAnaliseReprovada = BigDecimal.ZERO;
		
		if (this.analiseReprovada.size() > 0) {
			this.qtdeAnaliseReprovada = this.analiseReprovada.size();
			
			for (ContratoCobranca c : this.analiseReprovada) {
				this.valorAnaliseReprovada = valorAnaliseReprovada.add(c.getQuantoPrecisa());
			}
		}
	}

	public void geraConsultaContratosEmAnalise() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.emAnalise = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.emAnalise = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Em Analise");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.emAnalise = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Em Analise"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeEmAnalise = 0;
		this.valorTotalEmAnalise = BigDecimal.ZERO;
		
		if (this.emAnalise.size() > 0) {
			this.qtdeEmAnalise = this.emAnalise.size();
			
			for (ContratoCobranca c : this.emAnalise) {
				this.valorTotalEmAnalise = valorTotalEmAnalise.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAnalisePendente() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.analisePendente = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.analisePendente = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Analise Pendente");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.analisePendente = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Analise Pendente"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAnalisePendente = 0;
		this.valorAnalisePendente = BigDecimal.ZERO;
		
		if (this.analisePendente.size() > 0) {
			this.qtdeAnalisePendente = this.analisePendente.size();
			
			for (ContratoCobranca c : this.analisePendente) {
				this.valorAnalisePendente = valorAnalisePendente.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgPagtoBoleto() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agPagtoBoleto = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agPagtoBoleto = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Análise Aprovada");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agPagtoBoleto = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Análise Aprovada"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgPagtoBoleto = 0;
		this.valorTotalAgPagtoBoleto = BigDecimal.ZERO;
		
		if (this.agPagtoBoleto.size() > 0) {
			this.qtdeAgPagtoBoleto = this.agPagtoBoleto.size();
			
			for (ContratoCobranca c : this.agPagtoBoleto) {
				this.valorTotalAgPagtoBoleto = valorTotalAgPagtoBoleto.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgPAJUeLaudo() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agPAJUeLaudo = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agPAJUeLaudo = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. PAJU e Laudo");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agPAJUeLaudo = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. PAJU e Laudo"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgPAJUeLaudo = 0;
		this.valorTotalAgPAJUeLaudo = BigDecimal.ZERO;
		
		if (this.agPAJUeLaudo.size() > 0) {
			this.qtdeAgPAJUeLaudo = this.agPAJUeLaudo.size();
			
			for (ContratoCobranca c : this.agPAJUeLaudo) {
				this.valorTotalAgPAJUeLaudo = valorTotalAgPAJUeLaudo.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAnaliseComercial() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.analiseComercial = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.analiseComercial = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Análise Comercial");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.analiseComercial = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Análise Comercial"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAnaliseComercial = 0;
		this.valorTotalAnaliseComercial = BigDecimal.ZERO;
		
		if (this.analiseComercial.size() > 0) {
			this.qtdeAnaliseComercial = this.analiseComercial.size();
			
			for (ContratoCobranca c : this.analiseComercial) {
				this.valorTotalAnaliseComercial = valorTotalAnaliseComercial.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosComentarioJuridicoEsteira() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.comentarioJuridicoEsteira = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.comentarioJuridicoEsteira = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Comentario Jurídico");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.comentarioJuridicoEsteira = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Comentario Jurídico"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeComentarioJuridicoEsteira = 0;
		this.valorTotalComentarioJuridicoEsteira = BigDecimal.ZERO;
		
		if (this.comentarioJuridicoEsteira.size() > 0) {
			this.qtdeComentarioJuridicoEsteira = this.comentarioJuridicoEsteira.size();
			
			for (ContratoCobranca c : this.comentarioJuridicoEsteira) {
				this.valorTotalComentarioJuridicoEsteira = valorTotalComentarioJuridicoEsteira.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosPreComite() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.preComite = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.preComite = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Pré-Comite");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.preComite = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Pré-Comite"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdePreComite = 0;
		this.valorTotalPreComite = BigDecimal.ZERO;
		
		if (this.preComite.size() > 0) {
			this.qtdePreComite = this.preComite.size();
			
			for (ContratoCobranca c : this.preComite) {
				this.valorTotalPreComite = valorTotalPreComite.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgComite() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agComite = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agComite = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. Comite");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agComite = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. Comite"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgComite = 0;
		this.valorTotalAgComite = BigDecimal.ZERO;
		
		if (this.agComite.size() > 0) {
			this.qtdeAgComite = this.agComite.size();
			
			for (ContratoCobranca c : this.agComite) {
				this.valorTotalAgComite = valorTotalAgComite.add(c.getQuantoPrecisa());
			}
		}
	}

	public void geraConsultaContratosAgDOC() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agDOC = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agDOC = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. DOC");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agDOC = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. DOC"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgDOC = 0;
		this.valorTotalAgDOC = BigDecimal.ZERO;
		
		if (this.agDOC.size() > 0) {
			this.qtdeAgDOC = this.agDOC.size();
			
			for (ContratoCobranca c : this.agDOC) {
				this.valorTotalAgDOC = valorTotalAgDOC.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgCCB() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agCCB = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agCCB = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. CCB");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agCCB = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. CCB"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgCCB = 0;
		this.valorTotalAgCCB = BigDecimal.ZERO;
		
		if (this.agCCB.size() > 0) {
			this.qtdeAgCCB = this.agCCB.size();
			
			for (ContratoCobranca c : this.agCCB) {
				this.valorTotalAgCCB = valorTotalAgCCB.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgAssinatura() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agAssinatura = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agAssinatura = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. Assinatura");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agAssinatura = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. Assinatura"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgAssinatura = 0;
		this.valorTotalAgAssinatura = BigDecimal.ZERO;
		
		if (this.agAssinatura.size() > 0) {
			this.qtdeAgAssinatura = this.agAssinatura.size();
			
			for (ContratoCobranca c : this.agAssinatura) {
				this.valorTotalAgAssinatura = valorTotalAgAssinatura.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosAgRegistro() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agRegistro = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agRegistro = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. Registro");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agRegistro = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. Registro"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeAgRegistro = 0;
		this.valorTotalAgRegistro = BigDecimal.ZERO;
		
		if (this.agRegistro.size() > 0) {
			this.qtdeAgRegistro = this.agRegistro.size();
			
			for (ContratoCobranca c : this.agRegistro) {
				this.valorTotalAgRegistro = valorTotalAgRegistro.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosBaixado() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.baixado = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.baixado = contratoCobrancaDao.geraConsultaContratosCRMBaixadoReprovado(null, null, "Baixado");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.baixado = contratoCobrancaDao.geraConsultaContratosCRMBaixadoReprovado(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Baixado"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeBaixado = 0;
		this.valorTotalBaixado = BigDecimal.ZERO;
		
		if (this.baixado.size() > 0) {
			this.qtdeBaixado = this.baixado.size();
			
			for (ContratoCobranca c : this.baixado) {
				this.valorTotalBaixado = valorTotalBaixado.add(c.getQuantoPrecisa());
			}
		}
	}
	
	public void geraConsultaContratosReprovado() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.reprovado = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.reprovado = contratoCobrancaDao.geraConsultaContratosCRMBaixadoReprovado(null, null, "Reprovado");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.reprovado = contratoCobrancaDao.geraConsultaContratosCRMBaixadoReprovado(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Reprovado"); 	 
					}
				}
			} 
		}
		
		// soma valores total
		this.qtdeReprovado = 0;
		this.valorTotalReprovado = BigDecimal.ZERO;
		
		if (this.reprovado.size() > 0) {
			this.qtdeReprovado = this.reprovado.size();

			for (ContratoCobranca c : this.reprovado) {
				if (!CommonsUtil.semValor(c.getQuantoPrecisa())) {
					this.valorTotalReprovado = valorTotalReprovado.add(c.getQuantoPrecisa());

				}
			}
		}
	}
	
	public List<ContratoCobranca> populaStatus(List<ContratoCobranca> contratos) {
		// POPULA STATUS
		for (ContratoCobranca c : contratos) {
			
			if (CommonsUtil.mesmoValor(c.getStatus(), "Aprovado")) {
				c.setStatus("Aprovado");
			} else if (CommonsUtil.mesmoValor(c.getStatus(), "Reprovado")) {
				c.setStatus("Reprovado");
			} else if (CommonsUtil.mesmoValor(c.getStatus(), "Baixado")) {
				c.setStatus("Baixado");
			} else if (CommonsUtil.mesmoValor(c.getStatus(), "Desistência Cliente")) {
				c.setStatus("Reprovado");
			} else {
				
				if (!CommonsUtil.semValor(c.getStatusLead())){
					if (c.getStatusLead().equals("Novo Lead")) {
						c.setStatus("Novo Lead");
					}

					if (c.getStatusLead().equals("Em Tratamento")) {
						c.setStatus("Lead em Tratamento");
					}
					
					if (c.getStatusLead().equals("Reprovado")) {
						c.setStatus("Lead Reprovado");
					}
					
					if (c.getStatusLead().equals("Arquivado") && !c.isInicioAnalise()) {
						c.setStatus("Lead Arquivado");
					}

					if (c.getStatusLead().equals("Completo") && !c.isInicioAnalise()) {
						c.setStatus("Ag. Análise");
					}

				} else {
					c.setStatus("Não Definido");
				}
				
				if (c.isInicioAnalise()) {
					c.setStatus("Em Análise");
				}

				if (c.getCadastroAprovadoValor() != null) {
					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")) {
						c.setStatus("Em Análise");
					}
					
					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Pendente")) {
						c.setStatus("Análise Pendente");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& !c.isPagtoLaudoConfirmada()) {
						c.setStatus("Análise Pré-Aprovada");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && (!c.isLaudoRecebido() || !c.isPajurFavoravel())) {
						c.setStatus("Ag. PAJU e Laudo");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && !c.isAnaliseComercial() ) {
						c.setStatus("Análise Comercial");
					}
					
					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && !c.isComentarioJuridicoEsteira() ) {
						c.setStatus("Comentário Jurídico");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && !c.isPreAprovadoComite()) {
						c.setStatus("Pré-Comite");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && c.isPreAprovadoComite()
							&& !c.isDocumentosComite()) {
						c.setStatus("Ag. Comite");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && c.isPreAprovadoComite()
							&& c.isDocumentosComite() && !c.isAprovadoComite()) {
						c.setStatus("Ag. Comite");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && c.isPreAprovadoComite()
							&& c.isDocumentosComite() && c.isAprovadoComite() && !c.isDocumentosCompletos()) {
						c.setStatus("Ag. DOC");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && c.isPreAprovadoComite()
							&& c.isDocumentosComite() && c.isAprovadoComite() && c.isDocumentosCompletos()
							&& !c.isCcbPronta()) {
						c.setStatus("Ag. CCB");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && c.isPreAprovadoComite()
							&& c.isDocumentosComite() && c.isAprovadoComite() && c.isDocumentosCompletos()
							&& c.isCcbPronta() && c.isAgAssinatura()) {
						c.setStatus("Ag. Assinatura");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado") && c.isPagtoLaudoConfirmada()
							&& c.isLaudoRecebido() && c.isPajurFavoravel() && c.isAnaliseComercial() && c.isComentarioJuridicoEsteira() && c.isPreAprovadoComite()
							&& c.isDocumentosComite() && c.isAprovadoComite() && c.isDocumentosCompletos()
							&& c.isCcbPronta() && !c.isAgAssinatura() && c.isAgRegistro()) {
						c.setStatus("Ag. Registro");
					}
				}
				if (c.isAnaliseReprovada()) {
					c.setStatus("Análise Reprovada");
				}
			}
		}
		return contratos;
	}


	public List<ContratoCobranca> getNovoLead() {
		return novoLead;
	}

	public void setNovoLead(List<ContratoCobranca> novoLead) {
		this.novoLead = novoLead;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public int getQtdeLeads() {
		return qtdeLeads;
	}

	public void setQtdeLeads(int qtdeLeads) {
		this.qtdeLeads = qtdeLeads;
	}

	public BigDecimal getValorTotalLeads() {
		return valorTotalLeads;
	}

	public void setValorTotalLeads(BigDecimal valorTotalLeads) {
		this.valorTotalLeads = valorTotalLeads;
	}

	public List<ContratoCobranca> getEmAnalise() {
		return emAnalise;
	}

	public void setEmAnalise(List<ContratoCobranca> emAnalise) {
		this.emAnalise = emAnalise;
	}

	public int getQtdeEmAnalise() {
		return qtdeEmAnalise;
	}

	public void setQtdeEmAnalise(int qtdeEmAnalise) {
		this.qtdeEmAnalise = qtdeEmAnalise;
	}

	public BigDecimal getValorTotalEmAnalise() {
		return valorTotalEmAnalise;
	}

	public void setValorTotalEmAnalise(BigDecimal valorTotalEmAnalise) {
		this.valorTotalEmAnalise = valorTotalEmAnalise;
	}

	public List<ContratoCobranca> getAgPagtoBoleto() {
		return agPagtoBoleto;
	}

	public void setAgPagtoBoleto(List<ContratoCobranca> agPagtoBoleto) {
		this.agPagtoBoleto = agPagtoBoleto;
	}

	public int getQtdeAgPagtoBoleto() {
		return qtdeAgPagtoBoleto;
	}

	public void setQtdeAgPagtoBoleto(int qtdeAgPagtoBoleto) {
		this.qtdeAgPagtoBoleto = qtdeAgPagtoBoleto;
	}

	public BigDecimal getValorTotalAgPagtoBoleto() {
		return valorTotalAgPagtoBoleto;
	}

	public void setValorTotalAgPagtoBoleto(BigDecimal valorTotalAgPagtoBoleto) {
		this.valorTotalAgPagtoBoleto = valorTotalAgPagtoBoleto;
	}

	public List<ContratoCobranca> getAgPAJUeLaudo() {
		return agPAJUeLaudo;
	}

	public void setAgPAJUeLaudo(List<ContratoCobranca> agPAJUeLaudo) {
		this.agPAJUeLaudo = agPAJUeLaudo;
	}

	public int getQtdeAgPAJUeLaudo() {
		return qtdeAgPAJUeLaudo;
	}

	public void setQtdeAgPAJUeLaudo(int qtdeAgPAJUeLaudo) {
		this.qtdeAgPAJUeLaudo = qtdeAgPAJUeLaudo;
	}

	public BigDecimal getValorTotalAgPAJUeLaudo() {
		return valorTotalAgPAJUeLaudo;
	}

	public void setValorTotalAgPAJUeLaudo(BigDecimal valorTotalAgPAJUeLaudo) {
		this.valorTotalAgPAJUeLaudo = valorTotalAgPAJUeLaudo;
	}

	public List<ContratoCobranca> getAgDOC() {
		return agDOC;
	}

	public void setAgDOC(List<ContratoCobranca> agDOC) {
		this.agDOC = agDOC;
	}

	public int getQtdeAgDOC() {
		return qtdeAgDOC;
	}

	public void setQtdeAgDOC(int qtdeAgDOC) {
		this.qtdeAgDOC = qtdeAgDOC;
	}

	public BigDecimal getValorTotalAgDOC() {
		return valorTotalAgDOC;
	}

	public void setValorTotalAgDOC(BigDecimal valorTotalAgDOC) {
		this.valorTotalAgDOC = valorTotalAgDOC;
	}

	public List<ContratoCobranca> getAgCCB() {
		return agCCB;
	}

	public void setAgCCB(List<ContratoCobranca> agCCB) {
		this.agCCB = agCCB;
	}

	public int getQtdeAgCCB() {
		return qtdeAgCCB;
	}

	public void setQtdeAgCCB(int qtdeAgCCB) {
		this.qtdeAgCCB = qtdeAgCCB;
	}

	public BigDecimal getValorTotalAgCCB() {
		return valorTotalAgCCB;
	}

	public void setValorTotalAgCCB(BigDecimal valorTotalAgCCB) {
		this.valorTotalAgCCB = valorTotalAgCCB;
	}

	public List<ContratoCobranca> getAgAssinatura() {
		return agAssinatura;
	}

	public void setAgAssinatura(List<ContratoCobranca> agAssinatura) {
		this.agAssinatura = agAssinatura;
	}

	public int getQtdeAgAssinatura() {
		return qtdeAgAssinatura;
	}

	
	public void setQtdeAgAssinatura(int qtdeAgAssinatura) {
		this.qtdeAgAssinatura = qtdeAgAssinatura;
	}

	public BigDecimal getValorTotalAgAssinatura() {
		return valorTotalAgAssinatura;
	}

	public void setValorTotalAgAssinatura(BigDecimal valorTotalAgAssinatura) {
		this.valorTotalAgAssinatura = valorTotalAgAssinatura;
	}
	
	
	
	public List<ContratoCobranca> getAgRegistro() {
		return agRegistro;
	}

	public void setAgRegistro(List<ContratoCobranca> agRegistro) {
		this.agRegistro = agRegistro;
	}

	public int getQtdeAgRegistro() {
		return qtdeAgRegistro;
	}

	public void setQtdeAgRegistro(int qtdeAgRegistro) {
		this.qtdeAgRegistro = qtdeAgRegistro;
	}

	public BigDecimal getValorTotalAgRegistro() {
		return valorTotalAgRegistro;
	}

	public void setValorTotalAgRegistro(BigDecimal valorTotalAgRegistro) {
		this.valorTotalAgRegistro = valorTotalAgRegistro;
	}
	
	public List<ContratoCobranca> getPreComite() {
		return preComite;
	}

	public void setPreComite(List<ContratoCobranca> preComite) {
		this.preComite = preComite;
	}

	public int getQtdePreComite() {
		return qtdePreComite;
	}

	public void setQtdePreComite(int qtdePreComite) {
		this.qtdePreComite = qtdePreComite;
	}

	public BigDecimal getValorTotalPreComite() {
		return valorTotalPreComite;
	}

	public void setValorTotalPreComite(BigDecimal valorTotalPreComite) {
		this.valorTotalPreComite = valorTotalPreComite;
	}

	public List<ContratoCobranca> getAgComite() {
		return agComite;
	}

	public void setAgComite(List<ContratoCobranca> agComite) {
		this.agComite = agComite;
	}

	public int getQtdeAgComite() {
		return qtdeAgComite;
	}

	public void setQtdeAgComite(int qtdeAgComite) {
		this.qtdeAgComite = qtdeAgComite;
	}

	public BigDecimal getValorTotalAgComite() {
		return valorTotalAgComite;
	}

	public void setValorTotalAgComite(BigDecimal valorTotalAgComite) {
		this.valorTotalAgComite = valorTotalAgComite;
	}

	public List<ContratoCobranca> getTodosContratos() {
		return todosContratos;
	}

	public void setTodosContratos(List<ContratoCobranca> todosContratos) {
		this.todosContratos = todosContratos;
	}

	public int getQtdeTodosContratos() {
		return qtdeTodosContratos;
	}

	public void setQtdeTodosContratos(int qtdeTodosContratos) {
		this.qtdeTodosContratos = qtdeTodosContratos;
	}

	public BigDecimal getValorTodosContratos() {
		return valorTodosContratos;
	}

	public void setValorTodosContratos(BigDecimal valorTodosContratos) {
		this.valorTodosContratos = valorTodosContratos;
	}

	public String getTituloPagina() {
		return tituloPagina;
	}

	public void setTituloPagina(String tituloPagina) {
		this.tituloPagina = tituloPagina;
	}

	public List<ContratoCobranca> getAnaliseReprovada() {
		return analiseReprovada;
	}

	public void setAnaliseReprovada(List<ContratoCobranca> analiseReprovada) {
		this.analiseReprovada = analiseReprovada;
	}

	public int getQtdeAnaliseReprovada() {
		return qtdeAnaliseReprovada;
	}

	public void setQtdeAnaliseReprovada(int qtdeAnaliseReprovada) {
		this.qtdeAnaliseReprovada = qtdeAnaliseReprovada;
	}

	public BigDecimal getValorAnaliseReprovada() {
		return valorAnaliseReprovada;
	}

	public void setValorAnaliseReprovada(BigDecimal valorAnaliseReprovada) {
		this.valorAnaliseReprovada = valorAnaliseReprovada;
	}
}
