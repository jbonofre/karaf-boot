package org.apache.karaf.boot.jpa.impl;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.karaf.boot.jpa.PersistentUnit;
import org.apache.karaf.boot.jpa.Property;
import org.apache.karaf.boot.jpa.Provider;

import javanet.staxutils.IndentingXMLStreamWriter;

public class JpaProcessor extends AbstractProcessor {

    private boolean useHibernate;

    public JpaProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(
                PersistentUnit.class.getName()
        ));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<PersistentUnit, List<? extends AnnotationMirror>> units = new HashMap<PersistentUnit, List<? extends AnnotationMirror>>();
        for (Element elem : roundEnv.getElementsAnnotatedWith(PersistentUnit.class)) {
            PersistentUnit pu = elem.getAnnotation(PersistentUnit.class);
            units.put(pu, elem.getAnnotationMirrors());
        }
        if (!units.isEmpty()) {
            try {
                FileObject o = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
                                                                       "", "META-INF/persistence.xml");
                process(o.openWriter(), units);
                processingEnv.getMessager().printMessage(Kind.NOTE, "Generated META-INF/persistence.xml");
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Error: " + e.getMessage());
            }
            try (PrintWriter w = appendResource("META-INF/org.apache.karaf.boot.bnd")) {
                w.println("Private-Package: META-INF.*");
                w.println("Meta-Persistence: META-INF/persistence.xml");
                if (useHibernate) {
                    w.println("Import-Package: org.hibernate.proxy, javassist.util.proxy");
                }
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Error writing to META-INF/org.apache.karaf.boot.bnd: " + e.getMessage());
            }
        }
        return true;
    }

    public void process(Writer writer, Map<PersistentUnit, List<? extends AnnotationMirror>> units) throws Exception {
        Set<String> puNames = new HashSet<String>();
        XMLOutputFactory xof =  XMLOutputFactory.newInstance();
        XMLStreamWriter w = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(writer));
        w.setDefaultNamespace("http://java.sun.com/xml/ns/persistence");
        w.writeStartDocument();
        w.writeStartElement("persistence");
        w.writeAttribute("verson", "2.0");
        
        //w.println("<persistence version=\"2.0\" xmlns=\"http://java.sun.com/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd\">");
        for (PersistentUnit pu : units.keySet()) {
            if (pu.name() == null || pu.name().isEmpty()) {
                throw new IOException("Missing persistent unit name");
            }
            if (!puNames.add(pu.name())) {
                throw new IOException("Duplicate persistent unit name: " + pu.name());
            }
            w.writeStartElement("persistence-unit");
            w.writeAttribute("name", pu.name());
            w.writeAttribute("transaction-type", pu.transactionType().toString());
            writeElement(w, "description", pu.description());
            String providerName = getProvider(pu);
            writeElement(w, "provider", providerName);
            writeElement(w, "jta-data-source", pu.jtaDataSource());
            writeElement(w, "non-jta-data-source", pu.nonJtaDataSource());
            Map<String, String> props = new HashMap<>();
            addProperties(pu, props);
            addAnnProperties(units.get(pu), props);
            if (props.size() > 0) {
                w.writeStartElement("properties");
                for (String key : props.keySet()) {
                    w.writeEmptyElement("property");
                    w.writeAttribute("name", key);
                    w.writeAttribute("value", props.get(key));
                }
                w.writeEndElement();
            }
            w.writeEndElement();
        }
        w.writeEndElement();
        w.writeEndDocument();
        w.flush();
        w.close();
    }

    private void addProperties(PersistentUnit pu, Map<String, String> props) {
        if (pu.properties() == null) {
            return;
        }
        for (Property property : pu.properties()) {
            props.put(property.name(), property.value());
        }
    }

    private void addAnnProperties(List<? extends AnnotationMirror> annotations, Map<String, String> props)
        throws XMLStreamException {
        for (AnnotationMirror annMirror : annotations) {

            String name = null;
            for (AnnotationMirror a : processingEnv.getElementUtils().getAllAnnotationMirrors(annMirror.getAnnotationType().asElement())) {
                if (a.toString().startsWith("@org.apache.karaf.boot.jpa.PersistentUnit.ProviderProperty")) {
                    name = a.getElementValues().values().iterator().next().getValue().toString();
                    break;
                }
            }
            if (name != null) {
                String value = annMirror.getElementValues().values().iterator().next().getValue().toString();
                props.put(name, value);
            }
//                            processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation: " + annMirror);
//                            processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation type: " + annMirror.getAnnotationType());
//                            processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation annot: " + annMirror.getAnnotationType().getAnnotationMirrors());
//                            processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation annot: " + processingEnv.getElementUtils().getAllAnnotationMirrors(annMirror.getAnnotationType().asElement()));
//                            processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation values: " + annMirror.getElementValues());
//                            if (annMirror.getAnnotationType().getAnnotation(PersistentUnit.ProviderProperty.class) != null) {
//                                processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation ok");
//                            } else {
//                                processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Annotation nok");
//                            }
        }
    }

    private void writeElement(XMLStreamWriter w, String localName, String content) throws XMLStreamException {
        if (content != null && !content.isEmpty()) {
            w.writeStartElement(localName);
            w.writeCharacters(content);
            w.writeEndElement();
        }
    }

    private String getProvider(PersistentUnit pu) throws IOException {
        if (pu.provider() != Provider.Default && pu.providerName() != null && !pu.providerName().isEmpty()) {
            throw new IOException("At most one of provider and providerName can be used");
        }
        if (pu.provider() != null) {
            switch (pu.provider()) {
            case Hibernate:
                useHibernate = true;
                return "org.hibernate.jpa.HibernatePersistenceProvider";
            default:
                // TODO
                throw new IOException("Unsupported provider: " + pu.provider());
            }
        } else if (pu.providerName() != null) {
            return pu.providerName();
        } else {
            return null;
        }
    }

    private PrintWriter appendResource(String resource) throws IOException {
        try {
            FileObject o = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
                                                                   resource);
            return new PrintWriter(o.openWriter());
        } catch (Exception e) {
            try {
                FileObject o = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "",
                                                                    resource);
                CharArrayWriter baos = new CharArrayWriter();
                try (Reader r = o.openReader(true)) {
                    char[] buf = new char[4096];
                    int l;
                    while ((l = r.read(buf)) > 0) {
                        baos.write(buf, 0, l);
                    }
                }
                o.delete();
                o = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resource);
                Writer w = o.openWriter();
                w.write(baos.toCharArray());
                return new PrintWriter(w);
            } catch (Exception e2) {
                e2.addSuppressed(e);
                e2.printStackTrace();
                throw e2;
            }
        }
    }
}
