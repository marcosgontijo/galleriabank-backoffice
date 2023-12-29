package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;

public class RelatorioB3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Date dataEmissao;
	public Date dataVencimento;
	public BigDecimal valorEmissao;
	public Date dataConstituicaoCredito;
	public Date dataVencimentoCredito;
	public BigDecimal taxaJuros;
	public String numercaoCci;
	public String serieCci;
	public String nomeDevedor;
	public String cpfCnpjDevedor;
	public String naturezaDevedor;
	public String ufDevedor;
	public String cidadeDevedor;
	public String logradouroDevedor;
	public String numeroEnderecoDevedor;
	public String complementoDevedor;
	public String bairroDevedor;
	public String cepDevedor;
	public String ufImovel;
	public String municipioImovel;
	public String logradouroImovel;
	public String numeroEnderecoImovel;
	public String complementoImovel;
	public String bairroImovel;
	public String cepImovel;
	public String matriculaImovel;
	public String naturezaGarantia;
	public String numeroContrato;
	
	public RelatorioB3(ContratoCobranca contrato, CcbContrato ccb) {
		super();
		dataEmissao = contrato.getDataInicio();
		dataVencimento = ccb.getVencimentoUltimaParcelaPagamento();
		valorEmissao = contrato.getValorCCB();
		dataConstituicaoCredito = contrato.getDataInicio();
		dataVencimentoCredito = ccb.getVencimentoUltimaParcelaPagamento();
		taxaJuros = contrato.getTxJurosParcelas();
		numercaoCci = contrato.getNumeroContratoSeguro();
		serieCci = ccb.getSerieCcb();
		PagadorRecebedor pagador = contrato.getPagador();
		nomeDevedor = pagador.getNome();
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			cpfCnpjDevedor = pagador.getCpf();
			naturezaDevedor = "PF";
		} else if(!CommonsUtil.semValor(pagador.getCnpj())) {
			cpfCnpjDevedor = pagador.getCnpj();
			naturezaDevedor = "PJ";
		}
		ufDevedor = pagador.getEstado();
		cidadeDevedor = pagador.getCidade();
		logradouroDevedor = pagador.getEndereco();
		numeroEnderecoDevedor = pagador.getNumero();
		complementoDevedor = pagador.getComplemento();
		bairroDevedor = pagador.getBairro();
		cepDevedor = pagador.getCep();
		ImovelCobranca imovel = contrato.getImovel();
		ufImovel = imovel.getEstado();
		municipioImovel = imovel.getCidade();
		logradouroImovel = ccb.getLogradouroRuaImovel();
		numeroEnderecoImovel = ccb.getLogradouroNumeroImovel();
		complementoImovel = imovel.getComplemento();
		bairroImovel = imovel.getBairro();
		cepImovel = imovel.getCep();
		matriculaImovel = imovel.getNumeroMatricula();
		naturezaGarantia = naturezaDevedor;//
		numeroContrato = contrato.getNumeroContrato();
	}
	
	public RelatorioB3() {
		super();
	}

	
}
