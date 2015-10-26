package org.apache.karaf.boot.maven;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginConfigurationException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

@Mojo(name = "assembly", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresDependencyResolution = ResolutionScope.RUNTIME, requiresDependencyCollection = ResolutionScope.RUNTIME)
public class AssemblyMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Component
    @Requirement
    private MavenProject project;

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Component
    private ArtifactHandler artifactHandler;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            ArtifactFilter artifactFilter = null;

            ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(
                    mavenSession.getProjectBuildingRequest());

            buildingRequest.setProject(project);

            // non-verbose mode use dependency graph component, which gives
            // consistent results with Maven version
            // running
            DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null);
            
            List<DependencyNode> karafBootNodes = crawlNodes(rootNode.getChildren());
            
            String profileRegistry = "";
            
            if (!karafBootNodes.isEmpty()) {
                profileRegistry = buildJarUrls(karafBootNodes);
            }
            
            // invoke Karaf services plugin
            getLog().info("Invoking karaf-maven-plugin-plugin");
            Plugin karafMavenPlugin = new Plugin();
            karafMavenPlugin.setGroupId("org.apache.karaf.tooling");
            karafMavenPlugin.setArtifactId("karaf-maven-plugin");
            karafMavenPlugin.setVersion("4.0.2");
            karafMavenPlugin.setInherited(false);
            karafMavenPlugin.setExtensions(true);
            Xpp3Dom configuration = Xpp3DomBuilder.build(new ByteArrayInputStream(("<configuration>\n" +
                     " <useReferenceUrls>true</useReferenceUrls>\n" +
//                    "                    <environment>static</environment>\n" + 
                    "                    <profilesUri>\n" + 
                    profileRegistry +
//                    "                        jar:mvn:org.apache.karaf.demos.profiles/registry/${project.version}!/\n" +
                    "                    </profilesUri>\n" + 
                    "                    <startupProfiles>\n" +
                    "                        <profile>karaf</profile>\n" + 
                    "                    </startupProfiles>\n" +
                    "                </configuration>").getBytes()), "UTF-8");
            
            
            PluginDescriptor karafMavenPluginDescriptor = pluginManager.loadPlugin(karafMavenPlugin,
                    mavenProject.getRemotePluginRepositories(), mavenSession.getRepositorySession());
            MojoDescriptor karafMavenMojoDescriptor = karafMavenPluginDescriptor.getMojo("assembly");
            MojoExecution execution = new MojoExecution(karafMavenMojoDescriptor, configuration);
            pluginManager.executeMojo(mavenSession, execution);
        } catch (XmlPullParserException | IOException | PluginNotFoundException | PluginResolutionException
                | PluginDescriptorParsingException | InvalidPluginDescriptorException | PluginConfigurationException
                | PluginManagerException | DependencyGraphBuilderException e) {
            throw new MojoExecutionException("karaf-boot-maven-plugin failed", e);
        }
    }

    private String buildJarUrls(List<DependencyNode> karafBootNodes) {
        StringBuffer urlBuff = new StringBuffer();
        
        for (DependencyNode karafBootNode : karafBootNodes) {
            String groupId = karafBootNode.getArtifact().getGroupId();
            String artifactId = karafBootNode.getArtifact().getArtifactId();
            String version = karafBootNode.getArtifact().getVersion();
            
            urlBuff.append("jar:mvn:")
                   .append(groupId).append("/")
                   .append(artifactId).append("/")
                   .append(version).append("!/META-INF/profiles").append("\n");
        }
        
        return urlBuff.toString();
    }

    private List<DependencyNode> crawlNodes(List<DependencyNode> dependencyNodes) {
        List<DependencyNode> karafBootNodes = new ArrayList<>();
        
        for (DependencyNode dependencyNode : dependencyNodes) {
            if (dependencyNode.getArtifact().getArtifactId().startsWith("karaf-boot-start"))
                karafBootNodes.add(dependencyNode);
            else if (!dependencyNode.getChildren().isEmpty()) {
                karafBootNodes.addAll(crawlNodes(dependencyNode.getChildren()));
            }
        }
        
        return karafBootNodes;
    }
    
    /**
     * Gets the artifact filter to use when resolving the dependency tree.
     *
     * @return the artifact filter
     */
    private ArtifactFilter createResolvingArtifactFilter() {
        ArtifactFilter filter;

        List<String> patterns = new ArrayList<>();
        patterns.add("karaf-boot-*");
        filter = new PatternIncludesArtifactFilter(patterns);

        return filter;
    }

}
