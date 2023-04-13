package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import org.hibernate.annotations.Type;

public class DocumentoAnalise implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4489431101607924990L;

	private long id;
	private String idRemoto;
	private ContratoCobranca contratoCobranca;

	private String identificacao;
	private String cnpjcpf;
	private String tipoPessoa;
	private String motivoAnalise;
	private String path;
	private String tipo;

	private DocumentosAnaliseEnum tipoEnum;

	private String retorno;
	

	public boolean isRea() {
		return CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isReaNaoEnviado() {
		return CommonsUtil.semValor(idRemoto);
	}

	public boolean isReaProcessado() {
		return !CommonsUtil.semValor(retorno);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdRemoto() {
		return idRemoto;
	}

	public void setIdRemoto(String idRemoto) {
		this.idRemoto = idRemoto;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
		switch (tipo) {
		case "Rea":
			this.tipoEnum = DocumentosAnaliseEnum.REA;
			break;
		case "Relato":
			this.tipoEnum = DocumentosAnaliseEnum.RELATO;

			break;
		case "Crednet":
			this.tipoEnum = DocumentosAnaliseEnum.CREDNET;
			break;
		}
	}

	public DocumentosAnaliseEnum getTipoEnum() {
		return tipoEnum;
	}

	public void setTipoEnum(DocumentosAnaliseEnum tipoEnum) {
		this.tipoEnum = tipoEnum;
		this.tipo = tipoEnum.getNome();
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public String getCnpjcpf() {
		return cnpjcpf;
	}

	public void setCnpjcpf(String cnpjcpf) {
		this.cnpjcpf = cnpjcpf;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getMotivoAnalise() {
		return motivoAnalise;
	}

	public void setMotivoAnalise(String motivoAnalise) {
		this.motivoAnalise = motivoAnalise;
	}

}
