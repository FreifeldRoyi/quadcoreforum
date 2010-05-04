


package forum.server.updatedpersistentlayer;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class MessageType {

    protected long messageID;
    protected long authorID;
   
    protected String title;
  
    protected String content;
   
    protected List<Long> repliesIDs;
    protected GregorianCalendar postTime;
    protected long threadID; 

    /**
     * Gets the value of the messageID property.
     * 
     */
    public long getMessageID() {
        return messageID;
    }

    /**
     * Sets the value of the messageID property.
     * 
     */
    public void setMessageID(long value) {
        this.messageID = value;
    }
    /**
     * Gets the value of the threadID property.
     * 
     */
    public long getThreadID() {
        return threadID;
    }

    /**
     * Sets the value of the threadID property.
     * 
     */
    public void setThreadID(long value) {
        this.threadID = value;
    }

    /**
     * Gets the value of the author property.
     * 
     */
    public long getAuthorID() {
        return authorID;
    }

    /**
     * Sets the value of the author property.
     * 
     */
    public void setAuthorID(long value) {
        this.authorID = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the postTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public GregorianCalendar getPostTime() {
        return postTime;
    }

    /**
     * Sets the value of the postTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPostTime(GregorianCalendar value) {
        this.postTime = value;
    }

    /**
     * Gets the value of the repliesIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the repliesIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRepliesIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getRepliesIDs() {
        if (repliesIDs == null) {
            repliesIDs = new ArrayList<Long>();
        }
        return this.repliesIDs;
    }

}
