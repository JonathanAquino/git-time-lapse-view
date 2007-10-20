package com.jonathanaquino.svntimelapseview;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.jonathanaquino.svntimelapseview.helpers.GuiHelper;
import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

/**
 * A panel that prompts the user to enter a file path, username, and password.
 */
public class LoadPanel extends JPanel {

    /** Button that initiates the load. */
    JButton loadButton = new JButton("Load");

    /** Text field for entering the Subversion URL for the file. */
    private JTextField urlField = GuiHelper.pressOnEnterKey(new JTextField(30), loadButton);

    /** Text field for entering an SVN username. */
    private JTextField usernameField = GuiHelper.pressOnEnterKey(new JTextField(10), loadButton);

    /** Text field for entering an SVN password. */
    private JPasswordField passwordField = (JPasswordField) GuiHelper.pressOnEnterKey(new JPasswordField(10), loadButton);

    /** Text field for entering the maximum number of revisions to retrieve. */
    private JTextField limitField = GuiHelper.pressOnEnterKey(new JTextField(5), loadButton);

    /** Label for displaying brief descriptions of the load progress. */
    private JLabel statusLabel = new JLabel();

    /** Progress bar showing number of revisions downloaded. */
    private JProgressBar progressBar = new JProgressBar();

    /** The main window of the program. */
    private ApplicationWindow applicationWindow;

    /** The panel that contains the input fields. */
    private JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    /** The panel that displays the progress bar. */
    private JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    /** The Browse dialog */
    private JFileChooser fileChooser = null;

    /**
     * Creates a new LoadPanel.
     *
     * @param applicationWindow  the main window of the program
     */
    public LoadPanel(ApplicationWindow applicationWindow) {
        this.applicationWindow = applicationWindow;
        setLayout(new CardLayout());
        initializeFieldPanel();
        createProgressPanel();
        add(fieldPanel, "field-panel");
        add(progressPanel, "progress-panel");
    }

    /**
     * Creates the panel that displays the progress bar.
     */
    private void createProgressPanel() {
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        applicationWindow.getApplication().getLoader().cancel();
                    }
                });
            }
        });
        progressPanel.add(stopButton);
        progressPanel.add(progressBar);
        progressPanel.add(statusLabel);
    }

    /**
     * Creates the panel that contains the input fields.
     */
    private void initializeFieldPanel() {
        final Configuration configuration = applicationWindow.getApplication().getConfiguration();
        JLabel urlLabel = new JLabel("File Path/URL:");
        urlLabel.setToolTipText("The file path or URL, e.g., http://svn.svnkit.com/repos/svnkit/trunk/www/license.html");
        fieldPanel.add(urlLabel);
        fieldPanel.add(urlField);
        fieldPanel.add(createBrowseButton());
        JLabel usernameLabel = new JLabel("User:");
        usernameLabel.setToolTipText("Your username (if any)");
        fieldPanel.add(usernameLabel);
        fieldPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Pw:");
        passwordLabel.setToolTipText("Your password (if any)");
        fieldPanel.add(passwordLabel);
        fieldPanel.add(passwordField);
        JLabel limitLabel = new JLabel("Limit:");
        limitLabel.setToolTipText("Maximum number of revisions to retrieve");
        fieldPanel.add(limitLabel);
        fieldPanel.add(limitField);
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        applicationWindow.load(urlField.getText(), usernameField.getText(), String.valueOf(passwordField.getPassword()), Integer.parseInt(limitField.getText()));
                        showProgressPanel();
                    }
                });
            }
        });
        fieldPanel.add(loadButton);
        read(configuration);
    }

    private Component createBrowseButton() {
        JButton button = new JButton("...");
        button.setToolTipText("Browse for a file");
        button.setMargin(new Insets(0, 2, 0, 2));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        File directory = new File(urlField.getText()).getParentFile();
                        if (directory != null && directory.exists()) { getFileChooser().setCurrentDirectory(directory); }
                        if (JFileChooser.APPROVE_OPTION == getFileChooser().showOpenDialog(applicationWindow)) {
                            urlField.setText(getFileChooser().getSelectedFile().getPath());
                        }
                    }
                });
            }
        });
        return button;
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
     * Displays the panel that shows the progress bar.
     */
    public void showProgressPanel() {
        statusLabel.setText("Loading...");
        progressBar.setValue(0);
        ((CardLayout) getLayout()).show(this, "progress-panel");
        final SvnLoader loader = applicationWindow.getApplication().getLoader();
        final Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        if (! loader.isLoading()) {
                            updateProgressPanel();
                            ((Timer) e.getSource()).stop();
                            showFieldPanel();
                        } else if (loader.getTotalCount() > 0) {
                            updateProgressPanel();
                        }
                    }
                });
            }
            private void updateProgressPanel() {
                progressBar.setMaximum(loader.getTotalCount());
                progressBar.setValue(loader.getLoadedCount());
                statusLabel.setText("Loaded " + loader.getLoadedCount() + " / " + loader.getTotalCount() + " revisions");
            }
        });
        timer.start();
    }

    /**
     * Displays the panel that contains the input fields.
     */
    public void showFieldPanel() {
        ((CardLayout) getLayout()).show(this, "field-panel");
    }

    private JFileChooser getFileChooser() {
        if (fileChooser == null) {
              fileChooser = GuiHelper.createJFileChooserWithExistenceChecking();
              fileChooser.setDialogTitle("Select Workspace File");
              fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
              fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
              fileChooser.setMultiSelectionEnabled(false);
        }
        return fileChooser;
    }

}

