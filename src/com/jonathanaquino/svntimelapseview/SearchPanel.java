package com.jonathanaquino.svntimelapseview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jonathanaquino.svntimelapseview.helpers.GuiHelper;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

/**
 * The bar at the bottom of the application window.
 */
public class SearchPanel extends JPanel {

	/** The current diff being viewed. */
	private Diff currentDiff;

	/** The index of the difference being viewed, or -1 if none is being viewed. */
	private int differenceIndex = -1;

	/** Searches the text of the two revisions. */
	private Searcher searcher;

	/** The label displaying the number of differences in the current diff. */
	private JLabel differenceCountLabel = new JLabel();
	
	/** Checkbox for toggling between showing the entire file and showing differences only. */
	private JCheckBox showDifferencesOnlyCheckbox = new JCheckBox("Show differences only");

	/**
	 * Creates a new SearchPanel.
	 *
	 * @param applicationWindow  the main window of the program
	 */
	public SearchPanel(final ApplicationWindow applicationWindow) {
		setLayout(new GridBagLayout());
		final JTextField searchTextField = new JTextField(20);
		JButton searchButton = new JButton("Search");
		add(GuiHelper.pressOnEnterKey(searchTextField, searchButton), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(searchButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (searcher != null && searcher.search(searchTextField.getText().trim())) {
							applicationWindow.highlight(searcher.getSide(), searcher.getPosition(), searchTextField.getText().trim().length());
						}
					}
				});
			}}
		);
		showDifferencesOnlyCheckbox.setSelected(applicationWindow.getApplication().getConfiguration().getBoolean("showDifferencesOnly", false));
		showDifferencesOnlyCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						applicationWindow.getApplication().getConfiguration().setBoolean("showDifferencesOnly", isShowingDifferencesOnly());
						applicationWindow.loadRevision();
					}
				});
			}}
		);
		add(showDifferencesOnlyCheckbox, new GridBagConstraints(9, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
		add(differenceCountLabel, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
		JButton previousButton = GuiHelper.setShortcutKey(new JButton("\u25B2"), KeyEvent.VK_UP, InputEvent.ALT_MASK);
		JButton nextButton = GuiHelper.setShortcutKey(new JButton("\u25BC"), KeyEvent.VK_DOWN, InputEvent.ALT_MASK);
		previousButton.setMargin(new Insets(0, 4, 0, 4));
		nextButton.setMargin(new Insets(0, 4, 0, 4));
		previousButton.setToolTipText("Previous Difference (Alt+\u21E7)");
		nextButton.setToolTipText("Next Difference (Alt+\u21E9)");
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (currentDiff == null || currentDiff.getDifferencePositions().size() == 0) { return; }
						differenceIndex = previousDiffIndex(differenceIndex, currentDiff.getDifferencePositions().size());
						scrollToCurrentDifference(applicationWindow);
					}
				});
			}}
		);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (currentDiff == null || currentDiff.getDifferencePositions().size() == 0) { return; }
						differenceIndex = nextDiffIndex(differenceIndex, currentDiff.getDifferencePositions().size());
						scrollToCurrentDifference(applicationWindow);
					};
				});
			}}
		);
		add(previousButton, new GridBagConstraints(20, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nextButton, new GridBagConstraints(30, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * Creates a new SearchPanel without parameters, for testing.
	 */
	protected SearchPanel() {
	}

	/**
	 * Sets the current diff being viewed.
	 *
	 * @param currentDiff  the set of differences between the current two text files
	 */
	public void setCurrentDiff(Diff currentDiff) {
		this.currentDiff = currentDiff;
		int n = currentDiff.getDifferencePositions().size();
		differenceCountLabel.setText(n + " difference" + (n == 1 ? "" : "s"));
		differenceIndex = -1;
		searcher = new Searcher(currentDiff.getLeftText(), currentDiff.getRightText());
	}

	/**
	 * Returns the index of the difference to go to if the user clicks Previous Difference.
	 *
	 * @param diffIndex  the index of the difference being viewed, or -1 if none is being viewed
	 * @param count  the number of differences
	 * @return  the index of the previous difference
	 */
	public int previousDiffIndex(int diffIndex, int count) {
		if (diffIndex == -1) { return 0; }
		if (diffIndex == 0) { return 0; }
		return diffIndex - 1;
	}

	/**
	 * Returns the index of the difference to go to if the user clicks Next Difference.
	 *
	 * @param diffIndex  the index of the difference being viewed, or -1 if none is being viewed
	 * @param count  the number of differences
	 * @return  the index of the next difference
	 */
	public int nextDiffIndex(int diffIndex, int count) {
		if (diffIndex == -1) { return 0; }
		if (diffIndex == count - 1) { return count - 1; }
		return diffIndex + 1;
	}

	/**
	 * The line number of the current difference.
	 * 
	 * @return  the zero-based line number.
	 */
	private int getCurrentDifferencePosition() {
		return ((Integer)currentDiff.getDifferencePositions().get(differenceIndex)).intValue();
	}

	/**
	 * Scrolls the left and right editor panes to the current difference.
	 * 
	 * @param applicationWindow  the main window of the program
	 */
	private void scrollToCurrentDifference(ApplicationWindow applicationWindow) {
		applicationWindow.scrollToLine(Math.max(0, getCurrentDifferencePosition() - 1));
	}

	/**
	 * Whether the "Show differences only" checkbox is selected.
	 * 
	 * @return  whether to hide identical lines
	 */
	public boolean isShowingDifferencesOnly() {
		return showDifferencesOnlyCheckbox.isSelected();
	}

}
