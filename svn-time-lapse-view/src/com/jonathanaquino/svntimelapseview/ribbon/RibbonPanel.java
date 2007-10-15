package com.jonathanaquino.svntimelapseview.ribbon;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import com.jonathanaquino.svntimelapseview.ApplicationWindow;

/**
 * An interactive status line at the bottom of the application window.
 * RibbonPanel has two key benefits: modeless interaction, and easier development than
 * dialog boxes.
 */
public class RibbonPanel extends JPanel {

	/** Child panels keyed by name */
	private Map ribbons = new HashMap();
	
	/**
	 * Creates a new RibbonPanel.
	 * 
	 * @param applicationWindow  the main window of the program
	 */
	public RibbonPanel(ApplicationWindow applicationWindow) {
		setLayout(new CardLayout());
		addRibbon("load-ribbon", new LoadRibbon(applicationWindow));
		addRibbon("progress-ribbon", new ProgressRibbon(applicationWindow));
		addRibbon("search-ribbon", new SearchRibbon(applicationWindow));
	}
	
	/**
	 * Adds a child panel with the given name.
	 * 
	 * @param name  an identifier for the ribbon, e.g., "retrieve-revisions-ribbon"
	 * @param ribbon  the child panel
	 */
	public void addRibbon(String name, JPanel ribbon) {
		ribbons.put(name, ribbon);
		add(ribbon, name);
	}

	/**
	 * Sets the child panel to display.
	 * 
	 * @param name  the identifier for the ribbon to display
	 */
	public void setCurrentRibbon(String name) {
		((CardLayout) getLayout()).show(this, name);
		((Ribbon) ribbons.get(name)).initialize();
	}
	
}
