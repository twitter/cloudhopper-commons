
package com.cloudhopper.commons.xbean;

// third party imports
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports

public class XPathTest {

    private static final Logger logger = Logger.getLogger(XPathTest.class);

    @Test(expected=IllegalArgumentException.class)
    public void matchesNullParam() throws Exception {
        XPath xp = XPath.parse("/nodeA/nodeB");
        Assert.assertEquals(false, xp.matches(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void matchesEmptyParam() throws Exception {
        XPath xp = XPath.parse("/nodeA/nodeB");
        Assert.assertEquals(false, xp.matches(""));
    }

    @Test
    public void matches1() throws Exception {
        XPath xp = XPath.parse("/nodeA/nodeB");
        Assert.assertEquals(true, xp.matches("/nodeA"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB"));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeC"));
        Assert.assertEquals(false, xp.matches("/nodeB"));
        Assert.assertEquals(false, xp.matches("/nodeB/nodeB"));
        Assert.assertEquals(false, xp.matches("/no"));
    }

    @Test
    public void matches2() throws Exception {
        XPath xp = XPath.parse("/nodeA");
        Assert.assertEquals(true, xp.matches("/nodeA"));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeB"));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeC"));
        Assert.assertEquals(false, xp.matches("/nodeB"));
        Assert.assertEquals(false, xp.matches("/nodeB/nodeB"));
        Assert.assertEquals(false, xp.matches("/no"));
    }

    @Test
    public void matches3() throws Exception {
        XPath xp = XPath.parse("/nodeA/*");
        Assert.assertEquals(true, xp.matches("/nodeA"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB/nodeC"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB/nodeC/nodeD"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeC"));
        Assert.assertEquals(false, xp.matches("/nodeB"));
        Assert.assertEquals(false, xp.matches("/nodeB/nodeB"));
        Assert.assertEquals(false, xp.matches("/no"));
    }

    @Test
    public void matches4() throws Exception {
        XPath xp = XPath.parse("/nodeA/nodeB/*");
        Assert.assertEquals(true, xp.matches("/nodeA"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB"));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB/nodeC"));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeC"));
        Assert.assertEquals(false, xp.matches("/nodeB"));
        Assert.assertEquals(false, xp.matches("/nodeB/nodeB"));
        Assert.assertEquals(false, xp.matches("/no"));
    }

    @Test
    public void matchesNotIncludeParent1() throws Exception {
        XPath xp = XPath.parse("/nodeA/nodeB");
        Assert.assertEquals(false, xp.matches("/nodeA", false));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB", false));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeB/nodeC", false));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeC", false));
        Assert.assertEquals(false, xp.matches("/nodeB", false));
        Assert.assertEquals(false, xp.matches("/nodeB/nodeB", false));
        Assert.assertEquals(false, xp.matches("/no", false));
    }

    @Test
    public void matchesNotIncludeParent2() throws Exception {
        XPath xp = XPath.parse("/nodeA/nodeB/*");
        Assert.assertEquals(false, xp.matches("/nodeA", false));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB", false));
        Assert.assertEquals(true, xp.matches("/nodeA/nodeB/nodeC", false));
        Assert.assertEquals(false, xp.matches("/nodeA/nodeC", false));
        Assert.assertEquals(false, xp.matches("/nodeB", false));
        Assert.assertEquals(false, xp.matches("/nodeB/nodeB", false));
        Assert.assertEquals(false, xp.matches("/no", false));
    }

    
}
