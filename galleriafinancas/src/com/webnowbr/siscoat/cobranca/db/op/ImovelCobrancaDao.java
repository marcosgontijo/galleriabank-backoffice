package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ImovelCobrancaDao extends HibernateDao <ImovelCobranca,Long> {
		
	private String QUERY_ID_IMOVEL = "select id from reservarimovel.imovel order by nome";
	
    @SuppressWarnings("unchecked")
	public List<ImovelCobranca> relatorioImovelOrdenado() {
		return (List<ImovelCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelCobranca> objects = new ArrayList<ImovelCobranca>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ID_IMOVEL);
					
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
    
    
}
