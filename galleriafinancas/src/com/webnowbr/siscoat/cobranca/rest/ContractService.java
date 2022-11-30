package com.webnowbr.siscoat.cobranca.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.faces.bean.ManagedProperty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorAdicionais;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorSocio;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;

import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.Parametros;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;


@Path("/services")
public class ContractService {
	
	private ContratoCobranca objetoContratoCobranca;
	private ImovelCobranca objetoImovelCobranca;
	private PagadorRecebedor objetoPagador;
	private Set<PagadorRecebedorAdicionais> listaPagadores;
	private Set<PagadorRecebedorSocio> listSocios;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	public static void main(String[] args) {
		
		String authorization = "Basic d2Vibm93YnI6IVNpc0NvQXRAMjAyMSo=";
		
		String[] tokens;
		String username = "";
		String password = "";
		
		authorization = authorization.replace("Basic ", "");
		
		try {
			tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");

			username = tokens[0];
			password = tokens[1];
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("LeadBySite - username: " + authorization);
		System.out.println("LeadBySite - password: " + authorization);
    }

	@GET
	@Path("/TestarOperacao")
	public Response testarOperacao() {
		String retorno = "TESTE OK";

		String message = "{\"hello\": \"This is a JSON response\"}";

	    return Response
	      .status(Response.Status.OK)
	      .entity(message)
	      .type(MediaType.APPLICATION_JSON)
	      .build();
	}
	
	@POST
	@Path("/CriarOperacao")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response criarOperacao(String operacaoData, @HeaderParam("Token") String token, @HeaderParam("Authorization") String authorization) { 
		System.out.println("[Galleria Bank] Criar Operação - Authorization: " + authorization);
	
		if (authorization == null || !authorization.startsWith("Basic")) {
			String message = "{\"retorno\": \"Authentication Failed!!!\"}";
			
			return Response
				      .status(Response.Status.FORBIDDEN)
				      .entity(message)
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		} else {
			
			//TODO AUTENTICACAO VIA TOKEN
			
			/// decoda token de autenticação
			String[] tokens;
			String username = "";
			String password = ""; 
			
			authorization = authorization.replace("Basic ", "");
			
			try {
				tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");

				username = tokens[0];
				password = tokens[1];
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			if (username.equals("webnowbr") && password.equals("!SisCoAt@2021*")) {
				try {
					JSONObject contratoAPP = new JSONObject(operacaoData);

					clearCriacaoContrato();	
					
					JSONObject contratoAPPResponsavel = contratoAPP.getJSONObject("responsavel");
					
					if (contratoAPPResponsavel.has("codigoResponsavel")) {
						ResponsavelDao rDao = new ResponsavelDao();
						String codigoResponsavel = contratoAPPResponsavel.getString("codigoResponsavel");
						
						List<Responsavel> responsaveis = new ArrayList<Responsavel>();
						responsaveis = rDao.findByFilter("codigo", codigoResponsavel);
						
						if (responsaveis.size() > 0) {
							this.objetoContratoCobranca.setResponsavel(responsaveis.get(0));
							
							if (contratoAPP.has("numeroContrato")) {
								this.objetoContratoCobranca.setNumeroContrato(contratoAPP.getString("numeroContrato"));	
							} else {
								this.objetoContratoCobranca.setNumeroContrato(geraNumeroContrato());
							}
							
							this.objetoContratoCobranca.setTipoOperacao(contratoAPP.getString("tipoOperacao"));
							String tipoPessoa = contratoAPP.getString("tipoPessoa");
							this.objetoContratoCobranca.setCobrarComissaoCliente(contratoAPP.getString("cobrarComissaoCliente"));
							this.objetoContratoCobranca.setComissaoClienteValorFixo(contratoAPP.has("comissaoClienteValorFixo") 
									?  new BigDecimal(contratoAPP.getDouble("comissaoClienteValorFixo")) : null);
							this.objetoContratoCobranca.setComissaoClientePorcentagem(contratoAPP.has("comissaoClientePorcentagem")
									? new BigDecimal(contratoAPP.getDouble("comissaoClientePorcentagem")) : null);
							this.objetoContratoCobranca.setTipoCobrarComissaoCliente(contratoAPP.getString("tipoCobrarComissaoCliente"));
							this.objetoContratoCobranca.setBrutoLiquidoCobrarComissaoCliente(contratoAPP.getString("brutoLiquidoCobrarComissaoCliente"));
							
							this.objetoContratoCobranca.setQuantoPrecisa(new BigDecimal(contratoAPP.getDouble("quantoPrecisa")));
							this.objetoContratoCobranca.setObservacao(contratoAPP.has("observacao") ? contratoAPP.getString("observacao") : null);
							
							this.objetoContratoCobranca.setPagadorDonoGarantia(contratoAPP.getBoolean("pagadorDonoGarantia"));
							this.objetoContratoCobranca.setDivida(contratoAPP.getString("divida"));
							this.objetoContratoCobranca.setDividaValor(contratoAPP.has("dividaValor") ? new BigDecimal(contratoAPP.getDouble("dividaValor")) : null);
							
							
							/***
							 * VALORES DEFAULT
							 */
							this.objetoContratoCobranca.setInicioAnalise(false);
							this.objetoContratoCobranca.setStatusLead("Completo");
							this.objetoContratoCobranca.setDocumentosComite(false);
							this.objetoContratoCobranca.setCadastroAprovado(false);
							this.objetoContratoCobranca.setPedidoPreLaudo(false);
							this.objetoContratoCobranca.setPedidoPreLaudoComercial(false);
							this.objetoContratoCobranca.setPedidoLaudoPajuComercial(false);
							this.objetoContratoCobranca.setAnaliseComercial(false);
							this.objetoContratoCobranca.setComentarioJuridicoEsteira(false);
							this.objetoContratoCobranca.setPreAprovadoComite(false);
							this.objetoContratoCobranca.setAprovadoComite(false);
							this.objetoContratoCobranca.setPedidoLaudo(false);
							this.objetoContratoCobranca.setPedidoPajuComercial(false);
							this.objetoContratoCobranca.setTemTxAdm(false);
							
							/***
							 * OBJETO PAGADOR
							 */
							JSONObject contratoAPPPagador = contratoAPP.getJSONObject("pagadorRecebedor");
							this.objetoPagador = new PagadorRecebedor();
							PagadorRecebedorDao pagadorDao = new PagadorRecebedorDao();
							
							if (contratoAPPPagador.has("id")) {
								this.objetoPagador = pagadorDao.findById(contratoAPPPagador.getLong("id"));
							} else {
								this.objetoPagador.setId(-1);
								
								if (tipoPessoa.equals("PF")) {
									this.objetoPagador.setCpf(contratoAPPPagador.getString("cpfCnpj"));
								} else if(tipoPessoa.equals("PJ")) {
									this.objetoPagador.setCnpj(contratoAPPPagador.getString("cpfCnpj"));
								}
								
								this.objetoPagador.setNome(contratoAPPPagador.has("nome") ? contratoAPPPagador.getString("nome") : null);
								this.objetoPagador.setEmail(contratoAPPPagador.has("email") ? contratoAPPPagador.getString("email") : null);
								this.objetoPagador.setTelCelular(contratoAPPPagador.has("telCelular") ? contratoAPPPagador.getString("telCelular") : null);
								this.objetoPagador.setSexo(contratoAPPPagador.has("sexo") ? contratoAPPPagador.getString("sexo") : null);
								
								SimpleDateFormat dtNascimento = new SimpleDateFormat("yyyy-MM-dd");
								Date dtNascimentoDate = null;
								try {
									if(contratoAPPPagador.has("dataNascimento")) {
										dtNascimentoDate = dtNascimento.parse(contratoAPPPagador.getString("dataNascimento"));
										this.objetoPagador.setDtNascimento(dtNascimentoDate);
									}else {
										this.objetoPagador.setDtNascimento(null);
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							
								this.objetoPagador.setNomeMae(contratoAPPPagador.has("nomeMae") ? contratoAPPPagador.getString("nomeMae") : null);
								this.objetoPagador.setEstadocivil(contratoAPPPagador.has("estadoCivil") ? contratoAPPPagador.getString("estadoCivil") : null);
								this.objetoPagador.setCpfConjuge(contratoAPPPagador.has("cpfConjuge") ? contratoAPPPagador.getString("cpfConjuge") : null);
								this.objetoPagador.setNomeConjuge(contratoAPPPagador.has("nomeConjuge") ? contratoAPPPagador.getString("nomeConjuge") : null); 
								
								this.objetoPagador.setRgDocumentosCheckList(contratoAPPPagador.has("rgDocumentosCheckList") ? contratoAPPPagador.getBoolean("rgDocumentosCheckList") : false);
								this.objetoPagador.setComprovanteEnderecoDocumentosCheckList(contratoAPPPagador.has("comprovanteEnderecoDocumentosCheckList") ? contratoAPPPagador.getBoolean("comprovanteEnderecoDocumentosCheckList") : false);
								this.objetoPagador.setCertidaoCasamentoNascimentoDocumentosCheckList(contratoAPPPagador.has("certidaoCasamentoNascimentoDocumentosCheckList") ? contratoAPPPagador.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList") : false);
								this.objetoPagador.setFichaCadastralDocumentosCheckList(contratoAPPPagador.has("fichaCadastralDocumentosCheckList") ? contratoAPPPagador.getBoolean("fichaCadastralDocumentosCheckList") : false);
								this.objetoPagador.setBancoDocumentosCheckList(contratoAPPPagador.has("bancoDocumentosCheckList") ? contratoAPPPagador.getBoolean("bancoDocumentosCheckList") : false);
								this.objetoPagador.setTelefoneEmailDocumentosCheckList(contratoAPPPagador.has("telefoneEmailDocumentosCheckList") ? contratoAPPPagador.getBoolean("telefoneEmailDocumentosCheckList") : false);
								this.objetoPagador.setComprovanteRendaCheckList(contratoAPPPagador.has("comprovanteRendaCheckList") ? contratoAPPPagador.getBoolean("comprovanteRendaCheckList") : false);
								this.objetoPagador.setCombateFraudeCheckList(contratoAPPPagador.has("combateFraudeCheckList") ? contratoAPPPagador.getBoolean("combateFraudeCheckList") : false);
								this.objetoPagador.setCargoOcupacaoCheckList(contratoAPPPagador.has("cargoOcupacaoCheckList") ? contratoAPPPagador.getBoolean("cargoOcupacaoCheckList") : false);
								this.objetoPagador.setTaxaCheckList(contratoAPPPagador.has("taxaCheckList") ? contratoAPPPagador.getBoolean("taxaCheckList") : false);
							}
							
							this.objetoContratoCobranca.setPagador(this.objetoPagador);
							
							/***
							 * OBJETO IMOVEL
							 */
							JSONObject contratoAPPImovel = contratoAPP.getJSONObject("imovelCobranca");
							this.objetoImovelCobranca = new ImovelCobranca();
							ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
							
							if (contratoAPPImovel.has("id")) {
								this.objetoImovelCobranca = imovelCobrancaDao.findById(contratoAPPImovel.getLong("id"));
							} else {
								this.objetoImovelCobranca.setId(-1);
								this.objetoImovelCobranca.setCep(contratoAPPImovel.has("cep") ? contratoAPPImovel.getString("cep") : null);
								if(contratoAPPImovel.has("numero")) {
									this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco") + ", " + contratoAPPImovel.getString("numero"));
								}else {
									this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco"));									
								}
								this.objetoImovelCobranca.setComplemento(contratoAPPImovel.has("complemento") ? contratoAPPImovel.getString("complemento") : null);
								this.objetoImovelCobranca.setCidade(contratoAPPImovel.getString("cidade"));
								this.objetoImovelCobranca.setBairro(contratoAPPImovel.has("bairro") ? contratoAPPImovel.getString("bairro") : null);
								this.objetoImovelCobranca.setEstado(contratoAPPImovel.getString("estado"));
								this.objetoImovelCobranca.setNumeroCartorio(contratoAPPImovel.has("numeroCartorio") ? contratoAPPImovel.getString("numeroCartorio") : null);
								this.objetoImovelCobranca.setCartorio(contratoAPPImovel.has("cartorioRegistro") ? contratoAPPImovel.getString("cartorioRegistro") : null);
								this.objetoImovelCobranca.setCartorioEstado(contratoAPPImovel.has("cartorioEstado") ? contratoAPPImovel.getString("cartorioEstado") : null);
								this.objetoImovelCobranca.setCartorioMunicipio(contratoAPPImovel.has("cartorioMunicipio") ? contratoAPPImovel.getString("cartorioMunicipio") : null);
								this.objetoImovelCobranca.setNumeroMatricula(contratoAPPImovel.getString("numeroMatricula"));
								this.objetoImovelCobranca.setTipo(contratoAPPImovel.has("tipoImovel") ? contratoAPPImovel.getString("tipoImovel") : null);
								this.objetoImovelCobranca.setComprovanteMatriculaCheckList(contratoAPPImovel.has("comprovanteMatriculaCheckList") ? contratoAPPImovel.getBoolean("comprovanteMatriculaCheckList") : false);
								this.objetoImovelCobranca.setComprovanteFotosImovelCheckList(contratoAPPImovel.has("comprovanteFotosImovelCheckList") ? contratoAPPImovel.getBoolean("comprovanteFotosImovelCheckList") : false);
								this.objetoImovelCobranca.setComprovanteIptuImovelCheckList(contratoAPPImovel.has("comprovanteIptuImovelCheckList") ? contratoAPPImovel.getBoolean("comprovanteIptuImovelCheckList") : false);
								
								this.objetoImovelCobranca.setValoEstimado(new BigDecimal(contratoAPPImovel.has("valoEstimado") ? contratoAPPImovel.getDouble("valoEstimado") : null));								
							}
							
							this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
	
							/***
							 * OBJETO PAGADORES ADICIONAIS
							 */
							
							if(contratoAPP.has("pagadoresAdicionais")) {
								JSONArray pagadoresAdicionaisAPP = contratoAPP.getJSONArray("pagadoresAdicionais");
								
								if(pagadoresAdicionaisAPP.length() > 0) {
								
									for(int i = 0; i < pagadoresAdicionaisAPP.length(); i++) {
										JSONObject pessoaApp = pagadoresAdicionaisAPP.getJSONObject(i);
										if(pessoaApp.has("id")) {
											PagadorRecebedorAdicionais pagadorRecebedorAdicionais = new PagadorRecebedorAdicionais();
											PagadorRecebedor objetoPagadorAdicionais = new PagadorRecebedor();
											objetoPagadorAdicionais = pagadorDao.findById(contratoAPPPagador.getLong("id"));
											pagadorRecebedorAdicionais.setPessoa(objetoPagadorAdicionais);
											pagadorRecebedorAdicionais.setContratoCobranca(this.objetoContratoCobranca);
											
											pagadorRecebedorAdicionais.setNomeParticipanteCheckList(pessoaApp.getString("nomeParticipanteCheckList"));
											pagadorRecebedorAdicionais.setRgDocumentosCheckList(pessoaApp.getBoolean("rgDocumentosCheckList"));
											pagadorRecebedorAdicionais.setComprovanteEnderecoDocumentosCheckList(pessoaApp.getBoolean("comprovanteEnderecoDocumentosCheckList"));
											pagadorRecebedorAdicionais.setCertidaoCasamentoNascimentoDocumentosCheckList(pessoaApp.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
											pagadorRecebedorAdicionais.setFichaCadastralDocumentosCheckList(pessoaApp.getBoolean("fichaCadastralDocumentosCheckList"));
											pagadorRecebedorAdicionais.setBancoDocumentosCheckList(pessoaApp.getBoolean("bancoDocumentosCheckList"));
											pagadorRecebedorAdicionais.setTelefoneEmailDocumentosCheckList(pessoaApp.getBoolean("telefoneEmailDocumentosCheckList"));
											pagadorRecebedorAdicionais.setComprovanteRendaCheckList(pessoaApp.getBoolean("comprovanteRendaCheckList"));
											pagadorRecebedorAdicionais.setCombateFraudeCheckList(pessoaApp.getBoolean("combateFraudeCheckList"));
											pagadorRecebedorAdicionais.setCargoOcupacaoCheckList(pessoaApp.getBoolean("cargoOcupacaoCheckList"));
											pagadorRecebedorAdicionais.setTaxaCheckList(pessoaApp.getBoolean("taxaCheckList"));
											
											listaPagadores.add(pagadorRecebedorAdicionais);
											
											// TODO Salvar pagadorRecebedorAdicionais DS 
											
										}else {
											PagadorRecebedor pessoa = new PagadorRecebedor();
											PagadorRecebedorAdicionais pagadorRecebedorAdicionais = new PagadorRecebedorAdicionais();
											pessoa.setCpf(pessoaApp.getString("cpfCnpj"));
											pessoa.setSexo(pessoaApp.getString("sexo"));
											pessoa.setNomeMae(pessoaApp.getString("nomeMae"));
											pessoa.setNomeParticipanteCheckList(pessoaApp.getString("nomeParticipanteCheckList"));
											pessoa.setRgDocumentosCheckList(pessoaApp.getBoolean("rgDocumentosCheckList"));
											pessoa.setComprovanteEnderecoDocumentosCheckList(pessoaApp.getBoolean("comprovanteEnderecoDocumentosCheckList"));
											pessoa.setCertidaoCasamentoNascimentoDocumentosCheckList(pessoaApp.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
											pessoa.setFichaCadastralDocumentosCheckList(pessoaApp.getBoolean("fichaCadastralDocumentosCheckList"));
											pessoa.setBancoDocumentosCheckList(pessoaApp.getBoolean("bancoDocumentosCheckList"));
											pessoa.setTelefoneEmailDocumentosCheckList(pessoaApp.getBoolean("telefoneEmailDocumentosCheckList"));
											pessoa.setComprovanteRendaCheckList(pessoaApp.getBoolean("comprovanteRendaCheckList"));
											pessoa.setCombateFraudeCheckList(pessoaApp.getBoolean("combateFraudeCheckList"));
											pessoa.setCargoOcupacaoCheckList(pessoaApp.getBoolean("cargoOcupacaoCheckList"));
											pessoa.setTaxaCheckList(pessoaApp.getBoolean("taxaCheckList"));
											pagadorRecebedorAdicionais.setPessoa(pessoa);
											
											pagadorRecebedorAdicionais.setContratoCobranca(objetoContratoCobranca);
											
											pagadorRecebedorAdicionais.setNomeParticipanteCheckList(pessoaApp.getString("nomeParticipanteCheckList"));
											pagadorRecebedorAdicionais.setRgDocumentosCheckList(pessoaApp.getBoolean("rgDocumentosCheckList"));
											pagadorRecebedorAdicionais.setComprovanteEnderecoDocumentosCheckList(pessoaApp.getBoolean("comprovanteEnderecoDocumentosCheckList"));
											pagadorRecebedorAdicionais.setCertidaoCasamentoNascimentoDocumentosCheckList(pessoaApp.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
											pagadorRecebedorAdicionais.setFichaCadastralDocumentosCheckList(pessoaApp.getBoolean("fichaCadastralDocumentosCheckList"));
											pagadorRecebedorAdicionais.setBancoDocumentosCheckList(pessoaApp.getBoolean("bancoDocumentosCheckList"));
											pagadorRecebedorAdicionais.setTelefoneEmailDocumentosCheckList(pessoaApp.getBoolean("telefoneEmailDocumentosCheckList"));
											pagadorRecebedorAdicionais.setComprovanteRendaCheckList(pessoaApp.getBoolean("comprovanteRendaCheckList"));
											pagadorRecebedorAdicionais.setCombateFraudeCheckList(pessoaApp.getBoolean("combateFraudeCheckList"));
											pagadorRecebedorAdicionais.setCargoOcupacaoCheckList(pessoaApp.getBoolean("cargoOcupacaoCheckList"));
											pagadorRecebedorAdicionais.setTaxaCheckList(pessoaApp.getBoolean("taxaCheckList"));
											
											listaPagadores.add(pagadorRecebedorAdicionais);
											
											// TODO Salvar pagadorRecebedorAdicionais DS 
										}
									}
								}
							}
							
							
							/***
							 * OBJETO PAGADORES SOCIO
							 */
							
							if(contratoAPP.has("pagadoresSocio")) {
								JSONArray pagadoresSocioPP = contratoAPP.getJSONArray("pagadoresSocio");
								
								if(pagadoresSocioPP.length() > 0) {
									
									for(int i = 0; i < pagadoresSocioPP.length(); i++) {
										JSONObject pessoaApp = pagadoresSocioPP.getJSONObject(i);
										if(pessoaApp.has("id")) {
											PagadorRecebedorSocio pagadorRecebedorSocio = new PagadorRecebedorSocio();
											PagadorRecebedor objetoPagadorSocio = new PagadorRecebedor();
											objetoPagadorSocio = pagadorDao.findById(contratoAPPPagador.getLong("id"));
											pagadorRecebedorSocio.setPessoa(objetoPagadorSocio);
											pagadorRecebedorSocio.setContratoCobranca(this.objetoContratoCobranca);
											
											pagadorRecebedorSocio.setNomeParticipanteCheckList(pessoaApp.has("nomeParticipanteCheckList") ? pessoaApp.getString("nomeParticipanteCheckList") : null);
											pagadorRecebedorSocio.setRgDocumentosCheckList(pessoaApp.getBoolean("rgDocumentosCheckList"));
											pagadorRecebedorSocio.setComprovanteEnderecoDocumentosCheckList(pessoaApp.getBoolean("comprovanteEnderecoDocumentosCheckList"));
											pagadorRecebedorSocio.setCertidaoCasamentoNascimentoDocumentosCheckList(pessoaApp.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
											pagadorRecebedorSocio.setFichaCadastralDocumentosCheckList(pessoaApp.getBoolean("fichaCadastralDocumentosCheckList"));
											pagadorRecebedorSocio.setBancoDocumentosCheckList(pessoaApp.getBoolean("bancoDocumentosCheckList"));
											pagadorRecebedorSocio.setTelefoneEmailDocumentosCheckList(pessoaApp.getBoolean("telefoneEmailDocumentosCheckList"));
											pagadorRecebedorSocio.setComprovanteRendaCheckList(pessoaApp.getBoolean("comprovanteRendaCheckList"));
											pagadorRecebedorSocio.setCombateFraudeCheckList(pessoaApp.getBoolean("combateFraudeCheckList"));
											pagadorRecebedorSocio.setCargoOcupacaoCheckList(pessoaApp.getBoolean("cargoOcupacaoCheckList"));
											pagadorRecebedorSocio.setTaxaCheckList(pessoaApp.getBoolean("taxaCheckList"));
											
											listSocios.add(pagadorRecebedorSocio);
											
											// TODO Salvar pagadorRecebedorAdicionais DS 
											
										}else {
											PagadorRecebedor pessoa = new PagadorRecebedor();
											PagadorRecebedorSocio pagadorRecebedorSocio = new PagadorRecebedorSocio();
											pessoa.setCpf(pessoaApp.getString("cpfCnpj"));
											pessoa.setSexo(pessoaApp.getString("sexo"));
											pessoa.setNomeMae(pessoaApp.getString("nomeMae"));
											pessoa.setNomeParticipanteCheckList(pessoaApp.has("nomeParticipanteCheckList") ? pessoaApp.getString("nomeParticipanteCheckList") : null);
											pessoa.setRgDocumentosCheckList(pessoaApp.getBoolean("rgDocumentosCheckList"));
											pessoa.setComprovanteEnderecoDocumentosCheckList(pessoaApp.getBoolean("comprovanteEnderecoDocumentosCheckList"));
											pessoa.setCertidaoCasamentoNascimentoDocumentosCheckList(pessoaApp.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
											pessoa.setFichaCadastralDocumentosCheckList(pessoaApp.getBoolean("fichaCadastralDocumentosCheckList"));
											pessoa.setBancoDocumentosCheckList(pessoaApp.getBoolean("bancoDocumentosCheckList"));
											pessoa.setTelefoneEmailDocumentosCheckList(pessoaApp.getBoolean("telefoneEmailDocumentosCheckList"));
											pessoa.setComprovanteRendaCheckList(pessoaApp.getBoolean("comprovanteRendaCheckList"));
											pessoa.setCombateFraudeCheckList(pessoaApp.getBoolean("combateFraudeCheckList"));
											pessoa.setCargoOcupacaoCheckList(pessoaApp.getBoolean("cargoOcupacaoCheckList"));
											pessoa.setTaxaCheckList(pessoaApp.getBoolean("taxaCheckList"));
											pagadorRecebedorSocio.setPessoa(pessoa);
											
											pagadorRecebedorSocio.setContratoCobranca(objetoContratoCobranca);
											
											pagadorRecebedorSocio.setNomeParticipanteCheckList(pessoaApp.getString("nomeParticipanteCheckList"));
											pagadorRecebedorSocio.setRgDocumentosCheckList(pessoaApp.getBoolean("rgDocumentosCheckList"));
											pagadorRecebedorSocio.setComprovanteEnderecoDocumentosCheckList(pessoaApp.getBoolean("comprovanteEnderecoDocumentosCheckList"));
											pagadorRecebedorSocio.setCertidaoCasamentoNascimentoDocumentosCheckList(pessoaApp.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
											pagadorRecebedorSocio.setFichaCadastralDocumentosCheckList(pessoaApp.getBoolean("fichaCadastralDocumentosCheckList"));
											pagadorRecebedorSocio.setBancoDocumentosCheckList(pessoaApp.getBoolean("bancoDocumentosCheckList"));
											pagadorRecebedorSocio.setTelefoneEmailDocumentosCheckList(pessoaApp.getBoolean("telefoneEmailDocumentosCheckList"));
											pagadorRecebedorSocio.setComprovanteRendaCheckList(pessoaApp.getBoolean("comprovanteRendaCheckList"));
											pagadorRecebedorSocio.setCombateFraudeCheckList(pessoaApp.getBoolean("combateFraudeCheckList"));
											pagadorRecebedorSocio.setCargoOcupacaoCheckList(pessoaApp.getBoolean("cargoOcupacaoCheckList"));
											pagadorRecebedorSocio.setTaxaCheckList(pessoaApp.getBoolean("taxaCheckList"));
											
											listSocios.add(pagadorRecebedorSocio);
											
											// TODO Salvar pagadorRecebedorAdicionais DS 
										}
									}
								}
							}
							
							// salva contrato
							criaContratoBD();
							
							String message = "{\"retorno\": \"[Galleria Bank] Operação criada com sucesso!!!\"}";
				
						    return Response
						      .status(Response.Status.OK)
						      .entity(message)
						      .type(MediaType.APPLICATION_JSON)
						      .build();
						} else {
							String message = "{\"retorno\": \"[Galleria Bank] Código do Responsável não encontrato!!!\"}";
							
							return Response
								      .status(Response.Status.FORBIDDEN)
								      .entity(message)
								      .type(MediaType.APPLICATION_JSON)
								      .build();		
						}
					} else {
						String message = "{\"retorno\": \"R11 - O Código do Responsável não foi encontrado.\"}";
						
						return Response
							      .status(Response.Status.FORBIDDEN)
							      .entity(message)
							      .type(MediaType.APPLICATION_JSON)
							      .build();		
					}
				} catch (org.json.JSONException exception) {
					return Response
						      .status(Response.Status.BAD_REQUEST)
						      .entity("O campo " + exception.getMessage() + " não foi encontrado no payload recebido!!!")
						      .type(MediaType.APPLICATION_JSON)
						      .build();
				}
			} else {	
				String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed!!!\"}";
				
				return Response
					      .status(Response.Status.FORBIDDEN)
					      .entity(message)
					      .type(MediaType.APPLICATION_JSON)
					      .build();				
			}
		}
	}
	
	public String geraNumeroContrato() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		int numeroUltimoContrato = Integer.valueOf(contratoCobrancaDao.ultimoNumeroContrato());

		return String.format("%05d", numeroUltimoContrato);
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	public String getNomeUsuarioLogado() {
		User usuario = getUsuarioLogado();

		if (usuario.getLogin() != null) {
			if (!usuario.getLogin().equals("")) {
				return usuario.getLogin();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public User getUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);
			}
		}

		return usuario;
	}
		
	public void clearCriacaoContrato() {
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(gerarDataHoje());
		this.objetoContratoCobranca.setDataCadastro(gerarDataHoje());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(gerarDataHoje());
		this.objetoContratoCobranca.setUserCadastro(getNomeUsuarioLogado());
		this.objetoContratoCobranca.setGeraParcelaFinal(false);

		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagador = new PagadorRecebedor();

		this.objetoContratoCobranca.setStatus("Pendente");
		this.objetoContratoCobranca.setStatusContrato("Pendente");
		this.objetoContratoCobranca.setAgAssinatura(true);
		this.objetoContratoCobranca.setAgEnvioCartorio(true);
		this.objetoContratoCobranca.setAgRegistro(true);
		this.objetoContratoCobranca.setAnaliseReprovada(false);
		this.objetoContratoCobranca.setInicioAnalise(false);
		this.objetoContratoCobranca.setStatusLead("Completo");
		
		ParametrosDao pDao = new ParametrosDao();
		List<Parametros> cobrancaRecTxJuros = pDao.findByFilter("nome", "COBRANCA_REC_TX_JUROS");
		if(!cobrancaRecTxJuros.isEmpty()) {
			this.objetoContratoCobranca
			.setTxJuros(cobrancaRecTxJuros.get(0).getValorBigDecimal());
		}
		List<Parametros> cobrancaRecMulta = pDao.findByFilter("nome", "COBRANCA_REC_MULTA");
		if(!cobrancaRecMulta.isEmpty()) {
			this.objetoContratoCobranca.setTxMulta(cobrancaRecMulta.get(0).getValorBigDecimal());
		}
	}
	
	/**
	 * 
	 * @param origem os valores são publico ou aprovado
	 * @return
	 */
	public void criaContratoBD() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		this.objetoContratoCobranca.setPagador(validaPagadorOperacao());

		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
		
		// Cria objeto imovel, caso não exista
		if (this.objetoImovelCobranca.getId() == -1) {
			long idImovel = imovelCobrancaDao.create(this.objetoImovelCobranca);
			this.objetoImovelCobranca = imovelCobrancaDao.findById(idImovel);
			this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
		} 				
		
		this.objetoContratoCobranca.setRecebedor(null);

		contratoCobrancaDao.create(this.objetoContratoCobranca);
	}
	
	public PagadorRecebedor validaPagadorOperacao() {
		/***
		 * Busca pagador
		 */
		PagadorRecebedor pagadorRecebedor = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		if (this.objetoPagador.getId() <= 0) {
			List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
			boolean registraPagador = false;
			Long idPagador = (long) 0;

			if (this.objetoPagador.getCpf() != null) {
				pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf", this.objetoPagador.getCpf());
				if (pagadorRecebedorBD.size() > 0) {
					pagadorRecebedor = pagadorRecebedorBD.get(0);
				} else {
					pagadorRecebedor = this.objetoPagador;
					registraPagador = true;
				}
			}

			if (this.objetoPagador.getCnpj() != null) {
				pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj",
						this.objetoPagador.getCnpj());
				if (pagadorRecebedorBD.size() > 0) {
					pagadorRecebedor = pagadorRecebedorBD.get(0);
				} else {
					pagadorRecebedor = this.objetoPagador;
					registraPagador = true;
				}
			}

			if (pagadorRecebedor == null) {
				pagadorRecebedor = this.objetoPagador;
			}

			if (this.objetoPagador.getSite() != null && this.objetoPagador.getSite().equals("")) {
				if (!this.objetoPagador.getSite().contains("http")) {
					this.objetoPagador
							.setSite("HTTP://" + this.objetoPagador.getSite().toLowerCase());
				}
			}

			if (registraPagador) {
				idPagador = pagadorRecebedorDao.create(pagadorRecebedor);
				this.objetoPagador = pagadorRecebedorDao.findById(idPagador);
			}
		} 
		
		return this.objetoPagador;
	}
}