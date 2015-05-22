package com.jonathanaquino.gittimelapseview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.jonathanaquino.gittimelapseview.helpers.MiscHelper;


/**
 * Loads revisions from a Git repository.
 */
public class GitLoader {

    /** Whether revisions are currently being downloaded. */
    private volatile boolean loading = false;

    /** Whether the user has requested that the load be cancelled. */
    private volatile boolean cancelled = false;

    /** Number of revisions downloaded for the current file. */
    private volatile int loadedCount = 0;

    /** Total number of revisions to download for the current file. */
    private volatile int totalCount = 0;

    /** The list of Revisions being downloaded. */
    private List revisions;

    /**
     * Builds a list of revisions for the given file, using a thread.
     *
     * @param filePath  Git file path
     * @param limit  maximum number of revisions to download
     * @param afterLoad  operation to run after the load finishes
     */
    public void loadRevisions(final String filePath, final int limit, final Closure afterLoad) throws Exception {
        loading = true;
        cancelled = false;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                MiscHelper.handleExceptions(new Closure() {
                    public void execute() throws Exception {
                        loadRevisionsProper(filePath, limit, afterLoad);
                    }
                });
            }
        });
        thread.start();
    }

    /**
     * Builds a list of revisions for the given file.
     *
     * @param filePath  Git file path
     * @param limit  maximum number of revisions to download
     * @param afterLoad  operation to run after the load finishes
     */
    private void loadRevisionsProper(String filePath, int limit, Closure afterLoad) throws Exception {
        try {
            loadedCount = totalCount = 0;
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.readEnvironment().findGitDir(new File(filePath)).build();
            try {
                // From http://stackoverflow.com/a/205655/2391566
                String relativePath = repository.getDirectory().getParentFile().toURI().relativize(new File(filePath).toURI()).getPath();
                Git git = new Git(repository);
                Iterable<RevCommit> log = git.log().addPath(relativePath).call();
                revisions = new ArrayList();
                for (Iterator<RevCommit> i = log.iterator(); i.hasNext(); ) {
                    RevCommit revCommit = i.next();
                    if (cancelled) { break; }
                    RevTree revTree = revCommit.getTree();
                    TreeWalk treeWalk = new TreeWalk(repository);
                    treeWalk.addTree(revTree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(relativePath));
                    if (!treeWalk.next()) {
                        throw new IllegalStateException("Did not find expected file '" + relativePath + "'");
                    }
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    loader.copyTo(outputStream);                                                                          
                    String encoding = determineEncoding(outputStream.toByteArray());                
                    String content = encoding == null ? outputStream.toString() : outputStream.toString(encoding); 
                    revisions.add(new Revision(revCommit.getName(), 
                                               revCommit.getAuthorIdent().getName(), 
                                               formatDate(revCommit.getCommitTime(), null), 
                                               revCommit.getFullMessage(), 
                                               content));
                    loadedCount++;
                    if (loadedCount == limit) {
                        break;
                    }
                }
                Collections.reverse(revisions);
            } finally {
                repository.close();
            }
            afterLoad.execute();
        } finally {
            loading = false;
        }
    }

    /**
     * Tries to determine the character encoding of the given byte array.
     * 
     * @param array  the bytes of the string to analyze
     * @return  the encoding (e.g., UTF-8) or null if it could not be determined.
     */
    protected String determineEncoding(byte[] array) {
        if (array.length <= 2) { return null; }
        if (array[0] == (byte)0xFF && array[1] == (byte)0xFE) { return "UTF-16"; }
        if (array[0] == (byte)0xFE && array[1] == (byte)0xFF) { return "UTF-16"; }
        if (array[0] == (byte)0xEF && array[1] == (byte)0xBB) { return "UTF-8"; }
        return null;
    }

    /**
     * Formats the value of the date property
     *
     * @param time  a Unix timestamp
     * @param timeZone  ID for the time zone, or null
     * @return a friendlier date string
     */
    protected String formatDate(int time, String timeZone) {
        // From http://stackoverflow.com/a/17433005/2391566
        Date date = new Date(time*1000L); 
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        if (timeZone != null) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return simpleDateFormat.format(date);
    }

    /**
     * Returns whether revisions are currently being downloaded.
     *
     * @return  whether the GitLoader is loading revisions
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * Returns the number of revisions downloaded so far.
     *
     * @return  the number of revisions loaded in the current job.
     */
    public int getLoadedCount() {
        return loadedCount;
    }

    /**
     * Returns the total number of revisions that the current job is downloading.
     *
     * @return  the number of revisions being downloaded.
     */
    public int getTotalCount() {
        return totalCount;
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
     * Requests that the load be cancelled.
     */
    public void cancel() {
        cancelled = true;
    }

}
