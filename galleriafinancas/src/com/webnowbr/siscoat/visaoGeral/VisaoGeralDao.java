package com.webnowbr.siscoat.visaoGeral;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;



public class VisaoGeralDao extends HibernateDao <VisaoGeralVO,Long> {
	
	private static final String QUERY_GET_VG_CADASTRADOS = " SELECT COCO.ID IDCONTRATOCOBRANCA, "
			+ "	COCO.NUMEROCONTRATO, "
			+ "	PARE.NOME "
			+ " FROM COBRANCA.CONTRATOCOBRANCA COCO "
			+ " INNER JOIN COBRANCA.PAGADORRECEBEDOR PARE ON COCO.PAGADOR = PARE.ID "
			+ " WHERE DATACONTRATO BETWEEN ? ::TIMESTAMP AND ? ::TIMESTAMP "
			+ "	AND PAGADOR not in (15, 34, 14, 182, 417, 803) "
			+ "	and STATUS != 'Aprovado' "
			+ " ORDER BY NUMEROCONTRATO ";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getVgCadastro(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("Op Cadasstradas");
		visaoGeralGrupo.setCodigo(3);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_CADASTRADOS;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();

				visaoGeralGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				visaoGeralGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				visaoGeralGrupoDetalhe.setNome(rs.getString("nome"));
				
				visaoGeralGrupo.setQuantidade(visaoGeralGrupo.getQuantidade().add(BigInteger.ONE));
				visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
	
	private static final String QUERY_GET_VG_PRE_APROVADOS = " SELECT COCO.ID IDCONTRATOCOBRANCA, "
			+ "	COCO.NUMEROCONTRATO, "
			+ "	PARE.NOME "
			+ " FROM COBRANCA.CONTRATOCOBRANCA COCO "
			+ " INNER JOIN COBRANCA.PAGADORRECEBEDOR PARE ON COCO.PAGADOR = PARE.ID "
			+ " WHERE CadastroAprovadoData BETWEEN ? ::TIMESTAMP AND ? ::TIMESTAMP"
			+ " and CadastroAprovadovalor = 'Aprovado' "
			+ "	AND PAGADOR not in (15, 34, 14, 182, 417, 803) "
			+ " ORDER BY NUMEROCONTRATO ";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getVgPreAprovado(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("Op Pré-Aprovadas");
		visaoGeralGrupo.setCodigo(3);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_PRE_APROVADOS;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();

				visaoGeralGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				visaoGeralGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				visaoGeralGrupoDetalhe.setNome(rs.getString("nome"));
				
				visaoGeralGrupo.setQuantidade(visaoGeralGrupo.getQuantidade().add(BigInteger.ONE));
				visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
	
	private static final String QUERY_GET_VG_PAJU = " SELECT COCO.ID IDCONTRATOCOBRANCA, "
			+ "	COCO.NUMEROCONTRATO, "
			+ "	PARE.NOME "
			+ " FROM COBRANCA.CONTRATOCOBRANCA COCO "
			+ " INNER JOIN COBRANCA.PAGADORRECEBEDOR PARE ON COCO.PAGADOR = PARE.ID "
			+ " WHERE PajurFavoravelData BETWEEN ? ::TIMESTAMP AND ? ::TIMESTAMP"
			+ "	AND PAGADOR not in (15, 34, 14, 182, 417, 803) "
			+ " ORDER BY NUMEROCONTRATO ";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getVgPaju(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("Pedido Paju");
		visaoGeralGrupo.setCodigo(3);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_PAJU;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();

				visaoGeralGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				visaoGeralGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				visaoGeralGrupoDetalhe.setNome(rs.getString("nome"));
				
				visaoGeralGrupo.setQuantidade(visaoGeralGrupo.getQuantidade().add(BigInteger.ONE));
				visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
	
	private static final String QUERY_GET_VG_LAUDO = " SELECT COCO.ID IDCONTRATOCOBRANCA, "
			+ "	COCO.NUMEROCONTRATO, "
			+ "	PARE.NOME "
			+ " FROM COBRANCA.CONTRATOCOBRANCA COCO "
			+ " INNER JOIN COBRANCA.PAGADORRECEBEDOR PARE ON COCO.PAGADOR = PARE.ID "
			+ " WHERE LaudoRecebidoData BETWEEN ? ::TIMESTAMP AND ? ::TIMESTAMP"
			+ "	AND PAGADOR not in (15, 34, 14, 182, 417, 803) "
			+ " ORDER BY NUMEROCONTRATO ";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getVgLaudo(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("Pedido Laudo");
		visaoGeralGrupo.setCodigo(3);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_LAUDO;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();

				visaoGeralGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				visaoGeralGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				visaoGeralGrupoDetalhe.setNome(rs.getString("nome"));
				
				visaoGeralGrupo.setQuantidade(visaoGeralGrupo.getQuantidade().add(BigInteger.ONE));
				visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}

	private static final String QUERY_GET_VG_ASSINADOS = " select " 
			+ " coco.id idContratoCobranca, "
			+ " coco.numeroContrato, "
			+ " pare.nome,"
			+ " valorccb "
			+ " from cobranca.contratocobranca coco"
			+ " inner join cobranca.pagadorrecebedor pare on coco.pagador = pare.id"
			+ " where AgAssinaturaData between ? ::timestamp and  ? ::timestamp"
			+ " and pagador not in (15, 34,14, 182, 417, 803)"
			+ " order by numerocontrato;";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getVgAssinatura(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("CCBs Assinadas");
		visaoGeralGrupo.setCodigo(1);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_ASSINADOS;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();

				visaoGeralGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				visaoGeralGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				visaoGeralGrupoDetalhe.setNome(rs.getString("nome"));
				visaoGeralGrupoDetalhe.setValor(rs.getBigDecimal("valorccb"));

				visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
				visaoGeralGrupo.addValor(visaoGeralGrupoDetalhe.getValor());
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
		
	private static final String QUERY_GET_VG_REGISTRADOS = "select " 
			+ " coco.id idContratoCobranca, "
			+ " coco.numeroContrato, "
			+ " pare.nome,"
			+ " valorccb "
			+ " from cobranca.contratocobranca coco"
			+ " inner join cobranca.pagadorrecebedor pare on coco.pagador = pare.id"
			+ " where AgRegistroData between ? ::timestamp and  ? ::timestamp"
			+ " and pagador not in (15, 34,14, 182, 417, 803)"
			+ " order by numerocontrato;";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getVgRegistro(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("CCBs Pagas");
		visaoGeralGrupo.setCodigo(1);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_REGISTRADOS;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();

				visaoGeralGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				visaoGeralGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				visaoGeralGrupoDetalhe.setNome(rs.getString("nome"));
				visaoGeralGrupoDetalhe.setValor(rs.getBigDecimal("valorccb"));

				visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
				visaoGeralGrupo.addValor(visaoGeralGrupoDetalhe.getValor());
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	private static final String QUERY_GET_VG_DEBENTURISTA = " SELECT CCPI.ID, "
			+ "	COCO.ID IDCONTRATOCOBRANCA, "
			+ "	COCO.NUMEROCONTRATO, "
			+ "	PARE.NOME, "
			+ "	CCPI.NUMEROPARCELA, "
			+ "	CCPI.PARCELAMENSAL, "
			+ "	CCPI.VALORBAIXADO, "
			+ "	CCPI.JUROS, "
			+ "	CCPI.AMORTIZACAO, "
			+ "	CCPI.DATABAIXA,"
			+ " CCPI.BAIXADO, "
			+ " CCPI.SALDOCREDORATUALIZADO "
			+ " FROM COBRANCA.CONTRATOCOBRANCAPARCELASINVESTIDOR CCPI "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_1 CCPI1 ON CCPI.ID = CCPI1.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_2 CCPI2 ON CCPI.ID = CCPI2.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_3 CCPI3 ON CCPI.ID = CCPI3.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_4 CCPI4 ON CCPI.ID = CCPI4.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_5 CCPI5 ON CCPI.ID = CCPI5.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_6 CCPI6 ON CCPI.ID = CCPI6.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_7 CCPI7 ON CCPI.ID = CCPI7.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_8 CCPI8 ON CCPI.ID = CCPI8.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_9 CCPI9 ON CCPI.ID = CCPI9.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_PARCELAS_INVESTIDOR_JOIN_10 CCPI10 ON CCPI.ID = CCPI10.IDCONTRATOCOBRANCAPARCELASINVESTIDOR "
			+ " INNER JOIN COBRANCA.CONTRATOCOBRANCA COCO ON COCO.ID = CASE "
			+ "	WHEN CCPI1.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI1.IDCONTRATOCOBRANCAPARCELASINVESTIDOR1 "
			+ "	WHEN CCPI2.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI2.IDCONTRATOCOBRANCAPARCELASINVESTIDOR2 "
			+ "	WHEN CCPI3.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI3.IDCONTRATOCOBRANCAPARCELASINVESTIDOR3 "
			+ "	WHEN CCPI4.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI4.IDCONTRATOCOBRANCAPARCELASINVESTIDOR4 "
			+ "	WHEN CCPI5.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI5.IDCONTRATOCOBRANCAPARCELASINVESTIDOR5 "
			+ "	WHEN CCPI6.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI6.IDCONTRATOCOBRANCAPARCELASINVESTIDOR6 "
			+ "	WHEN CCPI7.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI7.IDCONTRATOCOBRANCAPARCELASINVESTIDOR7 "
			+ "	WHEN CCPI8.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI8.IDCONTRATOCOBRANCAPARCELASINVESTIDOR8 "
			+ "	WHEN CCPI9.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI9.IDCONTRATOCOBRANCAPARCELASINVESTIDOR9 "
			+ "	WHEN CCPI10.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN CCPI10.IDCONTRATOCOBRANCAPARCELASINVESTIDOR10 "
			+ " END "
			+ " INNER JOIN COBRANCA.PAGADORRECEBEDOR PARE ON PARE.ID = CASE "
			+ "	WHEN CCPI1.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR "
			+ "	WHEN CCPI2.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR2 "
			+ "	WHEN CCPI3.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR3 "
			+ "	WHEN CCPI4.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR4 "
			+ "	WHEN CCPI5.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR5 "
			+ "	WHEN CCPI6.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR6 "
			+ "	WHEN CCPI7.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR7 "
			+ "	WHEN CCPI8.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR8 "
			+ "	WHEN CCPI9.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR9 "
			+ "	WHEN CCPI10.IDCONTRATOCOBRANCAPARCELASINVESTIDOR IS NOT NULL THEN COCO.RECEBEDOR10 "
			+ " END "
			+ " WHERE COCO.STATUS = 'Aprovado'"
			+ " and baixado = true "
			+ " ORDER BY NUMEROCONTRATO, id ";	
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getTotalDeb(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("Total Deb");
		visaoGeralGrupo.setCodigo(10);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_DEBENTURISTA;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			rs = ps.executeQuery();

			String numeroContratoAnterior = "00001";
			String nomeAnterior = "abhdagdjasghd";
			int numeroparcelaAnterior = 999;
			long idcontrato = 0;
			
			
			BigDecimal saldoInvestidor = BigDecimal.ZERO;
			
			boolean inicio = true;
			
			while (rs.next()) {

				if (CommonsUtil.mesmoValor(rs.getString("NUMEROCONTRATO"), numeroContratoAnterior)
						&& CommonsUtil.mesmoValor(rs.getString("nome"), nomeAnterior)) {
					if (CommonsUtil.mesmoValor(rs.getString("NUMEROPARCELA"),
							CommonsUtil.stringValue(numeroparcelaAnterior + 1))) {
						saldoInvestidor = rs.getBigDecimal("SALDOCREDORATUALIZADO");
						numeroparcelaAnterior = CommonsUtil.intValue(rs.getString("NUMEROPARCELA"));
						numeroContratoAnterior = rs.getString("NUMEROCONTRATO");
						idcontrato = rs.getLong("idContratoCobranca");
						nomeAnterior = rs.getString("nome");
					} else {
						continue;
					}
				} else {
					if (!inicio) {
						VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();
						visaoGeralGrupoDetalhe.setIdContratoCobranca(idcontrato);
						visaoGeralGrupoDetalhe.setNumeroContrato(numeroContratoAnterior);
						visaoGeralGrupoDetalhe.setNome(nomeAnterior);
						visaoGeralGrupoDetalhe.setValor(saldoInvestidor);
						visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
						visaoGeralGrupo.addValor(visaoGeralGrupoDetalhe.getValor());
					}
					saldoInvestidor = rs.getBigDecimal("SALDOCREDORATUALIZADO");
					
					if (!rs.getString("NUMEROPARCELA").equals("Antecipação")) {
						numeroparcelaAnterior = CommonsUtil.intValue(rs.getString("NUMEROPARCELA"));
					}
					
					numeroContratoAnterior = rs.getString("NUMEROCONTRATO");
					idcontrato = rs.getLong("idContratoCobranca");
					nomeAnterior = rs.getString("nome");
				}
				inicio = false;
			}
			
			VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();
			
			visaoGeralGrupoDetalhe.setIdContratoCobranca(idcontrato);
			visaoGeralGrupoDetalhe.setNumeroContrato(numeroContratoAnterior);
			visaoGeralGrupoDetalhe.setNome(nomeAnterior);
			visaoGeralGrupoDetalhe.setValor(saldoInvestidor);

			visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
			visaoGeralGrupo.addValor(visaoGeralGrupoDetalhe.getValor());
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
	
	private static final String QUERY_GET_VG_FIDC = " SELECT COCO.NUMEROCONTRATO, "
			+ "	CCD.ID,"
			+ " COCO.ID IDCONTRATOCOBRANCA, "
			+ "	CCD.NUMEROPARCELA, "
			+ "	CCD.VlrSaldoParcela SALDODEVEDOR, "
			+ "	PARE.NOME,"
			+ " parcelapaga "
			+ " FROM COBRANCA.CONTRATOCOBRANCA COCO "
			+ " INNER JOIN COBRANCA.PAGADORRECEBEDOR PARE ON COCO.PAGADOR = PARE.ID "
			+ " LEFT JOIN COBRANCA.CONTRATOCOBRANCA_DETALHES_JOIN CCDJ ON CCDJ.IDCONTRATOCOBRANCA = COCO.ID "
			+ " INNER JOIN COBRANCA.CONTRATOCOBRANCADETALHES CCD ON CCD.ID = CCDJ.IDCONTRATOCOBRANCADETALHES "
			+ " WHERE PARCELAPAGA = TRUE "
			+ "	AND COCO.EMPRESA = 'FIDC GALLERIA' "
			+ " GROUP BY COCO.NUMEROCONTRATO, "
			+ " COCO.ID, "
			+ "	CCD.ID, "
			+ "	CCD.NUMEROPARCELA, "
			+ "	SALDODEVEDOR, "
			+ "	PARE.NOME "
			+ " ORDER BY COCO.NUMEROCONTRATO ASC, CCD.ID, "
			+ "	PARE.NOME ASC ";
	
	@SuppressWarnings("unchecked")
	public VisaoGeralGrupo getBrutoFidc(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		VisaoGeralGrupo visaoGeralGrupo = new VisaoGeralGrupo();
		visaoGeralGrupo.setDetalhe(new ArrayList<VisaoGeralGrupoDetalhe>(0));
		visaoGeralGrupo.setTipo("Cx Bruto FIDC");
		visaoGeralGrupo.setCodigo(2);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query = QUERY_GET_VG_FIDC;

			ps = connection.prepareStatement(query);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			rs = ps.executeQuery();

			String numeroContratoAnterior = "00001";
			String nomeAnterior = "abhdagdjasghd";
			int numeroparcelaAnterior = 999;
			long idcontrato = 0;
			visaoGeralGrupo.setValorTotal(BigDecimal.valueOf(100000000));
			
			BigDecimal saldoInvestidor = BigDecimal.ZERO;
			
			boolean inicio = true;
			
			while (rs.next()) {

				if (CommonsUtil.mesmoValor(rs.getString("NUMEROCONTRATO"), numeroContratoAnterior)) {
					if (CommonsUtil.mesmoValor(rs.getString("NUMEROPARCELA"),
							CommonsUtil.stringValue(numeroparcelaAnterior + 1))) {
						saldoInvestidor = rs.getBigDecimal("SALDODEVEDOR");
						numeroparcelaAnterior = CommonsUtil.intValue(rs.getString("NUMEROPARCELA"));
						numeroContratoAnterior = rs.getString("NUMEROCONTRATO");
						idcontrato = rs.getLong("idContratoCobranca");
						nomeAnterior = rs.getString("nome");
					} else {
						continue;
					}
				} else {
					if (!inicio) {
						VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();
						visaoGeralGrupoDetalhe.setIdContratoCobranca(idcontrato);
						visaoGeralGrupoDetalhe.setNumeroContrato(numeroContratoAnterior);
						visaoGeralGrupoDetalhe.setNome(nomeAnterior);
						visaoGeralGrupoDetalhe.setValor(saldoInvestidor);
						visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
						visaoGeralGrupo.subValor(visaoGeralGrupoDetalhe.getValor());
					}
					saldoInvestidor = rs.getBigDecimal("SALDODEVEDOR");
					numeroparcelaAnterior = CommonsUtil.intValue(rs.getString("NUMEROPARCELA"));
					numeroContratoAnterior = rs.getString("NUMEROCONTRATO");
					nomeAnterior = rs.getString("nome");
				}
				inicio = false;
			}
			
			VisaoGeralGrupoDetalhe visaoGeralGrupoDetalhe = new VisaoGeralGrupoDetalhe();
			
			visaoGeralGrupoDetalhe.setIdContratoCobranca(idcontrato);
			visaoGeralGrupoDetalhe.setNumeroContrato(numeroContratoAnterior);
			visaoGeralGrupoDetalhe.setNome(nomeAnterior);
			visaoGeralGrupoDetalhe.setValor(saldoInvestidor);

			visaoGeralGrupo.getDetalhe().add(visaoGeralGrupoDetalhe);
			visaoGeralGrupo.subValor(visaoGeralGrupoDetalhe.getValor());
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return visaoGeralGrupo;
	}
}
