package com.webnowbr.siscoat.relatorioLaudoPaju;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.seguro.mb.SeguroTabelaMB;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;

public class LaudoPajuDao extends HibernateDao <LaudoPajuVO,Long> {
	
	private static final String QUERY_LAUDO_PAJU = " select numerocontrato, pare.nome, valorLaudoPajuTotal, valorLaudoPajuPago, valorLaudoPajuFaltante  "
			+ " from cobranca.contratocobranca coco "
			+ " inner join cobranca.pagadorrecebedor pare on pare.id = coco.pagador "
			+ " where not (valorLaudoPajuTotal is null or valorLaudoPajuTotal = 0) " ;
	
	@SuppressWarnings("unchecked")
	public List<LaudoPajuVO> listaLaudoPaju() {
		return (List<LaudoPajuVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<LaudoPajuVO> objects = new ArrayList<LaudoPajuVO>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
				
					ps = connection.prepareStatement(QUERY_LAUDO_PAJU);

					rs = ps.executeQuery();
					
			
					LaudoPajuVO laudoPajuVO = null;
					
										
					while (rs.next()) {	
						
						laudoPajuVO = new LaudoPajuVO();
						
						laudoPajuVO.setNumeroContrato(rs.getString("numerocontrato"));						
						laudoPajuVO.setNomePagador(rs.getString("nome"));
						laudoPajuVO.setValorTotal(rs.getBigDecimal("valorLaudoPajuTotal"));							
						laudoPajuVO.setValorPago(rs.getBigDecimal("valorLaudoPajuPago"));				
						laudoPajuVO.setValorRestante(rs.getBigDecimal("valorLaudoPajuFaltante"));			
						objects.add(laudoPajuVO);															
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
				
				
			}
		});	
	}
}
