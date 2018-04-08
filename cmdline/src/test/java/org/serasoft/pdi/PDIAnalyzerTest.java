package org.serasoft.pdi;

/**
 *
 *  Copyright 2016 - Sergio Ramazzina : sergio.ramazzina@serasoft.it
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Class Name   : PDIToolsTest.java
 * Package Name : org.serasoft.pdi
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 28/12/16
 * Description  :
 */


public class PDIAnalyzerTest {

    @Test
    public void testGetListOfChecks() {

        String checks = "parameters,connections,trans_flag";

        ArrayList<String> checkList = PDIAnalyzer.getListOfChecks(checks);
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
