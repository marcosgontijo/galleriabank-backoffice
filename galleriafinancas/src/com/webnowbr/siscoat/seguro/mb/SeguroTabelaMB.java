package com.webnowbr.siscoat.seguro.mb;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;



/** ManagedBean. */
@ManagedBean(name = "seguroTabelaMB")
@SessionScoped
public class SeguroTabelaMB {
	
	private List<SeguroTabelaVO> contratosSeguro;
	
	public String clearFields() {
		return "/Relatorios/Seguro/SeguradoTabela.xhtml";
	}
	
	public String carregaListagem() {
		Segurado segurado = new Segurado();
		this.contratosSeguro = new ArrayList<SeguroTabelaVO>(0);
		
		try {
			for (SeguroTabelaVO seguroTabelaVO : contratosSeguro) {
				seguroTabelaVO.setNumeroContratoSeguro(segurado.getContratoCobranca().getNumeroContratoSeguro());
				/*
				 * seguroTabelaVO.setCodigoSegurado;
				 * seguroTabelaVO.setParcelasOriginais(contratoCobranca.
				 * getListContratoCobrancaParcelasInvestidor1());
				 * seguroTabelaVO.setParcelasFaltantes(contratoCobranca.
				 * getListContratoCobrancaParcelasInvestidor1());
				 * seguroTabelaVO.setAvaliacao(contratoCobranca.get);
				 */
				seguroTabelaVO.setCPFPrincipal(segurado.getPessoa().getCpf());
				seguroTabelaVO.setNomePrincipal(segurado.getPessoa().getNome());
				seguroTabelaVO.setPorcentagemPrincipal(segurado.getPorcentagemSegurador());
				
			}
			SeguroTabelaVO seguroTabelaVO = new SeguroTabelaVO();
			this.contratosSeguro.add(seguroTabelaVO);
			this.contratosSeguro.add(seguroTabelaVO);
			this.contratosSeguro.add(seguroTabelaVO);
			this.contratosSeguro.add(seguroTabelaVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public List<SeguroTabelaVO> getContratosSeguro() {
		return contratosSeguro;
	}

	public void setContratosSeguro(List<SeguroTabelaVO> contratosSeguro) {
		this.contratosSeguro = contratosSeguro;
	}
	
	
}
