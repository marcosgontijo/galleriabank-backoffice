package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FilaInvestidores implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private Date dataInsercao;
	private Date dataDisponibilidade;
	private BigDecimal valorDisponivel;
	private String observacao;
	private PagadorRecebedor investidor;
	
	public FilaInvestidores(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataInsercao() {
		return dataInsercao;
	}

	public void setDataInsercao(Date dataInsercao) {
		this.dataInsercao = dataInsercao;
	}

	public Date getDataDisponibilidade() {
		return dataDisponibilidade;
	}

	public void setDataDisponibilidade(Date dataDisponibilidade) {
		this.dataDisponibilidade = dataDisponibilidade;
	}

	public BigDecimal getValorDisponivel() {
		return valorDisponivel;
	}

	public void setValorDisponivel(BigDecimal valorDisponivel) {
		this.valorDisponivel = valorDisponivel;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public PagadorRecebedor getInvestidor() {
		return investidor;
	}

	public void setInvestidor(PagadorRecebedor investidor) {
		this.investidor = investidor;
	}
}