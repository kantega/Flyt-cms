/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.commons.log;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;

import java.io.*;

public class Log {

    public synchronized static void init(final File configFile, File logsDirectory) {

        try {

            new DOMConfigurator().doConfigure(readAndParseConfigFile(configFile, logsDirectory), LogManager.getLoggerRepository());
            Logger.getLogger(Log.class).info("OpenAksess logging initialized from " + configFile);
        } catch (Error e) {
            BasicConfigurator.resetConfiguration();
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.INFO);
        }
    }

    private static InputStream readAndParseConfigFile(File configFile, File logsDirectory) {
        try {
            byte[] bytes = IOUtils.toByteArray(new FileInputStream(configFile));
            String string = new String(bytes, "ISO-8859-1");
            // Need to replace windows backslash paths
            String logsDirectoryPath = logsDirectory.getAbsolutePath().replaceAll("\\\\", "/");
            String replacedString = string.replaceAll("\\$\\{logsDirectory\\}", logsDirectoryPath);
            return new ByteArrayInputStream(replacedString.getBytes("ISO-8859-1"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void error(String category, Throwable exception, Object context, Object identity) {
        Logger.getLogger(category).error(LogData.create(exception, context, identity));
    }

    public static void error(String category, String message, Object context, Object identity) {
        Logger.getLogger(category).error(LogData.create(message, context, identity));
    }

    public static void fatal(String category, Throwable exception, Object context, Object identity) {
        Logger.getLogger(category).fatal(LogData.create(exception, context, identity));
    }

    public static void debug(String category, String description, Object context, Object identity) {
        Logger.getLogger(category).debug(LogData.create(description, context, identity));
    }

    public static void debug(String category, String description) {
        Logger.getLogger(category).debug(LogData.create(description, null, null));
    }

    public static void info(String category, String description, Object context, Object identity) {
        Logger.getLogger(category).info(LogData.create(description, context, identity));
    }

    public static void info(String category, String description) {
        Logger.getLogger(category).info(LogData.create(description, null, null));
    }

}

