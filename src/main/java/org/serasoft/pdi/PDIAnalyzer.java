package org.serasoft.pdi;

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
 *
 */

import org.serasoft.pdi.parser.model.ProcessMetadata;
import org.serasoft.pdi.parser.JobParser;
import org.serasoft.pdi.parser.TransformationParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Class Name   : PDIAnalyzer.java
 * Package Name : org.serasoft.pdi
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 15/11/16
 * Description  :
 */
public class PDIAnalyzer {


    private static final String TRUE = "true";
    private static final String FALSE = "false";


    private static Logger l = LoggerFactory.getLogger(PDIAnalyzer.class);

    private static final String EXT_PDI_JOB = ".kjb";
    private static final String EXT_PDI_TRANSFORMATION = ".ktr";

    private static final String FOLLOW_NONE = "none";
    private static final String FOLLOW_DIR = "directory";
    private static final String FOLLOW_PROCLINKS = "links";

    public static void main(String[] args) throws Exception {

        Options opts = new Options();

        opts.addOption("report", false, "Generate a report documenting the procedures under analysis");

        opts.addOption("check", true, "Define a list of things to check: parameters, connection, trans_flag");
        opts.addOption("follow", true, "Values: directory, links, none");
        opts.addOption("outDir", true, "Path to output directory where we will write eventual output files");
        opts.addOption("srcDir", true, "Path to base directory containing the PDI processes source");
        opts.addOption("filename", true, "Pathname to file to be analyzed");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(opts, args);

        if (!cmdLine.hasOption("outDir")) {
            // Throw error
        }

        String outDir = cmdLine.hasOption("outDir") ? cmdLine.getOptionValue("outDir") : null;
        String follow = cmdLine.hasOption("follow") ? cmdLine.getOptionValue("follow") : FOLLOW_DIR;
        String checks = cmdLine.hasOption("check") ? cmdLine.getOptionValue("check") : null;

        // Follow links between procedures only if required and recurseSubdir = false (DEFAULT)
        ArrayList<String> checksList = null;
        if (checks != null)
             checksList = getListOfChecks(checks);

        boolean recurseDir = follow.equals(FOLLOW_DIR);
        boolean followLinks = follow.equals(FOLLOW_PROCLINKS);

        PDIAnalyzer analyzer = new PDIAnalyzer();
        if (cmdLine.hasOption("filename")) {
            // Read and process a single file
            analyzer.analyzeFile(cmdLine.getOptionValue("filename"), recurseDir, followLinks);
        } else if (cmdLine.hasOption("srcDir")) {
            // Read and process a directory of files
            analyzer.analyzeFiles(cmdLine.getOptionValue("srcDir"), recurseDir, followLinks);
        } else {
            // TODO error management
        }
    }


    public void analyzeFile(String filename, boolean recurse, boolean followLinks) {

        File f = new File(filename);

        if (f.isDirectory())
            // TODO Manage exit because is a directory
            System.exit(-4);

        if (!f.canRead())
            // TODO Manage exit because cannot be read
            System.exit(-5);

        startAnalysis(f, followLinks);

    }

    public void analyzeFiles(String srcDir, boolean recurse, boolean followLinks) {

        File f = new File(srcDir);

        if (!f.isDirectory())
            // TODO Manage exit because is not a directory
            System.exit(-3);

        ArrayList<File> completeFilesList = new ArrayList<>();
        getFilesList(f, completeFilesList, recurse);

        if (!completeFilesList.isEmpty()) {
            completeFilesList.forEach(file -> startAnalysis(file, followLinks));
        }
    }

    protected static ArrayList<String> getListOfChecks(String checks) {

        ArrayList<String> listOfChecks = null;
        StringTokenizer strTok = new StringTokenizer(checks, ",");
        while (strTok.hasMoreTokens()) {
            if (listOfChecks == null)
                listOfChecks = new ArrayList<>();
            listOfChecks.add(strTok.nextToken());
        }

        return listOfChecks;
    }

    private static void getFilesList(File f, ArrayList<File> completeFilesList, boolean recurse) {

        File[] files = f.listFiles();
        for (File item : files) {
            if (recurse && item.isDirectory() && !item.isHidden()) {
                getFilesList(item, completeFilesList, recurse);
            } else if (item.isFile() && !item.isHidden() &&
                    (item.getName().endsWith(EXT_PDI_JOB) ||
                            item.getName().endsWith(EXT_PDI_TRANSFORMATION))) {
                completeFilesList.add(item);
            }
        }
    }

    private ProcessMetadata startAnalysis(File f, boolean followLinks) {

        ProcessMetadata m = null;
        String name = f.getName();

        // TODO By default for now we output everything to console output
        if (name.endsWith(EXT_PDI_JOB)) {
            JobParser pje = new JobParser(f, 0, followLinks);
            m = pje.parse();

            if (m.getMissingRefs() != null && m.getMissingRefs().isEmpty()) {
                l.info ("We have missing refs!");
            }

        } else if (name.endsWith(EXT_PDI_TRANSFORMATION)) {
            TransformationParser parseTransf = new TransformationParser(f, 0, followLinks);
            m = parseTransf.parse();
        }

        return m;
    }

}
