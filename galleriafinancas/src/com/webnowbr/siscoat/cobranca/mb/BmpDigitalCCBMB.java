package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.CalendarUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.FaturaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.OperacaoContratoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.SaldoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SaqueIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SubContaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.BcMsgRetorno;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDaOperacao;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoCliente;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoClienteTraduzido;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoVencimento;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoModalidade;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "bmpDigitalCCBMB")
@SessionScoped
public class BmpDigitalCCBMB {

	/****
	 * 
		
	{
	  "auth": {
	    "Usuario": "joao@galleriafinancas.com.br",
	    "Senha": "Scr!2021",
	    "CodigoParametro": "GALLERIA_SCR",
	    "Chave": "eb11110f-9f0e-4a16-83d7-6229c949da4a"
	  },
	
	
	•	URL de Produção
	o	Integração
	https://bmpdigital.moneyp.com.br/api/BMPDigital/ <- concatenando o serviço
	o	Swagger
			https://bmpteste.moneyp.com.br/swagger/ui/index
	o	Dashboard
		https://bmpdigital.moneyp.com.br/
		Para acessarem podem utilizar o mesmo login e senha da integração
		Caso necessitem de outros usuários podem solicitar que liberamos

	 */
	
	/*
	 * TESTEEEE
	 * {
		  "auth": {
		    "Usuario": "galleria@bank.com.br",
		    "Senha": "Galleria!2022",
		    "CodigoParametro": "GALLERIA_BANK",
		    "Chave": " afde47ff-daf2-4084-8bc9-af4b315792db"
		  },
		
		
		•	URL de Homologação
		o	Integração
		https://bmpteste.moneyp.com.br/api/BMPDigital/ <- concatenando o serviço
		o	Swagger
			https://bmpteste.moneyp.com.br/swagger/ui/index
		o	Dashboard
			https://bmpteste.moneyp.com.br/
			Para acessarem podem utilizar o mesmo login e senha da integração
			Caso necessitem de outros usuários podem solicitar que liberamos		 
	 */

	
	/***
	 * INICIO ATRIBUTOS RECIBO
	 */
	
	private String documento;
	
	private TransferenciasObservacoesIUGU transferenciasObservacoesIUGU;
	
	static final String usuario =  "galleria@bank.com.br";
	static final String senha =  "Galleria!2022";
	static final String codigoParametro =  "GALLERIA_BANK";
	static final String chave =  "afde47ff-daf2-4084-8bc9-af4b315792db";
	/***
	 * FIM ATRIBUTOS RECIBO
	 */



	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA TRANSFERENCIA DE VALORES SUBCONTAS IUGU
	 * 
	 * @return
	 */
	
	public String clearFields() {
		this.documento = "";
		
		return "/Atendimento/Cobranca/ConsultaSCR.xhtml";
	}
	
	/***
	 * GERA JSON PARA CRIAÇÃO E EDIÇÃO DE PESSOA
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONPessoa(PagadorRecebedor pessoa) {
		JSONObject jsonPessoa = new JSONObject();
		jsonPessoa.put("auth", getJSONAuth());
		jsonPessoa.put("dto", getJSONDTO(pessoa));
		
		return jsonPessoa;
	}
	
	/***
	 * GERA JSON PARA PESSOA - AUTH
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONAuth() {
		JSONObject auth = new JSONObject();
		auth.put("Usuario", usuario);
		auth.put("Senha", senha);
		auth.put("CodigoParametro", codigoParametro);
		auth.put("Chave", chave);
		
		return auth;
	}

	/***
	 * GERA JSON PARA CLIENTE E FIDUCIANTE - DTO
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONDTO(PagadorRecebedor pessoa) {
		JSONObject jsonDTOPessoa = new JSONObject();

		jsonDTOPessoa.put("Nome", pessoa.getNome());
		
		// Se tem CodigoMoneyPlus no cadasto será feito update na integração
		String codigoMoneyPlus = "";
		if (pessoa.getCodigoMoneyPlus() != null && !pessoa.getCodigoMoneyPlus().equals("")) {
			jsonDTOPessoa.put("Codigo", pessoa.getCodigoMoneyPlus());
		}		
		
		// Se pessoa física
		if (pessoa.getCpf() != null && !pessoa.getCpf().equals("")) {
			JSONObject pessoaPFDTO = new JSONObject();
			pessoaPFDTO.put("Apelido", pessoa.getNome());
			pessoaPFDTO.put("Sexo", pessoa.getSexo());
			pessoaPFDTO.put("EstadoCivil ", pessoa.getEstadocivil());
			
			jsonDTOPessoa.put("DocumentoFederal", pessoa.getCpf()); 
			
			jsonDTOPessoa.put("PF", pessoaPFDTO);						
		} else {
			// se pessoa jurídica
			JSONObject pessoaPJDTO = new JSONObject();
			pessoaPJDTO.put("NomeFantasia", pessoa.getNome());
			pessoaPJDTO.put("DocumentoMunicipal", pessoa.getCnpj());
			
			jsonDTOPessoa.put("DocumentoFederal", pessoa.getCnpj()); 
			
			jsonDTOPessoa.put("PJ", pessoaPJDTO);
		}
		
		// dados de contato
		JSONObject pessoaDadosContatoDTO = new JSONObject();
		pessoaDadosContatoDTO.put("Email", pessoa.getEmail());
		pessoaDadosContatoDTO.put("TelefoneFixo1", pessoa.getTelResidencial());
		pessoaDadosContatoDTO.put("TelefoneCelular1", pessoa.getTelCelular());
		pessoaDadosContatoDTO.put("PaginaWeb", pessoa.getSite());
		
		jsonDTOPessoa.put("PessoaDadosContato", pessoaDadosContatoDTO);
	
		//PessoaDadoFinanceiro (Foundation.Data.Facade.BMPDigital.PessoaDadoFinanceiroDTO, optional)

		return jsonDTOPessoa;
	}
	
	/***
	 * GERA JSON PARA CLIENTE/FIDUNCIANTE - ENDEREÇO - DTO
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONDTOEndereco(PagadorRecebedor pessoa) {

		JSONObject jsonDTOPessoaEndereco = new JSONObject();
		//auth
		jsonDTOPessoaEndereco.put("auth", getJSONAuth());
		
		// inserçao parametro
		// Se pessoa física
		JSONObject jsonDTOParam = new JSONObject();
		//jsonDTOParam.put("DocumentoCliente", pessoa.getCodigoMoneyPlus());
		if (pessoa.getCpf() != null && !pessoa.getCpf().equals("")) {
			jsonDTOParam.put("DocumentoFederal", pessoa.getCpf());				
		} else {
			// se pessoa jurídica
			jsonDTOParam.put("DocumentoCliente", pessoa.getCnpj());
		}		
		jsonDTOPessoaEndereco.put("param", jsonDTOParam);
		
		// dto endereco
		JSONObject jsonDTO = new JSONObject();
		
		jsonDTO.put("CEP", pessoa.getCep());
		jsonDTO.put("Logradouro", pessoa.getEndereco());
		jsonDTO.put("NroLogradouro", pessoa.getNumero());
		jsonDTO.put("Bairro", pessoa.getBairro());
		jsonDTO.put("Complemento", pessoa.getComplemento());
		jsonDTO.put("Cidade", pessoa.getCidade());
		jsonDTO.put("UF", pessoa.getEstado());
		jsonDTO.put("EnderecoPrincipal", true);
		jsonDTO.put("EnderecoCorrespondencia", true);
		
		jsonDTOPessoaEndereco.put("dto", jsonDTO);
		
		return jsonDTOPessoaEndereco;
	}
	
	public void integraCCBMoneyPlus() {
		PagadorRecebedorDao pessoaDao = new PagadorRecebedorDao();
		PagadorRecebedor pessoa = new PagadorRecebedor();
		
		pessoa = pessoaDao.findById((long) 11184);
		getJSONPessoa(pessoa);
		getJSONDTOEndereco(pessoa);
		// "a09b3f13-16c2-41a9-9cee-4cf60dfc19e7"
		
		// verifica se a pessoa já existe na money plus
		/*
		boolean pessoaExiste = true;
		if (contrato.getPagador().getCodigoMoneyPlus() == null || contrato.getPagador().getCodigoMoneyPlus().equals("")) {
			pessoaExiste = false;
		}

		if (!pessoaExiste) {
			// TODO Cria pessoa na money plus
			// TODO atualiza código money plus
		}
		*/
		
		// TODO integra CCB
	}
	
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	// retorna a string do objeto JSON, mesmo em caso de nulos
	public String getStringJSON(JSONObject objetoJSON, String chave) {
		if (!objetoJSON.isNull(chave)) {
			return objetoJSON.getString(chave);
		} else {
			return "";
		}
	}
	
	// retorna a string do objeto JSON, mesmo em caso de nulos
	public boolean getObjectJSON(JSONObject objetoJSON, String chave) {
		if (objetoJSON.has(chave)) {
			if (!objetoJSON.isNull(chave)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO 
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
}