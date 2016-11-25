package it.serasoft.pdi.model;

import java.io.File;
import java.util.HashMap;

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
 * Class Name   : PDIProcessConnection.java
 * Package Name : it.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 25/11/16
 * Description  :
 */
public class PDIProcessConnection {

    private String name;
    private File pdiProcFile;
    private HashMap<String, String> properties = new HashMap<>();
    private HashMap<String, String> jdbcAttributes = new HashMap<>();

    public PDIProcessConnection(String name, File pdiProcFile) {
        this.name = name;
        this.pdiProcFile = pdiProcFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getPdiProcFile() {
        return pdiProcFile;
    }

    public void setPdiProcFile(File pdiProcFile) {
        this.pdiProcFile = pdiProcFile;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public HashMap<String, String> getJdbcAttributes() {
        return jdbcAttributes;
    }

    public void setJdbcAttributes(HashMap<String, String> jdbcAttributes) {
        this.jdbcAttributes = jdbcAttributes;
    }

    @Override
    public String toString() {
        return "PDIProcessConnection{" +
                "name='" + name + '\'' +
                '}';
    }
}
