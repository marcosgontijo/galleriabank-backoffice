package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.StreamedContent;

import com.itextpdf.text.pdf.PdfReader;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "plexiMB")
@SessionScoped
public class PlexiMB {
	
	private List<DocumentoAnalise> listPagador;
	//private List<String> estados;
	private String etapa;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	private ContratoCobranca contratoCobranca;
	
	
	public String clearFieldsContratoCobranca(List<DocumentoAnalise> listDocAnalise, String etapaConsulta, ContratoCobranca contrato) {
		//estados = new ArrayList<String>();
		//estados.add(estadoImovel);
		this.etapa = etapaConsulta;
		contratoCobranca = contrato;
		listPagador = new ArrayList<DocumentoAnalise>();
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getPagador())) {
				continue;
			}
			if((CommonsUtil.mesmoValor(etapa, "analise") && docAnalise.isLiberadoAnalise())
			|| (CommonsUtil.mesmoValor(etapa, "pedir paju") && docAnalise.isLiberadoAnalise())
					){
				listPagador.add(docAnalise);
				
				//if(CommonsUtil.semValor(docAnalise.getPlexiConsultas()) || docAnalise.getPlexiConsultas().size() == 0) {
					adiconarDocumentospagador(docAnalise);
				//}
			} else {
				continue;
			}
		}
		
		return "/Atendimento/Cobranca/Plexi.xhtml";
	}	
	
	public void criarPedido() {	//POST para gerar pedido
		FacesContext context = FacesContext.getCurrentInstance();
		PlexiService plexiService = new PlexiService();
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		User user = null;
		if(!CommonsUtil.semValor(loginBean)) {
			user = loginBean.getUsuarioLogado();
		}
		
		for(DocumentoAnalise docAnalise : listPagador) {
			PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
			pagadorRecebedorDao.merge(docAnalise.getPagador());
			List<PlexiConsulta> consultasExistentes = new ArrayList<PlexiConsulta>();
			List<PlexiConsulta> consultasExistentesDB = new ArrayList<PlexiConsulta>();
			boolean podeChamar = true;
			atualizarDocumentos(docAnalise);
			for(PlexiConsulta plexiConsulta : docAnalise.getPlexiConsultas()) {
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsulta);
				if(consultasExistentesRetorno.size() > 0) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							plexiConsulta.getPlexiDocumentos().getNome() + " - " + plexiConsulta.getNome() + ": Já existente", ""));
					consultasExistentes.add(plexiConsulta);
					consultasExistentesDB.add(consultasExistentesRetorno.get(0));
					continue;
				}
				podeChamar = verificaCamposDoc(plexiConsulta);
				if(!podeChamar) {
					break;
				}
			}
			
			docAnalise.getPlexiConsultas().removeAll(consultasExistentes);
			if(podeChamar) {
				List<PlexiConsulta> consultasFalhadas = new ArrayList<PlexiConsulta>();
				for(PlexiConsulta plexiConsulta : docAnalise.getPlexiConsultas()) {
					if(!CommonsUtil.semValor(plexiConsulta.getRequestId())) {
						continue;
					}
					FacesMessage facesMessage = plexiService.PedirConsulta(plexiConsulta, user, docAnalise);
					if(CommonsUtil.semValor(facesMessage) || CommonsUtil.mesmoValor(facesMessage.getSeverity(), 
							FacesMessage.SEVERITY_ERROR)) {
						consultasFalhadas.add(plexiConsulta);
						if(!CommonsUtil.semValor(facesMessage)) {
							context.addMessage(null, facesMessage);
						}	
					}
				}
				docAnalise.getPlexiConsultas().removeAll(consultasFalhadas);
				docAnalise.getPlexiConsultas().addAll(consultasExistentesDB);
				DocumentoAnaliseDao docAnaliseDao = new DocumentoAnaliseDao(); 
				docAnaliseDao.merge(docAnalise);
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
			}
		}
	}
	
	public void atualizarDocumentos(DocumentoAnalise docAnalise) {
		for(PlexiConsulta plexiConsulta : docAnalise.getPlexiConsultas()) {
			plexiConsulta.populatePagadorRecebedor(docAnalise.getPagador());
		}
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
	
	public void viewFilePlexi(PlexiConsulta consulta) {
		if(CommonsUtil.semValor(consulta)) {
			return;
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			String documentoBase64 = consulta.getPdf();
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Plexi: Ocorreu um problema ao gerar o PDF!", ""));
				return;
			}
			byte[] pdfBytes = java.util.Base64.getDecoder().decode(documentoBase64);
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;
			String mineFile;
			String fileExtension;
			try {
				PdfReader pdf = new PdfReader(pdfBytes);
				mineFile = "application/pdf";
				fileExtension = "pdf"; 
			} catch (Exception e) {
				mineFile = "text/html";
				fileExtension = "html"; 
			}		
			input = new BufferedInputStream(new ByteArrayInputStream(pdfBytes));
			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);
			response.setContentLength(pdfBytes.length);
			response.setHeader("Content-disposition", "inline; FileName=" + "Plexi."+fileExtension);
			output = new BufferedOutputStream(response.getOutputStream(), 10240);
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			output.flush();
			output.close();
			facesContext.responseComplete();
		} catch (NullPointerException e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Plexi: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Plexi: Ocorreu um problema ao gerar o PDF!", ""));
		}
	}
	
	public void removeDoc(DocumentoAnalise docAnalise, PlexiConsulta consulta) {
		docAnalise.getPlexiConsultas().remove(consulta);
	}
	
	public void removerPessoa(DocumentoAnalise docAnalise) {
		//docAnalise.getPlexiConsultas().clear();
		listPagador.remove(docAnalise);
	}
	
	public void adiconarDocumentospagador(DocumentoAnalise docAnalise) {
		List<PlexiDocumentos> plexiDocumentos = new ArrayList<PlexiDocumentos>();
		PlexiDocumentosDao plexiDocsDao = new PlexiDocumentosDao();
		if(CommonsUtil.semValor(docAnalise.getPlexiConsultas())) {
			docAnalise.setPlexiConsultas(new HashSet<>());
		}
		
		if(!CommonsUtil.semValor(docAnalise.getPagador().getCpf())) {
			plexiDocumentos = plexiDocsDao.getDocumentosPF(docAnalise.getEstadosConsulta(), etapa);
		} else {
			plexiDocumentos = plexiDocsDao.getDocumentosPJ(docAnalise.getEstadosConsulta(), etapa, docAnalise);
		}
		
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		
		for(PlexiDocumentos doc : plexiDocumentos) {
			PlexiConsulta plexiConsulta = new PlexiConsulta(docAnalise, doc);
			if(adicionaCamposDoc(docAnalise, plexiConsulta)) {
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsulta);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getPlexiConsultas().add(plexiConsulta);
				} else {
					PlexiConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getPlexiConsultas().add(consultasExistentesRetorno.get(0));
					}
					plexiConsulta.setDocumentoAnalise(null);
				}
			}
		}
	}
	
	public boolean verificaCamposDoc(PlexiConsulta plexiConsulta) {
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		boolean retorno = true;
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			if(CommonsUtil.semValor(plexiConsulta.getEndereco())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Endereço", ""));
			}
			
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {
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
				} else if(plexiConsulta.getUfRg().toCharArray().length > 2) {
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - UF Rg Inválido", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Nome da Mãe", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Data Nascimento", ""));
				}
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {			
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getSexo())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Sexo", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Nome Mãe", ""));
				}
			}
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf4/certidao-regional")) {
			if(!CommonsUtil.semValor(plexiConsulta.getCpf())) {			
				if(CommonsUtil.semValor(plexiConsulta.getRg())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getOrgaoExpedidor())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Orgao Rg", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getNomeMae())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Nome da Mãe", ""));
				}
				
				if(CommonsUtil.semValor(plexiConsulta.getDataNascimento())){
					retorno = false;
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Data Nascimento", ""));
				}
			}
		}
		
		/*
		 * if(CommonsUtil.mesmoValor(doc.getUrl(),
		 * "/api/maestro/fazenda-mg/certidao-debitos-tributarios")) {
		 * if(CommonsUtil.semValor(plexiConsulta.getCep())){ retorno = false;
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() +
		 * " - Falta Cep",""));}} if(CommonsUtil.mesmoValor(doc.getUrl(),
		 * "/api/maestro/fazenda-mg/certidao-debitos-tributarios")) {
		 * if(CommonsUtil.semValor(plexiConsulta.getCep())){ retorno = false;
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Cep",
		 * "")); } }
		 * 
		 * if(CommonsUtil.mesmoValor(doc.getUrl(),
		 * "/api/maestro/fazenda-sc/certidao-negativa-debitos")) {
		 * if(!CommonsUtil.semValor(plexiConsulta.getCnpj())) {
		 * if(CommonsUtil.semValor(plexiConsulta.getCpfSolicitante())){ retorno = false;
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() +
		 * " - Falta CPF Solicitante", "")); } } }
		 */

		return retorno;
	}
	
	public boolean adicionaCamposDoc(DocumentoAnalise docAnalise, PlexiConsulta plexiConsulta) {
	//retorna false caso consulta seja inserida por esse metodo
		
		PlexiDocumentos doc = plexiConsulta.getPlexiDocumentos();
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjdft/certidao-distribuicao")) {
			String[] tipoCertidaoArray = {"criminal", "civel"};
			for(String tipoCertidao : tipoCertidaoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
				plexiConsultaAux.setTipoCertidao(tipoCertidao);
				
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				} else {
					PlexiConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getPlexiConsultas().add(consultasExistentesRetorno.get(0));
					}
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrj/consulta-processual")) {
			String[] origemArray = {"primeiraInstancia"};
			String comarca = "todas";
			String[] competenciaArray = {"civel", "criminal", "criminalJuri"};
			
			for(String origem : origemArray) {
				for(String competencia : competenciaArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
					plexiConsultaAux.setOrigem(origem);
					plexiConsultaAux.setComarca(comarca);
					plexiConsultaAux.setCompetencia(competencia);
					List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
					if(consultasExistentesRetorno.size() <= 0) {
						docAnalise.getPlexiConsultas().add(plexiConsultaAux);
					} else {
						PlexiConsulta db = consultasExistentesRetorno.get(0);
						if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
								.collect(Collectors.toList()).size() <= 0) {
							docAnalise.getPlexiConsultas().add(db);
						}
						plexiConsultaAux.setDocumentoAnalise(null);
					}
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjrs/certidao-negativa")) {
			String[] tipoArray = {"3", "9"};
			for(String tipo : tipoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
				plexiConsultaAux.setTipo(tipo);
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				} else {
					PlexiConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getPlexiConsultas().add(db);
					}
					plexiConsultaAux.setDocumentoAnalise(null);
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/tjsp/certidao-negativa")) {
			String[] modeloArray = {"6", "52"};
			for(String modelo : modeloArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
				plexiConsultaAux.setModelo(modelo);
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				} else {
					PlexiConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getPlexiConsultas().add(db);
					}
					plexiConsultaAux.setDocumentoAnalise(null);
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf1/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			String[][] orgaosArray = { 
					{"ac","am","ap","ba","df","go","ma","mt","pa","pi","ro","rr","to"},
					{"varasJuizados"},
					{"regionalizada"}};
			for(String tipo : tipoArray) {
				for(String[] orgaos : orgaosArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
					plexiConsultaAux.setTipo(tipo);
					plexiConsultaAux.setOrgaos(orgaos);
					List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
					if(consultasExistentesRetorno.size() <= 0) {
						docAnalise.getPlexiConsultas().add(plexiConsultaAux);
					} else {
						PlexiConsulta db = consultasExistentesRetorno.get(0);
						if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
								.collect(Collectors.toList()).size() <= 0) {
							docAnalise.getPlexiConsultas().add(db);
						}
						plexiConsultaAux.setDocumentoAnalise(null);
					}
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf2/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			for(String tipo : tipoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
				plexiConsultaAux.setTipo(tipo);
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				} else {
					PlexiConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getPlexiConsultas().add(db);
					}
					plexiConsultaAux.setDocumentoAnalise(null);
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf3/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			String[] abrangenciaArray = {"sjsp"};//TODO sjms
			for(String tipo : tipoArray) {
				for(String abrangencia : abrangenciaArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
					plexiConsultaAux.setTipo(tipo);
					plexiConsultaAux.setAbrangencia(abrangencia);
					List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
					if(consultasExistentesRetorno.size() <= 0) {
						docAnalise.getPlexiConsultas().add(plexiConsultaAux);
					} else {
						PlexiConsulta db = consultasExistentesRetorno.get(0);
						if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
								.collect(Collectors.toList()).size() <= 0) {
							docAnalise.getPlexiConsultas().add(db);
						}
						plexiConsultaAux.setDocumentoAnalise(null);
					}
				}
			}
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf4/certidao-regional")) {
			String[] tipoArray = {"civil", "criminal"};
			for(String tipo : tipoArray) {
				PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
				plexiConsultaAux.setTipo(tipo);
				plexiConsultaAux.setEmail("tatiane@galleriabank.com.br");
				plexiConsultaAux.setSenha("r0P8Z9o8");
				List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getPlexiConsultas().add(plexiConsultaAux);
				} else {
					PlexiConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getPlexiConsultas().add(db);
					}
					plexiConsultaAux.setDocumentoAnalise(null);
				}
			}  
			return false;
		}
		
		if(CommonsUtil.mesmoValor(doc.getUrl(), 
				"/api/maestro/trf6/certidao-distribuicao")) {
			String[] tipoArray = {"civel", "criminal"};
			String[][] orgaosArray = {
					{"mg"}
					};
			for(String tipo : tipoArray) {
				for(String[] orgaos : orgaosArray) {
					PlexiConsulta plexiConsultaAux = new PlexiConsulta(docAnalise, doc);
					plexiConsultaAux.setTipo(tipo);
					plexiConsultaAux.setOrgaos(orgaos);
					List<PlexiConsulta> consultasExistentesRetorno = plexiConsultaDao.getConsultasExistentes(plexiConsultaAux);
					if(consultasExistentesRetorno.size() <= 0) {
						docAnalise.getPlexiConsultas().add(plexiConsultaAux);
					} else {
						PlexiConsulta db = consultasExistentesRetorno.get(0);
						if(docAnalise.getPlexiConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
								.collect(Collectors.toList()).size() <= 0) {
							docAnalise.getPlexiConsultas().add(db);
						}
						plexiConsultaAux.setDocumentoAnalise(null);
					}
				}
			}
			return false;
		}
		return true;
	}	
	
	public void atualizaConsultasDocumentoAnalise() {
		System.out.println("inicio");
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		plexiConsultaDao.addDocumentoAnalise();
		System.out.println("Fim");
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

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}	
}
