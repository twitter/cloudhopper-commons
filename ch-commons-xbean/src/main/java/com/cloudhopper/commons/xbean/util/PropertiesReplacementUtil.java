package com.cloudhopper.commons.xbean.util;

/*
 * #%L
 * ch-commons-xbean
 * %%
 * Copyright (C) 2015 Cloudhopper by Twitter
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

import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.commons.util.SubstitutionException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utilities for replacing templated properties in files before they are used to "configure" and XmlBean.
 * It is often the case that XBean is used to configure applications with sensitive information that 
 * should not reside in the code, but can be replaced in memory, at runtime, from a more secure source.
 *
 * Simple properties example (properties are templated as ${name}):
 * <pre>
 * {@code
 * InputStream is = PropertiesReplacementUtil.replace(confFile, propsFile);
 * Foo foo = XmlBeanFactory.create(is, Foo.class);
 * }
 * </pre>
 *
 * Properties example with different delimiters:
 * <pre>
 * {@code
 * Properties props = PropertiesReplacementUtil.loadProperties(propsFile);
 * InputStream is = PropertiesReplacementUtil.replaceProperties(new FileInputStream(confFile), props, "{{", "}}");
 * Foo foo = XmlBeanFactory.create(is, Foo.class);
 * }
 * </pre>
 *
 * Example for replacing from properties file, System.getProperties(), and System.getenv():
 * <pre>
 * {@code
 * InputStream is = PropertiesReplacementUtil.replace(confFile, propsFile, System.getProperties, true);
 * Foo foo = XmlBeanFactory.create(is, Foo.class);
 * }
 * </pre>
 * @author garth
 */
public class PropertiesReplacementUtil {

    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given file.
     * @param file The template file
     * @param props The properties file
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replace(File file, File props) throws IOException, SubstitutionException{
	return replace(file, props, null, false);
    }
    
    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given file.
     * @param file The template file
     * @param props The properties file
     * @param base Properties that should override those loaded from the file
     * @param env If properties from System.getenv should also be used
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replace(File file, File props, Properties base, boolean env) throws IOException, SubstitutionException{
	if (env) return replaceProperties(replaceEnv(file), loadProperties(base, props));
	else return replaceProperties(file, loadProperties(base, props));
    }
    
    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given file.
     * @param file The template file
     * @param props The properties
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replaceProperties(File file, Properties props) throws IOException, SubstitutionException {
	return replaceProperties(new FileInputStream(file), props);
    }

    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given InputStream source.
     * @param source The source stream
     * @param props The properties
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replaceProperties(InputStream source, Properties props) throws IOException, SubstitutionException {
	return replaceProperties(source, props, "${", "}");
    }

    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given InputStream source.
     * @param source The source stream
     * @param props The properties
     * @param startStr The String that marks the start of a replacement key such as "${"
     * @param endStr The String that marks the end of a replacement key such as "}"
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replaceProperties(InputStream source, Properties props, String startStr, String endStr) throws IOException, SubstitutionException {
	String template = streamToString(source);
	String replaced = StringUtil.substituteWithProperties(template, startStr, endStr, props);
	System.err.println(template);
	System.err.println(replaced);
	return new ByteArrayInputStream(replaced.getBytes());
    }

    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given file, using System.getenv as names/values.
     * @param file The template file
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replaceEnv(File file) throws IOException, SubstitutionException {
	return replaceEnv(new FileInputStream(file));
    }

    /**
     * Creates an InputStream containing the document resulting from replacing template
     * parameters in the given InputStream source, using System.getenv as names/values.
     * @param source The source stream
     * @return An InputStream containing the resulting document.
     */
    static public InputStream replaceEnv(InputStream source) throws IOException, SubstitutionException {
	String template = streamToString(source);
	String replaced = StringUtil.substituteWithEnvironment(template);
	System.err.println(template);
	System.err.println(replaced);
	return new ByteArrayInputStream(replaced.getBytes());
    }

    /**
     * Loads the given file into a Properties object.
     * @param file The file to load
     * @return Properties loaded from the file
     */
    static public Properties loadProperties(File file) throws IOException {
	return loadProperties(null, file);
    }
    
    /**
     * Loads the given file into a Properties object.
     * @param base Properties that should override those loaded from the file
     * @param file The file to load
     * @return Properties loaded from the file
     */
    static public Properties loadProperties(Properties base, File file) throws IOException {
	FileReader reader = new FileReader(file);
	Properties props = new Properties();
	props.load(reader);
	if (base != null) props.putAll(base);
	reader.close();
	return props;
    }

    static private String streamToString(InputStream source) throws IOException {
	InputStream is = new BufferedInputStream(source);
	try {
	    return StringUtil.readToString(is);
	} finally {
	    is.close();
	}
    }

}
