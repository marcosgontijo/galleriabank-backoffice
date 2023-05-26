package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.common.CommonsUtil;

public class DocketWebhookRetornoDocumento {

	public String  documentKitId;
	public String  produtoId;
	public String  kitId;
	public String  kitNome;
	public String  titularTipo;
	public String  documentoNome;
	public String  id;
	public String  status;
	public String  progresso;
	public Date dataEntrega;
	public String  situacao;
	public Long idExibicao;
	public String  titularId;
	public Integer titularIdExibicao;
	public String  pedidoId;
	
	public DocketWebhookRetornoDocumentoArquivoCampos  campos; 
	
    public List<DocketWebhookRetornoDocumentoArquivo> arquivos;

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

	public Long getIdExibicao() {
		return idExibicao;
	}

	public void setIdExibicao(Long idExibicao) {
		this.idExibicao = idExibicao;
	}

	public String getTitularId() {
		return titularId;
	}

	public void setTitularId(String titularId) {
		this.titularId = titularId;
	}

	public Integer getTitularIdExibicao() {
		return titularIdExibicao;
	}

	public void setTitularIdExibicao(Integer titularIdExibicao) {
		this.titularIdExibicao = titularIdExibicao;
	}

	public String getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}

	public DocketWebhookRetornoDocumentoArquivoCampos getCampos() {
		return campos;
	}

	public void setCampos(DocketWebhookRetornoDocumentoArquivoCampos campos) {
		this.campos = campos;
	}

	public List<DocketWebhookRetornoDocumentoArquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<DocketWebhookRetornoDocumentoArquivo> arquivos) {
		this.arquivos = arquivos;
	}

    
    
}
