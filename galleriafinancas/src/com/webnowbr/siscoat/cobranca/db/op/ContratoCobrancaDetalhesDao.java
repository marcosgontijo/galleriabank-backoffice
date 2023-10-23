package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

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
	
	private static final String QUERY_CONTRATO_COBRANCA_FROM_DETALHES = 	"SELECT coco.id " +
			" FROM cobranca.ContratoCobranca_Detalhes_join ccdj " +
			" inner join cobranca.contratocobranca coco on ccdj.idcontratocobranca = coco.id " +
			" where idcontratocobrancadetalhes = ?; "; 
			
	@SuppressWarnings("unchecked")
	public ContratoCobranca getContratoCobranca(final long idContratoCobrancaDetalhes) {
		return (ContratoCobranca) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				ContratoCobranca object = new ContratoCobranca();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_CONTRATO_COBRANCA_FROM_DETALHES);
					
					ps.setLong(1, idContratoCobrancaDetalhes);

					rs = ps.executeQuery();
					
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					while (rs.next()) {
						object = contratoCobrancaDao.findById(rs.getLong(1));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return object;
			}
		});	
	}	
	
	
	private static final String QUERY_ATUALIZACAO_IPCA = 	"select co.id "
			+ " from cobranca.contratocobrancadetalhes co"
			+ " inner join cobranca.contratocobranca_detalhes_join det_join on co.id = det_join.idcontratocobrancadetalhes"
			+ " inner join cobranca.contratocobranca cont on det_join.idcontratocobranca = cont.id"
			+ " where dataVencimento between ? and ? "
			+ " and parcelaPaga = false"
			+ " and tipocalculo in ( 'Price', 'SAC')" 
			+ " and cont.corrigidoipca = true"
			//+ " and numerocontrato in ('05212')" 
			;
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaDetalhes> getParcelasCalculoIpca() {
		return (List<ContratoCobrancaDetalhes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobrancaDetalhes> objects = new ArrayList<ContratoCobrancaDetalhes>();

				Connection connection = null;
				PreparedStatement ps = null;
				Date primeiroDiaProximoMes = DateUtil.getFirstDayOfNextMonth();
				java.sql.Date primeiroDiaProximoMesSQL = new java.sql.Date(primeiroDiaProximoMes.getTime());
				
				Date ultimoDiaProximoMes = DateUtil.getLastDayMonth(primeiroDiaProximoMes);
				java.sql.Date ultimoDiaProximoMesSQL = new java.sql.Date(ultimoDiaProximoMes.getTime());
				
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_ATUALIZACAO_IPCA);

					ps.setDate(1, primeiroDiaProximoMesSQL);
					ps.setDate(2, ultimoDiaProximoMesSQL);
					
					rs = ps.executeQuery();

					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					while (rs.next()) {
						objects.add(contratoCobrancaDetalhesDao.findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}	

	private static final String QUERY_PARCELAS_POR_VENCIMENTO = "select cd.id, cc.id from cobranca.contratocobrancadetalhes cd " + 
			"inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobrancadetalhes = cd.id " + 
			"inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " +
			"where cd.datavencimento >= ? ::timestamp " + 
			"and cd.datavencimento <= ? ::timestamp " +
			"and cd.parcelapaga = false " +
			"and cc.contratoEmCartorio = false " +
			"and cc.pagador != 14";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaDetalhes> getParcelasPorVencimento(final Date dtInicioConsulta, final Date dtFimConsulta) {
		return (List<ContratoCobrancaDetalhes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobrancaDetalhes> objects = new ArrayList<ContratoCobrancaDetalhes>();

				Connection connection = null;
				PreparedStatement ps = null;

				java.sql.Date dtInicioConsultaSQL = new java.sql.Date(dtInicioConsulta.getTime());
				java.sql.Date dtFimConsultaSQL = new java.sql.Date(dtFimConsulta.getTime());

				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_PARCELAS_POR_VENCIMENTO);

					ps.setDate(1, dtInicioConsultaSQL);
					ps.setDate(2, dtFimConsultaSQL);
					
					rs = ps.executeQuery();

					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					
					ContratoCobrancaDetalhes contratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
					while (rs.next()) {
						contratoCobrancaDetalhes = contratoCobrancaDetalhesDao.findById(rs.getLong(1));
						contratoCobrancaDetalhes.setContrato(contratoCobrancaDao.findById(rs.getLong(2)));
						
						contratoCobrancaDetalhes.setQtdParcelasVencidas(getParcelasVencidas(contratoCobrancaDetalhes.getContrato()));
						 
						// remove parcelas carencia
						if (contratoCobrancaDetalhes.getVlrParcela().compareTo(BigDecimal.ZERO) > 0) {
							objects.add(contratoCobrancaDetalhes);
						}
						
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}	
	
	public int getParcelasVencidas(ContratoCobranca contrato) {
		int parcelasVencidas = 0;
		Date dataHoje = DateUtil.gerarDataHoje();
		
		for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
			// verifica parcelas vencidas, se maior que 1 não mostra contrato
			if (parcela.getDataVencimento().before(dataHoje) && !parcela.isParcelaPaga()) {
				parcelasVencidas = parcelasVencidas + 1;
				
				if (parcelasVencidas > 1) {
					break;
				}
			}
		}
		
		return parcelasVencidas;
	}	
}
