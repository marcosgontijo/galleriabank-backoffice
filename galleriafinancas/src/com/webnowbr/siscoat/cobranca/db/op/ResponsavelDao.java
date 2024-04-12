package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.db.dao.*;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ResponsavelDao extends HibernateDao <Responsavel,Long> {
	
	
	/*******
	 * CONSULTA TODO MUNDO QUE POSSUI NO GUARDA CHUVA O RESPONSAVEL "X"
	 */
	private static final String QUERY_GET_GUARDA_CHUVA =  "select r.id, r_principal.id "
			//"select r.id, r.nome membro_guarda_chuva, r.email membro_email_guarda_chuva, r_principal.id, r_principal.nome dono_guarda_chuva, r_principal.email dono_email_guarda_chuva " 
			+ "	from infra.usuario_responsavel_join ur "
			+ "	inner join cobranca.responsavel r on r.id = ur.idresponsavel "
			+ "	inner join infra.users u on u.id = ur.idusuario "
			+ "	inner join cobranca.responsavel r_principal on r_principal.codigo = u.codigoresponsavel "
			+ "	where ur.idusuario in " 
			+ "	(select ur.idusuario from infra.usuario_responsavel_join ur "
			+ "	where idresponsavel = ?) ";
	
	@SuppressWarnings("unchecked")
	public List<Responsavel> getGuardaChuvaCompletoResponsavel(final long idResponsavel) {
		return (List<Responsavel>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Responsavel> guardaChuva = new ArrayList<Responsavel>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String query_QUERY_GET_GUARDA_CHUVA = QUERY_GET_GUARDA_CHUVA;
					
					ps = connection
							.prepareStatement(query_QUERY_GET_GUARDA_CHUVA);		
	
					ps.setLong(1, idResponsavel);
	
					rs = ps.executeQuery();
					
					ResponsavelDao responsavelDao = new ResponsavelDao();
					
					while (rs.next()) {
						// se primeira vez armazena o dono do guarda chuva
						if (guardaChuva.size() == 0) {
							guardaChuva.add(responsavelDao.findById(rs.getLong(2)));
						}
						
						guardaChuva.add(responsavelDao.findById(rs.getLong(1)));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return guardaChuva;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public String getGuardaChuvaCompletoResponsavelString(final long idResponsavel) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String guardaChuva = "";
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String query_QUERY_GET_GUARDA_CHUVA = QUERY_GET_GUARDA_CHUVA;
					
					ps = connection
							.prepareStatement(query_QUERY_GET_GUARDA_CHUVA);		
	
					ps.setLong(1, idResponsavel);
	
					rs = ps.executeQuery();
					
					ResponsavelDao responsavelDao = new ResponsavelDao();
					
					while (rs.next()) {
						// se primeira vez armazena o dono do guarda chuva
						if (guardaChuva.equals("")) {
							guardaChuva = responsavelDao.findById(rs.getLong(2)).getEmail();
						} 
						
						if (!guardaChuva.equals("")) {
							guardaChuva = guardaChuva + ", " + responsavelDao.findById(rs.getLong(1)).getEmail();
						}
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return guardaChuva;
			}
		});	
	}
	
	/*******
	 * CONSULTA RECURSIVA DOS GUARDA-CHUVAS A PARTIR DE UM RESPONSAVEL (MEMBROS E SUAS LISTAS COM OS RESPECTIVOS MEMBROS)
	 */
	private static final String QUERY_GET_GUARDA_CHUVA_RECURSIVO =  " select distinct(r.codigo) from infra.users u " + 
			"	inner join infra.usuario_responsavel_join ur on ur.idusuario = u.id " + 
			"	inner join cobranca.responsavel r on r.id = ur.idresponsavel " + 
			"	where u.codigoresponsavel = ? ";
	
	@SuppressWarnings("unchecked")
	public List<String> getGuardaChuvaRecursivoPorResponsavel(final String codigoResponsavel) {
		return (List<String>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<String> guardaChuvaRecursivo = new ArrayList<String>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String query_QUERY_GET_GUARDA_CHUVA_RECURSIVO = QUERY_GET_GUARDA_CHUVA_RECURSIVO;
					
					ps = connection
							.prepareStatement(query_QUERY_GET_GUARDA_CHUVA_RECURSIVO);		
	
					ps.setString(1, codigoResponsavel);
	
					rs = ps.executeQuery();
					
					while (rs.next()) {
						guardaChuvaRecursivo.add(rs.getString(1));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return guardaChuvaRecursivo;
			}
		});	
	}

	private static final String QUERY_GET_RESPONSAVEL_LEAD =  " select r.id, r.nome, r.codigo, u.login "
			+ " from infra.users u  "
			+ " inner join cobranca.responsavel r on r.codigo = u.codigoresponsavel   "
			+ " where UserCobrancaLead = true "
			+ " order by id ";
	
	@SuppressWarnings("unchecked")
	public List<Responsavel> getResponsaveisLead() {
		return (List<Responsavel>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Responsavel> lista = new ArrayList<Responsavel>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String query_QUERY_GET_GUARDA_CHUVA = QUERY_GET_RESPONSAVEL_LEAD;
			
					ps = connection
							.prepareStatement(query_QUERY_GET_GUARDA_CHUVA);		
					rs = ps.executeQuery();
			

					while (rs.next()) {
						Responsavel r = new Responsavel();
						r.setId(rs.getLong("id"));
						r.setNome(rs.getString("nome"));
						r.setCodigo(rs.getString("codigo"));
						
						lista.add(r);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return lista;
			}
		});	
	}
	
	private static final String QUERY_GET_USUARIOS_COM_PERMISSAO =  "select u.id, u.\"name\" , u.codigoresponsavel, r.nome, r.codigo from infra.users u \r\n"
			+ "inner join infra.usuario_responsavel_join urj on urj.idusuario = u.id\r\n"
			+ "inner join cobranca.responsavel r on r.id = urj.idresponsavel \r\n"
			+ "where r.codigo = ? and u.codigoresponsavel != ?\r\n"
			+ "and u.codigoresponsavel != '15810' and u.codigoresponsavel != '111' "
			+ "and u.codigoresponsavel != ?";
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersComPermissao(Responsavel responsavel) {
		return (List<User>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<User> lista = new ArrayList<User>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
			
					ps = connection.prepareStatement(QUERY_GET_USUARIOS_COM_PERMISSAO);	
					
					ps.setString(1, responsavel.getCodigo());
					ps.setString(2, responsavel.getCodigo());
					if(!CommonsUtil.semValor(responsavel.getDonoResponsavel()))
						ps.setString(3, responsavel.getDonoResponsavel().getCodigo());
					else
						ps.setString(3, "1");
					rs = ps.executeQuery();
			
					UserDao userDao = new UserDao();
					while (rs.next()) {
						User user = userDao.findById(rs.getLong(1));
						/*user.getListResponsavel().remove(responsavel);
						userDao.merge(user);*/
						lista.add(user);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return lista;
			}
		});	
	}
	
	private static final String QUERY_GET_COMERCIAL_DONO =  "select u.id from infra.users u \r\n"
			+ "where u.codigoresponsavel = ?";
	
	@SuppressWarnings("unchecked")
	public User getUsersComercialDono(Responsavel responsavel) {
		return (User) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				User user = null;
				if(CommonsUtil.semValor(responsavel.getDonoResponsavel())){
					return null;
				}
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
			
					ps = connection.prepareStatement(QUERY_GET_COMERCIAL_DONO);	
					
					ps.setString(1, responsavel.getDonoResponsavel().getCodigo());
					rs = ps.executeQuery();
			
					UserDao userDao = new UserDao();
					while (rs.next()) {
						user = userDao.findById(rs.getLong(1));
						/*if(!user.getListResponsavel().contains(responsavel)) {
							user.getListResponsavel().add(responsavel);
							userDao.merge(user);
						}*/
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return user;
			}
		});	
	}
	
	private static final String QUERY_GET_ASSISTENTE =  "select u.id from infra.users u \r\n"
			+ "where u.codigoresponsavel = ?";
	
	@SuppressWarnings("unchecked")
	public User getUsersAssistente(Responsavel responsavel) {
		return (User) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				User user = null;
				if(CommonsUtil.semValor(responsavel.getResponsavelAssistenteComercial())){
					return null;
				}
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
			
					ps = connection.prepareStatement(QUERY_GET_ASSISTENTE);	
					
					ps.setString(1, responsavel.getResponsavelAssistenteComercial().getCodigo());
					rs = ps.executeQuery();
			
					UserDao userDao = new UserDao();
					while (rs.next()) {
						user = userDao.findById(rs.getLong(1));
						/*if(!user.getListResponsavel().contains(responsavel)) {
							user.getListResponsavel().add(responsavel);
							userDao.merge(user);
						}*/
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return user;
			}
		});	
	}
	
}
