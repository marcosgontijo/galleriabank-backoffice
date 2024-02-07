package com.webnowbr.siscoat.cobranca.vo;

public class ProtocolMessageResultVO {

	/** Número da linha do arquivo que causou a mensagem */
	private Integer numeroLinhaArquivo;

	/** Número da nota fiscal ou pedido que causou a mensagem */
	private String numeroNotaFiscal;

	/** Número do documento que causou a mensagem */
	private String numeroDocumento;

	/** Texto da mensagem */
	private String mensagem;

	public ProtocolMessageResultVO(Integer numeroLinhaArquivo,
			String numeroNotaFiscal, String numeroDocumento, String mensagem) {
		super();
		this.numeroLinhaArquivo = numeroLinhaArquivo;
		this.numeroNotaFiscal = numeroNotaFiscal;
		this.numeroDocumento = numeroDocumento;
		this.mensagem = mensagem;
	}

	/**
	 * @see br.com.banicred.banisys.business.vo.BanisysVO#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		if (this.numeroLinhaArquivo != null) {
			sb.append("Linha ").append(this.numeroLinhaArquivo).append(":");
			sep = " ";
		}
		if (this.numeroNotaFiscal != null) {
			sb.append(sep).append(sep).append("NF ")
					.append(this.numeroNotaFiscal);
			sep = " ";
		}
		if (this.numeroDocumento != null) {
			sb.append(sep).append(" Doc ").append(this.numeroDocumento);
			sep = " ";
		}
		if (this.mensagem != null) {
			sb.append(sep).append(this.mensagem);
		}
		return sb.toString();
	}

	/** @see #numeroLinhaArquivo */
	public Integer getNumeroLinhaArquivo() {
		return numeroLinhaArquivo;
	}

	/** @see #numeroLinhaArquivo */
	public void setNumeroLinhaArquivo(Integer numeroLinhaArquivo) {
		this.numeroLinhaArquivo = numeroLinhaArquivo;
	}

	/** @see #numeroNotaFiscal */
	public String getNumeroNotaFiscal() {
		return numeroNotaFiscal;
	}

	/** @see #numeroNotaFiscal */
	public void setNumeroNotaFiscal(String numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}

	/** @see #numeroDocumento */
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	/** @see #numeroDocumento */
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	/** @see #mensagem */
	public String getMensagem() {
		return mensagem;
	}

	/** @see #mensagem */
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}


}
