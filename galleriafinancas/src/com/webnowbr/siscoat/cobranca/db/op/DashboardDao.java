package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Dashboard;
import com.webnowbr.siscoat.cobranca.db.model.GruposPagadores;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PesquisaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DashboardDao extends HibernateDao <Dashboard,Long> {

	// total contratos
	// contratos analise aprovada
	// contratos analise reprovada
	// aguardando pagamento
	// pagamento confirmando, aguardando laudo e paju
	private static final String QUERY_DASH_CONTRATOS =  	" select idresponsavel, nomeresponsavel, sum(totalNovosContratos) totalNovosContratos, sum(totalaEmAnalise) totalaEmAnalise, sum(totalAprovados) totalAprovados, sum(totalReprovados) totalReprovados from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) totalNovosContratos, 0 totalaEmAnalise, 0 totalAprovados, 0 totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Ag. Análise' "
			+ " and datacontrato >= ? ::timestamp "
			+ "   and datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 totalNovosContratos, count(c.id) totalaEmAnalise, 0 totalAprovados, 0 totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Em Análise' "
			+ " and datacontrato >= ? ::timestamp "
			+ "   and datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 totalNovosContratos, 0 totalaEmAnalise, count(c.id) totalAprovados, 0 totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Aprovado' "
			+ " and datacontrato >= ? ::timestamp "
			+ "   and datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 totalNovosContratos, 0 totalaEmAnalise, 0 totalAprovados, count(c.id) totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Reprovado' "
			+ " and datacontrato >= ? ::timestamp "
			+ "   and datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel";
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE =  	" select idresponsavel, nomeresponsavel, sum(totalNovosContratos) totalNovosContratos, sum(totalaEmAnalise) totalaEmAnalise, sum(totalAprovados) totalAprovados, sum(totalReprovados) totalReprovados from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) totalNovosContratos, 0 totalaEmAnalise, 0 totalAprovados, 0 totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Ag. Análise' "
			+ " and datacontrato >= ? ::timestamp "
			+ " and datacontrato <= ? ::timestamp "
			+ " r.donoResponsavel = ? "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 totalNovosContratos, count(c.id) totalaEmAnalise, 0 totalAprovados, 0 totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Em Análise' "
			+ " and datacontrato >= ? ::timestamp "
			+ " and datacontrato <= ? ::timestamp "
			+ " r.donoResponsavel = ? "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 totalNovosContratos, 0 totalaEmAnalise, count(c.id) totalAprovados, 0 totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Aprovado' "
			+ " and datacontrato >= ? ::timestamp "
			+ " and datacontrato <= ? ::timestamp "
			+ " r.donoResponsavel = ? "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 totalNovosContratos, 0 totalaEmAnalise, 0 totalAprovados, count(c.id) totalReprovados"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.status = 'Reprovado' "
			+ " and datacontrato >= ? ::timestamp "
			+ " and datacontrato <= ? ::timestamp "
			+ " r.donoResponsavel = ? "
			+ " group by r.id, r.nome, c.datacontrato"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel";
			
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratos(final Date dataInicio, final Date dataFim, final long idGerenteResponsavel) {
		return (List<Dashboard>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Dashboard> objects = new ArrayList<Dashboard>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;	
				
				try {
					connection = getConnection();
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
	
					if (idGerenteResponsavel > 0) {						
						ps = connection
								.prepareStatement(QUERY_DASH_CONTRATOS_POR_GERENTE);	
						
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);
						ps.setLong(3, idGerenteResponsavel);	
						
						ps.setDate(4, dtRelInicioSQL);
						ps.setDate(5, dtRelFimSQL);	
						ps.setLong(6, idGerenteResponsavel);	
						
						ps.setDate(7, dtRelInicioSQL);
						ps.setDate(8, dtRelFimSQL);
						ps.setLong(9, idGerenteResponsavel);	
		
						ps.setDate(10, dtRelInicioSQL);
						ps.setDate(11, dtRelFimSQL);
						ps.setLong(12, idGerenteResponsavel);	
					} else {						
						ps = connection
								.prepareStatement(QUERY_DASH_CONTRATOS);	
						
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);	
						
						ps.setDate(3, dtRelInicioSQL);
						ps.setDate(4, dtRelFimSQL);	
		
						ps.setDate(5, dtRelInicioSQL);
						ps.setDate(6, dtRelFimSQL);	
		
						ps.setDate(7, dtRelInicioSQL);
						ps.setDate(8, dtRelFimSQL);	
					}
	
					rs = ps.executeQuery();
					
					Dashboard dashboard = new Dashboard();
					
					ResponsavelDao responsavelDao = new ResponsavelDao();
					Responsavel responsavel = new Responsavel();
					
					if (idGerenteResponsavel > 0) {
						responsavel = responsavelDao.findById(idGerenteResponsavel);
					}
					
					while (rs.next()) {
						dashboard = new Dashboard();
						dashboard.setNomeResponsavel(rs.getString(2));
						
						responsavel = responsavelDao.findById(rs.getLong(1));
						dashboard.setResponsavel(responsavel);
						
						if (idGerenteResponsavel > 0) {
							dashboard.setGerenteResponsavel(responsavel.getNome());
						} else {
							if (responsavel.getDonoResponsavel() != null) {
								dashboard.setGerenteResponsavel(responsavel.getDonoResponsavel().getNome());
							}							
						}

						dashboard.setTotalNovosContratos(rs.getInt(3));
						dashboard.setTotalaEmAnalise(rs.getInt(4));
						dashboard.setTotalAprovados(rs.getInt(5));
						dashboard.setTotalReprovados(rs.getInt(6));
						
						objects.add(dashboard);
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
}
