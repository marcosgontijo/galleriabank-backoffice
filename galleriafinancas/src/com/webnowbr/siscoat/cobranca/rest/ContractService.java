package com.webnowbr.siscoat.cobranca.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;

import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;

import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


@Path("/services")
public class ContractService {
	
	private ContratoCobranca objetoContratoCobranca;
	private ImovelCobranca objetoImovelCobranca;
	private PagadorRecebedor objetoPagador;
	
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (username.equals("webnowbr") && password.equals("!SisCoAt@2021*")) {
				try {
					JSONObject contratoAPP = new JSONObject(operacaoData);

					ContratoCobrancaMB contratoCobrancaMB = new ContratoCobrancaMB();
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
							this.objetoContratoCobranca.setCobrarComissaoCliente(contratoAPP.getString("cobrarComissaoCliente"));
							this.objetoContratoCobranca.setTipoCobrarComissaoCliente(contratoAPP.getString("tipoCobrarComissaoCliente"));
							this.objetoContratoCobranca.setBrutoLiquidoCobrarComissaoCliente(contratoAPP.getString("brutoLiquidoCobrarComissaoCliente"));
							
							String valorDesejado = contratoAPP.getString("quantoPrecisa").replace(".", "").replace(",", ".");						
							this.objetoContratoCobranca.setQuantoPrecisa(new BigDecimal(valorDesejado));
							
							this.objetoContratoCobranca.setPagadorDonoGarantia(contratoAPP.getBoolean("pagadorDonoGarantia"));
							this.objetoContratoCobranca.setDivida(contratoAPP.getString("divida"));
							
							/***
							 * VALORES DEFAULT
							 */
							this.objetoContratoCobranca.setInicioAnalise(false);
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
							JSONObject contratoAPPPagador = contratoAPP.getJSONObject("pagador");
							this.objetoPagador = new PagadorRecebedor();
							PagadorRecebedorDao pagadorDao = new PagadorRecebedorDao();
							
							if (contratoAPPPagador.has("id")) {
								this.objetoPagador = pagadorDao.findById(contratoAPPPagador.getLong("id"));
							} else {
								this.objetoPagador.setId(-1);
								
								if (contratoAPPPagador.getString("tipoPessoa").equals("CPF")) {
									this.objetoPagador.setCpf(contratoAPPPagador.getString("cpfCnpj"));
								} else {
									this.objetoPagador.setCnpj(contratoAPPPagador.getString("cpfCnpj"));
								}
								
								this.objetoPagador.setNome(contratoAPPPagador.getString("nome"));
								this.objetoPagador.setSexo(contratoAPPPagador.getString("sexo"));
								
								SimpleDateFormat dtNascimento = new SimpleDateFormat("dd-MM-yyyy");
								Date dtNascimentoDate = null;
								try {
									dtNascimentoDate = dtNascimento.parse(contratoAPPPagador.getString("dataNascimento"));
									this.objetoPagador.setDtNascimento(dtNascimentoDate);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
								this.objetoPagador.setNomeMae(contratoAPPPagador.getString("nomeMae"));
								this.objetoPagador.setEstadocivil(contratoAPPPagador.getString("estadoCivil"));
								this.objetoPagador.setRgDocumentosCheckList(contratoAPPPagador.getBoolean("rgDocumentosCheckList"));
								this.objetoPagador.setComprovanteEnderecoDocumentosCheckList(contratoAPPPagador.getBoolean("comprovanteEnderecoDocumentosCheckList"));
								this.objetoPagador.setCertidaoCasamentoNascimentoDocumentosCheckList(contratoAPPPagador.getBoolean("certidaoCasamentoNascimentoDocumentosCheckList"));
								this.objetoPagador.setFichaCadastralDocumentosCheckList(contratoAPPPagador.getBoolean("fichaCadastralDocumentosCheckList"));
								this.objetoPagador.setBancoDocumentosCheckList(contratoAPPPagador.getBoolean("bancoDocumentosCheckList"));
								this.objetoPagador.setTelefoneEmailDocumentosCheckList(contratoAPPPagador.getBoolean("telefoneEmailDocumentosCheckList"));
								this.objetoPagador.setComprovanteRendaCheckList(contratoAPPPagador.getBoolean("comprovanteRendaCheckList"));
								this.objetoPagador.setCombateFraudeCheckList(contratoAPPPagador.getBoolean("combateFraudeCheckList"));
								this.objetoPagador.setCargoOcupacaoCheckList(contratoAPPPagador.getBoolean("cargoOcupacaoCheckList"));
								this.objetoPagador.setTaxaCheckList(contratoAPPPagador.getBoolean("taxaCheckList"));
								//this.objetoPagador.setIdTipo(contratoAPPPagador.getBoolean("idTipo"));
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
								this.objetoImovelCobranca.setCep(contratoAPPImovel.getString("cep"));
								this.objetoImovelCobranca.setEndereco(contratoAPPImovel.getString("endereco") + ", " + contratoAPPImovel.getString("numero"));
								this.objetoImovelCobranca.setComplemento(contratoAPPImovel.getString("complemento"));
								this.objetoImovelCobranca.setCidade(contratoAPPImovel.getString("cidade"));
								this.objetoImovelCobranca.setBairro(contratoAPPImovel.getString("bairro"));
								this.objetoImovelCobranca.setEstado(contratoAPPImovel.getString("estado"));
								this.objetoImovelCobranca.setNumeroCartorio(contratoAPPImovel.getString("numeroCartorio"));
								this.objetoImovelCobranca.setCartorio(contratoAPPImovel.getString("cartorioRegistro"));
								this.objetoImovelCobranca.setCartorioEstado(contratoAPPImovel.getString("cartorioEstado"));
								this.objetoImovelCobranca.setCartorioMunicipio(contratoAPPImovel.getString("cartorioMunicipio"));
								this.objetoImovelCobranca.setNumeroMatricula(contratoAPPImovel.getString("numeroMatricula"));
								this.objetoImovelCobranca.setTipo(contratoAPPImovel.getString("tipoImovel"));
								this.objetoImovelCobranca.setComprovanteMatriculaCheckList(contratoAPPImovel.getBoolean("comprovanteMatriculaCheckList"));
								this.objetoImovelCobranca.setComprovanteFotosImovelCheckList(contratoAPPImovel.getBoolean("comprovanteFotosImovelCheckList"));
								this.objetoImovelCobranca.setComprovanteIptuImovelCheckList(contratoAPPImovel.getBoolean("comprovanteIptuImovelCheckList"));
								
								//String valorEstimado = contratoAPPImovel.getString("valoEstimado").replace(".", "").replace(",", ".");						
								this.objetoImovelCobranca.setValoEstimado(new BigDecimal(contratoAPPImovel.getDouble("valoEstimado")));								
							}
							
							this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);
	
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
						String message = "{\"retorno\": \"[Galleria Bank] Código do Responsável não encontrato!!!\"}";
						
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
		
	public void clearCriacaoContrato() {
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(new Date());
		this.objetoContratoCobranca.setDataCadastro(new Date());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(new Date());
		this.objetoContratoCobranca.setGeraParcelaFinal(false);

		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagador = new PagadorRecebedor();

		this.objetoContratoCobranca.setStatus("Pendente");
		this.objetoContratoCobranca.setStatusContrato("Pendente");
		this.objetoContratoCobranca.setAgAssinatura(true);
		this.objetoContratoCobranca.setAgRegistro(true);
		this.objetoContratoCobranca.setAnaliseReprovada(false);
		this.objetoContratoCobranca.setInicioAnalise(false);
		this.objetoContratoCobranca.setStatusLead("Completo");
		
		ParametrosDao pDao = new ParametrosDao();
		this.objetoContratoCobranca
				.setTxJuros(pDao.findByFilter("nome", "COBRANCA_REC_TX_JUROS").get(0).getValorBigDecimal());
		this.objetoContratoCobranca
				.setTxMulta(pDao.findByFilter("nome", "COBRANCA_REC_MULTA").get(0).getValorBigDecimal());
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