package com.webnowbr.siscoat.common;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Rotinas utilitarias gerais
 * 
 */

public class CommonsUtil {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(CommonsUtil.class);

	public static Double castAsDouble(Object value) {
		return value == null ? null : doubleValue(value);
	}

	public static Integer castAsInteger(Object value) {
		return value == null ? null : intValue(value);
	}

	public static Long castAsLong(Object value) {
		return value == null ? null : longValue(value);
	}

	public static boolean booleanValue(String object) {
		if (semValor(object)) {
			return false;
		}
		return Boolean.parseBoolean(object);
	}

	public static boolean booleanValue(Boolean object) {
		if (object == null) {
			return false;
		}
		return object.booleanValue();
	}

	public static boolean booleanValue(Integer object) {
		if (object == null) {
			return false;
		}
		return object.intValue() == 1;
	}

	public static final Integer integerValue(Object value) {
		try {
			return value == null ? null : (value instanceof Integer) ? (Integer) value : intValue(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static char charValue(Character object) {
		return object == null ? (char) 0 : object.charValue();
	}

	public static Character characterValue(char object) {
		return object;
	}

	public static Character charValue(String object) {
		Character objChr = null;
		if (!semValor(object)) {
			char[] charArray = object.toCharArray();
			objChr = charArray[0];
		}
		return objChr;
	}

	public static short shortValue(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof Number) {
			return ((Number) object).shortValue();
		}
		return Short.parseShort(object.toString());
	}

	public static int intValue(Object object) {
		if (object == null) {
			return 0;
		}
		if (object == "") {
			return 0;
		}
		if (object instanceof Number) {
			return ((Number) object).intValue();
		}

		if (object instanceof Boolean) {
			return ((Boolean) object).booleanValue() ? 1 : 0;
		}
		return Integer.parseInt(object.toString());
	}

	public static int intValue(Object object, int defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		if (object instanceof Number) {
			return ((Number) object).intValue();
		}
		int result = 0;
		try {
			result = Integer.parseInt(object.toString());
		} catch (NumberFormatException e) {
			result = defaultValue;
		}
		return result;
	}

	public static long longValue(Object object) {
		if (object == null) {
			return 0l;
		}
		if (object instanceof Number) {
			return ((Number) object).longValue();
		}
		return Long.parseLong(object.toString());
	}

	public static double doubleValue(Object object) {
		if (object == null) {
			return 0d;
		}
		if (object instanceof Number) {
			return ((Number) object).doubleValue();
		}
		return Double.parseDouble(object.toString());
	}
	
	public static BigDecimal bigDecimalValue(Object object) {
		if (object == null) {
			return BigDecimal.ZERO;
		}		
		return BigDecimal.valueOf(doubleValue(object));
	}
	

	public static Date dateValue(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof Date) {
			return ((Date) object);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(stringValue(object));
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date dateValue(Object object, String formatoData) {
		if (object == null) {
			return null;
		}
		if (object instanceof Date) {
			return ((Date) object);
		}

		SimpleDateFormat sdf = new SimpleDateFormat(formatoData);
		try {
			return sdf.parse(stringValue(object));
		} catch (ParseException e) {
			return null;
		}
	}

	public static String stringValue(Object object) {
		return object == null ? null : object.toString();
	}

	public static String stringValueVazio(Object object) {
		return object == null ? "" : object.toString();
	}
	
	public static String stringValueUTF8(String str) {
		
	        Charset utf8charset = Charset.forName("UTF-8");
	        Charset iso88591charset = Charset.forName("ISO-8859-1");

	        ByteBuffer inputBuffer = ByteBuffer.wrap(str.getBytes());

	        // decode UTF-8
	        CharBuffer data = utf8charset.decode(inputBuffer);

	        // encode ISO-8559-1
	        ByteBuffer outputBuffer = iso88591charset.encode(data);
	        byte[] outputData = outputBuffer.array();

	        return new String(outputData);
	}

	/**
	 * Ajusta valor para valor monetário (arredondado com 2 casas decimais)
	 * 
	 * @param valor
	 * @return
	 */
	public static final double valorMonetario(Object valor) {
		return getDecimalRound(doubleValue(valor), 2);
	}

	/**
	 * Ajusta valor para valor monetário (arredondado com 2 casas decimais)
	 * 
	 * @param valor
	 * @return
	 */
	public static final double valorMonetario(Double valor) {
		if (semValor(valor)) {
			valor = 0d;
		}
		return getDecimalRound(doubleValue(valor), 2);
	}

	/**
	 * Ajusta valor para valor monetário (arredondado com 2 casas decimais)
	 * 
	 * @param valor
	 * @return
	 */
	public static final double valorMonetario(double valor) {
		return getDecimalRound(valor, 2);
	}

	/**
	 * Ajusta valor para valor monetário (arredondado com 2 casas decimais)
	 * 
	 * @param valor
	 * @return
	 */
	public static final double valorMonetarioNaoNegativo(double valor) {
		return valor <= 0d ? 0d : getDecimalRound(valor, 2);
	}

	/**
	 * Arredonda um número para uma determinada quantidade de casas decimais
	 * 
	 * @param value
	 * @param decimals
	 * @return número arredondado
	 */
	public static double getDecimalRound(final double value, final int decimals) {
		final BigDecimal valorExato = new BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP);
		return valorExato.doubleValue();
	}

	public static double doubleToDecimal(final Double value, final int decimals) {
		//String valor = value.toString();
		if (semValor(value)) {
			return 0d;
		}
		String valor = formataValorMonetario(value);
		valor = valor.replace(".", "");
		
		int ponto = valor.indexOf(",");
		if (ponto >= 0) {
			String inteiro = valor.substring(0, ponto);
			String decimal = valor.substring(ponto + 1);
			decimal = somenteNumeros(decimal);
			if (decimal.length() > decimals) {
				decimal = decimal.substring(0, decimals);
			}
			valor = inteiro + '.' + decimal;
		}

		return doubleValue(valor);
	}

	public static final Integer roundAsInteger(final Double valor) {
		if (valor == null) {
			return null;
		}
		double arredondado = getDecimalRound(valor.doubleValue(), 0);
		int result = (int) arredondado;
		return result == 0 ? null : result;
	}

	public static final int compare(Date o1, Date o2) {
		if (o1 == null) {
			return o2 == null ? 0 : -1;
		} else if (o2 == null) {
			return 1;
		}
		return o1.compareTo(o2);
	}

	public static final int compare(Boolean o1, Boolean o2) {
		if (o1 == null) {
			return o2 == null ? 0 : -1;
		} else if (o2 == null) {
			return 1;
		}
		return o1.compareTo(o2);
	}

	public static final int compare(String o1, String o2) {
		if (o1 == null) {
			return o2 == null ? 0 : -1;
		} else if (o2 == null) {
			return 1;
		}
		return o1.compareTo(o2);
	}

	public static final int compare(Double o1, Double o2) {
		if (o1 == null) {
			return o2 == null ? 0 : -1;
		} else if (o2 == null) {
			return 1;
		}
		return o1.compareTo(o2);
	}

	public static final int compare(int o1, int o2) {
		return o1 - o2;
	}

	public static final boolean mesmoValor(Boolean a, Boolean b) {
		return a == null ? b == null : a.equals(b);
	}

	public static final boolean mesmoValor(Boolean a, boolean b) {
		return a == null ? false : mesmoValor(booleanValue(a), b);
	}

	public static final boolean mesmoValor(boolean a, boolean b) {
		return b == a;
	}

	public static final boolean mesmoValor(Number a, Number b) {
		return a == null ? b == null : a.equals(b);
	}
	
	public static final boolean mesmoValor(BigDecimal a, BigDecimal b) {
		return a == null ? b == null : a.compareTo(b)==0;
	}
	
	public static final boolean mesmoValor(BigInteger a, BigInteger b) {
		return a == null ? b == null : a.compareTo(b)==0;
	}

	public static final boolean mesmoValor(String a, String b) {
		return a == null ? b == null : a.equals(b);
	}

	public static final boolean mesmoValor(Character a, Character b) {
		return a == null ? b == null : a.equals(b);
	}

//	public static final boolean mesmoValor(char a, char b) {
//		return  a == b;
//	}

	public static final boolean mesmoValor(Date a, Date b) {
		return a == null ? b == null : a.equals(b);
	}

	public static final boolean entreValor(Number a, Number b, Number c) {
		boolean teste = false;
		if (!semValor(a)) {
			if ((doubleValue(a)) >= doubleValue(b) && doubleValue(a) <= doubleValue(c)) {
				teste = true;
			}
		}
		return teste;
	}

	public static boolean semValor(Object value) {
		return value == null;
	}

	public static final boolean semValor(BigDecimal value) {
		return value == null || value.compareTo(BigDecimal.ZERO) == 0;
	}
	
	public static boolean semValor(StringBuilder value) {
		return value == null || value.length() == 0;
	}

	public static final boolean semValor(BigInteger value) {
		return value == null || value.compareTo(BigInteger.ZERO) == 0;
	}
	
	public static final boolean semValor(Integer value) {
		return value == null || value.intValue() == 0;
	}

	public static final boolean semValor(Long value) {
		return value == null || value.intValue() == 0;
	}

	public static final boolean semValor(Character value) {
		return value == null || value.charValue() == 0;
	}

	public static final boolean semValor(Double value) {
		return value == null || value.doubleValue() == 0d;
	}

	public static final boolean semValor(Date value) {
		return value == null;
	}

	/**
	 * Verifica se a string é nula ou vazia.
	 * 
	 * @param value
	 * @return
	 */
	public static final boolean semValor(String value) {
		return value == null || value.trim().isEmpty();
	}

	public static final boolean semValorNossoNumero(final String nossoNumero) {
		try {
			return semValor(nossoNumero) || intValue(nossoNumero.trim()) == 0;
		} catch (NumberFormatException ignored) {
		}
		return false;
	}

	/**
	 * Verifica se uma coleção é nula ou está vazia.
	 * 
	 * @param value
	 * @return
	 */
	public static final boolean semValor(Collection<?> value) {
		return value == null || value.isEmpty() || value.size() <= 0;
	}

	public static final boolean semValor(Map<?, ?> value) {
		return value == null || value.isEmpty();
	}

	public static final <T> boolean semValor(T[] value) {
		return value == null || value.length <= 0;
	}

	public static final <K, V> K buscaValor(final V valor, final Map<K, V> colecao,
			final K valorRetornoSeNaoEncontrado) {
		if (colecao != null && colecao.containsValue(valor)) {
			if (valor == null) {
				for (K key : colecao.keySet()) {
					if (colecao.get(key) == null) {
						return key;
					}
				}
			} else {
				for (K key : colecao.keySet()) {
					if (valor.equals(colecao.get(key))) {
						return key;
					}
				}
			}
		}
		return valorRetornoSeNaoEncontrado;
	}

	@SuppressWarnings("unchecked")
	public static final <T> boolean valorNaLista(T valor, T... lista) {
		if (lista != null && lista.length > 0) {
			if (valor == null) {
				for (T item : lista) {
					if (item == null) {
						return true;
					}
				}
			} else {
				for (T item : lista) {
					if (valor.equals(item)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static final byte[] toByteArray(List<String> contents) {
		final StringBuilder c = new StringBuilder();
		if (contents != null) {
			for (String line : contents) {
				c.append(line).append("\r\n");
			}
		}
		final String s = c.toString();
		if (s == null || s.isEmpty()) {
			return new byte[0];
		}
		return s.getBytes();
	}

	public static final byte[] toByteArray(InputStream contents) throws IOException {
		return IOUtils.toByteArray(contents);
	}

	public static final InputStream streamData(List<String> contents) {
		InputStream result = null;
		if (contents != null && !contents.isEmpty()) {
			result = new ByteArrayInputStream(toByteArray(contents));
		}
		return result;
	}

	/** Formatador geral para valores monetários */
	public static final DecimalFormatSymbols PT_BR_SYMBOLS;
	static {
		PT_BR_SYMBOLS = new DecimalFormatSymbols(new Locale("pt_BR"));
		PT_BR_SYMBOLS.setDecimalSeparator(',');
		PT_BR_SYMBOLS.setMonetaryDecimalSeparator(',');
		PT_BR_SYMBOLS.setGroupingSeparator('.');
		PT_BR_SYMBOLS.setCurrencySymbol("R$");
	};
	
	public static final DecimalFormatSymbols EN_US_SYMBOLS;
	static {
		EN_US_SYMBOLS = new DecimalFormatSymbols(new Locale("en_US"));
		EN_US_SYMBOLS.setDecimalSeparator('.');
		EN_US_SYMBOLS.setMonetaryDecimalSeparator('.');
		EN_US_SYMBOLS.setGroupingSeparator(',');
		EN_US_SYMBOLS.setCurrencySymbol("$");
	};

	public static final String formataNumero(Number numero, String formato) {
		if (numero == null) {
			return null;
		}
		final NumberFormat formatador = new DecimalFormat(formato, PT_BR_SYMBOLS);
		return formatador.format(numero);
	}

	public static final String formataCEP(String cep) {
		if (cep == null) {
			return null;
		}
		switch (cep.length()) {
		case 8:
			return cep.substring(0, 2) + "." + cep.substring(2, 5) + "-" + cep.substring(5);
		case 5:
			return cep.substring(0, 2) + "." + cep.substring(2);
		}
		return cep;
	}

	public static final String formataValorMonetario(Number valor) {
		return formataNumero(valor, "#,##0.00");
	}
	
	public static final String formataValorMonetario(BigDecimal valor, String moeda) {
		if(!CommonsUtil.semValor(valor)) {
			DecimalFormat df = new DecimalFormat("#,##0.00",  PT_BR_SYMBOLS);
			return moeda + df.format(valor);	
		} else {
			return "";
		}
	}
	
	public static final String formataValorMonetarioCci(BigDecimal valor, String moeda) {
		DecimalFormat df = new DecimalFormat("#,##0.00",  PT_BR_SYMBOLS);
		if(!CommonsUtil.semValor(valor)) {
			return moeda + df.format(valor);	
		} else {
			return moeda + df.format(BigDecimal.ZERO);
		}
	}
	
	public static final String formataValorMonetarioCciArredondado(BigDecimal valor, String moeda) {
		valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
		DecimalFormat df = new DecimalFormat("#,##0.00",  PT_BR_SYMBOLS);
		if(!CommonsUtil.semValor(valor)) {
			return moeda + df.format(valor);	
		} else {
			return moeda + df.format(BigDecimal.ZERO);
		}
	}
	
	public static final String formataValorTaxa(BigDecimal valor) {
		return formataNumero(valor, "#,##0.0000");
	}
	
	public static final String formataValorInteiro(int valor) {
		return formataNumero(valor, "#,##0");
	}

	/**
	 * Formata a data no padrão dd/MM/yyyy
	 * 
	 * @param data
	 * @return
	 */
	public static final String formataData(Date data) {
		String result = "";
		if (data != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			result = df.format(data);
		}
		return result;
	}

	/**
	 * Formata a data no padrão dd/MM/yyyy
	 * 
	 * @param data
	 * @return
	 */
	public static final String formataHora(Date data) {
		String result = "";
		if (data != null) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			result = df.format(data);
		}
		return result;
	}

	/**
	 * Formata a data/hora no padrão dd/MM/yyyy HH:mm
	 * 
	 * @param data
	 * @return
	 */
	public static final String formataDataHora(Date data) {
		String result = "";
		if (data != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			result = df.format(data);
		}
		return result;
	}

	public static final String formataData(Date data, String pattern) {
		String result = "";
		if (data != null) {
			DateFormat df = new SimpleDateFormat(pattern, new Locale("pt", "BR"));
			result = df.format(data);
		}
		return result;
	}

	private static byte[] readFully(InputStream stream) throws IOException {
		byte[] buffer = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int bytesRead;
		while ((bytesRead = stream.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	public static byte[] loadFile(String sourcePath) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(sourcePath);
			return readFully(inputStream);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	@SuppressWarnings("resource")
	public static void copyFile(File source, File destination) throws IOException {
		if (destination.exists())
			destination.delete();

		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;

		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destinationChannel = new FileOutputStream(destination).getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		} finally {
			if (sourceChannel != null && sourceChannel.isOpen())
				sourceChannel.close();
			if (destinationChannel != null && destinationChannel.isOpen())
				destinationChannel.close();
		}
	}

	public static final String formataTextoTamanhoArquivo(Number tamanho) {
		if (tamanho != null) {
			long bytes = tamanho.longValue();
			if (bytes < 0l) {
				return "";
			} else if (bytes < KILOBYTE) {
				return bytes + " B";
			} else if (bytes < MEGABYTE) {
				return (bytes / KILOBYTE) + " KB";
			} else if (bytes < GIGABYTE) {
				return (bytes / MEGABYTE) + " MB";
			} else if (bytes < TERABYTE) {
				return (bytes / GIGABYTE) + " GB";
			} else {
				return (bytes / TERABYTE) + " TB";
			}
		}
		return "";
	}

	private static final Long KILOBYTE = new Long(1024l);
	private static final Long MEGABYTE = new Long(1024l * 1024l);
	private static final Long GIGABYTE = new Long(1024l * 1024l * 1024l);
	private static final Long TERABYTE = new Long(1024l * 1024l * 1024l * 1024l);

	public static final String trimNull(String valor) {
		return valor == null || valor.trim().isEmpty() ? null : valor.trim();
	}

	public static final String nullAsEmpty(String valor) {
		return valor == null ? "" : valor;
	}

	public static final Integer zeroAsNull(int valor) {
		return zeroAsNull((Integer) valor);
	}

	public static final Integer zeroAsNull(Integer valor) {
		return valor == null || valor.intValue() == 0 ? null : valor;
	}

	public static final Double zeroAsNull(double valor) {
		return valor == 0d ? null : new Double(valor);
	}

	public static final Double zeroAsNull(Double valor) {
		return valor == null || valor.doubleValue() == 0d ? null : valor;
	}

	public static final byte[] carregaBytesArquivo(final File file) {
		byte[] result = null;
		{
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("carregaBytesArquivo: carregando bytes de [" + file.getPath() + "]");
			}
			try {
				final FileInputStream fin = new FileInputStream(file);
				try {
					result = new byte[(int) file.length()];
					fin.read(result);
				} catch (IOException e) {
					LOGGER.error("carregaBytesArquivo: IOException:" + e.getMessage());
					result = null;
				}
				try {
					fin.close();
				} catch (IOException ignored) {
					LOGGER.warn("carregaBytesArquivo: IOException (ignorado):" + ignored.getMessage());
					ignored.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				LOGGER.error("carregaBytesArquivo: FileNotFoundException:" + e.getMessage());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] getArray(T... valores) {
		return valores;
	}

	@SuppressWarnings("unchecked")
	public static final <T> List<T> getList(T... valores) {
		final List<T> lista = new ArrayList<T>();
		if (valores != null) {
			for (T valor : valores) {
				lista.add(valor);
			}
		}
		return lista;
	}

	public static final <T> List<T> toList(Collection<T> valores) {
		if (valores == null) {
			return null;
		}
		final List<T> lista = new ArrayList<T>();
		lista.addAll(valores);
		return lista;
	}
	
	   public static final  Long[] getListLongToArray(Collection<Long> valores) {
		    Long[] itemsArray = new Long[valores.size()];
	        itemsArray = valores.toArray(itemsArray);
	        return itemsArray;
	    }

	public static final <T> List<T> merge(List<T> destino, List<T> novos) {
		if (destino == null) {
			return novos;
		}

		if (novos != null) {
			destino.addAll(novos);
		}

		return destino;

	}

	

	/**
	 * Ajusta tamanho de um String Se String maior que tamanho - Se ajusta a
	 * Direita: pega do final do String até o tamanho - Se ajusta a Esquerda: pega
	 * do inicio do String até o tamanho Se String menor que tamanho - Se ajusta a
	 * Direita: Mantém original a direita, e completa com Filler até o tamanho - Se
	 * ajusta a Esquerda: Mantém original a esquerda, e completa com Filler até o
	 * tamanho Se String mesmo tamanho que original, retorna original.
	 * 
	 * @param original
	 * @param tamanho
	 * @param ajustaDireita
	 * @param filler
	 * @return
	 */

	public static final String ajustaTamanhoString(final String original, final int tamanho,
			final boolean ajustaDireita) {
		return ajustaTamanhoString(original, tamanho, ajustaDireita, null);
	}

	public static final String ajustaTamanhoString(String original, final Integer tamanho, final boolean ajustaDireita,
			String filler) {

		if (semValor(original)) {
			original = "";
		}

		if (semValor(tamanho)) {
			return original;
		}

		if (original.length() == tamanho) {
			return original;
		}

		if (original.length() > tamanho) {
			if (ajustaDireita) {
				return original.substring(original.length() - tamanho);
			} else {
				return original.substring(0, tamanho);
			}
		}

		filler = semValor(filler) ? " " : filler;

		// original.length() < tamanho
		if (ajustaDireita) {
			return StringUtils.repeat(filler, tamanho - original.length()) + original;
		} else {
			return original + StringUtils.repeat(filler, tamanho - original.length());
		}
	}

	public static final <T, Y> void simpleCopyProperties(T destiny, Y source, String... excludedFields)
			throws SecurityException , IllegalArgumentException , IllegalAccessException {

	
			Class<?> classSource = source.getClass();
			Class<?> classDestiny = destiny.getClass();

			while (classSource != null && classDestiny != null) {
				Field[] fields = classSource.getDeclaredFields();
				List<String> lstExcludedFields = new ArrayList<String>();
				lstExcludedFields.add("class");
				if (excludedFields != null) {
					lstExcludedFields.addAll(Arrays.asList(excludedFields));
				}
				for (Field field : fields) {

					if (!lstExcludedFields.contains(field.getName())
							&& (field.getType() == String.class || field.getType() == Character.class
									|| field.getType() == Date.class || field.getType() == Boolean.class
									|| field.getType() == Short.class || field.getType() == Integer.class
									|| field.getType() == Long.class || field.getType() == Float.class
									|| field.getType() == Double.class || field.getType() == BigDecimal.class)) {

						field.setAccessible(true);

						try {
							Field destinyField = classDestiny.getDeclaredField(field.getName());

							Object value = field.get(source);

							destinyField.setAccessible(true);
							destinyField.set(destiny, value);
						} catch (NoSuchFieldException e) {
							// Se não encontra campo na classe de destino,
							// continua
							// para o próximo campo silenciosamente.
						}

					}

				}

				classSource = classSource.getSuperclass();
				classDestiny = classDestiny.getSuperclass();

			}

	}

	/**
	 * Faz a divisão de <CODE>dividendo</CODE> por <CODE>divisor</CODE>. Usa
	 * BigDecimal para evitar problemas (NaN) de divisão de double.
	 * 
	 * @param dividendo
	 * @param divisor
	 * @return
	 */
	public static double divisaoPrecisa(Double dividendo, Double divisor) {
		if (dividendo != null && divisor != null && divisor.doubleValue() > 0d) {
			BigDecimal b1 = new BigDecimal(dividendo.doubleValue());
			BigDecimal b2 = new BigDecimal(divisor.doubleValue());
			BigDecimal divide = b1.divide(b2, new MathContext(2, RoundingMode.HALF_EVEN));
			return divide.doubleValue();
		}
		return 0d;
	}

	public static final String formataCnpjCpf(String cnpjCpf, boolean isUsarPrefixos) {
		String result = "";
		if (cnpjCpf != null) {
			switch (cnpjCpf.length()) {
			case 11:
				result = (isUsarPrefixos ? "CPF " : "") + cnpjCpf.substring(0, 3) + "." + cnpjCpf.substring(3, 6) + "."
						+ cnpjCpf.substring(6, 9) + "-" + cnpjCpf.substring(9);
				break;
			case 14:
				result = (isUsarPrefixos ? "CNPJ " : "") + cnpjCpf.substring(0, 2) + "." + cnpjCpf.substring(2, 5) + "."
						+ cnpjCpf.substring(5, 8) + "/" + cnpjCpf.substring(8, 12) + "-" + cnpjCpf.substring(12);
				break;
			default:
				result = cnpjCpf;
				break;
			}
		}
		return result;
	}

	public static final Double soma(Double... valores) {
		double result = 0d;
		if (valores != null) {
			for (Double valor : valores) {
				if (valor != null) {
					result += valor.doubleValue();
				}
			}
		}
		return result == 0d ? null : new Double(result);
	}

	public static final Integer soma(Integer... valores) {
		int result = 0;
		if (valores != null) {
			for (Integer valor : valores) {
				if (valor != null) {
					result += valor.intValue();
				}
			}
		}
		return result == 0d ? null : new Integer(result);
	}

	/* Retorna uma string com 0 na esquerda */
	public static final String strZero(String sVar, int iTamanho) {
		String sAux;
		String sRet;

		// Tirando espaços da variavel
		if (sVar != null) {
			sAux = sVar.trim();
		} else {
			sAux = "";
		}
		sRet = sAux;

		// Completando com zeros a esquerda
		for (int iCon = 0; iCon < ((iTamanho - sAux.length())); iCon++) {
			sRet = '0' + sRet;
		}

		// Retorna..
		return sRet;
	}

	/* Retorna uma string com 0 na esquerda */
	public static final String strZeroDireita(String sVar, int iTamanho) {
		String sAux;
		String sRet;

		// Tirando espaços da variavel
		if (sVar != null) {
			sAux = sVar.trim();
		} else {
			sAux = "";
		}
		sRet = sAux;

		// Completando com zeros a esquerda
		for (int iCon = 0; iCon < ((iTamanho - sAux.length())); iCon++) {
			sRet = sRet + '0';
		}

		// Retorna..
		return sRet;
	}

	public static String removeEComercial(String str) {

		str = str.replaceAll("&", "E");
		return str;

	}

	public static String removeAcentos(String str) {

		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;

	}

	public static final String removeEspacos(String s) {
		return s == null ? null : s.replace(Character.toString((char) 160), "").replaceAll("\\s+", "");
	}

	public static final String somenteNumeros(String s) {
		return s == null ? null : s.replaceAll("[^\\d]", "");
	}

	public static final String removeCaracteresInvalidos(String s) {

		if (s == null) {
			return null;
		}

		if ("".equals(s)) {
			return "";
		}

		StringBuilder sb = new StringBuilder(s);

		for (int i = 0; i < s.length(); i++) {

			int code = (int) sb.charAt(i);

			if ((code < 32 && code != '\n' && code != '\r') || code > 254) {
				sb.setCharAt(i, ' ');
			}
		}

		return sb.toString();
	}

	public static boolean eSomenteNumero(String s) {
		String sNumeros = somenteNumeros(s);

		return s.equals(sNumeros);

	}

	public static String montaCMC7(Integer numeroBancoCheque, Integer codigoCompensacaoCheque,
			String codigoAgenciaCheque, String codigoContaCorrenteCheque, String numeroCheque) {

		if (semValor(codigoCompensacaoCheque)) {
			codigoCompensacaoCheque = 18;
		}

		if (codigoContaCorrenteCheque.length() > 10) {
			codigoContaCorrenteCheque = codigoContaCorrenteCheque.substring(codigoContaCorrenteCheque.length() - 10,
					codigoContaCorrenteCheque.length());
		}
		String C1 = calculaDigitoVerificador11(
				strZero(stringValue(numeroBancoCheque), 3) + strZero(stringValue(codigoAgenciaCheque), 4));
		String C2 = calculaDigitoVerificador11(
				strZero(stringValue(codigoCompensacaoCheque), 3) + strZero(stringValue(numeroCheque), 6) + "5");
		String C3 = calculaDigitoVerificador11(strZero(stringValue(codigoContaCorrenteCheque), 10));

		String sCmc7 = "<" + strZero(stringValue(numeroBancoCheque), 3);
		sCmc7 += strZero(stringValue(codigoAgenciaCheque), 4);
		sCmc7 += C2 + "<";
		sCmc7 += strZero(stringValue(codigoCompensacaoCheque), 3);
		sCmc7 += strZero(stringValue(numeroCheque), 6);
		sCmc7 += "5" + ">";
		sCmc7 += C1;
		sCmc7 += strZero(stringValue(codigoContaCorrenteCheque), 10);
		sCmc7 += C3 + ";";

		// vo.setCodigoCompensacaoCheque(CommonsUtil.intValue(vo.getCmc7()
		// .substring(10, 12)));
		// vo.setNumeroDocumento(vo.getCmc7().substring(13, 19));
		// vo.setNumeroBancoCheque(CommonsUtil.intValue(vo.getCmc7().substring(1,
		// 4)));
		// vo.setCodigoAgenciaCheque(vo.getCmc7().substring(4, 8));
		// vo.setCodigoContaCorrenteCheque(vo.getCmc7().substring(25, 31));

		return sCmc7;

	}

	public static Boolean validaCMC7(String codigoCmc7Cheque) {
		String sCmc7 = somenteNumeros(codigoCmc7Cheque);
		if (mesmoValor(30, sCmc7.length())) {

			String C1 = calculaDigitoVerificador11(sCmc7.substring(0, 7));
			String C2 = calculaDigitoVerificador11(sCmc7.substring(8, 18));
			String C3 = calculaDigitoVerificador11(sCmc7.substring(19, 29));

			if (mesmoValor(C1, sCmc7.substring(18, 19)) && mesmoValor(C2, sCmc7.substring(7, 8))
					&& mesmoValor(C3, sCmc7.substring(29, 30))) {
				return true;
			}

		}

		return false;

	}

	/**
	 * @author :Allan Tenorio
	 * @since :10/07/2012
	 * @see :Calculo do Modulo 10 para geracao do digito verificador de boletos
	 *      bancários.
	 */
	// Módulo 10
	// Conforme o esquema abaixo, cada dígito do número, começando da direita
	// para a esquerda
	// (menos significativo para o mais significativo) é multiplicado, na ordem,
	// por 2, depois 1, depois 2, depois 1 e
	// assim sucessivamente.
	// Em vez de ser feito o somatório das multiplicações, será feito o
	// somatório dos dígitos das multiplicações
	// (se uma multiplicação der 12, por exemplo, será somado 1 + 2 = 3).
	// O somatório será dividido por 10 e se o resto (módulo 10) for diferente
	// de zero, o dígito será 10 menos este valor.
	// Número exemplo: 261533-4
	// +---+---+---+---+---+---+ +---+
	// | 2 | 6 | 1 | 5 | 3 | 3 | - | 4 |
	// +---+---+---+---+---+---+ +---+
	// | | | | | |
	// x1 x2 x1 x2 x1 x2
	// | | | | | |
	// =2 =12 =1 =10 =3 =6
	// +---+---+---+---+---+-> = (16 / 10) = 1, resto 6 => DV = (10 - 6) = 4
	public static int getMod10(String num) {
		// variáveis de instancia
		int soma = 0;
		int resto = 0;
		int dv = 0;
		String[] numeros = new String[num.length() + 1];
		int multiplicador = 2;
		String aux;
		String aux2;
		String aux3;
		for (int i = num.length(); i > 0; i--) {
			// Multiplica da direita pra esquerda, alternando os algarismos 2 e
			// 1
			if (multiplicador % 2 == 0) {
				// pega cada numero isoladamente
				numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * 2);
				multiplicador = 1;
			} else {
				numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * 1);
				multiplicador = 2;
			}
		}
		// Realiza a soma dos campos de acordo com a regra
		for (int i = (numeros.length - 1); i > 0; i--) {
			aux = String.valueOf(Integer.valueOf(numeros[i]));
			if (aux.length() > 1) {
				aux2 = aux.substring(0, aux.length() - 1);
				aux3 = aux.substring(aux.length() - 1, aux.length());
				numeros[i] = String.valueOf(Integer.valueOf(aux2) + Integer.valueOf(aux3));
			} else {
				numeros[i] = aux;
			}
		}
		// Realiza a soma de todos os elementos do array e calcula o digito
		// verificador
		// na base 10 de acordo com a regra.
		for (int i = numeros.length; i > 0; i--) {
			if (numeros[i - 1] != null) {
				soma += Integer.valueOf(numeros[i - 1]);
			}
		}
		resto = soma % 10;
		dv = 10 - resto;
		// retorna o digito verificador
		return dv;
	}

	/**
	 * @author :Allan Tenorio
	 * @since :11/07/2012
	 * @see :Calculo do Modulo 11 para geracao do digito verificador de boletos
	 *      bancários.
	 */
	// Módulo 11
	// Conforme o esquema abaixo, para calcular o primeiro dígito verificador,
	// cada dígito do número,
	// começando da direita para a esquerda (do dígito menos significativo para
	// o dígito mais significativo)
	// é multiplicado, na ordem, por 2, depois 3, depois 4 e assim
	// sucessivamente, até o primeiro dígito do número.
	// O somatório dessas multiplicações dividido por 11. O resto desta divisão
	// (módulo 11) é subtraido da base (11),
	// o resultado é o dígito verificador. Para calcular o próximo dígito,
	// considera-se o dígito anterior como parte
	// do número e efetua-se o mesmo processo. No exemplo, foi considerado o
	// número 261533:
	// +---+---+---+---+---+---+ +---+
	// | 2 | 6 | 1 | 5 | 3 | 3 | - | 9 |
	// +---+---+---+---+---+---+ +---+
	// | | | | | |
	// x7 x6 x5 x4 x3 x2
	// | | | | | |
	// =14 =36 =5 =20 =9 =6 soma = 90
	// +---+---+---+---+---+-> = (90 / 11) = 8,1818 , resto 2 => DV = (11 - 2) =
	// 9
	public static int getMod11(String num) {

		// variáveis de instancia
		int soma = 0;
		int resto = 0;
		int dv = 0;
		String[] numeros = new String[num.length() + 1];
		int multiplicador = 2;
		for (int i = num.length(); i > 0; i--) {
			// Multiplica da direita pra esquerda, incrementando o multiplicador
			// de 2 a 9
			// Caso o multiplicador seja maior que 9 o mesmo recomeça em 2
			if (multiplicador > 9) {
				// pega cada numero isoladamente
				multiplicador = 2;
				numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * multiplicador);
				multiplicador++;
			} else {
				numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * multiplicador);
				multiplicador++;
			}
		}
		// Realiza a soma de todos os elementos do array e calcula o digito
		// verificador
		// na base 11 de acordo com a regra.
		for (int i = numeros.length; i > 0; i--) {
			if (numeros[i - 1] != null) {
				soma += Integer.valueOf(numeros[i - 1]);
			}
		}
		resto = soma % 11;
		dv = 11 - resto;
		if (dv > 9 || dv == 0) {
			dv = 1;
		}
		// retorna o digito verificador
		return dv;
	}

	public static String calculaDigitoVerificador11(String numero) {

		char[] digitos = numero.toCharArray();
		int sum = 0;

		ArrayList<Integer> fator = new ArrayList<Integer>();

		int ifator = 0;
		int resto = 0;

		String digito = "";

		for (int i = 2; i >= 1; i--) {
			fator.add(i);
		}

		for (int i = numero.length() - 1; i >= 0; i--) {

			int multi = (digitos[i] - '0') * fator.get(ifator);

			char[] multiDigitos = String.valueOf(multi).toCharArray();

			for (int j = multiDigitos.length - 1; j >= 0; j--) {
				sum += (multiDigitos[j] - '0');
			}

			if (ifator == (fator.size() - 1)) {
				ifator = 0;
			} else {
				ifator += 1;
			}
		}

		resto = sum % 10;

		int digito1 = (10 - resto);
		if (digito1 > 9) {
			digito = "0";
		} else {
			digito = "" + digito1;
		}

		return (digito + "0").substring(0, 1);

	}

	/**
	 * Retorna o sistema operacional que o sistema está rodando Util para definir os
	 * locais de arquivos em ambiente Windows e Linux Bonatte: 06/03/2015 retorna
	 * true se for WINDOWS e false se for LINUX
	 */
	public static final boolean sistemaWindows() {
		String sistemaOperacional = System.getProperty("os.name").toUpperCase();
		boolean result = false;
		if (sistemaOperacional.contains("WINDOWS")) {
			// System.out.println("Rodando em Windows: "+sistemaOperacional);
			result = true;
		}
		return result;
	}

	public static final boolean servicoConsultaAtivo(String[] lista, Integer codigoServico) {
		boolean result = false;
		for (Integer i = 0; i < lista.length; i++) {
			if (mesmoValor(integerValue(lista[i]), codigoServico)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static final boolean crednetFeatureAtiva(String[] lista, String feature) {
		boolean result = false;
		for (Integer i = 0; i < lista.length; i++) {
			if (mesmoValor(stringValue(lista[i]), feature)) {
				result = true;
				break;
			}
		}
		return result;
	}

	

	public static Integer retornaCodigoPessoaDoNome(String nome) {
		Integer codigoPessoa = null;
		Integer inicio = nome.indexOf("[");
		if (inicio >= 0) {
			String codigo = nome.substring(inicio);
			codigo = somenteNumeros(codigo);
			codigoPessoa = integerValue(codigo);
		}
		return codigoPessoa;
	}

	public static Boolean converteBmpToJpg(String diretorio) {

		Boolean result = true;

		File file = new File(diretorio);

		// FileFilter filter = new FileFilter() {
		// @Override
		// public boolean accept(File pathname) {
		// return pathname.isFile();
		// }
		// };
		// File afile[] = file.listFiles(filter);

		File afile[] = file.listFiles();

		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivos = afile[i];
			if (arquivos.getName().toLowerCase().endsWith(".bmp")) {

				String fullFileNameBmp = arquivos.getAbsolutePath();
				String fullFileNameJpg = fullFileNameBmp.replaceAll(".bmp", ".jpg");

				converteBmpToJpg(fullFileNameBmp, fullFileNameJpg);
			}
		}
		return result;
	}

	// método para compactar arquivo
	public static Boolean converteBmpToJpg(String fullFileNameBmp, String fullFileNameJpg) {
		// Create file for the source
		Boolean result = true;
		File input = new File(fullFileNameBmp);

		// Create a file for the output
		File output = new File(fullFileNameJpg);

		BufferedImage image;
		try {
			// Read the file to a BufferedImage
			image = ImageIO.read(input);
			// Write the image to the destination as a JPG
			ImageIO.write(image, "jpg", output);

		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public static Boolean compactarParaZip(String diretorio) {

		Boolean result = true;

		File file = new File(diretorio);

		File afile[] = file.listFiles();

		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivos = afile[i];
			// if (arquivos.getName().toLowerCase().endsWith(".bmp")) {

			String fullFileNameIn = arquivos.getAbsolutePath();
			String fullFileNameZip = fullFileNameIn + ".zip";

			compactarParaZip(fullFileNameZip, fullFileNameIn);
			// }
		}
		return result;
	}

	// método para compactar arquivo
	public static void compactarParaZip(String arqSaida, String arqEntrada) {
		int cont;
		int TAMANHO_BUFFER = 4096;
		byte[] dados = new byte[TAMANHO_BUFFER];

		BufferedInputStream origem = null;
		FileInputStream streamDeEntrada = null;
		FileOutputStream destino = null;
		ZipOutputStream saida = null;
		ZipEntry entry = null;
		try {
			destino = new FileOutputStream(new File(arqSaida));
			saida = new ZipOutputStream(new BufferedOutputStream(destino));
			File file = new File(arqEntrada);
			streamDeEntrada = new FileInputStream(file);
			origem = new BufferedInputStream(streamDeEntrada, TAMANHO_BUFFER);
			entry = new ZipEntry(file.getName());
			saida.putNextEntry(entry);

			while ((cont = origem.read(dados, 0, TAMANHO_BUFFER)) != -1) {
				saida.write(dados, 0, cont);
			}
			origem.close();
			saida.close();
		} catch (IOException e) {
		}
	}

	public static Map<String, byte[]> retornaArquivosDoZip(byte[] arquivoZip) {

		Map<String, byte[]> mapArquivoDescompactado = new HashMap<String, byte[]>(0);

		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(arquivoZip));

		// get the zipped file list entry
		ZipEntry ze;
		try {
			ze = zis.getNextEntry();
			while (ze != null) {
				System.out.println(" -> Descompactando arquivo: " + ze.getName());
				if (!ze.isDirectory()) {
					// && !ze.getName().toLowerCase().endsWith("pdf")
					byte[] buffer = IOUtils.toByteArray(zis);
					mapArquivoDescompactado.put(ze.getName(), buffer);
				}
				ze = zis.getNextEntry();
			}
		} catch (IOException e) {
		}
		return mapArquivoDescompactado;
	}

	
	public static final String formataMesExtensoAbreviadoAno(Date data) {
		return formataMesExtensoAbreviadoAno(data, null);
	}

	public static final String formataMesExtensoAbreviadoAno(Date data, String separador) {
		String mesAno = formataData(data, "MMyyyy");
		String result = formataMesAno(mesAno);
		if (!semValor(separador)) {
			result = result.replace("/", separador);
		}
		return result;
	}

	public static final String formataMesExtensoAno(Date data) {
		return formataMesExtensoAno(data, null);
	}
	
	public static final String formataMesExtensoAno(Date data, String separador) {
		String mesAno = formataData(data, "MMyyyy");
		String result = formataMesAnoFull(mesAno);
		if (!semValor(separador)) {
			result = result.replace("/", separador);
		}
		return result;
	}
	

	public static final String formataMesAno(String mesAno) {
		if (mesAno == null) {
			mesAno = "";
		}
		if (!mesAno.isEmpty() && (eSomenteNumero(mesAno)) ) {
			Integer mes = Integer.parseInt(mesAno.substring(0, 2));
			Integer mesPosicao = ((mes - 1) * 3);

			String ano = mesAno.substring(2, 6);

			String meses = "JanFevMarAbrMaiJunJulAgoSetOutNovDez";
			String mesExtenso = meses.substring(mesPosicao, mesPosicao + 3);

			mesAno = mesExtenso + "/" + ano;
		}
		return mesAno;
	}

	public static final String formataMesAnoFull(String mesAno) {
		if (mesAno == null) {
			mesAno = "";
		}
		if (!mesAno.isEmpty() && (eSomenteNumero(mesAno)) ) {
			Integer mes = Integer.parseInt(mesAno.substring(0, 2));
			Integer mesPosicao = ((mes - 1) * 9);

			String ano = mesAno.substring(2, 6);

			String meses = "Janeiro  FevereiroMarço    Abril    Maio     Junho    Julho    Agosto   Setembro Outubro  Novembro Dezembro ";
			String mesExtenso = meses.substring(mesPosicao, mesPosicao + 9);

			mesAno = mesExtenso.trim() + "/" + ano;
		}
		return mesAno;
	}
	public static final String formataMesExtenso(Date data) {
		String mesAno = formataData(data, "MMyyyy");
		if (mesAno == null) {
			mesAno = "";
		}
		if (!mesAno.isEmpty() && (eSomenteNumero(mesAno)) ) {
			Integer mes = Integer.parseInt(mesAno.substring(0, 2));
			Integer mesPosicao = ((mes - 1) * 9);

			String meses = "Janeiro  FevereiroMarço    Abril    Maio     Junho    Julho    Agosto   Setembro Outubro  Novembro Dezembro ";
			String mesExtenso = meses.substring(mesPosicao, mesPosicao + 9);

			mesAno = mesExtenso.trim();
		}
		return mesAno;
	}
	
	public static String formataAnoMes(String anoMes) {
		if (anoMes == null) {
			anoMes = "";
		}
		if (!semValor(anoMes)) {
			String mesAno = anoMes.substring(4, 6) + anoMes.substring(0, 4);
			anoMes = formataMesAno(mesAno);
		}
		return anoMes;
	}

	public static final String formataAnoMesExtenso(Date data) {
		return formataAnoMesExtenso(data, null);
	}

	public static final String formataAnoMesExtenso(Date data, String separador) {
		String anoMes = formataData(data, "yyyyMM");
		String result = formataAnoMes(anoMes);
		String anoMesExt = result.substring(4, 8)+"/"+result.substring(0, 3);
		if (!semValor(separador)) {
			anoMesExt = anoMesExt.replace("/", separador);
		}
		return anoMesExt;
	}

	public static List<String> retornaAnoMesesDeUmPeriodo(Date data, int meses) {
		List<String> anoMesesRetorno = new ArrayList<String>();

		String anoMes = formataData(data, "yyyyMM");
		int ano = intValue(anoMes.substring(0, 4));
		int mes = intValue(anoMes.substring(5, 6));

		if (meses == 0) {
			anoMesesRetorno.add(strZero( stringValue(ano), 4)+ strZero(stringValue(mes) ,2));
		}
		if (meses > 0) {
			for (int i=0; i < meses; i++) {
				anoMesesRetorno.add(strZero( stringValue(ano), 4)+ strZero(stringValue(mes) ,2));
				mes++;
				if (mes > 12) {
					mes = 1;
					ano++;
				}
				anoMesesRetorno.add(strZero( stringValue(ano), 4)+ strZero(stringValue(mes) ,2));
			}
		}
		if (meses < 0) {
			meses = meses * -1;
			for (int i=0; i < meses; i++) {
				anoMesesRetorno.add(strZero( stringValue(ano), 4)+ strZero(stringValue(mes) ,2));
				mes--;
				if (mes <= 0) {
					mes = 12;
					ano--;
				}
			}
		}
		return anoMesesRetorno;
	}
	
	public static final Integer retornaAnoMesDeUmaData(Date data, int meses) {
		String anoMes = formataData(data, "yyyyMM");
		int ano = intValue(anoMes.substring(0, 4));
		int mes = intValue(anoMes.substring(5, 6));

		if (meses > 0) {
			for (int i=0; i < meses; i++) {
				mes++;
				if (mes > 12) {
					mes = 1;
					ano++;
				}
			}
		}
		if (meses < 0) {
			meses = meses * -1;
			for (int i=0; i < meses; i++) {
				mes--;
				if (mes <= 0) {
					mes = 12;
					ano--;
				}
			}
		}
		Integer anoMesResult = integerValue( strZero( stringValue(ano), 4)+ strZero(stringValue(mes) ,2) );
		
		return anoMesResult;
	}
	
	public static Date retornaPrimeiroDiaDoMes(String anoMes) {
		Date dataRetorno = null;
		if (anoMes.length() == 6) {
			String data = anoMes.substring(0, 4)+"-"+anoMes.substring(4, 6)+"-01";
			dataRetorno = dateValue(data); 
		}
		return dataRetorno;
	}
	
	public static String formataCpf(String cpf) {
		//cpf = CommonsUtil.somenteNumeros(cpf);
		char[] temp = cpf.toCharArray();
		char[] array = new char[14];
		array[3] = '.';
		array[7] = '.';
		array[11] = '-';
		int j = 0;
		for(int i = 0; i < array.length; i++) {
			if(!CommonsUtil.semValor(array[i])) {
				i++;
			}
			array[i] = temp[j];
			j++;
		}  
		cpf = new String(array);
		return cpf;
	}
	
	public static String formataCnpj(String cnpj) {
		//cnpj = CommonsUtil.somenteNumeros(cnpj);
		char[] temp = cnpj.toCharArray();
		char[] array = new char[17];
		array[2] = '.';
		array[6] = '.';
		array[10] = '/';
		array[14] = '-';
		int j = 0;
		for(int i = 0; i < array.length; i++) {
			if(!CommonsUtil.semValor(array[i])) {
				i++;
			}
			array[i] = temp[j];
			j++;
		}  
		cnpj = new String(array);
		return cnpj;
		
		//MaskFormatter mf = new MaskFormatter("##.###.###/####-##");        
		//cpfCnpjCCResp =  mf.valueToString(cpfCnpjCCResp);
	}
}
