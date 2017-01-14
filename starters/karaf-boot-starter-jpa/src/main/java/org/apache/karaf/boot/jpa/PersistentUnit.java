package org.apache.karaf.boot.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface PersistentUnit {

    String name();

    String description() default "";

    TransactionType transactionType() default TransactionType.RESOURCE_LOCAL;

    Provider provider() default Provider.Default;

    String providerName() default "";

    String jtaDataSource() default "";

    String nonJtaDataSource() default "";

    // TODO: mapping-file, jar-file, class, exclude-unlisted-classes, shared-cache-mode, validation-mode

    Property[] properties() default {};


    @Target(ElementType.ANNOTATION_TYPE)
    @interface ProviderProperty {
        String value();
    }

}
