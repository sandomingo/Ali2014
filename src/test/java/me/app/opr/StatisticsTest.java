package me.app.opr;

import org.junit.Test;

import java.util.Date;

/**
 * Created by SanDomingo on 4/17/14.
 */
public class StatisticsTest {
    @Test
    public void testString2Date() throws Exception {
        String dateString = "5月29日";
        Date result = Statistics.string2Date(dateString);
        System.out.println(result);
    }
}
