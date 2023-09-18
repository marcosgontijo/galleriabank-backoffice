package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.text.NumberFormat;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.cobranca.ws.plexi.PlexiConsulta;
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
	private String retornoLaudoRobo;

	private String retornoScr;
	private String observacao;
	private boolean excluido;
	
	private String retornoCNDTrabalhistaTST;
	private String retornoCNDFederal;
	private String retornoCNDEstadual;
	
	private List<PlexiConsulta> plexiConsultas = new ArrayList<PlexiConsulta>();
	private List<String> estadosConsulta = new ArrayList<String>();
	private String estadosConsultaStr;
	
	private boolean politicamenteExposta = false;
	private String pessoasPoliticamenteExpostas = "0";
	
	private double totalPendenciasValor = 0.0;
	private double totalValorApontamentos = 0.0;
	private double totalInadimplenciaValor = 0.0;
	private double totalLawSuitValor = 0.0;
	private double totalProtestosValor = 0.0;
	
	private int totalPendencias = 0;
	private int totalInadimplencias = 0;
	private int totalLawSuitApontamentos = 0;
	private int totalApontamentos = 0;
	private int totalCcfApontamentos = 0;
	private int totalProtestos = 0;
	

	public List<DocumentoAnaliseResumo> getResumoProcesso() {
		List<DocumentoAnaliseResumo> vProcesso = new ArrayList<>();
		EngineRetornoExecutionResultProcessos processo = null;
		try {
			processo = GsonUtil.fromJson(getRetornoProcesso(), EngineRetornoExecutionResultProcessos.class);
		} catch (Exception erro) {
			vProcesso.add(new DocumentoAnaliseResumo(null, null));
		}
		if (processo == null) {
			vProcesso.add(new DocumentoAnaliseResumo("não disponível", null));
		} else {
				if (processo.getProcessos()== null) {
					vProcesso.add(new DocumentoAnaliseResumo("Processos", "Não disponível"));
				} else {
					String processos = CommonsUtil.stringValue(processo.getProcessos());
					vProcesso.add(new DocumentoAnaliseResumo("Processos:", processos));
						}
				
//				if(processo.getProcessos() == null) {
//					vProcesso.add(new DocumentoAnaliseResumo("Criminal:", "Não disponível"));
//				} else {
//					String processos = CommonsUtil.stringValue(processo.getProcessos());
//					vProcesso.add(new DocumentoAnaliseResumo("Criminal:", "Não disponível"));
//				}
//				
//				if(processo.getProcessos() == null) {
//					vProcesso.add(new DocumentoAnaliseResumo("Trabalhista:", "Não disponível"));
//				} else {
//					vProcesso.add(new DocumentoAnaliseResumo("Trabalhista:", "Não disponível"));
//				}
//				
//				if(processo.getProcessos() == null) {
//					vProcesso.add(new DocumentoAnaliseResumo("Execução de título:", "Não disponível"));
//				} else {
//					vProcesso.add(new DocumentoAnaliseResumo("Execução de título:", "Não disponível"));
//				}
//				
//				if(processo.getProcessos() == null) {
//					vProcesso.add(new DocumentoAnaliseResumo("Execução fiscal:", "Não disponível"));
//				} else {
//					vProcesso.add(new DocumentoAnaliseResumo("Execução fiscal:", "Não disponível"));
//				}
//				
//				if(processo.getProcessos() == null) {
//					vProcesso.add(new DocumentoAnaliseResumo("Outros:", "Não disponível"));
//				} else {
//					vProcesso.add(new DocumentoAnaliseResumo("Outros:", "Não disponível"));
//				}
				
			}
		return vProcesso;
	}

	public List<DocumentoAnaliseResumo> getResumoEngine() {
		List<DocumentoAnaliseResumo> result = new ArrayList<>();
		EngineRetorno engine = null;
		try {
			engine = GsonUtil.fromJson(getRetornoEngine(), EngineRetorno.class);
		} catch (Exception erro) {
			result.add(new DocumentoAnaliseResumo(null, null));
		}

		if (engine == null) {
			result.add(new DocumentoAnaliseResumo("Não disponível", null));
		} else {			
			populaExecutionResult(engine);
			
			EngineRetornoRequestFields nome = engine.getRequestFields().stream()
					.filter(f -> f.getField().equals("nome")).findFirst().orElse(null);
			if (nome != null)
				result.add(new DocumentoAnaliseResumo("Nome:", nome.getValue()));

			if (CommonsUtil.mesmoValor(tipoPessoa, "PF")) {

				EngineRetornoRequestFields cpf = engine.getRequestFields().stream()
						.filter(g -> g.getField().equals("cpf")).findFirst().orElse(null);
				if (cpf != null)
					result.add(new DocumentoAnaliseResumo("CPF:", cpf.getValue()));
				
				if (engine.getConsultaCompleta().getBestInfo().getAge() != null 
						&& !engine.getConsultaCompleta().getBestInfo().getAge().isEmpty())
					result.add(new DocumentoAnaliseResumo("Idade:", engine.getConsultaCompleta().getBestInfo().getAge()));
					
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
						CommonsUtil.stringValue(processo.getTotal_acoes_judicias_reu() + totalLawSuitApontamentos)));
			}
			if(engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Pessoa Políticamente exposta:", "Não disponível"));
			} else {
				if (politicamenteExposta) {
					result.add(new DocumentoAnaliseResumo("Pessoa Políticamente exposta:", getPessoasPoliticamenteExpostas()));
				} else {
					result.add(new DocumentoAnaliseResumo("Pessoa Políticamente exposta:", "0"));
				}			
			}
			
			
			if(engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Valor processos:", "Não disponível"));
			} else {
				if (totalLawSuitApontamentos > 0) {
					result.add(new DocumentoAnaliseResumo("Valor processos:", String.format("%,.2f", totalLawSuitValor) 
																			 + " (" + CommonsUtil.stringValue(totalLawSuitApontamentos) + ")"));
				} else {
					result.add(new DocumentoAnaliseResumo("Valor processos:", "0"));
				}				
			}
			
			if(engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Pendências financeiras:", "Não disponível"));
			} else {
				if (totalPendencias > 0) {
					result.add(new DocumentoAnaliseResumo("Pendências financeiras:", String.format("%,.2f", totalPendenciasValor) 
																					+ " (" + CommonsUtil.stringValue(totalPendencias) + ")"));
				} else {
					result.add(new DocumentoAnaliseResumo("Pendências financeiras:", "0"));
				}
			}
			
			if(engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Cheque sem fundo:", "Não disponível"));
			} else {
				if (totalCcfApontamentos > 0) {
					result.add(new DocumentoAnaliseResumo("Cheque sem fundo:", CommonsUtil.stringValue(totalCcfApontamentos)));
				} else {
					result.add(new DocumentoAnaliseResumo("Cheque sem fundo:", "0"));
				}			
			}
			
			if(engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Inadimplências Comunicadas:", "Não disponível"));
			} else {
				if (totalInadimplencias > 0) {
					result.add(new DocumentoAnaliseResumo("Inadimplências Comunicadas:", String.format("%,.2f", totalInadimplenciaValor) 
																						+ " (" + CommonsUtil.stringValue(totalInadimplencias) + ")"));
				} else {
					result.add(new DocumentoAnaliseResumo("Inadimplências Comunicadas:", "0"));
				}	
			}
			
			if(engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Protesto:", "Não disponível"));
			} else {
				if (totalProtestos > 0) {
					result.add(new DocumentoAnaliseResumo("Protesto:", String.format("%,.2f", totalProtestosValor)
																		+ " (" + CommonsUtil.stringValue(totalProtestos) + ")"));
				} else {
					result.add(new DocumentoAnaliseResumo("Protesto:", "0"));
				}		
			}
		}

		return result;
	}

	private void populaExecutionResult(EngineRetorno engine) {		
		for (int i = 0; i < engine.getExecutionResult().size(); i++) {
			JSONObject objER = new JSONObject(engine.getExecutionResult().get(i));
			
			if (CommonsUtil.mesmoValor(objER.get("validationSource"), "provider-pep-relacionado")) {
				if (!CommonsUtil.mesmoValor(objER.getString("observation"), "")) {
					JSONArray novoObj = new JSONArray(objER.getString("observation"));
					politicamenteExposta = true;
					setPessoasPoliticamenteExpostas(novoObj.length());
				}
			}
			
			if (CommonsUtil.mesmoValor(objER.get("validationSource"), "Consulta completa Credito") 
				|| CommonsUtil.mesmoValor(objER.get("validationSource"), "Credito PJ")) {
				if (!CommonsUtil.mesmoValor(objER.getString("observation"), "")) {
					System.out.println(objER.getString("observation"));
					calculaPendenciasFinanceiras(new JSONObject(objER.getString("observation")));
				}
			}
		}
	}
	
	public void calculaPendenciasFinanceiras(JSONObject obj) {
		/*
		 * Apontamentos = Inadimplencia;
		 * CCF = Cheque sem fundo;
		 * LawSuit = Ação Judicial
		 * */
		
		if (obj.getJSONObject("Negative").has("PendenciesControlCred")) {
			totalValorApontamentos = obj.getJSONObject("Negative").getDouble("PendenciesControlCred");
		}
		
		if (obj.getJSONObject("Negative").has("TotalApontamentos")) {
			totalInadimplencias = obj.getJSONObject("Negative").getInt("TotalApontamentos");
			totalInadimplenciaValor = obj.getJSONObject("Negative").getDouble("TotalValorApontamentos");
		}
		
		if (obj.getJSONObject("Negative").has("TotalLawSuitApontamentos")) {
			totalLawSuitApontamentos = obj.getJSONObject("Negative").getInt("TotalLawSuitApontamentos");
			totalLawSuitValor = obj.getJSONObject("Negative").getDouble("TotalValorLawSuitApontamentos");
		}
		
		if (obj.getJSONObject("Negative").has("TotalCcfApontamentos")) {
			totalCcfApontamentos = obj.getJSONObject("Negative").getInt("TotalCcfApontamentos");
		}
	
		if (obj.getJSONObject("Negative").has("TotalProtests")) {
			totalProtestos = obj.getJSONObject("Negative").getInt("TotalProtests");
			totalProtestosValor = obj.getJSONObject("Negative").getDouble("TotalValorProtests");
		}
		
		totalPendenciasValor = totalInadimplenciaValor + totalLawSuitValor; 
		totalPendencias = totalInadimplencias + totalLawSuitApontamentos + totalCcfApontamentos;
	}


	public List<DocumentoAnaliseResumo> getResumoCenprot() {
		List<DocumentoAnaliseResumo> cenprot = new ArrayList<>();

		CenprotProtestos data = GsonUtil.fromJson(getRetornoCenprot(), CenprotProtestos.class);
		if (data == null) {
			cenprot.add(new DocumentoAnaliseResumo("Não disponível", null));
		} else {

			if (CommonsUtil.semValor(data.getProtestosBrasil().getEstados())) {
				cenprot.add(new DocumentoAnaliseResumo("Não disponível", null));
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
			    String creditoTomado = CommonsUtil.formataValorMonetario(dado.getResumoDoClienteTraduzido().getCarteiradeCredito());
			    double valorCreditoTomado = Double.parseDouble(creditoTomado.replace(".", "").replace(",", ".").replace("R$", "").trim());

			    if (dado.getResumoDoClienteTraduzido().getLimitesdeCredito() == null) {
				    scr.add(new DocumentoAnaliseResumo("Limites:", "Não Disponível"));
				} else {
				    String limiteCredito = CommonsUtil.formataValorMonetario(dado.getResumoDoClienteTraduzido().getLimitesdeCredito());
				    double valorLimiteCredito = Double.parseDouble(limiteCredito.replace(".", "").replace(",", ".").replace("R$", "").trim());
				    double soma = valorCreditoTomado + valorLimiteCredito;

				    // Formatar o valor da soma em moeda (real)
				    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
				    String somaFormatada = formatoMoeda.format(soma);

				    scr.add(new DocumentoAnaliseResumo("Carteira de Crédito Tomado:", somaFormatada));
				}
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
		return !isEngineProcessado() && (!this.motivoAnalise.startsWith("Empresa Vinculada")
				&& !this.motivoAnalise.startsWith("Sócio Vinculado ao Proprietario Anterior"));
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
		return !isCenprotProcessado()
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isCenprotProcessado() {
		return !CommonsUtil.semValor(retornoCenprot);
	}

	public boolean isPodeChamarProcesso() {
		return !isProcessoProcessado()
//				&& (CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL") || CommonsUtil
//						.mesmoValor(this.motivoAnalise.toUpperCase(), "EMPRESA VINCULADA AO PROPRIETARIO ATUAL"))
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
	
	public boolean isCNDEstadualProcessado() {
		return !CommonsUtil.semValor(retornoCNDEstadual);
	}
	
	public boolean isCNDFederalProcessado() {
		return !CommonsUtil.semValor(retornoCNDFederal);
	}
	
	public boolean isCNDTrabalhistaTSTProcessado() {
		return !CommonsUtil.semValor(retornoCNDTrabalhistaTST);
	}

	public boolean isPodeChamarSCR() {
		return !isScrProcessado()
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
	
	@Override
	public String toString() {
		return "DocumentoAnalise [id=" + id + ", tipo=" + tipo + "]";
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
	
	public String getRetornoLaudoRobo() {
		return retornoLaudoRobo;
	}
	public void SetRetornoLaudoRobo(String retornoLaudoRobo) {
		this.retornoLaudoRobo = retornoLaudoRobo;
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
	
	public List<PlexiConsulta> getPlexiConsultas() {
		return plexiConsultas;
	}

	public void setPlexiConsultas(List<PlexiConsulta> plexiConsultas) {
		this.plexiConsultas = plexiConsultas;
	}

	public String getRetornoCNDTrabalhistaTST() {
		return retornoCNDTrabalhistaTST;
	}

	public void setRetornoCNDTrabalhistaTST(String retornoCNDTrabalhistaTST) {
		this.retornoCNDTrabalhistaTST = retornoCNDTrabalhistaTST;
	}

	public String getRetornoCNDFederal() {
		return retornoCNDFederal;
	}

	public void setRetornoCNDFederal(String retornoCNDFederal) {
		this.retornoCNDFederal = retornoCNDFederal;
	}

	public String getRetornoCNDEstadual() {
		return retornoCNDEstadual;
	}

	public void setRetornoCNDEstadual(String retornoCNDEstadual) {
		this.retornoCNDEstadual = retornoCNDEstadual;
	}
	
	private String getPessoasPoliticamenteExpostas() {
		return pessoasPoliticamenteExpostas;
	}

	private void setPessoasPoliticamenteExpostas(int pessoasPoliticamenteExpostas) {
		this.pessoasPoliticamenteExpostas = Integer.toString(pessoasPoliticamenteExpostas);
	}
	
	public List<String> getEstadosConsulta() {
		if(!CommonsUtil.semValor(estadosConsultaStr)) {
			estadosConsulta = CommonsUtil.stringToList(estadosConsultaStr);
		}
		return estadosConsulta;
	}

	public void setEstadosConsulta(List<String> estadosConsulta) {
		this.estadosConsulta = estadosConsulta;
	}
	
	public String getEstadosConsultaStr() {
		if(!CommonsUtil.semValor(estadosConsulta)) {
			estadosConsultaStr = estadosConsulta.toString();
		}
		return estadosConsultaStr;
	}

	public void setEstadosConsultaStr(String estadosConsultaStr) {
		this.estadosConsultaStr = estadosConsultaStr;
	}
}
