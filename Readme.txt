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
