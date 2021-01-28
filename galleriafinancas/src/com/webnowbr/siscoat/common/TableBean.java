package com.webnowbr.siscoat.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="tableBean")
@SessionScoped
public class TableBean implements Serializable {

	private List<modelo> carsSmall;
	private List<medidas> medidas;

	public TableBean() {
		carsSmall = new ArrayList<modelo>();
		medidas = new ArrayList<medidas>();

		populateRandomCars(carsSmall);
		
		populateMedidas(medidas);
	}

	private void populateRandomCars(List<modelo> list) {
		carsSmall.add(new modelo("Alerta01", "Critical", "red"));
		carsSmall.add(new modelo("Alerta02", "Info", "yellow"));
		carsSmall.add(new modelo("Alerta03", "Alert", "normal"));
	}
	
	private void populateMedidas(List<medidas> list) {
		medidas.add(new medidas("Campo1", "Campo2", "Campo3","Campo4","Campo5"));
		medidas.add(new medidas("Campo1", "Campo2", "Campo3","Campo4","Campo5"));
		medidas.add(new medidas("Campo1", "Campo2", "Campo3","Campo4","Campo5"));
		medidas.add(new medidas("Campo1", "Campo2", "Campo3","Campo4","Campo5"));
		medidas.add(new medidas("Campo1", "Campo2", "Campo3","Campo4","Campo5"));
		medidas.add(new medidas("Campo1", "Campo2", "Campo3","Campo4","Campo5"));
	}	

	/**
	 * @return the medidas
	 */
	public List<medidas> getMedidas() {
		return medidas;
	}

	/**
	 * @param medidas the medidas to set
	 */
	public void setMedidas(List<medidas> medidas) {
		this.medidas = medidas;
	}

	public List<modelo> getCarsSmall() {
		return carsSmall;
	}

	public class modelo{
		private String alert;
		private String severidade;
		private String rowStyle;
		public modelo(String alert, String severidade, String rowStyle){
			this.alert = alert;
			this.severidade = severidade;
			this.rowStyle = rowStyle;
		}
		/**
		 * @return the alert
		 */
		public String getAlert() {
			return alert;
		}
		/**
		 * @param alert the alert to set
		 */
		public void setAlert(String alert) {
			this.alert = alert;
		}
		/**
		 * @return the severidade
		 */
		public String getSeveridade() {
			return severidade;
		}
		/**
		 * @param severidade the severidade to set
		 */
		public void setSeveridade(String severidade) {
			this.severidade = severidade;
		}
		/**
		 * @return the rowStyle
		 */
		public String getRowStyle() {
			return rowStyle;
		}
		/**
		 * @param rowStyle the rowStyle to set
		 */
		public void setRowStyle(String rowStyle) {
			this.rowStyle = rowStyle;
		}
}
	
	public class medidas{
		private String campo1;
		private String campo2;
		private String campo3;
		private String campo4;
		private String campo5;
		public medidas(String campo1, String campo2, String campo3, String campo4, String campo5){
			this.campo1 = campo1;
			this.campo2 = campo2;
			this.campo3 = campo3;
			this.campo4 = campo4;
			this.campo5 = campo5;
		}
		/**
		 * @return the campo1
		 */
		public String getCampo1() {
			return campo1;
		}
		/**
		 * @param campo1 the campo1 to set
		 */
		public void setCampo1(String campo1) {
			this.campo1 = campo1;
		}
		/**
		 * @return the campo2
		 */
		public String getCampo2() {
			return campo2;
		}
		/**
		 * @param campo2 the campo2 to set
		 */
		public void setCampo2(String campo2) {
			this.campo2 = campo2;
		}
		/**
		 * @return the campo3
		 */
		public String getCampo3() {
			return campo3;
		}
		/**
		 * @param campo3 the campo3 to set
		 */
		public void setCampo3(String campo3) {
			this.campo3 = campo3;
		}
		/**
		 * @return the campo4
		 */
		public String getCampo4() {
			return campo4;
		}
		/**
		 * @param campo4 the campo4 to set
		 */
		public void setCampo4(String campo4) {
			this.campo4 = campo4;
		}
		/**
		 * @return the campo5
		 */
		public String getCampo5() {
			return campo5;
		}
		/**
		 * @param campo5 the campo5 to set
		 */
		public void setCampo5(String campo5) {
			this.campo5 = campo5;
		}
}
}
				