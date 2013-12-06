package com.cloudhopper.commons.xbean.demo;

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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 *
 * @author joelauer
 */
public class AnnotationInfoMain {
    
    public static class TestA<T> {
        T item;
    }
    
    public ArrayList<TestA<String>> a = new ArrayList<TestA<String>>();
    //public TreeMap<String,Integer> a = new TreeMap<String,Integer>();
    //public String a = "";
    
    public static void main(String[] args) throws Exception {
        Field field = GenericInfoMain.class.getDeclaredField("a");
        Type type = field.getGenericType();
        
        //ArrayList<String> b = new ArrayList<String>();
        //Type type = b.getClass().getGenericSuperclass();
        
        System.out.println("type: " + type);
        System.out.println("class of type: " + type.getClass());
        
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            System.out.println("raw type: " + pt.getRawType());  
            System.out.println("owner type: " + pt.getOwnerType());  
            System.out.println("actual type args:");  
            System.out.println(" : " + type);
        }
        
    }

}
