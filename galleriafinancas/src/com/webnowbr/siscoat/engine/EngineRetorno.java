package com.webnowbr.siscoat.engine;

import java.util.List;
import java.util.Optional;

import com.webnowbr.siscoat.common.CommonsUtil;

import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

public class EngineRetorno {

	List<String> aditionalResponse;

	boolean allDone;
	boolean approved;
	String basketName;
	List<String> children;
	String classificationResult;

	EngineRetornoClient client;

	List<String> cycleCallManagers;
	String disaproveByRule;
	String endTime;
	String starTime;

	List<EngineRetornoExecutionResult> executionResult;
	List<String> plds;

	List<EngineRetornoRequestFields> requestFields;

	public EngineRetornoExecutionResultConsultaCompleta getConsultaCompleta() {

		Optional<EngineRetornoExecutionResult> consultaCompleta = executionResult.stream()
				.filter(e -> CommonsUtil.mesmoValor("busca_provider_QD_consulta_completa_pf", e.getField())).findAny();

		if (consultaCompleta.isPresent()) {

			EngineRetornoExecutionResultConsultaCompleta engineRetornoExecutionResultConsultaCompleta = GsonUtil
					.fromJson(consultaCompleta.get().getObservation(),
							EngineRetornoExecutionResultConsultaCompleta.class);

			return engineRetornoExecutionResultConsultaCompleta;

		}

		return null;

	}

	public List<String> getAditionalResponse() {
		return aditionalResponse;
	}

	public void setAditionalResponse(List<String> aditionalResponse) {
		this.aditionalResponse = aditionalResponse;
	}

	public boolean isAllDone() {
		return allDone;
	}

	public void setAllDone(boolean allDone) {
		this.allDone = allDone;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getBasketName() {
		return basketName;
	}

	public void setBasketName(String basketName) {
		this.basketName = basketName;
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	public String getClassificationResult() {
		return classificationResult;
	}

	public void setClassificationResult(String classificationResult) {
		this.classificationResult = classificationResult;
	}

	public EngineRetornoClient getClient() {
		return client;
	}

	public void setClient(EngineRetornoClient client) {
		this.client = client;
	}

	public List<String> getCycleCallManagers() {
		return cycleCallManagers;
	}

	public void setCycleCallManagers(List<String> cycleCallManagers) {
		this.cycleCallManagers = cycleCallManagers;
	}

	public String getDisaproveByRule() {
		return disaproveByRule;
	}

	public void setDisaproveByRule(String disaproveByRule) {
		this.disaproveByRule = disaproveByRule;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStarTime() {
		return starTime;
	}

	public void setStarTime(String starTime) {
		this.starTime = starTime;
	}

	public List<EngineRetornoExecutionResult> getExecutionResult() {
		return executionResult;
	}

	public void setExecutionResult(List<EngineRetornoExecutionResult> executionResult) {
		this.executionResult = executionResult;
	}

	public List<String> getPlds() {
		return plds;
	}

	public void setPlds(List<String> plds) {
		this.plds = plds;
	}

	public List<EngineRetornoRequestFields> getRequestFields() {
		return requestFields;
	}

	public void setRequestFields(List<EngineRetornoRequestFields> requestFields) {
		this.requestFields = requestFields;
	}

}
