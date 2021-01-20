package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "crmMB")
@SessionScoped

public class CRMMB {
	private List<ContratoCobranca> novoLead;
	private int qtdeLeads;
	private BigDecimal valorTotalLeads;
	
	private List<ContratoCobranca> emAnalise;
	private int qtdeEmAnalise;
	private BigDecimal valorTotalEmAnalise;
	
	private List<ContratoCobranca> agPagtoBoleto;
	private int qtdeAgPagtoBoleto;
	private BigDecimal valorTotalAgPagtoBoleto;
	
	private List<ContratoCobranca> agPAJUeLaudo;
	private int qtdeAgPAJUeLaudo;
	private BigDecimal valorTotalAgPAJUeLaudo;
	
	private List<ContratoCobranca> agDOC;
	private int qtdeAgDOC;
	private BigDecimal valorTotalAgDOC;
	
	private List<ContratoCobranca> agCCB;
	private int qtdeAgCCB;
	private BigDecimal valorTotalAgCCB;
	
	private List<ContratoCobranca> agAssinatura;
	private int qtdeAgAssinatura;
	private BigDecimal valorTotalAgAssinatura;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	public CRMMB() {
		
	}
	
	public String clearFields() {
		geraConsultaContratosNovoLead();
		geraConsultaContratosEmAnalise();
		geraConsultaContratosAgPagtoBoleto();
		geraConsultaContratosAgPAJUeLaudo();
		geraConsultaContratosAgDOC();
		geraConsultaContratosAgCCB();
		geraConsultaContratosAgAssinatura();
		
		return "/Atendimento/Cobranca/CRM.xhtml";
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
	
	public void geraConsultaContratosAgPagtoBoleto() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.agPagtoBoleto = new ArrayList<ContratoCobranca>();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.agPagtoBoleto = contratoCobrancaDao.geraConsultaContratosCRM(null, null, "Ag. Pagto. Boleto");
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.agPagtoBoleto = contratoCobrancaDao.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), "Ag. Pagto. Boleto"); 	 
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
}
