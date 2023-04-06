package com.webnowbr.siscoat.contab.db.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.contab.db.model.BalancoPatrimonial;
import com.webnowbr.siscoat.db.dao.HibernateDao;

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
	
	private static final String QUERY_DIREITOS_CREDITORIOS =  " select sum(ccd.vlrParcela)" 
			+ " from cobranca.contratocobranca coco"
			+ " left join cobranca.contratocobranca_detalhes_join ccdj on ccdj.idcontratocobranca = coco.id"
			+ " inner join cobranca.contratocobrancadetalhes ccd on	ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false"
			+ " inner join cobranca.pagadorrecebedor pare on pare.id = coco.pagador"
			+ " where status = 'Aprovado' and ccd.id is not null and pagador not in (15, 34, 14, 182, 417, 803)";
	
	@SuppressWarnings("unchecked")
	public BigDecimal consultaDireitosCreditorios() {
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
							.prepareStatement(QUERY_DIREITOS_CREDITORIOS);
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
}
