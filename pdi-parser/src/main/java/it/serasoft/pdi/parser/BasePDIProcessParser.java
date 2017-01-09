package it.serasoft.pdi.parser;

import it.serasoft.pdi.model.*;
import it.serasoft.pdi.utils.MetadataPath;
import it.serasoft.pdi.utils.OutputModule;
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
 * Class Name   : ParsePDIMetadata.java
 * Package Name : it.serasoft.pdi.utils
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */
public abstract class BasePDIProcessParser {

    private Logger l = LoggerFactory.getLogger(JobParser.class);

    protected OutputModule outputModule;

    protected File procFileRef;
    protected int depth;
    protected boolean followSymlinks;

    protected  ProcessMetadata collectedProcessMetadata;

    protected List<BasePDIProcessParser> linkedPDIMetadata;

    public BasePDIProcessParser(File procFileRef, int depth, boolean followSymlinks, OutputModule outputModule) {
        init(procFileRef, depth, followSymlinks, outputModule);
    }

    protected void init(File procFileRef, int depth, boolean followSymlinks, OutputModule outputModule) {

        collectedProcessMetadata = new ProcessMetadata();

        this.outputModule = outputModule;
        this.procFileRef = procFileRef;
        this.depth = depth;
        this.followSymlinks = followSymlinks;
    }

    public abstract void parse();

    public abstract void parse(String parentPDIProcName,
                               File parentPDIProcFile,
                               String callerStepName);


    public List<MissingReference> getMissingFilesReferencesList() {

        final List<MissingReference> missingRefs = new ArrayList<>();
        List<MissingReference> collectedMissingReferences = collectedProcessMetadata.getMissingRefs();

        if (this.linkedPDIMetadata != null && !this.linkedPDIMetadata.isEmpty()) {
            this.linkedPDIMetadata.forEach(item -> missingRefs.addAll(item.getMissingFilesReferencesList()));
        } else {
            if (!collectedMissingReferences.isEmpty()) {
                collectedMissingReferences.forEach(item -> missingRefs.add(item));
            }
        }

        return missingRefs;
    }

    public List<String> getReferencedProcessFilesList() {

        final List<String> refFilesList = new ArrayList<>();

        if (this.linkedPDIMetadata != null && !this.linkedPDIMetadata.isEmpty()) {
            this.linkedPDIMetadata.forEach(item -> refFilesList.addAll(item.getReferencedProcessFilesList()));
        } else {

            if (procFileRef != null) {
                refFilesList.add(procFileRef.getName());
            }
        }

        return refFilesList;
    }

    protected void parseParameters(XMLStreamReader xmlStreamReader, MetadataPath metadataPath) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("parameter")) {
                            parseParameter(xmlStreamReader, metadataPath);
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
                                  MetadataPath metadataPath) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;
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
                            parameterHolder.setDefaultValue(readElementText(xmlStreamReader, metadataPath));
                        } else if (elementName.equals("name")) {
                            parameterHolder = new Parameter(readElementText(xmlStreamReader, metadataPath));
                        } else if (elementName.equals("description")) {
                            parameterHolder.setDescription(readElementText(xmlStreamReader, metadataPath));
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        addParameterToCollectedMetadata(paramName, parameterHolder);
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

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;
        String elementValue = null;
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
                                retConn.getProperties().put(elementName, elementValue);
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
                                           Connection valuesMap) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("attribute")) {
                            parseConnectionAttribute(xmlStreamReader, metadataPath, valuesMap);
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
                                            Connection valuesMap) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;
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
                            valuesMap.getJdbcAttributes().put(elementValue, readElementText(xmlStreamReader, metadataPath));
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

    protected String parseSimpleTextElementByName(XMLStreamReader xmlStreamReader,
                                                  String elementName,
                                                  MetadataPath metadataPath) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String rValue = null;

        rValue = readElementText(xmlStreamReader, metadataPath);
        l.debug(elementName + ": " + rValue);

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        if (elementName.equals(elementName))
                            elementAnalyzed = true;
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return rValue;
    }

    protected String readElementText(XMLStreamReader xmlStreamReader, MetadataPath metadataPath) {

        StringBuilder content = new StringBuilder();

        int eventType = 0;
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

        List<Connection> connections = collectedProcessMetadata.getConnections();
        Map<String, Parameter> params = collectedProcessMetadata.getParams();
        List<MissingReference> missingRefs = collectedProcessMetadata.getMissingRefs();
        List<Variable> vars = collectedProcessMetadata.getVars();

        if (params != null && !params.isEmpty())
            outputModule.printParameters((HashMap<String, Parameter>) params);
        if (vars != null && !vars.isEmpty())
            outputModule.printVariables(vars);
        if (connections != null && !connections.isEmpty())
            outputModule.printConnections(connections);
        if (linkedPDIMetadata != null && !linkedPDIMetadata.isEmpty()) {
            linkedPDIMetadata.forEach(item -> outputModule.printMissingReferences(missingRefs));
        }


    }

    @Override
    public String toString() {
        return "BasePDIProcessParser{" +
                "procFileRef=" + procFileRef.getName() +
                ", name='" + collectedProcessMetadata.getName() + '\'' +
                '}';
    }

    protected void addConnectionToCollectedMetadata(Connection conn) {
        if (conn != null) {
            if (collectedProcessMetadata.getConnections() == null)
                // Lazy init connections structure
                collectedProcessMetadata.setConnections(new ArrayList<>());

            collectedProcessMetadata.getConnections().add(conn);
        } else {
            // TODO Throws exception in case connection is null
        }
    }

    protected void addVariableToCollectedMetadata(Variable var) {
        if (var != null) {
            if (collectedProcessMetadata.getVars() == null)
                // Lazy init variables structure
                collectedProcessMetadata.setVars(new ArrayList<>());

            collectedProcessMetadata.getVars().add(var);
        } else {
            // TODO Throws exception in case variable is null
        }
    }

    protected void addParameterToCollectedMetadata(String name, Parameter parameter) {

        if (name == null) {
            // TODO Throws exception in case parameter's name is null
        }

        if (parameter != null) {
            if (collectedProcessMetadata.getParams() == null)
                // Lazy init parameters structure
                collectedProcessMetadata.setParams(new HashMap<>());

            collectedProcessMetadata.getParams().put(name, parameter);
        } else {
            // TODO Throws exception in case parameter is null
        }
    }

    protected void addStepToCollectedMetadata(String name, Step step) {

        if (name == null) {
            // TODO Throws exception in case step's name is null
        }

        if (step != null) {
            if (collectedProcessMetadata.getSteps() == null)
                // Lazy init parameters structure
                collectedProcessMetadata.setSteps(new HashMap<>());

            collectedProcessMetadata.getSteps().put(name, step);
        } else {
            // TODO Throws exception in case parameter is null
        }
    }
}
