package com.jonathanaquino.svntimelapseview.ribbon;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import com.jonathanaquino.svntimelapseview.ApplicationWindow;
import com.jonathanaquino.svntimelapseview.Closure;
import com.jonathanaquino.svntimelapseview.SvnLoader;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

/**
 * A Ribbon that displays the loading progress.
 */
public class ProgressRibbon extends JPanel implements Ribbon {
	
	/** Label for displaying brief descriptions of the load progress. */
	private JLabel label = new JLabel();
	
	/** Progress bar showing number of revisions downloaded. */
	private JProgressBar progressBar = new JProgressBar();
	
	/** The main window of the program. */
	private ApplicationWindow applicationWindow;

	/** Loads revisions from a subversion repository. */
	private SvnLoader loader;
	
	/**
	 * Creates a new ProgressRibbon.
	 * 
	 * @param applicationWindow  the main window of the program
	 */
	public ProgressRibbon(final ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(progressBar);
		add(label);
	}

	/**
	 * Called when a Ribbon is displayed. May be called more than once.
	 */
	public void initialize() {		
		label.setText("Loading...");
		progressBar.setValue(0);
		loader = applicationWindow.getApplication().getLoader();
		final Timer timer = new Timer(500, new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (! loader.isLoading()) {
							updateDisplay();
							((Timer) e.getSource()).stop();
							applicationWindow.getRibbonPanel().setCurrentRibbon("search-ribbon");
						} else if (loader.getTotalCount() > 0) {
							updateDisplay();
						}
					}
				});
			}
		});
		timer.start();
	}
	
	private void updateDisplay() {
		progressBar.setMaximum(loader.getTotalCount());
		progressBar.setValue(loader.getLoadedCount());
		label.setText("Loaded " + loader.getLoadedCount() + " / " + loader.getTotalCount() + " revisions");
	}
	
}      

