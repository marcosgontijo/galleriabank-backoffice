package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.auxiliar.CompactadorUtil;
import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagarOrigemEnum;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ContaContabilDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.cobranca.service.FileService;
import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "contasPagarMB")
@SessionScoped
public class ContasPagarMB {

	
	private List<ContasPagar> contasPagar;
	private Map<String, Object> filters;
	private ContasPagar objetoContasPagar;
	
	private ContasPagar contasPagarArquivos;
	
	private boolean updateMode;
	private boolean deleteMode;
	private boolean baixaMode;

	private String tipoDespesa;
	private String tipoData;
	
	private Date relDataContratoInicio;
	private Date relDataContratoFim;

	private List<ContratoCobranca> listContratos;
	private long idContrato;
	// private String numeroContrato;
	private ContratoCobranca selectedContratoLov;
	private List<ContasPagar> contasPagarPosOperacao;
	private List<ContasPagar> contasPagarTodasOperacao;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<PagadorRecebedor> listRecebedorPagador;
	/** Objeto selecionado na LoV - Pagador. */
	private PagadorRecebedor selectedPagadorGenerico;
	String updatePagadorRecebedor = "";
	String updateResponsavel = "";

	private List<ContaContabil> listContasContabil;
	/** Objeto selecionado na LoV - Pagador. */
	private ContaContabil selectedContaContabil;
	
	private Responsavel selectedResponsavel;
	private List<Responsavel> listResponsavel;
	
	FileService fileService = new FileService();
	Collection<FileUploaded> filesPagar = new ArrayList<FileUploaded>();
	List<FileUploaded> deleteFilesPagar = new ArrayList<FileUploaded>();
	List<FileUploaded> deleteFilesContas= new ArrayList<FileUploaded>();
	
	
	private boolean addContasPagar;
	private boolean buscarContasPagar;
	StreamedContent downloadFile;
	FileUploaded selectedFile =  new FileUploaded();
	byte[] arquivos = null;
	List<FileUploaded> deletefiles = new ArrayList<FileUploaded>();
	
	public ContasPagarMB() {

	}
	// Lista Pós Operação
	public List<String> contaPagarDescricaoLista(){
		List<String> listaNome = new ArrayList<>();
		listaNome.add("Busca de endereços JUD");
		listaNome.add("Custas de agravo");
		listaNome.add("Custas de apelação");
		listaNome.add("Custas de certidão de Decurso de Prazo");
		listaNome.add("Custas de consolidação");
		listaNome.add("Custas de embargos de terceiro");
		listaNome.add("Custas de Guia de ITBI");
		listaNome.add("Custas de Intimação");
		listaNome.add("Custas de Intimação do RTD");
		listaNome.add("Custas de Prenotação");
		listaNome.add("Custas processuais iniciais");
		listaNome.add("Honorários sucumbenciais");
		listaNome.add("Intimação por AR");
		listaNome.add("Oficial de justiça");
		listaNome.add("Outros");
		
		return listaNome.stream().collect(Collectors.toList());
	}
	
	public void handleFilePagarUpload(FileUploadEvent event) throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
					
		if (event.getFile().getFileName().endsWith(".zip")) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança: não é possível anexar .zip", " não é possível anexar .zip"));
		} else {
			byte[] conteudo = event.getFile().getContents();
			fileService.salvarDocumento(conteudo, this.selectedContratoLov.getNumeroContrato(), 
					 event.getFile().getFileName(), "pagar", getUsuarioLogado());
			
			// atualiza lista de arquivos contidos no diretório
			//documentoConsultarTodos = new ArrayList<FileUploaded>();
			filesPagar = listaArquivosPagar();
		}
		
		if(!SiscoatConstants.DEV && !CommonsUtil.sistemaWindows()) {
			if(event.getFile().getFileName().contains("Pag ")
					|| event.getFile().getFileName().contains("PAG ")) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				ResponsavelDao rDao = new ResponsavelDao();
				Responsavel rGerente = new Responsavel();
				rGerente = rDao.findById((long) 1175); //camilo
				takeBlipMB.sendWhatsAppMessageComprovante(rGerente,
						"comprovante_anexado", 
						getNomeUsuarioLogado(),
						this.selectedContratoLov.getNumeroContrato(),
						event.getFile().getFileName());
			}
		}
	}
	
	public void handleFileContaPagarUpload(FileUploadEvent event) throws IOException {
		ContasPagar conta = (ContasPagar) event.getComponent().getAttributes().get("foo"); 
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(conta.getFileListId())) {
			conta.setFileListId(generateFileID());
		}	
				
		if (event.getFile().getFileName().endsWith(".zip")) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança: não é possível anexar .zip", " não é possível anexar .zip"));
		} else {
			byte[] conteudo = event.getFile().getContents();
			fileService.salvarDocumento(conteudo, this.selectedContratoLov.getNumeroContrato(), 
					 event.getFile().getFileName(), "pagar".concat("/").concat(conta.getFileListId()) , getUsuarioLogado());
			
			// atualiza lista de arquivos contidos no diretório
			conta.setFilesContas(listaArquivosContasPagar(conta));
		}
		
		if(!SiscoatConstants.DEV && !CommonsUtil.sistemaWindows()) {
			if(event.getFile().getFileName().contains("Pag ")
					|| event.getFile().getFileName().contains("PAG ")) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				ResponsavelDao rDao = new ResponsavelDao();
				Responsavel rGerente = new Responsavel();
				rGerente = rDao.findById((long) 1175); //camilo
				takeBlipMB.sendWhatsAppMessageComprovante(rGerente,
						"comprovante_anexado", 
						getNomeUsuarioLogado(),
						this.selectedContratoLov.getNumeroContrato(),
						event.getFile().getFileName());
			}
		}		
	}
	
	public void populateFilesContasPagar(ContasPagar conta) throws IOException {	
		contasPagarArquivos = conta;
		contasPagarArquivos.setFilesContas(listaArquivosContasPagar(contasPagarArquivos));
		
		PrimeFaces current = PrimeFaces.current();
		current.executeScript("PF('contaArquivosdlg').show();");
	}
	
	public String generateFileID() {
		return CommonsUtil.stringValue(System.currentTimeMillis());
	}
	
	public Collection<FileUploaded> listaArquivosPagar() {
		if(CommonsUtil.semValor(this.selectedContratoLov)) {
			return null;
		}
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
	//			String pathContrato = "C:/Users/Usuario/Desktop/"
		+ this.selectedContratoLov.getNumeroContrato() + "//pagar/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		
		List<FileUploaded> documentoConsultarTodos= new ArrayList<FileUploaded>();
		if (CommonsUtil.semValor(documentoConsultarTodos)) {
			FileService fileService = new FileService();
			documentoConsultarTodos = fileService.documentoConsultarTodos(this.getSelectedContratoLov().getNumeroContrato(), getUsuarioLogado());
		}
		return documentoConsultarTodos.stream().filter(f ->  CommonsUtil.mesmoValorIgnoreCase( f.getPathOrigin(), "pagar"))
//				.sorted(new Comparator<FileUploaded>() {
//				.documentoConsultarTodos(this.selectedContratoLov.getNumeroContrato(), getUsuarioLogado())
//			        public int compare(FileUploaded o1, FileUploaded o2) {
//			            return o1.getDate().compareTo(o2.getDate());
//			        }
//			    })
	.collect(Collectors.toList());
//		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
//		if (arqs != null) {
//			for (int i = 0; i < documentoConsultarTodos.size(); i++) {
//				File arquivo = arqs[i];
//
//				if(arquivo.isFile()) {
//					lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
//				}
//				
//			}
//		}
//		return lista;
	}
	
	public Collection<FileUploaded> listaArquivosContasPagar(ContasPagar conta) {
		if(CommonsUtil.semValor(conta.getFileListId())) {
			return new ArrayList<FileUploaded>();
		}
		
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
		//String pathContrato = "C:/Users/Usuario/Desktop/"
				+ this.selectedContratoLov.getNumeroContrato() + "//pagar/" + conta.getFileListId();
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				// String nome = arquivo.getName();
				// String dt_ateracao = formatData.format(new Date(arquivo.lastModified()));
				lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
			}
		}
		return lista;
	}
	
	
	
	public StreamedContent getDownloadFile() {

		if (this.selectedFile != null) {
			InputStream  stream;
			FileService fileService = new FileService();
			stream = new ByteArrayInputStream( fileService.abrirDocumentos(this.selectedFile,this.selectedContratoLov.getNumeroContrato(), getUsuarioLogado()));
			downloadFile = new DefaultStreamedContent(stream, this.selectedFile.getPath(),
					this.selectedFile.getName());
		}
		return this.downloadFile;
	}
	
	public void fileSelectionListener() {
		//Apesar dessa função não fazer nada ela é importante para o funcionamento do download em zip.
		//Não me pergunte o pq
	}
	
	public StreamedContent getDownloadAllFilesPagar() {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		try {
			CompactadorUtil compac = new CompactadorUtil();
			for (FileUploaded f : deleteFilesPagar) {
				String arquivo = f.getName();
			    byte[] arquivoByte = fileService.abrirDocumentos
			    		(f,this.selectedContratoLov.getNumeroContrato(), getUsuarioLogado());
				listaArquivos.put(arquivo, arquivoByte);
			}
			arquivos = compac.compactarZipByte(listaArquivos);
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format(selectedContratoLov.getNumeroContrato() + " Documentos_pagar.zip",
					"");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(arquivos));
			gerador.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	public StreamedContent getDownloadAllFilesContaPagar() {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		try {
			CompactadorUtil compac = new CompactadorUtil();
			for (FileUploaded f : deleteFilesContas) {
				String arquivo = f.getName();
			    byte[] arquivoByte = fileService.abrirDocumentos
			    		(f,this.selectedContratoLov.getNumeroContrato(), getUsuarioLogado());
				listaArquivos.put(arquivo, arquivoByte);
			}
			arquivos = compac.compactarZipByte(listaArquivos);
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format(selectedContratoLov.getNumeroContrato() + " Documentos_conta.zip",
					"");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(arquivos));
			gerador.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	public void viewFile(FileUploaded file) {
		String pathContrato = null;
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;
			String fileName = file.getName();
			String filePath = file.getPath();
			
			pathContrato = file.getPath() + "/" + fileName;
			String mineFile = "";
	
			if (fileName.contains(".jpg") || fileName.contains(".JPG")) {
				mineFile = "image-jpg";
			}
	
			if (fileName.contains(".jpeg") || fileName.contains(".jpeg")) {
				mineFile = "image-jpeg";
			}
	
			if (fileName.contains(".png") || fileName.contains(".PNG")) {
				mineFile = "image-png";
			}
	
			if (fileName.contains(".pdf") || fileName.contains(".PDF")) {
				mineFile = "application/pdf";
			}
	
			FileService fileService = new FileService();
			FileUploaded documentoSelecionado = new FileUploaded(fileName, null, filePath);
			byte[] arquivob = fileService.abrirDocumentos(documentoSelecionado,this.selectedContratoLov.getNumeroContrato(), getUsuarioLogado());
			InputStream arquivo = new ByteArrayInputStream( arquivob );
			
			input = new BufferedInputStream(arquivo, 10240);
	
			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);
	
			response.setContentLength(arquivob.length);
	
			response.setHeader("Content-disposition", "inline; filename=" + fileName);
			output = new BufferedOutputStream(response.getOutputStream(), 10240);
	
			// Write file contents to response.
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
	
			// Finalize task.
			output.flush();
			output.close();
			facesContext.responseComplete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(pathContrato);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(pathContrato);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteFile(FileUploaded f) {
		FileService fileService = new FileService();
		fileService.excluirDocumento(this.selectedContratoLov.getNumeroContrato(), f.getPathOrigin(), f.getName(),
				getUsuarioLogado());
	}
	
	public void deleteFile(List<FileUploaded> deleteFiles) {
		for (FileUploaded f : deleteFiles) {
			deleteFile(f);
//			f.getFile().delete();
		}
		File here = new File(".");
		System.out.println(here.getAbsolutePath());
		deleteFiles = new ArrayList<FileUploaded>();
		filesPagar = listaArquivosPagar();
		listaArquivosContasPagar(contasPagarArquivos);
	}
	
	public void pesquisaContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = cDao.consultaContratosCCBs();
	}
	
	public ContratoCobranca getContrato(String numeroContratoParametro) {		
		if (numeroContratoParametro != null) { 
			if (!numeroContratoParametro.equals("")) { 
				List<ContratoCobranca> contratos = new ArrayList<ContratoCobranca>();
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
				String numeroContrato = "";
				
				if (numeroContratoParametro.length() == 4) {
					numeroContrato = "0" + numeroContratoParametro;
				} else {
					numeroContrato = numeroContratoParametro;
				}
				
				contratos = cDao.findByFilter("numeroContrato", numeroContrato);
				
				if (contratos.size() > 0) {
					return contratos.get(0);	
				}				
			}
		}
		
		return null;
	}

	public String clearFieldsInsert() {
		this.objetoContasPagar = new ContasPagar();
		this.objetoContasPagar.setTipoDespesa(tipoDespesa);
		this.objetoContasPagar.setDataPagamento(DateUtil.gerarDataHoje());

		return "/Atendimento/Cobranca/ContasPagarInserir.xhtml";
	}
	
	public String clearFieldsPosOperacao() {
		this.objetoContasPagar = new ContasPagar();
		this.selectedContratoLov = new ContratoCobranca();
		this.filesPagar = new ArrayList<FileUploaded>();

		return "/Atendimento/Cobranca/ContasPagarPosOperacao.xhtml";
	}
	
	public void concluirContaPosOperacao() {
		this.objetoContasPagar.setContrato(this.selectedContratoLov);
		this.objetoContasPagar.setNumeroDocumento(this.selectedContratoLov.getNumeroContrato());
		this.objetoContasPagar.setPagadorRecebedor(this.selectedContratoLov.getPagador());
		this.objetoContasPagar.setTipoDespesa("C");
		this.objetoContasPagar.setOrigem(ContasPagarOrigemEnum.POS);
		this.objetoContasPagar.setResponsavel(this.selectedContratoLov.getResponsavel());
		/*if(!CommonsUtil.semValor(this.objetoContasPagar.getValor())) {
			if(!CommonsUtil.semValor(this.selectedContratoLov.getContaPagarValorTotal())) {
				this.selectedContratoLov.setContaPagarValorTotal(this.selectedContratoLov
						.getContaPagarValorTotal().add(this.objetoContasPagar.getValor()));
			} else {
				this.selectedContratoLov.setContaPagarValorTotal(this.objetoContasPagar.getValor());
			}
			if(!CommonsUtil.semValor(this.objetoContasPagar.getValorPagamento())) {
				if(CommonsUtil.mesmoValor(this.objetoContasPagar.getValorPagamento(), this.objetoContasPagar.getValor())) {
					this.objetoContasPagar.setContaPaga(true);
				} 
				this.selectedContratoLov.setContaPagarValorTotal(this.selectedContratoLov
						.getContaPagarValorTotal().subtract(this.objetoContasPagar.getValorPagamento()));
			}
		}	*/
		
		if(this.objetoContasPagar.isContaPaga() && CommonsUtil.semValor(this.objetoContasPagar.getDataPagamento())) {
			this.objetoContasPagar.setDataPagamento(DateUtil.gerarDataHoje());
		}
		this.selectedContratoLov.getListContasPagar().add(this.objetoContasPagar);
		
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		if(objetoContasPagar.getId() <= 0) {
			contasPagarDao.create(objetoContasPagar);
		} else {
			contasPagarDao.merge(objetoContasPagar);
		}
		
		this.objetoContasPagar = new ContasPagar();
		this.addContasPagar = false;
	}
	
	private BigDecimal calcularValorTotalContasPagar() {
		BigDecimal valorTotalContasPagarNovo = BigDecimal.ZERO;
		for (ContasPagar conta : this.contasPagarPosOperacao) {
			if (conta.isEditada()) 
				continue;
			
			if (!CommonsUtil.semValor(conta.getValor())) {
				valorTotalContasPagarNovo = valorTotalContasPagarNovo.add(conta.getValor());
			}

			/*if (!CommonsUtil.semValor(conta.getValorPagamento())) {
				if (CommonsUtil.mesmoValor(conta.getValorPagamento(), conta.getValor())) {
					conta.setContaPaga(true);
				}
				valorTotalContasPagarNovo = valorTotalContasPagarNovo.subtract(conta.getValorPagamento());
			}*/
		}
		return valorTotalContasPagarNovo;
	}
	
	public void editarContaPosOperacao(ContasPagar conta) {
		this.addContasPagar = true;
		this.objetoContasPagar = new ContasPagar();
		this.objetoContasPagar = conta;
		this.removerContaPosOperacao(conta);
	}
	
	public void removerContaPosOperacao(ContasPagar conta) {
		/*if(!CommonsUtil.semValor(this.objetoContasPagar.getValor())) {
			this.selectedContratoLov.setContaPagarValorTotal(this.selectedContratoLov
					.getContaPagarValorTotal().subtract(this.objetoContasPagar.getValor()));
		}
		if(!CommonsUtil.semValor(this.objetoContasPagar.getValorPagamento())) {
			this.selectedContratoLov.setContaPagarValorTotal(this.selectedContratoLov
					.getContaPagarValorTotal().add(this.objetoContasPagar.getValorPagamento()));
		}*/
		selectedContratoLov.setContaPagarValorTotal(calcularValorTotalContasPagar());
		this.selectedContratoLov.getListContasPagar().remove(conta);
	}
	
	public void salvarContasPosOperacao() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		try {				
			contratoCobrancaDao.merge(this.selectedContratoLov);
			
			if(!SiscoatConstants.DEV && !CommonsUtil.sistemaWindows()) {
				TakeBlipMB takeBlipMB = new TakeBlipMB (); 
				takeBlipMB.sendWhatsAppMessageNovaConta (this.selectedContratoLov.getNumeroContrato());
			}
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Contas Inseridas com sucesso!!",
							""));

			clearFieldsPosOperacao();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: " + e, ""));
		}
		
	}
	
	public String clearFieldsEditar() {
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		objetoContasPagar = contasPagarDao.findById(objetoContasPagar.getId());
		this.selectedContratoLov = objetoContasPagar.getContrato();
		filesPagar = listaArquivosPagar();
		
		return "/Atendimento/Cobranca/ContasPagarInserir.xhtml";
	}

	public String clearFields() {
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;
		
		if (tipoDespesa == null || tipoDespesa.isEmpty())
			tipoDespesa = "C";
		filters = new HashMap<String, Object>();
		filters.put("contaPaga", false);
		atualizaListagem();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedorPagador = pagadorRecebedorDao.findAll();

		ContaContabilDao contaContabilDao = new ContaContabilDao();
		listContasContabil = contaContabilDao.ContasContabilOrdenadaRaiz();
		
		ResponsavelDao rDao = new ResponsavelDao();
		this.listResponsavel = rDao.findAll();

		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}
	
	public String clearFieldsConsultar() {
		filters = new HashMap<String, Object>();
		filters.put("contaPaga", false);
		atualizaListagem();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedorPagador = pagadorRecebedorDao.findAll();

		ContaContabilDao contaContabilDao = new ContaContabilDao();
		listContasContabil = contaContabilDao.ContasContabilOrdenadaRaiz();
		
		ResponsavelDao rDao = new ResponsavelDao();
		this.listResponsavel = rDao.findAll();

		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}

	private void atualizaListagem() {
		ContasPagarDao cDao = new ContasPagarDao();
		//HashMap<String, Object> filtersConsulta = new HashMap<String, Object>();
		//filtersConsulta.putAll(filters);
		//filtersConsulta.put("tipoDespesa", tipoDespesa);
		//this.contasPagar = cDao.findByFilter(filtersConsulta);
		boolean contaPaga = false;
		
		if (filters.get("contaPaga") != null) {
			contaPaga = (Boolean) filters.get("contaPaga");
		} 
		
		try {
			this.contasPagar = cDao.atualizaListagemContasPagar(tipoDespesa, contaPaga, this.relDataContratoInicio, this.relDataContratoFim, tipoData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void populateSelectedContrato() {
		if(CommonsUtil.semValor(this.getSelectedContratoLov())) {
			return;
		}
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.setSelectedContratoLov(cDao.findById(this.getSelectedContratoLov().getId()));
		Set<ContasPagar> setResult = this.getSelectedContratoLov().getListContasPagar();
		this.contasPagarPosOperacao =  new ArrayList<>(setResult.stream()
																.filter(x -> x.getOrigem() == ContasPagarOrigemEnum.POS)
																.collect(Collectors.toList()));
		
		selectedContratoLov.setContaPagarValorTotal(calcularValorTotalContasPagar());
		
		filesPagar = listaArquivosPagar();
	}
	
	public void clearContrato() {
		this.selectedContratoLov = new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = cDao.consultaContratosCCBs();
	}

	public String clearFieldsContasPagas() {
		filters = new HashMap<String, Object>();
		filters.put("contaPaga", true);
		atualizaListagem();
		return "/Atendimento/Cobranca/ContasPagasConsultar.xhtml";
	}

	public void selectTipoDespesa() {
		atualizaListagem();
	}

	public String baixarConta() {
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.objetoContasPagar.getValorPagamento() == null)
			this.objetoContasPagar.setValorPagamento(this.objetoContasPagar.getValor());
	
		this.objetoContasPagar.setContaPaga(true);
		cDao.merge(this.objetoContasPagar);
		this.contasPagar.remove(this.objetoContasPagar);

		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}

	public String salvarConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.objetoContasPagar.getNumeroDocumento() != null) { 
			if (!this.objetoContasPagar.getNumeroDocumento().equals("")) { 
				ContratoCobranca contrato = new ContratoCobranca();				
				contrato = getContrato(this.objetoContasPagar.getNumeroDocumento());
				if (contrato != null) {
					this.objetoContasPagar.setContrato(contrato);
				}
			}
		}

		if (this.objetoContasPagar.getId() > 0) {
			cDao.merge(this.objetoContasPagar);

			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta alterada com sucesso!", ""));
		} else {
			cDao.create(this.objetoContasPagar);

			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta cadastrada com sucesso!", ""));
		}

		return clearFields();
	}
	
	public String salvarEBaixarConta() {
		salvarConta();
		updateMode = true;
		deleteMode = false;
		baixaMode = true;
		return clearFieldsEditar();
	}

	public String editarConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.objetoContasPagar.getNumeroDocumento() != null) { 
			if (!this.objetoContasPagar.getNumeroDocumento().equals("")) { 
				ContratoCobranca contrato = new ContratoCobranca();				
				contrato = getContrato(this.objetoContasPagar.getNumeroDocumento());
				if (contrato != null) {
					this.objetoContasPagar.setContrato(contrato);
				}
			}
		}

		cDao.merge(this.objetoContasPagar);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta alterada com sucesso!", ""));

		return clearFields();
	}

	public String excluirConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();

		cDao.delete(this.objetoContasPagar);

		this.contasPagar.remove(this.objetoContasPagar);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta excluída com sucesso!", ""));

		return clearFields();
	}
	
	
	public StreamedContent getDownloadAllFiles(List<FileUploaded> selectFiles) {
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();

		try {
			// recupera path do contrato
//			ParametrosDao pDao = new ParametrosDao();
//			CompactadorUtil compac = new CompactadorUtil();
//			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
//					+ this.contasPagarArquivos.getContrato().getNumeroContrato()+ "//pagar/";
//			// cria objetos para ZIP

			// Percorre arquivos selecionados e adiciona ao ZIP
			for (FileUploaded f : selectFiles) {
				String arquivo = f.getName();
				byte[] arquivoByte = f.getFile().getPath().getBytes();
				
				FileService fileService = new FileService();
				FileUploaded documentoSelecionado = new FileUploaded(f.getName(), null, "pagar");

				byte[] arquivob = fileService.abrirDocumentos(documentoSelecionado,
						this.contasPagarArquivos.getContrato().getNumeroContrato(), getUsuarioLogado());

				listaArquivos.put(arquivo, arquivob);

			}
			byte[] arquivos = CompactadorUtil.compactarZipByte(listaArquivos);

			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format(this.contasPagarArquivos.getContrato().getNumeroContrato() + " Documentos.zip",
					"");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(arquivos));
			gerador.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}
	
	public void setContasPagarPosOperacao(List<ContasPagar> contasPagarPosOperacao) {
		this.contasPagarPosOperacao = contasPagarPosOperacao;
	}
	public List<ContasPagar> getContasPagarPosOperacao() {				
		return this.contasPagarPosOperacao;
			
		
	}
	public void settarDataPagamento() {
		this.objetoContasPagar.setDataPagamento(this.objetoContasPagar.getDataVencimento());	
	}
	
	public void settarValorPagamento() {
		this.objetoContasPagar.setValorPagamento(this.objetoContasPagar.getValor());	
	}

	public void clearPagadorRecebedor() {
		this.objetoContasPagar.setPagadorRecebedor(null);
		this.selectedPagadorGenerico = new PagadorRecebedor();
	}

	public final void populateSelectedPagadorRecebedor() {
		this.objetoContasPagar.setPagadorRecebedor(this.selectedPagadorGenerico);
	}
	
	public final void pesquisaPagador() {		
		updatePagadorRecebedor = ":form:pagadorPanel";	
		updateResponsavel = ":form:pagadorPanel";
	}
	
	public final void populateSelectedResponsavel() {
		this.objetoContasPagar.setResponsavel(this.selectedResponsavel);
	}
	
	public void clearResponsavel() {
		this.objetoContasPagar.setResponsavel(null);
		this.selectedResponsavel = new Responsavel();
	}

	public void ContaContabil() {
		this.objetoContasPagar.setContaContabil(null);
		this.selectedContaContabil = new ContaContabil();
	}

	public final void populateSelectedContaContabil() {
		this.objetoContasPagar.setContaContabil(this.selectedContaContabil);
	}
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	public String getNomeUsuarioLogado() {
		User usuario = getUsuarioLogado();

		if (usuario.getLogin() != null) {
			if (!usuario.getLogin().equals("")) {
				return usuario.getLogin();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public User getUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);
			}
		}

		return usuario;
	}
		
	public List<ContasPagar> getContasPagar() {
		return contasPagar;
	}

	public void setContasPagar(List<ContasPagar> contasPagar) {
		this.contasPagar = contasPagar;
	}

	public ContasPagar getObjetoContasPagar() {
		return objetoContasPagar;
	}

	public void setObjetoContasPagar(ContasPagar objetoContasPagar) {
		this.objetoContasPagar = objetoContasPagar;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	public String getTipoDespesa() {
		return tipoDespesa;
	}

	public void setTipoDespesa(String tipoDespesa) {
		this.tipoDespesa = tipoDespesa;
	}

	public boolean isDeleteMode() {
		return deleteMode;
	}

	public void setDeleteMode(boolean deleteMode) {
		this.deleteMode = deleteMode;
	}

	public List<ContratoCobranca> getListContratos() {
		return listContratos;
	}

	public void setListContratos(List<ContratoCobranca> listContratos) {
		this.listContratos = listContratos;
	}

	public long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(long idContrato) {
		this.idContrato = idContrato;
	}

//	public String getNumeroContrato() {
//		return numeroContrato;
//	}
//
//	public void setNumeroContrato(String numeroContrato) {
//		this.numeroContrato = numeroContrato;
//	}

	public ContratoCobranca getSelectedContratoLov() {
		return selectedContratoLov;
	}

	public void setSelectedContratoLov(ContratoCobranca selectedContratoLov) {
		this.selectedContratoLov = selectedContratoLov;
	}

	public List<PagadorRecebedor> getListRecebedorPagador() {
		return listRecebedorPagador;
	}

	public void setListRecebedorPagador(List<PagadorRecebedor> listRecebedorPagador) {
		this.listRecebedorPagador = listRecebedorPagador;
	}

	public PagadorRecebedor getSelectedPagador() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagadorGenerico = selectedPagador;
	}

	public List<ContaContabil> getListContasContabil() {
		return listContasContabil;
	}

	public void setListContasContabil(List<ContaContabil> listContasContabil) {
		this.listContasContabil = listContasContabil;
	}

	public ContaContabil getSelectedContaContabil() {
		return selectedContaContabil;
	}

	public void setSelectedContaContabil(ContaContabil selectedContaContabil) {
		this.selectedContaContabil = selectedContaContabil;
	}

	public Date getRelDataContratoInicio() {
		return relDataContratoInicio;
	}

	public void setRelDataContratoInicio(Date relDataContratoInicio) {
		this.relDataContratoInicio = relDataContratoInicio;
	}

	public Date getRelDataContratoFim() {
		return relDataContratoFim;
	}

	public void setRelDataContratoFim(Date relDataContratoFim) {
		this.relDataContratoFim = relDataContratoFim;
	}

	public Responsavel getSelectedResponsavel() {
		return selectedResponsavel;
	}

	public void setSelectedResponsavel(Responsavel selectedResponsavel) {
		this.selectedResponsavel = selectedResponsavel;
	}

	public List<Responsavel> getListResponsavel() {
		return listResponsavel;
	}

	public void setListResponsavel(List<Responsavel> listResponsavel) {
		this.listResponsavel = listResponsavel;
	}

	public PagadorRecebedor getSelectedPagadorGenerico() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagadorGenerico(PagadorRecebedor selectedPagadorGenerico) {
		this.selectedPagadorGenerico = selectedPagadorGenerico;
	}

	public String getUpdatePagadorRecebedor() {
		return updatePagadorRecebedor;
	}

	public void setUpdatePagadorRecebedor(String updatePagadorRecebedor) {
		this.updatePagadorRecebedor = updatePagadorRecebedor;
	}

	public String getUpdateResponsavel() {
		return updateResponsavel;
	}

	public void setUpdateResponavel(String updateResponsavel) {
		this.updateResponsavel = updateResponsavel;
	}
	public boolean isAddContasPagar() {
		return addContasPagar;
	}
	public void setAddContasPagar(boolean addContasPagar) {
		this.addContasPagar = addContasPagar;
	}
	public FileUploaded getSelectedFile() {
		return selectedFile;
	}
	public void setSelectedFile(FileUploaded selectedFile) {
		this.selectedFile = selectedFile;
	}
	public Collection<FileUploaded> getFilesPagar() {
		return filesPagar;
	}
	public void setFilesPagar(Collection<FileUploaded> filesPagar) {
		this.filesPagar = filesPagar;
	}
	
	public List<FileUploaded> getDeleteFilesPagar() {
		return deleteFilesPagar;
	}
	public void setDeleteFilesPagar(List<FileUploaded> deleteFilesPagar) {
		this.deleteFilesPagar = deleteFilesPagar;
	}
	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

	public String getTipoData() {
		return tipoData;
	}

	public void setTipoData(String tipoData) {
		this.tipoData = tipoData;
	}
	public boolean isBaixaMode() {
		return baixaMode;
	}
	public void setBaixaMode(boolean baixaMode) {
		this.baixaMode = baixaMode;
	}
	public ContasPagar getContasPagarArquivos() {
		return contasPagarArquivos;
	}
	public void setContasPagarArquivos(ContasPagar contasPagarArquivos) {
		this.contasPagarArquivos = contasPagarArquivos;
	}
	public List<FileUploaded> getDeleteFilesContas() {
		return deleteFilesContas;
	}
	public void setDeleteFilesContas(List<FileUploaded> deleteFilesContas) {
		this.deleteFilesContas = deleteFilesContas;
	}
	public LoginBean getLoginBean() {
		return loginBean;
	}
	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}	
	public List<FileUploaded> getDeletefiles() {
		return deletefiles;
	}	
	public void setDeletefiles(List<FileUploaded> deletefiles) {
		this.deletefiles = deletefiles;
	}
	public boolean isBuscarContasPagar() {
		return buscarContasPagar;
	}
	public List<ContasPagar> getContasPagarTodasOperacao() {
		return contasPagarTodasOperacao;
	}
	public void setContasPagarTodasOperacao(List<ContasPagar> contasPagarTodasOperacao) {
		this.contasPagarTodasOperacao = contasPagarTodasOperacao;
	}
	public void setBuscarContasPagar(boolean buscarContasPagar) throws Exception {
		this.buscarContasPagar = buscarContasPagar;
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.getSelectedContratoLov().getId() != 0 
				&& contasPagarTodasOperacao == null) {
			contasPagarTodasOperacao = cDao.buscarContasPre(this.getSelectedContratoLov().getId());
		}
	}
	public void contasPagarPrePos(ContasPagar conta) {
		this.objetoContasPagar = new ContasPagar();
		this.objetoContasPagar = conta;
		this.objetoContasPagar.setOrigem(ContasPagarOrigemEnum.POS);
			
		if (this.contasPagarTodasOperacao.contains(conta)) {
			ContasPagarDao cDao = new ContasPagarDao();
			cDao.merge(this.objetoContasPagar);
			this.contasPagarTodasOperacao.remove(conta);
			this.objetoContasPagar = null;
			this.contasPagarPosOperacao.add(conta);
			calcularValorTotalContasPagar();
		}
	}

	
}
