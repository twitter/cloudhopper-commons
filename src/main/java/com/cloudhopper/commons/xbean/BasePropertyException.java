
package com.cloudhopper.commons.xbean;

/**
 * Base exception that includes information about the property that caused
 * the exception to occur.
 * @author joelauer
 */
public abstract class BasePropertyException extends XmlBeanException {

    private String propertyName;
    private String xmlPath;
    private Class targetClass;

    public BasePropertyException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public BasePropertyException(String propertyName, String xmlPath, String msg, Throwable t) {
        this(propertyName, xmlPath, null, msg, t);
    }

    public BasePropertyException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, targetClass, msg, null);
    }

    public BasePropertyException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(msg + " [property=" + propertyName + ", xml=" + xmlPath + (targetClass != null ? ", target=" + targetClass.getName() : "") + "]", t);
        this.propertyName = propertyName;
        this.xmlPath = xmlPath;
        this.targetClass = targetClass;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getXmlPath() {
        return this.xmlPath;
    }

    public Class getTargetClass() {
        return this.targetClass;
    }
}
