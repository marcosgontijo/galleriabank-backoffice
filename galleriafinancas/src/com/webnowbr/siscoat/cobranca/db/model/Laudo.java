package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class Laudo implements Serializable {
	
	private long id;
	private ContratoCobranca contratoCobranca;
	private String avaliacaoLaudo;
	private String avaliacaoEquipeLaudo;
	private String avaliacaoLaudoObservacao;
	private Boolean pedidoPreLaudo;
	private Date pedidoPreLaudoData;
	private String pedidoPreLaudoUsuario;
	private BigDecimal valorPreLaudo;
	private Boolean laudoSolicitado;
	private Date laudoSolicitadoData;
	private String laudoSolicitadoUsuario;
	private Boolean laudoRecebido;
	private Date laudoRecebidoData;
	private String laudoRecebidoUsuario;
	
	
	public Laudo() {
		
		super();
		this.contratoCobranca = new ContratoCobranca();
		this.avaliacaoLaudo = null;
		this.avaliacaoEquipeLaudo = null;
		this.avaliacaoLaudoObservacao = null;
		this.pedidoPreLaudo = false;
		this.pedidoPreLaudoData = null;
		this.pedidoPreLaudoUsuario = null;
		this.valorPreLaudo = null;
		this.laudoSolicitado = false;
		this.laudoSolicitadoData = null;
		this.laudoSolicitadoUsuario = null;
		this.laudoRecebido = false;
		this.laudoRecebidoData = null;
		this.laudoRecebidoUsuario = null;
		
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public ContratoCobranca getContratoCobranca() {
		return this.contratoCobranca;
	}
	
	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}
	
	public String getAvaliacaoLaudo() {
		return this.avaliacaoLaudo;
	}
	
	public void setAvaliacaoLaudo(String avaliacaoLaudo) {
		this.avaliacaoLaudo = avaliacaoLaudo;
	}
	
	public String getAvaliacaoEquipeLaudo() {
		return this.avaliacaoEquipeLaudo;
	}
	
	public void setAvaliacaoEquipeLaudo(String avaliacaoEquipeLaudo) {
		this.avaliacaoEquipeLaudo = avaliacaoEquipeLaudo;
	}
	
	public String getAvaliacaoLaudoObservacao() {
		return this.avaliacaoLaudoObservacao;
	}
	
	public void setAvaliacaoLaudoObservacao(String avaliacaoLaudoObservacao) {
		this.avaliacaoLaudoObservacao = avaliacaoLaudoObservacao;
	}
	
	public Boolean getPedidoPreLaudo() {
		return this.pedidoPreLaudo;
	}
	
	public void setPedidoPreLaudo(Boolean pedidoPreLaudo) {
		this.pedidoPreLaudo = pedidoPreLaudo;
	}
	
	public Date getPedidoPreLaudoData() {
		return this.pedidoPreLaudoData;
	}
	
	public void setPedidoPreLaudoData(Date pedidoPreLaudoData) {
		this.pedidoPreLaudoData = pedidoPreLaudoData;
	}
	
	public String getPedidoPreLaudoUsuario() {
		return this.pedidoPreLaudoUsuario;
	}
	
	public void setPedidoPreLaudoUsuario(String pedidoPreLaudoUsuario) {
		this.pedidoPreLaudoUsuario = pedidoPreLaudoUsuario;
	}
	
	public BigDecimal getValorPreLaudo() {
		return this.valorPreLaudo;
	}
	
	public void setValorPreLaudo(BigDecimal valorPreLaudo) {
		this.valorPreLaudo = valorPreLaudo;
	}
	
	public Boolean getLaudoSolicitado() {
		return this.laudoSolicitado;
	}
	
	public void setLaudoSolicitado(Boolean laudoSolicitado) {
		this.laudoSolicitado = laudoSolicitado;
	}
	
	public Date getLaudoSolicitadoData() {
		return this.laudoSolicitadoData;
	}
	
	public void setLaudoSolicitadoData(Date laudoSolicitadoData) {
		this.laudoSolicitadoData = laudoSolicitadoData;
	}
	
	public String getLaudoSolicitadoUsuario() {
		return this.laudoSolicitadoUsuario;
	}
	
	public void setLaudoSolicitadoUsuario(String laudoSolicitadoUsuario) {
		this.laudoSolicitadoUsuario = laudoSolicitadoUsuario;
	}
	
	public Boolean getLaudoRecebido() {
		return this.laudoRecebido;
	}
	
	public void setLaudoRecebido(Boolean laudoRecebido) {
		this.laudoRecebido = laudoRecebido;
	}
	
	public Date getLaudoRecebidoData() {
		return this.laudoRecebidoData;
	}
	
	public void setLaudoRecebidoData(Date laudoRecebidoData) {
		this.laudoRecebidoData = laudoRecebidoData;
	}
	
	public String getLaudoRecebidoUsuario() {
		return this.laudoRecebidoUsuario;
	}
	
	public void setLaudoRecebidoUsuario(String laudoRecebidoUsuario) {
		this.laudoRecebidoUsuario = laudoRecebidoUsuario;
	}
}