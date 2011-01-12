package org.kantega.openaksess.plugins.dbdiff;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.alteration.RecreateTableChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.model.CloneHelper;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.ddlutils.platform.PlatformImplBase;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class DbDiffTool {
    private Database actual;
    private Database wanted;
    private Platform platform;

    public DbDiffTool(Database actual, Database wanted, Platform platform) {
        this.actual = actual;
        this.wanted = wanted;
        this.platform = platform;
    }

    public String getAlterString(ModelChange change) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Database database = new CloneHelper().clone(actual);
        final StringWriter sw = new StringWriter();
        platform.getSqlBuilder().setWriter(sw);
        final Class<PlatformImplBase> clazz = PlatformImplBase.class;
        final Method method = clazz.getDeclaredMethod("processChanges", Database.class, Collection.class, CreationParameters.class);
        method.setAccessible(true);
        method.invoke(platform, database, Collections.singletonList(change), null);

        return sw.toString();

    }

    public boolean canMigrateData(List changes) {
        for(Object o : changes) {
            if(o instanceof RecreateTableChange) {
                if(!canMigrateData((RecreateTableChange) o)) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canMigrateData(RecreateTableChange change) {
        boolean canMigrateData = true;

        for (Iterator it = change.getOriginalChanges().iterator(); canMigrateData && it.hasNext();)
        {
            TableChange curChange = (TableChange)it.next();

            if (curChange instanceof AddColumnChange)
            {
                AddColumnChange addColumnChange = (AddColumnChange)curChange;

                if (addColumnChange.getNewColumn().isRequired() &&
                    !addColumnChange.getNewColumn().isAutoIncrement() &&
                    (addColumnChange.getNewColumn().getDefaultValue() == null))
                {
                    canMigrateData = false;
                }
            }
        }

        return canMigrateData;
    }
}
