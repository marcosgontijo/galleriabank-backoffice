package com.webnowbr.siscoat.infra.db.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Classe de armazenamendo da dados de Grupo.
 * @author domingos
 *
 */
public final class Parametros implements Serializable {
    /** serial.     */
	private static final long serialVersionUID = 5083684375625471856L;

	/** Chave primaria. */
    private long id;

    private String nome;
    private String valorString;
    private int valorInt;
    private long valorLong;
    private BigDecimal valorBigDecimal;
    private boolean valorBoolean;
    
    /**
     * Construtor.
     */
    public Parametros() {
    }

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the valorString
	 */
	public String getValorString() {
		return valorString;
	}

	/**
	 * @param valorString the valorString to set
	 */
	public void setValorString(String valorString) {
		this.valorString = valorString;
	}

	/**
	 * @return the valorInt
	 */
	public int getValorInt() {
		return valorInt;
	}

	/**
	 * @param valorInt the valorInt to set
	 */
	public void setValorInt(int valorInt) {
		this.valorInt = valorInt;
	}

	/**
	 * @return the valorLong
	 */
	public long getValorLong() {
		return valorLong;
	}

	/**
	 * @param valorLong the valorLong to set
	 */
	public void setValorLong(long valorLong) {
		this.valorLong = valorLong;
	}

	/**
	 * @return the valorBigDecimal
	 */
	public BigDecimal getValorBigDecimal() {
		return valorBigDecimal;
	}

	/**
	 * @param valorBigDecimal the valorBigDecimal to set
	 */
	public void setValorBigDecimal(BigDecimal valorBigDecimal) {
		this.valorBigDecimal = valorBigDecimal;
	}

	/**
	 * @return the valorBoolean
	 */
	public boolean isValorBoolean() {
		return valorBoolean;
	}

	/**
	 * @param valorBoolean the valorBoolean to set
	 */
	public void setValorBoolean(boolean valorBoolean) {
		this.valorBoolean = valorBoolean;
	}
}
