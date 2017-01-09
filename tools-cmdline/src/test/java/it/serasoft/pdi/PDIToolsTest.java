package it.serasoft.pdi;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Class Name   : PDIToolsTest.java
 * Package Name : it.serasoft.pdi
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 28/12/16
 * Description  :
 */


public class PDIToolsTest {

    @Test
    public void testGetListOfChecks() {

        String checks = "parameters,connections,trans_flag";

        ArrayList<String> checkList = PDITools.getListOfChecks(checks);
        assertNotNull(checkList);
        assertEquals(checkList.size(), 3);

        /*checkList.forEach(item -> {
            int index = 0;
            assertEquals(item,
                    (index == 0 ? "parameters" : (index == 1 ? "connections" : "trans_flag")));
            index++;
        });*/

    }
}
