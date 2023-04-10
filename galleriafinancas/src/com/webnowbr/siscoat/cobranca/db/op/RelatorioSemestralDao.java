package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.DataCalculoDebentures;
import com.webnowbr.siscoat.relatorio.vo.DebenturesRelatorio;
import com.webnowbr.siscoat.relatorio.vo.RelatorioSemestre;
import com.webnowbr.siscoat.relatorio.vo.SaquesDebentures;

public class RelatorioSemestralDao extends HibernateDao<RelatorioSemestre, Long> {

	private static final String QUERY_CONTRATOS_RECEBER = " select coco.id, numerocontrato, txJurosParcelas, empresa, ccd.dataVencimento, ccd.vlrParcela, pare.nome, corrigidoIPCA, corrigidonovoipca "
			+ " from cobranca.contratocobranca coco "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id "
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false "
			+ " inner join cobranca.pagadorrecebedor pare ON pare.id = coco.pagador "
			+ " where status = 'Aprovado' and ccd.id is not null " + " and pagador not in (15, 34,14, 182, 417, 803) "
			+ " ORDER BY numerocontrato asc, datavencimento asc ";

	private static final String QUERY_CONTRATOS_PAGAR_FAVORECIDO = "select\r\n" + "	coco.id,\r\n"
			+ "	coco.numerocontrato,\r\n" + "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor1,\r\n"
			+ "	coco.empresa,\r\n" + "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,\r\n" + "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n"
			+ "	coco.corrigidoIPCA,\r\n" + "	ccpi.id ccpiid\r\n" + "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_1 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor1 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor2,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_2 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor2 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor3,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_3 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor3 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor4,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_4 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor4 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor5,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_5 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor5 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor6,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_6 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor6 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor7,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_7 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor7 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor8,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_8 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor8 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor9,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_9 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor9 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "union all\r\n" + "select\r\n" + "	coco.id,\r\n" + "	coco.numerocontrato,\r\n"
			+ "	coco.txJurosParcelas,\r\n" + "	coco.taxaremuneracaoinvestidor10,\r\n" + "	coco.empresa,\r\n"
			+ "	ccpi.dataVencimento,\r\n" + "	ccpi.amortizacao,\r\n" + "	ccpi.capitalizacao,\r\n"
			+ "	ccpi.parcelaMensal,\r\n" + "	pare.nome,\r\n" + "	coco.pagador,\r\n" + "	coco.corrigidoIPCA,\r\n"
			+ "	ccpi.id ccpiid\r\n" + "from\r\n" + "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_10 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor10 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n" + "	pare.id = ccpi.investidor\r\n" + "where\r\n"
			+ "	status = 'Aprovado'\r\n" + "	and ccpi.id is not null\r\n" + "	and ccpi.baixado = 'false'\r\n"
			+ "order by\r\n" + "	numerocontrato asc,\r\n" + "	nome asc,\r\n" + "	datavencimento asc";

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
							if (rs.getBoolean("corrigidonovoipca")) {
								relatorio.setIndiceContratoRelatorio("Sim");
							} else {
								relatorio.setIndiceContratoRelatorio("Não");
							}
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
						relatorio.setValorAmortizacao(rs.getBigDecimal("amortizacao"));
						relatorio.setValorCapitalizacao(rs.getBigDecimal("capitalizacao"));
						relatorio.setTaxaContratoRelatorio(rs.getBigDecimal("txJurosParcelas"));
						relatorio.setTaxaInvestidor(rs.getBigDecimal(4));
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

	private static final String QUERY_DEBENTURES_NAO_MENSAL = "select\r\n"
			+ "	*\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido1, cp.datavencimento, c.taxaremuneracaoinvestidor1,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor1 valorface, c.dataInicioInvestidor1,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  1 numeroInvestidor, c.recebedor recebedor\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_1 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor1\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor1 = c.qtdeParcelasInvestidor1 - 1\r\n"
			+ "		and c.ocultarecebedor = false\r\n"
			+ "		and c.recebedorenvelope = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido2, cp.datavencimento, c.taxaremuneracaoinvestidor2,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor2 valorface, c.dataInicioInvestidor2,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  2 numeroInvestidor, c.recebedor2 recebedor\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_2 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor2\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor2 = c.qtdeParcelasInvestidor2 - 1\r\n"
			+ "		and c.ocultarecebedor2 = false\r\n"
			+ "		and c.recebedorenvelope2 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido3, cp.datavencimento, c.taxaremuneracaoinvestidor3,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor3 valorface, c.dataInicioInvestidor3,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  3 numeroInvestidor, c.recebedor3 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_3 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor3\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor3 = c.qtdeParcelasInvestidor3 - 1\r\n"
			+ "		and c.ocultarecebedor3 = false\r\n"
			+ "		and c.recebedorenvelope3 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido4, cp.datavencimento, c.taxaremuneracaoinvestidor4,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor4 valorface, c.dataInicioInvestidor4,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  4 numeroInvestidor, c.recebedor4 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_4 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor4\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor4 = c.qtdeParcelasInvestidor4 - 1\r\n"
			+ "		and c.ocultarecebedor4 = false\r\n"
			+ "		and c.recebedorenvelope4 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido5, cp.datavencimento, c.taxaremuneracaoinvestidor5,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor5 valorface, c.dataInicioInvestidor5,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  5 numeroInvestidor, c.recebedor5 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_5 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor5\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor5 = c.qtdeParcelasInvestidor5 - 1\r\n"
			+ "		and c.ocultarecebedor5 = false\r\n"
			+ "		and c.recebedorenvelope5 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido6, cp.datavencimento, c.taxaremuneracaoinvestidor6,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor6 valorface, c.dataInicioInvestidor6,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  6 numeroInvestidor, c.recebedor6 recebedor\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_6 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor6\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor6 = c.qtdeParcelasInvestidor6 - 1\r\n"
			+ "		and c.ocultarecebedor6 = false\r\n"
			+ "		and c.recebedorenvelope6 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido7, cp.datavencimento, c.taxaremuneracaoinvestidor7,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor7 valorface, c.dataInicioInvestidor7,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  7 numeroInvestidor, c.recebedor7 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_7 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor7\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor7 = c.qtdeParcelasInvestidor7 - 1\r\n"
			+ "		and c.ocultarecebedor7 = false\r\n"
			+ "		and c.recebedorenvelope7 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido8, cp.datavencimento, c.taxaremuneracaoinvestidor8,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor8 valorface, c.dataInicioInvestidor8,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  8 numeroInvestidor, c.recebedor8 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_8 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor8\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor8 = c.qtdeParcelasInvestidor8 - 1\r\n"
			+ "		and c.ocultarecebedor8 = false\r\n"
			+ "		and c.recebedorenvelope8 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido9, cp.datavencimento, c.taxaremuneracaoinvestidor9,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor9 valorface, c.dataInicioInvestidor9,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  9 numeroInvestidor, c.recebedor9 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_9 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor9\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor9 = c.qtdeParcelasInvestidor9 - 1\r\n"
			+ "		and c.ocultarecebedor9 = false\r\n"
			+ "		and c.recebedorenvelope9 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido10, cp.datavencimento, c.taxaremuneracaoinvestidor10,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor10 valorface, c.dataInicioInvestidor10,\r\n"
			+ "		c.id contrato, pare.id investidor, cp.id parcela,  10 numeroInvestidor, c.recebedor10 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_10 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor10\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		(( cp.parcelamensal != 0 and baixado ) or (cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor10 = c.qtdeParcelasInvestidor10 - 1\r\n"
			+ "		and c.ocultarecebedor10 = false \r\n"
			+ "		and c.recebedorenvelope10 = ?--\r\n"
			+ "		) investidores\r\n"
			+ "where\r\n"
			+ "	recebedor not in (14, 15, 34)\r\n"
			+ "	and numerocontrato in (select numerocontrato from (\r\n"
			+ "	select c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_1 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor1\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor1 = c.qtdeParcelasInvestidor1 - 1\r\n"
			+ "		and c.ocultarecebedor = false\r\n"
			+ "		and c.recebedorenvelope = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_2 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor2\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor2 = c.qtdeParcelasInvestidor2 - 1\r\n"
			+ "		and c.ocultarecebedor2 = false\r\n"
			+ "		and c.recebedorenvelope2 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_3 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor3\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor3 = c.qtdeParcelasInvestidor3 - 1\r\n"
			+ "		and c.ocultarecebedor3 = false\r\n"
			+ "		and c.recebedorenvelope3 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_4 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor4\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor4 = c.qtdeParcelasInvestidor4 - 1\r\n"
			+ "		and c.ocultarecebedor4 = false\r\n"
			+ "		and c.recebedorenvelope4 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_5 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor5\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor5 = c.qtdeParcelasInvestidor5 - 1\r\n"
			+ "		and c.ocultarecebedor5 = false\r\n"
			+ "		and c.recebedorenvelope5 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_6 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor6\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor6 = c.qtdeParcelasInvestidor6 - 1\r\n"
			+ "		and c.ocultarecebedor6 = false\r\n"
			+ "		and c.recebedorenvelope6 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_7 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor7\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor7 = c.qtdeParcelasInvestidor7 - 1\r\n"
			+ "		and c.ocultarecebedor7 = false\r\n"
			+ "		and c.recebedorenvelope7 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_8 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor8\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor8 = c.qtdeParcelasInvestidor8 - 1\r\n"
			+ "		and c.ocultarecebedor8 = false\r\n"
			+ "		and c.recebedorenvelope8 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_9 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor9\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor9 = c.qtdeParcelasInvestidor9 - 1\r\n"
			+ "		and c.ocultarecebedor9 = false\r\n"
			+ "		and c.recebedorenvelope9 = ?--\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		c.numerocontrato\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_10 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor10\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ?\r\n"
			+ "			and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor10 = c.qtdeParcelasInvestidor10 - 1\r\n"
			+ "		and c.ocultarecebedor10 = false\r\n"
			+ "		and c.recebedorenvelope10 = ?--\r\n"
			+ "		) investidores\r\n"
			+ "	) order by numerocontrato, numeroinvestidor, amortizacao asc \r\n"
			+ "";
	
	private static final String QUERY_DEBENTURES_MENSAL = "select\r\n"
			+ "	*\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido1, cp.datavencimento, c.taxaremuneracaoinvestidor1,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor1 valorface, c.dataInicioInvestidor1,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  1 numeroInvestidor, c.recebedor recebedor\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_1 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor1\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor1 != c.qtdeParcelasInvestidor1 - 1\r\n"
			+ "		and c.ocultarecebedor = false\r\n"
			+ "		and c.recebedorenvelope = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido2, cp.datavencimento, c.taxaremuneracaoinvestidor2,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor2 valorface, c.dataInicioInvestidor2,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  2 numeroInvestidor, c.recebedor2 recebedor\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_2 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor2\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor2 != c.qtdeParcelasInvestidor2 - 1\r\n"
			+ "		and c.ocultarecebedor2 = false\r\n"
			+ "		and c.recebedorenvelope2 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido3, cp.datavencimento, c.taxaremuneracaoinvestidor3,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor3 valorface, c.dataInicioInvestidor3,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  3 numeroInvestidor, c.recebedor3 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_3 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor3\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor3 != c.qtdeParcelasInvestidor3 - 1\r\n"
			+ "		and c.ocultarecebedor3 = false\r\n"
			+ "		and c.recebedorenvelope3 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido4, cp.datavencimento, c.taxaremuneracaoinvestidor4,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor4 valorface, c.dataInicioInvestidor4,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  4 numeroInvestidor, c.recebedor4 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_4 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor4\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor4 != c.qtdeParcelasInvestidor4 - 1\r\n"
			+ "		and c.ocultarecebedor4 = false\r\n"
			+ "		and c.recebedorenvelope4 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido5, cp.datavencimento, c.taxaremuneracaoinvestidor5,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor5 valorface, c.dataInicioInvestidor5,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  5 numeroInvestidor, c.recebedor5 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_5 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor5\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor5 != c.qtdeParcelasInvestidor5 - 1\r\n"
			+ "		and c.ocultarecebedor5 = false\r\n"
			+ "		and c.recebedorenvelope5 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido6, cp.datavencimento, c.taxaremuneracaoinvestidor6,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor6 valorface, c.dataInicioInvestidor6,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  6 numeroInvestidor, c.recebedor6 recebedor\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_6 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor6\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor6 != c.qtdeParcelasInvestidor6 - 1\r\n"
			+ "		and c.ocultarecebedor6 = false\r\n"
			+ "		and c.recebedorenvelope6 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido7, cp.datavencimento, c.taxaremuneracaoinvestidor7,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor7 valorface, c.dataInicioInvestidor7,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  7 numeroInvestidor, c.recebedor7 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_7 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor7\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor7 != c.qtdeParcelasInvestidor7 - 1\r\n"
			+ "		and c.ocultarecebedor7 = false\r\n"
			+ "		and c.recebedorenvelope7 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido8, cp.datavencimento, c.taxaremuneracaoinvestidor8,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor8 valorface, c.dataInicioInvestidor8,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  8 numeroInvestidor, c.recebedor8 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_8 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor8\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor8 != c.qtdeParcelasInvestidor8 - 1\r\n"
			+ "		and c.ocultarecebedor8 = false\r\n"
			+ "		and c.recebedorenvelope8 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido9, cp.datavencimento, c.taxaremuneracaoinvestidor9,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor9 valorface, c.dataInicioInvestidor9,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  9 numeroInvestidor, c.recebedor9 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_9 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor9\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor9 != c.qtdeParcelasInvestidor9 - 1\r\n"
			+ "		and c.ocultarecebedor9 = false\r\n"
			+ "		and c.recebedorenvelope9 = ?\r\n"
			+ "union\r\n"
			+ "	select\r\n"
			+ "		pare.nome, pare.cpf, pare.cnpj, pare.agencia, pare.conta,\r\n"
			+ "		c.numerocontrato, c.recebedorGarantido10, cp.datavencimento, c.taxaremuneracaoinvestidor10,\r\n"
			+ "		cp.parcelamensal, cp.capitalizacao, cp.amortizacao, cp.valorliquido, c.vlrInvestidor10 valorface, c.dataInicioInvestidor10,\r\n"
			+ "		cp.saldoCredorAtualizado, c.id contrato, pare.id investidor, cp.id parcela,  10 numeroInvestidor, c.recebedor10 recebedor \r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca_parcelas_investidor_join_10 cj\r\n"
			+ "	inner join cobranca.contratocobranca c on\r\n"
			+ "		c.id = cj.idcontratocobrancaparcelasinvestidor10\r\n"
			+ "	inner join cobranca.contratocobrancaparcelasinvestidor cp on\r\n"
			+ "		cp.id = cj.idcontratocobrancaparcelasinvestidor inner join cobranca.pagadorrecebedor pare on pare.id = cp.investidor\r\n"
			+ "	where\r\n"
			+ "		((cp.datavencimento >= ? and cp.datavencimento < ?))\r\n"
			+ "		and c.carenciaInvestidor10 != c.qtdeParcelasInvestidor10 - 1\r\n"
			+ "		and c.ocultarecebedor10 = false\r\n"
			+ "		and c.recebedorenvelope10 = ?\r\n"
			+ "		) investidores\r\n"
			+ "where\r\n"
			+ "	recebedor not in (14, 15, 34) \r\n"
			+ "	order by numerocontrato, numeroinvestidor\r\n"
			+ "";

	@SuppressWarnings("unchecked")
	public List<DebenturesRelatorio> getDebenturesNaoMensal(final Date dataInicio, final Date dataFim, final boolean envelope, final List<DataCalculoDebentures> listaDatasCalculo) {
		return (List<DebenturesRelatorio>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DebenturesRelatorio> listaDenbentures = new ArrayList<DebenturesRelatorio>();
				
				//List<DataCalculoDebentures> listaDatasCalculo2 = Copy(listaDatasCalculo);	
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_PARCELAS_POR_DATA = QUERY_DEBENTURES_NAO_MENSAL;

					ps = connection.prepareStatement(query_QUERY_GET_PARCELAS_POR_DATA);		

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(ajustaDataHoraFinal(dataFim).getTime());

					for(int i = 0; i < 60; ) {
						i++;
						ps.setDate(i, dtRelInicioSQL);
						i++;
						ps.setDate(i, dtRelFimSQL);
						i++;
						ps.setBoolean(i, envelope);
					}
				
					rs = ps.executeQuery();

					DebenturesRelatorio debenture = new DebenturesRelatorio();
					String numeroContratoantigo = "numeroContrato";
					int numeroInvestidoAntigo = 99999;
					while (rs.next()) {
						
						if(CommonsUtil.mesmoValor(rs.getString("numerocontrato"), "39131")) {
							String aa = "asda";	
						}
						
						if(!CommonsUtil.mesmoValor(rs.getString("numerocontrato"), numeroContratoantigo)
								|| !CommonsUtil.mesmoValor(rs.getInt("numeroinvestidor"), numeroInvestidoAntigo)) {
							if(!CommonsUtil.mesmoValor(numeroContratoantigo, "numeroContrato")) {
								listaDenbentures.add(debenture);
							}
							debenture = new DebenturesRelatorio();
							debenture.setNome(rs.getString("nome"));
							debenture.setCpfCnpj(CommonsUtil.stringValueVazio(rs.getString("cpf")) 
									+ CommonsUtil.stringValueVazio(rs.getString("cnpj")));
							debenture.setAgenciaConta(rs.getString("agencia") + " | " + rs.getString("conta"));
							debenture.setNumerocontrato(rs.getString("numerocontrato"));
							if (rs.getBoolean("recebedorgarantido1")) {
								debenture.setGarantido("Sim");
							} else {
								debenture.setGarantido("Não");
							}
							debenture.setDataVencimento(rs.getDate("datavencimento"));
							debenture.setTaxaMensal(rs.getBigDecimal("taxaremuneracaoinvestidor1"));
							debenture.setRecebeMensal("Não");			
							BigDecimal parcelaMensal = rs.getBigDecimal("parcelamensal");
							BigDecimal capitalizacao = rs.getBigDecimal("capitalizacao");
							BigDecimal amortizacao = rs.getBigDecimal("amortizacao");
							if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
								parcelaMensal = capitalizacao.add(amortizacao);
							} 
							debenture.setValorBruto(parcelaMensal);
							debenture.setValorLiquido(rs.getBigDecimal("valorliquido"));
							debenture.setValorFace(rs.getBigDecimal("valorface"));
							debenture.setDataInicio(rs.getDate("datainicioinvestidor1"));	
							debenture.setIdContrato(rs.getLong("contrato"));
							debenture.setIdInvestidor(rs.getLong("investidor"));
							debenture.setIdParcela(rs.getLong("parcela"));
							debenture.setNumeroInvestidor(rs.getInt("numeroinvestidor"));
							numeroContratoantigo = debenture.getNumerocontrato();
							numeroInvestidoAntigo = debenture.getNumeroInvestidor();
							debenture.setCalculos(Copy(listaDatasCalculo));
						} else if(CommonsUtil.mesmoValor(rs.getString("numerocontrato"), numeroContratoantigo)
							&& CommonsUtil.mesmoValor(rs.getInt("numeroinvestidor"), numeroInvestidoAntigo)) {
							SaquesDebentures saque = new SaquesDebentures();
							BigDecimal parcelaMensal = rs.getBigDecimal("parcelamensal");
							BigDecimal capitalizacao = rs.getBigDecimal("capitalizacao");
							BigDecimal amortizacao = rs.getBigDecimal("amortizacao");
							if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
								parcelaMensal = capitalizacao.add(amortizacao);
							}
							saque.setValorSaque(parcelaMensal);
							saque.setDataSaque(rs.getDate("datavencimento"));
							saque.setCalculos(Copy(listaDatasCalculo));
							debenture.getSaques().add(saque);
						}
					}
					listaDenbentures.add(debenture);	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listaDenbentures;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<DebenturesRelatorio> getDebenturesMensal(final Date dataInicio, final Date dataFim, final boolean envelope, final List<DataCalculoDebentures> listaDatasCalculo) {
		return (List<DebenturesRelatorio>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DebenturesRelatorio> listaDenbentures = new ArrayList<DebenturesRelatorio>();
				
				/*List<DataCalculoDebentures> listaDatasCalculo2 = new ArrayList<DataCalculoDebentures>();				
				for(DataCalculoDebentures data : listaDatasCalculo) {
					DataCalculoDebentures copia = new DataCalculoDebentures(); 
					CommonsUtil.simpleCopyProperties(copia, data, null);
					listaDatasCalculo2.add(copia);
				}*/

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_PARCELAS_POR_DATA = QUERY_DEBENTURES_MENSAL;

					ps = connection.prepareStatement(query_QUERY_GET_PARCELAS_POR_DATA);		

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(ajustaDataHoraFinal(dataFim).getTime());

					for(int i = 0; i < 30; ) {
						i++;
						ps.setDate(i, dtRelInicioSQL);
						i++;
						ps.setDate(i, dtRelFimSQL);
						i++;
						ps.setBoolean(i, envelope);
					}
				
					rs = ps.executeQuery();

					DebenturesRelatorio debenture = new DebenturesRelatorio();
					String numeroContratoantigo = "numeroContrato";
					int numeroInvestidoAntigo = 99999;
					while (rs.next()) {
						if(!CommonsUtil.mesmoValor(rs.getString("numerocontrato"), numeroContratoantigo)
								|| !CommonsUtil.mesmoValor(rs.getInt("numeroinvestidor"), numeroInvestidoAntigo)) {
							debenture = new DebenturesRelatorio();
							debenture.setNome(rs.getString("nome"));
							debenture.setCpfCnpj(CommonsUtil.stringValueVazio(rs.getString("cpf")) 
									+ CommonsUtil.stringValueVazio(rs.getString("cnpj")));
							debenture.setAgenciaConta(rs.getString("agencia") + " | " + rs.getString("conta"));
							debenture.setNumerocontrato(rs.getString("numerocontrato"));
							if (rs.getBoolean("recebedorgarantido1")) {
								debenture.setGarantido("Sim");
							} else {
								debenture.setGarantido("Não");
							}
							debenture.setDataVencimento(rs.getDate("datavencimento"));
							debenture.setTaxaMensal(rs.getBigDecimal("taxaremuneracaoinvestidor1"));
							debenture.setRecebeMensal("Sim");
							BigDecimal parcelaMensal = rs.getBigDecimal("parcelamensal");
							BigDecimal capitalizacao = rs.getBigDecimal("capitalizacao");
							BigDecimal amortizacao = rs.getBigDecimal("amortizacao");
							if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
								parcelaMensal = capitalizacao.add(amortizacao);
							} 
							debenture.setValorBruto(parcelaMensal);
							debenture.setValorLiquido(rs.getBigDecimal("valorliquido"));
							debenture.setValorFace(rs.getBigDecimal("valorface"));
							debenture.setDataInicio(rs.getDate("datainicioinvestidor1"));	
							debenture.setIdContrato(rs.getLong("contrato"));
							debenture.setIdInvestidor(rs.getLong("investidor"));
							debenture.setIdParcela(rs.getLong("parcela"));
							debenture.setNumeroInvestidor(rs.getInt("numeroinvestidor"));
							numeroContratoantigo = debenture.getNumerocontrato();
							numeroInvestidoAntigo = debenture.getNumeroInvestidor();
							debenture.setCalculos(Copy(listaDatasCalculo));
							
							SaquesDebentures saque = new SaquesDebentures();
							saque.setValorSaque(rs.getBigDecimal("saldocredoratualizado"));
							saque.setDataSaque(rs.getDate("datavencimento"));
							saque.setCalculos(Copy(listaDatasCalculo));
							debenture.getSaques().add(saque);
							listaDenbentures.add(debenture);
						} 
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listaDenbentures;
			}
		});	
	}
	
	
	public Date ajustaDataHoraFinal(Date dataFinal) {
	    // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(dataFinal);
        
        c.add(Calendar.DATE, 1); 
        
        return c.getTime();
	}
	
	public List<DataCalculoDebentures> Copy(List<DataCalculoDebentures> lista){
		List<DataCalculoDebentures> listaDatasCalculo2 = new ArrayList<DataCalculoDebentures>();				
		for(DataCalculoDebentures data : lista) {
			DataCalculoDebentures copia = new DataCalculoDebentures(data); 
			listaDatasCalculo2.add(copia);
		}
		return listaDatasCalculo2;
	}

}
