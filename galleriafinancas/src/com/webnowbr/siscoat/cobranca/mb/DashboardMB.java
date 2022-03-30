package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFPatternFormatting;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Dashboard;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DashboardDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name = "dashboardMB")
@SessionScoped
public class DashboardMB {

    private PieChartModel pieOrigemLead;
    private PieChartModel pieStatusLead;
    
    private Date dataInicio;
    private Date dataFim;
    private boolean consultaStatus;
    
    private List<Dashboard> dashContratos;
    
	private Responsavel selectedResponsavel;
	
	private List<Responsavel> listResponsavel;
	
	int totalContratosCadastrados;
	int totalContratosPreAprovados;
	int totalContratosBoletosPagos;
	int totalContratosCcbsEmitidas;
	int totalContratosRegistrados;
	
	BigDecimal totalValorContratosCadastrados;
	BigDecimal totalValorContratosPreAprovados;
	BigDecimal totalValorBoletosPagos;
	BigDecimal totalValorCcbsEmitidas;
	BigDecimal totalValorContratosRegistrados;
	
	private String updateResponsavel = ":form:responsavel";
	
	public DashboardMB() {
		
	}
	
	public final void populateSelectedResponsavel() {
		this.selectedResponsavel = this.selectedResponsavel;
	}
	
	public void clearResponsavel() {
		this.selectedResponsavel = new Responsavel();
		this.updateResponsavel = ":form:responsavel";
	}
	
	public String clearFieldsDashContratos() {
		
		this.totalContratosCadastrados = 0;
		this.totalContratosPreAprovados = 0;
		this.totalContratosBoletosPagos = 0;
		this.totalContratosCcbsEmitidas = 0;
		this.totalContratosRegistrados = 0;
		
		this.totalValorContratosCadastrados = BigDecimal.ZERO;
		this.totalValorContratosPreAprovados = BigDecimal.ZERO;
		this.totalValorBoletosPagos = BigDecimal.ZERO;
		this.totalValorCcbsEmitidas = BigDecimal.ZERO;
		this.totalValorContratosRegistrados = BigDecimal.ZERO;
		
		this.dataInicio = null;
		this.dataFim = null;
		this.selectedResponsavel = new Responsavel();
		ResponsavelDao rDao = new ResponsavelDao();
		this.listResponsavel = rDao.findAll();
		
		this.dashContratos = new ArrayList<Dashboard>();
		
		return "/Atendimento/Cobranca/DashboardManager.xhtml";
	}
	
	public void processaDashContratos() {
		
		DashboardDao dDao = new DashboardDao();
		if(CommonsUtil.semValor(this.selectedResponsavel.getId())) {
				this.dashContratos = dDao.getDashboardContratos(this.dataInicio, this.dataFim, this.consultaStatus);	
		} else {
				this.dashContratos = dDao.getDashboardContratosPorGerente(this.dataInicio, this.dataFim, this.selectedResponsavel.getId(), this.consultaStatus);
		}
		calculaSoma();
	}

	public void calculaSoma() {
		this.totalContratosCadastrados = 0;
		this.totalContratosPreAprovados = 0;
		this.totalContratosBoletosPagos = 0;
		this.totalContratosCcbsEmitidas = 0;
		this.totalContratosRegistrados = 0;
		
		this.totalValorContratosCadastrados = BigDecimal.ZERO;
		this.totalValorContratosPreAprovados = BigDecimal.ZERO;
		this.totalValorBoletosPagos = BigDecimal.ZERO;
		this.totalValorCcbsEmitidas = BigDecimal.ZERO;
		this.totalValorContratosRegistrados = BigDecimal.ZERO;
		
		for (Dashboard dash : this.getDashContratos()) {
			
			if (!CommonsUtil.semValor(dash.getContratosCadastrados())) {
				this.totalContratosCadastrados = totalContratosCadastrados + dash.getContratosCadastrados();
			}
			if (!CommonsUtil.semValor(dash.getContratosPreAprovados())) {
				this.totalContratosPreAprovados = totalContratosPreAprovados + dash.getContratosPreAprovados();
			}
			if (!CommonsUtil.semValor(dash.getContratosBoletosPagos())) {
				this.totalContratosBoletosPagos = totalContratosBoletosPagos + dash.getContratosBoletosPagos();
			}
			if (!CommonsUtil.semValor(dash.getContratosCcbsEmitidas())) {
				this.totalContratosCcbsEmitidas = totalContratosCcbsEmitidas + dash.getContratosCcbsEmitidas();
			}
			if (!CommonsUtil.semValor(dash.getContratosRegistrados())) {
				this.totalContratosRegistrados = totalContratosRegistrados + dash.getContratosRegistrados();
			}
			
			if (!CommonsUtil.semValor(dash.getValorContratosCadastrados())) {
				this.totalValorContratosCadastrados = totalValorContratosCadastrados.add(dash.getValorContratosCadastrados());
			}
			if (!CommonsUtil.semValor(dash.getValorContratosPreAprovados())) {
				this.totalValorContratosPreAprovados = totalValorContratosPreAprovados.add(dash.getValorContratosPreAprovados());
			}
			if (!CommonsUtil.semValor(dash.getValorBoletosPagos())) {
				this.totalValorBoletosPagos = totalValorBoletosPagos.add(dash.getValorBoletosPagos());
			}
			if (!CommonsUtil.semValor(dash.getValorCcbsEmitidas())) {
				this.totalValorCcbsEmitidas = totalValorCcbsEmitidas.add(dash.getValorCcbsEmitidas());
			}
			if (!CommonsUtil.semValor(dash.getValorContratosRegistrados())) {
				this.totalValorContratosRegistrados = totalValorContratosRegistrados.add(dash.getValorContratosRegistrados());
			}
		}
	}
	
	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	private void gravaCelula(Integer celula, int value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}
	
	private void formataCelula(Cell celula, XSSFWorkbook wb) {
		 CellStyle cellStyle = wb.createCellStyle();
		 cellStyle.setAlignment(HorizontalAlignment.CENTER);
		 cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		 celula.setCellStyle(cellStyle);
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever, String moeda) {
		if (text != null && text.contains(valorEscrito)) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetario(valorSobrescrever, moeda));
			r.setText(text, 0);
		}
		return text;
	}
	
	public StreamedContent readXLSXFile() throws IOException {
		//String sheetName =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/DashboardTabela.xlsx"));
		
		XSSFSheet sheet = wb.getSheetAt(0);
		
		int iLinha = 1;
		for (int iDashboard = 0 ; iDashboard < this.dashContratos.size();iDashboard++) {
			Dashboard dash = this.dashContratos.get(iDashboard);
			
			XSSFRow linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			gravaCelula(0, dash.getNomeResponsavel(), linha);
			formataCelula(linha.getCell(0), wb);
			gravaCelula(1, dash.getGerenteResponsavel(), linha);
			gravaCelula(2, dash.getContratosCadastrados(), linha);
			gravaCelula(3, CommonsUtil.formataValorMonetario(dash.getValorContratosCadastrados(),"R$ "), linha);
			gravaCelula(4, dash.getContratosPreAprovados(), linha);
			gravaCelula(5, CommonsUtil.formataValorMonetario(dash.getValorContratosPreAprovados(),"R$ "), linha);
			gravaCelula(6, dash.getContratosBoletosPagos(), linha);
			gravaCelula(7, CommonsUtil.formataValorMonetario(dash.getValorBoletosPagos(),"R$ "), linha);
			gravaCelula(8, dash.getContratosCcbsEmitidas(), linha);
			gravaCelula(9, CommonsUtil.formataValorMonetario(dash.getValorCcbsEmitidas(),"R$ "), linha);
			gravaCelula(10, dash.getContratosRegistrados(), linha);
			gravaCelula(11, CommonsUtil.formataValorMonetario(dash.getValorContratosRegistrados(),"R$ "), linha);	
			
			iLinha++;
		}
		
		calculaSoma();
		XSSFRow linha = sheet.getRow(1);
		
		gravaCelula(15, this.totalContratosCadastrados, linha);
		gravaCelula(16, CommonsUtil.formataValorMonetario(this.totalValorContratosPreAprovados,"R$ "), linha);
		gravaCelula(17, this.totalContratosPreAprovados, linha);
		gravaCelula(18, CommonsUtil.formataValorMonetario(this.totalValorContratosCadastrados,"R$ "), linha);	
		gravaCelula(19, this.totalContratosBoletosPagos, linha);
		gravaCelula(20, CommonsUtil.formataValorMonetario(this.totalValorBoletosPagos,"R$ "), linha);	
		gravaCelula(21, this.totalContratosCcbsEmitidas, linha);
		gravaCelula(22, CommonsUtil.formataValorMonetario(this.totalValorCcbsEmitidas,"R$ "), linha);	
		gravaCelula(23, this.totalContratosRegistrados, linha);
		gravaCelula(24, CommonsUtil.formataValorMonetario(this.totalValorContratosRegistrados,"R$ "), linha);
		
		
		
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		
		gerador.open(String.format("Galleria Bank - DashBoardTabela %s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;
	}
	
	public String clearFields() {
		//createPieModel();
		origemLead();
		statusLead();
		this.consultaStatus = false;
		return "/Atendimento/Cobranca/Dashboard.xhtml";
	}
	
    public void statusLead() {
    	pieStatusLead = new PieChartModel();
        ChartData data = new ChartData();
        
		Dashboard dashboard = new Dashboard();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao(); 
		try {
			dashboard = cDao.getStatusLeads();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<Number>();
        values.add(dashboard.getNovoLead());
        values.add(dashboard.getLeadEmTratamento());
        dataSet.setData(values);
        
        List<String> bgColors = new ArrayList<String>();
        bgColors.add("rgb(81, 132, 225)");
        bgColors.add("rgb(225, 223, 81)");
        dataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<String>();
        labels.add("Novo Lead");
        labels.add("Em Tratamento");
        
        data.setLabels(labels);

        //pieModel1.set("Não definidos", dashboard.getOutrasOrigens());
        
        pieStatusLead.setData(data);
    }

    public void origemLead() {
    	pieOrigemLead = new PieChartModel();
        ChartData data = new ChartData();
        
		Dashboard dashboard = new Dashboard();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao(); 
		try {
			dashboard = cDao.getOrigemLeads();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<Number>();
        values.add(dashboard.getEmprestimoComTerrenoEmGarantia());
        values.add(dashboard.getEmprestimoHomeEquity());
        values.add(dashboard.getEmprestimoOnline());
        values.add(dashboard.getEmprestimoOnlineYT());
        values.add(dashboard.getEmprestimoParaNegativados());
        values.add( dashboard.getRefinanciamentoDeImovel());
        values.add(dashboard.getSimuladorOnline());
        dataSet.setData(values);
        
        List<String> bgColors = new ArrayList<String>();
        bgColors.add("rgb(81, 132, 225)");
        bgColors.add("rgb(81, 225, 210)");
        bgColors.add("rgb(153, 210, 152)");
        bgColors.add("rgb(225, 223, 81)");
        bgColors.add("rgb(225, 189, 81)");
        bgColors.add("rgb(225, 112, 81)");
        bgColors.add("rgb(225, 81, 169)");
        dataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<String>();
        labels.add("Empréstimo com terreno em garantia");
        labels.add("Empréstimo Home Equity");
        labels.add("Empréstimo online");
        labels.add("Empréstimo online YT");
        labels.add("Empréstimo para negativados");
        labels.add("Refinanciamento de Imóvel");
        labels.add("Simulador online");
        data.setLabels(labels);

        //pieModel1.set("Não definidos", dashboard.getOutrasOrigens());
        
        pieOrigemLead.setData(data);
    }
	/*
	private void createPieModel() {
		Dashboard dashboard = new Dashboard();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao(); 
		try {
			dashboard = cDao.getOrigemLeads();
				
	        pieModel1 = new PieChartModel();
	 
	        pieModel1.set("Empréstimo com terreno em garantia", dashboard.getEmprestimoComTerrenoEmGarantia());
	        pieModel1.set("Empréstimo Home Equity", dashboard.getEmprestimoHomeEquity());
	        pieModel1.set("Empréstimo online", dashboard.getEmprestimoOnline());
	        pieModel1.set("Empréstimo online YT", dashboard.getEmprestimoOnlineYT());
	        pieModel1.set("Empréstimo para negativados", dashboard.getEmprestimoParaNegativados());
	        //pieModel1.set("Não definidos", dashboard.getOutrasOrigens());
	        pieModel1.set("Refinanciamento de Imóvel", dashboard.getRefinanciamentoDeImovel());
	        pieModel1.set("Simulador online", dashboard.getSimuladorOnline());
	 
	        pieModel1.setTitle("Leads por Tipo");
	        pieModel1.setLegendPosition("w");
	        pieModel1.setShadow(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public PieChartModel getPieModel1() {
		return pieModel1;
	}

	public void setPieModel1(PieChartModel pieModel1) {
		this.pieModel1 = pieModel1;
	}
*/
    
    public StreamedContent gerarCSVleads() throws IOException {
    	XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
		
		XSSFSheet sheet = wb.getSheetAt(0);
		DashboardDao dDao = new DashboardDao();
		List<ContratoCobranca> contratos = dDao.getContratosLead();
		
		XSSFRow linha = sheet.getRow(0);
		if(linha == null) {
			sheet.createRow(0);
			linha = sheet.getRow(0);
		}
		
		gravaCelula(0, "Numero Contrato", linha);
		gravaCelula(1, "Origem", linha);
		gravaCelula(2, "Valor", linha);
		gravaCelula(3, "Valor Aprovado", linha);
		gravaCelula(4, "Qualidade Lead", linha);	
		
		//gravaCelula(5, "Cidade", linha);
		//gravaCelula(6, "Estado", linha);
		//gravaCelula(7, "Data Contrato", linha);
		
		int iLinha = 1;
		for (int iContrato = 0 ; iContrato < contratos.size(); iContrato++) {
			ContratoCobranca contrato = contratos.get(iContrato);
						
			linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			Locale locale = new Locale("pt", "BR");  
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
			Calendar cal = Calendar.getInstance();
			cal.setTime(contrato.getDataContrato());
			cal.add(Calendar.HOUR, -3);
			Date oneHourBack = cal.getTime();
			String dataStr = sdf.format(oneHourBack.getTime());
			
			String qualidadeLead = "";
			
			if (CommonsUtil.mesmoValor(contrato.getStatus(), "Aprovado")) {
				qualidadeLead = "Lead Convertido";
			} else if (CommonsUtil.mesmoValor(contrato.getStatus(), "Reprovado")) {
				qualidadeLead = "Lead Reprovado";
			} else if (CommonsUtil.mesmoValor(contrato.getStatus(), "Desistência Cliente")) {
				qualidadeLead = "Lead Reprovado";
			} else if (CommonsUtil.mesmoValor(contrato.getStatus(), "Baixado")) {
				qualidadeLead = "Lead Reprovado";
			} else {
				if (!CommonsUtil.semValor(contrato.getStatusLead())){
					if (contrato.getStatusLead().equals("Novo Lead")) {
						qualidadeLead = "Lead Pendente";
					} else if (contrato.getStatusLead().equals("Em Tratamento")) {
						qualidadeLead = "Lead Pendente";
					} else if (contrato.getStatusLead().equals("Reprovado")) {
						qualidadeLead = "Lead Reprovado";
					} else if (contrato.getStatusLead().equals("Completo")) {
						qualidadeLead = "Lead Pendente";
					}
					
					if (contrato.getCadastroAprovadoValor() != null) {
						if (contrato.getCadastroAprovadoValor().equals("Reprovado")){
							qualidadeLead = "Lead Reprovado";
						}
						
						if(contrato.isPagtoLaudoConfirmada()) {
							qualidadeLead = "Pediu laudo e paju";
						}
						
						if(contrato.isAprovadoComite()) {
							qualidadeLead = "Aprovado pelo comitê";
						}
						
						if (!contrato.isAgAssinatura()) {
							qualidadeLead = "Lead Convertido";
						}
					}
				}
			}
	
			gravaCelula(0, contrato.getNumeroContrato(), linha);
			gravaCelula(1, contrato.getUrlLead(), linha);
			gravaCelula(2, CommonsUtil.formataValorMonetario(contrato.getQuantoPrecisa(),"R$ "), linha);
			if(!CommonsUtil.semValor(contrato.getValorCCB())) {
				gravaCelula(3, CommonsUtil.formataValorMonetario(contrato.getValorCCB(),"R$ "), linha);
			} else {
				gravaCelula(3, CommonsUtil.formataValorMonetario(contrato.getValorAprovadoComite(),"R$ "), linha);
			}
			gravaCelula(4, qualidadeLead, linha);

			//gravaCelula(5, contrato.getImovel().getCidade(), linha);
			//gravaCelula(6, contrato.getImovel().getEstado(), linha);
			//gravaCelula(7, dataStr, linha);
			
			iLinha++;
		}
		
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		
		gerador.open(String.format("Galleria Bank - DashBoardTabela %s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;
    }
    
    
	public PieChartModel getPieOrigemLead() {
		return pieOrigemLead;
	}

	public boolean isConsultaStatus() {
		return consultaStatus;
	}

	public void setConsultaStatus(boolean consultaStatus) {
		this.consultaStatus = consultaStatus;
	}

	public void setPieOrigemLead(PieChartModel pieOrigemLead) {
		this.pieOrigemLead = pieOrigemLead;
	}

	public PieChartModel getPieStatusLead() {
		return pieStatusLead;
	}

	public void setPieStatusLead(PieChartModel pieStatusLead) {
		this.pieStatusLead = pieStatusLead;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public List<Dashboard> getDashContratos() {
		return dashContratos;
	}

	public void setDashContratos(List<Dashboard> dashContratos) {
		this.dashContratos = dashContratos;
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

	public int getTotalContratosCadastrados() {
		return totalContratosCadastrados;
	}

	public void setTotalContratosCadastrados(int totalContratosCadastrados) {
		this.totalContratosCadastrados = totalContratosCadastrados;
	}

	public int getTotalContratosPreAprovados() {
		return totalContratosPreAprovados;
	}

	public void setTotalContratosPreAprovados(int totalContratosPreAprovados) {
		this.totalContratosPreAprovados = totalContratosPreAprovados;
	}

	public int getTotalContratosBoletosPagos() {
		return totalContratosBoletosPagos;
	}

	public void setTotalContratosBoletosPagos(int totalContratosBoletosPagos) {
		this.totalContratosBoletosPagos = totalContratosBoletosPagos;
	}

	public int getTotalContratosCcbsEmitidas() {
		return totalContratosCcbsEmitidas;
	}

	public void setTotalContratosCcbsEmitidas(int totalContratosCcbsEmitidas) {
		this.totalContratosCcbsEmitidas = totalContratosCcbsEmitidas;
	}

	public int getTotalContratosRegistrados() {
		return totalContratosRegistrados;
	}

	public void setTotalContratosRegistrados(int totalContratosRegistrados) {
		this.totalContratosRegistrados = totalContratosRegistrados;
	}

	public BigDecimal getTotalValorContratosCadastrados() {
		return totalValorContratosCadastrados;
	}

	public void setTotalValorContratosCadastrados(BigDecimal totalValorContratosCadastrados) {
		this.totalValorContratosCadastrados = totalValorContratosCadastrados;
	}

	public BigDecimal getTotalValorContratosPreAprovados() {
		return totalValorContratosPreAprovados;
	}

	public void setTotalValorContratosPreAprovados(BigDecimal totalValorContratosPreAprovados) {
		this.totalValorContratosPreAprovados = totalValorContratosPreAprovados;
	}

	public BigDecimal getTotalValorBoletosPagos() {
		return totalValorBoletosPagos;
	}

	public void setTotalValorBoletosPagos(BigDecimal totalValorBoletosPagos) {
		this.totalValorBoletosPagos = totalValorBoletosPagos;
	}

	public BigDecimal getTotalValorCcbsEmitidas() {
		return totalValorCcbsEmitidas;
	}

	public void setTotalValorCcbsEmitidas(BigDecimal totalValorCcbsEmitidas) {
		this.totalValorCcbsEmitidas = totalValorCcbsEmitidas;
	}

	public BigDecimal getTotalValorContratosRegistrados() {
		return totalValorContratosRegistrados;
	}

	public void setTotalValorContratosRegistrados(BigDecimal totalValorContratosRegistrados) {
		this.totalValorContratosRegistrados = totalValorContratosRegistrados;
	}

	public String getUpdateResponsavel() {
		return updateResponsavel;
	}

	public void setUpdateResponsavel(String updateResponsavel) {
		this.updateResponsavel = updateResponsavel;
	}
}