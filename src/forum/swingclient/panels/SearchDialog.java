/**
 * 
 */
package forum.swingclient.panels;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.StyleConstants;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.JForumTable;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author Royi Freifeld
 *
 */
public class SearchDialog extends JDialog implements GUIHandler, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4418635670089090353L;

	private static final int TABLE_LINES_NUMBER = 3;
	private static final int TABLE_ROW_HEIGHT = 17;

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

	private JForumTable resultsTable;
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

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			btn_search.doClick();
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.btn_cancel.doClick();
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	
	public SearchDialog()
	{
		this.initComponents();
		this.addKeyListener(this);
	}

	/** 
	 * @see forum.client.ui.events.GUIHandler#notifyError(java.lang.String)
	 */
	public void notifyError(String errorMessage) {
		//TODO: handle
	}

	
	private String[] splitSingleSearchLine(String line) {
		String[] tSplitted = line.split("\t");
		if (tSplitted.length > 6) {
			for (int j = 6; j < tSplitted.length; j++) {
				tSplitted[5] += ("\t" + tSplitted[j]);
			}
		}
		
		return tSplitted;
	}

	/**
	 * @see forum.client.ui.events.GUIHandler#refreshForum(java.lang.String)
	 */
	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("searchnotmessages")) {
			
			pnl_results.setVisible(false);
			setResizable(false);
			this.setMinimumSize(new Dimension(600, 255));
			this.setSize(new Dimension(600, 255));

			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
			int YY = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
			this.setLocation(X, YY);

			
			
			JOptionPane.showMessageDialog(this, "No messages were found", "empty", JOptionPane.INFORMATION_MESSAGE);

			this.txtFld_searchField.selectAll();
			this.txtFld_searchField.grabFocus();



		}
		else {

			this.resultsTable.setVisible(false);

			this.resultsTableModel.clearData();

			searchResultsContent = encodedView.split("\n\tARESULTMESSAGE: ");

			// remove all the previous listeners
			for (ActionListener tAL : btn_nextPage.getActionListeners())
				btn_nextPage.removeActionListener(tAL);
			for (ActionListener tAL : btn_prevPage.getActionListeners())
				btn_prevPage.removeActionListener(tAL);


			messagesIDs = new long[searchResultsContent.length - 1];

			
			String[][] tResultsTable = new String[Math.min(searchResultsContent.length - 1, this.selectedNumberOfResults)][4];
			for (index = 0; index < searchResultsContent.length - 1 && index < this.selectedNumberOfResults; index++) {
				String[] tCurrRes = this.splitSingleSearchLine(searchResultsContent[index + 1]);
				messagesIDs[index] = Long.parseLong(tCurrRes[2]);
				tResultsTable[index][0] = tCurrRes[1];
				tResultsTable[index][1] = tCurrRes[4];
				tResultsTable[index][2] = tCurrRes[5];
				tResultsTable[index][3] = Double.valueOf(tCurrRes[0]).floatValue() + "";
			}

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
					setResizable(false);
					setMinimumSize(new Dimension(600, 255));

					resultsTableModel.clearData();
					int tFinalIndex = index + selectedNumberOfResults;
					String[][] tResultsTable = new String[Math.min(searchResultsContent.length - 1 - index, SearchDialog.this.selectedNumberOfResults)][4];
					for (int i = 0; index < searchResultsContent.length - 1 && index < tFinalIndex; i++, index++) {

						
						String[] tCurrRes = splitSingleSearchLine(searchResultsContent[index + 1]);
						messagesIDs[index] = Long.parseLong(tCurrRes[2]);
						tResultsTable[i][0] = tCurrRes[1];
						tResultsTable[i][1] = tCurrRes[4];
						tResultsTable[i][2] = tCurrRes[5];
						tResultsTable[i][3] = Double.valueOf(tCurrRes[0]).floatValue() + "";
						
						
						
					}

					resultsTableModel.updateData(messagesIDs, tResultsTable);
					resultsTableModel.fireTableDataChanged();
					resultsTable.setVisible(true);
					setResizable(true);

					currentPageResNum = index % selectedNumberOfResults == 0? selectedNumberOfResults : index % selectedNumberOfResults; 
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
					setResizable(false);
					setMinimumSize(new Dimension(600, 255));

					resultsTableModel.clearData();
					int tFinalIndex = index - currentPageResNum;
					index = tFinalIndex - selectedNumberOfResults;
					String[][] tResultsTable = new String[Math.min(searchResultsContent.length - 1 - index, SearchDialog.this.selectedNumberOfResults)][4];
					for (int i = 0; index < tFinalIndex; i++, index++) {
						String[] tCurrRes = splitSingleSearchLine(searchResultsContent[index + 1]);
						messagesIDs[index] = Long.parseLong(tCurrRes[2]);
						tResultsTable[i][0] = tCurrRes[1];
						tResultsTable[i][1] = tCurrRes[4];
						tResultsTable[i][2] = tCurrRes[5];
						tResultsTable[i][3] = Double.valueOf(tCurrRes[0]).floatValue() + "";
					}

					resultsTableModel.updateData(messagesIDs, tResultsTable);
					resultsTableModel.fireTableDataChanged();
					resultsTable.setVisible(true);
					setResizable(true);

					currentPageResNum = selectedNumberOfResults;
					if (index > selectedNumberOfResults)
						btn_prevPage.setEnabled(true);
					else
						btn_prevPage.setEnabled(false);
					btn_nextPage.setEnabled(true);
				}		
			});	

			this.pnl_results.setVisible(true);
			setResizable(true);
			this.setSize(new Dimension(600, 600));
			this.setMinimumSize(new Dimension(600, 600));

			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
			int YY = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
			this.setLocation(X, YY);

		}

	}
	
	public long getSelectedID() {
		return this.selectedID;
	}

	
	private void createSearchResultsTable() {

		this.resultsTable = new JForumTable();
		this.resultsTable.setFocusable(false);
		this.resultsTable.setRowHeight(TABLE_ROW_HEIGHT * TABLE_LINES_NUMBER);

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
		String[] columns = {"Author", "Title", "Content", "Score"};
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
	
	
	private void initComponents() {
		this.setResizable(false);
		this.setMinimumSize(new Dimension(600, 255));

		
		this.selectedID = -1;
		this.toSearch = "";
		this.searchBy = "";
		
		this.createSearchResultsTable();

		this.btn_nextPage = new JButton("next");
		this.btn_prevPage = new JButton("prev");


		this.btn_nextPage.setPreferredSize(new Dimension(100, 40));
		this.btn_prevPage.setPreferredSize(new Dimension(100, 40));


		JScrollPane tScroll = new JScrollPane(this.resultsTable);

		tScroll.setPreferredSize(new Dimension(224, 100));
		this.pnl_results = new JPanel();
		this.pnl_results.setOpaque(false);
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
		this.radBtn_5.setOpaque(false);
		this.radBtn_5.setForeground(Color.white);
		this.radBtn_10 = new JRadioButton("10 results per page");
		this.radBtn_10.setOpaque(false);
		this.radBtn_10.setForeground(Color.white);
		this.radBtn_15 = new JRadioButton("15 results per page");
		this.radBtn_15.setOpaque(false);
		this.radBtn_15.setForeground(Color.white);
		this.radBtn_20 = new JRadioButton("20 results per page");
		this.radBtn_20.setOpaque(false);
		this.radBtn_20.setForeground(Color.white);


		this.radBtn_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedNumberOfResults = 5;
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
		this.pnl_resultRadBtnHolder.setOpaque(false);
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
		
		Border tBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.pnl_resultRadBtnHolder.setBorder(BorderFactory.createTitledBorder(tBorder, "Results per page", 0, 0, new Font("Tahoma", Font.BOLD, 13), Color.black));
		this.pnl_resultRadBtnHolder.add(this.radBtn_5);
		this.pnl_resultRadBtnHolder.add(this.radBtn_10);
		this.pnl_resultRadBtnHolder.add(this.radBtn_15);		
		this.pnl_resultRadBtnHolder.add(this.radBtn_20);

		
		this.radBtn_5.addKeyListener(this);
		this.radBtn_10.addKeyListener(this);
		this.radBtn_15.addKeyListener(this);
		this.radBtn_20.addKeyListener(this);

		
		/* search type option handling */
		this.btnGrp_searchOption = new ButtonGroup();
		this.radBtn_author = new JRadioButton("Search By Author");
		this.radBtn_author.setOpaque(false);
		this.radBtn_author.setForeground(Color.white);
		this.radBtn_content = new JRadioButton("Search By Content");
		this.radBtn_content.setOpaque(false);
		this.radBtn_content.setForeground(Color.white);
		
		this.radBtn_author.addKeyListener(this);
		this.radBtn_content.addKeyListener(this);
		
		this.btnGrp_searchOption.add(this.radBtn_author);
		this.btnGrp_searchOption.add(this.radBtn_content);

		this.pnl_searchOptionHolder = new JPanel(new FlowLayout());
		this.pnl_searchOptionHolder.setOpaque(false);

		this.pnl_searchOptionHolder.setLayout(new BoxLayout(this.pnl_searchOptionHolder, BoxLayout.X_AXIS));
		this.pnl_searchOptionHolder.setBorder(BorderFactory.createTitledBorder(tBorder, "Search option", 0, 0, new Font("Tahoma", Font.BOLD, 13), Color.black));

		this.pnl_searchOptionHolder.add(this.radBtn_author);
		this.pnl_searchOptionHolder.add(this.radBtn_content);

		/* searching area handling */
		this.txtFld_searchField = new JTextField("enter search phrase here");

		this.txtFld_searchField.addKeyListener(this);

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

		JPanel mainPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9062536731426386680L;

			public void paint(Graphics g) {
				g.drawImage(new ImageIcon("./images/background1.jpg").getImage(), 
						0, 0, 1920, 1200, null);
				setOpaque(false);
				super.paint(g);
			}
		};
		
		GroupLayout tLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tLayout);	
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

		this.getContentPane().add(mainPanel);

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
		this.btn_search.addKeyListener(this);
		this.btn_search.grabFocus();
		this.setSize(new Dimension(600, 265));

		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int YY = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, YY);


		this.radBtn_author.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lbl_searchDescription.setText("Please enter a user's name : ");
				if (pnl_results.isVisible())
					btn_search.doClick();
				//				lbl_searchDescription.setToolTipText("Enter a username to search by");
			}

		});

		this.radBtn_content.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				lbl_searchDescription.setText("Please enter a phrase : ");
				if (pnl_results.isVisible())
					btn_search.doClick();
			}

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
//			resultsTable.setVisible(false);
//			setResizable(false);
//			this.setMinimumSize(new Dimension(600, 255));

			resultsTableModel.clearData();
			resultsTableModel.fireTableDataChanged();
			controller.searchByAuthor(btn_search, toSearch);
			//resultsTable.setVisible(true);
		}

		else {
//			resultsTable.setVisible(false);
//			setResizable(false);
//			this.setMinimumSize(new Dimension(600, 255));

			resultsTableModel.clearData();
			resultsTableModel.fireTableDataChanged();
			controller.searchByContent(btn_search, toSearch);
			//resultsTable.setVisible(true);
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
