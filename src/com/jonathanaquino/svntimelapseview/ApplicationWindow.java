package com.jonathanaquino.svntimelapseview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import com.jonathanaquino.svntimelapseview.Searcher.Side;
import com.jonathanaquino.svntimelapseview.helpers.GuiHelper;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

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

    /** The panel containing the slider and slider buttons. */
    private JPanel sliderPanel = new JPanel(new GridBagLayout());

    /** The bar at the bottom of the application window. */
    private SearchPanel searchPanel;

    /** The value of the horizontal scroll bars on the editor panes. */
    private int horizontalScrollBarValue = 0;

    /** The value of the vertical scroll bars on the editor panes. */
    private int verticalScrollBarValue = 0;

    /** Number of freeze requests for the horizontal scroll bars */
    private int horizontalScrollBarLocks = 0;

    /** Number of freeze requests for the vertical scroll bars */
    private int verticalScrollBarLocks = 0;
    
    /** The panel that prompts the user to enter a file path, username, and password. */
    private LoadPanel loadPanel;

    /**
     * Creates a new ViewerFrame.
     *
     * @param application  the top-level object in the program.
     * @param filePathOrUrl  Subversion URL or working-copy file path
     * @param username  username, or an empty string for anonymous
     * @param password  password, or an empty string for anonymous
     * @param limit  maximum number of revisions to download
     */
    public ApplicationWindow(Application application, final String filePathOrUrl, final String username, final String password, final int limit) throws Exception {
        this.application = application;
        initialize();
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        if (filePathOrUrl != null) { load(filePathOrUrl, username, password, limit); }
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
        initializeEditorPane(leftEditorPane, 0, editorPanePanel);
        initializeEditorPane(rightEditorPane, 1, editorPanePanel);
        JPanel metadataPanel = new JPanel(new GridLayout(0, 2));
        initializeMetadataTextArea(leftMetadataTextArea, 0, metadataPanel);
        initializeMetadataTextArea(rightMetadataTextArea, 1, metadataPanel);
        JPanel innerPanel = new JPanel(new BorderLayout());
        add(sliderPanel, BorderLayout.NORTH);
        add(innerPanel, BorderLayout.CENTER);
        loadPanel = new LoadPanel(this);
        innerPanel.add(loadPanel, BorderLayout.NORTH);
        innerPanel.add(editorPanePanel, BorderLayout.CENTER);
        innerPanel.add(metadataPanel, BorderLayout.SOUTH);
        searchPanel = new SearchPanel(this);
        add(searchPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets up the slider that controls the current revision.
     */
    private void initializeSlider() {
        final Timer changeRevisionTimer = MiscHelper.createQuiescenceTimer(50, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        loadRevision();
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
        sliderPanel.add(new JLabel("Revisions:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        sliderPanel.add(slider, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        JButton previousButton = GuiHelper.setShortcutKey(new JButton("\u25C4"), KeyEvent.VK_LEFT, InputEvent.ALT_MASK);
        JButton nextButton = GuiHelper.setShortcutKey(new JButton("\u25BA"), KeyEvent.VK_RIGHT, InputEvent.ALT_MASK);
        previousButton.setMargin(new Insets(0, 4, 0, 4));
        nextButton.setMargin(new Insets(0, 4, 0, 4));
        previousButton.setToolTipText("Previous Revision (Alt+\u21E6)");
        nextButton.setToolTipText("Next Revision (Alt+\u21E8)");
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        if (slider.getValue() > 0) { slider.setValue(slider.getValue() - 1); }
                    }
                });
            }}
        );
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        if (slider.getValue() < slider.getMaximum()) { slider.setValue(slider.getValue() + 1); }
                    };
                });
            }}
        );
        sliderPanel.add(previousButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        sliderPanel.add(nextButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
                        freezeHorizontalScrollBarsDuring(new Closure() {
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
                        freezeVerticalScrollBarsDuring(new Closure() {
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
     * @param filePathOrUrl  Subversion URL or working-copy file path
     * @param username  username, or an empty string for anonymous
     * @param password  password, or an empty string for anonymous
     * @param limit  maximum number of revisions to download
     */
    public void load(final String filePathOrUrl, final String username, final String password, final int limit) throws Exception {
        application.load(filePathOrUrl, username, password, limit, new Closure() {
            public void execute() throws Exception {
                GuiHelper.invokeOnEventThread(new Runnable() {
                    public void run() {
                        MiscHelper.handleExceptions(new Closure() {
                            public void execute() throws Exception {
                                setTitle(filePathOrUrl);
                                setHorizontalScrollBarValue(0);
                                setVerticalScrollBarValue(0);
                                application.getConfiguration().set("url", filePathOrUrl);
                                application.getConfiguration().set("username", username);
                                application.getConfiguration().setInt("limit", limit);
                                loadPanel.read(application.getConfiguration());
                                slider.setMinimum(1);
                                slider.setMaximum(application.getRevisions().size() - 1);
                                slider.setValue(slider.getMaximum());
                                slider.setPaintTicks(application.getRevisions().size() < 100);
                            }
                        });
                    }
                });
            }
        });
    }
    
    /**
     * Displays the revision corresponding to the current slider value.
     */
    public void loadRevision() throws Exception {
        if (searchPanel.isShowingDifferencesOnly()) {
            setHorizontalScrollBarValue(0);
            setVerticalScrollBarValue(0);
        }
        setCurrentRevisionIndex(slider.getValue());
    }

    /**
     * Displays the n-1th and nth revisions.
     *
     * @param n  the index of the revision to display
     */
    private void setCurrentRevisionIndex(int n) throws Exception {
        List revisions = application.getRevisions();
        if (n >= revisions.size()) { return; }
        Diff diff = application.diff((Revision) revisions.get(n - 1), (Revision) revisions.get(n), searchPanel.isShowingDifferencesOnly());
        updateEditorPane(leftEditorPane, diff.getLeftHtml());
        updateEditorPane(rightEditorPane, diff.getRightHtml());
        updateMetadataTextArea(leftMetadataTextArea, (Revision) revisions.get(n - 1));
        updateMetadataTextArea(rightMetadataTextArea, (Revision) revisions.get(n));
        searchPanel.setCurrentDiff(diff);
    }

    /**
     * Populates the editor pane with the contents of the revision.
     *
     * @param editorPanel  the editor pane to update
     * @param html  the revision to display in the editor pane
     */
    private void updateEditorPane(final JEditorPane editorPane, String html) throws Exception {
        editorPane.setText(html);
        freezeHorizontalScrollBarsDuring(new Closure() {
            public void execute() throws Exception {
                freezeVerticalScrollBarsDuring(new Closure() {
                    public void execute() throws Exception {
                        updateScrollBars();
                    }
                });
            }
        });
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
     * Ensures that the horizontal scroll bar does not move during the operation.
     * Horizontal and vertical scroll bars are tracked independently because operations
     * like search may move both of them at once.
     *
     * @param closure  the operation to execute
     */
    private void freezeHorizontalScrollBarsDuring(Closure closure) throws Exception {
        horizontalScrollBarLocks++;
        try {
            closure.execute();
        } finally {
            // Use invokeLater to ensure that the lock is released *after* the editor pane finishes
            // changing its contents [Jon Aquino 2007-10-14]
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    horizontalScrollBarLocks--;
                }
            });
        }
    }

    /**
     * Ensures that the horizontal scroll bar does not move during the operation.
     * Horizontal and vertical scroll bars are tracked independently because operations
     * like search may move both of them at once.
     *
     * @param closure  the operation to execute
     */
    private void freezeVerticalScrollBarsDuring(Closure closure) throws Exception {
        verticalScrollBarLocks++;
        try {
            closure.execute();
        } finally {
            // Use invokeLater to ensure that the lock is released *after* the editor pane finishes
            // changing its contents [Jon Aquino 2007-10-14]
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    verticalScrollBarLocks--;
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
        if (horizontalScrollBarLocks > 0) { return; }
        this.horizontalScrollBarValue = horizontalScrollBarValue;
    }

    /**
     * Sets the value of the vertical scroll bars on editor panes.
     *
     * @param verticalScrollBarValue  the new value
     */
    private void setVerticalScrollBarValue(int verticalScrollBarValue) {
        if (verticalScrollBarLocks > 0) { return; }
        this.verticalScrollBarValue = verticalScrollBarValue;
    }

    /**
     * Updates the positions of the scrollbars for the editor panes.
     */
    private void updateScrollBars() throws Exception {
        JScrollBar leftVerticalScrollBar = ((JScrollPane) leftEditorPane.getParent().getParent()).getVerticalScrollBar();
        JScrollBar rightVerticalScrollBar = ((JScrollPane) rightEditorPane.getParent().getParent()).getVerticalScrollBar();
        if (leftVerticalScrollBar.getValue() != verticalScrollBarValue) { leftVerticalScrollBar.setValue(verticalScrollBarValue); }
        if (rightVerticalScrollBar.getValue() != verticalScrollBarValue) { rightVerticalScrollBar.setValue(verticalScrollBarValue); }

        JScrollBar leftHorizontalScrollBar = ((JScrollPane) leftEditorPane.getParent().getParent()).getHorizontalScrollBar();
        JScrollBar rightHorizontalScrollBar = ((JScrollPane) rightEditorPane.getParent().getParent()).getHorizontalScrollBar();
        if (leftHorizontalScrollBar.getValue() != horizontalScrollBarValue) { leftHorizontalScrollBar.setValue(horizontalScrollBarValue); }
        if (rightHorizontalScrollBar.getValue() != horizontalScrollBarValue) { rightHorizontalScrollBar.setValue(horizontalScrollBarValue); }
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
     *
    /**
     * Scrolls the editor panes to the given line
     *
     * @param position  the zero-based line number
     */
    public void scrollToLine(int position) {
        leftEditorPane.scrollToReference("Position" + position);
    }

    /**
     * Highlights the text at the given location.
     *
     * @param side  the left or right editor pane
     * @param position  the zero-based position
     * @param length  the amount of text to highlight
     * @throws BadLocationException
     */
    public void highlight(Side side, int position, int length) throws BadLocationException {
        leftEditorPane.getHighlighter().removeAllHighlights();
        rightEditorPane.getHighlighter().removeAllHighlights();
        int start = position + 1;
        int end = start + length;
        JEditorPane editorPane = side == Searcher.LEFT ? leftEditorPane : rightEditorPane;
        editorPane.getHighlighter().addHighlight(start, end, new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
        Rectangle rectangle = editorPane.modelToView(start);
        rectangle.add(editorPane.modelToView(end));
        editorPane.scrollRectToVisible(rectangle);
    }

}
