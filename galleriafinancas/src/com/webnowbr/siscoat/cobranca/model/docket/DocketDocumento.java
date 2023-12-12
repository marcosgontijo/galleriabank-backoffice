package com.webnowbr.siscoat.cobranca.model.docket;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;

public class DocketDocumento {

	@SerializedName("documentKitId")
	private String documentKitId;

	@SerializedName("produtoId")
	private String produtoId;

	@SerializedName("kitId")
	private String kitId;

	@SerializedName("kitNome")
	private String kitNome;

	@SerializedName("titularTipo")
	private String titularTipo;

	@SerializedName("documentoNome")
	private String documentoNome;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	@SerializedName("progresso")
	private String progresso;

	@SerializedName("dataEntrega")
	private Date dataEntrega;

	@SerializedName("situacao")
	private String situacao;

	@SerializedName("arquivos")
	private List<DocketDocumentoArquivo> arquivos;

	@SerializedName("idExibicao")
	private String idExibicao;

	@SerializedName("titularId")
	private String titularId;

	@SerializedName("titularIdExibicao")
	private String titularIdExibicao;

	@SerializedName("pedidoId")
	private String pedidoId;
	
	
	@SerializedName("campos")
	private DocketDocumentoCampo campos;

	public String getSituacaoPaju() {
		if (!CommonsUtil.mesmoValorIgnoreCase("NEGATIVA", situacao))
			return SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS;
		else
			return SiscoatConstants.CND_SITUACAO_NAO_POSSUI_DEBITOS;
	}

	public String getDocumentKitId() {
		return documentKitId;
	}

	public void setDocumentKitId(String documentKitId) {
		this.documentKitId = documentKitId;
	}

	public String getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(String produtoId) {
		this.produtoId = produtoId;
	}

	public String getKitId() {
		return kitId;
	}

	public void setKitId(String kitId) {
		this.kitId = kitId;
	}

	public String getKitNome() {
		return kitNome;
	}

	public void setKitNome(String kitNome) {
		this.kitNome = kitNome;
	}

	public String getTitularTipo() {
		return titularTipo;
	}

	public void setTitularTipo(String titularTipo) {
		this.titularTipo = titularTipo;
	}
	
	public String getNomePaju() {
		return this.documentoNome + " - " + campos.getEstadoNome();
	}
	
	

	public String getDocumentoNome() {
		return documentoNome;
	}

	public void setDocumentoNome(String documentoNome) {
		this.documentoNome = documentoNome;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProgresso() {
		return progresso;
	}

	public void setProgresso(String progresso) {
		this.progresso = progresso;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public List<DocketDocumentoArquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<DocketDocumentoArquivo> arquivos) {
		this.arquivos = arquivos;
	}

	public String getIdExibicao() {
		return idExibicao;
	}

	public void setIdExibicao(String idExibicao) {
		this.idExibicao = idExibicao;
	}

	public String getTitularId() {
		return titularId;
	}

	public void setTitularId(String titularId) {
		this.titularId = titularId;
	}

	public String getTitularIdExibicao() {
		return titularIdExibicao;
	}

	public void setTitularIdExibicao(String titularIdExibicao) {
		this.titularIdExibicao = titularIdExibicao;
	}

	public String getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}

	public DocketDocumentoCampo getCampos() {
		return campos;
	}

	public void setCampos(DocketDocumentoCampo campos) {
		this.campos = campos;
	}
	
}
