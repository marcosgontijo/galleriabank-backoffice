package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.infra.db.model.User;

public class ComissaoResponsavel implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private BigDecimal valorMinimo;
	private BigDecimal valorMaximo;
	private BigDecimal taxaRemuneracao;
	private String origem;
	private boolean ativa;
	private Date dataCriacao;
	private String loginCriacao;
	private User userCriacao;
	private Date dataRemocao;
	private String loginRemocao;
	private User userRemocao;
	private Responsavel responsavel;

	public ComissaoResponsavel() {
	}
	
	public ComissaoResponsavel(ComissaoResponsavel comissao) {
		this.setValorMinimo(comissao.getValorMinimo());
		this.setValorMaximo(comissao.getValorMaximo());
		this.setTaxaRemuneracao(comissao.getTaxaRemuneracao());	
		this.setAtiva(true);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public BigDecimal getValorMaximo() {
		return valorMaximo;
	}

	public void setValorMaximo(BigDecimal valorMaximo) {
		this.valorMaximo = valorMaximo;
	}

	public BigDecimal getTaxaRemuneracao() {
		return taxaRemuneracao;
	}

	public void setTaxaRemuneracao(BigDecimal taxaRemuneracao) {
		this.taxaRemuneracao = taxaRemuneracao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getLoginCriacao() {
		return loginCriacao;
	}

	public void setLoginCriacao(String loginCriacao) {
		this.loginCriacao = loginCriacao;
	}

	public User getUserCriacao() {
		return userCriacao;
	}

	public void setUserCriacao(User userCriacao) {
		this.userCriacao = userCriacao;
	}

	public Date getDataRemocao() {
		return dataRemocao;
	}

	public void setDataRemocao(Date dataRemocao) {
		this.dataRemocao = dataRemocao;
	}

	public String getLoginRemocao() {
		return loginRemocao;
	}

	public void setLoginRemocao(String loginRemocao) {
		this.loginRemocao = loginRemocao;
	}

	public User getUserRemocao() {
		return userRemocao;
	}

	public void setUserRemocao(User userRemocao) {
		this.userRemocao = userRemocao;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}	
}