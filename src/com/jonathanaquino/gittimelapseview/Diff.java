package com.jonathanaquino.gittimelapseview;

import java.util.List;

/**
 * The set of differences between two text files.
 */
public class Diff {

    /** HTML for the left side of the diff */
    private String leftHtml;

    /** HTML for the right side of the diff */
    private String rightHtml;

    /** Text for the left side of the diff */
    private String leftText;

    /** Text for the right side of the diff */
    private String rightText;

    /** Zero-based line numbers at which differences start. */
    private List differencePositions;
    
    /** String line numbers for the left file; "" for no line. */
    private List leftLineNumbers;
    
    /** String line numbers for the right file; "" for no line. */
    private List rightLineNumbers;

    /**
     * Creates a new Diff.
     *
     * @param leftHtml  HTML for the left side of the diff
     * @param rightHtml  HTML for the right side of the diff
     * @param leftText  text for the left side of the diff
     * @param rightText  text for the right side of the diff
     */
    public Diff(String leftHtml, String rightHtml, String leftText, String rightText, List leftLineNumbers, List rightLineNumbers, List differencePositions) {
        this.leftHtml = leftHtml;
        this.rightHtml = rightHtml;
        this.leftText = leftText;
        this.rightText = rightText;
        this.leftLineNumbers = leftLineNumbers;
        this.rightLineNumbers = rightLineNumbers;
        this.differencePositions = differencePositions;
    }

    /**
     * Returns the HTML for the left side of the diff.
     *
     * @return  the plain-HTML version, used by the unit tests
     */
    public String getLeftHtml() {
        return leftHtml;
    }

    /**
     * Returns the HTML for the right side of the diff.
     *
     * @return  the plain-HTML version, used by the unit tests
     */
    public String getRightHtml() {
        return rightHtml;
    }

    /**
     * Returns the text for the left side of the diff.
     *
     * @return  the plain-text version, used by the unit tests
     */
    public String getLeftText() {
        return leftText;
    }

    /**
     * Returns the text for the right side of the diff.
     *
     * @return  the plain-text version, used by the unit tests
     */
    public String getRightText() {
        return rightText;
    }
    
    /**
     * Returns line numbers for the left file.
     * 
     * @return  String line numbers; "" for no line number.
     */
    public List getLeftLineNumbers() {
        return leftLineNumbers;
    }
    
    /**
     * Returns line numbers for the right file.
     * 
     * @return  String line numbers; "" for no line number.
     */
    public List getRightLineNumbers() {
        return rightLineNumbers;
    }    

    /**
     * Returns the zero-based line numbers at which differences start.
     *
     * @return  0 for the first line, 3 for the fourth, etc.
     */
    public List getDifferencePositions() {
        return differencePositions;
    }


}
