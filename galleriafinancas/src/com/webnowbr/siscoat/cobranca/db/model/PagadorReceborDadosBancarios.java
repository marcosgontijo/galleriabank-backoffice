package com.webnowbr.siscoat.cobranca.db.model;

public class PagadorReceborDadosBancarios {

	private String banco;
	private String agencia;
	private String conta;
	private String contaDigito;
	private String tipoConta;

	private String pix;
	private String tipoPix;

	public PagadorReceborDadosBancarios() {
		super();
	}

	public PagadorReceborDadosBancarios(PagadorRecebedor pagadorRecebedor) {
		super();
		this.banco = pagadorRecebedor.getBanco();
		this.agencia = pagadorRecebedor.getAgencia();
		this.conta = pagadorRecebedor.getConta();
		this.contaDigito = pagadorRecebedor.getContaDigito();
		this.tipoConta = pagadorRecebedor.getTipoConta();
		
		this.pix = pagadorRecebedor.getPix();
		this.tipoPix = pagadorRecebedor.getTipoPix();
	}
	
	public PagadorReceborDadosBancarios(Responsavel responsavel) {
		super();
		this.banco = responsavel.getBanco();
		this.agencia = responsavel.getAgencia();
		this.conta = responsavel.getConta();
		this.contaDigito = responsavel.getContaDigito();
		this.tipoConta = responsavel.getTipoConta();
		this.pix = responsavel.getPix();
		this.tipoPix = responsavel.getTipoPix();
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
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

	public String getContaDigito() {
		return contaDigito;
	}

	public void setContaDigito(String contaDigito) {
		this.contaDigito = contaDigito;
	}

	public String getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}

	public String getTipoPix() {
		return tipoPix;
	}

	public void setTipoPix(String tipoPix) {
		this.tipoPix = tipoPix;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}


}
