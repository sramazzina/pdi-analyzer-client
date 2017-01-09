package it.serasoft.pdi.model;

/**
 * Class Name   : ProcessVariable.java
 * Package Name : it.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 01/12/16
 * Description  :
 */
public class Variable extends BaseVariable {

    private String scope;
    private String stepName;

    public Variable(String stepName, String name) {
        super(name);
        this.stepName = stepName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public String toString() {
        return "ProcessVariable{" +
                "name='" + getName() + '\'' +
                ", scope='" + scope + '\'' +
                ", stepName='" + stepName + '\'' +
                '}';
    }
}
