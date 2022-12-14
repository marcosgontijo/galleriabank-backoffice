package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorAdicionais;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class PagadorRecebedorAdicionaisDao extends HibernateDao <PagadorRecebedorAdicionais,Long> {
	
	private static final String QUERY_GET_PAGADORES_ADICIONAIS = "select * from cobranca.pagadorrecebedoradicionais " + 
			"where pessoa = ? ";
	
	@SuppressWarnings("unchecked")
	public List<PagadorRecebedorAdicionais> getPagadorAdicionaisPessoa(final long pessoa) {
		return (List<PagadorRecebedorAdicionais>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				final List<PagadorRecebedorAdicionais> pagadorRecebedorAdicionais = new ArrayList<PagadorRecebedorAdicionais>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_GET_PAGADORES_ADICIONAIS);		
	
					ps.setLong(1, pessoa);
	
					rs = ps.executeQuery();
					
					PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
					while (rs.next()) {
						PagadorRecebedor pagador = pagadorRecebedorDao.findById(pessoa);
						PagadorRecebedorAdicionais pagadorAdicionais = new PagadorRecebedorAdicionais();
						pagadorAdicionais.setId(rs.getLong("id"));
						pagadorAdicionais.setPessoa(new PagadorRecebedor());
						pagadorAdicionais.setPessoa(pagador);
						pagadorRecebedorAdicionais.add(pagadorAdicionais);
					}
										
				} finally {
					closeResources(connection, ps, rs);					
				}
				return pagadorRecebedorAdicionais;
			}
		});	
	}
	
}
