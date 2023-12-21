package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

public class CcbProcessosJudiciais implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	
	private BigDecimal valor = BigDecimal.ZERO;
	private BigDecimal valorAtualizado = BigDecimal.ZERO;
	private String numero = "";
	private ContasPagar contaPagar;
	private ContratoCobranca contrato;
	private PagadorRecebedor pagador;
	private String quitar = "";
	private String natureza = "";
	private boolean selecionadoComite = false;
	private String observacao = "";
	private String outrosParticipantes = "";
	private String origem = "";
	
	public CcbProcessosJudiciais(BigDecimal valor, String numero) {
		contaPagar = new ContasPagar();
		this.valor = valor;
		this.numero = numero;
	}
	
	public List<String> naturezaProcessos() {
		List<String> naturezas = new ArrayList<String>();
		naturezas.add("Execução fiscal - diversos");
		naturezas.add("Execução fiscal - IPTU");
		naturezas.add("Reclamação Trabalhista");
		naturezas.add("Execução de condomínio - diversos");
		naturezas.add("Execução de condomínio - garantia");
		naturezas.add("Execução de título - contratos bancários");
		naturezas.add("Ação monitória - contratos bancários");
		naturezas.add("Ação de cobrança - contratos bancários");
		naturezas.add("Cumprimento de Sentença");
		return naturezas;
	}
	
	public CcbProcessosJudiciais() {
		contaPagar = new ContasPagar();
	}
	
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public ContasPagar getContaPagar() {
		return contaPagar;
	}

	public void setContaPagar(ContasPagar contaPagar) {
		this.contaPagar = contaPagar;
	}

	public String getQuitar() {
		return quitar;
	}

	public void setQuitar(String quitar) {
		this.quitar = quitar;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public boolean isSelecionadoComite() {
		return selecionadoComite;
	}

	public void setSelecionadoComite(boolean selecionadoComite) {
		this.selecionadoComite = selecionadoComite;
	}

	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public BigDecimal getValorAtualizado() {
		return valorAtualizado;
	}

	public void setValorAtualizado(BigDecimal valorAtualizado) {
		this.valorAtualizado = valorAtualizado;
	}

	public String getNatureza() {
		return natureza;
	}

	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}

	public String getOutrosParticipantes() {
		return outrosParticipantes;
	}

	public void setOutrosParticipantes(String outrosParticipantes) {
		this.outrosParticipantes = outrosParticipantes;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
}
