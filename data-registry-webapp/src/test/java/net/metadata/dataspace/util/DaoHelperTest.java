package net.metadata.dataspace.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: alabri
 * Date: 01/10/2010
 * Time: 3:05:54 PM
 */
public class DaoHelperTest {
    @Test
    public void testFromDecimalToOtherBase() throws Exception {
        assertEquals("3z", DaoHelper.fromDecimalToOtherBase(31, 123));
        assertEquals("vh5", DaoHelper.fromDecimalToOtherBase(31, 25456));
    }

    @Test
    public void testFromOtherBaseToDecimal() throws Exception {
        assertEquals(new Integer(123), DaoHelper.fromOtherBaseToDecimal(31, "3z"));
        assertEquals(new Integer(25456), DaoHelper.fromOtherBaseToDecimal(31, "vh5"));
    }
}
