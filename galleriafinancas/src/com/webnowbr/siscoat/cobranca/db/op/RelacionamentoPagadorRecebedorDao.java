package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
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
						
						if(rs.getFetchSize() > 1 || listRelacoes.size() == 1) {
							if(CommonsUtil.mesmoValor(relacao.getPessoaRoot(), pagador)) {
								rprDao.getRelacionamentos(relacao.getPessoaChild(), listRelacoes);
							} else {
								rprDao.getRelacionamentos(relacao.getPessoaRoot(), listRelacoes);
							}	
						}
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}
	
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
					ps = connection.prepareStatement(QUERY_RELACIONAMENTOS);
					ps.setLong(1, pagadorRoot.getId());
					ps.setLong(2, pagadorChild.getId());
					rs = ps.executeQuery();
					RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
					while (rs.next()) {
						RelacionamentoPagadorRecebedor relacao = rprDao.findById(rs.getLong("id"));

						if (listRelacoes.contains(relacao)) {
							continue;
						} else {
							listRelacoes.add(relacao);
						}				
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}
	
	/*private static final String QUERY_RELACIONAMENTOS_ROOT = "select * from cobranca.RelacionamentoPagadorRecebedor " 
			+ " where pessoaChild = ? ";
	
	@SuppressWarnings("unchecked")
	public List<RelacionamentoPagadorRecebedor> getRelacoesRoot(final PagadorRecebedor pessoa) {
		return (List<RelacionamentoPagadorRecebedor>) executeDBOperation(new DBRunnable() {
			@Override
			public List<RelacionamentoPagadorRecebedor> run() throws Exception {
				List<RelacionamentoPagadorRecebedor> listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_RELACIONAMENTOS_ROOT);		
	
					ps.setLong(1, pessoa.getId());
	
					rs = ps.executeQuery();
										
					RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
					while (rs.next()) {
						PagadorRecebedorDao pDao = new PagadorRecebedorDao();
						
						RelacionamentoPagadorRecebedor relacao = rprDao.findById(rs.getLong("id"));
						listRelacoes.add(relacao);
						List<RelacionamentoPagadorRecebedor> relacoes =
								rprDao.getRelacoesRoot(pDao.findById(rs.getLong("pessoaRoot")));
						for(RelacionamentoPagadorRecebedor relacionamento : relacoes) {
							if(listRelacoes.contains(relacionamento)) {
								continue;
							}
							listRelacoes.add(relacionamento);
						}
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return listRelacoes;
			}
		});	
	}*/
}
