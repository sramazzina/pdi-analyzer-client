package it.serasoft.pdi.util;

import it.serasoft.pdi.utils.MetadataPath;
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
