package com.jonathanaquino.svntimelapseview;

/**
 * A revision of a file in Subversion
 */
public class Revision {

	/** The number identifying this revision */
	private long revisionNumber;

	/** The username of the person who submitted this revision */
	private String author;

	/** The date on which the revision was submitted */
	private String date;

	/** The log message accompanying the submission of this revision */
	private String logMessage;

	/** The contents of the file that was submitted */
	private String contents;

	public Revision(long revisionNumber, String author, String date, String logMessage, String contents) {
		this.revisionNumber = revisionNumber;
		this.author = author;
		this.date = date;
		this.logMessage = logMessage;
		this.contents = contents;
	}

	/**
	 * Returns the number identifying this revision.
	 *
	 * @return the value from Subversion
	 */
	public long getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * Returns the username of the person who submitted this revision.
	 *
	 * @return the value from Subversion
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Returns the date on which the revision was submitted.
	 *
	 * @return the value from Subversion
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Returns the log message accompanying the submission of this revision.
	 *
	 * @return the value from Subversion
	 */
	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * Returns the contents of the file that was submitted.
	 *
	 * @return the value from Subversion
	 */
	public String getContents() {
		return contents;
	}

}
