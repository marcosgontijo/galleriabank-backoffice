package com.webnowbr.siscoat.cobranca.model.request;

import java.math.BigDecimal;

import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

public class TermoCienciaRequest {
	private PagadorRecebedor pagador;
	private ImovelCobranca imovel;
	private BigDecimal valorPreLaudo;
	private String valorPreLaudoFormatado;
	private String valorPreLaudoExtenso;
	private String emissaoLocalData;

	public TermoCienciaRequest() {
		super();
	}

	public TermoCienciaRequest(ContratoCobranca contratoCobranca, BigDecimal valor) {

		super();

		this.imovel = contratoCobranca.getImovel();
		this.pagador = contratoCobranca.getPagador();
		this.valorPreLaudo = valor;
		if (this.valorPreLaudo.compareTo(BigDecimal.ZERO) > 0) {
			this.valorPreLaudo = this.valorPreLaudo.multiply(CommonsUtil.bigDecimalValue(.5));
		}

		ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
		valorPorExtenso.setNumber(this.valorPreLaudo);
		this.valorPreLaudoExtenso = valorPorExtenso.toString();
		this.valorPreLaudoFormatado = CommonsUtil.formataValorMonetarioCci(this.valorPreLaudo, "R$ ");

		this.emissaoLocalData = String.format("Votorantim, %s",
				CommonsUtil.formataData(DateUtil.getDataHoje(), "dd 'de' MMMM 'de' yyyy"));

	}

	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public ImovelCobranca getImovel() {
		return imovel;
	}

	public void setImovel(ImovelCobranca imovel) {
		this.imovel = imovel;
	}

	public BigDecimal getValorPreLaudo() {
		return valorPreLaudo;
	}

	public void setValorPreLaudo(BigDecimal valorPreLaudo) {
		this.valorPreLaudo = valorPreLaudo;
	}

	public String getEmissaoLocalData() {
		return emissaoLocalData;
	}

	public void setEmissaoLocalData(String emissaoLocalData) {
		this.emissaoLocalData = emissaoLocalData;
	}

	public String getValorPreLaudoExtenso() {
		return valorPreLaudoExtenso;
	}

	public void setValorPreLaudoExtenso(String valorPreLaudoExtenso) {
		this.valorPreLaudoExtenso = valorPreLaudoExtenso;
	}

	public String getValorPreLaudoFormatado() {
		return valorPreLaudoFormatado;
	}

	public void setValorPreLaudoFormatado(String valorPreLaudoFormatado) {
		this.valorPreLaudoFormatado = valorPreLaudoFormatado;
	}

}
