package com.webnowbr.siscoat.infra.mb;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.webnowbr.siscoat.infra.db.dao.TermoDao;
import com.webnowbr.siscoat.infra.db.dao.UserPerfilDao;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.UserPerfil;

/** ManagedBean. */
@ManagedBean(name = "termoMB")
@SessionScoped
public class TermoMB {

	/** Controle dos dados da Paginação. */
	private LazyDataModel<Termo> lazyModel;

	private Termo objetoTermo;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;

	private List<UserPerfil> perfil;

	public TermoMB() {

		lazyModel = new LazyDataModel<Termo>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Termo> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				TermoDao termoDao = new TermoDao();

				filters.put("termo", "false");

				setRowCount(termoDao.count(filters));
				return termoDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};
	}

	public String clearFields() {
		objetoTermo = new Termo();

		this.tituloPainel = "Adicionar";

		carregaListaPerfil();
		return "/Cadastros/Cobranca/TermoConsultar.xhtml";

	}
	
	public String clearFieldsEditar() {
		objetoTermo = new Termo();

		this.tituloPainel = "Adicionar";

		carregaListaPerfil();
		return "/Cadastros/Cobranca/TemoInserir.xhtml";

	}
	private void carregaListaPerfil() {
		if (perfil == null) {
			UserPerfilDao userPerfilDao = new UserPerfilDao();
			perfil = userPerfilDao.findAll().stream().sorted(Comparator.comparing(UserPerfil::getId))
					.collect(Collectors.toList());
		}
	}

	public LazyDataModel<Termo> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModel<Termo> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public Termo getObjetoTermo() {
		return objetoTermo;
	}

	public void setObjetoTermo(Termo objetoTermo) {
		this.objetoTermo = objetoTermo;
	}

	public List<UserPerfil> getPerfil() {
		return perfil;
	}

	public void setPerfil(List<UserPerfil> perfil) {
		this.perfil = perfil;
	}
	
	
}
