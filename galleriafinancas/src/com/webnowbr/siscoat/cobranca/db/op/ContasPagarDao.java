package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class ContasPagarDao extends HibernateDao<ContasPagar, Long> {

	private static final String QUERY_GET_DRE_CONTASPAGAR = "select  copa.id, copa.numeroDocumento, pare.nome,"
			+ " copa.valorpagamento,  copa.datapagamento, cont.id contId, cont.nome contNome, contPai.id contPaiId,"
			+ " contPai.nome contPaiNome, copa.descricao "
			+ " from cobranca.contaspagar copa"
			+ " left join cobranca.pagadorrecebedor pare on copa.pagadorrecebedor = pare.id"
			+ " left join cobranca.contacontabil cont on copa.contacontabil = cont.id"
			+ " left join cobranca.contacontabil contPai on cont.contacontabilpai = contPai.id"
			+ " where contapaga = true" + " and datapagamento between  ? ::timestamp and  ? ::timestamp"
			+ " and tipodespesa = 'E'" + " order by case when  contPai.nome is null then cont.nome"
			+ "               else contPai.nome " + "          end, cont.nome";


	public DemonstrativoResultadosGrupo getDreContasPagar(final Date dataInicio, final Date dataFim) throws Exception {

		DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
		demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
		demonstrativosResultadosGrupoDetalhe.setTipo("Contas Pagas");
		demonstrativosResultadosGrupoDetalhe.setCodigo(2);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query_QUERY_GET_DRE_CONTASPAGAR = QUERY_GET_DRE_CONTASPAGAR;

			ps = connection.prepareStatement(query_QUERY_GET_DRE_CONTASPAGAR);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {

				DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();

				demonstrativoResultadosGrupoDetalhe.setIdDetalhes(rs.getInt("id"));
				demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroDocumento"));
				
				if ( rs.getString("nome") != null)
					demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
				else
					demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("descricao"));
				Date dataVencimento = rs.getDate("datapagamento");
				demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataVencimento);
				demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("valorpagamento"));

				demonstrativoResultadosGrupoDetalhe.setIdContaContabil(rs.getLong("contId"));
				demonstrativoResultadosGrupoDetalhe.setNomeContaContabil(rs.getString("contNome"));
				demonstrativoResultadosGrupoDetalhe.setIdContaContabilPai(rs.getLong("contPaiId"));
				demonstrativoResultadosGrupoDetalhe.setNomeContaContabilPai(rs.getString("contPaiNome"));

				demonstrativosResultadosGrupoDetalhe.getDetalhe().add(demonstrativoResultadosGrupoDetalhe);

				demonstrativosResultadosGrupoDetalhe.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
				demonstrativosResultadosGrupoDetalhe.addJuros(demonstrativoResultadosGrupoDetalhe.getJuros());
				demonstrativosResultadosGrupoDetalhe
						.addAmortizacao(demonstrativoResultadosGrupoDetalhe.getAmortizacao());

			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return demonstrativosResultadosGrupoDetalhe;

	}
	
	private static final String QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = "select id from cobranca.contaspagar " + 
			"where 1=1 " + 
			"and tipodespesa = ? " +
			"and contapaga = ? ";

	public List<ContasPagar> atualizaListagemContasPagar(final String tipoDespesa, final Boolean contaPaga, final Date dataInicio, final Date dataFim) throws Exception {

		List<ContasPagar> listContasPagar = new ArrayList<ContasPagar>();

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			
			String query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR;
			
			if (dataInicio != null) {
				query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR + " and datavencimento >= ? ::timestamp ";
			}
			
			if (dataFim != null) {
				query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR = query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR + " and datavencimento <= ? ::timestamp ";
			}
			
			ps = connection.prepareStatement(query_QUERY_ATUALIZA_LISTAGEM_CONTASPAGAR);

			ps.setString(1, tipoDespesa);
			ps.setBoolean(2, contaPaga);
			
			int countParam = 2;
			
			if (dataInicio != null) {
				java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
				countParam = countParam + 1;
				ps.setDate(countParam, dtRelInicioSQL);				
			}
			
			if (dataFim != null) {
				java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
				countParam = countParam + 1;
				ps.setDate(countParam, dtRelFimSQL);
			}
			
			rs = ps.executeQuery();
			ContasPagarDao cDao = new ContasPagarDao();
			ContasPagar contasPagar = new ContasPagar();
			
			while (rs.next()) {
				contasPagar = cDao.findById(rs.getLong("id"));
				listContasPagar.add(contasPagar);
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return listContasPagar;
	}
}
