package com.webnowbr.siscoat.cobranca.mb;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadoVO;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;

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
			DemonstrativoResultadosGrupo subTotal = new DemonstrativoResultadosGrupo();
			subTotal.setTipo("Subtotal");
			subTotal.addValor(entradas.getValorTotal().subtract(saidas.getValorTotal()));
			subTotal.addJuros(entradas.getJurosTotal().subtract(saidas.getJurosTotal()));
			subTotal.addAmortizacao(entradas.getAmortizacaoTotal().subtract(saidas.getAmortizacaoTotal()));
			demonstrativoResultado.addDre(subTotal);			
			DemonstrativoResultadosGrupo contasPagar =	contasPagarDao.getDreContasPagar(dataInicio, dataFim);
			demonstrativoResultado.addDre(contasPagar);
			
			DemonstrativoResultadosGrupo total = new DemonstrativoResultadosGrupo();
			total.setTipo("Total");
			total.addValor(subTotal.getValorTotal().subtract(contasPagar.getValorTotal()));
			total.addJuros(subTotal.getJurosTotal().subtract(contasPagar.getJurosTotal()));
			total.addAmortizacao(subTotal.getAmortizacaoTotal().subtract(contasPagar.getAmortizacaoTotal()));
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
