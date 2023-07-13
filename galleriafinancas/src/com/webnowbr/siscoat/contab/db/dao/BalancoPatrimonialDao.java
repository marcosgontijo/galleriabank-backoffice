package com.webnowbr.siscoat.contab.db.dao;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.contab.db.model.BalancoPatrimonial;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioBalanco;

public class BalancoPatrimonialDao extends HibernateDao <BalancoPatrimonial,Long> {
	
	private static final String QUERY_BALANCO_PATRIMONIAL =  "select br.id from contab.balanco_patrimonial br ";
	
	@SuppressWarnings("unchecked")
	public List<BalancoPatrimonial> consultaBalancoPatrimonial() {
		return (List<BalancoPatrimonial>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BalancoPatrimonial> objects = new ArrayList<BalancoPatrimonial>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
																	
					ps = connection
							.prepareStatement(QUERY_BALANCO_PATRIMONIAL);
					rs = ps.executeQuery();
					
					BalancoPatrimonial balancoPatrimonial = new BalancoPatrimonial();
					
					while (rs.next()) {
						balancoPatrimonial = findById(rs.getLong(1));
						
						objects.add(balancoPatrimonial);						
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_ULTIMO_BALANCO =  " select max(id)\r\n"
			+ "from contab.balanco_patrimonial bp " ;
	
	public BalancoPatrimonial consultaUltimoBalanco() {
		return (BalancoPatrimonial) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {connection = getConnection();
				
					ps = connection.prepareStatement(QUERY_ULTIMO_BALANCO);
					rs = ps.executeQuery();
							
					BalancoPatrimonial balancoPatrimonial = new BalancoPatrimonial();
							
					while (rs.next()) {
							balancoPatrimonial = findById(rs.getLong(1));
							return balancoPatrimonial;						
							}
							
					} finally {
							closeResources(connection, ps, rs);					
							}
					return null;
					}
		});	
	}
			
	private static final String QUERY_PAGAR =  " select sum(parcelaMensal)"
			+ " from (select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor1, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, Coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_1 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor1 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false'	union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_2 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor2 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor3, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_3 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor3 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on 	pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id,coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor4, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA,ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_4 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor4 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where Status = 'Aprovado'and ccpi.id is not null\r\n"
			+ "			and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id,	coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor5,\r\n"
			+ "			coco.empresa, ccpi.dataVencimento, ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal,\r\n"
			+ "			pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_5 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor5 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor6, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_6 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor6 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor7, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_7 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor7 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor8, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_8 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor8 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor9, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_9 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor9 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' union all\r\n"
			+ "			select coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor10, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "			ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "			from cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "			inner join cobranca.contratocobranca_parcelas_investidor_join_10 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "			inner join cobranca.contratocobranca coco on parcelaJoin.idcontratocobrancaparcelasinvestidor10 = coco.id\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare on pare.id = ccpi.investidor\r\n"
			+ "			where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false') TOTAL";

	public BigDecimal consultaContasPagar() {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				BigDecimal objects = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
																	
					ps = connection
							.prepareStatement(QUERY_PAGAR);
					rs = ps.executeQuery();
					rs.next();
					
					objects = rs.getBigDecimal(1);
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});
	}
	
	private static final String QUERY_SOMA_PARCELA_X =  "select sum(parcelaMensal) as somaParcela, sum(taxaremuneracaoinvestidor1 * parcelaMensal) as fatorX  "
			+ "from (select	coco.id,\r\n"
			+ "coco.numerocontrato,	coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor1,\r\n"
			+ "coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador,\r\n"
			+ "coco.corrigidoIPCA, ccpi.id ccpiid from\r\n"
			+ "cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_1 parcelaJoin on\r\n"
			+ "ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "parcelaJoin.idcontratocobrancaparcelasinvestidor1 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado'	and ccpi.id is not null	and ccpi.baixado = 'false' \r\n"
			+ "and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor2,	coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao,	ccpi.capitalizacao, ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_2 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor2 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null	and ccpi.baixado = 'false'\r\n"
			+ "and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor3,	coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao,	ccpi.capitalizacao, ccpi.parcelaMensal,	pare.nome,	coco.pagador,	coco.corrigidoIPCA,\r\n"
			+ "ccpi.id ccpiid from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_3 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor3 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null	and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor4,	coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao,	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome,	coco.pagador,	coco.corrigidoIPCA,\r\n"
			+ "ccpi.id ccpiid from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_4 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor4 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null	and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor5,	coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_5 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor5 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor6, coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_6 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor6 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null	and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor7, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador,	coco.corrigidoIPCA,\r\n"
			+ "ccpi.id ccpiid from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_7 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor7 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null	and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor8, coco.empresa, ccpi.dataVencimento,\r\n"
			+ "ccpi.amortizacao,	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_8 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor8 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado'	and ccpi.id is not null	and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id,	coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor9,	coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao,	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome,	coco.pagador,	coco.corrigidoIPCA,ccpi.id ccpiid\r\n"
			+ "from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_9 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor9 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor where	status = 'Aprovado'	and ccpi.id is not null	and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)\r\n"
			+ "union all select	coco.id,	coco.numerocontrato,coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor10,	coco.empresa,\r\n"
			+ "ccpi.dataVencimento,	ccpi.amortizacao, ccpi.capitalizacao, ccpi.parcelaMensal, pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_10 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on	parcelaJoin.idcontratocobrancaparcelasinvestidor10 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on	pare.id = ccpi.investidor\r\n"
			+ "where status = 'Aprovado' and ccpi.id is not null and ccpi.baixado = 'false' and ccpi.investidor not in (15, 34, 14, 182, 417, 803)) total";

	public BigDecimal somaParcelaX() {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				BigDecimal objects = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
																	
					ps = connection.prepareStatement(QUERY_SOMA_PARCELA_X);
					rs = ps.executeQuery();
				
				while (rs.next()) {	
					BigDecimal SomaParcela = (rs.getBigDecimal(1));
					BigDecimal FatorX = (rs.getBigDecimal(2));
					BigDecimal CustoPonderado = (FatorX.divide(SomaParcela,MathContext.DECIMAL128));
					objects = CustoPonderado;
					System.out.println("Custo ponderado: " +CustoPonderado);
				}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});
	}
	
	private static final String QUERY_CONTRATOS_RECEBER_BALANCO = "select coco.id, numerocontrato, txJurosParcelas, empresa, ccd.dataVencimento, ccd.vlrParcela, pare.nome, corrigidoIPCA, corrigidonovoipca\r\n"
			+ "			from cobranca.contratocobranca coco\r\n"
			+ "			left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id\r\n"
			+ "			inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false\r\n"
			+ "			inner join cobranca.pagadorrecebedor pare ON pare.id = coco.pagador\r\n"
			+ "			where status = 'Aprovado' and ccd.id is not null and pagador not in (15, 34,14, 182, 417, 803) and ccd.vlrparcela is not null\r\n"
			+ "			ORDER BY numerocontrato asc, datavencimento asc";

	private static final String QUERY_CONTRATOS_PAGAR_FAVORECIDO_BALANCO = "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas, coco.taxaremuneracaoinvestidor1, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_1 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor1 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_2 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor2 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_3 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor3 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_4 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor4 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_5 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor5 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_6 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor6 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_7 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor7 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_8 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor8 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_9 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor9 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "union all\r\n"
			+ "select\r\n"
			+ "	coco.id, coco.numerocontrato, coco.txJurosParcelas,	coco.taxaremuneracaoinvestidor2, coco.empresa, ccpi.dataVencimento, ccpi.amortizacao,\r\n"
			+ "	ccpi.capitalizacao,	ccpi.parcelaMensal,	pare.nome, coco.pagador, coco.corrigidoIPCA, ccpi.id ccpiid\r\n"
			+ "from\r\n"
			+ "	cobranca.contratocobrancaparcelasinvestidor ccpi\r\n"
			+ "inner join cobranca.contratocobranca_parcelas_investidor_join_10 parcelaJoin on\r\n"
			+ "	ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor\r\n"
			+ "inner join cobranca.contratocobranca coco on\r\n"
			+ "	parcelaJoin.idcontratocobrancaparcelasinvestidor10 = coco.id\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on\r\n"
			+ "	pare.id = ccpi.investidor\r\n"
			+ "where\r\n"
			+ "	status = 'Aprovado'\r\n"
			+ "	and ccpi.id is not null\r\n"
			+ "	and ccpi.baixado = 'false'\r\n"
			+ "	and ccpi.parcelaMensal is not null\r\n"
			+ "order by\r\n"
			+ "	numerocontrato asc,\r\n"
			+ "	nome asc,\r\n"
			+ "	datavencimento asc";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioBalanco> listaRelatorioReceberBalanco() {
		return (List<RelatorioBalanco>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<RelatorioBalanco> objects = new ArrayList<RelatorioBalanco>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_RECEBER_BALANCO);


					rs = ps.executeQuery();
					RelatorioBalanco relatorio = null;

					while (rs.next()) {
						relatorio = new RelatorioBalanco();
						relatorio.setNumeroContratoRelatorio(rs.getString("numerocontrato"));
						relatorio.setNomePagadorRelatorio(rs.getString("nome"));
						relatorio.setDataVencimentoRelatorio(rs.getDate("dataVencimento"));
						relatorio.setValorContratoRelatorio(rs.getBigDecimal("vlrParcela"));
						relatorio.setTaxaContratoRelatorio(rs.getBigDecimal("txJurosParcelas"));
						relatorio.setEmpresaContratoRelatorio(rs.getString("empresa"));
						if (rs.getBoolean("corrigidoIPCA") || rs.getBoolean("corrigidonovoipca")) {
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
	public List<RelatorioBalanco> listaRelatorioPagarBalanco() {
		return (List<RelatorioBalanco>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<RelatorioBalanco> objects = new ArrayList<RelatorioBalanco>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_PAGAR_FAVORECIDO_BALANCO);

					rs = ps.executeQuery();
					RelatorioBalanco relatorio = null;

					while (rs.next()) {
						relatorio = new RelatorioBalanco();
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

	}
