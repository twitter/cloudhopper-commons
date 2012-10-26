package com.cloudhopper.commons.util.demo;

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

// java imports
import com.cloudhopper.commons.util.EncryptUtil;
import java.security.*;
import java.util.*;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class EncryptUtilMain {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: EncryptUtilMain <command> <args>");
            System.out.println("\t<command>: providers, encrypt, decrypt, generate-key");
            System.exit(0);
        }

        String command = args[0];
        if (command.equalsIgnoreCase("generate-key")) {
            System.out.println("New key: " + EncryptUtil.generateKey());
        } else if (command.equalsIgnoreCase("providers")) {
            showProviders();
        } else if (command.equalsIgnoreCase("encrypt")) {
            String key = args[1];
            String plaintext = args[2];
            System.out.println(plaintext + " = " + EncryptUtil.encrypt(key, plaintext));
        } else if (command.equalsIgnoreCase("decrypt")) {
            String key = args[1];
            String ciphertext = args[2];
            System.out.println(ciphertext + " = " + EncryptUtil.decrypt(key, ciphertext));
        }
    }

    public static void showProviders() {
        try {
            Provider[] providers = Security.getProviders();
            for (int i = 0; i < providers.length; i++) {
                System.out.println("Provider: " +
                        providers[i].getName() + ", " + providers[i].getInfo());
                for (Iterator itr = providers[i].keySet().iterator();
                        itr.hasNext();) {
                    String key = (String) itr.next();
                    String value = (String) providers[i].get(key);
                    System.out.println("\t" + key + " = " + value);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
