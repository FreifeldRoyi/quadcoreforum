/**
 * 
 */
package forum.client.panels;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import forum.client.ui.events.GUIHandler;

/**
 * @author Royi Freifeld
 *
 */
public class SearchPanel extends JPanel implements GUIHandler 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4418635670089090353L;
	
	//global components
	private ButtonGroup btnGrp_numberOfResults;
	private JRadioButton radBtn_5;
	private JRadioButton radBtn_10;
	private JRadioButton radBtn_15;
	private JRadioButton radBtn_20;
	private JPanel pnl_resultRadBtnHolder;	
	private JTextField txtFld_searchField;
	private JLabel lbl_searchDescription;
	
	public SearchPanel()
	{
		this.initComponents();
		
		
	}
	
	/** 
	 * @see forum.client.ui.events.GUIHandler#notifyError(java.lang.String)
	 */
	@Override
	public void notifyError(String errorMessage) 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see forum.client.ui.events.GUIHandler#refreshForum(java.lang.String)
	 */
	@Override
	public void refreshForum(String encodedView) 
	{
		// TODO Auto-generated method stub
	}
	
	private void initComponents()
	{
		this.btnGrp_numberOfResults = new ButtonGroup();;
		this.radBtn_5 = new JRadioButton();
		this.radBtn_10 = new JRadioButton();
		this.radBtn_15 = new JRadioButton();
		this.radBtn_20 = new JRadioButton();
		this.pnl_resultRadBtnHolder = new JPanel(); 	
		this.txtFld_searchField = new JTextField();
		this.lbl_searchDescription = new JLabel();
	}
}
