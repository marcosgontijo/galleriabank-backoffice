package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.db.dao.*;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ContratoCobrancaDetalhesDao extends HibernateDao <ContratoCobrancaDetalhes,Long> {

	private static final String QUERY_DELETE_OBSERVACOES = "delete from cobranca.contratocobrancadetalhesobservacoes co " +
			" where not exists ( select * from cobranca.contratocobrancadetalhes_observacoes_join coj " +
			" where coj.idcontratocobrancadetalhesobservacoes = co.id)";

	public Boolean limpaObservacoesNaoUsadas() {
		return (Boolean) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_DELETE_OBSERVACOES);

					ps.executeUpdate();
					return true;
						
				} finally {
					closeResources(connection, ps);
				}
			}
		});
	}  
	
	private static final String QUERY_OBSERVACOES_ORDENADAS = 	"select co.id from cobranca.contratocobrancadetalhesobservacoes co " +
			"inner join cobranca.contratocobrancadetalhes_observacoes_join coj on coj.idcontratocobrancadetalhesobservacoes = co.id " +
			"where coj.idcontratocobrancadetalhes = ? " + 
			"order by data desc";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaDetalhesObservacoes> listaObservacoesOrdenadas(final long idContrato) {
		return (List<ContratoCobrancaDetalhesObservacoes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobrancaDetalhesObservacoes> objects = new ArrayList<ContratoCobrancaDetalhesObservacoes>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_OBSERVACOES_ORDENADAS);
					
					ps.setLong(1, idContrato);

					rs = ps.executeQuery();
					
					ContratoCobrancaDetalhesObservacoesDao contratoCobrancaDetalhesObservacoesDao = new ContratoCobrancaDetalhesObservacoesDao();
					while (rs.next()) {
						objects.add(contratoCobrancaDetalhesObservacoesDao.findById(rs.getLong(1)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
}
