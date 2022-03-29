package com.webnowbr.siscoat.tempoAnalise;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class TempoAnaliseDao extends HibernateDao <Analise,Long> {
	
	private static final String CONTRATOS = " SELECT ID, inicioanaliseusuario, inicioanalisedata, CadastroAprovadoData "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and (CadastroAprovadovalor = 'Aprovado' or CadastroAprovadovalor = 'Pendente') "
			+ "	and inicioanalisedata >= ? ::timestamp "
			+ "	and inicioanalisedata <= ? ::timestamp "
			+ "	and CadastroAprovadoData >= ? ::timestamp "
			+ "	and CadastroAprovadoData <= ? ::timestamp "
			+ "	AND (PagtoLaudoConfirmadaData is null or date_trunc('day', CadastroAprovadoData) < date_trunc('day', PagtoLaudoConfirmadaData)) "
			+ "	and INICIOANALISEUSUARIO = CadastroAprovadoUSUARIO ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> listaContratos(Date dataInicio, Date dataFim) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection.prepareStatement(CONTRATOS);
										
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					
					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelFimSQL);
					
					rs = ps.executeQuery();
					
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					while (rs.next()) {
						objects.add(contratoCobrancaDao.findById(rs.getLong(1)));				
					}
					
				} finally {
					closeResources(connection, ps);
				}
				return objects;
			}
		});
	} 
	
	private static final String QT_DE_ANALISE = " SELECT INICIOANALISEUSUARIO, "
			+ "	SUM(CONTRATOSCADASTRADOS) CONTRATOSCADASTRADOS "
			+ " FROM (SELECT INICIOANALISEUSUARIO, "
			+ "	COUNT(C.ID) CONTRATOSCADASTRADOS "
			+ "	FROM COBRANCA.CONTRATOCOBRANCA C "
			+ "	WHERE C.STATUSLEAD = 'Completo' "
			+ "	and (CadastroAprovadovalor = 'Aprovado' or CadastroAprovadovalor = 'Pendente')  "
			+ "	and inicioanalisedata >= ? ::timestamp  "
			+ "	and inicioanalisedata <= ? ::timestamp  "
			+ "	and CadastroAprovadoData >= ? ::timestamp  "
			+ "	and CadastroAprovadoData <= ? ::timestamp "
			+ "	AND (PagtoLaudoConfirmadaData is null or date_trunc('day', CadastroAprovadoData) < date_trunc('day', PagtoLaudoConfirmadaData)) "
			+ "	and INICIOANALISEUSUARIO = CadastroAprovadoUSUARIO "
			+ "	GROUP BY INICIOANALISEUSUARIO) TOTAIS "
			+ " GROUP BY INICIOANALISEUSUARIO ";
	
	
	@SuppressWarnings("unchecked")
	public List<Analise> listaAnalises(Date dataInicio, Date dataFim) {
		return (List<Analise>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				
				List<Analise> objects = new ArrayList<Analise>();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QT_DE_ANALISE);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					
					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelFimSQL);
					
					rs = ps.executeQuery();
					
					TempoAnaliseDao tempoAnaliseDao = new TempoAnaliseDao();
					
					
					List<ContratoCobranca> todosContratos = tempoAnaliseDao.listaContratos(dataInicio, dataFim);
					 
					while (rs.next()) {
						
						long tempoMedio = 0;
						long difference_In_Hours = 0;
						long difference_In_Minutes = 0;
						long difference_In_Seconds = 0;
						
						Analise analise = new Analise();
						List<ContratosAnalise> contratosObjetoAnalise = new ArrayList<ContratosAnalise>();
												
						for(ContratoCobranca contrato : todosContratos) {

							if(CommonsUtil.mesmoValor(contrato.getInicioAnaliseUsuario(), rs.getString("inicioanaliseusuario"))){
								ContratosAnalise contratosAnalise = new ContratosAnalise();
								
								contratosAnalise.setContrato(contrato);
										
								long difference_In_Time = contrato.getCadastroAprovadoData().getTime() - contrato.getInicioAnaliseData().getTime();
								
								contratosAnalise.setTempoDeAnalise(difference_In_Time);
								
								tempoMedio = tempoMedio + difference_In_Time;
										
								contratosObjetoAnalise.add(contratosAnalise);
							}
						}
						
						tempoMedio = tempoMedio / rs.getInt("CONTRATOSCADASTRADOS");
						
						difference_In_Hours = (tempoMedio / (1000 * 60 * 60)) % 24;
						difference_In_Minutes = (tempoMedio / (1000 * 60)) % 60;
						difference_In_Seconds = (tempoMedio / 1000) % 60;
						
						String tempoMedioStr = difference_In_Hours + ":" + difference_In_Minutes + ":" + difference_In_Seconds;
						
						analise.setNome(rs.getString("InicioAnaliseUsuario"));
						analise.setQtdAnalises(rs.getInt("CONTRATOSCADASTRADOS"));
						analise.setTempoMedio(tempoMedioStr);
						analise.setContratos(contratosObjetoAnalise);
						objects.add(analise);
					}
					
					
				} finally {
					closeResources(connection, ps);
				}
				return objects;
			}
		});
	} 
	
}
