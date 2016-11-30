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
public class PDIProcessMissingReferences {

    private String referencingStepName;
    private String referencingProcName;
    private String referemcingProcFilename;
    private String type;
    private HashMap<String, Object> attributes;

    public PDIProcessMissingReferences(String referencingStepName,
                                       String referencingProcName,
                                       String referemcingProcFilename,
                                       String type) {
        this.referencingStepName = referencingStepName;
        this.referencingProcName = referencingProcName;
        this.referemcingProcFilename = referemcingProcFilename;
        this.type = type;
    }

    public String getReferencingStepName() {
        return referencingStepName;
    }

    public String getReferencingProcName() {
        return referencingProcName;
    }

    public String getReferemcingProcFilename() {
        return referemcingProcFilename;
    }

    public void addAttribute(String key, Object value) {
        if (attributes == null)
            attributes = new HashMap<>();

        attributes.put(key, value);
    }

    public HashMap<String, Object> getAttributesMap() {
        return attributes;
    }
}
