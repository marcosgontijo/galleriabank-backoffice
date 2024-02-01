package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

public class RelacionamentoPagadorRecebedor implements Serializable {

	private static final long serialVersionUID = 0;

	private long id;
	private PagadorRecebedor pessoaRoot;
	private String relacao;
	private PagadorRecebedor pessoaChild;
	private BigDecimal porcentagem;
	private String origem;
	private Date dataCadastro; 

	public RelacionamentoPagadorRecebedor(PagadorRecebedor pessoaRoot, String relacao, PagadorRecebedor pessoaChild,
			BigDecimal porcentagem, String origem) {
		super();
		this.pessoaRoot = pessoaRoot;
		this.relacao = relacao;
		this.pessoaChild = pessoaChild;
		this.porcentagem = porcentagem;
		this.origem = origem;
		this.dataCadastro = DateUtil.getDataHoraAgora();
	}
	
	public RelacionamentoPagadorRecebedor() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PagadorRecebedor getPessoaRoot() {
		return pessoaRoot;
	}

	public void setPessoaRoot(PagadorRecebedor pessoaRoot) {
		this.pessoaRoot = pessoaRoot;
	}

	public String getRelacao() {
		return relacao;
	}

	public void setRelacao(String relacao) {
		this.relacao = relacao;
	}

	public PagadorRecebedor getPessoaChild() {
		return pessoaChild;
	}

	public void setPessoaChild(PagadorRecebedor pessoaChild) {
		this.pessoaChild = pessoaChild;
	}

	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}

}
