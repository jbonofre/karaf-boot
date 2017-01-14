package org.apache.karaf.boot.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            //
            // Felix Bundle plugin
            //

            Map<String, String> instructions = new LinkedHashMap<>();
            // Starters supplied instructions
            File bndInst = new File(mavenProject.getBasedir(), "target/classes/META-INF/org.apache.karaf.boot.bnd");
            if (bndInst.isFile()) {
                complete(instructions, bndInst);
                bndInst.delete();
            }
            // Verify and use defaults
            if (instructions.containsKey("Import-Package")) {
                instructions.put("Import-Package", instructions.get("Import-Package") + ",*");
            }
            // Build config
            StringBuilder config = new StringBuilder();
            config.append("<configuration>" +
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
                    "<_include>-bnd.bnd</_include>"); // include user bnd file if present
            for (Map.Entry<String, String> entry : instructions.entrySet()) {
                config.append("<").append(entry.getKey()).append(">")
                        .append(entry.getValue())
                        .append("</").append(entry.getKey()).append(">");
            }
            config.append("</instructions>" +
                    "</configuration>");
            Xpp3Dom configuration = Xpp3DomBuilder.build(new StringReader(config.toString()));
            // Invoke plugin
            getLog().info("Invoking maven-bundle-plugin");
            Plugin felixBundlePlugin = new Plugin();
            felixBundlePlugin.setGroupId("org.apache.felix");
            felixBundlePlugin.setArtifactId("maven-bundle-plugin");
            felixBundlePlugin.setVersion("3.0.0");
            felixBundlePlugin.setInherited(true);
            felixBundlePlugin.setExtensions(true);
            PluginDescriptor felixBundlePluginDescriptor = pluginManager.loadPlugin(felixBundlePlugin, mavenProject.getRemotePluginRepositories(), mavenSession.getRepositorySession());
            MojoDescriptor felixBundleMojoDescriptor = felixBundlePluginDescriptor.getMojo("bundle");
            MojoExecution execution = new MojoExecution(felixBundleMojoDescriptor, configuration);
            pluginManager.executeMojo(mavenSession, execution);

        } catch (Exception e) {
            throw new MojoExecutionException("karaf-boot-maven-plugin failed", e);
        }
    }

    private void complete(Map<String, String> instructions, File bndInst) throws IOException {
        List<String> lines =  Files.readAllLines(bndInst.toPath());
        for (String line : lines) {
            if (!line.contains(":")) {
                continue;
            }
            String name = line.substring(0, line.indexOf(':')).trim();
            String value = line.substring(line.indexOf(':') + 1).trim();
            boolean prepend = false;
            if (name.startsWith("PREPEND-")) {
                prepend = true;
                name = name.substring("PREPEND-".length());
            }
            if (instructions.containsKey(name)) {
                if (prepend) {
                    instructions.put(name, value + "," + instructions.get(name));
                } else {
                    instructions.put(name, instructions.get(name) + "," + value);
                }
            } else {
                instructions.put(name, value);
            }
        }
    }

}
