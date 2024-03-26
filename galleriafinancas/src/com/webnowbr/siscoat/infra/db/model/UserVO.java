package com.webnowbr.siscoat.infra.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.common.CommonsUtil;

/**
 * Classe de armazenamento de dados de usuario.
 * 
 * @author domingos
 *
 */
public final class UserVO implements Serializable {

	private static final long serialVersionUID = -408744079447543740L;
	/** Chave primaria. */
	private Long id;
	/** Nome do usuario. */
	private String name;

	public UserVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserVO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @see id.
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @see id.
	 * @param idI the id to set
	 */
	public void setId(final Long idI) {
		this.id = idI;
	}

	/**
	 * Get.
	 * 
	 * @return name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set.
	 * 
	 * @param value - name
	 */
	public void setName(final String value) {
		this.name = value;
	}

}
