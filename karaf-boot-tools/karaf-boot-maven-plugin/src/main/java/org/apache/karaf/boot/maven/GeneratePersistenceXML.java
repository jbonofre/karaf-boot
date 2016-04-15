package org.apache.karaf.boot.maven;

import org.apache.karaf.boot.core.annotations.JPA;
import org.apache.karaf.boot.core.annotations.helper.Property;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.xbean.finder.ClassFinder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class GeneratePersistenceXML {

    private MavenProject mavenProject;
    private AbstractMojo abstractMojo;

    public GeneratePersistenceXML(MavenProject mavenProject,AbstractMojo abstractMojo) throws MalformedURLException{
        this.mavenProject = mavenProject;
        this.abstractMojo = abstractMojo;

        List<Class<?>> list = retrieveClassFinder().findAnnotatedClasses(JPA.class);
        ArrayList<JPA> jpaArrayList = new ArrayList<JPA>();
        for (Class classs : list) {
            JPA jpa = (JPA) classs.getAnnotation(JPA.class);
            jpaArrayList.add(jpa);
        }
        createPersistenceXML(jpaArrayList);
    }

    public ClassFinder retrieveClassFinder() throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();

        urls.add(new File(mavenProject.getBuild().getOutputDirectory()).toURI().toURL());
        for (Object artifactO : mavenProject.getArtifacts()) {
            Artifact artifact = (Artifact) artifactO;
            File file = artifact.getFile();
            if (file != null) {
                urls.add(file.toURI().toURL());
            }
        }
        ClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
        return new ClassFinder(loader, urls);
    }

    public void createPersistenceXML(List<JPA> jpaList) {

        FileOutputStream fileOutputStream = null;
        String persistenceBuilder = "<persistence>";

        for (JPA jpa : jpaList) {
            persistenceBuilder += "<persistence-unit name=\"" + jpa.name() + "\"";
            persistenceBuilder += " transaction-type=\"" + jpa.transactionType().toString() + "\">";

            if(jpa.description() != null && !jpa.description().equals("")){
                persistenceBuilder += "<description>" + jpa.description() + "</description>";
            }
            if(jpa.provider() != null && !jpa.provider().equals("")){
                persistenceBuilder += "<provider>" + jpa.provider() + "</provider>";
            }
            if(jpa.jtaDataSource() != null && !jpa.jtaDataSource().equals("")){
                persistenceBuilder += "<jta-data-source>" + jpa.jtaDataSource() + "</jta-data-source>";
            }
            if(jpa.nonJtaDataSource() != null && !jpa.nonJtaDataSource().equals("")){
                persistenceBuilder += "<non-jta-data-source>" + jpa.nonJtaDataSource() + "</non-jta-data-source>";
            }
            if(jpa.mappingFile() != null && !jpa.mappingFile().equals("")){
                persistenceBuilder +=  "<mapping-file>" + jpa.mappingFile() + "</mapping-file>";
            }
            if(jpa.jarFile() != null && !jpa.jarFile().equals("")){
                persistenceBuilder += "<jar-file>" + jpa.jarFile() + "</jar-file>";
            }

            for (String managedClass : jpa.classes()) {
                persistenceBuilder += "<class>" + managedClass + "</class>";
            }
            persistenceBuilder += "<exclude-unlisted-classes>" + jpa.excludeUnlistedClasses() + "</exclude-unlisted-classes>" +
                    "<shared-cache-mode>" + jpa.sharedCacheMode() + "</shared-cache-mode>" +
                    "<validation-mode>" + jpa.validationMode() + "</validation-mode>";
            persistenceBuilder += "<properties>" ;
            for (Property property : jpa.properties()) {
                persistenceBuilder += "<property" +
                        " name=\"" + property.name() + "\"" +
                        " value=\"" + property.value() + "\"" +
                        "/>";
            }
            persistenceBuilder += "</properties>" ;
            persistenceBuilder += "</persistence-unit>";
        }

        persistenceBuilder += "</persistence>";

        try {
            File persistenceXML = new File("src/main/resources/META-INF/persistence.xml");

            persistenceXML.mkdirs();

            if (!persistenceXML.exists()) {
                persistenceXML.createNewFile();
            }

            fileOutputStream = new FileOutputStream(persistenceXML, false);

            fileOutputStream.write(persistenceBuilder.getBytes());
        } catch (IOException e) {
            abstractMojo.getLog().error(e);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                abstractMojo.getLog().debug(e);
            }
        }
    }
}
