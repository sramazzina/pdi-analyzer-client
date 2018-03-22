package it.serasoft.pdi.model;

/**
 * Copyright 2016 - Sergio Ramazzina : sergio.ramazzina@serasoft.it
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.Map;

/**
 * Class Name   : StructureContainer.java
 * Package Name : it.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 09/01/17
 * Description  :
 */
public class ProcessMetadata {

    private static final String TRANSACTIONAL_YES = "Y";

    private ProcessTypeEnum typeEnum;
    private String name;
    private String description;
    private String extendedDescription;
    private boolean transactional;

    protected List<Connection> connections;
    protected Map<String, Parameter> params;
    protected Map<String, Step> steps;
    protected List<MissingReference> missingRefs;
    protected List<Variable> vars;

    public ProcessMetadata() {
        init();
    }


    protected void init() {

        transactional = false;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(String transactional) {
        this.transactional = transactional != null && transactional.equals(TRANSACTIONAL_YES);
    }

    public ProcessTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(ProcessTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public void setExtendedDescription(String extendedDescription) {
        this.extendedDescription = extendedDescription;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public Map<String, Parameter> getParams() {
        return params;
    }

    public void setParams(Map<String, Parameter> params) {
        this.params = params;
    }

    public Map<String, Step> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, Step> steps) {
        this.steps = steps;
    }

    public List<MissingReference> getMissingRefs() {
        return missingRefs;
    }

    public void setMissingRefs(List<MissingReference> missingRefs) {
        this.missingRefs = missingRefs;
    }

    public List<Variable> getVars() {
        return vars;
    }

    public void setVars(List<Variable> vars) {
        this.vars = vars;
    }
}
