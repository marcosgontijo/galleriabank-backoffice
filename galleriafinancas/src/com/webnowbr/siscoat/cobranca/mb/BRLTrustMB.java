package com.webnowbr.siscoat.cobranca.mb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
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

	public String clearFieldsBRLJson() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		
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
	
	public String geraJSONCessao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = "JSON BRL Trust.json";
		
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
		
		jsonCessao.put("identificadorCessao", simpleDateFormatyyyyMMdd.format(gerarDataHoje()) + this.objetoContratoCobranca.getNumeroContrato());
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			JSONObject jsonRecebivel = new JSONObject();
			jsonRecebivel.put("numeroControle", this.objetoContratoCobranca.getNumeroContratoSeguro() + "-" + parcela.getNumeroParcela());
			jsonRecebivel.put("coobrigacao", false);
			jsonRecebivel.put("ocorrencia", 1);
			jsonRecebivel.put("tipo", 73);
			//jsonRecebivel.put("tipo", 73); total 77
			//jsonRecebivel.put("tipo", 73); parcial 14
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
			// TODO se liquidação
			//jsonRecebivel.put("liquidacao", "2022-03-08");
			JSONObject jsonValores = new JSONObject();
			jsonValores.put("face", parcela.getVlrAmortizacaoParcela().add(parcela.getVlrJurosParcela()));
			jsonValores.put("aquisicao", 99999.99);
			// TODO se liquidação
			//jsonValores.put("liquidacao", 2500.30);
			
			jsonRecebivel.put("valores", jsonValores);
			
			JSONObject jsonDados = new JSONObject();
			jsonDados.put("juros", "IPCA+");			
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
						"SCR: PDF gerado com sucesso!",
						""));	

		return "/Atendimento/Cobranca/ContratoCobrancaJSON.xhtml";
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
}