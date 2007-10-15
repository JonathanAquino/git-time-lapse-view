package com.jonathanaquino.svntimelapseview.helpers;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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

}
