package forum.swingclient.panels;

import javax.swing.* ;  
import javax.swing.text.* ;  
import java.awt.* ;  

public class JRestrictedLengthTextField extends JTextField {  

	private static final String ALLOWED_CHARACTERS = "~!@#$^&*()_+=><?{}[].-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	
	
	private static final long serialVersionUID = 614647389750943872L;
	// Maximum number of characters allowed in this field  
	private int maximum ;  
	
	private boolean shouldLimitChars;
	private boolean spaceAllowed;
	
	/** 
	 *  Constructs a new empty LimitField with the specified number of columns and 
	 *  the specified maximum allowable characters. 
	 * 
	 *  @param columns - The number of columns to use to calculate the preferred width. 
	 *                   If columns is set to zero, the preferred width will be whatever 
	 *                   naturally results from the component implementation. 
	 *  @param max - The maximum number of characters allowed in this field. 
	 */ 
	public JRestrictedLengthTextField(int columns, int max, boolean spaceAllowed, boolean LimitChars) {
		super( columns );  
		this.spaceAllowed = spaceAllowed;
		maximum = max;
		shouldLimitChars = LimitChars;
	}  
	/** 
	 *  Constructs a new LimitField initialized with the specified text and columns and 
	 *  the specified maximum allowable characters. 
	 * 
	 *  @param text - The text to be displayed, or null. 
	 *  @param columns - The number of columns to use to calculate the preferred width. 
	 *                   If columns is set to zero, the preferred width will be whatever 
	 *                   naturally results from the component implementation. 
	 *  @param max - The maximum number of characters allowed in this field. 
	 */  
	public JRestrictedLengthTextField(String text, int columns, int max) {  
		super(text, columns);
		spaceAllowed = false;
		maximum = max;  
	}  
	/** 
	 *  Method creates the default implementation of the model to be used at construction 
	 *  if one isn't explicitly given. An instance of LimitDocument is returned. 
	 * 
	 *  @return The default model implementation. 
	 */  
	protected Document createDefaultModel() {
		return new LimitDocument();
	}  

	/** 
	 *  Class LimitDocument is used to customize a JTextField to allow only a certain number of. 
	 *  characters.  It will work even if text is pasted into the field from the clipboard 
	 *  or if it is altered via programmatic changes. 
	 */  
	private class LimitDocument extends PlainDocument {  
		private static final long serialVersionUID = -2470137650343918869L;

		public void insertString(int offs, String str, AttributeSet a)  
		throws BadLocationException {  
			StringBuffer buffer = new StringBuffer(getText(0, getLength()));  
			if (((buffer.length() + str.length()) <= maximum) && legal(str)) {  
				super.insertString(offs, str, a);  
			} 
			else {  
				Toolkit.getDefaultToolkit().beep();  
			}  
		}
		
		private boolean legal(String str) {
			if (!shouldLimitChars) {
				return true;
			}
			for (char toCheck : str.toCharArray())
				if (ALLOWED_CHARACTERS.indexOf(toCheck) != -1)
					continue;
				else if (spaceAllowed && Character.isSpaceChar(toCheck))
					continue;
				else return false;
			return true;
		}
	}
}