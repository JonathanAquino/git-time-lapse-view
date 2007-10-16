package com.jonathanaquino.svntimelapseview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.Timer;

import com.jonathanaquino.svntimelapseview.helpers.MiscHelper;

/**
 * Persistent configuration properties.
 */
public class Configuration {

    /** The configuration values */
    private Properties properties = new Properties();

    /** Filename for the config properties. */
    private String filePath;

    /** An object that, when triggered, saves the file when triggerings have "quieted down" for 1 second. */
    private Timer saveTimer = MiscHelper.createQuiescenceTimer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            MiscHelper.handleExceptions(new Closure() {
                public void execute() throws Exception {
                    save();
                }
            });
        }
    });

    /**
     * Creates a new Configuration for the given config file.
     *
     * @param filePath  pathname for the config properties, or null if none is available
     */
    public Configuration(String filePath) throws Exception {
        this.filePath = filePath;
        if (filePathSpecified() && new File(filePath).exists() && new File(filePath).isFile()) {
            FileInputStream inputStream = new FileInputStream(filePath);
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
        }
    }

    /**
     * Returns whether the filename for the config properties has been set.
     *
     * @return  whether the config filename has been specified
     */
    public boolean filePathSpecified() {
        return filePath != null && ! filePath.equals("");
    }

    /**
     * Returns the value corresponding to the given key
     *
     * @param key  the name of the value to retrieve
     * @param defaultValue  the value to return if no value exists yet
     * @return the value at the given key
     */
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Stores the value at the given key.
     *
     * @param key  the name of the value to store
     * @param value  the value to put at the given key
     */
    public void set(String key, String value) throws Exception {
        properties.put(key, value);
        saveTimer.restart();
    }

    /**
     * Saves the configuration file.
     */
    private void save() throws Exception {
        if (! filePathSpecified()) { return; }
        FileOutputStream outputStream = new FileOutputStream(filePath);
        properties.store(outputStream, "Configuration properties for SVN Time Lapse View");
        outputStream.close();
    }

    /**
     * Returns the int value corresponding to the given key
     *
     * @param key  the name of the value to retrieve
     * @param defaultValue  the value to return if no value exists yet
     * @return the value at the given key
     */
    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }

    /**
     * Stores the int value at the given key
     *
     * @param key  the name of the value to store
     * @param value  the value to put at the given key
     */
    public void setInt(String key, int value) throws Exception {
        set(key, String.valueOf(value));
    }

    /**
     * Returns the boolean value corresponding to the given key
     *
     * @param key  the name of the value to retrieve
     * @param defaultValue  the value to return if no value exists yet
     * @return the value at the given key
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    /**
     * Stores the boolean value at the given key
     *
     * @param key  the name of the value to store
     * @param value  the value to put at the given key
     */
    public void setBoolean(String key, boolean value) throws Exception {
        set(key, String.valueOf(value));
    }

}
