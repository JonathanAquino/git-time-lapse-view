package com.jonathanaquino.svntimelapseview.ribbon;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jonathanaquino.svntimelapseview.ApplicationWindow;
import com.jonathanaquino.svntimelapseview.Closure;
import com.jonathanaquino.svntimelapseview.Configuration;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

/**
 * A Ribbon that prompts the user to enter a file path for which to retrieve revisions.
 */
public class LoadRibbon extends JPanel implements Ribbon {

	/** Text field for entering the Subversion URL for the file. */
	private JTextField urlField = new JTextField(40);
	
	/** Text field for entering an SVN username. */
	private JTextField usernameField = new JTextField(10);
	
	/** Text field for entering an SVN password. */
	private JPasswordField passwordField = new JPasswordField(10);
	
	/** Text field for entering the maximum number of revisions to retrieve. */
	private JTextField limitField = new JTextField(5);
	
	/**
	 * Creates a new LoadRibbon.
	 * 
	 * @param applicationWindow  the main window of the program
	 */
	public LoadRibbon(final ApplicationWindow applicationWindow) {
		final Configuration configuration = applicationWindow.getApplication().getConfiguration();
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel urlLabel = new JLabel("File:");
		urlLabel.setToolTipText("The file path or URL, e.g., http://svn.svnkit.com/repos/svnkit/trunk/www/license.html");		
		add(urlLabel);				
		add(urlField);
		JLabel usernameLabel = new JLabel("User:");
		usernameLabel.setToolTipText("Your username (if any)");
		add(usernameLabel);
		add(usernameField);
		JLabel passwordLabel = new JLabel("Pw:");
		passwordLabel.setToolTipText("Your password (if any)");
		add(passwordLabel);
		add(passwordField);
		JLabel limitLabel = new JLabel("Limit:");
		limitLabel.setToolTipText("Maximum number of revisions to retrieve");
		add(limitLabel);		
		add(limitField);
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						applicationWindow.load(urlField.getText(), usernameField.getText(), String.valueOf(passwordField.getPassword()), Integer.parseInt(limitField.getText()));
						applicationWindow.getRibbonPanel().setCurrentRibbon("progress-ribbon");
					}
				});
			}			
		});
		add(loadButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						read(configuration);
						applicationWindow.getRibbonPanel().setCurrentRibbon("search-ribbon");
					}
				});
			}			
		});
		add(cancelButton);	
		read(configuration);
	}

	/**
	 * Reads the URL, username, and limit values from the configuration. 
	 * 
	 * @param configuration  configuration properties
	 */
	private void read(Configuration configuration) {
		urlField.setText(configuration.get("url", "http://svn.svnkit.com/repos/svnkit/trunk/www/license.html"));
		usernameField.setText(configuration.get("username", ""));
		limitField.setText(configuration.get("limit", "100"));
	}

	/**
	 * Called when a Ribbon is displayed. May be called more than once.
	 */
	public void initialize() {
	}
	
}      

