package com.webnowbr.siscoat.relatorio.mb;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.LazyDataModel;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/** ManagedBean. */
@ManagedBean(name = "relatorioVendaOperacaoMB")
@SessionScoped
public class RelatorioVendaOperacaoMB {
	/** Controle dos dados da Paginação. */
	// private LazyDataModel<RelatorioVendaOperacaoVO> lazyModel;
	/** Variavel. */
	private BigDecimal faixaValorInicial;
	private BigDecimal faixaValorFinal;
	private Integer situacaoInvestimentos = 0;
	private Integer SituacaoParcelas = 0;

	private List<RelatorioVendaOperacaoVO> contratosVenda;
	private List<RelatorioVendaOperacaoVO> contratosVendaPesquisa;

	public String clearFields() {
//		ContasPagarDao cDao = new ContasPagarDao();
//		Map<String, Object> filters = new HashMap<String,Object>();			
//		filters.put("contaPaga", false);
//		
//		this.contasPagar = cDao.findByFilter(filters);
//		
		contratosVenda = null;
		return "/Relatorios/Venda/RelatorioVendaOperacao.xhtml";
	}

	public void carregaListagem() {

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosVendaPesquisa = new ArrayList<RelatorioVendaOperacaoVO>(0);
		try {
			if (this.contratosVenda == null || this.contratosVenda.size() == 0)
				this.contratosVenda = contratoCobrancaDao.geraRelatorioVendaOperacao();

			for (RelatorioVendaOperacaoVO relatorioVendaOperacaoVO : contratosVenda) {
				if (((BigDecimal.valueOf(0).compareTo(faixaValorInicial ) == 0  && BigDecimal.valueOf(0).compareTo(faixaValorFinal ) == 0)
						|| ( relatorioVendaOperacaoVO.getValorVenda().compareTo(faixaValorInicial) >= 0
								&& relatorioVendaOperacaoVO.getValorVenda().compareTo(faixaValorFinal) <= 0))
						&& //
						(situacaoInvestimentos == 0 || (situacaoInvestimentos == 1
								&& relatorioVendaOperacaoVO.getPercVendido().compareTo(BigDecimal.valueOf(0)) > 0)
								|| (situacaoInvestimentos == 2 && relatorioVendaOperacaoVO.getPercVendido()
										.compareTo(BigDecimal.valueOf(0)) == 0))&& //
						(SituacaoParcelas == 0 || (SituacaoParcelas == 1
							&& relatorioVendaOperacaoVO.getSituacao())
							|| (SituacaoParcelas == 2 &&  !relatorioVendaOperacaoVO.getSituacao()))) {
					
					this.contratosVendaPesquisa.add(relatorioVendaOperacaoVO);
					
				}
			}

			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Get/Set */

	public BigDecimal getFaixaValorInicial() {
		return faixaValorInicial;
	}

	public void setFaixaValorInicial(BigDecimal faixaValorInicial) {
		this.faixaValorInicial = faixaValorInicial;
	}

	public BigDecimal getFaixaValorFinal() {
		return faixaValorFinal;
	}

	public void setFaixaValorFinal(BigDecimal faixaValorFinal) {
		this.faixaValorFinal = faixaValorFinal;
	}

	public Integer getSituacaoInvestimentos() {
		return situacaoInvestimentos;
	}

	public void setSituacaoInvestimentos(Integer situacaoInvestimentos) {
		this.situacaoInvestimentos = situacaoInvestimentos;
	}

	public Integer getSituacaoParcelas() {
		return SituacaoParcelas;
	}

	public void setSituacaoParcelas(Integer situacaoParcelas) {
		SituacaoParcelas = situacaoParcelas;
	}

	public List<RelatorioVendaOperacaoVO> getContratosVendaPesquisa() {
		return contratosVendaPesquisa;
	}

	public void setContratosVendaPesquisa(List<RelatorioVendaOperacaoVO> contratosVendaPesquisa) {
		this.contratosVendaPesquisa = contratosVendaPesquisa;
	}
//
//	public LazyDataModel<RelatorioVendaOperacaoVO> getLazyModel() {
//		return lazyModel;
//	}
//
//	public void setLazyModel(LazyDataModel<RelatorioVendaOperacaoVO> lazyModel) {
//		this.lazyModel = lazyModel;
//	}

}
