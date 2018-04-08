package org.serasoft.pdi.parser.model;

/*
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

/**
 * Class Name   : ProcessHop.java
 * Package Name : org.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 08/04/18
 * Description  :
 */
public class ProcessHop {

    String from;
    String to;
    String name;

    public ProcessHop(String from, String to, String name) {
        this.from = from;
        this.to = to;
        this.name = name;
    }

    public ProcessHop(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
