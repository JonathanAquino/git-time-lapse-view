package com.jonathanaquino.svntimelapseview.helpers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

import com.jonathanaquino.svntimelapseview.Closure;

/**
 * GUI-related utility functions.
 */
public class GuiHelper {

	/**
	 * Returns whether the frame is minimized.
	 *
	 * @param frame  the frame to inspect
	 * @return  whether the user has minimized the frame
	 */
	public static boolean minimized(JFrame frame) {
		return (frame.getExtendedState() & JFrame.ICONIFIED) == JFrame.ICONIFIED;
	}

	/**
	 * Returns whether the frame is maximized.
	 *
	 * @param frame  the frame to inspect
	 * @return  whether the user has maximized the frame
	 */
	public static boolean maximized(JFrame frame) {
		return (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
	}

	/**
	 * Maximizes the frame.
	 *
	 * @param frame  the frame to modify
	 */
	public static void maximize(JFrame frame) {
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

    /**
     * Ensures that the given operation runs on the AWT event dispatching thread.
     * GUI operations should be performed only on the AWT event dispatching thread.
     * Blocks until the Runnable is finished.
     *
     * @param r  the operation to execute
     */
    public static void invokeOnEventThread(Runnable r) throws InterruptedException, InvocationTargetException {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeAndWait(r);
        }
    }

    /**
     * Creates a file chooser that checks that the file chosen[ exists.
     * @return
     */
    public static JFileChooser createJFileChooserWithExistenceChecking() {
        return new JFileChooser() {
            public void approveSelection() {
                File[] files = selectedFiles(this);
                if (files.length == 0) { return; }
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].exists() && !files[i].isFile()) {
                        return;
                    }
                }
                super.approveSelection();
            }
        };
    }

    /**
     * Returns the files that the user has selected.
     *
     * @param chooser  the file dialog
     */
    public static File[] selectedFiles(JFileChooser chooser) {
    	// Work around Java Bug 4437688 "JFileChooser.getSelectedFile() returns
        // nothing when a file is selected"  [Jon Aquino 2007-10-15]
        return ((chooser.getSelectedFiles().length == 0) && (chooser.getSelectedFile() != null))
        		? new File[] { chooser.getSelectedFile() } : chooser.getSelectedFiles();
    }

    /**
     * Presses the given button if the user hits Enter in the text field.
     *
     * @param textField  the text field in which to listen for the Enter key
     * @param button  the button to push
     * @return  the text field
     */
    public static JTextField pressOnEnterKey(JTextField textField, final JButton button) {
    	textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						button.doClick();
					}
				});
			}
    	});
    	return textField;
    }
    
    /**
     * Associates the specified key with the button.
     * 
     * @param button  the button to click
     * @param keyCode  an int specifying the numeric code for a keyboard key
     * @param modifiers  a bitwise-ored combination of modifiers (see KeyStroke#getKeyStroke)
     * @return  the button
     */
	public static JButton setShortcutKey(final JButton button, int keyCode, int modifiers) {
		ActionMap actionMap = new ActionMapUIResource();
		actionMap.put("action", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				MiscHelper.handleExceptions(new Closure() {
					public void execute() throws Exception {
						button.doClick();
					}
				});
			}    		
		});
		SwingUtilities.replaceUIActionMap(button, actionMap);
		InputMap keyMap = new ComponentInputMap(button);    	
		keyMap.put(KeyStroke.getKeyStroke(keyCode, modifiers), "action");
		SwingUtilities.replaceUIInputMap(button, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
		return button;
	}

}
