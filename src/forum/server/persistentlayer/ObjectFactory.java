//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.08 at 10:55:34 PM IST 
//


package forum.server.persistentlayer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the persistentlayer package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Forum_QNAME = new QName("", "Forum");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: persistentlayer
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NamedComponent }
     * 
     */
    public NamedComponent createNamedComponent() {
        return new NamedComponent();
    }

    /**
     * Create an instance of {@link ForumType }
     * 
     */
    public ForumType createForumType() {
        return new ForumType();
    }

    /**
     * Create an instance of {@link ForumSubject }
     * 
     */
    public ForumSubject createForumSubject() {
        return new ForumSubject();
    }

    /**
     * Create an instance of {@link ForumUser }
     * 
     */
    public ForumUser createForumUser() {
        return new ForumUser();
    }

    /**
     * Create an instance of {@link ForumMessage }
     * 
     */
    public ForumMessage createForumMessage() {
        return new ForumMessage();
    }

    /**
     * Create an instance of {@link ForumThread }
     * 
     */
    public ForumThread createForumThread() {
        return new ForumThread();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForumType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Forum")
    public JAXBElement<ForumType> createForum(ForumType value) {
        return new JAXBElement<ForumType>(_Forum_QNAME, ForumType.class, null, value);
    }

}
