package org.apache.karaf.boot.core.annotations.helper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Properties {

    Property property();

}