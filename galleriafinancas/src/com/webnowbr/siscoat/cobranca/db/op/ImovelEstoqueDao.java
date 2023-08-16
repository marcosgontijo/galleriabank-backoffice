package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.CcbContrato;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ImovelEstoqueDao extends HibernateDao <ImovelEstoque,Long> {
		
	private String QUERY_ID_ESTOQUE = "select id from cobranca.imovelestoque";
	
    @SuppressWarnings("unchecked")
	public List<ImovelEstoque> relatorioImovelEstoque() {
		return (List<ImovelEstoque>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelEstoque> objects = new ArrayList<ImovelEstoque>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ID_ESTOQUE);
					
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
