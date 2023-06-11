package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.db.dao.*;

/**
 * DAO access layer for the Tecnico entity
 * 
 * @author hv.junior
 *
 */
public class PagadorRecebedorConsultaDao extends HibernateDao<PagadorRecebedorConsulta, Long> {

	private static final String QUERY_PESQUISA_CONSULTA = "select id from cobranca.pagadorrecebedorconsulta "
			+ "where pessoa = ? and tipo = ? ";

	@SuppressWarnings("unchecked")
	public PagadorRecebedorConsulta getConsultaByPagadorAndTipo(final PagadorRecebedor pagador,
			final DocumentosAnaliseEnum tipoConsulta) {
		return (PagadorRecebedorConsulta) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				PagadorRecebedorConsulta pagadorRecebedor = null;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_PESQUISA_CONSULTA);

					ps.setLong(1, pagador.getId());
					ps.setString(2, tipoConsulta.getNome());

					rs = ps.executeQuery();

					if (rs.next()) {
						pagadorRecebedor = findById(rs.getLong(1));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return pagadorRecebedor;
			}
		});
	}
	
}
