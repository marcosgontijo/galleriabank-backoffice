package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.Holder;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.util.CalendarUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.FaturaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.OperacaoContratoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.SaldoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SaqueIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SubContaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.model.UniProof;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;
import com.webnowbr.siscoat.cobranca.db.op.UniProofDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.BcMsgRetorno;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDaOperacao;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoCliente;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoClienteTraduzido;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoVencimento;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoModalidade;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "uniproofMB")
@SessionScoped
public class UniproofMB {

	/****
	 * 
		curl -X 'POST' \
		  'https://api-stage.uniproof.com.br/api/auth' \
		  -H 'Content-Type: application/json' \
		  -d '{
		  "email": "string",
		  "password": "string"
		}'
	 */

	/***
	 * INICIO ATRIBUTOS 
	 */

	static final String apiurl =  "https://api-stage.uniproof.com.br";
	static final String apiLogin =  "webnowbr@gmail.com";
	static final String apiPassword =  "Hvj28383*";
	
	private String authToken =  "";
	private String companyToken =  "";
	
	private String lotId =  "";
	private String containerId =  "";
	private String lotItemId =  "";
	
	private List<UniProof> processos = new ArrayList<UniProof>();
	
	/***
	 * FIM ATRIBUTOS RECIBO
	 */

	public String clearFieldsProcessos() {
		UniProofDao uniProofDao = new UniProofDao();
		
		this.processos = new ArrayList<UniProof>();
		
		this.processos = uniProofDao.findAll();
		
		return "/Atendimento/Cobranca/UniProofProcessosConsultar.xhtml";
	}
	
	
	/***
	 * GERA JSON PARA AUTH
	 * @param pessoa
	 * @return
	 */
	public JSONObject getJSONAuth() {
		JSONObject auth = new JSONObject();
		auth.put("email", apiLogin);
		auth.put("password", apiPassword);
		
		return auth;
	}

	public void getTokenAuth() {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL(apiurl + "/api/auth");

			JSONObject jsonObj = getJSONAuth();
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);
	
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
			
			int status = myURLConnection.getResponseCode();
			
			if (status == 201) {
				if (myResponse.has("token")) {					
					if (!myResponse.isNull("token")) {
						authToken = myResponse.getString("token");
					}
				}
				
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[UniProof] Autenticação realizada com sucesso!", ""));
			} else {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[UniProof] Problema ao fazer autenticação!", ""));
			}
						
			myURLConnection.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadDatabaseFinancas() {
	    try
        {
            //FileInputStream file = new FileInputStream(new File("C://Users//herme//Desktop//ProcessosFinancas.xlsx"));
	    	FileInputStream file = new FileInputStream(new File("//home//webnowbr//Siscoat//GalleriaFinancas//BDUniProof//ProcessosFinancas.xlsx"));
            ZipSecureFile.setMinInflateRatio(-1.0d);
 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            UniProof processo = new UniProof();
			UniProofDao uniProofDao = new UniProofDao();
			int countLine = 0;
			
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();                
                
                if (countLine == 0) {
                	row = rowIterator.next();
                	countLine = countLine + 1;
                }  
                
                	processo = new UniProof();

                	Cell cell = row.getCell(0);
                	//SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    //Date dataFormatada = formato.parse(cell.getStringCellValue()); 
                    processo.setUpdatedAt(cell.getDateCellValue());
                                  
                    cell = row.getCell(1);
                    processo.setStatusLabel(cell.getStringCellValue());
                                        
                    cell = row.getCell(2);
                    processo.setLotName(cell.getStringCellValue());
                    
            		cell = row.getCell(3);
                    processo.setLotDescription(cell.getStringCellValue());
                    
                    cell = row.getCell(6);
                    processo.setServiceName(cell.getStringCellValue());
                    
                    cell = row.getCell(8);
                    BigDecimal notPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setNotaryPrice(notPrice);
                    
                    cell = row.getCell(9);
                    BigDecimal uniPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setUniproofPrice(uniPrice);
                    
                    cell = row.getCell(10);
                    BigDecimal finalPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setFinalPrice(finalPrice);
                    
					processo.setCompanyToken("93771b11-cab9-4ff7-b5dc-4439efb615fc");
					processo.setCompanyName("GALLERIA FINANÇAS SECURITIZADORA S.A.");
           
                    uniProofDao.create(processo);
            }
            
            file.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public void loadDatabaseCorrespondente() {
	    try
        {
            //FileInputStream file = new FileInputStream(new File("C://Users//herme//Desktop//ProcessosCorrespondente.xlsx"));
            FileInputStream file = new FileInputStream(new File("//home//webnowbr//Siscoat//GalleriaFinancas//BDUniProof//ProcessosCorrespondente.xlsx"));
            ZipSecureFile.setMinInflateRatio(-1.0d);
 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            UniProof processo = new UniProof();
			UniProofDao uniProofDao = new UniProofDao();
			int countLine = 0;
			
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();                
                
                if (countLine == 0) {
                	row = rowIterator.next();
                	countLine = countLine + 1;
                }  
                
                	processo = new UniProof();

                	Cell cell = row.getCell(0);
                	//SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    //Date dataFormatada = formato.parse(cell.getStringCellValue()); 
                    processo.setUpdatedAt(cell.getDateCellValue());
                                  
                    cell = row.getCell(1);
                    processo.setStatusLabel(cell.getStringCellValue());
                                        
                    cell = row.getCell(2);
                    processo.setLotName(cell.getStringCellValue());
                    
            		cell = row.getCell(3);
                    processo.setLotDescription(cell.getStringCellValue());
                    
                    cell = row.getCell(6);
                    processo.setServiceName(cell.getStringCellValue());
                    
                    cell = row.getCell(8);
                    BigDecimal notPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setNotaryPrice(notPrice);
                    
                    cell = row.getCell(9);
                    BigDecimal uniPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setUniproofPrice(uniPrice);
                    
                    cell = row.getCell(10);
                    BigDecimal finalPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setFinalPrice(finalPrice);
                    
					processo.setCompanyToken("c363640f-223d-4acc-8837-9d0557260820");
					processo.setCompanyName("Galleria Correspondente Bancário Eireli");								
           
                    uniProofDao.create(processo);
            }
            
            file.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public void loadDatabaseFundo() {
	    try
        {
            //FileInputStream file = new FileInputStream(new File("C://Users//herme//Desktop//ProcessosCorrespondente.xlsx"));
            FileInputStream file = new FileInputStream(new File("//home//webnowbr//Siscoat//GalleriaFinancas//BDUniProof//ProcessosFundo.xlsx"));
            ZipSecureFile.setMinInflateRatio(-1.0d);
 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            UniProof processo = new UniProof();
			UniProofDao uniProofDao = new UniProofDao();
			int countLine = 0;
			
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();                
                
                if (countLine == 0) {
                	row = rowIterator.next();
                	countLine = countLine + 1;
                }  
                
                	processo = new UniProof();

                	Cell cell = row.getCell(0);
                	//SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    //Date dataFormatada = formato.parse(cell.getStringCellValue()); 
                    processo.setUpdatedAt(cell.getDateCellValue());
                                  
                    cell = row.getCell(1);
                    processo.setStatusLabel(cell.getStringCellValue());
                                        
                    cell = row.getCell(2);
                    processo.setLotName(cell.getStringCellValue());
                    
            		cell = row.getCell(3);
                    processo.setLotDescription(cell.getStringCellValue());
                    
                    cell = row.getCell(6);
                    processo.setServiceName(cell.getStringCellValue());
                    
                    cell = row.getCell(8);
                    BigDecimal notPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setNotaryPrice(notPrice);
                    
                    cell = row.getCell(9);
                    BigDecimal uniPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setUniproofPrice(uniPrice);
                    
                    cell = row.getCell(10);
                    BigDecimal finalPrice = BigDecimal.valueOf(cell.getNumericCellValue());
                    processo.setFinalPrice(finalPrice);
                    
					processo.setCompanyToken("14f0e08b-297e-48d1-8363-f67aed919300");
					processo.setCompanyName("Fundo de Investimento em Direitos Creditórios Galleria");								
           
                    uniProofDao.create(processo);
            }
            
            file.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJSONSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<UniProof> getProcessos() {
		return processos;
	}

	public void setProcessos(List<UniProof> processos) {
		this.processos = processos;
	}
}