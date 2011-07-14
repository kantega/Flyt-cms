package org.kantega.openaksess.plugins.dbdiff;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformInfo;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.model.Database;

import java.util.List;

/**
 *
 */
public class OaDatabaseSchema {
    private final Database actual;
    private final Database wanted;
    private final String sql;
    private final List<ModelChange> changes;
    private final Platform platform;
    private final String resourcePath;

    public OaDatabaseSchema(String resourcePath, Database actual, Database wanted, String sql, List<ModelChange> changes, Platform platform) {
        this.resourcePath = resourcePath;
        this.actual = actual;
        this.wanted = wanted;
        this.sql = sql;
        this.changes = changes;
        this.platform = platform;
    }

    public Database getActual() {   
        return actual;
    }

    public Database getWanted() {
        return wanted;
    }

    public String getSql() {
        return sql;
    }

    public List<ModelChange> getChanges() {
        return changes;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
