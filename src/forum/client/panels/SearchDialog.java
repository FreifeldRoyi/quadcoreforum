/**
 * 
 */
package forum.client.panels;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import com.sun.org.apache.bcel.internal.generic.NEW;

import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author Royi Freifeld
 *
 */
public class SearchDialog extends JDialog implements GUIHandler 
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

	private ButtonGroup btnGrp_searchOption;
	private JRadioButton radBtn_author;
	private JRadioButton radBtn_content;
	private JPanel pnl_searchOptionHolder;
	
	private JTable resultsTable;
	private TableModel resultsTableModel;

	
	private JTextField txtFld_searchField;
	private JLabel lbl_searchDescription;
	private JButton btn_search;
	private JButton btn_cancel;

	private JPanel pnl_results;
	private JTextArea txt_area_resultsView;
	private String[] searchResultsContent;	
	private JButton btn_nextPage;
	private JButton btn_prevPage;
	private int index;	
	private int currentPageResNum;	

	private int selectedNumberOfResults;
	
	private ControllerHandler controller;

	public SearchDialog()
	{
		this.initComponents();
	}

	/** 
	 * @see forum.client.ui.events.GUIHandler#notifyError(java.lang.String)
	 */
	public void notifyError(String errorMessage) 
	{
		System.out.println(errorMessage);
	}

	/**
	 * @see forum.client.ui.events.GUIHandler#refreshForum(java.lang.String)
	 */
	public void refreshForum(String encodedView) {
		System.out.println("got refresh search");
		this.txt_area_resultsView.setText("");
		if (encodedView.startsWith("searchnotmessages"))
			JOptionPane.showMessageDialog(this, "No messages were found", "empty", JOptionPane.INFORMATION_MESSAGE);
		else {			
//			this.setVisible(false);

			
/* ******************************************************/			
			this.resultsTable.setVisible(false);
			this.resultsTableModel.clearData();
			
/* ******************************************************/
			
			System.out.println("eeeeeeeeeeeeencoded = " + encodedView);
			
			searchResultsContent = encodedView.split("\n");
			// remove all the previous listeners
			for (ActionListener tAL : btn_nextPage.getActionListeners())
				btn_nextPage.removeActionListener(tAL);
			for (ActionListener tAL : btn_prevPage.getActionListeners())
				btn_prevPage.removeActionListener(tAL);


/*

			// this is the data which will be presented in the subjects table
			String[][] tData = new String[searchResultsContent.length][5];
			// this is the IDs array which should contain the presented subjects' IDs
			long[] tIDs = new long[searchResultsContent.length];
			for (int i = 0; i < searchResultsContent.length; i++) {
				String[] tCurrentSubjectInfo = searchResultsContent[i].split("\t");
				// this is the subject's id
				try {
					tIDs[i] = Long.parseLong(tCurrentSubjectInfo[0]);
					for (int j = 1; j < tCurrentSubjectInfo.length; j++)
						tData[i][j - 1] = tCurrentSubjectInfo[j];
					this.resultsTableModel.updateData(tIDs, tData);


				}
				catch (NumberFormatException e) {
					SystemLogger.warning("The server response related to subject's update is invalid");
					//						this.showingSubjectsOfName = "";
					//						this.showingSubjectsOfID = -1;
					this.resultsTableModel.clearData();
					break;
				}
			}

			resultsTableModel.fireTableDataChanged();
			this.resultsTable.setVisible(true);
			/*			if (showingSubjectsOfID > -1)
				container.switchToSubjectsAndThreadsView();
			else {
				container.switchToRootSubjectsView();
				container.stopWorkingAnimation();
			}
			 */





			for (index = 0; index < searchResultsContent.length && index < selectedNumberOfResults; index++)
				this.txt_area_resultsView.setText(txt_area_resultsView.getText() + searchResultsContent[index] + "\n");

			this.currentPageResNum = index;

			if (index < searchResultsContent.length)
				btn_nextPage.setEnabled(true);
			else
				btn_nextPage.setEnabled(false);
			btn_prevPage.setEnabled(false);

			btn_nextPage.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					txt_area_resultsView.setText("");
					int tFinalIndex = index + selectedNumberOfResults;
					while (index < searchResultsContent.length && index < tFinalIndex) {
						txt_area_resultsView.setText(txt_area_resultsView.getText() + searchResultsContent[index] + "\n");
						index++;
					}
					currentPageResNum = index % selectedNumberOfResults == 0? selectedNumberOfResults : index % selectedNumberOfResults; 
					
					if (index < searchResultsContent.length)
						btn_nextPage.setEnabled(true);
					else
						btn_nextPage.setEnabled(false);
					btn_prevPage.setEnabled(true);
				}
			});


			btn_prevPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					txt_area_resultsView.setText("");
					
					
					int tFinalIndex = index - currentPageResNum;
					index = tFinalIndex - selectedNumberOfResults;
					while (index < tFinalIndex) {
						txt_area_resultsView.setText(txt_area_resultsView.getText() + searchResultsContent[index] + "\n");
						index++;
					}
					currentPageResNum = selectedNumberOfResults; 
				
					
					btn_nextPage.setEnabled(true);
					
					if (index > selectedNumberOfResults)
						btn_prevPage.setEnabled(true);
					else
						btn_prevPage.setEnabled(false);
				}		
					
							});	

			this.pnl_results.setVisible(true);
			this.setMinimumSize(new Dimension(600, 500));
			System.out.println("end search updating in loop...");

			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
			int YY = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
			this.setLocation(X, YY);

//			this.setVisible(true);
			System.out.println("visible true ...");

		}
		System.out.println("end search updating ...");

	}

	private void initComponents()
	{

		this.resultsTable = new JTable();
		this.resultsTable.setSelectionModel(new DefaultListSelectionModel());
		this.resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.resultsTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (e.getClickCount() == 2) {
					int rowSelected = resultsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {
						resultsTable.setVisible(false);
//						threadsPanel.changeTableVisible();
//						threadsPanel.setVisible(true);
//						showingSubjectsOfName = resultsTableModel.getNameOfSubjectInRow(rowSelected) ;
//						container.startWorkingAnimation("retreiving subject " + 
//								showingSubjectsOfName
//								+ " content...");

						final long subjectToLoad = resultsTableModel.getIDofSubjectInRow(rowSelected);
/*						showingSubjectsOfID = subjectToLoad;
						try {
							ControllerHandlerFactory.getPipe().getSubjects(subjectToLoad, container);

							ControllerHandlerFactory.getPipe().getThreads(subjectToLoad, container);

							container.addToNavigate(showingSubjectsOfName, linkPressListener());

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
*/					}
				}
			}
		});
		String[] columns = {"Message ID", "Author", "Title", "Content" };
		resultsTableModel = new TableModel(columns);
		this.resultsTable.setModel(resultsTableModel);
		
		
		this.btn_nextPage = new JButton("next");
		this.btn_prevPage = new JButton("prev");

		
		this.txt_area_resultsView = new JTextArea();
		
		
		
		this.btn_nextPage.setPreferredSize(new Dimension(85, 35));
		this.btn_prevPage.setPreferredSize(new Dimension(85, 35));
		
		this.txt_area_resultsView.setPreferredSize(new Dimension(224, 39));
		this.txt_area_resultsView.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JScrollPane tScroll = new JScrollPane(this.txt_area_resultsView);
		
/* **********************************************************************/
		
//		JScrollPane tScroll = new JScrollPane(this.resultsTable);
		
/* **********************************************************************/	
		
		this.pnl_results = new JPanel();
		this.pnl_results.setBorder(BorderFactory.createTitledBorder("Search results"));
		
		
		
		GroupLayout tResultsLayout = new GroupLayout(this.pnl_results);
		tResultsLayout.setHorizontalGroup(tResultsLayout.createParallelGroup()
				.addGroup(tResultsLayout.createSequentialGroup()
						.addContainerGap()
						
						.addComponent(tScroll, GroupLayout.PREFERRED_SIZE, 
								GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addContainerGap())
								.addGroup(tResultsLayout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(this.btn_prevPage, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addGap(10, 10, 10)
												.addComponent(this.btn_nextPage, GroupLayout.PREFERRED_SIZE, 
														GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
														.addContainerGap()));

		tResultsLayout.setVerticalGroup(tResultsLayout.createSequentialGroup()
				.addComponent(tScroll, GroupLayout.PREFERRED_SIZE, 
						GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10)

						.addGroup(tResultsLayout.createParallelGroup()
								.addComponent(this.btn_prevPage, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.btn_nextPage, GroupLayout.PREFERRED_SIZE, 
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addContainerGap());
		this.pnl_results.setLayout(tResultsLayout);



		/* number of search results handling */
		this.btnGrp_numberOfResults = new ButtonGroup();;
		
		
		this.radBtn_5 = new JRadioButton("5 results per page");
		this.radBtn_10 = new JRadioButton("10 results per page");
		this.radBtn_15 = new JRadioButton("15 results per page");
		this.radBtn_20 = new JRadioButton("20 results per page");

		
		this.radBtn_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 5;
			}
		});

		this.radBtn_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 10;
			}
		});

		this.radBtn_15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 15;
			}
		});

		this.radBtn_20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 20;
			}
		});

		
		this.pnl_resultRadBtnHolder = new JPanel(); 
		BoxLayout bl = new BoxLayout(this.pnl_resultRadBtnHolder, BoxLayout.X_AXIS);
		this.pnl_resultRadBtnHolder.setLayout(bl);

		this.btnGrp_numberOfResults.add(this.radBtn_5);
		this.btnGrp_numberOfResults.add(this.radBtn_10);
		this.btnGrp_numberOfResults.add(this.radBtn_15);
		this.btnGrp_numberOfResults.add(this.radBtn_20);

/*		this.radBtn_5.setToolTipText("Show 5 results per page");
		this.radBtn_10.setToolTipText("Show 10 results per page");
		this.radBtn_15.setToolTipText("Show 15 results per page");
		this.radBtn_20.setToolTipText("Show 20 results per page");
*/
		this.radBtn_10.setSelected(true);

		this.pnl_resultRadBtnHolder.setBorder(BorderFactory.createTitledBorder("Results per page"));
		this.pnl_resultRadBtnHolder.add(this.radBtn_5);
		this.pnl_resultRadBtnHolder.add(this.radBtn_10);
		this.pnl_resultRadBtnHolder.add(this.radBtn_15);		
		this.pnl_resultRadBtnHolder.add(this.radBtn_20);

		
		
		
		
		
		
		/* search type option handling */
		this.btnGrp_searchOption = new ButtonGroup();
		this.radBtn_author = new JRadioButton("Search By Author");
		this.radBtn_content = new JRadioButton("Search By Content");

		this.btnGrp_searchOption.add(this.radBtn_author);
		this.btnGrp_searchOption.add(this.radBtn_content);

		this.pnl_searchOptionHolder = new JPanel(new FlowLayout());

		this.pnl_searchOptionHolder.setLayout(new BoxLayout(this.pnl_searchOptionHolder, BoxLayout.X_AXIS));
		this.pnl_searchOptionHolder.setBorder(BorderFactory.createTitledBorder("Search option"));

		this.pnl_searchOptionHolder.add(this.radBtn_author);
		this.pnl_searchOptionHolder.add(this.radBtn_content);

		/* searching area handling */
		this.txtFld_searchField = new JTextField("enter search phrase here");

		this.txtFld_searchField.setPreferredSize(new Dimension(200, 30));
		this.txtFld_searchField.setForeground(Color.GRAY);


		this.txtFld_searchField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				if (txtFld_searchField.getText().equals("enter search phrase here")) {
					txtFld_searchField.setForeground(Color.black);
					txtFld_searchField.setText("");
				}
			}
			public void focusLost(FocusEvent arg0) {
				if (txtFld_searchField.getText().isEmpty())	{
					txtFld_searchField.setForeground(Color.gray);
					txtFld_searchField.setText("enter search phrase here");
				}
			}

		});

		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {
				if (txtFld_searchField.getText().isEmpty()) {
					txtFld_searchField.setForeground(Color.gray);
					txtFld_searchField.setText("enter search phrase here");
					btn_search.requestFocus();
				}
			}
		});

		this.lbl_searchDescription = new JLabel();

		this.lbl_searchDescription.setPreferredSize(new Dimension(175, 30));



		this.btn_search = new JButton("Search");
		this.btn_search.setPreferredSize(new Dimension(85, 35));




		this.btn_cancel = new JButton("Cancel");

		this.btn_cancel.setPreferredSize(new Dimension(85, 35));





		GroupLayout tLayout = new GroupLayout(this.getContentPane());

		tLayout.setHorizontalGroup(tLayout.createParallelGroup(Alignment.CENTER)
				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(this.lbl_searchDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10)
						.addComponent(this.txtFld_searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10))
						.addGap(10, 10, 10)
						.addGroup(tLayout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addComponent(this.pnl_searchOptionHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addGap(10, 10, 10))
								.addGroup(tLayout.createSequentialGroup()
										.addGap(10, 10, 10)

										.addComponent(this.pnl_results, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
										.addGap(10, 10, 10))



										.addGroup(tLayout.createSequentialGroup()
												.addGap(10, 10, 10)
												.addComponent(this.pnl_resultRadBtnHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
												.addGap(10, 10, 10))
												.addGap(10, 10, 10)
												.addGroup(tLayout.createSequentialGroup()
														.addGap(0, 0, Short.MAX_VALUE)
														.addComponent(this.btn_search, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
														.addGap(30, 30, 30)
														.addComponent(this.btn_cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
														.addGap(10, 10, 10)));
		//								.addComponent(this.pnl_btnHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addGroup(tLayout.createParallelGroup()
						.addComponent(this.lbl_searchDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtFld_searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(this.pnl_searchOptionHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnl_resultRadBtnHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.pnl_results, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10)
						.addGroup(tLayout.createParallelGroup()
								.addComponent(this.btn_search, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btn_cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap()
		);
		//								.addComponent(this.pnl_btnHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		this.getContentPane().setLayout(tLayout);

		//		this.setLayout(this.layout);
		this.setTitle("Search");
		//		this.setBorder(BorderFactory.createTitledBorder("Search"));

		/*
		this.add(this.pnl_searchOptionHolder,BorderLayout.PAGE_START);
		this.add(this.pnl_searchArea, BorderLayout.CENTER);
		this.add(this.pnl_resultRadBtnHolder, BorderLayout.LINE_END);
		this.add(this.btn_search, BorderLayout.PAGE_END);
		 */
		//		this.setLBLText();

		this.setModal(true);
		
		this.pnl_results.setVisible(false);
		this.pack();
		this.btn_search.requestFocus();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int YY = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, YY);


		this.radBtn_author.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lbl_searchDescription.setText("Please enter a user's name : ");
//				lbl_searchDescription.setToolTipText("Enter a username to search by");
			}

		});

		this.radBtn_content.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				lbl_searchDescription.setText("Please enter a phrase : ");
/*				lbl_searchDescription.setToolTipText("Enter a phrase to search by.\n" +
						"It is possible to use logic operators such as 'AND' and 'OR'.\n" +
				"Logic operators are case sensative.");						
*/			}

		});
		this.radBtn_author.setSelected(true);

		this.radBtn_author.getActionListeners()[0].actionPerformed(new ActionEvent(this.radBtn_author, 0, ""));
		this.radBtn_10.getActionListeners()[0].actionPerformed(new ActionEvent(this.radBtn_10, 0, ""));		

		try {
			this.controller = ControllerHandlerFactory.getPipe();
			this.controller.addObserver(new GUIObserver(this), EventType.SEARCH_UPDATED);

			this.btn_search.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btn_search.setEnabled(false);
					if (radBtn_author.isSelected()) {
						
						controller.searchByAuthor(btn_search, txtFld_searchField.getText());
					}

					else {

						controller.searchByContent(btn_search, txtFld_searchField.getText());
					}
				}
			});
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		this.btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.deleteObserver(SearchDialog.this);
				dispose();
			}

		});


		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent arg0) {}

			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				btn_cancel.doClick();
			}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
			
		});
		

	}

/*	public void paint(Graphics g) {
		Dimension tDimension = this.getSize();
		if (!this.pnl_results.isVisible()) {
			if (tDimension.height > 224) {
				setVisible(false);
				setSize(new Dimension(tDimension.width, 224));
				setVisible(true);
			}
		}
		else
			if (tDimension.height < 400) {
				setVisible(false);
				setSize(new Dimension(tDimension.width, 400));
				setVisible(true);
			}			

		super.paint(g);
	}	*/
}

//TODO re-factor initComponents - split handling to small functions - much better!!!
