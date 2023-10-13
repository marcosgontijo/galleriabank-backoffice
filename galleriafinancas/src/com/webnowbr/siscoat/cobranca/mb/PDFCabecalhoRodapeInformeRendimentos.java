package com.webnowbr.siscoat.cobranca.mb;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFCabecalhoRodapeInformeRendimentos extends PdfPageEventHelper {
/*
    public void onStartPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Right"), 550, 800, 0);
    }
*/
    public void onEndPage(PdfWriter writer, Document document) {
    	Font normal8 = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
    	Locale locale = new Locale("pt", "BR"); 
		SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
    	
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("galleriafinancas.com.br | (19) 3255-4575", normal8), 50, 10, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase("Av. Dr. José Bonifácio Coutinho Nogueira, 150 - Térreo – Campinas/SP – CEP 13091-611", normal8), 550, 10, 0);
    }
   
}