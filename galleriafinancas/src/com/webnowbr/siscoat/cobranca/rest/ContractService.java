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
import javax.ws.rs.PUT;
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
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorAdicionaisDao;
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
	private List<ContratoCobranca> objetoContratoCobrancaList;
	private ImovelCobranca objetoImovelCobranca;
	private PagadorRecebedor objetoPagador;
	private List<PagadorRecebedor> pagadores;
	private Set<PagadorRecebedorAdicionais> listaPagadores;
	private Set<PagadorRecebedorSocio> listSocios;
	private PagadorRecebedorAdicionais pagadorRecebedorAdicionais;
	private static final String PENDENTE = "Pendente";
	private static final String COMPLETO = "Completo";
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	public static void main(String[] args) {
		
		String authorization = "Basic d2Vibm93YnI6IVNpc0NvQXRAMjAyMSo=";
		
		authorization = authorization.replace("Basic ", "");
		
		try {
			String[] tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");
			System.out.println("Token : "+tokens);

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
		
		if(verificarAutenticacao(authorization)) {
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
						
						this.objetoContratoCobranca.setPagadorDonoGarantia(contratoAPP.getBoolean("pagadorDonoGarantia") == true ? true : false);
						this.objetoContratoCobranca.setDivida(contratoAPP.getString("divida"));
						this.objetoContratoCobranca.setDividaValor(contratoAPP.has("dividaValor") ? new BigDecimal(contratoAPP.getDouble("dividaValor")) : null);
						
						
						/***
						 * VALORES DEFAULT
						 */
						this.objetoContratoCobranca.setUserCadastro(contratoAPP.has("userCadastro") ? contratoAPP.getString("userCadastro") : getNomeUsuarioLogado());
						this.objetoContratoCobranca.setInicioAnalise(false);
						this.objetoContratoCobranca.setStatus(contratoAPP.has("status") ? contratoAPP.getString("status") : PENDENTE);
						this.objetoContratoCobranca.setStatusContrato(contratoAPP.has("statusContrato") ? contratoAPP.getString("statusContrato") : PENDENTE);
						this.objetoContratoCobranca.setStatusLead(contratoAPP.has("statusLead") ? contratoAPP.getString("statusLead") : COMPLETO);
						this.objetoContratoCobranca.setContratoLead(contratoAPP.has("contratoLead") ? contratoAPP.getBoolean("contratoLead") : false);
						
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
							this.objetoImovelCobranca.setCidade(contratoAPPImovel.has("cidade") ? contratoAPPImovel.getString("cidade") : null);
							this.objetoImovelCobranca.setBairro(contratoAPPImovel.has("bairro") ? contratoAPPImovel.getString("bairro") : null);
							this.objetoImovelCobranca.setEstado(contratoAPPImovel.has("estado") ? contratoAPPImovel.getString("estado") : null);
							this.objetoImovelCobranca.setNumeroCartorio(contratoAPPImovel.has("numeroCartorio") ? contratoAPPImovel.getString("numeroCartorio") : null);
							this.objetoImovelCobranca.setCartorio(contratoAPPImovel.has("cartorioRegistro") ? contratoAPPImovel.getString("cartorioRegistro") : null);
							this.objetoImovelCobranca.setCartorioEstado(contratoAPPImovel.has("cartorioEstado") ? contratoAPPImovel.getString("cartorioEstado") : null);
							this.objetoImovelCobranca.setCartorioMunicipio(contratoAPPImovel.has("cartorioMunicipio") ? contratoAPPImovel.getString("cartorioMunicipio") : null);
							this.objetoImovelCobranca.setNumeroMatricula(contratoAPPImovel.has("numeroMatricula") ? contratoAPPImovel.getString("numeroMatricula") : null);
							this.objetoImovelCobranca.setTipo(contratoAPPImovel.has("tipoImovel") ? contratoAPPImovel.getString("tipoImovel") : null);
							this.objetoImovelCobranca.setComprovanteMatriculaCheckList(contratoAPPImovel.has("comprovanteMatriculaCheckList") ? contratoAPPImovel.getBoolean("comprovanteMatriculaCheckList") : false);
							this.objetoImovelCobranca.setComprovanteFotosImovelCheckList(contratoAPPImovel.has("comprovanteFotosImovelCheckList") ? contratoAPPImovel.getBoolean("comprovanteFotosImovelCheckList") : false);
							this.objetoImovelCobranca.setComprovanteIptuImovelCheckList(contratoAPPImovel.has("comprovanteIptuImovelCheckList") ? contratoAPPImovel.getBoolean("comprovanteIptuImovelCheckList") : false);
							
							this.objetoImovelCobranca.setValoEstimado(new BigDecimal(contratoAPPImovel.has("valoEstimado") ? contratoAPPImovel.getDouble("valoEstimado") : null));								
						}
						
						this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);

						// salva contrato
						Long idContratoCobranca = criaContratoBD();
						this.objetoContratoCobranca.setId(idContratoCobranca);
						criarEditarPagadoresAdicionais(contratoAPP);
						
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
			
		}else {
			String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed!!!\"}";
			
			return Response
				      .status(Response.Status.FORBIDDEN)
				      .entity(message)
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		}
	}

	@PUT
	@Path("/EditarOperacao")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editarOperacao(String operacaoData, @HeaderParam("Token") String token, @HeaderParam("Authorization") String authorization) { 
		System.out.println("[Galleria Bank] Editar Operação - Authorization: " + authorization);

		if(verificarAutenticacao(authorization)) {
			try {
				JSONObject contratoAPP = new JSONObject(operacaoData);
				
				clearEditarContrato();
				
				if (contratoAPP.has("numeroContrato")) {
					this.objetoContratoCobranca.setNumeroContrato(contratoAPP.getString("numeroContrato"));	
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					this.objetoContratoCobrancaList = contratoCobrancaDao.findByFilter("numeroContrato", contratoAPP.getString("numeroContrato"));
					if(this.objetoContratoCobrancaList.isEmpty()) {
						String message = "{\"retorno\": \"[Galleria Bank] Numero do Contrato não foi encontrato!!!\"}";
						return Response
							      .status(Response.Status.FORBIDDEN)
							      .entity(message)
							      .type(MediaType.APPLICATION_JSON)
							      .build();	
					}else {
						
						/*
						 * OBJETO CONTRATO COBRANCA
						 */
						this.objetoContratoCobranca = this.objetoContratoCobrancaList.get(0);
						JSONObject contratoAPPResponsavel = contratoAPP.getJSONObject("responsavel");
						if (contratoAPPResponsavel.has("codigoResponsavel")) {
						
							ResponsavelDao rDao = new ResponsavelDao();
							String codigoResponsavel = contratoAPPResponsavel.getString("codigoResponsavel");
							
							List<Responsavel> responsaveis = new ArrayList<Responsavel>();
							responsaveis = rDao.findByFilter("codigo", codigoResponsavel);
							
							if(!responsaveis.isEmpty()) {
								this.objetoContratoCobranca.setResponsavel(responsaveis.get(0));
							}
							
							this.objetoContratoCobranca.setTipoOperacao(contratoAPP.has("tipoOperacao") 
									? contratoAPP.getString("tipoOperacao") : this.objetoContratoCobranca.getTipoOperacao());
							String tipoPessoa = contratoAPP.has("tipoPessoa") ? contratoAPP.getString("tipoPessoa") : null;
							this.objetoContratoCobranca.setCobrarComissaoCliente(contratoAPP.has("cobrarComissaoCliente") 
									? contratoAPP.getString("cobrarComissaoCliente") : this.objetoContratoCobranca.getCobrarComissaoCliente());
							this.objetoContratoCobranca.setComissaoClienteValorFixo(contratoAPP.has("comissaoClienteValorFixo") 
									?  new BigDecimal(contratoAPP.getDouble("comissaoClienteValorFixo")) : this.objetoContratoCobranca.getComissaoClienteValorFixo());
							this.objetoContratoCobranca.setComissaoClientePorcentagem(contratoAPP.has("comissaoClientePorcentagem")
									? new BigDecimal(contratoAPP.getDouble("comissaoClientePorcentagem")) : this.objetoContratoCobranca.getComissaoClientePorcentagem());
							this.objetoContratoCobranca.setTipoCobrarComissaoCliente(contratoAPP.has("tipoCobrarComissaoCliente") 
									? contratoAPP.getString("tipoCobrarComissaoCliente") : this.objetoContratoCobranca.getTipoCobrarComissaoCliente());
							this.objetoContratoCobranca.setBrutoLiquidoCobrarComissaoCliente(contratoAPP.has("brutoLiquidoCobrarComissaoCliente") 
									? contratoAPP.getString("brutoLiquidoCobrarComissaoCliente") : this.objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente());
							
							this.objetoContratoCobranca.setQuantoPrecisa(contratoAPP.has("quantoPrecisa") 
									? new BigDecimal(contratoAPP.getDouble("quantoPrecisa")) : this.objetoContratoCobranca.getQuantoPrecisa());
							this.objetoContratoCobranca.setObservacao(contratoAPP.has("observacao") ? contratoAPP.getString("observacao") : null);
							
							this.objetoContratoCobranca.setPagadorDonoGarantia(contratoAPP.has("pagadorDonoGarantia") 
									? contratoAPP.getBoolean("pagadorDonoGarantia") : this.objetoContratoCobranca.getPagadorDonoGarantia());
							this.objetoContratoCobranca.setDivida(contratoAPP.has("divida") 
									? contratoAPP.getString("divida") : this.objetoContratoCobranca.getDivida());
							this.objetoContratoCobranca.setDividaValor(contratoAPP.has("dividaValor") 
									? new BigDecimal(contratoAPP.getDouble("dividaValor")) : this.objetoContratoCobranca.getDividaValor());
							
							/***
							 * VALORES DEFAULT
							 */
							this.objetoContratoCobranca.setStatusLead(contratoAPP.has("statusLead") ? contratoAPP.getString("statusLead") : COMPLETO);
							
							/***
							 * DADOS DO FLUXO DO CONTRATO COBRANCA - APP	
							 */
							this.objetoContratoCobranca.setValorEmprestimo(contratoAPP.has("valorEmprestimo") 
									? new BigDecimal(contratoAPP.getDouble("valorEmprestimo")) : this.objetoContratoCobranca.getValorEmprestimo());
							this.objetoContratoCobranca.setComentarioPendencia(contratoAPP.has("comentarioPendencia")
									? contratoAPP.getString("comentarioPendencia") : this.objetoContratoCobranca.getComentarioPendencia());
							this.objetoContratoCobranca.setFormaDePagamentoLaudoPAJU(contratoAPP.has("formaPagamentoLaudoPaju")
									? contratoAPP.getString("formaPagamentoLaudoPaju") 
									: this.objetoContratoCobranca.getFormaDePagamentoLaudoPAJU());
							this.objetoContratoCobranca.setNomeContatoAgendaLaudoAvaliacao(contratoAPP.has("nomeContatoAgendaLaudoAvaliacao")
									? contratoAPP.getString("nomeContatoAgendaLaudoAvaliacao")
									: this.objetoContratoCobranca.getNomeContatoAgendaLaudoAvaliacao());
							this.objetoContratoCobranca.setContatoAgendamendoLaudoAvaliacao(contratoAPP.has("contatoAgendamentoLaudoAvaliacao")
									? contratoAPP.getString("contatoAgendamentoLaudoAvaliacao")
									: this.objetoContratoCobranca.getContatoAgendamendoLaudoAvaliacao());
							this.objetoContratoCobranca.setObservacaoContatoAgendaLaudoAvaliacao(contratoAPP.has("observacaoContatoAgendaLaudoAvaliacao")
									? contratoAPP.getString("observacaoContatoAgendaLaudoAvaliacao")
									: this.objetoContratoCobranca.getObservacaoContatoAgendaLaudoAvaliacao());
							
							this.objetoContratoCobranca.setComentarioPreComite(contratoAPP.has("comentarioPreComite")
									? contratoAPP.getString("comentarioPreComite") : this.objetoContratoCobranca.getComentarioPreComite());
							
							/***
							 * OBJETO PAGADOR
							 */
							JSONObject contratoAPPPagador = contratoAPP.getJSONObject("pagadorRecebedor");
							this.objetoPagador = new PagadorRecebedor();
							this.objetoPagador.setId(contratoAPPPagador.has("id") 
									? contratoAPPPagador.getLong("id") : this.objetoContratoCobranca.getPagador().getId());
							
							if (tipoPessoa.equals("PF")) {
								this.objetoPagador.setCpf(contratoAPPPagador.has("cpfCnpj") 
									? contratoAPPPagador.getString("cpfCnpj") : this.objetoContratoCobranca.getPagador().getCpf());
							} else if(tipoPessoa.equals("PJ")) {
								this.objetoPagador.setCnpj(contratoAPPPagador.has("cpfCnpj") 
									? contratoAPPPagador.getString("cpfCnpj") : this.objetoContratoCobranca.getPagador().getCnpj());
							}
							
							this.objetoPagador.setNome(contratoAPPPagador.has("nome") 
									? contratoAPPPagador.getString("nome") : this.objetoContratoCobranca.getPagador().getNome());
							this.objetoPagador.setEmail(contratoAPPPagador.has("email") 
									? contratoAPPPagador.getString("email") : this.objetoContratoCobranca.getPagador().getEmail());
							this.objetoPagador.setTelCelular(contratoAPPPagador.has("telCelular") 
									? contratoAPPPagador.getString("telCelular") : this.objetoContratoCobranca.getPagador().getTelCelular());
							this.objetoPagador.setSexo(contratoAPPPagador.has("sexo") 
									? contratoAPPPagador.getString("sexo") : this.objetoContratoCobranca.getPagador().getSexo());
							
							SimpleDateFormat dtNascimento = new SimpleDateFormat("yyyy-MM-dd");
							Date dtNascimentoDate = null;
							try {
								if(contratoAPPPagador.has("dataNascimento")) {
									dtNascimentoDate = dtNascimento.parse(contratoAPPPagador.getString("dataNascimento"));
									this.objetoPagador.setDtNascimento(dtNascimentoDate);
								}else {
									this.objetoPagador.setDtNascimento(this.objetoContratoCobranca.getPagador().getDtNascimento());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						
							this.objetoPagador.setNomeMae(contratoAPPPagador.has("nomeMae") 
									? contratoAPPPagador.getString("nomeMae") : this.objetoContratoCobranca.getPagador().getNomeMae());
							this.objetoPagador.setEstadocivil(contratoAPPPagador.has("estadoCivil") 
									? contratoAPPPagador.getString("estadoCivil") : this.objetoContratoCobranca.getPagador().getEstadocivil());
							this.objetoPagador.setCpfConjuge(contratoAPPPagador.has("cpfConjuge") 
									? contratoAPPPagador.getString("cpfConjuge") : null);
							this.objetoPagador.setNomeConjuge(contratoAPPPagador.has("nomeConjuge") 
									? contratoAPPPagador.getString("nomeConjuge") : null);
							this.objetoPagador.setDtNascimentoConjuge(contratoAPPPagador.has("dtNascimentoConjuge") 
									? dtNascimento.parse(contratoAPPPagador.getString("dtNascimentoConjuge")) : null);
							this.objetoPagador.setNomeParticipanteCheckList(contratoAPPPagador.has("nomeParticipanteCheckList") 
									? contratoAPPPagador.getString("nomeParticipanteCheckList") : this.objetoContratoCobranca.getPagador().getNomeParticipanteCheckList()); 
							this.objetoContratoCobranca.setPagador(this.objetoPagador);
							
							/***
							 * OBJETO IMOVEL
							 */
							JSONObject contratoAPPImovel = contratoAPP.getJSONObject("imovelCobranca");
							this.objetoImovelCobranca = new ImovelCobranca();
							
								this.objetoImovelCobranca.setId(contratoAPPImovel.has("id")
										? contratoAPPImovel.getLong("id") : this.objetoContratoCobranca.getImovel().getId());
								this.objetoImovelCobranca.setCep(contratoAPPImovel.has("cep") 
										? contratoAPPImovel.getString("cep") : null);
								if(contratoAPPImovel.has("numero")) {
									this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco") + ", " + contratoAPPImovel.getString("numero"));
								}else {
									this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco"));									
								}
								this.objetoImovelCobranca.setComplemento(contratoAPPImovel.has("complemento") 
										? contratoAPPImovel.getString("complemento") : this.objetoContratoCobranca.getImovel().getComplemento());
								this.objetoImovelCobranca.setCidade(contratoAPPImovel.getString("cidade"));
								this.objetoImovelCobranca.setBairro(contratoAPPImovel.has("bairro") 
										? contratoAPPImovel.getString("bairro") : this.objetoContratoCobranca.getImovel().getBairro());
								this.objetoImovelCobranca.setEstado(contratoAPPImovel.getString("estado"));
								this.objetoImovelCobranca.setNumeroCartorio(contratoAPPImovel.has("numeroCartorio") 
										? contratoAPPImovel.getString("numeroCartorio") : this.objetoContratoCobranca.getImovel().getNumeroCartorio());
								this.objetoImovelCobranca.setCartorio(contratoAPPImovel.has("cartorioRegistro") 
										? contratoAPPImovel.getString("cartorioRegistro") : this.objetoContratoCobranca.getImovel().getCartorio());
								this.objetoImovelCobranca.setCartorioEstado(contratoAPPImovel.has("cartorioEstado") 
										? contratoAPPImovel.getString("cartorioEstado") : this.objetoContratoCobranca.getImovel().getCartorioEstado());
								this.objetoImovelCobranca.setCartorioMunicipio(contratoAPPImovel.has("cartorioMunicipio") 
										? contratoAPPImovel.getString("cartorioMunicipio") : this.objetoContratoCobranca.getImovel().getCartorioMunicipio());
								this.objetoImovelCobranca.setNumeroMatricula(contratoAPPImovel.has("numeroMatricula")
										? contratoAPPImovel.getString("numeroMatricula") : this.objetoContratoCobranca.getImovel().getNumeroMatricula());
								this.objetoImovelCobranca.setTipo(contratoAPPImovel.has("tipoImovel") 
										? contratoAPPImovel.getString("tipoImovel") : this.objetoContratoCobranca.getImovel().getTipo());
								
								this.objetoImovelCobranca.setValoEstimado(contratoAPPImovel.has("valoEstimado") 
										? new BigDecimal(contratoAPPImovel.getDouble("valoEstimado")) : this.objetoContratoCobranca.getImovel().getValoEstimado());								
							
							this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
					
							// atualizar contrato
							atualizarContratoBD();
							try {
								contratoCobrancaDao.merge(this.objetoContratoCobranca);
							} catch (RuntimeException e) {
								e.printStackTrace();
							}
							criarEditarPagadoresAdicionais(contratoAPP);
							
							String message = "{\"retorno\": \"[Galleria Bank] Operação editada com sucesso!!!\"}";
							
							return Response
									.status(Response.Status.OK)
									.entity(message)
									.type(MediaType.APPLICATION_JSON)
									.build();
						}else {
							String message = "{\"retorno\": \"R11 - O Código do Responsável não foi encontrado.\"}";
							
							return Response
								      .status(Response.Status.FORBIDDEN)
								      .entity(message)
								      .type(MediaType.APPLICATION_JSON)
								      .build();		
						}
							
					}
				} else {
					String message = "{\"retorno\": \"[Galleria Bank] Numero do Contrato não foi encontrato!!!\"}";
					return Response
						      .status(Response.Status.FORBIDDEN)
						      .entity(message)
						      .type(MediaType.APPLICATION_JSON)
						      .build();	
				}

			} catch (Exception exception) {
				return Response
					      .status(Response.Status.BAD_REQUEST)
					      .entity("O campo " + exception.getMessage() + " não foi encontrado no payload recebido!!!")
					      .type(MediaType.APPLICATION_JSON)
					      .build();
			}
			
		}else {
			String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed!!!\"}";
			
			return Response
				      .status(Response.Status.FORBIDDEN)
				      .entity(message)
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		}
	}
	
	private void criarEditarPagadoresAdicionais(JSONObject contratoAPP) {
		/***
		 * OBJETO PAGADORES ADICIONAIS
		 */
		PagadorRecebedorDao pagadorDao = new PagadorRecebedorDao();
		PagadorRecebedorAdicionaisDao pagadorAdicionaisDao = new PagadorRecebedorAdicionaisDao();
		
		if(contratoAPP.has("pagadores")) {
			JSONArray pagadoresAdicionaisAPP = contratoAPP.getJSONArray("pagadores");
			
			if(pagadoresAdicionaisAPP.length() > 0) {
				
				for(int i = 0; i < pagadoresAdicionaisAPP.length(); i++) {
					JSONObject pagadores = pagadoresAdicionaisAPP.getJSONObject(i);
					List<PagadorRecebedor> pessoas = new ArrayList<PagadorRecebedor>();
					
					String pagadoresCpfCnpj = pagadores.getString("cpfCnpj");
					if(pagadores.has("cpfCnpj") && pagadoresCpfCnpj.length() <= 14) { 
						pessoas = pagadorDao.findByFilter("cpf", pagadoresCpfCnpj);
					}else if(pagadores.has("cpfCnpj") && pagadoresCpfCnpj.length() >= 15) {
						pessoas = pagadorDao.findByFilter("cnpj", pagadoresCpfCnpj);
					}
					
					if(pessoas.isEmpty()) {
						PagadorRecebedor pessoa = new PagadorRecebedor();
						pessoa.setNome(pagadoresAdicionaisAPP.getJSONObject(i).getString("nome"));
						
						String pagadoresAdicionaisCpfCnpj = pagadoresAdicionaisAPP.getJSONObject(i).getString("cpfCnpj");
						if(pagadoresAdicionaisCpfCnpj.length() <= 14) {
							pessoa.setCpf(pagadoresAdicionaisCpfCnpj);
						}else if(pagadoresAdicionaisCpfCnpj.length() >= 15) {
							pessoa.setCnpj(pagadoresAdicionaisCpfCnpj);
						}
						pessoa.setNomeParticipanteCheckList(pagadoresAdicionaisAPP.getJSONObject(i).getString("nome"));
						
						Long idPagador = pagadorDao.create(pessoa);
						PagadorRecebedor novoPagador = pagadorDao.findById(idPagador);
						
						pagadorRecebedorAdicionais = new PagadorRecebedorAdicionais();
						pagadorRecebedorAdicionais.setId(-1);
						pagadorRecebedorAdicionais.setPessoa(novoPagador);
						pagadorRecebedorAdicionais.setContratoCobranca(this.objetoContratoCobranca);
						pagadorRecebedorAdicionais.setNomeParticipanteCheckList(pessoa.getNome());
						
						Long idPagadorAdicionais = pagadorAdicionaisDao.create(pagadorRecebedorAdicionais);
						System.out.println("Novo Pagador e Pagador Adicional ID: "+idPagadorAdicionais);
					}else {
						List<PagadorRecebedor> pagadorCadastrado = new ArrayList<PagadorRecebedor>();
						
						if(pagadoresCpfCnpj.length() <= 14) {
							pagadorCadastrado = pagadorDao.findByFilter("cpf", pagadoresCpfCnpj);
						}else if(pagadoresCpfCnpj.length() >= 15) {
							pagadorCadastrado = pagadorDao.findByFilter("cnpj", pagadoresCpfCnpj);
						}
						
						System.out.println("Pagador Adicional pessoa: "+pagadorCadastrado.get(0).getId());
						
						List<PagadorRecebedorAdicionais> pagadorAdicionaisCadastrado = 
								pagadorAdicionaisDao.getPagadorAdicionaisPessoa(pagadorCadastrado.get(0).getId());
						
						if(pagadorAdicionaisCadastrado.isEmpty()) {
							pagadorRecebedorAdicionais = new PagadorRecebedorAdicionais();
							pagadorRecebedorAdicionais.setId(-1);
							pagadorRecebedorAdicionais.setPessoa(pagadorCadastrado.get(0));
							pagadorRecebedorAdicionais.setContratoCobranca(this.objetoContratoCobranca);
							pagadorRecebedorAdicionais.setNomeParticipanteCheckList(pessoas.get(0).getNome());
							
							Long idPagadorAdicionais = pagadorAdicionaisDao.create(pagadorRecebedorAdicionais);
							
							System.out.println("Pagador Cadastrado e Pagador Adicional ID: "+idPagadorAdicionais);
						
						}
					}
				}
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
		this.objetoContratoCobranca.setGeraParcelaFinal(false);

		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagador = new PagadorRecebedor();

		this.objetoContratoCobranca.setAgAssinatura(true);
		this.objetoContratoCobranca.setAgEnvioCartorio(true);
		this.objetoContratoCobranca.setAgRegistro(true);
		this.objetoContratoCobranca.setAnaliseReprovada(false);
		this.objetoContratoCobranca.setInicioAnalise(false);
		
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
	
	public void clearEditarContrato() {
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(gerarDataHoje());
		this.objetoContratoCobranca.setDataCadastro(gerarDataHoje());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(gerarDataHoje());
		this.objetoContratoCobranca.setUserCadastro(getNomeUsuarioLogado());
		this.objetoContratoCobranca.setGeraParcelaFinal(false);
		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagador = new PagadorRecebedor();
	}
	
	/**
	 * 
	 * @param origem os valores são publico ou aprovado
	 * @return
	 */
	public Long criaContratoBD() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		validaPagadorOperacao();

		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
		
		// Cria objeto imovel, caso não exista
		if (this.objetoImovelCobranca.getId() == -1) {
			long idImovel = imovelCobrancaDao.create(this.objetoImovelCobranca);
			this.objetoImovelCobranca.setId(idImovel);
			this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
		} else {
			imovelCobrancaDao.update(this.objetoImovelCobranca);
		}
		
		this.objetoContratoCobranca.setRecebedor(null);
		
		return contratoCobrancaDao.create(this.objetoContratoCobranca);
	}
	
	public void atualizarContratoBD() {

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		pagadorRecebedorDao.merge(this.objetoPagador);

		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
		
		// Atualizar objeto imovel, caso não exista
		try { 
			imovelCobrancaDao.merge(this.objetoImovelCobranca);
		}catch (RuntimeException e) {
			System.out.println(e.getMessage());
		}
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
				if (!pagadorRecebedorBD.isEmpty()) {
					pagadorRecebedor = pagadorRecebedorBD.get(0);
				} else {
					pagadorRecebedor = this.objetoPagador;
					registraPagador = true;
				}
			}

			if (this.objetoPagador.getCnpj() != null) {
				pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj",
						this.objetoPagador.getCnpj());
				if (!pagadorRecebedorBD.isEmpty()) {
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
				this.objetoPagador.setId(idPagador);
				this.objetoContratoCobranca.setPagador(this.objetoPagador);
			}
		} 
		
		return this.objetoPagador;
	}
	
	private boolean verificarAutenticacao(String authorization) {
		if (authorization == null || !authorization.startsWith("Basic")) {
			return false;
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
				return true;
			} else {	
				return false;		
			}
		}
	}

	/**
	 * @return the objetoContratoCobranca
	 */
	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	/**
	 * @param objetoContratoCobranca the objetoContratoCobranca to set
	 */
	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	/**
	 * @return the objetoImovelCobranca
	 */
	public ImovelCobranca getObjetoImovelCobranca() {
		return objetoImovelCobranca;
	}

	/**
	 * @param objetoImovelCobranca the objetoImovelCobranca to set
	 */
	public void setObjetoImovelCobranca(ImovelCobranca objetoImovelCobranca) {
		this.objetoImovelCobranca = objetoImovelCobranca;
	}

	/**
	 * @return the objetoPagador
	 */
	public PagadorRecebedor getObjetoPagador() {
		return objetoPagador;
	}

	/**
	 * @param objetoPagador the objetoPagador to set
	 */
	public void setObjetoPagador(PagadorRecebedor objetoPagador) {
		this.objetoPagador = objetoPagador;
	}
	
	/**
	 * @return the pagadores
	 */
	public List<PagadorRecebedor> getPagadores() {
		return pagadores;
	}

	/**
	 * @param pagadores the pagadores to set
	 */
	public void setPagadores(List<PagadorRecebedor> pagadores) {
		this.pagadores = pagadores;
	}

	/**
	 * @return the listaPagadores
	 */
	public Set<PagadorRecebedorAdicionais> getListaPagadores() {
		return listaPagadores;
	}

	/**
	 * @param listaPagadores the listaPagadores to set
	 */
	public void setListaPagadores(Set<PagadorRecebedorAdicionais> listaPagadores) {
		this.listaPagadores = listaPagadores;
	}

	/**
	 * @return the listSocios
	 */
	public Set<PagadorRecebedorSocio> getListSocios() {
		return listSocios;
	}

	/**
	 * @param listSocios the listSocios to set
	 */
	public void setListSocios(Set<PagadorRecebedorSocio> listSocios) {
		this.listSocios = listSocios;
	}



	/**
	 * @return the pagadorRecebedorAdicionais
	 */
	public PagadorRecebedorAdicionais getPagadorRecebedorAdicionais() {
		return pagadorRecebedorAdicionais;
	}



	/**
	 * @param pagadorRecebedorAdicionais the pagadorRecebedorAdicionais to set
	 */
	public void setPagadorRecebedorAdicionais(PagadorRecebedorAdicionais pagadorRecebedorAdicionais) {
		this.pagadorRecebedorAdicionais = pagadorRecebedorAdicionais;
	}
	
	@Path("/")
	public String olaMundo() {
		return "Ola Mundo!!!";
	}
}