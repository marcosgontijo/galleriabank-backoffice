package com.webnowbr.siscoat.cobranca.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class DemonstrativoResultadosGrupo {

	int codigo;
	String tipo;
	BigInteger quantidade = BigInteger.ZERO;
	BigDecimal valorTotal = BigDecimal.ZERO;
	BigDecimal jurosTotal = BigDecimal.ZERO;
	BigDecimal amortizacaoTotal = BigDecimal.ZERO;

	List<DemonstrativoResultadosGrupoDetalhe> Detalhe;

	public void addValor(BigDecimal valor) {
		if (valor != null) {
			valorTotal = valorTotal.add(valor);
			quantidade = quantidade.add(BigInteger.ONE);
		}
	};

	public void addJuros(BigDecimal juros) {
		if (juros != null)
			jurosTotal = jurosTotal.add(juros);
	};

	public void addAmortizacao(BigDecimal amortizacao) {
		if (amortizacao != null)
			amortizacaoTotal = amortizacaoTotal.add(amortizacao);
	};

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public BigInteger getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigInteger quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getJurosTotal() {
		return jurosTotal;
	}

	public void setJurosTotal(BigDecimal jurosTotal) {
		this.jurosTotal = jurosTotal;
	}

	public BigDecimal getAmortizacaoTotal() {
		return amortizacaoTotal;
	}

	public void setAmortizacaoTotal(BigDecimal amortizacaoTotal) {
		this.amortizacaoTotal = amortizacaoTotal;
	}

	public List<DemonstrativoResultadosGrupoDetalhe> getDetalhe() {
		return Detalhe;
	}

	public void setDetalhe(List<DemonstrativoResultadosGrupoDetalhe> detalhe) {
		Detalhe = detalhe;
	}

}
