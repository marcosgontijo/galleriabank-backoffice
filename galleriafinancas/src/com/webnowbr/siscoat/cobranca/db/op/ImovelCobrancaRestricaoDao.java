package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ImovelCobrancaRestricao;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * 
 * @author alexandre murta
 *
 */
public class ImovelCobrancaRestricaoDao extends HibernateDao<ImovelCobrancaRestricao, Long> {
	private String QUERY_PESQUISA_RESTRICAO = "select id from cobranca.imovelcobrancarestricao where ativa and  numeroMatricula like '%' || ? || '%' and numeroCartorio = ? and cartorioEstado = ? and cartorioMunicipio = ?";

	@SuppressWarnings("unchecked")
	public List<ImovelCobrancaRestricao> pesquisaImovelRestricao(String numeroMatricula, String numeroCartorio,
			String cartorioEstado, String cartorioMunicipio) {
		return (List<ImovelCobrancaRestricao>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelCobrancaRestricao> objects = new ArrayList<ImovelCobrancaRestricao>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_PESQUISA_RESTRICAO);

					ps.setString(1, numeroMatricula);
					ps.setString(2, numeroCartorio);
					ps.setString(3, cartorioEstado);
					ps.setString(4, cartorioMunicipio);
					
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
