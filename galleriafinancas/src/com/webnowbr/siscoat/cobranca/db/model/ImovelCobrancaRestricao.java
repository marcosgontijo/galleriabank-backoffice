package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.db.op.CidadeDao;
import com.webnowbr.siscoat.common.CommonsUtil;

public class ImovelCobrancaRestricao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6665021772933498042L;

	private long id;
	private ContratoCobranca contratoCobranca;
	private String numeroMatricula;
	private String numeroCartorio;
	private String cartorioEstado;
	private String cartorioMunicipio;

	private Date dataCadastro;
	private String usuarioCadastro;

	private boolean ativa;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getNumeroMatricula() {
		return numeroMatricula;
	}

	public void setNumeroMatricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}

	public String getNumeroCartorio() {
		return numeroCartorio;
	}

	public void setNumeroCartorio(String numeroCartorio) {
		this.numeroCartorio = numeroCartorio;
	}

	public String getCartorioEstado() {
		return cartorioEstado;
	}

	public void setCartorioEstado(String cartorioEstado) {
		this.cartorioEstado = cartorioEstado;
	}

	public String getCartorioMunicipio() {
		return cartorioMunicipio;
	}

	public void setCartorioMunicipio(String cartorioMunicipio) {
		this.cartorioMunicipio = cartorioMunicipio;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(String usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

}
