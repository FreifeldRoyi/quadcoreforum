/**
 * 
 */
package forum.swingclient.panels;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

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
	private long selectedID;

	private ButtonGroup btnGrp_searchOption;
	private JRadioButton radBtn_author;
	private JRadioButton radBtn_content;
	private JPanel pnl_searchOptionHolder;

	private JTable resultsTable;
	private ForumTableModel resultsTableModel;

	private JTextField txtFld_searchField;
	private String toSearch;
	private String searchBy;
	private JLabel lbl_searchDescription;
	private JButton btn_search;
	private JButton btn_cancel;

	private JPanel pnl_results;

	private String[] searchResultsContent;
	private long[] messagesIDs;
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
		if (encodedView.startsWith("searchnotmessages")) {
			
			
//			this.setMinimumSize(new Dimension(555, 235));
//			this.setSize(new Dimension(555, 235));

			pnl_results.setVisible(false);
			this.setSize(new Dimension(600, 500));

			JOptionPane.showMessageDialog(this, "No messages were found", "empty", JOptionPane.INFORMATION_MESSAGE);
		}
		else {

			this.resultsTable.setVisible(false);
			this.resultsTableModel.clearData();

			System.out.println("eeeeeeeeeeeeencoded = " + encodedView);

			searchResultsContent = encodedView.split("\n\tARESULTMESSAGE: ");

			System.out.println("splitted:");
			for (int i = 0; i < searchResultsContent.length; i++)
				System.out.println("splitted[" + i + "] = " + searchResultsContent[i]);

			// remove all the previous listeners
			for (ActionListener tAL : btn_nextPage.getActionListeners())
				btn_nextPage.removeActionListener(tAL);
			for (ActionListener tAL : btn_prevPage.getActionListeners())
				btn_prevPage.removeActionListener(tAL);


			messagesIDs = new long[searchResultsContent.length - 1];

			String[][] tResultsTable = new String[Math.min(searchResultsContent.length - 1, this.selectedNumberOfResults)][3];
			for (index = 0; index < searchResultsContent.length - 1 && index < this.selectedNumberOfResults; index++) {
				messagesIDs[index] = Long.parseLong(searchResultsContent[index + 1].
						substring(0, searchResultsContent[index + 1].indexOf('\t')));
				String[] tCurrRes = searchResultsContent[index + 1].
				substring(searchResultsContent[index + 1].indexOf('\t') + 1).split("\t");
				System.out.println(Arrays.toString(tCurrRes));
				if (tCurrRes.length > 3) {
					for (int j = 3; j < tCurrRes.length; j++) {
						tCurrRes[2] += ("\t" + tCurrRes[j]);
					}
				}
				tResultsTable[index][0] = tCurrRes[0];
				tResultsTable[index][1] = tCurrRes[1];
				tResultsTable[index][2] = tCurrRes[2];
			}


			/*			
			System.out.println("tMessagesIDs:");
			for (int i = 0; i < tMessagesIDs.length; i++)
				System.out.println("tMessagesIDs[" + i + "] = " + tMessagesIDs[i]);


			System.out.println("resultssssssssssssssssssssssss:");
			for (int i = 0; i < tResultsTable.length; i++)
				for (int j = 0; j < tResultsTable[i].length; j++)
					System.out.println("tResultsTable[" + i + "][" + j + "] = " + tResultsTable[i][j]);
			 */
			resultsTable.setVisible(false);
			resultsTableModel.clearData();
			resultsTableModel.updateData(messagesIDs, tResultsTable);
			resultsTableModel.fireTableDataChanged();
			resultsTable.setVisible(true);










			this.currentPageResNum = index;

			if (index < searchResultsContent.length - 1)
				btn_nextPage.setEnabled(true);
			else
				btn_nextPage.setEnabled(false);
			btn_prevPage.setEnabled(false);

			btn_nextPage.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					resultsTable.setVisible(false);
					resultsTableModel.clearData();
					int tFinalIndex = index + selectedNumberOfResults;
					String[][] tResultsTable = new String[Math.min(searchResultsContent.length - 1 - index, SearchDialog.this.selectedNumberOfResults)][3];
					for (int i = 0; index < searchResultsContent.length - 1 && index < tFinalIndex; i++, index++) {
						messagesIDs[index] = Long.parseLong(searchResultsContent[index + 1].
								substring(0, searchResultsContent[index + 1].indexOf('\t')));
						String[] tCurrRes = searchResultsContent[index + 1].
						substring(searchResultsContent[index + 1].indexOf('\t') + 1).split("\t");
						System.out.println(Arrays.toString(tCurrRes));
						if (tCurrRes.length > 3) {
							for (int j = 3; j < tCurrRes.length; j++) {
								tCurrRes[2] += ("\t" + tCurrRes[j]);
							}
						}
						tResultsTable[i][0] = tCurrRes[0];
						tResultsTable[i][1] = tCurrRes[1];
						tResultsTable[i][2] = tCurrRes[2];
					}

					resultsTableModel.updateData(messagesIDs, tResultsTable);
					resultsTableModel.fireTableDataChanged();
					resultsTable.setVisible(true);

					currentPageResNum = index % selectedNumberOfResults == 0? selectedNumberOfResults : index % selectedNumberOfResults; 
					System.out.println("\n\nindex = " + index);
					System.out.println("\n\nsearchResultsContent = " + searchResultsContent.length);
					if (index < searchResultsContent.length - 1)
						btn_nextPage.setEnabled(true);
					else
						btn_nextPage.setEnabled(false);
					btn_prevPage.setEnabled(true);
				}
			});


			btn_prevPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resultsTable.setVisible(false);
					resultsTableModel.clearData();
					int tFinalIndex = index - currentPageResNum;
					index = tFinalIndex - selectedNumberOfResults;
					String[][] tResultsTable = new String[Math.min(searchResultsContent.length - 1 - index, SearchDialog.this.selectedNumberOfResults)][3];
					for (int i = 0; index < tFinalIndex; i++, index++) {
						messagesIDs[index] = Long.parseLong(searchResultsContent[index + 1].
								substring(0, searchResultsContent[index + 1].indexOf('\t')));
						String[] tCurrRes = searchResultsContent[index + 1].
						substring(searchResultsContent[index + 1].indexOf('\t') + 1).split("\t");
						System.out.println(Arrays.toString(tCurrRes));
						if (tCurrRes.length > 3) {
							for (int j = 3; j < tCurrRes.length; j++) {
								tCurrRes[2] += ("\t" + tCurrRes[j]);
							}
						}
						tResultsTable[i][0] = tCurrRes[0];
						tResultsTable[i][1] = tCurrRes[1];
						tResultsTable[i][2] = tCurrRes[2];
					}

					resultsTableModel.updateData(messagesIDs, tResultsTable);
					resultsTableModel.fireTableDataChanged();
					resultsTable.setVisible(true);

					currentPageResNum = selectedNumberOfResults;
					System.out.println("\n\nindex = " + index);
					System.out.println("\n\nsearchResultsContent = " + searchResultsContent.length);

					if (index > selectedNumberOfResults)
						btn_prevPage.setEnabled(true);
					else
						btn_prevPage.setEnabled(false);
					btn_nextPage.setEnabled(true);
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
	
	public long getSelectedID() {
		return this.selectedID;
	}

	private void initComponents()
	{
	//	this.setMinimumSize(new Dimension(600, 100));
		this.setResizable(false);
		this.selectedID = -1;
		this.toSearch = "";
		this.searchBy = "";
		this.resultsTable = new JTable();
		this.resultsTable.setSelectionModel(new DefaultListSelectionModel());
		this.resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.resultsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (e.getClickCount() == 2) {
					int rowSelected = resultsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1)
						selectedID = resultsTableModel.getIDofContentInRow(rowSelected);
					controller.deleteObserver(SearchDialog.this);
					setVisible(false);
				}
			}
		});
		String[] columns = {"Author", "Title", "Content" };
		resultsTableModel = new ForumTableModel(columns);
		this.resultsTable.setModel(resultsTableModel);


		this.btn_nextPage = new JButton("next");
		this.btn_prevPage = new JButton("prev");


		this.btn_nextPage.setPreferredSize(new Dimension(100, 40));
		this.btn_prevPage.setPreferredSize(new Dimension(100, 40));

		this.resultsTable.setBorder(BorderFactory.createLineBorder(Color.black));

		JScrollPane tScroll = new JScrollPane(this.resultsTable);

		tScroll.setPreferredSize(new Dimension(224, 100));
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
				System.out.println("toSearch = " + toSearch + " field = " + txtFld_searchField.getText());
				if (pnl_results.isVisible())// && toSearch.equals(txtFld_searchField.getText()))
					search();
			}
		});

		this.radBtn_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 10;
				if (pnl_results.isVisible())// && toSearch.equals(txtFld_searchField.getText()))
					search();
			}
		});

		this.radBtn_15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 15;
				if (pnl_results.isVisible())// && toSearch.equals(txtFld_searchField.getText()))
					search();
			}
		});

		this.radBtn_20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 20;
				if (pnl_results.isVisible())// && toSearch.equals(txtFld_searchField.getText()))
					search();
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

		this.txtFld_searchField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					btn_search.doClick();
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});


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
		this.btn_search.setPreferredSize(new Dimension(100, 40));


		this.btn_cancel = new JButton("Cancel");

		this.btn_cancel.setPreferredSize(new Dimension(100, 40));


		GroupLayout tLayout = new GroupLayout(this.getContentPane());

		tLayout.setHorizontalGroup(tLayout.createParallelGroup(Alignment.CENTER)
				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(this.lbl_searchDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10)
						.addComponent(this.txtFld_searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10))
						.addGap(20, 20, 20)
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
												.addGap(20, 20, 20)
												.addGroup(tLayout.createSequentialGroup()
														.addGap(0, 0, Short.MAX_VALUE)
														.addComponent(this.btn_search, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
														.addGap(10, 10, 10)
														.addComponent(this.btn_cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
														.addGap(10, 10, 10)));

		//
		//	.addGroup(tLayout.createParallelGroup()

		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(tLayout.createParallelGroup()
								.addComponent(this.lbl_searchDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.txtFld_searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
											.addGap(10, 10, 10)
								.addComponent(this.pnl_searchOptionHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(10, 10, 10)

								.addComponent(this.pnl_resultRadBtnHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(10, 10, 10)

								.addComponent(this.pnl_results, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10)
						.addGroup(tLayout.createParallelGroup()
								.addComponent(this.btn_search, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btn_cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(10, 10, 10)));

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
					toSearch = txtFld_searchField.getText();
					if (radBtn_author.isSelected())
						searchBy = "Author";
					else
						searchBy = "Content";
					btn_search.setEnabled(false);
					search();
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
	private void search() {
		if (searchBy.equals("Author")) {
			resultsTable.setVisible(false);
			resultsTableModel.clearData();
			resultsTableModel.fireTableDataChanged();
			controller.searchByAuthor(btn_search, toSearch);
			resultsTable.setVisible(true);
		}

		else {
			resultsTable.setVisible(false);
			resultsTableModel.clearData();
			resultsTableModel.fireTableDataChanged();
			controller.searchByContent(btn_search, toSearch);
			resultsTable.setVisible(true);
		}
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
