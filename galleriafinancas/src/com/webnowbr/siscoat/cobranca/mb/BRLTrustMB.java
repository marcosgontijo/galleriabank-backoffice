package com.webnowbr.siscoat.cobranca.mb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaBRLLiquidacao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;


@ManagedBean(name = "brlTrustMB")
@SessionScoped
public class BRLTrustMB {
	
	private boolean jsonGerado;
	private String pathJSON;
	private String nomeJSON;	
	private StreamedContent file;	
	
	List<ContratoCobranca> contratos = new ArrayList<ContratoCobranca>();
	private List<ContratoCobranca> selectedContratos = new ArrayList<ContratoCobranca>();
	ContratoCobranca objetoContratoCobranca = new ContratoCobranca();
	
	private String numContrato;
	private String cedenteCessao;
	private Date dataAquisicao;
	private Date dataBaixaInicial;
	private Date dataBaixaFinal;
	
	private boolean usaTaxaJurosDiferenciada;
	private BigDecimal txJurosCessao;
	
	private BigDecimal valorTotalFaceCessao;
	private BigDecimal valorTotalAquisicaoCessao;
	
	private BigDecimal somatoriaValorePresenteContratos;
	
	List<ContratoCobrancaBRLLiquidacao> parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
	ContratoCobrancaBRLLiquidacao parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();

	public String clearFieldsBRLJson() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		
		this.usaTaxaJurosDiferenciada = false;
		this.txJurosCessao = BigDecimal.ZERO;
		
		this.jsonGerado = false;
		
		this.valorTotalFaceCessao = BigDecimal.ZERO;
		this.valorTotalAquisicaoCessao = BigDecimal.ZERO;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJson.xhtml";
	}
	
	public void pesquisaContratosJSONCessao() {
		// cedenteBRLCessao;
		// dataAquisicaoCessao;
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.contratos = cDao.consultaContratosJSONCessao();
	}
	
	public void pesquisaContratosCessao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!this.cedenteCessao.equals("")) {
			if (this.numContrato.length() == 4) {
				this.numContrato = "0" + this.numContrato;
			} 
			
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			this.contratos = contratoCobrancaDao.consultaContratosBRLCessao(this.numContrato);
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"BRL JSON: Pesquisa efetuada com sucesso!",
							""));	
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"BRL JSON: Informe o cedente para a consulta dos contratos!",
							""));
		}
	}
	
	public String clearFieldsBRLJsonLiquidacao() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		
		this.dataBaixaInicial = gerarDataOntem();
		this.dataBaixaFinal = gerarDataOntem();
		
		this.parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
		this.parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();
		
		this.jsonGerado = false;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJsonLiquidacao.xhtml";
	}
	
	public String clearFieldsBRLJsonLiquidacaoMigracao() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.selectedContratos = new ArrayList<ContratoCobranca>();
		
		this.dataAquisicao = new Date();
		
		this.dataBaixaInicial = gerarDataOntem();
		this.dataBaixaFinal = gerarDataOntem();
		
		this.parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
		this.parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();
		
		this.somatoriaValorePresenteContratos = BigDecimal.ZERO;
		
		this.jsonGerado = false;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJsonMigracao.xhtml";
	}
	
    public void rowSelected() {
    	this.somatoriaValorePresenteContratos = calculaValorPresenteTotalContrato();
    }
 
    public void rowUnSelected() {
    	this.somatoriaValorePresenteContratos = calculaValorPresenteTotalContrato();
    }
    
    private BigDecimal calculaValorPresenteTotalContrato() {
    	BigDecimal total = BigDecimal.ZERO;
    	
    	for (ContratoCobranca contrato : this.selectedContratos) {
    		total = total.add(contrato.getSomatoriaValorPresente());
    	}
    	
    	return total;
    }
	
	public void pesquisaContratosLiquidacao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.numContrato.length() == 4) {
			this.numContrato = "0" + this.numContrato;
		} 
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.parcelasLiquidacao = contratoCobrancaDao.consultaContratosBRLLiquidacao(this.dataBaixaInicial, this.dataBaixaFinal, this.cedenteCessao);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"BRL JSON: Pesquisa efetuada com sucesso!",
						""));	
	}
	
	public void pesquisaContratosLiquidacaoMigracao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.numContrato.length() == 4) {
			this.numContrato = "0" + this.numContrato;
		} 
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratos = contratoCobrancaDao.consultaContratosBRLLiquidacaoMigracao(this.cedenteCessao);
		
		Date dataHoje = gerarDataHoje();
		BigDecimal somatoriaValorPresente = BigDecimal.ZERO;
		
		for (ContratoCobranca contrato : this.contratos) {
			somatoriaValorPresente = BigDecimal.ZERO;
			
			for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
				if (parcela.getDataVencimento().after(dataHoje)) {
					somatoriaValorPresente = somatoriaValorPresente.add(calcularValorPresenteParcela(parcela.getId(), contrato.getTxJurosParcelas(), dataHoje));
				}
			}
			
			contrato.setSomatoriaValorPresente(somatoriaValorPresente);
		}

		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"BRL JSON: Pesquisa efetuada com sucesso!",
						""));	
	}
	
	public void atualizaValorParcelaSemIPCA() {
		SimulacaoVO simulacaoVO = calcularParcelas();
		ContratoCobrancaDetalhesDao cDetalhesDao = new ContratoCobrancaDetalhesDao();
		
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			BigDecimal valorFace = BigDecimal.ZERO;
			BigDecimal valorJuros = BigDecimal.ZERO;
			BigDecimal valorAmortizacao = BigDecimal.ZERO;
			String numeroParcelaStr = parcela.getNumeroParcela();
			
			for (SimulacaoDetalheVO parcelasSimulacao : simulacaoVO.getParcelas()) {
				valorFace = BigDecimal.ZERO;
				valorJuros = BigDecimal.ZERO;
				valorAmortizacao = BigDecimal.ZERO;
				
				if (numeroParcelaStr.equals(parcelasSimulacao.getNumeroParcela().toString())) {
					valorJuros = parcelasSimulacao.getJuros();
					valorAmortizacao = parcelasSimulacao.getAmortizacao();
					break; 
				}
			}

			parcela.setValorJurosSemIPCA(valorJuros);
			parcela.setValorAmortizacaoSemIPCA(valorAmortizacao);
			
			cDetalhesDao.merge(parcela);
		}
	}
	
	public void atualizaContratoDadosCessaoBRL() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.objetoContratoCobranca.setCedenteBRLCessao(this.cedenteCessao);
		this.objetoContratoCobranca.setDataAquisicaoCessao(this.dataAquisicao);
		
		if (this.usaTaxaJurosDiferenciada) {
			this.objetoContratoCobranca.setTxJurosCessao(this.txJurosCessao);
		} else {
			this.objetoContratoCobranca.setTxJurosCessao(this.objetoContratoCobranca.getTxJurosParcelas());
		}
		
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
	}
	
	public static int mesesEntre(Calendar inicial , Calendar fim ){  
		int qtdMesesIni = (inicial.get(Calendar.YEAR) * 12) + inicial.get(Calendar.MONTH);
		int qtdMesesFim = (fim.get(Calendar.YEAR) * 12) + fim.get(Calendar.MONTH);
		return qtdMesesFim - qtdMesesIni;
	}
	
	public Calendar getDateCalendar(Date data) {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");

		Calendar calendar = Calendar.getInstance(zone, locale);

		calendar.setTime(data);
		
		return calendar;
	}
	
	public void geraJSONCessao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(gerarDataHoje()) + this.objetoContratoCobranca.getNumeroContrato();
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = this.objetoContratoCobranca.getNumeroContrato() + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		/**
		 * verifica se tem parcela zero
		 */
		int countCarencia = 0;
		boolean temParcelaZeo = false;

		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcela.getNumeroParcela().equals("0")) {
				temParcelaZeo = true;
			}
		}
		
		if (temParcelaZeo) {
			countCarencia = this.objetoContratoCobranca.getMesesCarencia() + 1;
		} else {
			countCarencia = this.objetoContratoCobranca.getMesesCarencia();
		}
		/**
		 * FIM
		 */
		
		int countParcelas = 0;
		
		ContratoCobrancaMB contratoCobranca = new ContratoCobrancaMB();		
		
		atualizaContratoDadosCessaoBRL();
		
		/***
		 * INICIO - GET VALOR FACE SEM IPCA
		 */
		
		atualizaValorParcelaSemIPCA();
		
		/***
		 * FIM - GET VALOR FACE SEM IPCA
		 */
		
		/***
		 * CALCULA VALOR PRESENTE CONTRATO
		 */
		//BigDecimal valorTotalPresenteContrato = contratoCobranca.calcularValorPresenteTotalContrato(this.objetoContratoCobranca);
		BigDecimal valorTotalPresenteContrato = BigDecimal.ZERO;
		BigDecimal taxaJurosCessao = BigDecimal.ZERO;
				
		if (this.objetoContratoCobranca != null) {
			if (this.objetoContratoCobranca.getTxJurosCessao() != null) {
				taxaJurosCessao = this.objetoContratoCobranca.getTxJurosCessao();
			} else {
				taxaJurosCessao = this.objetoContratoCobranca.getTxJurosParcelas();
			}
		} else {
			taxaJurosCessao = this.objetoContratoCobranca.getTxJurosParcelas();
		}
		
		Date datahoje = gerarDataHoje();
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcela.getDataVencimento().after(datahoje)) {
				BigDecimal valorPresenteParcela = calcularValorPresenteParcela(parcela.getId(), taxaJurosCessao, this.objetoContratoCobranca.getDataAquisicaoCessao());
				valorTotalPresenteContrato = valorTotalPresenteContrato.add(valorPresenteParcela);
			}
		}
		
		valorTotalPresenteContrato = valorTotalPresenteContrato.divide(this.objetoContratoCobranca.getValorImovel(), 4, RoundingMode.HALF_DOWN);
		valorTotalPresenteContrato = valorTotalPresenteContrato.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
		
		/***
		 * FIM - CALCULA VALOR PRESENTE CONTRATO
		 */
		
		/**
		 * INICIO - CALCULA MESES CARENCIA
		 */
		int mesesCarencia = 0;
		Date dataHoje = gerarDataHoje();
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (!parcela.isParcelaPaga()) {
				mesesCarencia = mesesEntre(getDateCalendar(dataHoje),getDateCalendar(parcela.getDataVencimento()));
				break;
			}			
		}
		
		if (mesesCarencia > 0) {
			mesesCarencia = mesesCarencia - 1;			
		}
		/**
		 * FIM - CALCULA MESES CARENCIA
		 */
		
		this.valorTotalFaceCessao = BigDecimal.ZERO;
		this.valorTotalAquisicaoCessao = BigDecimal.ZERO;
		
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) { 
			countParcelas = countParcelas + 1;
			if (countParcelas > countCarencia) {
				if (parcela.getDataVencimento().after(this.dataAquisicao)) {
					JSONObject jsonRecebivel = new JSONObject();
					
					String numeroParcela = "";
					
					if (parcela.getNumeroParcela().length() == 1) {
						numeroParcela = "00" + parcela.getNumeroParcela();
					} else if (parcela.getNumeroParcela().length() == 2) {
						numeroParcela = "0" + parcela.getNumeroParcela();
					} else {
						numeroParcela = parcela.getNumeroParcela();
					}
					
					jsonRecebivel.put("numeroControle", this.objetoContratoCobranca.getNumeroContratoSeguro() + "-" + numeroParcela);
					jsonRecebivel.put("coobrigacao", false);
					jsonRecebivel.put("ocorrencia", 1);
					jsonRecebivel.put("tipo", 73);
					jsonRecebivel.put("documento", this.objetoContratoCobranca.getNumeroContratoSeguro());
					jsonRecebivel.put("termoCessao", this.objetoContratoCobranca.getTermoCessao());
					
					JSONObject jsonSacado = new JSONObject();
					
					JSONObject jsonPessoa = new JSONObject();
					if (this.objetoContratoCobranca.getPagador().getCpf() != null && !this.objetoContratoCobranca.getPagador().getCpf().equals("")) {
						jsonPessoa.put("tipo", "PF");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCpf())));				
					} else {
						jsonPessoa.put("tipo", "PJ");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCnpj())));
					}
					jsonPessoa.put("nome", this.objetoContratoCobranca.getPagador().getNome());
					jsonSacado.put("pessoa", jsonPessoa);
					
					JSONObject jsonEndereco = new JSONObject();
					jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCep())));
					jsonEndereco.put("logradouro", this.objetoContratoCobranca.getPagador().getEndereco());
					jsonEndereco.put("numero", this.objetoContratoCobranca.getPagador().getNumero());
					jsonEndereco.put("complemento", this.objetoContratoCobranca.getPagador().getComplemento());
					jsonEndereco.put("bairro", this.objetoContratoCobranca.getPagador().getBairro());
					jsonEndereco.put("municipio", this.objetoContratoCobranca.getPagador().getCidade());
					jsonEndereco.put("uf", this.objetoContratoCobranca.getPagador().getEstado());
					jsonSacado.put("endereco", jsonEndereco);
			
					jsonRecebivel.put("sacado", jsonSacado);
					
					JSONObject jsonCedente = new JSONObject();
					jsonCedente.put("tipo", "PJ");
					
					if (this.cedenteCessao.equals("BMP Money Plus SCD S.A.")) {
						jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
						jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
					} else {
						jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
						jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
					}
					jsonRecebivel.put("cedente", jsonCedente);
					
					jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(this.dataAquisicao));
					jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(this.objetoContratoCobranca.getDataInicio()));
					jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
					JSONObject jsonValores = new JSONObject();
					
					BigDecimal valorTotalFaceCessaoCalc = BigDecimal.ZERO;
					
					valorTotalFaceCessaoCalc = parcela.getValorAmortizacaoSemIPCA().add(parcela.getValorJurosSemIPCA()).setScale(2, RoundingMode.HALF_EVEN);
					
					this.valorTotalFaceCessao = this.valorTotalFaceCessao.add(valorTotalFaceCessaoCalc);
					jsonValores.put("face", valorTotalFaceCessaoCalc);
					
					BigDecimal valorTotalAquisicaoCessaoCalc = BigDecimal.ZERO;
					
					if (this.usaTaxaJurosDiferenciada) {
						valorTotalAquisicaoCessaoCalc = calcularValorPresenteParcela(parcela.getId(), this.txJurosCessao, this.dataAquisicao);
						jsonValores.put("aquisicao", valorTotalAquisicaoCessaoCalc);
					} else {
						valorTotalAquisicaoCessaoCalc = calcularValorPresenteParcela(parcela.getId(), this.objetoContratoCobranca.getTxJurosParcelas(), this.dataAquisicao);
						jsonValores.put("aquisicao", valorTotalAquisicaoCessaoCalc);
					}					

					this.valorTotalAquisicaoCessao = this.valorTotalAquisicaoCessao.add(valorTotalAquisicaoCessaoCalc);
					
					jsonRecebivel.put("valores", jsonValores);
					
					JSONObject jsonDados = new JSONObject();
					
					if (this.objetoContratoCobranca.isCorrigidoIPCA()) {
						jsonDados.put("indice", "IPCA");
					}
					
					jsonDados.put("sistemaAmortizacao", this.objetoContratoCobranca.getTipoCalculo());
					jsonDados.put("valorDaGarantia", this.objetoContratoCobranca.getValorImovel());
					jsonDados.put("tipo", this.objetoContratoCobranca.getTipoImovel());					
					jsonDados.put("LTV", valorTotalPresenteContrato);					
					jsonDados.put("empresa", this.objetoContratoCobranca.getEmpresaImovel());
					jsonDados.put("contemSeguroMIPeDFI", "SIM");
					jsonDados.put("valorEmprestimo", this.objetoContratoCobranca.getValorCCB());
					jsonDados.put("garantiaAtual", this.objetoContratoCobranca.getImovel().getNome());
					
					
					jsonDados.put("taxaCessao", this.objetoContratoCobranca.getTxJurosParcelas());
					jsonDados.put("taxaJuros", this.objetoContratoCobranca.getTxJurosParcelas());
					jsonDados.put("numeroDeParcelas", this.objetoContratoCobranca.getQtdeParcelas());
					jsonDados.put("mesesDeCarencia", mesesCarencia);
																			
					jsonRecebivel.put("dados", jsonDados);		
					
					jsonRecebiveis.put(jsonRecebivel);
				}
			}
		}
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

		FileOutputStream fileStream;
		try {
			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
			OutputStreamWriter file;
			file = new OutputStreamWriter(fileStream, "UTF-8");
			
            file.write(jsonSchema.toString());
            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.jsonGerado = true;
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Geração JSON BRL Cessão: JSON gerado com sucesso!",
						""));	
	}
	
	public void geraJSONLiquidacaoMigracao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.jsonGerado = true;
		String contratosErros = null;
		
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(gerarDataHoje()) + "_LIQ";
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = "JSON_BRL_Trust_Migracao_" + identificadorCessao + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		Date dataHoje = gerarDataHoje();
		
		for (ContratoCobranca contrato : this.selectedContratos) {
			for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
				if (parcela.getDataVencimento().after(dataHoje)) {
					JSONObject jsonRecebivel = new JSONObject();
					
					String numeroParcela = "";
					
					if (parcela.getNumeroParcela().length() == 1) {
						numeroParcela = "00" + parcela.getNumeroParcela();
					} else if (parcela.getNumeroParcela().length() == 2) {
						numeroParcela = "0" + parcela.getNumeroParcela();
					} else {
						numeroParcela = parcela.getNumeroParcela();
					}
					
					jsonRecebivel.put("numeroControle", contrato.getNumeroContratoSeguro() + "-" + numeroParcela);
					jsonRecebivel.put("coobrigacao", false);
					jsonRecebivel.put("ocorrencia", 95);
					jsonRecebivel.put("tipo", 73);
					jsonRecebivel.put("documento", contrato.getNumeroContratoSeguro());
					jsonRecebivel.put("termoCessao", contrato.getTermoCessao());
					
					JSONObject jsonSacado = new JSONObject();
					
					JSONObject jsonPessoa = new JSONObject();
					if (contrato.getPagador().getCpf() != null && !contrato.getPagador().getCpf().equals("")) {
						jsonPessoa.put("tipo", "PF");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(contrato.getPagador().getCpf())));				
					} else {
						jsonPessoa.put("tipo", "PJ");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(contrato.getPagador().getCnpj())));
					}
					jsonPessoa.put("nome", contrato.getPagador().getNome());
					jsonSacado.put("pessoa", jsonPessoa);
					
					JSONObject jsonEndereco = new JSONObject();
					jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(contrato.getPagador().getCep())));
					jsonEndereco.put("logradouro", contrato.getPagador().getEndereco());
					jsonEndereco.put("numero", contrato.getPagador().getNumero());
					jsonEndereco.put("complemento", contrato.getPagador().getComplemento());
					jsonEndereco.put("bairro", contrato.getPagador().getBairro());
					jsonEndereco.put("municipio", contrato.getPagador().getCidade());
					jsonEndereco.put("uf", contrato.getPagador().getEstado());
					jsonSacado.put("endereco", jsonEndereco);
			
					jsonRecebivel.put("sacado", jsonSacado);
					
					JSONObject jsonCedente = new JSONObject();
					jsonCedente.put("tipo", "PJ");
					
					if (contrato.getCedenteBRLCessao().equals("BMP Money Plus SCD S.A.")) {
						jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
						jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
					} else {
						jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
						jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
					}
					jsonRecebivel.put("cedente", jsonCedente);
					
					jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(contrato.getDataAquisicaoCessao()));
					jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(contrato.getDataInicio()));
					jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
					jsonRecebivel.put("liquidacao", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
					JSONObject jsonValores = new JSONObject();
					
					if (parcela.getValorAmortizacaoSemIPCA() != null && parcela.getValorJurosSemIPCA() != null) {
						jsonValores.put("face", parcela.getValorAmortizacaoSemIPCA().add(parcela.getValorJurosSemIPCA()).setScale(2, RoundingMode.HALF_EVEN));
					} else {
						this.jsonGerado = false;
						
						if (contratosErros == null) {
							contratosErros = contrato.getNumeroContrato();
						} else {
							contratosErros = contratosErros + " / " + contrato.getNumeroContrato();
						}
					}
					
					System.out.println("contrato: " + contrato.getNumeroContrato());
					System.out.println("cessao: " + contrato.getTxJurosCessao());
					System.out.println("juros parcela: " + contrato.getTxJurosParcelas());
					
					if (contrato != null) {
						if (contrato.getTxJurosCessao() != null) {
							jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), contrato.getTxJurosCessao(), contrato.getDataAquisicaoCessao()));
						} else {
							jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), contrato.getTxJurosParcelas(), contrato.getDataAquisicaoCessao()));
						}
					} 
					
					//jsonValores.put("liquidacao", parcela.getVlrRecebido());
					jsonValores.put("liquidacao", calcularValorPresenteParcela(parcela.getId(), contrato.getTxJurosParcelas(), contrato.getDataAquisicaoCessao()));
					
					jsonRecebivel.put("valores", jsonValores);
					
					JSONObject jsonDados = new JSONObject();
					jsonDados.put("indice", "IPCA");			
					jsonRecebivel.put("dados", jsonDados);		
					
					jsonRecebiveis.put(jsonRecebivel);
				}
			}
		}
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

		FileOutputStream fileStream;
		try {
			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
			OutputStreamWriter file;
			file = new OutputStreamWriter(fileStream, "UTF-8");
			
            file.write(jsonSchema.toString());
            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.jsonGerado) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
							""));	
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Geração JSON BRL Liquidação: Este(s) contrato(s) precisa(m) do processo de gerar Cessão novamente: " + contratosErros,
							""));	
		}
	}
	
	
	public void geraJSONLiquidacao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.jsonGerado = true;
		String contratosErros = null;
		
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(gerarDataHoje()) + "_LIQ";
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = "JSON_BRL_Trust_Liquidacao_" + identificadorCessao + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		for (ContratoCobrancaBRLLiquidacao parcela : this.parcelasLiquidacao) {
			JSONObject jsonRecebivel = new JSONObject();
			
			String numeroParcela = "";
			
			if (parcela.getNumeroParcela().length() == 1) {
				numeroParcela = "00" + parcela.getNumeroParcela();
			} else if (parcela.getNumeroParcela().length() == 2) {
				numeroParcela = "0" + parcela.getNumeroParcela();
			} else {
				numeroParcela = parcela.getNumeroParcela();
			}
			
			jsonRecebivel.put("numeroControle", parcela.getContrato().getNumeroContratoSeguro() + "-" + numeroParcela);
			jsonRecebivel.put("coobrigacao", false);
			jsonRecebivel.put("ocorrencia", 77);
			jsonRecebivel.put("tipo", 73);
			jsonRecebivel.put("documento", parcela.getContrato().getNumeroContratoSeguro());
			jsonRecebivel.put("termoCessao", parcela.getContrato().getTermoCessao());
			
			JSONObject jsonSacado = new JSONObject();
			
			JSONObject jsonPessoa = new JSONObject();
			if (parcela.getContrato().getPagador().getCpf() != null && !parcela.getContrato().getPagador().getCpf().equals("")) {
				jsonPessoa.put("tipo", "PF");
				jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(parcela.getContrato().getPagador().getCpf())));				
			} else {
				jsonPessoa.put("tipo", "PJ");
				jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(parcela.getContrato().getPagador().getCnpj())));
			}
			jsonPessoa.put("nome", parcela.getContrato().getPagador().getNome());
			jsonSacado.put("pessoa", jsonPessoa);
			
			JSONObject jsonEndereco = new JSONObject();
			jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(parcela.getContrato().getPagador().getCep())));
			jsonEndereco.put("logradouro", parcela.getContrato().getPagador().getEndereco());
			jsonEndereco.put("numero", parcela.getContrato().getPagador().getNumero());
			jsonEndereco.put("complemento", parcela.getContrato().getPagador().getComplemento());
			jsonEndereco.put("bairro", parcela.getContrato().getPagador().getBairro());
			jsonEndereco.put("municipio", parcela.getContrato().getPagador().getCidade());
			jsonEndereco.put("uf", parcela.getContrato().getPagador().getEstado());
			jsonSacado.put("endereco", jsonEndereco);
	
			jsonRecebivel.put("sacado", jsonSacado);
			
			JSONObject jsonCedente = new JSONObject();
			jsonCedente.put("tipo", "PJ");
			
			if (parcela.getContrato().getCedenteBRLCessao().equals("BMP Money Plus SCD S.A.")) {
				jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
				jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
			} else {
				jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
				jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
			}
			jsonRecebivel.put("cedente", jsonCedente);
			
			jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(parcela.getContrato().getDataAquisicaoCessao()));
			jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(parcela.getContrato().getDataInicio()));
			jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
			jsonRecebivel.put("liquidacao", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
			JSONObject jsonValores = new JSONObject();
			
			if (parcela.getVlrAmortizacaoSemIPCA() != null && parcela.getVlrJurosSemIPCA() != null) {
				jsonValores.put("face", parcela.getVlrAmortizacaoSemIPCA().add(parcela.getVlrJurosSemIPCA()).setScale(2, RoundingMode.HALF_EVEN));
			} else {
				this.jsonGerado = false;
				
				if (contratosErros == null) {
					contratosErros = parcela.getContrato().getNumeroContrato();
				} else {
					contratosErros = contratosErros + " / " + parcela.getContrato().getNumeroContrato();
				}
			}
			
			System.out.println("contrato: " + parcela.getContrato().getNumeroContrato());
			System.out.println("cessao: " + parcela.getContrato().getTxJurosCessao());
			System.out.println("juros parcela: " + parcela.getContrato().getTxJurosParcelas());
			
			if (parcela.getContrato() != null) {
				if (parcela.getContrato().getTxJurosCessao() != null) {
					jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosCessao(), parcela.getContrato().getDataAquisicaoCessao()));
				} else {
					jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosParcelas(), parcela.getContrato().getDataAquisicaoCessao()));
				}
			} else {
				jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosParcelas(), parcela.getContrato().getDataAquisicaoCessao()));
			}
			
			jsonValores.put("liquidacao", parcela.getVlrRecebido());
			
			jsonRecebivel.put("valores", jsonValores);
			
			JSONObject jsonDados = new JSONObject();
			jsonDados.put("indice", "IPCA");			
			jsonRecebivel.put("dados", jsonDados);		
			
			jsonRecebiveis.put(jsonRecebivel);
		}
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

		FileOutputStream fileStream;
		try {
			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
			OutputStreamWriter file;
			file = new OutputStreamWriter(fileStream, "UTF-8");
			
            file.write(jsonSchema.toString());
            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.jsonGerado) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
							""));	
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Geração JSON BRL Liquidação: Este(s) contrato(s) precisa(m) do processo de gerar Cessão novamente: " + contratosErros,
							""));	
		}
	}
	
	private SimulacaoVO calcularParcelas() {
		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = BigDecimal.valueOf(0.38).divide(BigDecimal.valueOf(100));

		SimulacaoVO simulador = new SimulacaoVO();

		if (this.objetoContratoCobranca.getPagador().getCpf() != null) {
			if ( DateUtil.isAfterDate(this.objetoContratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF ) ) {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF_ANTIGA.divide(BigDecimal.valueOf(100));
			}else {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));
			}
			simulador.setTipoPessoa("PF");
		} else {
			if ( DateUtil.isAfterDate(this.objetoContratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF ) ) {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ_ANTIGA.divide(BigDecimal.valueOf(100));
			}else {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));
			}
			simulador.setTipoPessoa("PJ");
		}

		simulador.setDataSimulacao(DateUtil.getDataHoje());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		// valores
		simulador.setValorCredito(this.objetoContratoCobranca.getValorCCB());
		simulador.setTaxaJuros(this.objetoContratoCobranca.getTxJurosParcelas());
		simulador.setCarencia(BigInteger.valueOf(this.objetoContratoCobranca.getMesesCarencia()));
		simulador.setQtdParcelas(BigInteger.valueOf(this.objetoContratoCobranca.getQtdeParcelas()));
		simulador.setValorImovel(this.objetoContratoCobranca.getValorImovel());
//			simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculo());
		simulador.setNaoCalcularDFI(
				!(this.objetoContratoCobranca.isTemSeguroDFI() && this.objetoContratoCobranca.isTemSeguro()));
		simulador.setNaoCalcularMIP(
				!(this.objetoContratoCobranca.isTemSeguroMIP() && this.objetoContratoCobranca.isTemSeguro()));

		simulador.calcular();
		return simulador;
	}
	
	public void geraJSONLiquidacaoParcela() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String numeroParcela = "";
		
		if (this.parcelaLiquidacao.getNumeroParcela().length() == 1) {
			numeroParcela = "00" + this.parcelaLiquidacao.getNumeroParcela();
		} else if (this.parcelaLiquidacao.getNumeroParcela().length() == 2) {
			numeroParcela = "0" + this.parcelaLiquidacao.getNumeroParcela();
		} else {
			numeroParcela = this.parcelaLiquidacao.getNumeroParcela();
		}
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(gerarDataHoje()) + numeroParcela + "_LIQ_";
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = "JSON_BRL_Trust_Liquidacao_" + identificadorCessao + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		JSONObject jsonRecebivel = new JSONObject();
		
		jsonRecebivel.put("numeroControle", this.parcelaLiquidacao.getContrato().getNumeroContratoSeguro() + "-" + numeroParcela);
		jsonRecebivel.put("coobrigacao", false);
		jsonRecebivel.put("ocorrencia", 77);
		jsonRecebivel.put("tipo", 73 ); // total
		//jsonRecebivel.put("tipo", 73); parcial 14
		jsonRecebivel.put("documento", this.parcelaLiquidacao.getContrato().getNumeroContratoSeguro());
		jsonRecebivel.put("termoCessao", this.parcelaLiquidacao.getContrato().getTermoCessao());
		
		JSONObject jsonSacado = new JSONObject();
		
		JSONObject jsonPessoa = new JSONObject();
		if (this.parcelaLiquidacao.getContrato().getPagador().getCpf() != null && !this.parcelaLiquidacao.getContrato().getPagador().getCpf().equals("")) {
			jsonPessoa.put("tipo", "PF");
			jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.parcelaLiquidacao.getContrato().getPagador().getCpf())));				
		} else {
			jsonPessoa.put("tipo", "PJ");
			jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.parcelaLiquidacao.getContrato().getPagador().getCnpj())));
		}
		jsonPessoa.put("nome", this.parcelaLiquidacao.getContrato().getPagador().getNome());
		jsonSacado.put("pessoa", jsonPessoa);
		
		JSONObject jsonEndereco = new JSONObject();
		jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(this.parcelaLiquidacao.getContrato().getPagador().getCep())));
		jsonEndereco.put("logradouro", this.parcelaLiquidacao.getContrato().getPagador().getEndereco());
		jsonEndereco.put("numero", this.parcelaLiquidacao.getContrato().getPagador().getNumero());
		jsonEndereco.put("complemento", this.parcelaLiquidacao.getContrato().getPagador().getComplemento());
		jsonEndereco.put("bairro", this.parcelaLiquidacao.getContrato().getPagador().getBairro());
		jsonEndereco.put("municipio", this.parcelaLiquidacao.getContrato().getPagador().getCidade());
		jsonEndereco.put("uf", this.parcelaLiquidacao.getContrato().getPagador().getEstado());
		jsonSacado.put("endereco", jsonEndereco);

		jsonRecebivel.put("sacado", jsonSacado);
		
		JSONObject jsonCedente = new JSONObject();
		jsonCedente.put("tipo", "PJ");
		
		if (this.parcelaLiquidacao.getContrato().getCedenteBRLCessao().equals("BMP Money Plus SCD S.A.")) {
			jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
			jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
		} else {
			jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
			jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
		}
		jsonRecebivel.put("cedente", jsonCedente);
		
		jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
		jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getContrato().getDataInicio()));
		jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getDataVencimento()));
		jsonRecebivel.put("liquidacao", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getDataVencimento()));
		JSONObject jsonValores = new JSONObject();
		jsonValores.put("face", this.parcelaLiquidacao.getVlrAmortizacaoParcela().add(this.parcelaLiquidacao.getVlrJurosParcela()));
		
		if (parcelaLiquidacao.getContrato() != null) {
			if (parcelaLiquidacao.getContrato().getTxJurosCessao() != null) {
				jsonValores.put("aquisicao", calcularValorPresenteParcela(parcelaLiquidacao.getId(), parcelaLiquidacao.getContrato().getTxJurosCessao(), parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
			} else {
				jsonValores.put("aquisicao", calcularValorPresenteParcela(parcelaLiquidacao.getId(), parcelaLiquidacao.getContrato().getTxJurosParcelas(), parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
			}
		} else {
			jsonValores.put("aquisicao", calcularValorPresenteParcela(parcelaLiquidacao.getId(), parcelaLiquidacao.getContrato().getTxJurosParcelas(), parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
		}
		
		jsonValores.put("liquidacao", this.parcelaLiquidacao.getVlrRecebido());
		
		jsonRecebivel.put("valores", jsonValores);
		
		JSONObject jsonDados = new JSONObject();
		jsonDados.put("indice", "IPCA");			
		jsonRecebivel.put("dados", jsonDados);		
		
		jsonRecebiveis.put(jsonRecebivel);
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

		FileOutputStream fileStream;
		try {
			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
			OutputStreamWriter file;
			file = new OutputStreamWriter(fileStream, "UTF-8");
			
            file.write(jsonSchema.toString());
            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.jsonGerado = true;
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
						""));	
	}
	
	public BigDecimal calcularValorPresenteParcela(Long idParcela, BigDecimal txJuros, Date dataAquisicao){
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		//Date auxDataHoje = dataHoje.getTime();
		Date auxDataHoje = dataAquisicao;
		BigDecimal valorPresenteParcela;
		
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();		
		ContratoCobrancaDetalhes parcelas = cDao.findById(idParcela);
		
		BigDecimal juros = txJuros;
		BigDecimal saldo = BigDecimal.ZERO;
		
		if (parcelas.getValorJurosSemIPCA() != null && parcelas.getValorAmortizacaoSemIPCA() != null) {
			saldo = parcelas.getValorJurosSemIPCA().add(parcelas.getValorAmortizacaoSemIPCA());
		}
		
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(auxDataHoje, parcelas.getDataVencimento()));
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses); 
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
		
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		valorPresenteParcela = valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return valorPresenteParcela;
	}

	public String getStringSemCaracteres(String documento) {
		String retorno = "";
		
		retorno = documento.replace(".", "").replace("/", "").replace("-", "");
				
		return retorno;
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	public Date gerarDataOntem() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.add(Calendar.DATE, -1);
		
		return dataHoje.getTime();
	}

	public boolean isJsonGerado() {
		return jsonGerado;
	}

	public void setJsonGerado(boolean jsonGerado) {
		this.jsonGerado = jsonGerado;
	}

	public String getPathJSON() {
		return pathJSON;
	}

	public void setPathJSON(String pathJSON) {
		this.pathJSON = pathJSON;
	}

	public String getNomeJSON() {
		return nomeJSON;
	}

	public void setNomeJSON(String nomeJSON) {
		this.nomeJSON = nomeJSON;
	}

	public StreamedContent getFile() {
		String caminho =  this.pathJSON + this.nomeJSON;        
		String arquivo = this.nomeJSON;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		file = new DefaultStreamedContent(stream, caminho, arquivo); 

		return file;  
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getNumContrato() {
		return numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	public String getCedenteCessao() {
		return cedenteCessao;
	}

	public void setCedenteCessao(String cedenteCessao) {
		this.cedenteCessao = cedenteCessao;
	}

	public Date getDataAquisicao() {
		return dataAquisicao;
	}

	public void setDataAquisicao(Date dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	public List<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public List<ContratoCobrancaBRLLiquidacao> getParcelasLiquidacao() {
		return parcelasLiquidacao;
	}

	public void setParcelasLiquidacao(List<ContratoCobrancaBRLLiquidacao> parcelasLiquidacao) {
		this.parcelasLiquidacao = parcelasLiquidacao;
	}

	public Date getDataBaixaInicial() {
		return dataBaixaInicial;
	}

	public void setDataBaixaInicial(Date dataBaixaInicial) {
		this.dataBaixaInicial = dataBaixaInicial;
	}

	public Date getDataBaixaFinal() {
		return dataBaixaFinal;
	}

	public void setDataBaixaFinal(Date dataBaixaFinal) {
		this.dataBaixaFinal = dataBaixaFinal;
	}

	public ContratoCobrancaBRLLiquidacao getParcelaLiquidacao() {
		return parcelaLiquidacao;
	}

	public void setParcelaLiquidacao(ContratoCobrancaBRLLiquidacao parcelaLiquidacao) {
		this.parcelaLiquidacao = parcelaLiquidacao;
	}

	public boolean isUsaTaxaJurosDiferenciada() {
		return usaTaxaJurosDiferenciada;
	}

	public void setUsaTaxaJurosDiferenciada(boolean usaTaxaJurosDiferenciada) {
		this.usaTaxaJurosDiferenciada = usaTaxaJurosDiferenciada;
	}

	public BigDecimal getTxJurosCessao() {
		return txJurosCessao;
	}

	public void setTxJurosCessao(BigDecimal txJurosCessao) {
		this.txJurosCessao = txJurosCessao;
	}

	public BigDecimal getValorTotalFaceCessao() {
		return valorTotalFaceCessao;
	}

	public void setValorTotalFaceCessao(BigDecimal valorTotalFaceCessao) {
		this.valorTotalFaceCessao = valorTotalFaceCessao;
	}

	public BigDecimal getValorTotalAquisicaoCessao() {
		return valorTotalAquisicaoCessao;
	}

	public void setValorTotalAquisicaoCessao(BigDecimal valorTotalAquisicaoCessao) {
		this.valorTotalAquisicaoCessao = valorTotalAquisicaoCessao;
	}

	public List<ContratoCobranca> getSelectedContratos() {
		return selectedContratos;
	}

	public void setSelectedContratos(List<ContratoCobranca> selectedContratos) {
		this.selectedContratos = selectedContratos;
	}

	public BigDecimal getSomatoriaValorePresenteContratos() {
		return somatoriaValorePresenteContratos;
	}

	public void setSomatoriaValorePresenteContratos(BigDecimal somatoriaValorePresenteContratos) {
		this.somatoriaValorePresenteContratos = somatoriaValorePresenteContratos;
	}
}