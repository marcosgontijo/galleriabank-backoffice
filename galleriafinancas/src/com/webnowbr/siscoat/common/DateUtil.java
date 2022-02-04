package com.webnowbr.siscoat.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * @author jaugusto.jales
 */
public final class DateUtil {

	
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(DateUtil.class);

	/**
	 * All minutes have this many milliseconds except the last minute of the day
	 * on a day defined with a leap second.
	 */
	public static final long MILLISECS_PER_MINUTE = 60 * 1000;

	/**
	 * Number of milliseconds per hour, except when a leap second is inserted.
	 */
	public static final long MILLISECS_PER_HOUR = 60 * MILLISECS_PER_MINUTE;

	/**
	 * Number of leap seconds per day expect on <BR/>
	 * 1. days when a leap second has been inserted, e.g. 1999 JAN 1. <BR/>
	 * 2. Daylight-savings "spring forward" or "fall back" days.
	 */
	protected static final long MILLISECS_PER_DAY = 24 * MILLISECS_PER_HOUR;

	/** Avoid instantiation. */
	private DateUtil() {
	}

	/**
	 * Returns a date that represents the current day.
	 * 
	 * @return
	 */
	public static Date today() {
		Calendar cal = getZeroHourCal(new Date());
		return cal.getTime();
	}

	/**
	 * 
	 * @param ini
	 * @param fin
	 * @param check
	 * @return
	 */
	public static boolean between(Date ini, Date end, Date check) {
		return (ini.before(check) || ini.equals(check)) && (end.after(check) || end.equals(check));
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar toCalendarDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// cal.set(Calendar.HOUR, 0);
		// cal.set(Calendar.MINUTE, 0);
		// cal.set(Calendar.SECOND, 0);
		// cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	// =====================================================================

	public static final Calendar now() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		return cal;
	}

	public static Calendar getZeroHourCal(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return cal;
	}

	public static Date getZeroHour(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	public static Date getCustomHourDate(Date date, int hour, int minute) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, minute);
		cal.set(GregorianCalendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	public static boolean isFirstDayOfMonth(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(GregorianCalendar.DAY_OF_MONTH) == 1;
	}

	public static boolean isAfterFirstDayOfNextMonth(Date date) {
		return !getFirstDayOfNextMonth(DateUtil.getDataHoraAgora()).after(date);
	}

	public static int[] getDiaHoraMinutoSegundoEntreDates(Date dtInicio, Date dtFim) {
		int[] ret = new int[4];

		Calendar cal = GregorianCalendar.getInstance();

		cal.setTime(dtInicio);
		long startLong = cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());

		cal.setTime(dtFim);
		long endLong = cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());

		// Dias
		long dif = endLong - startLong;
		ret[0] = (int) Math.floor(dif / MILLISECS_PER_DAY);

		// Horas
		dif -= (ret[0] * MILLISECS_PER_DAY);
		ret[1] = Math.round(dif / MILLISECS_PER_HOUR);

		// Minutos
		dif -= (ret[1] * MILLISECS_PER_HOUR);
		ret[2] = Math.round(dif / MILLISECS_PER_MINUTE);

		// Segundos
		dif -= (ret[2] * MILLISECS_PER_MINUTE);
		ret[3] = Math.round(dif / 1000);

		return ret;
	}

	/**
	 * Checks whether the record is current or future with respect to a specific
	 * date.
	 * 
	 * @param start
	 * @param end
	 * @param newDate
	 * @return
	 */
	public static boolean isCurrentOrFuture(Date start, Date end, Date newDate) {
		if (newDate == null) {
			newDate = DateUtil.getDataHoraAgora();
		}
		if (isBetweenDate(newDate, start, end) || isAfterDate(newDate, start)) {
			return true;
		}
		return false;
	}

	public static Date getYesterday(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * Returns true if the end date is greater than the initial date
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isAfterDate(Date startDate, Date endDate) {
		final Calendar startCal = getZeroHourCal(startDate);
		final Calendar endCal = getZeroHourCal(endDate);
		return startCal.getTimeInMillis() < endCal.getTimeInMillis();
	}

	/**
	 * Returns true if the end date is greater than the initial date
	 * 
	 * @param startDate
	 * @param date
	 * @return
	 */
	public static boolean isAfterDateMinutes(Date startDate, Date endDate) {
		final Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		startCal.set(Calendar.MILLISECOND, 0);
		final Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		endCal.set(Calendar.MILLISECOND, 0);
		return startCal.getTimeInMillis() < endCal.getTimeInMillis();
	}

	public static boolean isEqualsDateMinutes(Date startDate, Date endDate) {
		final Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		startCal.set(Calendar.MILLISECOND, 0);
		final Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		endCal.set(Calendar.MILLISECOND, 0);
		return startCal.getTimeInMillis() == endCal.getTimeInMillis();
	}
	
	public static boolean isEqualsDate(Date startDate, Date endDate) {
		final Calendar startCal = getZeroHourCal(startDate);
		final Calendar endCal = getZeroHourCal(endDate);
		return startCal.getTimeInMillis() == endCal.getTimeInMillis();
	}

	/**
	 * Returns true or false according to the validation date this period from a
	 * current.
	 * 
	 * @param newDate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isBetweenDate(Date newDate, Date startDate, Date endDate) {
		newDate = getZeroHour(newDate);
		startDate = getZeroHour(startDate);
		endDate = getZeroHour(endDate);
		return startDate.getTime() <= newDate.getTime() && endDate.getTime() >= newDate.getTime();
	}

	/**
	 * Recupera o primeiro dia do primeiro mês apartir da data informada.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(GregorianCalendar.MONTH, 0);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	/**
	 * Recupera o primeiro dia do primeiro mês apartir da data informada.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfNextMonth(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(GregorianCalendar.MONTH, 1);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	/**
	 * Recupera o primeiro dia do primeiro mês apartir da data informada.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfPreviusMonth(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(GregorianCalendar.MONTH, -1);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	/**
	 * Recupera o primeiro dia do proximo mes.
	 * 
	 * @return
	 */
	public static Date getFirstDayOfNextMonth() {
		return getFirstDayOfNextMonth(getDataHoje());
	}

	/**
	 * Recupera o primeiro dia do mes anterior.
	 * 
	 * @return
	 */
	public static Date getFirstDayOfPreviusMonth() {
		return getFirstDayOfPreviusMonth(getDataHoje());
	}

	/**
	 * Recupera o primeiro dia do mes anterior.
	 * 
	 * @return
	 */
	public static Date getLastDayOfPreviusMonth() {
		return getYesterday(getFirstDayOfMonth(getDataHoje()));
	}

	/**
	 * Recupera o primeiro dia do mes anterior.
	 * 
	 * @return
	 */
	public static Date getLastDayMonth(Date primeiroDiaDoMes) {
		return getYesterday(adicionarPeriodo(primeiroDiaDoMes, 1, Calendar.MONTH));
	}

	/**
	 * @return data referência hoje - hora corrente
	 */
	public static final Date getDataHoraAgora() {
		Calendar cal = now();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getDataHoraAgora: " + cal.getTime());
		}
		return cal.getTime();
	}

	/**
	 * @return data referência padrão - primeiro de janeiro 1970
	 */
	public static Date getDataReferencia() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(GregorianCalendar.YEAR, 1970);
		cal.set(GregorianCalendar.MONTH, 1);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	/**
	 * @return data referência hoje - hora como 00:00:00
	 */
	public static Date getDataHoje() {
		Calendar cal = DateUtil.now();
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getDataHoje: " + cal.getTime());
		}
		return cal.getTime();
	}

	/**
	 * @return dia da dataHoje
	 */
	public static int getDiaHoje() {
		Calendar cal = DateUtil.now();
		return cal.get(GregorianCalendar.DAY_OF_MONTH);
	}

	/**
	 * Verifica se a <CODE>data</CODE> é a data de hoje
	 * 
	 * @param data
	 *            data a ser checada
	 * @return verdadeiro se for a data de hoje, cas contrário falso
	 */
	public static boolean isDataHoje(Date data) {
		int diferencaDias = getDaysBetweenDates(data, getDataHoje());
		return diferencaDias == 0;
	}

	/**
	 * Obtém a quantidade de dias entre duas datas
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDaysBetweenDates(Date startDate, Date endDate) {
		final Calendar start = getZeroHourCal(startDate);
		final Calendar end = getZeroHourCal(endDate);
		long startLong = start.getTimeInMillis() + start.getTimeZone().getOffset(start.getTimeInMillis());
		long endLong = end.getTimeInMillis() + end.getTimeZone().getOffset(end.getTimeInMillis());
		return (int) ((endLong - startLong) / MILLISECS_PER_DAY);
	}
	
	/**
	 * Obtém a quantidade de meses entre duas datas
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getMonthsBetweenDates(Date startDate, Date endDate) {
		final Calendar start = getZeroHourCal(getFirstDayOfMonth(startDate));
		final Calendar end = getZeroHourCal(getFirstDayOfMonth(endDate));
		int count = 0;
		
		while ( start.before(end)){
			start.add(Calendar.MONTH, 1);
			count++;
		}
		
		return count;
	}

	/**
	 * Obtém a quantidade de minutos entre duas datas
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getMinutesBetweenDates(Date startDate, Date endDate) {
		final Calendar start = toCalendarDate(startDate);
		final Calendar end = toCalendarDate(endDate);
		long startLong = start.getTimeInMillis() + start.getTimeZone().getOffset(start.getTimeInMillis());
		long endLong = end.getTimeInMillis() + end.getTimeZone().getOffset(end.getTimeInMillis());
		return (int) ((endLong - startLong) / MILLISECS_PER_MINUTE);
	}

	/**
	 * @return data referência amanhã - hora como 00:00:00
	 */
	public static Date getDataAmanha() {
		return getDataDias(1);
	}

	/**
	 * @return data referência ontem - hora como 00:00:00
	 */
	public static Date getDataOntem() {
		return getDataDias(-1);
	}

	/**
	 * Data alguns dias a partir de hoje - hora como 00:00:00.000
	 * 
	 * @param dias
	 *            número de dias
	 * @return data
	 */
	public static Date getDataDias(int dias) {
		Calendar cal = DateUtil.now();
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.add(GregorianCalendar.DAY_OF_MONTH, dias);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getDataDias: dias=" + dias + " data=" + cal.getTime());
		}
		return cal.getTime();
	}

	/**
	 * Data alguns dias a partir de uma data - hora como 00:00:00.000
	 * 
	 * @param dias
	 *            número de dias
	 * @return data
	 */
	public static Date getDataDias(Date data, int dias) {
		Calendar cal = null;
		if (data == null) {
			cal = DateUtil.now();
		} else {
			cal = GregorianCalendar.getInstance();
			cal.setTime(data);
		}
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.add(GregorianCalendar.DAY_OF_MONTH, dias);
		Date result = cal.getTime();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getDataDias: data=" + data + ", dias=" + dias + ", result=" + result);
		}
		return result;
	}

	/**
	 * Obtém dia da data (mesmo dia que a data, mas com hora, minuto, segundo e
	 * milissegundo zerados.
	 * 
	 * @return dia da data - hora como 00:00:00
	 */
	public static Date getDataSemHora(Date data) {
		return getDataDias(data, 0);
	}

	/** Retorna a data mais antiga entre as datas informadas por parâmetro */
	public static Date dataMaisAntiga(Date data1, Date data2) {
		return data1 == null ? data2 : data2 == null ? data1 : data1.before(data2) ? data1 : data2;
	}

	/** Retorna a data mais recente entre as datas informadas por parâmetro */
	public static Date dataMaisRecente(Date data1, Date data2) {
		return data1 == null ? data2 : data2 == null ? data1 : !data1.before(data2) ? data1 : data2;
	}

	public static Date adicionarDias(Date data, int quantidade) {
		return adicionarPeriodo(data, quantidade, Calendar.DATE);
	}


	public static Date adicionarMes(Date data, int quantidade) {
		return adicionarPeriodo(data, quantidade, Calendar.MONTH);
	}
	
	public static Date adicionarPeriodo(Date data, int quantidade, int tipo) {

		Calendar calendar = DateUtil.getZeroHourCal(data);
		calendar.add(tipo, quantidade);
		Date result = calendar.getTime();
		return result;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static Integer getDaysBetweenDates360(Date FechaIni, Date FechaFin, Boolean MetodoEuro) {

		Integer DiaIni = FechaIni.getDay();
		Integer DiaFin = FechaFin.getDay();

		if (!MetodoEuro) {
			if (DiaIni == 31)
				FechaIni = adicionarDias(FechaIni, -1);
			if ((DiaFin == 31) && (DiaIni == 31))
				FechaFin = adicionarDias(FechaFin, -1);
			else if (DiaFin == 31)
				FechaFin = adicionarDias(FechaFin, 1);
		} else {
			if (DiaIni == 31)
				FechaIni = adicionarDias(FechaIni, -1);
			if (DiaFin == 31)
				FechaFin = adicionarDias(FechaFin, -1);
		}
		DiaIni = FechaIni.getDay();
		DiaFin = FechaFin.getDay();
		
		if (FechaFin.getYear() > FechaIni.getYear())
			FechaFin = adicionarMes(FechaFin, 1);

		return getDaysBetweenDates(FechaIni, FechaFin) * 30 + DiaFin - DiaIni;
	}
	
	//https://stackoverflow.com/questions/30168056/what-is-the-exact-excel-days360-algorithm
	@SuppressWarnings("deprecation")
	public static double Days360(Date StartDate, Date EndDate)
	{		
	    int StartDay = StartDate.getDate();
	    int StartMonth = StartDate.getMonth();
	    StartMonth++;
	    int StartYear = StartDate.getYear();
	    StartYear += 1900;
	    
	    int EndDay = EndDate.getDate();
	    int EndMonth = EndDate.getMonth();
	    EndMonth++;
	    int EndYear = EndDate.getYear();
	    EndYear += 1900;

	    if (StartDay == 31 || IsLastDayOfFebruary(StartDate))
	    {
	        StartDay = 30;
	    }

	    if (StartDay == 30 && EndDay == 31)
	    {
	        EndDay = 30;
	    }

	    return ((EndYear - StartYear) * 360) + ((EndMonth - StartMonth) * 30) + (EndDay - StartDay);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean IsLastDayOfFebruary(Date date)
	{
		int dateDay = date.getDate();
	    int dateMonth = date.getMonth();
	    dateMonth++;
	    
	    Date primeiroDiaMes = DateUtil.getFirstDayOfMonth(date);
	    Date ultimoDiaMes = DateUtil.getLastDayMonth(primeiroDiaMes);
	    int ultimoDiaMesDay = ultimoDiaMes.getDate();
	    
	    if(dateMonth == 2 && dateDay == ultimoDiaMesDay) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
}
