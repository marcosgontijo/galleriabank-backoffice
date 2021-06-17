package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.db.dao.*;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ResponsavelDao extends HibernateDao <Responsavel,Long> {
	
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
}
