package com.webnowbr.siscoat.infra.mb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.db.model.TermoPopup;
import com.webnowbr.siscoat.cobranca.db.op.ImovelEstoqueDao;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.cobranca.service.ScrService;
import com.webnowbr.siscoat.cobranca.vo.FileGenerator;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.TermoDao;
import com.webnowbr.siscoat.infra.db.dao.TermoUsuarioDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.dao.UserPerfilDao;
import com.webnowbr.siscoat.infra.db.model.Termo;
import com.webnowbr.siscoat.infra.db.model.TermoUsuario;
import com.webnowbr.siscoat.infra.db.model.TermoUsuarioVO;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.db.model.UserPerfil;
import com.webnowbr.siscoat.infra.db.model.UserVO;
import com.webnowbr.siscoat.infra.db.model.UserVO;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "termoMB")
@SessionScoped
public class TermoMB {

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	/** Controle dos dados da Paginação. */
	private LazyDataModel<Termo> lazyModel;
	private TermoUsuario termoUsuario;
	private TermoUsuario termoUsuario;
	private Termo objetoTermo;
	private String base64imagem;
	private TermoUsuario objetoTermoUsuario;
	private String base64imagem;
	private TermoUsuario objetoTermoUsuario;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private boolean btnAceiteDesativado = false;
	private String tituloPainel = null;
	private String pathArquivoAnteriorSalvo = null;
	private String arquivoAnteriorSalvo = null;
	private StreamedContent pdfContent;
	private Termo termoUsuarioPopup;
	private String nomeUsuario;
	private String nomeUsuario;
	List<Termo> termos = new ArrayList<>();
	int itermo = -1;

	private UploadedFile file;
	private String idPerfilSelecionado;
	private String idPerfilSelecionado;

	private List<UserPerfil> perfil;
	Optional<UserPerfil> userPerfilPublico;
	Optional<UserPerfil> userPerfilIndividual;
	private List<User> users;
	private List<User> selectedUsers;

	private List<TermoUsuario> termosUsuario;
	private List<TermoPopup> usuarios = new ArrayList<>();
	private List<User> todosUsuario;
	List<UserVO> usuariosVinculados = new ArrayList<>();
	List<UserVO> listaOrigem = new ArrayList<>();
	List<UserVO> listaDestino = new ArrayList<>();
	List<UserVO> listaExcluir = new ArrayList<>();
	List<String> listaNomes = new ArrayList<>();

	private TermoUsuarioVO usuarioVO;
	User usuarioNew = null;
	Date dataAceite = null;

	private DualListModel<UserVO> listaDeUsuariosPickList;

	public DualListModel<UserVO> getListaDeUsuariosPickList() {
		return listaDeUsuariosPickList;
	}

	public void setListaDeUsuariosPickList(DualListModel<UserVO> listaDeUsuariosPickList) {
		this.listaDeUsuariosPickList = listaDeUsuariosPickList;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<User> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<User> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
	Optional<UserPerfil> userPerfilIndividual;
	private List<User> users;
	private List<User> selectedUsers;

	private List<TermoUsuario> termosUsuario;
	private List<TermoPopup> usuarios = new ArrayList<>();
	private List<User> todosUsuario;
	List<UserVO> usuariosVinculados = new ArrayList<>();
	List<UserVO> listaOrigem = new ArrayList<>();
	List<UserVO> listaDestino = new ArrayList<>();
	List<UserVO> listaExcluir = new ArrayList<>();
	List<String> listaNomes = new ArrayList<>();

	private TermoUsuarioVO usuarioVO;
	User usuarioNew = null;
	Date dataAceite = null;

	private DualListModel<UserVO> listaDeUsuariosPickList;

	public DualListModel<UserVO> getListaDeUsuariosPickList() {
		return listaDeUsuariosPickList;
	}

	public void setListaDeUsuariosPickList(DualListModel<UserVO> listaDeUsuariosPickList) {
		this.listaDeUsuariosPickList = listaDeUsuariosPickList;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<User> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<User> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	public TermoMB() {

		lazyModel = new LazyDataModel<Termo>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Termo> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				TermoDao termoDao = new TermoDao();

				filters.put("termo", "false");
				filters.put("deletado", "false");
				filters.put("deletado", "false");

				setRowCount(termoDao.count(filters));


				return termoDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};
	}
	

	private List<TermoUsuario> termosUsuario;
	private List<TermoPopup> usuarios = new ArrayList<>();

	private TermoUsuarioVO usuarioVO;
	User usuarioNew = null;
	Date dataAceite = null;

	public void listaUsuario(Long id) {
		usuarios.clear();
		TermoUsuarioDao termoDao = new TermoUsuarioDao();
		UserDao usuario = new UserDao();
		TermoUsuarioVO TermoUsuariovo = new TermoUsuarioVO();
		termosUsuario = termoDao.findByFilter("idTermo", id);

		for (TermoUsuario user : termosUsuario) {

			User userPesquisa = usuario.findById(user.getIdUsuario());
			TermoUsuariovo.setDataAceite(user.getDataAceite());
			TermoUsuariovo.setUsuario(userPesquisa);
			usuarios.add(new TermoPopup(TermoUsuariovo.getUsuario().getName(), CommonsUtil.formataDataHora(TermoUsuariovo.getDataAceite())));

		}

	}

	private boolean pdfGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent filePDF;

	@SuppressWarnings("deprecation")
	public StreamedContent geraPDF(Long id) {
		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();
		UserDao userDao = new UserDao();
	@SuppressWarnings("deprecation")
	public StreamedContent geraPDF(Long id) {
		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();
		UserDao userDao = new UserDao();
		Document document = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		termosUsuario = termoUsuarioDao.findByFilter("idTermo", id );
		
		try {
            // Criar um novo documento PDF
			 document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            Paragraph paragrafo = new Paragraph();
            Font fonteNegrito = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);
            paragrafo.setAlignment(Element.ALIGN_CENTER);
            paragrafo.add(new Paragraph(objetoTermo.getIdentificacao() + " - Assinantes", fonteNegrito));
            document.add(paragrafo);
            Paragraph spacer = new Paragraph("");
            spacer.setSpacingAfter(20f);
            document.add(spacer);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            for(TermoUsuario user : termosUsuario) {
            	User usuario = userDao.findById(user.getIdUsuario());
            Paragraph	paragrafoUsuario = new Paragraph( usuario.getName());
            paragrafoUsuario.setAlignment(Element.ALIGN_RIGHT);
            table.addCell(paragrafoUsuario);
            Paragraph paragrafoData = new Paragraph( CommonsUtil.formataDataHora(user.getDataAceite()));
            paragrafoData.setAlignment(Element.ALIGN_LEFT);
            table.addCell(paragrafoData);
           
            }
            document.add(table);
            // Fechar o documento
            document.close();

            System.out.println("PDF gerado com sucesso!");


			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format("Galleria Bank - Termos Assinantes.pdf", "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(baos.toByteArray()));
			gerador.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public User getUsuarioNew() {
		return usuarioNew;
	}

	public void setUsuarioNew(User usuarioNew) {
		this.usuarioNew = usuarioNew;
	}

	public List<TermoUsuario> getTermosUsuario() {
		return termosUsuario;
	}

	public void setTermosUsuario(List<TermoUsuario> termosUsuario) {
		this.termosUsuario = termosUsuario;
	}

	public String clearFields() {

		this.tituloPainel = "Consultar";

		carregaListaPerfil();
		return "/Cadastros/Cobranca/TermoConsultar.xhtml";

	}

	public String clearFieldsEditar() {
		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();
		TermoDao termoDao = new TermoDao();
		UserDao userdao = new UserDao();

		this.todosUsuario = userdao.carregarUsuariosLista();

		listaOrigem = new ArrayList<>();
		listaDestino = new ArrayList<>();
		listaExcluir = new ArrayList<>();
		listaOrigem.addAll(todosUsuario.stream()
				.map(v -> new UserVO(v.getId(), v.getName())).collect(Collectors.toList()));
		carregaListaPerfil();
	

		this.listaDeUsuariosPickList = new DualListModel<>(this.listaOrigem, this.listaDestino);
		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();
		TermoDao termoDao = new TermoDao();
		UserDao userdao = new UserDao();

		this.todosUsuario = userdao.carregarUsuariosLista();

		listaOrigem = new ArrayList<>();
		listaDestino = new ArrayList<>();
		listaExcluir = new ArrayList<>();
		listaOrigem.addAll(todosUsuario.stream()
				.map(v -> new UserVO(v.getId(), v.getName())).collect(Collectors.toList()));
		carregaListaPerfil();
	

		this.listaDeUsuariosPickList = new DualListModel<>(this.listaOrigem, this.listaDestino);

		if (objetoTermo == null) {
			setNomeUsuario();
			setNomeUsuario();
			objetoTermo = new Termo();
			this.idPerfilSelecionado = CommonsUtil.stringValue(this.userPerfilPublico.get().getId());
			this.idPerfilSelecionado = CommonsUtil.stringValue(this.userPerfilPublico.get().getId());
			objetoTermo.setUserPerfil(userPerfilPublico.get());
			this.tituloPainel = "Inserir";
			objetoTermo.setDeletado(false);
			objetoTermo.setUsuarioCriador(nomeUsuario);

		} else {
			this.idPerfilSelecionado = CommonsUtil.stringValue(this.objetoTermo.getUserPerfil().getId());
			objetoTermo.setDeletado(false);
			objetoTermo.setUsuarioCriador(nomeUsuario);

		} else {
			this.idPerfilSelecionado = CommonsUtil.stringValue(this.objetoTermo.getUserPerfil().getId());
			this.tituloPainel = "Editar";
			usuariosVinculados = termoUsuarioDao.findUsersByTermoId(objetoTermo.getId()).stream()
					.map(v -> new UserVO(v.getId(), v.getName())).collect(Collectors.toList());
//			 listaOrigem.removeAll(usuariosVinculados);
//			 listaOrigem = listaOrigem.stream().filter(p -> !usuariosVinculados.stream().map(m -> m.getId()).collect(Collectors.toList()).contains(p.getId())).collect(Collectors.toList());
			List<Long> f = usuariosVinculados.stream().map(m -> m.getId()).collect(Collectors.toList());
			List<UserVO> listaOrigemvinc = listaOrigem.stream().filter(p -> f.contains(p.getId()))
					.map(v -> new UserVO(v.getId(), v.getName())).collect(Collectors.toList());

			listaOrigem.removeAll(listaOrigemvinc);

			listaDestino = usuariosVinculados;
			DualListModel<UserVO> novaListaDeUsuariosPickList = new DualListModel<>(listaOrigem, listaDestino);
			this.listaDeUsuariosPickList = novaListaDeUsuariosPickList;
		}

			usuariosVinculados = termoUsuarioDao.findUsersByTermoId(objetoTermo.getId()).stream()
					.map(v -> new UserVO(v.getId(), v.getName())).collect(Collectors.toList());
//			 listaOrigem.removeAll(usuariosVinculados);
//			 listaOrigem = listaOrigem.stream().filter(p -> !usuariosVinculados.stream().map(m -> m.getId()).collect(Collectors.toList()).contains(p.getId())).collect(Collectors.toList());
			List<Long> f = usuariosVinculados.stream().map(m -> m.getId()).collect(Collectors.toList());
			List<UserVO> listaOrigemvinc = listaOrigem.stream().filter(p -> f.contains(p.getId()))
					.map(v -> new UserVO(v.getId(), v.getName())).collect(Collectors.toList());

			listaOrigem.removeAll(listaOrigemvinc);

			listaDestino = usuariosVinculados;
			DualListModel<UserVO> novaListaDeUsuariosPickList = new DualListModel<>(listaOrigem, listaDestino);
			this.listaDeUsuariosPickList = novaListaDeUsuariosPickList;
		}

		return "/Cadastros/Cobranca/TermoInserir.xhtml";

	}
	public void excluirTermo(Termo objetoTermoExcluir) {
	    TermoDao termoDao = new TermoDao();
	    setNomeUsuario();
	    objetoTermoExcluir.setUsuarioDelete(nomeUsuario);
	    objetoTermoExcluir.setDataDelete(DateUtil.gerarDataHoje());
	    objetoTermoExcluir.setDeletado(true);
	    termoDao.merge(objetoTermoExcluir);    
	    FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "/Cadastros/Cobranca/TermoConsultar.xhtml?faces-redirect=true");
	}

	public void excluirTermo(Termo objetoTermoExcluir) {
	    TermoDao termoDao = new TermoDao();
	    setNomeUsuario();
	    objetoTermoExcluir.setUsuarioDelete(nomeUsuario);
	    objetoTermoExcluir.setDataDelete(DateUtil.gerarDataHoje());
	    objetoTermoExcluir.setDeletado(true);
	    termoDao.merge(objetoTermoExcluir);    
	    FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "/Cadastros/Cobranca/TermoConsultar.xhtml?faces-redirect=true");
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

	public List<Termo> termosNaoAssinadosUsuario(User usuario) {
		TermoDao termoDao = new TermoDao();
		return termoDao.termosNaoAssinadosUsuario(usuario);
	}

	public String verificaTermosNaoAssinados() throws IOException {
		UserDao usuerDao = new UserDao();
		// if (CommonsUtil.semValor(termos)) {
//			TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();
		termos = termosNaoAssinadosUsuario(loginBean.getUsuarioLogado());

		for (Termo termo : termos) {
			TermoUsuario termoUsuario = loginBean.getUsuarioLogado().getListTermos().stream()
					.filter(t -> CommonsUtil.mesmoValor(t.getIdTermo(), termo.getId())).findAny().orElse(null);
//				TermoUsuario termoUsuario = termoUsuarioDao.termosUsuario(termo, loginBean.getUsuarioLogado());				
			if (!CommonsUtil.semValor(termoUsuario)) {
				if (CommonsUtil.semValor(termoUsuario.getDataCiencia())) {
					termoUsuario.setDataCiencia(DateUtil.getDataHoraAgora());
				if (CommonsUtil.semValor(termoUsuario.getDataCiencia())) {
					termoUsuario.setDataCiencia(DateUtil.getDataHoraAgora());
//							termoUsuarioDao.merge(termoUsuario);
				}
			} else {
				if (termo.getId() > 0) {
					termoUsuario = new TermoUsuario();
					termoUsuario.setDataCiencia(DateUtil.getDataHoraAgora());
					termoUsuario.setDataCiencia(DateUtil.getDataHoraAgora());
					termoUsuario.setIdTermo(termo.getId());
					termoUsuario.setIdUsuario(loginBean.getUsuarioLogado().getId());
					loginBean.getUsuarioLogado().getListTermos().add(termoUsuario);
				}
			}

			termo.setTermoUsuario(termoUsuario);
		}
		usuerDao.merge(loginBean.getUsuarioLogado());

		itermo = 0;
		if (!CommonsUtil.semValor(termos)) {
			PrimeFaces.current().executeScript("PF('dlgTermos').show();");
		}

		if (!CommonsUtil.semValor(termos)) {
			PrimeFaces.current().executeScript("PF('dlgTermos').show();");
		}

		return null;
	}

	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance();
		TermoDao termoDao = new TermoDao();
		String msgRetorno = null;
		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();

		try {
			if (CommonsUtil.semValor(objetoTermo.getArquivo())) {
				if (!validaFileUpload())
					return "";
			}
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

			if (objetoTermo.getUserPerfil().getId() == 5000) {
				for (UserVO user : this.listaDestino) {
					if (CommonsUtil.semValor(termoUsuarioDao.findTermoUsuario(objetoTermo.getId(), user.getId()))) {
						TermoUsuario termoUsuario = new TermoUsuario();
						termoUsuario.setIdTermo(objetoTermo.getId());
						termoUsuario.setIdUsuario(user.getId());
						termoUsuarioDao.merge(termoUsuario);
					}
				}
				for (UserVO user : this.listaExcluir) {

					TermoUsuario termoUsuario = termoUsuarioDao.findTermoUsuario(objetoTermo.getId(), user.getId());
					if (!CommonsUtil.semValor(termoUsuario)) {
						termoUsuarioDao.delete(termoUsuario);
					}
				}
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", msgRetorno));

		} catch (DAOException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo: " + objetoTermo.getIdentificacao(), e.getMessage()));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo: " + objetoTermo.getIdentificacao(), e.getMessage()));

			return "";
		}
		// this.listaDestino = new ArrayList<>();

		return "TermoConsultar.xhtml";
	}

	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance();
		TermoDao termoDao = new TermoDao();
		String msgRetorno = null;
		TermoUsuarioDao termoUsuarioDao = new TermoUsuarioDao();

		try {
			if (CommonsUtil.semValor(objetoTermo.getArquivo())) {
				if (!validaFileUpload())
					return "";
			}
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

			if (objetoTermo.getUserPerfil().getId() == 5000) {
				for (UserVO user : this.listaDestino) {
					if (CommonsUtil.semValor(termoUsuarioDao.findTermoUsuario(objetoTermo.getId(), user.getId()))) {
						TermoUsuario termoUsuario = new TermoUsuario();
						termoUsuario.setIdTermo(objetoTermo.getId());
						termoUsuario.setIdUsuario(user.getId());
						termoUsuarioDao.merge(termoUsuario);
					}
				}
				for (UserVO user : this.listaExcluir) {

					TermoUsuario termoUsuario = termoUsuarioDao.findTermoUsuario(objetoTermo.getId(), user.getId());
					if (!CommonsUtil.semValor(termoUsuario)) {
						termoUsuarioDao.delete(termoUsuario);
					}
				}
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", msgRetorno));

		} catch (DAOException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo: " + objetoTermo.getIdentificacao(), e.getMessage()));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo: " + objetoTermo.getIdentificacao(), e.getMessage()));

			return "";
		}
		// this.listaDestino = new ArrayList<>();

		return "TermoConsultar.xhtml";
	}

	public String getDescricaoTermo() {
		try {
			if (!CommonsUtil.semValor(termos))
				return CommonsUtil.stringValueVazio(termos.get(itermo).getDescricao());
		} catch (Exception e) {
			System.out.println(termos.get(itermo).getIdentificacao() + " sem descricao ");
		}
		return null;
	}

	public String getInstrucaoTermo() {
		try {
			if (!CommonsUtil.semValor(termos))
				return CommonsUtil.stringValueVazio(termos.get(itermo).getInstrucao());
		} catch (Exception e) {
			System.out.println(termos.get(itermo).getIdentificacao() + " sem instrucao ");
		}
		return null;
	}

	public String getAceiteExpirado() {
		try {
			if (!CommonsUtil.semValor(termos))
				return CommonsUtil.stringValueVazio(termos.get(itermo).isAceiteExpirado());
		} catch (Exception e) {
			System.out.println(termos.get(itermo).getIdentificacao() + " sem descricao ");
		}
		return null;
	}

	public String carregaPdfTermo() throws IOException {

//		if (CommonsUtil.semValor(termos)) {
//			termos = termosNaoAssinadosUsuario(loginBean.getUsuarioLogado());
//			itermo = 0;
//			if (!CommonsUtil.semValor( termos )) {
//				PrimeFaces.current().executeScript("PF('dlgTermos').show();");
//			}
//		}

		if (itermo > termos.size() - 1) {
			PrimeFaces.current().executeScript("PF('dlgTermos').hide();");
			return null;
		}

		InputStream fis = null;
		byte[] bytes = null;
		try {
//			fis = new FileInputStream(new File(termos.get(itermo).getPath()));

			PDDocument doc = PDDocument.load(new File(termos.get(itermo).getPath()));
			PDFRenderer pdfRenderer = new PDFRenderer(doc);

			BufferedImage joinBufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

			for (int x = 0; x < doc.getNumberOfPages(); x++) {
				BufferedImage bImage = pdfRenderer.renderImageWithDPI(x, 115,
						org.apache.pdfbox.rendering.ImageType.RGB);
				joinBufferedImage = joinBufferedImage(joinBufferedImage, bImage);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(joinBufferedImage, "png", baos);
			bytes = baos.toByteArray();

//			InputStream is = new ByteArrayInputStream(bytes);
//
//			pdfContent = new DefaultStreamedContent(is, "image/png", termos.get(itermo).getArquivo());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(termos.get(itermo).getPath());
			e.printStackTrace();
		}
		String base64 = Base64.getEncoder().encodeToString(bytes);
		btnAceiteDesativado = false;
		return "data:image/png;base64," + base64;
	}

	private BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {

		// do some calculate first
		int offset = 5;
		int wid = Math.max(img1.getWidth(), img2.getWidth()) + offset;
		int height = img1.getHeight() + img2.getHeight() + offset;
		// create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = newImage.createGraphics();
		Color oldColor = g2.getColor();
		// fill background
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, wid, height);
		// draw image
		g2.setColor(oldColor);
		g2.drawImage(img1, null, 0, 0);
		g2.drawImage(img2, null, 0, img1.getHeight() + offset);
		g2.dispose();
		return newImage;
	}

	public String aceitar() throws IOException {
		UserDao usuerDao = new UserDao();
		TermoUsuario termoUsuario = loginBean.getUsuarioLogado().getListTermos().stream()
				.filter(t -> CommonsUtil.mesmoValor(t.getIdTermo(), termos.get(itermo).getId())).findAny().orElse(null);
//		TermoUsuario termoUsuario = termoUsuarioDao.termosUsuario(termos.get(itermo), loginBean.getUsuarioLogado());

		if (!CommonsUtil.semValor(termoUsuario)) {
			termoUsuario.setDataAceite(DateUtil.getDataHoraAgora());

//		termoUsuarioDao.merge(termoUsuario);
			usuerDao.merge(loginBean.getUsuarioLogado());
		}
		termos.remove(itermo);
		return null;
	}

	public String aceiteAdiado() throws IOException {
		UserDao usuerDao = new UserDao();
		TermoUsuario termoUsuario = loginBean.getUsuarioLogado().getListTermos().stream()
				.filter(t -> CommonsUtil.mesmoValor(t.getIdTermo(), termos.get(itermo).getId())).findAny().orElse(null);
//		TermoUsuario termoUsuario = termoUsuarioDao.termosUsuario(termos.get(itermo), loginBean.getUsuarioLogado());
		if (!CommonsUtil.semValor(termoUsuario)) {
			termoUsuario.setDataAdiado(DateUtil.getDataHoraAgora());
			usuerDao.merge(loginBean.getUsuarioLogado());
		}
//		termoUsuarioDao.merge(termoUsuario);
		termos.remove(itermo);
		return null;
	}
 private List<Termo> usuarioTermosAssinados = new ArrayList<>();
	public void consultaTermosAssinados() {
		TermoUsuarioDao dao = new TermoUsuarioDao();
		usuarioTermosAssinados = dao.termosAssinados(loginBean.getUsuarioLogado());
	
	}
	public void AbrirDocumentoTermo(Termo termo) throws IOException {

		Path arquivo = Paths.get(termo.getPath());

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		byte[] contrato = null;
		
			 try (PDDocument document = PDDocument.load(Files.newInputStream(arquivo))) {
		            PDFRenderer pdfRenderer = new PDFRenderer(document);

		            // Iterar sobre as páginas do PDF
		            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
		                // Renderizar a página como uma imagem
		                BufferedImage bim = pdfRenderer.renderImageWithDPI(pageIndex, 300);

		                // Criar um ByteArrayOutputStream para armazenar os bytes da imagem
		                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		                // Escrever a imagem como PNG no ByteArrayOutputStream
		                ImageIO.write(bim, "png", byteArrayOutputStream);

		                // Obter os bytes da imagem
		                contrato = byteArrayOutputStream.toByteArray();
		                byteArrayOutputStream.close();
			if (CommonsUtil.semValor(contrato)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Processos: Ocorreu um problema ao gerar a imagem!", ""));
				return ;
			} else {
		
			}
		            }
		        
			 }
		
			 String base64 = Base64.getEncoder().encodeToString(contrato);
			
			base64imagem = "data:image/png;base64," + base64;
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

	public boolean isBtnAceiteDesativado() {
		return btnAceiteDesativado;
	}

	public void setBtnAceiteDesativado(boolean btnAceiteDesativado) {
		this.btnAceiteDesativado = btnAceiteDesativado;
	}

	public Termo getTermoUsuarioPopup() {
		return termoUsuarioPopup;
	}

	public void setTermoUsuarioPopup(Termo termoUsuarioPopup) {
		this.termoUsuarioPopup = termoUsuarioPopup;
	}

	public TermoUsuarioVO getUsuarioVO() {
		return usuarioVO;
	}

	public void setUsuarioVO(TermoUsuarioVO usuarioVO) {
		this.usuarioVO = usuarioVO;
	}

	public boolean isPdfGerado() {
		return pdfGerado;
	}

	public void setPdfGerado(boolean pdfGerado) {
		this.pdfGerado = pdfGerado;
	}

	public String getPathPDF() {
		return pathPDF;
	}

	public void setPathPDF(String pathPDF) {
		this.pathPDF = pathPDF;
	}

	public String getNomePDF() {
		return nomePDF;
	}

	public void setNomePDF(String nomePDF) {
		this.nomePDF = nomePDF;
	}

	public StreamedContent getFilePDF() {
		return filePDF;
	}

	public void setFilePDF(StreamedContent filePDF) {
		this.filePDF = filePDF;
	}

	public List<TermoPopup> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<TermoPopup> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Termo> getUsuarioTermosAssinados() {
		return usuarioTermosAssinados;
	}

	public void setUsuarioTermosAssinados(List<Termo> usuarioTermosAssinados) {
		this.usuarioTermosAssinados = usuarioTermosAssinados;
	}

	public String getBase64imagem() {
		return base64imagem;
	}

	public void setBase64imagem(String base64imagem) {
		this.base64imagem = base64imagem;
	}

}
