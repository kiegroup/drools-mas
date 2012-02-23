/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 *
 * @author salaboy
 */
public class LoggerHelper{
    private static Logger logger = LoggerFactory.getLogger(LoggerHelper.class);
    

    
    
    public static void warn(Marker marker, String string, Throwable thrwbl) {
        logger.warn(marker, string, thrwbl);
    }

    public static void warn(Marker marker, String string, Object[] os) {
        logger.warn(marker, string, os);
    }

    public static void warn(Marker marker, String string, Object o, Object o1) {
        logger.warn(marker, string, o, o1);
    }

    public static void warn(Marker marker, String string, Object o) {
        logger.warn(marker, string, o);
    }

    public static void warn(Marker marker, String string) {
        logger.warn(marker, string);
    }

    public static void warn(String string, Throwable thrwbl) {
        logger.warn(string, thrwbl);
    }

    public static void warn(String string, Object o, Object o1) {
        logger.warn(string, o, o1);
    }

    public static void warn(String string, Object[] os) {
        logger.warn(string, os);
    }

    public static void warn(String string, Object o) {
        logger.warn(string, o);
    }

    public static void warn(String string) {
        logger.warn(string);
    }

    public static void trace(Marker marker, String string, Throwable thrwbl) {
        logger.trace(marker, string, thrwbl);
    }

    public static void trace(Marker marker, String string, Object[] os) {
        logger.trace(marker, string, os);
    }

    public static void trace(Marker marker, String string, Object o, Object o1) {
        logger.trace(marker, string, o, o1);
    }

    public static void trace(Marker marker, String string, Object o) {
        logger.trace(marker, string, o);
    }

    public static void trace(Marker marker, String string) {
        logger.trace(marker, string);
    }

    public static void trace(String string, Throwable thrwbl) {
        logger.trace(string, thrwbl);
    }

    public static void trace(String string, Object[] os) {
        logger.trace(string, os);
    }

    public static void trace(String string, Object o, Object o1) {
        logger.trace(string, o, o1);
    }

    public static void trace(String string, Object o) {
        logger.trace(string, o);
    }

    public static void trace(String string) {
        logger.trace(string);
    }

    public static boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    public static boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public static boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    public static boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public static boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    public static boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public static boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    public static boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public static boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public static void info(Marker marker, String string, Throwable thrwbl) {
        logger.info(marker, string, thrwbl);
    }

    public static void info(Marker marker, String string, Object[] os) {
        logger.info(marker, string, os);
    }

    public static void info(Marker marker, String string, Object o, Object o1) {
        logger.info(marker, string, o, o1);
    }

    public static void info(Marker marker, String string, Object o) {
        logger.info(marker, string, o);
    }

    public static void info(Marker marker, String string) {
        logger.info(marker, string);
    }

    public static void info(String string, Throwable thrwbl) {
        logger.info(string, thrwbl);
    }

    public static void info(String string, Object[] os) {
        logger.info(string, os);
    }

    public static void info(String string, Object o, Object o1) {
        logger.info(string, o, o1);
    }

    public static void info(String string, Object o) {
        logger.info(string, o);
    }

    public static void info(String string) {
        logger.info(string);
    }

    public static String getName() {
        return logger.getName();
    }

    public static void error(Marker marker, String string, Throwable thrwbl) {
        logger.error(marker, string, thrwbl);
    }

    public static void error(Marker marker, String string, Object[] os) {
        logger.error(marker, string, os);
    }

    public static void error(Marker marker, String string, Object o, Object o1) {
        logger.error(marker, string, o, o1);
    }

    public static void error(Marker marker, String string, Object o) {
        logger.error(marker, string, o);
    }

    public static void error(Marker marker, String string) {
        logger.error(marker, string);
    }

    public static void error(String string, Throwable thrwbl) {
        logger.error(string, thrwbl);
    }

    public static void error(String string, Object[] os) {
        logger.error(string, os);
    }

    public static void error(String string, Object o, Object o1) {
        logger.error(string, o, o1);
    }

    public static void error(String string, Object o) {
        logger.error(string, o);
    }

    public static void error(String string) {
        logger.error(string);
    }

    public static void debug(Marker marker, String string, Throwable thrwbl) {
        logger.debug(marker, string, thrwbl);
    }

    public static void debug(Marker marker, String string, Object[] os) {
        logger.debug(marker, string, os);
    }

    public static void debug(Marker marker, String string, Object o, Object o1) {
        logger.debug(marker, string, o, o1);
    }

    public static void debug(Marker marker, String string, Object o) {
        logger.debug(marker, string, o);
    }

    public static void debug(Marker marker, String string) {
        logger.debug(marker, string);
    }

    public static void debug(String string, Throwable thrwbl) {
        logger.debug(string, thrwbl);
    }

    public static void debug(String string, Object[] os) {
        logger.debug(string, os);
    }

    public static void debug(String string, Object o, Object o1) {
        logger.debug(string, o, o1);
    }

    public static void debug(String string, Object o) {
        logger.debug(string, o);
    }

    public static void debug(String string) {
        logger.debug(string);
    }
    
}
