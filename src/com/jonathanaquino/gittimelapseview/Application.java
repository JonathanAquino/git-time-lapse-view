package com.jonathanaquino.gittimelapseview;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import com.jonathanaquino.gittimelapseview.helpers.DiffHelper;

/**
 * The top-level object in the program.
 */
public class Application {

    /**
     * Launches the application.
     */
    public static void main(String[] args) throws Exception {
        initializeLookAndFeel();
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option configOption = parser.addStringOption("config");
        CmdLineParser.Option limitOption = parser.addStringOption("limit");
        parser.parse(args);
        String filePathOrUrl = parser.getRemainingArgs().length > 0 ? parser.getRemainingArgs()[0] : null;
        String configFilePath = (String) parser.getOptionValue(configOption);
        if (configFilePath == null) { configFilePath = FileSystemView.getFileSystemView().getDefaultDirectory() + File.separator + "git_time_lapse_view.ini"; }
        String limitString = (String) parser.getOptionValue(limitOption);
        int limit = limitString == null ? 100 : Integer.parseInt(limitString);
        new ApplicationWindow(new Application(new Configuration(configFilePath)), filePathOrUrl, limit).setVisible(true);
    }


    /**
     * Sets the appearance of window controls. Call this as early as possible.
     */
    private static void initializeLookAndFeel() throws Exception {
        //Apple stuff from Raj Singh [Jon Aquino 2007-10-14]
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.showGrowBox", "true");
        if (UIManager.getLookAndFeel() != null && UIManager.getLookAndFeel().getClass().getName().equals(UIManager.getSystemLookAndFeelClassName())) {
            return;
        }
        String lookAndFeel = System.getProperty("swing.defaultlaf");
        if (lookAndFeel == null){ lookAndFeel = UIManager.getSystemLookAndFeelClassName(); }
        UIManager.setLookAndFeel(lookAndFeel);
    }

    /** Configuration properties */
    private Configuration configuration;

    /** Loads revisions from a Git repository. */
    private GitLoader loader = new GitLoader();

    /** Cache of revision Diffs, keyed by "revision-number-1, revision-number-2" */
    private Map diffCache = new HashMap();

    /** The Revisions for the file being examined. */
    private List revisions = new ArrayList();

    /**
     * Creates a new Application.
     *
     * @param configuration  configuration properties
     */
    public Application(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Returns the configuration properties.
     *
     * @return  the application settings
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Returns the set of differences between the contents of the two revisions.
     *
     * @param a  the first revision to examine
     * @param b  the second revision to examine
     * @param showDifferencesOnly  whether to hide identical lines
     * @return  a comparison of the lines in each revision
     */
    public Diff diff(Revision a, Revision b, boolean showDifferencesOnly) {
        String key = a.getRevisionNumber() + ", " + b.getRevisionNumber() + ", " + (showDifferencesOnly ? "differences only" : "all");
        if (! diffCache.containsKey(key)) {
            diffCache.put(key, DiffHelper.diff(a.getContents(), b.getContents(), showDifferencesOnly));
        }
        return (Diff) diffCache.get(key);
    }

    /**
     * Loads the revisions for the specified file.
     *
     * @param filePathOrUrl  Git URL or working-copy file path
     * @param limit  maximum number of revisions to download
     * @param afterLoad  operation to run after the load finishes
     */
    public void load(String filePathOrUrl, int limit, final Closure afterLoad) throws Exception {
        loader.loadRevisions(filePathOrUrl, limit, new Closure() {
            public void execute() throws Exception {
                List revisions = loader.getRevisions();
                if (revisions.size() == 0) { throw new Exception("No revisions found"); }
                if (revisions.size() == 1) { throw new Exception("Only one revision found"); }
                Application.this.revisions = revisions;
                diffCache = new HashMap();
                afterLoad.execute();
            }
        });
    }

    /**
     * Returns the Revisions for the file being examined.
     *
     * @return  the file's revision history
     */
    public List getRevisions() {
        return revisions;
    }

    /**
     * Returns the Git revision loader.
     *
     * @return the object that downloads revisions
     */
    public GitLoader getLoader() {
        return loader;
    }

}
