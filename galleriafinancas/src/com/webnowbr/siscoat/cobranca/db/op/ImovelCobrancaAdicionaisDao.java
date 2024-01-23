package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobrancaAdicionais;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ImovelCobrancaAdicionaisDao extends HibernateDao <ImovelCobrancaAdicionais,Long> {
	
	private String QUERY_BUSCA_IMOVEIS_ADD = "SELECT ia.id, ia.imovel, ia.contratocobranca, ia.relacaocomgarantia, ia.valorregistro "
											+ " FROM cobranca.imovelcobrancaadicionais ia "
											+ " INNER JOIN cobranca.contratocobranca cc ON cc.id = ia.contratrocobranca "
											+ " WHERE cc.id = ? ";
	
	private String QUERY_BUSCA_IMOVEIS_PRE_LAUDO = "SELECT ia.id, ia.imovel, ia.contratocobranca, ia.relacaocomgarantia, ia.valorregistro "
			+ " FROM cobranca.imovelcobrancaadicionais ia "
			+ " INNER JOIN cobranca.contratocobranca cc ON cc.id = ia.contratrocobranca "
			+ " WHERE cc.id = ? "
			+ " AND ia.prelaudocompass = true "
			+ " AND cc.avaliacaolaudo = 'Compass' ";
	
	@SuppressWarnings("unchecked")
	public List<ImovelCobranca> getListImoveisAdd( Long idContratoCobranca ) {
		return (List<ImovelCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelCobranca> objects = new ArrayList<ImovelCobranca>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				String QUERY_BUSCA_IMOVEIS_ADD_CUSTOM = QUERY_BUSCA_IMOVEIS_ADD;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_BUSCA_IMOVEIS_ADD_CUSTOM);
					
					ps.setLong(1, idContratoCobranca);
					
					rs = ps.executeQuery();
					
					ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
					ImovelCobranca imovelCobranca = new ImovelCobranca();
					
					while (rs.next()) {
						imovelCobranca = imovelCobrancaDao.findById(rs.getLong(2));
						objects.add(imovelCobranca);
					}
							
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ImovelCobranca> getListImoveisAddPreLaudoCompass( Long idContratoCobranca ) {
		return (List<ImovelCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelCobranca> objects = new ArrayList<ImovelCobranca>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				String QUERY_BUSCA_IMOVEIS_ADD_PRELAUDO_COMPASS_CUSTOM = QUERY_BUSCA_IMOVEIS_PRE_LAUDO;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_BUSCA_IMOVEIS_ADD_PRELAUDO_COMPASS_CUSTOM);
					
					ps.setLong(1, idContratoCobranca);
					
					rs = ps.executeQuery();
					
					ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
					ImovelCobranca imovelCobranca = new ImovelCobranca();
					
					while (rs.next()) {
						imovelCobranca = imovelCobrancaDao.findById(rs.getLong(2));
						objects.add(imovelCobranca);
					}
							
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});
	}
}
