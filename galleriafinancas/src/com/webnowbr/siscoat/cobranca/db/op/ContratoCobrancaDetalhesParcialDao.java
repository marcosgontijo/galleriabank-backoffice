package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.db.dao.HibernateDao;


/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ContratoCobrancaDetalhesParcialDao extends HibernateDao <ContratoCobrancaDetalhesParcial,Long> {

	private static final String QUERY_CONTRATO_COBRANCA_PARCIAL = 	"select parc.id"
			+ " from  cobranca.contratocobrancadetalhesparcial parc"
			+ " inner join cobranca.cobranca_detalhes_parcial_join parc_join on  parc_join.idcontratocobrancadetalhesparcial = parc.id"
			+ " where parc_join.idcontratocobrancadetalhes = ?"
			;
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaDetalhesParcial> getContratoCobrancaDetalhesParcial(Long idcontratocobrancadetalhes) {

		return (List<ContratoCobrancaDetalhesParcial>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobrancaDetalhesParcial> objects = new ArrayList<ContratoCobrancaDetalhesParcial>();

				Connection connection = null;
				PreparedStatement ps = null;
				
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_CONTRATO_COBRANCA_PARCIAL);
					
					ps.setLong(1, idcontratocobrancadetalhes);
					
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
