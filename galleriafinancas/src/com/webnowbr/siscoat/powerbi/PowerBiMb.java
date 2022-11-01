package com.webnowbr.siscoat.powerbi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.util.StringUtil;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;

import antlr.StringUtils;

/** ManagedBean. */
@ManagedBean(name = "powerBiMb")
@SessionScoped
public class PowerBiMb {
	public PowerBiVO powerBiHoje;
	public PowerBiVO powerBiOntem;
	public Collection<ContratoCobranca> contratosConsulta;
	public List<ContratoCobrancaDetalhes> listParcelas;
	public List<PowerBiDetalhes> powBiDetalhes = new ArrayList<PowerBiDetalhes>();
	
	///////////////////////////////////
	public Date dataInicio;
	public Date datafim;
	public List<PowerBiNew> powerBiNew = new ArrayList<PowerBiNew>();
	
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
	
	public String clearPowerBiNew() {
		dataInicio = null;
		datafim = null;
		powerBiNew = new ArrayList<PowerBiNew>();
		contratosConsulta = new ArrayList<ContratoCobranca>();
		powBiDetalhes = new ArrayList<PowerBiDetalhes>();
		
		return "/Atendimento/Cobranca/PowerBi/PowerBiNew.xhtml";
	}
	
	public void carregarListagemNew() {
		powerBiNew.add(getPBNewDataBase("Cadastradas"));
		powerBiNew.add(getPBNewDataBase("Aprovadas"));
		powerBiNew.add(getPBNewDataBase("Reprovadas"));
		powerBiNew.add(getPBNewDataBase("Com pedido de laudo"));
		powerBiNew.add(getPBNewDataBase("Com pedido de paju"));
		powerBiNew.add(getPBNewDataBase("Enviadas para Com. Jurídico"));
		powerBiNew.add(getPBNewDataBase("Comentadas pelo Jurídico"));
		powerBiNew.add(getPBNewDataBase("Enviadas para Validação Doc."));
		powerBiNew.add(getPBNewDataBase("Enviadas para Comitê"));
		powerBiNew.add(getPBNewDataBase("Enviadas para Ag. Doc"));
		powerBiNew.add(getPBNewDataBase("Enviadas para Ag. CCB"));
		powerBiNew.add(getPBNewDataBase("com CCI Emitida"));		
		powerBiNew.add(getPBNewDataBase("com CCI Assinada"));
	}
	
	public PowerBiNew getPBNewDataBase(String tipo) {
		PowerBiDao powerBiDao = new PowerBiDao();
		return powerBiDao.pbNew(dataInicio, datafim, tipo);
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

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDatafim() {
		return datafim;
	}

	public void setDatafim(Date datafim) {
		this.datafim = datafim;
	}

	public List<PowerBiNew> getPowerBiNew() {
		return powerBiNew;
	}

	public void setPowerBiNew(List<PowerBiNew> powerBiNew) {
		this.powerBiNew = powerBiNew;
	}	
	
	
}

