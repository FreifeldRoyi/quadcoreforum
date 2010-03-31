//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.01 at 12:42:21 AM IDT 
//


package forum.server.persistentlayer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SubjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{}NamedComponentType">
 *       &lt;sequence>
 *         &lt;element name="subjectID" type="{}NonNegativeLong"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="subSubjectsIDs" type="{}NonNegativeLong"/>
 *         &lt;/sequence>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="threadsIDs" type="{}NonNegativeLong"/>
 *         &lt;/sequence>
 *         &lt;element name="lastAddedMessageID" type="{}NonNegativeLong" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubjectType", propOrder = {
    "subjectID",
    "subSubjectsIDs",
    "threadsIDs",
    "lastAddedMessageID"
})
public class SubjectType
    extends NamedComponentType
{

    protected long subjectID;
    @XmlElement(type = Long.class)
    protected List<Long> subSubjectsIDs;
    @XmlElement(type = Long.class)
    protected List<Long> threadsIDs;
    protected Long lastAddedMessageID;

    /**
     * Gets the value of the subjectID property.
     * 
     */
    public long getSubjectID() {
        return subjectID;
    }

    /**
     * Sets the value of the subjectID property.
     * 
     */
    public void setSubjectID(long value) {
        this.subjectID = value;
    }

    /**
     * Gets the value of the subSubjectsIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subSubjectsIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubSubjectsIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getSubSubjectsIDs() {
        if (subSubjectsIDs == null) {
            subSubjectsIDs = new ArrayList<Long>();
        }
        return this.subSubjectsIDs;
    }

    /**
     * Gets the value of the threadsIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the threadsIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getThreadsIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getThreadsIDs() {
        if (threadsIDs == null) {
            threadsIDs = new ArrayList<Long>();
        }
        return this.threadsIDs;
    }

    /**
     * Gets the value of the lastAddedMessageID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLastAddedMessageID() {
        return lastAddedMessageID;
    }

    /**
     * Sets the value of the lastAddedMessageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLastAddedMessageID(Long value) {
        this.lastAddedMessageID = value;
    }

}
