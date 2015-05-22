package com.jonathanaquino.gittimelapseview;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.jonathanaquino.gittimelapseview.helpers.GuiHelper;
import com.jonathanaquino.gittimelapseview.helpers.MiscHelper;
import com.jonathanaquino.gittimelapseview.helpers.Rot13;

/**
 * A panel that prompts the user to enter a file path.
 */
public class LoadPanel extends JPanel {
    
    /** Button that initiates the load. */
    JButton loadButton = new JButton("Load");

    /** Text field for entering the file path. */
    private JTextField filePathField = GuiHelper.pressOnEnterKey(new JTextField(30), loadButton);

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

    /** The dialog for browsing a filesystem. */
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
        JLabel filePathLabel = new JLabel("File Path:");
        filePathLabel.setToolTipText("The file path");
        fieldPanel.add(filePathLabel);
        fieldPanel.add(filePathField);
        fieldPanel.add(createBrowseButton());
        JLabel limitLabel = new JLabel("Limit:");
        limitLabel.setToolTipText("Maximum number of revisions to retrieve");
        fieldPanel.add(limitLabel);
        fieldPanel.add(limitField);
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        applicationWindow.load(filePathField.getText(), Integer.parseInt(limitField.getText()));
                    }
                });
            }
        });
        fieldPanel.add(loadButton);       
        read(configuration);
    }

    private Component createBrowseButton() {
        JButton button = new JButton("...");
        button.setToolTipText("Browse directories");
        button.setMargin(new Insets(0, 2, 0, 2));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        File directory = new File(filePathField.getText()).getParentFile();
                        if (directory != null && directory.exists()) { getFileChooser().setCurrentDirectory(directory); }
                        if (JFileChooser.APPROVE_OPTION == getFileChooser().showOpenDialog(applicationWindow)) {
                            filePathField.setText(getFileChooser().getSelectedFile().getPath());
                            applicationWindow.load(filePathField.getText(), Integer.parseInt(limitField.getText()));
                        }
                    }
                });
            }
        });
        return button;
    }

    /**
     * Reads the file path and limit values from the configuration.
     *
     * @param configuration  configuration properties
     */
    public void read(Configuration configuration) {
        filePathField.setText(configuration.get("filePath", "/Users/jona/AboutHandler.php"));
        limitField.setText(configuration.get("limit", "10000"));
    }
    
    /**
     * Displays the panel that shows the progress bar.
     */
    public void showProgressPanel() {
        statusLabel.setText("Loading...");
        progressBar.setValue(0);
        ((CardLayout) getLayout()).show(this, "progress-panel");
        final GitLoader loader = applicationWindow.getApplication().getLoader();
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
    private void showFieldPanel() {
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
