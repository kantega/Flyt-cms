package org.kantega.openaksess.plugins.dbdiff.transform;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Database;

/**
 *
 */
public interface ModelTransformer {
    void transform(Database database, Database wanted, Platform platform);
}
