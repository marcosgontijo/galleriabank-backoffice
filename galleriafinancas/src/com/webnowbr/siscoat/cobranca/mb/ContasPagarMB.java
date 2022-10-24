package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ContaContabilDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@ManagedBean(name = "contasPagarMB")
@SessionScoped
public class ContasPagarMB {

	
	private List<ContasPagar> contasPagar;
	private Map<String, Object> filters;
	private ContasPagar objetoContasPagar;

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
	
	Collection<FileUploaded> filesPagar = new ArrayList<FileUploaded>();
	List<FileUploaded> DeleteFilesPagar = new ArrayList<FileUploaded>();
	
	private boolean addContasPagar;
	StreamedContent downloadFile;
	FileUploaded selectedFile = new FileUploaded();
	
	public ContasPagarMB() {

	}
	// Lista Pós Operação
	public List<String> contaPagarDescricaoLista(){
		List<String> listaNome = new ArrayList<>();
		listaNome.add("Busca de endereços JUD");
		listaNome.add("Custas de agravo");
		listaNome.add("Custas de apelação");
		listaNome.add("Custas embargos de terceiro");
		listaNome.add("Custas processuais iniciais");
		listaNome.add("Honorários sucumbenciais");
		listaNome.add("Intimação por AR");
		listaNome.add("Oficial de justiça");
		listaNome.add("Outros");
		
		return listaNome.stream().collect(Collectors.toList());
	}
	
	public void handleFilePagarUpload(FileUploadEvent event) throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		// recupera local onde será gravado o arquivo
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ this.selectedContratoLov.getNumeroContrato() + "//pagar/";

		// cria o diretório, caso não exista
		File diretorio = new File(pathContrato);
		if (!diretorio.isDirectory()) {
			diretorio.mkdir();
		}

		if(event.getFile().getFileName().endsWith(".zip")) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: não é possível anexar .zip", " não é possível anexar .zip"));
		} else {
			// cria o arquivo
			byte[] conteudo = event.getFile().getContents();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(pathContrato + event.getFile().getFileName());
				fos.write(conteudo);
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}

			// atualiza lista de arquivos contidos no diretório
			filesPagar = listaArquivosPagar();
		}
	}
	
	public Collection<FileUploaded> listaArquivosPagar() {
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ this.selectedContratoLov.getNumeroContrato() + "//pagar/";
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
			FileInputStream stream;
			try {
				stream = new FileInputStream(this.selectedFile.getFile().getAbsolutePath());
				downloadFile = new DefaultStreamedContent(stream, this.selectedFile.getPath(),
						this.selectedFile.getFile().getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Cobrança - Download de Arquivos - Arquivo Não Encontrado");
			}
		}
		return this.downloadFile;
	}
	
	public void viewFilePagar(String fileName) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
					+ this.selectedContratoLov.getNumeroContrato() + "/" + fileName;

			/*
			 * 'docx' =>
			 * 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
			 * 'xlsx' =>
			 * 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'word'
			 * => 'application/msword', 'xls' => 'application/excel', 'pdf' =>
			 * 'application/pdf' 'psd' => 'application/x-photoshop'
			 */
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

			File arquivo = new File(pathContrato);

			input = new BufferedInputStream(new FileInputStream(arquivo), 10240);

			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);

			response.setContentLength((int) arquivo.length());

			response.setHeader("Content-disposition", "inline; filename=" + arquivo.getName());
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			// Write file contents to response.
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			// Finalize task.
			output.flush();
			facesContext.responseComplete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		this.objetoContasPagar.setDataPagamento(gerarDataHoje());

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
		this.objetoContasPagar.setResponsavel(this.selectedContratoLov.getResponsavel());
		if(!CommonsUtil.semValor(this.objetoContasPagar.getValor())) {
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
		}	
		
		if(this.objetoContasPagar.isContaPaga() && CommonsUtil.semValor(this.objetoContasPagar.getDataPagamento())) {
			this.objetoContasPagar.setDataPagamento(gerarDataHoje());
		}
		this.selectedContratoLov.getListContasPagar().add(this.objetoContasPagar);
		this.objetoContasPagar = new ContasPagar();
		this.addContasPagar = false;
		
	}
	
	public void editarContaPosOperacao(ContasPagar conta) {
		this.addContasPagar = true;
		this.objetoContasPagar = new ContasPagar();
		this.objetoContasPagar = conta;
		this.removerContaPosOperacao(conta);
	}
	
	public void removerContaPosOperacao(ContasPagar conta) {
		if(!CommonsUtil.semValor(this.objetoContasPagar.getValor())) {
			this.selectedContratoLov.setContaPagarValorTotal(this.selectedContratoLov
					.getContaPagarValorTotal().subtract(this.objetoContasPagar.getValor()));
		}
		if(!CommonsUtil.semValor(this.objetoContasPagar.getValorPagamento())) {
			this.selectedContratoLov.setContaPagarValorTotal(this.selectedContratoLov
					.getContaPagarValorTotal().add(this.objetoContasPagar.getValorPagamento()));
		}
		this.selectedContratoLov.getListContasPagar().remove(conta);
	}
	
	public void salvarContasPosOperacao() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		try {				
			contratoCobrancaDao.merge(this.selectedContratoLov);
			
			TakeBlipMB takeBlipMB = new TakeBlipMB (); 
			takeBlipMB.sendWhatsAppMessageNovaConta (this.selectedContratoLov.getNumeroContrato());
			
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
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.setSelectedContratoLov(cDao.findById(this.getSelectedContratoLov().getId()));
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

	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
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
	
	public class FileUploaded {
		private File file;
		private String name;
		private String path;

		public FileUploaded() {
		}

		public FileUploaded(String name, File file, String path) {
			this.name = name;
			this.file = file;
			this.path = path;
		}

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * @param file the file to set
		 */
		public void setFile(File file) {
			this.file = file;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}
	}

	public final void populateSelectedContaContabil() {
		this.objetoContasPagar.setContaContabil(this.selectedContaContabil);
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
		return DeleteFilesPagar;
	}
	public void setDeleteFilesPagar(List<FileUploaded> DeleteFilesPagar) {
		this.DeleteFilesPagar = DeleteFilesPagar;
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

}
