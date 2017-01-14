package org.apache.karaf.boot.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.karaf.boot.jpa.PersistentUnit.ProviderProperty;

public interface OpenJpa {

    @ProviderProperty("openjpa.AutoClear")
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    @interface AutoClear {
        Value value();
        enum Value {
            Datastore, All;
            public String toString() {
                return super.toString().toLowerCase();
            }
        }
    }

    @ProviderProperty("openjpa.AutoDetach")
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    @interface AutoDetach {
        Value value();
        enum Value {
            Close, Commit, Nontx_Read;
            public String toString() {
                return super.toString().toLowerCase().replace('_', '-');
            }
        }
    }

}
