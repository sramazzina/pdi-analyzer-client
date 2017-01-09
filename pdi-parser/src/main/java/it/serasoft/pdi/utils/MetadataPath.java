package it.serasoft.pdi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

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
 * Class Name   : PDIMetadataElementDepthUtil.java
 * Package Name : it.serasoft.pdi.utils
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */
public class MetadataPath {

    private LinkedList<String> queue;
    private static Logger l = LoggerFactory.getLogger(MetadataPath.class);


    public MetadataPath() {
        init(null);
    }

    public MetadataPath(String rootElement) {
        init(rootElement);
    }

    protected void init(String rootElement) {
        queue = new LinkedList<>();
        if (rootElement != null)
            push(rootElement);
    }

    public void push(String value) {
        queue.addLast(value);
        // l.debug("Depth: " + depth() + " - Path: " + path());
    }

    public void pop() {
        queue.removeLast();
    }

    public int depth() {
        return queue.size();
    }

    public String path() {

        return "/" + String.join("/", queue);
    }

    @Override
    public String toString() {
        return "PDIMetadataPath{" +
                "queue=" + queue.toString() +
                '}';
    }
}
