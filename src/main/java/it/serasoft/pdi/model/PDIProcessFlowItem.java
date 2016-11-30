package it.serasoft.pdi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class Name   : PDIProcessStep.java
 * Package Name : it.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 27/11/16
 * Description  :
 */
public class PDIProcessFlowItem {

    private String name;
    private String description;
    private String type;
    private List<PDIProcessFlowItem> goesTo;
    private List<PDIProcessFlowItem> comesFrom;


    private HashMap<String, String> attributes = new HashMap<>();

    public PDIProcessFlowItem(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void addGoesToItem(PDIProcessFlowItem item) {

        if (goesTo == null)
            goesTo = new ArrayList<>();

        goesTo.add(item);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
