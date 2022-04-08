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
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
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
import javax.xml.ws.Holder;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
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
	public JSONObject getJSONPessoaDTO(PagadorRecebedor pessoa, String nacionalidadeEmitente) {
		JSONObject jsonPessoa = new JSONObject();
		jsonPessoa.put("auth", getJSONAuth());
		
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
			
			pessoaPFDTO.put("Nacionalidade", nacionalidadeEmitente);
			pessoaPFDTO.put("EstadoCivil", pessoa.getEstadocivil());
			pessoaPFDTO.put("RG", pessoa.getRg());
			pessoaPFDTO.put("NomePai", pessoa.getNomePai());
			pessoaPFDTO.put("NomeMae", pessoa.getNomeMae());
			pessoaPFDTO.put("NomeConjuge", pessoa.getNomeConjuge());
			pessoaPFDTO.put("CPFConjuge", pessoa.getCpfConjuge());
			
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
		
		jsonPessoa.put("dto", jsonDTOPessoa);
		
		return jsonPessoa;
	}
	
	/***
	 * GERA JSON PARA CLIENTE/FIDUNCIANTE - ENDEREÇO - DTO
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONEnderecoDTO(PagadorRecebedor pessoa) {

		JSONObject jsonDTOPessoaEndereco = new JSONObject();
		//auth
		jsonDTOPessoaEndereco.put("auth", getJSONAuth());
		
		// inserçao parametro
		// Se pessoa física
		JSONObject jsonDTOParam = new JSONObject();
		if (pessoa.getCpf() != null && !pessoa.getCpf().equals("")) {
			jsonDTOParam.put("DocumentoCliente", pessoa.getCpf());				
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
	
	/***
	 * GERA JSON PARA ENVIO DA PROPOSTA
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONEnvioProposta(PagadorRecebedor cliente, PagadorRecebedor fiduciante, String numeroParcelasPagamento, 
			BigDecimal taxaDeJurosMes, BigDecimal valorIOF, String numeroBanco, 
			String agencia, String contaCorrente, BigDecimal valorCredito, String numeroContrato, Date vencimentoPrimeiraParcelaPagamento) {

		JSONObject jsonEnvioProposta = new JSONObject();		
		
		//auth
		jsonEnvioProposta.put("auth", getJSONAuth());
		
		//parametros ARRAY
		JSONArray jsonParametros = new JSONArray();
		JSONObject jsonParametro = new JSONObject();
		
		jsonParametro.put("nome", "");
		jsonParametro.put("valor", "");
		
		jsonParametros.put(jsonParametro);
		
		//auth
		jsonEnvioProposta.put("parametros", jsonParametros);

		// DTO
		JSONObject jsonDTOEnvioProposta = new JSONObject();
		
		// CLIENTE - Se pessoa física
		if (cliente.getCpf() != null && !cliente.getCpf().equals("")) {
			jsonDTOEnvioProposta.put("DocumentoCliente", cliente.getCpf());	
			jsonDTOEnvioProposta.put("PercIOF", "0,0082");
		} else {
			// se pessoa jurídica
			jsonDTOEnvioProposta.put("DocumentoCliente", cliente.getCnpj());
			jsonDTOEnvioProposta.put("PercIOF", "0,0041");
		}
		
		// FIDUCIANTE - Se pessoa física
		if (fiduciante.getCpf() != null && !fiduciante.getCpf().equals("")) {
			jsonDTOEnvioProposta.put("DocumentoParceiroCorrespondente", fiduciante.getCpf());				
		} else {
			// se pessoa jurídica
			jsonDTOEnvioProposta.put("DocumentoParceiroCorrespondente", fiduciante.getCnpj());
		}	
		
		//TODO
		jsonDTOEnvioProposta.put("CodigoOperacao", numeroContrato);
		jsonDTOEnvioProposta.put("VlrSolicitado", valorCredito);
		jsonDTOEnvioProposta.put("Prazo", numeroParcelasPagamento);
		jsonDTOEnvioProposta.put("PercJurosNegociado", taxaDeJurosMes);
		jsonDTOEnvioProposta.put("VlrIOF", valorIOF);		
		jsonDTOEnvioProposta.put("PercIOFAdicional", 0);
		jsonDTOEnvioProposta.put("VlrParcela", 0);XXXXXXXXXXXXXXXXXXXXX
		jsonDTOEnvioProposta.put("VlrTAC", 0);
		jsonDTOEnvioProposta.put("VlrBoleto", 0);
		jsonDTOEnvioProposta.put("TipoContrato", "CSG");
		//TODO
		jsonDTOEnvioProposta.put("DtPrimeiroVencto", vencimentoPrimeiraParcelaPagamento);
		jsonDTOEnvioProposta.put("VlrSeguro", 0);
		jsonDTOEnvioProposta.put("VlrAvaliacao", 0);
		jsonDTOEnvioProposta.put("VlrRegistroCartorio", 0);
		jsonDTOEnvioProposta.put("VlrSeguroMensal1", 0);
		jsonDTOEnvioProposta.put("VlrSeguroMensal2", 0);

		JSONObject jsonContaPagamentoDTO = new JSONObject();
		jsonContaPagamentoDTO.put("CodigoBanco", numeroBanco);
		jsonContaPagamentoDTO.put("TipoConta", 0);
		jsonContaPagamentoDTO.put("Agencia", agencia);
		jsonContaPagamentoDTO.put("AgenciaDig", "");
		jsonContaPagamentoDTO.put("Conta", contaCorrente);
		jsonContaPagamentoDTO.put("ContaDig", "");
		jsonContaPagamentoDTO.put("NumeroBanco", numeroBanco);
		jsonContaPagamentoDTO.put("DocumentoFederalPagamento", "");
		jsonContaPagamentoDTO.put("NomePagamento", "");
		
		jsonDTOEnvioProposta.put("PropostaContaPagamentoDTO", jsonContaPagamentoDTO);
		
		jsonEnvioProposta.put("dto", jsonDTOEnvioProposta);
		
		return jsonEnvioProposta;
	}
	
	/***
	 * GERA JSON PARA INCLUIR AVALISTA NA PROPOSTA
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONPropostaIncluirAvalista(PagadorRecebedor avalista, String nacionalidadeAvalista) {

		JSONObject jsonIncluiAvalista = new JSONObject();	
		
		//auth
		jsonIncluiAvalista.put("auth", getJSONAuth());
		
		// busca proposta
		JSONObject jsonFindProposta = new JSONObject();
		jsonFindProposta.put("CodigoProposta", "");
		jsonFindProposta.put("CodigoOperacao", "");
		jsonIncluiAvalista.put("dto", jsonFindProposta);
		
		// Dados do Avalista		
		JSONObject jsonDTOPessoa = new JSONObject();

		jsonDTOPessoa.put("Nome", avalista.getNome());
		
		// Se pessoa física
		if (avalista.getCpf() != null && !avalista.getCpf().equals("")) {
			JSONObject pessoaPFDTO = new JSONObject();
			pessoaPFDTO.put("Apelido", avalista.getNome());
			pessoaPFDTO.put("Sexo", avalista.getSexo());
			pessoaPFDTO.put("EstadoCivil ", avalista.getEstadocivil());
			
			pessoaPFDTO.put("Nacionalidade ", nacionalidadeAvalista);
			pessoaPFDTO.put("EstadoCivil ", avalista.getEstadocivil());
			pessoaPFDTO.put("RG ", avalista.getRg());
			pessoaPFDTO.put("NomePai ", avalista.getNomePai());
			pessoaPFDTO.put("NomeMae ", avalista.getNomeMae());
			pessoaPFDTO.put("NomeConjuge ", avalista.getNomeConjuge());
			pessoaPFDTO.put("CPFConjuge ", avalista.getCpfConjuge());
			
			jsonDTOPessoa.put("DocumentoFederal", avalista.getCpf()); 
			
			jsonDTOPessoa.put("avalistaPF", pessoaPFDTO);						
		} else {
			// se pessoa jurídica
			JSONObject pessoaPJDTO = new JSONObject();
			pessoaPJDTO.put("NomeFantasia", avalista.getNome());
			pessoaPJDTO.put("DocumentoMunicipal", avalista.getCnpj());
			
			jsonDTOPessoa.put("DocumentoFederal", avalista.getCnpj()); 
			
			jsonDTOPessoa.put("avalistaPJ", pessoaPJDTO);
		}
		
		jsonIncluiAvalista.put("avalista", jsonDTOPessoa);
		
		return jsonIncluiAvalista;
	}
	
	/***
	 * GERA JSON PARA INCLUIR CAMPOS EXTRAS
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONPropostaIncluirCamposExtras() {

		JSONObject jsonIncluiCamposExtras = new JSONObject();	
		
		//auth
		jsonIncluiCamposExtras.put("auth", getJSONAuth());
		
		// dto
		JSONObject jsonIncluiCamposExtrasDTO = new JSONObject();
		jsonIncluiCamposExtrasDTO.put("CodigoProposta", "");
		jsonIncluiCamposExtrasDTO.put("CodigoOperacao", "");		
		jsonIncluiCamposExtras.put("dto", jsonIncluiCamposExtrasDTO);
		
		// campos extra
		// campos sugeridos no documento da money plus
		JSONArray jsonCamposExtras = new JSONArray();
		JSONObject jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "Identificação do imovel");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);
		
		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "Descricaodoimovel");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);
		
		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "tituloemodoaquisicao");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);
		
		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "registroimoveis");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);
		
		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "matricula");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);
		
		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "prefeitura");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "numerocadastro");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "valorvenal");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "valordoimovel");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "laudovaliacao");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "criteriorevisao");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "valorminimogarantia");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "SeguroDFInomeSeguradora");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "SeguroDFICNPJseguradora");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "SeguroDFIapolice");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "SeguroDFIvigencia");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "SeguroMIPnomeSeguradora");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "SeguroMIPapolice");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);

		jsonIncluiCampoExtra = new JSONObject();
		jsonIncluiCampoExtra.put("NomeCampo", "ClausulaIPCA");
		jsonIncluiCampoExtra.put("ValorCampo", "");				
		jsonCamposExtras.put(jsonIncluiCampoExtra);
				
		jsonIncluiCamposExtras.put("camposExtras", jsonCamposExtras);

		return jsonIncluiCamposExtras;
	}
	
	/***
	 * GERA JSON PARA FINALIZAR PROPOSTA
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONPropostaFinalizar() {

		JSONObject jsonPropostaFinalizar = new JSONObject();	
		
		//auth
		jsonPropostaFinalizar.put("auth", getJSONAuth());
		
		// dto
		JSONObject jsonPropostaFinalizarDTO = new JSONObject();
		jsonPropostaFinalizarDTO.put("CodigoProposta", "");
		jsonPropostaFinalizarDTO.put("CodigoOperacao", "");		
		jsonPropostaFinalizar.put("dto", jsonPropostaFinalizarDTO);
		
		// campos sugeridos no documento da money plus
		JSONArray jsonParametros = new JSONArray();
		JSONObject jsonParametro = new JSONObject();
		jsonParametro.put("NomeCampo", "FINALIZACAO_VALIDARPARCELAS");
		jsonParametro.put("ValorCampo", "FALSE");				
		jsonParametros.put(jsonParametro);
		
		jsonParametro = new JSONObject();
		jsonParametro.put("NomeCampo", "IP_ADDRESS");
		jsonParametro.put("ValorCampo", getIpAdress());	
		
		jsonParametros.put(jsonParametro);

		jsonPropostaFinalizar.put("parametros", jsonParametros);

		return jsonPropostaFinalizar;
	}
	
	/***
	 * GERA JSON PARA LIBERAR PROPOSTA
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONPropostaLiberar() {

		JSONObject jsonPropostaLiberar = new JSONObject();	
		
		//auth
		jsonPropostaLiberar.put("auth", getJSONAuth());
		
		// dto
		JSONObject jsonPropostaFinalizarDTO = new JSONObject();
		jsonPropostaFinalizarDTO.put("CodigoProposta", "");
		jsonPropostaFinalizarDTO.put("CodigoOperacao", "");		
		jsonPropostaLiberar.put("dto", jsonPropostaFinalizarDTO);
		
		// campos sugeridos no documento da money plus
		JSONArray jsonParametros = new JSONArray();
		JSONObject jsonParametro = new JSONObject();
		jsonParametro.put("NomeCampo", "");
		jsonParametro.put("ValorCampo", "");				
		jsonParametros.put(jsonParametro);
		
		jsonParametros.put(jsonParametro);

		jsonPropostaLiberar.put("parametros", jsonParametros);

		return jsonPropostaLiberar;
	}
	
	/***
	 * GERA JSON PARA CANCELAR PROPOSTA
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONPropostaCancelar() {

		JSONObject jsonPropostaCancelar = new JSONObject();	
		
		//auth
		jsonPropostaCancelar.put("auth", getJSONAuth());
		
		// dto
		JSONObject jsonPropostaFinalizarDTO = new JSONObject();
		jsonPropostaFinalizarDTO.put("CodigoProposta", "");
		jsonPropostaFinalizarDTO.put("CodigoOperacao", "");		
		jsonPropostaCancelar.put("dto", jsonPropostaFinalizarDTO);
		
		jsonPropostaCancelar.put("textoMotivoCancelamento", "");
		
		// campos sugeridos no documento da money plus
		JSONArray jsonParametros = new JSONArray();
		JSONObject jsonParametro = new JSONObject();
		jsonParametro.put("NomeCampo", "");
		jsonParametro.put("ValorCampo", "");				
		jsonParametros.put(jsonParametro);
		
		jsonParametros.put(jsonParametro);

		jsonPropostaCancelar.put("parametros", jsonParametros);

		return jsonPropostaCancelar;
	}
	
	/***
	 * CHAMADA PRINCIPAL PARA MONEY PLUS
	 * @param pessoa
	 * @return
	 */
	public void integraCCBMoneyPlus() {
		
		/***
		 * 
		 * CHAVES DA MAIORIA DAS APIs
		 * PagadorRecebedor - codigoMoneyPlus (código da pessoa no money plus)
		 * ContratoCobranca - codigoPropostaMoneyPlus (código da proposta no money plus)
		 * ContratoCobranca - numeroContrato (código da operação no Siscoat) 
		 * 
		 */
		PagadorRecebedorDao pessoaDao = new PagadorRecebedorDao();
		PagadorRecebedor pessoa = new PagadorRecebedor();
		
		pessoa = pessoaDao.findById((long) 11184);
		// Pessoa teste "a09b3f13-16c2-41a9-9cee-4cf60dfc19e7"
		//TESTADO OK
		// Cadastro de cliente
		// Cadastro do Fiduciante 
		// /api/BMPDigital/CreateUpdatePessoa
		///getJSONPessoaDTO(pessoa);
		//TESTADO OK
		//	Cadastro de cliente
		//  Cadastro do Fiduciante 
		// /api/BMPDigital/CreateUpdatePessoaEndereco
		//getJSONEnderecoDTO(pessoa);
		
		// Envio da Proposta
		// /api/BMPDigital/IncluirPropostaManualSimplificado
		getJSONEnvioProposta(pessoa, pessoa);
		
		// Incluir avalista
		// /api/BMPDigital/PropostaIncluirAvalista
		getJSONPropostaIncluirAvalista(pessoa);
		
		// Incluir campos extras na Proposta
		// /api/BMPDigital/PropostaIncluirCampoExtra
		getJSONPropostaIncluirCamposExtras();
		
		// Impressao CCB é na tela através de link
		// Para visualização da CCB utilize a URL abaixo, a variável será o guid da proposta devolvido no response da API de inclusão da proposta
		// http://bmpteste.moneyp.com.br/imprimirccb.aspx?code={GUID_PROPOSTA}
		
		// TODO
		//UploadDocumentoProposta
		
		// Após a CCB assinada Finalizar Proposta
		// /api/BMPDigital/PropostaFinalizar
		getJSONPropostaFinalizar();
		
		// /api/BMPDigital/PropostaLiberar
		// Liberação para pagamento (Quando o Financeiro for pagar a proposta)
		getJSONPropostaLiberar();
		
		// /api/BMPDigital/PropostaCancelar 
		// Cancelamento da proposta
		getJSONPropostaCancelar();
		
		// Call-back
		// Podemos enviar call-back com o status da proposta a URL deve ser pública e tem que tratar os dois ou três parâmetros 
		// (proposta número de operação gerado pelo sistema M Plus ou Identificador, número de operação do vosso sistema)
		//http://sistema.galleriabank.com.br/galleriafinancas/siscoat/services/StatusPropostaMoneyPlus?proposta=XXXX&situacao=000&identificador=111111
		
		UploadedFile file;
		
		
		
		
		// verifica se a pessoa já existe na money plus
		/*
		boolean pessoaExiste = true;
		if (contrato.getPagador().getCodigoMoneyPlus() == null || contrato.getPagador().getCodigoMoneyPlus().equals("")) {
			pessoaExiste = false;
		}

		if (!pessoaExiste) {
			// TODO Cria pessoa na money plus
			// TODO atualiza endereço money plus
		}
		*/
		
		// TODO integra CCB
	}
	
	// joão - 67fe4af8-fb58-4b3f-8ba3-8e9e469c26d3
	public void enviaEmitente(PagadorRecebedor pessoa, String nacionalidadeEmitente) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://bmpteste.moneyp.com.br/api/BMPDigital/CreateUpdatePessoa");

			JSONObject jsonObj = getJSONPessoaDTO(pessoa, nacionalidadeEmitente);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			
			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */			
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
						
			if (!myResponse.getBoolean("Result")) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[MoneyPlus] Envia Pessoa - ERRO: " + myResponse.getString("Msg"), ""));
			} else {					
				if (myResponse.has("Result")) {					
					if (myResponse.getBoolean("Result")) {
						
						String codigoRetorno = "";
						if (myResponse.has("Codigo")) {
							codigoRetorno = myResponse.getString("Codigo");
							
							PagadorRecebedorDao pDao = new PagadorRecebedorDao();
							
							pessoa.setCodigoMoneyPlus(codigoRetorno);
							
							pDao.merge(pessoa);
							
							context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
									"[MoneyPlus] Envia Pessoa - Pessoa inserida/atualizada com sucesso! Cód.: " + myResponse.getString("Codigo"), ""));
						}
					} 
				} 
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void enviaAvalista(PagadorRecebedor pessoa, String nacionalidadeEmitente) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://bmpteste.moneyp.com.br/api/BMPDigital/CreateUpdatePessoa");

			JSONObject jsonObj = getJSONPessoaDTO(pessoa, nacionalidadeEmitente);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			
			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */			
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
						
			if (!myResponse.getBoolean("Result")) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[MoneyPlus] Envia Pessoa - ERRO: " + myResponse.getString("Msg"), ""));
			} else {					
				if (myResponse.has("Result")) {					
					if (myResponse.getBoolean("Result")) {
						
						String codigoRetorno = "";
						if (myResponse.has("Codigo")) {
							codigoRetorno = myResponse.getString("Codigo");
							
							PagadorRecebedorDao pDao = new PagadorRecebedorDao();
							
							pessoa.setCodigoMoneyPlus(codigoRetorno);
							
							pDao.merge(pessoa);
							
							context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
									"[MoneyPlus] Envia Pessoa - Pessoa inserida/atualizada com sucesso! Cód.: " + myResponse.getString("Codigo"), ""));
						}
					} 
				} 
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void enviaFiduciante(PagadorRecebedor pessoa, String nacionalidadeEmitente) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://bmpteste.moneyp.com.br/api/BMPDigital/CreateUpdatePessoa");

			JSONObject jsonObj = getJSONPessoaDTO(pessoa, nacionalidadeEmitente);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			
			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */			
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
						
			if (!myResponse.getBoolean("Result")) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[MoneyPlus] Envia Pessoa - ERRO: " + myResponse.getString("Msg"), ""));
			} else {					
				if (myResponse.has("Result")) {					
					if (myResponse.getBoolean("Result")) {
						
						String codigoRetorno = "";
						if (myResponse.has("Codigo")) {
							codigoRetorno = myResponse.getString("Codigo");
							
							PagadorRecebedorDao pDao = new PagadorRecebedorDao();
							
							pessoa.setCodigoMoneyPlus(codigoRetorno);
							
							pDao.merge(pessoa);
							
							context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
									"[MoneyPlus] Envia Pessoa - Pessoa inserida/atualizada com sucesso! Cód.: " + myResponse.getString("Codigo"), ""));
						}
					} 
				} 
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviaEndereco(PagadorRecebedor pessoa) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();

			URL myURL = new URL("https://bmpteste.moneyp.com.br/api/BMPDigital/CreateUpdatePessoaEndereco");

			JSONObject jsonObj = getJSONEnderecoDTO(pessoa);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			
			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */			
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
						
			if (!myResponse.getBoolean("Result")) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[MoneyPlus] Envia Endereço - ERRO: " + myResponse.getString("Msg"), ""));
			} else {					
				if (myResponse.has("Result")) {					
					if (myResponse.getBoolean("Result")) {
						context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
								"[MoneyPlus] Envia Endereço - Endereço inserida/atualizada com sucesso! ", ""));
					}
				} 
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void enviaProposta(PagadorRecebedor emitente, PagadorRecebedor fiduciante,
			String numeroParcelasPagamento, BigDecimal taxaDeJurosMes, BigDecimal valorIOF, String numeroBanco, 
			String agencia, String contaCorrente, BigDecimal valorCredito, String numeroContrato, Date vencimentoPrimeiraParcelaPagamento) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();

			URL myURL = new URL("https://bmpteste.moneyp.com.br/api/BMPDigital/IncluirPropostaManualSimplificado");

			JSONObject jsonObj = getJSONEnvioProposta(emitente, fiduciante, numeroParcelasPagamento, taxaDeJurosMes, 
					valorIOF, numeroBanco, agencia, contaCorrente, valorCredito, numeroContrato, vencimentoPrimeiraParcelaPagamento);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			
			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */			
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
						
			if (!myResponse.getBoolean("Result")) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[MoneyPlus] Envia Proposta - ERRO: " + myResponse.getString("Msg"), ""));
			} else {					
				if (myResponse.has("Result")) {					
					if (myResponse.getBoolean("Result")) {
						context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
								"[MoneyPlus] Envia Proposta - Proposta enviada com sucesso! ", ""));
					}
				} 
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJSONSucesso(InputStream inputStream) {
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
	
	// retorna o IP da máquina virtual
	public String getIpAdress() {
		String ip = "";
		try {
			InetAddress myIP = InetAddress.getLocalHost();
			ip = myIP.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ip;
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