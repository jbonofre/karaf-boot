package org.apache.karaf.boot.core.annotations;

import org.apache.karaf.boot.core.annotations.helper.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JPA {

    String name();

    TransactionType transactionType() default TransactionType.RESOURCE_LOCAL;

    String description() default "";

    String provider() default "";

    String jtaDataSource() default "";

    String nonJtaDataSource() default "";

    String mappingFile() default "";

    String jarFile() default "";

    // Same as "class" in persistence.xml
    String[] classes() default "";

    boolean excludeUnlistedClasses() default true;

    SharedCacheMode sharedCacheMode() default SharedCacheMode.UNSPECIFIED;

    ValidationMode validationMode() default ValidationMode.AUTO;

    Property[] properties() default {};

}
