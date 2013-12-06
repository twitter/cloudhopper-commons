package com.cloudhopper.commons.xml;

/*
 * #%L
 * ch-commons-xbean
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

// third party imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an XPath. Parses a subset of valid XPath queries and exposes
 * them in getter and setter properties.
 * 
 * @author joelauer
 */
public class XPath {
    private static Logger logger = LoggerFactory.getLogger(XPath.class);

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
     * Helper method for compiling xpath and selecting a node in one method call.
     * If the same xpath will be reused, it's more efficient to create an instance
     * than to use this method.
     * <br><br>
     * Selects the first node starting from the rootNode that matches this xpath.
     * Useful for selecting a subtree.  For example, if the xpath is "/nodeA/nodeB",
     * and that path exists starting from rootNode, then "nodeB" would be returned.
     * @param rootNode The node to start from
     * @param xpath The xpath to select
     * @return The selected node that matches the xpath
     */
    public static XmlParser.Node select(XmlParser.Node rootNode, String xpath) {
        XPath xp = XPath.parse(xpath);
        return xp.select(rootNode);
    }
   

    /**
     * Selects the first node starting from the rootNode that matches this xpath.
     * Useful for selecting a subtree.  For example, if the xpath is "/nodeA/nodeB",
     * and that path exists starting from rootNode, then "nodeB" would be returned.
     * @param rootNode The node to start from
     * @return The selected node that matches the xpath
     */
    public XmlParser.Node select(XmlParser.Node rootNode) {
        // check that the first char is /
        if (normalizedXPath.charAt(0) != '/') {
            throw new IllegalArgumentException("First part of xpath must be /");
        }
        
        // check that the path length is > 2
        if (normalizedXPath.length() <= 1) {
            throw new IllegalArgumentException("Xpath length must be > 1");
        }

        // we'll always start at the root
        XmlParser.Node node = rootNode;

        // navigate thru each part of the "path"
        int startPos = 1;
        boolean selectedRootNode = false;
        do {
            // find the next occurrence of the / char
            int nextPos = normalizedXPath.indexOf('/', startPos+1);

            // if not found then this is the last tag
            if (nextPos < 0) {
                // set the nextPos to the end of this string
                nextPos = normalizedXPath.length();
            }

            String tag = normalizedXPath.substring(startPos, nextPos);

            logger.debug("tag: " + tag);

            // unique case: we may currently be at the root node, in which
            // case we want to skip it....
            if (!selectedRootNode) {
                // the "tag" MUST equal the root node's tag
                if (!node.getTag().equals(tag)) {
                    // xpath didn't select the root node correctly
                    return null;
                } else {
                    // xpath selected root node correctly, we basically will
                    // skip incrementing the node
                    selectedRootNode = true;
                }
            } else {
                // try to find this tag
                node = node.getChild(tag);
                if (node == null) {
                    // return null since this path doesn't exist
                    return null;
                }
            }

            // increment startPos
            startPos = nextPos+1;
        } while (startPos < normalizedXPath.length());

        // if we get here, then return the node
        return node;
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
