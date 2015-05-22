package com.jonathanaquino.gittimelapseview;

/**
 * A revision of a file in Git
 */
public class Revision {

    /** The number identifying this revision */
    private String revisionNumber;

    /** The username of the person who submitted this revision */
    private String author;

    /** The date on which the revision was submitted */
    private String date;

    /** The log message accompanying the submission of this revision */
    private String logMessage;

    /** The contents of the file that was submitted */
    private String contents;

    public Revision(String revisionNumber, String author, String date, String logMessage, String contents) {
        this.revisionNumber = revisionNumber;
        this.author = author;
        this.date = date;
        this.logMessage = logMessage;
        this.contents = contents;
    }

    /**
     * Returns the number identifying this revision.
     *
     * @return the value from Git
     */
    public String getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * Returns the username of the person who submitted this revision.
     *
     * @return the value from Git
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the date on which the revision was submitted.
     *
     * @return the value from Git
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the log message accompanying the submission of this revision.
     *
     * @return the value from Git
     */
    public String getLogMessage() {
        return logMessage;
    }

    /**
     * Returns the contents of the file that was submitted.
     *
     * @return the value from Git
     */
    public String getContents() {
        return contents;
    }

}
