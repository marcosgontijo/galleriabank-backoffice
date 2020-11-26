package com.webnowbr.siscoat.cobranca.mb.directd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@ManagedBean(name = "consultaRobos")
@SessionScoped
public class ConsultaRobos {
	
	StreamedContent downloadAllFiles;
	
	private String cnpj;
	private String cpf;
	private boolean consultasGeradas;
	private String nome;
	private String estado;
	
	private List<String> selectedConsultas;
    private List<String> selectedTRF3;
    private List<String> selectedProtesto;
    private List<String> selectedTST;
    private List<String> selectedCADIN;
    
    private boolean consultaPor;
	
	@ManagedProperty(value = "#{consultaTRF3}")
	protected ConsultaTRF3 consultaTRF3;
	
	@ManagedProperty(value = "#{consultaPFPlus}")
	protected ConsultaPFPlus consultaPFPlus;
	
	@ManagedProperty(value = "#{consultaPF}")
	protected ConsultaPF consultaPF;
	
	@ManagedProperty(value = "#{consultaPJ}")
	protected ConsultaPJ consultaPJ;
	
	@ManagedProperty(value = "#{consultaAntCriminais}")
	protected ConsultaAntCriminais consultaAntCriminais;
	
	@ManagedProperty(value = "#{consultaCND}")
	protected ConsultaCND consultaCND;
	
	@ManagedProperty(value = "#{consultaCNDFederal}")
	protected ConsultaCNDFederal consultaCNDFederal;
	
	@ManagedProperty(value = "#{consultaProtestosIEPTB}")
	protected ConsultaProtestosIEPTB consultaProtestosIEPTB;
	
	@ManagedProperty(value = "#{consultaTST}")
	protected ConsultaTST consultaTST;
	
	@ManagedProperty(value = "#{consultaPEP}")
	protected ConsultaPEP consultaPEP;
	
	@ManagedProperty(value = "#{consultaCadin}")
	protected ConsultaCadin consultaCadin;
	
	@ManagedProperty(value = "#{consultaTribunalJusticaSP}")
	protected ConsultaTribunalJusticaSP consultaTribunalJusticaSP;
	
	@ManagedProperty(value = "#{consultaPJQSA}")
	protected ConsultaPJQSA consultaPJQSA;
	
	public ConsultaRobos() {
		this.consultaTRF3 = new ConsultaTRF3();
		this.consultaPFPlus = new ConsultaPFPlus();
		this.consultaPF = new ConsultaPF();
		this.consultaPJ = new ConsultaPJ();
		this.consultaAntCriminais = new ConsultaAntCriminais();
		this.consultaCND = new ConsultaCND();
		this.consultaCNDFederal = new ConsultaCNDFederal();
		this.consultaProtestosIEPTB = new ConsultaProtestosIEPTB();
		this.consultaTST = new ConsultaTST();
		this.consultaPEP = new ConsultaPEP();
		this.consultaCadin = new ConsultaCadin();
		this.consultaTribunalJusticaSP = new ConsultaTribunalJusticaSP();
		this.consultaPJQSA = new ConsultaPJQSA();
	}
	
	public String clearFields() {
		this.cnpj = "";
		this.cpf = "";
		this.nome = "";
		this.estado = "";
		
		this.consultaPor = true;
		
		this.selectedConsultas = new ArrayList<String>();
		this.selectedConsultas.add("Pessoa Jurídica");
		
		this.selectedTRF3 = new ArrayList<String>();
		this.selectedTRF3.add("CNPJ");
		
		this.selectedProtesto = new ArrayList<String>();
		this.selectedProtesto.add("CNPJ");
		
		this.selectedTST = new ArrayList<String>();
		this.selectedTST.add("CNPJ");
		
		this.selectedCADIN = new ArrayList<String>();
		this.selectedCADIN.add("CNPJ");
        
		this.consultasGeradas = false;
		
		return "/Atendimento/ConsultasDirectd/ConsultaRobos.xhtml";
	}
	
	public void gerarConsultasRobos() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.consultasGeradas = true;
		
		if (this.consultaPor) {		
			// TRF 3
			consultaTRF3.clearConsultaTRF3();
			consultaTRF3.setTipoDocumento(false);
			consultaTRF3.setCnpj(this.cnpj);			
			consultaTRF3.consultaTRF3();
	
			if (this.consultaTRF3.isConsultaGerada()) {
				consultaTRF3.gerarPDF();
			}
			
			// PJ
			consultaPJ.clearConsultaPJ();
			consultaPJ.setCnpj(this.cnpj);
			consultaPJ.consultaPJ();
			
			if (this.consultaPJ.isConsultaGerada()) {
				consultaPJ.gerarPDF();
			}
	
			// PROTESTOS
			this.consultaProtestosIEPTB.clearProtestosIEPTB();
			this.consultaProtestosIEPTB.setCnpj(this.cnpj);			
			this.consultaProtestosIEPTB.consultaProtestosIEPTB();
			
			if (this.consultaProtestosIEPTB.isConsultaGerada()) {
				this.consultaProtestosIEPTB.gerarPDF();
			}
			
			// TST
			this.consultaTST.clearConsultaTST();
			this.consultaTST.setCnpj(cnpj);
			this.consultaTST.consultaTST();
			
			if (this.consultaTST.isConsultaGerada()) {
				this.consultaTST.gerarPDF();
			}
			
			// CND
			consultaCND.clearConsultaCND();
			consultaCND.setCnpj(this.cnpj);
			consultaCND.setUf("São Paulo");
			consultaCND.consultaCND();
			
			if (this.consultaCND.isConsultaGerada()) {
				consultaCND.gerarPDF();
			}
					
			// CND Federal
			consultaCNDFederal.clearConsultaCND();
			consultaCNDFederal.setCnpj(this.cnpj);
			consultaCNDFederal.consultaCND();
			
			if (this.consultaCNDFederal.isConsultaGerada()) {
				consultaCNDFederal.gerarPDF();
			}
			
			// CADIN
			consultaCadin.clearConsultaCadin();
			consultaCadin.setCnpj(this.cnpj);
			consultaCadin.consultaCadin();
			
			if (this.consultaCadin.isConsultaGerada()) {
				consultaCadin.gerarPDF();
			}
			
			/*
			 *TODO
			// TJSP
			consultaTribunalJusticaSP.clearConsultaTribunalJusticaSP();
			consultaTribunalJusticaSP.setCnpj(this.cnpj);
			consultaTribunalJusticaSP.consultaTribunalJusticaSP();
			
			if (this.consultaTribunalJusticaSP.isConsultaGerada()) {
				consultaTribunalJusticaSP.gerarPDF();
			}
			*/
	
			// PJ QSA
			consultaPJQSA.clearConsultaPJQSA();
			consultaPJQSA.setCnpj(this.cnpj);
			consultaPJQSA.consultaPJQSA();
			
			if (this.consultaPJQSA.isConsultaGerada()) {
				consultaPJQSA.gerarPDF();
			}
		} else {
			// PF PLUS
			consultaPFPlus.clearConsultaPFPlus();
			consultaPFPlus.setCpf(cpf);
			consultaPFPlus.consultaPFPlus();
			
			if (this.consultaPFPlus.isConsultaGerada()) {
				consultaPFPlus.gerarPDF();
			}
			
			// PF
			consultaPF.clearConsultaPF();
			consultaPF.setCpf(cpf);
			consultaPF.consultaPF();
			
			if (this.consultaPJ.isConsultaGerada()) {
				consultaPF.gerarPDF();
			}
			
			// Ant Criminais
			consultaAntCriminais.clearConsultaAntCriminais();
			consultaAntCriminais.setCpf(this.cpf);	
			consultaAntCriminais.setNome(this.nome);
			consultaAntCriminais.consultaAntCriminais();
			
			if (this.consultaAntCriminais.isConsultaGerada()) {
				consultaAntCriminais.gerarPDF();
			}
			
			// PEP
			consultaPEP.clearConsultaPEP();
			consultaPEP.setCpf(this.cpf);
			consultaPEP.consultaPEP();
			
			if (this.consultaPEP.isConsultaGerada()) {
				consultaPEP.gerarPDF();
			}
			
			// PROTESTOS
			this.consultaProtestosIEPTB.clearProtestosIEPTB();
			this.consultaProtestosIEPTB.setCpf(this.cpf);
			this.consultaProtestosIEPTB.consultaProtestosIEPTB();
			
			if (this.consultaProtestosIEPTB.isConsultaGerada()) {
				this.consultaProtestosIEPTB.gerarPDF();
			}
			
			// TST
			this.consultaTST.clearConsultaTST();
			this.consultaTST.setCpf(this.cpf);
			this.consultaTST.consultaTST();
			
			if (this.consultaTST.isConsultaGerada()) {
				this.consultaTST.gerarPDF();
			}
			
			// CADIN
			consultaCadin.clearConsultaCadin();
			consultaCadin.setCpf(this.cpf);
			
			consultaCadin.consultaCadin();
			
			if (this.consultaCadin.isConsultaGerada()) {
				consultaCadin.gerarPDF();
			}
		}
				
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				"Consultas Robos: Consultas geradas com sucesso!"
				, ""));		
	}
	
	/**
	 * Método para fazer download de todos os arquivos do diretório do contrato
	 * @return
	 */
	public StreamedContent getDownloadAllFiles() {
		try {
			// recupera path do contrato
			ParametrosDao pDao = new ParametrosDao(); 
			String pathContrato = pDao.findByFilter("nome", "CONSULTAS_DIRECTD").get(0).getValorString();
			
			// cria objetos para ZIP
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			// cria arquivo ZIP
			fileWriter = new FileOutputStream(pathContrato + "Consultas_Robos.zip");
			zip = new ZipOutputStream(fileWriter);
			
			// TRF 3			
			if (this.consultaTRF3.isPdfGerado()) {
				addFileToZip("", consultaTRF3.getPathPDF() + consultaTRF3.getNomePDF(), zip);
			}	
			
			// PF PLUS			
			if (this.consultaPFPlus.isPdfGerado()) {
				addFileToZip("", consultaPFPlus.getPathPDF() + consultaPFPlus.getNomePDF(), zip);
			}	

			// PF			
			if (this.consultaPF.isPdfGerado()) {
				addFileToZip("", consultaPF.getPathPDF() + consultaPF.getNomePDF(), zip);
			}	
			
			// PJ			
			if (this.consultaPJ.isPdfGerado()) {
				addFileToZip("", consultaPJ.getPathPDF() + consultaPJ.getNomePDF(), zip);
			}	
			
			// TST
			if (this.consultaTST.isPdfGerado()) {
				addFileToZip("", consultaTST.getPathPDF() + consultaTST.getNomePDF(), zip);
			}
							
			// Protestos
			if (this.consultaProtestosIEPTB.isPdfGerado()) {
				addFileToZip("", consultaProtestosIEPTB.getPathPDF() + consultaProtestosIEPTB.getNomePDF(), zip);
			}
			
			// Ant Criminais			
			if (this.consultaAntCriminais.isPdfGerado()) {
				addFileToZip("", consultaAntCriminais.getPathPDF() + consultaAntCriminais.getNomePDF(), zip);
			}	
						
			// CND			
			if (this.consultaCND.isPdfGerado()) {
				addFileToZip("", consultaCND.getPathPDF() + consultaCND.getNomePDF(), zip);
			}	
				
			// CND Federal
			if (this.consultaCNDFederal.isPdfGerado()) {
				addFileToZip("", consultaCNDFederal.getPathPDF() + consultaCNDFederal.getNomePDF(), zip);
			}						
			
	
			
			// PEP
			if (this.consultaPEP.isPdfGerado()) {
				addFileToZip("", consultaPEP.getPathPDF() + consultaPEP.getNomePDF(), zip);
			}	
			
			// CADIN
			if (this.consultaCadin.isPdfGerado()) {
				addFileToZip("", consultaCadin.getPathPDF() + consultaCadin.getNomePDF(), zip);
			}	
			
			// TJSP
			/*
			if (this.consultaTribunalJusticaSP.isPdfGerado()) {
				addFileToZip("", consultaTribunalJusticaSP.getPathPDF() + consultaTribunalJusticaSP.getNomePDF(), zip);
			}
			*/	
			
			// PJ QSA
			if (this.consultaPJQSA.isPdfGerado()) {
				addFileToZip("", consultaPJQSA.getPathPDF() + consultaPJQSA.getNomePDF(), zip);
			}	
						
			// Fecha o ZIP
			zip.flush();
			zip.close();
			
			// Recupera ZIP gerado para fazer download
			FileInputStream stream = new FileInputStream(pathContrato + "Consultas_Robos.zip");
			downloadAllFiles = new DefaultStreamedContent(stream, pathContrato, "Consultas_Robos.zip");

		} catch (Exception e) {
			System.out.println(e);
		}
		
		return this.downloadAllFiles;
	}
	
	/***
	 * Exemplo de adicionar arquivos a um zip existente
	 * @param path
	 * @param srcFile
	 * @param zip
	 * @throws Exception
	 */
	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
			throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}
	
	/**
	 * Exemplo de adicionar uam pasta a um zip existente
	 * @param path
	 * @param srcFolder
	 * @param zip
	 * @throws Exception
	 */
	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
			throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setDownloadAllFiles(StreamedContent downloadAllFiles) {
		this.downloadAllFiles = downloadAllFiles;
	}

	public ConsultaTRF3 getConsultaTRF3() {
		return consultaTRF3;
	}

	public void setConsultaTRF3(ConsultaTRF3 consultaTRF3) {
		this.consultaTRF3 = consultaTRF3;
	}

	public ConsultaPFPlus getConsultaPFPlus() {
		return consultaPFPlus;
	}

	public void setConsultaPFPlus(ConsultaPFPlus consultaPFPlus) {
		this.consultaPFPlus = consultaPFPlus;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public ConsultaPF getConsultaPF() {
		return consultaPF;
	}

	public void setConsultaPF(ConsultaPF consultaPF) {
		this.consultaPF = consultaPF;
	}

	public ConsultaPJ getConsultaPJ() {
		return consultaPJ;
	}

	public void setConsultaPJ(ConsultaPJ consultaPJ) {
		this.consultaPJ = consultaPJ;
	}

	public ConsultaAntCriminais getConsultaAntCriminais() {
		return consultaAntCriminais;
	}

	public void setConsultaAntCriminais(ConsultaAntCriminais consultaAntCriminais) {
		this.consultaAntCriminais = consultaAntCriminais;
	}

	public ConsultaCND getConsultaCND() {
		return consultaCND;
	}

	public void setConsultaCND(ConsultaCND consultaCND) {
		this.consultaCND = consultaCND;
	}

	public ConsultaCNDFederal getConsultaCNDFederal() {
		return consultaCNDFederal;
	}

	public void setConsultaCNDFederal(ConsultaCNDFederal consultaCNDFederal) {
		this.consultaCNDFederal = consultaCNDFederal;
	}

	public ConsultaProtestosIEPTB getConsultaProtestosIEPTB() {
		return consultaProtestosIEPTB;
	}

	public void setConsultaProtestosIEPTB(ConsultaProtestosIEPTB consultaProtestosIEPTB) {
		this.consultaProtestosIEPTB = consultaProtestosIEPTB;
	}

	public ConsultaTST getConsultaTST() {
		return consultaTST;
	}

	public void setConsultaTST(ConsultaTST consultaTST) {
		this.consultaTST = consultaTST;
	}

	public ConsultaPEP getConsultaPEP() {
		return consultaPEP;
	}

	public void setConsultaPEP(ConsultaPEP consultaPEP) {
		this.consultaPEP = consultaPEP;
	}

	public ConsultaCadin getConsultaCadin() {
		return consultaCadin;
	}

	public void setConsultaCadin(ConsultaCadin consultaCadin) {
		this.consultaCadin = consultaCadin;
	}

	public ConsultaTribunalJusticaSP getConsultaTribunalJusticaSP() {
		return consultaTribunalJusticaSP;
	}

	public void setConsultaTribunalJusticaSP(ConsultaTribunalJusticaSP consultaTribunalJusticaSP) {
		this.consultaTribunalJusticaSP = consultaTribunalJusticaSP;
	}

	public ConsultaPJQSA getConsultaPJQSA() {
		return consultaPJQSA;
	}

	public void setConsultaPJQSA(ConsultaPJQSA consultaPJQSA) {
		this.consultaPJQSA = consultaPJQSA;
	}

	public boolean isConsultasGeradas() {
		return consultasGeradas;
	}

	public void setConsultasGeradas(boolean consultasGeradas) {
		this.consultasGeradas = consultasGeradas;
	}

	public List<String> getSelectedConsultas() {
		return selectedConsultas;
	}

	public void setSelectedConsultas(List<String> selectedConsultas) {
		this.selectedConsultas = selectedConsultas;
	}

	public List<String> getSelectedTRF3() {
		return selectedTRF3;
	}

	public void setSelectedTRF3(List<String> selectedTRF3) {
		this.selectedTRF3 = selectedTRF3;
	}

	public List<String> getSelectedProtesto() {
		return selectedProtesto;
	}

	public void setSelectedProtesto(List<String> selectedProtesto) {
		this.selectedProtesto = selectedProtesto;
	}

	public List<String> getSelectedTST() {
		return selectedTST;
	}

	public void setSelectedTST(List<String> selectedTST) {
		this.selectedTST = selectedTST;
	}

	public List<String> getSelectedCADIN() {
		return selectedCADIN;
	}

	public void setSelectedCADIN(List<String> selectedCADIN) {
		this.selectedCADIN = selectedCADIN;
	}

	public boolean isConsultaPor() {
		return consultaPor;
	}

	public void setConsultaPor(boolean consultaPor) {
		this.consultaPor = consultaPor;
	}
}