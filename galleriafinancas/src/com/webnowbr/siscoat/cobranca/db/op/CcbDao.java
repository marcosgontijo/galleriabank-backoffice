package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.webnowbr.siscoat.cobranca.db.model.CcbContrato;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class CcbDao extends HibernateDao <CcbContrato,Long> {
	
	private static final String QUERY_CONSULTA_CCBS =  " select "
			+ "	ccb.id, ccb.numeroCcb,  ccb.numeroOperacao, ccb.nomeEmitente "
			+ " FROM "
			+ "	cobranca.ccbcontrato ccb ";
	
	private static final String QUERY_CONSULTA_CCB_CONTRATO =  " select "
			+ "	ccb.id FROM "
			+ "	cobranca.ccbcontrato ccb "
			+ " where objetoContratoCobranca = ?";
	
	@SuppressWarnings("unchecked")
	public List<CcbContrato> ConsultaCCBs() {
		return (List<CcbContrato>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<CcbContrato> objects = new ArrayList<CcbContrato>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONSULTA_CCBS);

					rs = ps.executeQuery();
					while (rs.next()) {						CcbContrato ccbContrato = new CcbContrato();
						ccbContrato.setId(rs.getLong("id"));
						ccbContrato.setNumeroCcb(rs.getString("numeroccb"));
						ccbContrato.setNumeroOperacao(rs.getString("numeroOperacao"));
						ccbContrato.setNomeEmitente(rs.getString("nomeEmitente"));
						objects.add(ccbContrato);	
															
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public CcbContrato ConsultaCcbPorContrato(ContratoCobranca contrato) {
		return (CcbContrato) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				CcbContrato object = new CcbContrato();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONSULTA_CCBS);
					ps.setLong(1, contrato.getId());
					rs = ps.executeQuery();
					while (rs.next()) {
						CcbDao ccbDao = new CcbDao();
						object = ccbDao.findById(rs.getLong("id"));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return object;
			}
		});	
	}	
	

	private static final String QUERY_CONSULTA_TESTEMUNHAS= "select id, nome, cpf, rg from cobranca.pagadorrecebedor p ";
	
	@SuppressWarnings("unchecked")
	public PagadorRecebedor ConsultaTestemunha(Long id) {
		return (PagadorRecebedor) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedor object = new PagadorRecebedor();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query = QUERY_CONSULTA_TESTEMUNHAS;
				query = query + " where p.id = " + id;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();
					while (rs.next()) {				
						object.setId(rs.getLong("id"));		
						object.setNome(rs.getString("nome"));
						object.setCpf(rs.getString("cpf"));
						object.setRg(rs.getString("rg"));
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return object;
			}
		});	
	}	
	
	private static final String QUERY_CONSULTA_SERIECCB = "select distinct length(serieccb),serieccb from cobranca.ccbcontrato c \r\n"
			+ "where serieccb != ''\r\n"
			+ "and serieccb is not null\r\n"
			+ "order by length(serieccb) desc,serieccb desc";
	
	@SuppressWarnings("unchecked")
	public String ultimaSerieCCB() {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String retorno = "";
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query = QUERY_CONSULTA_SERIECCB;
				
				try {
					connection = getConnection();
					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();
					rs.next();
					retorno = rs.getString("serieccb");
					
				} finally {
					closeResources(connection, ps, rs);					
				}
				return retorno;
			}
		});	
	}	
	
}
