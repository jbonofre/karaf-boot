package org.apache.karaf.boot.jpa.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;

import org.apache.karaf.boot.jpa.PersistentUnit;
import org.apache.karaf.boot.jpa.Property;
import org.apache.karaf.boot.jpa.Provider;
import org.apache.karaf.boot.jpa.TransactionType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JpaProcessorTest {

    @Ignore
    @Test
    public void testProcess() throws Exception {
        JpaProcessor processor = new JpaProcessor();
        Map<PersistentUnit, List<? extends AnnotationMirror>> units = new HashMap<>();
        PersistentUnit pu = getTestPersitentUnit();
        units.put(pu, Collections.emptyList());
        URL url = this.getClass().getResource("/expected_persistence.xml");
        byte[] encoded = Files.readAllBytes(new File(url.toURI()).toPath());
        String expected = new String(encoded, Charset.forName("utf-8"));
        StringWriter writer = new StringWriter();
        processor.process(writer, units);
        Assert.assertEquals(expected, writer.getBuffer().toString());
    }

    private PersistentUnit getTestPersitentUnit() {
        PersistentUnit pu = mock(PersistentUnit.class);
        when(pu.name()).thenReturn("test-pu");
        when(pu.provider()).thenReturn(Provider.Hibernate);
        when(pu.transactionType()).thenReturn(TransactionType.JTA);
        when(pu.description()).thenReturn("Some description");
        when(pu.jtaDataSource()).thenReturn("myds");
        Property dialect = prop("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        Property[] props = new Property[] { dialect };
        when(pu.properties()).thenReturn(props);
        return pu;
    }

    private Property prop(String name, String value) {
        Property dialect = mock(Property.class);
        when(dialect.name()).thenReturn(name);
        when(dialect.value()).thenReturn(value);
        return dialect;
    }
    
    
}
