package com.webnowbr.siscoat.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class LoggerFactory {

    /**
     * Hidden constructor.
     */
    private LoggerFactory() {

    }

    /**
     * Factory method for Log instances.
     * @return A Log instance for the calling class.
     */
    public static Log getLogger() {
        Throwable t = new Throwable();
        StackTraceElement callingClass = t.getStackTrace()[1];
        return LogFactory.getLog(callingClass.getClassName());
    }
}
