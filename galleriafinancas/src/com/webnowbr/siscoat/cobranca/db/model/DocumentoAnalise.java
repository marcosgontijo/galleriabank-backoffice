package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.faces.model.SelectItem;

import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.op.GravamesReaDao;
import com.webnowbr.siscoat.cobranca.db.op.RelacionamentoPagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.mb.FileUploadMB.FileUploaded;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.cobranca.service.BigDataService;
import com.webnowbr.siscoat.cobranca.service.EngineService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.ScrService;
import com.webnowbr.siscoat.cobranca.vo.FileGenerator;
import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetorno;
import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetornoBloco;
import com.webnowbr.siscoat.cobranca.ws.netrin.NetrinConsulta;
import com.webnowbr.siscoat.cobranca.ws.plexi.PlexiConsulta;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetornoRequestFields;
import br.com.galleriabank.dataengine.cliente.model.retorno.AntecedentesCriminais.EngineRetornoExecutionResultAntecedenteCriminaisEvidences;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaNegative;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultConsultaQuodScore;
import br.com.galleriabank.dataengine.cliente.model.retorno.processos.EngineRetornoExecutionResultProcessos;
import br.com.galleriabank.netrin.cliente.model.PPE.PpeResponse;
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
	private boolean liberadoCertidoes;	
	
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
	private String retornoRelacionamento;

	private String retornoScr;
	private String observacao;
	private boolean excluido;
	
	private String retornoCNDTrabalhistaTST;
	private String retornoCNDFederal;
	private String retornoCNDEstadual;
	
	//private List<PlexiConsulta> plexiConsultas = new ArrayList<PlexiConsulta>();
	private Set<PlexiConsulta> plexiConsultas = new HashSet<>();
	private Set<NetrinConsulta> netrinConsultas = new HashSet<>();
	private Set<DocketConsulta> docketConsultas = new HashSet<>();
	private List<String> estadosConsulta = new ArrayList<String>();
	private String estadosConsultaStr;
	
	private Set<GravamesRea> gravamesRea = new HashSet<>();
	
	private boolean politicamenteExposta = false;
	private boolean isPepVip = false;
	private String pessoasPoliticamenteExpostas = "0";
	private String dialogHeader;
	
	
	private boolean isPefinRefinAvailable = false;
	private boolean isCcfApontamentosAvailable = false;
	private boolean isProtestosAvailable = false;
	private boolean isScoreBaixo = false;
	private boolean isDividaVencidaAvailable = false;
	private boolean isPrejuizoBacenAvailable = false;
	private boolean isRelacionamentoBacenIniciadoAvailable = false;
	private boolean isRiscoTotalAvailable = false;
	private FileUploaded file;
	private String ressalvaProcessosNome = "";
	private String ressalvaTrabalhistaNome = "";
	private String ressalvaPefinNome = "";
	private String ressalvaProtestoNome = "";
	private String ressalvaCcfNome = "";
	private boolean contemAcoesEngine = false;
	private boolean contemAcoesProcesso = false;
	private boolean isScoreBaixo450 = false;
	private boolean isScoreBaixo700 = false;
	private boolean isInicioRelacionamentoInexistente = false;
	private boolean isRiscoTotal20k = false;
	private boolean isRiscoTotal50k = false;
	private boolean isProtestoCenprotAvailable = false;
	
	private List<DocumentoAnaliseResumo> resumorelacionamentos;
	

	public List<DocumentoAnaliseResumo> getResumoProcesso() {
		List<DocumentoAnaliseResumo> vProcesso = new ArrayList<>();
		br.com.galleriabank.bigdata.cliente.model.processos.ProcessoResult processoResponse = null;
		try {
			processoResponse = GsonUtil.fromJson(getRetornoProcesso() , br.com.galleriabank.bigdata.cliente.model.processos.ProcessoResult.class);
		} catch (Exception erro) {
			vProcesso.add(new DocumentoAnaliseResumo(null, null));
		}
		if (processoResponse == null) {
			vProcesso.add(new DocumentoAnaliseResumo("não disponível", null));
		} else {
			br.com.galleriabank.bigdata.cliente.model.processos.ProcessoResumo processo = processoResponse.getProcessoResumo();
			if (processo.getCriminal() == null) {
				vProcesso.add(new DocumentoAnaliseResumo("Criminal:", "Nada consta"));
			} else {
				String processosQuantidade = CommonsUtil.stringValue(processo.getCriminal().stream().mapToInt(p -> p.getQuatidade()).sum());
				Double processosValor = processo.getCriminal().stream().mapToDouble(p -> p.getValor()).sum();
				vProcesso.add(new DocumentoAnaliseResumo("Criminal:", String.format("%,.2f", processosValor) + " (" + processosQuantidade + ")"));
				
				if (Integer.parseInt(processosQuantidade) > 0) {
					contemAcoesProcesso = true;
					ressalvaProcessosNome = processoResponse.getNome();
				}
			}

			if (processo.getTrabalhista() == null) {
				vProcesso.add(new DocumentoAnaliseResumo("Trabalhista:", "Nada consta"));
			} else {
				String processosQuantidade = CommonsUtil.stringValue(processo.getTrabalhista().stream().mapToInt(p -> p.getQuatidade()).sum());
				Double processosValor = processo.getTrabalhista().stream().mapToDouble(p -> p.getValor()).sum();
				
				vProcesso.add(new DocumentoAnaliseResumo("Trabalhista:", String.format("%,.2f", processosValor) + " (" + processosQuantidade + ")"));
				ressalvaTrabalhistaNome = processoResponse.getNome();
				
				if (Integer.parseInt(processosQuantidade) > 0) {
					contemAcoesProcesso = true;
				}
			}

			if (processo.getTituloExtraJudicial() == null) {
				vProcesso.add(new DocumentoAnaliseResumo("Execução de título:", "Nada consta"));
			} else {
				String processosQuantidade = CommonsUtil.stringValue(processo.getTituloExtraJudicial().stream().mapToInt(p -> p.getQuatidade()).sum());
				Double processosValor = processo.getTituloExtraJudicial().stream().mapToDouble(p -> p.getValor()).sum();
				vProcesso.add(new DocumentoAnaliseResumo("Execução de título:", String.format("%,.2f", processosValor) + " (" + processosQuantidade + ")"));
				
				if (Integer.parseInt(processosQuantidade) > 0) {
					contemAcoesProcesso = true;
				}
			}

			if (processo.getTituloExecucaoFiscal() == null) {
				vProcesso.add(new DocumentoAnaliseResumo("Execução Fiscal:", "Nada consta"));
			} else {
				String processosQuantidade = CommonsUtil.stringValue(processo.getTituloExecucaoFiscal().stream().mapToInt(p -> p.getQuatidade()).sum());
				Double processosValor = processo.getTituloExecucaoFiscal().stream().mapToDouble(p -> p.getValor()).sum();
				vProcesso.add(new DocumentoAnaliseResumo("Execução Fiscal:", String.format("%,.2f", processosValor) + " (" + processosQuantidade + ")"));
				
				if (Integer.parseInt(processosQuantidade) > 0) {
					contemAcoesProcesso = true;
				}
			}

			if (processo.getOutros() == null) {
				vProcesso.add(new DocumentoAnaliseResumo("Outros:", "Nada consta"));
			} else {
				String processosQuantidade = CommonsUtil.stringValue(processo.getOutros().stream().mapToInt(p -> p.getQuatidade()).sum());
				Double processosValor = processo.getOutros().stream().mapToDouble(p -> p.getValor()).sum();
				vProcesso.add(new DocumentoAnaliseResumo("Outros:", String.format("%,.2f", processosValor) + " (" + processosQuantidade + ")"));
				
				if (Integer.parseInt(processosQuantidade) > 0) {
					contemAcoesProcesso = true;
				}
			}

		}
		return vProcesso;
	}
	
	private String dialogHeader() {
		String str = "";
		EngineRetorno engine = null;
		engine = GsonUtil.fromJson(getRetornoEngine(), EngineRetorno.class);
		if (engine != null) {
			if (engine.getConsultaCompleta() != null) {
				EngineRetornoRequestFields nome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome"))
						.findFirst().orElse(null);

				EngineRetornoRequestFields cpf = engine.getRequestFields().stream().filter(g -> g.getField().equals("cpf"))
							.findFirst().orElse(null);

				EngineRetornoRequestFields cnpj = engine.getRequestFields().stream()
							.filter(s -> s.getField().equals("cnpj")).findFirst().orElse(null);
				
				if (nome != null) {
					str = nome.getValue();
					str = str.trim();
				}
				
				if (cpf != null) {
					if (engine.getConsultaCompleta() != null && engine.getConsultaCompleta().getBestInfo().getAge() != null) {
						str = String.join(" - ", str, engine.getConsultaCompleta().getBestInfo().getAge() + " Anos");
					} else {
						str = String.join(" - ", str, null);
					}
					
					str = String.join(" - ", str, cpf.getValue());
				}
				
				if (cnpj != null) {
					str = String.join(" - ", str, cnpj.getValue());
				}
				if (getMotivoAnalise() != null && !getMotivoAnalise().isEmpty()) {
					str = String.join(" - ", str, getMotivoAnalise());
				}
			} else {
				if (engine.getDadosCadastraisPJ() != null) {
					if (engine.getDadosCadastraisPJ().getBestInfo() != null) {						
						str = engine.getDadosCadastraisPJ().getBestInfo().getCompanyName();
						str = str.trim();
						str = String.join(" - ", str, engine.getDadosCadastraisPJ().getCnpj());
					}
				}
			}			
		} else {
			str = this.getIdentificacao();
			str = String.join(" - ", str, this.cnpjcpf);

		}
		if (this.pagador.getInicioEmpresa() != null) {
			str = String.join(" - ", str, calcularIdade(this.pagador.getInicioEmpresa()) + " Anos");
		}
		return str;
	}
	
	public String calcularIdade(Date data) {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date dateHoje = dataHoje.getTime();

		long idadeLong = dateHoje.getTime() - data.getTime();
		idadeLong = TimeUnit.DAYS.convert(idadeLong, TimeUnit.MILLISECONDS);
		idadeLong = idadeLong / 30;
		idadeLong = idadeLong / 12;
		
		return CommonsUtil.stringValue(idadeLong);
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
			populaResumoEngine(engine, result);
		}
		return result;
	}
	
	private void populaResumoEngine(EngineRetorno engine, List<DocumentoAnaliseResumo> result) {
		EngineRetornoExecutionResultConsultaNegative engineRetorno = (engine.getDadosCadastraisPJ() != null)
				? engine.getDadosCadastraisPJ().getNegative()
				: (engine.getConsultaCompleta() != null ? engine.getConsultaCompleta().getNegative() : null);

		EngineRetornoExecutionResultProcessos processo = (engine.getDadosCadastraisPJ() != null)
				? engine.getProcessosPJ()
				: engine.getProcessos();

		if (engine.getDadosCadastraisPJ() == null && engine.getConsultaCompleta() == null) {
			result.add(new DocumentoAnaliseResumo("Score:", "Não disponivel"));
			result.add(new DocumentoAnaliseResumo("Prob. Pag:", "Não disponivel"));
		} else {
			EngineRetornoExecutionResultConsultaQuodScore score = (engine.getDadosCadastraisPJ() != null)
					? engine.getDadosCadastraisPJ().getQuodScorePJ()
					: engine.getConsultaCompleta().getQuodScore();

			result.add(new DocumentoAnaliseResumo("Score:", CommonsUtil.stringValue(score.getScore())));

			if (score.getScore() > 0 && score.getScore() < 450) {
				isScoreBaixo450 = true;
			} else if (score.getScore() > 451 && score.getScore() < 700) {
				isScoreBaixo700 = true;
			}

			result.add(new DocumentoAnaliseResumo("Prob. Pag:",
					CommonsUtil.stringValue(score.getProbabilityOfPayment() + "%")));
		}

		if (engine.getDadosCadastraisPJ() == null && engine.getConsultaCompleta() == null) {
			result.add(new DocumentoAnaliseResumo("Pefin/Refin:", "Não disponível"));
		} else {
			if (engineRetorno.getTotalApontamentos() > 0) {
				result.add(new DocumentoAnaliseResumo("Pefin/Refin:",
						String.format("%,.2f", engineRetorno.getTotalValorApontamentos()) + " ("
								+ CommonsUtil.stringValue(engineRetorno.getTotalApontamentos()) + ")"));
				if (engineRetorno.getTotalValorApontamentos().compareTo(new BigDecimal(1000)) > 0) {
					isPefinRefinAvailable = true;
				}
				ressalvaPefinNome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome"))
						.findFirst().orElse(null).getValue();
			} else {
				result.add(new DocumentoAnaliseResumo("Pefin/Refin:", "0"));
			}
		}

		if (engine.getDadosCadastraisPJ() == null && engine.getConsultaCompleta() == null) {
			result.add(new DocumentoAnaliseResumo("Protesto:", "Não disponível"));
		} else {
			if (engineRetorno.getTotalProtests() > 0) {
				result.add(new DocumentoAnaliseResumo("Protesto:",
						String.format("%,.2f", engineRetorno.getTotalValorProtests()) + " ("
								+ CommonsUtil.stringValue(engineRetorno.getTotalProtests()) + ")"));
				if (engineRetorno.getTotalValorProtests().compareTo(new BigDecimal(1000)) > 0) {
					isProtestosAvailable = true;
				}
				ressalvaProtestoNome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome"))
						.findFirst().orElse(null).getValue();
			} else {
				result.add(new DocumentoAnaliseResumo("Protesto:", "0"));
			}
		}

		if (engine.getDadosCadastraisPJ() == null && engine.getConsultaCompleta() == null) {
			result.add(new DocumentoAnaliseResumo("Cheque sem fundo:", "Não disponível"));
		} else {
			if (engineRetorno.getTotalCcfApontamentos() > 0) {
				result.add(new DocumentoAnaliseResumo("Cheque sem fundo:",
						CommonsUtil.stringValue(engineRetorno.getTotalCcfApontamentos())));
				isCcfApontamentosAvailable = true;
				ressalvaCcfNome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome"))
						.findFirst().orElse(null).getValue();
			} else {
				result.add(new DocumentoAnaliseResumo("Cheque sem fundo:", "0"));
			}
		}

		if (engine.getDadosCadastraisPJ() == null && engine.getConsultaCompleta() == null) {
			result.add(new DocumentoAnaliseResumo("Ações Judiciais:", "Não disponível"));
		} else {
			if (engineRetorno.getTotalLawSuitApontamentos() > 0) {
				result.add(new DocumentoAnaliseResumo("Ações Judiciais:",
						String.format("%,.2f", engineRetorno.getTotalValorLawSuitApontamentos()) + " ("
								+ CommonsUtil.stringValue(engineRetorno.getTotalLawSuitApontamentos()) + ")"));

			} else {
				result.add(new DocumentoAnaliseResumo("Ações Judiciais:", "0"));
			}
		}

		if (engine.getDadosCadastraisPJ() != null && engine.getConsultaCompleta() == null) {
			if (engine.getDadosCadastraisPJ().getBestInfo() == null) {
				result.add(new DocumentoAnaliseResumo("Situação Cadastral:", "Nada consta"));
			}
			result.add(new DocumentoAnaliseResumo("Situação Cadastral:",
					engine.getDadosCadastraisPJ().getBestInfo().getCompanyStatus()));
		}

		if (processo == null) {
			result.add(new DocumentoAnaliseResumo("Nº de processos judiciais:", "Não disponível"));
		} else {
			result.add(new DocumentoAnaliseResumo("Nº de processos judiciais:", CommonsUtil.stringValue(
					processo.getTotal_acoes_judicias_reu() + engineRetorno.getTotalLawSuitApontamentos())));

			if (processo.getTotal_acoes_judicias_reu() + engineRetorno.getTotalLawSuitApontamentos() > 0) {
				contemAcoesEngine = true;
				ressalvaProcessosNome = engine.getRequestFields().stream().filter(f -> f.getField().equals("nome"))
						.findFirst().orElse(null).getValue();
			}
		}

		if (engine.getConsultaCompleta() != null && engine.getDadosCadastraisPJ() == null) {
			if (engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("Participação em empresas:", "Não disponível"));
			} else {
				if (engine.getConsultaCompleta().getEnterpriseData().getPartnership() != null) {
					result.add(new DocumentoAnaliseResumo("Participação em empresas:", CommonsUtil.stringValue(engine
							.getConsultaCompleta().getEnterpriseData().getPartnership().getPartnerships().size())));
				} else {
					result.add(new DocumentoAnaliseResumo("Participação em empresas:", "0"));
				}
			}

			if (engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("PEP ou VIP:", "Não disponível"));
			} else {
				if (CommonsUtil.mesmoValor(engine.getConsultaCompleta().getErrorMessage(), "Cliente PEP ou VIP")) {
					result.add(new DocumentoAnaliseResumo("PEP ou VIP:", "Sim"));
				} else {
					result.add(new DocumentoAnaliseResumo("PEP ou VIP:", "Não"));
				}
			}

			if (engine.getConsultaCompleta() == null) {
				result.add(new DocumentoAnaliseResumo("PEP Relacionado:", "Não disponível"));
			} else {
				if (engine.getPep()) {
					result.add(new DocumentoAnaliseResumo("PEP Relacionado:", "Sim"));
				} else {
					result.add(new DocumentoAnaliseResumo("PEP Relacionado:", "Não"));
				}
			}

			if (engine.getConsultaAntecedenteCriminais() == null) {
				result.add(new DocumentoAnaliseResumo("Antecedentes criminais:", "Não disponível"));
			} else {
				EngineRetornoExecutionResultAntecedenteCriminaisEvidences mensagem = engine
						.getConsultaAntecedenteCriminais().getEvidences();
				result.add(new DocumentoAnaliseResumo("Antecedentes criminais:",
						(mensagem.getMessage() != null) ? mensagem.getMessage() : "Nada consta"));
			}
		}
	}

	public List<DocumentoAnaliseResumo> getResumoCenprot() {
		List<DocumentoAnaliseResumo> cenprot = new ArrayList<>();

		CenprotResponse data = GsonUtil.fromJson(getRetornoCenprot(), CenprotResponse.class);
		if (data == null) {
			cenprot.add(new DocumentoAnaliseResumo("Não disponível", null));
		} else {

			if (CommonsUtil.semValor(data.getCenprotProtestos().getProtestosBrasil().getEstados())) {
				cenprot.add(new DocumentoAnaliseResumo("Nada consta", null));
			} else {
				String valorEstado = "";
				for (ProtestosBrasilEstado estado : data.getCenprotProtestos().getProtestosBrasil().getEstados()) {

					valorEstado = CommonsUtil.stringValue(String.format("%,.2f", estado.getValorTotal())) + " (" + estado.getQuantidadeTotal()
							+ ") ";
					cenprot.add(new DocumentoAnaliseResumo(estado.getEstado(), valorEstado));
				}
				if (new BigDecimal(valorEstado).compareTo(new BigDecimal(1000)) > 0) {
					isProtestoCenprotAvailable = true;
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
				if (dado.getResumoDoClienteTraduzido().getCarteiraVencido().compareTo(new BigDecimal(1000)) > 0) {
					isDividaVencidaAvailable = true;
				}
			}

			if (dado.getResumoDoClienteTraduzido().getPrejuizo() == null) {
				scr.add(new DocumentoAnaliseResumo("Prejuizo:", "Não Disponível"));
			} else {
				String prejuizo = CommonsUtil.formataValorMonetario(dado.getResumoDoClienteTraduzido().getPrejuizo());
				scr.add(new DocumentoAnaliseResumo("Prejuizo:", prejuizo));
				if (dado.getResumoDoClienteTraduzido().getPrejuizo().compareTo(new BigDecimal(1000)) > 0) {
					isPrejuizoBacenAvailable = true;
				}
			}			
			
			if (dado.getResumoDoClienteTraduzido().getCarteiradeCredito() == null) {
			    scr.add(new DocumentoAnaliseResumo("Carteira de Crédito Total:", "Não Disponível"));
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
			
			if (dado.getResumoDoClienteTraduzido().getDtInicioRelacionamento() == null) {
				scr.add(new DocumentoAnaliseResumo("Data inicio relacionamento:", "--"));
				setInicioRelacionamentoInexistente(true);
			} else {		
				String[] str = dado.getResumoDoClienteTraduzido().getDtInicioRelacionamento().split("-");
				
				if (Integer.parseInt(str[0]) > 2018) {
					setInicioRelacionamentoBacen(true);
				}
				
				LinkedList<String> listStrData = new LinkedList<String>();
				for(int i = 0; i < str.length; ++i) {
					listStrData.addFirst(str[i]);
				}
				
				String dataRelacionamento = String.join("/", listStrData);
				
				scr.add(new DocumentoAnaliseResumo("Data inicio relacionamento:", dataRelacionamento));
			}
			
			if (dado.getResumoDoClienteTraduzido().getRiscoTotal().compareTo(new BigDecimal(20000)) < 0) {
				setRiscoTotal20k(true);
			} else if (dado.getResumoDoClienteTraduzido().getRiscoTotal().compareTo(new BigDecimal(20001)) > 0 
					&& dado.getResumoDoClienteTraduzido().getRiscoTotal().compareTo(new BigDecimal(50000)) < 0) {
				setRiscoTotal50k(true);
			}
		}

		return scr;
	}
	
	public List<DocumentoAnaliseResumo> getResumoRelacionamentos() {
		
		if (resumorelacionamentos != null) {
			return resumorelacionamentos;
		}
		resumorelacionamentos = new ArrayList<DocumentoAnaliseResumo>();
		
//		List<DocumentoAnaliseResumo> resumorelacionamentos = new ArrayList<>();

		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		List<RelacionamentoPagadorRecebedor> listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();

		listRelacoes = rprDao.getRelacionamentos(pagador, listRelacoes);
		for (RelacionamentoPagadorRecebedor relacionamentoPagadorRecebedor : listRelacoes) {

			PagadorRecebedor pessoaRelacao = new PagadorRecebedor();

			if (  CommonsUtil.mesmoValor( relacionamentoPagadorRecebedor.getPessoaRoot().getId(),pagador.getId()))
				pessoaRelacao = relacionamentoPagadorRecebedor.getPessoaChild();
			else
				pessoaRelacao = relacionamentoPagadorRecebedor.getPessoaRoot();

			String descricao = pessoaRelacao.getNome() + " "
					+ (!CommonsUtil.semValor(pessoaRelacao.getCpf()) ? "CPF: " + pessoaRelacao.getCpf()
							: "CNPJ: " + pessoaRelacao.getCnpj());
			String valor = relacionamentoPagadorRecebedor.getRelacao();

			resumorelacionamentos.add(new DocumentoAnaliseResumo(descricao, valor));
		}


		return resumorelacionamentos;

	}
	
 	public boolean isPodeChamarRea() {
		return isReaNaoEnviado() && CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}
	
	public boolean isPodeChamarCertidoes() {
		return !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
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
		return !CommonsUtil.semValor(engine) && !CommonsUtil.semValor(engine.getIdCallManager());
	}

	public boolean isPodeChamarSerasa() {
		return !isSerasaProcessado() && (CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL")
				|| CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "COMPRADOR"));
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

	public boolean isPodeChamarRelacionamentos() {
		return !isRelaciomentoProcessado() && CommonsUtil.mesmoValor("PJ", tipoPessoa)
				&& !CommonsUtil.mesmoValor(DocumentosAnaliseEnum.REA, tipoEnum);
	}

	public boolean isPodeChamarPpe() {
		return !isPpeProcessado() && CommonsUtil.mesmoValor("PF", tipoPessoa)
				&& (CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "PROPRIETARIO ATUAL") ||
						CommonsUtil.mesmoValor(this.motivoAnalise.toUpperCase(), "COMPRADOR"))
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
	
	public boolean isRelaciomentoProcessado() {
		return !CommonsUtil.semValor(retornoRelacionamento);
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
	
	public void adicionarEstadoCpf() {
		if(CommonsUtil.semValor(pagador))
			return;
		if(CommonsUtil.semValor(pagador.getCpf()))
			return;
		String cpfNum = CommonsUtil.somenteNumeros(pagador.getCpf()).trim();
		if(CommonsUtil.semValor(cpfNum.length() < 8))
			return;
	
		int index = CommonsUtil.integerValue(cpfNum.charAt(8));
		List<String> estadosReturn = pegarEstadoCpf(index);
		adicionaEstados(estadosReturn);
	}
	
	public List<String> pegarEstadoCpf(int i){
		List<String> estadosReturn = new ArrayList<String>();
		switch (i) {
		case 0:
			estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"RS"}));
			break;
		case 1:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"DF", "GO", "MT", "MS", "TO"}));
			break;
		case 2:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"PA", "AM", "AC", "AP", "RO", "RR"}));
			break;
		case 3:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"CE", "MA", "PI"}));
			break;
		case 4:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"PE", "RN", "PB", "AL"}));
			break;
		case 5:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"BA", "SE"}));
			break;
		case 6:
			estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"MG"}));
			break;
		case 7:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"RJ", "ES"}));
			break;
		case 8:
			estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"SP"}));
			break;
		case 9:
			//estadosReturn = new ArrayList<String>(Arrays.asList(new String[]{"PR", "SC"}));
			break;
		default:
			break;
		}
		return estadosReturn;
	}
	
	public void pegarEstadoImovel() {
		if(CommonsUtil.semValor(contratoCobranca)) 
			return;
		if(CommonsUtil.semValor(contratoCobranca.getImovel())) 
			return;
		if(CommonsUtil.semValor(contratoCobranca.getImovel().getEstado())) 
			return;
		
		adicionaEstados(contratoCobranca.getImovel().getEstado());
	}
	
	public void adiconarEstadosPeloCadastro() {
		adicionarEstadoCpf();
		pegarEstadoImovel();
	}
	
	public void adicionaEstados(List<String> estados) {
		if(CommonsUtil.semValor(estados))
			return;
		for (String estado : estados) {
			adicionaEstados(estado);
		}
	}
	
	public void adicionaEstados(String estado) {
		if(CommonsUtil.semValor(estado))
			return;
		if(getEstadosConsulta().contains(estado)) 
			return;
		if(estado.length() != 2) 
			return;
		if(!CommonsUtil.semValor(estado) && !getEstadosConsulta().contains(estado)) {
			List<String> aux = getEstadosConsulta();
			aux.add(estado);
			estadosConsultaStr = aux.toString();
		}
	}
	
	public void removerEstado(String estado) {
		if(!CommonsUtil.semValor(estado) && getEstadosConsulta().contains(estado)) {
			List<String> aux = getEstadosConsulta();
			aux.remove(estado);
			estadosConsultaStr = aux.toString();
		}
	}
	
	public Map<String, byte[]> zipDeCertidoes(){
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		listaArquivos.putAll(zipEngine());
		listaArquivos.putAll(zipProtesto());
		listaArquivos.putAll(zipProcesso());
		listaArquivos.putAll(zipScr());
		for(DocketConsulta docket : docketConsultas) {
			listaArquivos.putAll(zipDocket(docket));
		}
		for(PlexiConsulta plexi : plexiConsultas) {
			listaArquivos.putAll(zipPlexi(plexi));
		}
		for(NetrinConsulta netrin : netrinConsultas) {
			listaArquivos.putAll(zipNetrin(netrin));
		}
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipEngine() {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		String primeiroNome = getPagador().getNome().split(" ")[0];
		String nomeArquivo = "Engine " + primeiroNome.replace(",", "_") + 
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		if(CommonsUtil.semValor(getEngine())) 
			return listaArquivos;
		EngineService engineService = new EngineService();
		engineService.baixarDocumentoEngine(getEngine());
		if(CommonsUtil.semValor(getEngine().getPdfBase64())) 
			return listaArquivos;
		String documentoBase64 = getEngine().getPdfBase64();
		byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
		listaArquivos.put(nomeArquivo, pdfBytes);
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipProtesto() {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		String primeiroNome = getPagador().getNome().split(" ")[0];
		String nomeArquivo = "Cenprot "+ primeiroNome.replace(",", "_") + 
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		NetrinService netrin = new NetrinService();
		String documentoBase64 = netrin.baixarDocumento(this);
		if(!CommonsUtil.semValor(documentoBase64)) {
			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			listaArquivos.put(nomeArquivo, pdfBytes);
		}
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipProcesso() {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		String primeiroNome = getPagador().getNome().split(" ")[0];
		String nomeArquivo = "Processos " + primeiroNome.replace(",", "_") + 
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		BigDataService bigData = new BigDataService();
		String documentoBase64 = bigData.baixarDocumentoProcesso(this);
		if(!CommonsUtil.semValor(documentoBase64)) {
			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			listaArquivos.put(nomeArquivo, pdfBytes);
		}
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipScr() {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		if(CommonsUtil.semValor(getRetornoScr()))
			return listaArquivos;
		String primeiroNome = getPagador().getNome().split(" ")[0];
		String nomeArquivo = "SCR " + primeiroNome.replace(",", "_") +
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		FileGenerator fileGenerator = new FileGenerator();
		fileGenerator.setDocumento(this.getCnpjcpf());
		ScrResult scrResult = GsonUtil.fromJson(getRetornoScr(), ScrResult.class);
		ScrService scrService = new ScrService();
		byte[] pdfBytes = scrService.geraContrato(scrResult, fileGenerator);
		listaArquivos.put(nomeArquivo, pdfBytes);
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipDocket(DocketConsulta docket) {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		String nomeArquivo = docket.getDocketDocumentos().getDocumentoNome() +
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		String documentoBase64 = docket.getPdf();
		if(!CommonsUtil.semValor(documentoBase64)) {
			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			listaArquivos.put(nomeArquivo, pdfBytes);
		}
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipPlexi(PlexiConsulta plexi) {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		String primeiroNome = getPagador().getNome().split(" ")[0];
		String nomeArquivo = plexi.getNomeCompleto() + " " + primeiroNome.replace(",", "_") + 
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		String documentoBase64 = plexi.getPdf();
		if(!CommonsUtil.semValor(documentoBase64)) {
			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			listaArquivos.put(nomeArquivo, pdfBytes);
		}
		return listaArquivos;
	}
	
	public Map<String, byte[]> zipNetrin(NetrinConsulta netrin) {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		String url = netrin.getNetrinDocumentos().getUrlService();
		String nomedoc = "";
		if (CommonsUtil.mesmoValor(url, "/api/v1/processo")) {
			nomedoc = "Consulta Processual";
		} else if (CommonsUtil.mesmoValor(url, "/api/v1/CNDEstadual")) {
			nomedoc = "CND Estadual " +  netrin.getUf().toUpperCase();
		} else if (CommonsUtil.mesmoValor(url, "/api/v1/CNDFederal")) {
			nomedoc = "CND Federal";
		} else if (CommonsUtil.mesmoValor(url, "/api/v1/CNDTrabalhistaTST")) {
			nomedoc = "CNDT TST";
		}
		String primeiroNome = getPagador().getNome().split(" ")[0];
		String nomeArquivo = nomedoc + " " + primeiroNome.replace(",", "_") + 
				"_" + CommonsUtil.somenteNumeros(getPagador().getCpfCnpj()) + ".pdf";
		String documentoBase64 = netrin.getPdf();
		if(!CommonsUtil.semValor(documentoBase64)) {
			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			listaArquivos.put(nomeArquivo, pdfBytes);
		}
		return listaArquivos;
	}
	
	public List<SelectItem>motivosAnalise(){
		List<SelectItem> motivos = new ArrayList<SelectItem>();
		motivos.add(new SelectItem("Proprietario Atual","Proprietario Atual"));
		motivos.add(new SelectItem("Proprietario Anterior","Proprietario Anterior"));
		motivos.add(new SelectItem("Comprador","Comprador"));
		if(CommonsUtil.mesmoValor(tipoPessoa, "PJ")) {
			motivos.add(new SelectItem("Empresa Vinculada ao Proprietario Atual","Empresa Vinculada ao Proprietario Atual"));
			motivos.add(new SelectItem("Empresa Vinculada ao Proprietario Anterior","Empresa Vinculada ao Proprietario Anterior"));
			motivos.add(new SelectItem("Empresa Vinculada ao Comprador","Empresa Vinculada ao Comprador"));
			motivos.add(new SelectItem("Empresa Vinculada ao Sócio Vinculado ao Proprietario Atual","Empresa Vinculada ao Sócio Vinculado ao Proprietario Atual"));
		} else if(CommonsUtil.mesmoValor(tipoPessoa, "PF")) {
			motivos.add(new SelectItem("Sócio Vinculado ao Proprietario Atual","Sócio Vinculado ao Proprietario Atual"));
			motivos.add(new SelectItem("Sócio Vinculado ao Proprietario Anterior","Sócio Vinculado ao Proprietario Anterior"));
			motivos.add(new SelectItem("Sócio Vinculado ao Comprador","Sócio Vinculado ao Comprador"));
		}
		motivos.add(new SelectItem("Matricula para consulta","Matricula para consulta"));
		return motivos;
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

	public boolean isLiberadoCertidoes() {
		return liberadoCertidoes;
	}

	public void setLiberadoCertidoes(boolean liberadoCertidoes) {
		this.liberadoCertidoes = liberadoCertidoes;
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
	public void setRetornoLaudoRobo(String retornoLaudoRobo) {
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
		adiconarEstadosPeloCadastro();
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
	
	public Set<PlexiConsulta> getPlexiConsultas() {
		return plexiConsultas;
	}

	public void setPlexiConsultas(Set<PlexiConsulta> plexiConsultas) {
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
		return estadosConsultaStr;
	}

	public void setEstadosConsultaStr(String estadosConsultaStr) {
		this.estadosConsultaStr = estadosConsultaStr;
	}

	public Set<NetrinConsulta> getNetrinConsultas() {
		return netrinConsultas;
	}

	public void setNetrinConsultas(Set<NetrinConsulta> netrinConsultas) {
		this.netrinConsultas = netrinConsultas;
	}
	
	private void setPessoasPoliticamenteExpostas(int pessoasPoliticamenteExpostas) {
		this.pessoasPoliticamenteExpostas = Integer.toString(pessoasPoliticamenteExpostas);
	}
	
  public String getDialogHeader() {
    	dialogHeader = dialogHeader();
        return dialogHeader;
    }

	public Set<DocketConsulta> getDocketConsultas() {
		return docketConsultas;
	}

	public void setDocketConsultas(Set<DocketConsulta> docketConsultas) {
		this.docketConsultas = docketConsultas;
	}
	
	public boolean isPefinRefinAvailable() {
		return isPefinRefinAvailable;
	}

	public void setHasPefinRefin(boolean hasPefinRefin) {
		this.isPefinRefinAvailable = hasPefinRefin;
	}

	public boolean isCcfApontamentosAvailable() {
		return isCcfApontamentosAvailable;
	}

	public void setHasCcfApontamentos(boolean hasCcfApontamentos) {
		this.isCcfApontamentosAvailable = hasCcfApontamentos;
	}

	public boolean isProtestosAvailable() {
		return isProtestosAvailable;
	}

	public void setHasProtestos(boolean hasProtestos) {
		this.isProtestosAvailable = hasProtestos;
	}

	public boolean isScoreBaixo() {
		return isScoreBaixo;
	}

	public void setHasScoreBaixo(boolean hasScoreBaixo) {
		this.isScoreBaixo = hasScoreBaixo;
	}

	public boolean isDividaVencidaAvailable() {
		return isDividaVencidaAvailable;
	}

	public void setHasDividaVencida(boolean hasDividaVencida) {
		this.isDividaVencidaAvailable = hasDividaVencida;
	}

	public boolean isPrejuizoBacenAvailable() {
		return isPrejuizoBacenAvailable;
	}

	public void setHasPrejuizoBacen(boolean hasPrejuizoBacen) {
		this.isPrejuizoBacenAvailable = hasPrejuizoBacen;
	}

	public boolean isRelacionamentoBacenIniciadoAvailable() {
		return isRelacionamentoBacenIniciadoAvailable;
	}

	public void setInicioRelacionamentoBacen(boolean inicioRelacionamentoBacen) {
		this.isRelacionamentoBacenIniciadoAvailable = inicioRelacionamentoBacen;
	}

	public boolean isRiscoTotalAvailable() {
		return isRiscoTotalAvailable;
	}

	public void setHasRiscoTotal(boolean hasRiscoTotal) {
		this.isRiscoTotalAvailable = hasRiscoTotal;
	}

	public String getRetornoRelacionamento() {
		return retornoRelacionamento;
	}

	public void setRetornoRelacionamento(String retornoRelacionamento) {
		this.retornoRelacionamento = retornoRelacionamento;
	}
	
	public String getRessalvaProcessosNome() {
		return ressalvaProcessosNome;
	}

	public String getRessalvaTrabalhistaNome() {
		return ressalvaTrabalhistaNome;
	}

	public String getRessalvaPefinNome() {
		return ressalvaPefinNome;
	}

	public String getRessalvaProtestoNome() {
		return ressalvaProtestoNome;
	}

	public String getRessalvaCcfNome() {
		return ressalvaCcfNome;
	}

	public boolean isContemAcoesEngine() {
		return contemAcoesEngine;
	}

	public boolean isContemAcoesProcesso() {
		return contemAcoesProcesso;
	}

	public boolean isInicioRelacionamentoInexistente() {
		return isInicioRelacionamentoInexistente;
	}

	public void setInicioRelacionamentoInexistente(boolean isInicioRelacionamentoInexistente) {
		this.isInicioRelacionamentoInexistente = isInicioRelacionamentoInexistente;
	}

	public boolean isRiscoTotal20k() {
		return isRiscoTotal20k;
	}

	public void setRiscoTotal20k(boolean isRiscoTotal20k) {
		this.isRiscoTotal20k = isRiscoTotal20k;
	}

	public boolean isRiscoTotal50k() {
		return isRiscoTotal50k;
	}

	public void setRiscoTotal50k(boolean isRiscoTotal50k) {
		this.isRiscoTotal50k = isRiscoTotal50k;
	}

	public boolean isScoreBaixo450() {
		return isScoreBaixo450;
	}

	public void setScoreBaixo450(boolean isScoreBaixo450) {
		this.isScoreBaixo450 = isScoreBaixo450;
	}

	public boolean isScoreBaixo700() {
		return isScoreBaixo700;
	}

	public void setScoreBaixo700(boolean isScoreBaixo700) {
		this.isScoreBaixo700 = isScoreBaixo700;
	}

	public boolean isProtestoCenprotAvailable() {
		return isProtestoCenprotAvailable;
	}

	public void setProtestoCenprotAvailable(boolean isProtestoCenprotAvailable) {
		this.isProtestoCenprotAvailable = isProtestoCenprotAvailable;
	}

	public Set<GravamesRea> getGravamesRea() {
		return gravamesRea;
	}

	public void setGravamesRea(Set<GravamesRea> gravamesRea) {
		this.gravamesRea = gravamesRea;
	}
}
