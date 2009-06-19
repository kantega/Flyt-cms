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

package no.kantega.publishing.test.database;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.InputStream;

/**
 *
 */
public class HSQLDBDatabaseCreator {
    private String databaseName;
    private InputStream sqlCreateScript;
    static int dbCounter = 0;

    public HSQLDBDatabaseCreator(String datebaseName, InputStream sqlCreateScript) {
        this.databaseName = datebaseName;
        this.sqlCreateScript = sqlCreateScript;
    }

    public DataSource createDatabase() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:aksess"+ databaseName + dbCounter++);

        if (sqlCreateScript == null) {
            System.out.println("sqlCreateScript == null!!");
        }

        try {
            InputStreamReader in = new InputStreamReader(sqlCreateScript, "iso-8859-1");

            StringWriter sw = new StringWriter();

            char[] buffer = new char[4096];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                sw.write(buffer, 0, n);
            }
            String sql = sw.toString();
            String[] statements = sql.split(";");
            for (String statement : statements) {
                dataSource.getConnection().createStatement().execute(statement);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dataSource;
    }
}


