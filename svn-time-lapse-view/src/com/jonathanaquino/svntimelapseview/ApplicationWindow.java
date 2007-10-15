package com.jonathanaquino.svntimelapseview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.time.DateUtils;

import com.jonathanaquino.svntimelapseview.helpers.GuiHelper;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;
import com.jonathanaquino.svntimelapseview.ribbon.RibbonPanel;

/**
 * The main window of the program.
 */
public class ApplicationWindow extends JFrame {
	
	/** The top-level object in the program */
	private Application application;
	
	/** The slider that controls the current revision */
	private JSlider slider = new JSlider(1, 30);
	
	/** The text area showing details for the left file. */
	private JTextArea leftMetadataTextArea = new JTextArea();
	
	/** The text area showing details for the right file. */
	private JTextArea rightMetadataTextArea = new JTextArea();
	
	/** The editor pane displaying the contents of the left file. */
	private JEditorPane leftEditorPane = new JEditorPane();
	
	/** The editor pane displaying the contents of the right file. */
	private JEditorPane rightEditorPane = new JEditorPane();
	
	/** The interactive status line at the bottom of the application window. */
	private RibbonPanel ribbonPanel;

	/** The value of the horizontal scroll bars on the editor panes. */
	private int horizontalScrollBarValue = 0;
	
	/** The value of the vertical scroll bars on the editor panes. */
	private int verticalScrollBarValue = 0;
	
	/** Number of freeze requests for the scroll bar values */
	private int scrollBarValueLocks = 0;
	
	/**
	 * Creates a new ViewerFrame.
	 * 
	 * @param application  the top-level object in the program.
	 * @param url  Subversion URL of the initial file to open, or null to open nothing
	 * @param username  username, or an empty string for anonymous
	 * @param password  password, or an empty string for anonymous 
	 * @param limit  maximum number of revisions to download
	 */
	public ApplicationWindow(Application application, final String url, final String username, final String password, final int limit) throws Exception {
		this.application = application;
		initialize();		
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (url != null) { load(url, username, password, limit); }
					}					
				});
			}
		});
	}

	/**
	 * Sets up the GUI components.
	 */
	private void initialize() throws Exception {
		setTitle("SVN Time-Lapse View");
		initializeWindowPosition();
		getContentPane().setLayout(new BorderLayout());		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});	
		initializeSlider();
		JPanel editorPanePanel = new JPanel(new GridLayout(0, 2));
		add(editorPanePanel, BorderLayout.CENTER);
		initializeEditorPane(leftEditorPane, 0, editorPanePanel);
		initializeEditorPane(rightEditorPane, 1, editorPanePanel);
		JPanel southPanel = new JPanel(new BorderLayout());
		add(southPanel, BorderLayout.SOUTH);
		JPanel metadataPanel = new JPanel(new GridLayout(0, 2));
		southPanel.add(metadataPanel, BorderLayout.CENTER);
		initializeMetadataTextArea(leftMetadataTextArea, 0, metadataPanel);
		initializeMetadataTextArea(rightMetadataTextArea, 1, metadataPanel);
		ribbonPanel = new RibbonPanel(this);
		southPanel.add(ribbonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Sets up the slider that controls the current revision.
	 */
	private void initializeSlider() {
		final Timer changeRevisionTimer = MiscHelper.createQuiescenceTimer(50, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						setCurrentRevisionIndex(slider.getValue());
					}			
				});
			}			
		});		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    changeRevisionTimer.restart();	
			}			
		});
		slider.setSnapToTicks(true);
		slider.setMinorTickSpacing(1);
		JPanel sliderPanel = new JPanel(new GridBagLayout());
		sliderPanel.add(slider, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		JButton previousButton = new JButton("<");
		JButton nextButton = new JButton(">");
		previousButton.setToolTipText("Previous Revision");
		nextButton.setToolTipText("Next Revision");
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (slider.getValue() > 0) { slider.setValue(slider.getValue() - 1); }
			}}
		);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (slider.getValue() < application.getRevisions().size()) { slider.setValue(slider.getValue() + 1); }
			}}
		);
		sliderPanel.add(previousButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		sliderPanel.add(nextButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(sliderPanel, BorderLayout.NORTH);
	}

	/**
	 * Sets up one of the editor panes showing the contents of the file.
	 * 
	 * @param editorPane  the editor pane to initialize
	 * @param x  0 or 1 for left or right
	 * @param parentPanel  the panel to which to add the editor pane
	 */
	private void initializeEditorPane(JEditorPane editorPane, int x, JPanel parentPanel) {
	    editorPane.setContentType("text/html");
	    editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setMaximumSize(new Dimension(100, 5000));
	    parentPanel.add(scrollPane);
	    scrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						setHorizontalScrollBarValue(e.getValue());
						freezeScrollBarsDuring(new Closure() {
							public void execute() throws Exception {
								updateScrollBars();
							}			
						});
					}			
				});
			}        	
	    });
	    scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						setVerticalScrollBarValue(e.getValue());
						freezeScrollBarsDuring(new Closure() {
							public void execute() throws Exception {
								updateScrollBars();
							}			
						});
					}			
				});
			}        	
	    });
	}

	/**
	 * Sets up the location and dimensions of the window.
	 */
	private void initializeWindowPosition() {
		final Configuration configuration = application.getConfiguration();
		setSize(configuration.getInt("width", 900), configuration.getInt("height", 665));
		setLocation(configuration.getInt("x", 0), configuration.getInt("y", 0));
		if (configuration.getBoolean("maximized", false)) { GuiHelper.maximize(this); }
		this.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (GuiHelper.minimized(ApplicationWindow.this)) { return; }
						if (GuiHelper.maximized(ApplicationWindow.this)) { return; }
						configuration.setInt("x", getLocation().x);
						configuration.setInt("y", getLocation().y);
					}					
				});
			}
			public void componentResized(ComponentEvent e) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						if (GuiHelper.minimized(ApplicationWindow.this)) { return; }
						if (GuiHelper.maximized(ApplicationWindow.this)) {
							configuration.setBoolean("maximized", true);
							return;
						}
						configuration.setBoolean("maximized", false);
						configuration.setInt("width", getWidth());
						configuration.setInt("height", getHeight());
						
					}					
				});
			}
		});
	}

	/**
	 * Sets up one of the text areas showing file details.
	 * 
	 * @param metadataTextArea  the text area to initialize
	 * @param x  0 or 1 for left or right
	 * @param parentPanel  the panel to which to add the text area
	 */
	private void initializeMetadataTextArea(JTextArea metadataTextArea, int x, JPanel parentPanel) {
		metadataTextArea.setEditable(false);
		metadataTextArea.setWrapStyleWord(true);
		metadataTextArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(metadataTextArea);
		scrollPane.setPreferredSize(new Dimension(100, 100));
		parentPanel.add(scrollPane);		
	}

	/**
	 * Loads the revisions for the specified file.
	 * 
	 * @param url  Subversion URL of the file to open
	 * @param username  username, or an empty string for anonymous
	 * @param password  password, or an empty string for anonymous
	 * @param limit  maximum number of revisions to download
	 */
	public void load(final String url, final String username, final String password, final int limit) throws Exception {
		application.load(url, username, password, limit, new Closure() {
			public void execute() throws Exception {
				GuiHelper.invokeOnEventThread(new Runnable() {
					public void run() {
						MiscHelper.handleExceptions(new Closure() {
							public void execute() throws Exception {
								setTitle(url);
								setHorizontalScrollBarValue(0);
								setVerticalScrollBarValue(0);
								application.getConfiguration().set("url", url);
								application.getConfiguration().set("username", username);
								application.getConfiguration().setInt("limit", limit);
								slider.setMinimum(1);
								slider.setMaximum(application.getRevisions().size() - 1);
								slider.setValue(slider.getMaximum());
								slider.setPaintTicks(application.getRevisions().size() < 100);
								setCurrentRevisionIndex(slider.getMaximum());
							}
						});
					}					
				});
			}			
		});
	}

	/**
	 * Displays the n-1th and nth revisions.
	 * 
	 * @param n  the index of the revision to display
	 */
	private void setCurrentRevisionIndex(int n) throws Exception {
		List revisions = application.getRevisions();
		if (n >= revisions.size()) { return; }
		Diff diff = application.diff((Revision) revisions.get(n - 1), (Revision) revisions.get(n));
		updateEditorPane(leftEditorPane, diff.getLeftHtml());
		updateEditorPane(rightEditorPane, diff.getRightHtml());
		updateMetadataTextArea(leftMetadataTextArea, (Revision) revisions.get(n - 1));
		updateMetadataTextArea(rightMetadataTextArea, (Revision) revisions.get(n));
	}

	/**
	 * Populates the editor pane with the contents of the revision.
	 * 
	 * @param editorPanel  the editor pane to update
	 * @param html  the revision to display in the editor pane
	 */
	private void updateEditorPane(final JEditorPane editorPane, String html) throws Exception {
		editorPane.setText(html);		
		updateScrollBars();			
	}
	
	/**
	 * Populates the text area with metadata from the revision.
	 * 
	 * @param metadataTextArea  the text area to update
	 * @param revision  the revision to display
	 */
	private void updateMetadataTextArea(JTextArea metadataTextArea, Revision revision) {
		metadataTextArea.setText("Rev " + revision.getRevisionNumber() + ", "
				+ "by " + revision.getAuthor() + ", "
				+ revision.getDate() + "\n"
				+ revision.getLogMessage().trim());
		metadataTextArea.setCaretPosition(0);
	}

	/**
	 * Ensures that the vertical scroll bar does not move during the operation.
	 * 
	 * @param closure  the operation to execute
	 */
	private void freezeScrollBarsDuring(Closure closure) throws Exception {
		scrollBarValueLocks++;
		try {
			closure.execute();
		} finally {
			// Use invokeLater to ensure that the lock is released *after* the editor pane finishes
			// changing its contents [Jon Aquino 2007-10-14]
            SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollBarValueLocks--;
				}
            });			
		}
	}

	/**
	 * Sets the value of the horizontal scroll bars on editor panes.
	 * 
	 * @param horizontalScrollBarValue  the new value
	 */
	private void setHorizontalScrollBarValue(int horizontalScrollBarValue) {
		if (scrollBarValueLocks > 0) { return; }
		this.horizontalScrollBarValue = horizontalScrollBarValue;
	}
	
	/**
	 * Sets the value of the vertical scroll bars on editor panes.
	 * 
	 * @param verticalScrollBarValue  the new value
	 */
	private void setVerticalScrollBarValue(int verticalScrollBarValue) {
		if (scrollBarValueLocks > 0) { return; }
		this.verticalScrollBarValue = verticalScrollBarValue;
	}
	
	/**
	 * Updates the positions of the vertical scrollbars for the editor panes.
	 */
	private void updateScrollBars() throws Exception {
		freezeScrollBarsDuring(new Closure() {
			public void execute() throws Exception {
				JScrollBar leftVerticalScrollBar = ((JScrollPane) leftEditorPane.getParent().getParent()).getVerticalScrollBar();
				JScrollBar rightVerticalScrollBar = ((JScrollPane) rightEditorPane.getParent().getParent()).getVerticalScrollBar();
				if (leftVerticalScrollBar.getValue() != verticalScrollBarValue) { leftVerticalScrollBar.setValue(verticalScrollBarValue); }
				if (rightVerticalScrollBar.getValue() != verticalScrollBarValue) { rightVerticalScrollBar.setValue(verticalScrollBarValue); }

				JScrollBar leftHorizontalScrollBar = ((JScrollPane) leftEditorPane.getParent().getParent()).getHorizontalScrollBar();
				JScrollBar rightHorizontalScrollBar = ((JScrollPane) rightEditorPane.getParent().getParent()).getHorizontalScrollBar();
				if (leftHorizontalScrollBar.getValue() != horizontalScrollBarValue) { leftHorizontalScrollBar.setValue(horizontalScrollBarValue); }
				if (rightHorizontalScrollBar.getValue() != horizontalScrollBarValue) { rightHorizontalScrollBar.setValue(horizontalScrollBarValue); }
			}			
		});
	}

	/**
	 * Returns the program's top-level object.
	 * 
	 * @return  the main object
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Returns the interactive status line at the bottom of the application window.
	 * 
	 * @return  the ribbon line
	 */
	public RibbonPanel getRibbonPanel() {
		return ribbonPanel;
	}

}
