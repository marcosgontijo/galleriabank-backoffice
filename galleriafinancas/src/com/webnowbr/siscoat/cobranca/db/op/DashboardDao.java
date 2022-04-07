package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Dashboard;
import com.webnowbr.siscoat.cobranca.db.model.GruposPagadores;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PesquisaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DashboardDao extends HibernateDao <Dashboard,Long> {

	// total contratos
	// contratos analise aprovada
	// contratos analise reprovada
	// aguardando pagamento
	// pagamento confirmando, aguardando laudo e paju
	
	private static final String QUERY_DASH_CONTRATOS =  " select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ " string_agg(numerosCadastrados,'#$&!') numerosCadastrados, string_agg(numerosPREAPROVADOS,'#$&!') numerosPREAPROVADOS, string_agg(numerosBOLETOSPAGOS,'#$&!') numerosBOLETOSPAGOS, string_agg(numerosCCBSEMITIDAS,'#$&!') numerosCCBSEMITIDAS, string_agg(numerosREGISTRADOS,'#$&!') numerosREGISTRADOS from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados, "
			+ " STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados, "
			+ " null numerosCadastrados, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados, "
			+ " null numerosCadastrados, null numerosPREAPROVADOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados, "
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.AgAssinatura = 'false' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados, "
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.status = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " ORDER BY IDRESPONSAVEL asc";
	
	private static final String QUERY_DASH_CONTRATOS_POR_STATUS =  	" select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ " string_agg(numerosCadastrados,'#$&!') numerosCadastrados, string_agg(numerosPREAPROVADOS,'#$&!') numerosPREAPROVADOS, string_agg(numerosBOLETOSPAGOS,'#$&!') numerosBOLETOSPAGOS, string_agg(numerosCCBSEMITIDAS,'#$&!') numerosCCBSEMITIDAS, string_agg(numerosREGISTRADOS,'#$&!') numerosREGISTRADOS "
			+ " from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ " and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.AgAssinatura = 'false' "
			+ " and AgAssinaturaData >= ? ::timestamp "
			+ " and AgAssinaturaData <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " where c.status = 'Aprovado' "
			+ " and agRegistroData >= ? ::timestamp "
			+ " and agRegistroData <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel";
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratos(final Date dataInicio, final Date dataFim, boolean consultarPorStatus, boolean consultarGerente) {
		return (List<Dashboard>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Dashboard> objects = new ArrayList<Dashboard>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					if(consultarPorStatus) {
						if(consultarGerente) {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_APENAS_GERENTE_POR_STATUS);
						} else {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_STATUS);
						}
					} else {
						if(consultarGerente) {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_APENAS_GERENTE);
						} else {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS);
						}
					}
					
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelFimSQL);

					ps.setDate(5, dtRelInicioSQL);
					ps.setDate(6, dtRelFimSQL);

					ps.setDate(7, dtRelInicioSQL);
					ps.setDate(8, dtRelFimSQL);

					ps.setDate(9, dtRelInicioSQL);
					ps.setDate(10, dtRelFimSQL);

					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					ResponsavelDao responsavelDao = new ResponsavelDao();
					Responsavel responsavel = new Responsavel();
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					

					while (rs.next()) {
						dashboard = new Dashboard();
						
						if(CommonsUtil.semValor(rs.getString(2))) {
							dashboard.setNomeResponsavel("-");
						} else {
							dashboard.setNomeResponsavel(rs.getString(2));
						}
						
						responsavel = responsavelDao.findById(rs.getLong(1));
						dashboard.setResponsavel(responsavel);
						
						if(!consultarGerente) {
							if (responsavel.getDonoResponsavel() != null) {
								dashboard.setGerenteResponsavel(responsavel.getDonoResponsavel().getNome());
							}
						}

						dashboard.setContratosCadastrados(rs.getInt(3));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal(4));
						dashboard.getListaCadastrados();
						
						dashboard.setContratosPreAprovados(rs.getInt(5));
						dashboard.setValorContratosPreAprovados(rs.getBigDecimal(6));
						
						
						dashboard.setContratosBoletosPagos(rs.getInt(7));
						dashboard.setValorBoletosPagos(rs.getBigDecimal(8));
						
						
						dashboard.setContratosCcbsEmitidas(rs.getInt(9));
						dashboard.setValorCcbsEmitidas(rs.getBigDecimal(10));
						
						
						dashboard.setContratosRegistrados(rs.getInt(11));
						dashboard.setValorContratosRegistrados(rs.getBigDecimal(12));
						
						//recebe os contratos
						List<String> listaCadastrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(13))) {
							listaCadastrados = Arrays.asList(rs.getString(13).split(Pattern.quote("#$&!")));
							dashboard.setListaCadastrados(new ArrayList<ContratoCobranca>());
							
							
							for(String cadastro : listaCadastrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCadastrados().add(coco);
							}
						}
						
						List<String> listaPreAprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(14))) {
							listaPreAprovados = Arrays.asList(rs.getString(14).split(Pattern.quote("#$&!")));
							dashboard.setListaPreAprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaPreAprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaPreAprovados().add(coco);
							}
						}
						
						List<String> listaBoletosPagos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(15))) {
							listaBoletosPagos = Arrays.asList(rs.getString(15).split(Pattern.quote("#$&!")));
							dashboard.setListaBoletosPagos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaBoletosPagos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaBoletosPagos().add(coco);
							}
						}
						
						List<String> listaCcbsEmitidas = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(16))) {
							listaCcbsEmitidas = Arrays.asList(rs.getString(16).split(Pattern.quote("#$&!")));
							dashboard.setListaCcbsEmitidas(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaCcbsEmitidas) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCcbsEmitidas().add(coco);
							}
						}
						
						List<String> listaRegistrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(17))) {
							listaRegistrados = Arrays.asList(rs.getString(17).split(Pattern.quote("#$&!")));
							dashboard.setListaRegistrados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaRegistrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaRegistrados().add(coco);
							}
						}

						objects.add(dashboard);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE =  " select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados,"
			+ " string_agg(numerosCadastrados,'#$&!') numerosCadastrados, string_agg(numerosPREAPROVADOS,'#$&!') numerosPREAPROVADOS, string_agg(numerosBOLETOSPAGOS,'#$&!') numerosBOLETOSPAGOS, string_agg(numerosCCBSEMITIDAS,'#$&!') numerosCCBSEMITIDAS, string_agg(numerosREGISTRADOS,'#$&!') numerosREGISTRADOS"
			+ " from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.AgAssinatura = 'false' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " union all "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c "
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel "
			+ " where c.status = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " ) totais "
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel ";
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE_POR_STATUS =  " select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados,"
			+ " string_agg(numerosCadastrados,'#$&!') numerosCadastrados, string_agg(numerosPREAPROVADOS,'#$&!') numerosPREAPROVADOS, string_agg(numerosBOLETOSPAGOS,'#$&!') numerosBOLETOSPAGOS, string_agg(numerosCCBSEMITIDAS,'#$&!') numerosCCBSEMITIDAS, string_agg(numerosREGISTRADOS,'#$&!') numerosREGISTRADOS "
			+ " from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, null numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ " and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosCCBSEMITIDAS, null numerosREGISTRADOS"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.AgAssinatura = 'false' "
			+ " and AgAssinaturaData >= ? ::timestamp "
			+ " and AgAssinaturaData <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " union all "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados,"
			+ " null numerosCadastrados, null numerosPREAPROVADOS, null numerosBOLETOSPAGOS, null numerosCCBSEMITIDAS, STRING_AGG(CONCAT(C.NUMEROCONTRATO,'!&$',P.nome), '#$&!' ) numerosREGISTRADOS "
			+ " from cobranca.contratocobranca c "
			+ " inner join cobranca.responsavel r on r.id = c.responsavel " + " inner join cobranca.pagadorrecebedor p on p.id = c.pagador "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel "
			+ " where c.status = 'Aprovado' "
			+ " and agRegistroData >= ? ::timestamp "
			+ " and agRegistroData <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " ) totais "
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel ";
	
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratosPorGerente(final Date dataInicio, final Date dataFim, final long idGerenteResponsavel, boolean consultarPorStatus) {
		return (List<Dashboard>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Dashboard> objects = new ArrayList<Dashboard>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					if(consultarPorStatus) {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_GERENTE);
					} else {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_GERENTE_POR_STATUS);
					}

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					ps.setLong(3, idGerenteResponsavel);
					ps.setLong(4, idGerenteResponsavel);
									
					ps.setDate(5, dtRelInicioSQL);
					ps.setDate(6, dtRelFimSQL);
					ps.setLong(7, idGerenteResponsavel);
					ps.setLong(8, idGerenteResponsavel);
										
					ps.setDate(9, dtRelInicioSQL);
					ps.setDate(10, dtRelFimSQL);
					ps.setLong(11, idGerenteResponsavel);
					ps.setLong(12, idGerenteResponsavel);
									
					ps.setDate(13, dtRelInicioSQL);
					ps.setDate(14, dtRelFimSQL);
					ps.setLong(15, idGerenteResponsavel);
					ps.setLong(16, idGerenteResponsavel);
									
					ps.setDate(17, dtRelInicioSQL);
					ps.setDate(18, dtRelFimSQL);
					ps.setLong(19, idGerenteResponsavel);
					ps.setLong(20, idGerenteResponsavel);

					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					ResponsavelDao responsavelDao = new ResponsavelDao();
					Responsavel responsavel = new Responsavel();
					Responsavel responsavelGerente = new Responsavel();

					responsavel = responsavelDao.findById(idGerenteResponsavel);
					responsavelGerente = responsavelDao.findById(idGerenteResponsavel);

					while (rs.next()) {
						dashboard = new Dashboard();
						dashboard.setNomeResponsavel(rs.getString(2));

						responsavel = responsavelDao.findById(rs.getLong(1));
						dashboard.setResponsavel(responsavel);

						dashboard.setGerenteResponsavel(responsavelGerente.getNome());

						dashboard.setContratosCadastrados(rs.getInt(3));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal(4));
						
						
						dashboard.setContratosPreAprovados(rs.getInt(5));
						dashboard.setValorContratosPreAprovados(rs.getBigDecimal(6));
						
						
						dashboard.setContratosBoletosPagos(rs.getInt(7));
						dashboard.setValorBoletosPagos(rs.getBigDecimal(8));
						
						
						dashboard.setContratosCcbsEmitidas(rs.getInt(9));
						dashboard.setValorCcbsEmitidas(rs.getBigDecimal(10));
						
						
						dashboard.setContratosRegistrados(rs.getInt(11));
						dashboard.setValorContratosRegistrados(rs.getBigDecimal(12));
						
						//recebe os contratos
						List<String> listaCadastrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(13))) {
							listaCadastrados = Arrays.asList(rs.getString(13).split(Pattern.quote("#$&!")));
							dashboard.setListaCadastrados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaCadastrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCadastrados().add(coco);
							}
						}
						
						List<String> listaPreAprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(14))) {
							listaPreAprovados = Arrays.asList(rs.getString(14).split(Pattern.quote("#$&!")));
							dashboard.setListaPreAprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaPreAprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaPreAprovados().add(coco);
							}
						}
						
						List<String> listaBoletosPagos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(15))) {
							listaBoletosPagos = Arrays.asList(rs.getString(15).split(Pattern.quote("#$&!")));
							dashboard.setListaBoletosPagos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaBoletosPagos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaBoletosPagos().add(coco);
							}
						}
						
						List<String> listaCcbsEmitidas = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(16))) {
							listaCcbsEmitidas = Arrays.asList(rs.getString(16).split(Pattern.quote("#$&!")));
							dashboard.setListaCcbsEmitidas(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaCcbsEmitidas) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCcbsEmitidas().add(coco);
							}
						}
						
						List<String> listaRegistrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString(17))) {
							listaRegistrados = Arrays.asList(rs.getString(17).split(Pattern.quote("#$&!")));
							dashboard.setListaRegistrados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaRegistrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaRegistrados().add(coco);
							}
						}

						objects.add(dashboard);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	private static final String QUERY_DASH_CONTRATOS_APENAS_GERENTE =  " select  IDGERENTE,\r\n"
			+ "	NOMEGerente,\r\n"
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSCADASTRADOS) VALORCONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(CONTRATOSPREAPROVADOS) CONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(VALORCONTRATOSPREAPROVADOS) VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(CONTRATOSBOLETOSPAGOS) CONTRATOSBOLETOSPAGOS,\r\n"
			+ "	SUM(VALORBOLETOSPAGOS) VALORBOLETOSPAGOS,\r\n"
			+ "	SUM(CONTRATOSCCBSEMITIDAS) CONTRATOSCCBSEMITIDAS,\r\n"
			+ "	SUM(VALORCCBSEMITIDAS) VALORCCBSEMITIDAS,\r\n"
			+ "	SUM(CONTRATOSREGISTRADOS) CONTRATOSREGISTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSREGISTRADOS) VALORCONTRATOSREGISTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSCADASTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCADASTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSPREAPROVADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSPREAPROVADOS,\r\n"
			+ "	STRING_AGG(NUMEROSBOLETOSPAGOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSBOLETOSPAGOS,\r\n"
			+ "	STRING_AGG(NUMEROSCCBSEMITIDAS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCCBSEMITIDAS,\r\n"
			+ "	STRING_AGG(NUMEROSREGISTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSREGISTRADOS\r\n"
			+ "		from(\r\n"
			+ "SELECT IDRESPONSAVEL,IDGERENTE,\r\n"
			+ "	NOMERESPONSAVEL, NOMEGerente,\r\n"
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSCADASTRADOS) VALORCONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(CONTRATOSPREAPROVADOS) CONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(VALORCONTRATOSPREAPROVADOS) VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(CONTRATOSBOLETOSPAGOS) CONTRATOSBOLETOSPAGOS,\r\n"
			+ "	SUM(VALORBOLETOSPAGOS) VALORBOLETOSPAGOS,\r\n"
			+ "	SUM(CONTRATOSCCBSEMITIDAS) CONTRATOSCCBSEMITIDAS,\r\n"
			+ "	SUM(VALORCCBSEMITIDAS) VALORCCBSEMITIDAS,\r\n"
			+ "	SUM(CONTRATOSREGISTRADOS) CONTRATOSREGISTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSREGISTRADOS) VALORCONTRATOSREGISTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSCADASTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCADASTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSPREAPROVADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSPREAPROVADOS,\r\n"
			+ "	STRING_AGG(NUMEROSBOLETOSPAGOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSBOLETOSPAGOS,\r\n"
			+ "	STRING_AGG(NUMEROSCCBSEMITIDAS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCCBSEMITIDAS,\r\n"
			+ "	STRING_AGG(NUMEROSREGISTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSREGISTRADOS\r\n"
			+ "FROM\r\n"
			+ "	(SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE, \r\n"
			+ "			R.NOME NOMERESPONSAVEL,  R1.NOME NOMEGerente,\r\n"
			+ "			COUNT(C.ID) CONTRATOSCADASTRADOS,\r\n"
			+ "			SUM(C.QUANTOPRECISA) VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.STATUSLEAD = 'Completo'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.NOME NOMEGerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSPREAPROVADOS,\r\n"
			+ "			SUM(C.QUANTOPRECISA) VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL\r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.CADASTROAPROVADOVALOR = 'Aprovado'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.NOME NOMEGerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			SUM(C.QUANTOPRECISA) VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.PAGTOLAUDOCONFIRMADA = 'true'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID,IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.NOME NOMEGerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			SUM(C.VALORCCB) VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.AGASSINATURA = 'false'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.nome nomegerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSREGISTRADOS,\r\n"
			+ "			SUM(C.VALORCCB) VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.STATUS = 'Aprovado'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ? ::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME) TOTAIS\r\n"
			+ "GROUP BY IDRESPONSAVEL, IDGERENTE,\r\n"
			+ "	NOMERESPONSAVEL, nomegerente) totalGerente\r\n"
			+ "Group by IDGERENTE,\r\n"
			+ "	NOMEGerente\r\n"
			+ "ORDER BY IDGERENTE ASC ";
	
	private static final String QUERY_DASH_CONTRATOS_APENAS_GERENTE_POR_STATUS =  " select  IDGERENTE,\r\n"
			+ "	NOMEGerente,\r\n"
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSCADASTRADOS) VALORCONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(CONTRATOSPREAPROVADOS) CONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(VALORCONTRATOSPREAPROVADOS) VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(CONTRATOSBOLETOSPAGOS) CONTRATOSBOLETOSPAGOS,\r\n"
			+ "	SUM(VALORBOLETOSPAGOS) VALORBOLETOSPAGOS,\r\n"
			+ "	SUM(CONTRATOSCCBSEMITIDAS) CONTRATOSCCBSEMITIDAS,\r\n"
			+ "	SUM(VALORCCBSEMITIDAS) VALORCCBSEMITIDAS,\r\n"
			+ "	SUM(CONTRATOSREGISTRADOS) CONTRATOSREGISTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSREGISTRADOS) VALORCONTRATOSREGISTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSCADASTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCADASTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSPREAPROVADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSPREAPROVADOS,\r\n"
			+ "	STRING_AGG(NUMEROSBOLETOSPAGOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSBOLETOSPAGOS,\r\n"
			+ "	STRING_AGG(NUMEROSCCBSEMITIDAS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCCBSEMITIDAS,\r\n"
			+ "	STRING_AGG(NUMEROSREGISTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSREGISTRADOS\r\n"
			+ "		from(\r\n"
			+ "SELECT IDRESPONSAVEL,IDGERENTE,\r\n"
			+ "	NOMERESPONSAVEL, NOMEGerente,\r\n"
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSCADASTRADOS) VALORCONTRATOSCADASTRADOS,\r\n"
			+ "	SUM(CONTRATOSPREAPROVADOS) CONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(VALORCONTRATOSPREAPROVADOS) VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "	SUM(CONTRATOSBOLETOSPAGOS) CONTRATOSBOLETOSPAGOS,\r\n"
			+ "	SUM(VALORBOLETOSPAGOS) VALORBOLETOSPAGOS,\r\n"
			+ "	SUM(CONTRATOSCCBSEMITIDAS) CONTRATOSCCBSEMITIDAS,\r\n"
			+ "	SUM(VALORCCBSEMITIDAS) VALORCCBSEMITIDAS,\r\n"
			+ "	SUM(CONTRATOSREGISTRADOS) CONTRATOSREGISTRADOS,\r\n"
			+ "	SUM(VALORCONTRATOSREGISTRADOS) VALORCONTRATOSREGISTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSCADASTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCADASTRADOS,\r\n"
			+ "	STRING_AGG(NUMEROSPREAPROVADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSPREAPROVADOS,\r\n"
			+ "	STRING_AGG(NUMEROSBOLETOSPAGOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSBOLETOSPAGOS,\r\n"
			+ "	STRING_AGG(NUMEROSCCBSEMITIDAS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSCCBSEMITIDAS,\r\n"
			+ "	STRING_AGG(NUMEROSREGISTRADOS,\r\n"
			+ "\r\n"
			+ "		'#$&!') NUMEROSREGISTRADOS\r\n"
			+ "FROM\r\n"
			+ "	(SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE, \r\n"
			+ "			R.NOME NOMERESPONSAVEL,  R1.NOME NOMEGerente,\r\n"
			+ "			COUNT(C.ID) CONTRATOSCADASTRADOS,\r\n"
			+ "			SUM(C.QUANTOPRECISA) VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.STATUSLEAD = 'Completo'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.NOME NOMEGerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSPREAPROVADOS,\r\n"
			+ "			SUM(C.QUANTOPRECISA) VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL\r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.CADASTROAPROVADOVALOR = 'Aprovado'\r\n"
			+ "			AND INICIOANALISEDATA >= ? ::TIMESTAMP\r\n"
			+ "			AND INICIOANALISEDATA <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.NOME NOMEGerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			SUM(C.QUANTOPRECISA) VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.PAGTOLAUDOCONFIRMADA = 'true'\r\n"
			+ "			AND pagtoLaudoConfirmadaData >= ? ::TIMESTAMP\r\n"
			+ "			AND pagtoLaudoConfirmadaData <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID,IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.NOME NOMEGerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			SUM(C.VALORCCB) VALORCCBSEMITIDAS,\r\n"
			+ "			0 CONTRATOSREGISTRADOS,\r\n"
			+ "			0 VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSCCBSEMITIDAS,\r\n"
			+ "			NULL NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.AGASSINATURA = 'false'\r\n"
			+ "			AND AgAssinaturaData >= ? ::TIMESTAMP\r\n"
			+ "			AND AgAssinaturaData <= ?::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME,\r\n"
			+ "			C.DATACONTRATO\r\n"
			+ "		UNION ALL SELECT R.ID IDRESPONSAVEL, R1.ID IDGERENTE,\r\n"
			+ "			R.NOME NOMERESPONSAVEL, R1.nome nomegerente,\r\n"
			+ "			0 CONTRATOSCADASTRADOS,\r\n"
			+ "			0 VALORCONTRATOSCADASTRADOS,\r\n"
			+ "			0 CONTRATOSPREAPROVADOS,\r\n"
			+ "			0 VALORCONTRATOSPREAPROVADOS,\r\n"
			+ "			0 CONTRATOSBOLETOSPAGOS,\r\n"
			+ "			0 VALORBOLETOSPAGOS,\r\n"
			+ "			0 CONTRATOSCCBSEMITIDAS,\r\n"
			+ "			0 VALORCCBSEMITIDAS,\r\n"
			+ "			COUNT(C.ID) CONTRATOSREGISTRADOS,\r\n"
			+ "			SUM(C.VALORCCB) VALORCONTRATOSREGISTRADOS,\r\n"
			+ "			NULL NUMEROSCADASTRADOS,\r\n"
			+ "			NULL NUMEROSPREAPROVADOS,\r\n"
			+ "			NULL NUMEROSBOLETOSPAGOS,\r\n"
			+ "			NULL NUMEROSCCBSEMITIDAS,\r\n"
			+ "			STRING_AGG(CONCAT(C.NUMEROCONTRATO,\r\n"
			+ "\r\n"
			+ "															'!&$',\r\n"
			+ "															P.NOME),\r\n"
			+ "\r\n"
			+ "				'#$&!') NUMEROSREGISTRADOS\r\n"
			+ "		FROM COBRANCA.CONTRATOCOBRANCA C\r\n"
			+ "		INNER JOIN COBRANCA.RESPONSAVEL R ON R.ID = C.RESPONSAVEL \r\n"
			+ "	 left JOIN COBRANCA.RESPONSAVEL R1 ON R1.ID = R.DONORESPONSAVEL\r\n"
			+ "		INNER JOIN COBRANCA.PAGADORRECEBEDOR P ON P.ID = C.PAGADOR\r\n"
			+ "		WHERE C.STATUS = 'Aprovado'\r\n"
			+ "			AND agRegistroData >= ? ::TIMESTAMP\r\n"
			+ "			AND agRegistroData <= ? ::TIMESTAMP\r\n"
			+ "		GROUP BY R.ID, IDGERENTE,\r\n"
			+ "			R.NOME) TOTAIS\r\n"
			+ "GROUP BY IDRESPONSAVEL, IDGERENTE,\r\n"
			+ "	NOMERESPONSAVEL, nomegerente) totalGerente\r\n"
			+ "Group by IDGERENTE,\r\n"
			+ "	NOMEGerente\r\n"
			+ "ORDER BY IDGERENTE ASC ";
	
	
	
	private static final String QUERY_CONTRATOS_LEAD =  " select numerocontrato, imv.cidade, imv.cep, imv.estado, datacontrato, motivoReprovaLead, motivoReprovaSelectItem, quantoprecisa, valoraprovadocomite, valorccb, urllead, statuslead, status, inicioanalise, cadastroaprovadovalor, PagtoLaudoConfirmada, LaudoRecebido, PajurFavoravel, AprovadoComite, AgAssinatura from cobranca.contratocobranca cc "
			+ " inner join cobranca.imovelcobranca imv on imv.id = cc.imovel "
			+ " inner join cobranca.pagadorrecebedor pare on pare.id = cc.pagador "
			+ " where urllead is not null and urllead != '' and pare.nome not LIKE '%teste%' and pare.nome  not LIKE '%Teste%' "
			+ " order by cc.id desc ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratosLead() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_LEAD);
					rs = ps.executeQuery();					

					while (rs.next()) {
						ContratoCobranca contrato = new ContratoCobranca();
						
						contrato.setNumeroContrato(rs.getString("numerocontrato"));
						contrato.setImovel(new ImovelCobranca());
						contrato.getImovel().setCidade(rs.getString("cidade"));
						contrato.getImovel().setCep(rs.getString("cep"));
						contrato.getImovel().setEstado(rs.getString("estado"));
						contrato.setDataContrato(rs.getTimestamp("datacontrato"));
						contrato.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contrato.setValorAprovadoComite(rs.getBigDecimal("valorAprovadoComite"));
						contrato.setValorCCB(rs.getBigDecimal("valorccb"));
						contrato.setUrlLead(rs.getString("urllead"));
						contrato.setMotivoReprovaLead(rs.getString("motivoReprovaLead"));
						contrato.setMotivoReprovaSelectItem(rs.getString("motivoReprovaSelectItem"));
						contrato.setStatusLead(rs.getString("statuslead"));
						contrato.setStatus(rs.getString("status"));
						contrato.setInicioAnalise(rs.getBoolean("inicioanalise"));
						contrato.setCadastroAprovadoValor(rs.getString("cadastroaprovadovalor"));
						contrato.setPagtoLaudoConfirmada(rs.getBoolean("PagtoLaudoConfirmada"));
						contrato.setLaudoRecebido(rs.getBoolean("laudorecebido"));
						contrato.setPajurFavoravel(rs.getBoolean("pajurfavoravel"));
						contrato.setAprovadoComite(rs.getBoolean("aprovadocomite"));
						contrato.setAgAssinatura(rs.getBoolean("AgAssinatura"));
						objects.add(contrato);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}	
}
