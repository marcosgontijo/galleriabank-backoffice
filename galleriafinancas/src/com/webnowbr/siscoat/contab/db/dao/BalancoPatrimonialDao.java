package com.webnowbr.siscoat.contab.db.dao;

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

}
