package it.serasoft.pdi.parser;

import it.serasoft.pdi.model.PDIProcessConnection;
import it.serasoft.pdi.model.PDIProcessParameterHolder;
import it.serasoft.pdi.model.PDIProcessFlowItem;
import it.serasoft.pdi.utils.ConsoleOutputUtil;
import it.serasoft.pdi.utils.PDIMetadataPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
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
 */

/**
 * Class Name   : ParseTransformation.java
 * Package Name : it.serasoft.pdi.parser
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */

public class ParseTransformation extends ParsePDIMetadata {

    private Logger l = LoggerFactory.getLogger(ParseTransformation.class);

    public ParseTransformation(File transFile, int depth, boolean followSymlinks) {
        super(transFile, depth, followSymlinks);
    }

    public void parse() {
        parse(null, null, null);
    }

    public void parse(String parentPDIProcName, File parentPDIProcFile, String callerStepName) {

        try {
            PDIMetadataPath metadataPath = new PDIMetadataPath();

            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(procFileRef));
            String prevElementName = "";
            String elementName = null;
            int eventType = 0;

            while (xmlStreamReader.hasNext()) {

                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        metadataPath.push(elementName);
                        elementName = xmlStreamReader.getLocalName();

                        if (elementName.equals("step")) {
                            parseStep(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("name") && prevElementName.equals("info")) {
                            name = parseSimpleTextElementByName(xmlStreamReader, "name", metadataPath);
                            System.out.println("Analyzing transformation metadata - File: " + name
                                    + "\n| Filename: " + procFileRef.getName()
                                    + "\n| Path: " + procFileRef.getParent()
                                    + (parentPDIProcName != null ? "\n| Caller: " + parentPDIProcName : "")
                                    + (parentPDIProcFile != null ? "\n| Caller Filename: " + parentPDIProcFile.getName() : "")
                                    + (callerStepName != null ? "\n| Caller Step: " + callerStepName : ""));
                        } else if (elementName.equals("description") && metadataPath.path().equals("/job/description")) {
                            desc = parseSimpleTextElementByName(xmlStreamReader, "description", metadataPath);
                        } else if (elementName.equals("extended_description") && metadataPath.path().equals("/job/extended_description")) {
                            extDesc = parseSimpleTextElementByName(xmlStreamReader, "extended_description", metadataPath);
                        } else if (elementName.equals("parameters") && metadataPath.path().equals("/transformation/parameters")) {
                            parseParameters(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("connection") && metadataPath.path().equals("/transformation/connection")) {
                            PDIProcessConnection conn = parseConnection(xmlStreamReader, metadataPath);

                            if (conn != null) {
                                connections.add(conn);
                            }
                        }
                        prevElementName = elementName;
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if(elementName.equals("transformation")) {
                            printReport();
                        }
                        break;
                }
            }
        } catch (FileNotFoundException e1) {

        } catch (XMLStreamException e2) {
            l.error(e2.getLocalizedMessage());
        }
    }

    private void parseStep(XMLStreamReader xmlStreamReader, PDIMetadataPath metadataPath){

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;
        String name = null;
        String pdiProcFilename = null;
        PDIProcessFlowItem step = null;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("name")) {
                            name = readElementText(xmlStreamReader, metadataPath);
                            l.debug("Name: " + name);
                        } else if (elementName.equals("type")) {
                            step = new PDIProcessFlowItem(name, readElementText(xmlStreamReader, metadataPath));
                            l.debug("Type: " + step.getType());
                        } else if (elementName.equals("description")) {
                            step.setDescription(readElementText(xmlStreamReader, metadataPath));
                            l.debug("Description: " + step.getName());
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
                        steps.put(name, step);
                        if (elementName.equals("step"))
                            elementAnalyzed = true;
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }


    }

}
