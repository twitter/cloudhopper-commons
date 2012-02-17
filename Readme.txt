Cloudhopper, Inc.
http://www.cloudhopper.com/
Commons XBean Library
--------------------------------------------------------------------------------

The Xbean Java library is a set of simple utility classes for configuring a
Java object from XML.  The Java object may contain other Java objects in which
case the XML may configure a graph of Java objects.  The properties of a
class that need configured generally must use JavaBean naming patterns.

The library does have the ability to bypass access permissions and directly
set the value of private or protected fields.  The library will always try to
use the appropriate get/set methods and fallback to directly setting the field
value if a get/set is missing and "access private properties" is enabled.

While its best if a Java class configured with this library has an empty
constructor, the library does not always need to actually create a new object.
For properties that represent other Java objects, the library will always check
to see if a value already exists, and set the properties on that instance.
This is the one case where the Java object being configured does not necessarily
need an empty constructor.

================================ Overview ======================================

The XmlBean utility class starts with the "root" object for configuring. Each
element of the XML that is a child of the root tag represents the name of the
JavaBean property to set.  A property may consist of either a getXXXX/setXXXXX
method and/or the actual field of the class itself.  The XmlBean class can be
enabled to directly access and get/set the value of a class member without
actually requiring a public getXXXXX/setXXXXX method.  By default, the XmlBean
will attempt to prefer the public getXXXXX/setXXXXX method first and fallback
to directly setting/getting the class member.

The naming of properties is relatively strict.  For example, assume the following
is the Java class:

  public class Host {
    public int id;
    private String name;
    private String ip;
    public String comment;
  }

The following is the XML document that will be used with the XmlBean class.

  <configuration>
    <id>1</id>
    <name>www.twitter.com</name>
    <ip>10.1.1.1</ip>
  </configuration>

The following Java code would be used to not only create a new Host object, but
also configure it with those values.
 
  File xmlFile = ...; 
  Host host = XmlBeanFactory.create(xmlFile, Host.class);

Rather than delegating the creation to the XmlBeanFactory, there is another
option of passing an instance of a Host object you want to configure.  This
allows you to set the values within your Java code and only have the XML file
override or set new values.

  File xmlFile = ...; 
  Host host = new Host();
  host.id = 2;
  host.comment = "test comment";
  XmlBeanFactory.configure(xmlFile, host);

After running this example, the value of host.id would be 1.  The value of
host.comment would still be "test comment" since it wasn't set in the XML.

If you wanted to make the private fields accessible to your other Java code 
you'd usually add a JavaBean setter and getter.  Let's change the example:

  public class Host {
    private int id;
    private String name;
    private String ip;
    private String comment;

    public void setId(int id) {
      this.id = id;
    }

    public int getId() {
      return this.id;
    }
  }

The XmlBean library would still be able to set the private fields name, ip,
and comment.  In the case where an "id" is set, the library will prefer
using the "setId" setter method.

=========================== Java Object Graphs =================================

While the previous examples used simple Java types such as a String or an int,
complicated Java objects that follow the JavaBean pattern are also supported.
Only classes that have an empty constructor (no arguments) can be supported
for creation by the XmlBean library.

================ Typing and Collections/Maps in version 2.x ====================

In version 1.x of the library, objects that need to be created would by default
be created using the class of the property.  This limitation prevents creating
concrete subclasses to be used in lieu of the actual class of the property.
In many situations, this leads to awkward workarounds such as the following:

  <customHandler>com.cloudhopper.integration.pario.ParioSmppHandler</customHandler>
  <custom>
    <carrierIdLength>4</carrierIdLength>
    <operatorMap>
      <stratusOperatorId>1</stratusOperatorId>
      <parioCarrierId>6015</parioCarrierId>
    </operatorMap>
    <operatorMap>
      <stratusOperatorId>2</stratusOperatorId>
      <parioCarrierId>6016</parioCarrierId>
    </operatorMap>
  </custom>

As the SAX processing of the XML is performed, on the root object being configured
the setCustomHandler() method will be called first.  The workaround is to create
the actual instance of the object for the field "custom".  When the custom field
is processed next in the SAX stream, the library will fetch the value of the
custom field rather than try to create a new instance.  This is how a specific
subclass can be created using version 1.x of this library.  The syntax works,
but isn't as clear as the alternative method in version 2.x of the library:

  <custom type="com.cloudhopper.integration.pario.ParioSmppHandler">
    <carrierIdLength>4</carrierIdLength>
    <operatorMap>
      <stratusOperatorId>1</stratusOperatorId>
      <parioCarrierId>6015</parioCarrierId>
    </operatorMap>
    <operatorMap>
      <stratusOperatorId>2</stratusOperatorId>
      <parioCarrierId>6016</parioCarrierId>
    </operatorMap>
  </custom>

The new Collections and Maps support in version 2.x can streamline this example
even further.  In version 1.x, the only way to support multiple elements that
used the same name was to define an "addOperatorMap()" method.  Version 1.x
of the library, would call "addOperatorMap()" two times on the "custom" object.
In version 2.x, the following XML syntax is more clear that they belong together
as part of the same map.  Furthermore, less code is required in Java, since the
map's key can be automatically used from a value of one of the properties of 
the value itself.  For example:

  <custom type="com.cloudhopper.integration.pario.ParioSmppHandler">
    <carrierIdLength>4</carrierIdLength>
    <operatorMappings value="operatorMap" key="stratusOperatorId">
      <operatorMap>
        <stratusOperatorId>1</stratusOperatorId>
        <parioCarrierId>6015</parioCarrierId>
      </operatorMap>
      <operatorMap>
        <stratusOperatorId>2</stratusOperatorId>
        <parioCarrierId>6016</parioCarrierId>
      </operatorMap>
    </operatorMappings>
  </custom>

In the Java object being configured, this can be accomplished with one field.

  TreeMap<Integer,OperatorMap> operatorMappings;

Version 2.x of the library also supports Java annotations to eliminate the
requirement of including "key" or "value" attributes for the collection or map
element (e.g. operatorMappings).  This can be accomplished as follows:

  @XmlBeanProperty(value="operatorMap", key="stratusOperatorId")
  TreeMap<Integer,OperatorMap> operatorMappings;
