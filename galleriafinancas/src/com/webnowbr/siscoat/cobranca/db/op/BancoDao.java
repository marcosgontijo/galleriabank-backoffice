package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.Banco;
import com.webnowbr.siscoat.cobranca.db.model.Cartorio;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Docket;
import com.webnowbr.siscoat.cobranca.service.BancoCentral;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class BancoDao extends HibernateDao <Banco,Long> {
	

	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(BancoDao.class);
	
	
	public Banco pesquisaBanco(String cnpjBase, Integer codigoBanco)
			throws DAOException {
		Banco banco = new Banco();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("pesquisaBancos");
		}

		banco = pesquisaBanco(cnpjBase);
		if (banco.getCodigoBanco() == null) {
			banco = pesquisaBanco(codigoBanco);
		}

		return banco;
	}
	
	public Banco pesquisaBanco(String cnpjBase) throws DAOException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("pesquisaBancos");
		}
		
		return (Banco) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				Banco banco = new Banco();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String QUERY_VERIFICA_CARTORIO = "select distinct id from Banco banc where cnpjBase = ?1 order by banc.codigoBanco " ;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder(QUERY_VERIFICA_CARTORIO);

					ps = connection.prepareStatement(query.toString());
					ps.setString(1, cnpjBase);
					rs = ps.executeQuery();

					CartorioDao dao = new CartorioDao();

					while (rs.next()) {
						banco = findById(rs.getLong(1));
						break;
					}

					closeResources(connection, ps, rs);
				} catch (Exception e) {
					return null;
				}
				return banco;
			}

		});
	}
	
	public Banco pesquisaBanco(Integer codigoBanco) throws DAOException {
		Banco banco = new Banco();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("pesquisaBancos");
		}
		
		return (Banco) executeDBOperation(new DBRunnable() {

			@Override
			public Object run() throws Exception {
				Banco banco = new Banco();
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String QUERY_VERIFICA_CARTORIO = "select distinct id from Banco banc where codigoBanco = ?1 order by banc.codigoBanco " ;
				try {
					connection = getConnection();
					StringBuilder query = new StringBuilder(QUERY_VERIFICA_CARTORIO);

					ps = connection.prepareStatement(query.toString());
					ps.setInt(1, codigoBanco);
					rs = ps.executeQuery();

					while (rs.next()) {
						banco = findById(rs.getLong(1));
						break;
					}

					closeResources(connection, ps, rs);
				} catch (Exception e) {
					return null;
				}
				return banco;
			}

		});
	}
	
}
