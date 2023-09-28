package com.webnowbr.siscoat.cobranca.ws.netrin;

import java.util.Date;

import org.apache.commons.lang3.math.Fraction;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;

public class NetrinConsulta {

	private long id;
	private String cpfCnpj;
	private String status;
	private String uf;
	private String cep;
	private String pdf;
	private String retorno;
	private DocumentoAnalise documentoAnalise;
	private NetrinDocumentos netrinDocumentos;
	private User usuario;
	private Date dataConsulta;
	private boolean expirado;
		
	public NetrinConsulta() {
		super();
	}

	public NetrinConsulta(DocumentoAnalise documentoAnalise, NetrinDocumentos netrinDocumentos) {
		super();
		this.documentoAnalise = documentoAnalise;
		populatePagadorRecebedor(documentoAnalise.getPagador());
		this.netrinDocumentos = netrinDocumentos;
	}

	public void populatePagadorRecebedor(PagadorRecebedor pagador) {
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			cpfCnpj = pagador.getCpf();
		}
		if(!CommonsUtil.semValor(pagador.getCnpj())) {
			cpfCnpj = pagador.getCnpj();
		}
		if(!CommonsUtil.semValor(pagador.getCep())) {
			cep = pagador.getCep();
		}
		
	}
	
	@Override
	public String toString() {
		return "NetrinConsulta [id=" + id + ", cpfCnpj=" + cpfCnpj + ", netrinDocumentos=" + netrinDocumentos + "]";
	}
	
	/*@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + (int)id;
	    result = prime * result + (int)id;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    NetrinConsulta other = (NetrinConsulta) obj;
	    if (id != other.id)
	        return false;
	    if (id != other.id)
	        return false;
	    return true;
	}*/

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

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
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

	public NetrinDocumentos getNetrinDocumentos() {
		return netrinDocumentos;
	}

	public void setNetrinDocumentos(NetrinDocumentos netrinDocumentos) {
		this.netrinDocumentos = netrinDocumentos;
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
}
