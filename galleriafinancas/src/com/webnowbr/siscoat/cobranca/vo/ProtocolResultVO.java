package com.webnowbr.siscoat.cobranca.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProtocolResultVO {

	/**
	 * Codigo do Cedente
	 */
	private Integer codigoCedente;
	
	/**
	 * Número de protocolo, dado pelo código do lote gerado, ou
	 * <code>null</code> em caso de erro no processoamento do lote
	 */
	private Integer protocolCode;

	/** Data de processamento do lote */
	private Date processingDate;

	/** Quantidade de documentos processados */
	private Integer numberOfDocuments;

	/** Valor total dos documentos processados */
	private Double totalValue;

	/**
	 * Status da validação do arquivo CNAB: Novo (0), Válido (1), Inválido (2),
	 * Reservado (3), Finalizado (4), Cancelado (5). Novo indica um lote recém
	 * criado e ainda não tratado. Reservado indica um lote em uso na abertura
	 * de um borderô. Finalizado indica que todos os documentos do lote já foram
	 * usados ou descartados. Cancelado indica que o lote foi descartado pela
	 * operação.
	 */
	private Short status;

	/** Adiciona mensagem de erro */
	public void addErrorMessage(final String mensagem) {
		addErrorMessage(mensagem);
		status = 2;
	}

	/** Adiciona mensagem de erro */
	public void addErrorMessage(final int numeroLinha,
			final String numeroNotaFiscal, final String numeroDocumento,
			final String mensagem) {
		addErrorMessage(numeroLinha, numeroNotaFiscal, numeroDocumento,
				mensagem);
		status = 2;
	}

	/** @see #protocolCode */
	public Integer getProtocolCode() {
		return protocolCode;
	}

	/** @see #protocolCode */
	public void setProtocolCode(Integer protocolCode) {
		this.protocolCode = protocolCode;
	}

	/**
	 * @return the codigoCedente
	 */
	public Integer getCodigoCedente() {
		return codigoCedente;
	}

	/**
	 * @param codigoCedente the codigoCedente to set
	 */
	public void setCodigoCedente(Integer codigoCedente) {
		this.codigoCedente = codigoCedente;
	}

	/** @see #processingDate */
	public Date getProcessingDate() {
		return processingDate;
	}

	/** @see #processingDate */
	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	/** @see #numberOfDocuments */
	public Integer getNumberOfDocuments() {
		return numberOfDocuments;
	}

	/** @see #numberOfDocuments */
	public void setNumberOfDocuments(Integer numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	/** @see #totalValue */
	public Double getTotalValue() {
		return totalValue;
	}

	/** @see #totalValue */
	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	/** @see #status */
	public Short getStatus() {
		return status;
	}

	/** @see #status */
	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * @see br.com.banicred.banisys.business.vo.BanisysVO#toString()
	 */
	@Override
	public String toString() {
		List<ProtocolMessageResultVO> mensagens = getMensagens();
		return super.toString()
				+ (mensagens == null || mensagens.isEmpty() ? "" : mensagens
						.get(0));
	}




	/** Lista de mensagens geradas no processamento do lote */
	private List<ProtocolMessageResultVO> mensagens;

	/** Adiciona mensagem informativa */
	public void addMessage(final String message) {
		if (mensagens == null) {
			mensagens = new ArrayList<ProtocolMessageResultVO>(0);
		}
		mensagens.add(new ProtocolMessageResultVO(null, null, null, message));
	}

	/** Adiciona mensagem informativa */
	public void addMessage(final int lineNo, final String numeroNotaFiscal,
			final String docNo, final String message) {
		if (mensagens == null) {
			mensagens = new ArrayList<ProtocolMessageResultVO>(0);
		}
		mensagens.add(new ProtocolMessageResultVO(lineNo, numeroNotaFiscal,
				docNo, message));
	}

	
	/** Obtém quantidade de mensagens de processamento do lote. */
	public int getQtdMensagens() {
		return mensagens == null ? 0 : mensagens.size();
	}

	/** @see #mensagens */
	public List<ProtocolMessageResultVO> getMensagens() {
		return mensagens;
	}

	/** @see #mensagens */
	public void setMensagens(List<ProtocolMessageResultVO> mensagens) {
		this.mensagens = mensagens;
	}


}
