package com.payamy.etl.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.InputStream;

public class ConfigManager {

    private static ConfigManager configManager;
    private final PropertiesConfiguration configuration;

    private ConfigManager() throws ConfigurationException {
        InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties");

        configuration = new PropertiesConfiguration();
        configuration.load(in);
    }

    public String getString(String key) {
        return configuration.getString(key);
    }

    public Integer getInt(String key) {
        return configuration.getInt(key);
    }

    public String[] getList( String key) {
        return configuration.getStringArray(key);
    }

    public static ConfigManager getInstance() throws ConfigurationException {
        if (configManager == null) {
            configManager = new ConfigManager();
        }
        return configManager;
    }
}
