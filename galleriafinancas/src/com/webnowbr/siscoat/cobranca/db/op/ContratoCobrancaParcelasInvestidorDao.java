package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.db.dao.*;

public class ContratoCobrancaParcelasInvestidorDao extends HibernateDao <ContratoCobrancaParcelasInvestidor,Long> {

	private static final String QUERY_GET_PARCELAS_POR_DATA =  	"select id idparcela, numerocontrato, recebedor, pagador, recebedorenvelope, empresa, recebedorgarantido, datavencimento, baixado from (" + 
			" select cp.id, c.numerocontrato, c.recebedor recebedor, c.pagador pagador, c.recebedorenvelope recebedorenvelope, empresa, c.recebedorgarantido1 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_1 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor1" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor = false " + 
			"union" + 
			"  select cp.id, c.numerocontrato, c.recebedor2 recebedor, c.pagador pagador, c.recebedorenvelope2 recebedorenvelope, empresa, c.recebedorgarantido2 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_2 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor2" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor2 = false " + 
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor3 recebedor, c.pagador pagador, c.recebedorenvelope3 recebedorenvelope, empresa, c.recebedorgarantido3 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_3 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor3" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor3 = false " + 
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor4 recebedor, c.pagador pagador, c.recebedorenvelope4 recebedorenvelope, empresa, c.recebedorgarantido4 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_4 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor4" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor4 = false " + 
			" union" + 
			"  select cp.id, c.numerocontrato, c.recebedor5 recebedor, c.pagador pagador, c.recebedorenvelope5 recebedorenvelope, empresa, c.recebedorgarantido5 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_5 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor5" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " +
			" and c.ocultarecebedor5 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor6 recebedor, c.pagador pagador, c.recebedorenvelope6 recebedorenvelope, empresa, c.recebedorgarantido6 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_6 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor6" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor6 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor7 recebedor, c.pagador pagador, c.recebedorenvelope7 recebedorenvelope, empresa, c.recebedorgarantido7 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_7 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor7" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor7 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor8 recebedor, c.pagador pagador, c.recebedorenvelope8 recebedorenvelope, empresa, c.recebedorgarantido8 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_8 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor8" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
			" and c.ocultarecebedor8 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor9 recebedor, c.pagador pagador, c.recebedorenvelope9 recebedorenvelope, empresa, c.recebedorgarantido9 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_9 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor9" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " +
			" and c.ocultarecebedor9 = false " + 
			"  union" + 
			"  select cp.id, c.numerocontrato, c.recebedor10 recebedor, c.pagador pagador, c.recebedorenvelope10 recebedorenvelope, empresa, c.recebedorgarantido10 recebedorgarantido, cp.datavencimento datavencimento, cp.baixado baixado from cobranca.contratocobranca_parcelas_investidor_join_10 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor10" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.datavencimento >= ? ::timestamp  " + 
			" and cp.datavencimento < ? ::timestamp " + 
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
					java.sql.Date dtRelFimSQL = new java.sql.Date(ajustaDataHoraFinal(dataFim).getTime());

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
						contratoCobrancaParcelasInvestidor.setInvestidorGarantido(rs.getBoolean(7));
						
						// VERIFICA SE HÁ PARCELAS DE CONTRATO VENCIDA NO CONTRATO EM QUE O 
						// INVESTIDOR FAZ PARTE
						// SE galleria financas SA parcelas sempre em dia - ID 14
						if (contratoCobrancaParcelasInvestidor.getPagador().getId() == 14) {
							contratoCobrancaParcelasInvestidor.setParcelaContratoVencida(false);
						} else {
							contratoCobrancaParcelasInvestidor.setParcelaContratoVencida(verificaParcelasVencidaContrato(contratoCobrancaParcelasInvestidor.getNumeroContrato()));
						}
						
						parcelas.add(contratoCobrancaParcelasInvestidor);								
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return parcelas;
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
	public boolean verificaParcelasVencidaContrato(String numeroContrato) {
		ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
		List<ContratoCobrancaDetalhes> ccDetalhes = new ArrayList<ContratoCobrancaDetalhes>();
		
		// get parcelas do contrato do investidor
		ccDetalhes = ccDao.getParcelasContratoPorNumeroContrato(numeroContrato);
		
		// verifica se há parcelas vencidas no contrato como
		boolean parcelaVencida = false;
		
		for (ContratoCobrancaDetalhes ccd : ccDetalhes) {
			//parcelaVencida = verificaSeParcelaVencida(ccd.getDataVencimento(), ccd.isParcelaPaga()); 
			
			Locale locale = new Locale("pt", "BR");  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
			SimpleDateFormat month = new SimpleDateFormat("MM", locale);
			
			Date dataHoje = getDataHoje();
			String datahojeStr = sdf.format(dataHoje.getTime());
			String dataParcelaStr = sdf.format(ccd.getDataVencimento());
			
			// pega o mes para verificar se é a parcela anterior
			long mesHoje = Long.valueOf(month.format(dataHoje));
			long mesParcela = Long.valueOf(month.format(ccd.getDataVencimento()));
		
			Date dataParcela;
			
			try {
				dataHoje = sdf.parse(datahojeStr);
				dataParcela = sdf.parse(dataParcelaStr);
			
				if (dataParcela.before(dataHoje) && !ccd.isParcelaPaga()) {	
					// valida se a parcela é do mês anterior
					if (mesParcela != mesHoje) {
						parcelaVencida = true;
						break;
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return parcelaVencida;
	}
	
	public Date getDataHoje() {
		TimeZone zone = TimeZone.getDefault(); 
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHojeCalendar = Calendar.getInstance(zone, locale);
		Date dataHoje = dataHojeCalendar.getTime();
		
		return dataHoje;
	}
	
	public boolean verificaSeParcelaVencida(Date dataVencimento, boolean parcelaPaga) {
		Locale locale = new Locale("pt", "BR");  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		
		String datahojeStr = sdf.format(getDataHoje().getTime());
		String dataParcelaStr = sdf.format(dataVencimento);
		
		Date dataHoje;
		Date dataParcela;
		
		try {
			dataHoje = sdf.parse(datahojeStr);
			dataParcela = sdf.parse(dataParcelaStr);
			
			if (dataParcela.before(dataHoje) && !parcelaPaga) {				
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
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
	
	private static final String QUERY_GET_PARCELAS_POR_DATA_BAIXADO_INFORME =  	"select id idparcela, numerocontrato, recebedor from (" + 
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
			" order by numerocontrato";

	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaParcelasInvestidor> getParcelasPorDataInvestidorBaixadasInforme(final Date dataInicio, final Date dataFim, final long idInvestidor) {
		return (List<ContratoCobrancaParcelasInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaParcelasInvestidor> parcelas = new ArrayList<ContratoCobrancaParcelasInvestidor>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_PARCELAS_POR_DATA_BAIXADO = QUERY_GET_PARCELAS_POR_DATA_BAIXADO_INFORME;

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

							if (idInvestidor > 0) {
								if (contratoCobrancaParcelasInvestidor.getInvestidor().getId() == idInvestidor) {
									parcelas.add(contratoCobrancaParcelasInvestidor);	
								}
							} else {
								parcelas.add(contratoCobrancaParcelasInvestidor);	
							}					
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return parcelas;
			}
		});	
	}
	
	private static final String QUERY_GET_PARCELAS_POR_DATA_IR =  	"select id idparcela, numerocontrato, recebedor from (" + 
			" select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor recebedor from cobranca.contratocobranca_parcelas_investidor_join_1 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor1" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			"union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor2 recebedor from cobranca.contratocobranca_parcelas_investidor_join_2 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor2" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor3 recebedor from cobranca.contratocobranca_parcelas_investidor_join_3 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor3" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor4 recebedor from cobranca.contratocobranca_parcelas_investidor_join_4 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor4" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor5 recebedor from cobranca.contratocobranca_parcelas_investidor_join_5 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor5" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			"  union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor6 recebedor from cobranca.contratocobranca_parcelas_investidor_join_6 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor6" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			"  union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor7 recebedor from cobranca.contratocobranca_parcelas_investidor_join_7 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor7" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			"  union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor8 recebedor from cobranca.contratocobranca_parcelas_investidor_join_8 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor8" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			"  union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor9 recebedor from cobranca.contratocobranca_parcelas_investidor_join_9 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor9" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			"  union" + 
			"  select cp.id, cp.saldocredoratualizado, c.numerocontrato, c.recebedor10 recebedor from cobranca.contratocobranca_parcelas_investidor_join_10 cj" + 
			" inner join cobranca.contratocobranca c on c.id = cj.idcontratocobrancaparcelasinvestidor10" + 
			" inner join cobranca.contratocobrancaparcelasinvestidor cp on cp.id = cj.idcontratocobrancaparcelasinvestidor" + 
			" where cp.databaixa >= ? ::timestamp  " + 
			" and cp.databaixa <= ? ::timestamp " + 
			" ) investidores" + 
			" where recebedor = ? " + 
			" order by numerocontrato";

	@SuppressWarnings("unchecked")
	public BigDecimal getParcelasPorDataInvestidorIR(final Date dataInicio, final Date dataFim, final long idInvestidor) {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaParcelasInvestidor> parcelas = new ArrayList<ContratoCobrancaParcelasInvestidor>();
				BigDecimal valorPeriodo = BigDecimal.ZERO;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				ContratoCobrancaParcelasInvestidor contratoCobrancaParcelasInvestidor = new ContratoCobrancaParcelasInvestidor();
				ContratoCobrancaParcelasInvestidor contratoCobrancaParcelasInvestidorTmp = new ContratoCobrancaParcelasInvestidor();
				
				try {
					connection = getConnection();

					String query_QUERY_GET_PARCELAS_POR_DATA_BAIXADO = QUERY_GET_PARCELAS_POR_DATA_IR;

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
					
					ps.setLong(21, idInvestidor);
					
					rs = ps.executeQuery();

					ContratoCobrancaParcelasInvestidorDao contratoCobrancaParcelasInvestidorDao = new ContratoCobrancaParcelasInvestidorDao();
					PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();					
					
					String numeroContrato = "";
					int numeroParcela = 0;					
					
					while (rs.next()) {
						contratoCobrancaParcelasInvestidor = contratoCobrancaParcelasInvestidorDao.findById(rs.getLong(1));
						contratoCobrancaParcelasInvestidor.setNumeroContrato(rs.getString(2));
						contratoCobrancaParcelasInvestidor.setInvestidor(pagadorRecebedorDao.findById(rs.getLong(3)));
						
						if (contratoCobrancaParcelasInvestidor.getIrRetido() != null && !contratoCobrancaParcelasInvestidor.getIrRetido().toString().equals("0.00")) {	
							// verifica se é a primeira vez que passa no loop,s e sim armazena dados
							if (numeroContrato.equals("") && numeroParcela == 0) {
								numeroContrato = contratoCobrancaParcelasInvestidor.getNumeroContrato();
								numeroParcela = Integer.valueOf(contratoCobrancaParcelasInvestidor.getNumeroParcela());
								contratoCobrancaParcelasInvestidorTmp = contratoCobrancaParcelasInvestidor;
							} else {
								// se não, verifica se é o mesmo contrato
								if (numeroContrato.equals(contratoCobrancaParcelasInvestidor.getNumeroContrato())) {
									// se sim, verifica se a parcela é maior ou menor
									if (Integer.valueOf(contratoCobrancaParcelasInvestidor.getNumeroParcela()) > numeroParcela) {
										numeroContrato = contratoCobrancaParcelasInvestidor.getNumeroContrato();
										numeroParcela = Integer.valueOf(contratoCobrancaParcelasInvestidor.getNumeroParcela());
										contratoCobrancaParcelasInvestidorTmp = contratoCobrancaParcelasInvestidor;
									}									
								} else {
									// se mudou o contrato popula valor e coleta novo contrato
									if (idInvestidor > 0) {
										if (contratoCobrancaParcelasInvestidorTmp.getInvestidor().getId() == idInvestidor) {
											parcelas.add(contratoCobrancaParcelasInvestidorTmp);	
										}
									} else {
										parcelas.add(contratoCobrancaParcelasInvestidorTmp);	
									}
									// popula variaveis com os dados do novo contrato
									numeroContrato = contratoCobrancaParcelasInvestidor.getNumeroContrato();
									numeroParcela = Integer.valueOf(contratoCobrancaParcelasInvestidor.getNumeroParcela());
									contratoCobrancaParcelasInvestidorTmp = contratoCobrancaParcelasInvestidor;
								}
							}
						}						
					}
				} finally {	
					if (idInvestidor > 0) {
						if (contratoCobrancaParcelasInvestidorTmp.getInvestidor() != null) {
							if (contratoCobrancaParcelasInvestidorTmp.getInvestidor().getId() == idInvestidor) {
								parcelas.add(contratoCobrancaParcelasInvestidorTmp);	
							}
						}
					} else {
						parcelas.add(contratoCobrancaParcelasInvestidorTmp);	
					}
					
					closeResources(connection, ps, rs);					
				}
				
				//sumariza parcelas e retorna o valor no período
				for (ContratoCobrancaParcelasInvestidor parcelaInvestidor : parcelas) {
					valorPeriodo = valorPeriodo.add(parcelaInvestidor.getSaldoCredorAtualizado());
				}
				return valorPeriodo;
			}
		});	
	}
}
