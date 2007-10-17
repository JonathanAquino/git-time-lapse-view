package com.jonathanaquino.svntimelapseview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;


/**
 * Loads revisions from a subversion repository.
 */
public class SvnLoader {

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
	 * @param filePathOrUrl  Subversion URL or working-copy file path
	 * @param username  username, or null for anonymous
	 * @param password  password, or null for anonymous
	 * @param limit  maximum number of revisions to download
	 * @param afterLoad  operation to run after the load finishes
	 */
	public void loadRevisions(final String filePathOrUrl, final String username, final String password, final int limit, final Closure afterLoad) throws Exception {
		loading = true;
		cancelled = false;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						loadRevisionsProper(filePathOrUrl, username, password, limit, afterLoad);
					}
				});
			}
		});
		thread.start();
	}

	/**
	 * Builds a list of revisions for the given file.
	 *
	 * @param filePathOrUrl  Subversion URL or working-copy file path
	 * @param username  username, or null for anonymous
	 * @param password  password, or null for anonymous
	 * @param limit  maximum number of revisions to download
	 * @param afterLoad  operation to run after the load finishes
	 */
	private void loadRevisionsProper(String filePathOrUrl, String username, String password, int limit, Closure afterLoad) throws SVNException, Exception {
		try {
			loadedCount = totalCount = 0;
			SVNURL fullUrl = svnUrl(filePathOrUrl, username, password);
			String url = fullUrl.removePathTail().toString();
			String filePath = fullUrl.getPath().replaceAll(".*/", "");
			SVNRepository repository = repository(url, username, password);
			List svnFileRevisions = new ArrayList(repository.getFileRevisions(filePath, null, 0, repository.getLatestRevision()));
			Collections.reverse(svnFileRevisions);
			List svnFileRevisionsToDownload = svnFileRevisions.subList(Math.max(0, svnFileRevisions.size() - limit), svnFileRevisions.size());
			totalCount = svnFileRevisionsToDownload.size();
			revisions = new ArrayList();
			for (Iterator i = svnFileRevisionsToDownload.iterator(); i.hasNext(); ) {
				SVNFileRevision r = (SVNFileRevision) i.next();
				if (cancelled) { break; }
				Map p = r.getRevisionProperties();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				repository.getFile(r.getPath(), r.getRevision(), null, outputStream);
				revisions.add(new Revision(r.getRevision(), (String) p.get(SVNRevisionProperty.AUTHOR), formatDate((String) p.get(SVNRevisionProperty.DATE)), (String) p.get(SVNRevisionProperty.LOG), outputStream.toString()));
				loadedCount++;
			}
			Collections.reverse(revisions);
			afterLoad.execute();
		} finally {
			loading = false;
		}
	}

	/**
	 * Normalizes the given file path or URL.
	 *
	 * @param filePathOrUrl  Subversion URL or working-copy file path
	 * @param username  username, or null for anonymous
	 * @param password  password, or null for anonymous
	 * @return  the corresponding Subversion URL
	 */
	private SVNURL svnUrl(String filePathOrUrl, String username, String password) throws SVNException {
		SVNURL svnUrl;
		if (new File(filePathOrUrl).exists()) {
			SVNClientManager clientManager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), username, password);
			svnUrl = clientManager.getWCClient().doInfo(new File(filePathOrUrl), SVNRevision.WORKING).getURL();
		} else {
			svnUrl = SVNURL.parseURIEncoded(filePathOrUrl);
		}
		return svnUrl;
	}

	/**
	 * Formats the value of the date property
	 *
	 * @param date  the revision date
	 * @return a friendlier date string
	 */
	protected String formatDate(String date) {
		return date.replaceFirst("(.*)T(.*:.*):.*", "$1 $2");
	}

	/**
	 * Returns the specified Subversion repository
	 *
	 * @param url  URL of the Subversion repository or one of its files
	 * @param username  username, or null for anonymous
	 * @param password  password, or null for anonymous
	 * @return  the repository handle
	 */
	private SVNRepository repository(String url, String username, String password) throws Exception {
		DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup(); /* svn:// and svn+xxx:// */
        FSRepositoryFactory.setup(); /* file:// */
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(username, password));
		return repository;
	}

	/**
	 * Returns whether revisions are currently being downloaded.
	 *
	 * @return  whether the SvnLoader is loading revisions
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
