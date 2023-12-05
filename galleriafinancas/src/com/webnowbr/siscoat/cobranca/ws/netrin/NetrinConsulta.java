package com.webnowbr.siscoat.cobranca.ws.netrin;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.netrin.cliente.model.cndestadual.CndEstadualResponse;
import br.com.galleriabank.netrin.cliente.model.cndfederal.CndFederalResponse;
import br.com.galleriabank.netrin.cliente.model.cndttst.CndTrabalhistaTSTResponse;
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
		if (!CommonsUtil.semValor(pagador.getCpf())) {
			cpfCnpj = pagador.getCpf();
		}
		if (!CommonsUtil.semValor(pagador.getCnpj())) {
			cpfCnpj = pagador.getCnpj();
		}
		if (!CommonsUtil.semValor(pagador.getCep())) {
			cep = pagador.getCep();
		}

	}
	/*
	 * public INetrinCndResponse getNetrinResponse() { if
	 * (CommonsUtil.mesmoValor(this.netrinDocumentos.getId(),
	 * SiscoatConstants.NETRIN_CND_FEDERAL)) return GsonUtil.fromJson(this.retorno,
	 * CndFederalCndResponse.class); else if
	 * (CommonsUtil.mesmoValor(this.netrinDocumentos.getId(),
	 * SiscoatConstants.NETRIN_CND_ESTADUAL)) return GsonUtil.fromJson(this.retorno,
	 * CndFederalCndResponse.class); else if
	 * (CommonsUtil.mesmoValor(this.netrinDocumentos.getId(),
	 * SiscoatConstants.NETRIN_CND_TRABALHISTA)) return
	 * GsonUtil.fromJson(this.retorno, CndFederalCndResponse.class); else return
	 * null; }
	 */

	public String getStatusConsulta() {
		if (CommonsUtil.mesmoValor(this.netrinDocumentos.getId(), SiscoatConstants.NETRIN_CND_FEDERAL)) {
			CndFederalResponse response = GsonUtil.fromJson(this.retorno, CndFederalResponse.class);
			if(CommonsUtil.semValor( response.getReceitaFederalCND().getDebitosPendentesPGFN() ) &&
					CommonsUtil.semValor( response.getReceitaFederalCND().getDebitosPendentesRFB() ))
				return SiscoatConstants.CND_SITUACAO_ERRO_AO_CONSULTAR;
			else if (CommonsUtil.mesmoValor("Não", response.getReceitaFederalCND().getDebitosPendentesPGFN())
					&& CommonsUtil.mesmoValor("Não", response.getReceitaFederalCND().getDebitosPendentesRFB())) {
				return SiscoatConstants.CND_SITUACAO_NAO_POSSUI_DEBITOS;
			} else
				return SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS;
		} else if (CommonsUtil.mesmoValor(this.netrinDocumentos.getId(), SiscoatConstants.NETRIN_CND_ESTADUAL)) {
			CndEstadualResponse response = GsonUtil.fromJson(this.retorno, CndEstadualResponse.class);
			if (!CommonsUtil.mesmoValor("Não", response.getSefazCND().getEmitiuCertidao())) {
				return SiscoatConstants.CND_SITUACAO_NAO_POSSUI_DEBITOS;
			} else
				return SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS;
		} else if (CommonsUtil.mesmoValor(this.netrinDocumentos.getId(), SiscoatConstants.NETRIN_CND_TRABALHISTA)) {
			CndTrabalhistaTSTResponse response = GsonUtil.fromJson(this.retorno, CndTrabalhistaTSTResponse.class);
			if (!CommonsUtil.mesmoValor("Não", response.getTribunalSuperiorTrabalhoCNDT().getEmitiuCertidao())) {
				return SiscoatConstants.CND_SITUACAO_NAO_POSSUI_DEBITOS;
			} else
				return SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS;
		} else
			return null;
	}

	public void popularCampos(NetrinConsulta consulta) {
		this.status = consulta.getStatus();
		this.uf = consulta.getUf();
		this.pdf = consulta.getPdf();
		this.retorno = consulta.getRetorno();
		this.usuario = consulta.getUsuario();
		this.dataConsulta = consulta.getDataConsulta();
	}

	@Override
	public String toString() {
		return "NetrinConsulta [id=" + id + ", cpfCnpj=" + cpfCnpj + ", netrinDocumentos=" + netrinDocumentos + "]";
	}

	public boolean verificaCamposDoc() {
		NetrinConsulta netrinConsulta = this;
		NetrinDocumentos doc = netrinConsulta.getNetrinDocumentos();
		boolean retorno = true;

		if (CommonsUtil.mesmoValor(doc.getUrlService(), "/api/v1/CNDEstadual")
				&& CommonsUtil.mesmoValor(netrinConsulta.getUf().toUpperCase().trim(), "MG")) {
			if (CommonsUtil.semValor(netrinConsulta.getCep())) {
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Cep p/ MG", ""));
			}
		}
		return retorno;
	}

	public String getNomeCompleto() {
		String nome = netrinDocumentos.getNome();
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
