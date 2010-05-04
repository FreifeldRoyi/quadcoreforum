
package forum.server.updatedpersistentlayer;

import java.util.ArrayList;
import java.util.List;



public class SubjectType
    
{

    protected long subjectID;
    protected List<Long> subSubjectsIDs;
 
    protected List<Long> threadsIDs;
    protected Long lastAddedMessageID;
    protected boolean isTopLevel;
  
    protected String name;
    protected String description;
  
    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

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

    /**
     * Gets the value of the isToLevel property.
     * 
     */
    public boolean isIsTopLevel() {
        return isTopLevel;
    }

    /**
     * Sets the value of the isToLevel property.
     * 
     */
    public void setIsTopLevel(boolean value) {
        this.isTopLevel = value;
    }

}
