package forum.swingclient.panels;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class JLinkButton extends JButton { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8296074202945080957L;
	
	private static final Color LINK_COLOR = Color.WHITE; 
	private static final Border LINK_BORDER = BorderFactory.createEmptyBorder(0, 0, 1, 0); 
	private static final Border HOVER_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, LINK_COLOR); 
	
	private class LinkMouseListener extends MouseAdapter { 
		private Border hoverBorder;
		
		public LinkMouseListener() {
			this.hoverBorder = HOVER_BORDER;
		}	
		
		public LinkMouseListener(Color c) {
			this.hoverBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, c); ;
		}	
		
		
		public void mouseEntered(MouseEvent e){ 
			((JComponent)e.getComponent()).setBorder(this.hoverBorder); 
		} 

		public void mouseReleased(MouseEvent e){ 
			((JComponent)e.getComponent()).setBorder(this.hoverBorder); 
		} 
		
		public void mouseExited(MouseEvent e){ 
			((JComponent)e.getComponent()).setBorder(LINK_BORDER); 
		} 
	};
	
	public JLinkButton(String text) {
		super(text);
		this.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.setBorder(LINK_BORDER); 
		this.setForeground(LINK_COLOR); 
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
		this.setFocusPainted(false); 
		this.setRequestFocusEnabled(false); 
		this.setContentAreaFilled(false);
		this.addMouseListener(new LinkMouseListener()); 
	}
	
	public JLinkButton(String text, Color color) {
		this(text);
		this.addMouseListener(new LinkMouseListener(color));
	}

}