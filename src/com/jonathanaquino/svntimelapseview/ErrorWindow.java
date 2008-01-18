package com.jonathanaquino.svntimelapseview;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A window that displays stack traces.
 */
public class ErrorWindow extends JFrame {

    /** The area in which the stack traces are displayed. */
    JTextArea textArea = new JTextArea();

    /**
     * Creates a new ErrorWindow.
     */
    public ErrorWindow() {
        setTitle("Error Log");
        setSize(500, 309);
        getContentPane().setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Sets the contents of the error window.
     *
     * @param text  the text to display
     */
    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }

}
