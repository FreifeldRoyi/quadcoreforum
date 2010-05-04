/**
 * 
 */
package forum.client.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

/**
 * @author sepetnit
 *
 */
public class JNavigatePanel extends JPanel {
	private Queue<JLinkButton> links;
	private final String arrow = " >> ";
	private final Font arrowFont = new Font("Tahoma", Font.BOLD, 10);

	public JNavigatePanel(JLinkButton first) {
		this.links = new LinkedList<JLinkButton>();
		this.insertLink(first);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.updateNavigateView();
	}

	private JLabel createArrowLabel() {
		JLabel tArrow = new JLabel(arrow);
		tArrow.setFont(arrowFont);
		return tArrow;	
	}
	private void updateNavigateView() {
		this.removeAll();
		this.setVisible(false);

		Iterator<JLinkButton> tIter = links.iterator();
		while (tIter.hasNext()) {			
			this.add(tIter.next());
			if (tIter.hasNext())
				this.add(createArrowLabel());
		}
		this.setVisible(true);
	}

	private void removeAllBeforeAction(JLinkButton button) {
		this.removeAllBeforeAction(button.getText());
	}
	
	public void removeAllBeforeAction(String text) {
		System.out.println("sss");
		Iterator<JLinkButton> tIter = links.iterator();
		while (tIter.hasNext() && !tIter.next().getText().equals(text)) {}
		while (tIter.hasNext()) {
			tIter.next();
			tIter.remove();
			System.out.println("removeing");
		}
		System.out.println(links.size());

		this.updateNavigateView();
	}	

	public void insertLink(final JLinkButton link) {
		link.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeAllBeforeAction(link);
			}
		});
		this.links.add(link);
		if (this.links.size() > 1) // this is not the first link
			this.add(createArrowLabel());
		this.add(link);
	}

	public void insertLink(String text, ActionListener listener) {
		JLinkButton toAdd = new JLinkButton(text);
		toAdd.addActionListener(listener);
		this.insertLink(toAdd);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JNavigatePanel test = new JNavigatePanel(new JLinkButton("hello"));
		test.setSize(400, 100);
		test.insertLink(new JLinkButton("ARE"));
		test.insertLink(new JLinkButton("WE"));
		test.insertLink(new JLinkButton("WEE"));

		f.getContentPane().add(test);
		f.setSize(400, 100);
		f.pack();
		f.setVisible(true);
	}
}
