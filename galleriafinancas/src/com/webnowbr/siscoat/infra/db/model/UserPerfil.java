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
public final class UserPerfil implements Serializable {
    /** serial. */
    private static final long serialVersionUID = -408744079447543740L;
    /** Chave primaria. */
    private long id;
    /** Nome do perfil. */
    private String perfil;
  
  
    /**
     * Construtor.
     */
    public UserPerfil() {
    }


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getPerfil() {
		return perfil;
	}


	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

   
}
