package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
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
import com.webnowbr.siscoat.cobranca.db.model.DocketConsulta;
import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocketConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketEstadosDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentosDocketDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name="docketMB")
@SessionScoped
public class DocketMB {
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	private List<DocumentoAnalise> listPagador;
	private String etapa;
	private ContratoCobranca contratoCobranca;
		
	public String clearFieldsContratoCobranca(List<DocumentoAnalise> listDocAnalise, String etapaConsultas, ContratoCobranca contrato) {
		etapa = etapaConsultas;
		contratoCobranca = contrato;
		listPagador = new ArrayList<DocumentoAnalise>();
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getPagador())) {
				continue;
			}
			if((CommonsUtil.mesmoValor(etapa, "analise") && docAnalise.isLiberadoAnalise())
			|| (CommonsUtil.mesmoValor(etapa, "pedir paju") && docAnalise.isLiberadoCertidoes())) {
				listPagador.add(docAnalise);
				adiconarDocumentospagador(docAnalise);
			} else if((CommonsUtil.mesmoValor(etapa, "ReAnalise") && docAnalise.isLiberadoAnalise())){
				listPagador.add(docAnalise);
				etapa = "analise";
				adiconarDocumentospagador(docAnalise);
				etapa = "pedir paju";
				adiconarDocumentospagador(docAnalise);
			} else {
				continue;
			}
		}
		
		return "/Atendimento/Cobranca/Docket.xhtml";
	}
	
	public void calcularValoresCertidoes(List<DocumentoAnalise> listDocAnalise, ContratoCobranca contrato) {
		contratoCobranca = contrato;
		listPagador = new ArrayList<DocumentoAnalise>();
		BigDecimal valorTotalCertidao = BigDecimal.ZERO;
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getPagador())) {
				continue;
			}
			
			if(docAnalise.isLiberadoAnalise()){
				listPagador.add(docAnalise);
				etapa = "ReAnalise";
				
				//adiconarDocumentospagador(docAnalise);
				List<DocumentosDocket> docketDocumentos = new ArrayList<DocumentosDocket>();
				DocumentosDocketDao docketDocsDao = new DocumentosDocketDao();
				if(!CommonsUtil.semValor(docAnalise.getPagador().getCpf())) {
					docketDocumentos = docketDocsDao.getDocumentosPF(docAnalise.getEstadosConsulta(), etapa);
				} else {
					docketDocumentos = docketDocsDao.getDocumentosPJ(docAnalise.getEstadosConsulta(), etapa);
				}
				List<DocketConsulta> listAux = new ArrayList<DocketConsulta>();
				for (DocumentosDocket doc : docketDocumentos) {
					DocketConsulta docketConsulta = null;
					for (String uf : docAnalise.getEstadosConsulta()) {
						if (doc.getEstados().contains(uf)) {
							docketConsulta = new DocketConsulta(doc);
							docketConsulta.setUf(uf);
							listAux.add(docketConsulta);
						}
					}
				}
				for (DocketConsulta docketConsulta : listAux) {
					docketConsulta.verificaValor();
					valorTotalCertidao = valorTotalCertidao.add(docketConsulta.getValorCertidao());
				}
			} else {
				continue;
			}
		}
		BigDecimal valorCartorio = BigDecimal.ZERO;
		if(!CommonsUtil.semValor(contrato.getValorCartorio()))
			valorCartorio = contrato.getValorCartorio();
		if(!CommonsUtil.semValor(contrato.getValorCertidao()))
			valorCartorio = valorCartorio.subtract(contrato.getValorCertidao());
		
		valorCartorio = valorCartorio.add(valorTotalCertidao);
		contrato.setValorCertidao(valorTotalCertidao);
		contrato.setValorCartorio(valorCartorio);
	}
	
	public void adiconarDocumentospagador(DocumentoAnalise docAnalise) {
		List<DocumentosDocket> docketDocumentos = new ArrayList<DocumentosDocket>();
		DocumentosDocketDao docketDocsDao = new DocumentosDocketDao();
		if(CommonsUtil.semValor(docAnalise.getDocketConsultas())) {
			docAnalise.setDocketConsultas(new HashSet<>());
		}
		
		if(!CommonsUtil.semValor(docAnalise.getPagador().getCpf())) {
			docketDocumentos = docketDocsDao.getDocumentosPF(docAnalise.getEstadosConsulta(), etapa);
		} else {
			docketDocumentos = docketDocsDao.getDocumentosPJ(docAnalise.getEstadosConsulta(), etapa);
		}
		
		DocketConsultaDao docketConsultaDao = new DocketConsultaDao();
		ContratoCobranca contrato = docAnalise.getContratoCobranca();
		List<DocketConsulta> listAux = new ArrayList<DocketConsulta>();
		for (DocumentosDocket doc : docketDocumentos) {
			DocketConsulta docketConsulta = null;
			/*if (doc.getDocumentoNome().contains("Federal")) {
				docketConsulta = new DocketConsulta(docAnalise, doc);
				docketConsulta.setUf(contrato.getImovel().getEstado());
				listAux.add(docketConsulta);
			} else {*/
			for (String uf : docAnalise.getEstadosConsulta()) {
				if (doc.getEstados().contains(uf)) {
					docketConsulta = new DocketConsulta(docAnalise, doc);
					docketConsulta.setUf(uf);
					listAux.add(docketConsulta);
				}
			}
			//}
		}
		for (DocketConsulta docketConsulta : listAux) {
			DocketEstadosDao estadosDao = new DocketEstadosDao();
			DocketEstados docketEstado = estadosDao.getEstadoByUf(docketConsulta.getUf());
			docketConsulta.setEstadoId(docketEstado.getIdDocket());
			docketConsulta.setCidade(docketEstado.getCapital().getNome());
			docketConsulta.setCidadeId(docketEstado.getCapital().getIdDocket());
			docketConsulta.verificaValor();
			
			List<DocketConsulta> consultasExistentesRetorno = docketConsultaDao.getConsultasExistentes(docketConsulta);
			if (consultasExistentesRetorno.size() <= 0) {
				docAnalise.getDocketConsultas().add(docketConsulta);
			} else {
				DocketConsulta db = consultasExistentesRetorno.get(0);
				if (docAnalise.getDocketConsultas().stream().filter(d -> CommonsUtil.mesmoValor(d.getId(), db.getId()))
						.collect(Collectors.toList()).size() <= 0) {
					//verificar se tem consulta com mesmo documento analise
					List<DocketConsulta> listaDbDocAnalise = consultasExistentesRetorno.stream().filter(
							t -> CommonsUtil.mesmoValor(t.getDocumentoAnalise().getId(), docAnalise.getId()))
							.collect(Collectors.toList());
					//se tiver, insere 
					if(listaDbDocAnalise.size() > 0) {
						docAnalise.getDocketConsultas().add(listaDbDocAnalise.get(0));
						docketConsulta.setDocumentoAnalise(null);
					//senão, duplica
					} else {
						docketConsulta.popularCampos(consultasExistentesRetorno.get(0));
						docAnalise.getDocketConsultas().add(docketConsulta);
					}
				}
			}
		}
	}
	
	public boolean verificaCamposDoc(DocketConsulta docketConsulta) {
		DocumentosDocket doc = docketConsulta.getDocketDocumentos();
		boolean retorno = true;
		PagadorRecebedor pagador = docketConsulta.getDocumentoAnalise().getPagador();
		
		if(!CommonsUtil.semValor(pagador.getCpf())){
			if(CommonsUtil.semValor(pagador.getNome())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, pagador.getNome() + " - Falta Nome", ""));
			}
			
			if(CommonsUtil.semValor(pagador.getNomeMae())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, pagador.getNome() + " - Falta Nome da Mãe", ""));
			}
			
			if(CommonsUtil.semValor(pagador.getDtNascimento())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, pagador.getNome() + " - Falta Data de Nascimento", ""));
			}
			
			if(CommonsUtil.semValor(pagador.getRg())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, pagador.getNome() + " - Falta RG", ""));
			}
		} else if(!CommonsUtil.semValor(pagador.getCnpj())){ 
			if(CommonsUtil.semValor(pagador.getNome())){
				retorno = false;
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, pagador.getNome() + " - Falta Nome", ""));
			}
		} else {
			retorno = false;
			FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_ERROR, pagador.getNome() + " - Falta Documento", ""));
		}
		return retorno;
	}
	
	public void criarPedido() {	//POST para gerar pedido
		FacesContext context = FacesContext.getCurrentInstance();
		DocketService docketService = new DocketService();
		DocketConsultaDao docketConsultaDao = new DocketConsultaDao();
		User user = null;
		if(!CommonsUtil.semValor(loginBean)) {
			user = loginBean.getUsuarioLogado();
		}
		boolean podeChamar = true;
		for(DocumentoAnalise docAnalise : listPagador) {
			PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
			pagadorRecebedorDao.merge(docAnalise.getPagador());
			List<DocketConsulta> consultasExistentes = new ArrayList<DocketConsulta>();
			List<DocketConsulta> consultasExistentesDB = new ArrayList<DocketConsulta>();
			
			//atualizarDocumentos(docAnalise);
			for(DocketConsulta docketConsulta : docAnalise.getDocketConsultas()) {
				docketConsulta.populatePagadorRecebedor(docAnalise.getPagador());
				/*List<DocketConsulta> consultasExistentesRetorno = docketConsultaDao.getConsultasExistentes(docketConsulta);
				if(consultasExistentesRetorno.size() > 0) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							docketConsulta.getDocketDocumentos().getDocumentoNome() + " - " + docketConsulta.getCpfCnpj() + ": Já existente", ""));
					consultasExistentes.add(docketConsulta);
					consultasExistentesDB.add(consultasExistentesRetorno.get(0));
					continue;
				}*/
				if(CommonsUtil.mesmoValor(docketConsulta.getDocketDocumentos().getEtapa(), "ReAnalise") &&
						(!docAnalise.getContratoCobranca().isEmReanalise() || !docAnalise.getContratoCobranca().isReanalisePronta())) {
					continue;
				}
				
				if(!CommonsUtil.semValor(docketConsulta.getIdDocket())) 
				continue;
				
				podeChamar = verificaCamposDoc(docketConsulta);
				if(!podeChamar) 
					break;
				
			}
			docAnalise.getDocketConsultas().removeAll(consultasExistentes);
		}
		
		if (podeChamar) {
			docketService.criaPedidoDocketDocumentoAnalise(listPagador, user, etapa);
			
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
		}
	}
		
	public void removeDoc(DocumentoAnalise docAnalise, DocketConsulta consulta) {
		docAnalise.getDocketConsultas().remove(consulta);
	}
	
	public void removerPessoa(DocumentoAnalise docAnalise) {
		//docAnalise.getDocketConsultas().clear();
		listPagador.remove(docAnalise);
	}
	
	public StreamedContent decodarBaixarArquivo(DocketConsulta consulta) {
		if(CommonsUtil.semValor(consulta) && CommonsUtil.semValor(consulta.getPdf()) ) {
			//System.out.println("Arquivo Base64 não existe");
			return null;
		}
		
		byte[] decoded = Base64.getDecoder().decode(consulta.getPdf());
		
		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		String nomeArquivoDownload = "Galleria Bank - Docket " 
				+ CommonsUtil.removeAcentos(consulta.getDocketDocumentos().getDocumentoNome())  + ".pdf";
		gerador.open(nomeArquivoDownload);
		gerador.feed(in);
		gerador.close();
		return null;
	}
	
	public void viewFileDocket(DocketConsulta consulta) {
		if(CommonsUtil.semValor(consulta)) {
			return;
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			String documentoBase64 = consulta.getPdf();
			if (CommonsUtil.semValor(documentoBase64)) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Docket: Ocorreu um problema ao gerar o PDF!", ""));
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
			response.setHeader("Content-disposition", "inline; FileName=" + "Docket."+fileExtension);
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
					"Docket: Ocorreu um problema ao gerar o PDF!", ""));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Docket: Ocorreu um problema ao gerar o PDF!", ""));
		}
	}
	
	public LoginBean getLoginBean() {
		return loginBean;
	}
	
	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
	public List<DocumentoAnalise> getListPagador() {
		return listPagador;
	}
	
	public void setListPagador(List<DocumentoAnalise> listPagador) {
		this.listPagador = listPagador;
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