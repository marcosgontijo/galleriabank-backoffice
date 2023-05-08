package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaData {

	@SerializedName( "Year")
	String year;
	@SerializedName( "Month")
	String month;
	@SerializedName( "Day")
	String day;
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
}
