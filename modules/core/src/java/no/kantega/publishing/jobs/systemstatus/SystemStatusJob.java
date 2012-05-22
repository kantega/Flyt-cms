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

package no.kantega.publishing.jobs.systemstatus;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SystemStatusJob extends QuartzJobBean {
    private static final String SOURCE = "aksess.jobs.SystemStatusJob";


    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {
        StringBuffer msg = new StringBuffer();

        msg.append("connections: {");
        msg.append( dbConnectionFactory.getActiveConnections() +" active; ");
        msg.append( dbConnectionFactory.getIdleConnections() +" idle; ");
        msg.append( dbConnectionFactory.getMaxConnections() +" max.}");

        DecimalFormat format = new DecimalFormat("#,###.##");
        long mb = 1024*1024;
        List<MemoryPoolMXBean> memoryBeans = ManagementFactory.getMemoryPoolMXBeans();
 
        for (MemoryPoolMXBean mbean: memoryBeans) {
            String name = mbean.getName();
            MemoryUsage usage = mbean.getUsage();
            msg.append( " memory: " + name + " {");
            msg.append( format.format(usage.getUsed()/(double)mb) +" MB used; ");
            msg.append( format.format(usage.getInit()/(double)mb) +" MB init; ");
            msg.append( format.format(usage.getMax()/(double)mb) +" MB max}");
        }

        int debugConnectionsLogThreshold = 10;
        try {
            debugConnectionsLogThreshold = Aksess.getConfiguration().getInt("database.debugconnections.logthreshold", 10);
        } catch (ConfigurationException e) {
            Log.debug(SOURCE, "********* Klarte ikke å lese aksess.conf **********", null, null);
            Log.error(SOURCE, e, null, null);
            System.out.println("error:" + e);
        }

        if (dbConnectionFactory.isDebugConnections() && dbConnectionFactory.getActiveConnections() >= debugConnectionsLogThreshold) {
            msg.append("\nWarning: DBCP open connections is high. ");
            msg.append("(DBCP open connections: " + dbConnectionFactory.getActiveConnections() + ", ");
            Map map = dbConnectionFactory.connections;
            msg.append("Aksess open connections: " + map.values().size() + ")\n");
            Iterator it = map.values().iterator();
            while (it.hasNext()) {
              msg.append("*****\n");
              StackTraceElement[] stackTraceElement = (StackTraceElement[]) it.next();
              for (int i = 0; i < stackTraceElement.length && i < 5; i++) {
                  StackTraceElement e = stackTraceElement[i];
                  msg.append(" - " +  e.getClassName() + "." + e.getMethodName() + " (" + e.getLineNumber() + ") \n");
              }
            }
        }
        Log.info(SOURCE, msg.toString(), null, null);
    }
}
