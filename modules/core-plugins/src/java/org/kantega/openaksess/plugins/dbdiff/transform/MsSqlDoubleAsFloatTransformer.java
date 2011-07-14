package org.kantega.openaksess.plugins.dbdiff.transform;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;

import java.sql.Types;

/**
 *
 */
public class MsSqlDoubleAsFloatTransformer implements ModelTransformer {
    public void transform(Database database, Database wanted, Platform platform) {
        if (platform instanceof MSSqlPlatform) {
            for (Table table : database.getTables()) {
                for (Column column : table.getColumns()) {
                    Table wantedTable = wanted.findTable(table.getName(), false);
                    if (wantedTable != null) {
                        Column wantedColumn = wantedTable.findColumn(column.getName());
                        if (wantedColumn != null) {
                            if (column.getTypeCode() == Types.DOUBLE && wantedColumn.getTypeCode() == Types.FLOAT) {
                                column.setTypeCode(Types.FLOAT);
                            }
                        }
                    }
                }
            }
        }
    }
}
