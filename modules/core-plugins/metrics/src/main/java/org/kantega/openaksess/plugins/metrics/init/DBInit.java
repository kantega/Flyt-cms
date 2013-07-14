package org.kantega.openaksess.plugins.metrics.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;

import javax.sql.DataSource;

public class DBInit {
    private static final Logger log = LoggerFactory.getLogger(DBInit.class);

    @Autowired
    public DBInit(DataSource  dataSource, DatabasePopulator databasePopulator) {
        try {
            DatabasePopulatorUtils.execute(databasePopulator, dataSource);
            log.info("Created Metrics DB");
        } catch (Exception e) {
            log.info("Running metrics db init script failed, assuming because db already exists.");
        }
    }
}