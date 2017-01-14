package org.apache.karaf.boot.shell.impl;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.karaf.shell.api.action.lifecycle.Service;

public class ShellProcessor extends AbstractProcessor {

    boolean hasRun;

    public ShellProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<String>();
        set.add(Service.class.getName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<String> packages = new TreeSet<>();
        for (Element elem : roundEnv.getElementsAnnotatedWith(Service.class)) {
            packages.add(elem.getEnclosingElement().toString());
        }

        if (!packages.isEmpty()) {
            if (!hasRun) {
                hasRun = true;
                // Add the Karaf embedded package
                try (PrintWriter w = appendResource("META-INF/org.apache.karaf.boot.bnd")) {
                    w.println("Karaf-Commands: " + String.join(",", packages));
                } catch (Exception e) {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "Error writing to META-INF/org.apache.karaf.boot.bnd: " + e.getMessage());
                }
            }
        }

        return true;
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
