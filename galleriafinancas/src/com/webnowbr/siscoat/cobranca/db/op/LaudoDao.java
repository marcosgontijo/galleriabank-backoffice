package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.db.model.Cidade;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Laudo;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class LaudoDao extends HibernateDao <Laudo,Long> {
	
	private static final String QUERY_INFOS_LAUDO = " SELECT l.* "
													+ " FROM cobranca.laudo l "
													+ " WHERE l.contratoCobranca = ? ";
		
	@SuppressWarnings("unchecked")
	public Laudo getLaudoPorIdContratoCobranca(final long idContratoCobranca) {
		return (Laudo) executeDBOperation(new DBRunnable() {
			
			@Override
			public Object run() throws Exception {
				
				Laudo laudo = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					String QUERY_INFOS_LAUDO_CUSTOM = QUERY_INFOS_LAUDO;

					ps = connection
							.prepareStatement(QUERY_INFOS_LAUDO_CUSTOM);
				
					ps.setLong(1, idContratoCobranca);
	
					rs = ps.executeQuery();
					
					
					while (rs.next()) {
						laudo = findById(rs.getLong(1));						
						break;												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return laudo;
			}
		});	
	}
	
}