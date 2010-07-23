package forum.swingclient.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

public class ForumUsersPieChart extends ChartPanel implements GUIHandler {

	private static final long serialVersionUID = -4010973107641941836L;

	private enum UserTypeIndexes {
		ADMINISTRATORS, MODERATORS, MEMBERS, GUESTS;
	}

	private boolean showExistingOrConnected;

	private Map<UserTypeIndexes, Collection<String>> existingUsernames;

	private Long[] activeConnectedNumbers;
	private PieDataset activeConnectedNumbersDataset;

	private Long[] existingMembersNumbers;
	private PieDataset existingMembersNumbersDataset; 

	private ControllerHandler controller;
	
	private JFreeChart chart;

	
	public ForumUsersPieChart() {
		super(null);
		this.existingUsernames = new HashMap<UserTypeIndexes, Collection<String>>();
		this.existingUsernames.put(UserTypeIndexes.ADMINISTRATORS, new HashSet<String>());
		this.existingUsernames.put(UserTypeIndexes.MODERATORS, new HashSet<String>());
		this.existingUsernames.put(UserTypeIndexes.MEMBERS, new HashSet<String>());

		this.clearExistingUsernames();
		showExistingOrConnected = true;
		activeConnectedNumbers = null;
		existingMembersNumbers = null;
		this.chart = this.createChart(this.createDataset(activeConnectedNumbers));
		
		setChart(this.chart);
		
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(300, 290));
		try {
			controller = ControllerHandlerFactory.getPipe();
			controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);
			controller.getAllMembers(this);
		} 
		catch (IOException e) {
			controller = null;
		}
	}

	public void dispose() {
		controller.deleteObserver(this);
	}
	
	public void updateExistingStatistics() {
		this.showExistingOrConnected = true;
		this.controller.getAllMembers(this);
	}
	
	public void updateConnectedStatistics() {
		this.showExistingOrConnected = false;
		this.controller.getActiveUsersNumber();
	}
	
	private void clearExistingUsernames() {
		this.existingUsernames.get(UserTypeIndexes.ADMINISTRATORS).clear();
		this.existingUsernames.get(UserTypeIndexes.MODERATORS).clear();
		this.existingUsernames.get(UserTypeIndexes.MEMBERS).clear();
	}

	private PieDataset createDataset(Long[] values) {
		if (values != null && values.length <= 4) { // legal data array 
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (int i = 0; i < values.length; i++)
				if (values[i] > 0)
					dataset.setValue(UserTypeIndexes.values()[i] + " (" + values[i] + ")", values[i]);
			return dataset;      
		}
		return null;
	}

	private JFreeChart createChart(PieDataset dataset) {

		JFreeChart chart = ChartFactory.createPieChart(
				null,  // chart title
				dataset,             // data
				false,               // include legend
				true,
				false
		);

		
		PiePlot plot = (PiePlot) chart.getPlot();
		
		plot.setInsets(new  
		             RectangleInsets(UnitType.ABSOLUTE, 0.0, 0.0, 0.0, 0.0));
		
		plot.setBackgroundAlpha(0);
		plot.setOutlineVisible(false);
		plot.setLabelFont(new Font("Tahoma", Font.BOLD, 11));
		plot.setNoDataMessage("No data available");
		plot.setNoDataMessageFont(new Font("Tahoma", Font.BOLD, 15));
		plot.setCircular(false);
		plot.setMaximumLabelWidth(0.4);
		plot.setSimpleLabels(true);
		plot.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

		plot.setToolTipGenerator(null);
		return chart;
	}

	@Override
	public void notifyError(String errorMessage) {
		// do nothing - the view will remain same as the last legal one
	}

	@Override
	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("activeusernames")) {

			this.refreshExistingMembers(encodedView);
		}
		else if (encodedView.startsWith("activenumbers")) {

			this.refreshConnectedMembers(encodedView);
		}
		else return;
	}

	private void refreshExistingMembers(final String encodedView) {
		new SwingWorker<Long[], Void>() {
			

			protected Long[] doInBackground() throws Exception {

				Long[] tNumbers = new Long[3];
				clearExistingUsernames();

				for (int i = 0; i < tNumbers.length; i++)
					tNumbers[i] = 0L;

				String[] tMembers = encodedView.split("\n");

				for (int i = 1; i < tMembers.length; i++) {
					String[] tSplittedDetails = tMembers[i].split("\t");
					//existingIDs.add(Long.parseLong(tSplittedDetails[0]));
					if (tSplittedDetails[4].equals("ADMIN")) {
						tNumbers[UserTypeIndexes.ADMINISTRATORS.ordinal()]++;
						existingUsernames.get(UserTypeIndexes.ADMINISTRATORS).add(tSplittedDetails[1]);
					}
					else if (tSplittedDetails[4].equals("MODERATOR")) {
						tNumbers[UserTypeIndexes.MODERATORS.ordinal()]++;
						existingUsernames.get(UserTypeIndexes.MODERATORS).add(tSplittedDetails[1]);
					}
					else if (tSplittedDetails[4].equals("MEMBER")) {
						tNumbers[UserTypeIndexes.MEMBERS.ordinal()]++;
						existingUsernames.get(UserTypeIndexes.MEMBERS).add(tSplittedDetails[1]);
					}
				}
				return tNumbers;

			}

			protected void done() {
				try {
					existingMembersNumbers = this.get();
					existingMembersNumbersDataset =
						ForumUsersPieChart.this.createDataset(existingMembersNumbers);
					if (showExistingOrConnected) {
						PiePlot tPlot = (PiePlot) chart.getPlot();
						tPlot.setDataset(existingMembersNumbersDataset);
						tPlot.datasetChanged(new DatasetChangeEvent(this, existingMembersNumbersDataset));

						chart.plotChanged(new PlotChangeEvent(tPlot));

					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
					existingMembersNumbers = null;
				} 
				catch (ExecutionException e) {
					e.printStackTrace();
					existingMembersNumbers = null;
				}
			}
		}.execute();
	}

	private void refreshConnectedMembers(final String encodedView) {
		if (this.existingMembersNumbers == null)
			return;

		new SwingWorker<Long[], Void>() {
			@Override
			protected Long[] doInBackground() throws Exception {
				
				Long[] tNumbers = new Long[4];

				for (int i = 0; i < tNumbers.length; i++)
					tNumbers[i] = 0L;

				String[] tSplitted = encodedView.split("\t");

				tNumbers[UserTypeIndexes.GUESTS.ordinal()] = Long.parseLong(tSplitted[1]);

				for (int i = 3; i < tSplitted.length; i++) {
					for (UserTypeIndexes value : UserTypeIndexes.values())
						if (existingUsernames.get(value).contains(tSplitted[i])) {
							tNumbers[value.ordinal()]++;
							break;
						}
				}
				return tNumbers;
			}

			@Override
			public void done() {
				try {
					activeConnectedNumbers = get();
					activeConnectedNumbersDataset =
						ForumUsersPieChart.this.createDataset(activeConnectedNumbers);
					if (!showExistingOrConnected) {
						PiePlot tPlot = (PiePlot) chart.getPlot();
						tPlot.setDataset(activeConnectedNumbersDataset);
						tPlot.datasetChanged(new DatasetChangeEvent(this, activeConnectedNumbersDataset));
						chart.plotChanged(new PlotChangeEvent(tPlot));
					}
				} catch (InterruptedException e) {
					activeConnectedNumbers = null;
					e.printStackTrace();
					return;
				} catch (ExecutionException e) {
					e.printStackTrace();
					activeConnectedNumbers = null;
					return;
				}
			}
		}.execute();
	}
}