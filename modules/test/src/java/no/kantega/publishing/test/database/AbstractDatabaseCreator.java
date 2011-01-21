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
import java.io.*;
import java.sql.SQLException;

public abstract class AbstractDatabaseCreator {
    protected String databaseName;
    private InputStream sqlCreateScript;

    public AbstractDatabaseCreator(String datebaseName, InputStream sqlCreateScript) {
        this.databaseName = datebaseName;
        this.sqlCreateScript = sqlCreateScript;
    }

    protected abstract DriverManagerDataSource createDataSource();

    public DataSource createDatabase() {
        DriverManagerDataSource dataSource = createDataSource();

        if (sqlCreateScript == null) {
            System.out.println("sqlCreateScript == null!!");
        }

        try {
            StringWriter sw = new StringWriter();

            BufferedReader reader = new BufferedReader(new InputStreamReader(sqlCreateScript, "iso-8859-1"));
            String line = reader.readLine();
            for (; line != null; line = reader.readLine()) {
                if (!line.startsWith("--")) {  // Ignore comments
                    sw.write(line);
                }
            }
            String sql = sw.toString();
            String[] statements = sql.split(";");
            for (String statement : statements) {
                try {
                    dataSource.getConnection().createStatement().execute(statement);
                } catch (SQLException ex) {
                    // Ignore drop table if it fails
                    if (!statement.toUpperCase().contains("DROP ")) {
                        System.out.println("ERROR: parsing:" + statement);
                        System.out.println(ex.toString());
                        throw new RuntimeException(ex);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dataSource;
    }
}