package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoRequestFields;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminaisEvidences;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaQuodScore;
import br.com.galleriabank.dataengine.cliente.model.retorno.processos.EngineRetornoExecutionResultProcessos;
import br.com.galleriabank.netrin.cliente.model.PPE.PpeResponse;
import br.com.galleriabank.netrin.cliente.model.cenprot.CenprotResponse;
import br.com.galleriabank.netrin.cliente.model.cenprot.ProtestosBrasilEstado;
import br.com.galleriabank.netrin.cliente.model.dossie.DossieRequest;
import br.com.galleriabank.netrin.cliente.model.processos.ProcessoResponse;
import br.com.galleriabank.serasacrednet.cliente.model.CredNet;
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

	private PagadorRecebedor pagador; // titulares pra enviar pedido

	private String identificacao;
	private String cnpjcpf;
	private String tipoPessoa;
	private String motivoAnalise;
	private String path;
	private String tipo;
	private boolean liberadoAnalise;
	private boolean liberadoSerasa;
	private boolean liberadoCenprot;
	private boolean liberadoProcesso;
	private boolean liberadoScr;

	private DocumentosAnaliseEnum tipoEnum;

	private String retorno;
	private String retornoEngine;
	private String retornoSerasa;
	private String retornoCenprot;
	private String retornoProcesso;
	private String retornoPpe;

	private String retornoScr;
	private String observacao;
	
	public List<DocumentoAnaliseResumo> getResumoEngine() {
		List<DocumentoAnaliseResumo> result = new ArrayList<>();
		EngineRetorno engine = GsonUtil.fromJson(getRetornoEngine(), EngineRetorno.class);
		if(engine == null) {
			result.add(new DocumentoAnaliseResumo("nâo disponível", null));
		} else {
		EngineRetornoRequestFields nome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome"))
				.findFirst().orElse(null);
		if (nome != null)
			result.add(new DocumentoAnaliseResumo("Nome:", nome.getValue()));

		if (CommonsUtil.mesmoValor(tipoPessoa, "PF")) {

			EngineRetornoRequestFields cpf = engine.getRequestFields().stream().filter(g -> g.getField().equals("cpf"))
					.findFirst().orElse(null);
			if (cpf != null)
				result.add(new DocumentoAnaliseResumo("CPF:", cpf.getValue()));

		} else if (CommonsUtil.mesmoValor(tipoPessoa, "PJ")) {
			EngineRetornoRequestFields cnpj = engine.getRequestFields().stream()
					.filter(s -> s.getField().equals("cnpj")).findFirst().orElse(null);
			if (cnpj != null)
				result.add(new DocumentoAnaliseResumo("CNPJ:", cnpj.getValue()));
		}

		if (engine.getConsultaCompleta() == null) {
			result.add(new DocumentoAnaliseResumo("Score:", "Não disponivel"));
		} else {
			EngineRetornoExecutionResultConsultaQuodScore score = engine.getConsultaCompleta().getQuodScore();
			result.add(new DocumentoAnaliseResumo("Score:", CommonsUtil.stringValue(score.getScore())));
		}

		if (engine.getConsultaAntecedenteCriminais() == null) {
			result.add(new DocumentoAnaliseResumo("Antecedentes criminais:", "Não disponível"));
		} else {
			EngineRetornoExecutionResultAntecedenteCriminaisEvidences mensagem = engine
					.getConsultaAntecedenteCriminais().getEvidences();
			result.add(new DocumentoAnaliseResumo("Antecedentes criminais:", mensagem.getMessage()));
		}

		if (engine.getProcessos() == null) {
			result.add(new DocumentoAnaliseResumo("Numero  de processos:", "Não disponível"));

		} else {
			EngineRetornoExecutionResultProcessos processo = engine.getProcessos();
			result.add(new DocumentoAnaliseResumo("Numero  de processos:",
					CommonsUtil.stringValue(processo.getTotal_acoes_judiciais())));
		}
			}	
		
		
		

		return result;
	}
	public List<DocumentoAnaliseResumo> getResumoSerasa(){
		List<DocumentoAnaliseResumo> Serasa = new ArrayList<>();
		CredNet dados = GsonUtil.fromJson(getRetornoSerasa(), CredNet.class);
		if(CommonsUtil.mesmoValor(tipoPessoa, "PF")) {
			
		
		String cheque = CommonsUtil.stringValue(dados.getChequeSemFundo());
		if(dados.getChequeSemFundo() == null) {
		
			Serasa.add(new DocumentoAnaliseResumo("Cheque Sem Fundo:","Não disponível" ));
			} else {
				Serasa.add(new DocumentoAnaliseResumo("Cheque Sem Fundo:", cheque));
			}
		String divida = CommonsUtil.stringValue(dados.getDividaVencidaResumo());
		if(divida == null) {
			Serasa.add(new DocumentoAnaliseResumo("Divida vencida:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Dívida vencida:", divida));
		}
		String pefin = CommonsUtil.stringValue(dados.getPefinResumo());
		if(pefin == null) {
			Serasa.add(new DocumentoAnaliseResumo("Pefin:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Pefin:", pefin));
		}
		String refin = CommonsUtil.stringValue(dados.getRefinResumo());
		if(refin == null) {
			Serasa.add(new DocumentoAnaliseResumo("Refin:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Refin:", refin));
		}
		String protesto = CommonsUtil.stringValue(dados.getProtesto());
		if(protesto == null) {
			Serasa.add(new DocumentoAnaliseResumo("Protesto:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Protesto:", protesto));
		}
		String acoes =  CommonsUtil.stringValue(dados.getAcoesCivil());
		if(acoes == null) {
			Serasa.add(new DocumentoAnaliseResumo("Ações Civis:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Ações Civis:", acoes));
		}
		String falencia = CommonsUtil.stringValue(dados.getFalencias());
		if(falencia == null) {
			Serasa.add(new DocumentoAnaliseResumo("Falências:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Falências:", falencia));
		}
		String falenciaInsuceso = CommonsUtil.stringValue(dados.getFalenciasInsucesso());
		if(falenciaInsuceso == null) {
			Serasa.add(new DocumentoAnaliseResumo("Falência Insucesso:", "Não Disponível"));
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Falência Insucesso:", falenciaInsuceso));
		}
		} else {
			Serasa.add(new DocumentoAnaliseResumo("Menu não disponível para PJ", null));
		}
		return Serasa;
		
	}
	public List<DocumentoAnaliseResumo> getResumoCenprot(){
		List<DocumentoAnaliseResumo> cenprot = new ArrayList<>();
		if(getRetornoCenprot() == null) {
			cenprot.add(new DocumentoAnaliseResumo("não disponivel", null));
		} else {
		CenprotResponse data = GsonUtil.fromJson(getRetornoCenprot(), CenprotResponse.class);
		if(data.getCenprotProtestos().getProtestosBrasil() == null) {
			cenprot.add(new DocumentoAnaliseResumo("Não Disponível","0"));
		}else {
			for (ProtestosBrasilEstado estado : data.getCenprotProtestos().getProtestosBrasil().getEstados()) {
			
				String valorEstado = CommonsUtil.stringValue(estado.getValorTotal()) + " (" + estado.getValorTotal() + ") "; 
				cenprot.add(new DocumentoAnaliseResumo(estado.getEstado(), valorEstado)); 	
			
		}
		}
		
		}
		
		return cenprot;
	}
	public List<DocumentoAnaliseResumo> getResumoScr(){
		List<DocumentoAnaliseResumo> scr = new ArrayList<>();
		ScrResult dado = GsonUtil.fromJson(getRetornoScr(), ScrResult.class);
		if(dado == null) {
			scr.add(new DocumentoAnaliseResumo("Dados não disponíveis", "0"));
		} else {
		 String carteira = CommonsUtil.stringValue( dado.getResumoDoClienteTraduzido().getCarteiraVencer());
		 if(carteira == null) {
			 scr.add(new DocumentoAnaliseResumo("Carteira a vencer:", "Não Disponível" ));
			 
		 } else {
			 scr.add(new DocumentoAnaliseResumo("Carteira a vencer:", carteira));
		 }
	String carteiraVencido = CommonsUtil.stringValue(dado.getResumoDoClienteTraduzido().getCarteiraVencido());
	if(carteiraVencido == null) {
		scr.add(new DocumentoAnaliseResumo("Carteira vencido:", "Não Disponível" ));
		 
	 } else {
		 scr.add(new DocumentoAnaliseResumo("Carteira vencido:", carteiraVencido));
	 }
	String prejuizo = CommonsUtil.stringValue(dado.getResumoDoClienteTraduzido().getPrejuizo());
	if(prejuizo == null) {
		scr.add(new DocumentoAnaliseResumo("Prejuizo:", "Não Disponível" ));
		 
	 } else {
		 scr.add(new DocumentoAnaliseResumo("Prejuizo:", prejuizo));
	 }
	String creditoTomado = CommonsUtil.stringValue(dado.getResumoDoClienteTraduzido().getCarteiradeCredito());
	if(creditoTomado == null) {
		scr.add(new DocumentoAnaliseResumo("Carteira de Crédito Tomado:", "Não Disponível" ));
		 
	 } else {
		 scr.add(new DocumentoAnaliseResumo("Carteira de Crédito Tomado:", creditoTomado));
	 }
	
		}
		
		return scr;
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
		return !CommonsUtil.mesmoValor(motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL") && !isEngineProcessado()
				&& (CommonsUtil.mesmoValor("PF", tipoPessoa) || (CommonsUtil.mesmoValor("PJ", tipoPessoa)
						&& !this.motivoAnalise.contains("Empresa Vinculada")));
	}

	public boolean isEngineProcessado() {
		return !CommonsUtil.semValor(engine) && !CommonsUtil.semValor(engine.getIdCallManager());
	}

	public boolean isPodeChamarSerasa() {
		return !isSerasaProcessado() && CommonsUtil.mesmoValor("PF", tipoPessoa)
				&& CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL"); // (CommonsUtil.mesmoValor("PJ",
																									// // tipoPessoa) ||
	}

	public boolean isSerasaProcessado() {
		return !CommonsUtil.semValor(retornoSerasa);
	}

	public boolean isPodeChamarCenprot() {
		return isEngineProcessado() && !isCenprotProcessado()
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isCenprotProcessado() {
		return !CommonsUtil.semValor(retornoCenprot);
	}

	public boolean isPodeChamarProcesso() {
		return !isProcessoProcessado() && CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL")
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isPodeChamarPpe() {
		return !isPpeProcessado() && CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL")
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isProcessoProcessado() {
		return !CommonsUtil.semValor(retornoProcesso);
	}

	public boolean isScrProcessado() {
		return !CommonsUtil.semValor(retornoScr);
	}

	public boolean isPodeChamarSCR() {
		return isEngineProcessado() && !isScrProcessado()
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isPpeProcessado() {
		return !CommonsUtil.semValor(retornoPpe);
	}

	
	public boolean isPodeDownlaodDossie() {
		return isPpeProcessado() && isCenprotProcessado() && isProcessoProcessado()
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}
	
	public void addObservacao(String observacao) {

		if (this.observacao == null) {
			this.observacao = "";
		} else {
			this.observacao = this.observacao + " - ";
		}

		this.observacao = observacao;
	}
	
	public String getDossieRequest() {

		DossieRequest dossieRequest = new DossieRequest();
		dossieRequest.setCpf(pagador.getCpf());
		dossieRequest.setNome(pagador.getNome());
		dossieRequest.setCenprot(GsonUtil.fromJson(retornoCenprot, CenprotResponse.class));
		dossieRequest.setPpe(GsonUtil.fromJson(retornoPpe, PpeResponse.class));
		dossieRequest.setProcesso(GsonUtil.fromJson(retornoProcesso, ProcessoResponse.class));

		return GsonUtil.toJson(dossieRequest);
		
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

	public String getRetornoProcesso() {
		return retornoProcesso;
	}

	public void setRetornoProcesso(String retornoProcesso) {
		this.retornoProcesso = retornoProcesso;
	}

	public String getRetornoPpe() {
		return retornoPpe;
	}

	public void setRetornoPpe(String retornoPpe) {
		this.retornoPpe = retornoPpe;
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

	public boolean isLiberadoProcesso() {
		return liberadoProcesso;
	}

	public void setLiberadoProcesso(boolean liberadoProcesso) {
		this.liberadoProcesso = liberadoProcesso;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
