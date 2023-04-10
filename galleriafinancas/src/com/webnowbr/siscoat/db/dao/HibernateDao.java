package com.webnowbr.siscoat.db.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.JDBCConnectionException;

import com.webnowbr.siscoat.common.LoggerFactory;

/**
 * Hibernate implementation of the DAO interface.
 * @param <T> DAO Data type.
 * @param <ID> DAO id type.
 */
public abstract class HibernateDao<T, ID extends Serializable> implements DAO<T, ID> {
    /** 
     * Logger.
     */
    private static final Log LOG = LoggerFactory.getLogger();

    /**
     * Ascending sort order for pages.
     */
    private static final String SORT_ORDER_ASCENDING = "ASCENDING";

    /**
     * The persistent class used by this DAO.
     */
    private final Class<T> mPersistentClass;

	/**
	 * The Hibernate session factory.
	 */
	private static SessionFactory sSessionFactory;
	
	/**
	 * Lazy loading of the SessionFactory. It would be better to make this loading in
	 * the static initialization block. The problem is that in some situations (portal..)
	 * the sessionFactory inside {@link HibernateFactory} hadn't been initialized yet.
	 * So it was opted for this lazy loading approach.
	 * 
	 * @return
	 */
	private static SessionFactory getStaticSessionFactory() {
    	if(sSessionFactory == null) {
    		try {
    			sSessionFactory = HibernateFactory.getSessionFactory();
    		} catch (Exception e) {
    			LOG.error("Error while creating session factory", e);
    		}
    	}
		return sSessionFactory;
	}

    /**
     * Constructor. To use HibernateDao, create a child class with concrete parameters. Then there are two options to
     * use the DAO:
     * 
     * <pre>
     * 1 - One session/transaction per request. Ex:
     * // This will open a session, do the update and close the session.
     * ABCDao abcDao = new ABCDao();
     * abcDao.update(abc);
     * 
     * 2 - A long transaction for several requests (use this option if operations are dependent on one another or to
     * increase performance of multiple operations. But avoid leaving the session open for too long). Ex:
     * try{
     *      HibernateDAO.startTransaction();
     *      ABCDao abcDao = new ABCDao();
     *      ABC abc = abcDao.findById(1);
     *      DEFDao defDao = new DEFDao();
     *      DEF def = defDao.findById(1);
     *      abc.setSomething(def.getSomething());
     *      abcDao.update(abc);
     *      defDao.delete(def);
     * } catch(Exception e){
     *      HibernateDAO.rollbackTransaction();
     * } finally {
     *      HibernateDAO.commitTransaction()
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public HibernateDao() {
        mPersistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Starts a transaction with the database. When calling this method it is the client's responsibility to call
     * {@link commitTransaction} or {@link rollbackTransaction} when finished. The client does not need to open a
     * session. The DAO will do this when the transaction is open.
     */
    public static void startTransaction() {
    	getStaticSessionFactory().getCurrentSession().beginTransaction();
    }

    /**
     * Commits the transaction previously started by {@link startTransaction}, flushing all changes to database. This
     * method will also close the outstanding session.
     */
    public static void commitTransaction() {
    	if (getStaticSessionFactory().getCurrentSession().getTransaction().isActive()) {
    		getStaticSessionFactory().getCurrentSession().getTransaction().commit();
    	}
    }

    /**
     * Rolls back the transaction previously started by {@link startTransaction}, canceling all changes done since
     * {@link startTransaction} has been called. This method will also close the outstanding session.
     */
    public static void rollbackTransaction() {
    	if (getStaticSessionFactory().getCurrentSession().getTransaction().isActive()) {
    		getStaticSessionFactory().getCurrentSession().getTransaction().rollback();
    	}
    }

    /**
     * Returns the session factory.
     * @return The session factory.
     */
    protected final SessionFactory getSessionFactory() {
        return getStaticSessionFactory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID create(final T entity) {
        return (ID) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
                ID id = (ID) getStaticSessionFactory().getCurrentSession().save(entity);
                getStaticSessionFactory().getCurrentSession().flush();
                return id;
            }
        });
    }

    /**
     * Merges all changes to the given entity.
     * @param entity The entity.
     */
    public void merge(final T entity) {
        executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
            	getStaticSessionFactory().getCurrentSession().merge(entity);
            	getStaticSessionFactory().getCurrentSession().flush();
                return null;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public T findById(final ID id) {
        return (T) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
                return getStaticSessionFactory().getCurrentSession().get(mPersistentClass, id);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return (List<T>) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
                Criteria criteria = getStaticSessionFactory().getCurrentSession().createCriteria(mPersistentClass);
                criteria.addOrder(Order.asc("id")); // by default the list is ordered by id.
                return criteria.list();
            }
        });
    }

    /**
     * Counts all instances that match the given filter (or all instances if filters is null).
     * @param filters The filters to be used.
     * @return The number of instances.
     */
    public int count(final Map<String, Object> filters) {
        return (Integer) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
                Criteria criteria = getStaticSessionFactory().getCurrentSession().createCriteria(mPersistentClass);
                criteria.setProjection(Projections.rowCount());
                buildCriteria(criteria, filters);
                return ((Long) criteria.uniqueResult()).intValue();
            }

            @Override
            protected Object getErrorReturnValue() {
                return 0;
            }
        });
    }

    /**
     * Builds an hibernate criteria object based on the given filters.
     * @param criteria The criteria to be built.
     * @param filters The filters.
     */
    private void buildCriteria(final Criteria criteria, final Map<String, Object> filters) {
        if (filters != null) {
            for (String propertyName : filters.keySet()) {
                String filterValue = (String) filters.get(propertyName);

                Class<?> type = getPropertyType(mPersistentClass, propertyName);

                if (type != null) {
                    String[] propertyNames = propertyName.split("\\.");
                    if (propertyNames.length > 1) {
                        criteria.createAlias(propertyNames[0], propertyNames[0]);
                        type = getPropertyType(type, propertyNames[1]);
                    }

                    if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                        criteria.add(Restrictions.eq(propertyName, Long.parseLong(filterValue.replace("\"", ""))));
                    } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                        criteria.add(Restrictions.eq(propertyName, Integer.parseInt(filterValue.replace("\"", ""))));
                    } else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
                        criteria.add(Restrictions.eq(propertyName, Short.parseShort(filterValue.replace("\"", ""))));
                    } else if (String.class.isAssignableFrom(type)) {
                        //criteria.add(Restrictions.ilike(propertyName, "%" + filterValue + "%"));                        		
                        criteria.add(Restrictions.ilike(propertyName,  "%" + filterValue + "%" , MatchMode.ANYWHERE));

                    } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                        criteria.add(Restrictions.eq(propertyName,
                                Boolean.parseBoolean(filterValue) || filterValue.equals("1")));
                    }
                }
            }
        }
    }

    /**
     * Returns the property type.
     * @param propertyClass The property class.
     * @param propertyName The property name.
     * @return The property type.
     */
    private Class<?> getPropertyType(final Class<?> propertyClass, final String propertyName) {
        String[] propertyNames = propertyName.split("\\.");

        final String getMethod = "get" + propertyNames[0].toLowerCase();
        final String isMethod = "is" + propertyNames[0].toLowerCase();

        Method[] methods = propertyClass.getMethods();
        for (Method method : methods) {
            if (method.getName().toLowerCase().contains(getMethod) || method.getName().toLowerCase().contains(isMethod)) {
                return method.getReturnType();
            }
        }

        return null;
    }

    /**
     * Returns a list of entities based on the provided filter. The list will start at startPosition and will contain at
     * most maxResults items. Results will also be be sorted by sortField.
     * @param startPosition The first result to retrieve, starting from 0.
     * @param maxResults The maximum number of results.
     * @param sortField The field name to be used when sorting.
     * @param sortOrder The sorting order ("ASCENDING" or "DESCENDING")
     * @param filters The filters to use.
     * @return The entity list.
     */
    @SuppressWarnings({"unchecked" })
    public final List<T> findByFilter(final int startPosition, final int maxResults, final String sortField,
            final String sortOrder, final Map<String, Object> filters) {
        return (List<T>) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
                LOG.info("Loading the lazy " + mPersistentClass.getSimpleName() + " data between " + startPosition
                        + " and " + (startPosition + maxResults));
                Criteria criteria = getStaticSessionFactory().getCurrentSession().createCriteria(mPersistentClass);
                buildCriteria(criteria, filters);
                String modifiedSortField = sortField;
                if (sortField != null) {
                    int index = sortField.indexOf(".");
                    if (index >= 0) {
                        if (!filters.containsKey(sortField)) {
                            // If the filter contains the sort field, its alias has already been added by buildCriteria.
                            String sortRoot = sortField.substring(0, index);
                            criteria.createAlias(sortRoot, sortRoot);
                        }
                        modifiedSortField = sortField.substring(index + 1);
                    }
                }

                if (sortOrder.equals(SORT_ORDER_ASCENDING)) {
                    criteria.addOrder(Order.asc((modifiedSortField != null) ? modifiedSortField
                            : getDefaultSortPropertyName()));
                } else {
                    criteria.addOrder(Order.desc((modifiedSortField != null) ? modifiedSortField
                            : getDefaultSortPropertyName()));
                }
                criteria.setFirstResult(startPosition);
                criteria.setMaxResults(maxResults);

                return criteria.list();
            }
        });
    }

    /**
     * Returns a list of entities based on the provided property/value filter.
     * @param propertyName The property name to compare.
     * @param value The value to compare.
     * @return The entity list.
     */
    public List<T> findByFilter(final String propertyName, final Object value) {
        return findByCriteria(Restrictions.eq(propertyName, value));
    }

    /**
     * Returns a list of entities based on the provided property/value filter.
     * @param filters The property/value filters to use.
     * @return The entity list.
     */
    public List<T> findByFilter(final Map<String, Object> filters) {
        return findByCriteria(Restrictions.allEq(filters));
    }

    /**
     * Returns the default property name to be used when ordering results for this dao. Returns "id" by default and must
     * be overridden by child classes.
     * @return The column.
     */
    protected String getDefaultSortPropertyName() {
        return "id";
    }

    /**
     * Returns the list of instances based on the provided criteria.
     * @param criterion The criterion list.
     * @return The list of entities.
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(final Criterion... criterion) {
        return (List<T>) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
                Criteria criteria = getStaticSessionFactory().getCurrentSession().createCriteria(mPersistentClass);
                for (Criterion c : criterion) {
                    criteria.add(c);
                }
                List<T> results = criteria.list();
                return (results == null) ? new ArrayList<T>() : results;
            }
        });
    }

    /**
     * TODO Realiza a busca de uma classe buscando registros que possuam valores nulos na coluna passada como parâmetro.
     * @param nullColumns - Columns with null values.
     * @param nonNullColumns - Columns with non null values.
     * @return List<T> - Found objects.
     * @author Hermes Vieira Jr.
     */
    public List<T> findByNullOrNotNull(final List<String> nullColumns, final List<String> nonNullColumns) {
        List<Criterion> criteria = new ArrayList<Criterion>();

        if (nullColumns != null) {
            for (String column : nullColumns) {
                criteria.add(Restrictions.isNull(column));
            }
        }
        if (nonNullColumns != null) {
            for (String column : nonNullColumns) {
                criteria.add(Restrictions.isNotNull(column));
            }
        }

        return findByCriteria(criteria.toArray(new Criterion[criteria.size()]));
    }
    
    @SuppressWarnings("unchecked")
	public T findByHightestValue(final String field) {
        return (T) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
            	final Criteria criteria = getStaticSessionFactory().getCurrentSession().createCriteria(mPersistentClass);

            	criteria.addOrder(Order.desc(field));
            	criteria.setMaxResults(1);
            	
            	final List<T> results = criteria.list();
            	
            	return (results.size() > 0) ? results.get(0) : null;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
	public List<T> findByLE(final String field, final Object value) {
        return (List<T>) executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
            	final Criteria criteria = getStaticSessionFactory().getCurrentSession().createCriteria(mPersistentClass);
            	criteria.add(Restrictions.le(field, value));          	
            	
            	final List<T> results = criteria.list();
            	
            	return (results == null) ? new ArrayList<T>() : results;
            }
        });
    }

    @Override
    public void update(final T entity) {
        executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
            	getStaticSessionFactory().getCurrentSession().update(entity);
            	getStaticSessionFactory().getCurrentSession().flush();
                return null;
            }
        });
    }

    @Override
    public void delete(final T entity) {
        executeDBOperation(new DBRunnable() {
            @Override
            public Object run() {
            	getStaticSessionFactory().getCurrentSession().delete(entity);
            	getStaticSessionFactory().getCurrentSession().flush();
                return null;
            }
        });
    }

    /**
     * Helper class to run a database operation.
     */
    public abstract class DBRunnable {
        /**
         * Runs the database operation specific code.
         * @return The operation result.
         */
        protected abstract Object run() throws Exception;

        /**
         * Returns the value to be used in case of errors. Default implementation returns null.
         * @return The value to be used in case of errors.
         */
        protected Object getErrorReturnValue() {
            return null;
        }
    }

    /**
     * Executes a database operation returning the result provided by {@link DBRunnable}. This method will open the
     * session and transaction if necessary and will close both at the end.
     * @param operation The operation to be run.
     * @return The operation result.
     */
    protected Object executeDBOperation(final DBRunnable operation) {
        Object result = operation.getErrorReturnValue();
        Session session = null;
        Transaction transaction = null;
        boolean isOurTransaction = false;
        Exception exception = null;
        try {
            // Session is open here.
            session = getStaticSessionFactory().getCurrentSession();
            try {
                // This is a little hack. getTransaction will throw an exception if beginTransaction has not been
                // called. So far this is the only way to find out if the transaction is already created.
                transaction = session.getTransaction();

                if (!transaction.isActive()) {
                    transaction = session.beginTransaction();
                    isOurTransaction = true;
                }
            } catch (Exception e) {
                transaction = session.beginTransaction();
                isOurTransaction = true;
            }

            result = operation.run();

            // Only close the transaction if it has been created here.
            if (isOurTransaction) {
                // Session will be closed here.
                transaction.commit();
            }
        } catch (JDBCConnectionException e) {
            exception = e;
            throw new DBConnectionException(e);
        } catch (Exception e) {
            exception = e;
            throw new DAOException(e);
        } finally {
            if (exception != null) {
                LOG.error("Exception executing DB Operation:" + exception.getMessage());
                if (isOurTransaction) {
                    // Session will be closed here.
                    transaction.rollback();
                    LOG.error("Rolling back all operations.");
                }
            }
        }

        return result;
    }
    
	/**
	 * Provides a raw {@link Connection}. Important to notice that this
	 * connection and the resources created from it (PreparedStatement,
	 * ResultSet..) must be close after using them.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected Connection getConnection() {
		return getSessionFactory().getCurrentSession().connection();
	}

	/**
	 * Closes a {@link Connection} and a {@link PreparedStatement}. Null values
	 * can be informed safely.
	 * 
	 * @param connection
	 * @param ps
	 * @throws SQLException
	 */
	protected void closeResources(Connection connection, PreparedStatement ps)
			throws SQLException {
		closeResources(connection, ps, null);
	}

	/**
	 * Closes a {@link Connection}, a {@link PreparedStatement} and a
	 * {@link ResultSet}. Null values can be informed safely.
	 * 
	 * @param connection
	 * @param ps
	 * @param rs
	 * @throws SQLException
	 */
	protected void closeResources(Connection connection, PreparedStatement ps,
			ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		if (connection != null) {
			connection.close();
		}
	}

}