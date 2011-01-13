package org.kantega.openaksess.plugins.dbdiff.transform;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.*;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.ddlutils.platform.mysql.MySqlPlatform;

/**
 * Look for indexes that has an equally named foregin key in the wanted database
 * On mysql MyISAM tables, foreign keys are confused as indexes
 */
public class MyISAMForeginKeyTransformer implements ModelTransformer {
    public void transform(Database database, Database wanted, Platform platform) {

        if (platform instanceof MySqlPlatform) {
            for (Table table : database.getTables()) {
                for (Index index : table.getIndices()) {
                    Table wantedTable = wanted.findTable(table.getName());

                    if (wantedTable != null) {
                        ForeignKey key = findCandidateForeignKey(platform.getSqlBuilder(), index, wantedTable);
                        if (key != null) {
                            table.removeIndex(index);
                            table.addForeignKey(new CloneHelper().clone(key, table, database, true));
                        }


                    }
                }
            }
        }
    }

    private ForeignKey findCandidateForeignKey(SqlBuilder sqlBuilder, Index index, Table wantedTable) {
        for(ForeignKey candidateFK : wantedTable.getForeignKeys()) {
            String candidateName = sqlBuilder.getForeignKeyName(wantedTable, candidateFK);

            if(candidateName.equals(index.getName())) {
                return candidateFK;
            }
        }
        return null;
    }
}
