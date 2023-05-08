package com.webnowbr.siscoat.engine;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaCompleta {

	// CADASTRO POSITIVO
	@SerializedName("CPStatus")
	String cpStatus;
	@SerializedName("Segment")
	String segment;
	@SerializedName("HasOnlyMinimumPII")
	boolean hasOnlyMinimumPII;
	@SerializedName("HasNegativeData")
	boolean hasNegativeData;
	@SerializedName("HasInquiryData")
	boolean hasInquiryData;

	@SerializedName("BestInfo")
	EngineRetornoRequestBestInfo bestInfo;

	@SerializedName("QuodScore")
	EngineRetornoExecutionResultConsultaQuodScore quodScore;

	@SerializedName("Negative")
	EngineRetornoExecutionResultConsultaNegative negative;

	@SerializedName("Inquiries")
	EngineRetornoExecutionResultInquiry inquiries;

	@SerializedName("Indicators")
	List<EngineRetornoExecutionResultIndicator> indicators;

	public String getCpStatus() {
		return cpStatus;
	}

	public void setCpStatus(String cpStatus) {
		this.cpStatus = cpStatus;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public boolean isHasOnlyMinimumPII() {
		return hasOnlyMinimumPII;
	}

	public void setHasOnlyMinimumPII(boolean hasOnlyMinimumPII) {
		this.hasOnlyMinimumPII = hasOnlyMinimumPII;
	}

	public boolean isHasNegativeData() {
		return hasNegativeData;
	}

	public void setHasNegativeData(boolean hasNegativeData) {
		this.hasNegativeData = hasNegativeData;
	}

	public boolean isHasInquiryData() {
		return hasInquiryData;
	}

	public void setHasInquiryData(boolean hasInquiryData) {
		this.hasInquiryData = hasInquiryData;
	}

	public EngineRetornoRequestBestInfo getBestInfo() {
		return bestInfo;
	}

	public void setBestInfo(EngineRetornoRequestBestInfo bestInfo) {
		this.bestInfo = bestInfo;
	}

	public EngineRetornoExecutionResultConsultaQuodScore getQuodScore() {
		return quodScore;
	}

	public void setQuodScore(EngineRetornoExecutionResultConsultaQuodScore quodScore) {
		this.quodScore = quodScore;
	}
//
//	public EngineRetornoExecutionResultConsultaNegative getNegative() {
//		return negative;
//	}
//
//	public void setNegative(EngineRetornoExecutionResultConsultaNegative negative) {
//		this.negative = negative;
//	}
//
//	public EngineRetornoExecutionResultInquiry getInquiries() {
//		return inquiries;
//	}
//
//	public void setInquiries(EngineRetornoExecutionResultInquiry inquiries) {
//		this.inquiries = inquiries;
//	}
//
//	public EngineRetornoExecutionResultIndicator getIndicators() {
//		return indicators;
//	}
//
//	public void setIndicators(EngineRetornoExecutionResultIndicator indicators) {
//		this.indicators = indicators;
//	}

}