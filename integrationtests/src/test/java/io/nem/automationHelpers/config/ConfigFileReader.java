package io.nem.automationHelpers.config;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigFileReader {

    private Properties properties;
    private final String propertyFile= "configs/config-default.properties";


    public ConfigFileReader() {
        BufferedReader reader;
        try {
            final Path resourcePath = Paths.get(Thread.currentThread().getContextClassLoader().getResource(propertyFile).getPath());
            reader = new BufferedReader(new FileReader(resourcePath.toFile().getAbsolutePath()));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException( propertyFile + " file not found");
        }
    }

    public String getApiHost(){
        return getPropertyValue("apiHost");
    }

    public int getApiPort() {
        return Integer.parseInt(getPropertyValue("apiPort"));
    }

    public String getApiServerKey() {
        return getPropertyValue("apiServerKey");
    }

    public String getUserKey() {
        return getPropertyValue("userKey");
    }

    public String getMongodbHost() {
        return getPropertyValue("mongodbHost");
    }

    public int getMongodbPort() {
        return Integer.parseInt(getPropertyValue("mongodbPort"));
    }

    public String getNetworkType() {
        return getPropertyValue("networkType");
    }

    public BigInteger getMosaicId() {
        return new BigInteger(getPropertyValue("mosaicId"), 16);
    }

    public int getSocketTimeoutInMilliseconds() {
        return Integer.parseInt(getPropertyValue("socketTimeoutInMilliseconds"));
    }

    public int getDatabaseQueryTimeoutInSeconds() {
        return Integer.parseInt(getPropertyValue("databaseQueryTimeoutInSeconds"));
    }

    private String getPropertyValue(String propertyName)
    {
        String propertyValue = properties.getProperty(propertyName);
        if(propertyValue != null)
        {
            return propertyValue;
        }

        throw new RuntimeException(propertyName + " not specified in the " + propertyFile + " file.");
    }
}