//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.16 at 01:31:32 AM IST 
//


package forum.server.persistentlayer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="subjectID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="subSubjects" type="{}SubjectType"/>
 *         &lt;/sequence>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="subThreads" type="{}ThreadType"/>
 *         &lt;/sequence>
 *         &lt;element name="numOfThreads">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="lastAddedMessage" type="{}MessageType" minOccurs="0"/>
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
    "subSubjects",
    "subThreads",
    "numOfThreads",
    "lastAddedMessage"
})
public class SubjectType
    extends NamedComponentType
{

    protected long subjectID;
    protected List<SubjectType> subSubjects;
    protected List<ThreadType> subThreads;
    protected int numOfThreads;
    protected MessageType lastAddedMessage;

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
     * Gets the value of the subSubjects property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subSubjects property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubSubjects().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubjectType }
     * 
     * 
     */
    public List<SubjectType> getSubSubjects() {
        if (subSubjects == null) {
            subSubjects = new ArrayList<SubjectType>();
        }
        return this.subSubjects;
    }

    /**
     * Gets the value of the subThreads property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subThreads property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubThreads().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ThreadType }
     * 
     * 
     */
    public List<ThreadType> getSubThreads() {
        if (subThreads == null) {
            subThreads = new ArrayList<ThreadType>();
        }
        return this.subThreads;
    }

    /**
     * Gets the value of the numOfThreads property.
     * 
     */
    public int getNumOfThreads() {
        return numOfThreads;
    }

    /**
     * Sets the value of the numOfThreads property.
     * 
     */
    public void setNumOfThreads(int value) {
        this.numOfThreads = value;
    }

    /**
     * Gets the value of the lastAddedMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageType }
     *     
     */
    public MessageType getLastAddedMessage() {
        return lastAddedMessage;
    }

    /**
     * Sets the value of the lastAddedMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageType }
     *     
     */
    public void setLastAddedMessage(MessageType value) {
        this.lastAddedMessage = value;
    }

}
