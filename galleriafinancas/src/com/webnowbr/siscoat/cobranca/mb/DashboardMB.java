package com.webnowbr.siscoat.cobranca.mb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import com.webnowbr.siscoat.cobranca.db.model.Dashboard;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DashboardDao;

@ManagedBean(name = "dashboardMB")
@SessionScoped
public class DashboardMB {

    private PieChartModel pieOrigemLead;
    private PieChartModel pieStatusLead;
    
    private Date dataInicio;
    private Date dataFim;
    
    private List<Dashboard> dashContratos;
    
	public DashboardMB() {
		
	}
	
	public String clearFieldsDashContratos() {
		
		this.dataInicio = null;
		this.dataFim = null;
		
		this.dashContratos = new ArrayList<Dashboard>();
		
		return "/Atendimento/Cobranca/DashboardManager.xhtml";
	}
	
	public void processaDashContratos() {
		
		DashboardDao dDao = new DashboardDao();
		this.dashContratos = dDao.getDashboardContratos(this.dataInicio, this.dataFim);
		
	}
	
	public String clearFields() {
		//createPieModel();
		origemLead();
		statusLead();
		
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

	public PieChartModel getPieOrigemLead() {
		return pieOrigemLead;
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
}