package com.webnowbr.siscoat.cobranca.ws.caf;
import java.util.List;


public class CombateAFraudeTransaction{

	public List<CombateAFraudeFiles> files;	
	public String templateId;	
	public String _callbackUrl;
	public CombateAFraudeAttributes attributes;	
	
	private String requestId;
	private String id;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
}
