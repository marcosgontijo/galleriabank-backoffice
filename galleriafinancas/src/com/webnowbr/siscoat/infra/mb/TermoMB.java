package com.webnowbr.siscoat.infra.mb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.TermoDao;
import com.webnowbr.siscoat.infra.db.dao.TermoUsuarioDao;
import com.webnowbr.siscoat.infra.db.dao.UserPerfilDao;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.TermoUsuario;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.db.model.UserPerfil;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "termoMB")
@SessionScoped
public class TermoMB {
	

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	/** Controle dos dados da Paginação. */
	private LazyDataModel<Termo> lazyModel;

	private Termo objetoTermo;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private String tituloPainel = null;
	private String pathArquivoAnteriorSalvo = null;
	private String arquivoAnteriorSalvo = null;
	private StreamedContent pdfContent;
	List<Termo> termos = new ArrayList<>();
	int itermo = 0;

	private UploadedFile file;

	private List<UserPerfil> perfil;
	Optional<UserPerfil> userPerfilPublico;

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

		this.tituloPainel = "Consultar";

		carregaListaPerfil();
		return "/Cadastros/Cobranca/TermoConsultar.xhtml";

	}

	public String clearFieldsEditar() {

		if (objetoTermo == null) {
			objetoTermo = new Termo();
			if (userPerfilPublico == null)
				carregaListaPerfil();
			objetoTermo.setUserPerfil(userPerfilPublico.get());
			this.tituloPainel = "Inserir";
		} else
			this.tituloPainel = "Editar";

		carregaListaPerfil();
		return "/Cadastros/Cobranca/TermoInserir.xhtml";

	}

	private void carregaListaPerfil() {
		if (perfil == null) {
			UserPerfilDao userPerfilDao = new UserPerfilDao();
			perfil = userPerfilDao.findAll().stream().sorted(Comparator.comparing(UserPerfil::getId))
					.collect(Collectors.toList());

			userPerfilPublico = perfil.stream().filter(p -> p.getId() == 1000l).findFirst();
		}
	}

	/***
	 * handler de upload do arquivo
	 * 
	 * @param event
	 * @throws IOException
	 */
	public boolean validaFileUpload() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (CommonsUtil.semValor(file)) {
			if (!CommonsUtil.semValor(this.objetoTermo.getArquivo())) {
				return true;
			} else if (!CommonsUtil.semValor(pathArquivoAnteriorSalvo)) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Termo: novo arquivo não enviado", "novo arquivo não enviado"));
				return false;
			}

		}

		// recupera local onde será gravado o arquivo
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "ARQUIVOS_TERMO").get(0).getValorString();

		File diretorio = new File(pathContrato);
		if (!diretorio.isDirectory()) {
			diretorio.mkdir();
		}

		if (new File(pathContrato + file.getFileName()).exists()) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Termo: arquivo já existente",
					"Arquivo " + file.getFileName() + " já existente no diretorio"));
			return false;
		}

		// cria o arquivo
		if (!file.getFileName().endsWith(".pdf")) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Termo: arquivo não é pdf",
					" somente é possível anexar .pdf"));
			return false;
		} else {
			byte[] conteudo = file.getContents();
			FileOutputStream fos;
			try {

				fos = new FileOutputStream(pathContrato + file.getFileName());
				fos.write(conteudo);
				fos.close();

			} catch (Exception e) {
				System.out.println(e);
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Termo: erro ao salvar arquivo", e.getMessage()));
				return false;
			}

			this.objetoTermo.setArquivo(file.getFileName());
			this.objetoTermo.setPath(pathContrato + file.getFileName());
			return true;
		}
	}

	public String trocarArquivo() {
		if (!CommonsUtil.semValor(this.objetoTermo.getPath()) && CommonsUtil.semValor(pathArquivoAnteriorSalvo))
			pathArquivoAnteriorSalvo = this.objetoTermo.getPath();
		arquivoAnteriorSalvo = this.objetoTermo.getPath();

		this.objetoTermo.setArquivo(null);
		this.objetoTermo.setPath(null);
		return null;
	}

	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance();
		TermoDao termoDao = new TermoDao();
		String msgRetorno = null;

		try {
			if (!validaFileUpload())
				return "";

			if (CommonsUtil.semValor(objetoTermo.getId())) {
				termoDao.create(objetoTermo);
				msgRetorno = "inserido";
			} else {
				termoDao.merge(objetoTermo);
				msgRetorno = "atualizado";
			}

			if (!CommonsUtil.semValor(pathArquivoAnteriorSalvo)) {
				new File(pathArquivoAnteriorSalvo).delete();
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Termo: Registro " + msgRetorno + " com sucesso! (Usuário: " + objetoTermo.getIdentificacao() + ")",
					""));

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo: " + objetoTermo.getIdentificacao(), e.getMessage()));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo: " + objetoTermo.getIdentificacao(), e.getMessage()));

			return "";
		}

		return "TermoConsultar.xhtml";
	}

	public List<Termo> termosNaoAssinadosUsuario(User usuario) {
		TermoDao termoDao = new TermoDao();
		return termoDao.findAll();
	}
	


	public String verificaTermosNaoAssinadoso() throws IOException {

		if (CommonsUtil.semValor(termos)) {
			termos = termosNaoAssinadosUsuario(loginBean.getUsuarioLogado());
			itermo = 0;
			if (!CommonsUtil.semValor(termos)) {
				PrimeFaces.current().executeScript("PF('dlgTermos').show();");
			}
		}
		return null;
	}


	public String carregaPdfTermo() throws IOException {

		if (CommonsUtil.semValor(termos)) {
			termos = termosNaoAssinadosUsuario(loginBean.getUsuarioLogado());
			itermo = 0;
			if (!CommonsUtil.semValor( termos )) {
				PrimeFaces.current().executeScript("PF('dlgTermos').show();");
			}
		}

		if (itermo > termos.size() - 1) {
			PrimeFaces.current().executeScript("PF('dlgTermos').hide();");
			return null;
		}

		InputStream fis = null;
		try {
			fis = new FileInputStream(new File(termos.get(itermo).getPath()));

			pdfContent = new DefaultStreamedContent(fis, "application/pdf", termos.get(itermo).getArquivo());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "data:application/pdf;base64, " + Base64.getEncoder().encodeToString(IOUtils.toByteArray(fis));
	}

	public String aceitar() throws IOException {

		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();
		TermoUsuario termoUsuario = new TermoUsuario();
		termoUsuario.setDataAceite(DateUtil.getDataHoraAgora());
		termoUsuario.setIdTermo(termos.get(itermo).getId());
		termoUsuario.setIdUsuario(loginBean.getUsuarioLogado().getId());
		termoUsuario.setIdx(0);
		termoUsuarioDao.create(termoUsuario);
		
		itermo++;
		
		return null;
	}
	
	

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public StreamedContent getPdfContent() {
		return pdfContent;
	}

	public void setPdfContent(StreamedContent pdfContent) {
		this.pdfContent = pdfContent;
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

	public String getTituloPainel() {
		return tituloPainel;
	}

	public void setTituloPainel(String tituloPainel) {
		this.tituloPainel = tituloPainel;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isDeleteMode() {
		return deleteMode;
	}

	public void setDeleteMode(boolean deleteMode) {
		this.deleteMode = deleteMode;
	}

}
