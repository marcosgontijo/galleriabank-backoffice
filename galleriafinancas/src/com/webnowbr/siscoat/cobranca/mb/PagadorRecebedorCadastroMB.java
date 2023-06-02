package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;

import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPF;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPJ;

/** ManagedBean. */
@ManagedBean(name = "pagadorRecebedorCadastroMB")
@SessionScoped
public class PagadorRecebedorCadastroMB {

	private String cpf;
	private String cnpj;
	private String tipoPessoa;
	private PagadorRecebedor pagadorRecebedor;

	public void cadastrarSimplificadoPagadorRecebedor() {
		NetrinService netrinService = new NetrinService();
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		pagadorRecebedor = new PagadorRecebedor();

		if (CommonsUtil.mesmoValor("PF", tipoPessoa) && !CommonsUtil.semValor(cpf)) {

			pagadorRecebedor.setCpf(cpf);

			ReceitaFederalPF receitaFederalPF = netrinService.requestCadastroPF(cpf);

			pagadorRecebedor.setNome(receitaFederalPF.getCpfBirthdate().getNome());
			pagadorRecebedor.setDataCasamento(
					CommonsUtil.dateValue(receitaFederalPF.getCpfBirthdate().getDataNascimento(), "dd/MM/YYYY"));
			pagadorRecebedor.setSexo(receitaFederalPF.getCpfBirthdate().getGenero());
			pagadorRecebedor.setNomeMae(receitaFederalPF.getCpfBirthdate().getNomeMae());

		} else if (CommonsUtil.mesmoValor("PJ", tipoPessoa) && !CommonsUtil.semValor(cnpj)) {

			pagadorRecebedor.setCnpj(cnpj);

			ReceitaFederalPJ receitaFederalPJ = netrinService.requestCadastroPJ(cnpj);

			pagadorRecebedor.setNome(receitaFederalPJ.getReceitaFederal().getRazaoSocial());
			pagadorRecebedor.setEndereco(receitaFederalPJ.getReceitaFederal().getLogradouro());
			pagadorRecebedor.setNumero(receitaFederalPJ.getReceitaFederal().getNumero());
			pagadorRecebedor.setComplemento(receitaFederalPJ.getReceitaFederal().getComplemento());
			pagadorRecebedor.setBairro(receitaFederalPJ.getReceitaFederal().getBairro());
			pagadorRecebedor.setCidade(receitaFederalPJ.getReceitaFederal().getMunicipio());
			pagadorRecebedor.setCep(receitaFederalPJ.getReceitaFederal().getCep());
			pagadorRecebedor.setEstado(receitaFederalPJ.getReceitaFederal().getUf());
			pagadorRecebedor.setEmail(receitaFederalPJ.getReceitaFederal().getEmail());
//				pagadorRecebedor.sett(receitaFederalPJ.getReceitaFederal().getTelefone());

		}
		if (!CommonsUtil.semValor(cpf) || !CommonsUtil.semValor(cnpj))
			pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(pagadorRecebedor);

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
