package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultInquiryDetailHistory {

	@SerializedName("InquiryDetail")
	List<EngineRetornoExecutionResultInquiryDetail> inquiryDetails;

	public List<EngineRetornoExecutionResultInquiryDetail> getInquiryDetails() {
		return inquiryDetails;
	}

	public void setInquiryDetails(List<EngineRetornoExecutionResultInquiryDetail> inquiryDetails) {
		this.inquiryDetails = inquiryDetails;
	}

}
