package it.serasoft.pdi.utils;

import it.serasoft.pdi.model.PDIProcessConnection;
import it.serasoft.pdi.model.PDIProcessMissingReferences;
import it.serasoft.pdi.model.PDIProcessParameterHolder;

import java.util.HashMap;
import java.util.List;

/**
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
 */


/**
 * Class Name   : ConsoleOutputUtil.java
 * Package Name : it.serasoft.pdi.utils
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 25/11/16
 * Description  :
 */
public class ConsoleOutputUtil {

    public static void printParameters(HashMap<String, PDIProcessParameterHolder> parms) {

        System.out.println("| Parameters");
        System.out.println("| ============================================");
        parms.forEach((key, value) -> {
            System.out.println("| | Name: " + key + " - Description: " + value.getDescription() + " - Default: " + value.getDefaultValue());
        });
    }

    public static void printConnections(List<PDIProcessConnection> conns) {
        System.out.println("| Connections");
        System.out.println("| ============================================");
        conns.forEach(item -> {
            System.out.println("| | Connection Name: " + item.getName());
            item.getProperties().forEach((key, value) -> {
                if (value.length()>0) System.out.println("| | |" +  key + "-> " + value);
            });
        });
    }

    public static void printMissingReferences(List<PDIProcessMissingReferences> missingRefs) {

        if (!missingRefs.isEmpty()) {
            System.out.println("| Missing References");
            System.out.println("| ============================================");
            missingRefs.forEach(item -> {
                item.getAttributesMap().forEach((key, value) -> {
                    System.out.println("| " + key + " -> " + value);
                });
            });
        }
    }
}
