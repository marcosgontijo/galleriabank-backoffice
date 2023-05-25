package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoExecutionResult;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoRequestFields;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaCompleta;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaQuodScore;
import br.com.galleriabank.serasacrednet.cliente.util.GsonUtil;

public class DocumentoAnalise implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4489431101607924990L;

	private long id;
	private String idRemoto;

	private DataEngine engine;
	
	private ContratoCobranca contratoCobranca;

	private PagadorRecebedor pagador; //titulares pra enviar pedido
	
	private String identificacao;
	private String cnpjcpf;
	private String tipoPessoa;
	private String motivoAnalise;
	private String path;
	private String tipo;
	private boolean liberadoAnalise;
	private boolean liberadoSerasa;
	private boolean liberadoCenprot;

	private DocumentosAnaliseEnum tipoEnum;

	private String retorno;
	private String retornoEngine;
	private String retornoSerasa;
	private String retornoCenprot;
	private String observacao;
	
	public List<DocumentoAnaliseResumo> getResumoEngine() {
		List<DocumentoAnaliseResumo> result = new ArrayList<>();  
		EngineRetorno engine = GsonUtil.fromJson(getRetornoEngine(), EngineRetorno.class);
		DocumentoAnaliseResumo documento =  new DocumentoAnaliseResumo();
		DocumentoAnaliseResumo documento2 = new DocumentoAnaliseResumo();
		DocumentoAnaliseResumo documento3 = new DocumentoAnaliseResumo();
		if(CommonsUtil.mesmoValor( tipoPessoa , "PF")) {
		EngineRetornoExecutionResultConsultaQuodScore score = engine.getConsultaCompleta().getQuodScore();
		EngineRetornoRequestFields nome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome")).findFirst().orElse(null);
		EngineRetornoRequestFields cpf = engine.getRequestFields().stream().filter(g-> g.getField().equals("cpf")).findFirst().orElse(null);
		documento.setDescricao("nome:");
		documento.setValor(nome.getValue());
		documento2.setDescricao("cpf:");
		documento2.setValor(cpf.getValue());
		documento3.setDescricao("Score serasa:");
		documento3.setNumero(score.getScore());
		
		} else if(CommonsUtil.mesmoValor( tipoPessoa , "PJ")) {
			EngineRetornoRequestFields nome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome")).findFirst().orElse(null);
			EngineRetornoRequestFields cnpj = engine.getRequestFields().stream().filter(s -> s.getField().equals("cnpj")).findFirst().orElse(null);
			documento.setDescricao("nome:");
			documento.setValor(nome.getValue());
			documento2.setDescricao("cnpj:");
			documento2.setValor(cnpj.getValue());
		}
		
		
		

		
		result.add(documento);
		result.add(documento2);
		result.add(documento3);
		return result;
		
	}
	

	public boolean isPodeChamarRea() {
		return isReaNaoEnviado() && CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isReaNaoEnviado() {
		return CommonsUtil.semValor(idRemoto);
	}

	public boolean isReaProcessado() {
		return !CommonsUtil.semValor(retorno);
	}
	
	public boolean isPodeChamarEngine() {
		return  !isEngineProcessado() && (CommonsUtil.mesmoValor("PJ", tipoPessoa) || CommonsUtil.mesmoValor("PF", tipoPessoa));
	}

	public boolean isEngineProcessado() {
		return !CommonsUtil.semValor(engine) && !CommonsUtil.semValor(engine.getIdCallManager());
	}	
	
	public boolean isPodeChamarSerasa() {
		return isEngineProcessado() && !isSerasaProcessado() && CommonsUtil.mesmoValor("PF", tipoPessoa); // (CommonsUtil.mesmoValor("PJ", tipoPessoa) ||
	}

	public boolean isSerasaProcessado() {
		return !CommonsUtil.semValor(retornoSerasa);
	}	
	
	public boolean isPodeChamarCenprot() {
		return isEngineProcessado() && !isCenprotProcessado();
	}
	
	public boolean isCenprotProcessado() {
		return !CommonsUtil.semValor(retornoCenprot);
	}
	
	
	
	public void addObservacao(String observacao) {
		
		if (this.observacao == null) {
			this.observacao = "";
		}else {
			this.observacao = this.observacao + " - ";
		}

		this.observacao = observacao;
	}

			
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdRemoto() {
		return idRemoto;
	}

	public void setIdRemoto(String idRemoto) {
		this.idRemoto = idRemoto;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
		switch (tipo) {
		case "Rea":
			this.tipoEnum = DocumentosAnaliseEnum.REA;
			break;
		case "Relato":
			this.tipoEnum = DocumentosAnaliseEnum.RELATO;

			break;
		case "Crednet":
			this.tipoEnum = DocumentosAnaliseEnum.CREDNET;
			break;
		}
	}

	public boolean isLiberadoAnalise() {
		return liberadoAnalise;
	}

	public void setLiberadoAnalise(boolean liberadoAnalise) {
		this.liberadoAnalise = liberadoAnalise;
	}

	public DocumentosAnaliseEnum getTipoEnum() {
		return tipoEnum;
	}

	public void setTipoEnum(DocumentosAnaliseEnum tipoEnum) {
		this.tipoEnum = tipoEnum;
		this.tipo = tipoEnum.getNome();
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public String getRetornoEngine() {
		return retornoEngine;
	}

	public void setRetornoEngine(String retornoEngine) {
		this.retornoEngine = retornoEngine;
	}

	public String getRetornoCenprot() {
		return retornoCenprot;
	}

	public void setRetornoCenprot(String retornoCenprot) {
		this.retornoCenprot = retornoCenprot;
	}

	public String getRetornoSerasa() {
		return retornoSerasa;
	}

	public void setRetornoSerasa(String retornoSerasa) {
		this.retornoSerasa = retornoSerasa;
	}

	public String getCnpjcpf() {
		return cnpjcpf;
	}

	public void setCnpjcpf(String cnpjcpf) {
		this.cnpjcpf = cnpjcpf;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getMotivoAnalise() {
		return motivoAnalise;
	}

	public DataEngine getEngine() {
		return engine;
	}

	public void setEngine(DataEngine engine) {
		this.engine = engine;
	}

	public void setMotivoAnalise(String motivoAnalise) {
		this.motivoAnalise = motivoAnalise;
	}

	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public boolean isLiberadoSerasa() {
		return liberadoSerasa;
	}

	public void setLiberadoSerasa(boolean liberadoSerasa) {
		this.liberadoSerasa = liberadoSerasa;
	}

	public boolean isLiberadoCenprot() {
		return liberadoCenprot;
	}

	public void setLiberadoCenprot(boolean liberadoCenprot) {
		this.liberadoCenprot = liberadoCenprot;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
