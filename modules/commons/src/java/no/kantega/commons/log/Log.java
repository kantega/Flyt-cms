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

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;

import java.io.File;

public class Log {

    public synchronized static void init(final File configFile) {

        try {

            DOMConfigurator.configure(configFile.getAbsolutePath());
            Logger.getLogger(Log.class).info("OpenAksess logging initialized from " +configFile.getAbsolutePath());
        } catch (Error e) {
            BasicConfigurator.resetConfiguration();
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.INFO);
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

    public static void info(String category, String description, Object context, Object identity) {
        Logger.getLogger(category).info(LogData.create(description, context, identity));
    }

}

