package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoRequestFields;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminaisEvidences;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaQuodScore;
import br.com.galleriabank.dataengine.cliente.model.retorno.processos.EngineRetornoExecutionResultProcessos;


import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoExecutionResult;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoRequestFields;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminais;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminaisEvidences;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminaisResult;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaCompleta;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaQuodScore;
import br.com.galleriabank.dataengine.cliente.model.retorno.processos.EngineRetornoExecutionResultProcessos;
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
	private boolean liberadoScr;
	
	private DocumentosAnaliseEnum tipoEnum;

	private String retorno;
	private String retornoEngine;
	private String retornoSerasa;
	private String retornoCenprot;
	private String retornoScr;
	private String observacao;
	
	public List<DocumentoAnaliseResumo> getResumoEngine() {
		List<DocumentoAnaliseResumo> result = new ArrayList<>();  
		EngineRetorno engine = GsonUtil.fromJson(getRetornoEngine(), EngineRetorno.class);
		DocumentoAnaliseResumo documento =  new DocumentoAnaliseResumo();
		DocumentoAnaliseResumo documento2 = new DocumentoAnaliseResumo();
		DocumentoAnaliseResumo documento3 = new DocumentoAnaliseResumo();
		DocumentoAnaliseResumo documento4 = new DocumentoAnaliseResumo();
		DocumentoAnaliseResumo documento5 = new DocumentoAnaliseResumo();
		if(CommonsUtil.mesmoValor( tipoPessoa , "PF")) {
		
			
			
		EngineRetornoExecutionResultConsultaQuodScore score = engine.getConsultaCompleta().getQuodScore();
		EngineRetornoRequestFields nome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome")).findFirst().orElse(null);
		EngineRetornoRequestFields cpf = engine.getRequestFields().stream().filter(g-> g.getField().equals("cpf")).findFirst().orElse(null);
		documento.setDescricao("nome:");
		documento.setValor(nome.getValue());
		documento2.setDescricao("cpf:");
		documento2.setValor(cpf.getValue());
		documento3.setDescricao("Score serasa:");
		documento3.setValor(CommonsUtil.stringValue(score.getScore()));
		if(engine.getConsultaAntecedenteCriminais() == null) {
			documento4.setDescricao("antecedentes criminais:");
			documento4.setValor("não disponível");
		}else {
		EngineRetornoExecutionResultAntecedenteCriminaisEvidences mensagem = engine.getConsultaAntecedenteCriminais().getEvidences();
		documento4.setDescricao("antecedentes criminais:");
		documento4.setValor(mensagem.getMessage());
		}
		if(engine.getProcessos() == null) {
			documento5.setDescricao("numero  de processos:");
			documento5.setValor("Não disponível");
			
			
		} else {
		EngineRetornoExecutionResultProcessos processo = engine.getProcessos();
		documento5.setDescricao("numero de processos:");
		documento5.setValor(CommonsUtil.stringValue(processo.getTotal_acoes_judiciais()));
		}
		
		
		} else if(CommonsUtil.mesmoValor( tipoPessoa , "PJ")) {
			EngineRetornoRequestFields nome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome")).findFirst().orElse(null);
			EngineRetornoRequestFields cnpj = engine.getRequestFields().stream().filter(s -> s.getField().equals("cnpj")).findFirst().orElse(null);
			documento.setDescricao("nome:");
			documento.setValor(nome.getValue());
			documento2.setDescricao("cnpj:");
			documento2.setValor(cnpj.getValue());
			if(engine.getConsultaCompleta() == null) {
				documento3.setDescricao("score serasa:");
				documento3.setValor("não disponivel");
			} else {
				EngineRetornoExecutionResultConsultaQuodScore score = engine.getConsultaCompleta().getQuodScore();
				documento3.setDescricao("score serasa:");
				documento3.setValor(CommonsUtil.stringValue(score.getScore()));
			}
			if(engine.getConsultaAntecedenteCriminais() == null) {
				documento4.setDescricao("antecedentes criminais:");
				documento4.setValor("não disponível");
			}else {
			EngineRetornoExecutionResultAntecedenteCriminaisEvidences mensagem = engine.getConsultaAntecedenteCriminais().getEvidences();
			documento4.setDescricao("antecedentes criminais:");
			documento4.setValor(mensagem.getMessage());
			
			if(engine.getProcessos() == null) {
				documento5.setDescricao("numero  de processos:");
				documento5.setValor("Não disponível");
				
				
			} else {
			EngineRetornoExecutionResultProcessos processo = engine.getProcessos();
			documento5.setDescricao("numero de processos:");
			documento5.setValor(CommonsUtil.stringValue(processo.getTotal_acoes_judiciais()));
			
			
		} }
		
		
		}

		
		result.add(documento);
		result.add(documento2);
		result.add(documento3);
		result.add(documento4);
		result.add(documento5);
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
		return  !isEngineProcessado() && (CommonsUtil.mesmoValor("PF", tipoPessoa) || ( CommonsUtil.mesmoValor("PJ", tipoPessoa)  &&
				!this.motivoAnalise.contains( "Empresa Vinculada")));
	}

	public boolean isEngineProcessado() {
		return !CommonsUtil.semValor(engine) && !CommonsUtil.semValor(engine.getIdCallManager());
	}	
	
	public boolean isPodeChamarSerasa() {
		return !isSerasaProcessado() && CommonsUtil.mesmoValor("PF", tipoPessoa)
				&& CommonsUtil.mesmoValor(this.motivoAnalise, "Proprietario Atual"); // (CommonsUtil.mesmoValor("PJ",
																						// tipoPessoa) ||
	}

	public boolean isSerasaProcessado() {
		return !CommonsUtil.semValor(retornoSerasa);
	}	
	
	public boolean isPodeChamarCenprot() {
		return isEngineProcessado() && !isCenprotProcessado() && !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}
	
	public boolean isCenprotProcessado() {
		return !CommonsUtil.semValor(retornoCenprot);
	}
	
	public boolean isScrProcessado() {
		return !CommonsUtil.semValor(retornoScr);
	}
	
	public boolean isPodeChamarSCR() {
		return isEngineProcessado() && !isScrProcessado() && !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
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

		this.tipoEnum = DocumentosAnaliseEnum.parse(tipo);
//		
//		switch (tipo) {
//		case "Rea":
//			this.tipoEnum = DocumentosAnaliseEnum.REA;
//			break;
//		case "Relato":
//			this.tipoEnum = DocumentosAnaliseEnum.RELATO;
//
//			break;
//		case "Crednet":
//			this.tipoEnum = DocumentosAnaliseEnum.CREDNET;
//			break;
//		}
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

	public boolean isLiberadoScr() {
		return liberadoScr;
	}

	public void setLiberadoScr(boolean liberadoScr) {
		this.liberadoScr = liberadoScr;
	}

	public String getRetornoScr() {
		return retornoScr;
	}

	public void setRetornoScr(String retornoScr) {
		this.retornoScr = retornoScr;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
