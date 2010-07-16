/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.StyleConstants;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.ui.JForumTable;
import forum.swingclient.ui.events.GUIHandler;

/**
 * @author sepetnit
 *
 */
public class MembersDialog2 extends JDialog implements GUIHandler {

	private JForumTable resultsTable;
	private ControllerHandler controller;
	private boolean promotionAllowed;
	private JButton btn_promote_demote;
	

	public JDialog getMembersDialog(Component mainpanel, 
			boolean promotionAllowed) throws IOException{
		try {
			return new MembersDialog2(promotionAllowed);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(mainpanel, "Can't connect to the forum database",
					"error", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}
	
	@Override
	public void notifyError(String errorMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshForum(String encodedView) {
		// TODO Auto-generated method stub
		
	}
	
	public MembersDialog2(boolean promotionAllowed) throws IOException {
		this.promotionAllowed = promotionAllowed;
		this.btn_promote_demote = new JButton("promote to moderator");
		this.btn_promote_demote.setPreferredSize(new Dimension(200, 35));

		try {
			controller = ControllerHandlerFactory.getPipe();
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	
	private long selectedID;
	
	
	
	
	
	
	
	
	private void createMembersTable() {
		this.resultsTable = new JForumTable();
		this.resultsTable.setFocusable(false);
		this.resultsTable.setRowHeight(30);

		this.resultsTable.setFont(new Font("Tahoma", Font.BOLD, 13));

		this.resultsTable.setSelectionModel(new DefaultListSelectionModel());
		this.resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.resultsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
					int rowSelected = resultsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1)
						selectedID = resultsTableModel.getIDofContentInRow(rowSelected);
					controller.deleteObserver(SearchDialog.this);
					setVisible(false);
			}
		});
		String[] columns = {"Username", "Title", "Content", "Score"};
		resultsTableModel = new ForumTableModel(columns);
		this.resultsTable.setModel(resultsTableModel);
		
		this.resultsTable.setBorder(BorderFactory.createLineBorder(Color.black));
		
		TableCellRenderer tLeftAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_LEFT);
		TableCellRenderer tCenterAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_CENTER);
		
		this.resultsTable.setColumnRenderer(0, tLeftAlignmentRenderer);
		this.resultsTable.setColumnRenderer(1, tLeftAlignmentRenderer);
		this.resultsTable.setColumnRenderer(2, tLeftAlignmentRenderer);
		this.resultsTable.setColumnRenderer(3, tCenterAlignmentRenderer);

		// sets widths
		this.resultsTable.setColumnWidth(0, 20);
		this.resultsTable.setColumnWidth(1, 20);
		this.resultsTable.setColumnWidth(2, 20);
		this.resultsTable.setColumnWidth(3, 20);
		

		this.resultsTable.setSelectionOnMouseMotion(true);
		
	}



}
