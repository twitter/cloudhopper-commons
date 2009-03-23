
package com.cloudhopper.commons.xbean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.StringTokenizer;

import java.util.TreeMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import org.mortbay.logger.Log;
//import org.mortbay.util.LazyList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.log4j.Logger;

public class XmlParser {
    
    private static final Logger logger = Logger.getLogger(XmlParser.class);

    //private Map _redirectMap = new HashMap();
    private SAXParser _parser;
    private boolean trimText;
    private ArrayList<XPath> includeXPaths;
    private ArrayList<XPath> excludeXPaths;

    //private Map _observerMap;
    //private Stack _observers = new Stack();
    //private String _xpath;
    //private Object _xpaths;
    //private String _dtd;

    public XmlParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            _parser = factory.newSAXParser();

            //_parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", validating);
            _parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            _parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            

        } catch (Exception e) {
            logger.warn(e);
            throw new Error(e.toString());
        }

        // by default, trim "text" values of whitespace to leave them empty
        trimText = true;
        // by default empty include and exclude xpaths
        includeXPaths = new ArrayList<XPath>();
        excludeXPaths = new ArrayList<XPath>();

        //boolean validating_dft = factory.getClass().toString().startsWith("org.apache.xerces.");
        //String validating_prop = System.getProperty("org.mortbay.xml.XmlParser.Validating", validating_dft ? "true" : "false");
        //boolean validating = Boolean.valueOf(validating_prop).booleanValue();
        //setValidating(validating);
    }

    public void setTrimText(boolean value) {
        this.trimText = value;
    }

    public boolean getTrimText() {
        return this.trimText;
    }

    /**
    public XmlParser(boolean validating) {
        setValidating(validating);
    }
     */

    /* ------------------------------------------------------------ */
    /**
    public void setValidating(boolean validating)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);
            _parser = factory.newSAXParser();

            try
            {
                if (validating)
                    _parser.getXMLReader().setFeature("http://apache.org/xml/features/validation/schema", validating);
            }
            catch (Exception e)
            {
                if (validating)
                    logger.warn("Schema validation may not be supported: ", e);
                //else
                    //logger.ignore(e);
            }

            _parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", validating);
            _parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            _parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        }
        catch (Exception e)
        {
            logger.warn(e);
            throw new Error(e.toString());
        }
    }
     */

    /* ------------------------------------------------------------ */
    /**
     * @param name
     * @param entity
     */
    /**
    public synchronized void redirectEntity(String name, URL entity)
    {
        if (entity != null)
            _redirectMap.put(name, entity);
    }
     */

    
    /**
     * Adds an include XPath to filter nodes that are returned while parsing
     * the XML document. For example, adding "/configuration/testA" means that
     * only nodes "configuration", "testA", and any of its children will be
     * returned as nodes.  All other nodes will essentially be "hidden" from
     * the returned DOM tree.
     * @param xpath The XPath filter to include during parsing such as "/configuration/testA"
     */
    public void addIncludeXPath(String xpath) {
        // FIXME: is this a valid xpath?
        // FIXME: is this xpath already added?
        includeXPaths.add(XPath.parse(xpath));
    }

    /**
     * Adds an exclude XPath to filter nodes that are returned while parsing
     * the XML document. For example, adding "/configuration/testA" means that
     * node "/configuration/testA" and any of its children will be excluded
     * as nodes returned while parsing.  All other nodes will be returned, but
     * this one will essentially be "hidden".
     * @param xpath The XPath filter to exclude during parsing such as "/configuration/testA"
     */
    public void addExcludeXPath(String xpath) {
        // FIXME: is this a valid xpath?
        // FIXME: is this xpath already added?
        excludeXPaths.add(XPath.parse(xpath));
    }

    /* ------------------------------------------------------------ */
    /**
    public String getDTD()
    {
        return _dtd;
    }
     */

    /* ------------------------------------------------------------ */
    /**
     * Add a ContentHandler. Add an additional _content handler that is triggered on a tag name. SAX
     * events are passed to the ContentHandler provided from a matching start element to the
     * corresponding end element. Only a single _content handler can be registered against each tag.
     *
     * @param trigger Tag local or q name.
     * @param observer SAX ContentHandler
     */
    /**
    public synchronized void addContentHandler(String trigger, ContentHandler observer)
    {
        if (_observerMap == null)
            _observerMap = new HashMap();
        _observerMap.put(trigger, observer);
    }
     */

    /* ------------------------------------------------------------ */
    public synchronized Node parse(InputSource source) throws IOException, SAXException
    {
//        _dtd=null;
        Handler handler = new Handler();
        XMLReader reader = _parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        if (logger.isDebugEnabled())
            logger.debug("parsing: sid=" + source.getSystemId() + ",pid=" + source.getPublicId());
        _parser.parse(source, handler);
        if (handler.error != null)
            throw handler.error;
        Node root = (Node)handler.root;
        handler.reset();
        return root;
    }

    /**
     * Parse XML from a String.
     */
    public synchronized Node parse(String xml) throws IOException, SAXException {
        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
        return parse(is);
    }

    /**
     * Parse XML from File.
     */
    public synchronized Node parse(File file) throws IOException, SAXException {
        return parse(new InputSource(file.toURI().toURL().toString()));
    }

    /**
     * Parse XML from InputStream.
     */
    public synchronized Node parse(InputStream in) throws IOException, SAXException {
        //_dtd=null;
        Handler handler = new Handler();
        XMLReader reader = _parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        _parser.parse(new InputSource(in), handler);
        if (handler.error != null)
            throw handler.error;
        Node root = (Node)handler.root;
        handler.reset();
        return root;
    }

    private class Handler extends DefaultHandler {
        
        Node root = null;
        SAXParseException error;
        private Node context;
        private int depth;
        private boolean noop;
        private int noopDepth;

        Handler() {
            reset();
        }

        void reset() {
            root = null;
            error = null;
            context = null;
            depth = -1;
            noop = false;
            noopDepth = -1;
        }

        @Override
        public void processingInstruction( String target, String value ) {
            //logger.debug("processing instr!");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            // figure out tag
            String tag = (uri == null || uri.equals("")) ? qName : localName;

            // debug
            //logger.trace("startElement: tag=" + tag);

            // always increment our depth
            depth++;

            //
            // if NOOP, then skip
            //
            if (noop) {
                //logger.trace("in noop mode, skipping this element");
                return;
            }

            // create the current node we're parsing
            Node node = new Node(tag, attrs);

            // always set the new node's parent to the current context
            // if this happens to be the root node, then it'll be null
            node.setParent(context);

            // debug
            //logger.trace("node: " + tag + ", path: " + node.getPath());

            //
            // is this node in our excludeXPath?
            //
            if (excludeXPaths != null && excludeXPaths.size() > 0) {
                String path = node.getPath();
                boolean match = false;
                for (int i = 0; !match && i < excludeXPaths.size(); i++) {
                    XPath xpath = excludeXPaths.get(i);
                    match = xpath.matches(path, false);

                }
                // if there was a match, then we want to exclude this node and any children nodes
                if (match) {
                    //logger.debug("turning on noop mode (due to exclusion match)");
                    noop = true;
                    noopDepth = depth;
                    return;
                }
            }

            //
            // is this node in our includeXPath list?
            //
            if (includeXPaths != null && includeXPaths.size() > 0) {
                String path = node.getPath();
                boolean match = false;
                for (int i = 0; !match && i < includeXPaths.size(); i++) {
                    XPath xpath = includeXPaths.get(i);
                    match = xpath.matches(path);

                }
                // if no match, then we do NOT want to include this node
                if (!match) {
                    //logger.debug("turning on noop mode (due to NO inclusion match)");
                    noop = true;
                    noopDepth = depth;
                    return;
                }
            }

            // is this the first node (root)?
            if (depth == 0) {
                root = node;
            } else {
                // add this node as a child to the current context
                context.addChild(node);    
            }
      
            // we're done, so set the current context to the current node
            context = node;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            // always decrement our depth
            depth--;

            //
            // check if we should turn off noop
            //
            if (noop) {
                if (depth < noopDepth) {
                    //logger.trace("turning off noop mode");
                    noop = false;
                } else {
                    //logger.trace("skipping end of element since in noop");
                }
                return;
            }

            // reset the context to this context's parent
            context = context.getParent();
        }

        @Override
        public void ignorableWhitespace(char buf[], int offset, int len) throws SAXException {
            // do nothing
            //logger.debug("ignorable whitespace included!");
        }

        @Override
        public void characters(char buf[], int offset, int len) throws SAXException {
            //
            // if NOOP, then skip
            //
            if (noop) {
                //logger.trace("in noop mode, skipping characters");
                return;
            }

            if (buf == null || buf.length <= 0) {
                // do nothing
                return;
            }
            // convert to string
            String text = new String(buf, offset, len);
            // check if "trim" feature is turned on
            if (trimText) {
                text = text.trim();
            }
            // now, only set a text value if its not empty
            if (text != null && !text.isEmpty()) {
                context.setText(text);
            }
        }

        @Override
        public void warning(SAXParseException ex) {
            logger.warn(ex);
            logger.warn("WARNING @ " + getLocationString(ex) + " : " + ex.toString());
        }

        @Override
        public void error(SAXParseException ex) throws SAXException {
            // Save error and continue to report other errors
            if (error == null)
                error = ex;
            //logger.debug(ex);
            logger.error("ERROR @ " + getLocationString(ex) + " : " + ex.toString());
        }

        @Override
        public void fatalError(SAXParseException ex) throws SAXException {
            error = ex;
            //logger.debug(ex);
            logger.error("FATAL @ " + getLocationString(ex) + " : " + ex.toString());
            throw ex;
        }

        private String getLocationString(SAXParseException ex) {
            return ex.getSystemId() + " line:" + ex.getLineNumber() + " col:" + ex.getColumnNumber();
        }

        @Override
        public InputSource resolveEntity(String pid, String sid) {
            //logger.debug("resolveEntity(" + pid + ", " + sid + ")");
            /**
            if (logger.isDebugEnabled())
                logger.debug("resolveEntity(" + pid + ", " + sid + ")");

            if (sid!=null && sid.endsWith(".dtd"))
                _dtd=sid;

            URL entity = null;
            if (pid != null)
                entity = (URL) _redirectMap.get(pid);
            if (entity == null)
                entity = (URL) _redirectMap.get(sid);
            if (entity == null)
            {
                String dtd = sid;
                if (dtd.lastIndexOf('/') >= 0)
                    dtd = dtd.substring(dtd.lastIndexOf('/') + 1);

                if (logger.isDebugEnabled())
                    logger.debug("Can't exact match entity in redirect map, trying " + dtd);
                entity = (URL) _redirectMap.get(dtd);
            }

            if (entity != null)
            {
                try
                {
                    InputStream in = entity.openStream();
                    if (logger.isDebugEnabled())
                        logger.debug("Redirected entity " + sid + " --> " + entity);
                    InputSource is = new InputSource(in);
                    is.setSystemId(sid);
                    return is;
                }
                catch (IOException e)
                {
                    logger.warn(e);
                }
            }
            
             */
            return null;
        }
    }

    /**
     * XML Attribute.
     */
    public static class Attribute {

        private String _name;
        private String _value;

        Attribute(String n, String v) {
            _name = n;
            _value = v;
        }

        public String getName() {
            return _name;
        }

        public String getValue() {
            return _value;
        }
    }

    /**
     * XML Node. Represents an XML element with optional attributes and ordered content.
     */
    public static class Node {
        
        private String tag;
        private Attribute[] attrs;
        private String text;
        
        Node parent;
        private ArrayList<Node> children;
        
        private String path;

        //private boolean _lastString = false;

        Node (String tag, Attributes attrs) {
            this.tag = tag;
            if (attrs == null) {
                // make it a zero array
                this.attrs = new Attribute[0];
            } else {
                this.attrs = new Attribute[attrs.getLength()];
                for (int i = 0; i < attrs.getLength(); i++) {
                    String name = attrs.getLocalName(i);
                    if (name == null || name.equals(""))
                        name = attrs.getQName(i);
                    this.attrs[i] = new Attribute(name, attrs.getValue(i));
                }
            }
        }

        /**
         * Tests if this node contains a "text" value.  If the XmlParser's "trimText"
         * feature is turned on, then whitespace characters will be trimmed and
         * this text value will be null (not set).
         * @return True if a text value exists, otherwise false.
         */
        public boolean hasText() {
            return (this.text != null);
        }

        protected void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public String getTag() {
            return this.tag;
        }

        /**
         * Lazily gets path of the node such as "/root/nodeA/subNodeB". The path
         * is only generated on the first call to this method, then its permanently
         * stored in this node for quick lookup.
         */
        public String getPath() {
            if (path == null) {
                if (getParent() != null && getParent().getTag() != null)
                    path = getParent().getPath() + "/" + getTag();
                else
                    path = "/" + getTag();
            }
            return path;
        }

        public boolean hasParent() {
            return (this.parent != null);
        }

        protected void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return this.parent;
        }

        protected void addChild(Node child) {
            if (this.children == null) {
                this.children = new ArrayList<Node>();
            }
            this.children.add(child);
        }

        public int getChildrenSize() {
            if (this.children == null) {
                return 0;
            } else {
                return this.children.size();
            }
        }

        public boolean hasChildren() {
            return (this.children != null && this.children.size() > 0);
        }

        /**
         * Returns list of children or null if none exist.
         */
        public ArrayList<Node> getChildren() {
            return this.children;
        }

        public Node getChild(int index) throws IndexOutOfBoundsException {
            return this.children.get(index);
        }


        public boolean hasAttributes() {
            return (this.attrs != null && this.attrs.length > 0);
        }

        /**
         * Get an array of element attributes or null if none exist
         */
        public Attribute[] getAttributes() {
            return this.attrs;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get an element attribute.
         *
         * @return attribute or null.
         */
        /**
        public String getAttribute(String name)
        {
            return getAttribute(name, null);
        }
         */

        /* ------------------------------------------------------------ */
        /**
         * Get an element attribute.
         *
         * @return attribute or null.
         */
        /**
        public String getAttribute(String name, String dft)
        {
            if (_attrs == null || name == null)
                return dft;
            for (int i = 0; i < _attrs.length; i++)
                if (name.equals(_attrs[i].getName()))
                    return _attrs[i].getValue();
            return dft;
        }
         */

        /* ------------------------------------------------------------ */
        /**
         * Get the number of children nodes.
         */
        /**
        public int size()
        {
            if (_list != null)
                return _list.size();
            return 0;
        }
         */

        /* ------------------------------------------------------------ */
        /**
         * Get the ith child node or content.
         *
         * @return Node or String.
         */
        /**
        public Object get(int i)
        {
            if (_list != null)
                return _list.get(i);
            return null;
        }
         */

        /* ------------------------------------------------------------ */
        /**
         * Get the first child node with the tag.
         *
         * @param tag
         * @return Node or null.
         */
        /**
        public Node get(String tag)
        {
            if (_list != null)
            {
                for (int i = 0; i < _list.size(); i++)
                {
                    Object o = _list.get(i);
                    if (o instanceof Node)
                    {
                        Node n = (Node) o;
                        if (tag.equals(n._tag))
                            return n;
                    }
                }
            }
            return null;
        }
         */

        /* ------------------------------------------------------------ */
        /**
        public void add(int i, Object o)
        {
            if (_list == null)
                _list = new ArrayList();
            if (o instanceof String)
            {
                if (_lastString)
                {
                    int last = _list.size() - 1;
                    _list.set(last, (String) _list.get(last) + o);
                }
                else
                    _list.add(i, o);
                _lastString = true;
            }
            else
            {
                _lastString = false;
                _list.add(i, o);
            }
        }
         */

        /* ------------------------------------------------------------ */
        /**
        public void clear()
        {
            if (_list != null)
                _list.clear();
            _list = null;
        }
         */

        /* ------------------------------------------------------------ */
        /**
         * Get a tag as a string.
         *
         * @param tag The tag to get
         * @param tags IF true, tags are included in the value.
         * @param trim If true, trim the value.
         * @return results of get(tag).toString(tags).
         */
        /**
        public String getString(String tag, boolean tags, boolean trim)
        {
            Node node = get(tag);
            if (node == null)
                return null;
            String s = node.toString(tags);
            if (s != null && trim)
                s = s.trim();
            return s;
        }
         */

        /**
        public synchronized String toString()
        {
            return toString(true);
        }

        public synchronized String toString(boolean tag)
        {
            StringBuilder buf = new StringBuilder();
            toString(buf, tag);
            return buf.toString();
        }

        public synchronized String toString(boolean tag, boolean trim)
        {
            String s = toString(tag);
            if (s != null && trim)
                s = s.trim();
            return s;
        }

        private synchronized void toString(StringBuilder buf, boolean tag)
        {
            if (tag)
            {
                buf.append("<");
                buf.append(_tag);

                if (_attrs != null)
                {
                    for (int i = 0; i < _attrs.length; i++)
                    {
                        buf.append(' ');
                        buf.append(_attrs[i].getName());
                        buf.append("=\"");
                        buf.append(_attrs[i].getValue());
                        buf.append("\"");
                    }
                }
            }

            if (_list != null)
            {
                if (tag)
                    buf.append(">");
                for (int i = 0; i < _list.size(); i++)
                {
                    Object o = _list.get(i);
                    if (o == null)
                        continue;
                    if (o instanceof Node)
                        ((Node) o).toString(buf, tag);
                    else
                        buf.append(o.toString());
                }
                if (tag)
                {
                    buf.append("</");
                    buf.append(_tag);
                    buf.append(">");
                }
            }
            else if (tag)
                buf.append("/>");
        }
         */

        /* ------------------------------------------------------------ */
        /**
         * Iterator over named child nodes.
         *
         * @param tag The tag of the nodes.
         * @return Iterator over all child nodes with the specified tag.
         */
        /**
        public Iterator iterator(final String tag)
        {
            return new Iterator()
            {
                int c = 0;
                Node _node;
         *
                public boolean hasNext()
                {
                    if (_node != null)
                        return true;
                    while (_list != null && c < _list.size())
                    {
                        Object o = _list.get(c);
                        if (o instanceof Node)
                        {
                            Node n = (Node) o;
                            if (tag.equals(n._tag))
                            {
                                _node = n;
                                return true;
                            }
                        }
                        c++;
                    }
                    return false;
                }

                public Object next()
                {
                    try
                    {
                        if (hasNext())
                            return _node;
                        throw new NoSuchElementException();
                    }
                    finally
                    {
                        _node = null;
                        c++;
                    }
                }

                public void remove()
                {
                    throw new UnsupportedOperationException("Not supported");
                }
            };
        }
         */
    }
}

