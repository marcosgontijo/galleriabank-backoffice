package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.apache.xmlgraphics.util.uri.CommonURIResolver;
import org.primefaces.PrimeFaces;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoRequestFields;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminaisEvidences;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaQuodScore;
import br.com.galleriabank.dataengine.cliente.model.retorno.processos.EngineRetornoExecutionResultProcessos;
import br.com.galleriabank.netrin.cliente.model.PPE.PpeResponse;
import br.com.galleriabank.netrin.cliente.model.cenprot.CenprotProtestos;
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
	private boolean liberadoContinuarAnalise;
	
	private boolean liberadoSerasa;
	private boolean liberadoCenprot;
	private boolean liberadoProcesso;
	private boolean liberadoScr;

	private DocumentosAnaliseEnum tipoEnum;

	private String retorno;
	private String retornoEngine;
	private String retornoSerasa;
	private String retornoCenprot;
	private String tipoProcesso;
	private String retornoProcesso;
	private String retornoPpe;

	private String retornoScr;
	private String observacao;
	private boolean excluido;

	public List<DocumentoAnaliseResumo> getResumoEngine() {
		List<DocumentoAnaliseResumo> result = new ArrayList<>();
		EngineRetorno engine = null;
		try {
			engine = GsonUtil.fromJson(getRetornoEngine(), EngineRetorno.class);
		} catch (Exception erro) {
			result.add(new DocumentoAnaliseResumo(null, null));
		}

		if (engine == null) {
			result.add(new DocumentoAnaliseResumo("nâo disponível", null));
		} else {
			EngineRetornoRequestFields nome = engine.getRequestFields().stream()
					.filter(f -> f.getField().equals("nome")).findFirst().orElse(null);
			if (nome != null)
				result.add(new DocumentoAnaliseResumo("Nome:", nome.getValue()));

			if (CommonsUtil.mesmoValor(tipoPessoa, "PF")) {

				EngineRetornoRequestFields cpf = engine.getRequestFields().stream()
						.filter(g -> g.getField().equals("cpf")).findFirst().orElse(null);
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

	public List<DocumentoAnaliseResumo> getResumoSerasa() {
		List<DocumentoAnaliseResumo> serasa = new ArrayList<>();
		CredNet dados = GsonUtil.fromJson(getRetornoSerasa(), CredNet.class);
		if (dados == null) {
			serasa.add(new DocumentoAnaliseResumo("não disponível", null));
			return serasa;
		} else {
			if (CommonsUtil.mesmoValor(tipoPessoa, "PF")) {

				if (dados.getChequeSemFundo() == null) {
					serasa.add(new DocumentoAnaliseResumo("Cheque Sem Fundo:", "Não disponível"));
				} else {
					String cheque = CommonsUtil.stringValue(dados.getChequeSemFundo().getPcsfQtCheques());
					serasa.add(new DocumentoAnaliseResumo("Cheque Sem Fundo:", cheque));
				}

				if (dados.getDividaVencidaResumo() == null
						|| dados.getDividaVencidaResumo().getPpfiVlTotalPendencia() == null) {
					serasa.add(new DocumentoAnaliseResumo("Divida vencida:", "Não Disponível"));
				} else {
					String divida = CommonsUtil
							.formataValorMonetario(dados.getDividaVencidaResumo().getPpfiVlTotalPendencia());
					serasa.add(new DocumentoAnaliseResumo("Dívida vencida:", divida));
				}

				if (dados.getPefinResumo() == null || dados.getPefinResumo().getPpfiVlTotalPendencia() == null) {
					serasa.add(new DocumentoAnaliseResumo("Pefin:", "Não Disponível"));
				} else {
					String pefin = CommonsUtil.formataValorMonetario(dados.getPefinResumo().getPpfiVlTotalPendencia());
					serasa.add(new DocumentoAnaliseResumo("Pefin:", pefin));
				}

				if (dados.getRefinResumo() == null || dados.getRefinResumo().getPpfiVlTotalPendencia() == null) {
					serasa.add(new DocumentoAnaliseResumo("Refin:", "Não Disponível"));
				} else {
					String refin = CommonsUtil.formataValorMonetario(dados.getRefinResumo().getPpfiVlTotalPendencia());
					serasa.add(new DocumentoAnaliseResumo("Refin:", refin));
				}

				if (dados.getProtesto() == null) {
					serasa.add(new DocumentoAnaliseResumo("Protesto:", "Não Disponível"));
				} else {
					String protesto = CommonsUtil.formataValorMonetario(dados.getProtesto().getPeptVlTotal());
					serasa.add(new DocumentoAnaliseResumo("Protesto:", protesto));
				}

				if (dados.getAcoesCivil() == null) {
					serasa.add(new DocumentoAnaliseResumo("Ações Civis:", "Não Disponível"));
				} else {
					String acoes = CommonsUtil.formataValorMonetario(dados.getAcoesCivil().getPeajVlTotalAcao());
					serasa.add(new DocumentoAnaliseResumo("Ações Civis:", acoes));
				}

				if (dados.getFalencias() == null) {
					serasa.add(new DocumentoAnaliseResumo("Falências:", "Não Disponível"));
				} else {
					String falencia = CommonsUtil.formataValorMonetario(dados.getFalencias().getPeajVlTotalAcao());
					serasa.add(new DocumentoAnaliseResumo("Falências:", falencia));
				}

				if (dados.getFalenciasInsucesso() == null) {
					serasa.add(new DocumentoAnaliseResumo("Falência Insucesso:", "Não Disponível"));
				} else {
					String falenciaInsuceso = CommonsUtil
							.formataValorMonetario(dados.getFalenciasInsucesso().getPeajVlTotalAcao());
					serasa.add(new DocumentoAnaliseResumo("Falência Insucesso:", falenciaInsuceso));
				}
			} else {

				if (CommonsUtil.mesmoValor(tipoPessoa, "PF")) {

					if (dados.getChequeSemFundo() == null) {
						serasa.add(new DocumentoAnaliseResumo("Cheque Sem Fundo:", "Não disponível"));
					} else {
						String cheque = CommonsUtil.stringValue(dados.getChequeSemFundo().getPcsfQtCheques());
						serasa.add(new DocumentoAnaliseResumo("Cheque Sem Fundo:", cheque));
					}

					if (dados.getDividaVencidaResumo() == null
							|| dados.getDividaVencidaResumo().getPpfiVlTotalPendencia() == null) {
						serasa.add(new DocumentoAnaliseResumo("Divida vencida:", "Não Disponível"));
					} else {
						String divida = CommonsUtil
								.formataValorMonetario(dados.getDividaVencidaResumo().getPpfiVlTotalPendencia());
						serasa.add(new DocumentoAnaliseResumo("Dívida vencida:", divida));
					}

					if (dados.getPefinResumo() == null || dados.getPefinResumo().getPpfiVlTotalPendencia() == null) {
						serasa.add(new DocumentoAnaliseResumo("Pefin:", "Não Disponível"));
					} else {
						String pefin = CommonsUtil
								.formataValorMonetario(dados.getPefinResumo().getPpfiVlTotalPendencia());
						serasa.add(new DocumentoAnaliseResumo("Pefin:", pefin));
					}

					if (dados.getRefinResumo() == null || dados.getRefinResumo().getPpfiVlTotalPendencia() == null) {
						serasa.add(new DocumentoAnaliseResumo("Refin:", "Não Disponível"));
					} else {
						String refin = CommonsUtil
								.formataValorMonetario(dados.getRefinResumo().getPpfiVlTotalPendencia());
						serasa.add(new DocumentoAnaliseResumo("Refin:", refin));
					}

					if (dados.getProtesto() == null) {
						serasa.add(new DocumentoAnaliseResumo("Protesto:", "Não Disponível"));
					} else {
						String protesto = CommonsUtil.formataValorMonetario(dados.getProtesto().getPeptVlTotal());
						serasa.add(new DocumentoAnaliseResumo("Protesto:", protesto));
					}

					if (dados.getAcoesCivil() == null) {
						serasa.add(new DocumentoAnaliseResumo("Ações Civis:", "Não Disponível"));
					} else {
						String acoes = CommonsUtil.formataValorMonetario(dados.getAcoesCivil().getPeajVlTotalAcao());
						serasa.add(new DocumentoAnaliseResumo("Ações Civis:", acoes));
					}

					if (dados.getFalencias() == null) {
						serasa.add(new DocumentoAnaliseResumo("Falências:", "Não Disponível"));
					} else {
						String falencia = CommonsUtil.formataValorMonetario(dados.getFalencias().getPeajVlTotalAcao());
						serasa.add(new DocumentoAnaliseResumo("Falências:", falencia));
					}

					if (dados.getFalenciasInsucesso() == null) {
						serasa.add(new DocumentoAnaliseResumo("Falência Insucesso:", "Não Disponível"));
					} else {
						String falenciaInsuceso = CommonsUtil
								.formataValorMonetario(dados.getFalenciasInsucesso().getPeajVlTotalAcao());
						serasa.add(new DocumentoAnaliseResumo("Falência Insucesso:", falenciaInsuceso));
					}
				} else {
					serasa.add(new DocumentoAnaliseResumo("Menu não disponível para PJ", null));
				}
			}
		}
		return serasa;

	}

	public List<DocumentoAnaliseResumo> getResumoCenprot() {
		List<DocumentoAnaliseResumo> cenprot = new ArrayList<>();

		CenprotProtestos data = GsonUtil.fromJson(getRetornoCenprot(), CenprotProtestos.class);
		if (data == null) {
			cenprot.add(new DocumentoAnaliseResumo("não disponível", null));
		} else {

			if (CommonsUtil.semValor(data.getProtestosBrasil().getEstados())) {
				cenprot.add(new DocumentoAnaliseResumo("Não Disponível", null));
			} else {
				for (ProtestosBrasilEstado estado : data.getProtestosBrasil().getEstados()) {

					String valorEstado = CommonsUtil.stringValue(estado.getValorTotal()) + " (" + estado.getValorTotal()
							+ ") ";
					cenprot.add(new DocumentoAnaliseResumo(estado.getEstado(), valorEstado));

				}
			}
		}

		return cenprot;
	}

	public List<DocumentoAnaliseResumo> getResumoScr() {
		List<DocumentoAnaliseResumo> scr = new ArrayList<>();
		ScrResult dado = GsonUtil.fromJson(getRetornoScr(), ScrResult.class);
		if (dado == null) {
			scr.add(new DocumentoAnaliseResumo("Dados não disponíveis", "0"));
		} else {
			if (dado.getResumoDoClienteTraduzido().getCarteiraVencer() == null) {
				scr.add(new DocumentoAnaliseResumo("Carteira a vencer:", "Não Disponível"));
			} else {
				String carteira = CommonsUtil
						.formataValorMonetario(dado.getResumoDoClienteTraduzido().getCarteiraVencer());
				scr.add(new DocumentoAnaliseResumo("Carteira a vencer:", carteira));
			}

			if (dado.getResumoDoClienteTraduzido().getCarteiraVencido() == null) {
				scr.add(new DocumentoAnaliseResumo("Carteira vencido:", "Não Disponível"));
			} else {
				String carteiraVencido = CommonsUtil
						.formataValorMonetario(dado.getResumoDoClienteTraduzido().getCarteiraVencido());
				scr.add(new DocumentoAnaliseResumo("Carteira vencido:", carteiraVencido));
			}

			if (dado.getResumoDoClienteTraduzido().getPrejuizo() == null) {
				scr.add(new DocumentoAnaliseResumo("Prejuizo:", "Não Disponível"));
			} else {
				String prejuizo = CommonsUtil.formataValorMonetario(dado.getResumoDoClienteTraduzido().getPrejuizo());
				scr.add(new DocumentoAnaliseResumo("Prejuizo:", prejuizo));
			}

			if (dado.getResumoDoClienteTraduzido().getCarteiradeCredito() == null) {
				scr.add(new DocumentoAnaliseResumo("Carteira de Crédito Tomado:", "Não Disponível"));
			} else {
				String creditoTomado = CommonsUtil
						.formataValorMonetario(dado.getResumoDoClienteTraduzido().getCarteiradeCredito());
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
//		return !isEngineProcessado();				
				
		return !isEngineProcessado()
				&& (!this.motivoAnalise.startsWith("Empresa Vinculada"));
		
	}

	public boolean isEngineProcessado() {
		return !CommonsUtil.semValor(engine) && !CommonsUtil.semValor(engine.getIdCallManager())  ;
	}

	public boolean isPodeChamarSerasa() {
		return !isSerasaProcessado() && CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL"); // (CommonsUtil.mesmoValor("PJ",
																														// //
																														// tipoPessoa)
																														// ||
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
		return !isPpeProcessado() && CommonsUtil.mesmoValor("PF", tipoPessoa)
				&& CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL")
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

	public boolean isAnaliseBloqueada() {
		PpeResponse resultPEP = null;
		if (isPpeProcessado()) {
			resultPEP = GsonUtil.fromJson(getRetornoPpe().replace("\"details\":\"\"", "\"details\":{}"),
					PpeResponse.class);
		}

		if (!CommonsUtil.semValor(resultPEP) && !CommonsUtil.semValor(resultPEP.getPepKyc()))
			if (!CommonsUtil.booleanValue(isLiberadoContinuarAnalise())
					&& CommonsUtil.mesmoValorIgnoreCase("Sim", resultPEP.getPepKyc().getCurrentlyPEP())
					&& (resultPEP.getPepKyc().getHistoryPEP().stream()
							.filter(p -> CommonsUtil.mesmoValor(p.getLevel(), "1")).findAny().isPresent())) {
				return true;
			}

		return  CommonsUtil.mesmoValor(observacao, "Verificar Engine");
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

	public boolean isLiberadoContinuarAnalise() {
		return liberadoContinuarAnalise;
	}

	public void setLiberadoContinuarAnalise(boolean liberadoContinuarAnalise) {
		this.liberadoContinuarAnalise = liberadoContinuarAnalise;
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
	
	public String getRetornoEnginePj() {
		return retornoEngine;
	}
	
	public void setRetornoEnginePj(String retornoEngine) {
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

	public String getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(String tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
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

	public boolean getExcluido() {
		return excluido;
	}

	public void setExcluido(boolean excluido) {
		this.excluido = excluido;
	}

}
