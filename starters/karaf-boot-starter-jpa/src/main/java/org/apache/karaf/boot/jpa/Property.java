package org.apache.karaf.boot.jpa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Property {

    String name();

    String value();

}
