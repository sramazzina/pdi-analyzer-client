package it.serasoft.pdi.model;

/**
 * Class Name   : ProcessVariableBase.java
 * Package Name : it.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 01/12/16
 * Description  :
 */
public class BaseVariable {

    private String name;

    public BaseVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
