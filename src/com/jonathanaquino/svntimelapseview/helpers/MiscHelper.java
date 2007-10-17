package com.jonathanaquino.svntimelapseview.helpers;

import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.Timer;

import com.jonathanaquino.svntimelapseview.Closure;
import com.jonathanaquino.svntimelapseview.ErrorWindow;

/**
 * Miscellaneous utility functions.
 */
public class MiscHelper {

    /**
     * Deals with uncaught exceptions in the closure.
     *
     * @param closure  a closure (anonymous function)
     */
    public static void handleExceptions(Closure closure) {
        try {
            closure.execute();
        } catch (final Throwable t) {
            t.printStackTrace(System.err);
            try {
                GuiHelper.invokeOnEventThread(new Runnable() {
                    public void run() {
                        errorWindow().setText(stackTrace(t));
                        errorWindow().setVisible(true);
                    }
                });
            } catch (Throwable t2) {
                t2.printStackTrace(System.err);
            }
        }
    }

    private static ErrorWindow errorWindow;

    /**
     * Returns the singleton LogWindow instance.
     *
     * @return  the window that displays stack traces
     */
    private static ErrorWindow errorWindow() {
        if (errorWindow == null) {
            errorWindow = new ErrorWindow();
        }
        return errorWindow;
    }

    /**
     * Returns an object that, when triggered, calls the ActionListener once triggerings have
     * "quieted down" for the given period of time.
     *
     * @param delay  the amount of time to wait after the last timer restart before calling the ActionListener.
     * @param listener  the callback
     */
    public static Timer createQuiescenceTimer(int delay, ActionListener listener) {
        Timer timer = new Timer(delay, listener);
        timer.setCoalesce(true);
        timer.setInitialDelay(delay);
        timer.setRepeats(false);
        return timer;
    }

    /**
     * Returns an throwable's stack trace.
     *
     * @param t  the throwable from which to extract the stack trace
     */
    public static String stackTrace(Throwable t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        t.printStackTrace(ps);
        return os.toString();
    }

}
