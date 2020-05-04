package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.db.dao.*;

public class ContratoCobrancaParcelasInvestidorDao extends HibernateDao <ContratoCobrancaParcelasInvestidor,Long> {

	private static final String QUERY_GET_PARCELAS_POR_DATA =  	"select id idparcela, numerocontrato, recebedor, pagador, recebedorenvelope, empresa from (" + 
			" select cp.id, c.numerocontrato, c.recebedor recebedor, c.pagador pagador, c.recebedorenvelope recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_1 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor1" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor = false " + 
			"union" + 
			"  select cp.id, c.numerocontrato, c.recebedor2 recebedor, c.pagador pagador, c.recebedorenvelope2 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_2 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor2" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor2 = false " + 
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor3 recebedor, c.pagador pagador, c.recebedorenvelope3 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_3 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor3" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor3 = false " + 
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor4 recebedor, c.pagador pagador, c.recebedorenvelope4 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_4 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor4" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor4 = false " + 
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor5 recebedor, c.pagador pagador, c.recebedorenvelope5 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_5 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor5" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " +
			" and c.ocultarecebedor5 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor6 recebedor, c.pagador pagador, c.recebedorenvelope6 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_6 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor6" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor6 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor7 recebedor, c.pagador pagador, c.recebedorenvelope7 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_7 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor7" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor7 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor8 recebedor, c.pagador pagador, c.recebedorenvelope8 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_8 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor8" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor8 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor9 recebedor, c.pagador pagador, c.recebedorenvelope9 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_9 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor9" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " +
			" and c.ocultarecebedor9 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor10 recebedor, c.pagador pagador, c.recebedorenvelope10 recebedorenvelope, empresa from cobranca.contratocobranca_parcelas_investidor_join_10 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor10" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento <= ? ::timestamp " + 
			" and c.ocultarecebedor10 = false " + 
			" ) investidores" + 
			" where recebedor not in (14,15,34) " + 
			" order by recebedor";

	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaParcelasInvestidor> getParcelasPorDataInvestidor(final Date dataInicio, final Date dataFim) {
		return (List<ContratoCobrancaParcelasInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaParcelasInvestidor> parcelas = new ArrayList<ContratoCobrancaParcelasInvestidor>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_PARCELAS_POR_DATA = QUERY_GET_PARCELAS_POR_DATA;

					ps = connection
							.prepareStatement(query_QUERY_GET_PARCELAS_POR_DATA);		

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

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
					ps.setDate(11, dtRelInicioSQL);
					ps.setDate(12, dtRelFimSQL);
					ps.setDate(13, dtRelInicioSQL);
					ps.setDate(14, dtRelFimSQL);
					ps.setDate(15, dtRelInicioSQL);
					ps.setDate(16, dtRelFimSQL);
					ps.setDate(17, dtRelInicioSQL);
					ps.setDate(18, dtRelFimSQL);
					ps.setDate(19, dtRelInicioSQL);
					ps.setDate(20, dtRelFimSQL);
					
					rs = ps.executeQuery();

					ContratoCobrancaParcelasInvestidorDao contratoCobrancaParcelasInvestidorDao = new ContratoCobrancaParcelasInvestidorDao();
					PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
					ContratoCobrancaParcelasInvestidor contratoCobrancaParcelasInvestidor = new ContratoCobrancaParcelasInvestidor();
					
					while (rs.next()) {
						contratoCobrancaParcelasInvestidor = contratoCobrancaParcelasInvestidorDao.findById(rs.getLong(1));
						contratoCobrancaParcelasInvestidor.setNumeroContrato(rs.getString(2));
						contratoCobrancaParcelasInvestidor.setInvestidor(pagadorRecebedorDao.findById(rs.getLong(3)));
						contratoCobrancaParcelasInvestidor.setPagador(pagadorRecebedorDao.findById(rs.getLong(4)));
						contratoCobrancaParcelasInvestidor.setEnvelope(rs.getBoolean(5));
						contratoCobrancaParcelasInvestidor.setEmpresa(rs.getString(6));

						parcelas.add(contratoCobrancaParcelasInvestidor);								
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return parcelas;
			}
		});	
	}
	
	private static final String QUERY_GET_PARCELAS_POR_DATA_BAIXADO =  	"select id idparcela, numerocontrato, recebedor from (" + 
			" select cp.id, c.numerocontrato, c.recebedor recebedor from cobranca.contratocobranca_parcelas_investidor_join_1 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor1" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			"union" + 
			"  select cp.id, c.numerocontrato, c.recebedor2 recebedor from cobranca.contratocobranca_parcelas_investidor_join_2 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor2" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor3 recebedor from cobranca.contratocobranca_parcelas_investidor_join_3 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor3" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor4 recebedor from cobranca.contratocobranca_parcelas_investidor_join_4 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor4" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor5 recebedor from cobranca.contratocobranca_parcelas_investidor_join_5 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor5" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor6 recebedor from cobranca.contratocobranca_parcelas_investidor_join_6 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor6" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor7 recebedor from cobranca.contratocobranca_parcelas_investidor_join_7 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor7" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor8 recebedor from cobranca.contratocobranca_parcelas_investidor_join_8 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor8" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor9 recebedor from cobranca.contratocobranca_parcelas_investidor_join_9 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor9" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor10 recebedor from cobranca.contratocobranca_parcelas_investidor_join_10 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor10" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" and baixado = true " +
			" ) investidores" + 
			" order by recebedor";

	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaParcelasInvestidor> getParcelasPorDataInvestidorBaixadas(final Date dataInicio, final Date dataFim, final long idInvestidor) {
		return (List<ContratoCobrancaParcelasInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaParcelasInvestidor> parcelas = new ArrayList<ContratoCobrancaParcelasInvestidor>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_PARCELAS_POR_DATA_BAIXADO = QUERY_GET_PARCELAS_POR_DATA_BAIXADO;

					ps = connection
							.prepareStatement(query_QUERY_GET_PARCELAS_POR_DATA_BAIXADO);		

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

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
					ps.setDate(11, dtRelInicioSQL);
					ps.setDate(12, dtRelFimSQL);
					ps.setDate(13, dtRelInicioSQL);
					ps.setDate(14, dtRelFimSQL);
					ps.setDate(15, dtRelInicioSQL);
					ps.setDate(16, dtRelFimSQL);
					ps.setDate(17, dtRelInicioSQL);
					ps.setDate(18, dtRelFimSQL);
					ps.setDate(19, dtRelInicioSQL);
					ps.setDate(20, dtRelFimSQL);
					
					rs = ps.executeQuery();

					ContratoCobrancaParcelasInvestidorDao contratoCobrancaParcelasInvestidorDao = new ContratoCobrancaParcelasInvestidorDao();
					PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
					ContratoCobrancaParcelasInvestidor contratoCobrancaParcelasInvestidor = new ContratoCobrancaParcelasInvestidor();
					
					while (rs.next()) {
						contratoCobrancaParcelasInvestidor = contratoCobrancaParcelasInvestidorDao.findById(rs.getLong(1));
						contratoCobrancaParcelasInvestidor.setNumeroContrato(rs.getString(2));
						contratoCobrancaParcelasInvestidor.setInvestidor(pagadorRecebedorDao.findById(rs.getLong(3)));

						if (contratoCobrancaParcelasInvestidor.getIrRetido() != null && !contratoCobrancaParcelasInvestidor.getIrRetido().toString().equals("0.00")) {
							if (idInvestidor > 0) {
								if (contratoCobrancaParcelasInvestidor.getInvestidor().getId() == idInvestidor) {
									parcelas.add(contratoCobrancaParcelasInvestidor);	
								}
							} else {
								parcelas.add(contratoCobrancaParcelasInvestidor);	
							}
						}						
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return parcelas;
			}
		});	
	}
}
