package com.webnowbr.siscoat.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
	
	public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
	    Calendar startCal = Calendar.getInstance();
	    startCal.setTime(startDate);

	    Calendar endCal = Calendar.getInstance();
	    endCal.setTime(endDate);

	    int workDays = 0;

	    //Return 0 if start and end are the same
	    if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
	        return 0;
	    }

	    if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
	        startCal.setTime(endDate);
	        endCal.setTime(startDate);
	    }
	    
	    
	    
	    List<Calendar> listaferiados = new ArrayList<Calendar>();
	    listaferiados = getFeriados();
	    
	    while(endCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY 
        		|| endCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        		|| listaferiados.contains(endCal)) {
	    	endCal.add(Calendar.DAY_OF_MONTH, 1);
	    }
	    
	    endCal.add(Calendar.DAY_OF_MONTH, -1);
	    
	    do {
	       //excluding start date
	        startCal.add(Calendar.DAY_OF_MONTH, 1);
	        if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY 
	        		&& startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
	        		&& !listaferiados.contains(startCal)) {
	            ++workDays;
	        }
	    } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

	    return workDays;
	}
	
	private static List<Calendar> getFeriados(){
		List<Calendar> listaferiados = new ArrayList<Calendar>();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		Date dataHj = dataHoje.getTime();
		
		for(int a = dataHj.getYear() + 1900; a < dataHj.getYear() + 2000; a++) {
			List<Calendar> listaferiadosAno = new ArrayList<Calendar>();
			listaferiadosAno = getFeriadosMoveis(a);
			listaferiados.addAll(listaferiadosAno);
		}
		
		return listaferiados;
	}
	
	public static List<Calendar> getFeriadosMoveis(int year) {
		//baseado no codigo: https://pt.stackoverflow.com/questions/318809/dias-%C3%BAteis-e-api-java-8-como-verificar/320353#320353
		List<Calendar> dates = new ArrayList<Calendar>();

		Date pascoaDate;
		
		Date anoNovoDate;
		Date tiradentesDate;
		Date diaDoTrabalhoDate;
		Date independenciaDate;
		Date nossaSenhoraDate;
		Date finadosDate;
		Date proclamacaoDate;
		Date natalDate;
		
		Calendar pascoa = Calendar.getInstance();
		Calendar carnaval = Calendar.getInstance();
		Calendar corpusChristi = Calendar.getInstance();
		Calendar sextaFeiraSanta = Calendar.getInstance();
		
		Calendar anoNovo = Calendar.getInstance();
		Calendar tiradentes = Calendar.getInstance();
		Calendar diaDoTrabalho = Calendar.getInstance();
		Calendar independencia = Calendar.getInstance();
		Calendar nossaSenhora = Calendar.getInstance();
		Calendar finados = Calendar.getInstance();
		Calendar proclamacao = Calendar.getInstance();
		Calendar natal = Calendar.getInstance();

	    int a = year % 19;
	    int b = year / 100;
	    int c = year % 100;
	    int d = b / 4;
	    int e = b % 4;
	    int f = (b + 8) / 25;
	    int g = (b - f + 1) / 3;
	    int h = (19 * a + b - d - g + 15) % 30;
	    int i = c / 4;
	    int k = c % 4;
	    int l = (32 + 2 * e + 2 * i - h - k) % 7;
	    int m = (a + 11 * h + 22 * l) / 451;
	    int month = (h + l - 7 * m + 114) / 31;
	    int day = ((h + l - 7 * m + 114) % 31) + 1;

	    pascoaDate = new Date(year - 1900, month - 1, day);
	    pascoa.setTime(pascoaDate);	    
	    anoNovoDate = new Date(year - 1900, 1 - 1, 1);
	    anoNovo.setTime(anoNovoDate);
	    tiradentesDate= new Date(year - 1900, 4 - 1, 21);
	    tiradentes.setTime(tiradentesDate);
	    diaDoTrabalhoDate = new Date(year - 1900, 5 - 1, 01);
	    diaDoTrabalho.setTime(diaDoTrabalhoDate);
	    independenciaDate = new Date(year - 1900, 9 - 1, 07);
	    independencia.setTime(independenciaDate);
	    nossaSenhoraDate = new Date(year - 1900, 10 - 1, 12);
	    nossaSenhora.setTime(nossaSenhoraDate);	    
	    finadosDate = new Date(year - 1900, 11 - 1, 2);
	    finados.setTime(finadosDate);    
	    proclamacaoDate = new Date(year - 1900, 11 - 1, 15);
	    proclamacao.setTime(proclamacaoDate);	    
	    natalDate = new Date(year - 1900, 12 - 1, 25);
	    natal.setTime(natalDate);
	    
	    // Carnaval 47 dias antes da pascoa (sempre cai na terça)
	    pascoa.add(Calendar.DAY_OF_MONTH, -47);
	    carnaval.setTimeInMillis(pascoa.getTimeInMillis());;
	    pascoa.add(Calendar.DAY_OF_MONTH, 47);
	    
	    // CorpusChristi 60 dias apos a pascoa
	    pascoa.add(Calendar.DAY_OF_MONTH, 60);
	    corpusChristi.setTimeInMillis(pascoa.getTimeInMillis());
	    pascoa.add(Calendar.DAY_OF_MONTH, -60);

	    pascoa.add(Calendar.DAY_OF_MONTH, -2);
	    sextaFeiraSanta.setTimeInMillis(pascoa.getTimeInMillis());
	    pascoa.add(Calendar.DAY_OF_MONTH, 2);

	    // páscoa cai sempre no domingo, entao não precisaria adicionar como feriado
	    // dates.add(pascoa);

	    // carnaval: adicionar um dia antes e depois (emenda de segunda e quarta-feira de cinzas)
	    dates.add(carnaval);
	    Calendar diaAntCarnaval = Calendar.getInstance();
	    diaAntCarnaval.setTimeInMillis(carnaval.getTimeInMillis());
	    diaAntCarnaval.add(Calendar.DAY_OF_MONTH, -1);
	    dates.add(diaAntCarnaval); // emenda a segunda-feira

	    // corpus christi, emendar (adicionar a sexta)
	    dates.add(corpusChristi);
	    // if apenas para confirmar se é quinta-feira
	    if(corpusChristi.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
	    	corpusChristi.add(Calendar.DAY_OF_MONTH, -1);
	    } else if(corpusChristi.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
	    	corpusChristi.add(Calendar.DAY_OF_MONTH, 1);
	    }
	    
	    dates.add(corpusChristi);
	    dates.add(sextaFeiraSanta);	 
	    dates.add(anoNovo);
	    dates.add(tiradentes);
	    dates.add(diaDoTrabalho);
	    dates.add(independencia); 
	    dates.add(nossaSenhora);	        
	    dates.add(finados); 
	    dates.add(proclamacao);      
	    dates.add(natal);
	    
	    return dates;
	}
	
	public static String getDiaDaSemana(Date date) {
		switch (date.getDay()) {
		case 0:
			return "domingo";
		case 1:
			return "segunda-feira";
		case 2:
			return "terça-feira";
		case 3:
			return "quarta-feira";
		case 4:
			return "quinta-feira";
		case 5:
			return "sexta-feira";
		case 6:
			return "sábado";
		}
		return "";
	}
	
	
}
