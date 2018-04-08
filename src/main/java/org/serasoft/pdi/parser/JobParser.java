package org.serasoft.pdi.parser;

/*
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


import org.serasoft.pdi.parser.model.*;
import org.serasoft.pdi.parser.utils.MetadataPath;
import org.serasoft.pdi.parser.utils.ResolvePDIInternalVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Name   : ParseJobEntries.java
 * Package Name : org.serasoft.pdi.parse
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */
public class JobParser extends org.serasoft.pdi.parser.BasePDIProcessParser {

    private Logger l = LoggerFactory.getLogger(JobParser.class);

    public JobParser(File jobFile, int depth, boolean followSymlinks) {
        super(jobFile, depth, followSymlinks);
    }

    public ProcessMetadata parse() {
        return parse(null, null, null);
    }

    public ProcessMetadata parse(String parentPDIProcName, File parentprocFileRef, String callerStepName) {

        Map<String, ProcessItem> items = new HashMap<>();
        ProcessMetadata processMetadata = new ProcessMetadata();

        try {
            MetadataPath metadataPath = new MetadataPath();

            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(procFileRef));
            String elementName;
            int eventType;

            // Set process type in collected information' structure
            processMetadata.setTypeEnum(ProcessTypeEnum.JOB);

            while (xmlStreamReader.hasNext()) {

                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);

                        if (metadataPath.path().equals("/job/entries")) {
                            parseEntries(xmlStreamReader, metadataPath, processMetadata);
                        } else if (metadataPath.path().equals("/job/hops")) {
                            parseHops(xmlStreamReader, metadataPath, processMetadata);
                        } else if (metadataPath.path().equals("/job/name")) {
                            processMetadata.setName(readElementText(xmlStreamReader, metadataPath));
                            System.out.println("Analyzing job metadata - File: " + processMetadata.getName()
                                    + "\n| Filename: " + procFileRef.getName()
                                    + "\n| Path: " + procFileRef.getParent()
                                    + (parentPDIProcName != null ? "\n| Caller: " + parentPDIProcName : "")
                                    + (parentprocFileRef != null ? "\n| Caller Filename: " + parentprocFileRef.getName() : "")
                                    + (callerStepName != null ? "\n| Caller Step: " + callerStepName : ""));
                        } else if (metadataPath.path().equals("/job/description")) {
                            processMetadata.setDescription(readElementText(xmlStreamReader, metadataPath));
                        } else if (metadataPath.path().equals("/job/extended_description")) {
                            processMetadata.setExtendedDescription(readElementText(xmlStreamReader, metadataPath));
                        } else if (metadataPath.path().equals("/job/parameters")) {
                            parseParameters(xmlStreamReader, metadataPath, processMetadata);
                        } else if (metadataPath.path().equals("/job/connection")) {
                            Connection conn = parseConnection(xmlStreamReader, metadataPath);
                            addConnectionToCollectedMetadata(conn, processMetadata);
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("job")) {
                            // TODO: Manage events on job parse finish?
                            outputObjectContent();
                        }
                        break;
                }
            }

        } catch (FileNotFoundException e1) {

            MissingReference missingRef = new MissingReference(callerStepName,
                    parentPDIProcName,
                    parentprocFileRef.getAbsolutePath());
            missingRef.setType("JobFileRef");
            missingRef.setRefValue(procFileRef.getAbsoluteFile().getName());

            List<MissingReference> collectedMissingRefs = processMetadata.getMissingRefs();

            if (collectedMissingRefs == null) {
                collectedMissingRefs = new ArrayList<>();
            }

            collectedMissingRefs.add(missingRef);
            processMetadata.setMissingRefs(collectedMissingRefs);
/*
            l.error("| WARNING - File " + procFileRef + " was not found as expected by PDI analyzer. Please check!"
                    + (parentPDIProcName != null ? "\n| | Caller: " + parentPDIProcName : "")
                    + (parentprocFileRef != null ? "\n| | Caller Filename: " + parentprocFileRef.getName() : "")
                    + (callerStepName != null ? "\n| | Caller Step: " + callerStepName : ""));
*/
        } catch (XMLStreamException e2) {
            l.error(e2.getLocalizedMessage());
        } catch (Exception e) {
            l.error(e.getMessage());
        }

        return processMetadata;
    }

    private void parseHops(XMLStreamReader xmlStreamReader,
                           MetadataPath metadataPath,
                           ProcessMetadata processMetadata) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("hop")) {
                            ProcessHop h = parseHop(xmlStreamReader, metadataPath);
                            if (processMetadata.getHops() == null)
                                processMetadata.setHops(new ArrayList<>());

                            processMetadata.getHops().add(h);
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("hops"))
                            elementAnalyzed = true;
                        break;
                }

            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    private ProcessHop parseHop(XMLStreamReader xmlStreamReader,
                                MetadataPath metadataPath) {

        int eventType;
        boolean elementAnalyzed = false;
        String from = null, to = null;
        ProcessHop hop = null;
        String elementName;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);

                        if (elementName.equals("from")) {
                            from = readElementText(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("to")) {
                            to = readElementText(xmlStreamReader, metadataPath);
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("hop")) {
                            // ProcessHop hop = new ProcessHop()
                            elementAnalyzed = true;
                            if (from != null && to != null)
                                hop = new ProcessHop(from, to);
                        }
                        break;
                }

            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return hop;
    }

    private void parseEntries(XMLStreamReader xmlStreamReader, MetadataPath metadataPath, ProcessMetadata processMetadata) {

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("entry")) {
                            ProcessItem item = parseEntry(xmlStreamReader, metadataPath, processMetadata.getName());
                            if (processMetadata.getItems() == null)
                                processMetadata.setItems(new HashMap<>());
                            processMetadata.getItems().put(item.getName(), item);
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if (elementName.equals("entries"))
                            elementAnalyzed = true;
                        break;
                }

            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    private ProcessItem parseEntry(XMLStreamReader xmlStreamReader,
                                   MetadataPath metadataPath,
                                   String procName) {

        boolean elementAnalyzed = false;
        String elementName;
        String procFileRefname;
        String itemClass = null;
        String entryName = null;
        String entryDescription = null;
        ProcessItem item = null;
        int eventType;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("name")) {
                            entryName = readElementText(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("type")) {
                            itemClass = readElementText(xmlStreamReader, metadataPath);
                            item = new ProcessItem(ProcessItemTypeEnum.TASK,
                                    itemClass,
                                    entryName);
                            item.setDescription(entryDescription);
                        } else if (elementName.equals("description")) {
                            entryDescription = readElementText(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("filename")) {
                            procFileRefname = readElementText(xmlStreamReader, metadataPath);

                            if (procFileRefname != null) {

                                procFileRefname = ResolvePDIInternalVariables.resolve(procFileRefname, procFileRef.getParent());
                                l.debug("Filename: " + procFileRefname);
                                ProcessMetadata pm = null;

                                if (followSymlinks && itemClass.equals("JOB")) {
                                    JobParser parseJob = new JobParser(new File(procFileRefname), depth + 1, followSymlinks);
                                    pm = parseJob.parse(procName, procFileRef, entryName);
                                } else if (followSymlinks && itemClass.equals("TRANS")) {
                                    TransformationParser parseTrans = new TransformationParser(new File(procFileRefname), depth + 1, followSymlinks);
                                    pm = parseTrans.parse(procName, procFileRef, entryName);
                                }
                                item.setLinkedProcess(pm);
                            }
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        metadataPath.pop();
                        elementName = xmlStreamReader.getLocalName();
                        if (elementName.equals("entry"))
                            elementAnalyzed = true;
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return item;
    }

}
