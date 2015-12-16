package org.apache.aries.jpa.example.tasklist.feature;

public @interface Feature {
    String name();
    String version() default "";
    String[] repositories() default {};
    String[] features();
    String pid() default "";
    String[] config() default {};
    String configFile() default "";
}
