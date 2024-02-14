package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;

public class DocketConsulta {

	private long id;
	private String idDocket;
	private String cpfCnpj;
	private String status;
	private String uf;
	private String cidade;
	private String pdf;
	private String retorno;
	private DocumentoAnalise documentoAnalise;
	private DocumentosDocket docketDocumentos;
	private User usuario;
	private Date dataConsulta;
	private boolean expirado;
	
	private String estadoId;
	private String cidadeId;
	private String pedidoId;
		
	public DocketConsulta() {
		super();
	}

	public DocketConsulta(DocumentoAnalise documentoAnalise, DocumentosDocket docketDocumentos) {
		super();
		this.documentoAnalise = documentoAnalise;
		populatePagadorRecebedor(documentoAnalise.getPagador());
		this.docketDocumentos = docketDocumentos;
	}
	
	public void popularCampos(DocketConsulta consulta) {
		this.idDocket = consulta.getIdDocket();
		this.status = consulta.getStatus();
		this.uf = consulta.getUf();
		this.cidade = consulta.getCidade();
		this.pdf = consulta.getPdf();
		this.retorno = consulta.getRetorno();
		this.docketDocumentos = consulta.getDocketDocumentos();
		this.usuario = consulta.getUsuario();
		this.dataConsulta = consulta.getDataConsulta();
		this.expirado = consulta.isExpirado();
		this.estadoId = consulta.getEstadoId();
		this.cidadeId = consulta.getCidadeId();
		this.pedidoId = consulta.getPedidoId();
	}

	public void populatePagadorRecebedor(PagadorRecebedor pagador) {
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			cpfCnpj = pagador.getCpf();
		}
		if(!CommonsUtil.semValor(pagador.getCnpj())) {
			cpfCnpj = pagador.getCnpj();
		}
	}

	@Override
	public String toString() {
		return "DocketConsulta [id=" + id + ", cpfCnpj=" + cpfCnpj + ", docketDocumentos=" + docketDocumentos + "]";
	}
	
	public String getNomeCompleto() {
		String nome = docketDocumentos.getDocumentoNome();
		if (!CommonsUtil.semValor(uf)) {
			nome = nome + " " + uf;
			return nome;
		}
		return nome;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public DocumentoAnalise getDocumentoAnalise() {
		return documentoAnalise;
	}

	public void setDocumentoAnalise(DocumentoAnalise documentoAnalise) {
		this.documentoAnalise = documentoAnalise;
	}

	public DocumentosDocket getDocketDocumentos() {
		return docketDocumentos;
	}

	public void setDocketDocumentos(DocumentosDocket docketDocumentos) {
		this.docketDocumentos = docketDocumentos;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public boolean isExpirado() {
		return expirado;
	}

	public void setExpirado(boolean expirado) {
		this.expirado = expirado;
	}

	public String getIdDocket() {
		return idDocket;
	}

	public void setIdDocket(String idDocket) {
		this.idDocket = idDocket;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(String estadoId) {
		this.estadoId = estadoId;
	}

	public String getCidadeId() {
		return cidadeId;
	}

	public void setCidadeId(String cidadeId) {
		this.cidadeId = cidadeId;
	}

	public String getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}	
}
