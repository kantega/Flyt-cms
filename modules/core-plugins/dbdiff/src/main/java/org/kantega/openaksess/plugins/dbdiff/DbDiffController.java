package org.kantega.openaksess.plugins.dbdiff;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.ddlutils.DdlUtilsException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.CloneHelper;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;
import org.kantega.jexmec.PluginManager;
import org.kantega.openaksess.plugins.dbdiff.transform.ModelTransformer;
import org.kantega.openaksess.plugins.dbdiff.transform.MsSqlDoubleAsFloatTransformer;
import org.kantega.openaksess.plugins.dbdiff.transform.MyISAMForeginKeyTransformer;
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

    @Autowired
    private PluginManager<OpenAksessPlugin> pluginManager;

    @RequestMapping(method = RequestMethod.GET)
    public String view(ModelMap model) throws IOException {


        List<OaDatabaseSchema> schemas = new ArrayList<>();

        Set<String> allTableNames = new TreeSet<>();

        Set<String> knownTableNames = new TreeSet<>();

        final String name = null;
        final String catalog = null;
        String schema = null;
        final String[] tableTypes = null;


        Platform platform = PlatformFactory.createNewPlatformInstance(aksessDataSource);
        if(platform instanceof MSSqlPlatform) {
            platform.getPlatformInfo().addNativeTypeMapping(Types.CLOB, "TEXT", Types.CLOB);
            platform.getPlatformInfo().addNativeTypeMapping(Types.BLOB, "IMAGE", Types.BLOB);
            schema ="dbo";
        }
        platform.setSqlCommentsOn(false);
        platform.setScriptModeOn(true);

        Database actual = platform.readModelFromDatabase(name, catalog, schema, tableTypes);

        Map<String, URL> schemaResourcePaths = getSchemaResourcePaths();
        for (Map.Entry<String, URL> path : schemaResourcePaths.entrySet()) {

            URL resourcePath = path.getValue();

            try(InputStream stream = resourcePath.openStream()) {

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

                    String sql = null;

                    try {
                        sql = platform.getAlterModelSql(actualCopy, wanted);
                        schemas.add(new OaDatabaseSchema(path.getKey(), actualCopy, wanted, sql, changes, platform, null));
                    } catch (DdlUtilsException e) {
                        schemas.add(new OaDatabaseSchema(path.getKey(), actualCopy, wanted, sql, changes, platform, e));
                    }


                }
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

    private Map<String, URL> getSchemaResourcePaths() throws IOException {


        Map<ClassLoader, ClassLoader> classLoaders = new IdentityHashMap<ClassLoader, ClassLoader>();

        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            ClassLoader classLoader = pluginManager.getClassLoader(plugin);
            classLoaders.put(classLoader, classLoader);
        }

        classLoaders.put(getClass().getClassLoader(), getClass().getClassLoader());


        Map<String, URL> resourcePaths = new LinkedHashMap<String, URL>();


        for(ClassLoader classLoader : classLoaders.keySet()) {

            final Enumeration<URL> schemaListResources = classLoader.getResources("META-INF/services/openaksess-dbschemas.txt");

            for (URL listUrl : Collections.list(schemaListResources)) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(listUrl.openStream(), Charset.forName("utf-8")))) {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.length() > 0) {
                            URL resource = classLoader.getResource(line);
                            if (resource == null) {
                                throw new IllegalArgumentException("File " + listUrl + " specifies schema file which could not be found: " + line);
                            }
                            resourcePaths.put(line, resource);
                        }
                    }
            }
        }
        return resourcePaths;
    }


}
