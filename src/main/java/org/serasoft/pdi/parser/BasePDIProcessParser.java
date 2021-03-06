package org.serasoft.pdi.parser;

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

import org.serasoft.pdi.parser.model.*;
import org.serasoft.pdi.parser.utils.MetadataPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Name   : ParsePDIMetadata.java
 * Package Name : org.serasoft.pdi.utils
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */
public abstract class BasePDIProcessParser {

    private Logger l = LoggerFactory.getLogger(JobParser.class);

    protected File procFileRef;
    protected int depth;
    protected boolean followSymlinks;

    public BasePDIProcessParser(File procFileRef, int depth, boolean followSymlinks) {
        init(procFileRef, depth, followSymlinks);
    }

    protected void init(File procFileRef, int depth, boolean followSymlinks) {

        this.procFileRef = procFileRef;
        this.depth = depth;
        this.followSymlinks = followSymlinks;
    }

    public abstract ProcessMetadata parse();

    public abstract ProcessMetadata parse(String parentPDIProcName,
                                          File parentPDIProcFile,
                                          String callerStepName);


    protected void parseParameters(XMLStreamReader xmlStreamReader,
                                   MetadataPath metadataPath,
                                   ProcessMetadata processMetadata) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("parameter")) {
                            parseParameter(xmlStreamReader, metadataPath, processMetadata);
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("parameters"))
                            elementAnalyzed = true;
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    protected void parseParameter(XMLStreamReader xmlStreamReader,
                                  MetadataPath metadataPath,
                                  ProcessMetadata processMetadata) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;
        String paramName = null;

        Parameter parameterHolder = null;
        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);


                        if (elementName.equals("default_value")) {
                            assert parameterHolder != null;
                            parameterHolder.setDefaultValue(readElementText(xmlStreamReader, metadataPath));
                        } else if (elementName.equals("name")) {
                            paramName = readElementText(xmlStreamReader, metadataPath);
                            parameterHolder = new Parameter(paramName);
                        } else if (elementName.equals("description")) {
                            assert parameterHolder != null;
                            parameterHolder.setDescription(readElementText(xmlStreamReader, metadataPath));
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        addParameterToCollectedMetadata(parameterHolder, processMetadata);
                        if (elementName.equals("parameter")) {
                            elementAnalyzed = true;
                        }
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }


    protected Connection parseConnection(XMLStreamReader xmlStreamReader, MetadataPath metadataPath) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;
        String elementValue;
        Connection retConn = null;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("attributes")) {
                            parseConnectionAttributes(xmlStreamReader, metadataPath, retConn);
                        } else {
                            elementValue = readElementText(xmlStreamReader, metadataPath);
                            if (elementName.equals("name")) {
                                retConn = new Connection(elementValue, procFileRef);
                            } else {
                                addConnectionPropertyToCollectedMetadata(retConn, elementName, elementValue);
                            }
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        if (elementName.equals("connection")) {
                            elementAnalyzed = true;
                        }
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return retConn;
    }

    private void parseConnectionAttributes(XMLStreamReader xmlStreamReader,
                                           MetadataPath metadataPath,
                                           Connection conn) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("attribute")) {
                            parseConnectionAttribute(xmlStreamReader, metadataPath, conn);
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        if (elementName.equals("attributes")) {
                            elementAnalyzed = true;
                        }
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    protected void parseConnectionAttribute(XMLStreamReader xmlStreamReader,
                                            MetadataPath metadataPath,
                                            Connection conn) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;
        String elementValue = null;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("code")) {
                            elementValue = readElementText(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("attribute")) {
                            addJdbcAttributeToCollectedMetadata(conn, elementValue, readElementText(xmlStreamReader, metadataPath));
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        if (elementName.equals("attribute"))
                            elementAnalyzed = true;
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    protected String readElementText(XMLStreamReader xmlStreamReader, MetadataPath metadataPath) {

        StringBuilder content = new StringBuilder();

        int eventType;
        boolean elementAnalyzed = false;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.CHARACTERS:
                    case XMLStreamReader.CDATA:
                        content.append(xmlStreamReader.getText());
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementAnalyzed = true;
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public void outputObjectContent() {


    }

    protected void addConnectionToCollectedMetadata(Connection conn, ProcessMetadata processMetadata) {
        if (conn != null) {
            if (processMetadata.getConnections() == null)
                // Lazy init connections structure
                processMetadata.setConnections(new ArrayList<>());

            processMetadata.getConnections().add(conn);
        } else {
            // TODO Throws exception in case connection is null
        }
    }

    protected void addVariableToCollectedMetadata(Variable var, ProcessMetadata processMetadata) {
        if (var != null) {
            if (processMetadata.getVars() == null)
                // Lazy init variables structure
                processMetadata.setVars(new ArrayList<>());

            processMetadata.getVars().add(var);
        } else {
            // TODO Throws exception in case variable is null
        }
    }

    protected void addParameterToCollectedMetadata(Parameter parameter, ProcessMetadata processMetadata) {

        if (parameter != null) {
            if (processMetadata.getParams() == null)
                // Lazy init parameters structure
                processMetadata.setParams(new HashMap<>());

            processMetadata.getParams().put(parameter.getName(), parameter);
        } else {
            // TODO Throws exception in case parameter is null
        }
    }

    protected void addItemToItemsCollection(String name, ProcessItem item, Map<String, ProcessItem> items) {

        if (name == null) {
            // TODO Throws exception in case step's name is null
        }

        if (item != null) {
            if (items == null)
                // Lazy init parameters structure
                items = new HashMap<>();

            items.put(name, item);
        } else {
            // TODO Throws exception in case parameter is null
        }
    }

    protected void addConnectionPropertyToCollectedMetadata(Connection c, String name, String value) {

        if (name == null) {
            // TODO Throws exception in case step's connection property name is null
        }

        if (value != null) {
            if (c.getProperties() == null)
                // Lazy init connection properties structure
                c.setProperties(new HashMap<>());

            c.getProperties().put(name, value);
        } else {
            // TODO Throws exception in case connection property is null
        }
    }

    protected void addJdbcAttributeToCollectedMetadata(Connection c, String name, String value) {

        if (name == null) {
            // TODO Throws exception in case JDBC attribute's name is null
        }

        if (value != null) {
            if (c.getJdbcAttributes() == null)
                // Lazy init parameters structure
                c.setJdbcAttributes(new HashMap<>());

            c.getJdbcAttributes().put(name, value);
        } else {
            // TODO Throws exception in case JDBC attribute is null
        }
    }
}
