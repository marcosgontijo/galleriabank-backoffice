package com.webnowbr.siscoat.cobranca.ws.netrin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.pdf.PdfReader;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

/** ManagedBean. */
@ManagedBean(name = "netrinMB")
@SessionScoped
public class NetrinMB {
	
	private List<DocumentoAnalise> listPagador;
	private String etapa;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	public UploadedFile uploadedFile;
	//NetrinConsulta netrinConsulta = new NetrinConsulta();
	String cpfCnpj;
	
	
	public String clearFieldsContratoCobranca(List<DocumentoAnalise> listDocAnalise, String etapaConsultas) {
		//estados = new ArrayList<String>();
		//estados.add(estadoImovel);
		etapa = etapaConsultas;
		listPagador = new ArrayList<DocumentoAnalise>();
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getPagador())) {
				continue;
			}
			if((CommonsUtil.mesmoValor(etapa, "analise") && docAnalise.isLiberadoAnalise())
			|| (CommonsUtil.mesmoValor(etapa, "pedir paju") && docAnalise.isLiberadoCertidoes())) {
				listPagador.add(docAnalise);
				
				//if(CommonsUtil.semValor(docAnalise.getNetrinConsultas()) || docAnalise.getNetrinConsultas().size() == 0) {
					adiconarDocumentospagador(docAnalise);
				//}
			} else {
				continue;
			}
		}
		
		return "/Atendimento/Cobranca/Netrin.xhtml";
	}	
	
	public void criarPedido() {	//POST para gerar pedido
		FacesContext context = FacesContext.getCurrentInstance();
		NetrinService netrinService = new NetrinService();
		NetrinConsultaDao netrinConsultaDao = new NetrinConsultaDao();
		User user = null;
		if(!CommonsUtil.semValor(loginBean)) {
			user = loginBean.getUsuarioLogado();
		}
		
		for(DocumentoAnalise docAnalise : listPagador) {
			List<NetrinConsulta> consultasExistentes = new ArrayList<NetrinConsulta>();
			List<NetrinConsulta> consultasExistentesDB = new ArrayList<NetrinConsulta>();
			boolean podeChamar = true;
			//atualizarDocumentos(docAnalise);
			for(NetrinConsulta netrinConsulta : docAnalise.getNetrinConsultas()) {
				netrinConsulta.populatePagadorRecebedor(docAnalise.getPagador());
				List<NetrinConsulta> consultasExistentesRetorno = netrinConsultaDao.getConsultasExistentes(netrinConsulta);
				if(consultasExistentesRetorno.size() > 0) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							netrinConsulta.getNetrinDocumentos().getNome() + " - " + netrinConsulta.getCpfCnpj() + ": Já existente", ""));
					consultasExistentes.add(netrinConsulta);
					consultasExistentesDB.add(consultasExistentesRetorno.get(0));
					continue;
				}
				podeChamar = verificaCamposDoc(netrinConsulta);
				if(!podeChamar) {
					break;
				}
			}
			
			docAnalise.getNetrinConsultas().removeAll(consultasExistentes);
			if(podeChamar) {
				List<NetrinConsulta> consultasFalhadas = new ArrayList<NetrinConsulta>();
				for(NetrinConsulta netrinConsulta : docAnalise.getNetrinConsultas()) {
					if(!CommonsUtil.semValor(netrinConsulta.getRetorno())) {
						continue;
					}
					FacesMessage facesMessage = netrinService.pedirConsulta(netrinConsulta);
					if(CommonsUtil.semValor(facesMessage) || CommonsUtil.mesmoValor(facesMessage.getSeverity(), 
							FacesMessage.SEVERITY_ERROR)) {
						consultasFalhadas.add(netrinConsulta);
						if(!CommonsUtil.semValor(facesMessage)) {
							context.addMessage(null, facesMessage);
						}	
					}
				}
				docAnalise.getNetrinConsultas().removeAll(consultasFalhadas);
				docAnalise.getNetrinConsultas().addAll(consultasExistentesDB);
				DocumentoAnaliseDao docAnaliseDao = new DocumentoAnaliseDao(); 
				docAnaliseDao.merge(docAnalise);
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
			}
		}
	}
	
	public void atualizarDocumentos(DocumentoAnalise docAnalise) {
		for(NetrinConsulta netrinConsulta : docAnalise.getNetrinConsultas()) {
			netrinConsulta.populatePagadorRecebedor(docAnalise.getPagador());
		}
	}
	
	public StreamedContent decodarBaixarArquivo(NetrinConsulta consulta) {
		if(CommonsUtil.semValor(consulta) && CommonsUtil.semValor(consulta.getPdf()) ) {
			//System.out.println("Arquivo Base64 não existe");
			return null;
		}
		
		byte[] decoded = Base64.getDecoder().decode(consulta.getPdf());
		
		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		String nomeArquivoDownload = "Galleria Bank - Netrin " 
		+ CommonsUtil.removeAcentos(consulta.getNetrinDocumentos().getNome())  + ".pdf";
		gerador.open(nomeArquivoDownload);
		gerador.feed(in);
		gerador.close();
		return null;
	}
	
	public void viewFileNetrin(NetrinConsulta consulta) {
		if(CommonsUtil.semValor(consulta)) {
			return;
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			String documentoBase64 = consulta.getPdf();
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Netrin: Ocorreu um problema ao gerar o PDF!", ""));
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
			response.setHeader("Content-disposition", "inline; FileName=" + "Netrin."+fileExtension);
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
					"Netrin: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Netrin: Ocorreu um problema ao gerar o PDF!", ""));
		}
	}
	
	public void removeDoc(DocumentoAnalise docAnalise, NetrinConsulta consulta) {
		docAnalise.getNetrinConsultas().remove(consulta);
	}
	
	public void removerPessoa(DocumentoAnalise docAnalise) {
		docAnalise.getNetrinConsultas().clear();
		listPagador.remove(docAnalise);
	}
	
	public void adiconarDocumentospagador(DocumentoAnalise docAnalise) {
		List<NetrinDocumentos> netrinDocumentos = new ArrayList<NetrinDocumentos>();
		NetrinDocumentosDao netrinDocsDao = new NetrinDocumentosDao();
		if(CommonsUtil.semValor(docAnalise.getNetrinConsultas())) {
			docAnalise.setNetrinConsultas(new HashSet<>());
		}
		
		if(!CommonsUtil.semValor(docAnalise.getPagador().getCpf())) {
			netrinDocumentos = netrinDocsDao.getDocumentosPF(docAnalise.getEstadosConsulta(), etapa);
		} else {
			netrinDocumentos = netrinDocsDao.getDocumentosPJ(docAnalise.getEstadosConsulta(), etapa);
		}
		
		NetrinConsultaDao netrinConsultaDao = new NetrinConsultaDao();
		
		for(NetrinDocumentos doc : netrinDocumentos) {
			if(CommonsUtil.mesmoValor(doc.getUrlService(), "/api/v1/CNDEstadual")){
				for(String estado : docAnalise.getEstadosConsulta()) {
					NetrinConsulta netrinConsulta = new NetrinConsulta(docAnalise, doc);
					netrinConsulta.setUf(estado);
					List<NetrinConsulta> consultasExistentesRetorno = netrinConsultaDao.getConsultasExistentes(netrinConsulta);
					if(consultasExistentesRetorno.size() <= 0) {
						docAnalise.getNetrinConsultas().add(netrinConsulta);
					} else {
						NetrinConsulta db = consultasExistentesRetorno.get(0);
						if(docAnalise.getNetrinConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
								.collect(Collectors.toList()).size() <= 0) {
							docAnalise.getNetrinConsultas().add(consultasExistentesRetorno.get(0));
						}
						netrinConsulta.setDocumentoAnalise(null);
					}
				}
			} else {
				NetrinConsulta netrinConsulta = new NetrinConsulta(docAnalise, doc);
				List<NetrinConsulta> consultasExistentesRetorno = netrinConsultaDao.getConsultasExistentes(netrinConsulta);
				if(consultasExistentesRetorno.size() <= 0) {
					docAnalise.getNetrinConsultas().add(netrinConsulta);
				} else {
					NetrinConsulta db = consultasExistentesRetorno.get(0);
					if(docAnalise.getNetrinConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
							.collect(Collectors.toList()).size() <= 0) {
						docAnalise.getNetrinConsultas().add(consultasExistentesRetorno.get(0));
					}
					netrinConsulta.setDocumentoAnalise(null);
				}
			}
		}
	}
	
	public boolean verificaCamposDoc(NetrinConsulta netrinConsulta) {
		NetrinDocumentos doc = netrinConsulta.getNetrinDocumentos();
		boolean retorno = true;
		
		if(CommonsUtil.mesmoValor(doc.getUrlService(), "/api/v1/CNDEstadual")
				&& CommonsUtil.mesmoValor(netrinConsulta.getUf().toUpperCase().trim(), "MG")) {
			if(CommonsUtil.semValor(netrinConsulta.getCep())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, doc.getNome() + " - Falta Cep p/ MG", ""));
			}
		}
		return retorno;
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

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	
}
