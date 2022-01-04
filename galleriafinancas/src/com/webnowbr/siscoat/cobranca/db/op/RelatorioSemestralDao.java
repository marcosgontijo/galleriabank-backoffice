package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioSemestre;

public class RelatorioSemestralDao extends HibernateDao<RelatorioSemestre, Long> {

	private static final String QUERY_CONTRATOS_RECEBER = " select coco.id, numerocontrato, txJurosParcelas, empresa, ccd.dataVencimento, ccd.vlrParcela, pare.nome, corrigidoIPCA"
			+ " from cobranca.contratocobranca coco "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id "
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false "
			+ " inner join cobranca.pagadorrecebedor pare ON pare.id = coco.pagador "
			+ " where status = 'Aprovado' and ccd.id is not null " + " and pagador not in (15, 34,14, 182, 417, 803) "
			+ " ORDER BY numerocontrato asc, datavencimento asc ";

	private static final String QUERY_CONTRATOS_PAGAR_FAVORECIDO = " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_1 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor1 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'" + "	union all"
			+ "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_2 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor2 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'"
			+ "		union all" + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_3 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor3 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'"
			+ "		union all" + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_4 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor4 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'"
			+ "		union all" + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_5 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor5 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'"
			+ "		union all" + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_6 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor6 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'"
			+ "		union all" + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA "
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_7 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor "
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor7 = coco.id"
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false'"
			+ "		union all" + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA "
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_8 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor "
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor8 = coco.id "
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false' "
			+ "		union all " + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA "
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_9 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor "
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor9 = coco.id "
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false' "
			+ "		union all " + "	"
			+ " select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.empresa, ccpi.dataVencimento, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi "
			+ "	inner join cobranca.contratocobranca_parcelas_investidor_join_10 parcelaJoin "
			+ "		on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor "
			+ "	inner join cobranca.contratocobranca coco "
			+ "		on parcelaJoin.idcontratocobrancaparcelasinvestidor10 = coco.id "
			+ "	inner join cobranca.pagadorrecebedor pare " + "		ON pare.id = ccpi.investidor "
			+ "	where status = 'Aprovado' and ccpi.id is not null " + "		and ccpi.baixado = 'false' " + "	"
			+ "	ORDER BY numerocontrato asc, nome asc, datavencimento asc ";

	@SuppressWarnings("unchecked")
	public List<RelatorioSemestre> listaRelatorioReceber() {
		return (List<RelatorioSemestre>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<RelatorioSemestre> objects = new ArrayList<RelatorioSemestre>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_RECEBER);

					// ps.setString(1, sdf.format(dataDesagio));

					rs = ps.executeQuery();
					RelatorioSemestre relatorio = null;

					while (rs.next()) {
						relatorio = new RelatorioSemestre();
						relatorio.setNumeroContratoRelatorio(rs.getString("numerocontrato"));
						relatorio.setNomePagadorRelatorio(rs.getString("nome"));
						relatorio.setDataVencimentoRelatorio(rs.getDate("dataVencimento"));
						relatorio.setValorContratoRelatorio(rs.getBigDecimal("vlrParcela"));
						relatorio.setTaxaContratoRelatorio(rs.getBigDecimal("txJurosParcelas"));
						relatorio.setEmpresaContratoRelatorio(rs.getString("empresa"));
						if (rs.getBoolean("corrigidoIPCA")) {
							relatorio.setIndiceContratoRelatorio("Sim");
						} else {
							relatorio.setIndiceContratoRelatorio("Não");
						}
						objects.add(relatorio);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;

			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<RelatorioSemestre> listaRelatorioPagar() {
		return (List<RelatorioSemestre>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<RelatorioSemestre> objects = new ArrayList<RelatorioSemestre>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_PAGAR_FAVORECIDO);

					// ps.setString(1, sdf.format(dataDesagio));

					rs = ps.executeQuery();
					RelatorioSemestre relatorio = null;

					while (rs.next()) {
						relatorio = new RelatorioSemestre();
						relatorio.setNumeroContratoRelatorio(rs.getString("numerocontrato"));
						relatorio.setNomePagadorRelatorio(rs.getString("nome"));
						relatorio.setDataVencimentoRelatorio(rs.getDate("dataVencimento"));
						relatorio.setValorContratoRelatorio(rs.getBigDecimal("parcelaMensal"));
						relatorio.setTaxaContratoRelatorio(rs.getBigDecimal("txJurosParcelas"));
						relatorio.setEmpresaContratoRelatorio(rs.getString("empresa"));
						if (rs.getBoolean("corrigidoIPCA")) {
							relatorio.setIndiceContratoRelatorio("Sim");
						} else {
							relatorio.setIndiceContratoRelatorio("Não");
						}

						if (SiscoatConstants.PAGADOR_GALLERIA.contains(rs.getBigDecimal("pagador").longValue())) {
							relatorio.setTipoPagadorRelatorio("Debênture");
						} else {
							relatorio.setTipoPagadorRelatorio("Favorecido");
						}
						objects.add(relatorio);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;

			}
		});
	}
}
