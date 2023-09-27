package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;

/** ManagedBean. */
@ManagedBean(name = "pagadorRecebedorCadastroMB")
@SessionScoped
public class PagadorRecebedorCadastroMB {
	

	private String cpf;
	private String cnpj;
	private String tipoPessoa;
	private PagadorRecebedor pagadorRecebedor;

	public void cadastrarSimplificadoPagadorRecebedor() {
		
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		pagadorRecebedor = new PagadorRecebedor();

//		String stringResponse = null;
		if (CommonsUtil.mesmoValor("PF", tipoPessoa) && !CommonsUtil.semValor(cpf)) {

			pagadorRecebedor.setCpf(cpf);
			
			pagadorRecebedor = pagadorRecebedorService.preecheDadosReceita(pagadorRecebedor);

//			ReceitaFederalPF receitaFederalPF = netrinService.requestCadastroPF(pagadorRecebedor);
//
//			stringResponse = GsonUtil.toJson(receitaFederalPF);

		} else if (CommonsUtil.mesmoValor("PJ", tipoPessoa) && !CommonsUtil.semValor(cnpj)) {

			pagadorRecebedor.setCnpj(cnpj);

			pagadorRecebedor = pagadorRecebedorService.preecheDadosReceita(pagadorRecebedor);

//			ReceitaFederalPJ receitaFederalPJ = netrinService.requestCadastroPJ(pagadorRecebedor);
//
//			stringResponse = GsonUtil.toJson(receitaFederalPJ);


		}
			
		
//		if (!CommonsUtil.semValor(cpf) || !CommonsUtil.semValor(cnpj))
//			pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(pagadorRecebedor);
//
//		if (!CommonsUtil.semValor(stringResponse) && !CommonsUtil.semValor(pagadorRecebedor.getId())  ) {
//			pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
//					DocumentosAnaliseEnum.RECEITA_FEDERAL, stringResponse);
//		}
	}

	public void clearFieldsNovoCadastro() {
		this.cpf = null;
		this.cnpj = null;
		this.tipoPessoa = null;
	}

	public boolean validaCPF(FacesContext facesContext, UIComponent uiComponent, Object object) {
		return ValidaCPF.isCPF(object.toString());
	}

	public boolean validaCNPJ(FacesContext facesContext, UIComponent uiComponent, Object object) {
		return ValidaCNPJ.isCNPJ(object.toString());
	}

	public boolean tipoPessoaIsFisica() {
		return CommonsUtil.mesmoValor("PF", tipoPessoa);
	}

	public boolean tipoPessoaIsJuridica() {
		return CommonsUtil.mesmoValor("PJ", tipoPessoa);
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public PagadorRecebedor getPagadorRecebedor() {
		return pagadorRecebedor;
	}

	public void setPagadorRecebedor(PagadorRecebedor pagadorRecebedor) {
		this.pagadorRecebedor = pagadorRecebedor;
	}


	
}