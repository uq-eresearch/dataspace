package net.metadata.dataspace.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extension of Apache DateUtils, providing support for OAI-PMH date formats
 * Author: alabri
 * Date: 07/02/2011
 * Time: 3:17:01 PM
 */
public class DateUtil extends org.apache.commons.lang.time.DateUtils {

    public static final String[] OAI_DATE_FORMATS = new String[]{"yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss"};
    private static SimpleDateFormat sdfShort = new SimpleDateFormat(OAI_DATE_FORMATS[0]);
    private static SimpleDateFormat sdfLong = new SimpleDateFormat(OAI_DATE_FORMATS[1]);


    public static String formatDate(Date modificationDate, boolean showTime) {
        if (showTime) {

            return sdfLong.format(modificationDate);
        } else {

            return sdfShort.format(modificationDate);
        }
    }

}
