package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.webnowbr.siscoat.cobranca.db.model.AnaliseComite;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;



public class PowerBiDao extends HibernateDao <PowerBiVO,Long> {
	
	private static final String CONTRATOS_INICIO_ANALISE = " SELECT ID, inicioanaliseusuario "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and date_trunc('day', inicioanalisedata) = date_trunc('day',  ? ::TIMESTAMP) "
			+ " order by inicioanaliseusuario asc ";
	
	private static final String CONTRATOS_ANALISE = " SELECT ID, CadastroAprovadoUsuario "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and CadastroAprovadovalor = 'Aprovado' "
			+ " AND (PagtoLaudoConfirmadaData is null or date_trunc('day', CadastroAprovadoData) < date_trunc('day', PagtoLaudoConfirmadaData)) "
			+ "	and date_trunc('day', CadastroAprovadoData) = date_trunc('day',  ? ::TIMESTAMP) "
			+ " order by CadastroAprovadoUsuario asc ";
	 
	private static final String CONTRATOS_ASSINADOS = " SELECT  ID, AgAssinaturaUsuario "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and date_trunc('day', AgAssinaturaData) = date_trunc('day',  ? ::TIMESTAMP) "
			+ " order by AgAssinaturaUsuario asc ";
	
	private static final String CONTRATOS_REGISTRADOS = " SELECT  ID, AgRegistroUsuario "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and date_trunc('day', AgRegistroData) = date_trunc('day',  ? ::TIMESTAMP) "
			+ " order by AgRegistroUsuario asc " ;
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> listaContratos(Date data, String tipoPesquisa) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
				
					if(CommonsUtil.mesmoValor(tipoPesquisa, "inicioAnalise")) {
						ps = connection.prepareStatement(CONTRATOS_INICIO_ANALISE);
					} else if(CommonsUtil.mesmoValor(tipoPesquisa, "analiseAprovada")) {
						ps = connection.prepareStatement(CONTRATOS_ANALISE);
					} else if(CommonsUtil.mesmoValor(tipoPesquisa, "assinatura")) {
						ps = connection.prepareStatement(CONTRATOS_ASSINADOS);
					} else if(CommonsUtil.mesmoValor(tipoPesquisa, "registro")) {
						ps = connection.prepareStatement(CONTRATOS_REGISTRADOS);
					}
					
					java.sql.Date dtRelSQL = new java.sql.Date(data.getTime());
					ps.setDate(1, dtRelSQL);
					
					rs = ps.executeQuery();
					
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					while (rs.next()) {
						objects.add(contratoCobrancaDao.findById(rs.getLong(1)));				
					}
					
				} finally {
					closeResources(connection, ps);
				}
				return objects;
			}
		});
	} 
	
	private static final String POWERBI_INICIO_ANALISE = " SELECT inicioanaliseusuario, "
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS "
			+ " from (SELECT inicioanaliseusuario, "
			+ "	COUNT(C.ID) CONTRATOSCADASTRADOS "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ " WHERE C.STATUSLEAD = 'Completo' "
			+ "	AND date_trunc('day', inicioanalisedata) = date_trunc('day',  ? ::TIMESTAMP) "
			+ "	GROUP BY inicioanaliseusuario) TOTAIS "
			+ " GROUP BY inicioanaliseusuario ";
	
	private static final String POWERBI_ANALISE = " SELECT CadastroAprovadoUsuario, "
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS "
			+ " from (SELECT CadastroAprovadoUsuario, "
			+ "	COUNT(C.ID) CONTRATOSCADASTRADOS "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and CadastroAprovadovalor = 'Aprovado' "
			+ "	AND date_trunc('day', CadastroAprovadoData) = date_trunc('day',  ? ::TIMESTAMP) "
			+ " AND (PagtoLaudoConfirmadaData is null or date_trunc('day', CadastroAprovadoData) < date_trunc('day', PagtoLaudoConfirmadaData)) "
			+ "	GROUP BY CadastroAprovadoUsuario) TOTAIS "
			+ " GROUP BY CadastroAprovadoUsuario ";
	
	private static final String POWERBI_ASSINADOS = " SELECT AgAssinaturaUsuario, "
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS "
			+ " from (SELECT AgAssinaturaUsuario, "
			+ "	COUNT(C.ID) CONTRATOSCADASTRADOS "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ " WHERE C.STATUSLEAD = 'Completo' "
			+ "	AND date_trunc('day', AgAssinaturaData) = date_trunc('day',  ? ::TIMESTAMP) "
			+ "	GROUP BY AgAssinaturaUsuario) TOTAIS "
			+ " GROUP BY AgAssinaturaUsuario ";
	
	private static final String POWERBI_REGISTRADOS = " SELECT AgRegistroUsuario, "
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS "
			+ " from (SELECT AgRegistroUsuario, "
			+ "	COUNT(C.ID) CONTRATOSCADASTRADOS "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	AND date_trunc('day', AgRegistroData) = date_trunc('day',  ? ::TIMESTAMP) "
			+ "	GROUP BY AgRegistroUsuario) TOTAIS "
			+ " GROUP BY AgRegistroUsuario ";
	
	@SuppressWarnings("unchecked")
	public List<PowerBiDetalhes> listaPowerBiDetalhes(Date data, String tipoPesquisa) {
		return (List<PowerBiDetalhes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				List<PowerBiDetalhes> objects = new ArrayList<PowerBiDetalhes>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					if(CommonsUtil.mesmoValor(tipoPesquisa, "inicioAnalise")) {
						ps = connection.prepareStatement(POWERBI_INICIO_ANALISE);
					} else if(CommonsUtil.mesmoValor(tipoPesquisa, "analiseAprovada")) {
						ps = connection.prepareStatement(POWERBI_ANALISE);
					} else if(CommonsUtil.mesmoValor(tipoPesquisa, "assinatura")) {
						ps = connection.prepareStatement(POWERBI_ASSINADOS);
					} else if(CommonsUtil.mesmoValor(tipoPesquisa, "registro")) {
						ps = connection.prepareStatement(POWERBI_REGISTRADOS);
					}
					
					java.sql.Date dtRelSQL = new java.sql.Date(data.getTime());
					ps.setDate(1, dtRelSQL);
					
					rs = ps.executeQuery();
					
					PowerBiDao powerBiDao = new PowerBiDao();
					
					List<ContratoCobranca> todosContratos = powerBiDao.listaContratos(data,  tipoPesquisa);
					
					while (rs.next()) {
						PowerBiDetalhes powBiDetalhes = new PowerBiDetalhes();
						List<ContratoCobranca> contratosPowerBi = new ArrayList<ContratoCobranca>();
						
						for(ContratoCobranca contrato : todosContratos) {
							
							if(CommonsUtil.mesmoValor(tipoPesquisa, "inicioAnalise")) {
								if(CommonsUtil.mesmoValor(contrato.getInicioAnaliseUsuario(), rs.getString("inicioanaliseusuario"))){
									contratosPowerBi.add(contrato);
									powBiDetalhes.setNome(rs.getString("InicioAnaliseUsuario"));
								}
							} else if(CommonsUtil.mesmoValor(tipoPesquisa, "analiseAprovada")) {
								if(CommonsUtil.mesmoValor(contrato.getCadastroAprovadoUsuario(), rs.getString("CadastroAprovadoUsuario"))){
									contratosPowerBi.add(contrato);
									powBiDetalhes.setNome(rs.getString("CadastroAprovadoUsuario"));
								}
							} else if(CommonsUtil.mesmoValor(tipoPesquisa, "assinatura")) {
								if(CommonsUtil.mesmoValor(contrato.getAgAssinaturaUsuario(), rs.getString("AgAssinaturaUsuario"))){
									contratosPowerBi.add(contrato);
									powBiDetalhes.setNome(rs.getString("AgAssinaturaUsuario"));
								}
							} else if(CommonsUtil.mesmoValor(tipoPesquisa, "registro")) {
								if(CommonsUtil.mesmoValor(contrato.getAgRegistroUsuario(), rs.getString("AgRegistroUsuario"))){
									powBiDetalhes.setNome(rs.getString("agRegistroUsuario"));
									contratosPowerBi.add(contrato);
								}
							}
							
								
						}
						
						powBiDetalhes.setQtdContratos(rs.getInt("CONTRATOSCADASTRADOS"));
						powBiDetalhes.setContratos(contratosPowerBi);
						objects.add(powBiDetalhes);
					}
					
					
				} finally {
					closeResources(connection, ps);
				}
				return objects;
			}
		});
	} 
	
	private static final String POWER_BI= " select datacontrato, status, statusLead, inicioanaliseusuario, inicioanalisedata, leadcompletodata, cadastroAprovadoData, PagtoLaudoConfirmadaData, cadastroAprovadoValor, agassinaturadata, aprovadodata, quantoprecisa, valorccb, contratoLead "
			+ " from cobranca.contratocobranca coco  ";
	
	@SuppressWarnings("unchecked")
	public PowerBiVO powerBiConsulta(Date data) {
		return (PowerBiVO) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PowerBiVO object = new PowerBiVO();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				int mes = data.getMonth(); 
				int ano = data.getYear(); 
				int dia = data.getDate(); 
				
				try {
					connection = getConnection();
					
					ps = connection.prepareStatement(POWER_BI);

					rs = ps.executeQuery();
					
					PowerBiVO powerBi = new PowerBiVO();
					
					int qtdCadastradas = 0;
					int qtdNovosLeads = 0;
					int qtdLeadsCompletos = 0;
					int qtdAnalisadas = 0;
					int qtdAssinadas = 0;
					int qtdRegistradas = 0;
					int qtdInicioAnalise = 0;
					
					List<PowerBiDetalhes> analises = new ArrayList<PowerBiDetalhes>();
					List<PowerBiDetalhes> preAprovacoes = new ArrayList<PowerBiDetalhes>();
					List<PowerBiDetalhes> assinaturas = new ArrayList<PowerBiDetalhes>();
					List<PowerBiDetalhes> registros = new ArrayList<PowerBiDetalhes>();
					
					BigDecimal vlrCadastradas = BigDecimal.ZERO;
					
					BigDecimal vlrNovosLeads = BigDecimal.ZERO;
					BigDecimal vlrLeadsCompletos = BigDecimal.ZERO;
					
					BigDecimal vlrInicioAnalise = BigDecimal.ZERO;
					BigDecimal vlrAnalisadas = BigDecimal.ZERO;
					BigDecimal vlrAssinadas = BigDecimal.ZERO;
					BigDecimal vlrRegistradas = BigDecimal.ZERO;
					
					while (rs.next()) {
						
						if (!CommonsUtil.semValor((rs.getDate("datacontrato")))) {
							if (!rs.getBoolean("contratoLead")) {
								if (!CommonsUtil.mesmoValor(rs.getString("status"), "Aprovado")) {
									if (CommonsUtil.mesmoValor(rs.getDate("datacontrato").getMonth(), mes)
											&& CommonsUtil.mesmoValor(rs.getDate("datacontrato").getYear(), ano)
											&& CommonsUtil.mesmoValor(rs.getDate("datacontrato").getDate(), dia)) {
										qtdCadastradas++;
										if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
											vlrCadastradas = vlrCadastradas.add(rs.getBigDecimal("quantoprecisa"));
										}
									}
								}
							} else {
								if (CommonsUtil.mesmoValor(rs.getDate("datacontrato").getMonth(), mes)
										&& CommonsUtil.mesmoValor(rs.getDate("datacontrato").getYear(), ano)
										&& CommonsUtil.mesmoValor(rs.getDate("datacontrato").getDate(), dia)) {
									qtdNovosLeads++;
									if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
										vlrNovosLeads = vlrNovosLeads.add(rs.getBigDecimal("quantoprecisa"));
									}
								}
							}
						}
						
						if (!CommonsUtil.semValor((rs.getDate("leadcompletodata")))) {
							if (!CommonsUtil.mesmoValor(rs.getString("statuslead"), "Aprovado")) {
								if (CommonsUtil.mesmoValor(rs.getDate("leadcompletodata").getMonth(), mes)
										&& CommonsUtil.mesmoValor(rs.getDate("leadcompletodata").getYear(), ano)
										&& CommonsUtil.mesmoValor(rs.getDate("leadcompletodata").getDate(), dia)) {
									qtdLeadsCompletos++;
									if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
										vlrLeadsCompletos = vlrLeadsCompletos.add(rs.getBigDecimal("quantoprecisa"));
									}
								}
							}
						}
						
						if (!CommonsUtil.semValor((rs.getDate("inicioanalisedata")))) {
							if (CommonsUtil.mesmoValor(rs.getDate("inicioanalisedata").getMonth(), mes)
									&& CommonsUtil.mesmoValor(rs.getDate("inicioanalisedata").getYear(), ano)
									&& CommonsUtil.mesmoValor(rs.getDate("inicioanalisedata").getDate(), dia)) {
								qtdInicioAnalise++;
								if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
									vlrInicioAnalise = vlrInicioAnalise.add(rs.getBigDecimal("quantoprecisa"));
								}
							}
						}
						
						if(!CommonsUtil.semValor(rs.getDate("PagtoLaudoConfirmadaData"))){
							if (!CommonsUtil.semValor((rs.getDate("cadastroAprovadoData")))) {
								if (!(rs.getDate("cadastroAprovadoData").getMonth() > rs.getDate("PagtoLaudoConfirmadaData").getMonth()) &&
										(rs.getDate("cadastroAprovadoData").getYear() > rs.getDate("PagtoLaudoConfirmadaData").getYear()) &&
										(rs.getDate("cadastroAprovadoData").getDate() > rs.getDate("PagtoLaudoConfirmadaData").getDate())) {
									if (CommonsUtil.mesmoValor(rs.getString("cadastroAprovadoValor"), "Aprovado")) {
										if (CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getMonth(), mes)
												&& CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getYear(), ano)
												&& CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getDate(), dia)) {
											qtdAnalisadas++;
											if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
												vlrAnalisadas = vlrAnalisadas.add(rs.getBigDecimal("quantoprecisa"));
											}
										}
									}
								}
							}
						} else {
							if (!CommonsUtil.semValor((rs.getDate("cadastroAprovadoData")))) {
								if (CommonsUtil.mesmoValor(rs.getString("cadastroAprovadoValor"), "Aprovado")) {
									if (CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getMonth(), mes)
											&& CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getYear(), ano)
											&& CommonsUtil.mesmoValor(rs.getDate("cadastroAprovadoData").getDate(), dia)) {
										qtdAnalisadas++;
										if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
											vlrAnalisadas = vlrAnalisadas.add(rs.getBigDecimal("quantoprecisa"));
										}
									}
								}
							}
						}

						if (!CommonsUtil.semValor((rs.getDate("agassinaturadata")))) {
							if (CommonsUtil.mesmoValor(rs.getDate("agassinaturadata").getMonth(), mes)
									&& CommonsUtil.mesmoValor(rs.getDate("agassinaturadata").getYear(), ano)
									&& CommonsUtil.mesmoValor(rs.getDate("agassinaturadata").getDate(), dia)) {
								qtdAssinadas++;
								if (!CommonsUtil.semValor(rs.getBigDecimal("quantoprecisa"))) {
									vlrAssinadas = vlrAssinadas.add(rs.getBigDecimal("quantoprecisa"));
								}
							}
						}

						if (!CommonsUtil.semValor((rs.getDate("aprovadodata")))) {
							if (CommonsUtil.mesmoValor(rs.getDate("aprovadodata").getMonth(), mes)
									&& CommonsUtil.mesmoValor(rs.getDate("aprovadodata").getYear(), ano)
									&& CommonsUtil.mesmoValor(rs.getDate("aprovadodata").getDate(), dia)) {
								qtdRegistradas++;
								if (!CommonsUtil.semValor(rs.getBigDecimal("valorccb"))) {
									vlrRegistradas = vlrRegistradas.add(rs.getBigDecimal("valorccb"));
								}
							}
						}		
					}
					
					powerBi.setNumeroOperacoesAssinadas(BigInteger.valueOf(qtdAssinadas));
					powerBi.setNumeroNovosLeadsCadastrados(BigInteger.valueOf(qtdNovosLeads));
					powerBi.setNumeroLeadsCompletos(BigInteger.valueOf(qtdLeadsCompletos));
					powerBi.setNumeroOperacoesInicioAnalise(BigInteger.valueOf(qtdInicioAnalise));
					powerBi.setNumeroOperacoesAnalisadas(BigInteger.valueOf(qtdAnalisadas));
					powerBi.setNumeroOperacoesCadastradas(BigInteger.valueOf(qtdCadastradas));
					powerBi.setNumeroOperacoesRegistradas(BigInteger.valueOf(qtdRegistradas));
					
					powerBi.setValorNovosLeadsCadastrados(vlrNovosLeads);
					powerBi.setValorLeadsCompletos(vlrLeadsCompletos);
					powerBi.setValorOperacoesAssinadas(vlrAssinadas);
					powerBi.setValorOperacoesInicioAnalise(vlrInicioAnalise);
					powerBi.setValorOperacoesCadastradas(vlrCadastradas);
					powerBi.setValorOperacoesRegistradas(vlrRegistradas);
					powerBi.setValorOperacoesAnalisadas(vlrAnalisadas);
					
					powerBi.setDataConsulta(data);
					
					object = powerBi;
						
				} finally {
					closeResources(connection, ps);
				}
				return object;
			}
		});
	}  
	
	private static final String PARCELAS_FIDC = " select cc.id from cobranca.contratocobranca cc "
			+ "	where empresa = 'FIDC GALLERIA' and status = 'Aprovado' ";
	
	private static final String PARCELAS_SECURITIZADORA = " select cc.id from cobranca.contratocobranca cc "
			+ "	where empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.'and status = 'Aprovado' ";
	
	@SuppressWarnings("unchecked")
	public DadosContratosVO dadosContratosConsulta(final Date data, final String empresa) {
		return (DadosContratosVO) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				DadosContratosVO object = new DadosContratosVO();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				TimeZone zone = TimeZone.getDefault();
				Locale locale = new Locale("pt", "BR");
				Calendar dataHoje = Calendar.getInstance(zone, locale);
				Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
				dataHoje.set(Calendar.HOUR_OF_DAY, 0);
				dataHoje.set(Calendar.MINUTE, 0);
				dataHoje.set(Calendar.SECOND, 0);
				dataHoje.set(Calendar.MILLISECOND, 0);
				Date dataAtual = dataHoje.getTime();
				
				int mes = data.getMonth(); 
				int ano = data.getYear(); 
				int dia = data.getDate();
				
				try {
					connection = getConnection();
					
					if(CommonsUtil.mesmoValor(empresa, "Securitizadora")) {
						ps = connection.prepareStatement(PARCELAS_SECURITIZADORA);
					} else if (CommonsUtil.mesmoValor(empresa, "Fidc")) {
						ps = connection.prepareStatement(PARCELAS_FIDC);
					}
				
					rs = ps.executeQuery();
					
					DadosContratosVO dadosSecuritizadora = new DadosContratosVO();
					
					int numeroContratosAtraso = 0;
					int numeroContratosPagas = 0;
					int numeroContratosQuitados = 0;
					
					BigDecimal vlrParcelasAtraso = BigDecimal.ZERO;
					BigDecimal vlrParcelasPagas = BigDecimal.ZERO;
					BigDecimal vlrParcelasQuitadas = BigDecimal.ZERO;
					
					BigDecimal vlrContratosQuitados = BigDecimal.ZERO;	
					BigDecimal vlrContratosPagos = BigDecimal.ZERO;
					BigDecimal vlrContratosAtraso = BigDecimal.ZERO;
					
					int prazoContrato = 1;
					BigDecimal valorPareclaPaga = BigDecimal.ZERO;
					
					String numeroContratoAtrasoAntigo = "asdasdasd";
					String numeroContratoPagoAntigo = "asdasdasd";
					String numeroContratoQuitadoAntigo = "asdasdasd";
					
					Collection<ContratoCobranca> contratosAtraso = new ArrayList<ContratoCobranca>();
					Collection<ContratoCobranca> contratosPagos = new ArrayList<ContratoCobranca>();
					Collection<ContratoCobranca> contratosQuitados = new ArrayList<ContratoCobranca>();
					
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					while (rs.next()) {
						ContratoCobranca contrato = contratoCobrancaDao.findById(rs.getLong("id"));
						valorPareclaPaga = BigDecimal.ZERO;
						prazoContrato = 1;
						
						if (!CommonsUtil.semValor(contrato.getListContratoCobrancaDetalhes())) {

							for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
								BigDecimal valorParcela = BigDecimal.ZERO;
								if (!CommonsUtil.semValor(ccd.getVlrParcela())) {
									valorParcela = ccd.getVlrParcela();
								} else {
									if (!CommonsUtil.semValor(ccd.getSeguroDFI())) {
										valorParcela = valorParcela.add(ccd.getSeguroDFI());
									}
									if (!CommonsUtil.semValor(ccd.getSeguroMIP())) {
										valorParcela = valorParcela.add(ccd.getSeguroMIP());
									}
									if (!CommonsUtil.semValor(ccd.getVlrAmortizacaoParcela())) {
										valorParcela = valorParcela.add(ccd.getVlrAmortizacaoParcela());
									}
									if (!CommonsUtil.semValor(ccd.getVlrJurosParcela())) {
										valorParcela = valorParcela.add(ccd.getVlrJurosParcela());
									}
								}

								BigDecimal somaBaixas = BigDecimal.ZERO;
								if (ccd.isAmortizacao()) {
									somaBaixas = ccd.getVlrParcela();
								} else {
									for (ContratoCobrancaDetalhesParcial cBaixas : ccd
											.getListContratoCobrancaDetalhesParcial()) {
										ccd.setDataUltimoPagamento(cBaixas.getDataPagamento());
										if(!CommonsUtil.semValor(cBaixas.getVlrRecebido())) {
											somaBaixas = somaBaixas.add(cBaixas.getVlrRecebido());
										}										
									}
								}

								ccd.setValorTotalPagamento(somaBaixas);

								dataVencimentoParcela.setTime(ccd.getDataVencimento());
								dataVencimentoParcela.set(Calendar.HOUR_OF_DAY, 0);
								dataVencimentoParcela.set(Calendar.MINUTE, 0);
								dataVencimentoParcela.set(Calendar.SECOND, 0);
								dataVencimentoParcela.set(Calendar.MILLISECOND, 0);

								if (dataVencimentoParcela.getTime().before(data) && !ccd.isParcelaPaga()) {
									ccd.setParcelaVencida(true);
									vlrParcelasAtraso = vlrParcelasAtraso.add(valorParcela);
									if (!CommonsUtil.semValor(contrato.getValorCCB())) {
										vlrContratosAtraso = vlrContratosAtraso.add(contrato.getValorCCB());
									}

									if (!CommonsUtil.mesmoValor(numeroContratoAtrasoAntigo,
											contrato.getNumeroContrato())) {
										numeroContratoAtrasoAntigo = contrato.getNumeroContrato();
										numeroContratosAtraso++;
										contratosAtraso.add(contrato);
									}
								} else if (ccd.isParcelaPaga()) {
									if (!CommonsUtil.semValor(ccd.getDataUltimoPagamento())) {
										if (ccd.getDataUltimoPagamento().after(data)) {
											vlrParcelasAtraso = vlrParcelasAtraso.add(valorParcela);

											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosAtraso = vlrContratosAtraso.add(contrato.getValorCCB());
											}

											if (!CommonsUtil.mesmoValor(numeroContratoAtrasoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoAtrasoAntigo = contrato.getNumeroContrato();
												numeroContratosAtraso++;
												contratosAtraso.add(contrato);
											}
										} else if (CommonsUtil.mesmoValor(ccd.getDataUltimoPagamento().getMonth(), mes)
												&& CommonsUtil.mesmoValor(ccd.getDataUltimoPagamento().getYear(), ano)
												&& CommonsUtil.mesmoValor(ccd.getDataUltimoPagamento().getDate(),
														dia)) {
											valorPareclaPaga = valorParcela;
											if (!CommonsUtil.mesmoValor(numeroContratoPagoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoPagoAntigo = contrato.getNumeroContrato();
												numeroContratosPagas++;
												contratosPagos.add(contrato);
											}
											vlrParcelasPagas = vlrParcelasPagas.add(valorParcela);
											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosPagos = vlrContratosPagos.add(contrato.getValorCCB());
											}
											if (!CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "Amortização")) {
												if(CommonsUtil.mesmoValor(contrato.getListContratoCobrancaDetalhes().get(0).getNumeroParcela(), "0")){
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size() - 1
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												} else {
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size()
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												}
											}
										}
									} else if (!CommonsUtil.semValor(ccd.getDataPagamento())) {
										if (ccd.getDataPagamento().after(data)) {
											vlrParcelasAtraso = vlrParcelasAtraso.add(valorParcela);

											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosAtraso = vlrContratosAtraso.add(contrato.getValorCCB());
											}

											if (!CommonsUtil.mesmoValor(numeroContratoAtrasoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoAtrasoAntigo = contrato.getNumeroContrato();
												numeroContratosAtraso++;
												contratosAtraso.add(contrato);
											}
										} else if (CommonsUtil.mesmoValor(ccd.getDataPagamento().getMonth(), mes)
												&& CommonsUtil.mesmoValor(ccd.getDataPagamento().getYear(), ano)
												&& CommonsUtil.mesmoValor(ccd.getDataPagamento().getDate(), dia)) {
											valorPareclaPaga = valorParcela;
											if (!CommonsUtil.mesmoValor(numeroContratoPagoAntigo,
													contrato.getNumeroContrato())) {
												numeroContratoPagoAntigo = contrato.getNumeroContrato();
												numeroContratosPagas++;
												contratosPagos.add(contrato);
											}
											vlrParcelasPagas = vlrParcelasPagas.add(valorParcela);
											if (!CommonsUtil.semValor(contrato.getValorCCB())) {
												vlrContratosPagos = vlrContratosPagos.add(contrato.getValorCCB());
											}
											if (!CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "Amortização")) {
												if(CommonsUtil.mesmoValor(contrato.getListContratoCobrancaDetalhes().get(0).getNumeroParcela(), "0")){
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size() - 1
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												} else {
													prazoContrato = contrato.getListContratoCobrancaDetalhes().size()
															- CommonsUtil.intValue(ccd.getNumeroParcela());
												}
											}
										}
									}
								}
							}

							if (CommonsUtil.mesmoValor(prazoContrato, 0) && !CommonsUtil
									.mesmoValor(numeroContratoQuitadoAntigo, contrato.getNumeroContrato())) {
								numeroContratoQuitadoAntigo = contrato.getNumeroContrato();
								numeroContratosQuitados++;
								contratosQuitados.add(contrato);
								if (!CommonsUtil.semValor(contrato.getValorCCB())) {
									vlrContratosQuitados = vlrContratosQuitados.add(contrato.getValorCCB());
								}
								vlrParcelasQuitadas = vlrParcelasQuitadas.add(valorPareclaPaga);
							}
						}
					}
					
					dadosSecuritizadora.setNumeroContratosAtraso(numeroContratosAtraso);
					dadosSecuritizadora.setNumeroContratosPagas(numeroContratosPagas);
					dadosSecuritizadora.setNumeroContratosQuitados(numeroContratosQuitados);
					
					dadosSecuritizadora.setVlrContratosAtraso(vlrContratosAtraso);
					dadosSecuritizadora.setVlrContratosPagos(vlrContratosPagos);
					dadosSecuritizadora.setVlrContratosQuitados(vlrContratosQuitados);
					
					dadosSecuritizadora.setVlrParcelasAtraso(vlrParcelasAtraso);
					dadosSecuritizadora.setVlrParcelasPagas(vlrParcelasPagas);
					dadosSecuritizadora.setVlrParcelasQuitadas(vlrParcelasQuitadas);
					
					dadosSecuritizadora.setContratosAtraso(contratosAtraso);
					dadosSecuritizadora.setContratosPagos(contratosPagos);
					dadosSecuritizadora.setContratosQuitados(contratosQuitados);
					
					object = dadosSecuritizadora;
						
				} finally {
					closeResources(connection, ps);
				}
				return object;
			}
		});
	}  
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final String CONTRATOS_POWERBI_NEW = " select c.id, c.numeroContrato, c.quantoPrecisa, r.nome "
			+ "	from cobranca.contratocobranca c "
			+ " left join cobranca.pagadorRecebedor r on r.id = c.pagador";
	
	private static final String POWERBI_DETALHES_NEW = " analista, count(c.id) qtd,	sum(c.quantoPrecisa) valor "
			+ "	from cobranca.contratocobranca c "
			+ " where numeroContrato in ( ";

	private static final String POWERBI_DETALHES_NEW2 = " analista, c.id, c.numerocontrato, c.quantoPrecisa, r.nome "
			+ "	from cobranca.contratocobranca c "
			+ " left join cobranca.pagadorRecebedor r on r.id = c.pagador"
			+ " where numeroContrato in ( ";
	
	@SuppressWarnings("unchecked")
	public PowerBiNew pbNew(final Date dataInicio, final Date dataFim, final String tipo) {
		return (PowerBiNew) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PowerBiNew object = new PowerBiNew();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query = CONTRATOS_POWERBI_NEW;
				String query2 = POWERBI_DETALHES_NEW2;
				String param1 = null;

				try {
					connection = getConnection();				
					if(CommonsUtil.mesmoValor(tipo, "Cadastradas")) {
						object.setTipo("Cadastradas");
						query = query + " where dataCadastro >= ? ::timestamp " 
						+ " and dataCadastro <= ? ::timestamp ";					
					} else if(CommonsUtil.mesmoValor(tipo, "Aprovadas")) {
						object.setTipo("Aprovadas");
						query = query + " where cadastroAprovadoData >= ? ::timestamp " 
						+ " and cadastroAprovadoData <= ? ::timestamp"
						+ " and cadastroAprovadoValor = 'Aprovado' ";	
						param1 = "select c.cadastroAprovadoUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Reprovadas")) {
						object.setTipo("Reprovadas");
						query = query + " where analiseReprovadaData >= ? ::timestamp " 
						+ " and analiseReprovadaData <= ? ::timestamp ";
						param1 = "select c.analiseReprovadaUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Com pedido de laudo")) {
						object.setTipo("Com pedido de laudo");
						query = query + " where pedidoLaudoData >= ? ::timestamp " 
						+ " and pedidoLaudoData <= ? ::timestamp ";	
						param1 = "select c.pedidoLaudoUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Com pedido de paju")) {
						object.setTipo("Com pedido de paju");
						query = query + " where pagtoLaudoConfirmadaData >= ? ::timestamp " 
						+ " and pagtoLaudoConfirmadaData <= ? ::timestamp ";	
						param1 = "select c.pagtoLaudoConfirmadaUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Enviadas para Com. Jurídico")) {
						object.setTipo("Enviadas para Com. Jurídico");
						query = query + " where analiseComercialData >= ? ::timestamp " 
						+ " and analiseComercialData <= ? ::timestamp ";	
						param1 = "select c.analiseComercialUsuario";
					}  else if(CommonsUtil.mesmoValor(tipo, "Comentadas pelo Jurídico")) {
						object.setTipo("Comentadas pelo Jurídico");
						query = query + " where comentarioJuridicoEsteiraData >= ? ::timestamp " 
						+ " and comentarioJuridicoEsteiraData <= ? ::timestamp ";		
						param1 = "select c.comentarioJuridicoEsteiraUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Enviadas para Validação Doc.")) {
						object.setTipo("Enviadas para Validação Doc.");
						query = query + " where preAprovadoComiteData >= ? ::timestamp " 
						+ " and preAprovadoComiteData <= ? ::timestamp ";
						param1 = "select c.preAprovadoComiteUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Enviadas para Comitê")) {
						object.setTipo("Enviadas para Comitê");
						query = query + " where documentosComiteData >= ? ::timestamp " 
						+ " and documentosComiteData <= ? ::timestamp ";	
						param1 = "select c.documentosComiteUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Enviadas para Ag. Doc")) {
						object.setTipo("Enviadas para Ag. Doc");
						query = query + " where aprovadoComiteData >= ? ::timestamp " 
						+ " and aprovadoComiteData <= ? ::timestamp ";		
						param1 = "select c.aprovadoComiteUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "Enviadas para Ag. CCB")) {
						object.setTipo("Enviadas para Ag. CCB");
						query = query + " where documentosCompletosData >= ? ::timestamp " 
						+ " and documentosCompletosData <= ? ::timestamp ";	
						param1 = "select c.documentosCompletosUsuario";
					} else if(CommonsUtil.mesmoValor(tipo, "com CCI Emitida")) {
						object.setTipo("com CCI Emitida");
						query = query + " where ccbProntaData >= ? ::timestamp " 
						+ " and ccbProntaData <= ? ::timestamp ";			
						param1 = "select c.ccbProntaUsuario";
					}  else if(CommonsUtil.mesmoValor(tipo, "com CCI Assinada")) {
						object.setTipo("com CCI Assinada");
						query = query + " where agAssinaturaData >= ? ::timestamp " 
						+ " and agAssinaturaData <= ? ::timestamp ";
						param1 = "select c.agAssinaturaUsuario";
					}
					
					query = query + " order by id desc ";
					ps = connection.prepareStatement(query);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());				
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					
					rs = ps.executeQuery();
					List<String> numerosContratos = new ArrayList<String>(0);
					while(rs.next()) {
						ContratoCobranca contrato = new ContratoCobranca();
						contrato.setId(rs.getLong("id"));
						contrato.setNumeroContrato(rs.getString("numeroContrato"));
						numerosContratos.add(contrato.getNumeroContrato());
						contrato.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
						contrato.setNomePagador(rs.getString("nome"));
						object.getContratos().add(contrato);
						if(!CommonsUtil.semValor(contrato.getQuantoPrecisa())) {
							object.setValorOperacoes(object.getValorOperacoes().add(contrato.getQuantoPrecisa()));	
						}						
					}

					object.setNumeroOperacoes(object.getContratos().size());
					
					rs.close();				
					if (!CommonsUtil.semValor(param1) && object.getNumeroOperacoes() > 0) {
						query2 = param1 + query2;
						boolean comeco = true;
						for(String s : numerosContratos) {
							if(comeco) {
								query2 = query2 + "'" + s + "'";
								comeco = false;
							} else {
								query2 = query2 + ",'" + s + "'";
							}						
						}
						query2 = query2 + " ) order by analista";
						
						ps = connection.prepareStatement(query2);	
						rs = ps.executeQuery();
						String analistaAterior = "inicio";
						PowerBiDetalhes pbDetalhes = new PowerBiDetalhes();
						//ContratoCobranca contratoDetalhes = new ContratoCobranca();
						while (rs.next()) {
							ContratoCobranca contratoDetalhes = new ContratoCobranca();
							if(CommonsUtil.mesmoValor(rs.getString("analista"), analistaAterior)
								|| CommonsUtil.mesmoValor(analistaAterior, "inicio")) {

							} else {							
								pbDetalhes.setNome(analistaAterior);
								pbDetalhes.setQtdContratos(pbDetalhes.getContratos().size());
								object.getDetalhes().add(pbDetalhes);							
								pbDetalhes = new PowerBiDetalhes();																					
							}
							
							contratoDetalhes.setId(rs.getLong("id"));
							contratoDetalhes.setNumeroContrato(rs.getString("numeroContrato"));						
							contratoDetalhes.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
							contratoDetalhes.setNomePagador(rs.getString("nome"));	
							pbDetalhes.getContratos().add(contratoDetalhes);
							analistaAterior = rs.getString("analista");
						}
						
						pbDetalhes.setNome(analistaAterior);
						pbDetalhes.setQtdContratos(pbDetalhes.getContratos().size());
						object.getDetalhes().add(pbDetalhes);							
						pbDetalhes = new PowerBiDetalhes();	
					}					
				} finally {
					closeResources(connection, ps);
				}
				return object;
			}
		});
	}
}
