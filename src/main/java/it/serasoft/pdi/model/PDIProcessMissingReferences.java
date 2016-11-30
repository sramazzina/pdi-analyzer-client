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

    private String type;
    private HashMap<String, Object> attributes;


    public PDIProcessMissingReferences(String type) {
        this.type = type;
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
