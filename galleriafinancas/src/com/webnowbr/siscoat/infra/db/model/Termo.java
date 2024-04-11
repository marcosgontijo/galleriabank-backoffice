package com.webnowbr.siscoat.infra.db.model;

import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

public class Termo {

	private long id;

	private String identificacao;

	private String descricao;

	private String instrucao;

	private Integer diasAceite;

	private Date inicioValidade;

	private Date fimValidade;

	private String arquivo;

	private String path;

	private UserPerfil userPerfil;

	private transient TermoUsuario termoUsuario;
	
	private String usuarioCriador;
	
	private String usuarioDelete;
	
	private Date dataDelete;

	private Boolean deletado;


	public boolean isAceiteExpirado() {
		if (CommonsUtil.semValor(termoUsuario))
			return false;
		else if (!CommonsUtil.semValor(termoUsuario.getDataAceite()))
			return false;
		else if (com.webnowbr.siscoat.common.DateUtil.getDifferenceDays(termoUsuario.getDataCiencia(), DateUtil.getDataHoje() ) < CommonsUtil.intValue( this.diasAceite))
			return false;
		else
			return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getInstrucao() {
		return instrucao;
	}

	public void setInstrucao(String instrucao) {
		this.instrucao = instrucao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	
	public Integer getDiasAceite() {
		return diasAceite;
	}

	public void setDiasAceite(Integer diasAceite) {
		this.diasAceite = diasAceite;
	}

	public Date getInicioValidade() {
		return inicioValidade;
	}

	public void setInicioValidade(Date inicioValidade) {
		this.inicioValidade = inicioValidade;
	}

	public Date getFimValidade() {
		return fimValidade;
	}

	public void setFimValidade(Date fimValidade) {
		this.fimValidade = fimValidade;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public UserPerfil getUserPerfil() {
		return userPerfil;
	}

	public void setUserPerfil(UserPerfil userPerfil) {
		this.userPerfil = userPerfil;
	}

	public TermoUsuario getTermoUsuario() {
		return termoUsuario;
	}

	public void setTermoUsuario(TermoUsuario termoUsuario) {
		this.termoUsuario = termoUsuario;
	}

	public String getUsuarioCriador() {
		return usuarioCriador;
	}

	public void setUsuarioCriador(String usuarioCriador) {
		this.usuarioCriador = usuarioCriador;
	}

	public String getUsuarioDelete() {
		return usuarioDelete;
	}

	public void setUsuarioDelete(String usuarioDelete) {
		this.usuarioDelete = usuarioDelete;
	}
	public Date getDataDelete() {
		return dataDelete;
	}

	public void setDataDelete(Date dataDelete) {
		this.dataDelete = dataDelete;
	}

	public Boolean getDeletado() {
		return deletado;
	}

	public void setDeletado(Boolean deletado) {
		this.deletado = deletado;
	}

}
