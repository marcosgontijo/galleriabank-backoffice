package com.webnowbr.siscoat.cobranca.ws.caf;

public class CombateAFraudeFiles {
	
	public String type; //"RG_FRONT, RG_BACK, CNH_FRONT, CNH_BACK, CRLV ou SELFIE"
	public String data; // url ou base64
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	
}
