package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "plexiMB")
@SessionScoped
public class PlexiMB {
	
	private List<DocumentoAnalise> listPagador;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	public UploadedFile uploadedFile;
	//PlexiConsulta plexiConsulta = new PlexiConsulta();
	String cpfCnpj;
	
	public String clearFieldsContratoCobranca(List<DocumentoAnalise> listDocAnalise) {
		listPagador = new ArrayList<DocumentoAnalise>();
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getPagador())) {
				continue;
			}
			if(docAnalise.isLiberadoAnalise()) {
				listPagador.add(docAnalise);
				adiconarDocumentospagador(docAnalise);
			} else {
				continue;
			}
		}
		
		return "/Atendimento/Cobranca/Plexi.xhtml";
	}	
	
	public void criarPedido() {	//POST para gerar pedido
		
		PlexiService plexiService = new PlexiService();
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		User user = null;
		if(!CommonsUtil.semValor(loginBean)) {
			user = loginBean.getUsuarioLogado();
		}
		
		for(DocumentoAnalise docAnalise : listPagador) {
			List<PlexiConsulta> consultasExistentes = new ArrayList<PlexiConsulta>();
			boolean podeChamar = true;
			atualizarDocumentos(docAnalise);
			for(PlexiConsulta plexiConsulta : docAnalise.getPlexiConsultas()) {
				consultasExistentes = plexiConsultaDao.getConsultasExistentes(plexiConsulta);
				if(consultasExistentes.size() > 0) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, plexiConsulta.getPlexiDocumentos().getNome() + " - já Existente" ,""));
					System.out.println("Consulta Ja Existente");
					consultasExistentes.add(plexiConsulta);
					continue;
				}
				podeChamar = verificaCamposDoc(plexiConsulta);
				if(!podeChamar) {
					break;
				}
			}
			
			docAnalise.getPlexiConsultas().removeAll(consultasExistentes);
			if(podeChamar) {
				for(PlexiConsulta plexiConsulta : docAnalise.getPlexiConsultas()) {
					plexiService.PedirConsulta(plexiConsulta, user, docAnalise);
				}
				
				DocumentoAnaliseDao docAnaliseDao = new DocumentoAnaliseDao(); 
				docAnaliseDao.merge(docAnalise);
			}
		}
		
		System.out.println("Finalizado");
	}
	
	public void atualizarDocumentos(DocumentoAnalise docAnalise) {
		for(PlexiConsulta plexiConsulta : docAnalise.getPlexiConsultas()) {
			plexiConsulta.populatePagadorRecebedor(docAnalise.getPagador());
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	}
	
	public void popularDocumentos() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook((uploadedFile.getInputstream()));
		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);
		
		PlexiDocumentosDao plexiDocsDao = new PlexiDocumentosDao();
		while (!CommonsUtil.semValor(linha)) {
			
			linha = sheet.getRow(iLinha);
			if(CommonsUtil.semValor(linha) 
				||  CommonsUtil.semValor(linha.getCell(0)) 
				||  CommonsUtil.semValor(linha.getCell(0).getStringCellValue())
				||  CommonsUtil.semValor(linha.getCell(1)) 
				||  CommonsUtil.semValor(linha.getCell(1).getStringCellValue())
				||  CommonsUtil.semValor(linha.getCell(2)) 
				||  CommonsUtil.semValor(linha.getCell(2).getStringCellValue())
				||  CommonsUtil.semValor(linha.getCell(3)) 
				||  CommonsUtil.semValor(linha.getCell(3).getStringCellValue())
				) {
				break;
			}
			
			String url = (linha.getCell(0).getStringCellValue());
			String nome = (linha.getCell(1).getStringCellValue());
			String pfStr = (linha.getCell(2).getStringCellValue());
			String pjStr = (linha.getCell(3).getStringCellValue());
			String obs = "";
			if(!CommonsUtil.semValor(linha.getCell(4)) && !CommonsUtil.semValor(linha.getCell(4).getStringCellValue()) ) {
				obs = (linha.getCell(4).getStringCellValue());
			}
			
			
			PlexiDocumentos doc = new PlexiDocumentos();
			if(plexiDocsDao.findByFilter("url", url).size() > 0) {
				doc = plexiDocsDao.findByFilter("url", url).get(0);
			}
			doc.setUrl(url);
			doc.setNome(nome);
			doc.setObs(obs);
			
			if(CommonsUtil.mesmoValor(pfStr, "false")) {
				doc.setPf(false);
			} else if(CommonsUtil.mesmoValor(pfStr, "true")) {
				doc.setPf(true);
			}
			
			if(CommonsUtil.mesmoValor(pjStr, "false")) {
				doc.setPj(false);
			} else if(CommonsUtil.mesmoValor(pjStr, "true")) {
				doc.setPj(true);
			}
			
			if(doc.getId() > 0) {
				plexiDocsDao.merge(doc);
			} else {
				plexiDocsDao.create(doc);
			}
			
			iLinha++;
		}
	}
	
	public void clearDialog() {
		this.uploadedFile = null;
	}
	
	public StreamedContent decodarBaixarArquivo(PlexiConsulta consulta) {
		if(CommonsUtil.semValor(consulta) && CommonsUtil.semValor(consulta.getPdf()) ) {
			//System.out.println("Arquivo Base64 não existe");
			return null;
		}
		
		byte[] decoded = Base64.getDecoder().decode(consulta.getPdf());
		
		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		String nomeArquivoDownload = "Galleria Bank - Plexi " 
		+ CommonsUtil.removeAcentos(consulta.getPlexiDocumentos().getNome())  + ".pdf";
		gerador.open(nomeArquivoDownload);
		gerador.feed(in);
		gerador.close();
		return null;
	}
	
	public void removeDoc(DocumentoAnalise docAnalise, PlexiConsulta consulta) {
		docAnalise.getPlexiConsultas().remove(consulta);
	}
	
	public void removerPessoa(DocumentoAnalise docAnalise) {
		docAnalise.getPlexiConsultas().clear();
		listPagador.remove(docAnalise);
	}
	
	public void adiconarDocumentospagador(DocumentoAnalise docAnalise) {
		List<PlexiDocumentos> plexiDocumentos = new ArrayList<PlexiDocumentos>();
		PlexiDocumentosDao plexiDocsDao = new PlexiDocumentosDao();
		docAnalise.setPlexiConsultas(new ArrayList<PlexiConsulta>());
		if(!CommonsUtil.semValor(docAnalise.getPagador().getCpf())) {
			plexiDocumentos = plexiDocsDao.getDocumentosPF();
		} else {
			plexiDocumentos = plexiDocsDao.getDocumentosPJ();
		}
		
		for(PlexiDocumentos doc : plexiDocumentos) {
			PlexiConsulta plexiConsulta = new PlexiConsulta(docAnalise.getPagador(), doc);
			if(adicionaCamposDoc(docAnalise, plexiConsulta)) {
				docAnalise.getPlexiConsultas().add(plexiConsulta);
			}
		}
	}
	
	public boolean verificaCamposDoc(PlexiConsulta plexiConsulta) {
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		boolean retorno = true;
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/fazenda-mg/certidao-debitos-tributarios")) {
			if(CommonsUtil.semValor(plexiConsulta.getCep())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Cep",""));
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/fazenda-mg/certidao-debitos-tributarios")) {
			if(CommonsUtil.semValor(plexiConsulta.getCep())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Cep", ""));
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			if(CommonsUtil.semValor(plexiConsulta.getEndereco())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Endereço", ""));
			}
			
			if(CommonsUtil.semValor(plexiConsulta.getRg())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
			}
			
			if(CommonsUtil.semValor(plexiConsulta.getOrgaoExpedidorRg())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Orgao Rg", ""));
			}
			
			if(CommonsUtil.semValor(plexiConsulta.getUfRg())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta UF Rg", ""));
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			if(CommonsUtil.semValor(plexiConsulta.getRg())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
			}
		}
		
		return retorno;
	}
	
	public boolean adicionaCamposDoc(DocumentoAnalise docAnalise, PlexiConsulta plexiConsulta) {
	//retorna false caso consulta seja inserida por esse metodo
		
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjdft/certidao-distribuicao")) {
			String[] tipoCertidaoArray = {"criminal", "civel"};
			for(String tipoCertidao : tipoCertidaoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
				plexiConsulta.setTipoCertidao(tipoCertidao);
				docAnalise.getPlexiConsultas().add(plexiConsultaAux);
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrj/consulta-processual")) {
			String[] origemArray = {"primeiraInstancia", "segundaInstancia"};
			String comarca = "todas";
			String[] competenciaArray = {"civel", "criminal", "criminalJuri"};
			
			for(String origem : origemArray) {
				for(String competencia : competenciaArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
					plexiConsultaAux.setOrigem(origem);
					plexiConsultaAux.setComarca(comarca);
					plexiConsultaAux.setCompetencia(competencia);
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			String[] tipoArray = {"3", "9"};
			for(String tipo : tipoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
				plexiConsulta.setTipo(tipo);
				docAnalise.getPlexiConsultas().add(plexiConsultaAux);
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			String[] modeloArray = {"6", "52"};
			for(String modelo : modeloArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
				plexiConsulta.setModelo(modelo);
				docAnalise.getPlexiConsultas().add(plexiConsultaAux);
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf1/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			String[][] orgaosArray = { 
					{"ac","am","ap","ba","df","go","ma","mt","pa","pi","ro","rr","to","trf1"},
					{"varasJuizados"},
					{"regionalizada"}};
			for(String tipo : tipoArray) {
				for(String[] orgaos : orgaosArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
					plexiConsultaAux.setTipo(tipo);
					plexiConsultaAux.setOrgaos(orgaos);
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf2/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			for(String tipo : tipoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
				plexiConsultaAux.setTipo(tipo);
				docAnalise.getPlexiConsultas().add(plexiConsultaAux);
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf3/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			String[] abrangenciaArray = {"sjsp", "sjms"};
			for(String tipo : tipoArray) {
				for(String abrangencia : abrangenciaArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
					plexiConsultaAux.setTipo(tipo);
					plexiConsultaAux.setAbrangencia(abrangencia);
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf4/certidao-regional")) {
			String[] tipoArray = {"civel", "criminal"};
			for(String tipo : tipoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
				plexiConsultaAux.setTipo(tipo);
				docAnalise.getPlexiConsultas().add(plexiConsultaAux);
			}  
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf6/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			String[][] orgaosArray = {
					{"mg","trf1"}
					};
			for(String tipo : tipoArray) {
				for(String[] orgaos : orgaosArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise.getPagador(), doc);
					plexiConsultaAux.setTipo(tipo);
					plexiConsultaAux.setOrgaos(orgaos);
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				}
			}
			return false;
		}
		return true;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public List<DocumentoAnalise> getListPagador() {
		return listPagador;
	}

	public void setListPagador(List<DocumentoAnalise> listPagador) {
		this.listPagador = listPagador;
	}



	public LoginBean getLoginBean() {
		return loginBean;
	}



	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
	

}
