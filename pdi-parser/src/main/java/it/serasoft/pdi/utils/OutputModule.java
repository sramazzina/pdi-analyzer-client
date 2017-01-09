package it.serasoft.pdi.utils;

import it.serasoft.pdi.model.Connection;
import it.serasoft.pdi.model.MissingReference;
import it.serasoft.pdi.model.Parameter;
import it.serasoft.pdi.model.Variable;

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
 * Class Name   : OutputModule.java
 * Package Name : it.serasoft.pdi.utils
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 09/01/17
 * Description  :
 */
public interface OutputModule {

    void printGeneralInfos();

    void printParameters(HashMap<String, Parameter> parms);

    void printVariables(List<Variable> vars);

    void printConnections(List<Connection> conns);

    void printMissingReferences(List<MissingReference> missingRefs);
}