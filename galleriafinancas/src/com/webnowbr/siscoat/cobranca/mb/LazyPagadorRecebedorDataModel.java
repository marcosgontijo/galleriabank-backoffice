package com.webnowbr.siscoat.cobranca.mb;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;

public class LazyPagadorRecebedorDataModel extends LazyDataModel<PagadorRecebedor> {
 
    private List<PagadorRecebedor> datasource;
 
    public LazyPagadorRecebedorDataModel(List<PagadorRecebedor> datasource) {
        this.datasource = datasource;
    }
 
    @Override
    public PagadorRecebedor getRowData(String rowKey) {
        for (PagadorRecebedor pagadorRecebedor : datasource) {
            if (String.valueOf(pagadorRecebedor.getId()).equals(rowKey)) {
                return pagadorRecebedor;
            }
        }
 
        return null;
    }
 
    @Override
    public Object getRowKey(PagadorRecebedor pagadorRecebedor) {
        return pagadorRecebedor.getId();
    }
 
    @Override
	public List<PagadorRecebedor> load(final int first, final int pageSize, final String sortField,
		final SortOrder sortOrder, final Map<String, Object> filters) {

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		setRowCount(pagadorRecebedorDao.count(filters));
		return pagadorRecebedorDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
	}
}