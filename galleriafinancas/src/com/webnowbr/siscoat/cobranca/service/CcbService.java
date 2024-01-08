package com.webnowbr.siscoat.cobranca.service;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.JDBCException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.PorcentagemPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.Averbacao;
import com.webnowbr.siscoat.cobranca.db.model.CcbContrato;
import com.webnowbr.siscoat.cobranca.db.model.CcbParticipantes;
import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.CcbParticipantesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.SeguradoDAO;
import com.webnowbr.siscoat.cobranca.mb.ImpressoesPDFMB;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;

import net.sf.jasperreports.engine.JRException;

@SuppressWarnings("deprecation")
public class CcbService {
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
	PorcentagemPorExtenso porcentagemPorExtenso = new PorcentagemPorExtenso();
	private char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	public UploadedFile uploadedFile;
    public String fileName;
    public String fileType;
    public int fileTypeInt;
    ByteArrayInputStream bis = null;
    
	private ArrayList<UploadedFile> filesList = new ArrayList<UploadedFile>();   
    CcbContrato objetoCcb;
    SimulacaoVO simulador;
    
	public CcbService(ArrayList<UploadedFile> filesList, CcbContrato objetoCcb, SimulacaoVO simulador) {
		super();
		this.filesList = filesList;
		this.objetoCcb = objetoCcb;
		this.simulador = simulador;
	}

	public byte[] geraCci() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			XWPFRun run3;
			List<CcbParticipantes> segurados = new ArrayList<CcbParticipantes>();
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")
						|| CommonsUtil.mesmoValor(participante.getTipoParticipante(), "DEVEDOR FIDUCIANTE") ) {
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
					segurados.add(participante);
				} else if(CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")){
					segurados.add(participante);
				}  else if(CommonsUtil.mesmoValor(participante.getTipoParticipante(), "COMPRADOR")){
					segurados.add(participante);
				}
			}
			if(objetoCcb.isTerceiroGarantidor()) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/CciTg.docx"));
			} else {
				if ( CommonsUtil.semValor( objetoCcb.getProcessosJucidiais() ) )
					document = new XWPFDocument(getClass().getResourceAsStream("/resource/Cci.docx"));
				else
					document = new XWPFDocument(getClass().getResourceAsStream("/resource/CciComProcesso.docx"));
			}		
			
			String numerosProcessos = "";
			BigDecimal totalProcessos = BigDecimal.ZERO;
			if (!CommonsUtil.semValor(objetoCcb.getProcessosJucidiais())) {
				for (CcbProcessosJudiciais processo : objetoCcb.getProcessosJucidiais()) {
					if (CommonsUtil.semValor(processo.getValorAtualizado())) {
						continue;
					}
					numerosProcessos = numerosProcessos + ((!CommonsUtil.semValor(numerosProcessos)) ? ", " : "")
							+ "Nº " + CommonsUtil.stringValueVazio(processo.getNumero()) + " ";
					totalProcessos = totalProcessos.add(processo.getValorAtualizado());
				}
				numerosProcessos = numerosProcessos.trim();
			}

			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			organizaSegurados(segurados);
		
			int indexSegurados = 41;
			
			for(Segurado segurado : objetoCcb.getListSegurados()) {
				XWPFTable table = document.getTables().get(0);
				table.insertNewTableRow(indexSegurados);
				XWPFTableRow tableRow1 = table.getRow(indexSegurados);
				XWPFParagraph paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				CTTcBorders border = tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);		
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText("Nome:");
				tableRow1.createCell();////////////////////////////////////////////////////////////////////////
				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				border = tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText(segurado.getPessoa().getNome());
				indexSegurados++;////////////////////////////////////////////////////////////////////////////////
				table.insertNewTableRow(indexSegurados);
				XWPFTableRow tableRow2 = table.getRow(indexSegurados);				
				tableRow2.createCell();
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow2.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				border = tableRow2.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				run = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText("Percentual:");
				tableRow2.createCell();//////////////////////////////////////////////////////////////////////////
				tableRow2.getCell(1).setParagraph(paragraph);
				tableRow2.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow2.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				border = tableRow2.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);			
				run = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetario(segurado.getPorcentagemSegurador()) + "%");
				indexSegurados++;
			}
			
			
		
			
			XWPFTable table = document.getTables().get(0);
			XWPFTableRow tableRow1 = table.getRow(3);
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {										
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();	
				run.setFontSize(12);
				run.setText(alphabet[iParticipante] + ") ");
				run.setBold(true);
				run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
				//run2.setFontFamily("Calibri");
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();
				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();
					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else if(participante.getSocios().size() > 0){
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					} else {
						socios = "";
					}
					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);
					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = tableRow1.getCell(0).getParagraphArray(0).createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = tableRow1.getCell(0).getParagraphArray(0).createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}									
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					if(CommonsUtil.semValor(objetoCcb.getTipoPessoaEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setTipoPessoaEmitente("PF");
						} else {
							objetoCcb.setTipoPessoaEmitente("PJ");
						}
					}
					
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
					objetoCcb.setTipoParticipanteEmitente("DEVEDOR FIDUCIANTE");
				}
				run3 = tableRow1.getCell(0).getParagraphArray(0).createRun();	
				run3.setFontSize(12);
				run3.setText(" (“" + participante.getTipoParticipante() + "”)");
				run3.setBold(true);
				run3.addBreak();
				iParticipante++;
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
						
						text = trocaValoresXWPF(text, r, "numerosProcessos",numerosProcessos);
						text = trocaValoresXWPF(text, r, "totalProcessos", CommonsUtil.formataValorMonetario(totalProcessos));
						
			        }
			    }
			}	
			
			
			
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM;
			if(!CommonsUtil.semValor(objetoCcb.getPrazo()) && !CommonsUtil.semValor(objetoCcb.getNumeroParcelasPagamento())) {
				taxaAdm = taxaAdm.multiply(BigDecimal.valueOf( Long.parseLong(CommonsUtil.somenteNumeros(objetoCcb.getPrazo())) - Long.parseLong(CommonsUtil.somenteNumeros(objetoCcb.getNumeroParcelasPagamento())) + 1));
			} 
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;

			if (!CommonsUtil.semValor(objetoCcb.getValorMipParcela()))
				totalPrimeiraParcela = objetoCcb.getValorMipParcela();
			if (!CommonsUtil.semValor(objetoCcb.getValorDfiParcela()))
				totalPrimeiraParcela = totalPrimeiraParcela.add(objetoCcb.getValorDfiParcela());
			if (!CommonsUtil.semValor(objetoCcb.getValorParcela()))
				totalPrimeiraParcela = totalPrimeiraParcela.add(objetoCcb.getValorParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
						
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);										
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", objetoCcb.getValorLiquidoCredito());								
								text = trocaValoresXWPFCci(text, r, "custoEmissao", objetoCcb.getCustoEmissao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", objetoCcb.getCustoEmissao());	
								text = trocaValoresXWPFCci(text, r, "valorIOF", objetoCcb.getValorIOF(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", objetoCcb.getValorIOF());	
								text = trocaValoresXWPFCci(text, r, "valorDespesas", objetoCcb.getValorDespesas(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", objetoCcb.getValorDespesas());	
								
								text = trocaValoresXWPF(text, r, "titularConta", objetoCcb.getTitularConta());
								text = trocaValoresXWPF(text, r, "agencia", objetoCcb.getAgencia());
								text = trocaValoresXWPF(text, r, "contaCorrente", objetoCcb.getContaCorrente());					
								text = trocaValoresXWPF(text, r, "nomeBanco", objetoCcb.getNomeBanco());
								text = trocaValoresXWPF(text, r, "pixBanco", objetoCcb.getPixBanco());
								
								text = trocaValoresXWPF(text, r, "prazoContrato", objetoCcb.getPrazo());
								text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", objetoCcb.getNumeroParcelasPagamento());
								text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", objetoCcb.getVencimentoPrimeiraParcelaPagamento());
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", objetoCcb.getTaxaDeJurosMes());
								text = trocaValoresXWPF(text, r, "taxaDeJurosAno", objetoCcb.getTaxaDeJurosAno());
								text = trocaValoresXWPF(text, r, "cetMes", objetoCcb.getCetMes());
								text = trocaValoresXWPF(text, r, "cetAno", objetoCcb.getCetAno());
								
								text = trocaValoresXWPFCci(text, r, "totalPrimeiraParcela", totalPrimeiraParcela, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "TotalPrimeiraParcela", totalPrimeiraParcela);	
								text = trocaValoresXWPFCci(text, r, "valorMipParcela", objetoCcb.getValorMipParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorMipParcela", objetoCcb.getValorMipParcela());			
								text = trocaValoresXWPFCci(text, r, "valorDfiParcela", objetoCcb.getValorDfiParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDfiParcela", objetoCcb.getValorDfiParcela());
								text = trocaValoresXWPFCci(text, r, "valorParcela", objetoCcb.getValorParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorParcela", objetoCcb.getValorParcela());		
								
								text = trocaValoresXWPF(text, r, "serieCcb", objetoCcb.getSerieCcb());
								text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "numeroCCB", objetoCcb.getNumeroCcb());
								
								text = trocaValoresXWPF(text, r, "numeroRegistroMatricula", objetoCcb.getNumeroRegistroMatricula());
								
								text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
								text = trocaValoresXWPF(text, r, "numeroImovel", objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "inscricaoMunicipal", objetoCcb.getInscricaoMunicipal());
								text = trocaValoresXWPFCci(text, r, "vendaLeilao", objetoCcb.getVendaLeilao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", objetoCcb.getVendaLeilao());	
								
								text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", objetoCcb.getVencimentoUltimaParcelaPagamento());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "tipoParticipanteEmitente", objetoCcb.getTipoParticipanteEmitente());	 		
								text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
						
								
								if (text != null && text.contains("sistemaAmortizacao")) {
									if(CommonsUtil.mesmoValor(objetoCcb.getSistemaAmortizacao(), "Price")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "Tabela Price");
									} else if(CommonsUtil.mesmoValor(objetoCcb.getSistemaAmortizacao(), "SAC")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "SAC - Sistema de Amortização Constante");
									} else {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "");
									}
								}
								
								if (text != null && text.contains("participantesCci")) {
									text = text.replace("participantesCci", "");
									r.setText(text, 0);			
								}
								
								if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
									int iImagem = 0;
									int idImage = 50;
									for(iImagem = 0; iImagem < filesList.size(); iImagem++) {
										r.addBreak();
										populateFiles(iImagem);
										r.addPicture(bis, fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
										r.addBreak();	
									}
									for (int i = 0; i < r.getCTR().getDrawingList().size(); i++) {
										CTDrawing drawing = r.getCTR().getDrawingList().get(i);
										drawing.getInlineList().get(0).getDocPr().setId(idImage);
										idImage++;
									}
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
									adicionarEnter(text, r);
								} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");
								}
							}
						}
					}
				}
			}
		    
		    
		    
		    XWPFTableRow tableRow2 = document.getTableArray(1).getRow(1);

		    paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			
			if (objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
				@SuppressWarnings("unused")
				int qtdePessoasEsquerdo = 0;
				for (int iPartTab = 0; iPartTab < objetoCcb.getListaParticipantes().size(); iPartTab++) {

					CcbParticipantes participante = objetoCcb.getListaParticipantes().get(iPartTab);
					if (iPartTab != 0) {
						if (iPartTab % 2 != 0) {

							run = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("____________________________________   ");
							run.setBold(false);
							run.addBreak();

							run2 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();

							run3 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();

							qtdePessoasEsquerdo++;
						} else {
							run = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("____________________________________   ");
							run.setBold(false);
							run.addBreak();

							run2 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();

							run3 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();
							qtdePessoasEsquerdo--;
						}
					}
				}
			}
			
			int indexParcela = 1;
			

			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(3);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
			
			geraPaginaContratoII(document, "9DC83E", false);

			table = document.getTableArray(2);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(objetoCcb.getDataDeEmissao(), objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteDFI(), "R$ "));
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public byte[] geraCciAquisicao() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/AquisicaoCCI_Novo.docx"));
				
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Times New Roman");
			fonts.setAscii("Times New Roman");
			fonts.setEastAsia("Times New Roman");
			fonts.setCs("Times New Roman");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			
			XWPFParagraph paragraph;
			
			XWPFTable table = document.getTables().get(0);
			setTableAlignment(table, STJc.CENTER);
			XWPFTableRow tableRow1 = table.getRow(2);
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			XWPFTableRow tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {		
				
				if(CommonsUtil.mesmoValor(participante.getTipoOriginal(), "TERCEIRO GARANTIDOR") 
						|| CommonsUtil.mesmoValor(participante.getTipoOriginal(), "Vendedor")) {
					participante.setTipoParticipante("Vendedor");
				
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			tableRow1 = table.getRow(4);
			tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			iParticipante = 0;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {		
				
				if (CommonsUtil.mesmoValor(participante.getTipoOriginal(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					participante.setTipoParticipante("Devedor");
								
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));		

						text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());								
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());
						text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
						
						if(CommonsUtil.mesmoValor(text, "aaaaaaaaaaa")){
							text = trocaValoresXWPF(text, r, "aaaaaaaaaaa", "");	 	
							
							for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
							
								r.setText("____________________________________________________________________");
								r.setBold(true);
								r.setFontSize(12);
								r.setFontFamily("Times New Roman");
								r.addCarriageReturn();
								r.setText(participante.getTipoParticipante().toUpperCase() + ": " + participante.getPessoa().getNome());
								r.addCarriageReturn();
								r.addCarriageReturn();
								r.addCarriageReturn();
							}
						}
			        }
			    }
			}	
		
			int indexSegurados = 47;
			
			for(Segurado segurado : objetoCcb.getListSegurados()) {
				table = document.getTables().get(0);
				table.insertNewTableRow(indexSegurados);
				tableRow1 = table.getRow(indexSegurados);
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				CTTcBorders border = tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.TRIPLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Nome: " + segurado.getPessoa().getNome());
				tableRow1.createCell();////////////////////////////////////////////////////////////////////////
				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);			
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Percentual: ");
				run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run2.setFontSize(12);
				run2.setFontFamily("Times New Roman");
				run2.setBold(true);
				run2.setText(CommonsUtil.formataValorMonetario(segurado.getPorcentagemSegurador()) + "%");
				tableRow1.createCell();
				tableRow1.getCell(2).getCTTc().addNewTcPr();
				CTHMerge hMerge = CTHMerge.Factory.newInstance();
				table = document.getTables().get(0);
				hMerge.setVal(STMerge.RESTART);
				table.getRow(indexSegurados).getCell(1).getCTTc().getTcPr().setHMerge(hMerge);
				CTHMerge hMerge1 = CTHMerge.Factory.newInstance();
				hMerge.setVal(STMerge.CONTINUE);
				table.getRow(indexSegurados).getCell(2).getCTTc().getTcPr().setHMerge(hMerge1);
				border = tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				indexSegurados++;
			}
			// First Row
						
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;
			if (!CommonsUtil.semValor(objetoCcb.getValorMipParcela()))
				totalPrimeiraParcela = objetoCcb.getValorMipParcela();
			if (!CommonsUtil.semValor(objetoCcb.getValorDfiParcela()))
			totalPrimeiraParcela = totalPrimeiraParcela.add(objetoCcb.getValorDfiParcela());
			if (!CommonsUtil.semValor(objetoCcb.getValorParcela()))
			totalPrimeiraParcela = totalPrimeiraParcela.add(objetoCcb.getValorParcela());
			if (!CommonsUtil.semValor(taxaAdm))
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
			
			BigDecimal despesas = objetoCcb.getValorDespesas();
			BigDecimal custasCartorarias = BigDecimal.ZERO;
			BigDecimal itbi =  BigDecimal.ZERO;
			
			if(!objetoCcb.getDespesasAnexo2().isEmpty()) {
				for(ContasPagar cartorioItbi : objetoCcb.getDespesasAnexo2()) {
					if(!CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")
							&& !CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						continue;
					}
					
					if(CommonsUtil.semValor(cartorioItbi.getValor())) {
						continue;
					}
					
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")) {
						custasCartorarias = custasCartorarias.add(cartorioItbi.getValor());
					}
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						itbi = itbi.add(cartorioItbi.getValor());
					}
					despesas = despesas.subtract(cartorioItbi.getValor());
				}
			}
			
			objetoCcb.setCustasCartorariasValor(custasCartorarias);
			objetoCcb.setItbiValor(itbi);
			
			
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);		 
								
								text = trocaValoresXWPFCci(text, r, "precoVendaCompra", objetoCcb.getPrecoVendaCompra(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "PrecoVendaCompra", objetoCcb.getPrecoVendaCompra());	
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", objetoCcb.getValorLiquidoCredito());								
								text = trocaValoresXWPFCci(text, r, "custoEmissao", objetoCcb.getCustoEmissao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", objetoCcb.getCustoEmissao());	
								text = trocaValoresXWPFCci(text, r, "valorIOF", objetoCcb.getValorIOF(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", objetoCcb.getValorIOF());	
								text = trocaValoresXWPFCci(text, r, "valorDespesas", despesas, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", despesas);	
								
								text = trocaValoresXWPFCci(text, r, "custasCartorariasValor", objetoCcb.getCustasCartorariasValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustasCartorariasValor", objetoCcb.getCustasCartorariasValor());
								
								text = trocaValoresXWPFCci(text, r, "itbiValor", objetoCcb.getItbiValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ItbiValor", objetoCcb.getItbiValor());
								
								text = trocaValoresXWPF(text, r, "titularConta", objetoCcb.getTitularConta());
								text = trocaValoresXWPF(text, r, "agencia", objetoCcb.getAgencia());
								text = trocaValoresXWPF(text, r, "contaCorrente", objetoCcb.getContaCorrente());					
								text = trocaValoresXWPF(text, r, "nomeBanco", objetoCcb.getNomeBanco());		
				
								text = trocaValoresXWPF(text, r, "prazoContrato", objetoCcb.getPrazo());
								text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", objetoCcb.getNumeroParcelasPagamento());
								text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", objetoCcb.getVencimentoPrimeiraParcelaPagamento());
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", objetoCcb.getTaxaDeJurosMes());
								text = trocaValoresXWPF(text, r, "taxaDeJurosAno", objetoCcb.getTaxaDeJurosAno());
								text = trocaValoresXWPF(text, r, "cetMes", objetoCcb.getCetMes());
								text = trocaValoresXWPF(text, r, "cetAno", objetoCcb.getCetAno());
								
								text = trocaValoresXWPFCci(text, r, "totalPrimeiraParcela", totalPrimeiraParcela, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "TotalPrimeiraParcela", totalPrimeiraParcela);	
								text = trocaValoresXWPFCci(text, r, "valorMipParcela", objetoCcb.getValorMipParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorMipParcela", objetoCcb.getValorMipParcela());			
								text = trocaValoresXWPFCci(text, r, "valorDfiParcela", objetoCcb.getValorDfiParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDfiParcela", objetoCcb.getValorDfiParcela());
								text = trocaValoresXWPFCci(text, r, "valorParcela", objetoCcb.getValorParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorParcela", objetoCcb.getValorParcela());		
								
								text = trocaValoresXWPF(text, r, "numeroCCB", objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "serieCcb", objetoCcb.getSerieCcb());
								
								text = trocaValoresXWPF(text, r, "numeroRegistroMatricula", objetoCcb.getNumeroRegistroMatricula());
								
								text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
								text = trocaValoresXWPF(text, r, "numeroImovel", objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "inscricaoMunicipal", objetoCcb.getInscricaoMunicipal());
								text = trocaValoresXWPFCci(text, r, "vendaLeilao", objetoCcb.getVendaLeilao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", objetoCcb.getVendaLeilao());	
								
								text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", objetoCcb.getVencimentoUltimaParcelaPagamento());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
								

								text = trocaValoresXWPF(text, r, "elaboradorNome", objetoCcb.getElaboradorNome());								
								text = trocaValoresXWPF(text, r, "elaboradorCrea", objetoCcb.getElaboradorCrea());
								text = trocaValoresXWPF(text, r, "responsavelNome", objetoCcb.getResponsavelNome());
								text = trocaValoresXWPF(text, r, "responsavelCrea", objetoCcb.getResponsavelCrea());
								
								
								
								
								if (text != null && text.contains("sistemaAmortizacao")) {
									if(CommonsUtil.mesmoValor(objetoCcb.getSistemaAmortizacao(), "Price")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "Tabela Price");
									} else if(CommonsUtil.mesmoValor(objetoCcb.getSistemaAmortizacao(), "SAC")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "SAC - Sistema de Amortização Constante");
									} else {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "");
									}
								}
								
								if (text != null && text.contains("participantesCci")) {
									text = text.replace("participantesCci", "");
									r.setText(text, 0);			
								}
								
								if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
									int iImagem = 0;
									for(iImagem = 0; iImagem < filesList.size(); iImagem++) {
										r.addBreak();
										populateFiles(iImagem);
										r.addPicture(bis, fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
										r.addBreak();	
									}
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
									adicionarEnter(text, r);
								} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");
								}
							}
						}
						
						for (XWPFTable t : cell.getTables()) {
							for (XWPFTableRow row2 : t.getRows()) {
								for (XWPFTableCell cell2 : row2.getTableCells()) {
									for (XWPFParagraph p2 : cell2.getParagraphs()) {
										for (XWPFRun r2 : p2.getRuns()) {
											String text = r2.getText(0);
											text = trocaValoresXWPF(text, r2, "cartorioImovel", objetoCcb.getCartorioImovel());
											text = trocaValoresXWPF(text, r2, "cidadeImovel", objetoCcb.getCidadeImovel());
											text = trocaValoresXWPF(text, r2, "ufImovel", objetoCcb.getUfImovel());		
											text = trocaValoresXWPF(text, r2, "numeroImovel", objetoCcb.getNumeroImovel());
											text = trocaValoresXWPF(text, r2, "inscricaoMunicipal", objetoCcb.getInscricaoMunicipal());
											text = trocaValoresXWPFCci(text, r2, "vendaLeilao", objetoCcb.getVendaLeilao(), "R$ ");
											text = trocaValoresDinheiroExtensoXWPF(text, r2, "VendaLeilao", objetoCcb.getVendaLeilao());	
											
										}
									}
								}
							}
						}
					}
				}
			}
		    
		    int indexParcela = 1;
			
			//calcularSimulador();

			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(2);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
			
			table = document.getTableArray(1);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(objetoCcb.getDataDeEmissao(), objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteDFI(), "R$ "));
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraCciFinanciamento() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			List<CcbParticipantes> segurados = new ArrayList<CcbParticipantes>();
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
					segurados.add(participante);
				}
				 else if(CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")){
					segurados.add(participante);
				}  else if(CommonsUtil.mesmoValor(participante.getTipoParticipante(), "COMPRADOR")){
					segurados.add(participante);
				}
			}
			
			
			if ( CommonsUtil.semValor( objetoCcb.getProcessosJucidiais() ) )
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/CCI - Financiamento202310SemProcesso.docx"));
			else
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/CCI - Financiamento202310ComProcesso.docx"));
			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Times New Roman");
			fonts.setAscii("Times New Roman");
			fonts.setEastAsia("Times New Roman");
			fonts.setCs("Times New Roman");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
					
			XWPFParagraph paragraph;
			
			XWPFTable table = document.getTables().get(0);
			setTableAlignment(table, STJc.CENTER);
			XWPFTableRow tableRow1 = table.getRow(2);
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			XWPFTableRow tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {		
				
				if(CommonsUtil.mesmoValor(participante.getTipoOriginal(), "TERCEIRO GARANTIDOR") 
						|| CommonsUtil.mesmoValor(participante.getTipoOriginal(), "Vendedor")) {
					participante.setTipoParticipante("Vendedor");
				
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " - " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			tableRow1 = table.getRow(4);
			tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			iParticipante = 0;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {		
				
				if (CommonsUtil.mesmoValor(participante.getTipoOriginal(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					participante.setTipoParticipante("Comprador");
								
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			String numerosProcessos = "";
			String totalProcessosFormatado = "";
			BigDecimal totalProcessos = BigDecimal.ZERO;
			if (!CommonsUtil.semValor(objetoCcb.getProcessosJucidiais())) {
				for (CcbProcessosJudiciais processo : objetoCcb.getProcessosJucidiais()) {
					if (CommonsUtil.semValor(processo.getValorAtualizado())) {
						continue;
					}
					numerosProcessos = numerosProcessos + ((!CommonsUtil.semValor(numerosProcessos)) ? ", " : "")
							+ "Nº " + CommonsUtil.stringValueVazio(processo.getNumero());
					totalProcessos = totalProcessos.add(processo.getValorAtualizado());
				}
				numerosProcessos = numerosProcessos.trim();
			}
			if (totalProcessos.compareTo(BigDecimal.ZERO) > 0) {
				int ultimaVirgula = numerosProcessos.lastIndexOf(", ");
				if (ultimaVirgula > -1) {
					numerosProcessos = numerosProcessos.substring(0, ultimaVirgula) + " e "
							+ numerosProcessos.substring(ultimaVirgula + 2);
				}
				valorPorExtenso.setNumber(totalProcessos);
				totalProcessosFormatado = CommonsUtil.formataValorMonetarioCci(totalProcessos, "") + " ("
						+ valorPorExtenso.toString() + ")";
			}
			XWPFRun runAssinatura = null;
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));		

						text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());								
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());
						text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
						
						text = trocaValoresXWPF(text, r, "elaboradorNome", objetoCcb.getElaboradorNome());								
						text = trocaValoresXWPF(text, r, "elaboradorCrea", objetoCcb.getElaboradorCrea());
						text = trocaValoresXWPF(text, r, "responsavelNome", objetoCcb.getResponsavelNome());
						text = trocaValoresXWPF(text, r, "responsavelCrea", objetoCcb.getResponsavelCrea());
						
						text = trocaValoresXWPF(text, r, "numerosProcessos",numerosProcessos);
						text = trocaValoresXWPF(text, r, "totalProcessos",totalProcessosFormatado);
						
						if(CommonsUtil.mesmoValor(text, "aaaaaaaaaaa")){
							text = trocaValoresXWPF(text, r, "aaaaaaaaaaa", "");	 	
							
							for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
							
								r.setText("____________________________________________________________________");
								r.setBold(true);
								r.setFontSize(12);
								r.setFontFamily("Times New Roman");
								r.addCarriageReturn();
								r.setText(participante.getTipoParticipante().toUpperCase() + ": " + participante.getPessoa().getNome());
								r.addCarriageReturn();
								r.addCarriageReturn();
								r.addCarriageReturn();
							}
						}
						
						if(CommonsUtil.mesmoValor(text, "TextoAssinatura")){
							text = trocaValoresXWPF(text, r, "TextoAssinatura", "");	 	
							runAssinatura = r;
							
						}
			        }
			    }
			}
			if(!CommonsUtil.semValor(runAssinatura)) {
				XWPFRun r = runAssinatura;
				for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
					r = r.getParagraph().createRun();
					r.setText("______________________________________");
					r.setBold(false);
					r.setFontSize(12);
					r.setFontFamily("Calibri");
					r.addCarriageReturn();
					XWPFRun r2 = r.getParagraph().createRun();
					r2.setBold(true);
					r2.setFontSize(12);
					r2.setFontFamily("Calibri");
					r2.setText(participante.getPessoa().getNome());
					r2.addCarriageReturn();
					r = r.getParagraph().createRun();
					r.setFontSize(12);
					r.setFontFamily("Calibri");
					r.setText(participante.getTipoParticipante().toUpperCase());
					r.addCarriageReturn();
					r.addCarriageReturn();
				}
			}
			
			
			organizaSegurados(segurados);
			int indexSegurados = 61;
			
			for(Segurado segurado : objetoCcb.getListSegurados()) {
				table = document.getTables().get(0);
				table.insertNewTableRow(indexSegurados);
				tableRow1 = table.getRow(indexSegurados);
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph);		
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setText("");
				tableRow1.getCell(0).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(1).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(2).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(3).getCTTc().addNewTcPr();
				tableRow1.createCell();
				tableRow1.getCell(4).getCTTc().addNewTcPr();			
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph);		
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setText("");
				tableRow1.getCell(5).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(6).getCTTc().addNewTcPr();				
				//tableRow1.createCell();
				//tableRow1.getCell(7).getCTTc().addNewTcPr();				
				CTHMerge hMerge = CTHMerge.Factory.newInstance();
				table = document.getTables().get(0);
				hMerge.setVal(STMerge.RESTART);
				table.getRow(indexSegurados).getCell(0).getCTTc().getTcPr().setHMerge(hMerge);				
				CTHMerge hMerge1 = CTHMerge.Factory.newInstance();
				hMerge1.setVal(STMerge.CONTINUE);
				table.getRow(indexSegurados).getCell(1).getCTTc().getTcPr().setHMerge(hMerge1);
				table.getRow(indexSegurados).getCell(2).getCTTc().getTcPr().setHMerge(hMerge1);				
				table.getRow(indexSegurados).getCell(3).getCTTc().getTcPr().setHMerge(hMerge1);
				table.getRow(indexSegurados).getCell(4).getCTTc().getTcPr().setHMerge(hMerge1);				
				CTHMerge hMerge2 = CTHMerge.Factory.newInstance();
				table = document.getTables().get(0);
				hMerge2.setVal(STMerge.RESTART);
				table.getRow(indexSegurados).getCell(5).getCTTc().getTcPr().setHMerge(hMerge2);
				table.getRow(indexSegurados).getCell(6).getCTTc().getTcPr().setHMerge(hMerge1);
				//table.getRow(indexSegurados).getCell(7).getCTTc().getTcPr().setHMerge(hMerge1);
				
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				CTTcBorders border = tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.TRIPLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				run = tableRow1.getCell(0).getParagraphArray(0).getRuns().get(0);
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Nome: " + segurado.getPessoa().getNome());
				
				tableRow1.getCell(5).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);	
				border = tableRow1.getCell(5).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				border = tableRow1.getCell(6).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getRight().setColor("808080");
				border.getLeft().setColor("808080");
				/*border = tableRow1.getCell(7).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");*/
				run = tableRow1.getCell(5).getParagraphArray(0).getRuns().get(0);
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Percentual: ");
				run2 = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run2.setFontSize(12);
				run2.setFontFamily("Times New Roman");
				run2.setBold(true);
				run2.setText(CommonsUtil.formataValorMonetario(segurado.getPorcentagemSegurador()) + "%");
				indexSegurados++;
			}
			// First Row
						
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;
			totalPrimeiraParcela = objetoCcb.getValorMipParcela();
			totalPrimeiraParcela = totalPrimeiraParcela.add(objetoCcb.getValorDfiParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(objetoCcb.getValorParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
			
			BigDecimal despesas = objetoCcb.getValorDespesas();
			BigDecimal custasCartorarias = BigDecimal.ZERO;
			BigDecimal itbi =  BigDecimal.ZERO;
			
			if(!objetoCcb.getDespesasAnexo2().isEmpty()) {
				for(ContasPagar cartorioItbi : objetoCcb.getDespesasAnexo2()) {
					if(!CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")
							&& !CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						continue;
					}
					
					if(CommonsUtil.semValor(cartorioItbi.getValor())) {
						continue;
					}
					
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")) {
						custasCartorarias = custasCartorarias.add(cartorioItbi.getValor());
					}
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						itbi = itbi.add(cartorioItbi.getValor());
					}
					despesas = despesas.subtract(cartorioItbi.getValor());
				}
			}
			
			objetoCcb.setCustasCartorariasValor(custasCartorarias);
			objetoCcb.setItbiValor(itbi);
			
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);		 
								
								text = trocaValoresXWPFCci(text, r, "precoVendaCompra", objetoCcb.getPrecoVendaCompra(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "PrecoVendaCompra", objetoCcb.getPrecoVendaCompra());	
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", objetoCcb.getValorLiquidoCredito());								
								text = trocaValoresXWPFCci(text, r, "custoEmissao", objetoCcb.getCustoEmissao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", objetoCcb.getCustoEmissao());	
								text = trocaValoresXWPFCci(text, r, "valorIOF", objetoCcb.getValorIOF(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", objetoCcb.getValorIOF());	
								text = trocaValoresXWPFCci(text, r, "valorDespesas", despesas, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", despesas);	
								
								text = trocaValoresXWPFCci(text, r, "custasCartorariasValor", objetoCcb.getCustasCartorariasValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustasCartorariasValor", objetoCcb.getCustasCartorariasValor());
								
								text = trocaValoresXWPFCci(text, r, "itbiValor", objetoCcb.getItbiValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ItbiValor", objetoCcb.getItbiValor());
								
								text = trocaValoresXWPFCci(text, r, "recursosProprios", objetoCcb.getRecursosProprios(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "RecursosProprios", objetoCcb.getRecursosProprios());							
								text = trocaValoresXWPFCci(text, r, "recursosFinanciamento", objetoCcb.getRecursosFinanciamento(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "RecursosFinanciamento", objetoCcb.getRecursosFinanciamento());
								
								text = trocaValoresXWPF(text, r, "titularContaVendedor", objetoCcb.getTitularContaVendedor());
								text = trocaValoresXWPF(text, r, "agenciaVendedor", objetoCcb.getAgenciaVendedor());
								text = trocaValoresXWPF(text, r, "contaCorrenteVendedor", objetoCcb.getContaCorrenteVendedor());					
								text = trocaValoresXWPF(text, r, "nomeBancoVendedor", objetoCcb.getNomeBancoVendedor() + " - " + objetoCcb.getNumeroBancoVendedor() );		
								text = trocaValoresXWPF(text, r, "digitoBancoVendedor", objetoCcb.getDigitoBancoVendedor());	
								text = trocaValoresXWPF(text, r, "tipoContaBancoVendedor", objetoCcb.getTipoContaBancoVendedor());	
								
								text = trocaValoresXWPF(text, r, "titularConta", objetoCcb.getTitularConta());
								text = trocaValoresXWPF(text, r, "agencia", objetoCcb.getAgencia());
								text = trocaValoresXWPF(text, r, "contaCorrente", objetoCcb.getContaCorrente());					
								text = trocaValoresXWPF(text, r, "nomeBanco", objetoCcb.getNomeBanco() + " - " + objetoCcb.getNumeroBanco() );		
								text = trocaValoresXWPF(text, r, "digitoBanco", objetoCcb.getDigitoBanco());	
								text = trocaValoresXWPF(text, r, "tipoContaBanco", objetoCcb.getTipoContaBanco());	
				
								text = trocaValoresXWPF(text, r, "prazoContrato", objetoCcb.getPrazo());
								text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", objetoCcb.getNumeroParcelasPagamento());
								text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", objetoCcb.getVencimentoPrimeiraParcelaPagamento());
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", objetoCcb.getTaxaDeJurosMes());
								text = trocaValoresXWPF(text, r, "taxaDeJurosAno", objetoCcb.getTaxaDeJurosAno());
								text = trocaValoresXWPF(text, r, "cetMes", objetoCcb.getCetMes());
								text = trocaValoresXWPF(text, r, "cetAno", objetoCcb.getCetAno());
								
								text = trocaValoresXWPFCci(text, r, "totalPrimeiraParcela", totalPrimeiraParcela, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "TotalPrimeiraParcela", totalPrimeiraParcela);	
								text = trocaValoresXWPFCci(text, r, "valorMipParcela", objetoCcb.getValorMipParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorMipParcela", objetoCcb.getValorMipParcela());			
								text = trocaValoresXWPFCci(text, r, "valorDfiParcela", objetoCcb.getValorDfiParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDfiParcela", objetoCcb.getValorDfiParcela());
								text = trocaValoresXWPFCci(text, r, "valorParcela", objetoCcb.getValorParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorParcela", objetoCcb.getValorParcela());		
								
								text = trocaValoresXWPF(text, r, "numeroCCB", objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "serieCCB", objetoCcb.getSerieCcb());
								text = trocaValoresXWPF(text, r, "numeroRegistroMatricula", objetoCcb.getNumeroRegistroMatricula());
								
								text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
								text = trocaValoresXWPF(text, r, "numeroImovel", objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "inscricaoMunicipal", objetoCcb.getInscricaoMunicipal());
								text = trocaValoresXWPFCci(text, r, "vendaLeilao", objetoCcb.getVendaLeilao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", objetoCcb.getVendaLeilao());	
								
								text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", objetoCcb.getVencimentoUltimaParcelaPagamento());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
								
								if (text != null && text.contains("sistemaAmortizacao")) {
									if(CommonsUtil.mesmoValor(objetoCcb.getSistemaAmortizacao(), "Price")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "Tabela Price");
									} else if(CommonsUtil.mesmoValor(objetoCcb.getSistemaAmortizacao(), "SAC")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "SAC - Sistema de Amortização Constante");
									} else {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "");
									}
								}
								
								if (text != null && text.contains("participantesCci")) {
									text = text.replace("participantesCci", "");
									r.setText(text, 0);			
								}
								
								if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
									int iImagem = 0;
									for(iImagem = 0; iImagem < filesList.size(); iImagem++) {
										r.addBreak();
										populateFiles(iImagem);
										r.addPicture(bis, fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
										r.addBreak();	
									}
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
									adicionarEnter(text, r);
								} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");
								}
							}
						}
						
						for (XWPFTable t : cell.getTables()) {
							for (XWPFTableRow row2 : t.getRows()) {
								for (XWPFTableCell cell2 : row2.getTableCells()) {
									for (XWPFParagraph p2 : cell2.getParagraphs()) {
										for (XWPFRun r2 : p2.getRuns()) {
											String text = r2.getText(0);
											text = trocaValoresXWPF(text, r2, "cartorioImovel", objetoCcb.getCartorioImovel());
											text = trocaValoresXWPF(text, r2, "cidadeImovel", objetoCcb.getCidadeImovel());
											text = trocaValoresXWPF(text, r2, "ufImovel", objetoCcb.getUfImovel());		
											text = trocaValoresXWPF(text, r2, "numeroImovel", objetoCcb.getNumeroImovel());
											text = trocaValoresXWPF(text, r2, "inscricaoMunicipal", objetoCcb.getInscricaoMunicipal());
											text = trocaValoresXWPFCci(text, r2, "vendaLeilao", objetoCcb.getVendaLeilao(), "R$ ");
											text = trocaValoresDinheiroExtensoXWPF(text, r2, "VendaLeilao", objetoCcb.getVendaLeilao());	
											
										}
									}
								}
							}
						}
					}
				}
			}
		    
		    int indexParcela = 1;
		    
			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
//			calcularSimulador();
			
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(2);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
		    
		    geraPaginaContratoII(document, "9DC83E", false);
			table = document.getTableArray(1);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(objetoCcb.getDataDeEmissao(), objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteDFI(), "R$ "));
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraInstrumentoEmissaoCCI() throws IOException{
		try {
			XWPFDocument document;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					objetoCcb.setTerceiroGarantidor(true);
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/Instrumento_Emissao_CCI_BMP.docx"));
					
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
				}
			}
			
			int prazoAno = CommonsUtil.intValue(objetoCcb.getPrazo()) / 12;
			String prazoAnoStr = CommonsUtil.stringValue(prazoAno);
			String estado = estadoPorExtenso(objetoCcb.getUfImovel());
						
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);
					            
					            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "numeroCCB", objetoCcb.getNumeroCcb());
					            
								text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	
								text = trocaValoresXWPF(text, r, "cpfEmitente", objetoCcb.getCpfEmitente());								
								text = trocaValoresXWPF(text, r, "logradouroEmitente", objetoCcb.getEmitentePrincipal().getPessoa().getEndereco());	
								text = trocaValoresXWPF(text, r, "numeroEmitente", objetoCcb.getEmitentePrincipal().getPessoa().getNumero());	
								text = trocaValoresXWPF(text, r, "complementoEmitente", objetoCcb.getEmitentePrincipal().getPessoa().getComplemento());								
								text = trocaValoresXWPF(text, r, "cepEmitente", objetoCcb.getEmitentePrincipal().getPessoa().getCep());
								text = trocaValoresXWPF(text, r, "cidadeEmitente",objetoCcb.getEmitentePrincipal().getPessoa().getCidade());			
								text = trocaValoresXWPF(text, r, "ufEmitente", objetoCcb.getEmitentePrincipal().getPessoa().getEstado());							
									
								text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());								
				
								text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", estado);
								text = trocaValoresXWPF(text, r, "estadoImovel", objetoCcb.getUfImovel());			
								text = trocaValoresXWPF(text, r, "numeroImovel", objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "logradouroRuaImovel", objetoCcb.getLogradouroRuaImovel());
								text = trocaValoresXWPF(text, r, "logradouroNumeroImovel", objetoCcb.getLogradouroNumeroImovel());
								text = trocaValoresXWPF(text, r, "bairroImovel", objetoCcb.getBairroImovel());
								text = trocaValoresXWPF(text, r, "cepImovel", objetoCcb.getCepImovel());

								text = trocaValoresXWPF(text, r, "parcelaDia", objetoCcb.getVencimentoPrimeiraParcelaPagamento().getDate());
								text = trocaValoresXWPF(text, r, "parcelaMes", CommonsUtil.formataMesExtenso(objetoCcb.getVencimentoPrimeiraParcelaPagamento()).toLowerCase());
								text = trocaValoresXWPF(text, r, "parcelaAno", (objetoCcb.getVencimentoPrimeiraParcelaPagamento().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "vencimentoDia", objetoCcb.getVencimentoUltimaParcelaPagamento().getDate());
								text = trocaValoresXWPF(text, r, "vencimentoMes", CommonsUtil.formataMesExtenso(objetoCcb.getVencimentoUltimaParcelaPagamento()).toLowerCase());
								text = trocaValoresXWPF(text, r, "vencimentoAno", (objetoCcb.getVencimentoUltimaParcelaPagamento().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "prazoAno", prazoAnoStr);
								text = trocaValoresNumeroExtensoXWPF(text, r, "Prazo", prazoAnoStr);
								
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", objetoCcb.getTaxaDeJurosMes());
								
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());
								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		    
		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());								
						text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
					}
			    }
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] geraAnexoII() throws IOException {
		try {
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun runHeader = paragraphHeader.createRun();
			runHeader.addPicture(getClass().getResourceAsStream("/resource/GalleriaBank.png"), 6, "Galleria Bank",
					Units.toEMU(130), Units.toEMU(72));

			geraPaginaContratoII(document, "8880F4", true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (JDBCException jdbce) {
		    jdbce.getSQLException().getNextException().printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			e.getCause().printStackTrace();
		} 
		return null;
	}

	public byte[] geraCessao() throws IOException{
		try {
			XWPFDocument document;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					objetoCcb.setTerceiroGarantidor(true);
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/CESSAO.docx"));
					
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					participante.setTipoParticipante("DEVEDOR");
				}
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());	
						text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());
						text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());				
						text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
						
						if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
							int iImagem = 0;
							for(iImagem = 0; iImagem < filesList.size(); iImagem++) {
								r.addBreak();
								populateFiles(iImagem);
								r.addPicture(bis, fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
								r.addBreak();	
							}
							text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
							adicionarEnter(text, r);
						} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
							text = trocaValoresXWPF(text, r, "ImagemImovel", "");
						}
						
						text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroImovel", objetoCcb.getNumeroImovel());
						
						text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", objetoCcb.getNumeroParcelasPagamento());
						text = trocaValoresNumeroExtensoXWPF(text, r, "NumeroParcelasPagamento", objetoCcb.getNumeroParcelasPagamento());
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", objetoCcb.getVencimentoPrimeiraParcelaPagamento());
						text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", objetoCcb.getVencimentoUltimaParcelaPagamento());
						
						text = trocaValoresXWPF(text, r, "taxaDeJurosMes", objetoCcb.getTaxaDeJurosMes());
						text = trocaValoresXWPF(text, r, "taxaDeJurosAno", objetoCcb.getTaxaDeJurosAno());
						
						text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());			       
					}
			    }
			}	
								
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraEndossosEmPretoGalleria() throws IOException{
		try {
			XWPFDocument document;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					objetoCcb.setTerceiroGarantidor(true);
				}
			}	
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/EndossosEmPretoGalleria.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}				
				}
			}

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            text = trocaValoresXWPF(text, r, "emissaoData", objetoCcb.getDataDeEmissao());								
						text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());		            
						text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente().toUpperCase());	
						text = trocaValoresXWPF(text, r, "cpfEmitente", objetoCcb.getCpfEmitente()); 
					}
			    }
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraCartaSplitDinamica() throws IOException{
		try {
			XWPFDocument document = new XWPFDocument();
			XWPFRun run;
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(100);
			run = paragraph.createRun();
			run.setText("Votorantim/SP, " + objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("À");
			run.setFontSize(11);
			run.setBold(false);
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getCpfEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						}
					}
					if(CommonsUtil.semValor(objetoCcb.getCpfEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCnpj())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
				}
			}
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Galleria Sociedade de Crédito Direto S.A.");
			run.setFontSize(11);
			run.setBold(true);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Autorizamos a efetivação de transferência no valor de ");
			run.setFontSize(11);
			run.setBold(false);
			BigDecimal valorCartaSplit =   objetoCcb.getValorLiquidoCredito().add(objetoCcb.getValorDespesas());
			valorPorExtenso.setNumber(valorCartaSplit);
			run2 = paragraph.createRun();
			run2.setText(CommonsUtil.formataValorMonetario(valorCartaSplit, "R$ ") + " (" + valorPorExtenso.toString() + ")," );
			run2.setFontSize(11);
			run2.setBold(true);
			run = paragraph.createRun();
			run.setText(" conforme dados abaixo, crédito oriundo da CCI n° " + objetoCcb.getNumeroCcb() + ", datada de " + objetoCcb.getDataDeEmissao().getDate() + " de "
					+ CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
					+ (objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(100);
			run = paragraph.createRun();
			run.setText("Contas a serem creditadas");
			run.setFontSize(11);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Nome: Galleria Correspondente Bancário Eireli");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run.setText("CPF/CNPJ: 34.787.885/0001-32");
			run.addCarriageReturn();
			run.setText("Banco: ");
			run2 = paragraph.createRun();
			run2.setText("Banco do Brasil");
			run2.setFontSize(11);
			run2.setBold(true);
			run2.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("Agência: 1515-6");
			run.addCarriageReturn();
			run.setText("C/C: 131094-1");
			run.addCarriageReturn();
			run.setText("Valor: ");
			run2 = paragraph.createRun();
			if(objetoCcb.getCCBValor().compareTo(BigDecimal.ZERO) > 0) {
				valorPorExtenso.setNumber(valorCartaSplit);
				run2.setText(CommonsUtil.formataValorMonetario(valorCartaSplit, "R$ ")  + " (" + valorPorExtenso.toString() + ") " );
			} else {
				valorPorExtenso.setNumber(objetoCcb.getValorDespesas());
				run2.setText(CommonsUtil.formataValorMonetario(objetoCcb.getValorDespesas(), "R$ ")  + " (" + valorPorExtenso.toString() + ") " );
			}
			run2.setFontSize(11);
			run2.setBold(true);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(100);
			run = paragraph.createRun();
			run.setText("Contas a serem creditadas (conta cliente no contrato Money)");
			run.setFontSize(11);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Nome: " + objetoCcb.getNomeEmitente().toUpperCase());
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run.setText("CPF/CNPJ: " + objetoCcb.getCpfEmitente());
			run.addCarriageReturn();
			run.setText("Banco: ");
			run2 = paragraph.createRun();
			run2.setText(objetoCcb.getNomeBanco() + "");
			run2.setFontSize(11);
			run2.setBold(true);
			run2.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("Agência: " + objetoCcb.getAgencia());
			run.addCarriageReturn();
			run.setText("C/C: " + objetoCcb.getContaCorrente() + " Pix: " + objetoCcb.getPixBanco());
			run.addCarriageReturn();
			run.setText("Valor: ");
			valorPorExtenso.setNumber(objetoCcb.getValorLiquidoCredito());
			run2 = paragraph.createRun();
			if(objetoCcb.getCCBValor().compareTo(BigDecimal.ZERO) > 0) {
				run2.setText("R$ 0,00" + " (Zero reais) " );
			} else {
				run2.setText(CommonsUtil.formataValorMonetario(objetoCcb.getValorLiquidoCredito(), "R$ ")  + " (" + valorPorExtenso.toString() + ") " );
			}
			
			run2.setFontSize(11);
			run2.setBold(true);
			run3 = paragraph.createRun();
			run3.setText("* Credito será efetuado somente no registro da alienação Fiduciária da CCI "
					+ objetoCcb.getNumeroCcb() + " da matricula " 
					+ objetoCcb.getNumeroImovel()  + " do " 
					+ objetoCcb.getCartorioImovel() + "° RI de " 
					+ objetoCcb.getCidadeImovel()  + " - " 
					+ objetoCcb.getUfImovel() );
			run3.setFontSize(11);
			run3.setColor("ff0000");
			run3.setBold(true);		
			run3.addCarriageReturn();
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();
		
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("_____________________________________________________________________________");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("NOME/RAZÃO SOCIAL: " + objetoCcb.getNomeEmitente().toUpperCase());
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("CPF/CNPJ: " + objetoCcb.getCpfEmitente());
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("(EMITENTE)");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			
			if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
				ContratoCobranca contrato = objetoCcb.getObjetoContratoCobranca();
				//this.objetoContratoCobranca = cDao.findById(objetoCcb.getObjetoContratoCobranca().getId());
				
				contrato.setValorCartaSplit(objetoCcb.getValorLiquidoCredito());
				contrato.setNomeBancarioCartaSplit(objetoCcb.getNomeEmitente());
				contrato.setCpfCnpjBancarioCartaSplit(objetoCcb.getCpfEmitente());
				contrato.setBancoBancarioCartaSplit(objetoCcb.getNomeBanco());
				contrato.setAgenciaBancarioCartaSplit(objetoCcb.getAgencia());
				contrato.setContaBancarioCartaSplit(objetoCcb.getContaCorrente());		
				
				//cDao.merge(contrato);
				//objetoCcb.setObjetoContratoCobranca(contrato);	
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraDeclaracaoNaoUniaoEstavel(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/DeclaracaoNaoUniaoEstavel.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			run = document.getParagraphs().get(1).getRuns().get(1);
			document.getParagraphs().get(1).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			run2 = document.getParagraphs().get(1).insertNewRun(2);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }				            
			            text = trocaValoresXWPF(text, r, "cidadeEmitente", (participante.getPessoa().getCidade()));    
			            text = trocaValoresXWPF(text, r, "ufEmitente", (participante.getPessoa().getEstado()));			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "cpfEmitente", (participante.getPessoa().getCpf()));
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());						
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraDeclaracaoUniaoEstavel(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/DeclaracaoUniaoEstavel.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			int paragraph = 4;
			run = document.getParagraphs().get(paragraph).insertNewRun(1);
			document.getParagraphs().get(paragraph).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			run2 = document.getParagraphs().get(paragraph).insertNewRun(2);
			run2.setFontSize(11);
			String filho;
			String nacionalidade = participante.getNacionalidade();
			String estadoCivilStr = "";
			PagadorRecebedor pessoa = participante.getPessoa();

			if (participante.isFeminino()) {
				if (CommonsUtil.mesmoValor(participante.getNacionalidade(), "brasileiro")) {
					nacionalidade = "brasileira";
				}
				filho = "filha";
				if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SOLTEIRO")) {
					estadoCivilStr = "solteira";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "VIÚVO")) {
					estadoCivilStr = "viúva";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "DIVORCIADO")) {
					estadoCivilStr = "divorciada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO")) {
					estadoCivilStr = "separada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO JUDICIALMENTE")) {
					estadoCivilStr = "separada judicialmente";
				} 
			} else {
				estadoCivilStr = pessoa.getEstadocivil().toLowerCase();
				filho = "filho";
			}
			estadoCivilStr = estadoCivilStr + " convivente em união estável";
			
			PagadorRecebedorDao pagadorDao = new PagadorRecebedorDao();
			PagadorRecebedor conjuge = pagadorDao.findByFilter("cpf", participante.getPessoa().getCpfConjuge()).get(0);
			CcbParticipantesDao partDao = new CcbParticipantesDao();
			CcbParticipantes participanteConjuge = partDao.findByFilter("pessoa", conjuge).get(0);
			String filhoConjuge;
			String nacionalidadeConjuge = participanteConjuge.getNacionalidade();
			String estadoCivilStrConjuge = "";
			if (participanteConjuge.isFeminino()) {
				if (CommonsUtil.mesmoValor(participanteConjuge.getNacionalidade(), "brasileiro")) {
					nacionalidadeConjuge = "brasileira";
				}
				filhoConjuge = "filha";
				if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "SOLTEIRO")) {
					estadoCivilStrConjuge = "solteira";
				} else if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "VIÚVO")) {
					estadoCivilStrConjuge = "viúva";
				} else if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "DIVORCIADO")) {
					estadoCivilStrConjuge = "divorciada";
				} else if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "SEPARADO")) {
					estadoCivilStrConjuge = "separada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO JUDICIALMENTE")) {
					estadoCivilStrConjuge = "separada judicialmente";
				} 
			} else {
				estadoCivilStrConjuge = conjuge.getEstadocivil().toLowerCase();
				filhoConjuge = "filho";
			}
			estadoCivilStrConjuge = estadoCivilStrConjuge + " convivente em união estável";
			
			
			run2.setText( filho + " de " + pessoa.getNomeMae() + " e " + pessoa.getNomePai() + ", "
					+ nacionalidade + ", "+ pessoa.getAtividade() + ", "+ estadoCivilStr + ","
					+ " portador(a) da Cédula de Identidade RG nº "+ pessoa.getRg() + " " + pessoa.getOrgaoEmissorRG() + ","
					+ " inscrito(a) no CPF/MF sob o nº "+ pessoa.getCpf() +", endereço eletrônico: "+ pessoa.getEmail() +" e ");	
			
			run = document.getParagraphs().get(paragraph).insertNewRun(3);
			run.setFontSize(11);
			run.setText(conjuge.getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			
			run2 = document.getParagraphs().get(paragraph).insertNewRun(4);
			run2.setFontSize(11);
			run2.setText( filhoConjuge + " de " + conjuge.getNomeMae() + " e " + conjuge.getNomePai() + ", "
					+ nacionalidadeConjuge + ", "+ conjuge.getAtividade() + ", "+ estadoCivilStrConjuge + ","
					+ " portador(a) da Cédula de Identidade RG nº "+ conjuge.getRg() + " " + conjuge.getOrgaoEmissorRG() + ","
					+ " inscrito(a) no CPF/MF sob o nº "+ conjuge.getCpf() +", endereço eletrônico: "+ conjuge.getEmail() 
					+ ", residentes e domiciliados à "+ pessoa.getEndereco() +", nº "+ pessoa.getNumero() +", "
					+ pessoa.getComplemento() + ", "+ pessoa.getBairro() + ", " 
					+ pessoa.getCidade()+"/"+pessoa.getEstado()+", CEP "+ pessoa.getCep()+"; ");
			
			////
		

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			            	            
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
						text = trocaValoresXWPF(text, r, "nomeConjuge", (participante.getPessoa().getNomeConjuge()));
						text = trocaValoresXWPF(text, r, "cpfEmitente", (participante.getPessoa().getCpf()));    
						text = trocaValoresXWPF(text, r, "cpfConjuge", (participante.getPessoa().getCpfConjuge()));
							            
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
			         
			            text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());						
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
						text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());						
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] geraDeclaracaoDestinacaoRecursos(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/DeclaracaoDeCienciaDestinacaoDeRecurso.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			run = document.getParagraphs().get(5).insertNewRun(0);
			document.getParagraphs().get(5).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().trim().toUpperCase() + ", ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			run.setCharacterSpacing(1*10);
			run.setFontSize(11);
			run2 = document.getParagraphs().get(5).insertNewRun(1);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			run2.setUnderline(UnderlinePatterns.NONE);
			run2.setFontSize(11);
			run2.setText(run2.getText(0).replace(';', ','));
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			           
			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());		          
			            text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroMatricula", objetoCcb.getNumeroImovel());
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
							}
						}
					}
				}
			}
		    
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraTermoResponsabilidadeAnuenciaPaju(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			int fontSize = 10;
			
			if(CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "PR") || CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "Paraná")) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeResponsabilidadeAnuenciaPajuPR.docx"));
				fontSize = 12;
			} else if(CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "RJ") || CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "Rio de Janeiro")) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeResponsabilidadeAnuenciaPajuRJ.docx"));
				fontSize = 11;
			} else {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeResponsabilidadeAnuenciaPaju.docx"));
				fontSize = 10;
			}
						
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			int paragraph = 2;
			run = document.getParagraphs().get(paragraph).insertNewRun(0);
			document.getParagraphs().get(paragraph).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().trim().toUpperCase() + ", ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			run.setCharacterSpacing(1*10);
			run.setFontSize(fontSize);
			run2 = document.getParagraphs().get(paragraph).insertNewRun(1);
			run = document.getParagraphs().get(paragraph).insertNewRun(2);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			run2.setUnderline(UnderlinePatterns.NONE);
			run2.setFontSize(fontSize);
			run2.setText(run2.getText(0).replace(';', ','));
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			           
			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());		          
			            text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroMatricula", objetoCcb.getNumeroImovel());
						
						text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());						
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
						text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
						
						Date pajuGerado = objetoCcb.getObjetoContratoCobranca().getDataPajuComentado();
						
						text = trocaValoresXWPF(text, r, "pajuDia", pajuGerado.getDate());
						text = trocaValoresXWPF(text, r, "pajuMes", CommonsUtil.formataMesExtenso(pajuGerado).toLowerCase());
						text = trocaValoresXWPF(text, r, "pajuAno", (pajuGerado.getYear() + 1900));
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
							}
						}
					}
				}
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraTermoPajuRJ_PR(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			int fontSize = 10;
			
			if(CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "PR") || CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "Paraná")) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeResponsabilidadeAnuenciaPajuPR.docx"));
				fontSize = 12;
			} else if(CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "RJ") || CommonsUtil.mesmoValor(objetoCcb.getUfImovel(), "Rio de Janeiro")) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeResponsabilidadeAnuenciaPajuRJ.docx"));
				fontSize = 11;
			} else {
				return null;
			}
						
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			int paragraph = 2;
			run = document.getParagraphs().get(paragraph).insertNewRun(0);
			document.getParagraphs().get(paragraph).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().trim().toUpperCase() + ", ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			run.setCharacterSpacing(1*10);
			run.setFontSize(fontSize);
			run2 = document.getParagraphs().get(paragraph).insertNewRun(1);
			run = document.getParagraphs().get(paragraph).insertNewRun(2);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			run2.setUnderline(UnderlinePatterns.NONE);
			run2.setFontSize(fontSize);
			run2.setText(run2.getText(0).replace(';', ','));
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			           
			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());		          
			            text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroMatricula", objetoCcb.getNumeroImovel());
						
						text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());						
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
						text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
						
						Date pajuGerado = objetoCcb.getObjetoContratoCobranca().getDataPajuComentado();
						
						text = trocaValoresXWPF(text, r, "pajuDia", pajuGerado.getDate());
						text = trocaValoresXWPF(text, r, "pajuMes", CommonsUtil.formataMesExtenso(pajuGerado).toLowerCase());
						text = trocaValoresXWPF(text, r, "pajuAno", (pajuGerado.getYear() + 1900));
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
							}
						}
					}
				}
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraTermoIncomunicabilidadeImovel(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeIncomunicabilidadeImovel.docx"));

			String tipoUniao = ""; //sociedade conjugal/união estável 
			String pronome = ""; //minha/meu
			String tipoConjuge = ""; //cônjuge/companheiro
			
			if(CommonsUtil.mesmoValor(participante.getPessoa().getSexoConjuge(), "FEMININO")) {
				pronome = "minha";
				tipoConjuge = "companheira";
			} else {
				pronome = "meu";
				tipoConjuge = "companheiro";
			}
			
			if(participante.isUniaoEstavel()) {
				tipoUniao = "união estável";
			} else {
				tipoUniao = "sociedade conjugal";
				tipoConjuge = "cônjuge";
			}
			
		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            } 						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
						text = trocaValoresXWPF(text, r, "cpfEmitente", (participante.getPessoa().getCpf()));    
			            text = trocaValoresXWPF(text, r, "dataCompraImovel", objetoCcb.getDataCompraImovel());
			            text = trocaValoresXWPF(text, r, "numeroMatricula", objetoCcb.getNumeroImovel());		          
			            text = trocaValoresXWPF(text, r, "cartorioImovel", objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
						
						text = trocaValoresXWPF(text, r, "tipoUniao", tipoUniao);
						text = trocaValoresXWPF(text, r, "dataCasamento", participante.getPessoa().getDataCasamento());
						text = trocaValoresXWPF(text, r, "pronome", pronome);
						text = trocaValoresXWPF(text, r, "tipoConjuge", tipoConjuge);
						
						text = trocaValoresXWPF(text, r, "nomeConjuge", participante.getPessoa().getNomeConjuge());
						text = trocaValoresXWPF(text, r, "cpfConjuge", participante.getPessoa().getCpfConjuge());
						
						text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
							}
						}
					}
				}
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraFichaPPE() throws IOException{
		try {			
			InputStream is = getClass().getResourceAsStream("/resource/Ficha PPE.pdf");
			byte[] bytes = IOUtils.toByteArray(is);
			return bytes;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraFichaPLDeFT() throws IOException{
		try {
			InputStream is = getClass().getResourceAsStream("/resource/Ficha PLD e FT.pdf");
			byte[] bytes = IOUtils.toByteArray(is);
			return bytes;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] geraFichaCadastroNova(PagadorRecebedor pagador) throws IOException{
		ImpressoesPDFMB impressaoMb = new ImpressoesPDFMB();
		return impressaoMb.geraPdfCadastroPagadorRecebedorNovo(pagador);
	}
	
	public byte[] geraFichaCadastro(PagadorRecebedor pagador) throws IOException{
		ImpressoesPDFMB impressaoMb = new ImpressoesPDFMB();
		return impressaoMb.geraPdfCadastroPagadorRecebedor(pagador);
	}
	
	public byte[] geraAverbacao(CcbParticipantes participante) throws IOException{
		try {
			
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			List<String> documentos = new ArrayList<String>();
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/Averbacao.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			run = document.getParagraphs().get(2).getRuns().get(2);
			document.getParagraphs().get(2).setAlignment(ParagraphAlignment.BOTH);
			run.setFontSize(12);
			String runText = run.getText(0);
			trocaValoresXWPF(runText, run, "paragrafoPF", "");
			run.setText(participante.getPessoa().getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			run2 = document.getParagraphs().get(2).insertNewRun(3);
			run2.setFontSize(12);
			geraParagrafoPF(run2, participante);
			run2.getText(0).replace("; ", "");
			runText = run2.getText(0);
			trocaValoresXWPF(runText, run2, "; ", "");
			
			int index = 6;
			boolean inicio = true;
			for(Averbacao averbacao : participante.getPessoa().getListAverbacao()) {
				if(!documentos.contains(averbacao.getDocumento())) {
					documentos.add(averbacao.getDocumento());
				}
				run = document.getParagraphs().get(5).insertNewRun(index);
				String informacaoString = "";
				if(CommonsUtil.mesmoValor(averbacao.getInformacao(), "CNH")) {
					informacaoString = "o nº da Cédula de Identidade CNH";
				} else if(CommonsUtil.mesmoValor(averbacao.getInformacao(), "RG")) {
					informacaoString = "o nº da Cédula de Identidade RG";
				} else if(CommonsUtil.mesmoValor(averbacao.getInformacao(), "Estado Civil")) {
					informacaoString = "o estado civil";
				} else if(CommonsUtil.mesmoValor(averbacao.getInformacao(), "Nome")) {
					informacaoString = "o nome do proprietário";
				}
				if(CommonsUtil.mesmoValor(averbacao.getTipoAverbacao(), "INCLUIR")) {
					informacaoString = informacaoString + " n° ";
				} else if(CommonsUtil.mesmoValor(averbacao.getTipoAverbacao(), "ALTERAR")) {
					informacaoString = informacaoString + ", onde se lia ";
				}
				
				if(!inicio) {
					run2 = document.getParagraphs().get(5).insertNewRun(index);
					run2.setText(" e ");
					index++;
				} else {
					inicio = false;
				}
				run.setText(averbacao.getTipoAverbacao() + " ");
				run.setBold(true);
				index++;
				run2 = document.getParagraphs().get(5).insertNewRun(index);
				run2.setBold(false);
				run2.setText(informacaoString);
				if(CommonsUtil.mesmoValor(averbacao.getTipoAverbacao(), "INCLUIR")) {
					index++;
					run = document.getParagraphs().get(5).insertNewRun(index);
					run.setText(averbacao.getTexto1() + ",");
				} else {
					index++;
					run = document.getParagraphs().get(5).insertNewRun(index);
					run.setText(averbacao.getTexto1() + ", passará a CONSTAR " + averbacao.getTexto2() + ",");
					run.setBold(true);
				}
				index++;
			}
			
			XWPFParagraph paragrafo = document.getParagraphs().get(6);
			for(String documento : documentos) {
				run = paragrafo.createRun();
				if(CommonsUtil.mesmoValor(documento, "RG"))
					run.setText("- Cópia do " + documento);
				else
					run.setText("- Cópia da " + documento);
				run.addCarriageReturn();
			}

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			            
			            text = trocaValoresXWPF(text, r, "cidadeEmitente", (participante.getPessoa().getCidade()));    
			            text = trocaValoresXWPF(text, r, "ufEmitente", (participante.getPessoa().getEstado()));			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "cpfEmitente", (participante.getPessoa().getCpf()));
			            
			            text = trocaValoresXWPF(text, r, "cidadeImovel", objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroImovel", objetoCcb.getNumeroImovel());
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());						
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		   
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraAditamentoCartaDeDesconto() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			XWPFRun run3;
			XWPFTable table;
			XWPFTableRow tableRow1;
			XWPFTableRow tableRow2;
			int quadroResumo = 1;
			int anexo1Cabecalho = 2;
			int anexo1 = 3;
			int anexo2 = 4;
			int assinatura = 5;
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")
						|| CommonsUtil.mesmoValor(participante.getTipoParticipante(), "DEVEDOR FIDUCIANTE") ) {
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
				} 
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/AditamentoCartaDesconto.DOCX"));
			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			XWPFRun participantesCci = null;
			XWPFRun criarAnexoII = null;
			XWPFRun paragafo1AnexoII = null;
			XWPFRun paragafo2AnexoII = null;
			
			Date dataAditamento = DateUtil.gerarDataHoje();
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			             		
			            text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
						
						text = trocaValoresXWPF(text, r, "aditamentoDia", dataAditamento.getDate());
						text = trocaValoresXWPF(text, r, "aditamentoMes", CommonsUtil.formataMesExtenso(dataAditamento).toLowerCase());
						text = trocaValoresXWPF(text, r, "aditamentoAno", (dataAditamento.getYear() + 1900));

						text = trocaValoresXWPF(text, r, "numeroCCB", objetoCcb.getNumeroCcb());
						
						text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());
						
						if (text != null && text.contains("participantesCci")) {
							text = text.replace("participantesCci", "");
							r.setText(text, 0);
							participantesCci = r;
						}
						
						if (text != null && text.contains("criarAnexoII")) {
							text = text.replace("criarAnexoII", "");
							r.setText(text, 0);
							criarAnexoII = r;
						}
						if (text != null && text.contains("paragrafoAnexoII")) {
							text = text.replace("paragrafoAnexoII", "");
							r.setText(text, 0);
							paragafo1AnexoII = r;
						}
						if (text != null && text.contains("paragrafo2AnexoII")) {
							text = text.replace("paragrafo2AnexoII", "");
							r.setText(text, 0);
							paragafo2AnexoII = r;
						}
			        }
			    }
			}	
			
			XWPFParagraph paragraph = participantesCci.getParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {										
				run = paragraph.createRun();	
				run.addBreak();
				run.setFontSize(12);
				run.setText(RomanNumerals(iParticipante + 2) + " - ");
				run.setBold(true);
				run2 = paragraph.createRun();
				run.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
				//run2.setFontFamily("Calibri");
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();
				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();
					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else if(participante.getSocios().size() > 0){
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					} else {
						socios = "";
					}
					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);
					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}									
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					if(CommonsUtil.semValor(objetoCcb.getTipoPessoaEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setTipoPessoaEmitente("PF");
						} else {
							objetoCcb.setTipoPessoaEmitente("PJ");
						}
					}
					
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
					objetoCcb.setTipoParticipanteEmitente("DEVEDOR FIDUCIANTE");
				}
				run3 = paragraph.createRun();	
				run3.setFontSize(12);
				run3.setText(" (“" + participante.getTipoParticipante() + "”)");
				run3.setBold(true);
				run3.addBreak();
				iParticipante++;
			}
						
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);										
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", objetoCcb.getValorLiquidoCredito());							
								text = trocaValoresXWPFCci(text, r, "valorDespesas", objetoCcb.getValorDespesas(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", objetoCcb.getValorDespesas());		
								
								text = trocaValoresXWPF(text, r, "serieCcb", objetoCcb.getSerieCcb());
								text = trocaValoresXWPF(text, r, "numeroCCI", objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "numeroCCB", objetoCcb.getNumeroCcb());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (objetoCcb.getDataDeEmissao().getYear() + 1900));
									 		
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", objetoCcb.getNomeEmitente());
								
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		    
		    
		    tableRow2 = document.getTableArray(assinatura).getRow(1);

		    paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			if (objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
				@SuppressWarnings("unused")
				int qtdePessoasEsquerdo = 0;
				for (int iPartTab = 0; iPartTab < objetoCcb.getListaParticipantes().size(); iPartTab++) {
					CcbParticipantes participante = objetoCcb.getListaParticipantes().get(iPartTab);
					if(CommonsUtil.mesmoValor(participante, objetoCcb.getEmitentePrincipal()))
						continue;
					int cell = 0;
					if (qtdePessoasEsquerdo == 0) {
						cell = 0;
						qtdePessoasEsquerdo++;
					} else {
						cell = 1;
						qtdePessoasEsquerdo--;
					}
					run = tableRow2.getCell(cell).getParagraphArray(0).createRun();
					run.addBreak();
					run.setFontSize(12);
					run.setText("____________________________________   ");
					run.setBold(false);
					run.addBreak();

					run2 = tableRow2.getCell(cell).getParagraphArray(0).createRun();
					run2.setFontSize(12);
					run2.setText(participante.getPessoa().getNome());
					run2.setBold(true);
					run2.addBreak();

					run3 = tableRow2.getCell(cell).getParagraphArray(0).createRun();
					run3.setFontSize(12);
					run3.setText(participante.getTipoParticipante());
					run3.setBold(false);
					run3.addBreak();
				}
			}
			
			int indexParcela = 1;
			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(anexo1);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
			
			table = document.getTableArray(anexo1Cabecalho);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(objetoCcb.getDataDeEmissao(), objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(objetoCcb.getMontanteDFI(), "R$ "));
			
			geraPaginaContratoII(document, "9DC83E", false,
					criarAnexoII.getParagraph(), 
					paragafo1AnexoII.getParagraph(), 
					paragafo2AnexoII.getParagraph());
			
			
			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			
			XWPFTable tableAnexo2 = document.getTableArray(document.getTables().size() - 1);
			document.setTable(anexo2, tableAnexo2);
			for(int i = 0; i < document.getBodyElements().size(); i++) {
				if(CommonsUtil.mesmoValor(document.getBodyElements().get(i), tableAnexo2)) {
					document.removeBodyElement(i);
					break;
				}
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraDownloadByteArray(byte[] file, String fileName) throws JRException, IOException {
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		String nomeSemvirgula = objetoCcb.getNomeEmitente();
		if(nomeSemvirgula.contains(",")) {
			nomeSemvirgula = nomeSemvirgula.replace(",", "");
	    }
		gerador.open(String.format("Galleria Bank - "+fileName, ""));
		gerador.feed(new ByteArrayInputStream(file));
		gerador.close();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();		
		return bos.toByteArray();
	}
		
	
	private void geraPaginaContratoII(XWPFDocument document, String cor, boolean gerarAssinatura) throws IOException {
		geraPaginaContratoII(document, cor, gerarAssinatura, document.createParagraph(), document.createParagraph(), document.createParagraph());
	}

	private void geraPaginaContratoII(XWPFDocument document, String cor, boolean gerarAssinatura,
			XWPFParagraph paragraph, XWPFParagraph paragraph1, XWPFParagraph paragraph2) {
		XWPFRun run;
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(100);
		
		String documento = "";
		
		for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {				
			if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
				if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
					objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
				}
				
				if(CommonsUtil.semValor(objetoCcb.getCpfEmitente())) {
					if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
						objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						documento = "CPF: ";
					} else {
						objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						documento = "CNPJ: ";
					}
				}
			}
		}
		
		run = paragraph.createRun();
		run.setText("ANEXO II");
		run.setFontSize(11);
		run.setBold(true);
		XWPFRun run2 = paragraph.createRun();
		
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		
		run.addCarriageReturn();
		run.setText("CÉDULA DE CRÉDITO IMOBILIÁRIO Nº " + objetoCcb.getNumeroCcb());
		run.setFontSize(11);
		run.setBold(true);
		run.addCarriageReturn();
		run.setText("DESPESAS ACESSÓRIAS (DEVIDAS A TERCEIROS)");
		run.setFontSize(11);
		run.setBold(true);
		
		paragraph = paragraph1;
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(200);
		
		run = paragraph.createRun();
		run.setText("Para todos os fins e efeitos de direito, conforme previsto na ");
		run.setFontSize(11);
		run.setBold(false);
		
		run2 = paragraph.createRun();
		run2.setFontSize(11);
		run2.setText("cláusula 3.5 do Quadro Resumo da Cédula de Crédito Imobiliário n° " + objetoCcb.getNumeroCcb() 
			+ ", datada de " + CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy") );
		run2.setBold(true);
		
		run = paragraph.createRun();
		run.setText(" (CCI), autorizo o pagamento das despesas acessórias e dos "
				+ "compromissos diversos abaixo relacionados e aprovados por mim previamente no valor total de");
		run.setFontSize(11);
		run.setBold(false);			
		
		calcularValorDespesa();

		valorPorExtenso.setNumber(objetoCcb.getValorDespesas()); 
		run2 = paragraph.createRun();
		run2.setFontSize(11);
		run2.setText(" "+ CommonsUtil.formataValorMonetario(objetoCcb.getValorDespesas(), "R$ ") + " ("+ valorPorExtenso.toString() +"), ");
		run2.setBold(true);
		
		run = paragraph.createRun();
		run.setText("por meio do crédito oriundo da CCI. O montante total necessário para o pagamento"
				+ " das despesas acessórias e dos compromissos diversos será transferido para a conta"
				+ " da Galleria Correspondente Bancário Sociedade Unipessoal Ltda, CNPJ 34.787.885/0001-32, Banco do Brasil"
				+ " – Ag: 1515-6 C/C: 131094-1, que, na condição de Correspondente Bancário da Galleria Sociedade de Crédito Direto,"
				+ " será a responsável por efetuar todos os pagamentos devidamente especificados na"
				+ " tabela abaixo:");
		run.setFontSize(11);
		run.setBold(false);	
		
		XWPFTable table = document.createTable();
		table.setWidth((int) (6.1 * 1440));
		table.getCTTbl().getTblPr().getTblW().unsetType();
		setTableAlign(table, ParagraphAlignment.CENTER);

		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));
		
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		
		// create first row
		XWPFTableRow tableRow = table.getRow(0);

		tableRow.getCell(0).setParagraph(paragraph);
		tableRow.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		tableRow.getCell(0).setColor(cor);
		//tableRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(3000) ));
		run = tableRow.getCell(0).getParagraphArray(0).createRun();
		run.setFontSize(12);
		run.setBold(true);
		run.setColor("ffffff");
		run.setText("Descrição da despesa ou do Compromisso Diverso");
		
		tableRow.addNewTableCell();
		tableRow.addNewTableCell();

		tableRow.getCell(1).setParagraph(paragraph);
		tableRow.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		tableRow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
		tableRow.getCell(1).setColor(cor);
		run = tableRow.getCell(1).getParagraphArray(0).createRun();
		run.setFontSize(12);
		run.setBold(true);
		run.setText("Forma de Pagamento");
		run.setColor("ffffff");	
		
		tableRow.getCell(2).setParagraph(paragraph);
		tableRow.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
		tableRow.getCell(2).setColor(cor);
		tableRow.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		run = tableRow.getCell(2).getParagraphArray(0).createRun();
		run.setFontSize(12);
		run.setBold(true);
		run.setColor("ffffff");
		run.setText("Valor");
				
		for(ContasPagar despesa : objetoCcb.getDespesasAnexo2()) {
			XWPFTableRow tableRow1 = table.createRow();
			
			tableRow1.getCell(0).setParagraph(paragraph);
			tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Cartório") || CommonsUtil.mesmoValor(despesa.getDescricao(), "Registro")) {
				run.setText("Custas Cartorárias");
			} else if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Certidão de Casamento")) {
				run.setText("Certidão de estado civil");
			} else if(CommonsUtil.mesmoValor(despesa.getDescricao(), "IPTU")) {
				run.setText("IPTU em Atraso");
			} else if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Condomínio")) {
				run.setText("Condomínio em Atraso");
			} else {
				run.setText(despesa.getDescricao());
			}
			
			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			if(CommonsUtil.mesmoValor(despesa.getFormaTransferencia(), "TED")) {
				run.setText("Ted no "+ despesa.getBancoTed() +" AG: "+ despesa.getAgenciaTed()
				+" C/C: "+ despesa.getContaTed() + " Chave Pix:" + despesa.getPix() + " " + despesa.getNomeTed() 
				+" CPF/CNPJ: "+ despesa.getCpfTed()); 
			} else {
				run.setText(despesa.getFormaTransferencia());
			}
			run.setColor("000000");
			if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Crédito CCI")) {
				run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run2.addBreak();
				run2.addBreak();
				run2.setText("* Credito será efetuado somente no registro da alienação Fiduciária da CCI " + objetoCcb.getNumeroCcb() 
						+ " da matricula " + objetoCcb.getNumeroImovel() + " do "+ objetoCcb.getCartorioImovel() 
						+ "° Cartório de Registro de Imóveis de " + objetoCcb.getCidadeImovel() + " - " + objetoCcb.getUfImovel() + "* ");
				run2.setColor("FF0000");
			}
			
			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			run.setText(CommonsUtil.formataValorMonetario(despesa.getValor(), "R$ "));
		}
		
		for(CcbProcessosJudiciais processo : objetoCcb.getProcessosJucidiais()) {
			ContasPagar despesa = processo.getContaPagar();
			if( !processo.isSelecionadoComite() || CommonsUtil.semValor(despesa)) {
				continue;
			}
			
			XWPFTableRow tableRow1 = table.createRow();
			
			tableRow1.getCell(0).setParagraph(paragraph);
			tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText(despesa.getDescricao());
			run.setColor("000000");
			
			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText(despesa.getFormaTransferencia());
			run.setColor("000000");
			
			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			run.setText(CommonsUtil.formataValorMonetario(despesa.getValor(), "R$ "));
		}
		
		
		if (gerarAssinatura) {
			XWPFTableRow tableRow1 = table.createRow();

			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW()
					.setW(BigInteger.valueOf(CommonsUtil.longValue(2800)));
			tableRow1.getCell(1).setColor(cor);

			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setText("Total");
			run.setColor("ffffff");

			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW()
					.setW(BigInteger.valueOf(CommonsUtil.longValue(2800)));
			tableRow1.getCell(2).setColor(cor);

			run = tableRow1.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("ffffff");
			run.setBold(true);
			run.setText(CommonsUtil.formataValorMonetario(objetoCcb.getValorDespesas(), "R$ "));

			paragraph = paragraph2;
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);

			run = paragraph.createRun();
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();

			run.setText("_____________________________________________________________________________");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();

			run2 = paragraph.createRun();
			run2.setColor("000000");
			run2.setFontSize(12);
			run2.setText("" + objetoCcb.getNomeEmitente().toUpperCase());
			run2.setBold(true);
			run2.addCarriageReturn();
			run2.setText(documento + objetoCcb.getCpfEmitente());
			
			if (!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
				ContratoCobranca contrato = objetoCcb.getObjetoContratoCobranca();
				contrato.setContaPagarValorTotal(objetoCcb.getValorDespesas());
			}
		}
	}

	private void geraParagrafoPF(XWPFRun run2, CcbParticipantes participante){
		if(participante.isEmpresa())
			return;
		run2.setFontSize(12);
		String filho;
		String nacionalidade = null;
		String estadoCivilStr = "";
		String conjugeStr = "";
		PagadorRecebedor pessoa = participante.getPessoa();
		
		if(participante.isFeminino()) {
			filho = "filha";
			if (CommonsUtil.mesmoValor(participante.getNacionalidade(), "brasileiro")) 
				nacionalidade = "brasileira";
			if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "CASADO")) {
				estadoCivilStr = "casada";
			} else {
				if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SOLTEIRO"))
					estadoCivilStr = "solteira";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "VIÚVO"))
					estadoCivilStr = "viúva";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "DIVORCIADO"))
					estadoCivilStr = "divorciada";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO"))
					estadoCivilStr = "separada";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO JUDICIALMENTE"))
					estadoCivilStr = "separada judicialmente";
			}
		} else {
			filho = "filho";
			nacionalidade = participante.getNacionalidade();
			estadoCivilStr = pessoa.getEstadocivil().toLowerCase();
		}
		
		if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "CASADO")) {
			if(!CommonsUtil.semValor(pessoa.getDataCasamento())) {
				estadoCivilStr = estadoCivilStr + " em " + CommonsUtil.formataData(pessoa.getDataCasamento(), "dd/MM/yyyy");
			}
			
			if(!CommonsUtil.mesmoValor(pessoa.getRegimeCasamento(), "parcial de bens")) {
				conjugeStr = ", sob o regime " + pessoa.getRegimeCasamento() + ", na vigência da lei 6.515/77 (" + 
					pessoa.getNomeConjuge() + " " + pessoa.getCpfConjuge() + "), conforme pacto antenupcial registrado no "+
					pessoa.getRegistroPactoAntenupcial() + ", sob livro " + pessoa.getLivroPactoAntenupcial() + ", folhas " + 
					pessoa.getFolhasPactoAntenupcial() + ", datada de " + CommonsUtil.formataData(pessoa.getDataPactoAntenupcial()) ;
			} else {
				conjugeStr = ", sob o regime " + pessoa.getRegimeCasamento() + ", na vigência da lei 6.515/77 (" + 
						pessoa.getNomeConjuge() + " " + pessoa.getCpfConjuge() + ")" ;
			}
		} else {
			if(participante.isUniaoEstavel()) {
				estadoCivilStr = estadoCivilStr + " convivente em união estável";
			} else {
				estadoCivilStr = estadoCivilStr + " não convivente em união estável";
			}
		}
		String rgCnhString = "";
		if(CommonsUtil.mesmoValor(pessoa.getTipoDocumento(), "RG")){
			rgCnhString = "RG";
		} else if(CommonsUtil.mesmoValor(pessoa.getTipoDocumento(), "CNH")){
			rgCnhString = "CNH";
		}
		
		
		run2.setText( filho + " de " + pessoa.getNomeMae() + " e " + pessoa.getNomePai() + ", "
				+ nacionalidade + ", "+ pessoa.getAtividade() + ", "+ estadoCivilStr 
				+ conjugeStr + ","
				+ " portador(a) da Cédula de Identidade " + rgCnhString + " nº "+ pessoa.getRg() + " " + pessoa.getOrgaoEmissorRG() + ","
				+ " inscrito(a) no CPF/MF sob o nº "+ pessoa.getCpf() +", endereço eletrônico: "+ pessoa.getEmail() +","
				+ " residente e domiciliado à "+ pessoa.getEndereco() +", nº "+ pessoa.getNumero() +", "
				+ pessoa.getComplemento() + ", "+ pessoa.getBairro() + ", " 
				+ pessoa.getCidade()+"/"+pessoa.getEstado()+", CEP "+ pessoa.getCep()+"; ");
	}
	
	private void organizaSegurados(List<CcbParticipantes> segurados) {
		if(segurados.size() <= 0) {
			return;
		}
		BigDecimal porcentagem =  BigDecimal.valueOf(100).divide(BigDecimal.valueOf(segurados.size()), MathContext.DECIMAL128).setScale(2, BigDecimal.ROUND_HALF_UP);
		if(objetoCcb.getListSegurados().size() == segurados.size()) {
			return;
		}
		
		SeguradoDAO seguradoDAO = new SeguradoDAO();
		objetoCcb.getListSegurados().clear();
		if(objetoCcb.getObjetoContratoCobranca().getListSegurados().size() == segurados.size()) {
			for(Segurado segurado : objetoCcb.getObjetoContratoCobranca().getListSegurados()) {
				objetoCcb.getListSegurados().add(segurado);
			}
		} else {
			for(Segurado segurado : objetoCcb.getObjetoContratoCobranca().getListSegurados()) {
				objetoCcb.getObjetoContratoCobranca().getListSegurados().remove(segurado);
				segurado.setContratoCobranca(null);
				seguradoDAO.delete(segurado);
			}
			objetoCcb.getObjetoContratoCobranca().getListSegurados().clear();
		
			for(CcbParticipantes participante : segurados) {
				Segurado segurado = new Segurado();
				if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
					segurado.setPessoa(participante.getPessoa());
					segurado.setPorcentagemSegurador(porcentagem);
					segurado.setPosicao(objetoCcb.getListSegurados().size() + 1);
					if(!objetoCcb.getObjetoContratoCobranca().getListSegurados().contains(segurado)) {		
						segurado.setContratoCobranca(objetoCcb.getObjetoContratoCobranca());
						objetoCcb.getObjetoContratoCobranca().getListSegurados().add(segurado);
					}
					if(!objetoCcb.getListSegurados().contains(segurado)) {	
						
						seguradoDAO.create(segurado);
						objetoCcb.getListSegurados().add(segurado);
					}
				}
			}
		}
	}

	public void populateFiles(int index) throws IOException {
		uploadedFile = filesList.get(index);
		fileName = uploadedFile.getFileName();
	    fileType = uploadedFile.getContentType();
	    if(fileType.contains("png")) {
	    	fileTypeInt = 6;
	    	fileType = "png";
	    } else if(fileType.contains("jpeg")) {
	    	fileTypeInt = 5;
	    	fileType = "jpeg";
	    }
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    if (uploadedFile != null) {
			RenderedImage picture = ImageIO.read((uploadedFile.getInputstream()));
			ImageIO.write(picture, "png", baos);
			baos.flush();
			// InputStream is = new ByteArrayInputStream(baos.toByteArray());
			baos.close();
			this.bis = new ByteArrayInputStream(baos.toByteArray());
		}
	}
	
	public String estadoPorExtenso(String uf) {
		if(!CommonsUtil.semValor(uf)) {
			if(CommonsUtil.mesmoValor(uf, "AC")) {
				return "Acre";
			} else if(CommonsUtil.mesmoValor(uf, "AL")) {
				return "Alagoas";
			} else if(CommonsUtil.mesmoValor(uf, "AP")) {
				return "Amapá";
			} else if(CommonsUtil.mesmoValor(uf, "AM")) {
				return "Amazonas";
			} else if(CommonsUtil.mesmoValor(uf, "BA")) {
				return "Bahia";
			} else if(CommonsUtil.mesmoValor(uf, "CE")) {
				return "Ceará";
			} else if(CommonsUtil.mesmoValor(uf, "DF")) {
				return "Distrito Federal";
			} else if(CommonsUtil.mesmoValor(uf, "ES")) {
				return "Espírito Santo";
			} else if(CommonsUtil.mesmoValor(uf, "GO")) {
				return "Goiás";
			} else if(CommonsUtil.mesmoValor(uf, "MA")) {
				return "Maranhão";
			} else if(CommonsUtil.mesmoValor(uf, "MT")) {
				return "Mato Grosso";
			} else if(CommonsUtil.mesmoValor(uf, "MS")) {
				return "Mato Grosso";
			} else if(CommonsUtil.mesmoValor(uf, "MG")) {
				return "Minas Gerais";
			} else if(CommonsUtil.mesmoValor(uf, "PA")) {
				return "Pará";
			} else if(CommonsUtil.mesmoValor(uf, "PB")) {
				return "Paraíba";
			} else if(CommonsUtil.mesmoValor(uf, "PR")) {
				return "Paraná";
			} else if(CommonsUtil.mesmoValor(uf, "PE")) {
				return "Pernambuco";
			} else if(CommonsUtil.mesmoValor(uf, "PI")) {
				return "Piauí";
			} else if(CommonsUtil.mesmoValor(uf, "RJ")) {
				return "Rio de Janeiro";
			} else if(CommonsUtil.mesmoValor(uf, "RN")) {
				return "Rio Grande do Norte";
			} else if(CommonsUtil.mesmoValor(uf, "RS")) {
				return "Rio Grande do Sul";
			} else if(CommonsUtil.mesmoValor(uf, "RO")) {
				return "Rondônia";
			} else if(CommonsUtil.mesmoValor(uf, "RR")) {
				return "Roraima";
			} else if(CommonsUtil.mesmoValor(uf, "SC")) {
				return "Santa Catarina";
			} else if(CommonsUtil.mesmoValor(uf, "SP")) {
				return "São Paulo";
			} else if(CommonsUtil.mesmoValor(uf, "SE")) {
				return "Sergipe";
			} else if(CommonsUtil.mesmoValor(uf, "TO")) {
				return "Tocantins";
			} else {
				return uf;
			}
		}
		return "";
	}

	public void calcularValorDespesa() {
		BigDecimal total =  BigDecimal.ZERO;
		
		if(!objetoCcb.getDespesasAnexo2().isEmpty()) {
			for(ContasPagar despesas : objetoCcb.getDespesasAnexo2()) {
				if(!CommonsUtil.semValor(despesas.getValor()))
					total = total.add(despesas.getValor());
			}
		}
		
		if(!objetoCcb.getProcessosJucidiais().isEmpty()) {
			for(CcbProcessosJudiciais processo : objetoCcb.getProcessosJucidiais()) {
				if(!CommonsUtil.semValor(processo.getValorAtualizado()))
					total = total.add(processo.getValorAtualizado());
			}
		}
		objetoCcb.setValorDespesas(total);
	}
	
	public void adicionarEnter(String text, XWPFRun r) {
		if (text != null && text.contains("\n")) {
			String[] lines = text.split("\n");
			r.setText(lines[0], 0); // set first line into XWPFRun
			for (int i = 1; i < lines.length; i++) {
				// add break and insert new text
				r.addBreak();
				r.setText(lines[i]);
			}
		} else {
			r.setText(text, 0);
		}
	}
	
	public void setTableAlign(XWPFTable table,ParagraphAlignment align) {
	    CTTblPr tblPr = table.getCTTbl().getTblPr();
	    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
	    STJc.Enum en = STJc.Enum.forInt(align.getValue());
	    jc.setVal(en);
	}
	
	public void setTableAlignment(XWPFTable table, STJc.Enum justification) {
	    CTTblPr tblPr = table.getCTTbl().getTblPr();
	    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
	    jc.setVal(justification);
	}

	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, String valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			text = text.replace(valorEscrito, valorSobrescrever);
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever, String moeda) {
		if (text != null && text.contains(valorEscrito)) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetario(valorSobrescrever, moeda));
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPFCci(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever, String moeda) {
		if (text != null && text.contains(valorEscrito)) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetarioCci(valorSobrescrever, moeda) );
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			if(CommonsUtil.mesmoValor(valorEscrito, "tarifaAntecipada")) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetario(valorSobrescrever));
			} else {
				text = text.replace(valorEscrito, CommonsUtil.formataValorTaxa(valorSobrescrever));
			}
			r.setText(text, 0);
		}
		return text;
	}

	public String trocaValoresDinheiroExtensoXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito)) {
			if(CommonsUtil.semValor(valorSobrescrever)) {
				text = text.replace("Extenso" + valorEscrito , "Zero reais");
				r.setText(text, 0);
			} else {
				valorPorExtenso.setNumber(valorSobrescrever);
				text = text.replace("Extenso" + valorEscrito , valorPorExtenso.toString());
				r.setText(text, 0);	
			}	
		}
		return text;
	}
	
	public String trocaValoresTaxaExtensoXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito)) {
			if(CommonsUtil.semValor(valorSobrescrever)) {
				text = text.replace("Extenso" + valorEscrito, "Zero");
			} else {
				porcentagemPorExtenso.setNumber(valorSobrescrever);
				text = text.replace("Extenso" + valorEscrito, porcentagemPorExtenso.toString());
				
			}
		}
		r.setText(text, 0);
		return text;
	}
	
	public String trocaValoresNumeroExtensoXWPF(String text, XWPFRun r, String valorEscrito, String valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito )) {
			numeroPorExtenso.setNumber(BigDecimal.valueOf(CommonsUtil.doubleValue(valorSobrescrever)));
			text = text.replace("Extenso" + valorEscrito , numeroPorExtenso.toString());
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, Date valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			text = text.replace(valorEscrito, CommonsUtil.formataData(valorSobrescrever, "dd/MM/yyyy"));
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, Integer valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			text = text.replace(valorEscrito, CommonsUtil.stringValue(valorSobrescrever));
			r.setText(text, 0);
		}
		return text;
	}

	private void CabecalhoAnexo1(XWPFTable table, int r, int c, String text) {
		XWPFRun run;
		XWPFTableRow tableRow1;
		tableRow1 = table.getRow(r);
		run = tableRow1.getCell(c).getParagraphArray(0).createRun();
		run.setFontSize(8);
		run.setFontFamily("Calibri");
		run.setBold(true);
		run.setText(text);
	}

	/////////////////////////////////////////Daqui pra frente é só pra trás////////////////////////////////////////////////////////////////////
    private boolean fiducianteGerado = false;
    private boolean devedorGerado = false;
	
	public byte[] geraCcbDinamica() throws IOException {
		clearDocumentosNovos();
		try {
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun runHeader = paragraphHeader.createRun();
			runHeader.addPicture(getClass().getResourceAsStream("/resource/BMP MoneyPlus.png"), 6, "BMP MoneyPlus",
					Units.toEMU(130), Units.toEMU(72));
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.setText("VIA NEGOCIÁVEL");
			runHeader.setFontSize(12);
			runHeader.setColor("0000ff");
			runHeader.setBold(true);

			XWPFRun run;

			XWPFParagraph paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("CÉDULA DE CRÉDITO BANCÁRIO");
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();
			run.addCarriageReturn();

			run.setText("Nº " + objetoCcb.getNumeroCcb());
			run.setFontSize(14);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();

			fazParagrafoSimples(document, paragraph, run, "1.  Partes:", true);

			geraParagrafoComposto(document, paragraph, run, run2,
					"I – CREDOR: BMP SOCIEDADE DE CRÉDITO DIRETO S.A.",
					", instituição financeira, inscrita no CNPJ/MF sob nº 34.337.707/0001-00,"
						+ " com sede na Av. Paulista, 1765, 1º Andar, CEP 01311-200, São Paulo, SP,"
						+ " neste ato, representada na forma do seu Estatuto Social; ",
					true, false);

			int iParticipante = 2;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);

				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(RomanNumerals(iParticipante) + " – " + participante.getTipoParticipante() + ":");
				run.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
				run.setBold(true);

				run2 = paragraph.createRun();
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();
				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();

					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else if(participante.getSocios().size() > 0){
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					} else {
						socios = "";
					}

					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);

					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}
				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					
					if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
				}
				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					objetoCcb.setTerceiroGarantidor(true);
				}

				iParticipante++;
			}

			fazParagrafoSimples(document, paragraph, run, "Considerando que: ", false);
			
			if(objetoCcb.isTerceiroGarantidor()) {
				fazParagrafoSimples(document, paragraph, run,
						"a)	O EMITENTE e o TERCEIRO(S) GARANTIDOR(ES) declara(m) e garante(m) "
						+ "que está(ão) devidamente autorizado a firmar a presente Cédula de "
						+ "Crédito Bancário (“CCB”), e assumir todas as obrigações aqui pactuadas"
						+ " e cumprir todos os seus termos e condições até quitação final de todas"
						+ " as obrigações aqui estabelecidas, uma vez que as obrigações pecuniárias"
						+ " assumidas nesta CCB são compatíveis com a capacidade econômico-financeira "
						+ "do EMITENTE para honrá-las;",
						false, ParagraphAlignment.BOTH);
			} else {
				fazParagrafoSimples(document, paragraph, run,
						"a) O EMITENTE declara e garante que está devidamente "
								+ "autorizado a firmar a presente Cédula de Crédito Bancário (“CCB”),"
								+ " e assumir todas as obrigações aqui pactuadas e cumprir todos os "
								+ "seus termos e condições até quitação final de todas as obrigações aqui "
								+ "estabelecidas, uma vez que as obrigações pecuniárias assumidas "
								+ "nesta CCB são compatíveis com a capacidade econômico-financeira do"
								+ " EMITENTE para honrá-las;",
						false, ParagraphAlignment.BOTH);
			}

			fazParagrafoSimples(document, paragraph, run, "b) O EMITENTE declara e garante que cumpre o disposto na"
					+ " legislação referente à Política Nacional de Meio Ambiente"
					+ " e não aplicará os recursos decorrentes desta CCB no financiamento "
					+ "de qualquer atividade ou projeto que caracterize crime contra o"
					+ " meio ambiente, que cause poluição e/ou que prejudique o ordenamento"
					+ " urbano e o patrimônio cultural, obrigando-se a respeitar integralmente"
					+ " as normas contidas nas Leis nº 9.605/98 e nº 9.985/2000 e demais"
					+ " regras complementares; e ainda que não utilizará os recursos no "
					+ "desenvolvimento de suas atividades comerciais e vinculadas ao seu objeto"
					+ " social, formas nocivas ou de exploração de trabalho forçado e/ou mão" + " de obra infantil.",
					false, ParagraphAlignment.BOTH);

			fazParagrafoSimples(document, paragraph, run,
					"Em garantia do integral cumprimento de todas as obrigações,"
							+ " principais e acessórias, assumidas pelo EMITENTE, as Partes"
							+ " resolvem celebrar a presente Cédula de Crédito Bancário, a qual"
							+ " se regerá pelas seguintes cláusulas e condições: ",
					false, ParagraphAlignment.BOTH);

			fazParagrafoSimples(document, paragraph, run, "2.	DAS CARACTERÍSTICAS DA OPERAÇÃO DE CRÉDITO", true);

			valorPorExtenso.setNumber(objetoCcb.getValorCredito());
			geraParagrafoComposto(document, paragraph, run, run2, "2.1. Valor do Crédito: ",
					CommonsUtil.formataValorMonetario(objetoCcb.getValorCredito(), "R$ ") + " (" + valorPorExtenso.toString() + ");",
					true, false);

			valorPorExtenso.setNumber(objetoCcb.getCustoEmissao());
			geraParagrafoComposto(document, paragraph, run, run2, "2.1.1. Custo de Emissão: ",
					CommonsUtil.formataValorMonetario(objetoCcb.getCustoEmissao(), "R$ ") + " (" + valorPorExtenso.toString()
							+ "), e será pago pelo EMITENTE na data"
							+ " de emissão desta CCB, sendo o mesmo deduzido no ato da liberação do recurso"
							+ " que entrará a crédito na Conta Corrente descrita no item 2.5 desta CCB, e"
							+ " será devido por conta da guarda, manutenção e atualização de dados cadastrais,"
							+ " bem como permanente e contínua geração de dados relativos ao cumprimento dos"
							+ " direitos e obrigações decorrentes deste instrumento;",
					true, false);

			valorPorExtenso.setNumber(objetoCcb.getValorIOF());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.1.2. Valor do Imposto sobre Operações Financeiras (IOF): ",
					CommonsUtil.formataValorMonetario(objetoCcb.getValorIOF(), "R$ ") + " (" + valorPorExtenso.toString()
							+ "), conforme apurado na Planilha"
							+ " de Cálculo (Anexo I), calculado nos termos da legislação vigente"
							+ " na data de ocorrência do fato gerador, tendo como base de cálculo"
							+ " o Valor do Crédito mencionado no item 2.1;",
					true, false);

			valorPorExtenso.setNumber(objetoCcb.getValorDespesas());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.1.3. Valor destinado ao pagamento de despesas acessórias (devidas a terceiros): ",
					CommonsUtil.formataValorMonetario(objetoCcb.getValorDespesas(), "R$ ") + " (" + valorPorExtenso.toString() + ") conforme anexo II;", true,
					false);
			
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.1.3.1 ", "Os valores mencionados no ANEXO II expressam estimativas,"
							+ " sendo que caso haja necessidade de complementação para quitação,"
							+ " o(a) EMITENTE autoriza desde já e independentemente de notificação,"
							+ " que seja realizado o desconto destes valores do montante líquido a"
							+ " ser liberado, bem como, caso os valores sejam menores no momento da"
							+ " quitação dos débitos, o CREDOR irá realizar o depósito da diferença"
							+ " na Conta indicada no item 2.5.", true, false);

			valorPorExtenso.setNumber(objetoCcb.getValorLiquidoCredito());
			geraParagrafoComposto(document, paragraph, run, run2, "2.1.4. Valor Líquido do Crédito: ",
					"O valor líquido do crédito concedido é de "
							+ CommonsUtil.formataValorMonetario(objetoCcb.getValorLiquidoCredito(), "R$ ") + "" + " ("
							+ valorPorExtenso.toString() + "), após o desconto do Custo de Emissão,"
							+ " IOF e Despesas Acessórias desta CCB;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.1.5.",
					" O EMITENTE está ciente e concorda que é de sua responsabilidade"
							+ " o pagamento dos valores indicados nos itens supramencionados, bem "
							+ "como os relativos aos tributos e demais despesas que incidam ou venham"
							+ " a incidir sobre a operação, inclusive as que façam necessária para o "
							+ "registro da garantia real perante a circunscrição imobiliária competente.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.1.6.",
					" O EMITENTE concorda que o valor relativo ao IOF será incorporado à"
							+ " sua dívida confessada, sendo pago nos mesmos termos do parcelamento"
							+ " do saldo devedor em aberto.",
					true, false);

			fazParagrafoSimplesSemReturn(document, paragraph, run, "2.2.	Encargos Financeiros:", true);

			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "(X) Pré-fixado,",
					" calculado com base no ano de 365 dias;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "(X) Pós-fixado: ",
					"atualização dos valores pela variação mensal do Índice Nacional "
							+ "de Preços ao Consumidor Amplo – IPCA/IBGE, apurado a partir da data"
							+ " de emissão até a efetiva quitação da CCB, sendo esta atualização "
							+ "condição essencial do presente negócio, que o saldo devedor e o valor"
							+ " de cada uma das parcelas serão atualizados monetária e mensalmente, de"
							+ " acordo com o índice de atualização referido;",
					true, false);

			fazParagrafoSimplesSemReturn(document, paragraph, run, "2.3. Taxa de Juros Efetiva: ", true);

			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "Mês: ",
					CommonsUtil.formataValorTaxa(objetoCcb.getTaxaDeJurosMes()) + "%", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "Ano: ",
					CommonsUtil.formataValorTaxa(objetoCcb.getTaxaDeJurosAno()) + "%", true, false);

			fazParagrafoSimplesSemReturn(document, paragraph, run, "2.4. Custo Efetivo Total (“CET”):", true);

			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "Mês: ", CommonsUtil.formataValorTaxa(objetoCcb.getCetMes()) + "%",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "Ano: ", CommonsUtil.formataValorTaxa(objetoCcb.getCetAno()) + "%",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.5. Forma de Liberação do Crédito: ",
					"O CREDOR realizará o crédito na Conta Corrente nº " + objetoCcb.getContaCorrente() + "," + " Agência nº "
							+ objetoCcb.getAgencia() + ", BANCO " + objetoCcb.getNumeroBanco() + " – " + objetoCcb.getNomeBanco() + ", em até 5 (cinco)"
							+ " dias úteis após o cumprimento das condições precedentes estabelecidas "
							+ "na cláusula 4.4 abaixo;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.6. Forma de pagamento: ",
					"O EMITENTE realizará o pagamento, nos termos do Anexo "
							+ "I desta CCB, em conta corrente do CREDOR ou a quem este indicar; ",
					true, false);

			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(objetoCcb.getNumeroParcelasPagamento()));
			valorPorExtenso.setNumber(objetoCcb.getMontantePagamento());
			geraParagrafoComposto(document, paragraph, run, run2, "2.7. Fluxo de Pagamento (Juros e Amortização): ",
					objetoCcb.getNumeroParcelasPagamento() + " (" + numeroPorExtenso.toString() + ")"
							+ " parcelas mensais, sendo a 1ª parcela com vencimento em "
							+ CommonsUtil.formataData(objetoCcb.getVencimentoPrimeiraParcelaPagamento(), "dd/MM/yyyy")
							+ " e a última com vencimento " + "em "
							+ CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy")
							+ ", corrigidas pela variação"
							+ " mensal do IPCA/IBGE, totalizando, na data de emissão desta CCB," + " o montante de "
							+ CommonsUtil.formataValorMonetario(objetoCcb.getMontantePagamento(), "R$ ") + " ("
							+ valorPorExtenso.toString() + "), conforme ANEXO I;",
					true, false);

			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(objetoCcb.getNumeroParcelasMIP()));
			valorPorExtenso.setNumber(objetoCcb.getMontanteMIP());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.7.1. Valor e Fluxo de Pagamento do Seguro de Morte e Invalidez Permanente (MIP): ",
					objetoCcb.getNumeroParcelasMIP() + " (" + numeroPorExtenso.toString() + ") parcelas mensais,"
							+ " sendo a 1ª parcela com vencimento em "
							+ CommonsUtil.formataData(objetoCcb.getVencimentoPrimeiraParcelaMIP(), "dd/MM/yyyy") + " "
							+ "e a última com vencimento em "
							+ CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaMIP(), "dd/MM/yyyy") + ", corrigidas"
							+ " pela variação mensal do IPCA/IBGE, totalizando, na data de emissão "
							+ "desta CCB, o montante de " + CommonsUtil.formataValorMonetario(objetoCcb.getMontanteMIP(), "R$ ") + " ("
							+ valorPorExtenso.toString() + "), conforme ANEXO I. ",
					true, false);

			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(objetoCcb.getNumeroParcelasDFI()));
			valorPorExtenso.setNumber(objetoCcb.getMontanteDFI());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.7.2. Valor e Fluxo de Pagamento do Seguro de Danos Físicos ao Imóvel (DFI): ",
					objetoCcb.getNumeroParcelasDFI() + " (" + numeroPorExtenso.toString() + ") parcelas"
							+ " mensais, sendo a 1ª parcela com vencimento em "
							+ CommonsUtil.formataData(objetoCcb.getVencimentoPrimeiraParcelaDFI(), "dd/MM/yyyy") + " "
							+ "e a última com vencimento em "
							+ CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaDFI(), "dd/MM/yyyy") + ", corrigidas pela"
							+ " variação mensal do IPCA/IBGE, totalizando, na data de emissão desta CCB,"
							+ " o montante de " + CommonsUtil.formataValorMonetario(objetoCcb.getMontanteDFI(), "R$ ") + " ("
							+ valorPorExtenso.toString() + "), conforme ANEXO I.",
					true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.7.3. Tarifa mensal de administração do contrato: ","Será cobrado"
							+ " mensalmente o valor de R$ 25,00 (vinte e cinco reais)"
							+ " a título de tarifa para administração do contrato.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.8. ",
					"A atualização pela variação mensal do Índice Nacional"
							+ " de Preços ao Consumidor Amplo – IPCA/IBGE será devida"
							+ " desde o momento da emissão desta CCB, independentemente "
							+ "da data ajustada para o pagamento da 1ª parcela.",
					true, false);

			porcentagemPorExtenso.setNumber(objetoCcb.getTarifaAntecipada());
			String tarifaAntecipadastr = porcentagemPorExtenso.toString();
			if(CommonsUtil.semValor(objetoCcb.getTarifaAntecipada())) {
				tarifaAntecipadastr = "Zero";
			}
			geraParagrafoComposto(document, paragraph, run, run2, "2.9. Tarifa de Liquidação Antecipada: ",
					CommonsUtil.formataValorTaxa(objetoCcb.getTarifaAntecipada()) + "% (" + tarifaAntecipadastr + " por cento);",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.10. Data de Emissão: ",
					CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy") + ";", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.11. Data de Vencimento: ",
					CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy") + ";", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.12. Praça de Pagamento: ", "São Paulo/SP.", true,
					false);

			fazParagrafoSimples(document, paragraph, run, "3. DAS GARANTIAS", true);

			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.1. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			if(objetoCcb.isTerceiroGarantidor()) {
				run2.setText("Em garantia do fiel, integral e pontual cumprimento "
						+ "de todas as obrigações assumidas na presente CCB, o(s) TERCEIRO(S)"
						+ " GARANTIDOR(ES) aliena(m) fiduciariamente ao CREDOR o(s) bem(ens) "
						+ "imóvel(eis), de sua propriedade, bem(ns) com a(s) seguinte(s) "
						+ "descrição(ões):");
			} else {
				run2.setText("Em garantia do fiel, integral e pontual "
						+ "cumprimento de todas as obrigações assumidas na presente CCB,"
						+ " o EMITENTE aliena fiduciariamente ao CREDOR o(s) bem(ens)"
						+ " imóvel(eis), de sua propriedade, bem(ns) com a(s) seguinte(s) "
						+ "descrição(ões):");
			}
			run2.setBold(false);

			int iImagem = 0;
			for (UploadedFile imagem : filesList) {
				run3 = paragraph.createRun();
				run3.addCarriageReturn();
				this.populateFiles(iImagem);
				run3.addPicture(bis, fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
				run3.addCarriageReturn();
				iImagem++;
			}

			run4 = paragraph.createRun();
			run4.setFontSize(12);
			run4.removeCarriageReturn();
			run4.setText("Objeto da matrícula nº " + objetoCcb.getNumeroImovel() + " "
					+ "(“Bem Imóvel” ou “Imóvel”), registrada perante o " + objetoCcb.getCartorioImovel()
					+ "° Cartório de Registro de Imóveis da " + "Comarca de " + objetoCcb.getCidadeImovel() + " – " + objetoCcb.getUfImovel()
					+ " (“RGI”), nos termos" + " e condições anuídos pelas Partes no Instrumento Particular "
					+ "de Alienação Fiduciária Bem Imóvel (“Termo de Garantia”), o "
					+ "qual faz parte desta CCB como parte acessória e inseparável.");
			run4.addCarriageReturn();

			geraParagrafoComposto(document, paragraph, run, run2, "3.2. ",
					"Se solteiro(a), viúvo(a), divorciado(a) ou separado(a) "
							+ "judicialmente, declara, sob responsabilidade civil e criminal, "
							+ "que o imóvel aqui objetivado não foi adquirido na constância de "
							+ "união estável prevista na Lei nº 9.278, de 10/05/96 e no Código Civil, "
							+ "razão pela qual é seu único e exclusivo proprietário.",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "3.3.	Seguros:", true);

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.1. ",
					"O CREDOR Fica autorizado neste ato a contratar em nome do(s) EMITENTE, os seguros para "
							+ "cobertura dos riscos de morte e invalidez permanente e de danos físicos ao(s) Imóvel(is) descrito(s) "
							+ "na cláusula 3 acima, cujos prêmios deverão ser pagos mensalmente. O CREDOR, ou quem vier a substituí-lo, "
							+ "será nomeado beneficiário das respectivas apólices/certificados de seguro, e receberá o capital segurado"
							+ " ou indenização em caso de sinistro para utilização dos valores daí decorrentes na liquidação total"
							+ " ou parcial das obrigações de pagamento oriundas do presente instrumento. O valor do prêmio dos"
							+ " referidos seguros será reajustado conforme definido em apólice e poderá ser revisto e alterado"
							+ " desde o início da contratação, ou seja, na elaboração da proposta de empréstimo ou financiamento,"
							+ " até a liquidação integral da CCB, de acordo com as regras estabelecidas na respectiva"
							+ " apólice de seguros que são estipuladas pela companhia seguradora. ",
					true, false);
			
			if(objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoComposto(document, paragraph, run, run2, "3.3.1.1. ",
						"Assim, declaram-se cientes o EMITENTE e o(s) TERCEIRO(S) "
						+ "GARANTIDOR(ES) que qualquer alteração nas condições "
						+ "inicialmente informadas para a contratação, tais como,"
						+ " mas não se limitando, por exemplo, a(s) idade(s) do(s) "
						+ "proponente(s), poderá refletir em modificação no prêmio dos"
						+ " seguros a serem contratados para a devida formalização deste"
						+ " empréstimo com garantia imobiliária. ",
						true, false);
			} else {
				geraParagrafoComposto(document, paragraph, run, run2, "3.3.1.1. ",
						"Assim, declara-se ciente o EMITENTE que qualquer alteração"
								+ " nas condições inicialmente informadas para a contratação,"
								+ " tais como, mas não se limitando, por exemplo, a(s) idade(s)"
								+ " do(s) proponente(s), poderá refletir em modificação no prêmio"
								+ " dos seguros a serem contratados para a devida formalização deste"
								+ " empréstimo com garantia imobiliária.",
						true, false);
			}

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.1.2. ",
					"Declara ainda o EMITENTE e o(s) TERCEIROS(S) GARANTIDOR(ES) que:", true, false,
					ParagraphAlignment.LEFT);

			CTNumbering cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML);
			CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);

			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
			XWPFNumbering numbering = document.createNumbering();
			BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
			BigInteger numID = numbering.addNum(abstractNumID);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"tem(têm) ciência e concorda(m) integralmente com os termos das condições gerais "
							+ "ora apresentadas com relação ao Seguro de pessoa com cobertura de Morte e "
							+ "Invalidez Permanente por Acidente (MIP) e ao Seguro de danos com cobertura de "
							+ "Danos Físicos ao Imóvel (DFI), tendo pleno conhecimento de todas as suas "
							+ "coberturas e riscos excluídos ",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"os próprios EMITENTE ou seus beneficiários, herdeiros ou sucessores, deverão "
							+ "comunicar ao CREDOR e a Seguradora, imediatamente e por escrito, a ocorrência "
							+ "de qualquer sinistro, bem como, qualquer evento suscetível de agravar "
							+ "consideravelmente o risco coberto, sob pena de perder o direito à indenização se "
							+ "for provado que silenciou de má-fé;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"autoriza(m), desde já, de forma expressa, irrevogável e inequívoca, que a "
							+ "Seguradora realize o levantamento de informações médicas em hospitais, clínicas "
							+ "e/ou consultórios, bem como, que solicite a realização de perícia médica quando	necessária.",
					false);

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.2. ",
					"Se, em decorrência de sinistro, " + "a Seguradora por qualquer motivo "
							+ "desembolsar indenização em valor " + "insuficiente a quitação do saldo"
							+ " devedor do empréstimo objeto deste " + "instrumento, ficará(ão) o EMITENTE ou seu(s)"
							+ " herdeiro(s) e/ou sucessor(es) obrigado(s) a efetiva"
							+ " liquidação do saldo devedor remanescente perante o CREDOR.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.3. ",
					"Na hipótese da cláusula acima, no caso de não liquidação do"
							+ " saldo remanescente pelos DEVEDOR(ES), seus herdeiros e"
							+ " sucessores a qualquer título, sobre estes incidirá os encargos"
							+ " moratórios previstos na cláusula 6, bem como a respectiva "
							+ "execução da garantia pelo CREDOR ou quem vier a substituí-lo.",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "4. DA CONCESSÃO DO CRÉDITO", true);

			geraParagrafoComposto(document, paragraph, run, run2, "4.1. ",
					"O EMITENTE pagará por esta CCB ao CREDOR ou a quem este "
							+ "vier a indicar, em moeda corrente nacional, o Valor do "
							+ "Crédito acrescido de encargos, conforme expressamente "
							+ "indicado na cláusula 2 acima, calculados desde a data da "
							+ "emissão desta CCB pelo EMITENTE até a data do seu respectivo "
							+ "pagamento integral ao CREDOR, acrescidos, quando aplicáveis,"
							+ " dos encargos moratórios, conforme disposto na presente CCB; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.2. ",
					"O EMITENTE tem expresso conhecimento de que os juros"
							+ " ajustados para o empréstimo a que se refere à presente"
							+ " CCB são calculados, sempre e invariavelmente, de forma"
							+ " diária e capitalizada, conforme permitido pela legislação" + " aplicável; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.3. ",
					"O EMITENTE declara que tomou conhecimento do cálculo do CET"
							+ " indicado no item 2.4 acima, previamente à operação de "
							+ "empréstimo contratada por meio da presente CCB, através "
							+ "de planilha de cálculo que lhe foi apresentada pelo CREDOR; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.4. ",
					"O EMITENTE concorda que a Liberação do Crédito "
							+ "prevista na cláusula 2.5 está condicionada ao cumprimento"
							+ " das seguintes condições precedentes, de forma cumulativa"
							+ " e satisfatória para o CREDOR:",
					true, false);

			CTNumbering cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft);
			CTAbstractNum cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			XWPFAbstractNum abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			XWPFNumbering numbering2 = document.createNumbering();
			BigInteger abstractNumID2 = numbering2.addAbstractNum(abstractNum2);
			BigInteger numID2 = numbering2.addNum(abstractNumID2);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Entrega de todas as vias da CCB e Instrumento Particular"
							+ " de Alienação Fiduciária de Bem(ns) Imóvel(eis) em Garantia e "
							+ "Outras Avenças, devidamente assinadas pelas Partes com todas as "
							+ "firmas reconhecidas ou mediante assinatura eletrônica compatível"
							+ " com os padrões do ICP-BRASIL;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Entrega da matrícula atualizada com o registro da alienação "
							+ "fiduciária do imóvel descrito na cláusula 3 dessa CCB" + " em favor do CREDOR.",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"No caso de haver processo judicial em andamento,"
							+ " a ser quitado na forma do ANEXO II da presente CCB,"
							+ " concorda o EMITENTE que a liberação do crédito estará"
							+ " condicionada à comprovação do protocolo do acordo "
							+ "assinado pelas partes litigantes nos autos, o qual deve"
							+ " conter obrigatoriamente a menção à quitação e o pedido" + " extinção do processo.",
					false);
			
			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Emissão da Certidão Negativa de Débitos – CND Municipal atualizada, em que não"
					+ " conste débitos de Imposto Predial e Territorial Urbano – IPTU.",
					false);
			
			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Emissão da Certidão Negativa de Débitos – CND dos débitos condominiais.",
					false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.1. ",
					"Caso haja parcelamento judicial ou administrativo vigente para pagamento dos débitos de IPTU "
					+ "ou condomínio que torne possível a emissão da CND, pelo fato da existência da dívida ainda"
					+ " representar risco à garantia, é condição necessária à Liberação do Crédito que toda a dívida"
					+ " seja quitada.",
					true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.2. ",
					"Caso existam débitos municipais de IPTU ou condomínio, parcelados ou não, ajuizados ou não,"
					+ " o(a) EMITENTE autoriza o desconto destes valores para quitação das dívidas nos termos do"
					+ " ANEXO II, caso em que se compromete a encaminhar ao CREDOR as respectivas guias para pagamento.",
					true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.3. ",
					"Caso a Certidão de Débitos seja positiva, a exclusivo critério do CREDOR a operação "
					+ "poderá ser cancelada, devendo o EMITENTE reembolsar os valores gastos até o registro"
					+ " da garantia.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.5. ",
					"O(A) EMITENTE concorda que, caso as condições precedentes "
							+ "acima não sejam cumpridas no prazo de até 30 (trinta) "
							+ "dias corridos contados da emissão da CCB, o referido título"
							+ " poderá, a critério do CREDOR, ser considerado cancelado,"
							+ " deixando de surtir efeitos, obrigações, direitos e deveres "
							+ "às Partes, devendo o(a) EMITENTE reembolsar todos os gastos "
							+ "despendidos pelo CREDOR. ",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "5. DA FORMA DE PAGAMENTO E PRAZO", true);

			geraParagrafoComposto(document, paragraph, run, run2, "5.1. Depósito em Conta Corrente: ",
					"Fica o EMITENTE instruído pelo CREDOR, em caráter irrevogável e irretratável,"
							+ " a depositar em conta corrente nos termos da cláusula 2.6 acima,"
							+ " de titularidade do CREDOR ou a quem este vier a indicar (“Conta Corrente”),"
							+ " os valores relativos às parcelas da CCB indicadas no ANEXO I, "
							+ "acrescidas dos respectivos encargos, inclusive debitar os valores"
							+ " correspondentes a mora, IOF, tarifas e demais despesas aqui previstas.",
					true, false);

			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.2. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O(s) EMITENTE(S) declara(m)-se ciente(s) de que o pagamento "
					+ "das parcelas mensais e os encargos, conforme valores e prazos "
					+ "estabelecidos no ANEXO I dessa CCB, ");
			run2.setBold(false);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("não estão vinculados à data de liberação do Valor Líquido do Crédito");
			run.setBold(true);

			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.removeCarriageReturn();
			run2.setText(", devendo tais encargos serem pagos a partir da data ajustada" + " no item ");

			run = paragraph.createRun();
			run.setFontSize(12);
			run.removeCarriageReturn();
			run.setText("2.7");
			run.setBold(true);

			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.removeCarriageReturn();
			run2.setText(
					", sob pena de incidência de atualização monetária, juros e multa, de acordo com o quanto disposto na cláusula 6.");
			run2.addCarriageReturn();

			/*
			 * XWPFRun run5 = paragraph.createRun(); run5.setFontSize(12);
			 * run5.removeCarriageReturn(); run5.setText("2.7"); run5.addCarriageReturn();
			 * 
			 * XWPFRun run6 = paragraph.createRun(); run6.setFontSize(12);
			 * run6.removeCarriageReturn(); run6.
			 * setText(", sob pena de incidência de atualização monetária, juros e multa, de acordo com o quanto disposto na cláusula 6."
			 * ); run6.addCarriageReturn();
			 */

			geraParagrafoComposto(document, paragraph, run, run2, "5.3. ",
					"Na hipótese de haver parcelas mensais vencidas e não pagas na "
							+ "data de liberação do Valor Líquido do Crédito, o(s) DEVEDOR(ES),"
							+ " desde já, autoriza(m) o CREDOR a descontar desse valor,"
							+ " descrito na cláusula 2.1.4, eventual montante devido em"
							+ " razão do não pagamento das parcelas mensais ajustadas"
							+ " conforme ANEXO I, incluindo encargos moratórios conforme"
							+ " previsto na Cláusula 6 dessa CCB.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "5.4. ",
					"Os pagamentos devidos ao CREDOR, previstos na presente CCB,"
							+ " serão efetuados via boleto bancário a ser encaminhado ao"
							+ " endereço físico ou eletrônico do EMITENTE constante do item"
							+ " II da cláusula 1. Fica estabelecido que a falta de recebimento "
							+ "do aviso de cobrança ou boleto bancário não exime o EMITENTE de "
							+ "efetuar os pagamentos previstos nesta CCB, nem constitui justificativa"
							+ " para atraso em sua liquidação ou isenção de penalidades moratórias,"
							+ " cabendo ao EMITENTE entrar em contato com o CREDOR, ou quem o substituir,"
							+ " em tempo hábil, visando à obtenção de boleto para pagamento.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "5.5. ",
					"Em razão do acordado nesta cédula quanto ao valor, prestações,"
							+ " parcelas, reajustes e atualizações, o pagamento de qualquer"
							+ " prestação atualizada de maneira diversa da estabelecida nesta CCB,"
							+ " inclusive perante terceiros autorizados a recebê-las,"
							+ " não implicará na quitação do respectivo débito ou " + "repactuação da dívida.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "5.6. ",
					"Qualquer diferença verificada entre os créditos"
							+ " efetivados na conta corrente do CREDOR e a sistemática"
							+ " de cálculos dos valores estabelecidos nesta CCB,"
							+ " deverá ser imediatamente liquidada pelo EMITENTE no "
							+ "prazo máximo de 48 (quarenta e oito) horas, contadas"
							+ " do aviso que o CREDOR lhe dirigir neste sentido,"
							+ " caso em que, não realizado o pagamento após esse " + "prazo, estará em mora.",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "6. DO ATRASO NO PAGAMENTO E ENCARGOS MORATÓRIOS", true);

			geraParagrafoComposto(document, paragraph, run, run2, "6.1. ",
					"Na hipótese de inadimplemento ou mora, o EMITENTE estará "
							+ "obrigado a pagar ao CREDOR ou a quem este indicar, cumulativamente,"
							+ " além da quantia correspondente à dívida em aberto, os seguintes " + "encargos: ",
					true, false);

			cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft_NoHanging_bold);
			cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			numbering2 = document.createNumbering();
			abstractNumID2 = numbering2.addAbstractNum(abstractNum2);
			numID2 = numbering2.addNum(abstractNumID2);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Juros remuneratórios nos mesmos percentuais das taxas "
							+ "contratadas nessa CCB, calculados a partir do vencimento "
							+ "da(s) parcela(s) em aberto até a data do efetivo pagamento;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Juros de mora à razão de 1% a.m. (um por cento ao mês), "
							+ "calculados a partir do vencimento da(s) parcela(s) em aberto"
							+ " até a data do efetivo pagamento;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Multa contratual, de natureza não compensatória, de 2% (dois por cento)"
							+ " incidente sobre o montante atualizado (juros remuneratórios e juros de mora)"
							+ " total do débito apurado e não pago;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Na hipótese do CREDOR vir a ser compelido a recorrer"
							+ " a meios administrativos ou judiciais para receber o seu crédito,"
							+ " as despesas de cobrança, estas limitadas a 20% (vinte por cento)"
							+ " sobre o valor do saldo devedor e, havendo procedimento judicial, "
							+ "custas processuais e honorários advocatícios, estes fixados judicialmente.",
					false);

			fazParagrafoSimples(document, paragraph, run, "7. DO VENCIMENTO ANTECIPADO", true);

			geraParagrafoComposto(document, paragraph, run, run2, "7.1. ",
					"Além das demais hipóteses estabelecidas em lei e nesta CCB,"
							+ " a dívida aqui contraída pelo EMITENTE, a partir do primeiro "
							+ "dia útil da liberação do Valor do Crédito, reputar-se-á "
							+ "antecipadamente vencida, facultando-se ao credor da CCB exigir "
							+ "a imediata e integral satisfação de seu crédito, independentemente "
							+ "de aviso ou notificação judicial ou extrajudicial de qualquer espécie,"
							+ " na ocorrência de qualquer das hipóteses previstas nos artigos 333 e "
							+ "1.425 do Código Civil Brasileiro e, ainda, nas seguintes hipóteses:",
					true, false);

			cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft_NoHanging_bold2);
			cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			numbering2 = document.createNumbering();
			abstractNumID2 = numbering.addAbstractNum(abstractNum2);
			numID2 = numbering2.addNum(abstractNumID2);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se ocorrer inadimplemento de qualquer obrigação assumida pelo EMITENTE, "
							+ "em consonância com as cláusulas e condições aqui estabelecidas, "
							+ "principalmente no que tange ao pagamento das parcelas devidas "
							+ "em decorrências do empréstimo a ele concedido por força da " + "presente CCB;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2, "Se ocorrer inadimplemento "
					+ "de qualquer obrigação assumida pelo EMITENTE, e/ou quaisquer sociedades"
					+ " direta ou indiretamente ligadas, coligadas, controladoras ou controladas"
					+ " pelo EMITENTE (doravante denominadas “AFILIADAS”), inclusive no exterior,"
					+ " de suas obrigações decorrentes de outros contratos, empréstimos ou descontos"
					+ " celebrados com o CREDOR e/ou quaisquer sociedades, direta ou indiretamente,"
					+ " ligadas, coligadas, controladoras ou controladas pelo credor da CCB ou seu "
					+ "cessionário, e/ou com terceiros, e/ou rescisão ou declaração de vencimento "
					+ "antecipado dos respectivos documentos, por culpa do EMITENTE e/ou de quaisquer " + "AFILIADAS;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se for protestado qualquer "
							+ "título de responsabilidade do EMITENTE em razão do inadimplemento de obrigação "
							+ "cujo valor individual ou em conjunto seja igual ou superior a R$ 100.000,00 "
							+ "(cem mil reais), sem que a justificativa para tal medida tenha sido apresentada"
							+ " ao credor da CCB, no prazo que lhe tiver sido solicitada ou, sendo ou tendo sido"
							+ " apresentada a justificativa, se esta não for considerada satisfatória pelo CREDOR,"
							+ " ressalvado o protesto tirado por erro ou má-fé do respectivo portador;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se O EMITENTE for inscrito no "
							+ "Cadastro de Emitentes de Cheques sem Fundo – CCF, ou, ainda, constem informações "
							+ "negativas a seu respeito no Sistema de Informações de Crédito do Banco Central,"
							+ " que, a critério do credor da CCB, possa afetar a sua capacidade de cumprir as "
							+ "obrigações assumidas na presente CCB ou no Termo de Garantia;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se o EMITENTE e/ou quaisquer AFILIADAS,"
							+ " inclusive no exterior, tornarem-se insolventes, requerer(em) ou tiver(em), falência, "
							+ "insolvência civil, recuperação judicial ou extrajudicial requerida ou decretada, sofrer "
							+ "intervenções, regime de administração especial temporária, ou liquidação judicial ou"
							+ " extrajudicial;",
					false);
			if(objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoBulletList(document, paragraph, run, numID2, "Se for comprovada a falsidade de qualquer"
						+ " declaração, informação ou documento que houver sido, respectivamente, firmada, prestada ou "
						+ "entregue pelo EMITENTE e TERCEIRO(S) GARANTIDOR(ES), ao CREDOR;", false);
			} else {
				geraParagrafoBulletList(document, paragraph, run, numID2, "Se for comprovada a falsidade de qualquer"
						+ " declaração, informação ou documento que houver sido, respectivamente, firmada, prestada ou"
						+ " entregue pelo EMITENTE, ao CREDOR;", false);
			}

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se o EMITENTE sofrer qualquer (quaisquer) "
							+ "medida(s) judicial(ais) ou extrajudicial(ais) que por qualquer forma, possa(m) afetar "
							+ "negativamente os créditos do empréstimo e/ou as garantias conferidas ao credor da CCB;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se as garantias fidejussórias, "
							+ "ora e/ou que venham a ser eventualmente convencionadas, por qualquer fato atinente"
							+ " ao seu objeto ou prestador se tornar inábeis, impróprias, ou insuficientes para "
							+ "assegurar o pagamento da dívida, e desde que não sejam substituídas, ou complementadas,"
							+ " quando solicitada por escrito pelo CREDOR ou a quem este vier a indicar;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento "
							+ "do credor da CCB ocorrer a transferência a terceiros dos direitos e obrigações do"
							+ " EMITENTE previstos nesta CCB e no Termo de Garantia;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento do "
							+ "credor da CCB ocorrer alienação, cessão, doação ou transferência, por qualquer meio, "
							+ "de bens, ativos ou direitos de propriedade do EMITENTE e/ou de quaisquer AFILIADAS, "
							+ "quando aplicável que, no entendimento do credor, possam levar ao descumprimento das "
							+ "obrigações previstas na presente CCB;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento do"
							+ " credor da CCB, o EMITENTE, quando aplicável, tiver total ou parcialmente, o seu"
							+ " controle acionário, direto ou indireto, cedido, transferido ou por qualquer outra"
							+ " forma alienado ou modificado;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se ocorrer mudança ou alteração do"
							+ " objeto social do EMITENTE, quando aplicável, de forma a alterar as atividades"
							+ " principais ou a agregar às suas atividades novos negócios que possam representar "
							+ "desvios em relação às atividades atualmente desenvolvidas;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento do"
							+ " credor da CCB, o EMITENTE sofrer, durante a vigência desta CCB, qualquer operação"
							+ " de transformação, incorporação, fusão ou cisão;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se a garantia real objeto do"
							+ " Instrumento Particular de Alienação Fiduciária de Bem Imóvel não for efetivamente"
							+ " registrada junto ao RGI no prazo de até 30(trinta) dias corridos a contar da"
							+ " emissão desta CCB; e",
					false);
			if(objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoBulletList(document, paragraph, run, numID2,
						"o)	Se o Bem Imóvel objeto da garantia à presente CCB apresentar quaisquer características,"
						+ " ônus ou gravame ou caso ocorra qualquer ato ou omissão por parte de EMITENTE e/ou TERCEIRO(S)"
						+ " GARANTIDOR(ES), que impeça a efetiva constituição da garantia regulada nos termos Instrumento"
						+ " Particular de Alienação Fiduciária de Bem Imóvel.",
						false);
			} else {
				geraParagrafoBulletList(document, paragraph, run, numID2,
						"Se o Bem Imóvel objeto" + " da garantia à presente CCB apresentar quaisquer características, ônus "
								+ "ou gravame ou caso ocorra qualquer ato ou omissão por parte do EMITENTE,"
								+ " que impeça a efetiva constituição da garantia regulada nos termos Instrumento"
								+ " Particular de Alienação Fiduciária de Bem Imóvel.",
						false);
			}

			geraParagrafoComposto(document, paragraph, run, run2, "7.2. ",
					"No caso de falta de pagamento"
							+ " de qualquer parcela(s) na(s) data(s) de seu(s) respectivo(s) vencimento(s),"
							+ " o CREDOR poderá, por mera liberdade e sem que tal situação caracterize novação"
							+ " ou alteração das condições estabelecidas nesta CCB – optar pela cobrança somente"
							+ " da(s) parcela(s) devida(s) em aberto, comprometendo-se o EMITENTE,"
							+ " em contrapartida, a liquidá-la(s) imediatamente quando instado(s) para tal,"
							+ " sob pena de ultimar-se o vencimento antecipado de toda a dívida; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "7.2.1. ",
					"Declarado o vencimento "
							+ "antecipado de toda a dívida, o credor da CCB apresentará ao EMITENTE notificação "
							+ "contendo o saldo devedor final, incluindo principal, juros, encargos, despesas e "
							+ "tributos, a ser pago pelo EMITENTE no dia útil imediatamente subsequente ao "
							+ "recebimento de referida notificação, sob pena de ser considerado em mora, "
							+ "independentemente de qualquer aviso ou notificação judicial ou extrajudicial;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "7.2.2. ", "Na declaração de vencimento "
					+ "antecipado da dívida pelo credor da CCB, além do valor apurado nos termos do item 7.2.1 acima,"
					+ " serão acrescidos os encargos previstos na cláusula 6 às parcelas vencidas. ", true, false);

			fazParagrafoSimples(document, paragraph, run, "8. LIQUIDAÇÃO ANTECIPADA", true);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1. ", "O EMITENTE poderá liquidar"
					+ " antecipadamente, total ou parcialmente, suas obrigações decorrentes desta CCB, "
					+ "desde que previamente acordado, de modo satisfatório ao credor da CCB e ao EMITENTE,"
					+ " as condições de tal liquidação antecipada. Para tanto, o EMITENTE deverá encaminhar"
					+ " ao credor da CCB, solicitação por escrito, com antecedência mínima de 10 (dez) dias úteis;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.1. ",
					"Se indicada a Tarifa de "
							+ "Liquidação Antecipada no item 2.10 acima, o EMITENTE, desde já, se obriga a pagar "
							+ "ao CREDOR, na data da liquidação, a Tarifa de Liquidação Antecipada sobre o valor"
							+ " efetivamente pago antecipadamente, a título de indenização pelos custos relacionados"
							+ " com a quebra de captação de recursos;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.2. ", "Nas situações em que as despesas "
					+ "associadas à contratação realizada por meio desta CCB forem também objeto de financiamento "
					+ "ou empréstimo, essas despesas integrarão igualmente a operação para apuração do valor "
					+ "presente para fins de amortização, total ou parcial, da dívida ainda em aberto;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.3. ", "Caso haja saldo devedor a ser"
					+ " pago acrescentar-se-ão, às prestações em atraso, e as penalidades previstas neste instrumento,"
					+ " bem como os juros remuneratórios calculados pro rata die e quaisquer outras despesas de "
					+ "responsabilidade do EMITENTE nos termos desta CCB;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.4. ",
					"Sempre que for necessário,"
							+ " a apuração do saldo devedor do EMITENTE será realizada pelo CREDOR mediante planilha "
							+ "de cálculo, que constituirá documento integrante e inseparável da presente CCB. ",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "9.	DECLARAÇÕES", true);

			geraParagrafoComposto(document, paragraph, run, run2, "9.1. ",
					"As Partes signatárias, cada uma por si, declaram e garantem que: ", true, false);

			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoHanging_bold);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);

			geraParagrafoBulletListComposta(document, paragraph, run, run2,
					"Possui plena capacidade e "
							+ "legitimidade para celebrar a presente CCB, realizar todas as operações e cumprir todas "
							+ "as obrigações aqui assumidas",
					", bem como dos instrumentos de garantia, tendo tomado todas as medidas"
							+ " de natureza societária e outras eventualmente necessárias para autorizar "
							+ "a sua celebração, implementação e cumprimento de todas as obrigações " + "constituídas;",
					true, false, numID, UnderlinePatterns.SINGLE);

			geraParagrafoBulletList(document, paragraph, run, numID, "A celebração desta CCB e do Termo de Garantia,"
					+ " e o cumprimento das obrigações de cada uma das Partes: (a) não violam qualquer disposição contida"
					+ " nos seus documentos societários; (b) não violam qualquer lei, regulamento, decisão judicial, "
					+ "administrativa ou arbitral, aos quais a respectiva Parte esteja vinculada; (c) não exigem qualquer"
					+ " consentimento, ação ou autorização, prévia ou posterior, de terceiros;", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Esta CCB e o Termo de Garantia são validamente "
					+ "celebrados e constituem obrigação legal, válida, vinculante e exequível contra cada uma das Partes,"
					+ " de acordo com os seus termos;", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Cada Parte está apta a cumprir as obrigações ora"
					+ " previstas nesta CCB e nos instrumentos de garantia, e agirá em relação aos mesmos de boa-fé e com"
					+ " lealdade;", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Nenhuma Parte depende economicamente da outra;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Nenhuma das Partes se encontra em estado de"
					+ " necessidade ou sob coação para celebrar esta CCB e/ou quaisquer contratos e compromissos a "
					+ "ela relacionados e acessórios ", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "As discussões sobre o objeto contratual, "
					+ "crédito, encargos incidentes e obrigações acessórias, oriundos desta CCB e dos instrumentos"
					+ " de garantia, foram feitas, conduzidas e implementadas por livre iniciativa das Partes;", false);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"O CREDOR e EMITENTE, são pessoas devidamente estruturadas,"
							+ " qualificadas e capacitadas para entender a estrutura financeira e jurídica objeto desta CCB, e estão "
							+ "acostumadas a celebrar, em seus respectivos campos de atuação, títulos e instrumentos de garantia semelhantes"
							+ " aos previstos nesta CCB, não havendo entre as Partes qualquer relação de hipossuficiência ou ainda natureza de"
							+ " consumo na relação aqui tratada.",
					false);
			
			geraParagrafoBulletList(document, paragraph, run, numID,
					"EMITENTE(S), TERCEIRO(S) GARANTIDOR(ES), AVALISTA(S) e ANUENTE(S) declaram expressamente,"
					+ " sob pena de responsabilidade civil e criminal, que não possuem nenhum negócio jurídico"
					+ " pactuado entre si ou com terceiros que tenha relação com emissão desta CCB ou com a garantia"
					+ " oferecida, estando cientes de que nada poderá ser oponível ao credor com a finalidade de "
					+ "prejudicar os pagamentos ou a execução da garantia.",
					false);

			fazParagrafoSimples(document, paragraph, run, "10.	DAS DISPOSIÇÕES FINAIS", true);

			geraParagrafoComposto(document, paragraph, run, run2, "10.1. Tolerância: ",
					"A tolerância não implica perdão, renúncia, novação ou alteração da dívida ou das condições aqui previstas e o pagamento do principal, mesmo sem ressalvas, não será considerado ou presumido a quitação dos encargos. Dessa forma, as Partes acordam que qualquer prática diversa da aqui pactuada, mesmo que reiterada, não poderá ser interpretada como novação;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.1.1 Declarações Específicas: ",
					"O EMITENTE declara que: "
							+ "(i) está ciente que o surto do novo coronavírus (COVID-19), reconhecido oficialmente como pandemia pela "
							+ "Organização Mundial de Saúde (OMS), é anterior à celebração desta CCB e que a pandemia não apresenta "
							+ "caráter de imprevisibilidade, extraordinariedade ou superveniência no presente momento, (ii) reconhece "
							+ "que tais eventos não configuram caso fortuito ou de força maior, conforme definição do artigo 393 do Código"
							+ " Civil, e (iii) compromete-se a honrar as obrigações assumidas nos termos desta CCB; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2,
					"10.2. Comunicação aos Serviços de Proteção ao Crédito: ",
					"Na hipótese de ocorrer descumprimento de qualquer obrigação ou atraso no pagamento, o CREDOR ou a quem este"
							+ " vier a indicar poderá comunicar o fato a qualquer serviço de proteção ao crédito, como Serasa Experian ou"
							+ " qualquer outro órgão encarregado de cadastrar atraso nos pagamentos e o descumprimento de obrigações "
							+ "contratuais, informando o nome do EMITENTE.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.3. Reforço de Garantias: ", "O CREDOR poderá, "
					+ "a qualquer tempo, exigir reforço de garantias, ficando estipulado o prazo de 5 (cinco) dias úteis "
					+ "contados da data de sua solicitação, pelo CREDOR, por carta sob protocolo ou registro postal, para "
					+ "que o EMITENTE providencie o respectivo reforço, sob pena do imediato vencimento da presente CCB, "
					+ "independentemente de interpelação judicial ou notificação judicial ou extrajudicial;", true,
					false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.4. Alteração da CCB: ",
					"A presente CCB somente poderá "
							+ "ser alterada mediante aditivo próprio devidamente assinado pelas Partes; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2,
					"10.5. Comunicação ao Sistema de Informação de Créditos " + "(“SCR”): ",
					"O CREDOR, neste ato, comunica ao EMITENTE que a presente operação de empréstimo, será "
							+ "registrada no SCR gerido pelo Banco Central do Brasil (“BACEN”), que tem por finalidade subsidiar"
							+ " o BACEN para fins de supervisão de risco de crédito a que estão expostas as instituições"
							+ " financeiras e ainda intercambiar informações entre as instituições financeiras; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.5.1 ",
					"O EMITENTE poderá ter acesso aos dados "
							+ "constantes em seu SCR, por meio de central de atendimento ao público do BACEN;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.5.2 ",
					"Em caso de discordância quanto às informações"
							+ " do SCR, bem como pedidos de correções, o EMITENTE deverá entrar em contato com a Ouvidoria do CREDOR,"
							+ " nos termos do item 10.11 abaixo;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.5.3 ",
					"O EMITENTE autoriza o CREDOR ou a quem este "
							+ "indicar, a qualquer tempo: a: (i) efetuar consultas ao Sistema de Informações de Crédito – SCR – do Banco"
							+ " Central do Brasil (“SCR”), nos termos da Resolução nº 3.658, do Conselho Monetário Nacional, de 17.12.2008,"
							+ " conforme alterada e os serviços de proteção ao crédito SPC, Serasa e outras em que o CREDOR seja "
							+ "cadastrado; (ii) fornecer ao Banco Central do Brasil informações sobre esta CCB, para integrar o SCR; "
							+ "e (iii) proceder conforme disposições que advierem de novas exigências feitas pelo Banco Central do Brasil"
							+ " ou autoridades. ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.6. Efeitos do CCB: ",
					"As Partes convencionam que as "
							+ "obrigações pecuniárias estipuladas na presente CCB passam a vigorar a partir de sua respectiva emissão;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.7. ", "Se qualquer item ou cláusula desta CCB "
					+ "vier a ser considerado ilegal, inexequível ou, por qualquer motivo, ineficaz, todos os demais itens"
					+ " e cláusulas continuarão em vigor, plenamente válidos e eficazes. As Partes, desde já, se comprometem"
					+ " a negociar, no menor prazo possível, item ou cláusula que, conforme o caso, venha a substituir o item"
					+ " ou cláusula ilegal, inexequível ou ineficaz. Nessa negociação, deverá ser considerado o objetivo das "
					+ "Partes na data de assinatura dessa CCB, bem como o contexto no qual o item ou cláusula ilegal, inexequível"
					+ " ou ineficaz foi inserido.", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.8. Irrevogabilidade e Irretratabilidade: ",
					"A presente CCB é firmada em caráter irrevogável e irretratável, obrigando as Partes, seus "
							+ "herdeiros e/ou sucessores; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.9. Base de Dados: ",
					"O EMITENTE declara e concorda "
							+ "expressamente que ao firmar a presente CCB passará a fazer parte integrante da base de clientes do CREDOR,"
							+ " ou a quem este vier a indicar, autorizando, assim através das informações cadastrais que o CREDOR, ou a "
							+ "quem este vier a indicar, possui a respeito dele o oferecimento de produtos e/ou serviços;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.10. Ouvidoria: ",
					"O EMITENTE declara ter ciência de que o "
							+ "CREDOR disponibiliza um canal de Ouvidoria para que sejam feitas sugestões e/ou reclamações através do telefone"
							+ " (11) 3810-9333;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11. Legislação: ", "Aplica-se a presente CCB, as"
					+ " disposições da Lei 10.931, de 02 de agosto de 2004, e posteriores alterações (“Lei 10.931”), declarando"
					+ " o EMITENTE ter conhecimento que a presente CCB é um título executivo extrajudicial e representa dívida "
					+ "em dinheiro, certa, líquida e exigível, seja pela soma nela indicada, seja pelo saldo devedor "
					+ "demonstrado em planilha de cálculo ou nos extratos de Conta Corrente, a serem emitidos consoante "
					+ "o que preceitua a aludida Lei 10.931;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11.1  ", "O EMITENTE declara ter ciência que: (i)"
					+ " o CREDOR integra o Sistema Financeiro Nacional, submetendo-se à disciplina e regras pelo Conselho"
					+ " Monetário Nacional e Banco Central do Brasil; e (ii) as taxas de juros cobradas nas operações "
					+ "financeiras realizadas pelo CREDOR, incluindo a presente CCB, não estão submetidas ao limite de"
					+ " 12% (doze por cento) ao ano, como já decidiu o Supremo Tribunal Federal, sendo legítima a "
					+ "cobrança de juros e encargos superiores a esse percentual;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11.2 ", "A tolerância, por uma das partes, "
					+ "quanto a alguma demora, atraso ou omissão da outra parte no cumprimento das obrigações ajustadas"
					+ " neste instrumento, ou a não aplicação, na ocasião oportuna, das penalidades previstas "
					+ "será considerada mera liberalidade, não se configurando como precedente ou novação "
					+ "contratual", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11.3 ", "Se vier a tornar impossível"
					+ " a aplicação das regras previstas nesta Cédula, seja por força de eventual caráter cogente"
					+ " de imperativos legais que venham a ser baixados, seja em decorrência de ausência de consenso"
					+ " entre as Partes, considerar-se-á rescindida esta CCB e, em consequência, a dívida dela"
					+ " oriunda se considerará antecipadamente vencida, da mesma forma e com os mesmos efeitos "
					+ "previstos, efetivando-se a cobrança de juros “pro-rata temporis”; ", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12. Cessão ou Endosso: ", "O CREDOR fica "
					+ "expressamente autorizado a qualquer tempo, a seu exclusivo critério e independentemente da "
					+ "prévia anuência do EMITENTE, a ceder a terceiros os direitos de crédito que detém em razão desta CCB,"
					+ " bem como a transferi-la a terceiros mediante endosso da “via negociável”, sendo certo que "
					+ "a cessão ou o endosso não caracterizarão violação do sigilo bancário em relação ao EMITENTE."
					+ " Ocorrendo a cessão ou o endosso, o cessionário/endossatário desta CCB assumirá automaticamente"
					+ " a qualidade de credor desta CCB, passando a ser titular de todos os direitos e obrigações dela "
					+ "decorrentes; ", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12.1 ", "Após o endosso pelo CREDOR desta CCB,"
					+ " o EMITENTE desde já, reconhece a validade da emissão e do endosso desta CCB de forma física ou eletrônica, "
					+ "o que é feito com base no art. 889, §3º, do Código Civil. ", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12.2",
					" Na hipótese de transferência da presente CCB,"
							+ " o seu novo titular ficará automaticamente sub-rogado em todos os direitos e garantias que "
							+ "cabiam ao CREDOR original, independentemente de qualquer formalidade, passando a ter acesso "
							+ "livre e direto a todas as informações relacionadas à operação bancária e respectivas garantias,"
							+ " a exemplo de duplicatas e/ou direitos creditórios e/ou quaisquer outras garantias eventualmente "
							+ "constituídas, reconhecendo o EMITENTE que o novo titular da CCB possui o inequívoco direito de "
							+ "acompanhar detidamente todo o andamento da operação bancária, motivo pelo qual, da mesma forma,"
							+ " estará automaticamente sub-rogado a consultar as informações consolidadas em seu nome, no SCR, "
							+ "SERASA – Centralização de Serviços os Bancos S.A. e quaisquer  outros órgãos, entidades ou empresas,"
							+ " julgados pertinentes pelo CREDOR, permanecendo válida a presente autorização durante todo o tempo"
							+ " em que subsistir em aberto e não liquidadas as obrigações decorrentes da presente CCB. ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12.3 ",
					"A cessão dos direitos sempre compreenderá"
							+ " os acessórios, títulos, instrumentos que os representam e anexos. De tal forma, ao formalizar a cessão "
							+ "dos direitos de crédito, por meio de Contrato de Cessão, o CREDOR estará cedendo, automaticamente,"
							+ " todos os direitos, privilégios, preferências, prerrogativas, garantias e ações, legal e contratualmente"
							+ " previstas, que sejam inerentes ao direito de crédito cedido, inclusive: (i) o direito de receber "
							+ "integralmente o seu valor, acrescido dos juros, das multas, da atualização monetária e/ou demais encargos"
							+ " remuneratórios e/ou moratórios; (ii) o direito de ação e o de protesto em face do respectivo EMITENTE,"
							+ " para exigir o cumprimento da obrigação de pagamento, ou visando resguardar qualquer direito; (iii)"
							+ " as garantias eventualmente existentes, sejam reais ou pessoais; e (iv) o direito de declarar o direito "
							+ "de crédito vencido antecipadamente, nas hipóteses contratadas com o EMITENTE e naquelas previstas na"
							+ " legislação aplicável;",
					true, false);
			if(objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoComposto(document, paragraph, run, run2, "10.12.4 ", "O EMITENTE e/ou TERCEIRO(S) GARANTIDOR(ES),"
						+ " está(ão) integralmente ciente(s) e de acordo com o seguinte: (i) qualquer litígio ou questionamento,"
						+ " judicial ou extrajudicial, que possa vir a ser ajuizado, deverá ser ajuizado, àquele portador"
						+ " endossatário da CCB na data do ajuizamento do litígio ou questionamento; e (ii) o ajuizamento "
						+ "de qualquer ação, judicial ou extrajudicial, pelo EMITENTE e/ou TERCEIRO(S) GARANTIDOR(ES), "
						+ "contra o CREDOR, após o mesmo ter endossado esta CCB para terceiro, o EMITENTE e/ou TERCEIRO(S)"
						+ " GARANTIDOR(ES), estará(ão) sujeito(s) ao pagamento de indenização por perdas e danos, e "
						+ "ressarcimento de todo e quaisquer custos e despesas que o CREDOR venha a incorrer "
						+ "(incluindo honorários advocatícios) para defesa de seus direitos no respectivo litígio;", true, false);
			} else {
				geraParagrafoComposto(document, paragraph, run, run2, "10.12.4 ", "O EMITENTE, está integralmente ciente(s)"
						+ " e de acordo com o seguinte: (i) qualquer litígio ou questionamento, judicial ou extrajudicial, que possa "
						+ "vir a ser ajuizado, deverá ser ajuizado, àquele portador endossatário da CCB na data do ajuizamento do "
						+ "litígio ou questionamento; e (ii) o ajuizamento de qualquer ação, judicial ou extrajudicial, pelo EMITENTE,"
						+ " contra o CREDOR, após o mesmo ter endossado esta CCB para terceiro, o EMITENTE, estará sujeito ao "
						+ "pagamento de indenização por perdas e danos, e ressarcimento de todo e quaisquer custos e despesas"
						+ " que o CREDOR venha a incorrer (incluindo honorários advocatícios) para defesa de seus direitos no "
						+ "respectivo litígio;", true, false);
			}

			geraParagrafoComposto(document, paragraph, run, run2, "10.13. Emissão de Certificados de CCB: ",
					"O CREDOR, "
							+ "ou a quem este vier a indicar, poderá emitir certificados de CCB com lastro no presente título, podendo"
							+ " negociá-los livremente no mercado;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.13.1 ",
					"Caso haja a emissão do certificado referido "
							+ "no item 10.13, a presente CCB ficará custodiada em instituição financeira autorizada, a qual passará a "
							+ "proceder às cobranças dos valores devidos, junto ao EMITENTE;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.13.2 ", "O EMITENTE desde já se declara de acordo"
					+ " com a emissão do certificado referido no item 10.13, obrigando-se a atender às solicitações da instituição"
					+ " custodiante, bem como, aceitam a cessão de crédito, independentemente de qualquer aviso "
					+ "ou formalidade;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.14.	Foro: ", "Ajustam as Partes que será sempre"
					+ " competente para conhecer e dirimir qualquer questão oriunda ou decorrente da presente CCB, o foro"
					+ " da comarca de São Paulo capital com a exclusão de qualquer outro, por mais privilegiado que seja,"
					+ " reservando-se o credor da CCB o direito de optar, a seu exclusivo critério, pelo foro da sede"
					+ " do EMITENTE ou, ainda, pelo foro da situação dos bens dados em garantia;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.15. ", "Em caso de contratação eletrônica, "
					+ "as Partes ratificam que admitem como válido, para fins de comprovação de autoria e integridade,"
					+ " a assinatura e informações constantes no presente documento, as quais foram capturadas de forma"
					+ " eletrônica e utilizadas nesta Cédula, constituindo título executivo extrajudicial nos termos "
					+ "do artigo 28 da Lei nº 10.931 2004 e para todos os fins de direito, ainda que seja estabelecida "
					+ "com assinatura eletrônica ou certificação fora dos padrões ICP-BRASIL, conforme disposto pelo art."
					+ " 10 da Medida Provisória nº 2.200/2001.", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.16. ", "A presente CCB é emitida e firmada "
					+ "em 2 (duas) vias, constando na 1ª via a expressão “Via Negociável” e nas demais, a expressão "
					+ "“Via Não Negociável”. ", true, false);

			fazParagrafoSimples(document, paragraph, run,
					"São Paulo, SP, " + objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (objetoCcb.getDataDeEmissao().getYear() + 1900) + ".",
					false);

			fazParagrafoSimples(document, paragraph, run,
					"(O final desta página foi intencionalmente deixado em branco)", false, ParagraphAlignment.CENTER);

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);

			fazParagrafoSimples(document, paragraph, run, "(Segue a página de assinaturas)", false,
					ParagraphAlignment.CENTER);

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);
			
			paragraph = document.createParagraph();	
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(Página de assinaturas da Cédula de Crédito "
					+ "Bancário nº " + objetoCcb.getNumeroCcb() + ", emitida por "+ objetoCcb.getNomeEmitente().toUpperCase() +", CPF/MF nº "+ objetoCcb.getCpfEmitente() +", em favor de "
					+ "BMP SOCIEDADE DE CRÉDITO DIRETO S.A., CNPJ/ MF sob nº 34.337.707/0001-00,"
					+ " em "+ CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy" )+".)");
			run.setBold(false);
			run.setItalic(true);
			run.addCarriageReturn();

			XWPFTable table = document.createTable();

			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);

			table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));

			// create first row
			XWPFTableRow tableRow1 = table.getRow(0);

			tableRow1.getCell(0).setParagraph(paragraph);
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("____________________________________   ");
			run.setBold(false);
			run.addBreak();

			run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A");
			run2.setBold(true);
			run2.addBreak();

			run4 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("CREDOR");
			run4.setBold(false);

			tableRow1.addNewTableCell();

			tableRow1.getCell(1).setParagraph(paragraph);

			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("____________________________________ ");
			run.setBold(false);
			run.addBreak();

			run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText(objetoCcb.getNomeEmitente().toUpperCase());
			run2.setBold(true);
			run2.addBreak();

			run3 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run3.setFontSize(12);
			run3.setText(" ");
			run3.setBold(true);
			run3.addBreak();

			run4 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("EMITENTE");
			run4.setBold(false);

			XWPFTableRow tableRow2 = table.createRow();

			if (objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
				int qtdePessoasEsquerdo = 0;
				for (int iPartTab = 0; iPartTab < objetoCcb.getListaParticipantes().size(); iPartTab++) {

					CcbParticipantes participante = objetoCcb.getListaParticipantes().get(iPartTab);
					if (iPartTab != 0) {
						if (iPartTab % 2 != 0) {

							run = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("____________________________________   ");
							run.setBold(false);
							run.addBreak();

							run2 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();

							run3 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();

							qtdePessoasEsquerdo++;
						} else {
							run = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("____________________________________   ");
							run.setBold(false);
							run.addBreak();

							run2 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();

							run3 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();
							qtdePessoasEsquerdo--;
						}
					}
				}
				run4 = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				run4.addBreak();
				run4.setText("Testemunhas");
				run4.setBold(false);
				run4.addBreak();
				run4.setText("____________________________________");

				run4 = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				for (int i = 0; i <= qtdePessoasEsquerdo; i++) {
					run4.addBreak();
					run4.addBreak();
					run4.addBreak();
				}
				run4.setText("____________________________________   ");
				run4.setBold(false);

			} else {
				tableRow2.getCell(0).setParagraph(paragraph);
				run = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.setText("Testemunhas");
				run.setBold(false);
				run.addBreak();
				run.setText("____________________________________");

				tableRow2.getCell(1).setParagraph(paragraph);
				run = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.addBreak();
				run.setText("____________________________________   ");
				run.setBold(false);
			}
			
			XWPFTableRow tableRow3 = table.createRow();
			tableRow3.getCell(0).setParagraph(paragraph);
			run = tableRow3.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + objetoCcb.getNomeTestemunha1());
			run.setBold(false);

			tableRow3.getCell(1).setParagraph(paragraph);
			run = tableRow3.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + objetoCcb.getNomeTestemunha2());
			run.setBold(false);

			XWPFTableRow tableRow4 = table.createRow();
			tableRow4.getCell(0).setParagraph(paragraph);
			run = tableRow4.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + objetoCcb.getRgTestemunha1());
			run.setBold(false);

			tableRow4.getCell(1).setParagraph(paragraph);
			run = tableRow4.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + objetoCcb.getRgTestemunha2());
			run.setBold(false);

			XWPFTableRow tableRow5 = table.createRow();
			tableRow5.getCell(0).setParagraph(paragraph);
			run = tableRow5.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + objetoCcb.getCpfTestemunha1());
			run.setBold(false);

			tableRow5.getCell(1).setParagraph(paragraph);
			run = tableRow5.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + objetoCcb.getCpfTestemunha2());
			run.setBold(false);

			CTTblPr tblpro = table.getCTTbl().getTblPr();

			CTTblBorders borders = tblpro.addNewTblBorders();
			borders.addNewBottom().setVal(STBorder.NONE);
			borders.addNewLeft().setVal(STBorder.NONE);
			borders.addNewRight().setVal(STBorder.NONE);
			borders.addNewTop().setVal(STBorder.NONE);
			// also inner borders
			borders.addNewInsideH().setVal(STBorder.NONE);
			borders.addNewInsideV().setVal(STBorder.NONE);

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);

			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("ANEXO I");
			run.addCarriageReturn();
			run.setText("CÉDULA DE CRÉDITO BANCÁRIO Nº " + objetoCcb.getNumeroCcb());
			run.addCarriageReturn();
			run.setText("PLANILHA DE CÁLCULO");
			run.setBold(true);

			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

			paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.setText("pág. ");

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);

			/*
			 * for (XWPFParagraph p : document.getParagraphs()) { List<XWPFRun> runs =
			 * p.getRuns(); if (runs != null) { for (XWPFRun r : runs) { String text =
			 * r.getText(0); adicionarEnter(text, r); } } }
			 */

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (JDBCException jdbce) {
		    jdbce.getSQLException().getNextException().printStackTrace();
		} catch (Exception e) {
			e.getCause().printStackTrace();
		} 
		return null;
	}
	
	public byte[] geraAFDinamica() throws IOException {
		clearDocumentosNovos();
		try {
			XWPFDocument document = new XWPFDocument();	
			XWPFRun run;
	
			XWPFParagraph paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("INSTRUMENTO PARTICULAR DE ALIENAÇÃO FIDUCIÁRIA DE BEM(NS) IMÓVEL(EIS) EM GARANTIA E OUTRAS AVENÇAS");
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();			
			
			run.setFontSize(12);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "Pelo presente instrumento particular firmado"
					+ " nos termos do artigo 38 da Lei nº 9.514/1997, com a redação que lhe foi dada "
					+ "pelo artigo 53 da Lei nº 11.076/2004, as Partes: ", false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "De um lado, na qualidade de outorgante(s) ", "FIDUCIANTE(s),", false, true);
			
			int iParticipante = 1;
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					
					if(CommonsUtil.semValor(objetoCcb.getCpfEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
				}
				
				if(participante.isFiduciante()) {
					participante.setTipoParticipante("FIDUCIANTE");
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);
	
				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(iParticipante + ")");
				run.addTab();
				run.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
				run.setBold(true);
	
				run2 = paragraph.createRun();
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();
	
				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();
	
					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else {
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					}
	
					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);
	
					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}
	
				iParticipante++;
				} else {
					participante.setTipoParticipante("DEVEDOR");
				}
			}
			
			fazParagrafoSimples(document, paragraph, run, "De outro lado, na qualidade de outorgada fiduciária, ", false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(iParticipante + ")");
			run.addTab();
			run.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A., ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("instituição financeira, inscrita no CNPJ/MF sob"
					+ " nº 34.337.707/0001-00, com sede na Av. Paulista,"
					+ " 1765, 1º Andar, CEP 01311-200, São Paulo, SP, neste ato,"
					+ " representada na forma do seu Estatuto Social (“");
			run2.setBold(false);
			
			iParticipante++;
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”, e quando em conjunto com o ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S), ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("doravante denominadas “");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText( "PARTES");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("” e, isoladamente, “");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("PARTE");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”).");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				if(!participante.isFiduciante()) {
	
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);
	
				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(iParticipante + ")");
				run.addTab();
				run.setText("DEVEDOR: " + participante.getPessoa().getNome().toUpperCase() + ", ");
				run.setBold(true);
	
				run2 = paragraph.createRun();
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();
	
				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();
	
					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else {
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					}
	
					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);
	
					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}
				
				iParticipante++;
				} 
			}
			
			fazParagrafoSimples(document, paragraph, run, "CONSIDERANDO QUE: ", true);
			
			CTNumbering cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold);
			CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);
	
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
			XWPFNumbering numbering = document.createNumbering();
			BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
			BigInteger numID = numbering.addNum(abstractNumID);
			
			//criarParagrafo(document, paragraph, ParagraphAlignment.BOTH, numID);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Em ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(" o ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIANTE " + objetoCcb.getNomeEmitente().toUpperCase() );
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(" emitiu a(s) Cédula(s) de Crédito Bancário nº ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(objetoCcb.getNumeroCcb() + " ");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("em favor da ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIÁRIA");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", com as características previstas na Cláusula 3ª abaixo "
					+ "(“CCB(s)”), passando a ser devedora da totalidade do valor principal, juros "
					+ "remuneratórios e encargos, presentes e futuros, principais e acessórios "
					+ "decorrentes do referido título (“Obrigações Garantidas”);");
			run.setBold(false);
			run.addCarriageReturn();
			
			geraParagrafoBulletListComposta(document, paragraph, run, run2, "As obrigações, pecuniárias ou não,"
					+ " previstas na(s) CCB(s) são garantidas pela alienação fiduciária de Imóvel(eis) descrito"
					+ " abaixo bem como registrado(s) perante o "+ objetoCcb.getCartorioImovel() +"° Cartório de Registro de Imóveis da "
					+ "Comarca de "+ objetoCcb.getCidadeImovel() +" – "+ objetoCcb.getUfImovel() +" “RGI”, de propriedade do(s) ", "FIDUCIANTE(S).", false, true, numID, UnderlinePatterns.NONE);
			
	
			geraParagrafoBulletList(document, paragraph, run, numID , "Nos termos da(s) CCB(s), o protocolo da garantia "
					+ "de Alienação Fiduciária junto ao RGI é condição precedente ao seu desembolso devendo o "
					+ "registro ser concluído no prazo de até 30(trinta) dias contados da emissão da CCB sob pena"
					+ " de vencimento antecipado do referido título;", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID , "A presente garantia de Alienação Fiduciária é celebrada"
					+ " sem prejuízo das outras garantias constituídas ou que venham a ser constituídas em favor da(s) CCB(s);", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID , "As Partes dispuseram de tempo e condições adequadas para "
					+ "a avaliação e discussão de todas as cláusulas desta Alienação Fiduciária (abaixo definido), "
					+ "cuja celebração, execução e extinção são pautadas pelos princípios da igualdade, probidade,"
					+ " lealdade e boa-fé.", false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Resolvem, na melhor forma de direito, celebrar o presente ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Instrumento Particular de Alienação Fiduciária de Bens Imóveis em Garantia e Outras Avenças ");
			run2.setBold(false);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(“");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Contrato de Alienação Fiduciária");
			run2.setBold(false);
			run2.setItalic(false);
			run2.setUnderline(UnderlinePatterns.SINGLE); 
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("”), que se regerá pelas cláusulas a seguir redigidas e demais disposições,"
					+ " contratuais e legais, aplicáveis. ");
			run.setBold(false);
			run.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA PRIMEIRA – DO OBJETO", true);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.1 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Em garantia do cumprimento das Obrigações Garantidas, "
					+ "nesta data representadas pela(s) CCB nº ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(objetoCcb.getNumeroCcb() + " ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("melhor descritas na clausula 2ª abaixo, o(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("aliena(m) fiduciariamente, em favor da ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA, ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("o(s) Imóvel(eis) de sua titularidade e de seguinte descrição: ");
			run2.setBold(false);
			
			int iImagem = 0;
			for (UploadedFile imagem : filesList) {
				run3 = paragraph.createRun();
				run3.addCarriageReturn();
				this.populateFiles(iImagem);
				run3.addPicture(bis, fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
				run3.addCarriageReturn();
				iImagem++;
			}
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Objeto da matrícula nº "+ objetoCcb.getNumeroImovel() +" (“Bem Imóvel” ou “Imóvel”), "
					+ "registrada perante o "+ objetoCcb.getCartorioImovel() +"° Cartório de Registro de Imóveis da "
					+ "Comarca de "+ objetoCcb.getCidadeImovel() +" – "+ objetoCcb.getUfImovel() +" (");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("“RGI”");
			run.setBold(false);
			run.setItalic(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("(“Bem(ns) Imóvel(eis) ou Imóvel(eis)”) bem conforme identificado no ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Anexo I ");
			run.setBold(true);
			run.setItalic(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("ao presente (“");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Alienação Fiduciária");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”). ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "1.2 ", "Se solteiro(a), viúvo(a), divorciado(a)"
					+ " ou separado(a) judicialmente, declara, sob responsabilidade civil e criminal, que o imóvel "
					+ "aqui objetivado não foi adquirido na constância de união estável prevista na Lei nº 9.278,"
					+ " de 10/05/96 e no Código Civil, razão pela qual é seu único e exclusivo proprietário.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "1.3 ", "O(s) FIDUCIANTE(S), declara(m), "
					+ "sob as penas da lei, que não está(ão) vinculado(s) como empregador(es) ao INSS - Instituto"
					+ " Nacional do Seguro Social, bem como não ser(em) produtor(es) rural(is), não estando, assim,"
					+ " incurso(s) nas restrições da legislação pertinente, dispensando a apresentação de Certidão "
					+ "Negativa de Débitos – CND. Todavia, na hipótese de ser(em) contribuinte(s) desse órgão,"
					+ " declara(m) ciente(s) e responsável(eis) pela apresentação da CND-INSS ao Cartório de "
					+ "Registro de Imóveis.", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.4 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("A transferência da propriedade fiduciária do(s) Imóvel(eis), pelo(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("à ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("se opera com o registro desta Alienação Fiduciária no "
					+ "competente Cartório de Registro de Imóveis indicado na "
					+ "descrição acima e subsistirá, durante seu prazo de vigência,"
					+ " até o cumprimento válido e eficaz da totalidade das"
					+ " Obrigações Garantidas. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "1.5 ", "Obriga(m)-se o(s) FIDUCIANTE(S),"
					+ " seus herdeiros e sucessores a qualquer título das Partes a providenciar o "
					+ "registro do presente instrumento, com a constituição da Alienação Fiduciária "
					+ "aqui prevista, e averbação da CCB na matrícula do Imóvel objeto da garantia,"
					+ " no prazo de 30 (trinta) dias a contar de sua assinatura, sob pena deste"
					+ " CONTRATO ser considerado automaticamente resolvido, independentemente"
					+ " de qualquer notificação prévia ou outra formalidade, hipótese em que "
					+ "não serão devidas quaisquer indenizações ao(s) EMITENTE(S). Nesta hipótese,"
					+ " o(s) EMITENTE(S) deverá(ão) ressarcir o CREDOR das despesas de custo de"
					+ " emissão da CCB e outras despesas decorrentes desta no prazo máximo de "
					+ "48 (quarenta e oito) horas contadas da data em que for(em) notificado(s) "
					+ "para tanto, sob pena de sofrer(em) execução específica.", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.6 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Em ocorrendo a cessão, endosso ou qualquer outra forma de transferência"
					+ " da(s) CCB(s) e/ou dos créditos dela oriundos à terceiros(“");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Sucessores");
			run.setBold(false);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”), referidos Sucessores passarão a ser os legítimos titulares e beneficiários"
					+ " da presente Alienação Fiduciária, de forma que toda menção à ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("deverá ser interpretada como referindo-se aos Sucessores"
					+ " (efetivos titulares dos créditos, conforme constante do SNA da CETIP)"
					+ " e sendo certo, ainda, que todas as disposições do presente contrato"
					+ " serão mantidas.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA SEGUNDA – DOS REQUISITOS DO ARTIGO 24º DA LEI 9514/1997", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "2.1 ", "As Partes declaram, para os fins do artigo 24 da Lei nº 9.514/1997, "
					+ "que as Obrigações Garantidas apresentam as exatas características principais indicadas na abaixo: ", true, false);
			
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold_Roman);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
	
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Obrigação Garantida:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" Cédula de Crédito Bancário nº " + objetoCcb.getNumeroCcb() + " ");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor do Principal da Dívida:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" a soma do saldo devedor das Obrigações Garantidas,"
					+ " na data do leilão, nele incluídos os juros convencionais, "
					+ "as penalidades e os demais encargos contratuais conforme"
					+ " termos da clausula 5.7 deste instrumento; ");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor do Crédito:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			valorPorExtenso.setNumber(objetoCcb.getValorCredito()); 
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+ CommonsUtil.formataValorMonetario(objetoCcb.getValorCredito(), "R$ ") + " ("+ valorPorExtenso.toString() +");");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Condições de Pagamento:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(objetoCcb.getNumeroParcelasPagamento()));
			valorPorExtenso.setNumber(objetoCcb.getMontantePagamento()); 
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+ objetoCcb.getNumeroParcelasPagamento() +" ("+ numeroPorExtenso.toString() +") parcelas,"
					+ " sendo a 1ª. parcela com vencimento em "+ CommonsUtil.formataData(objetoCcb.getVencimentoPrimeiraParcelaPagamento(), "dd/MM/yyyy")  +""
					+ " e a última parcela com vencimento em "+ CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy")  +","
					+ " totalizando o montante de "+ CommonsUtil.formataValorMonetario(objetoCcb.getMontantePagamento(), "R$ ") +" ("+ valorPorExtenso.toString() +");");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Encargos Financeiros:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
	
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("(X) ");
			run2.setBold(false);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Pré-fixado");
			run.setBold(true);
			run.setItalic(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", calculado com base no ano de 365 dias;");
			run2.setBold(false);
			run2.setItalic(true);
			run2.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("(X) ");
			run2.setBold(false);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Pós-fixado");
			run.setBold(true);
			run.setItalic(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(": atualização dos valores pela variação mensal do Índice "
					+ "Nacional de Preços ao Consumidor Amplo – IPCA/IBGE, apurado "
					+ "a partir da data de emissão até a efetiva quitação da CCB;");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Taxa de Juros Efetiva: ");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Mes: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(objetoCcb.getTaxaDeJurosMes()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.addTab();
			run.setUnderline(UnderlinePatterns.NONE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Ano: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(objetoCcb.getTaxaDeJurosAno()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.NONE);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Custo Efetivo Total (“CET”)");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Mes: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(objetoCcb.getCetMes()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.addTab();
			run.setUnderline(UnderlinePatterns.NONE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Ano: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(objetoCcb.getCetAno()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.NONE);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Data de Emissão:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+ CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy") +";");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Data de Vencimento:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+CommonsUtil.formataData(objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy")+"." );
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Clausula de Constituição da Propriedade Fiduciária:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 1.1 deste instrumento;");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Clausula assegurando o Fiduciante – enquanto adimplente - ao uso do Bem(ns) Imóvel(eis):");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 3.9. deste instrumento;");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Indicação, para efeito de venda em público leilão, do valor do imóvel -");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 6.1 deste instrumento e");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Cláusula dispondo sobre os procedimentos de que trata o art. 27 da Lei 9514/97:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 5ª deste instrumento");
			run2.setBold(false);
			run2.setItalic(true);
			run2.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA TERCEIRA – DAS CARACTERÍSTICAS DA GARANTIA FIDUCIÁRIA", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.1. ", "Inicialmente as Partes fazem constar que a"
					+ " presente garantia é constituída nos termos da Lei 9514/97 e suas atualizações e que, com base "
					+ "na autorização constante no parágrafo primeiro do artigo 22 da referida lei, não é firmada no "
					+ "âmbito de operação de financiamento imobiliário operado pelo SFI. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.2. ", "As Partes anuem e o(s) FIDUCIANTE(s) "
					+ "ratificam que, entende-se por Obrigações Garantidas a totalidade da(s) cédula(s) de crédito"
					+ " bancário que contenham a presente garantia fiduciária constituída em garantia"
					+ " (“Garantia Fiduciária”).", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.3. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Ficará a cargo do FIDUCIANTE(S) realizar o "
					+ "registro da Alienação Fiduciária do Imóvel(eis) na(s) respectiva(s) matrícula(s) do(s)"
					+ " Imóvel(eis) perante o Cartório de Registro de Imóveis competente nos prazos estabelecidos "
					+ "entre as Partes ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setBold(false);
			run.setText("sendo tal descumprimento considerado como hipótese de vencimento antecipado das Obrigações Garantidas.");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.4. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("As Partes desde já se obrigam a disponibilizar, "
					+ "apresentar documentos e praticar os atos que vierem a ser"
					+ " necessários para formalizar o registro da Alienação Fiduciária"
					+ " (“Obrigações para Registro”) e, nesse sentido ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setBold(false);
			run.setText("declaram anuência de que qualquer ação ou omissão realizada no sentindo "
					+ "de prejudicar a efetiva constituição da Garantia Fiduciária será considerada "
					+ "também como hipótese de vencimento antecipado das Obrigações Garantidas.");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.5. ", "A presente Garantia Fiduciária "
					+ "compreende a propriedade fiduciária do Imóvel(eis) e todas as acessões, "
					+ "melhorias e benfeitorias existentes. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.6. ", "O(s) FIDUCIANTE(S) se obriga(m) "
					+ "a manter o Imóvel(eis) ora alienado fiduciariamente nos termos deste instrumento,"
					+ " em perfeito estado de segurança e utilização, além de realizar todas as obras,"
					+ " reparos e benfeitorias necessárias. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.7. ", "Mediante o registro da presente"
					+ " Alienação Fiduciária na(s) matrícula(s) do(s) Imóvel(eis), estará constituída a"
					+ " propriedade fiduciária sobre o(s) Imóvel(eis) em nome do FIDUCIÁRIA, efetivando-se"
					+ " o desdobramento da posse e tornando-se o(s) FIDUCIANTE(S) possuidor(es) direto(s)"
					+ " com direito à utilização do(s) Imóvel(eis) e a FIDUCIÁRIA, ou os Sucessores,"
					+ " conforme o caso, possuidores indiretos do(s) Imóvel(eis).", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.8. ", "A posse direta de que fica "
					+ "investida o(s) FIDUCIANTE(S) manter-se-ão até o adimplemento total das Obrigações "
					+ "Garantidas e enquanto estas permanecerem adimplidas, obrigando-se o(s) FIDUCIANTE(S)"
					+ " a manter, conservar e guardar o(s) Imóvel(eis), pagar pontualmente todos os tributos,"
					+ " taxas e quaisquer outras contribuições ou encargos que incidam ou venham "
					+ "a incidir sobre estes ou que sejam inerentes à Garantia Fiduciária..", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.9. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Para fins de atendimento ao inciso V do artigo 24º da Lei 9.514/97,"
					+ " as Partes anuem que é assegurado ao(s) FIDUCIANTE(S) titular do(s) Imóvel(eis),"
					+ " enquanto adimplente(s), a livre utilização, por sua conta e risco do(s) Imóvel(eis). ");
			run2.setUnderline(UnderlinePatterns.SINGLE);
			run2.setBold(true);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.10. ", "Caso o(s) FIDUCIANTE(S) não pague(m)"
					+ " pontualmente todos os tributos, despesas e encargos relativos ao(s) Imóvel(eis), a FIDUCIÁRIA,"
					+ " ou os Sucessores, conforme o caso, poderão, a seu critério, pagar tais tributos,"
					+ " despesas e encargos e solicitar o correspondente reembolso, que deverá ser feito dentro "
					+ "de 15 (quinze) dias de solicitação neste sentido, sob pena de, sobre o valor em atraso, "
					+ "incidirem juros moratórios de 1% (um por cento) ao mês, ou fração de mês em atraso, mais "
					+ "correção monetária de acordo com o IPCA/IBGE, tudo calculado desde a data de vencimento "
					+ "até a data do respectivo pagamento, além de multa não compensatória de 2% (dois por cento)"
					+ " sobre o valor em atraso.  ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.11. ", "A FIDUCIÁRIA, ou os Sucessores, "
					+ "conforme o caso, reservam-se ao direito de, a qualquer tempo, com periodicidade não"
					+ " inferior à trimestral e mediante aviso com 5 (cinco) dias de antecedência, exigir "
					+ "comprovantes de pagamento dos referidos encargos fiscais e/ou tributários, ou de quaisquer "
					+ "outras contribuições, ou ainda, conforme o caso, a comprovação de questionamentos"
					+ " administrativo e/ou judicial referentes a valores eventualmente não pagos, relacionados"
					+ " com os tributos incidentes. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.12. ", "O(s) FIDUCIANTE(S) titular(es)"
					+ " do(s) Imóvel(eis) declara(m) e informa(m) que o(s) Bem(ns) Imóvel(eis) outorgado(s) "
					+ "em garantia não é(são) nem faz(em) parte de bem de família de maneira que ratificam que,"
					+ " caso em algum momento da vigência das Obrigações Garantidas tal condição venha a ser "
					+ "contestada, servirá a presente clausula como RENÚNCIA aos benefícios de tal natureza. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.13. ", "O(s) FIDUCIANTE(S) titular(es) do(s) "
					+ "Imóvel(eis) também declaram que o(s) Bem(ns) Imóvel(eis) não conta(m) com usufruto em nome "
					+ "de terceiros se responsabilizando pelas penas impostas, inclusive indenizatórias, aos"
					+ " que declaram condições que não contemplam a realidade dos fatos.", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA QUARTA – DA CONSTITUIÇÃO DA MORA E DO INADIMPLEMENTO – "
					+ "PROCEDIMENTOS DO ARTIGO 26º DA LEI 9514/1997", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.1. ", "Nos termos do artigo 26 da Lei nº 9.514/1997, "
					+ "vencida e não paga, no todo ou em parte as Obrigações Garantidas, consolidar-se-á, a propriedade do(s) "
					+ "Imóvel(eis) em nome da FIDUCIÁRIA, observadas as disposições a seguir. ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("4.2. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Para fins do parágrafo 3º mesmo artigo, as Partes convencionam que, ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("decorrido o prazo de 15(quinze) dias corridos da data de vencimento"
					+ " parcial ou total de qualquer dos títulos representativos das Obrigações"
					+ " Garantidas (“Prazo de Carência”),");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" a FIDUCIÁRIA, ou os eventuais sucessores, conforme o caso, "
					+ "poderá, a seu critério, iniciar o procedimento de excussão da presente"
					+ " Garantia Fiduciária através da intimação do(s) FIDUCIANTE(S) nos "
					+ "termos do artigo 26, § 1º da Lei nº 9.514/1997.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.3. ", "O simples pagamento "
					+ "do principal ou de parte dos valores atrasados, sem encargos pactuados, "
					+ "não exonerará o(s) FIDUCIANTE(S) OU DEVEDOR, da responsabilidade de "
					+ "liquidar(em) tais obrigações, continuando em mora para todos os efeitos "
					+ "legais, contratuais e da excussão iniciada;", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4. ", "O procedimento de "
					+ "intimação para pagamento obedecerá aos seguintes requisitos:", true, false);
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold2);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "A intimação será requerida pela FIDUCIÁRIA, "
					+ "ou por seu sucessor conforme o caso, ao Oficial do Serviço de Registro de Imóveis competente,"
					+ " indicando o valor total das obrigações garantidas decorrentes da(s) CCB(s) vencidas e não pagas;", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "A intimação far-se-á pessoalmente ao(s) FIDUCIANTE(S)"
					+ " e será realizada pelo Oficial do Serviço de Registro de Imóveis da circunscrição imobiliária onde "
					+ "se localizar o Imóvel(eis), podendo, a critério do Oficial, vir a ser realizada por seu preposto ou"
					+ " por meio do Serviço de Registro de Títulos e Documentos da respectiva comarca da situação do Imóvel(eis),"
					+ " ou, a critério da FIDUCIÁRIA por meio do Serviço de Registro de Títulos e Documentos  do domicílio de"
					+ " quem deva recebê-la, ou, ainda, pelo correio, com aviso de recebimento a ser firmado pelo(s) FIDUCIANTE(S),"
					+ " ou por quem deva receber a intimação;", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Quando se tratar de pessoa jurídica, a intimação será feita"
					+ " ao(s) representantes ou a procuradores regularmente constituídos pelo(s) FIDUCIANTE(S);", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Nos termos da Lei nº 13.465/2017, quando, por duas vezes,"
					+ " o Oficial de Registro de Imóveis ou de Registro de Títulos e Documentos ou o serventuário por eles"
					+ " credenciado ou o Oficial Registro de Títulos e Documentos  do domicilio do(s) FIDUCIANTE(S) "
					+ "houver procurado o(s) FIDUCIANTE(S) titular(es) do(s) Imóvel(eis) em seu domicílio ou residência "
					+ "sem o encontrar, deverá, havendo suspeita motivada de ocultação, intimar qualquer pessoa da família "
					+ "ou, em sua falta, qualquer vizinho de que, no dia útil imediato, retornará ao imóvel, a fim de efetuar"
					+ " a intimação, na hora que designar, aplicando-se subsidiariamente o disposto nos arts. 252, 253 e 254 "
					+ "da Lei no 13.105, de 16 de março de 2015 (Código de Processo Civil);", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Nos condomínios edilícios ou outras espécies de "
					+ "conjuntos imobiliários com controle de acesso, a intimação poderá ser feita ao funcionário da"
					+ " portaria responsável pelo recebimento de correspondência; e", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Quando o(s) FIDUCIANTE(ES), ou seu representante"
					+ " legal ou procurador encontrar-se em local ignorado, incerto ou inacessível, o fato será "
					+ "certificado pelo serventuário encarregado da diligência e informado ao oficial de Registro "
					+ "de Imóveis, que, à vista da certidão, promoverá a intimação por edital publicado durante 3 "
					+ "(três) dias, pelo menos, em um dos jornais de maior circulação local ou noutro de comarca "
					+ "de fácil acesso, se no local não houver imprensa diária, contado o prazo para purgação da"
					+ " mora da data da última publicação do edital;", false);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Os FIDUCIANTES constituem-se bastantes procuradores, uns dos outros, "
					+ "outorgando-se mutuamente poderes gerais, podendo qualquer um deles receber citações,"
					+ " intimações, comunicações, notificações, acordar, negociar, quitar, dar e receber,"
					+ " em nome um do outro, encarregando-se de dar ciência à outra parte de quaisquer"
					+ " obrigações decorrentes da CCB e da presente garantia");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", não podendo qualquer um deles alegar desconhecimento do que a outra parte"
					+ " fez e/ou realizou em relação ao presente instrumento e em especial receber "
					+ "todas as intimações decorrentes da Lei 9514/97, promovidas dor Cartório de Registro "
					+ "de Imóveis ou outro autorizado em lei, sem exceção.”;");
			run2.setBold(true);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.5. ", "Purgada a mora, perante o"
					+ " Cartório de Registro de Imóvel(eis) competente, a presente Alienação Fiduciária"
					+ " se restabelecerá, caso ainda exista(m) Obrigações Garantidas. Nesta hipótese, "
					+ "nos 3 (três) dias seguintes, o Oficial entregará à FIDUCIÁRIA, ou aos Sucessores,"
					+ " conforme o caso, as importâncias recebidas, deduzidas as despesas de cobrança e"
					+ " de intimação.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.6. ", "O não pagamento, pelo(s) FIDUCIANTE(S)"
					+ " de qualquer valor devido pelas Obrigações Garantidas vencidas e não pagas, depois de"
					+ " devidamente comunicada nos termos da intimação tratada acima, bastará para a configuração"
					+ " da não purgação da mora. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.7. ", "Não havendo purgação da mora, "
					+ "o Oficial do Cartório de Registro de Imóvel(eis) certificará o fato e promoverá a "
					+ "averbação, na matrícula do(s) Imóvel(eis), da consolidação da propriedade do(s) "
					+ "Imóvel(eis) em nome da FIDUCIÁRIA, cabendo a esta, apresentar o comprovante de"
					+ " recolhimento do respectivo Imposto sobre Transmissão de Bens Imóveis – ITBI e,"
					+ " se for o caso, do laudêmio.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.7.1 ", "O(s) FIDUCIANTE(s) pode(rão),"
					+ " com a anuência da FIDUCIÁRIA, dar seu direito eventual ao imóvel em pagamento da dívida,"
					+ " dispensados os procedimentos previstos no art. 27º da Lei 9.514/1997.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.7.2 ", "Até a data da averbação"
					+ " da consolidação da propriedade fiduciária, é assegurado ao(s) FIDUCIANTE(S) ou DEVEDOR,"
					+ " quando aplicável, pagar as parcelas da dívida vencidas e as despesas de que trata o"
					+ " inciso II do § 3o do art. 27, hipótese em que convalescerá o contrato de Alienação"
					+ " Fiduciária.", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA QUINTA – DOS LEILÕES PÚBLICOS EXTRAJUDICIAIS E PROCEDIMENTOS DO ARTIGO 27º DA LEI 9514/97 ", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.1. ", "Consolidada a propriedade do(s)"
					+ " Imóvel(eis) em nome da FIDUCIÁRIA, esta promoverá os públicos leilões, extrajudicialmente,"
					+ " para alienação em questão, no prazo de 30 (trinta) dias contados do registro da referida"
					+ " consolidação. ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.2. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se no primeiro público leilão, o maior "
					+ "lance oferecido for inferior ao ");
			run2.setBold(false);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor do Imóvel");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" leiloado, conforme definição na clausula 6ª abaixo, será realizado o segundo leilão,"
					+ " nos 15 (quinze) dias seguintes.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.3. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("No segundo leilão, será aceito o maior lance oferecido, desde que igual ou superior ao ");
			run2.setBold(false);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor da Dívida");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", das despesas, dos prêmios de seguro, dos encargos legais, inclusive tributos,"
					+ " e das contribuições condominiais. ");
			run2.setBold(false);
			run2.addCarriageReturn();			
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.4. ", "Os leilões públicos extrajudiciais"
					+ " (primeiro e segundo) serão anunciados em edital único, resumido,"
					+ " por três vezes em jornal de ampla circulação na Comarca da situação do(s)"
					+ " Imóvel(eis) ou em outro de comarca de fácil acesso, se, no local do(s) Imóvel(eis) "
					+ "não houver imprensa com circulação diária; ", true, false);
			
			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "5.4.1. ", "Diante do acima exposto obriga-se o"
					+ " FIDUCIANTE a manter seus dados de notificação atualizados de forma que, caso não o faça,"
					+ " as notificações serão endereçadas aos seguintes endereços abaixo:", true, false);
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				if(participante.isFiduciante()) {
					if(!fiducianteGerado) {
						paragraph = document.createParagraph();
						paragraph.setAlignment(ParagraphAlignment.BOTH);
						paragraph.setSpacingBefore(0);
						paragraph.setSpacingAfter(0);
						paragraph.setSpacingBetween(1);
	
						run = paragraph.createRun();
						run.setFontSize(12);
						run.addCarriageReturn();
						run.setText("Pelo FIDUCIANTE:");
						run.setBold(false);
						run.setUnderline(UnderlinePatterns.SINGLE);
						fiducianteGerado = true;
					}
		
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);
	
				run = paragraph.createRun();
				run.addCarriageReturn();
				run.setFontSize(12);
				run.setText(participante.getPessoa().getNome());
				run.setBold(true);
				run.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText(participante.getPessoa().getEndereco() + ", n° " + participante.getPessoa().getNumero() + ", " + participante.getPessoa().getCidade() + " - " + participante.getPessoa().getEstado());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("CEP " + participante.getPessoa().getCep());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("E-mail: " + participante.getPessoa().getEmail());
				run2.setBold(false);
				
				} else {
					if(!devedorGerado) {
						paragraph = document.createParagraph();
						paragraph.setAlignment(ParagraphAlignment.BOTH);
						paragraph.setSpacingBefore(0);
						paragraph.setSpacingAfter(0);
						paragraph.setSpacingBetween(1);
	
						run = paragraph.createRun();
						run.setFontSize(12);
						run.addCarriageReturn();
						run.setText("Pelo DEVEDOR:");
						run.setBold(false);
						run.setUnderline(UnderlinePatterns.SINGLE);
						devedorGerado = true;
					}
		
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);
	
				run = paragraph.createRun();
				run.addCarriageReturn();
				run.setFontSize(12);
				run.setText(participante.getPessoa().getNome());
				run.setBold(true);
				run.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText(participante.getPessoa().getEndereco() + ", n° " + participante.getPessoa().getNumero() + ", " + participante.getPessoa().getCidade() + " - " + participante.getPessoa().getEstado());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("CEP " + participante.getPessoa().getCep());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("E-mail: " + participante.getPessoa().getEmail());
				run2.setBold(false);
				}
			}
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.addCarriageReturn();
			run.setFontSize(12);
			run.setText("Pela FIDUCIÁRIA:");
			run.setBold(false);
			run.setUnderline(UnderlinePatterns.SINGLE);
			fiducianteGerado = true;
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.addCarriageReturn();
			run.setFontSize(12);
			run.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A. ");
			run.setBold(true);
			run.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Av. Paulista, 1765, 1º Andar, CEP 01311-200, São Paulo, SP ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("E-mail: ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("cb@moneyp.com.br");
			run.setBold(false);
			run.setColor("0000ff");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.5. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Após a averbação da consolidação da propriedade fiduciária no "
					+ "patrimônio da FIDUCIÁRIA e até a data da realização do segundo leilão,"
					+ " é assegurado aos FIDUCIANTE(S) o direito de preferência para adquirir"
					+ " o(s) Imóvel(eis) por preço correspondente ao ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor da Dívida");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", somado aos encargos, dos prêmios de seguro, dos encargos legais,"
					+ " inclusive tributos, e das contribuições condominiais, aos valores "
					+ "correspondentes ao imposto sobre transmissão inter vivos e ao laudêmio,"
					+ " se for o caso, pagos para efeito de consolidação da propriedade "
					+ "fiduciária no patrimônio da FIDUCIÁRIA, e às ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Despesas ");
			run.setBold(true);
	
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("inerentes ao procedimento de cobrança e leilão, incumbindo,"
					+ " também, ao(s) FIDUCIANTE(S) o pagamento dos encargos tributários"
					+ " e despesas exigíveis para a nova aquisição do(s) Imóvel(eis), de "
					+ "que trata este parágrafo, inclusive custas, impostos e emolumentos. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.6. ", "Para os fins do disposto na cláusula 5.5."
					+ " deste instrumento, as datas, horários e locais dos leilões serão comunicados ao devedor "
					+ "mediante correspondência dirigida aos endereços constantes do contrato, inclusive ao endereço"
					+ " eletrônico.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.7. ", "Para os fins do disposto no artigo 27º"
					+ " da Lei 9.514/1997, entende-se por: ", true, false);
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold_Roman_NoLeft_NoHanging);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			geraParagrafoBulletListComposta(document, paragraph, run, run2, "– Valor da Dívida: ", "a soma do saldo devedor das"
					+ " operações representativas das Obrigações Garantidas, na data do leilão, nele incluídos os juros "
					+ "convencionais, as penalidades e os demais encargos contratuais; ", true, false, numID, UnderlinePatterns.NONE);
			
			geraParagrafoBulletListComposta(document, paragraph, run, run2, "– Despesas: ", "a soma das importâncias correspondentes aos"
					+ " encargos e custas de intimação, e as necessárias à realização do público leilão, nestas compreendidas"
					+ " as relativas aos anúncios, publicações de editais, à comissão do leiloeiro, avaliações e perícias,"
					+ " Imposto sob transmissão recolhido para fins de consolidação da propriedade bem como, adicionalmente,"
					+ " honorários advocatícios extrajudiciais no importe de 20%(vinte por cento) sob o Valor da Dívida"
					+ " relacionados aos procedimentos de cobrança.  ", true, false, numID, UnderlinePatterns.NONE);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.8. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Nos ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("cinco dias que se seguirem à venda do(s) Imóvel(eis)");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" no leilão, a FIDUCIÁRIA entregará ao(s) FIDUCIANTE(S) a importância que sobejar, "
					+ "considerando-se nela compreendido o valor da indenização de benfeitorias, depois de deduzidos"
					+ " o Valor da Dívida e das Despesas e encargos aplicáveis, fato esse que importará em recíproca "
					+ "quitação, não se aplicando o disposto na parte final do art. 516 do Código Civil.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.9. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se, no segundo leilão, o maior lance oferecido não for igual ou superior ao ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor da Dívida");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" somado às Despesas e dos Encargos, considerar-se-á extinta a dívida "
					+ "e exonerada a FIDUCIÁRIA da obrigação de entregar ao(s) FIDUCIANTE(S) o sobejo"
					+ " retratado na clausula acima. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.9.1. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Na hipótese dessa clausula, a FIDUCIÁRIA, ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("no prazo de cinco dias a contar da data do segundo leilão");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", dará ao(s) FIDUCIANTE(S) quitação da dívida, mediante termo próprio.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.10. ", "Se o(s) Imóvel (eis) estiver(em) locado(s),"
					+ " a locação poderá ser denunciada com o prazo de 30(trinta) dias para desocupação, salvo se"
					+ " tiver havido aquiescência por escrito da FIDUCIÁRIA, devendo a denúncia ser realizada no "
					+ "prazo de 90(noventa) dias a contar da data da consolidação da propriedade na FIDUCIÁRIA, "
					+ "devendo essa condição constar expressamente em cláusula contratual específica, destacando-se"
					+ " das demais por sua apresentação gráfica.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.11. ", "A critério do CREDOR,"
					+ " poderá ser realizada a alteração de propriedade do imóvel no contrato de aluguel,"
					+ " mediante aditivo próprio que independerá de notificação ou anuência do DEVEDOR, "
					+ "caso em que os alugueis serão devidos ao CREDOR desde a consolidação.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.12. ", "Responde o(s) FIDUCIANTE(S)"
					+ " pelo pagamento dos impostos, taxas, contribuições condominiais e quaisquer outros "
					+ "encargos que recaiam ou venham a recair sobre o(s) Imóvel(eis), cuja posse tenha sido "
					+ "transferida para a FIDUCIÁRIA, até a data em que a FIDUCIÁRIA vier "
					+ "a ser imitida na posse.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.13. ", "A cessão de quaisquer das Obrigações Garantidas"
					+ " implicará a transferência, ao cessionário, de todos os direitos e obrigações inerentes à propriedade"
					+ " fiduciária em garantia. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.14. ", "O(s) FIDUCIANTE(S), com anuência expressa da "
					+ "FIDUCIÁRIA, poderá transmitir os direitos de que seja titular sobre o(s) Imóvel(eis) objeto da "
					+ "alienação fiduciária em garantia, assumindo o adquirente as respectivas obrigações. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.15. ", "O(s) FIDUCIANTE(S) deverá(ão) desocupar"
					+ " o imóvel até a data da realização do primeiro público leilão, deixando-o livre e desimpedido de "
					+ "pessoas e coisas. ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.16. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Não ocorrendo a desocupação do(s) Imóvel(eis), no prazo e forma ajustados, a FIDUCIÁRIA,"
					+ " ou seus Sucessores, inclusive o adquirente do Imóvel(eis) em leilão ou posteriormente, "
					+ "poderá requerer a reintegração de sua posse cumulada com cobrança do valor da Taxa de Ocupação"
					+ " desde a data da consolidação (observado o limite máximo mensal ou por fração de 1% acima estabelecido)"
					+ " e demais despesas previstas neste Instrumento de Alienação, sendo concedida, liminarmente, a ordem "
					+ "judicial de desocupação no prazo máximo de 60 (sessenta) dias, desde que comprovada, mediante certidão"
					+ " da matrícula do Imóvel(eis) a consolidação da plena propriedade em nome da ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", ou do registro do contrato celebrado em decorrência do leilão, "
					+ "conforme quem seja o autor da ação de reintegração de posse. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.17. ", "O fiador ou terceiro"
					+ " interessado que pagar a dívida ficará sub-rogado, de pleno direito, no crédito e na propriedade "
					+ "fiduciária. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.18. ", "Na hipótese de insolvência do(s)"
					+ " FIDUCIANTE(S) fica assegurada à FIDUCIÁRIA a restituição do(s) Imóvel(eis) alienado(s) "
					+ "fiduciariamente, na forma da legislação pertinente.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.19. ", "Aplicam-se à propriedade fiduciária regida"
					+ " por este instrumento, no que couber, as disposições dos arts. 647 e 648 do Código Civil.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.20. ", "Na hipótese de desapropriação, total ou parcial,"
					+ " do(s) Imóvel(eis), a FIDUCIÁRIA, como proprietária, ainda que em caráter resolúvel, será o único e exclusivo"
					+ " beneficiário da justa e prévia indenização paga pelo poder expropriante.", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA SEXTA – DO VALOR DE VENDA DO(S) IMÓVEL(EIS) PARA FINS DE LEILÃO ", true);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.1. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("As Partes convencionam que o valor de venda total do(s) Imóvel(eis) para fins de leilão, é de  ");
			run2.setBold(false);
			
			valorPorExtenso.setNumber(objetoCcb.getVendaLeilao());
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorMonetario( objetoCcb.getVendaLeilao(), "R$ ") + " ("+ valorPorExtenso.toString() +"),");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("conforme Laudo de Avaliação (anexo) elaborado por ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(objetoCcb.getElaboradorNome() +" - CREA "+ objetoCcb.getElaboradorCrea());
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" e responsável ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(objetoCcb.getResponsavelNome() +" - CREA "+ objetoCcb.getResponsavelCrea() +", ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("o qual deverá ser devidamente atualizado pelo IGP-M/FGV, desde a data base do"
					+ " Laudo até a data de realização de cada leilão (“Valor de Venda do Imóvel(eis) em "
					+ "Leilão” ou “Valor do Imóvel(eis)”).  (novo)");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.2. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Até o pagamento integral da(s) CCB(s), a qualquer momento e "
					+ "independentemente do devido cumprimento das demais obrigações da ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE "+ objetoCcb.getNomeEmitente().toUpperCase() +" ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("contratadas no âmbito da CCB, o valor do Imóvel(eis) deverá ser equivalente a, pelo menos, ");
			run2.setBold(false);
			
			porcentagemPorExtenso.setNumber(objetoCcb.getPorcentagemImovel());
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(objetoCcb.getPorcentagemImovel()) +"% ("+ porcentagemPorExtenso.toString() +" por cento) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("do saldo devedor da CCB, acrescido dos juros remuneratórios e, conforme o caso, encargos moratórios (“Razão Mínima”).");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.3. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Na hipótese de a Razão Mínima não ser observada, a qualquer momento, ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("o(s) FIDUCIANTE(S) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("deverá(ão), no prazo de até 10 (dez) dias contados do recebimento de comunicação nesse sentido, oferecer à ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("outra garantia que este considere aceitável,"
					+ " a seu exclusivo critério, para reforço das garantias nos termos da(s) CCBs. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.4. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Nos termos do parágrafo único do artigo 24º da Lei 9.514/1997 "
					+ "atualizado pela Lei nº 13.465/2017, anuem as Partes que, ");
			run2.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("caso o Valor do Imóvel indicado na clausula 6.1 seja inferior"
					+ " ao utilizado pelo órgão competente como base de cálculo para a apuração"
					+ " do imposto sobre transmissão inter vivos, exigível por força da "
					+ "consolidação da propriedade em nome do credor fiduciário, o Valor "
					+ "Mínimo de Venda do Imóvel(eis) em Leilão deverá automaticamente "
					+ "corresponder ao valor de tal apuração.");
			run2.setBold(false);
			run2.setUnderline(UnderlinePatterns.SINGLE);
			run2.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLAUSULA SÉTIMA - DAS DISPOSIÇÕES GERAIS", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.1 ", "A tolerância por qualquer das "
					+ "Partes quanto a alguma demora, atraso ou omissão das outras no cumprimento das "
					+ "obrigações ajustadas nesta Alienação Fiduciária, ou a não aplicação, na ocasião "
					+ "oportuna, das cominações aqui constantes, não acarretará o cancelamento das penalidades,"
					+ " nem dos poderes ora conferidos, podendo ser aplicadas aquelas e exercidos estes,"
					+ " a qualquer tempo, caso permaneçam as causas. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.1.1 ", "O disposto no item 7.1, acima,"
					+ " prevalecerá ainda que a tolerância ou a não aplicação das cominações ocorra repetidas vezes,"
					+ " consecutiva ou alternadamente. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.2 ", "A ocorrência de uma ou mais hipóteses referidas"
					+ " acima não implicará novação ou modificação de quaisquer disposições	desta Alienação Fiduciária,"
					+ " as quais permanecerão íntegras e em pleno vigor, como se nenhum favor houvesse ocorrido.", true, false);
						
			geraParagrafoComposto(document, paragraph, run, run2, "7.3 ", "As obrigações constituídas por "
					+ "esta Alienação Fiduciária são extensivas e obrigatórias aos cessionários,"
					+ " promissários-cessionários, herdeiros e sucessores a qualquer título"
					+ " das Partes.  ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.4 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Na hipótese de desapropriação total ou parcial do Imóvel(eis), a ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", ou os Sucessores, conforme o caso, como proprietários do Imóvel(eis),"
					+ " ainda que em caráter fiduciário, serão os únicos e exclusivos beneficiários"
					+ " da justa e prévia indenização paga pelo poder expropriante, até o limite do"
					+ " saldo devedor das Obrigações Garantidas à época, sendo tais valores"
					+ " amortizados das Obrigações Garantidas.");
			run2.setBold(false);
			run2.addCarriageReturn();
	
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.4.1 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se, no dia de seu recebimento pela ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", ou pelos Sucessores, conforme o caso, a proporção da indenização conforme item 7.4, acima, for: ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Superior ao saldo devedor das Obrigações Garantidas à época,"
					+ " a importância que sobejar será entregue aos ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIANTE(S)");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("; ou");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Inferior ao saldo devedor das Obrigações Garantidas à época, a ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIÁRIA");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", ou os Sucessores, conforme o caso,"
					+ " ficarão exonerados da obrigação de restituição de qualquer quantia, a que título for, em favor dos ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIANTE(S)");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", pela integral liquidação das Obrigações Garantidas.");
			run.setBold(false);
			run.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.5 ", "As Partes autorizam e determinam,"
					+ " desde já, que o Sr. Oficial do Serviço de Registro de Imóveis competente proceda,"
					+ " total ou parcialmente, a todos os assentamentos, registros e averbações necessários"
					+ " decorrentes da presente Alienação Fiduciária, isentando-os de qualquer responsabilidade "
					+ "pelo devido cumprimento do disposto neste instrumento.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.6 ", "Fica desde logo estipulado que"
					+ " a presente Alienação Fiduciária revoga e substitui todo e qualquer entendimento havido"
					+ " entre as Partes anteriormente a esta data sobre o mesmo objeto. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.7 ", "Todas as comunicações entre as Partes "
					+ "serão consideradas válidas quando enviadas nos endereços constantes da cláusula 5.4.1 desta"
					+ " Alienação Fiduciária, observado, inclusive o disposto no item 4.4. alínea “g”, ou em outros"
					+ " que venham a indicar, por escrito, no curso desta relação. Cada Parte deverá comunicar "
					+ "imediatamente a outra sobre a mudança de seu endereço.", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.8 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Neste ato e como condição de celebração do presente instrumento, o(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S)");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" nomeia(m) a ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("de forma irrevogável e irretratável, para representá-lo(s) na celebração de"
					+ " escrituras de registro da presente que eventualmente se façam necessárias por"
					+ " exigência do competente Oficial de Registro de Imóveis, podendo este descrever "
					+ "e caracterizar o(s) Imóvel(eis), suas benfeitorias, perímetro e confrontantes, "
					+ "bem como cumprir alterar todo e qualquer outro item que se faça necessário, desde"
					+ " que mantidas as condições comerciais ora pactuadas, podendo inclusive substabelecer,"
					+ " com reservas os poderes ora conferidos. Ainda, o(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S)");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" compromete-se neste ato a fornecer toda a documentação necessária para tanto. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.9 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Os “");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Considerandos");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("” e os Anexos constituem partes integrantes e inseparáveis da"
					+ " presente Alienação Fiduciária, e serão considerados meios válidos"
					+ " e eficazes para fins de interpretação das Cláusulas deste. ");
			run2.setBold(false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA OITAVA – DA LEI DE REGÊNCIA E DO FORO DE ELEIÇÃO ", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "8.1 ", "A presente Alienação Fiduciária é regida,"
					+ " material e processualmente, pelas leis da República Federativa do Brasil e faz parte"
					+ " acessória da(s) CCB(s).", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "8.2 ", "Todo litígio ou controvérsia originário"
					+ " ou decorrente desta Alienação Fiduciária e dos demais Documentos da Operação será submetido"
					+ " ao Foro da Comarca de São Paulo, Estado de São Paulo, único competente para conhecer e "
					+ "dirimir quaisquer questões ou litígios, com renúncia expressa a qualquer outro, por mais"
					+ " privilegiado que seja ou venha a ser. ", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "E, por estarem assim, justas e contratadas, as Partes assinam"
					+ " a presente Alienação Fiduciária em 2 (duas) vias, de igual teor e forma, na presença das 2 (duas)"
					+ " testemunhas abaixo identificadas. ", false);
			
			fazParagrafoSimples(document, paragraph, run,
					"São Paulo, SP, " + objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (objetoCcb.getDataDeEmissao().getYear() + 1900) + ".",
					false);
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setText("(O final desta página foi intencionalmente deixado em branco)");
			run.setBold(false);
			run.setItalic(true);
			run.addCarriageReturn();
			run.setText("(Segue a página de assinaturas)");
	
			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);
			
			paragraph = document.createParagraph();	
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(Página de assinaturas do INSTRUMENTO PARTICULAR DE ALIENAÇÃO FIDUCIÁRIA DE BEM(NS) IMÓVEL(EIS) EM GARANTIA E OUTRAS AVENÇAS "
					+ "nº " + objetoCcb.getNumeroCcb() + ", emitida por "+ objetoCcb.getNomeEmitente().toUpperCase() +", CPF/MF nº "+ objetoCcb.getCpfEmitente() +", em favor de "
					+ "BMP SOCIEDADE DE CRÉDITO DIRETO S.A., CNPJ/ MF sob nº 34.337.707/0001-00,"
					+ " em "+ CommonsUtil.formataData(objetoCcb.getDataDeEmissao(), "dd/MM/yyyy" )+".)");
			run.setBold(false);
			run.setItalic(true);
			run.addCarriageReturn();
	
			XWPFTable table = document.createTable();
	
			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
	
			table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));
	
			// create first row
			XWPFTableRow tableRow1 = table.getRow(0);
	
			tableRow1.getCell(0).setParagraph(paragraph);
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("__________________________________");
			run.setBold(false);
			run.addBreak();
	
			run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A");
			run2.setBold(true);
			run2.addBreak();
	
			run4 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("FIDUCIÁRIA");
			run4.setBold(false);
	
			tableRow1.addNewTableCell();
	
			tableRow1.getCell(1).setParagraph(paragraph);
	
			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("__________________________________");
			run.setBold(false);
			run.addBreak();
	
			run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText(objetoCcb.getNomeEmitente().toUpperCase());
			run2.setBold(true);
			run2.addBreak();
	
			run3 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run3.setFontSize(12);
			run3.setText(" ");
			run3.setBold(true);
			run3.addBreak();
	
			run4 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("EMITENTE");
			run4.setBold(false);
	
			XWPFTableRow tableRow2 = table.createRow();
	
			if (objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
				int qtdePessoasEsquerdo = 0;
				for (int iPartTab = 0; iPartTab < objetoCcb.getListaParticipantes().size(); iPartTab++) {
	
					CcbParticipantes participante = objetoCcb.getListaParticipantes().get(iPartTab);
					if (iPartTab != 0) {
						if (iPartTab % 2 != 0) {
	
							run = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("__________________________________");
							run.setBold(false);
							run.addBreak();
	
							run2 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();
	
							run3 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();
	
							qtdePessoasEsquerdo++;
						} else {
							run = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("__________________________________");
							run.setBold(false);
							run.addBreak();
	
							run2 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();
	
							run3 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();
							
							qtdePessoasEsquerdo--;
						}
					}
				}
				run4 = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				run4.addBreak();
				run4.setText("Testemunhas");
				run4.setBold(false);
				run4.addBreak();
				run4.setText("__________________________________");
	
				run4 = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				for (int i = 0; i <= qtdePessoasEsquerdo; i++) {
					run4.addBreak();
					run4.addBreak();
					run4.addBreak();
				}
				run4.setText("__________________________________");
				run4.setBold(false);
	
			} else {
				tableRow2.getCell(0).setParagraph(paragraph);
				run = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.setText("Testemunhas");
				run.setBold(false);
				run.addBreak();
				run.setText("__________________________________ ");
	
				tableRow2.getCell(1).setParagraph(paragraph);
				run = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.addBreak();
				run.setText("__________________________________ ");
				run.setBold(false);
			}
	
			// create third row
			XWPFTableRow tableRow3 = table.createRow();
			tableRow3.getCell(0).setParagraph(paragraph);
			run = tableRow3.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + objetoCcb.getNomeTestemunha1());
			run.setBold(false);
	
			tableRow3.getCell(1).setParagraph(paragraph);
			run = tableRow3.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + objetoCcb.getNomeTestemunha2());
			run.setBold(false);
	
			XWPFTableRow tableRow4 = table.createRow();
			tableRow4.getCell(0).setParagraph(paragraph);
			run = tableRow4.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + objetoCcb.getRgTestemunha1());
			run.setBold(false);
	
			tableRow4.getCell(1).setParagraph(paragraph);
			run = tableRow4.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + objetoCcb.getRgTestemunha2());
			run.setBold(false);
	
			XWPFTableRow tableRow5 = table.createRow();
			tableRow5.getCell(0).setParagraph(paragraph);
			run = tableRow5.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + objetoCcb.getCpfTestemunha1());
			run.setBold(false);
	
			tableRow5.getCell(1).setParagraph(paragraph);
			run = tableRow5.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + objetoCcb.getCpfTestemunha2());
			run.setBold(false);
	
			CTTblPr tblpro = table.getCTTbl().getTblPr();
	
			CTTblBorders borders = tblpro.addNewTblBorders();
			borders.addNewBottom().setVal(STBorder.NONE);
			borders.addNewLeft().setVal(STBorder.NONE);
			borders.addNewRight().setVal(STBorder.NONE);
			borders.addNewTop().setVal(STBorder.NONE);
			// also inner borders
			borders.addNewInsideH().setVal(STBorder.NONE);
			borders.addNewInsideV().setVal(STBorder.NONE);		
			
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null) {
				headerFooterPolicy = document.createHeaderFooterPolicy();
			}
			
			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
	
			paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
	
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);
	
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");
			
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewFldChar().setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] geraNCDinamica() throws IOException{
		try {
			XWPFDocument document = new XWPFDocument();	
			
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();
	
			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun runHeader = paragraphHeader.createRun();
			runHeader.addPicture(getClass().getResourceAsStream("/resource/BMP MoneyPlus.png"), 6, "BMP MoneyPlus",
					Units.toEMU(130), Units.toEMU(72));
	
			XWPFRun run;
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("São Paulo, SP, " + objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
			run.setFontSize(10);
			run.setBold(false);
			run.addCarriageReturn();
			XWPFRun run2 = paragraph.createRun();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("À");
			run.setFontSize(10);
			run.setBold(false);
			
			for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(objetoCcb.getNomeEmitente())) {
						objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					
				}
			}
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText(objetoCcb.getNomeEmitente().toUpperCase());
			run.setFontSize(10);
			run.setBold(true);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText(objetoCcb.getEmitentePrincipal().getPessoa().getEndereco() +", nº "+ objetoCcb.getEmitentePrincipal().getPessoa().getNumero() +", "+ objetoCcb.getEmitentePrincipal().getPessoa().getComplemento());
			run.setFontSize(10);
			run.setBold(false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText(objetoCcb.getEmitentePrincipal().getPessoa().getBairro());
			run.setFontSize(10);
			run.setBold(false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText(objetoCcb.getEmitentePrincipal().getPessoa().getCidade() +" – " + objetoCcb.getEmitentePrincipal().getPessoa().getEstado());
			run.setFontSize(10);
			run.setBold(false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("CEP "+ objetoCcb.getEmitentePrincipal().getPessoa().getEstado() +";");
			run.setFontSize(10);
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("REF.: Contrato de CCI nº " + objetoCcb.getNumeroCcb());
			run.setFontSize(10);
			run.setBold(true);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Prezado(s) Cliente(s) ");
			run.setFontSize(10);
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Pela presente, levamos ao seu conhecimento que, nesta data,"
					+ " o GALLERIA FINANÇAS SECURITIZADORA S.A., inscrito no CNPJ/MF "
					+ "sob nº 34.425.347/0001-06, adquiriu da BMP SOCIEDADE "
					+ "DE CRÉDITO DIRETO S.A. os direitos de crédito, decorrentes da(s) "
					+ "Cédula(s) de Crédito Imobiliário (“CCI”) em referência, celebrado por"
					+ " V. Sa(s), dos vencimentos a partir de ");
			run.setFontSize(10);
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setText(objetoCcb.getDataDeEmissao().getDate()+"");
			run2.setFontSize(10);
			run2.setBold(true);
			run2.setUnderline(UnderlinePatterns.SINGLE);
			
			run = paragraph.createRun();
			run.setText(" de ");
			run.setFontSize(10);
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setText(CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase());
			run2.setFontSize(10);
			run2.setBold(true);
			run2.setUnderline(UnderlinePatterns.SINGLE);
			
			run = paragraph.createRun();
			run.setText(" de ");
			run.setFontSize(10);
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setText( (objetoCcb.getDataDeEmissao().getYear() + 1900)  + ",");
			run2.setFontSize(10);
			run2.setBold(true);
			run2.setUnderline(UnderlinePatterns.SINGLE);
			
			run = paragraph.createRun();
			run.setText(" (inclusive).");
			run.setFontSize(10);
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Assim, em face da operação contratada, fica(m) V. "
					+ "Sa(s) notificadas que a partir de "+ objetoCcb.getDataDeEmissao().getDate() +" de "+
					CommonsUtil.formataMesExtenso(objetoCcb.getDataDeEmissao()).toLowerCase() +" de "+ (objetoCcb.getDataDeEmissao().getYear() + 1900) +","
					+ " (inclusive), o pagamento das parcelas referentes a(s) CCI ");
			run.setFontSize(10);
			run.setBold(false);
							
			run2 = paragraph.createRun();
			run2.setText("Nº " + objetoCcb.getNumeroCcb());
			run2.setFontSize(10);
			run2.setBold(true);
			run = paragraph.createRun();
			run.setText(" deverão ser efetuados diretamente ao GALLERIA FINANÇAS SECURITIZADORA S.A.,"
					+ " na conta de nº 300793-6, mantida na agência nº 1515-6, Banco 001 - Banco do Brasil S.A.,"
					+ " ou à sua ordem.");
			run.setFontSize(10);
			run.setBold(false);
			
			
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("Qualquer alteração dos procedimentos acima descritos dependerá"
					+ " de prévia e expressa autorização do BMP SOCIEDADE DE CRÉDITO DIRETO S.A. ");
			run.setFontSize(10);
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("Atenciosamente, ");
			run.setFontSize(10);
			run.setBold(false);
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("___________________________________________________________");
			run.setFontSize(9);
			run.setBold(false);
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A");
			run.setFontSize(11);
			run.setBold(true);
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("CEDENTE");
			run.setFontSize(9);
			run.setBold(false);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("Ciente: ");
			run.setFontSize(9);
			run.setBold(false);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("___________________________________________________________");
			run.setFontSize(9);
			run.setBold(false);
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText(objetoCcb.getNomeEmitente().toUpperCase());
			run.setFontSize(11);
			run.setBold(true);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("EMITENTE");
			run.setFontSize(9);
			run.setBold(false);
			
			headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null) {
				headerFooterPolicy = document.createHeaderFooterPolicy();
			}
			
			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
	
			paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
	
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);
	
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");
			
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewFldChar().setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			return out.toByteArray();			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void clearDocumentosNovos() {
		fiducianteGerado = false;
		devedorGerado = false;
		
		for (CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			if(CommonsUtil.semValor(participante.getTipoOriginal())) {
				participante.setTipoOriginal(participante.getTipoParticipante());
			} else {
				participante.setTipoParticipante(participante.getTipoOriginal());
			}
		}
	}
	
	public void fazParagrafoSimples(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold) {
		paragraph = document.createParagraph();		
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.addCarriageReturn();
	}
	
	public void fazParagrafoSimplesSemReturn(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold) {
		paragraph = document.createParagraph();		
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
	}
	
	public void fazParagrafoSimples(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold, ParagraphAlignment alinhamento) {
		paragraph = document.createParagraph();	
		paragraph.setAlignment(alinhamento);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.addCarriageReturn();
	}
	
	public void geraParagrafoComposto(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2) {
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
		run2.addCarriageReturn();
	}
	
	public void geraParagrafoCompostoSemReturn(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2) {
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
	}
	
	public void geraParagrafoComposto(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2, ParagraphAlignment alinhamento) {
		paragraph = document.createParagraph();
		paragraph.setAlignment(alinhamento);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
		run2.addCarriageReturn();
	}
	
	public void geraParagrafoBulletList(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, BigInteger numID, String texto, boolean bold) {
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setSpacingBetween(1);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.addCarriageReturn();
	}
	
	public void geraParagrafoBulletListSemReturn(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, BigInteger numID, String texto, boolean bold) {
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setSpacingBetween(1);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
	}
	
	public void geraParagrafoBulletListComposta(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2, BigInteger numID, UnderlinePatterns underline) {
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.setUnderline(underline);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
		run2.addCarriageReturn();
	}
	
	public static String RomanNumerals(int Int) {
		LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
		roman_numerals.put("M", 1000);
		roman_numerals.put("CM", 900);
		roman_numerals.put("D", 500);
		roman_numerals.put("CD", 400);
		roman_numerals.put("C", 100);
		roman_numerals.put("XC", 90);
		roman_numerals.put("L", 50);
		roman_numerals.put("XL", 40);
		roman_numerals.put("X", 10);
		roman_numerals.put("IX", 9);
		roman_numerals.put("V", 5);
		roman_numerals.put("IV", 4);
		roman_numerals.put("I", 1);
		String res = "";
		for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
			int matches = Int / entry.getValue();
			res += repeat(entry.getKey(), matches);
			Int = Int % entry.getValue();
		}
		return res;
	}
	
	public static String repeat(String s, int n) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	static String cTAbstractNumBulletXML = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"0\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoLeft = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"1\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"360\" w:hanging=\"360\"/></w:pPr><w:rPr><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoLeft_NoHanging_bold = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"2\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"0\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoLeft_NoHanging_bold2 = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"3\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"0\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoHanging_bold = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"4\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"360\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"5\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold_Roman = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"6\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1-\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1080\" w:hanging=\"720\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:i w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1-.%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1-.%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2880\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold2 = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"7\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold_Roman_NoLeft_NoHanging= "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"8\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"0\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1.%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1.%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
}
