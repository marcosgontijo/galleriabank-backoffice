package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoRequestBestInfo {

	@SerializedName("CPF")
	String cpf;
	@SerializedName("CPFStatus")
	String cpfStatus;
	
	@SerializedName( "PersonName")
	EngineRetornoExecutionResultConsultaPersonalName personName;
	@SerializedName( "MotherName")
	EngineRetornoExecutionResultConsultaPersonalNameFull motherName;
	
	@SerializedName("DOB")
	EngineRetornoExecutionResultConsultaData dob;

	@SerializedName("Age")
	String age;
	@SerializedName("Gender")
	String gender;
	@SerializedName("Address")
	EngineRetornoExecutionResultConsultaAddress address;

	@SerializedName("Email")
	EngineRetornoExecutionResultConsultaEmail email;

	@SerializedName("PhoneNumber")
	EngineRetornoExecutionResultConsultaPhone phoneNumber;
	@SerializedName("MobilePhoneNumber")
	EngineRetornoExecutionResultConsultaPhone mobilePhoneNumber;

	@SerializedName( "PersonNameHistory")
	EngineRetornoExecutionResultConsultaPersonalNameArray  personNameHistory;

	@SerializedName("AddressHistory")
	EngineRetornoExecutionResultConsultaAddressHistory addressHistory;

	@SerializedName("EmailHistory")
	EngineRetornoExecutionResultConsultaEmailHistory emailHistory;

	@SerializedName("PhoneNumberHistory")
	EngineRetornoExecutionResultConsultaPhoneHistory phoneNumberHistory;

	@SerializedName("MobilePhoneNumberHistory")
	EngineRetornoExecutionResultConsultaMobileHistory mobilePhoneNumberHistory;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCpfStatus() {
		return cpfStatus;
	}

	public void setCpfStatus(String cpfStatus) {
		this.cpfStatus = cpfStatus;
	}

	public EngineRetornoExecutionResultConsultaData getDob() {
		return dob;
	}

	public void setDob(EngineRetornoExecutionResultConsultaData dob) {
		this.dob = dob;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public EngineRetornoExecutionResultConsultaAddress getAddress() {
		return address;
	}

	public void setAddress(EngineRetornoExecutionResultConsultaAddress address) {
		this.address = address;
	}

	public EngineRetornoExecutionResultConsultaEmail getEmail() {
		return email;
	}

	public void setEmail(EngineRetornoExecutionResultConsultaEmail email) {
		this.email = email;
	}

	public EngineRetornoExecutionResultConsultaPhone getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(EngineRetornoExecutionResultConsultaPhone phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public EngineRetornoExecutionResultConsultaPhone getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(EngineRetornoExecutionResultConsultaPhone mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public EngineRetornoExecutionResultConsultaAddressHistory getAddressHistory() {
		return addressHistory;
	}

	public void setAddressHistory(EngineRetornoExecutionResultConsultaAddressHistory addressHistory) {
		this.addressHistory = addressHistory;
	}

	public EngineRetornoExecutionResultConsultaEmailHistory getEmailHistory() {
		return emailHistory;
	}

	public void setEmailHistory(EngineRetornoExecutionResultConsultaEmailHistory emailHistory) {
		this.emailHistory = emailHistory;
	}

	public EngineRetornoExecutionResultConsultaPersonalName getPersonName() {
		return personName;
	}

	public void setPersonName(EngineRetornoExecutionResultConsultaPersonalName personName) {
		this.personName = personName;
	}

	public EngineRetornoExecutionResultConsultaPersonalNameFull getMotherName() {
		return motherName;
	}

	public void setMotherName(EngineRetornoExecutionResultConsultaPersonalNameFull motherName) {
		this.motherName = motherName;
	}

	public EngineRetornoExecutionResultConsultaPersonalNameArray getPersonNameHistory() {
		return personNameHistory;
	}

	public void setPersonNameHistory(EngineRetornoExecutionResultConsultaPersonalNameArray personNameHistory) {
		this.personNameHistory = personNameHistory;
	}

	public EngineRetornoExecutionResultConsultaPhoneHistory getPhoneNumberHistory() {
		return phoneNumberHistory;
	}

	public void setPhoneNumberHistory(EngineRetornoExecutionResultConsultaPhoneHistory phoneNumberHistory) {
		this.phoneNumberHistory = phoneNumberHistory;
	}

	public EngineRetornoExecutionResultConsultaMobileHistory getMobilePhoneNumberHistory() {
		return mobilePhoneNumberHistory;
	}

	public void setMobilePhoneNumberHistory(EngineRetornoExecutionResultConsultaMobileHistory mobilePhoneNumberHistory) {
		this.mobilePhoneNumberHistory = mobilePhoneNumberHistory;
	}

}
