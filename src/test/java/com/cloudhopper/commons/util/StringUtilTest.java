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

// third party imports
import java.util.Properties;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class StringUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(StringUtilTest.class);

    private static final byte[] TEST_BYTES1 = {'H','E','L','L','O',' ','W','O','R','L','D'};
    private static final byte[] TEST_HELLO = {'H','E','L','L','O'};
    private static final byte[] TEST_H = {'H'};
    private static final byte[] TEST_WORLD = {'W','O','R','L','D'};
    private static final byte[] TEST_W = {'W'};
    private static final byte[] TEST_D = {'D'};
    private static final byte[] TEST_SPACEH = {' ','H'};
    private static final byte[] TEST_SPACEW = {' ','W'};

    @Test
    public void getAsciiString() {
        String result = StringUtil.getAsciiString(TEST_BYTES1);
        Assert.assertEquals("HELLO WORLD", result);
    }

    @Test
    public void toAsciiBytes() {
        byte[] result = StringUtil.getAsciiBytes("HELLO WORLD");
        Assert.assertArrayEquals(TEST_BYTES1, result);
    }

    @Test
    public void stripQuotes() {
        String string0 = "\"10958\"";
        String string1 = "10958\"";
        String string2 = "\"10958";
        String string3 = "10958";
        String string4 = "";
        Assert.assertEquals("10958", StringUtil.stripQuotes(string0));
        Assert.assertEquals("10958", StringUtil.stripQuotes(string1));
        Assert.assertEquals("10958", StringUtil.stripQuotes(string2));
        Assert.assertEquals("10958", StringUtil.stripQuotes(string3));
        Assert.assertEquals("", StringUtil.stripQuotes(string4));
        String string5 = "\"";
        String string6 = "1\"";
        String string7 = "\"1";
        String string8 = "1";
        Assert.assertEquals("", StringUtil.stripQuotes(string5));
        Assert.assertEquals("1", StringUtil.stripQuotes(string6));
        Assert.assertEquals("1", StringUtil.stripQuotes(string7));
        Assert.assertEquals("1", StringUtil.stripQuotes(string8));
    }

    @Test
    public void indexOfWithStringArray() {
        Assert.assertEquals(-1, StringUtil.indexOf(null, ""));
        Assert.assertEquals(-1, StringUtil.indexOf(null, null));
        String[] strings = { "a", null };
        Assert.assertEquals(-1, StringUtil.indexOf(strings, ""));
        Assert.assertEquals(-1, StringUtil.indexOf(strings, "b"));
        Assert.assertEquals(0, StringUtil.indexOf(strings, "a"));
        Assert.assertEquals(1, StringUtil.indexOf(strings, null));
    }

    @Test
    public void containsWithStringArray() {
        Assert.assertEquals(false, StringUtil.contains(null, ""));
        Assert.assertEquals(false, StringUtil.contains(null, null));
        String[] strings = { "a", null };
        Assert.assertEquals(false, StringUtil.contains(strings, ""));
        Assert.assertEquals(false, StringUtil.contains(strings, "b"));
        Assert.assertEquals(true, StringUtil.contains(strings, "a"));
        Assert.assertEquals(true, StringUtil.contains(strings, null));
        String[] strings1 = { "FirstName", "LastName" };
        Assert.assertEquals(true, StringUtil.contains(strings1, "FirstName"));
        Assert.assertEquals(true, StringUtil.contains(strings1, "LastName"));
    }

    @Test
    public void isSafeChar() {
        Assert.assertEquals(true, StringUtil.isSafeChar('%'));
        Assert.assertEquals(true, StringUtil.isSafeChar('A'));
        Assert.assertEquals(true, StringUtil.isSafeChar('9'));
        Assert.assertEquals(true, StringUtil.isSafeChar('#'));
        Assert.assertEquals(true, StringUtil.isSafeChar('!'));
        Assert.assertEquals(false, StringUtil.isSafeChar(' '));
        Assert.assertEquals(false, StringUtil.isSafeChar('\n'));
        Assert.assertEquals(false, StringUtil.isSafeChar('\r'));
        Assert.assertEquals(false, StringUtil.isSafeChar('\t'));
        Assert.assertEquals(false, StringUtil.isSafeChar('"'));
    }

    @Test
    public void isSafeString() {
        Assert.assertEquals(true, StringUtil.isSafeString("abcdefghijklmnopqrstuvwzwz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Assert.assertEquals(false, StringUtil.isSafeString(" "));
        Assert.assertEquals(false, StringUtil.isSafeString("\n"));
        Assert.assertEquals(false, StringUtil.isSafeString("\t"));
        Assert.assertEquals(false, StringUtil.isSafeString("\r"));
    }

    @Test
    public void capitalize() {
        Assert.assertEquals(null, StringUtil.capitalize(null));
        Assert.assertEquals("", StringUtil.capitalize(""));
        Assert.assertEquals("J", StringUtil.capitalize("j"));
        Assert.assertEquals("J", StringUtil.capitalize("J"));
        Assert.assertEquals("Joe", StringUtil.capitalize("joe"));
        Assert.assertEquals("Joe", StringUtil.capitalize("Joe"));
    }

    @Test
    public void uncapitalize() {
        Assert.assertEquals(null, StringUtil.uncapitalize(null));
        Assert.assertEquals("", StringUtil.uncapitalize(""));
        Assert.assertEquals("j", StringUtil.uncapitalize("j"));
        Assert.assertEquals("j", StringUtil.uncapitalize("J"));
        Assert.assertEquals("joe", StringUtil.uncapitalize("joe"));
        Assert.assertEquals("joe", StringUtil.uncapitalize("Joe"));
    }

    @Test
    public void substituteWithProperties() throws Exception {
        // create test properties
        Properties props = new Properties();
        props.put("TEST1", "Hello World");
        props.put(" TEST1", "whitespace matters");
        props.put("TEST2", "Hello");
        props.put("TEST3", "World");

        // simple substitution
        String result = StringUtil.substituteWithProperties("$ENV{TEST1}", "$ENV{", "}", props);
        Assert.assertEquals("Hello World", result);

        // no substitution
        result = StringUtil.substituteWithProperties("no key here", "$ENV{", "}", props);
        Assert.assertEquals("no key here", result);

        // substitution with text before and after
        result = StringUtil.substituteWithProperties("Yo $ENV{TEST1} this is cool", "$ENV{", "}", props);
        Assert.assertEquals("Yo Hello World this is cool", result);

        // substitution with text before only
        result = StringUtil.substituteWithProperties("Y$ENV{TEST1}", "$ENV{", "}", props);
        Assert.assertEquals("YHello World", result);

        // substitution with text after only
        result = StringUtil.substituteWithProperties("$ENV{TEST1}Y", "$ENV{", "}", props);
        Assert.assertEquals("Hello WorldY", result);

        // key name does not include TRIMMING -- whitespace matters
        result = StringUtil.substituteWithProperties("$ENV{ TEST1}", "$ENV{", "}", props);
        Assert.assertEquals("whitespace matters", result);
        
        // substitution with empty string
        result = StringUtil.substituteWithProperties("", "$ENV{", "}", props);
        Assert.assertEquals("", result);

        // substitution with null
        result = StringUtil.substituteWithProperties(null, "$ENV{", "}", props);
        Assert.assertEquals(null, result);

        // substitution with multiple replacments
        result = StringUtil.substituteWithProperties("$ENV{TEST2}$ENV{TEST3}", "$ENV{", "}", props);
        Assert.assertEquals("HelloWorld", result);

        // substitution with multiple replacments
        result = StringUtil.substituteWithProperties("$ENV{TEST2} $ENV{TEST3}", "$ENV{", "}", props);
        Assert.assertEquals("Hello World", result);

        // substitution with multiple replacments and text before, after
        result = StringUtil.substituteWithProperties("Yo $ENV{TEST2} $ENV{TEST3} dude", "$ENV{", "}", props);
        Assert.assertEquals("Yo Hello World dude", result);
    }

    @Test(expected=SubstitutionException.class)
    public void substituteWithPropertiesMissingKeyThrowsException() throws Exception {
        // create test properties
        Properties props = new Properties();
        props.put("TEST1", "Hello World");

        // key is missing, should throw exception
        String string0 = StringUtil.substituteWithProperties("$ENV{TEST2}", "$ENV{", "}", props);
    }

    @Test(expected=SubstitutionException.class)
    public void substituteWithPropertiesEmptyKeyThrowsException() throws Exception {
        // create test properties
        Properties props = new Properties();
        props.put("TEST1", "Hello World");
        // key is empty
        String string0 = StringUtil.substituteWithProperties("$ENV{}", "$ENV{", "}", props);
    }

    @Test(expected=SubstitutionException.class)
    public void substituteWithPropertiesMissingEndTokenThrowsException() throws Exception {
        // create test properties
        Properties props = new Properties();
        props.put("TEST1", "Hello World");
        // end of string token is missing, should throw exception
        String string0 = StringUtil.substituteWithProperties("$ENV{TEST1", "$ENV{", "}", props);
    }

    @Test
    public void substituteWithEnvironment() throws Exception {
        // grab some common env vars on linux/windows
        String username = System.getenv("USERNAME");

        // FIXME: this test isn't actually reliable perhaps if the value is missing...
        // let's just skip this test if the username is missing
        if (username != null) {
            // simple substitution
            String result = StringUtil.substituteWithEnvironment("Hello $ENV{USERNAME}");
            Assert.assertEquals("Hello " + username, result);
        } else {
            // skip this test...
        }
    }

    @Test
    public void escapeXml() throws Exception {
        // "bread" & "butter'ed" => &quot;bread&quot; &amp; &quot;butter&apos;ed&quot;
        String str0 = StringUtil.escapeXml("\"bread\" & \"butter'ed\"");
        Assert.assertEquals("&quot;bread&quot; &amp; &quot;butter&apos;ed&quot;", str0);

        str0 = StringUtil.escapeXml("\n\r");
        Assert.assertEquals("&#10;&#13;", str0);

        str0 = StringUtil.escapeXml(null);
        Assert.assertNull(str0);

        str0 = StringUtil.escapeXml("");
        Assert.assertEquals("", str0);

        str0 = StringUtil.escapeXml("Hello World");
        Assert.assertEquals("Hello World", str0);
    }

    @Test
    public void toStringWithNullAsEmpty() throws Exception {
        Assert.assertEquals("", StringUtil.toStringWithNullAsEmpty(null));
        Assert.assertEquals("", StringUtil.toStringWithNullAsEmpty(""));
        Assert.assertEquals("ABC", StringUtil.toStringWithNullAsEmpty(new StringBuilder("ABC")));
    }

    @Test
    public void toStringWithNullAsReplaced() throws Exception {
        Assert.assertEquals("<NULL>", StringUtil.toStringWithNullAsReplaced(null));
        Assert.assertEquals("", StringUtil.toStringWithNullAsReplaced(""));
        Assert.assertEquals("ABC", StringUtil.toStringWithNullAsReplaced(new StringBuilder("ABC")));
    }

    @Test
    public void toStringWithNullAsNull() throws Exception {
        Assert.assertEquals(null, StringUtil.toStringWithNullAsNull(null));
        Assert.assertEquals("", StringUtil.toStringWithNullAsNull(""));
        Assert.assertEquals("ABC", StringUtil.toStringWithNullAsNull(new StringBuilder("ABC")));
    }

    @Test
    public void removeAllCharsExceptDigits() throws Exception {
        Assert.assertEquals(null, StringUtil.removeAllCharsExceptDigits(null));
        Assert.assertEquals("", StringUtil.removeAllCharsExceptDigits(""));
        Assert.assertEquals("", StringUtil.removeAllCharsExceptDigits("ABC"));
        Assert.assertEquals("1", StringUtil.removeAllCharsExceptDigits("+1"));
        Assert.assertEquals("13135551212", StringUtil.removeAllCharsExceptDigits("+1 313-555 1212"));
    }
}
