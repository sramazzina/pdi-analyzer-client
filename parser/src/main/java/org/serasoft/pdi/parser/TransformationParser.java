package org.serasoft.pdi.parser;

/*
 * Copyright 2016 - Sergio Ramazzina : sergio.ramazzina@serasoft.it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import org.serasoft.pdi.model.*;
import org.serasoft.pdi.utils.MetadataPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Class Name   : ParseTransformation.java
 * Package Name : org.serasoft.pdi.parser
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */

public class TransformationParser extends org.serasoft.pdi.parser.BasePDIProcessParser {

    private Logger l = LoggerFactory.getLogger(TransformationParser.class);

    public TransformationParser(File transFile, int depth, boolean followSymlinks) {
        super(transFile, depth, followSymlinks);
    }

    public ProcessMetadata parse() {
        return parse(null, null, null);
    }

    public ProcessMetadata parse(String parentPDIProcName, File parentPDIProcFile, String callerStepName) {

        ProcessMetadata processMetadata = new ProcessMetadata();

        try {
            MetadataPath metadataPath = new MetadataPath();

            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(procFileRef));
            String elementName;
            int eventType;

            // Set process type in collected informations' structure
            processMetadata.setTypeEnum(ProcessTypeEnum.TRANSFORMATION);

            while (xmlStreamReader.hasNext()) {

                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);

                        if (metadataPath.path().equals("/transformation/step")) {
                            parseStep(xmlStreamReader, metadataPath, processMetadata);
                        } else if (metadataPath.path().equals("/transformation/info/name")) {
                            processMetadata.setName(readElementText(xmlStreamReader, metadataPath));
                            System.out.println("Analyzing transformation metadata - File: " + processMetadata.getName()
                                    + "\n| Filename: " + procFileRef.getName()
                                    + "\n| Path: " + procFileRef.getParent()
                                    + (parentPDIProcName != null ? "\n| Caller: " + parentPDIProcName : "")
                                    + (parentPDIProcFile != null ? "\n| Caller Filename: " + parentPDIProcFile.getName() : "")
                                    + (callerStepName != null ? "\n| Caller Step: " + callerStepName : ""));
                        } else if (metadataPath.path().equals("/transformation/info/description")) {
                            processMetadata.setDescription(readElementText(xmlStreamReader, metadataPath));
                        } else if (metadataPath.path().equals("/transformation/info/extended_description")) {
                            processMetadata.setExtendedDescription(readElementText(xmlStreamReader, metadataPath));
                        } else if (metadataPath.path().equals("/transformation/info/unique_connections")) {
                            processMetadata.setTransactional(readElementText(xmlStreamReader, metadataPath));
                        } else if (metadataPath.path().equals("/transformation/info/parameters")) {
                            parseParameters(xmlStreamReader, metadataPath, processMetadata);
                        } else if (metadataPath.path().equals("/transformation/connection")) {
                            Connection conn = parseConnection(xmlStreamReader, metadataPath);

                            addConnectionToCollectedMetadata(conn, processMetadata);
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("transformation")) {
                            // TODO: Manage events on transformation parse finish?
                            outputObjectContent();
                        }
                        break;
                }
            }
        } catch (FileNotFoundException e1) {
            // TODO Manage missing refs for transformations. There could exists in Mapping, Transf Executor
        } catch (XMLStreamException e2) {
            l.error(e2.getLocalizedMessage());
        }

        return processMetadata;
    }

    private void parseStep(XMLStreamReader xmlStreamReader,
                           MetadataPath metadataPath,
                           ProcessMetadata processMetadata) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;
        String stepName = null;
        String pdiProcFilename;
        ProcessItem step = null;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("name")) {
                            stepName = readElementText(xmlStreamReader, metadataPath);
                            l.debug("Name: " + stepName);
                        } else if (elementName.equals("type")) {
                            String stepItemClass = readElementText(xmlStreamReader, metadataPath);
                            step = new ProcessItem(ProcessItemTypeEnum.STEP,
                                    stepItemClass,
                                    stepName);
                            if (stepItemClass.equals("SetVariable")) {
                                extractVariablesDefinition(stepName,
                                        xmlStreamReader,
                                        metadataPath,
                                        processMetadata);
                            }
                        } else if (elementName.equals("description")) {
                            assert step != null;
                            step.setDescription(readElementText(xmlStreamReader, metadataPath));
                        } else if (elementName.equals("filename")) {
                            pdiProcFilename = readElementText(xmlStreamReader, metadataPath);
                            if (pdiProcFilename != null) {
                                l.debug("Filename: " + pdiProcFilename);
                            }
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        // Each step is identified in the map's keys set by using its name
                        // addStepToCollectedMetadata(stepName, step);
                        if (elementName.equals("step"))
                            elementAnalyzed = true;
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void extractVariablesDefinition(String stepName,
                                            XMLStreamReader xmlStreamReader,
                                            MetadataPath metadataPath,
                                            ProcessMetadata processMetadata) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;
        Variable var = null;

        try {
            while (xmlStreamReader.hasNext()) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("variable_name")) {
                            var = new Variable(stepName, readElementText(xmlStreamReader, metadataPath));
                        } else if (elementName.equals("variable_type")) {
                            assert var != null;
                            var.setScope(readElementText(xmlStreamReader, metadataPath));
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("fields"))
                            elementAnalyzed = true;
                        else if (elementName.equals("field"))
                            addVariableToCollectedMetadata(var, processMetadata);
                        break;
                }

                if (elementAnalyzed) break;
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
