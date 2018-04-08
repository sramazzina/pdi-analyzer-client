package org.serasoft.pdi.model;

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

/**
 * Class Name   : PDIProcessMissingReferences.java
 * Package Name : org.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 30/11/16
 * Description  :
 */
public class MissingReference {

    private String referencingStepName;
    private String referencingProcName;
    private String referencingProcFilename;
    private String type;
    private String refValue;

    public MissingReference(String referencingStepName,
                            String referencingProcName,
                            String referencingProcFilename) {
        this.referencingStepName = referencingStepName;
        this.referencingProcName = referencingProcName;
        this.referencingProcFilename = referencingProcFilename;
    }

    public String getReferencingStepName() {
        return referencingStepName;
    }

    public String getReferencingProcName() {
        return referencingProcName;
    }

    public String getReferencingProcFilename() {
        return referencingProcFilename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefValue() {
        return refValue;
    }

    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }
}
