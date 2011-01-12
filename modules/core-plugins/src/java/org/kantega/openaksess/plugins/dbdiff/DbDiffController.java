package org.kantega.openaksess.plugins.dbdiff;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Controller
public class DbDiffController {

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource aksessDataSource;
    
    @RequestMapping(method = RequestMethod.GET)
    public String  view(ModelMap model) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("org/kantega/openaksess/db/openaksess-dbschema.xml");


        if(stream != null) {

            Database wanted = new DatabaseIO().read(new InputStreamReader(stream, Charset.forName("utf-8")));
            Set<String> wantedTablesNames  = new HashSet<String>();
            for (Table table : wanted.getTables()) {
                wantedTablesNames.add(table.getName());
            }

            Platform platform = PlatformFactory.createNewPlatformInstance(aksessDataSource);
            platform.setSqlCommentsOn(false);
            platform.setScriptModeOn(true);

            final Database actual = platform.readModelFromDatabase(null);

            for(Table table : actual.getTables()) {
                if(!wantedTablesNames.contains(table.getName())) {
                    actual.removeTable(table);
                }
            }

            final List<TableChange> changes = platform.getChanges(actual, wanted);

            String sql = platform.getAlterModelSql(actual, wanted);

            model.addAttribute("alters", sql);
            model.addAttribute("changes", changes);

            model.addAttribute("actualModel", actual);
            model.addAttribute("wantedModel", wanted);
            model.addAttribute("platformInfo", platform.getPlatformInfo());
            model.addAttribute("dbDiffTool", new DbDiffTool(actual, wanted, platform));
        }

        return "org/kantega/openaksess/plugins/dbdiff/view";
    }
}
