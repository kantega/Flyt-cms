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

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemStatusJob {
    private static final Logger log = LoggerFactory.getLogger(SystemStatusJob.class);

    private DecimalFormat format;
    private long MB;
    private int debugConnectionsLogThreshold;

    @PostConstruct
    public void init(){
        format = new DecimalFormat("#,###.##");
        MB = 1024*1024;
        debugConnectionsLogThreshold = Aksess.getConfiguration().getInt("database.debugconnections.logthreshold", 10);
    }

    @Scheduled(cron = "${SystemStatusJob.cron:30 0/5 * * * ?}")
    public void executeSystemStatusJob() {
        StringBuilder msg = new StringBuilder();

        msg.append("connections: {");
        msg.append(dbConnectionFactory.getActiveConnections()).append(" active; ");
        msg.append(dbConnectionFactory.getIdleConnections()).append(" idle; ");
        msg.append(dbConnectionFactory.getMaxConnections()).append(" max.}");

        List<MemoryPoolMXBean> memoryBeans = ManagementFactory.getMemoryPoolMXBeans();

        for (MemoryPoolMXBean mbean: memoryBeans) {
            String name = mbean.getName();
            MemoryUsage usage = mbean.getUsage();
            msg.append(" memory: ").append(name).append(" {");
            msg.append(format.format(usage.getUsed() / (double) MB)).append(" MB used; ");
            msg.append(format.format(usage.getInit() / (double) MB)).append(" MB init; ");
            msg.append(format.format(usage.getMax() / (double) MB)).append(" MB max}");
        }

        if (dbConnectionFactory.isDebugConnections() && dbConnectionFactory.getActiveConnections() >= debugConnectionsLogThreshold) {
            msg.append("\nWarning: DBCP open connections is high. ");
            msg.append("(DBCP open connections: ").append(dbConnectionFactory.getActiveConnections()).append(", ");
            Map<Connection, StackTraceElement[]> map = new HashMap<>(dbConnectionFactory.connections);
            msg.append("Aksess open connections: ").append(map.values().size()).append(")\n");
            for (StackTraceElement[] stackTraceElement : map.values()) {
                msg.append("*****\n");
                for (int i = 0; i < stackTraceElement.length && i < 5; i++) {
                    StackTraceElement e = stackTraceElement[i];
                    msg.append(" - ").append(e.getClassName()).append(".").append(e.getMethodName()).append(" (").append(e.getLineNumber()).append(") \n");
                }
            }
        }
        log.info( msg.toString());
    }
}
