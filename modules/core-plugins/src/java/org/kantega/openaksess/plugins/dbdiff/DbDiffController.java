package org.kantega.openaksess.plugins.dbdiff;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.CloneHelper;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;
import org.kantega.openaksess.plugins.dbdiff.transform.ModelTransformer;
import org.kantega.openaksess.plugins.dbdiff.transform.MyISAMForeginKeyTransformer;
import org.kantega.openaksess.plugins.dbdiff.transform.MsSqlDoubleAsFloatTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.*;

/**
 *
 */
@Controller
public class DbDiffController {

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource aksessDataSource;

    private final ClassLoader loader = getClass().getClassLoader();

    @RequestMapping(method = RequestMethod.GET)
    public String view(ModelMap model) throws IOException {


        List<OaDatabaseSchema> schemas = new ArrayList<OaDatabaseSchema>();

        Set<String> allTableNames = new TreeSet<String>();

        Set<String> knownTableNames = new TreeSet<String>();

        Platform platform = PlatformFactory.createNewPlatformInstance(aksessDataSource);
        if(platform instanceof MSSqlPlatform) {
            platform.getPlatformInfo().addNativeTypeMapping(Types.CLOB, "TEXT", Types.CLOB);
            platform.getPlatformInfo().addNativeTypeMapping(Types.BLOB, "IMAGE", Types.BLOB);
            //platform.getPlatformInfo().addNativeTypeMapping(Types.FLOAT, "FLOAT", Types.FLOAT);
        }
        platform.setSqlCommentsOn(false);
        platform.setScriptModeOn(true);

        Database actual = platform.readModelFromDatabase(null);

        for (String resourcePath : getSchemaResoucePaths()) {

            InputStream stream = loader.getResourceAsStream(resourcePath);

            if (stream != null) {
                Database wanted = new DatabaseIO().read(new InputStreamReader(stream, Charset.forName("utf-8")));
                Set<String> wantedTablesNames = new HashSet<String>();
                for (Table table : wanted.getTables()) {
                    wantedTablesNames.add(table.getName());
                }
                knownTableNames.addAll(wantedTablesNames);


                final Database actualCopy = new CloneHelper().clone(actual);

                transform(actualCopy, wanted, platform);

                for (Table table : actualCopy.getTables()) {
                    allTableNames.add(table.getName());
                    if (!wantedTablesNames.contains(table.getName())) {
                        actualCopy.removeTable(table);
                    }
                }

                final List<ModelChange> changes = platform.getChanges(actualCopy, wanted);

                String sql = platform.getAlterModelSql(actualCopy, wanted);

                schemas.add(new OaDatabaseSchema(resourcePath, actualCopy, wanted, sql, changes, platform));

            }
        }

        Collections.sort(schemas, new Comparator<OaDatabaseSchema>() {
            public int compare(OaDatabaseSchema a, OaDatabaseSchema b) {
                return a.getWanted().getName().compareTo(b.getWanted().getName());
            }
        });

        Set<String> unknownTables = new TreeSet<String>(allTableNames);
        unknownTables.removeAll(knownTableNames);

        Database unknown = new CloneHelper().clone(actual);
        for(Table table : unknown.getTables()) {
            if(knownTableNames.contains(table.getName())) {
                unknown.removeTable(table);
            }
        }

        unknown.setName("unknown-tables");
        final StringWriter unknownWriter = new StringWriter();
        new DatabaseIO().write(unknown, unknownWriter);

        final String unknowTablesDeleteSql = platform.getAlterModelSql(unknown, new Database());


        model.addAttribute("unknownModel", unknownWriter.toString());
        model.addAttribute("unknownTables", unknownTables);
        model.addAttribute("unknownTablesDeleteSql", unknowTablesDeleteSql);
        model.addAttribute("schemas", schemas);
        model.addAttribute("dbDiffTool", new DbDiffTool());

        return "org/kantega/openaksess/plugins/dbdiff/view";
    }

    private void transform(Database database, Database wanted, Platform platform) {
        for(ModelTransformer transformer : Arrays.asList(new MyISAMForeginKeyTransformer(), new MsSqlDoubleAsFloatTransformer())) {
            transformer.transform(database, wanted, platform);
        }
    }

    private List<String> getSchemaResoucePaths() throws IOException {

        List<String> resourcePaths = new ArrayList<String>();

        // OpenAksess
        resourcePaths.add("org/kantega/openaksess/db/openaksess-dbschema.xml");


        // Plugins etc:
        final Enumeration<URL> schemaListResources = loader.getResources("META-INF/services/openaksess-dbschemas.txt");

        for (URL listUrl : Collections.list(schemaListResources)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(listUrl.openStream(), Charset.forName("utf-8")));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    resourcePaths.add(line);
                }
            }

            br.close();
        }
        return resourcePaths;
    }


}
