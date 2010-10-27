package net.metadata.dataspace.util;

/**
 * User: alabri
 * Date: 01/10/2010
 * Time: 2:51:41 PM
 */
public class DaoHelper {

    private static final String BASE_CHARACTERS = "0123456789bcdfghjklmnpqrstvwxyz";

    public static String fromDecimalToOtherBase(int base, int decimalNumber) {
        String tempVal = decimalNumber == 0 ? "0" : "";
        int mod = 0;

        while (decimalNumber != 0) {
            mod = decimalNumber % base;
            tempVal = BASE_CHARACTERS.substring(mod, mod + 1) + tempVal;
            decimalNumber = decimalNumber / base;
        }
        return tempVal;
    }

    public static Integer fromOtherBaseToDecimal(int base, String number) {
        int iterator = number.length();
        int returnValue = 0;
        int multiplier = 1;

        while (iterator > 0) {
            returnValue = returnValue + (BASE_CHARACTERS.indexOf(number.substring(iterator - 1, iterator)) * multiplier);
            multiplier = multiplier * base;
            --iterator;
        }
        return returnValue;
    }

}
