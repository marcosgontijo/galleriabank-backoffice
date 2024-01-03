package com.webnowbr.siscoat.cobranca.db.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.annotations.Filter;
import org.json.JSONObject;
import org.primefaces.PrimeFaces;

import com.webnowbr.siscoat.cobranca.model.cep.CepResult;
import com.webnowbr.siscoat.cobranca.service.CepService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.ComissaoOrigemEnum;
import com.webnowbr.siscoat.common.CommonsUtil;

import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaContaBancariaRequest;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaContaBancariaResponse;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaPixRequest;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaPixResponse;

public class Responsavel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nome;
	private String endereco;
	private String bairro;
	private String complemento;
	private String cidade;
	private String estado;
	private String telResidencial;
	private String telCelular;
	private String email;
	private Date dtNascimento;
	private String observacao;
	private String codigo;
	private String contato;

	private Date dataCadastro;
	private Responsavel responsavelCaptador;
	private Responsavel responsavelAssistenteComercial;
	private boolean desativado;
	private Date dataDesativado;

	private String rg;
	private String cpf;
	private String cnpj;

	private String cpfCC;
	private String cnpjCC;
	private String cpfCnpjCC;
	private String nomeCC;
	private String banco;
	private List<SelectItem> listaBancos;
	private String agencia;
	private String conta;
	private String contaDigito;
	private String tipoConta;
	private boolean contaBancariaValidada;

	private String pix;
	private String tipoPix;
	private String bancoPix;
	private String agenciaPix;
	private String contaPix;
	private String contaDigitoPix;
	private boolean pixValidado;

	private String cep;
	private Responsavel donoResponsavel;
	
	Set<ComissaoResponsavel> taxasComissao;
	

	private BigDecimal taxaRemuneracao;

	private String whatsAppNumero;

	private boolean superlogica;
	private String cidadeFilial;

	private PagadorReceborDadosBancarios dadosBancariosOriginal;

	public Responsavel() {
	}

	public Responsavel(long id, String nome, String endereco, String bairro, String complemento, String cidade,
			String estado, String telResidencial, String telCelular, String email, Date dtNascimento, String observacao,
			String rg, String cpf, String cep) {
		this.id = id;
		this.nome = nome;
		this.endereco = endereco;
		this.bairro = bairro;
		this.complemento = complemento;
		this.cidade = cidade;
		this.estado = estado;
		this.telResidencial = telResidencial;
		this.telCelular = telCelular;
		this.email = email;
		this.dtNascimento = dtNascimento;
		this.observacao = observacao;
		this.rg = rg;
		this.cpf = cpf;
		this.cep = cep;
	}

	public List<String> completeBancos(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> bancos = new ArrayList<>();
		for (BancosEnum banco : BancosEnum.values()) {
			String bancoStr = banco.getNomeCompleto().toString();
			bancos.add(bancoStr);
		}
		return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}

	public void getEnderecoByViaNet() {
		try {
			CepService cepService = new CepService();
			CepResult consultaCep = cepService.consultaCep(this.cep);

			if (CommonsUtil.semValor(consultaCep) || !CommonsUtil.semValor(consultaCep.getErro())) {

			} else {

				if (!CommonsUtil.semValor(consultaCep.getEndereco())) {
					this.setEndereco(consultaCep.getEndereco());
				}
				if (!CommonsUtil.semValor(consultaCep.getBairro())) {
					this.setBairro(consultaCep.getBairro());
				}
				if (!CommonsUtil.semValor(consultaCep.getCidade())) {
					this.setCidade(consultaCep.getCidade());
				}
				if (!CommonsUtil.semValor(consultaCep.getEstado())) {
					this.setEstado(consultaCep.getEstado());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	public void salvarDadosBancarios() {
		dadosBancariosOriginal = new PagadorReceborDadosBancarios(this);
	}

	public String verificaAlteracaoContaCobranca() {

		if (!CommonsUtil.mesmoValor(this.getBanco(), dadosBancariosOriginal.getBanco())
				|| !CommonsUtil.mesmoValor(this.getAgencia(), dadosBancariosOriginal.getAgencia())
				|| !CommonsUtil.mesmoValor(this.getConta(), dadosBancariosOriginal.getConta())
				|| !CommonsUtil.mesmoValor(this.getContaDigito(), dadosBancariosOriginal.getContaDigito())
				|| !CommonsUtil.mesmoValor(this.getTipoConta(), dadosBancariosOriginal.getTipoConta())) {
			this.setContaBancariaValidada(false);
		}
		return null;

	}

	public String verificaAlteracaoPix() {
		if (!CommonsUtil.mesmoValor(this.getPix(), dadosBancariosOriginal.getPix())
				|| !CommonsUtil.mesmoValor(this.getTipoPix(), dadosBancariosOriginal.getTipoPix())) {
			this.setPixValidado(false);
			this.setBancoPix(null);
			this.setAgenciaPix(null);
			this.setContaPix(null);
			this.setContaDigitoPix(null);
		}
		return null;
	}

	public String validaPix() {

		FacesContext context = FacesContext.getCurrentInstance();
		NetrinService netrinService = new NetrinService();

		String documento = CommonsUtil.somenteNumeros(this.getCpfCnpjCC());
		if (!CommonsUtil.semValor(this.getCpfCnpjCC())) {
			documento = CommonsUtil.somenteNumeros(this.getCpf());
			if (!CommonsUtil.semValor(this.getCnpj())) {
				documento = CommonsUtil.somenteNumeros(this.getCnpj());
			}
		}

		ValidaPixRequest validaPixRequest = new ValidaPixRequest(this.getPix(), this.getTipoPix(), documento);
		ValidaPixResponse result = netrinService.requestValidaPix(validaPixRequest, context);

		if (!CommonsUtil.semValor(result) && !CommonsUtil.semValor(result.getValidaPix())
				&& CommonsUtil.mesmoValorIgnoreCase("Sim", result.getValidaPix().getValidacaoConta())) {
			this.setPixValidado(true);
			this.setBancoPix(result.getValidaPix().getConta().getCodigoBanco());
			this.setAgenciaPix(result.getValidaPix().getConta().getAgencia());
			this.setContaPix(result.getValidaPix().getConta().getConta());
			this.setContaDigitoPix(result.getValidaPix().getConta().getContaDigito());
			PrimeFaces.current().ajax().update("form:comissaoCliente");
		}

		return null;
	}

	public String validaContaBancaria() {

		FacesContext context = FacesContext.getCurrentInstance();
		NetrinService netrinService = new NetrinService();

		String documento = CommonsUtil.somenteNumeros(this.getCpfCnpjCC());
		if (CommonsUtil.semValor(documento)) {
			if (!CommonsUtil.semValor(this.getCpfCC())) {
				documento = CommonsUtil.somenteNumeros(this.getCpfCC());
			}
			if (!CommonsUtil.semValor(this.getCnpjCC())) {
				documento = CommonsUtil.somenteNumeros(this.getCnpjCC());
			}
		}

		ValidaContaBancariaRequest validaContaBancariaRequest = new ValidaContaBancariaRequest(documento,
				this.getCodigoBanco(), this.getAgencia(), this.getConta(), this.getContaDigito(), this.getTipoConta());
		ValidaContaBancariaResponse result = netrinService.requestValidaContaBancaria(validaContaBancariaRequest,
				context);

		if (!CommonsUtil.semValor(result)
				&& CommonsUtil.mesmoValorIgnoreCase("Sim", result.getValidaContaBancaria().getValidacaoConta())) {
			this.setContaBancariaValidada(true);
			PrimeFaces.current().ajax().update("form:comissaoCliente");
		}

		return null;
	}

	public String getCodigoBanco() {
		String[] banco = getBanco().split(Pattern.quote("|"));
		if (banco.length > 0) {
			return CommonsUtil.trimNull(banco[0]);
		} else
			return null;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the endereco
	 */
	public String getEndereco() {
		return endereco;
	}

	/**
	 * @param endereco the endereco to set
	 */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	/**
	 * @return the bairro
	 */
	public String getBairro() {
		return bairro;
	}

	/**
	 * @param bairro the bairro to set
	 */
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	/**
	 * @return the complemento
	 */
	public String getComplemento() {
		return complemento;
	}

	/**
	 * @param complemento the complemento to set
	 */
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	/**
	 * @return the cidade
	 */
	public String getCidade() {
		return cidade;
	}

	/**
	 * @param cidade the cidade to set
	 */
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	/**
	 * @return the estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * @return the telResidencial
	 */
	public String getTelResidencial() {
		return telResidencial;
	}

	/**
	 * @param telResidencial the telResidencial to set
	 */
	public void setTelResidencial(String telResidencial) {
		this.telResidencial = telResidencial;
	}

	/**
	 * @return the telCelular
	 */
	public String getTelCelular() {
		return telCelular;
	}

	/**
	 * @param telCelular the telCelular to set
	 */
	public void setTelCelular(String telCelular) {
		this.telCelular = telCelular;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the dtNascimento
	 */
	public Date getDtNascimento() {
		return dtNascimento;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @param dtNascimento the dtNascimento to set
	 */
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Responsavel))
			return false;
		Responsavel other = (Responsavel) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	/**
	 * @return the rg
	 */
	public String getRg() {
		return rg;
	}

	/**
	 * @param rg the rg to set
	 */
	public void setRg(String rg) {
		this.rg = rg;
	}

	/**
	 * @return the cpf
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * @param cpf the cpf to set
	 */
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/**
	 * @return the cep
	 */
	public String getCep() {
		return cep;
	}

	/**
	 * @param cep the cep to set
	 */
	public void setCep(String cep) {
		this.cep = cep;
	}

	/**
	 * @return the cnpj
	 */
	public String getCnpj() {
		return cnpj;
	}

	/**
	 * @param cnpj the cnpj to set
	 */
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public Responsavel getDonoResponsavel() {
		return donoResponsavel;
	}

	public void setDonoResponsavel(Responsavel donoResponsavel) {
		this.donoResponsavel = donoResponsavel;
	}

	public BigDecimal getTaxaRemuneracao() {
		return taxaRemuneracao;
	}

	public void setTaxaRemuneracao(BigDecimal taxaRemuneracao) {
		this.taxaRemuneracao = taxaRemuneracao;
	}

	public String getWhatsAppNumero() {
		return whatsAppNumero;
	}

	public void setWhatsAppNumero(String whatsAppNumero) {
		this.whatsAppNumero = whatsAppNumero;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Responsavel getResponsavelCaptador() {
		return responsavelCaptador;
	}

	public void setResponsavelCaptador(Responsavel responsavelCaptador) {
		this.responsavelCaptador = responsavelCaptador;
	}

	public Responsavel getResponsavelAssistenteComercial() {
		return responsavelAssistenteComercial;
	}

	public void setResponsavelAssistenteComercial(Responsavel responsavelAssistenteComercial) {
		this.responsavelAssistenteComercial = responsavelAssistenteComercial;
	}

	public String getCpfCC() {
		return cpfCC;
	}

	public void setCpfCC(String cpfCC) {
		this.cpfCC = cpfCC;
	}

	public String getCnpjCC() {
		return cnpjCC;
	}

	public void setCnpjCC(String cnpjCC) {
		this.cnpjCC = cnpjCC;
	}

	public String getNomeCC() {
		return nomeCC;
	}

	public void setNomeCC(String nomeCC) {
		this.nomeCC = nomeCC;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public List<SelectItem> getListaBancos() {
		return listaBancos;
	}

	public void setListaBancos(List<SelectItem> listaBancos) {
		this.listaBancos = listaBancos;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	public Set<ComissaoResponsavel> getTaxasComissao() {
		return taxasComissao;
	}
	
	public List<ComissaoResponsavel> getTaxasComissao(ComissaoOrigemEnum origem) {
		if (!CommonsUtil.semValor(taxasComissao))
			return taxasComissao.stream().filter(c -> c.isAtiva() && origem.getNome().equals(c.getOrigem()))
					.sorted((o1, o2) -> (o1.getValorMinimo().compareTo(o2.getValorMinimo())))
					.collect(Collectors.toList());
		else
			return null;
	}

	public void setTaxasComissao(Set<ComissaoResponsavel> taxasComissao) {
		this.taxasComissao = taxasComissao;
	}

	public String getCpfCnpjCC() {
		return cpfCnpjCC;
	}

	public void setCpfCnpjCC(String cpfCnpjCC) {
		this.cpfCnpjCC = cpfCnpjCC;
	}

	public boolean isDesativado() {
		return desativado;
	}

	public void setDesativado(boolean desativado) {
		this.desativado = desativado;
	}

	public Date getDataDesativado() {
		return dataDesativado;
	}

	public void setDataDesativado(Date dataDesativado) {
		this.dataDesativado = dataDesativado;
	}

	public boolean isSuperlogica() {
		return superlogica;
	}

	public void setSuperlogica(boolean superlogica) {
		this.superlogica = superlogica;
	}

	public String getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}

	public String getCidadeFilial() {
		return cidadeFilial;
	}

	public void setCidadeFilial(String cidadeFilial) {
		this.cidadeFilial = cidadeFilial;
	}

	public String getContaDigito() {
		return contaDigito;
	}

	public void setContaDigito(String contaDigito) {
		this.contaDigito = contaDigito;
	}

	public boolean isContaBancariaValidada() {
		return contaBancariaValidada;
	}

	public void setContaBancariaValidada(boolean contaBancariaValidada) {
		this.contaBancariaValidada = contaBancariaValidada;
	}

	public String getTipoPix() {
		return tipoPix;
	}

	public void setTipoPix(String tipoPix) {
		this.tipoPix = tipoPix;
	}

	public String getBancoPix() {
		return bancoPix;
	}

	public void setBancoPix(String bancoPix) {
		this.bancoPix = bancoPix;
	}

	public String getAgenciaPix() {
		return agenciaPix;
	}

	public void setAgenciaPix(String agenciaPix) {
		this.agenciaPix = agenciaPix;
	}

	public String getContaPix() {
		return contaPix;
	}

	public void setContaPix(String contaPix) {
		this.contaPix = contaPix;
	}

	public String getContaDigitoPix() {
		return contaDigitoPix;
	}

	public void setContaDigitoPix(String contaDigitoPix) {
		this.contaDigitoPix = contaDigitoPix;
	}

	public boolean isPixValidado() {
		return pixValidado;
	}

	public void setPixValidado(boolean pixValidado) {
		this.pixValidado = pixValidado;
	}

}