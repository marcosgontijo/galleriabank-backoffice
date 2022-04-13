package com.webnowbr.siscoat.visaoGeral;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class VisaoGeralGrupo {

	int codigo;
	String tipo;
	BigInteger quantidade = BigInteger.ZERO;
	BigDecimal valorTotal = BigDecimal.ZERO;

	List<VisaoGeralGrupoDetalhe> Detalhe;

	public void addValor(BigDecimal valor) {
		if (valor != null) {
			valorTotal = valorTotal.add(valor);
			quantidade = quantidade.add(BigInteger.ONE);
		}
	};
	
	public void subValor(BigDecimal valor) {
		if (valor != null) {
			valorTotal = valorTotal.subtract(valor);
			quantidade = quantidade.add(BigInteger.ONE);
		}
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

	public List<VisaoGeralGrupoDetalhe> getDetalhe() {
		return Detalhe;
	}

	public void setDetalhe(List<VisaoGeralGrupoDetalhe> detalhe) {
		Detalhe = detalhe;
	}

}
