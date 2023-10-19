package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
import com.webnowbr.siscoat.cobranca.service.EngineService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class RelacionamentoPagadorRecebedorDao extends HibernateDao <RelacionamentoPagadorRecebedor,Long> {
	
	private static final String QUERY_RELACIONAMENTOS = "select * from cobranca.RelacionamentoPagadorRecebedor " 
			+ " where pessoaRoot = ? "
			+ " or pessoaChild = ? ";
	
	@SuppressWarnings("unchecked")
	public List<RelacionamentoPagadorRecebedor> getRelacionamentos(final PagadorRecebedor pagador,
			List<RelacionamentoPagadorRecebedor> listRelacoes) {
		return (List<RelacionamentoPagadorRecebedor>) executeDBOperation(new DBRunnable() {
			@Override
			public List<RelacionamentoPagadorRecebedor> run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_RELACIONAMENTOS);

					ps.setLong(1, pagador.getId());
					ps.setLong(2, pagador.getId());

					rs = ps.executeQuery();
					RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();

					while (rs.next()) {
						RelacionamentoPagadorRecebedor relacao = rprDao.findById(rs.getLong("id"));

						if (listRelacoes.contains(relacao)) {
							continue;
						} else {
							listRelacoes.add(relacao);
						}
						
						if(CommonsUtil.mesmoValor(relacao.getPessoaRoot(), pagador)) {
							rprDao.getRelacionamentos(relacao.getPessoaChild(), listRelacoes);
						} else if (CommonsUtil.mesmoValor(relacao.getPessoaChild(), pagador))  {
							rprDao.getRelacionamentos(relacao.getPessoaRoot(), listRelacoes);
						}	
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}
	
	private static final String QUERY_RELACIONAMENTOS_EXISTENTES = "select id from cobranca.RelacionamentoPagadorRecebedor " 
			+ " where pessoaRoot = ? "
			+ " and pessoaChild = ? ";
	
	@SuppressWarnings("unchecked")
	public List<RelacionamentoPagadorRecebedor> verificaRelacaoExistente(final PagadorRecebedor pagadorRoot,
			final PagadorRecebedor pagadorChild) {
		return (List<RelacionamentoPagadorRecebedor>) executeDBOperation(new DBRunnable() {
			@Override
			public List<RelacionamentoPagadorRecebedor> run() throws Exception {
				
				List<RelacionamentoPagadorRecebedor> listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_RELACIONAMENTOS_EXISTENTES);
					ps.setLong(1, pagadorRoot.getId());
					ps.setLong(2, pagadorChild.getId());
					rs = ps.executeQuery();
					RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
					while (rs.next()) {
						RelacionamentoPagadorRecebedor relacao = rprDao.findById(rs.getLong("id"));

						if (listRelacoes.size() == 0) {
							listRelacoes.add(relacao);
						} else {
							continue;
						}				
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}
	
	
	private static final String QUERY_populaGeralDB = " select id, identificacao, tipo, tipopessoa, retornoserasa\r\n"
			+ "from cobranca.documentosanalise d\r\n"
			+ "where retornoserasa is not null\r\n"
			+ "	and retornoserasa != ''\r\n"
			+ "	and (tipo is null\r\n"
			+ "		or tipo != 'Rea')\r\n"
			+ "	and pagador is not null\r\n"
			+ "	and id > ? \r\n"
			+ "	and id < ? \r\n"
			+ "	order by id " ;
	
	@SuppressWarnings("unchecked")
	public List<RelacionamentoPagadorRecebedor> populaGeralDB(int param1, int param2) {
		return (List<RelacionamentoPagadorRecebedor>) executeDBOperation(new DBRunnable() {
			@Override
			public List<RelacionamentoPagadorRecebedor> run() throws Exception {
				
				List<RelacionamentoPagadorRecebedor> listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_populaGeralDB);
					ps.setLong(1, param1);
					ps.setLong(2, param2);
					
					rs = ps.executeQuery();
					DocumentoAnaliseDao docDao = new DocumentoAnaliseDao();
					while (rs.next()) {
						DocumentoAnalise docAnalise = docDao.findById(rs.getLong("id"));
						SerasaService serasa = new SerasaService();
						serasa.requestSerasa(docAnalise, null);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}
	
	private static final String QUERY_populaGeralDB_engine = " select id, identificacao, tipo, tipopessoa, retornoengine  "
			+ " from cobranca.documentosanalise d "
			+ " where retornoengine is not null "
			+ " and retornoengine != '' "
			+ " and pagador is not null " ;
	
	@SuppressWarnings("unchecked")
	public List<RelacionamentoPagadorRecebedor> populaGeralDBEngine() {
		return (List<RelacionamentoPagadorRecebedor>) executeDBOperation(new DBRunnable() {
			@Override
			public List<RelacionamentoPagadorRecebedor> run() throws Exception {
				
				List<RelacionamentoPagadorRecebedor> listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_populaGeralDB_engine);
					
					rs = ps.executeQuery();
					DocumentoAnaliseDao docDao = new DocumentoAnaliseDao();
					while (rs.next()) {
						DocumentoAnalise docAnalise = docDao.findById(rs.getLong("id"));
						EngineService service = new EngineService();
						service.gerarRelacoesEngine(docAnalise);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}
	
}
