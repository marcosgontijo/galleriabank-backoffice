package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.mb.RelatorioEstoque;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ImovelEstoqueDao extends HibernateDao <ImovelEstoque,Long> {
		
	private String QUERY_ID_ESTOQUE = "select id from cobranca.imovelestoque";
	
    @SuppressWarnings("unchecked")
	public List<ImovelEstoque> relatorioImovelEstoque() {
		return (List<ImovelEstoque>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelEstoque> objects = new ArrayList<ImovelEstoque>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ID_ESTOQUE);
					
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
    @SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaImovelEstoqueNaoVendido() {
  		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
  			@Override
  			public Object run() throws Exception {
  				
  				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
  				String QUERY_ID_ESTOQUE = "select c.id from cobranca.contratocobranca c  \r\n"
  						+ "inner join cobranca.imovelcobranca i on i.id  = c.imovel  \r\n"
  						+ "inner join cobranca.imovelestoque i2 on i.imovelestoque  = i2.id \r\n "
  						+ "where i2.statusatual != 'Vendido' ";
  				Connection connection = null;
  				PreparedStatement ps = null;
  				ResultSet rs = null;
  				
  				try {
  					connection = getConnection();
  					
  					ps = connection
  							.prepareStatement(QUERY_ID_ESTOQUE);
  					
  					rs = ps.executeQuery();
  					ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
  					while (rs.next()) {
  						objects.add(contratoDao.findById(rs.getLong(1)));	
  					}
  							
  				} finally {
  					closeResources(connection, ps, rs);					
  				}
  				return objects;
  			}
  		});
  	}
    @SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaImovelEstoqueTudo() {
  		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
  			@Override
  			public Object run() throws Exception {
  				
  				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
  				String QUERY_ID_ESTOQUE = "select c.id from cobranca.contratocobranca c  \r\n"
  						+ "inner join cobranca.imovelcobranca i on i.id  = c.imovel  \r\n"
  						+ "inner join cobranca.imovelestoque i2 on i.imovelestoque  = i2.id \r\n ";
  				Connection connection = null;
  				PreparedStatement ps = null;
  				ResultSet rs = null;
  				
  				try {
  					connection = getConnection();
  					
  					ps = connection
  							.prepareStatement(QUERY_ID_ESTOQUE);
  					
  					rs = ps.executeQuery();
  					ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
  					while (rs.next()) {
  						objects.add(contratoDao.findById(rs.getLong(1)));	
  					}
  							
  				} finally {
  					closeResources(connection, ps, rs);					
  				}
  				return objects;
  			}
  		});
  	}
    @SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaImovelEstoqueVendido() {
  		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
  			@Override
  			public Object run() throws Exception {
  				
  				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
  				String QUERY_ID_ESTOQUE = "select c.id from cobranca.contratocobranca c  \r\n"
  						+ "inner join cobranca.imovelcobranca i on i.id  = c.imovel  \r\n"
  						+ "inner join cobranca.imovelestoque i2 on i.imovelestoque  = i2.id \r\n "
  						+ "where i2.statusatual = 'Vendido'";
  				Connection connection = null;
  				PreparedStatement ps = null;
  				ResultSet rs = null;
  				
  				try {
  					connection = getConnection();
  					
  					ps = connection
  							.prepareStatement(QUERY_ID_ESTOQUE);
  					
  					rs = ps.executeQuery();
  					ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
  					while (rs.next()) {
  						objects.add(contratoDao.findById(rs.getLong(1)));	
  					}
  							
  				} finally {
  					closeResources(connection, ps, rs);					
  				}
  				return objects;
  			}
  		});
  	}
    private String QUERY_RELATORIO_ESTOQUE = "select c.numerocontrato, ie.variacaocusto, ie.ltvleilao, ie.valoremprestimo, ie.vendaforcada, ie.valormercado, p.nome, i.numeromatricula, \r\n"
    		+ "concat (i.endereco, ', ', i.bairro, ', ', i.complemento, ', ', i.cidade, ', ', i.estado, '- ', i.cep) as Imovel, ie.dataconsolidado, ie.dataleilao1, ie.dataleilao2, \r\n"
    		+ "ie.dataleilao3 as LeilaoEstoque, ie.statusleilao, ie.leiloeiro, ie.statusatual, ie.valorleilao2, ie.valorvenda, ie.datavenda, ie.tipovenda, ie.quitado \r\n"
    		+ "	from cobranca.contratocobranca c\r\n"
    		+ "	left join cobranca.imovelcobranca i on c.imovel = i.id\r\n"
    		+ "	left join cobranca.imovelestoque ie on i.imovelestoque  = ie.id\r\n"
    		+ "	left join cobranca.pagadorrecebedor p on c.pagador = p.id\r\n"
    		+ "	where c.status = 'Aprovado' and c.valorccb > 0 and ie.quitado is not null and c.imovel <> 29\r\n"
    		+ "	order by c.numerocontrato";
    
    @SuppressWarnings("unchecked")
	public List<RelatorioEstoque> listRelatorioEstoque() {
		return (List<RelatorioEstoque>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<RelatorioEstoque> objects = new ArrayList<RelatorioEstoque>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_RELATORIO_ESTOQUE);
					rs = ps.executeQuery();
					RelatorioEstoque relatorio = null;
					while (rs.next()) {
						relatorio = new RelatorioEstoque();
						relatorio.setNumeroContratoRelatorio(rs.getString("numerocontrato"));
						relatorio.setVariacaoCustoRelatorio(rs.getBigDecimal("variacaocusto"));
						relatorio.setLtvLeilaoRelatorio(rs.getBigDecimal("ltvleilao"));
						relatorio.setValorEmprestimoRelatorio(rs.getBigDecimal("valoremprestimo"));
						relatorio.setVendaForcadaRelatorio(rs.getBigDecimal("vendaforcada"));
						relatorio.setValorMercadoRelatorio(rs.getBigDecimal("valormercado"));
						relatorio.setNomePagadorRelatorio(rs.getString("nome"));
						relatorio.setNumeroMatriculaRelatorio(rs.getString("numeromatricula"));
						relatorio.setEnderecoCompletoRelatorio(rs.getString("Imovel"));
						relatorio.setDataConsolidadoRelatorio(rs.getDate("dataconsolidado"));
						relatorio.setDataLeilao1Relatorio(rs.getDate("dataleilao1"));
						relatorio.setDataLeilao2Relatorio(rs.getDate("dataleilao2"));
						relatorio.setDataLeilao3Relatorio(rs.getDate("LeilaoEstoque"));
						relatorio.setLeiloeiroRelatorio(rs.getString("leiloeiro"));
						relatorio.setStatusLeilaoRelatorio(rs.getString("statusleilao"));
						relatorio.setStatusAtualRelatorio(rs.getString("statusatual"));
						relatorio.setValorLeilao2Relatorio(rs.getBigDecimal("valorleilao2"));
						relatorio.setValorVendaRelatorio(rs.getBigDecimal("valorvenda"));
						relatorio.setDataVendaRelatorio(rs.getDate("datavenda"));
						relatorio.setTipoVendaRelatorio(rs.getString("tipovenda"));
												
						objects.add(relatorio);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;

			}
		});
	}

    private String QUERY_ESTOQUE_BALANCO = "select id \r\n"
    		+ "from cobranca.imovelestoque i \r\n"
    		+ "where quitado is true";
    
    @SuppressWarnings("unchecked")
	public List<ImovelEstoque> balancoEstoque() {
		return (List<ImovelEstoque>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelEstoque> objects = new ArrayList<ImovelEstoque>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ESTOQUE_BALANCO);
					
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
