package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.RegistroImovelTabela;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class RegistroImovelTabelaDao extends HibernateDao <RegistroImovelTabela,Long> {
	private static final String QUERY_REGISTRO_VALOR = "select total from cobranca.RegistroImovelTabela " + 
			"where valorMin <= ?"
			+ "and valorMax >= ?";
	
	@SuppressWarnings("unchecked")
	public BigDecimal getValorRegistro(final BigDecimal valor) {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				BigDecimal valorRetorno = BigDecimal.ZERO;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_REGISTRO_VALOR);		
	
					ps.setBigDecimal(1, valor);
					ps.setBigDecimal(2, valor);
	
					rs = ps.executeQuery();
										
					while (rs.next()) {
						valorRetorno = rs.getBigDecimal(1);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return valorRetorno;
			}
		});	
	}
	
	
	private static final String QUERY_LISTAR_REGISTROS = "select id from cobranca.RegistroImovelTabela " + 
			"where date_trunc('year', data) = date_trunc('year', ? ::timestamp) "
			+ "order by total asc";
	
	@SuppressWarnings("unchecked")
	public List<RegistroImovelTabela> listarRegistros(final Date dataHoje) {
		return (List<RegistroImovelTabela> ) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RegistroImovelTabela> retorno = new ArrayList<RegistroImovelTabela>();
				RegistroImovelTabelaDao rDao = new RegistroImovelTabelaDao();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_LISTAR_REGISTROS);		
					
					java.sql.Date dataAux = new java.sql.Date(dataHoje.getTime());
					ps.setDate(1, dataAux);
	
					rs = ps.executeQuery();
					
					while (rs.next()) {
						retorno.add(rDao.findById(rs.getLong(1)));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return retorno;
			}
		});	
	}
}
