/*
 * Copyright 2012 Twitter, Inc..
 *
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
 */
package com.cloudhopper.commons.xbean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.SAXException;

/**
 *
 * @author joelauer
 */
public class XmlBeanFactory {
    
    static private XmlBean createXmlBean() {
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        return xbean;
    }
    
    /**
     * Creates a new object of the specified type configured by parsing the
     * provided XML.<br/>
     * The underlying XmlBean will start at the root of the XML and will access
     * private fields as needed.
     * @param xml The XML String containing the document to configure the object
     * @param type The class to create a new object instance
     * @return A new object configured by the XML
     * @see XmlBean#configure(java.lang.String, java.lang.Object) 
     */
    static public <E> E create(String xml, Class<E> type) throws XmlBeanException, IOException, SAXException, InstantiationException, IllegalAccessException {
        Object obj = type.newInstance();
        configure(xml, obj);
        return (E)obj;
    }
    
    /**
     * Creates a new object of the specified type configured by parsing the
     * provided XML.<br/>
     * The underlying XmlBean will start at the root of the XML and will access
     * private fields as needed.
     * @param is The InputStream containing the XML document to configure the object
     * @param type The class to create a new object instance
     * @return A new object configured by the XML
     * @see XmlBean#configure(java.io.InputStream, java.lang.Object) 
     */
    static public <E> E create(InputStream is, Class<E> type) throws XmlBeanException, IOException, SAXException, InstantiationException, IllegalAccessException {
        Object obj = type.newInstance();
        configure(is, obj);
        return (E)obj;
    }
    
    /**
     * Creates a new object of the specified type configured by parsing the
     * provided XML.<br/>
     * The underlying XmlBean will start at the root of the XML and will access
     * private fields as needed.
     * @param file The file containing the XML document to configure the object
     * @param type The class to create a new object instance
     * @return A new object configured by the XML
     * @see XmlBean#configure(java.io.File, java.lang.Object) 
     */
    static public <E> E create(File file, Class<E> type) throws XmlBeanException, IOException, SAXException, InstantiationException, IllegalAccessException {
        Object obj = type.newInstance();
        configure(file, obj);
        return (E)obj;
    }
    
    /**
     * Configures the object by parsing the provided XML.<br/>
     * The underlying XmlBean will start at the root of the XML and will access
     * private fields as needed.
     * @param xml The XML String containing the document to configure the object
     * @param obj The object instance to configure
     * @see XmlBean#configure(java.lang.String, java.lang.Object) 
     */
    static public void configure(String xml, Object obj) throws XmlBeanException, IOException, SAXException {
        createXmlBean().configure(xml, obj);
    }
    
    /**
     * Configures the object by parsing the provided XML.<br/>
     * The underlying XmlBean will start at the root of the XML and will access
     * private fields as needed.
     * @param is The InputStream containing the XML document to configure the object
     * @param obj The object instance to configure
     * @see XmlBean#configure(java.io.InputStream, java.lang.Object) 
     */
    static public void configure(InputStream is, Object obj) throws XmlBeanException, IOException, SAXException {
        createXmlBean().configure(is, obj);
    }
    
    /**
     * Configures the object by parsing the provided XML.<br/>
     * The underlying XmlBean will start at the root of the XML and will access
     * private fields as needed.
     * @param file The file containing the XML document to configure the object
     * @param obj The object instance to configure
     * @see XmlBean#configure(java.io.File, java.lang.Object) 
     */
    static public void configure(File file, Object obj) throws XmlBeanException, IOException, SAXException {
        createXmlBean().configure(file, obj);
    }
    
}
