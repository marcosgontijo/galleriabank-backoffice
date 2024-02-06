package com.webnowbr.siscoat.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CsvToExcel {
	 
    public static final char CSV_FILE_DELIMITER = ',';
 
    @SuppressWarnings("unused")
	public static byte[] convertCsvToExcel(byte[] strSource)
            throws IllegalArgumentException, IOException {
 
        Workbook workBook = null;
        ByteArrayOutputStream  fos = new ByteArrayOutputStream();
        
       
		
        // Check that the source file exists.
        InputStream sourceFile = new ByteArrayInputStream(strSource);
        
//        if (!sourceFile.exists()) {
//            throw new IllegalArgumentException("The source CSV file cannot be found at " + sourceFile);
//        }
 
        // Check that the destination folder exists to save the Excel file.
        
 
        // Getting BufferedReader object
        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile));
 
        // Getting XSSFWorkbook or HSSFWorkbook object based on excel file format
//        if (extension.equals(".xlsx")) {
            workBook = new XSSFWorkbook();
//        } else {
//            workBook = new HSSFWorkbook();
//        }
 
        Sheet sheet = workBook.createSheet("Sheet");
 
        String nextLine;
        int rowNum = 0;
        
                
        while ((nextLine = br.readLine()) != null) {
            Row currentRow = sheet.createRow(rowNum++);
            String rowData[] = nextLine.split(String.valueOf(CSV_FILE_DELIMITER));
            int iCellAdicionada = 0;
            for (int i = 0; i < rowData.length; i++) {
                if (NumberUtils.isDigits(rowData[i])) {
                    currentRow.createCell(iCellAdicionada).setCellValue(Integer.parseInt(rowData[i]));
                } else if (NumberUtils.isNumber(rowData[i])) {
                    currentRow.createCell(iCellAdicionada).setCellValue(Double.parseDouble(rowData[i]));
                } else {
                	if (CommonsUtil.stringValue( rowData[i] ).startsWith("\"")) {
                		String texto = rowData[i].substring(1);
                		
                		while(!CommonsUtil.stringValue( rowData[i] ).endsWith("\"")) {
                			i++;
                			
							if (CommonsUtil.stringValue(rowData[i]).endsWith("\"")) {
								int tamanhoTexto = CommonsUtil.stringValue(rowData[i]).length();
								texto = texto + CommonsUtil.stringValue(rowData[i]).substring(0, tamanhoTexto - 1);
							}else
                			texto =  texto + CommonsUtil.stringValue( rowData[i] );
                		}
                		currentRow.createCell(iCellAdicionada).setCellValue(texto);
                		
                	}else {
                		currentRow.createCell(iCellAdicionada).setCellValue(rowData[i]);
                	}
                }
                iCellAdicionada++;
            }
        }

        workBook.write(fos);
 
        try {
            // Closing workbook, fos, and br object
//            workBook.close();
        	if ( fos != null)
        		fos.close();
        	
            br.close();
 
        } catch (IOException e) {
            System.out.println("Exception While Closing I/O Objects");
            e.printStackTrace();
        }
        if ( fos != null)
        	return fos.toByteArray();
        else 
        	return null;
 
    }
 
   


}
