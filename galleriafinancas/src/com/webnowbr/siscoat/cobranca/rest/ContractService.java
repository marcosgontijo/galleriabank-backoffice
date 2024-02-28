package com.webnowbr.siscoat.cobranca.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedProperty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.Parametros;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@Path("/services")
public class ContractService {

	private final Logger logger = LoggerFactory.getLogger(ContractService.class);

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
	SimpleDateFormat dataPadraoSql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	private PagadorRecebedorDao objetoPagadorDao;

	public static void main(String[] args) {

		String authorization = "Basic d2Vibm93YnI6IVNpc0NvQXRAMjAyMSo=";

		authorization = authorization.replace("Basic ", "");

		try {
			String[] tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");
			System.out.println("Token : " + tokens);

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

		return Response.status(Response.Status.OK).entity(message).type(MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("/CriarOperacao")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response criarOperacao(String operacaoData, @HeaderParam("Token") String token,
			@HeaderParam("Authorization") String authorization) {
		System.out.println("Contract Service - Criar Operacao - Authorization: {} " + authorization);

		if (RestService.verificarAutenticacao(authorization)) {
			try {
				JSONObject contratoAPP = new JSONObject(operacaoData);
				System.out.println("Inicio Contract Service - criarOperacao");

				clearCriacaoContrato();

				JSONObject contratoAPPResponsavel = contratoAPP.getJSONObject("responsavel");
				System.out.println("Contract Service - Criar Operacao - Codigo do Responsavel: {} "
						+ contratoAPPResponsavel.getString("codigo"));

				if (contratoAPPResponsavel.has("codigo")) {
					ResponsavelDao rDao = new ResponsavelDao();
					String codigo = contratoAPPResponsavel.getString("codigo");

					List<Responsavel> responsaveis = new ArrayList<Responsavel>();
					responsaveis = rDao.findByFilter("codigo", codigo);

					if (responsaveis.size() > 0) {
						this.objetoContratoCobranca.setResponsavel(responsaveis.get(0));

						if (contratoAPP.has("numeroContrato")) {
							System.out.println("Contract Service - Criar Operacao - Numero do Contrato: {} "
									+ contratoAPP.getString("numeroContrato"));
							this.objetoContratoCobranca.setNumeroContrato(contratoAPP.getString("numeroContrato"));
						} else {
							this.objetoContratoCobranca.setNumeroContrato(geraNumeroContrato());
							System.out.println("Contract Service - Criar Operacao - Numero do Contrato: {} "
									+ this.objetoContratoCobranca.getNumeroContrato());
						}

						this.objetoContratoCobranca.setTipoOperacao(
								contratoAPP.has("tipoOperacao") ? contratoAPP.getString("tipoOperacao") : null);
						this.objetoContratoCobranca
								.setCobrarComissaoCliente(contratoAPP.getString("cobrarComissaoCliente"));
						this.objetoContratoCobranca
								.setComissaoClienteValorFixo(contratoAPP.has("comissaoClienteValorFixo")
										? new BigDecimal(contratoAPP.getDouble("comissaoClienteValorFixo"))
										: null);
						this.objetoContratoCobranca
								.setComissaoClientePorcentagem(contratoAPP.has("comissaoClientePorcentagem")
										? new BigDecimal(contratoAPP.getDouble("comissaoClientePorcentagem"))
										: null);
						this.objetoContratoCobranca
								.setTipoCobrarComissaoCliente(contratoAPP.has("tipoCobrarComissaoCliente")
										? contratoAPP.getString("tipoCobrarComissaoCliente")
										: null);
						this.objetoContratoCobranca.setBrutoLiquidoCobrarComissaoCliente(
								contratoAPP.has("brutoLiquidoCobrarComissaoCliente")
										? contratoAPP.getString("brutoLiquidoCobrarComissaoCliente")
										: null);

						this.objetoContratoCobranca
								.setQuantoPrecisa(new BigDecimal(contratoAPP.getDouble("quantoPrecisa")));
						

						this.objetoContratoCobranca.setTaxaPreDefinida(contratoAPP.has("taxaPreDefinida")
								? new BigDecimal(contratoAPP.getDouble("taxaPreDefinida"))
								: this.objetoContratoCobranca.getTaxaPreDefinida());
						
						System.out.println("Contract Service - Criar Operacao - Imovel QuantoPrecisa: {} "
								+ contratoAPP.getDouble("quantoPrecisa"));
						this.objetoContratoCobranca.setObservacao(
								contratoAPP.has("observacao") ? contratoAPP.getString("observacao") : null);

						this.objetoContratoCobranca.setPagadorDonoGarantia(
								contratoAPP.has("pagadorDonoGarantia") ? contratoAPP.getBoolean("pagadorDonoGarantia")
										: false);
						this.objetoContratoCobranca.setDivida(contratoAPP.getString("divida"));
						this.objetoContratoCobranca.setDividaValor(
								contratoAPP.has("dividaValor") ? new BigDecimal(contratoAPP.getDouble("dividaValor"))
										: null);

						/***
						 * VALORES DEFAULT
						 */
						this.objetoContratoCobranca
								.setUserCadastro(contratoAPP.has("userCadastro") ? contratoAPP.getString("userCadastro")
										: getNomeUsuarioLogado());
						this.objetoContratoCobranca.setInicioAnalise(false);
						this.objetoContratoCobranca
								.setStatus(contratoAPP.has("status") ? contratoAPP.getString("status") : PENDENTE);
						this.objetoContratoCobranca.setStatusContrato(
								contratoAPP.has("statusContrato") ? contratoAPP.getString("statusContrato") : PENDENTE);
						this.objetoContratoCobranca.setStatusLead(
								contratoAPP.has("statusLead") ? contratoAPP.getString("statusLead") : COMPLETO);
						this.objetoContratoCobranca.setContratoLead(
								contratoAPP.has("contratoLead") ? contratoAPP.getBoolean("contratoLead") : false);

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
							System.out.println("Contract Service - Criar Operacao - Pagador ID: {} "
									+ contratoAPPPagador.getLong("id"));
							this.objetoPagador = pagadorDao.findById(contratoAPPPagador.getLong("id"));
						} else {
							this.objetoPagador.setId(-1);

							if (contratoAPPPagador.has("cpfCnpj")) {
								if (contratoAPPPagador.getString("cpfCnpj").length() <= 14) {
									System.out.println("Contract Service - Criar Operacao - Novo Pagador CPF: {} "
											+ contratoAPPPagador.getString("cpfCnpj"));
									this.objetoPagador.setCpf(contratoAPPPagador.getString("cpfCnpj"));
								} else if (contratoAPPPagador.getString("cpfCnpj").length() >= 15) {
									System.out.println("Contract Service - Criar Operacao - Novo Pagador CNPJ: {} "
											+ contratoAPPPagador.getString("cpfCnpj"));
									this.objetoPagador.setCnpj(contratoAPPPagador.getString("cpfCnpj"));
								}
							}

							SimpleDateFormat dtNascimento = new SimpleDateFormat("yyyy-MM-dd");
							Date dtNascimentoDate = null;
							try {
								if (contratoAPPPagador.has("dataNascimento")) {
									dtNascimentoDate = dtNascimento
											.parse(contratoAPPPagador.getString("dataNascimento"));
									this.objetoPagador.setDtNascimento(dtNascimentoDate);
								} else {
									this.objetoPagador.setDtNascimento(null);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							this.objetoPagador.setRgDocumentosCheckList(contratoAPPPagador.has("rgDocumentosCheckList")
									? contratoAPPPagador.getBoolean("rgDocumentosCheckList")
									: false);
							this.objetoPagador.setComprovanteEnderecoDocumentosCheckList(
									contratoAPPPagador.has("comprovanteEnderecoDocumentosCheckList")
											? contratoAPPPagador.getBoolean("comprovanteEnderecoDocumentosCheckList")
											: false);
							this.objetoPagador.setCertidaoCasamentoNascimentoDocumentosCheckList(
									contratoAPPPagador.has("certidaoCasamentoNascimentoDocumentosCheckList")
											? contratoAPPPagador
													.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList")
											: false);
							this.objetoPagador.setFichaCadastralDocumentosCheckList(
									contratoAPPPagador.has("fichaCadastralDocumentosCheckList")
											? contratoAPPPagador.getBoolean("fichaCadastralDocumentosCheckList")
											: false);
							this.objetoPagador
									.setBancoDocumentosCheckList(contratoAPPPagador.has("bancoDocumentosCheckList")
											? contratoAPPPagador.getBoolean("bancoDocumentosCheckList")
											: false);
							this.objetoPagador.setTelefoneEmailDocumentosCheckList(
									contratoAPPPagador.has("telefoneEmailDocumentosCheckList")
											? contratoAPPPagador.getBoolean("telefoneEmailDocumentosCheckList")
											: false);
							this.objetoPagador
									.setComprovanteRendaCheckList(contratoAPPPagador.has("comprovanteRendaCheckList")
											? contratoAPPPagador.getBoolean("comprovanteRendaCheckList")
											: false);
							this.objetoPagador
									.setCombateFraudeCheckList(contratoAPPPagador.has("combateFraudeCheckList")
											? contratoAPPPagador.getBoolean("combateFraudeCheckList")
											: false);
							this.objetoPagador
									.setCargoOcupacaoCheckList(contratoAPPPagador.has("cargoOcupacaoCheckList")
											? contratoAPPPagador.getBoolean("cargoOcupacaoCheckList")
											: false);
							this.objetoPagador.setTaxaCheckList(contratoAPPPagador.has("taxaCheckList")
									? contratoAPPPagador.getBoolean("taxaCheckList")
									: false);
						}

						this.objetoPagador
								.setNome(contratoAPPPagador.has("nome") ? contratoAPPPagador.getString("nome") : null);
						this.objetoPagador.setEmail(
								contratoAPPPagador.has("email") ? contratoAPPPagador.getString("email") : null);
						this.objetoPagador.setTelCelular(
								contratoAPPPagador.has("telCelular") ? contratoAPPPagador.getString("telCelular")
										: null);
						this.objetoPagador
								.setSexo(contratoAPPPagador.has("sexo") ? contratoAPPPagador.getString("sexo") : null);
						this.objetoPagador.setNomeMae(
								contratoAPPPagador.has("nomeMae") ? contratoAPPPagador.getString("nomeMae") : null);
						this.objetoPagador.setEstadocivil(
								contratoAPPPagador.has("estadoCivil") ? contratoAPPPagador.getString("estadoCivil")
										: null);
						this.objetoPagador.setCpfConjuge(
								contratoAPPPagador.has("cpfConjuge") ? contratoAPPPagador.getString("cpfConjuge")
										: null);
						this.objetoPagador.setNomeConjuge(
								contratoAPPPagador.has("nomeConjuge") ? contratoAPPPagador.getString("nomeConjuge")
										: null);
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
							this.objetoImovelCobranca
									.setCep(contratoAPPImovel.has("cep") ? contratoAPPImovel.getString("cep") : null);
							System.out.println("Contract Service - Criar Operacao - Imovel CEP: {} "
									+ contratoAPPImovel.getString("cep"));
							if (contratoAPPImovel.has("numero")) {
								this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco") + ", "
										+ contratoAPPImovel.getString("numero"));
							} else {
								this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco"));
							}
							System.out.println("Contract Service - Criar Operacao - Imovel Endereço: {} "
									+ contratoAPPImovel.getString("endereco"));
							this.objetoImovelCobranca.setComplemento(
									contratoAPPImovel.has("complemento") ? contratoAPPImovel.getString("complemento")
											: null);
							this.objetoImovelCobranca.setCidade(
									contratoAPPImovel.has("cidade") ? contratoAPPImovel.getString("cidade") : null);
							this.objetoImovelCobranca.setBairro(
									contratoAPPImovel.has("bairro") ? contratoAPPImovel.getString("bairro") : null);
							this.objetoImovelCobranca.setEstado(
									contratoAPPImovel.has("estado") ? contratoAPPImovel.getString("estado") : null);
							this.objetoImovelCobranca.setNumeroCartorio(contratoAPPImovel.has("numeroCartorio")
									? contratoAPPImovel.getString("numeroCartorio")
									: null);
							this.objetoImovelCobranca.setCartorio(contratoAPPImovel.has("cartorioRegistro")
									? contratoAPPImovel.getString("cartorioRegistro")
									: null);
							this.objetoImovelCobranca.setCartorioEstado(contratoAPPImovel.has("cartorioEstado")
									? contratoAPPImovel.getString("cartorioEstado")
									: null);
							this.objetoImovelCobranca.setCartorioMunicipio(contratoAPPImovel.has("cartorioMunicipio")
									? contratoAPPImovel.getString("cartorioMunicipio")
									: null);
							this.objetoImovelCobranca.setNumeroMatricula(contratoAPPImovel.has("numeroMatricula")
									? contratoAPPImovel.getString("numeroMatricula")
									: null);
							this.objetoImovelCobranca.setTipo(
									contratoAPPImovel.has("tipoImovel") ? contratoAPPImovel.getString("tipoImovel")
											: null);
							this.objetoImovelCobranca.setComprovanteMatriculaCheckList(
									contratoAPPImovel.has("comprovanteMatriculaCheckList")
											? contratoAPPImovel.getBoolean("comprovanteMatriculaCheckList")
											: false);
							this.objetoImovelCobranca.setComprovanteFotosImovelCheckList(
									contratoAPPImovel.has("comprovanteFotosImovelCheckList")
											? contratoAPPImovel.getBoolean("comprovanteFotosImovelCheckList")
											: false);
							this.objetoImovelCobranca.setComprovanteIptuImovelCheckList(
									contratoAPPImovel.has("comprovanteIptuImovelCheckList")
											? contratoAPPImovel.getBoolean("comprovanteIptuImovelCheckList")
											: false);

							this.objetoImovelCobranca.setValoEstimado(new BigDecimal(
									contratoAPPImovel.has("valoEstimado") ? contratoAPPImovel.getDouble("valoEstimado")
											: null));
							System.out.println("Contract Service - Criar Operacao - Imovel ValoEstimado: {} "
									+ contratoAPPImovel.getDouble("valoEstimado"));
						}

						this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
						User user = getUsuarioLogado();
						if (user.getId() <= 0) {
							user = null;
						}
						this.objetoContratoCobranca.populaStatusEsteira(user);
						// salva contrato
						Long idContratoCobranca = criaContratoBD();
						this.objetoContratoCobranca.setId(idContratoCobranca);
						criarEditarPagadoresAdicionais(contratoAPP);

						String message = "{\"retorno\": \"Contract Service - Criar Operação - Operação criada com sucesso !!!\"}";
						System.out.println("Fim Contract Service - Criar Operacao - Operacao criada com sucesso !!!");
						return Response.status(Response.Status.OK).entity(message).type(MediaType.APPLICATION_JSON)
								.build();
					} else {
						String message = "{\"retorno\": \"[Galleria Bank] Código do Responsável não encontrato !!!\"}";
						logger.warn("Contract Service - Criar Operacao - Código do Responsável não encontrato !!!");

						return Response.status(Response.Status.FORBIDDEN).entity(message)
								.type(MediaType.APPLICATION_JSON).build();
					}
				} else {
					String message = "{\"retorno\": \"R11 - O Código do Responsável não foi encontrado.\"}";
					logger.warn(
							"Contract Service - Criar Operacao - R11 - O Código do Responsável não foi encontrado.");

					return Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.APPLICATION_JSON)
							.build();
				}
			} catch (org.json.JSONException exception) {
				logger.warn("Contract Service - Criar Operacao - O campo " + exception.getMessage()
						+ " não foi encontrado no payload recebido!!!");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("O campo " + exception.getMessage() + " não foi encontrado no payload recebido !!!")
						.type(MediaType.APPLICATION_JSON).build();
			}

		} else {
			String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed !!!\"}";
			logger.warn("Contract Service - Criar Operacao - Authentication Failed !!!");

			return Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.APPLICATION_JSON).build();
		}
	}

	@PUT
	@Path("/EditarOperacao")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editarOperacao(String operacaoData, @HeaderParam("Token") String token,
			@HeaderParam("Authorization") String authorization) {
		System.out.println("[Galleria Bank] Editar Operação - Authorization: " + authorization);

		if (RestService.verificarAutenticacao(authorization)) {
			try {
				JSONObject contratoAPP = new JSONObject(operacaoData);
				System.out.println("Inicio Contract Service - Editar Operacao");

				clearEditarContrato();

				if (contratoAPP.has("numeroContrato")) {
					this.objetoContratoCobranca.setNumeroContrato(contratoAPP.getString("numeroContrato"));
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					this.objetoContratoCobrancaList = contratoCobrancaDao.findByFilter("numeroContrato",
							contratoAPP.getString("numeroContrato"));
					if (this.objetoContratoCobrancaList.isEmpty()) {
						String message = "{\"retorno\": \"[Galleria Bank] Numero do Contrato não foi encontrato!!!\"}";
						logger.warn("Contract Service - Criar Operacao - Numero do Contrato não foi encontrato !!!");
						return Response.status(Response.Status.FORBIDDEN).entity(message)
								.type(MediaType.APPLICATION_JSON).build();
					} else {

						/*
						 * OBJETO CONTRATO COBRANCA
						 */
						this.objetoContratoCobranca = this.objetoContratoCobrancaList.get(0);
						JSONObject contratoAPPResponsavel = contratoAPP.getJSONObject("responsavel");
						if (contratoAPPResponsavel.has("codigo")) {

							ResponsavelDao rDao = new ResponsavelDao();
							String codigo = contratoAPPResponsavel.getString("codigo");

							List<Responsavel> responsaveis = new ArrayList<Responsavel>();
							responsaveis = rDao.findByFilter("codigo", codigo);

							if (!responsaveis.isEmpty()) {
								this.objetoContratoCobranca.setResponsavel(responsaveis.get(0));
							}

							this.objetoContratoCobranca.setTipoOperacao(
									contratoAPP.has("tipoOperacao") ? contratoAPP.getString("tipoOperacao")
											: this.objetoContratoCobranca.getTipoOperacao());
							String tipoPessoa = contratoAPP.has("tipoPessoa") ? contratoAPP.getString("tipoPessoa")
									: null;
							this.objetoContratoCobranca
									.setCobrarComissaoCliente(contratoAPP.has("cobrarComissaoCliente")
											? contratoAPP.getString("cobrarComissaoCliente")
											: this.objetoContratoCobranca.getCobrarComissaoCliente());
							this.objetoContratoCobranca
									.setComissaoClienteValorFixo(contratoAPP.has("comissaoClienteValorFixo")
											? new BigDecimal(contratoAPP.getDouble("comissaoClienteValorFixo"))
											: this.objetoContratoCobranca.getComissaoClienteValorFixo());
							this.objetoContratoCobranca
									.setComissaoClientePorcentagem(contratoAPP.has("comissaoClientePorcentagem")
											? new BigDecimal(contratoAPP.getDouble("comissaoClientePorcentagem"))
											: this.objetoContratoCobranca.getComissaoClientePorcentagem());
							this.objetoContratoCobranca
									.setTipoCobrarComissaoCliente(contratoAPP.has("tipoCobrarComissaoCliente")
											? contratoAPP.getString("tipoCobrarComissaoCliente")
											: this.objetoContratoCobranca.getTipoCobrarComissaoCliente());
							this.objetoContratoCobranca.setBrutoLiquidoCobrarComissaoCliente(
									contratoAPP.has("brutoLiquidoCobrarComissaoCliente")
											? contratoAPP.getString("brutoLiquidoCobrarComissaoCliente")
											: this.objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente());

							this.objetoContratoCobranca.setQuantoPrecisa(contratoAPP.has("quantoPrecisa")
									? new BigDecimal(contratoAPP.getDouble("quantoPrecisa"))
									: this.objetoContratoCobranca.getQuantoPrecisa());
							
							this.objetoContratoCobranca.setTaxaPreDefinida(contratoAPP.has("taxaPreDefinida")
									? new BigDecimal(contratoAPP.getDouble("taxaPreDefinida"))
									: this.objetoContratoCobranca.getTaxaPreDefinida());
							
							this.objetoContratoCobranca.setObservacao(
									contratoAPP.has("observacao") ? contratoAPP.getString("observacao") : null);

							this.objetoContratoCobranca.setPagadorDonoGarantia(contratoAPP.has("pagadorDonoGarantia")
									? contratoAPP.getBoolean("pagadorDonoGarantia")
									: this.objetoContratoCobranca.getPagadorDonoGarantia());
							this.objetoContratoCobranca
									.setDivida(contratoAPP.has("divida") ? contratoAPP.getString("divida")
											: this.objetoContratoCobranca.getDivida());
							this.objetoContratoCobranca.setDividaValor(contratoAPP.has("dividaValor")
									? new BigDecimal(contratoAPP.getDouble("dividaValor"))
									: this.objetoContratoCobranca.getDividaValor());

							/***
							 * VALORES DEFAULT
							 */
							this.objetoContratoCobranca.setStatusLead(
									contratoAPP.has("statusLead") ? contratoAPP.getString("statusLead") : COMPLETO);

							/***
							 * DADOS DO FLUXO DO CONTRATO COBRANCA - APP
							 */
							this.objetoContratoCobranca.setValorEmprestimo(contratoAPP.has("valorEmprestimo")
									? new BigDecimal(contratoAPP.getDouble("valorEmprestimo"))
									: this.objetoContratoCobranca.getValorEmprestimo());
							this.objetoContratoCobranca.setComentarioPendencia(contratoAPP.has("comentarioPendencia")
									? contratoAPP.getString("comentarioPendencia")
									: this.objetoContratoCobranca.getComentarioPendencia());
							this.objetoContratoCobranca
									.setFormaDePagamentoLaudoPAJU(contratoAPP.has("formaPagamentoLaudoPaju")
											? contratoAPP.getString("formaPagamentoLaudoPaju")
											: this.objetoContratoCobranca.getFormaDePagamentoLaudoPAJU());
							this.objetoContratoCobranca.setNomeContatoAgendaLaudoAvaliacao(
									contratoAPP.has("nomeContatoAgendaLaudoAvaliacao")
											? contratoAPP.getString("nomeContatoAgendaLaudoAvaliacao")
											: this.objetoContratoCobranca.getNomeContatoAgendaLaudoAvaliacao());
							this.objetoContratoCobranca.setContatoAgendamendoLaudoAvaliacao(
									contratoAPP.has("contatoAgendamentoLaudoAvaliacao")
											? contratoAPP.getString("contatoAgendamentoLaudoAvaliacao")
											: this.objetoContratoCobranca.getContatoAgendamendoLaudoAvaliacao());
							this.objetoContratoCobranca.setObservacaoContatoAgendaLaudoAvaliacao(
									contratoAPP.has("observacaoContatoAgendaLaudoAvaliacao")
											? contratoAPP.getString("observacaoContatoAgendaLaudoAvaliacao")
											: this.objetoContratoCobranca.getObservacaoContatoAgendaLaudoAvaliacao());
							this.objetoContratoCobranca.setComentarioContatoAgendaLaudoAvaliacao(
									contratoAPP.has("comentarioContatoAgendaLaudoAvaliacao")
											? contratoAPP.getString("comentarioContatoAgendaLaudoAvaliacao")
											: this.objetoContratoCobranca.getComentarioContatoAgendaLaudoAvaliacao());

							this.objetoContratoCobranca.setComentarioPreComite(contratoAPP.has("comentarioPreComite")
									? contratoAPP.getString("comentarioPreComite")
									: this.objetoContratoCobranca.getComentarioPreComite());

							/***
							 * OBJETO PAGADOR
							 */
							JSONObject contratoAPPPagador = contratoAPP.getJSONObject("pagadorRecebedor");
							this.objetoPagador = new PagadorRecebedor();
							this.objetoPagadorDao = new PagadorRecebedorDao();
							
							//caso id seja encontrado => fazer find by ID usando PagadorDao
							if(contratoAPPPagador.has("id")) {
								this.objetoPagador = this.objetoPagadorDao.findById(contratoAPPPagador.getLong("id"));
							}
							//caso o cpf ja esteja no banco mas o id ainda nao => fazer find by CPF/CNPJ usando PagadorDao
							else if (!contratoAPPPagador.has("id") && contratoAPPPagador.has("cpfCnpj") ) {
								  
								  this.objetoPagador = this.objetoPagadorDao.getConsultaByCpfCnpj(contratoAPPPagador.getString("cpfCnpj"));
							        // Se existir, busca o pagador pelo cpf/cnpj e atualiza apenas os dados vazios
								
							}
							//caso nao tenha nem id ou cpf/cnpj na base => novo cadastro
							
							if (StringUtils.isNotEmpty(tipoPessoa) && tipoPessoa.equals("PF")) {
								if(CommonsUtil.semValor(this.objetoPagador.getCpf())) 	
									this.objetoPagador.setCpf(contratoAPPPagador.has("cpfCnpj") ? contratoAPPPagador.getString("cpfCnpj")
												: this.objetoContratoCobranca.getPagador().getCpf());
							} else if (StringUtils.isNotEmpty(tipoPessoa) && tipoPessoa.equals("PJ")) {
								if(CommonsUtil.semValor(this.objetoPagador.getCnpj())) 
								this.objetoPagador.setCnpj(contratoAPPPagador.has("cpfCnpj") ? contratoAPPPagador.getString("cpfCnpj")
												: this.objetoContratoCobranca.getPagador().getCnpj());
							}
							if(CommonsUtil.semValor(this.objetoPagador.getNome())) 
							this.objetoPagador.setNome(contratoAPPPagador.has("nome") ? contratoAPPPagador.getString("nome")
											: this.objetoContratoCobranca.getPagador().getNome());
							if(CommonsUtil.semValor(this.objetoPagador.getEmail())) 
							this.objetoPagador.setEmail(contratoAPPPagador.has("email") ? contratoAPPPagador.getString("email")
											: this.objetoContratoCobranca.getPagador().getEmail());
							if(CommonsUtil.semValor(this.objetoPagador.getTelCelular())) 
							this.objetoPagador.setTelCelular(
									contratoAPPPagador.has("telCelular") ? contratoAPPPagador.getString("telCelular")
											: this.objetoContratoCobranca.getPagador().getTelCelular());
							if(CommonsUtil.semValor(this.objetoPagador.getSexo())) 
							this.objetoPagador.setSexo(contratoAPPPagador.has("sexo") ? contratoAPPPagador.getString("sexo")
											: this.objetoContratoCobranca.getPagador().getSexo());

							SimpleDateFormat dtNascimento = new SimpleDateFormat("yyyy-MM-dd");
							Date dtNascimentoDate = null;
							if(CommonsUtil.semValor(this.objetoPagador.getDtNascimento())) { 
								try {
									if (contratoAPPPagador.has("dataNascimento")) {
										dtNascimentoDate = dtNascimento
												.parse(contratoAPPPagador.getString("dataNascimento"));
										this.objetoPagador.setDtNascimento(dtNascimentoDate);
									} else {
										this.objetoPagador.setDtNascimento(
												this.objetoContratoCobranca.getPagador().getDtNascimento());
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
							if(CommonsUtil.semValor(this.objetoPagador.getNomeMae())) 
							this.objetoPagador.setNomeMae(
									contratoAPPPagador.has("nomeMae") ? contratoAPPPagador.getString("nomeMae")
											: this.objetoContratoCobranca.getPagador().getNomeMae());
							if(CommonsUtil.semValor(this.objetoPagador.getEstadocivil())) 
							this.objetoPagador.setEstadocivil(
									contratoAPPPagador.has("estadoCivil") ? contratoAPPPagador.getString("estadoCivil")
											: this.objetoContratoCobranca.getPagador().getEstadocivil());
							if(CommonsUtil.semValor(this.objetoPagador.getCpfConjuge())) 
							this.objetoPagador.setCpfConjuge(
									contratoAPPPagador.has("cpfConjuge") ? contratoAPPPagador.getString("cpfConjuge")
											: null);
							if(CommonsUtil.semValor(this.objetoPagador.getNomeConjuge())) 
							this.objetoPagador.setNomeConjuge(
									contratoAPPPagador.has("nomeConjuge") ? contratoAPPPagador.getString("nomeConjuge")
											: null);
							if(CommonsUtil.semValor(this.objetoPagador.getDtNascimentoConjuge())) 
							this.objetoPagador.setDtNascimentoConjuge(contratoAPPPagador.has("dtNascimentoConjuge")
									? dtNascimento.parse(contratoAPPPagador.getString("dtNascimentoConjuge"))
									: null);
							if(CommonsUtil.semValor(this.objetoPagador.getNomeParticipanteCheckList())) 
							this.objetoPagador
									.setNomeParticipanteCheckList(contratoAPPPagador.has("nomeParticipanteCheckList")
											? contratoAPPPagador.getString("nomeParticipanteCheckList")
											: this.objetoContratoCobranca.getPagador().getNomeParticipanteCheckList());
													     
						
							this.objetoContratoCobranca.setPagador(this.objetoPagador);	
							
							

// Fazer uma coluna origem para checar de onde esta vindo o cadastro
							
							/***
							 * OBJETO IMOVEL
							 */
							JSONObject contratoAPPImovel = contratoAPP.getJSONObject("imovelCobranca");
							this.objetoImovelCobranca = new ImovelCobranca();

							this.objetoImovelCobranca
									.setId(contratoAPPImovel.has("id") ? contratoAPPImovel.getLong("id")
											: this.objetoContratoCobranca.getImovel().getId());
							this.objetoImovelCobranca
									.setCep(contratoAPPImovel.has("cep") ? contratoAPPImovel.getString("cep") : null);
							if (contratoAPPImovel.has("numero")) {
								this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco") + ", "
										+ contratoAPPImovel.getString("numero"));
							} else {
								this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco"));
							}
							this.objetoImovelCobranca.setComplemento(
									contratoAPPImovel.has("complemento") ? contratoAPPImovel.getString("complemento")
											: this.objetoContratoCobranca.getImovel().getComplemento());
							this.objetoImovelCobranca.setCidade(contratoAPPImovel.getString("cidade"));
							this.objetoImovelCobranca
									.setBairro(contratoAPPImovel.has("bairro") ? contratoAPPImovel.getString("bairro")
											: this.objetoContratoCobranca.getImovel().getBairro());
							this.objetoImovelCobranca.setEstado(contratoAPPImovel.getString("estado"));
							this.objetoImovelCobranca.setNumeroCartorio(contratoAPPImovel.has("numeroCartorio")
									? contratoAPPImovel.getString("numeroCartorio")
									: this.objetoContratoCobranca.getImovel().getNumeroCartorio());
							this.objetoImovelCobranca.setCartorio(contratoAPPImovel.has("cartorioRegistro")
									? contratoAPPImovel.getString("cartorioRegistro")
									: this.objetoContratoCobranca.getImovel().getCartorio());
							this.objetoImovelCobranca.setCartorioEstado(contratoAPPImovel.has("cartorioEstado")
									? contratoAPPImovel.getString("cartorioEstado")
									: this.objetoContratoCobranca.getImovel().getCartorioEstado());
							this.objetoImovelCobranca.setCartorioMunicipio(contratoAPPImovel.has("cartorioMunicipio")
									? contratoAPPImovel.getString("cartorioMunicipio")
									: this.objetoContratoCobranca.getImovel().getCartorioMunicipio());
							this.objetoImovelCobranca.setNumeroMatricula(contratoAPPImovel.has("numeroMatricula")
									? contratoAPPImovel.getString("numeroMatricula")
									: this.objetoContratoCobranca.getImovel().getNumeroMatricula());
							this.objetoImovelCobranca.setTipo(
									contratoAPPImovel.has("tipoImovel") ? contratoAPPImovel.getString("tipoImovel")
											: this.objetoContratoCobranca.getImovel().getTipo());

							this.objetoImovelCobranca.setValoEstimado(contratoAPPImovel.has("valoEstimado")
									? new BigDecimal(contratoAPPImovel.getDouble("valoEstimado"))
									: this.objetoContratoCobranca.getImovel().getValoEstimado());

							this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
							
							//Novos campos referentes a NF
							this.objetoContratoCobranca
									.setNotaFiscalEmitida(contratoAPP.has("notaFiscalEmitida") ? contratoAPP.getBoolean("notaFiscalEmitida")
									: this.objetoContratoCobranca.isNotaFiscalEmitida());
							
							this.objetoContratoCobranca
									.setNotaFiscalEmitidaUsuario(contratoAPP.has("notaFiscalEmitidaUsuario") ? contratoAPP.getString("notaFiscalEmitidaUsuario")
									: this.objetoContratoCobranca.getNotaFiscalEmitidaUsuario());
							
							if (contratoAPP.has("notaFiscalEmitidaData")) {
								Date notaFiscalEmitidaData;
								try {
									notaFiscalEmitidaData = dataPadraoSql
											.parse(contratoAPP.getString("notaFiscalEmitidaData"));
								} catch (Exception e) {
									notaFiscalEmitidaData = CommonsUtil.dateValue(
											contratoAPP.getString("notaFiscalEmitidaData"), "yyyy-MM-dd HH:mm");
								}
		
								this.objetoContratoCobranca.setNotaFiscalEmitidaData(notaFiscalEmitidaData);
							}
							
							this.objetoContratoCobranca
									.setNotaFiscalPaga(contratoAPP.has("notaFiscalPaga") ? contratoAPP.getBoolean("notaFiscalPaga")
									: this.objetoContratoCobranca.isNotaFiscalPaga());
					
							this.objetoContratoCobranca
									.setNotaFiscalPagaUsuario(contratoAPP.has("notaFiscalPagaUsuario") ? contratoAPP.getString("notaFiscalPagaUsuario")
									: this.objetoContratoCobranca.getNotaFiscalPagaUsuario());
							
							if (contratoAPP.has("notaFiscalPagaData")) {
								Date dateNotaFiscalPaga;
								try {
									dateNotaFiscalPaga = dataPadraoSql
											.parse(contratoAPP.getString("notaFiscalPagaData"));
								} catch (Exception e) {
									dateNotaFiscalPaga = CommonsUtil.dateValue(
											contratoAPP.getString("notaFiscalPagaData"), "yyyy-MM-dd HH:mm");
								}
		
								this.objetoContratoCobranca.setNotaFiscalEmitidaData(dateNotaFiscalPaga);
							}
							
							/*if (contratoAPP.has("dataUltimaAtualizacao")) {
								Date dateUltimaAtualizacao;
								try {
									dateUltimaAtualizacao = dataPadraoSql
											.parse(contratoAPP.getString("dataUltimaAtualizacao"));
								} catch (Exception e) {
									dateUltimaAtualizacao = CommonsUtil.dateValue(
											contratoAPP.getString("dataUltimaAtualizacao"), "yyyy-MM-dd HH:mm");
								}
		
								this.objetoContratoCobranca.setDataUltimaAtualizacao(dateUltimaAtualizacao);
							}*/
							

							// atualizar contrato
							atualizarContratoBD();

							User user = getUsuarioLogado();
							if (user.getId() <= 0) {
								user = null;
							}
							this.objetoContratoCobranca.populaStatusEsteira(user);
							this.objetoContratoCobranca
									.setContratoPrioridadeAlta(contratoAPP.has("contratoPrioridadeAlta")
											? contratoAPP.getBoolean("contratoPrioridadeAlta")
											: false); 
											
							if (contratoAPP.has("contratoPrioridadeAltaData")) {
								Date datePrioridade;
								try {
									datePrioridade = dataPadraoSql
											.parse(contratoAPP.getString("contratoPrioridadeAltaData"));
								} catch (Exception e) {
									datePrioridade = CommonsUtil.dateValue(
											contratoAPP.getString("contratoPrioridadeAltaData"), "yyyy-MM-dd HH:mm");
								}

								this.objetoContratoCobranca.setContratoPrioridadeAltaData(datePrioridade);
							}
							
							this.objetoContratoCobranca
									.setContratoPrioridadeAltaUser(contratoAPP.has("contratoPrioridadeAltaUser")
											? contratoAPP.getString("contratoPrioridadeAltaUser")
											: null);
	
							try {
								contratoCobrancaDao.merge(this.objetoContratoCobranca);
							} catch (RuntimeException e) {
								e.printStackTrace();
							}
							criarEditarPagadoresAdicionais(contratoAPP);

							String message = "{\"retorno\": \"[Galleria Bank] Operação editada com sucesso!!!\"}";
							System.out.println(
									"Fim Contract Service - Editar Operacao - Operacao editada com sucesso !!!");
							return Response.status(Response.Status.OK).entity(message).type(MediaType.APPLICATION_JSON)
									.build();
						} else {
							String message = "{\"retorno\": \"R11 - O Código do Responsável não foi encontrado.\"}";
							logger.warn(
									"Contract Service - Criar Operacao - R11 - O Código do Responsável não foi encontrado.");
							return Response.status(Response.Status.FORBIDDEN).entity(message)
									.type(MediaType.APPLICATION_JSON).build();
						}

					}
				} else {
					String message = "{\"retorno\": \"[Galleria Bank] Numero do Contrato não foi encontrato !!!\"}";
					logger.warn("Contract Service - Criar Operacao - Numero do Contrato não foi encontrato !!!");
					return Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.APPLICATION_JSON)
							.build();
				}

			} catch (Exception exception) {
				logger.warn("Contract Service - Editar Operacao - O campo " + exception.getMessage()
						+ " não foi encontrado no payload recebido!!!");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("O campo " + exception.getMessage() + " não foi encontrado no payload recebido!!!")
						.type(MediaType.APPLICATION_JSON).build();
			}

		} else {
			String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed!!!\"}";
			logger.warn("Contract Service - Editar Operacao - Authentication Failed !!!");

			return Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.APPLICATION_JSON).build();
		}
	}

	private void criarEditarPagadoresAdicionais(JSONObject contratoAPP) {
		/***
		 * OBJETO PAGADORES ADICIONAIS
		 */
		PagadorRecebedorDao pagadorDao = new PagadorRecebedorDao();
		PagadorRecebedorAdicionaisDao pagadorAdicionaisDao = new PagadorRecebedorAdicionaisDao();

		if (contratoAPP.has("pagadores")) {
			JSONArray pagadoresAdicionaisAPP = contratoAPP.getJSONArray("pagadores");

			if (pagadoresAdicionaisAPP.length() > 0) {

				for (int i = 0; i < pagadoresAdicionaisAPP.length(); i++) {
					JSONObject pagadores = pagadoresAdicionaisAPP.getJSONObject(i);
					List<PagadorRecebedor> pessoas = new ArrayList<PagadorRecebedor>();

					if (pagadores.has("cpfCnpj")) {
						String pagadoresCpfCnpj = pagadores.getString("cpfCnpj");
						if (pagadores.has("cpfCnpj") && pagadoresCpfCnpj.length() <= 14) {
							pessoas = pagadorDao.findByFilter("cpf", pagadoresCpfCnpj);
						} else if (pagadores.has("cpfCnpj") && pagadoresCpfCnpj.length() >= 15) {
							pessoas = pagadorDao.findByFilter("cnpj", pagadoresCpfCnpj);
						}

						if (pessoas.isEmpty()) {
							PagadorRecebedor pessoa = new PagadorRecebedor();
							pessoa.setNome(pagadoresAdicionaisAPP.getJSONObject(i).getString("nome"));

							String pagadoresAdicionaisCpfCnpj = pagadoresAdicionaisAPP.getJSONObject(i)
									.getString("cpfCnpj");
							if (pagadoresAdicionaisCpfCnpj.length() <= 14) {
								pessoa.setCpf(pagadoresAdicionaisCpfCnpj);
							} else if (pagadoresAdicionaisCpfCnpj.length() >= 15) {
								pessoa.setCnpj(pagadoresAdicionaisCpfCnpj);
							}
							pessoa.setNomeParticipanteCheckList(
									pagadoresAdicionaisAPP.getJSONObject(i).getString("nome"));

							Long idPagador = pagadorDao.create(pessoa);
							PagadorRecebedor novoPagador = pagadorDao.findById(idPagador);

							pagadorRecebedorAdicionais = new PagadorRecebedorAdicionais();
							pagadorRecebedorAdicionais.setId(-1);
							pagadorRecebedorAdicionais.setPessoa(novoPagador);
							pagadorRecebedorAdicionais.setContratoCobranca(this.objetoContratoCobranca);
							pagadorRecebedorAdicionais.setNomeParticipanteCheckList(pessoa.getNome());

							Long idPagadorAdicionais = pagadorAdicionaisDao.create(pagadorRecebedorAdicionais);
							System.out.println("Novo Pagador e Pagador Adicional ID: " + idPagadorAdicionais);
						} else {
							List<PagadorRecebedor> pagadorCadastrado = new ArrayList<PagadorRecebedor>();

							if (pagadoresCpfCnpj.length() <= 14) {
								pagadorCadastrado = pagadorDao.findByFilter("cpf", pagadoresCpfCnpj);
							} else if (pagadoresCpfCnpj.length() >= 15) {
								pagadorCadastrado = pagadorDao.findByFilter("cnpj", pagadoresCpfCnpj);
							}

							System.out.println("Pagador Adicional pessoa: " + pagadorCadastrado.get(0).getId());

							List<PagadorRecebedorAdicionais> pagadorAdicionaisCadastrado = pagadorAdicionaisDao
									.getPagadorAdicionaisPessoa(pagadorCadastrado.get(0).getId());

							if (pagadorAdicionaisCadastrado.isEmpty()) {
								pagadorRecebedorAdicionais = new PagadorRecebedorAdicionais();
								pagadorRecebedorAdicionais.setId(-1);
								pagadorRecebedorAdicionais.setPessoa(pagadorCadastrado.get(0));
								pagadorRecebedorAdicionais.setContratoCobranca(this.objetoContratoCobranca);
								pagadorRecebedorAdicionais.setNomeParticipanteCheckList(pessoas.get(0).getNome());

								Long idPagadorAdicionais = pagadorAdicionaisDao.create(pagadorRecebedorAdicionais);

								System.out.println("Pagador Cadastrado e Pagador Adicional ID: " + idPagadorAdicionais);

							}
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
		this.objetoContratoCobranca.setDataContrato(DateUtil.gerarDataHoje());
		this.objetoContratoCobranca.setDataCadastro(DateUtil.gerarDataHoje());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(DateUtil.gerarDataHoje());
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
		if (!cobrancaRecTxJuros.isEmpty()) {
			this.objetoContratoCobranca.setTxJuros(cobrancaRecTxJuros.get(0).getValorBigDecimal());
		}
		List<Parametros> cobrancaRecMulta = pDao.findByFilter("nome", "COBRANCA_REC_MULTA");
		if (!cobrancaRecMulta.isEmpty()) {
			this.objetoContratoCobranca.setTxMulta(cobrancaRecMulta.get(0).getValorBigDecimal());
		}
	}

	public void clearEditarContrato() {
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(DateUtil.gerarDataHoje());
		this.objetoContratoCobranca.setDataCadastro(DateUtil.gerarDataHoje());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(DateUtil.gerarDataHoje());
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
		} catch (RuntimeException e) {
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
				pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj", this.objetoPagador.getCnpj());
				if (!pagadorRecebedorBD.isEmpty()) {
					pagadorRecebedor = pagadorRecebedorBD.get(0);
				} else {
					pagadorRecebedor = this.objetoPagador;
					registraPagador = true;
				}
			}

			if (StringUtils.isNotEmpty(this.objetoPagador.getNome())) {
				pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("nome", this.objetoPagador.getNome());
				pagadorRecebedor = this.objetoPagador;
				registraPagador = true;
			} else if (pagadorRecebedor == null) {
				pagadorRecebedor = this.objetoPagador;
			}

			if (this.objetoPagador.getSite() != null && this.objetoPagador.getSite().equals("")) {
				if (!this.objetoPagador.getSite().contains("http")) {
					this.objetoPagador.setSite("HTTP://" + this.objetoPagador.getSite().toLowerCase());
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

}