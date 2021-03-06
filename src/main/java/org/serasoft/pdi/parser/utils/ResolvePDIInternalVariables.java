package org.serasoft.pdi.parser.utils;

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
 * Class Name   : ReslvePDIInternalVariable.java
 * Package Name : org.serasoft.pdi.utils
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */

public class ResolvePDIInternalVariables {

    public static String resolve(String pdiContentEntry, String value) {
        return pdiContentEntry.replace("${Internal.Job.Filename.Directory}", value)
                .replace("${Internal.Transformation.Filename.Directory}", value)
                .replace("${Internal.Entry.Current.Directory}", value);
    }
}
