package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.util.Arrays;
import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.model.User;

public class PlexiConsulta {

	private long id;
	private String requestId;
	private String cpfCnpj;
	private String status;
	private String pdf;
	private String webhookRetorno;
	private PlexiDocumentos plexiDocumentos;
	private User usuario;
	private Date dataConsulta;
	private boolean expirado;
	private String uf;// fazenda MG
	private String cep;// fazenda MG
	private String cpfSolicitante; // fazenda SC PJ
	private String cpf="";// PGE SP DIvida Ativa PF
	private String cnpj="";// PGE SP DIvida Ativa PJ
	private String tipoCertidao;//TJDFT Certidão de Distribuição {criminal, civel, falencia_concordata, especial}
	private String nomeMae;// (TJDFT Certidão de Distribuição PF + criminal ou especial) (TJRS + PF) (TJSP + PF)
	private String comarca;// (TJRJ + primeiraInstancia)
	private String origem;//TJRJ {primeiraInstancia, segundaInstancia}
	private String competencia;//TJRJ
	private String tipo;// (TJRS {3, 9}) (TRFX {civel, criminal, eleitoral})
	private String modelo; // TJSP Certidoes
	private String nome;//TJRS TJSP TRF3 TRT2
	private String endereco;//TJRS
	private String rg;// (TJRS + PF) (TJSP + PF)
	private String orgaoExpedidorRg;//TJRS + PF
	private String ufRg;//TJRS + PF
	private String dataNascimento;// (TJRS + PF) (TJSP + PF)
	private String sexo;// TJSP + PF {m,f}
	private String[] orgaos; // (TRF1) (TRF6)
	private String orgaosStr; // (TRF1) (TRF6)
	private String abrangencia;// TRF3
	private boolean arquivado;// TRT15
		
	public PlexiConsulta() {
		super();
	}

	public PlexiConsulta(PagadorRecebedor pagador, PlexiDocumentos plexiDocumentos) {
		super();
		populatePagadorRecebedor(pagador);
		this.plexiDocumentos = plexiDocumentos;
	}

	public void populatePagadorRecebedor(PagadorRecebedor pagador) {
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			cpf = pagador.getCpf();
		}
		if(!CommonsUtil.semValor(pagador.getCnpj())) {
			cnpj = pagador.getCnpj();
		}
		cpfCnpj = cpf + cnpj;
		
		if(!CommonsUtil.semValor(pagador.getNome())) {
			nome = pagador.getNome();
		}
		if(!CommonsUtil.semValor(pagador.getDtNascimento())) {
			dataNascimento = CommonsUtil.formataData(pagador.getDtNascimento(), "dd/MM/yyyy");
		}
		if(!CommonsUtil.semValor(pagador.getRg())) {
			rg = pagador.getRg();
		}
		if(!CommonsUtil.semValor(pagador.getOrgaoEmissorRG())) {
			orgaoExpedidorRg = pagador.getOrgaoEmissorRG();
		}
		if(!CommonsUtil.semValor(pagador.getCep())) {
			cep = pagador.getCep();
		}
		if(!CommonsUtil.semValor(pagador.getEstado())) {
			ufRg = pagador.getEstado();
		}
		if(!CommonsUtil.semValor(pagador.getEndereco())) {
			endereco = pagador.getEndereco();
		}
		if(!CommonsUtil.semValor(pagador.getSexo())) {
			sexo = CommonsUtil.stringValue(pagador.getSexo().toCharArray()[0]);
		}
		if(!CommonsUtil.semValor(pagador.getNomeMae())) {
			nomeMae = pagador.getNomeMae();
		}
	}
	
	@Override
	public String toString() {
		return "PlexiConsulta [id=" + id + ", requestId=" + requestId + ", cpfCnpj=" + cpfCnpj + ", plexiDocumentos="
				+ plexiDocumentos + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getWebhookRetorno() {
		return webhookRetorno;
	}

	public void setWebhookRetorno(String webhookRetorno) {
		this.webhookRetorno = webhookRetorno;
	}

	public PlexiDocumentos getPlexiDocumentos() {
		return plexiDocumentos;
	}

	public void setPlexiDocumentos(PlexiDocumentos plexiDocumentos) {
		this.plexiDocumentos = plexiDocumentos;
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

	public String getCpfSolicitante() {
		return cpfSolicitante;
	}

	public void setCpfSolicitante(String cpfSolicitante) {
		this.cpfSolicitante = cpfSolicitante;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getTipoCertidao() {
		return tipoCertidao;
	}

	public void setTipoCertidao(String tipoCertidao) {
		this.tipoCertidao = tipoCertidao;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getComarca() {
		return comarca;
	}

	public void setComarca(String comarca) {
		this.comarca = comarca;
	}
	
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getOrgaoExpedidorRg() {
		return orgaoExpedidorRg;
	}

	public void setOrgaoExpedidorRg(String orgaoExpedidorRg) {
		this.orgaoExpedidorRg = orgaoExpedidorRg;
	}

	public String getUfRg() {
		return ufRg;
	}

	public void setUfRg(String ufRg) {
		this.ufRg = ufRg;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String[] getOrgaos() {
		if(!CommonsUtil.semValor(orgaosStr)) {
			orgaos = CommonsUtil.stringToArray(orgaosStr);
		}
		return orgaos;
	}

	public void setOrgaos(String[] orgaos) {
		this.orgaos = orgaos;
	}
	
	public String getOrgaosStr() {
		if(!CommonsUtil.semValor(orgaos)) {
			orgaosStr = Arrays.asList(orgaos).toString();
		}
		return orgaosStr;
	}

	public void setOrgaosStr(String orgaosStr) {
		this.orgaosStr = orgaosStr;
	}

	public String getAbrangencia() {
		return abrangencia;
	}

	public void setAbrangencia(String abrangencia) {
		this.abrangencia = abrangencia;
	}

	public boolean isArquivado() {
		return arquivado;
	}

	public void setArquivado(boolean arquivado) {
		this.arquivado = arquivado;
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
