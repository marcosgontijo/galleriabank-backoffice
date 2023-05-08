package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultInquiry {

	@SerializedName("InquiryCountLast30Days")
	int inquiryCountLast30Days;
	@SerializedName("InquiryCountLast31to60Days")
	int inquiryCountLast31to60Days;
	@SerializedName("InquiryCountLast61to90Days")
	int inquiryCountLast61to90Days;
	@SerializedName("InquiryCountMore90Days")
	int inquiryCountMore90Days;
	
	@SerializedName("InquiryDetails")
	EngineRetornoExecutionResultInquiryDetailHistory inquiryDetails;

	public int getInquiryCountLast30Days() {
		return inquiryCountLast30Days;
	}

	public void setInquiryCountLast30Days(int inquiryCountLast30Days) {
		this.inquiryCountLast30Days = inquiryCountLast30Days;
	}

	public int getInquiryCountLast31to60Days() {
		return inquiryCountLast31to60Days;
	}

	public void setInquiryCountLast31to60Days(int inquiryCountLast31to60Days) {
		this.inquiryCountLast31to60Days = inquiryCountLast31to60Days;
	}

	public int getInquiryCountLast61to90Days() {
		return inquiryCountLast61to90Days;
	}

	public void setInquiryCountLast61to90Days(int inquiryCountLast61to90Days) {
		this.inquiryCountLast61to90Days = inquiryCountLast61to90Days;
	}

	public int getInquiryCountMore90Days() {
		return inquiryCountMore90Days;
	}

	public void setInquiryCountMore90Days(int inquiryCountMore90Days) {
		this.inquiryCountMore90Days = inquiryCountMore90Days;
	}

	public EngineRetornoExecutionResultInquiryDetailHistory getInquiryDetails() {
		return inquiryDetails;
	}

	public void setInquiryDetails(EngineRetornoExecutionResultInquiryDetailHistory inquiryDetails) {
		this.inquiryDetails = inquiryDetails;
	}

}
