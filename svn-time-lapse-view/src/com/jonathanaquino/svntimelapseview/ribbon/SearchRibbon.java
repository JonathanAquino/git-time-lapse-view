package com.jonathanaquino.svntimelapseview.ribbon;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jonathanaquino.svntimelapseview.ApplicationWindow;
import com.jonathanaquino.svntimelapseview.Closure;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

/**
 * A Ribbon that allows the user to search the text of the currently displayed revisions.
 */
public class SearchRibbon extends JPanel implements Ribbon {

	/**
	 * Creates a new SearchRibbon.
	 * 
	 * @param applicationWindow  the main window of the program
	 */
	public SearchRibbon(final ApplicationWindow applicationWindow) {		
		setLayout(new FlowLayout(FlowLayout.LEFT));		
		add(new JLabel("TODO"));		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						applicationWindow.getRibbonPanel().setCurrentRibbon("load-ribbon");
					}
				});
			}			
		});
		add(cancelButton);
	}

	/**
	 * Called when a Ribbon is displayed. May be called more than once.
	 */
	public void initialize() {		
	}
	
}
