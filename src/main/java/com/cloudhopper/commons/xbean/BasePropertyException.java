/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

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
