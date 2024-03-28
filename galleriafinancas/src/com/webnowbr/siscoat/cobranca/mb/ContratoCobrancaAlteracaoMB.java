package com.webnowbr.siscoat.cobranca.mb;

import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.PrimeFaces;

import com.webnowbr.siscoat.cobranca.db.model.ComparativoCamposEsteira;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracaoDetalhe;
import com.webnowbr.siscoat.cobranca.db.op.ComparativoCamposEsteiraDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaLogsAlteracaoDao;
import com.webnowbr.siscoat.cobranca.service.ContratoCobrancaService;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "contratoCobrancaAlteracaoMB")
@SessionScoped
public class ContratoCobrancaAlteracaoMB {
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	ContratoCobrancaLogsAlteracao contratoCobrancaLogsAlteracao;
	private List<ComparativoCamposEsteira> comparativoCamposEsteira;
	
	public String exibePopPupSeNaoConfirmar() {
		ContratoCobrancaService contratoCobrancaService = new ContratoCobrancaService();
		PrimeFaces current = PrimeFaces.current();

		this.contratoCobrancaLogsAlteracao = contratoCobrancaService
				.exibePopPupSeNaoConfirmar(this.loginBean.getUsuarioLogado().getLogin());
		
		if (contratoCobrancaLogsAlteracao != null) {
			ComparativoCamposEsteiraDao comparativosCamposEsteraDao = new ComparativoCamposEsteiraDao();
			
			this.comparativoCamposEsteira = comparativosCamposEsteraDao.findByFilter("validar", true);
			current.executeScript("PF('comparacoesPopPupIdIndexvar').show();");
		}

		return null;
	}

	public ContratoCobrancaLogsAlteracao getContratoCobrancaLogsAlteracao() {
		return contratoCobrancaLogsAlteracao;
	}

	public void setContratoCobrancaLogsAlteracao(ContratoCobrancaLogsAlteracao contratoCobrancaLogsAlteracao) {
		this.contratoCobrancaLogsAlteracao = contratoCobrancaLogsAlteracao;
	}
	
	public void finalizaCheckListStatus() {
		
		if (!contratoCobrancaLogsAlteracao.getDetalhes().isEmpty()) {
			ContratoCobrancaLogsAlteracaoDao contratoCobrancaLogsAlteracaoDao = new ContratoCobrancaLogsAlteracaoDao();
			contratoCobrancaLogsAlteracao.setLogJustificado(true);	
			
			contratoCobrancaLogsAlteracaoDao.merge(contratoCobrancaLogsAlteracao);
			PrimeFaces.current().executeScript("PF('comparacoesPopPupIdIndexvar').hide();");
		}
	}
	
	public String retornaDescricaoCampo(String nomePropiedade) {
		Optional<String> descricao =  comparativoCamposEsteira.stream()
				.filter(f -> f.getNome_propiedade().equalsIgnoreCase(nomePropiedade.toLowerCase()))
				.map(x -> x.getDescricao()).findAny();
		if(descricao.isPresent()) {
			return descricao.get();
		} 
		return nomePropiedade;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
}
