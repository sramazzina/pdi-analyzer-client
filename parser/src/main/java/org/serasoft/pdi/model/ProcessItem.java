package org.serasoft.pdi.model;

/*
 *
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
 *
 */

/**
 * Class Name   : ProcessItem.java
 * Package Name : org.serasoft.pdi.model
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 08/04/18
 * Description  :
 */
public class ProcessItem {

    private ProcessItemTypeEnum type;
    private String itemClass;
    private String name;
    private String description;

    public ProcessItem(ProcessItemTypeEnum type) {
        this.type = type;
    }

    public ProcessItem(ProcessItemTypeEnum type, String itemClass, String name) {
        this.type = type;
        this.itemClass = itemClass;
        this.name = name;
    }

    public ProcessItemTypeEnum getType() {
        return type;
    }

    public String getItemClass() {
        return itemClass;
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
}
