package com.webnowbr.siscoat.common.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.common.db.model.BoletosRemessa;
import com.webnowbr.siscoat.db.dao.*;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class BoletosRemessaDao extends HibernateDao <BoletosRemessa,Long> {
	
	private static final String QUERY_ULTIMO_SEQ_REMESSA = "select nextval('cobranca.cobranca_seq_remessa')" ;
	
	private static final String QUERY_BOLETOS_NAO_REGISTRADOS = "select id from cobranca.boletosremessa where geradoremessa = false";
	
	private static final String QUERY_BOLETOS_GERADOS_PERIODO =  "select br.id from cobranca.boletosremessa br " 
			+ "where br.dtemissao >= ? ::timestamp "
			+ "and br.dtemissao <= ? ::timestamp "
			+ "and geradoremessa = false ";
	
	private static final String QUERY_BOLETOS_GERADOS_CONTRATO =  "select br.id from cobranca.boletosremessa br " 
			+ "where br.numerocontrato = ? "
			+ "and geradoremessa = false ";
	
	@SuppressWarnings("unchecked")
	public String ultimoSequencialRemessa() {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String object = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ULTIMO_SEQ_REMESSA);				
	
					rs = ps.executeQuery();
					rs.next();
					
					object = rs.getString(1);
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return object;
			}
		});	
	}
	
    @SuppressWarnings("unchecked")
	public List<BoletosRemessa> getBoletosNaoRegistrados() {
		return (List<BoletosRemessa>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BoletosRemessa> objects = new ArrayList<BoletosRemessa>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_BOLETOS_NAO_REGISTRADOS);
					
					rs = ps.executeQuery();
					
					while (rs.next()) {
						objects.add(findById(rs.getLong(1)));	
					}
							
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});
	}	
    
	@SuppressWarnings("unchecked")
	public List<BoletosRemessa> consultaBoletoContrato(final String contrato) {
		return (List<BoletosRemessa>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BoletosRemessa> objects = new ArrayList<BoletosRemessa>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
																	
					ps = connection
							.prepareStatement(QUERY_BOLETOS_GERADOS_CONTRATO);
	
					ps.setString(1, contrato);	
					
					rs = ps.executeQuery();
					
					BoletosRemessa boletosRemessa = new BoletosRemessa();
					
					while (rs.next()) {
						boletosRemessa = findById(rs.getLong(1));
						
						objects.add(new BoletosRemessa(boletosRemessa.getSistema(), boletosRemessa.getNumeroContrato(), boletosRemessa.getParcela(),
								boletosRemessa.getDtVencimento(), boletosRemessa.getDtEmissao(), boletosRemessa.getValor(), boletosRemessa.getDocumento(),
								boletosRemessa.getNomeSacado(), boletosRemessa.getEndereco(), boletosRemessa.getBairro(), boletosRemessa.getCep(),
								boletosRemessa.getCidade(), boletosRemessa.getUf(), boletosRemessa.getGeradoRemessa(), boletosRemessa.getId()));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<BoletosRemessa> consultaBoletoPeriodo(final Date dtRelInicio, final Date dtRelFim) {
		return (List<BoletosRemessa>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BoletosRemessa> objects = new ArrayList<BoletosRemessa>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
																	
					ps = connection
							.prepareStatement(QUERY_BOLETOS_GERADOS_PERIODO);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
					rs = ps.executeQuery();
					
					BoletosRemessa boletosRemessa = new BoletosRemessa();
					
					while (rs.next()) {
						boletosRemessa = findById(rs.getLong(1));
						
						objects.add(new BoletosRemessa(boletosRemessa.getSistema(), boletosRemessa.getNumeroContrato(), boletosRemessa.getParcela(),
								boletosRemessa.getDtVencimento(), boletosRemessa.getDtEmissao(), boletosRemessa.getValor(), boletosRemessa.getDocumento(),
								boletosRemessa.getNomeSacado(), boletosRemessa.getEndereco(), boletosRemessa.getBairro(), boletosRemessa.getCep(),
								boletosRemessa.getCidade(), boletosRemessa.getUf(), boletosRemessa.getGeradoRemessa(), boletosRemessa.getId()));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
}
