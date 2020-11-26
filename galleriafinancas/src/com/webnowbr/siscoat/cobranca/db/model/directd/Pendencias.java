package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Pendencias implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
		"Pendencias": [
            {
                "Entidade": "Procuradoria Geral do Estado",
                "DataInclusaoCadin": "2018-09-13T00:00:00.0000000",
                "QuantidadePendencias": 1,
                "LocalRegularizacao": "R ÓBIDOS, 674, SÃO JOSÉ DOS CAMPOS."
            }
        ]
	*****
	*****/
	
	private String entidade;
	private Date dataInclusaoCadin;
	private int quantidadePendencias;
	private String localRegularizacao;
	
	public Pendencias() {
		
	}

	public String getEntidade() {
		return entidade;
	}

	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Date getDataInclusaoCadin() {
		return dataInclusaoCadin;
	}

	public void setDataInclusaoCadin(Date dataInclusaoCadin) {
		this.dataInclusaoCadin = dataInclusaoCadin;
	}

	public int getQuantidadePendencias() {
		return quantidadePendencias;
	}

	public void setQuantidadePendencias(int quantidadePendencias) {
		this.quantidadePendencias = quantidadePendencias;
	}

	public String getLocalRegularizacao() {
		return localRegularizacao;
	}

	public void setLocalRegularizacao(String localRegularizacao) {
		this.localRegularizacao = localRegularizacao;
	}
}