package org.apache.karaf.boot.karaf.impl;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.karaf.util.tracker.annotation.Managed;
import org.apache.karaf.util.tracker.annotation.ProvideService;
import org.apache.karaf.util.tracker.annotation.RequireService;
import org.apache.karaf.util.tracker.annotation.Services;

public class KarafProcessor extends AbstractProcessor {

    boolean hasRun;

    public KarafProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<String>();
        set.add(Services.class.getName());
        set.add(Managed.class.getName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<String> instructions = new ArrayList<>();
        Properties props = new Properties();

        for (Element elem : roundEnv.getElementsAnnotatedWith(Services.class)) {
            for (AnnotationMirror mirror : elem.getAnnotationMirrors()) {
                if (Services.class.getName().equals(((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                    Map<String, Object> values = getAnnotationValues(mirror);
                    if (values.containsKey("provides")) {
                        for (AnnotationMirror p : (List<AnnotationMirror>) values.get("provides")) {
                            Map<String, Object> pv = getAnnotationValues(p);
                            String n = pv.get("value").toString();
                            instructions.add("Provide-Capability: osgi.service;effective:=active;objectClass=" + n);
                        }
                    }
                    if (values.containsKey("requires")) {
                        for (AnnotationMirror r : (List<AnnotationMirror>) values.get("requires")) {
                            Map<String, Object> rv = getAnnotationValues(r);
                            String value = rv.get("value").toString();
                            String filter = (String) rv.getOrDefault("filter", "");
                            boolean opt = ((Boolean) rv.getOrDefault("optional", false));

                            String fltWithClass = combine(filter, "(objectClass=" + value + ")");
                            instructions.add("Require-Capability: osgi.service;effective:=active;filter:=\"" + fltWithClass + "\"");
                            props.setProperty(value, filter);
                        }
                    }
                }
            }
            instructions.add("Bundle-Activator: " + ((TypeElement) elem).getQualifiedName().toString());

            Managed managed = elem.getAnnotation(Managed.class);
            if (managed != null) {
                props.setProperty("pid", managed.value());
            }

            String name = "OSGI-INF/karaf-tracker/" + ((TypeElement) elem).getQualifiedName().toString();
            try (OutputStream os = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", name).openOutputStream()) {
                props.store(os, null);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Error writing to " + name + ": " + e.getMessage());
            }
        }

        instructions.add("Private-Package: org.apache.karaf.util.tracker");
        instructions.add("PREPEND-Import-Package: !org.apache.karaf.util.tracker.annotation");

        if (!hasRun) {
            hasRun = true;
            // Add the Karaf embedded package
            try (PrintWriter w = appendResource("META-INF/org.apache.karaf.boot.bnd")) {
                for (String instr : instructions) {
                    w.println(instr);
                }
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Error writing to META-INF/org.apache.karaf.boot.bnd: " + e.getMessage());
            }
        }

        return true;
    }

    private Map<String, Object> getAnnotationValues(AnnotationMirror mirror) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
            map.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        }
        return map;
    }

    private String combine(String filter1, String filter2) {
        if (filter1!=null && !filter1.isEmpty()) {
            return "(&" + filter2 + filter1 + ")";
        } else {
            return filter2;
        }
    }

    private PrintWriter appendResource(String resource) throws IOException {
        try {
            FileObject o = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resource);
            return new PrintWriter(o.openWriter());
        } catch (Exception e) {
            try {
                FileObject o = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", resource);
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
