package com.webnowbr.siscoat.cobranca.db.op;

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
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;
import com.webnowbr.siscoat.boletosPagos.mb.BoletosPagosMB;
import com.webnowbr.siscoat.boletosPagos.vo.BoletosPagosVO;

public class BoletosPagosDao extends HibernateDao <BoletosPagosVO,Long> {
	
	private static final String QUERY_BOLETOS_PAGOS = " select coco.numerocontrato , pagtoLaudoConfirmadaData"
			+ " from cobranca.contratocobranca coco "
			+ " where coco.pagtoLaudoConfirmada = true "
			+ " and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ " and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ " group by coco.numerocontrato, pagtoLaudoConfirmadaData";
	
	@SuppressWarnings("unchecked")
	public List<BoletosPagosVO> listaBoletosPagos(final long idContrato, Date dataInicio, Date dataFim) {
		return (List<BoletosPagosVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<BoletosPagosVO> objects = new ArrayList<BoletosPagosVO>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					ps = connection.prepareStatement(QUERY_BOLETOS_PAGOS);
					
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					rs = ps.executeQuery();

					BoletosPagosVO boletosPagosVO = null;
										
					while (rs.next()) {	
						boletosPagosVO = new BoletosPagosVO();
						
						if(boletosPagosVO != null) {
							boletosPagosVO.setDataBoletoPago(rs.getDate("pagtoLaudoConfirmadaData"));
							boletosPagosVO.setNumeroContrato(rs.getString("numerocontrato"));
							objects.add(boletosPagosVO);
						}
					}
					
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
				
				
			}
		});	
	}
	
}