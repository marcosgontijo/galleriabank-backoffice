package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class StarkBankBaixa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
    public BigDecimal valor;
    public BigDecimal valorPagamento;
    public String nomePagador;
    public String idTransacao;
    public Date dataPagamento;
    public String documento;
    public String linhaBoleto;
    public String formaPagamento;
    public String statusPagamento;
    public String comentario;
    public ContasPagar contasPagar;
    
    public String metodoPix;
    
	private String pix;
	private String nomeRecebedor;
	private String banco;
	private String conta;
	private String agencia;
	private String ispb;

	private Responsavel responsavel;
	
	private String descricaoStarkBank;
	
	private String tipoContaBancaria;
	
	private StarkBankBoleto comprovantePagamentoStarkBank;
	private StarkBankPix comprovantePagamentoPixStarkBank;
	
	public StarkBankBaixa(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public String getIdTransacao() {
		return idTransacao;
	}

	public void setIdTransacao(String idTransacao) {
		this.idTransacao = idTransacao;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getLinhaBoleto() {
		return linhaBoleto;
	}

	public void setLinhaBoleto(String linhaBoleto) {
		this.linhaBoleto = linhaBoleto;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public ContasPagar getContasPagar() {
		return contasPagar;
	}

	public void setContasPagar(ContasPagar contasPagar) {
		this.contasPagar = contasPagar;
	}

	public String getStatusPagamento() {
		return statusPagamento;
	}

	public void setStatusPagamento(String statusPagamento) {
		this.statusPagamento = statusPagamento;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	public String getNomeRecebedor() {
		return nomeRecebedor;
	}

	public void setNomeRecebedor(String nomeRecebedor) {
		this.nomeRecebedor = nomeRecebedor;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getIspb() {
		return ispb;
	}

	public void setIspb(String ispb) {
		this.ispb = ispb;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public String getDescricaoStarkBank() {
		return descricaoStarkBank;
	}

	public void setDescricaoStarkBank(String descricaoStarkBank) {
		this.descricaoStarkBank = descricaoStarkBank;
	}

	public StarkBankBoleto getComprovantePagamentoStarkBank() {
		return comprovantePagamentoStarkBank;
	}

	public void setComprovantePagamentoStarkBank(StarkBankBoleto comprovantePagamentoStarkBank) {
		this.comprovantePagamentoStarkBank = comprovantePagamentoStarkBank;
	}

	public StarkBankPix getComprovantePagamentoPixStarkBank() {
		return comprovantePagamentoPixStarkBank;
	}

	public void setComprovantePagamentoPixStarkBank(StarkBankPix comprovantePagamentoPixStarkBank) {
		this.comprovantePagamentoPixStarkBank = comprovantePagamentoPixStarkBank;
	}

	public String getTipoContaBancaria() {
		return tipoContaBancaria;
	}

	public void setTipoContaBancaria(String tipoContaBancaria) {
		this.tipoContaBancaria = tipoContaBancaria;
	}

	public BigDecimal getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public String getMetodoPix() {
		return metodoPix;
	}

	public void setMetodoPix(String metodoPix) {
		this.metodoPix = metodoPix;
	}
}