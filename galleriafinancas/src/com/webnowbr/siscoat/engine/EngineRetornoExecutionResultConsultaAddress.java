package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaAddress {

	@SerializedName( "Street")
	String street;
	@SerializedName( "Number")
	String number;
	@SerializedName( "Complement")
	String complement;
	@SerializedName( "Neighborhood")
	String neighborhood;
	@SerializedName( "City")
	String city;
	@SerializedName( "State")
	String state;
	@SerializedName( "PostalCode")
	String postalCode;
	EngineRetornoExecutionResultConsultaData dateLastSeen;
	
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getComplement() {
		return complement;
	}
	public void setComplement(String complement) {
		this.complement = complement;
	}
	public String getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public EngineRetornoExecutionResultConsultaData getDateLastSeen() {
		return dateLastSeen;
	}
	public void setDateLastSeen(EngineRetornoExecutionResultConsultaData dateLastSeen) {
		this.dateLastSeen = dateLastSeen;
	}
	
	

}
