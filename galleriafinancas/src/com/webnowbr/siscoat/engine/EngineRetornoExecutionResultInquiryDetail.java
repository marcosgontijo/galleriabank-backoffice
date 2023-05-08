package com.webnowbr.siscoat.engine;

public class EngineRetornoExecutionResultInquiryDetail {

	EngineRetornoExecutionResultConsultaData dateInquiry;
	int inquiryCount;
	String segment;

	public EngineRetornoExecutionResultConsultaData getDateInquiry() {
		return dateInquiry;
	}

	public void setDateInquiry(EngineRetornoExecutionResultConsultaData dateInquiry) {
		this.dateInquiry = dateInquiry;
	}

	public int getInquiryCount() {
		return inquiryCount;
	}

	public void setInquiryCount(int inquiryCount) {
		this.inquiryCount = inquiryCount;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

}
