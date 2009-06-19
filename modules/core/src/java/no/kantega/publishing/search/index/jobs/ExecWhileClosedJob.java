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

package no.kantega.publishing.search.index.jobs;

import org.apache.log4j.Logger;
import no.kantega.search.index.jobs.context.JobContext;
import no.kantega.search.index.jobs.IndexJob;

import java.io.*;

/**
 *
 */
public class ExecWhileClosedJob extends IndexJob {
    private Logger log = Logger.getLogger(getClass());

    private String command;

    public void executeJob(JobContext context) {
        long before = System.currentTimeMillis();

        try {
            context.getIndexWriterManager().ensureClosed("aksess");
            if(log.isDebugEnabled()) {
                log.debug("Executing command " + command);
            }

            Process p = Runtime.getRuntime().exec(command);

            p.waitFor();

            log.info("Command " + command +" returned exit code " +p.exitValue() +" in " + (System.currentTimeMillis() - before) +" ms.");
            if(log.isDebugEnabled()) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringWriter writer = new StringWriter();


                while(reader.ready()) {
                    writer.write(reader.readLine()+"\n");
                }
                log.debug("Command output was: [ " +writer.toString() +"]");
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
