package com.webnowbr.siscoat.relatorio.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.CartorioDao;
import com.webnowbr.siscoat.common.CommonsUtil;

public class RelatorioInadimplencia {
	public String empresa;
	public String numeroContrato;
	public String nomePagador;
	public boolean estaEmCartorio;
	public String statusCartorio;
	public BigDecimal valorCcb;
	public BigDecimal valorGarantia;
	public ContratoCobranca contrato;
	
	public Date primeiroAtraso;
	public ContratoCobrancaDetalhes primeiraParcelaAtraso;
	public int qtdParcelasAtraso;
	public List<ContratoCobrancaDetalhes> parcelasAtraso;
	
	public int qtdParcelasPagas;
	public List<ContratoCobrancaDetalhes> parcelasPagas;
	
	public RelatorioInadimplencia() {
		super();
		parcelasAtraso = new ArrayList<ContratoCobrancaDetalhes>();
		parcelasPagas = new ArrayList<ContratoCobrancaDetalhes>();
	}
	
	public void populateCampos(){
		qtdParcelasAtraso = parcelasAtraso.size();
		if(parcelasAtraso.size() > 0) {
			primeiraParcelaAtraso = parcelasAtraso.get(0);
			primeiroAtraso = primeiraParcelaAtraso.getDataVencimento();
		}
		qtdParcelasPagas = parcelasPagas.size();
		if(!CommonsUtil.semValor(contrato)) {
			empresa = contrato.getEmpresa();
			numeroContrato = contrato.getNumeroContrato();
			estaEmCartorio = contrato.isContratoEmCartorio();
			valorCcb = contrato.getValorCCB();
			valorGarantia = contrato.getValorImovel();
			if(!CommonsUtil.semValor(contrato.getPagador())) {
				nomePagador = contrato.getPagador().getNome();
			}
		}
		CartorioDao dao = new CartorioDao();
		if(!CommonsUtil.semValor(dao.consultaUltimoCartorio(contrato)))
			statusCartorio = dao.consultaUltimoCartorio(contrato).getStatus();
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public boolean isEstaEmCartorio() {
		return estaEmCartorio;
	}

	public void setEstaEmCartorio(boolean estaEmCartorio) {
		this.estaEmCartorio = estaEmCartorio;
	}

	public BigDecimal getValorCcb() {
		return valorCcb;
	}

	public void setValorCcb(BigDecimal valorCcb) {
		this.valorCcb = valorCcb;
	}

	public BigDecimal getValorGarantia() {
		return valorGarantia;
	}

	public void setValorGarantia(BigDecimal valorGarantia) {
		this.valorGarantia = valorGarantia;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public Date getPrimeiroAtraso() {
		return primeiroAtraso;
	}

	public void setPrimeiroAtraso(Date primeiroAtraso) {
		this.primeiroAtraso = primeiroAtraso;
	}

	public ContratoCobrancaDetalhes getPrimeiraParcelaAtraso() {
		return primeiraParcelaAtraso;
	}

	public void setPrimeiraParcelaAtraso(ContratoCobrancaDetalhes primeiraParcelaAtraso) {
		this.primeiraParcelaAtraso = primeiraParcelaAtraso;
	}

	public List<ContratoCobrancaDetalhes> getParcelasAtraso() {
		return parcelasAtraso;
	}

	public void setParcelasAtraso(List<ContratoCobrancaDetalhes> parcelasAtraso) {
		this.parcelasAtraso = parcelasAtraso;
	}

	public int getQtdParcelasPagas() {
		return qtdParcelasPagas;
	}

	public void setQtdParcelasPagas(int qtdParcelasPagas) {
		this.qtdParcelasPagas = qtdParcelasPagas;
	}

	public List<ContratoCobrancaDetalhes> getParcelasPagas() {
		return parcelasPagas;
	}

	public void setParcelasPagas(List<ContratoCobrancaDetalhes> parcelasPagas) {
		this.parcelasPagas = parcelasPagas;
	}

	public int getQtdParcelasAtraso() {
		return qtdParcelasAtraso;
	}

	public void setQtdParcelasAtraso(int qtdParcelasAtraso) {
		this.qtdParcelasAtraso = qtdParcelasAtraso;
	}

	public String getStatusCartorio() {
		return statusCartorio;
	}

	public void setStatusCartorio(String statusCartorio) {
		this.statusCartorio = statusCartorio;
	}
}	
