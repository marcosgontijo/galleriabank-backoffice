package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
	
	private static final String QUERY_DASH_CONTRATOS =  	" select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.ccbpronta = 'true' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel";
	
	private static final String QUERY_DASH_CONTRATOS_POR_STATUS =  	" select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ " and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.ccbpronta = 'true' "
			+ " and ccbProntaData >= ? ::timestamp "
			+ " and ccbProntaData <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Aprovado' "
			+ " and agRegistroData >= ? ::timestamp "
			+ " and agRegistroData <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel";
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratos(final Date dataInicio, final Date dataFim, boolean consultarPorStatus) {
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
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_STATUS);
					} else {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS);
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

					while (rs.next()) {
						dashboard = new Dashboard();
						dashboard.setNomeResponsavel(rs.getString(2));

						responsavel = responsavelDao.findById(rs.getLong(1));
						dashboard.setResponsavel(responsavel);

						if (responsavel.getDonoResponsavel() != null) {
							dashboard.setGerenteResponsavel(responsavel.getDonoResponsavel().getNome());
						}

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

						objects.add(dashboard);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE =  " select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.ccbpronta = 'true' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " union all "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados"
			+ " from cobranca.contratocobranca c "
			+ " inner join cobranca.responsavel r on r.id = c.responsavel "
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel "
			+ " where c.status = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " ) totais "
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel ";
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE_POR_STATUS =  " select idresponsavel, nomeresponsavel, sum(contratosCadastrados) contratosCadastrados, sum(valorContratosCadastrados) valorContratosCadastrados, sum(contratosPreAprovados) contratosPreAprovados, sum(valorContratosPreAprovados) valorContratosPreAprovados, sum(contratosBoletosPagos) contratosBoletosPagos, sum(valorBoletosPagos) valorBoletosPagos, sum(contratosCcbsEmitidas) contratosCcbsEmitidas, sum(valorCcbsEmitidas) valorCcbsEmitidas, sum(contratosRegistrados) contratosRegistrados, sum(valorContratosRegistrados) valorContratosRegistrados from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) contratosCadastrados, sum(c.quantoPrecisa) valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.statuslead = 'Completo' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, count(c.id) contratosPreAprovados, sum(c.quantoPrecisa) valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.cadastroaprovadovalor = 'Aprovado' "
			+ " and inicioanalisedata >= ? ::timestamp "
			+ " and inicioanalisedata <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, count(c.id) contratosBoletosPagos, sum(c.quantoPrecisa) valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.pagtolaudoconfirmada = 'true' "
			+ " and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ " and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, count(c.id) contratosCcbsEmitidas, sum(c.valorccb) valorCcbsEmitidas, 0 contratosRegistrados, 0 valorContratosRegistrados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where c.ccbpronta = 'true' "
			+ " and ccbProntaData >= ? ::timestamp "
			+ " and ccbProntaData <= ? ::timestamp "
			+ " and (r1.ID = ? or r.ID = ?) "
			+ " group by r.id, r.nome, c.datacontrato "
			+ " union all "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 contratosCadastrados, 0 valorContratosCadastrados, 0 contratosPreAprovados, 0 valorContratosPreAprovados, 0 contratosBoletosPagos, 0 valorBoletosPagos, 0 contratosCcbsEmitidas, 0 valorCcbsEmitidas, count(c.id) contratosRegistrados, sum(c.valorccb) valorContratosRegistrados"
			+ " from cobranca.contratocobranca c "
			+ " inner join cobranca.responsavel r on r.id = c.responsavel "
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

						objects.add(dashboard);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}	
	
	private static final String QUERY_CONTRATOS_LEAD =  " select numerocontrato, imv.cidade, imv.estado, datacontrato, quantoprecisa, valoraprovadocomite, valorccb, urllead, statuslead, status, inicioanalise, cadastroaprovadovalor, PagtoLaudoConfirmada, LaudoRecebido, PajurFavoravel, AprovadoComite, AgAssinatura from cobranca.contratocobranca cc "
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
						contrato.getImovel().setEstado(rs.getString("estado"));
						contrato.setDataContrato(rs.getTimestamp("datacontrato"));
						contrato.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contrato.setValorAprovadoComite(rs.getBigDecimal("valorAprovadoComite"));
						contrato.setValorCCB(rs.getBigDecimal("valorccb"));
						contrato.setUrlLead(rs.getString("urllead"));
						contrato.setStatusLead(rs.getString("statuslead"));
						contrato.setStatus(rs.getString("status"));
						contrato.setInicioAnalise(rs.getBoolean("inicioanalise"));
						contrato.setCadastroAprovadoValor(rs.getString("cadastroaprovadovalor"));
						contrato.setPagtoLaudoConfirmada(rs.getBoolean("PagtoLaudoConfirmada"));
						contrato.setLaudoRecebido(rs.getBoolean("laudorecebido"));
						contrato.setPajurFavoravel(rs.getBoolean("pajurfavoravel"));
						contrato.setAprovadoComite(rs.getBoolean("aprovadocomite"));
						contrato.setContratoAssinado(rs.getBoolean("AgAssinatura"));
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
