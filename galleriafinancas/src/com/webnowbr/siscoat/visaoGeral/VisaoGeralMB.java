package com.webnowbr.siscoat.visaoGeral;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.op.CDIDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.common.SiscoatConstants;

/** ManagedBean. */
@ManagedBean(name = "visaoGeralMB")
@SessionScoped
public class VisaoGeralMB {
	// variaveis filtro
	Date dataInicio;
	Date dataFim;

	VisaoGeralVO visaoGeral;

	public String clearFields() {
		visaoGeral = new VisaoGeralVO();
		// this.tituloPainel = "Adicionar";

		return "/Atendimento/Cobranca/Contabilidade/VisaoGeral.xhtml";
	}

	public String gerarVG() {
		visaoGeral = new VisaoGeralVO();
		VisaoGeralDao vgDao = new VisaoGeralDao();

		try {
			
			//VisaoGeralGrupo contratos = contratoCobrancaDao.getDreContrato(dataInicio, dataFim);
			//visaoGeral.addDre(contratos);
			
			VisaoGeralGrupo cadastratos = vgDao.getVgCadastro(dataInicio, dataFim);
			visaoGeral.addVg(cadastratos);
			
			VisaoGeralGrupo preAprovados = vgDao.getVgPreAprovado(dataInicio, dataFim);
			visaoGeral.addVg(preAprovados);
			
			VisaoGeralGrupo laudo = vgDao.getVgLaudo(dataInicio, dataFim);
			visaoGeral.addVg(laudo);
			
			VisaoGeralGrupo paju = vgDao.getVgPaju(dataInicio, dataFim);
			visaoGeral.addVg(paju);
			
			VisaoGeralGrupo assinaturas = vgDao.getVgAssinatura(dataInicio, dataFim);
			visaoGeral.addVg(assinaturas);
			
			VisaoGeralGrupo registros = vgDao.getVgRegistro(dataInicio, dataFim);
			visaoGeral.addVg(registros);
			
			VisaoGeralGrupo debenture = vgDao.getTotalDeb(dataInicio, dataFim);
			visaoGeral.addVg(debenture);
			
			VisaoGeralGrupo cxBrutoFidc = vgDao.getBrutoFidc(dataInicio, dataFim);			
			visaoGeral.addVg(cxBrutoFidc);
			
			VisaoGeralGrupo cxLiquido = new VisaoGeralGrupo();
			cxLiquido.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
			cxLiquido.setTipo("Cx Líquido");
			cxLiquido.setCodigo(2);
			cxLiquido.setValorTotal(cxBrutoFidc.getValorTotal().subtract(registros.getValorTotal()));
			visaoGeral.addVg(cxLiquido);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public VisaoGeralVO getVisaoGeral() {
		return visaoGeral;
	}

	public void setVisaoGeral(VisaoGeralVO visaoGeral) {
		this.visaoGeral = visaoGeral;
	}

}
