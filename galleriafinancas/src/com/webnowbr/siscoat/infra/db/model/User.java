package com.webnowbr.siscoat.infra.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;

/**
 * Classe de armazenamento de dados de usuario.
 * @author domingos
 *
 */
public final class User implements Serializable {
    /** serial. */
    private static final long serialVersionUID = -408744079447543740L;
    /** Chave primaria. */
    private long id;
    /** Nome do usuario. */
    private String name;
    /** Nivel de operacao. */
    private int level;
    /** Nome de login do usuario. */
    private String login;
    /** Senha. */
    private String password;
    /** Informacoes adicionais. */
    private String addInfo;
    
    /** Informacoes adicionais. */
    private String path;    
    /** Grupos ao qual o usuario esta associado. */
    private List<GroupAdm> groupList;
    
    private List<Responsavel> listResponsavel;
    
    private String ip;
    
    private boolean administrador;
    private boolean userPosto;
    private boolean userLocacao;
    private boolean userCobranca;
    
    private boolean userIuguPosto;
    
    private boolean userCobrancaEdita;
    private boolean userCobrancaBaixa;
    private boolean userCobrancaIugu;
    private boolean userCobrancaLead;
    
    private boolean userCobrancaFinanceiro;
    
    private boolean blockBackoffice;
        
    private boolean userPreContrato;
    private boolean userPreContratoIUGU;
    private boolean userPreContratoAnalista;
    private boolean userInvestidor;
    
    private boolean comiteEditar;
    private boolean comiteConsultar;
    
    private boolean assistFinanceiro;
    private boolean userPosOperacao;
    
    private boolean userAvaliadorImovel;
    private boolean userLaudo;
    private boolean userGalache;
    private boolean userAgenteEspelhamento;
   
    private boolean profileMarketing;
        
    private String codigoResponsavel;    

    private Date ultimoAcesso;
    
    private List<String> diasSemana;
    
    private Date horaInicioPermissaoAcesso;   
    private Date horaFimPermissaoAcesso;
    
    private String key;
    private String urlQRCode;
    
    private boolean twoFactorAuth;
    
    private boolean profileAnalistaCredito;
    private boolean profileAnalistaComite;
    private boolean profileAnalistaPosComite;
    private boolean profileGerenteAnalise;
    private boolean profileContrato;
    private boolean profileCobranca;
    private boolean profileComentarioJuridico;
    private boolean profileAvaliadorImovel;
    private boolean profileAvaliadorImovelCompass;
    private boolean profileAvaliadorImovelGalache;
    private boolean profileLaudo; 
    
    /**
     * Construtor.
     */
    public User() {
    }

    /**
     * @see id.
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @see id.
     * @param idI the id to set
     */
    public void setId(final long idI) {
        this.id = idI;
    }

    /**
     * Get.
     * @return lista de grupos.
     */
    public List<GroupAdm> getGroupList() {
        return groupList;
    }

    /**
     * Set.
     * @param value - grupos
     */
    public void setGroupList(final List<GroupAdm> value) {
        this.groupList = value;
    }

    /**
     * Get.
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set.
     * @param value - name
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Get.
     * @return level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set.
     * @param value - level
     */
    public void setLevel(final int value) {
        this.level = value;
    }

    /**
     * Get.
     * @return login.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Set.
     * @param value - login
     */
    public void setLogin(final String value) {
        this.login = value;
    }

    /**
     * Get.
     * @return password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set.
     * @param value - password.
     */
    public void setPassword(final String value) {
        this.password = value;
    }

    /**
     * Get.
     * @return addInfo.
     */
    public String getAddInfo() {
        return addInfo;
    }

    /**
     * Set.
     * @param value - addInfo
     */
    public void setAddInfo(final String value) {
        this.addInfo = value;
    }

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the administrador
	 */
	public boolean isAdministrador() {
		return administrador;
	}

	/**
	 * @param administrador the administrador to set
	 */
	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	/**
	 * @return the userPosto
	 */
	public boolean isUserPosto() {
		return userPosto;
	}

	/**
	 * @param userPosto the userPosto to set
	 */
	public void setUserPosto(boolean userPosto) {
		this.userPosto = userPosto;
	}

	/**
	 * @return the userLocacao
	 */
	public boolean isUserLocacao() {
		return userLocacao;
	}

	/**
	 * @param userLocacao the userLocacao to set
	 */
	public void setUserLocacao(boolean userLocacao) {
		this.userLocacao = userLocacao;
	}

	/**
	 * @return the userCobranca
	 */
	public boolean isUserCobranca() {
		return userCobranca;
	}

	/**
	 * @param userCobranca the userCobranca to set
	 */
	public void setUserCobranca(boolean userCobranca) {
		this.userCobranca = userCobranca;
	}

	/**
	 * @return the userCobrancaEdita
	 */
	public boolean isUserCobrancaEdita() {
		return userCobrancaEdita;
	}

	/**
	 * @param userCobrancaEdita the userCobrancaEdita to set
	 */
	public void setUserCobrancaEdita(boolean userCobrancaEdita) {
		this.userCobrancaEdita = userCobrancaEdita;
	}

	/**
	 * @return the userCobrancaBaixa
	 */
	public boolean isUserCobrancaBaixa() {
		return userCobrancaBaixa;
	}

	/**
	 * @param userCobrancaBaixa the userCobrancaBaixa to set
	 */
	public void setUserCobrancaBaixa(boolean userCobrancaBaixa) {
		this.userCobrancaBaixa = userCobrancaBaixa;
	}

	/**
	 * @return the userPreContrato
	 */
	public boolean isUserPreContrato() {
		return userPreContrato;
	}

	/**
	 * @param userPreContrato the userPreContrato to set
	 */
	public void setUserPreContrato(boolean userPreContrato) {
		this.userPreContrato = userPreContrato;
	}

	/**
	 * @return the codigoResponsavel
	 */
	public String getCodigoResponsavel() {
		return codigoResponsavel;
	}

	/**
	 * @param codigoResponsavel the codigoResponsavel to set
	 */
	public void setCodigoResponsavel(String codigoResponsavel) {
		this.codigoResponsavel = codigoResponsavel;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the ultimoAcesso
	 */
	public Date getUltimoAcesso() {
		return ultimoAcesso;
	}

	/**
	 * @param ultimoAcesso the ultimoAcesso to set
	 */
	public void setUltimoAcesso(Date ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	public boolean isUserIuguPosto() {
		return userIuguPosto;
	}

	public void setUserIuguPosto(boolean userIuguPosto) {
		this.userIuguPosto = userIuguPosto;
	}

	public List<String> getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(List<String> diasSemana) {
		this.diasSemana = diasSemana;
	}

	public Date getHoraInicioPermissaoAcesso() {
		return horaInicioPermissaoAcesso;
	}

	public void setHoraInicioPermissaoAcesso(Date horaInicioPermissaoAcesso) {
		this.horaInicioPermissaoAcesso = horaInicioPermissaoAcesso;
	}

	public Date getHoraFimPermissaoAcesso() {
		return horaFimPermissaoAcesso;
	}

	public void setHoraFimPermissaoAcesso(Date horaFimPermissaoAcesso) {
		this.horaFimPermissaoAcesso = horaFimPermissaoAcesso;
	}

	public boolean isUserCobrancaIugu() {
		return userCobrancaIugu;
	}

	public void setUserCobrancaIugu(boolean userCobrancaIugu) {
		this.userCobrancaIugu = userCobrancaIugu;
	}

	public boolean isUserInvestidor() {
		return userInvestidor;
	}

	public void setUserInvestidor(boolean userInvestidor) {
		this.userInvestidor = userInvestidor;
	}

	public boolean isUserPreContratoIUGU() {
		return userPreContratoIUGU;
	}

	public void setUserPreContratoIUGU(boolean userPreContratoIUGU) {
		this.userPreContratoIUGU = userPreContratoIUGU;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrlQRCode() {
		return urlQRCode;
	}

	public void setUrlQRCode(String urlQRCode) {
		this.urlQRCode = urlQRCode;
	}

	public boolean isTwoFactorAuth() {
		return twoFactorAuth;
	}

	public void setTwoFactorAuth(boolean twoFactorAuth) {
		this.twoFactorAuth = twoFactorAuth;
	}

	public boolean isUserPreContratoAnalista() {
		return userPreContratoAnalista;
	}

	public void setUserPreContratoAnalista(boolean userPreContratoAnalista) {
		this.userPreContratoAnalista = userPreContratoAnalista;
	}

	public List<Responsavel> getListResponsavel() {
		return listResponsavel;
	}

	public void setListResponsavel(List<Responsavel> listResponsavel) {
		this.listResponsavel = listResponsavel;
	}

	public boolean isUserCobrancaLead() {
		return userCobrancaLead;
	}

	public void setUserCobrancaLead(boolean userCobrancaLead) {
		this.userCobrancaLead = userCobrancaLead;
	}

	public boolean isAssistFinanceiro() {
		return assistFinanceiro;
	}

	public void setAssistFinanceiro(boolean assistFinanceiro) {
		this.assistFinanceiro = assistFinanceiro;
	}

	public boolean isComiteEditar() {
		return comiteEditar;
	}

	public void setComiteEditar(boolean comiteEditar) {
		this.comiteEditar = comiteEditar;
	}

	public boolean isComiteConsultar() {
		return comiteConsultar;
	}

	public void setComiteConsultar(boolean comiteConsultar) {
		this.comiteConsultar = comiteConsultar;
	}

	public boolean isUserAvaliadorImovel() {
		return userAvaliadorImovel;
	}

	public void setUserAvaliadorImovel(boolean userAvaliadorImovel) {
		this.userAvaliadorImovel = userAvaliadorImovel;
	}

	public boolean isUserLaudo() {
		return userLaudo;
	}

	public void setUserLaudo(boolean userLaudo) {
		this.userLaudo = userLaudo;
	}

	public boolean isUserAgenteEspelhamento() {
		return userAgenteEspelhamento;
	}

	public void setUserAgenteEspelhamento(boolean userAgenteEspelhamento) {
		this.userAgenteEspelhamento = userAgenteEspelhamento;
	}

	public boolean isUserGalache() {
		return userGalache;
	}

	public void setUserGalache(boolean userGalache) {
		this.userGalache = userGalache;
	}

	public boolean isBlockBackoffice() {
		return blockBackoffice;
	}

	public void setBlockBackoffice(boolean blockBackoffice) {
		this.blockBackoffice = blockBackoffice;
	}

	public boolean isUserCobrancaFinanceiro() {
		return userCobrancaFinanceiro;
	}

	public void setUserCobrancaFinanceiro(boolean userCobrancaFinanceiro) {
		this.userCobrancaFinanceiro = userCobrancaFinanceiro;
	}

	public boolean isUserPosOperacao() {
		return userPosOperacao;
	}

	public void setUserPosOperacao(boolean userPosOperacao) {
		this.userPosOperacao = userPosOperacao;
	}

	public boolean isProfileAnalistaCredito() {
		return profileAnalistaCredito;
	}

	public void setProfileAnalistaCredito(boolean profileAnalistaCredito) {
		this.profileAnalistaCredito = profileAnalistaCredito;
	}

	public boolean isProfileAnalistaComite() {
		return profileAnalistaComite;
	}

	public void setProfileAnalistaComite(boolean profileAnalistaComite) {
		this.profileAnalistaComite = profileAnalistaComite;
	}

	public boolean isProfileAnalistaPosComite() {
		return profileAnalistaPosComite;
	}

	public void setProfileAnalistaPosComite(boolean profileAnalistaPosComite) {
		this.profileAnalistaPosComite = profileAnalistaPosComite;
	}

	public boolean isProfileGerenteAnalise() {
		return profileGerenteAnalise;
	}

	public void setProfileGerenteAnalise(boolean profileGerenteAnalise) {
		this.profileGerenteAnalise = profileGerenteAnalise;
	}

	public boolean isProfileContrato() {
		return profileContrato;
	}

	public void setProfileContrato(boolean profileContrato) {
		this.profileContrato = profileContrato;
	}

	public boolean isProfileComentarioJuridico() {
		return profileComentarioJuridico;
	}

	public void setProfileComentarioJuridico(boolean profileComentarioJuridico) {
		this.profileComentarioJuridico = profileComentarioJuridico;
	}

	public boolean isProfileAvaliadorImovel() {
		return profileAvaliadorImovel;
	}

	public void setProfileAvaliadorImovel(boolean profileAvaliadorImovel) {
		this.profileAvaliadorImovel = profileAvaliadorImovel;
	}

	public boolean isProfileAvaliadorImovelCompass() {
		return profileAvaliadorImovelCompass;
	}

	public void setProfileAvaliadorImovelCompass(boolean profileAvaliadorImovelCompass) {
		this.profileAvaliadorImovelCompass = profileAvaliadorImovelCompass;
	}

	public boolean isProfileAvaliadorImovelGalache() {
		return profileAvaliadorImovelGalache;
	}

	public void setProfileAvaliadorImovelGalache(boolean profileAvaliadorImovelGalache) {
		this.profileAvaliadorImovelGalache = profileAvaliadorImovelGalache;
	}

	public boolean isProfileLaudo() {
		return profileLaudo;
	}

	public void setProfileLaudo(boolean profileLaudo) {
		this.profileLaudo = profileLaudo;
	}

	public boolean isProfileCobranca() {
		return profileCobranca;
	}

	public void setProfileCobranca(boolean profileCobranca) {
		this.profileCobranca = profileCobranca;
	}

	public boolean isProfileMarketing() {
		return profileMarketing;
	}

	public void setProfileMarketing(boolean profileMarketing) {
		this.profileMarketing = profileMarketing;
	}
}
