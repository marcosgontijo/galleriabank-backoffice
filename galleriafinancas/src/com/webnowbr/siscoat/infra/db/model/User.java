package com.webnowbr.siscoat.infra.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    
    private String ip;
    
    private boolean administrador;
    private boolean userPosto;
    private boolean userLocacao;
    private boolean userCobranca;
    
    private boolean userIuguPosto;
    
    private boolean userCobrancaEdita;
    private boolean userCobrancaBaixa;
    private boolean userCobrancaIugu;
    
    private boolean userPreContrato;
    private boolean userPreContratoIUGU;
    private boolean userInvestidor;
    private String codigoResponsavel;

    private Date ultimoAcesso;
    
    private List<String> diasSemana;
    
    private Date horaInicioPermissaoAcesso;   
    private Date horaFimPermissaoAcesso;
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
}
