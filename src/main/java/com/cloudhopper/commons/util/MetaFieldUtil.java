package com.cloudhopper.commons.util;

/*
 * #%L
 * ch-commons-util
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloudhopper.commons.util.annotation.MetaField;

/**
 * Utility class for generating an array of MetaFieldInfo objects at runtime by
 * reflecting a class and searching for the MetaField annotation.
 * <br>
 * NOTE: This automatically handles unwrapping certain types such as an AtomicReference
 * where the actual values you'd want to validate is the one contained within.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class MetaFieldUtil {
    private static Logger logger = LoggerFactory.getLogger(MetaFieldUtil.class);

    /**
     * Creates a string for an object based on the MetaField annotations.
     * @param obj The object to search
     * @return A string representing the current values of any MetaFields
     */
    public static String toMetaFieldInfoString(Object obj) {
        StringBuffer buf = new StringBuffer(100);
        MetaFieldInfo[] fields = toMetaFieldInfoArray(obj, null, true);
        for (int i = 0; i < fields.length; i++) {
            MetaFieldInfo field = fields[i];
            buf.append(field.name);
            buf.append("=");
            // if the field's actual value is non-null and of type String
            if (field.actualValue != null && field.actualValue.getClass().equals(String.class)) {
                buf.append('"');
                buf.append(field.value);
                buf.append('"');
            } else {
                buf.append(field.value);
            }
            // if not last element, delimit with a comma
            if (i+1 < fields.length)
                buf.append(",");
        }
        return buf.toString();
    }

    /**
     * Returns a new MetaFieldInfo array of all Fields annotated with MetaField
     * in the runtime object type. If the object is null, this method will
     * return null. Will use the default "null" string representation of
     * null values. NOTE: This method recursively searches the object.
     * @param obj The runtime instance of the object to search. It must be an
     *          instance of the type.  If its a subclass of the type, this method
     *          will only return MetaFields of the type passed in.
     * @return The MetaFieldInfo array
     */
    public static MetaFieldInfo[] toMetaFieldInfoArray(Object obj) {
        return toMetaFieldInfoArray(obj, null);
    }


    /**
     * Returns a new MetaFieldInfo array of all Fields annotated with MetaField
     * in the runtime object type. If the object is null, this method will
     * return null. NOTE: This method recursively searches the object.
     * @param obj The runtime instance of the object to search. It must be an
     *          instance of the type.  If its a subclass of the type, this method
     *          will only return MetaFields of the type passed in.
     * @param stringForNullValues If a field is null, this is the string to swap
     *      in as the String value vs. "null" showing up.
     * @return The MetaFieldInfo array
     */
    public static MetaFieldInfo[] toMetaFieldInfoArray(Object obj, String stringForNullValues) {
        return toMetaFieldInfoArray(obj, stringForNullValues, false);
    }


    /**
     * Returns a new MetaFieldInfo array of all Fields annotated with MetaField
     * in the runtime object type. If the object is null, this method will
     * return null. NOTE: This method recursively searches the object.
     * @param obj The runtime instance of the object to search. It must be an
     *          instance of the type.  If its a subclass of the type, this method
     *          will only return MetaFields of the type passed in.
     * @param stringForNullValues If a field is null, this is the string to swap
     *      in as the String value vs. "null" showing up.
     * @param ignoreAnnotatedName Whether to ignore the name of the MetaField
     *      and always return the field name instead.  If false, this method will
     *      use the annotated name if it exists, otherwise it'll use the field name.
     * @return The MetaFieldInfo array
     */
    public static MetaFieldInfo[] toMetaFieldInfoArray(Object obj, String stringForNullValues, boolean ignoreAnnotatedName) {
        return toMetaFieldInfoArray((obj != null ? obj.getClass() : null), obj, stringForNullValues, ignoreAnnotatedName, true);
    }


    /**
     * Returns a new MetaFieldInfo array of all Fields annotated with MetaField
     * in the object type. The object must be an instance of the type, otherwise
     * this method may throw a runtime exception. Optionally, this method can
     * recursively search the object to provide a deep view of the entire class.
     * @param type The class type of the object
     * @param obj The runtime instance of the object to search. It must be an
     *          instance of the type.  If its a subclass of the type, this method
     *          will only return MetaFields of the type passed in.
     * @param stringForNullValues If a field is null, this is the string to swap
     *      in as the String value vs. "null" showing up.
     * @param ignoreAnnotatedName Whether to ignore the name of the MetaField
     *      and always return the field name instead.  If false, this method will
     *      use the annotated name if it exists, otherwise it'll use the field name.
     * @param recursive If true, this method will recursively search any fields
     *      to see if they also contain MetaField annotations. If they do, this
     *      method will include those in its returned array.
     * @return The MetaFieldInfo array
     */
    public static MetaFieldInfo[] toMetaFieldInfoArray(Class type, Object obj, String stringForNullValues, boolean ignoreAnnotatedName, boolean recursive) {
        return internalToMetaFieldInfoArray(type, obj, null, null, stringForNullValues, ignoreAnnotatedName, recursive);
    }


    /**
     * Optionally, recursively returns a new MetaFieldInfo array of all Fields
     * annotated with MetaField in the object. If recursive is true, this will
     * also search any fields in each class and add them in their order as well.
     * @param type The class type of the obj (in case obj is null)
     * @param obj The object to search. If this is null, will default values for each field to null.
     * @param appendFieldName Will append this value to the name of the field (really for subfields)
     * @param stringForNullValues If a field is null, this is the string to swap
     *      in as the String value vs. "null" showing up.
     * @return The new MetaFieldInfo array
     */
    protected static MetaFieldInfo[] internalToMetaFieldInfoArray(Class type, Object obj, String preFieldName,
            String postFieldName, String stringForNullValues, boolean ignoreAnnotatedName, boolean recursive) {

        // get the class hierarchy
        Class[] hierarchy = ClassUtil.getClassHierarchy(type);
        // if the class hierarchy is 0 (there are definitely no meta fields to process
        if (hierarchy == null || hierarchy.length == 0)
            return null;
        
        // create the array we'll return
        ArrayList<MetaFieldInfo> infos = new ArrayList<MetaFieldInfo>();

        // make sure appendFieldName is always an empty String
        if (preFieldName == null)
            preFieldName = "";
        if (postFieldName == null)
            postFieldName = "";

        // keep searching up until we reach an Object class type
        for (Class classType : hierarchy) {
            
            // begin search for each field (variable)
            for (Field f : classType.getDeclaredFields()) {

                // is this field marked as a MetaField?
                if (f.isAnnotationPresent(MetaField.class)) {

                    // get the annotation
                    MetaField metaField = f.getAnnotation(MetaField.class);

                    // create the new counter info object we'll store
                    // now, this class may actually still represent a class we want to recursively search
                    // we'll create the new info object for now, but this may change
                    MetaFieldInfo info = new MetaFieldInfo();
                    
                    // save the field name
                    info.fieldName = f.getName();
                    // save the field class
                    info.fieldClass = f.getType();
                    // save the level
                    info.level = metaField.level();

                    // is there an annotated name? if not we'll default to the field name
                    info.name = preFieldName + (!metaField.name().equals("") && !ignoreAnnotatedName ? metaField.name() : f.getName()) + postFieldName;

                    // default the recursive pre field names
                    String recursivePreFieldName = preFieldName + f.getName() + ".";
                    String recursivePostFieldName = "";
                    
                    // if this had an actual name, not using the field name, slightly diff format
                    if (!metaField.name().equals("") && !ignoreAnnotatedName) {
                        // only add [ and ] if this is not the first
                        if (!preFieldName.equals("")) {
                            recursivePreFieldName = preFieldName + metaField.name() + "][";
                            recursivePostFieldName = "]";
                        } else {
                            recursivePreFieldName = metaField.name() + "[";
                            recursivePostFieldName = "]";
                        }
                    }

                    // required to read private variables
                    f.setAccessible(true);

                    // read the actual value if the obj isn't null
                    if (obj != null) {
                        // get the actual value
                        try {
                            info.actualValue = f.get(obj);
                        } catch (Exception e) {
                            logger.error("impossible case if run outside of an applet");
                        }

                        //
                        // handle "unwrapping" objects -- AtomicReference
                        //
                        if (info.actualValue != null && f.getType().equals(AtomicReference.class)) {
                            //logger.debug("AtomicReference found, going to unwrap it");
                            AtomicReference ref = (AtomicReference)info.actualValue;
                            // get the actual underlying value
                            info.actualValue = ref.get();
                            // if not null, we can get its type too
                            if (info.actualValue != null) {
                                info.fieldClass = info.actualValue.getClass();
                            }
                        }
                        
                    } else {
                        info.actualValue = null;
                    }

                    // get a proper string value of this counter
                    if (info.actualValue == null) {
                        info.value = stringForNullValues;
                    } else {
                        info.value = info.actualValue.toString();
                    }
                    
                    // add our annotated description
                    info.description = metaField.description();

                    // if this is recursive, we need to check of the subvalues are actually wanted instead...
                    // if the actual value is null though, we know the sub-values are null...
                    if (recursive) {
                        // see if there are any MetaFields in the sub-object
                        MetaFieldInfo[] subInfos = internalToMetaFieldInfoArray(info.fieldClass, info.actualValue, recursivePreFieldName, recursivePostFieldName, stringForNullValues, ignoreAnnotatedName, recursive);
                        // was anything returned
                        if (subInfos == null || subInfos.length == 0) {
                            infos.add(info);
                        } else {
                            // otherwise, add everything
                            // if there were any, also add these
                            for (MetaFieldInfo subInfo : subInfos) {
                                infos.add(subInfo);
                            }
                        }
                    } else {
                        // add this to our array
                        infos.add(info);
                    }
                }
            }
        }

        // return our array
        return infos.toArray(new MetaFieldInfo[0]);
    }

}
