package com.webnowbr.siscoat.common;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ExcelTable {

	private Map<String, Map<Integer, Object>> columns;

	private int size;

	private ExcelTable() {
		super();
		this.columns = new LinkedHashMap<String, Map<Integer, Object>>(0);
	}

	public int size() {
		return this.size;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Set<String> columnSet = this.columns.keySet();
		for (String column : columnSet) {
			sb.append("\t" + column);
		}
		sb.append("\n");
		for (int row = 1; row < size; row++) {
			for (String column : columnSet) {
				Object o = getCellValue(column, row);
				if (o != null) {
					sb.append("\t" + o.toString());
				} else {
					sb.append("\t");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public Object getCellValue(String column, Integer row) {
		Object result = null;
		Map<Integer, Object> col = this.columns.get(column);
		if (col != null) {
			result = col.get(row);
		}
		return result;
	}

	public void setCellValue(String column, Integer row, Object value) {
		Map<Integer, Object> col = this.columns.get(column);
		if (col == null) {
			col = new HashMap<Integer, Object>(0);
		}
		col.put(row, value);
		this.columns.put(column, col);
	}

	private void setSize(int size) {
		this.size = size;
	}

	public static final class Builder {

		public ExcelTable build(final byte[] conteudo, final Map<String, String> defaultNamesMap) throws IOException {
			ExcelTable table = build((Object) conteudo, defaultNamesMap);
			return table;
		}

		public ExcelTable build(final String fileName, final Map<String, String> defaultNamesMap) throws IOException {
			ExcelTable table = build((Object) fileName, defaultNamesMap);
			return table;
		}

		private ExcelTable build(Object file, final Map<String, String> defaultNamesMap) throws IOException {
			List<List<Object>> loadedTable = read(file);

			loadedTable = applyDefaultNames(loadedTable, defaultNamesMap);
			ExcelTable table = setUpTable(loadedTable);
			return table;
		}

		/** Prepara leitura de de dados */
		private InputStream open(Object file) {
			InputStream is = null;
			if (file instanceof byte[]) {
				is = open((byte[]) file);
			} else {
				if (file instanceof String) {
					is = open((String) file);
				}
			}
			return is;

		}

		/** Prepara leitura de dados a partir de buffer em memória */
		private InputStream open(final byte[] conteudo) {
			final InputStream is = new ByteArrayInputStream(conteudo);
			return is;
		}

		/** Prepara leitura de dados a partir de um arquivo */
		private InputStream open(String fileName) {
			InputStream is = null;
			try {
				is = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return is;
		}

		/** Cria o ExcelTable com sua estrutura definitiva */
		private ExcelTable setUpTable(List<List<Object>> loadedTable) {

			ExcelTable table = null;
			if (loadedTable != null && loadedTable.size() > 0) {

				table = new ExcelTable();

				table.setSize(loadedTable.size());

				List<Object> header = loadedTable.get(0);

				for (int rowIndex = 1; rowIndex < loadedTable.size(); rowIndex++) {

					List<Object> row = loadedTable.get(rowIndex);

					final int maxIndex = Math.min(header.size(), row.size());
					for (int cellIndex = 0; cellIndex < maxIndex; cellIndex++) {

						Object cell = header.get(cellIndex);
						Object value = row.get(cellIndex);

						if (cell != null && value != null) {
							String columnName = (String) cell;
							table.setCellValue(columnName, rowIndex, value);
						}
					}
				}
			}
			return table;
		}

		/**
		 * Aplica uma indentificação padronizada de campos aos nomes das colunas
		 * 
		 * @param loadedTable     - estrutura com dados representando a planilha
		 * @param defaultNamesMap - mapa com a chave representando o nome a ser
		 *                        subistituido e o valor o nome padrão
		 * @return
		 */
		private List<List<Object>> applyDefaultNames(List<List<Object>> loadedTable,
				Map<String, String> defaultNamesMap) {

			if (loadedTable != null && loadedTable.size() > 0 && defaultNamesMap != null
					&& defaultNamesMap.size() > 0) {

				List<Object> header = loadedTable.get(0);

				if (header != null && header.size() > 0) {
					for (int i = 0; i < header.size(); i++) {
						Object cell = header.get(i);
						if (cell != null && cell instanceof String) {
							String columnName = (String) cell;
							String fieldDefaultName = defaultNamesMap.get(columnName.toUpperCase());
							if (fieldDefaultName != null) {
								header.set(i, fieldDefaultName);
							}
						}
					}
					loadedTable.set(0, header);
				}
			}
			return loadedTable;
		}

		/**
		 * Faz a leitura de uma planilha diretamente de um arquivo
		 * 
		 * @param is - Stream para arquivo
		 * @return estrutura com dados representando a planilha
		 */
		private List<List<Object>> read(Object file) throws IOException {

			InputStream is = null;
			List<List<Object>> loadedTable = null;
			try {
				boolean isXmlFile = false;
				// processa arquivos xls
				try {
					is = open(file);
					HSSFWorkbook workbook = new HSSFWorkbook(is);
					loadedTable = readTable(workbook);
				} catch (OfficeXmlFileException x) {
					is.close();
					is = null;
					isXmlFile = true;
				}
				// processa arquivos xlsx
				if (isXmlFile) {
					is = open(file);
					XSSFWorkbook workbook = new XSSFWorkbook(is);
					loadedTable = readTable(workbook);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return loadedTable;
		}

		/**
		 * Efetua a leitura da planilha
		 * 
		 * @param workbook - objeto que representa a planilha (a implementação fornecida
		 *                 varia segundo o tipo de palnilha: xls ou xlsx)
		 * 
		 * @return estrutura com dados representando a planilha
		 */
		private List<List<Object>> readTable(Workbook workbook) {
			List<List<Object>> loadedTable = new ArrayList<List<Object>>();
			int headerIndex = -1;
			int headerColumns = -1;
			int footerIndex = -1;
			// se necessário é possível fazer a leitura de multiplas abas
			Sheet worksheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = worksheet.rowIterator();
			int rowCounter = 0;
			// efetua a leitura das linhas
			while (rowIterator.hasNext()) {
				Row row = (Row) rowIterator.next();

				int cellCounter = 0;
				int strCounter = 0;
				int numbCounter = 0;
				List<Object> colList = new ArrayList<Object>();
				// efetua a leitura das colunas dentro de uma linha
				for (; cellCounter < row.getLastCellNum(); cellCounter++) {
					final Cell cell = row.getCell(cellCounter);
					if (cell == null) {
						// Mantendo número de elementos de listas iguais
						colList.add(cellCounter, null);
						continue;
					}
					int cellType = cell.getCellType();
					if (cellType == Cell.CELL_TYPE_STRING) {
						// Leitura de Strings
						String cellValue = cell.getStringCellValue();
						colList.add(cellCounter, cellValue);
						strCounter++;
					} else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
						// Leitura de valores numéricos
						try {
							if (!org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {

								Double cellValue = new Double(cell.getNumericCellValue());
								colList.add(cellCounter, cellValue);

							} else {
								Date cellValue = cell.getDateCellValue();
								colList.add(cellCounter, cellValue);
							}
						} catch (Exception e) {
							colList.add(cellCounter, CommonsUtil.stringValue(cell));
						}
						numbCounter++;
					} else {
						// Mantendo número de elementos de listas iguais
						colList.add(cellCounter, null);
					}
				}
				// define início dos dados
				if (headerIndex < 0 && strCounter > 2 && numbCounter == 0) {
					headerIndex = rowCounter;
					headerColumns = cellCounter;
				}
				// define final dos dados
				if ((strCounter + numbCounter) < 3) {
					if (headerIndex >= 0 && footerIndex < 0 && headerColumns > 2) {
						footerIndex = rowCounter;
					}
				} else {
					footerIndex = -1;
				}
				loadedTable.add(rowCounter, colList);
				rowCounter++;
			}
			// remove linhas anteriores ao cabeçalho
			// e define linha de cabeçalho como sendo a primeira
			int removeCounter = 0;
			for (int index = 0; index < loadedTable.size(); index++) {
				if (index < headerIndex) {
					removeCounter++;
				} else {
					break;
				}
			}
			for (int index = 0; index < removeCounter; index++) {
				loadedTable.remove(0);
				footerIndex--;
			}
			// remove linhas extras (que não contém dados)
			if (footerIndex > 0) {
				while (loadedTable.size() > footerIndex) {
					loadedTable.remove(footerIndex);
				}
			}
			return loadedTable;
		}
	}

}
