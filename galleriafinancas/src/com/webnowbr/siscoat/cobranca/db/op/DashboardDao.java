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
	private static final String QUERY_DASH_CONTRATOS =  	" select idresponsavel, nomeresponsavel, sum(total) totalcontratos, sum(totalaprovados) totalaprovados, sum(totalreprovados) totalreprovados, sum(totalagpagamento) totalagpagamento, sum(totalpago) totalpago  from ( "
			+ " select r.id idresponsavel, r.nome nomeresponsavel, count(c.id) total, 0 totalaprovados, 0 totalreprovados, 0 totalagpagamento, 0 totalpago"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.datacontrato >= ? ::timestamp "
			+ " and c.datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 total, count(c.id) totalaprovados, 0 totalreprovados, 0 totalagpagamento, 0 totalpago"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.analisereprovada = false and c.cadastroAprovadoValor = 'Aprovado'"
			+ " and c.cadastroAprovadoData >= ? ::timestamp "
			+ " and c.cadastroAprovadoData <= ? ::timestamp "
			+ " group by r.id, r.nome"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 total, 0 totalaprovados, count(c.id) totalreprovados, 0 totalagpagamento, 0 totalpago"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where c.analisereprovada = true"
			+ " and c.analiseReprovadaData >= ? ::timestamp "
			+ " and c.analiseReprovadaData <= ? ::timestamp "
			+ " group by r.id, r.nome"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 total, 0 totalaprovados, 0 totalreprovados, count(c.id) totalagpagamento, 0 totalpago"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = false" 
			+ " and c.datacontrato >= ? ::timestamp "
			+ " and c.datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome"
			+ " union all"
			+ " select r.id idresponsavel, r.nome nomeresponsavel, 0 total, 0 totalaprovados, 0 totalreprovados, 0 totalagpagamento, count(c.id) totalpago"
			+ " from cobranca.contratocobranca c"
			+ " inner join cobranca.responsavel r on r.id = c.responsavel"
			+ " where cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = true and (laudoRecebido = false or pajurFavoravel = false)"
			+ " and c.datacontrato >= ? ::timestamp "
			+ " and c.datacontrato <= ? ::timestamp "
			+ " group by r.id, r.nome"
			+ " ) totais"
			+ " group by idresponsavel, nomeresponsavel "
			+ " order by nomeresponsavel";
			
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratos(final Date dataInicio, final Date dataFim) {
		return (List<Dashboard>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Dashboard> objects = new ArrayList<Dashboard>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;	
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_DASH_CONTRATOS);		
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelFimSQL);	
	
					ps.setDate(5, dtRelInicioSQL);
					ps.setDate(6, dtRelFimSQL);	
	
					ps.setDate(7, dtRelInicioSQL);
					ps.setDate(8, dtRelFimSQL);	
	
					ps.setDate(9, dtRelInicioSQL);
					ps.setDate(10, dtRelFimSQL);	
	
					rs = ps.executeQuery();
					
					Dashboard dashboard = new Dashboard();
					
					while (rs.next()) {
						dashboard = new Dashboard();
						dashboard.setNomeResponsavel(rs.getString(2));
						dashboard.setTotalcontratos(rs.getInt(3));
						dashboard.setTotalaprovados(rs.getInt(4));
						dashboard.setTotalreprovados(rs.getInt(5));
						dashboard.setTotalagpagamento(rs.getInt(6));
						dashboard.setTotalpago(rs.getInt(7));	
						
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
