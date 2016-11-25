package it.serasoft.pdi;

import it.serasoft.pdi.parser.ParseJob;
import it.serasoft.pdi.parser.ParseTransformation;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;


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
 * Class Name   : PDITools.java
 * Package Name : it.serasoft.pdi
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 15/11/16
 * Description  :
 */
public class PDITools {


    private static final String ACCESS_FOLLOW_LINKS = "FOLLOW-LINKS";
    private static final String ACCESS_READ_DIR = "READ-DIR";

    private static final String TRUE = "true";
    private static final String FALSE = "false";


    private Logger l = LoggerFactory.getLogger(PDITools.class);

    private static final String EXT_PDI_JOB = ".kjb";
    private static final String EXT_PDI_TRANSFORMATION = ".ktr";

    public static void main(String[] args) throws Exception {

        Options opts = new Options();

        opts.addOption("report", false, "Generate a report documenting the procedures under analysis");

        opts.addOption("accessStrategy", true, "The strategy used to access the files: FOLLOW-LINKS, READ-DIR");
        opts.addOption("recurseSubdir", true, "With -accessStrategy:READ-DIR, if true we will recurse subdirectories");
        opts.addOption("filename", true, "with -accessStrategy:FOLLOW-LINKS, path the \"main\" PDI process");
        opts.addOption("outDir", true, "Path to output directory where we will write eventual output files");
        opts.addOption("srcDir", true, "Path to base directory containing the PDI processes source");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(opts, args);

        String filename = cmdLine.hasOption("filename") ? cmdLine.getOptionValue("filename") : null;
        String outDir = cmdLine.hasOption("outDir") ? cmdLine.getOptionValue("outDir") : null;
        String srcDir = cmdLine.hasOption("srcDir") ? cmdLine.getOptionValue("srcDir") : null;
        String accessStrategy = cmdLine.hasOption("accessStrategy") ? cmdLine.getOptionValue("accessStrategy") : null;
        boolean recurseSubdir = cmdLine.hasOption("recurseSubdir") ? cmdLine.getOptionValue("recurseSubdir").equals(TRUE) : false;

        if (accessStrategy.equals(ACCESS_FOLLOW_LINKS)) {
            if (filename == null) {
                // TODO Manage filename = null with accessStrategy:FOLLOW-LINKS
                System.exit(-1);
            }

            startNavigateLynks(filename);

        } else if (accessStrategy.equals(ACCESS_READ_DIR)) {
            if (srcDir == null) {
                // TODO Manage filename = null with accessStrategy:READ-DIR
                System.exit(-2);
            }
            startReadingDir(srcDir, recurseSubdir);
        }
    }

    private static void startNavigateLynks(String filename) throws Exception {

        PDITools pdiTools = new PDITools();
        File f = new File(filename);

        if (f != null) {
            startAnalysis(f, true);
        } else {
            throw new Exception();
        }
    }

    private static void startReadingDir(String srcDir, boolean recurse) {

        File f = new File(srcDir);

        if (!f.isDirectory())
            // TODO Manage directory is not a directory
            System.exit(-3);

        ArrayList<File> completeFilesList = new ArrayList<>();
        getFilesList(f, completeFilesList, recurse);

        if (!completeFilesList.isEmpty()) {
            completeFilesList.forEach(file -> startAnalysis(file, false));
        }
    }

    private static void getFilesList(File f, ArrayList<File> completeFilesList, boolean recurse) {

        File[] files = f.listFiles();
        for (File item : files) {
            if (recurse && item.isDirectory() && !item.isHidden() ) {
                getFilesList(item, completeFilesList, recurse);
            } else if (item.isFile() && !item.isHidden() &&
                    (item.getName().endsWith(EXT_PDI_JOB) ||
                    item.getName().endsWith(EXT_PDI_TRANSFORMATION))) {
                completeFilesList.add(item);
            }
        }
    }

    private static void startAnalysis(File f, boolean followSymlinks) {

        String name = f.getName();

        if (name.endsWith(EXT_PDI_JOB)) {
            ParseJob pje = new ParseJob(f, 0, followSymlinks);
            pje.parse();
        } else if (name.endsWith(EXT_PDI_TRANSFORMATION)) {
            ParseTransformation parseTransf = new ParseTransformation(f, 0, followSymlinks);
            parseTransf.parse();
        }
    }

}
