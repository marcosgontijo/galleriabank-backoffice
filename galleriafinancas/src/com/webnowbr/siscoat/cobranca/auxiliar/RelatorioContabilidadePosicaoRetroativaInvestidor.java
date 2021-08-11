package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;

public class RelatorioContabilidadePosicaoRetroativaInvestidor {

	private ContratoCobranca contrato;	
	
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor;
	
	private BigDecimal totalAPagarPorContrato;
	
	private boolean temParcelas;
	
	public RelatorioContabilidadePosicaoRetroativaInvestidor() {

	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidor() {
		return listContratoCobrancaParcelasInvestidor;
	}

	public void setListContratoCobrancaParcelasInvestidor(
			List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidor) {
		this.listContratoCobrancaParcelasInvestidor = listContratoCobrancaParcelasInvestidor;
	}

	public BigDecimal getTotalAPagarPorContrato() {
		return totalAPagarPorContrato;
	}

	public void setTotalAPagarPorContrato(BigDecimal totalAPagarPorContrato) {
		this.totalAPagarPorContrato = totalAPagarPorContrato;
	}

	public boolean isTemParcelas() {
		return temParcelas;
	}

	public void setTemParcelas(boolean temParcelas) {
		this.temParcelas = temParcelas;
	}
}
