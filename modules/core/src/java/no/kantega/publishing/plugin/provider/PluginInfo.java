package no.kantega.publishing.plugin.provider;

import java.io.File;
import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class PluginInfo {
    private final File source;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private String name;
    private String description;
    private File resourceDirectory;
    private Set<String> dependencies = Collections.emptySet();

    public PluginInfo(File source, String groupId, String artifactId, String version) {
        this.source = source;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name == null ? getArtifactId() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }


    public File getSource() {
        return source;
    }

    public String getKey() {
        return groupId +":" + artifactId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public File getResourceDirectory() {
        return resourceDirectory;
    }

    public void setResourceDirectory(File resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }
}
