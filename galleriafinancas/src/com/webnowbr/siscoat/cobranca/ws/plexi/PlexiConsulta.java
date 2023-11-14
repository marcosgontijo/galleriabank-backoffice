package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.util.Arrays;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
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
	protected DocumentoAnalise documentoAnalise;
	protected PlexiDocumentos plexiDocumentos;
	protected User usuario;
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
	private String orgaoExpedidor;//TRF4
	private String ufRg;//TJRS + PF
	private String dataNascimento;// (TJRS + PF) (TJSP + PF)
	private String sexo;// TJSP + PF {m,f}
	private String[] orgaos; // (TRF1) (TRF6)
	private String orgaosStr; // (TRF1) (TRF6)
	private String abrangencia;// TRF3
	private boolean arquivado;// TRT15
	private String email;
	private String senha;
	
		
	public PlexiConsulta() {
		
		super();
	}

	public PlexiConsulta(DocumentoAnalise documentoAnalise, PlexiDocumentos plexiDocumentos) {
		super();
		this.documentoAnalise = documentoAnalise;
		populatePagadorRecebedor(documentoAnalise.getPagador());
		this.plexiDocumentos = plexiDocumentos;
	}
	
	public void popularCampos(PlexiConsulta consulta) {
		this.requestId = consulta.getRequestId();
		this.status = consulta.getStatus();
		this.pdf = consulta.getPdf();
		this.webhookRetorno = consulta.getWebhookRetorno();
		this.usuario = consulta.getUsuario();
		this.dataConsulta = consulta.getDataConsulta();
		this.expirado = consulta.isExpirado();
		this.uf = consulta.getUf();
		this.cpfSolicitante = consulta.getCpfSolicitante();
		this.tipoCertidao = consulta.getTipoCertidao();
		this.comarca = consulta.getComarca();
		this.origem = consulta.getOrigem();
		this.competencia = consulta.getCompetencia();
		this.tipo = consulta.getTipo();
		this.modelo = consulta.getModelo();
		this.orgaos = consulta.getOrgaos();
		this.orgaosStr = consulta.getOrgaosStr();
		this.abrangencia = consulta.getAbrangencia();
		this.arquivado = consulta.isArquivado();
		this.email = consulta.getEmail();
		this.senha = consulta.getSenha();
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
			//nome = pagador.getNome().replace(",", "").replace(".", "").replace("/", "").replace("-", "");
			nome = CommonsUtil.somenteLetrasNumeros(pagador.getNome());
		}
		if(!CommonsUtil.semValor(pagador.getDtNascimento())) {
			dataNascimento = CommonsUtil.formataData(pagador.getDtNascimento(), "dd/MM/yyyy");
		}
		if(!CommonsUtil.semValor(pagador.getRg())) {
			rg = pagador.getRg();
		}
		if(!CommonsUtil.semValor(pagador.getOrgaoEmissorRG())) {
			orgaoExpedidorRg = pagador.getOrgaoEmissorRG();
			orgaoExpedidor = pagador.getOrgaoEmissorRG();
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
	
	public String getNomeCompleto() {
		String nome = plexiDocumentos.getNome();
		PlexiDocumentos doc = plexiDocumentos;
		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/tjdft/certidao-distribuicao")) {
			nome = nome + " (" + tipo + ")";
			//String[] tipoCertidaoArray = {"criminal", "civel"};
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/tjrj/consulta-processual")) {
			nome = nome + "(" + origem + "-" + competencia + ")";
			//String[] origemArray = {"primeiraInstancia", "segundaInstancia"};
			//String[] competenciaArray = {"civel", "criminal", "criminalJuri"};
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/tjrs/certidao-negativa")) {
			nome = nome + " (" + tipo + ")";
			//String[] tipoArray = { "3", "9" };
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/tjsp/certidao-negativa")) {
			nome = nome + " (" + modelo + ")";
			//String[] modeloArray = { "6", "52" };
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/trf1/certidao-distribuicao")) {
			nome = nome + "(" + tipo + "-" + getOrgaosStr() + ")";

			//String[] tipoArray = { "civel", "criminal" };
			//String[][] orgaosArray = {
			//		{ "ac", "am", "ap", "ba", "df", "go", "ma", "mt", "pa", "pi", "ro", "rr", "to", "trf1" },
			//		{ "varasJuizados" }, { "regionalizada" } };
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/trf2/certidao-distribuicao")) {
			nome = nome + " (" + tipo + ")";
			//String[] tipoArray = { "civel", "criminal" };
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/trf3/certidao-distribuicao")) {
			nome = nome + " (" + tipo + "-" + abrangencia + ")";
			//String[] tipoArray = { "civel", "criminal" };
			//String[] abrangenciaArray = { "sjsp", "sjms" };
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/trf4/certidao-regional")) {
			nome = nome + " (" + tipo + ")";
			//String[] tipoArray = { "civil", "criminal" };
			return nome;
		}

		if (CommonsUtil.mesmoValor(doc.getUrl(), "/api/maestro/trf6/certidao-distribuicao")) {
			nome = nome + "(" + tipo + "-" + getOrgaosStr() + ")";
			//String[] tipoArray = { "civel", "criminal" };
			//String[][] orgaosArray = { { "mg", "trf1" } };
			return nome;
		}
		return nome;
	}
	
	public boolean verificaCamposDoc() {
		PlexiConsulta plexiConsulta = this;
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		boolean retorno = true;
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			if(CommonsUtil.semValor(plexiConsulta.getEndereco())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Endereço", ""));
			}
			
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getOrgaoExpedidorRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Orgao Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getUfRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta UF Rg", ""));
				} else if(plexiConsulta.getUfRg().toCharArray().length > 2) {
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - UF Rg Inválido", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Nome da Mãe", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Data Nascimento", ""));
				}
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {	
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Data Nascimento", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getSexo())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Sexo", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Nome Mãe", ""));
				}
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf4/certidao-regional")) {
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {			
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getOrgaoExpedidor())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Orgao Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Nome da Mãe", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Data Nascimento", ""));
				}
			}
		}
		
		/*
		 * if(CommonsUtil.mesmoValor(doc.getUrl(),
		 * "/api/maestro/fazenda-mg/certidao-debitos-tributarios")) {
		 * if(CommonsUtil.semValor(plexiConsulta.getCep())){ retorno = false;
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() +
		 * " - Falta Cep",""));}} if(CommonsUtil.mesmoValor(doc.getUrl(),
		 * "/api/maestro/fazenda-mg/certidao-debitos-tributarios")) {
		 * if(CommonsUtil.semValor(plexiConsulta.getCep())){ retorno = false;
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Cep",
		 * "")); } }
		 * 
		 * if(CommonsUtil.mesmoValor(doc.getUrl(),
		 * "/api/maestro/fazenda-sc/certidao-negativa-debitos")) {
		 * if(!CommonsUtil.semValor(plexiConsulta.getCnpj())) {
		 * if(CommonsUtil.semValor(plexiConsulta.getCpfSolicitante())){ retorno = false;
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() +
		 * " - Falta CPF Solicitante", "")); } } }
		 */

		return retorno;
	}
	
	public boolean verificaCamposDocSemMsg() {
		PlexiConsulta plexiConsulta = this;
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		boolean retorno = true;
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			if(CommonsUtil.semValor(plexiConsulta.getEndereco())){
				retorno = false;
			}
			
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getOrgaoExpedidorRg())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getUfRg())){
					retorno = false;
				} else if(plexiConsulta.getUfRg().toCharArray().length > 2) {
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
				}
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {	
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getSexo())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
				}
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf4/certidao-regional")) {
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {			
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getOrgaoExpedidor())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
				}
			}
		}

		return retorno;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getOrgaoExpedidor() {
		return orgaoExpedidor;
	}

	public void setOrgaoExpedidor(String orgaoExpedidor) {
		this.orgaoExpedidor = orgaoExpedidor;
	}

	public DocumentoAnalise getDocumentoAnalise() {
		return documentoAnalise;
	}

	public void setDocumentoAnalise(DocumentoAnalise documentoAnalise) {
		this.documentoAnalise = documentoAnalise;
	}
}
