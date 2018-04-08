package serasoft.pdi.util;

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

import org.junit.Test;
import org.serasoft.pdi.parser.utils.MetadataPath;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Class Name   : PDIMetadataPathTest.java
 * Package Name : org.serasoft.pdi.util
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */

public class MetadataPathTest {

    @Test
    public void testPop() {
    }

    @Test
    public void testPush() {

    }

    @Test
    public void testDepth() {

        MetadataPath metadataPath = new MetadataPath("root");

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");

        assertNotNull(metadataPath);
        assertEquals(5, metadataPath.depth());

    }

    @Test
    public void testDepthRemove() {

        MetadataPath metadataPath = new MetadataPath("root");

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");
        metadataPath.pop();

        assertNotNull(metadataPath);
        assertEquals(4, metadataPath.depth());

    }

    @Test
    public void testPathPush() {

        MetadataPath metadataPath = new MetadataPath("root");

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");

        assertNotNull(metadataPath);
        assertEquals("/root/a/b/c/d", metadataPath.path());


    }

    @Test
    public void testPathRemove() {

        MetadataPath metadataPath = new MetadataPath("root");

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");
        metadataPath.pop();

        assertNotNull(metadataPath);
        assertEquals("/root/a/b/c", metadataPath.path());

    }

}
