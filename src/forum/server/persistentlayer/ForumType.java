//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.08 at 10:55:34 PM IST 
//


package forum.server.persistentlayer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ForumType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ForumType">
 *   &lt;complexContent>
 *     &lt;extension base="{}NamedComponent">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="numOfConnected">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="forumSubjects" type="{}ForumSubject"/>
 *         &lt;/sequence>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="registeredUsers" type="{}ForumUser"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ForumType", propOrder = {
    "numOfConnectedAndForumSubjectsAndRegisteredUsers"
})
public class ForumType
    extends NamedComponent
{

    @XmlElements({
        @XmlElement(name = "registeredUsers", type = ForumUser.class),
        @XmlElement(name = "numOfConnected", type = Integer.class),
        @XmlElement(name = "forumSubjects", type = ForumSubject.class)
    })
    protected List<Object> numOfConnectedAndForumSubjectsAndRegisteredUsers;

    /**
     * Gets the value of the numOfConnectedAndForumSubjectsAndRegisteredUsers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the numOfConnectedAndForumSubjectsAndRegisteredUsers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNumOfConnectedAndForumSubjectsAndRegisteredUsers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ForumUser }
     * {@link Integer }
     * {@link ForumSubject }
     * 
     * 
     */
    public List<Object> getNumOfConnectedAndForumSubjectsAndRegisteredUsers() {
        if (numOfConnectedAndForumSubjectsAndRegisteredUsers == null) {
            numOfConnectedAndForumSubjectsAndRegisteredUsers = new ArrayList<Object>();
        }
        return this.numOfConnectedAndForumSubjectsAndRegisteredUsers;
    }

}
