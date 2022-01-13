package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;


public class PagadorRecebedorSocio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6223346147907743099L;
	private PagadorRecebedor pessoa;
	private long id;
	private ContratoCobranca contratoCobranca;
	
	private String nomeParticipanteCheckList;
	private boolean rgDocumentosCheckList;
	private boolean comprovanteEnderecoDocumentosCheckList;
	private boolean certidaoCasamentoNascimentoDocumentosCheckList;
	private boolean fichaCadastralDocumentosCheckList;
	private boolean bancoDocumentosCheckList;
	private boolean telefoneEmailDocumentosCheckList;
	private boolean comprovanteRendaCheckList;
	private boolean combateFraudeCheckList;
	private boolean cargoOcupacaoCheckList;
	private boolean taxaCheckList;

	
	public PagadorRecebedor getPessoa() {
		return pessoa;
	}

	public void setPessoa(PagadorRecebedor pessoa) {
		this.pessoa = pessoa;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getNomeParticipanteCheckList() {
		return nomeParticipanteCheckList;
	}

	public void setNomeParticipanteCheckList(String nomeParticipanteCheckList) {
		this.nomeParticipanteCheckList = nomeParticipanteCheckList;
	}

	public boolean isRgDocumentosCheckList() {
		return rgDocumentosCheckList;
	}

	public void setRgDocumentosCheckList(boolean rgDocumentosCheckList) {
		this.rgDocumentosCheckList = rgDocumentosCheckList;
	}

	public boolean isComprovanteEnderecoDocumentosCheckList() {
		return comprovanteEnderecoDocumentosCheckList;
	}

	public void setComprovanteEnderecoDocumentosCheckList(boolean comprovanteEnderecoDocumentosCheckList) {
		this.comprovanteEnderecoDocumentosCheckList = comprovanteEnderecoDocumentosCheckList;
	}

	public boolean isCertidaoCasamentoNascimentoDocumentosCheckList() {
		return certidaoCasamentoNascimentoDocumentosCheckList;
	}

	public void setCertidaoCasamentoNascimentoDocumentosCheckList(boolean certidaoCasamentoNascimentoDocumentosCheckList) {
		this.certidaoCasamentoNascimentoDocumentosCheckList = certidaoCasamentoNascimentoDocumentosCheckList;
	}

	public boolean isFichaCadastralDocumentosCheckList() {
		return fichaCadastralDocumentosCheckList;
	}

	public void setFichaCadastralDocumentosCheckList(boolean fichaCadastralDocumentosCheckList) {
		this.fichaCadastralDocumentosCheckList = fichaCadastralDocumentosCheckList;
	}

	public boolean isBancoDocumentosCheckList() {
		return bancoDocumentosCheckList;
	}

	public void setBancoDocumentosCheckList(boolean bancoDocumentosCheckList) {
		this.bancoDocumentosCheckList = bancoDocumentosCheckList;
	}

	public boolean isTelefoneEmailDocumentosCheckList() {
		return telefoneEmailDocumentosCheckList;
	}

	public void setTelefoneEmailDocumentosCheckList(boolean telefoneEmailDocumentosCheckList) {
		this.telefoneEmailDocumentosCheckList = telefoneEmailDocumentosCheckList;
	}

	public boolean isComprovanteRendaCheckList() {
		return comprovanteRendaCheckList;
	}

	public void setComprovanteRendaCheckList(boolean comprovanteRendaCheckList) {
		this.comprovanteRendaCheckList = comprovanteRendaCheckList;
	}

	public boolean isCombateFraudeCheckList() {
		return combateFraudeCheckList;
	}

	public void setCombateFraudeCheckList(boolean combateFraudeCheckList) {
		this.combateFraudeCheckList = combateFraudeCheckList;
	}

	public boolean isCargoOcupacaoCheckList() {
		return cargoOcupacaoCheckList;
	}

	public void setCargoOcupacaoCheckList(boolean cargoOcupacaoCheckList) {
		this.cargoOcupacaoCheckList = cargoOcupacaoCheckList;
	}

	public boolean isTaxaCheckList() {
		return taxaCheckList;
	}

	public void setTaxaCheckList(boolean taxaCheckList) {
		this.taxaCheckList = taxaCheckList;
	}
	

}


