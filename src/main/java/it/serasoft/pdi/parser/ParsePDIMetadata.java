package it.serasoft.pdi.parser;

import it.serasoft.pdi.model.PDIProcessConnection;
import it.serasoft.pdi.model.PDIProcessFlowItem;
import it.serasoft.pdi.model.PDIProcessParameterHolder;
import it.serasoft.pdi.utils.ConsoleOutputUtil;
import it.serasoft.pdi.utils.PDIMetadataPath;
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
public abstract class ParsePDIMetadata {

    private Logger l = LoggerFactory.getLogger(ParseJob.class);
    protected File procFileRef;
    protected int depth;
    protected boolean followSymlinks;

    protected String name;
    protected String desc;
    protected String extDesc;

    protected List<PDIProcessConnection> connections;
    protected Map<String, PDIProcessParameterHolder> params;
    protected Map<String, PDIProcessFlowItem> steps;
    protected List<ParsePDIMetadata> linkedPDIMetadata;

    public ParsePDIMetadata(File procFileRef, int depth, boolean followSymlinks) {

        this.procFileRef = procFileRef;
        this.depth = depth;
        this.followSymlinks = followSymlinks;
        init();
    }

    protected void init() {

        connections = new ArrayList<>();
        params = new HashMap<>();
        steps = new HashMap<>();
    }

    public abstract void parse();

    public abstract void parse(String parentPDIProcName,
                               File parentPDIProcFile,
                               String callerStepName);

    public List<PDIProcessConnection> getConnections() {
        return connections;
    }

    public Map<String, PDIProcessParameterHolder> getParams() {
        return params;
    }

    public Map<String, PDIProcessFlowItem> getSteps() {
        return steps;
    }

    protected void parseParameters(XMLStreamReader xmlStreamReader, PDIMetadataPath metadataPath) {

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
                                  PDIMetadataPath metadataPath) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;
        String paramName = null;

        PDIProcessParameterHolder parameterHolder = new PDIProcessParameterHolder();
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
                            paramName = readElementText(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("description")) {
                            parameterHolder.setDescription(readElementText(xmlStreamReader, metadataPath));
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        params.put(paramName, parameterHolder);
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


    protected PDIProcessConnection parseConnection(XMLStreamReader xmlStreamReader, PDIMetadataPath metadataPath) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;
        String elementValue = null;
        PDIProcessConnection retConn = null;

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
                                retConn = new PDIProcessConnection(elementValue, procFileRef);
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
                                           PDIMetadataPath metadataPath,
                                           PDIProcessConnection valuesMap) {

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
                                            PDIMetadataPath metadataPath,
                                            PDIProcessConnection valuesMap) {

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
                                                  PDIMetadataPath metadataPath) {

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

    protected String readElementText(XMLStreamReader xmlStreamReader, PDIMetadataPath metadataPath) {

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

    public void printReport() {

        if (params != null && !params.isEmpty())
            ConsoleOutputUtil.printParameters((HashMap<String, PDIProcessParameterHolder>) params);
        if (connections != null && !connections.isEmpty())
            ConsoleOutputUtil.printConnections(connections);


    }
}
