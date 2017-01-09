package it.serasoft.pdi.model;

import java.util.HashMap;

/**
 * Class Name   : PDIProcessMissingReferences.java
 * Package Name : it.serasoft.pdi.model
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
