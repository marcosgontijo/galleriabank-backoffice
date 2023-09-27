package com.webnowbr.siscoat.cobranca.db.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import com.webnowbr.siscoat.cobranca.db.model.ContratoTipoTemplate;
import com.webnowbr.siscoat.db.dao.HibernateDao;

public class ContratoTipoTemplateDao extends HibernateDao<ContratoTipoTemplate, Long> {

	private static final String QUERY_GET_TEMPLATE = "select id from cobranca.contrato_tipo_template "
			+ "where codigoTipoTemplate = ? ";

	@SuppressWarnings("unchecked")
	public ContratoTipoTemplate getTemplate(final String codigoTipoTemplate) {
		return (ContratoTipoTemplate) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {

				Collection<ContratoTipoTemplate> objects = new ArrayList<ContratoTipoTemplate>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_GET_TEMPLATE);

					ps.setString(1, codigoTipoTemplate);

					rs = ps.executeQuery();

					while (rs.next()) {
						return findById(rs.getLong("id"));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Collection<ContratoTipoTemplate> getTemplateBloco(final String codigoTipoTemplate) {
		return (Collection<ContratoTipoTemplate>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {

				Collection<ContratoTipoTemplate> objects = new ArrayList<ContratoTipoTemplate>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_GET_TEMPLATE);

					ps.setString(1, codigoTipoTemplate);

					rs = ps.executeQuery();

					while (rs.next()) {
						objects.add(findById(rs.getLong("id")));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}

}
