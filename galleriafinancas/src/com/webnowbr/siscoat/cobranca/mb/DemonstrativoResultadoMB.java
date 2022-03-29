package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.op.CDIDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadoVO;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.common.SiscoatConstants;

/** ManagedBean. */
@ManagedBean(name = "demonstrativoResultadoMB")
@SessionScoped
public class DemonstrativoResultadoMB {
	// variaveis filtro
	Date dataInicio;
	Date dataFim;

	DemonstrativoResultadoVO demonstrativoResultado;

	public String clearFields() {
		demonstrativoResultado = new DemonstrativoResultadoVO();
		// this.tituloPainel = "Adicionar";

		return "/Atendimento/Cobranca/Contabilidade/DemonstrativoResultado.xhtml";
	}

	public String gerarDRE() {
		demonstrativoResultado = new DemonstrativoResultadoVO();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		DebenturesInvestidorDao debenturesInvestidorDao = new DebenturesInvestidorDao();

		try {
			
			DemonstrativoResultadosGrupo contratos = contratoCobrancaDao.getDreContrato(dataInicio, dataFim);
			demonstrativoResultado.addDre(contratos);
			
			DemonstrativoResultadosGrupo debentures = debenturesInvestidorDao.getDreDebentures(dataInicio, dataFim);
			demonstrativoResultado.addDre(debentures);
			
			DemonstrativoResultadosGrupo entradas = contratoCobrancaDao.getDreEntradas(dataInicio, dataFim);
			demonstrativoResultado.addDre(entradas);
			DemonstrativoResultadosGrupo saidas = contratoCobrancaDao.getDreSaidas(dataInicio, dataFim);
			demonstrativoResultado.addDre(saidas);
			
			DemonstrativoResultadosGrupo investidoresFidc = new DemonstrativoResultadosGrupo();
			investidoresFidc.setTipo("Pagamento Investidores FIDC");
			investidoresFidc.setCodigo(1);
			BigDecimal jurosFidc = SiscoatConstants.VALOR_FIDC;
			BigDecimal taxaFidc = SiscoatConstants.TAXA_AA_FIDC;
			taxaFidc = taxaFidc.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
			CDIDao cdiDao = new CDIDao();
			CDIMB cdiMB = new CDIMB();
			BigDecimal cdi = cdiDao.getTaxaCDIMes(cdiMB.getDataComMesAnterior(dataInicio));
			taxaFidc = taxaFidc.add(cdi);
			taxaFidc = taxaFidc.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
			jurosFidc = jurosFidc.multiply(taxaFidc);
			jurosFidc = jurosFidc.setScale(2, BigDecimal.ROUND_HALF_UP);
			investidoresFidc.addValor(jurosFidc);
			investidoresFidc.addJuros(jurosFidc);
			investidoresFidc.addAmortizacao(BigDecimal.ZERO);
			demonstrativoResultado.addDre(investidoresFidc);
			
			DemonstrativoResultadosGrupo subTotal = new DemonstrativoResultadosGrupo();
			subTotal.setTipo("Subtotal");
			subTotal.setCodigo(1);
			subTotal.addValor(entradas.getValorTotal().subtract(saidas.getValorTotal()).subtract(jurosFidc));
			subTotal.addJuros(entradas.getJurosTotal().subtract(saidas.getJurosTotal()).subtract(jurosFidc));
			subTotal.addAmortizacao(entradas.getAmortizacaoTotal().subtract(saidas.getAmortizacaoTotal()));
			demonstrativoResultado.addDre(subTotal);			
				
			//DemonstrativoResultadosGrupo contasPagar =	contasPagarDao.getDreContasPagar(dataInicio, dataFim);
			//demonstrativoResultado.addDre(contasPagar);
			
			DemonstrativoResultadosGrupo contasPagarSecuritizadora = contasPagarDao.getDreContasPagarEmpresa(dataInicio, dataFim, "Securitizadora");
			demonstrativoResultado.addDre(contasPagarSecuritizadora);
			
			DemonstrativoResultadosGrupo contasPagarFidc =	contasPagarDao.getDreContasPagarEmpresa(dataInicio, dataFim, "Fidc");
			demonstrativoResultado.addDre(contasPagarFidc);
			
			BigDecimal valorTotalContas = contasPagarSecuritizadora.getValorTotal().add(contasPagarFidc.getValorTotal());
			BigDecimal jurosTotalContas = contasPagarSecuritizadora.getJurosTotal().add(contasPagarFidc.getJurosTotal());
			BigDecimal amortizacaoTotalContas = contasPagarSecuritizadora.getAmortizacaoTotal().add(contasPagarFidc.getAmortizacaoTotal());
			
			DemonstrativoResultadosGrupo total = new DemonstrativoResultadosGrupo();
			total.setTipo("Total");
			total.setCodigo(1);
			total.addValor(subTotal.getValorTotal().subtract(valorTotalContas));
			total.addJuros(subTotal.getJurosTotal().subtract(jurosTotalContas));
			total.addAmortizacao(subTotal.getAmortizacaoTotal().subtract(amortizacaoTotalContas));
			demonstrativoResultado.addDre(total);
			
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

	public DemonstrativoResultadoVO getDemonstrativoResultado() {
		return demonstrativoResultado;
	}

	public void setDemonstrativoResultado(DemonstrativoResultadoVO demonstrativoResultado) {
		this.demonstrativoResultado = demonstrativoResultado;
	}

}
