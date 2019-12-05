package com.webnowbr.siscoat.infra.db.model;

import java.io.Serializable;
import java.util.List;

/**
 * Classe de armazenamendo da dados de Grupo.
 * @author domingos
 *
 */
public final class GroupAdm implements Serializable {
    /** serial.     */
    private static final long serialVersionUID = -8035213123920757805L;
    /** Chave primaria. */
    private long id;
    /** Sigla do grupo. */
    private String acronym;
    /** Nome do grupo. */
    private String name;
    /** Informacao adicional. */
    private String addInfo;
    private List<User> userList;
    private int degree;
    private boolean enabled;
    
    /**
     * Construtor.
     */
    public GroupAdm() {
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
     * @return sigla.
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * Set.
     * @param value - sigla.
     */
    public void setAcronym(final String value) {
        this.acronym = value;
    }

    /**
     * Get.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set.
     * @param value - name.
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Get.
     * @return addInfo
     */
    public String getAddInfo() {
        return addInfo;
    }

    /**
     * Set.
     * @param value - addInfo.
     */
    public void setAddInfo(final String value) {
        this.addInfo = value;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

	/**
	 * @return the degree
	 */
	public int getDegree() {
		return degree;
	}

	/**
	 * @param degree the degree to set
	 */
	public void setDegree(int degree) {
		this.degree = degree;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
