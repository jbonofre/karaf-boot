package org.apache.karaf.boot.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.ByteArrayInputStream;

@Mojo(name = "generate", threadSafe = true, defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, inheritByDefault = false)
public class GenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException {
        try {
            // invoke Karaf services plugin
            getLog().info("Invoking karaf-services-maven-plugin");
            Plugin karafServicesPlugin = new Plugin();
            karafServicesPlugin.setGroupId("org.apache.karaf.tooling");
            karafServicesPlugin.setArtifactId("karaf-services-maven-plugin");
            karafServicesPlugin.setVersion("4.0.1");
            karafServicesPlugin.setInherited(false);
            karafServicesPlugin.setExtensions(true);
            Xpp3Dom configuration = Xpp3DomBuilder.build(new ByteArrayInputStream(("<configuration>" +
                    "<project>${project}</project>" +
                    "<activatorProperty>BNDExtension-Bundle-Activator</activatorProperty>" +
                    "<requirementsProperty>BNDExtension-Require-Capability</requirementsProperty>" +
                    "<capabilitiesProperty>BNDExtension-Provide-Capability</capabilitiesProperty>" +
                    "<outputDirectory>${project.build.directory}/generated/karaf-tracker</outputDirectory>" +
                    "<classLoader>project</classLoader>" +
                    "</configuration>").getBytes()), "UTF-8");
            PluginDescriptor karafServicesPluginDescriptor = pluginManager.loadPlugin(karafServicesPlugin, mavenProject.getRemotePluginRepositories(), mavenSession.getRepositorySession());
            MojoDescriptor karafServicesMojoDescriptor = karafServicesPluginDescriptor.getMojo("service-metadata-generate");
            MojoExecution execution = new MojoExecution(karafServicesMojoDescriptor, configuration);
            pluginManager.executeMojo(mavenSession, execution);

            //Check for JPA presence.
            for (Dependency dependency : mavenProject.getDependencies()) {
                getLog().info("ArtifactID : " + dependency.getArtifactId());
                if (dependency.getArtifactId().equals("karaf-boot-core-jpa")) {
                    new GeneratePersistenceXML(mavenProject, this);
                }
            }

            // invoke Felix bundle plugin
            getLog().info("Invoking maven-bundle-plugin");
            Plugin felixBundlePlugin = new Plugin();
            felixBundlePlugin.setGroupId("org.apache.felix");
            felixBundlePlugin.setArtifactId("maven-bundle-plugin");
            felixBundlePlugin.setVersion("3.0.0");
            felixBundlePlugin.setInherited(true);
            felixBundlePlugin.setExtensions(true);
            // TODO check if a osgi.bnd file is present in the project base directory
            // TODO if jpa-start is provided as persistence.xml location
            configuration = Xpp3DomBuilder.build(new ByteArrayInputStream(("<configuration>" +
                    "<finalName>${project.build.finalName}</finalName>" +
                    "<outputDirectory>${project.build.outputDirectory}</outputDirectory>" +
                    "<m_mavenSession>${session}</m_mavenSession>" +
                    "<project>${project}</project>" +
                    "<buildDirectory>${project.build.directory}</buildDirectory>" +
                    "<supportedProjectTypes>" +
                    "<supportedProjectType>jar</supportedProjectType>" +
                    "<supportedProjectType>bundle</supportedProjectType>" +
                    "<supportedProjectType>war</supportedProjectType>" +
                    "</supportedProjectTypes>" +
                    "<instructions>" +
                    "<Private-Package>org.apache.karaf.util.tracker</Private-Package>" +
                    "<_dsannotations>*</_dsannotations>" +
                    "</instructions>" +
                    "</configuration>").getBytes()), "UTF-8");
            PluginDescriptor felixBundlePluginDescriptor = pluginManager.loadPlugin(felixBundlePlugin, mavenProject.getRemotePluginRepositories(), mavenSession.getRepositorySession());
            MojoDescriptor felixBundleMojoDescriptor = felixBundlePluginDescriptor.getMojo("bundle");
            execution = new MojoExecution(felixBundleMojoDescriptor, configuration);
            pluginManager.executeMojo(mavenSession, execution);
        } catch (Exception e) {
            throw new MojoExecutionException("karaf-boot-maven-plugin failed", e);
        }
    }
}
