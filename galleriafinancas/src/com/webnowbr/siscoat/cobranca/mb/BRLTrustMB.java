package com.webnowbr.siscoat.cobranca.mb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaBRLLiquidacao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


@ManagedBean(name = "brlTrustMB")
@SessionScoped
public class BRLTrustMB {
	
	private boolean jsonGerado;
	private String pathJSON;
	private String nomeJSON;	
	private StreamedContent file;	
	
	List<ContratoCobranca> contratos = new ArrayList<ContratoCobranca>();
	ContratoCobranca objetoContratoCobranca = new ContratoCobranca();
	
	private String numContrato;
	private String cedenteCessao;
	private Date dataAquisicao;
	private Date dataBaixa;
	
	private boolean usaTaxaJurosDiferenciada;
	private BigDecimal txJurosCessao;
	
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
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJson.xhtml";
	}
	
	public void pesquisaContratosCessao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.numContrato.length() == 4) {
			this.numContrato = "0" + this.numContrato;
		} 
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratos = contratoCobrancaDao.consultaContratosBRLCessao(this.numContrato);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"BRL JSON: Pesquisa efetuada com sucesso!",
						""));	
	}
	
	public String clearFieldsBRLJsonLiquidacao() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		
		this.dataBaixa = gerarDataOntem();
		
		this.parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
		this.parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();
		
		this.jsonGerado = false;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJsonLiquidacao.xhtml";
	}
	
	public void pesquisaContratosLiquidacao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.numContrato.length() == 4) {
			this.numContrato = "0" + this.numContrato;
		} 
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.parcelasLiquidacao = contratoCobrancaDao.consultaContratosBRLLiquidacao(this.dataBaixa, this.cedenteCessao);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"BRL JSON: Pesquisa efetuada com sucesso!",
						""));	
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
		this.nomeJSON = "JSON_BRL_Trust_Cessao_" + identificadorCessao + ".json";
		
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
		try{
			int countParcelas = 0;
			for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
				countParcelas = countParcelas + 1;
				if (countParcelas > countCarencia) {
					if (parcela.getDataVencimento().after(gerarDataHoje())) {
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
						jsonValores.put("face", parcela.getVlrAmortizacaoParcela().add(parcela.getVlrJurosParcela()));
						
						if (this.usaTaxaJurosDiferenciada) {
							jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), this.txJurosCessao, this.dataAquisicao));
						} else {
							jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), this.objetoContratoCobranca.getTxJurosParcelas(), this.dataAquisicao));
						}					
	
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
			
			this.jsonGerado = true;
			
			atualizaContratoDadosCessaoBRL();
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Geração JSON BRL Cessão: JSON gerado com sucesso!",
							""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Geração JSON BRL Cessão: JSON falhou!" +e,
							""));
			e.printStackTrace();
		}
	}
	
	public void geraJSONLiquidacao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(this.dataBaixa) + "_LIQ";
		
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
			jsonRecebivel.put("ocorrencia", 1);
			jsonRecebivel.put("tipo", 77 ); // total
			//jsonRecebivel.put("tipo", 73); parcial 14
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
			jsonValores.put("face", parcela.getVlrAmortizacaoParcela().add(parcela.getVlrJurosParcela()));
			jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosCessao(), parcela.getContrato().getDataAquisicaoCessao()));
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
		
		this.jsonGerado = true;
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
						""));	
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
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(this.dataBaixa) + numeroParcela + "_LIQ_";
		
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
		jsonRecebivel.put("ocorrencia", 1);
		jsonRecebivel.put("tipo", 77 ); // total
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
		jsonValores.put("aquisicao", calcularValorPresenteParcela(this.parcelaLiquidacao.getId(), this.parcelaLiquidacao.getContrato().getTxJurosCessao(), this.parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
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
		BigDecimal saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
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
		
		if(!CommonsUtil.semValor(documento) && !CommonsUtil.mesmoValor(documento,  "")) {
			retorno = documento.replace(".", "").replace("/", "").replace("-", "");
		}
				
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

	public Date getDataBaixa() {
		return dataBaixa;
	}

	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
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
}