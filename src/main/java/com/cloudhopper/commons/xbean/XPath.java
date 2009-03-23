
package com.cloudhopper.commons.xbean;

/**
 * Represents an XPath. Parses a subset of valid XPath queries and exposes
 * them in getter and setter properties.
 * 
 * @author joelauer
 */
public class XPath {
    private String fullXPath;
    private String normalizedXPath;
    private boolean includeChildren;

    private XPath() {
        fullXPath = null;
        this.normalizedXPath = null;
        includeChildren = false;
    }

    /**
     * Returns the normalized XPath.  For example, "/nodeA/*" will return "/nodeA"
     */
    public String getNormalizedXPath() {
        return this.normalizedXPath;
    }

    /**
     * Returns the full XPath (not normalized).  For example, "/nodeA/*" will return "/nodeA/*"
     */
    public String getFullXPath() {
        return this.fullXPath;
    }

    /**
     * Returns whether this XPath include child nodes.
     * @return
     */
    public boolean includeChildren() {
        return this.includeChildren;
    }

    /**
     * Does the path match this XPath?
     * @param xpath A path such as "/nodeA/nodeB"
     * @return True if the xpath matches, false otherwise.
     */
    public boolean matches(String xpath) {
        return matches(xpath, true);
    }

    /**
     * Does the path match?  If includeParent is true, then a parent match
     * is included.  For example, an XPath of "/nodeA/nodeB" will include a match
     * for "/nodeA" if includeParent is true.  Otherwise, "/nodeA" will return
     * false.
     *
     * @param xpath A path such as "/nodeA/nodeB"
     * @return True if the xpath matches, false otherwise.
     */
    public boolean matches(String xpath, boolean includeParent) {
        if (xpath == null || xpath.equals("")) {
            throw new IllegalArgumentException("xpath cannot be null or empty");
        }

        // make sure xpath doesn't end with an /
        if (xpath.endsWith("/")) {
            throw new IllegalArgumentException("Should not end with /");
        }

        // the xpath might be a parent path
        if (includeParent && normalizedXPath.startsWith(xpath+"/")) {
            return true;
        }

        // otherwise, either this is an exact match or a child of a previous match
        if (xpath.startsWith(normalizedXPath)) {
            // if lengths are equal, then its a perfect match
            if (xpath.length() == normalizedXPath.length()) {
                return true;
            } else if (includeChildren) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Parses an XPath and returns its representation as an object.  Only a subset
     * of XPath queries are supported.  Valid queries are:<br><br>
     * /<br>
     * /*<br>
     * /nodeA<br>
     * /nodeA/*<br>
     *
     * @param xpath
     * @return
     * @throws java.lang.IllegalArgumentException
     */
    public static XPath parse(String xpath) throws IllegalArgumentException {
        XPath xp = new XPath();

        // save the xpath as the full xpath
        xp.fullXPath = xpath;
        
        // does the xpath end with /*
        if (xpath.length() >= 2 && xpath.endsWith("/*")) {
            xp.includeChildren = true;
            xpath = xpath.substring(0, xpath.length()-2);
        }

        // normalized path is the rest
        xp.normalizedXPath = xpath;

        return xp;
    }
    
}
