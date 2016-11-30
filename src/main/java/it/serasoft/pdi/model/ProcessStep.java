package it.serasoft.pdi.model;

/**
 * Class Name   : PDIProcessStep.java
 * Package Name : it.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 27/11/16
 * Description  :
 */
public class ProcessStep {

    private String name;
    private String description;
    private String type;

    public ProcessStep(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
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

}
