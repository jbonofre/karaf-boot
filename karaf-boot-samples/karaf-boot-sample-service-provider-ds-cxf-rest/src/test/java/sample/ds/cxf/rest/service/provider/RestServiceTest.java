package sample.ds.cxf.rest.service.provider;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.karaf.features.FeaturesService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import javax.inject.Inject;
import java.io.File;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class RestServiceTest {

    private final static String ENDPOINT_ADDRESS = "http://localhost:8181/cxf";

    @Inject
    protected FeaturesService featuresService;

    @Configuration
    public static Option[] configure() throws Exception {
        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("4.0.3"))
                        .karafVersion("4.0.3")
                        .useDeployFolder(false)
                        .unpackDirectory(new File("target/paxexam/unpack")),
                logLevel(LogLevelOption.LogLevel.WARN),
                features(maven().groupId("org.apache.karaf.features").artifactId("standard").type("xml").classifier("features").version("4.0.3"), "scr"),
                features(maven().groupId("org.apache.cxf.karaf").artifactId("apache-cxf").type("xml").classifier("features").version("3.1.4"), "cxf"),
                features(maven().groupId("org.apache.cxf.dosgi").artifactId("cxf-dosgi").type("xml").classifier("features").version("1.7.0"), "cxf-dosgi-core"),
                mavenBundle().groupId("org.apache.karaf.boot").artifactId("karaf-boot-sample-service-provider-ds-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("org.apache.karaf.boot").artifactId("karaf-boot-sample-service-provider-ds-cxf-rest").version("1.0.0-SNAPSHOT"),
                keepRuntimeFolder()
        };
    }

    @Test
    public void testProvisioning() throws Exception{
        // first check that the features are installed
            Assert.assertTrue(featuresService.isInstalled(featuresService.getFeature("scr")));
            Assert.assertTrue(featuresService.isInstalled(featuresService.getFeature("cxf")));
            Assert.assertTrue(featuresService.isInstalled(featuresService.getFeature("cxf-dosgi-core")));
    }

    @Test
    public void testRestService(){
        HelloServiceRest client = JAXRSClientFactory.create(ENDPOINT_ADDRESS, HelloServiceRest.class);
        Assert.assertEquals("Hello morgan !", client.hello("morgan"));
    }
}
