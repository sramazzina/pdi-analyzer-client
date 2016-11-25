package it.serasoft.pdi.util;

import it.serasoft.pdi.utils.PDIMetadataPath;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * Class Name   : PDIMetadataPathTest.java
 * Package Name : it.serasoft.pdi.util
 * <p>
 * Created By   : Sergio Ramazzina - sergio.ramazzina@serasoft.it
 * Creation Date: 24/11/16
 * Description  :
 */

public class PDIMetadataPathTest {

    @Test
    public void testPop() {
    }

    @Test
    public void testPush() {

    }

    @Test
    public void testDepth() {

        PDIMetadataPath metadataPath = new PDIMetadataPath();

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");

        assertNotNull(metadataPath);
        assertEquals(4, metadataPath.depth());

    }

    @Test
    public void testDepthRemove() {

        PDIMetadataPath metadataPath = new PDIMetadataPath();

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");
        metadataPath.pop();

        assertNotNull(metadataPath);
        assertEquals(3, metadataPath.depth());

    }

    @Test
    public void testPath() {

        PDIMetadataPath metadataPath = new PDIMetadataPath();

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");

        assertNotNull(metadataPath);
        assertEquals("/a/b/c/d", metadataPath.path());


    }

    @Test
    public void testPathRemove() {

        PDIMetadataPath metadataPath = new PDIMetadataPath();

        metadataPath.push("a");
        metadataPath.push("b");
        metadataPath.push("c");
        metadataPath.push("d");
        metadataPath.pop();

        assertNotNull(metadataPath);
        assertEquals("/a/b/c", metadataPath.path());

    }

}
