package com.webnowbr.siscoat.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;

/**
 * This class read the 'sysconf.properties' configuration file and loads the
 * paths into memory. Other modules/applications can access the PropertyLoader
 * to get the path of the configuration files.
 * 
 * @author walterwong
 */
public final class PropertyLoader {
	/**
	 * Logger.
	 */
	private static final Log LOG = LoggerFactory.getLogger();

	/**
	 * Node da propriedade do arquivo de configuracao.
	 */
	public static final String CONFIGURATION_FILE_KEY = "config.file";

	/**
	 * Configuration file name.
	 */
	private static final String CONFIGURATION_FILE_NAME = "sysconf.properties";
	/**
	 * Configuration file name. no windows
	 */
	private static final String CONFIGURATION_FILE_NAME_WINDOWS = "c:\\siscoat\\conf\\sysconf.properties";
	/**
	 * Configuration file name. no linux
	 */
	private static final String CONFIGURATION_FILE_NAME_LINUX = "/home/webnowbr/Siscoat/Conf/sysconf.properties";

	/**
	 * Configuration file name.
	 */
	private static final String WEBAAPI_FILE_NAME = "webapiconfig.properties";
	
	/**
	 * Configuration file name. no windows
	 */
	private static final String  WEBAAPI_FILE_NAME_WINDOWS = "c:\\siscoat\\conf\\webapiconfig.properties";
	/**
	 * Configuration file name. no linux
	 */
	private static final String  WEBAAPI_FILE_NAME_LINUX = "/home/webnowbr/Siscoat/Conf/webapiconfig.properties";

	/**
	 * Project configuration properties loaded from configuration file.
	 */
	private static Properties sAdrimsProperties;

	/**
	 * Flag indicating if the project is running gin unit test mode.
	 */
	private static boolean sIsUnitTest;

	static {
		loadProperties();
		String value = getProperty("app.unitTest");
		sIsUnitTest = (value != null) ? Boolean.valueOf(value.trim()) : false;
	}

	/**
	 * Private constructor to avoid instantiation.
	 */
	private PropertyLoader() {
		
	}

	/**
	 * Load properties from configuration file.
	 */
	private static synchronized void loadProperties() {
		InputStream input = null;
		sAdrimsProperties = new Properties();
		try {
			String configurationFileName = System.getProperty(CONFIGURATION_FILE_KEY);
			if (configurationFileName != null) {
				LOG.info("Loading adrims properties from file: " + configurationFileName);
				input = new FileInputStream(configurationFileName);
				sAdrimsProperties.load(input);
			} else {
				LOG.info("-D" + CONFIGURATION_FILE_KEY + " parameter not present. Looking in the classpath for the "
						+ CONFIGURATION_FILE_NAME);

				input = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGURATION_FILE_NAME);

				if (CommonsUtil.sistemaWindows() && new File(CONFIGURATION_FILE_NAME_WINDOWS).exists()) {
					input = new FileInputStream(CONFIGURATION_FILE_NAME_WINDOWS);
				} else 	if (new File(CONFIGURATION_FILE_NAME_LINUX).exists()) {
					input = new FileInputStream(CONFIGURATION_FILE_NAME_LINUX);
				}
				
				if (input != null) {
					LOG.info(CONFIGURATION_FILE_NAME + " found in the classpath.");
					sAdrimsProperties.load(input);
				} else {
					final File file = new File(".");
					LOG.info(CONFIGURATION_FILE_NAME + " not found on path: " + file.getAbsolutePath());
				}
				
				
				InputStream inputWebApi = Thread.currentThread().getContextClassLoader().getResourceAsStream(WEBAAPI_FILE_NAME);

				if (CommonsUtil.sistemaWindows() && new File(WEBAAPI_FILE_NAME_WINDOWS).exists()) {
					inputWebApi = new FileInputStream(WEBAAPI_FILE_NAME_WINDOWS);
				} else 	if (new File(WEBAAPI_FILE_NAME_LINUX).exists()) {
					inputWebApi = new FileInputStream(WEBAAPI_FILE_NAME_LINUX);
				}
				if (inputWebApi != null) {
					LOG.info(CONFIGURATION_FILE_NAME + " found in the classpath.");
					sAdrimsProperties.load(inputWebApi);
				} else {
					final File file = new File(".");
					LOG.info(CONFIGURATION_FILE_NAME + " not found on path: " + file.getAbsolutePath());
				}				
			}
		} catch (IOException ioe) {
			LOG.error(CONFIGURATION_FILE_NAME + " not found!");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioExc) {
					LOG.error("Error closing stream", ioExc);
				}
			}
		}
	}

	/**
	 * This method gets the property given by the name and returns it if present,
	 * otherwise, it returns an empty string.
	 * 
	 * @param propertyName - property to be retrieved
	 * @return Property return
	 */
	public static String getProperty(final String propertyName) {
		return getString(propertyName, "");
	}

	/**
	 * Returns a property value as boolean.
	 * 
	 * @param propertyName The property name to retrieve.
	 * @param defaultValue The value to return if the property is not found.
	 * @return the boolean value of the property.
	 */
	public static boolean getBoolean(final String propertyName, final boolean defaultValue) {
		LOG.debug("Retrieving boolean property: " + propertyName);
		String property = sAdrimsProperties.getProperty(propertyName);
		if (property != null) {
			return (Boolean.parseBoolean(property.trim()));
		}

		return defaultValue;
	}

	/**
	 * Returns a property value as boolean. Will return false if the property is not
	 * found.
	 * 
	 * @param propertyName The property name to retrieve.
	 * @return the boolean value of the property.
	 */
	public static boolean getBoolean(final String propertyName) {
		return getBoolean(propertyName, false);
	}

	/**
	 * Returns a property value as String.
	 * 
	 * @param propertyName The property name to retrieve.
	 * @param defaultValue The value to return if the property is not found.
	 * @return the String value of the property.
	 */
	public static String getString(final String propertyName, final String defaultValue) {
		LOG.debug("Retrieving String property: " + propertyName);
		return sAdrimsProperties.getProperty(propertyName, defaultValue);
	}

	/**
	 * Returns a property value as String. Will return null if the property is not
	 * found.
	 * 
	 * @param propertyName The property name to retrieve.
	 * @return the String value of the property.
	 */
	public static String getString(final String propertyName) {
		return getString(propertyName, null);
	}

	/**
	 * Returns a property value as integer.
	 * 
	 * @param propertyName The property name to retrieve.
	 * @param defaultValue The value to return if the property is not found.
	 * @return the integer value of the property.
	 */
	public static int getInteger(final String propertyName, final int defaultValue) {
		LOG.debug("Retrieving integer property: " + propertyName);
		String property = sAdrimsProperties.getProperty(propertyName);
		if (property != null) {
			try {
				return (Integer.parseInt(property.trim()));
			} catch (NumberFormatException e) {
				// Ignoring exception.
			}
		}

		return defaultValue;
	}

	public static boolean isInUnitTestMode() {
		return sIsUnitTest;
	}
}
