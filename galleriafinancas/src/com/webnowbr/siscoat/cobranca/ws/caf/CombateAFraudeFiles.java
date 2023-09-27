package com.webnowbr.siscoat.cobranca.ws.caf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class CombateAFraudeFiles {
	
	private long id;
	public String type; //"RG_FRONT, RG_BACK, CNH_FRONT, CNH_BACK, CRLV ou SELFIE"
	public String data; // url ou base64
	
	private String mimeType;
	private String fileExtension;
	
	public InputStream getInputStream() {
		String base64 = this.data;
		String delims="[,]";
		String[] parts = base64.split(delims);
		String imageString = parts[1];
		byte[] imageByteArray = Base64.getMimeDecoder().decode(imageString);
		
		//Find out image type
		mimeType = null;
		fileExtension = null;
		try {
			//mimeType = parts[0];
			String[] parts2 = parts[0].split("[:]");
			String[] parts3 = parts2[1].split("[;]");
			mimeType = parts3[0];
			String[] tokens = parts3[0].split("[/]");
			fileExtension = tokens[1];
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		/*byte[] decoded = Base64.getDecoder().decode(file.data);*/
		InputStream in = new ByteArrayInputStream(imageByteArray);
		return in;
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	
}
