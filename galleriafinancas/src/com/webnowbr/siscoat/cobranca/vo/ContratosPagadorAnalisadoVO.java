package com.webnowbr.siscoat.cobranca.vo;

import java.util.Collection;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;

public class ContratosPagadorAnalisadoVO {
	PagadorRecebedor pagador;
	RelacionamentoPagadorRecebedor relacionamento;
	
	private Collection<ContratoCobranca> contratosPagadorAnalisado;

	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public RelacionamentoPagadorRecebedor getRelacionamento() {
		return relacionamento;
	}

	public void setRelacionamento(RelacionamentoPagadorRecebedor relacionamento) {
		this.relacionamento = relacionamento;
	}

	public Collection<ContratoCobranca> getContratosPagadorAnalisado() {
		return contratosPagadorAnalisado;
	}

	public void setContratosPagadorAnalisado(Collection<ContratoCobranca> contratosPagadorAnalisado) {
		this.contratosPagadorAnalisado = contratosPagadorAnalisado;
	}

}
