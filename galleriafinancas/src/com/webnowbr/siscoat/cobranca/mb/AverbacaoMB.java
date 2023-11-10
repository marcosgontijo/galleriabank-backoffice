package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.model.Averbacao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorAdicionais;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorSocio;
import com.webnowbr.siscoat.common.CommonsUtil;

/** ManagedBean. */@ManagedBean(name = "averbacaoMB")
@SessionScoped
@SuppressWarnings("deprecation")
public class AverbacaoMB {
    private Averbacao averbacaoSelecionada;
    private List<PagadorRecebedor> listPagador;
    private PagadorRecebedor pagadorSelecionado;
    private ContratoCobranca contratoCobranca;
    
    public void clearAverbacao(ContratoCobranca contrato) {
    	this.contratoCobranca = contrato;
		averbacaoSelecionada = new Averbacao(BigDecimal.valueOf(600.00));
		listPagador = new ArrayList<PagadorRecebedor>();
		listPagador.add(contrato.getPagador());
		for(PagadorRecebedorAdicionais pagadorAdicional : contrato.getListaPagadores()) {
			listPagador.add(pagadorAdicional.getPessoa());
		}
		for(PagadorRecebedorSocio pagadorSocio : contrato.getListSocios()) {
			listPagador.add(pagadorSocio.getPessoa());
		}
	}
    
    public void pesquisaPagador() {
		this.pagadorSelecionado = new PagadorRecebedor();
	}
    
    public void populateSelectedPagador() {
    	averbacaoSelecionada.setPagador(pagadorSelecionado);
		this.pagadorSelecionado = new PagadorRecebedor();
	}
    
    public void addAverbacao() {
    	attDocumentoAverbacao();
		if (CommonsUtil.semValor(contratoCobranca.getListAverbacao())) {
			contratoCobranca.setListAverbacao(new HashSet<>());
		}
		averbacaoSelecionada.setContratoCobranca(contratoCobranca);
		contratoCobranca.getListAverbacao().add(averbacaoSelecionada);
		calcularValorTotalAverbacao();
		averbacaoSelecionada = new Averbacao();
	}
    
    public void attDocumentoAverbacao() {
		if(CommonsUtil.mesmoValor(averbacaoSelecionada.getInformacao(), "CNH")) {
			averbacaoSelecionada.setDocumento("CNH");
		} else if(CommonsUtil.mesmoValor(averbacaoSelecionada.getInformacao(), "RG")) {
			averbacaoSelecionada.setDocumento("RG");
		} else if(CommonsUtil.mesmoValor(averbacaoSelecionada.getInformacao(), "Estado Civil")) {
			averbacaoSelecionada.setDocumento("Certidão de Casamento");
		}
	}
    
    public void removeAverbacao(Averbacao averbacao) {
    	contratoCobranca.getListAverbacao().remove(averbacao);
    	averbacao.setPagador(null);
    	averbacao.setContratoCobranca(null);
		calcularValorTotalAverbacao();
	}

    
    private BigDecimal calcularValorTotalAverbacao() {
		BigDecimal valorTotal = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(contratoCobranca.getListAverbacao())) {
			for (Averbacao averbacao : this.contratoCobranca.getListAverbacao()) {
				if (!CommonsUtil.semValor(averbacao.getValor())) {
					valorTotal = valorTotal.add(averbacao.getValor());
				}
			}
		}
		this.contratoCobranca.setValorTotalAverbacao(valorTotal);
		return valorTotal;
	}
    
	public Averbacao getAverbacaoSelecionada() {
		return averbacaoSelecionada;
	}

	public void setAverbacaoSelecionada(Averbacao averbacaoSelecionada) {
		this.averbacaoSelecionada = averbacaoSelecionada;
	}

	public List<PagadorRecebedor> getListPagador() {
		return listPagador;
	}

	public void setListPagador(List<PagadorRecebedor> listPagador) {
		this.listPagador = listPagador;
	}

	public PagadorRecebedor getPagadorSelecionado() {
		return pagadorSelecionado;
	}

	public void setPagadorSelecionado(PagadorRecebedor pagadorSelecionado) {
		this.pagadorSelecionado = pagadorSelecionado;
	}
}
