package com.webnowbr.siscoat.powerbi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;

/** ManagedBean. */
@ManagedBean(name = "powerBiMb")
@SessionScoped
public class PowerBiMb {
	public PowerBiVO powerBiHoje;
	public PowerBiVO powerBiOntem;
	public Collection<ContratoCobranca> contratosConsulta;
	public List<ContratoCobrancaDetalhes> listParcelas;
	public List<PowerBiDetalhes> powBiDetalhes = new ArrayList<PowerBiDetalhes>();
	
	public String clearPowerBi() {
		powerBiHoje = new PowerBiVO();
		powerBiOntem = new PowerBiVO();		
		contratosConsulta = new ArrayList<ContratoCobranca>();
		listParcelas = new ArrayList<ContratoCobrancaDetalhes>();
		powBiDetalhes = new ArrayList<PowerBiDetalhes>();
				
		return "/Atendimento/Cobranca/PowerBi/PowerBi.xhtml";
	}
	
	public void powerBiHoje() {
		PowerBiDao powerBiDao = new PowerBiDao();
		powerBiHoje = powerBiDao.powerBiConsulta(powerBiHoje.getDataConsulta());
		powerBiHoje.setSecuritizadora(powerBiDao.dadosContratosConsulta(powerBiHoje.getDataConsulta(), "Securitizadora"));
		powerBiHoje.setFidc(powerBiDao.dadosContratosConsulta(powerBiHoje.getDataConsulta(), "Fidc"));
		
		powerBiHoje.setAnalises(powerBiDao.listaPowerBiDetalhes(powerBiHoje.getDataConsulta(), "inicioAnalise"));
		powerBiHoje.setPreAprovacoes(powerBiDao.listaPowerBiDetalhes(powerBiHoje.getDataConsulta(), "analiseAprovada"));
		powerBiHoje.setAssinaturas(powerBiDao.listaPowerBiDetalhes(powerBiHoje.getDataConsulta(), "assinatura"));
		powerBiHoje.setRegistros(powerBiDao.listaPowerBiDetalhes(powerBiHoje.getDataConsulta(), "registro"));
		
	}
	
	public void powerBiOntem() {
		PowerBiDao powerBiDao = new PowerBiDao();
		powerBiOntem = powerBiDao.powerBiConsulta(powerBiOntem.getDataConsulta());
		powerBiOntem.setSecuritizadora(powerBiDao.dadosContratosConsulta(powerBiOntem.getDataConsulta(), "Securitizadora"));
		powerBiOntem.setFidc(powerBiDao.dadosContratosConsulta(powerBiOntem.getDataConsulta(), "Fidc"));
		
		powerBiOntem.setAnalises(powerBiDao.listaPowerBiDetalhes(powerBiOntem.getDataConsulta(), "inicioAnalise"));
		powerBiOntem.setPreAprovacoes(powerBiDao.listaPowerBiDetalhes(powerBiOntem.getDataConsulta(), "analiseAprovada"));
		powerBiOntem.setAssinaturas(powerBiDao.listaPowerBiDetalhes(powerBiOntem.getDataConsulta(), "assinatura"));
		powerBiOntem.setRegistros(powerBiDao.listaPowerBiDetalhes(powerBiOntem.getDataConsulta(), "registro"));
	}
	
	public void powerBiTodos() {
		powerBiHoje();
		powerBiOntem();
	}
	
	
	
	public PowerBiVO getPowerBiHoje() {
		return powerBiHoje;
	}
	public void setPowerBiHoje(PowerBiVO powerBiHoje) {
		this.powerBiHoje = powerBiHoje;
	}
	public PowerBiVO getPowerBiOntem() {
		return powerBiOntem;
	}
	public void setPowerBiOntem(PowerBiVO powerBiOntem) {
		this.powerBiOntem = powerBiOntem;
	}
	public Collection<ContratoCobranca> getContratosConsulta() {
		return contratosConsulta;
	}
	public void setContratosConsulta(Collection<ContratoCobranca> contratosConsulta) {
		this.contratosConsulta = contratosConsulta;
	}
	public List<ContratoCobrancaDetalhes> getListParcelas() {
		return listParcelas;
	}
	public void setListParcelas(List<ContratoCobrancaDetalhes> listParcelas) {
		this.listParcelas = listParcelas;
	}
	public List<PowerBiDetalhes> getPowBiDetalhes() {
		return powBiDetalhes;
	}
	public void setPowBiDetalhes(List<PowerBiDetalhes> powBiDetalhes) {
		this.powBiDetalhes = powBiDetalhes;
	}		
}

