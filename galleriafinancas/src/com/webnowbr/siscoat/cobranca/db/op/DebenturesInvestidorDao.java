package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.db.dao.*;

public class DebenturesInvestidorDao extends HibernateDao<DebenturesInvestidor, Long> {

	private static final String QUERY_TITULOS_QUITADOS = "select d.id from cobranca.DebenturesInvestidor d "
			+ "where d.dataDebentures >= ? ::timestamp and d.dataDebentures <= ? ::timestamp ";

	@SuppressWarnings("unchecked")
	public List<DebenturesInvestidor> getDebenturesPorPeriodo(final Date dtRelInicio, final Date dtRelFim) {
		return (List<DebenturesInvestidor>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<DebenturesInvestidor> objects = new ArrayList<DebenturesInvestidor>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_TITULOS_QUITADOS);

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					rs = ps.executeQuery();

					DebenturesInvestidorDao dbDao = new DebenturesInvestidorDao();
					DebenturesInvestidor db = new DebenturesInvestidor();

					while (rs.next()) {
						db = new DebenturesInvestidor();

						db = dbDao.findById(rs.getLong(1));

						objects.add(db);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	
	private static final String QUERY_GET_DRE_DEBENTURES = "select " 
			+ "       dataDebentures,"
			+ "       contrato, "
			+ "       numeroCautela, "
			+ "       pare.nome,"
			+ "       qtdedebentures "
			+ " from cobranca.DebenturesInvestidor dein "
			+ " inner join cobranca.pagadorrecebedor pare on dein.recebedor = pare.id"
	 + " where dataDebentures between ? ::timestamp and  ? ::timestamp"
	 + " order by dataDebentures;";	

	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreDebentures(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
		demonstrativosResultadosGrupoDetalhe
				.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
		demonstrativosResultadosGrupoDetalhe.setTipo("Debêntures emitidas");
		demonstrativosResultadosGrupoDetalhe.setCodigo(4);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query_QUERY_GET_DRE_SAIDAS = QUERY_GET_DRE_DEBENTURES;

			ps = connection.prepareStatement(query_QUERY_GET_DRE_SAIDAS);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();

				demonstrativoResultadosGrupoDetalhe.setIdContratoCobranca(rs.getLong("contrato"));
				demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroCautela"));
				demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
				Date dataContrato = rs.getDate("dataDebentures");
				demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataContrato);
				demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("qtdedebentures").multiply(BigDecimal.valueOf(1000)));

					demonstrativosResultadosGrupoDetalhe.getDetalhe()
							.add(demonstrativoResultadosGrupoDetalhe);
					demonstrativosResultadosGrupoDetalhe
							.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return demonstrativosResultadosGrupoDetalhe;
	}
	
	
}
