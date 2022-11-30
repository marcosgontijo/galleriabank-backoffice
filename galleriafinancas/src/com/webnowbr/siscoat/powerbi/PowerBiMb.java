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
import com.webnowbr.siscoat.common.CommonsUtil;

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
		powerBiNew = new ArrayList<PowerBiNew>();
		contratosConsulta = new ArrayList<ContratoCobranca>();
		powBiDetalhes = new ArrayList<PowerBiDetalhes>();
		powerBiNew.add(getPBNewDataBase("Cadastradas"));
		PowerBiNew pbAnalisadas = new PowerBiNew();
		PowerBiNew pbAprovadas = new PowerBiNew();
		PowerBiNew pbReprovadas = new PowerBiNew();
		
		pbAprovadas = getPBNewDataBase("Aprovadas");	
		pbReprovadas = getPBNewDataBase("Reprovadas");
		pbAnalisadas = mergePowerBiNew(pbAprovadas, pbReprovadas);
		pbAnalisadas.setTipo("Analisadas");
		pbAprovadas = getPBNewDataBase("Aprovadas"); // consulta feita novamente pois lista de contratos de cada analista fica zuada apos o merge
		powerBiNew.add(pbAnalisadas);
		powerBiNew.add(pbAprovadas);
		powerBiNew.add(pbReprovadas);
		powerBiNew.add(getPBNewDataBase("Pendenciadas"));
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
	
	public PowerBiNew mergePowerBi(PowerBiNew a, PowerBiNew b){
		PowerBiNew c = new PowerBiNew();
		
		for(PowerBiDetalhes pda : a.getDetalhes()) {
			for(PowerBiDetalhes pdb : b.getDetalhes()) {
				if(containsAnalista(a.getDetalhes(), pdb.getNome())) {
					if(CommonsUtil.mesmoValor(pda.getNome(), pdb.getNome())){
						PowerBiDetalhes cDetalhes = new PowerBiDetalhes();
						cDetalhes.setNome(pda.getNome());
						cDetalhes.setContratos(pda.getContratos());
						cDetalhes.getContratos().addAll(pdb.getContratos());
						cDetalhes.setQtdContratos(cDetalhes.getContratos().size());
						c.getDetalhes().add(cDetalhes);
					}
				} else {
					c.getDetalhes().add(pdb);
				}	
			}
		}
		c.setContratos(a.getContratos());
		c.getContratos().addAll(b.getContratos());
		c.setNumeroOperacoes(a.getNumeroOperacoes() + b.getNumeroOperacoes());
		c.setValorOperacoes(a.getValorOperacoes().add(b.getValorOperacoes()));
		
		return c;
	}
	
	public PowerBiNew mergePowerBiNew(PowerBiNew a, PowerBiNew b){
		PowerBiNew c = new PowerBiNew();
		List<PowerBiDetalhes> aDetalhes = new ArrayList<>(a.getDetalhes());
		for(PowerBiDetalhes aa : aDetalhes) {
			c.getDetalhes().add(new PowerBiDetalhes(aa));
		}
		//c.getDetalhes().addAll(aDetalhes);
		//c.getDetalhes().addAll(b.getDetalhes());
		
		for(PowerBiDetalhes pdb : b.getDetalhes()) {
			boolean analistaEncontrado = false;
			for(PowerBiDetalhes pdc : c.getDetalhes()) {		
				if(CommonsUtil.mesmoValor(pdc.getNome(), pdb.getNome())){
					pdc.getContratos().addAll(pdb.getContratos());
					pdc.setQtdContratos(pdc.getContratos().size());		
					analistaEncontrado = true;
					break;
				}	
			}
			if(!analistaEncontrado) {
				c.getDetalhes().add(pdb);
			}
		}
		
		c.getContratos().addAll(a.getContratos());
		c.getContratos().addAll(b.getContratos());
		c.setNumeroOperacoes(a.getNumeroOperacoes() + b.getNumeroOperacoes());
		c.setValorOperacoes(a.getValorOperacoes().add(b.getValorOperacoes()));
		
		return c;
	}
	
	public boolean containsAnalista(List<PowerBiDetalhes> list, String name){
		return false;
		//return list.stream().map(PowerBiDetalhes::getNome).filter(name::equals).findFirst().isPresent();
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

