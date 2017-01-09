package it.serasoft.pdi.parser;

import it.serasoft.pdi.model.Connection;
import it.serasoft.pdi.model.MissingReference;
import it.serasoft.pdi.utils.MetadataPath;
import it.serasoft.pdi.utils.OutputModule;
import it.serasoft.pdi.utils.ResolvePDIInternalVariables;
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
import java.util.List;


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
 * Class Name   : ParseJobEntries.java
 * Package Name : it.serasoft.pdi.parse
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */
public class JobParser extends it.serasoft.pdi.parser.BasePDIProcessParser {

    private Logger l = LoggerFactory.getLogger(JobParser.class);

    public JobParser(File jobFile, int depth, boolean followSymlinks, OutputModule outputModule) {
        super(jobFile, depth, followSymlinks, outputModule);
    }

    public void parse() {
        parse(null, null, null);
    }

    public void parse(String parentPDIProcName, File parentprocFileRef, String callerStepName) {

        try {
            MetadataPath metadataPath = new MetadataPath();

            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(procFileRef));
            String elementName = null;
            int eventType = 0;

            while (xmlStreamReader.hasNext()) {

                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);

                        if (elementName.equals("entries")) {
                            parseEntries(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("name") && metadataPath.path().equals("/job/name")) {
                            collectedProcessMetadata.setName(parseSimpleTextElementByName(xmlStreamReader, "name", metadataPath));
                            System.out.println("Analyzing job metadata - File: " + collectedProcessMetadata.getName()
                                    + "\n| Filename: " + procFileRef.getName()
                                    + "\n| Path: " + procFileRef.getParent()
                                    + (parentPDIProcName != null ? "\n| Caller: " + parentPDIProcName : "")
                                    + (parentprocFileRef != null ? "\n| Caller Filename: " + parentprocFileRef.getName() : "")
                                    + (callerStepName != null ? "\n| Caller Step: " + callerStepName : ""));
                        } else if (elementName.equals("description") && metadataPath.path().equals("/job/description")) {
                            collectedProcessMetadata.setDescription(parseSimpleTextElementByName(xmlStreamReader, "description", metadataPath));
                        } else if (elementName.equals("extended_description") && metadataPath.path().equals("/job/extended_description")) {
                            collectedProcessMetadata.setExtendedDescription((parseSimpleTextElementByName(xmlStreamReader, "extended_description", metadataPath)));
                        } else if (elementName.equals("parameters") && metadataPath.path().equals("/job/parameters")) {
                            parseParameters(xmlStreamReader, metadataPath);
                        } else if (elementName.equals("connection") && metadataPath.path().equals("/job/connection")) {
                            Connection conn = parseConnection(xmlStreamReader, metadataPath);

                            addConnectionToCollectedMetadata(conn);
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.pop();
                        if(elementName.equals("job")) {
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

            List<MissingReference> collectedMissingRefs = collectedProcessMetadata.getMissingRefs();

            if (collectedMissingRefs == null) {
                collectedMissingRefs = new ArrayList<>();
            }

            collectedMissingRefs.add(missingRef);
            collectedProcessMetadata.setMissingRefs(collectedMissingRefs);
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
    }

    private void parseEntries(XMLStreamReader xmlStreamReader, MetadataPath metadataPath) {

        int eventType = 0;
        boolean elementAnalyzed = false;
        String elementName = null;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("entry")) {
                            parseEntry(xmlStreamReader, metadataPath);
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

    private void parseEntry(XMLStreamReader xmlStreamReader, MetadataPath metadataPath){

        int eventType;
        boolean elementAnalyzed = false;
        String elementName;
        String entryType = null;
        String entryName = null;
        String entryDescription;
        String procFileRefname;

        try {
            while (xmlStreamReader.hasNext() && !elementAnalyzed) {
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        elementName = xmlStreamReader.getLocalName();
                        metadataPath.push(elementName);
                        if (elementName.equals("name")) {
                            entryName = readElementText(xmlStreamReader, metadataPath);
                            l.debug("Name: " + entryName);
                        } else if (elementName.equals("type")) {
                            entryType = readElementText(xmlStreamReader, metadataPath);
                            l.debug("Type: " + entryType);
                        } else if (elementName.equals("description")) {
                            entryDescription = readElementText(xmlStreamReader, metadataPath);
                            l.debug("Description: " + entryDescription);
                        } else if (elementName.equals("filename")) {
                            procFileRefname = readElementText(xmlStreamReader, metadataPath);
                            String thisProcName = collectedProcessMetadata.getName();
                            if (procFileRefname != null) {
                                procFileRefname = ResolvePDIInternalVariables.internalProcessDirectories(procFileRef.getParent(), procFileRefname);
                                l.debug("Filename: " + procFileRefname);
                                if (followSymlinks && entryType.equals("JOB")) {
                                    JobParser parseJob = new JobParser(new File(procFileRefname), depth + 1, followSymlinks, outputModule);
                                    parseJob.parse(thisProcName, procFileRef, entryName);
                                    if (linkedPDIMetadata == null) {
                                        linkedPDIMetadata = new ArrayList<>();
                                    }
                                    linkedPDIMetadata.add(parseJob);
                                } else if (followSymlinks && entryType.equals("TRANS")) {
                                    TransformationParser parseTrans = new TransformationParser(new File(procFileRefname), depth + 1, followSymlinks, outputModule);
                                    parseTrans.parse(thisProcName, procFileRef, entryName);
                                    if (linkedPDIMetadata == null) {
                                        linkedPDIMetadata = new ArrayList<>();
                                    }
                                    linkedPDIMetadata.add(parseTrans);
                                }
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


    }

}
